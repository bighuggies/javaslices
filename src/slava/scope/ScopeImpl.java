package slava.scope;

import java.util.HashMap;
import java.util.Map;

public class ScopeImpl implements Scope {

	private final Scope enclosingScope;
	private final Map<String, Symbol> symbols = new HashMap<String, Symbol>();
	private final String name;

	public ScopeImpl(Scope enclosingScope, String name) {
		this.enclosingScope = enclosingScope;
		this.name = name;
	}

	@Override
	public String getScopeName() {
		return name;
	}

	@Override
	public Scope getEnclosingScope() {
		return enclosingScope;
	}

	@Override
	public void define(Symbol symbol) {
		if (symbols.containsKey(symbol.getName())) {
			throw new AlreadyDefinedException();
		}
		
		symbols.put(symbol.getName(), symbol);
	}

	@Override
	public Symbol resolve(String name) {
		return symbols.get(name);
	}
}
