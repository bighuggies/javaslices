package slava.scope;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;

public class Symbol {
	public final String name;

	public Symbol(String name) {
		this.name = name;
	}
	
	public Symbol(MethodDeclaration method) {
		this(buildName(method));
	}
	
	private static String buildName(MethodDeclaration method) {
		StringBuilder params = new StringBuilder();
		
		if (method.getParameters() != null) {
			for(Parameter p : method.getParameters()) {
				params.append(p.getType() + " " + p.getId().getName() + ", ");
			}
			
			params.substring(0, params.length() - 1);
		}
		
		return method.getName() + "(" + params.toString() + ")";
	}

	@Override
	public String toString() {
		return this.name;
	}

	public boolean equals(Symbol symbol) {
		return this.name == symbol.name;
	}
}
