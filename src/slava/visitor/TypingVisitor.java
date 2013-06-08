package slava.visitor;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypingVisitor<A> extends VoidVisitorAdapter<A> {
	private final Map<String, String> types = new HashMap<String, String>();
	private final List<String> classes = new ArrayList<String>();
	private final List<String> primitives = new ArrayList<String>();

	{
		primitives.add("int");
		primitives.add("long");
		primitives.add("float");
		primitives.add("double");
		primitives.add("boolean");
		primitives.add("String");
	}

	@Override
	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		primitives.add(n.getName());
		super.visit(n, arg);
	}

	public List<String> getClasses() {
		return primitives;
	}
}
