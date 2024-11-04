package ast;

public class QdryInt extends QdryVal {
    final Long qrdyInt;
    
    public QdryInt(long qrdyInt) {
        this.qrdyInt = qrdyInt;
    }

    public long getInt() {
        return qrdyInt;
    }

    @Override
    public String toString() {
        return qrdyInt.toString();
    }
}
