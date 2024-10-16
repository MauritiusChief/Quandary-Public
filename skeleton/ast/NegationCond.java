package ast;

public class NegationCond extends Cond {

    final Cond cond;

    public NegationCond(Cond cond, Location loc) {
        super(loc);
        this.cond = cond;
    }

    public Expr getCond() {
        return cond;
    }

    @Override
    public String toString() {
        return "!(" + cond + ")";
    }
}
