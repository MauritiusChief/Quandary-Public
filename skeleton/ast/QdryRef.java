package ast;

public class QdryRef extends QdryVal {
    public QdryQ qrdyQ;
    
    public QdryRef(QdryQ qrdyQ) {
        this.qrdyQ = qrdyQ;
    }

    public QdryQ getRef() {
        return qrdyQ;
        // return qrdyQ != null ? qrdyQ : QdryNil.getInstance();
    }

    @Override
    public String toString() {
        if (qrdyQ == null || qrdyQ == QdryNil.getInstance()) {
            return "nil";
        }
        return "(" + qrdyQ.toString() + ")";
    }
}
