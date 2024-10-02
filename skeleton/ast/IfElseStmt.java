package ast;

import java.io.PrintStream;

public class IfElseStmt extends Stmt {

    final Cond cond;
    final Stmt stmt1;
    final Stmt stmt2;

    public IfElseStmt(Cond cond, Stmt stmt1, Stmt stmt2, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt1 = stmt1;
        this.stmt2 = stmt2;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt1() {
        return stmt1;
    }
    public Stmt getStmt2() {
        return stmt2;
    }

    @Override
    public String toString() {
        return "if (" + cond.toString() + ")" + stmt1.toString() + "else" + stmt2.toString();
    }
}
