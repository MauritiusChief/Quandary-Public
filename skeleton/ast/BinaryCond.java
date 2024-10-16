package ast;

public class BinaryCond extends Cond {

    final Cond cond1;
    final Operator operator;
    final Cond cond2;

    public BinaryCond(Cond cond1, Operator operator, Cond cond2, Location loc) {
        super(loc);
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }

    public Cond getLeftCond() {
        return cond1;
    }

    public Operator getOperator() {
        return operator;
    }
    
    public Cond getRightCond() {
        return cond2;
    }

    @Override
    public String toString() {
        String s = operator.toString();
        return "(" + cond1 + " " + s + " " + cond2 + ")";
    }
}
