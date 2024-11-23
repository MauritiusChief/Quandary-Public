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
        // String s = null;
        // switch (type) { 
        //     case 1: s = "int"; break;
        //     case 2: s = "Q"; break;
        //     case 3: s = "Ref"; break;
        // } 
        // return s + " ";
        return "";
    }
}
