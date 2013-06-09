package slava.visitor;

import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import slava.scope.Symbol;

public class DefinitionVisitor<A> extends VoidVisitorAdapter<A> {
	@Override
	public void visit(Parameter n, A arg) {
		n.getSlavaScope().defineSymbol(
				new Symbol(n.getId().getName(), n.getType().toString(), n));
	}

	@Override
	public void visit(VariableDeclarationExpr n, A arg) {
		for (VariableDeclarator v : n.getVars()) {
			n.getSlavaScope().defineSymbol(
					new Symbol(v.getId().getName(), n.getType().toString(), n));
		}
	}
}
