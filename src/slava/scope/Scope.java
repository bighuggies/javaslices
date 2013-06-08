package slava.scope;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import slava.exception.AlreadyDefinedException;

public class Scope {

	public static enum ScopeType {
		GLOBAL, CLASS, ENUM, CONSTRUCTOR, METHOD, LOCAL, UNKOWN
	}

	private final Scope parent;
	private final Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	private final Map<String, Symbol> members = new HashMap<String, Symbol>();
	private final Map<String, Symbol> types = new HashMap<String, Symbol>();

	private final Map<String, Scope> scopes = new HashMap<String, Scope>();

	private final Map<String, Map> symbolTable = new HashMap<String, Map>();

	private final ScopeType type;
	private final String name;

	public Scope(Scope parent) {
		this(parent, ScopeType.UNKOWN);
	}

	public Scope(Scope parent, ScopeType type) {
		this(parent, type, UUID.randomUUID().toString());
	}

	public Scope(Scope parent, ScopeType type, String name) {
		this.parent = parent;
		this.type = type;
		this.name = name;

		symbolTable.put("symbols", symbols);
		symbolTable.put("members", members);
		symbolTable.put("types", types);
	}

	public ScopeType getType() {
		return type;
	}

	public Scope pushScope() {
		return pushScope(UUID.randomUUID().toString());
	}

	public Scope pushScope(String name) {
		return pushScope(ScopeType.UNKOWN, name);
	}
	
	public Scope pushScope(ScopeType type) {
		return pushScope(type, UUID.randomUUID().toString());
	}
	
	public Scope pushScope(ScopeType type, String name) {
		Scope newScope = new Scope(this, type, name);
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
		System.out.print("\n");
		
		loopPrint(" ", indent);
		System.out.println(this.type + ": " + this.name);
		loopPrint(" ", indent);
		loopPrint("=", this.name.length() + this.type.toString().length() + 2);
		System.out.print("\n");
		
		printMap(this.types, indent);
		printMap(this.members, indent);
		printMap(this.symbols, indent);

		for (Scope s : scopes.values()) {
			s.dump(indent + 2);
		}
	}

	public <A, B> void printMap(Map<A, B> map, int indent) {
		for (B s : map.values()) {
			loopPrint(" ", indent);
			System.out.println(s.toString());
		}
	}
	
	public void loopPrint(String str, int num) {
		for (int i = 0; i < num; i++) {
			System.out.print(str);
		}
	}
}
