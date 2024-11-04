package ast;

public abstract class QdryVal {
    @Override
    public String toString() {
        if (this instanceof QdryRef) {
            return ((QdryRef)this).toString();
        } else {
            return ((QdryInt)this).toString();
        }
    }
}
