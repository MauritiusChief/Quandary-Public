package ast;

public class QdryNil extends QdryRef {
    
    private static final QdryNil instance = new QdryNil();

    private QdryNil() {
        super(instance);
    }

    public static QdryNil getInstance() {
        return instance;
    }

    @Override
    public QdryVal getRight() {
        return instance;
    }

    @Override
    public QdryVal getLeft() {
        return instance;
    }

    @Override
    public String toString() {
        return "nil";
    }
}