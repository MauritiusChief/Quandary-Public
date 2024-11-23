package ast;

public class TypeDecl extends ASTNode {

    public enum Type {INT, REF, Q};
    final Type t;

    public TypeDecl(Type type, Location loc) {
        super(loc);
        this.t = type;
    }

    public Type getType() {
        return this.t;
    }


    @Override
    public String toString() {
        String s = null;
        switch (t) { 
            case INT: s = "int"; break;
            case Q: s = "Q"; break;
            case REF: s = "Ref"; break;
        } 
        return s + " ";
    }
}
