package ast;

public class Cond extends Expr {

    public static final int LE = 1;
    public static final int GE = 2;
    public static final int EQ = 3;
    public static final int NE = 4;
    public static final int LT = 5;
    public static final int GT = 6;
    public static final int AND = 7;
    public static final int OR = 8;
    public static final int NOT = 9;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public Cond(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }
    public Cond(Expr expr1, int operator, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = null;
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
            case LE:  s = "<="; break;
            case GE: s = ">="; break;
            case EQ: s = "=="; break;
            case NE:  s = "!="; break;
            case LT: s = "<"; break;
            case GT: s = ">"; break;
            case AND:  s = "&&"; break;
            case OR: s = "||"; break;
            case NOT: s = "!"; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
