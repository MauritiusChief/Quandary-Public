package ast;

public class CallExpr extends Expr
{
    final IdentExpr id;
    final ExprList exprList;
    
    public  CallExpr(IdentExpr id, ExprList exprList, Location loc){
        super(loc);
        this.id = id;
        this.exprList = exprList;
    }
    public IdentExpr getId(){
        return id;
    }
    public ExprList getExprList(){
        return exprList;
    }
    @Override
    public String toString(){
        return id.toString() + "(" + exprList.toString() + ")";
    }
    
}