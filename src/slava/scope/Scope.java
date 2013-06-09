package slava.scope;

import japa.parser.ast.type.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import slava.exception.CompileException;

public class Scope {

	public static enum ScopeType {
		GLOBAL, CLASS, ENUM, CONSTRUCTOR, METHOD, LOCAL, UNKOWN
	}

	private final Scope parent;
	private final Map<String, Symbol> variables = new HashMap<String, Symbol>();
	private final Map<String, Symbol> methods = new HashMap<String, Symbol>();
	private final Map<String, Symbol> fields = new HashMap<String, Symbol>();
	private final Map<String, Symbol> types = new HashMap<String, Symbol>();

	private final Map<String, Scope> scopes = new HashMap<String, Scope>();
	private final Map<String, Scope> superClasses = new HashMap<String, Scope>();

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

		symbolTable.put("symbols", variables);
		symbolTable.put("methods", methods);
		symbolTable.put("fields", fields);
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

	public Scope pushSuperClass(String name) {
		Scope scope = this;

		while (scope != null) {
			for (Scope s : scope.getEnclosedScopes()) {
				if (s.name.equals(name) && s.type == ScopeType.CLASS) {
					superClasses.put(name, s);
					return s;
				}
			}

			scope = scope.popScope();
		}

		return null;
	}

	public Scope getEnclosedScope(String name) {
		return scopes.get(name);
	}

	public Collection<Scope> getEnclosedScopes() {
		return scopes.values();
	}

	public Scope findEnclosedScope(String name) {
		Scope found = null;

		if (scopes.containsKey(name)) {
			return getEnclosedScope(name);
		}

		for (Scope s : getEnclosedScopes()) {
			found = s.findEnclosedScope(name);
		}

		return found;
	}

	public Scope getRootScope() {
		Scope scope = this;

		while (scope.popScope() != null)
			scope = scope.popScope();

		return scope;
	}

	public Collection<Scope> getSuperClasses() {
		return superClasses.values();
	}

	public Scope getTypeScope() {
		Scope x = this;
		
		while (x.type != ScopeType.CLASS)
			x = x.popScope();

		return x;
	}

	public Symbol defineMethod(Symbol symbol) {
		if (methods.containsKey(symbol.name)) {
			throw new CompileException("Method already defined.");
		}

		this.methods.put(symbol.name, symbol);
		return symbol;
	}

	public Symbol resolveMethod(String key) {
		if (variables.containsKey(key)) {
			return variables.get(key);
		}

		if (methods.containsKey(key)) {
			return methods.get(key);
		}

		if (getTypeScope().resolveMethod(key) != null) {
			return getTypeScope().resolveMethod(key);
		}

		for (Scope p : getTypeScope().getSuperClasses()) {
			Symbol method = p.resolveMethod(key);

			if (method != null) {
				return method;
			}
		}

		return null;
	}

	public Symbol defineField(Symbol symbol) {
		if (fields.containsKey(symbol.name)) {
			throw new CompileException("Field already defined.");
		}

		this.fields.put(symbol.name, symbol);
		return symbol;
	}

	public Symbol resolveField(String key) {
		Symbol x = resolve("fields", key);
		
		if (x != null) {
			return x;
		}

		if (getTypeScope() != this) {
			if (getTypeScope().resolveField(key) != null) {
				return getTypeScope().resolveField(key);
			}
		}

		for (Scope p : getTypeScope().getSuperClasses()) {
			Symbol field = p.resolveField(key);

			if (field != null) {
				return field;
			}
		}

		return null;
	}

	public Symbol defineType(Symbol type) {
		return this.define("types", type.name, type);
	}

	public Symbol resolveType(Type type) {
		return this.resolve("types", type.toString());
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
			throw new CompileException(key + " already exists in " + table); // no
																				// shadowing
		}

		symbolTable.get(table).put(key, value);
		return value;
	}

	public <T> T resolve(String table, String key) {
		// hack to fix arrays
		key = key.toString().replaceAll("\\[\\]", "");

		if (symbolTable.get(table).containsKey(key)) {
			return (T) symbolTable.get(table).get(key);
		}

		if (this.popScope() != null) {
			return this.popScope().resolve(table, key);
		}

		return null;
	}

	public void dumpAll(int indent) {
		getRootScope().dump(indent);
	}

	public void dump(int indent) {
		System.out.print("\n");

		loopPrint(" ", indent);
		System.out.println(this.type + ": " + this.name);
		loopPrint(" ", indent);
		loopPrint("=", this.name.length() + this.type.toString().length() + 2);
		System.out.print("\n");

		printMap(this.types, indent);
		printMap(this.methods, indent);
		printMap(this.fields, indent);
		printMap(this.variables, indent);

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

	@Override
	public String toString() {
		return this.type + ": " + this.name;
	}
}
