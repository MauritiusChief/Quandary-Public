package ast;

public class CallStmt extends Stmt
{
    final String ident;
    final ExprList exprList;
    
    public  CallStmt(String ident, ExprList exprList, Location loc){
        super(loc);
        this.ident = ident;
        this.exprList = exprList;
    }
    public String getIdentStr() {
        return ident;
    }
    public ExprList getExprList() {
        return exprList;
    }
    @Override
    public String toString(){
        if (exprList == null){
            return ident + "()";
        }
        return ident + "(" + exprList.toString() + ")";
    }
    
}