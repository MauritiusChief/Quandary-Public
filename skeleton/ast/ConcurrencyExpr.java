package ast;

public class ConcurrencyExpr extends Expr
{
    final BinaryExpr binaryExpr;
    
    public  ConcurrencyExpr(BinaryExpr binaryExpr, Location loc){
        super(loc);
        this.binaryExpr = binaryExpr;
    }
    public BinaryExpr getBinaryExpr(){
        return binaryExpr;
    }
    @Override
    public String toString(){
        return "[" + binaryExpr.toString() + "]";
    }
    
}