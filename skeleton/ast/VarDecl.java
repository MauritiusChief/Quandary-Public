package ast;

public class VarDecl extends ASTNode {

    final IdentExpr ident;

    public VarDecl(IdentExpr ident, Location loc) {
        super(loc);
        this.ident = ident;
    }

    public IdentExpr getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return ident.toString();
    }
}