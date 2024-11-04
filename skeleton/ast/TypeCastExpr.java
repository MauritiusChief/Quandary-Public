package ast;

public class TypeCastExpr extends Expr {

    final Type type;
    final Expr expr;

    public TypeCastExpr(Type type, Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
        this.type = type;
    }

    public Type getType() {
        return type;
    }
    public Expr getExpr() {
        return expr;
    }


    @Override
    public String toString() {
        return "(" + type.toString() + ")" + expr;
    }
}
