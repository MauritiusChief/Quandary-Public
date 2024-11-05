package ast;

public abstract class QdryVal extends QdryQ {

    public QdryVal() {
        super(QdryNil.getInstance(), QdryNil.getInstance());
    }

    @Override
    public String toString() {
        if (this instanceof QdryRef) {
            return ((QdryRef)this).toString();
        } else if (this instanceof QdryInt) {
            return ((QdryInt)this).toString();
        } else {
            return ((QdryNil)this).toString();
        }
    }
}
