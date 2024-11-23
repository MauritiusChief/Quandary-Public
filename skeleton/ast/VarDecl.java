package ast;

import ast.TypeDecl.Type;

public class VarDecl extends ASTNode {

    final boolean multiple;
    // final IdentExpr ident;
    final String name;
    final Type t;

    public VarDecl(boolean multiple, Type type, String name, Location loc) {
    // public VarDecl(boolean multiple, Type type, IdentExpr ident, Location loc) {
        super(loc);
        this.multiple = multiple;
        // this.ident = ident;
        this.name = name;
        this.t = type;
    }

    public boolean isMutable() {
        return this.multiple;
    }

    // public IdentExpr getIdent() {
    //     return ident;
    // }
    public String getName() {
        return this.name;
    }
    public Type getType() {
        return this.t;
    }

    @Override
    public String toString() {
        return t.toString() + this.name;
    }
}