package ast;

public class QdryQ {
    public QdryVal left;
    public QdryVal right;

    public QdryQ(QdryVal left, QdryVal right) {
        this.left = left;
        this.right = right;
    }

    public QdryVal getLeft() {
        return left;
        // return left != null ? left : QdryNil.getInstance();
    }
    
    public QdryVal getRight() {
        return right;
        // return right != null ? right : QdryNil.getInstance();
    }

    @Override
    public String toString() {
        if (left == QdryNil.getInstance() && right == QdryNil.getInstance()) {
            return "nil";
        }
        if (left == QdryNil.getInstance()) {
            return "nil ." + right.toString();
        }
        if (right == QdryNil.getInstance()) {
            return left.toString() + " . nil";
        }
        return left.toString() + " . " + right.toString();
    }
}
