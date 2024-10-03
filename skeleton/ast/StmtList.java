package ast;

public class StmtList extends ASTNode {

    final StmtList stmtList;
    final Stmt stmt;

    public StmtList(Stmt stmt, StmtList stmtList, Location loc) {
        super(loc);
        this.stmtList = stmtList;
        this.stmt = stmt;
    }
    public StmtList(Location loc){
        super(loc);
        this.stmtList = null;
        this.stmt = null;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    public Stmt getStmt() {
        return stmt;
    }

    @Override
    public String toString() {
        if(stmtList == null){
            return stmt.toString();
        }
        return stmt.toString() + stmtList.toString();
    }
}