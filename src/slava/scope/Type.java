package slava.scope;

public class Type {
	public final String name;

	public Type(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
