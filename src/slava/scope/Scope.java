package slava.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import slava.exception.AlreadyDefinedException;

public class Scope {

	private final Scope parent;
	private final Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	private final Map<String, Symbol> members = new HashMap<String, Symbol>();
	private final Map<String, Symbol> types = new HashMap<String, Symbol>();

	private final Map<String, Scope> scopes = new HashMap<String, Scope>();

	private final Map<String, Map> symbolTable = new HashMap<String, Map>();

	public Scope(Scope parent) {
		this.parent = parent;

		symbolTable.put("symbols", symbols);
		symbolTable.put("members", members);
		symbolTable.put("types", types);
	}

	public Scope pushScope() {
		return pushScope(UUID.randomUUID().toString());
	}

	public Scope pushScope(String name) {
		Scope newScope = new Scope(this);
		this.scopes.put(name, newScope);
		return newScope;
	}

	public Scope popScope() {
		return parent;
	}

	public Scope getEnclosedScope(String name) {
		return scopes.get(name);
	}

	public Collection<Scope> getEnclosedScopes() {
		return scopes.values();
	}

	public Symbol defineMember(Symbol symbol) {
		return this.define("members", symbol.name, symbol);
	}

	public Symbol resolveMember(String name) {
		return this.resolve("members", name);
	}

	public Symbol defineType(Symbol type) {
		return this.define("types", type.name, type);
	}

	public Symbol resolveType(String name) {
		return this.resolve("types", name);
	}

	public Symbol defineSymbol(Symbol symbol) {
		return this.define("symbols", symbol.name, symbol);
	}

	public Symbol resolveSymbol(String name) {
		return this.resolve("symbols", name);
	}

	public <T> T define(String table, String key, T value) {
		if (this.resolve(table, key) != null) {
			throw new AlreadyDefinedException(); // no shadowing
		}

		symbolTable.get(table).put(key, value);
		return value;
	}

	public <T> T resolve(String table, String key) {

		if (symbolTable.get(table).containsKey(key)) {
			return (T) symbolTable.get(table).get(key);
		}

		if (this.popScope() != null) {
			return this.popScope().resolve(table, key);
		}

		return null;
	}

	public void dumpAll(int indent) {
		Scope scope = this;

		while (scope.popScope() != null)
			scope = scope.popScope();

		scope.dump(indent);
	}

	public void dump(int indent) {
		printMap(this.types, indent);
		printMap(this.members, indent);
		printMap(this.symbols, indent);

		for (Scope s : scopes.values()) {
			s.dump(indent + 2);
		}
	}

	public <A, B> void printMap(Map<A, B> map, int indent) {
		for (B s : map.values()) {
			for (int i = 0; i < indent; i++) {
				System.out.print(" ");
			}

			System.out.println(s.toString());
		}
	}
}
