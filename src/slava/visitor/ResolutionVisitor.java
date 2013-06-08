package slava.visitor;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import slava.exception.NotDefinedException;
import slava.scope.Symbol;

public class ResolutionVisitor<A> extends VoidVisitorAdapter<A> {
	public void visit(AssignExpr n, A arg) {
	}

	public void visit(MethodDeclaration n, A arg) {
		Symbol x = n.getSlavaScope().resolveType(n.getType().toString());

		if (x == null)
			throw new NotDefinedException();

		if (n.getParameters() != null) {
			for (Parameter p : n.getParameters()) {
				Symbol z = n.getSlavaScope()
						.resolveType(p.getType().toString());
				if (z == null)
					throw new NotDefinedException();
			}
		}
	}
}
