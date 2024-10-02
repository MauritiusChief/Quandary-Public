package ast;

public class ExprList extends ASTNode {

    final NeExprList neExprList;

    public ExprList(NeExprList neExprList, Location loc) {
        super(loc);
        this.neExprList = neExprList;
    }
    public ExprList(Location loc){
        super(loc);
        this.neExprList = null;
    }

    public NeExprList getNeExprList() {
        return neExprList;
    }

    @Override
    public String toString() {
        return neExprList.toString();
    }
}
