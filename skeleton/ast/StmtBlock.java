package ast;

public class StmtBlock extends Stmt {

    final StmtList stmtList;

    public StmtBlock(StmtList stmtList, Location loc) {
        super(loc);
        this.stmtList = stmtList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return "{" + stmtList.toString() + "}" ;
    }
}