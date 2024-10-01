package ast;

public class UnaryExpr extends Expr {

    public static final int NEGATE = 1;

    final Expr expr;
    final int operator;

    public UnaryExpr(Expr expr, int operator, Location loc) {
        super(loc);
        this.expr = expr;
        this.operator = operator;
    }

    public Expr getExpr() {
        return expr;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case NEGATE: s = "-"; break;
        }
        return "(" + s + expr + ")";
    }
}
