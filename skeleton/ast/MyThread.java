package ast;

import java.util.Map;

import interpreter.Interpreter;

public class MyThread extends Thread{
    private final Expr expr;
    private QdryVal val;
    private final Map<String, QdryVal> map;

    public MyThread(Expr expr, Map<String, QdryVal> map) {
        this.expr = expr;
        this.map = map;
    }
    @Override
    public void run() {
        Interpreter interpreter = Interpreter.getInterpreter();
        QdryVal result = interpreter.evaluateExpr(expr, map);
        setVal(result);
    }

     public synchronized QdryVal getVal() {
        return val;
    }

    private synchronized void setVal(QdryVal val) {
        this.val = val;
    }
}