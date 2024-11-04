package ast;

public class AssignStmt extends Stmt{
    final String ident;
    final Expr expr;

    public AssignStmt(String ident, Expr expr, Location loc) {
        super(loc);
        this.ident = ident;
        this.expr = expr;
    }

    public String getIdentStr() {
        return ident;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return ident + " = " + expr + ";";
    }
}
