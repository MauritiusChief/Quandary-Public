package interpreter;

public class SExpression {
    
    private Object left,  right;

    public static final SExpression NIL = new SExpression(null, null);

    public SExpression(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    public Object left() {
        return this.left;
    }
    public Object right() {
        return this.right;
    }
    public void setRight(Object newRight) {
        this.right = newRight;
    }
    public void setLeft(Object newLeft) {
        this.left = newLeft;
    }

    @Override
    public String toString() {
        if (this == NIL) return "nil";
        return "("+left+" . "+right+")";
    }

}
