package ast;

public class Type extends ASTNode {

    public static final int INT = 1;

    public final int type;

    public Type(int type, Location loc) {
        super(loc);
        this.type = type;
    }

    public int getType() {
        return type;
    }


    @Override
    public String toString() {
        String s = null;
        switch (type) {
            case 1:  s = "int"; break;
        }
        return s + " ";
    }
}
