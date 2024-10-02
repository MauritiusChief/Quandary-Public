package ast;

import java.io.PrintStream;

public class DeclStmt extends Stmt {

    final VarDecl varDecl;
    final Expr expr;

    public DeclStmt(VarDecl varDecl, Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.varDecl = varDecl;
    }

    public Expr getExpr() {
        return expr;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }

    @Override
    public String toString() {
        return varDecl.toString() + "=" + expr.toString();
    }
}
