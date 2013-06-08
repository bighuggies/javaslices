package slava.scope;

public class Symbol {
	public final String name;

	public Symbol(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
