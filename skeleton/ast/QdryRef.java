package ast;

public class QdryRef extends QdryVal {
    public QdryQ qrdyQ;
    
    public QdryRef(QdryQ qrdyQ) {
        this.qrdyQ = qrdyQ;
    }

    public QdryQ getRef() {
        return qrdyQ;
    }

    @Override
    public String toString() {
        if (qrdyQ == null) {
            return "nil";
        }
        return "("+qrdyQ.toString()+")";
    }
}
