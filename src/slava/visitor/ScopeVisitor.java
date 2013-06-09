package slava.visitor;

import japa.parser.ast.BlockComment;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.LineComment;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.AnnotationDeclaration;
import japa.parser.ast.body.AnnotationMemberDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.EmptyMemberDeclaration;
import japa.parser.ast.body.EmptyTypeDeclaration;
import japa.parser.ast.body.EnumConstantDeclaration;
import japa.parser.ast.body.EnumDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.InitializerDeclaration;
import japa.parser.ast.body.JavadocComment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.ArraySliceExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.BreakStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.ContinueStmt;
import japa.parser.ast.stmt.DoStmt;
import japa.parser.ast.stmt.EmptyStmt;
import japa.parser.ast.stmt.ExplicitConstructorInvocationStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.ForeachStmt;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.stmt.LabeledStmt;
import japa.parser.ast.stmt.ReturnStmt;
import japa.parser.ast.stmt.SwitchEntryStmt;
import japa.parser.ast.stmt.SwitchStmt;
import japa.parser.ast.stmt.SynchronizedStmt;
import japa.parser.ast.stmt.ThrowStmt;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.stmt.TypeDeclarationStmt;
import japa.parser.ast.stmt.WhileStmt;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.PrimitiveType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.type.WildcardType;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import slava.scope.Scope;
import slava.scope.Scope.ScopeType;
import slava.scope.Symbol;

public class ScopeVisitor<A> extends VoidVisitorAdapter<A> {
	private Scope globalScope;
	private Scope currentScope;

	public ScopeVisitor(Scope root) {
		this.globalScope = root;
		this.currentScope = root;

		globalScope.defineType(new Symbol("void"));
		globalScope.defineType(new Symbol("int"));
		globalScope.defineType(new Symbol("long"));
		globalScope.defineType(new Symbol("float"));
		globalScope.defineType(new Symbol("double"));
		globalScope.defineType(new Symbol("boolean"));
		globalScope.defineType(new Symbol("char"));
		globalScope.defineType(new Symbol("String"));
		globalScope.defineType(new Symbol("Object"));
	}

	// - Compilation Unit ----------------------------------
	@Override
	public void visit(CompilationUnit n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(PackageDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ImportDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(TypeParameter n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(LineComment n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(BlockComment n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	// - Body ----------------------------------------------
	@Override
	public void visit(ClassOrInterfaceDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.CLASS, n.getName());
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(EnumDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.ENUM, n.getName());
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(EmptyTypeDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnumConstantDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(AnnotationDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(AnnotationMemberDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(VariableDeclarator n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(VariableDeclaratorId n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ConstructorDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.CONSTRUCTOR,
				n.getName());
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(MethodDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.METHOD, n.getName());
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(Parameter n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(EmptyMemberDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(InitializerDeclaration n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(JavadocComment n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	// - Type ----------------------------------------------
	@Override
	public void visit(ClassOrInterfaceType n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(PrimitiveType n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ReferenceType n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(VoidType n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(WildcardType n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	// - Expression ----------------------------------------
	@Override
	public void visit(ArrayAccessExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArraySliceExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArrayCreationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ArrayInitializerExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(AssignExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(BinaryExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(CastExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ClassExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ConditionalExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(EnclosedExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(FieldAccessExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(InstanceOfExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(StringLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(IntegerLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(LongLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(IntegerLiteralMinValueExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(LongLiteralMinValueExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(CharLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(DoubleLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(BooleanLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(NullLiteralExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(MethodCallExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(NameExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ObjectCreationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(QualifiedNameExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ThisExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(SuperExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(UnaryExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(VariableDeclarationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(MarkerAnnotationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(SingleMemberAnnotationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(NormalAnnotationExpr n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(MemberValuePair n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	// - Statements ----------------------------------------
	@Override
	public void visit(ExplicitConstructorInvocationStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(TypeDeclarationStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(AssertStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(BlockStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(LabeledStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(EmptyStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ExpressionStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(SwitchStmt n, A arg) {

		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(SwitchEntryStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(BreakStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(ReturnStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(IfStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(WhileStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(ContinueStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(DoStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(ForeachStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(ForStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(ThrowStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(SynchronizedStmt n, A arg) {
		n.setSlavaScope(currentScope);
		super.visit(n, arg);
	}

	@Override
	public void visit(TryStmt n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}

	@Override
	public void visit(CatchClause n, A arg) {
		n.setSlavaScope(currentScope);
		currentScope = currentScope.pushScope(ScopeType.LOCAL);
		super.visit(n, arg);
		currentScope = currentScope.popScope();
	}
}
