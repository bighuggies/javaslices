package slava.visitor;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import slava.scope.Scope;
import slava.scope.Symbol;

public class DefinitionVisitor<A> extends VoidVisitorAdapter<A> {
	private Scope globalScope = new Scope(null);
	private Scope currentScope = globalScope;

	{
		globalScope.defineType(new Symbol("void"));
		globalScope.defineType(new Symbol("int"));
		globalScope.defineType(new Symbol("long"));
		globalScope.defineType(new Symbol("float"));
		globalScope.defineType(new Symbol("double"));
		globalScope.defineType(new Symbol("boolean"));
		globalScope.defineType(new Symbol("String"));
	}
	
	@Override
	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		currentScope.defineType(new Symbol(n.getName()));
		currentScope = currentScope.pushScope(n.getName());
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	public void visit(EnumDeclaration n, A arg) {
		currentScope.defineType(new Symbol(n.getName()));
		currentScope = currentScope.pushScope();
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	public void visit(FieldDeclaration n, A arg) {
		for (VariableDeclarator v : n.getVariables()) {
			currentScope.defineMember(new Symbol(v.getId().getName()));
		}
	}

	public void visit(MethodDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope.defineMember(new Symbol(n.getName()));
		currentScope = currentScope.pushScope();
		
		if (n.getParameters() != null) {
			for (Parameter p : n.getParameters()) {
				currentScope.defineSymbol(new Symbol(p.getId().getName()));
			}
		}

		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	public void visit(EnumConstantDeclaration n, A arg) {
		currentScope.defineMember(new Symbol(n.getName()));
	}

	public void visit(BlockStmt n, A arg) {
		currentScope = currentScope.pushScope();
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	public void visit(VariableDeclarationExpr n, A arg) {
		for (VariableDeclarator v : n.getVars()) {
			currentScope.defineSymbol(new Symbol(v.getId().getName()));
		}
	}

	public void visit(VariableDeclaratorId n, A arg) {
	}

	public void dump() {
		globalScope.dump(0);
	}
}
