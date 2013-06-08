package slava.visitor;

import slava.scope.Symbol;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class TypingVisitor<A> extends VoidVisitorAdapter<A> {
	@Override
	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		n.getSlavaScope().defineType(new Symbol(n.getName()));
	}

	@Override
	public void visit(FieldDeclaration n, A arg) {
		for (VariableDeclarator v : n.getVariables()) {
			n.getSlavaScope().defineMember(new Symbol(v.getId().getName()));
		}
	}

	@Override
	public void visit(MethodDeclaration n, A arg) {
		n.getSlavaScope().defineMember(new Symbol(n.getName()));
	}

	@Override
	public void visit(EnumDeclaration n, A arg) {
		n.getSlavaScope().defineType(new Symbol(n.getName()));
	}

	@Override
	public void visit(EnumConstantDeclaration n, A arg) {
		n.getSlavaScope().defineMember(new Symbol(n.getName()));
	}
}
