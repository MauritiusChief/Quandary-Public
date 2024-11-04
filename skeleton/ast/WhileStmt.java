package ast;

public class WhileStmt extends Stmt{
    final Cond cond;
    final Stmt stmt;

    public WhileStmt(Cond cond, Stmt stmt, Location loc) {
        super(loc);
        this.cond = cond;
        this.stmt = stmt;
    }

    public Cond getCond() {
        return cond;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public String toString() {
        return "while (" + cond.toString() + ") {" + stmt + "}";
    }
}
