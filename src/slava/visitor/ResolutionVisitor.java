package slava.visitor;

import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArraySliceExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.visitor.GenericVisitorAdapter;
import slava.exception.CompileException;
import slava.scope.Symbol;

public class ResolutionVisitor extends GenericVisitorAdapter<Symbol, Object> {
	public Symbol visit(MethodDeclaration n, Object arg) {
		Symbol x = n.getSlavaScope().resolveType(n.getType());

		if (x == null) {
			throw new CompileException("Type <" + n.getType() + "> on line "
					+ n.getBeginLine() + " not defined");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(Parameter n, Object arg) {
		Symbol x = n.getSlavaScope().resolveType(n.getType());

		if (x == null) {
			throw new CompileException("Type <" + n.getType() + "> on line "
					+ n.getBeginLine() + " not defined");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(FieldDeclaration n, Object arg) {
		Symbol x = n.getSlavaScope().resolveType(n.getType());

		if (x == null) {
			throw new CompileException("Type <" + n.getType() + "> on line "
					+ n.getBeginLine() + " not defined");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(VariableDeclarationExpr n, Object arg) {
		// only fires for variables in local scopes
		// System.out.println("DeclarationExpr: " + n);

		Symbol x = n.getSlavaScope().resolveType(n.getType());

		if (x == null) {
			throw new CompileException("Type <" + n.getType() + "> on line "
					+ n.getBeginLine() + " not defined");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(ArrayAccessExpr n, Object arg) {
		Symbol x = n.getIndex().accept(this, arg);

		if (x == null) {
			throw new CompileException("Array accessor type on line "
					+ n.getBeginLine() + " not defined");
		} else if (x.name != "int") {
			throw new CompileException("Illegal array access on line "
					+ n.getBeginLine() + " to array <" + n.getName() + ">");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(ArraySliceExpr n, Object arg) {
		Symbol x = n.getStartIndex().accept(this, arg);
		Symbol y = n.getEndIndex().accept(this, arg);

		if (x == null) {
			throw new CompileException("Array accessor type on line "
					+ n.getBeginLine() + " not defined");
		} else if (x.name != "int") {
			throw new CompileException("Illegal array access on line "
					+ n.getBeginLine() + " to array <" + n.getName() + ">");
		}

		if (y == null) {
			throw new CompileException("Array accessor type on line "
					+ n.getBeginLine() + " not defined");
		} else if (y.name != "int") {
			throw new CompileException("Illegal array access on line "
					+ n.getBeginLine() + " to array <" + n.getName() + ">");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(ArrayCreationExpr n, Object arg) {
		for (Expression e : n.getDimensions()) {
			Symbol x = e.accept(this, arg);

			if (x == null) {
				throw new CompileException("Array accessor type on line "
						+ n.getBeginLine() + " not defined");
			} else if (x.name != "int") {
				throw new CompileException("Illegal array dimensions on line "
						+ n.getBeginLine());
			}
		}

		return super.visit(n, arg);
	}

	// TODO
	public Symbol visit(AssignExpr n, Object arg) {
		Symbol x = n.getTarget().accept(this, arg);
		Symbol y = n.getValue().accept(this, arg);

		if (x == null) {
			throw new CompileException(n.getTarget() + " is not in scope");
		}

		if (x.node.getBeginLine() > n.getBeginLine()) {
			throw new CompileException(x.name + "accessed before definition");
		}
		
		if (y == null) {
			throw new CompileException(n.getValue() + " is not in scope");
		}

		if (!x.type.name.equals(y.type.name)) {
			throw new CompileException("Type of x does not match y");
		}

		return super.visit(n, arg);
	}

	public Symbol visit(ConditionalExpr n, Object arg) {
		// ternary
		return super.visit(n, arg);
	}

	public Symbol visit(FieldAccessExpr n, Object arg) {
		Symbol x = n.getSlavaScope().getTypeScope().resolveField(n.getField());

		if (x == null) {
			throw new CompileException("Field <" + n.getField() + "> on line "
					+ n.getBeginLine() + " not defined on type <"
					+ n.getSlavaScope().getTypeScope() + ">");
		}

		return super.visit(n, arg);
	}

	/* ----------- TYPES ------------- */

	@Override
	public Symbol visit(StringLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("String");
	}

	@Override
	public Symbol visit(IntegerLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("int");
	}

	@Override
	public Symbol visit(LongLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("long");
	}

	@Override
	public Symbol visit(CharLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("char");
	}

	@Override
	public Symbol visit(DoubleLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("double");
	}

	@Override
	public Symbol visit(BooleanLiteralExpr n, Object arg) {
		return n.getSlavaScope().resolveType("boolean");
	}

	@Override
	public Symbol visit(NullLiteralExpr n, Object arg) {
		// TODO: is this good?
		return n.getSlavaScope().resolveType("Object");
	}

	@Override
	public Symbol visit(NameExpr n, Object arg) {
		Symbol x = n.getSlavaScope().resolveSymbol(n.getName());

		if (x == null) {
			x = n.getSlavaScope().resolveField(n.getName());
		}

		return x;
	}
}
