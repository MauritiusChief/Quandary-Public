package ast;

public class IdentExpr extends Expr {

    final String ident;

    public IdentExpr(String ident, Location loc) {
        super(loc);
        this.ident = ident;
    }

    public String getIdentStr() {
        return ident;
    }

    @Override
    public String toString() {
        return ident.toString();
    }
}