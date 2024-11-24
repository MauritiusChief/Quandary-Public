package ast;

import java.util.Map;

import interpreter.Interpreter;

public class MyThread extends Thread{
    final Expr expr;
    QdryVal val;
    final Map<String, QdryVal> map;
    public MyThread(Expr expr, Map<String, QdryVal> map) {
        this.expr = expr;
        this.map = map;
    }
    @Override
    public void run() {
        Interpreter interpreter = Interpreter.getInterpreter();
        val = interpreter.evaluateExpr(expr, map);
    }

    public QdryVal getVal() {
        return val;
    }
}