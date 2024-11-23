package ast;

import ast.TypeDecl.Type;

public class AssignStmt extends Stmt{
    final String name;
    final Expr expr;

    public AssignStmt(String name, Expr expr, Location loc) {
        super(loc);
        this.name = name;
        this.expr = expr;
    }

    public String getName() {
        return name;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return name + " = " + expr + ";";
    }
}
