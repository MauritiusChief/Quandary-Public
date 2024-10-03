package ast;

public class VarDecl extends ASTNode {

    final IdentExpr ident;
    final Type type;

    public VarDecl(Type type, IdentExpr ident, Location loc) {
        super(loc);
        this.ident = ident;
        this.type = type;
    }

    public IdentExpr getIdent() {
        return ident;
    }
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString() + ident.toString();
    }
}