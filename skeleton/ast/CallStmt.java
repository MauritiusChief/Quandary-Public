package ast;

public class CallStmt extends Stmt
{
    final CallExpr call;
    
    public  CallStmt(IdentExpr id, ExprList exprList, Location loc){
        super(loc);
        this.call = new CallExpr(id, exprList, loc);
    }
    public CallExpr getCall() {
        return call;
    }
    @Override
    public String toString(){
        return call.toString();
    }
    
}