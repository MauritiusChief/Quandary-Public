package ast;

public class RefExpr extends Expr {

    public static final int DOT = 1;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public RefExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }

    public Expr getLeftExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case DOT:  s = "."; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
