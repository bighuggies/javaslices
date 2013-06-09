package slava.visitor;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import slava.scope.Symbol;

public class TypingVisitor<A> extends VoidVisitorAdapter<A> {
	@Override
	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		if (n.getExtends() != null) {
			for (ClassOrInterfaceType c : n.getExtends()) {
				n.getSlavaScope().getEnclosedScope(n.getName())
						.pushSuperClass(c.getName());
			}
		}

		if (n.getImplements() != null) {
			for (ClassOrInterfaceType c : n.getImplements()) {
				n.getSlavaScope().getEnclosedScope(n.getName())
						.pushSuperClass(c.getName());
			}
		}

		n.getSlavaScope().defineType(new Symbol(n.getName(), n.getName(), n));

		super.visit(n, arg);
	}

	@Override
	public void visit(FieldDeclaration n, A arg) {
		for (VariableDeclarator v : n.getVariables()) {
			n.getSlavaScope().defineField(
					new Symbol(v.getId().getName(), n.getType().toString(), n));
		}

		super.visit(n, arg);
	}

	@Override
	public void visit(ConstructorDeclaration n, A arg) {
		n.getSlavaScope().defineMethod(new Symbol(n.getName()));

		super.visit(n, arg);
	}

	@Override
	public void visit(MethodDeclaration n, A arg) {
		n.getSlavaScope().defineMethod(new Symbol(n));

		super.visit(n, arg);
	}

	@Override
	public void visit(EnumDeclaration n, A arg) {
		n.getSlavaScope().defineType(new Symbol(n.getName(), n.getName(), n));

		super.visit(n, arg);
	}

	@Override
	public void visit(EnumConstantDeclaration n, A arg) {
		n.getSlavaScope().defineField(new Symbol(n.getName()));

		super.visit(n, arg);
	}
}
