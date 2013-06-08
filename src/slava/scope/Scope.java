package slava.scope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import slava.exception.AlreadyDefinedException;

public class Scope {

	private final Scope parent;
	private final Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	private final Map<String, Symbol> members = new HashMap<String, Symbol>();
	private final Map<String, Type> types = new HashMap<String, Type>();
	private final ArrayList<Scope> scopes = new ArrayList<Scope>();

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public Scope defineScope(Scope scope) {
		scopes.add(scope);
		return scope;
	}

	public Symbol defineMember(Symbol symbol) {
		return this.define(this.members, symbol.name, symbol);
	}

	public Symbol resolveMember(String name) {
		return this.resolve(this.members, name);
	}

	public Type defineType(Type type) {
		return this.define(this.types, type.name, type);
	}

	public Type resolveType(String name) {
		return this.resolve(this.types, name);
	}

	public Symbol defineSymbol(Symbol symbol) {
		return this.define(this.symbols, symbol.name, symbol);
	}

	public Symbol resolveSymbol(String name) {
		return this.resolve(this.symbols, name);
	}

	public <T> T define(Map<String, T> map, String key, T value) {
		if (this.resolve(map, key) != null) {
			throw new AlreadyDefinedException(); // no shadowing
		}

		map.put(key, value);
		return value;
	}

	public <T> T resolve(Map<String, T> map, String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		if (this.getEnclosingScope() != null) {
			return this.getEnclosingScope().resolve(map, key);
		}

		return null;
	}

	public Scope getEnclosingScope() {
		return parent;
	}

	public void dump(int indent) {
		printMap(this.types, indent);
		printMap(this.members, indent);
		printMap(this.symbols, indent);
		
		for (Scope s : scopes) {
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
