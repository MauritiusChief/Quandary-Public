package ast;

import java.util.LinkedList;
import java.util.List;

public class CallExpr extends Expr
{
    // final IdentExpr id;
    final String name;
    final ExprList args;
    
    // public  CallExpr(IdentExpr id, ExprList exprList, Location loc){
    public  CallExpr(String name, ExprList exprList, Location loc){
        super(loc);
        this.name = name;
        this.args = exprList;
    }

    public List<Expr> getArguments() {
        List<Expr> result;
        if (this.args == null) {
            result = new LinkedList<>();
        } else {
            result = args.getNeExprList().getArguments();
        }
        return result;
    }

    // public IdentExpr getIdent(){
    //     return id;
    // }
    public String getName(){
        return this.name;
    }
    public ExprList getExprList(){
        return args;
    }
    @Override
    public String toString(){
        if (args == null){
            return name + "()";
        }
        return name + "(" + args.toString() + ")";
    }
    
}