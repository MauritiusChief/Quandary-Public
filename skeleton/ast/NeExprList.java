package ast;

public class NeExprList extends ASTNode {

    final Expr expr;
    final NeExprList neExprList;

    public NeExprList(Expr expr, NeExprList neExprList, Location loc) {
        super(loc);
        this.neExprList = neExprList;
        this.expr = expr;
    }
    public NeExprList(Expr expr, Location loc){
        super(loc);
        this.neExprList = null;
        this.expr = expr;
    }

    public NeExprList getNeExprList() {
        return neExprList;
    }
    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return neExprList.toString();
    }
}
