package slava.scope;

import japa.parser.ast.Node;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

public class Symbol {
	public final String name;
	public final Symbol type;
	public final Node node;

	public Symbol(String name) {
		this(name, null, null);
	}

	public Symbol(String name, String type) {
		this(name, type, null);
	}

	public Symbol(String name, String type, Node node) {
		this.name = name;
		this.node = node;

		if (type != null) {
			this.type = new Symbol(type);
		} else {
			this.type = null;
		}
	}

	public Symbol(MethodDeclaration method) {
		this(buildName(method));
	}

	private static String buildName(MethodDeclaration method) {
		StringBuilder params = new StringBuilder();

		if (method.getParameters() != null) {
			for (Parameter p : method.getParameters()) {
				params.append(p.getType() + " " + p.getId().getName() + ", ");
			}

			params.substring(0, params.length() - 1);
		}

		return method.getName() + "(" + params.toString() + ")";
	}

	@Override
	public String toString() {
		return this.type.name + " " + this.name;
	}

	public boolean equals(Symbol symbol) {
		return this.name == symbol.name && this.type.name == symbol.type.name;
	}
}
