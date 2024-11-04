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
    }

    public QdryVal getRight() {
        return right;
    }

    @Override
    public String toString() {
        return left.toString() + "." + right.toString();
    }
}
