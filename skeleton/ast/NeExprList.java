package ast;

import java.util.LinkedList;
import java.util.List;

public class NeExprList extends ASTNode {

    final Expr arg;
    final NeExprList restNeExprList;

    public NeExprList(Expr expr, NeExprList neExprList, Location loc) {
        super(loc);
        this.restNeExprList = neExprList;
        this.arg = expr;
    }

    public List<Expr> getArguments() {
        List<Expr> args = new LinkedList<>();
        if (restNeExprList == null)  {
            // the right most parameter
            args = new LinkedList<>();
            args.add(arg);
        } else {
            args = restNeExprList.getArguments();
            args.add(0, arg);
        }
        return args;
    }

    public NeExprList getNeExprList() {
        return restNeExprList;
    }
    public Expr getExpr() {
        return arg;
    }

    @Override
    public String toString() {
        if (restNeExprList == null){
            return arg.toString();
        }
        return restNeExprList.toString();
    }
}
