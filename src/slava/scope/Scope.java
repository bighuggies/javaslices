package slava.scope;

public interface Scope {
	public String getScopeName();

	public Scope getEnclosingScope();

	public void define(Symbol symbol) throws AlreadyDefinedException;

	public Symbol resolve(String name);
}
