package ast;

import ast.TypeDecl.Type;

public class DeclStmt extends Stmt {

    final VarDecl varDecl;
    final Expr expr;

    public DeclStmt(VarDecl varDecl, Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.varDecl = varDecl;
    }

    public String getName() {
        return this.varDecl.getName();
    }
    public boolean isMutable() {
        return varDecl.isMutable();
    }
    public Type getType() {
        return varDecl.getType();
    }

    public Expr getExpr() {
        return expr;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }

    @Override
    public String toString() {
        return varDecl.toString() + " = " + expr.toString();
    }
}
