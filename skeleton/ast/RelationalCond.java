package ast;

public class RelationalCond extends Cond {

    final Expr expr1;
    final Operator operator;
    final Expr expr2;

    public RelationalCond(Expr expr1, Operator operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public Operator getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    @Override
    public String toString() {
        String s = operator.toString();
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
