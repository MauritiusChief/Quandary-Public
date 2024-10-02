package ast;

public class FuncDef extends ASTNode {

    final VarDecl varDecl;
    final FormalDeclList formalDeclList;
    final StmtList stmtList;

    public FuncDef(VarDecl varDecl, FormalDeclList formalDeclList,StmtList stmtList, Location loc) {
        super(loc);
        this.formalDeclList = formalDeclList;
        this.varDecl = varDecl;
        this.stmtList = stmtList;
    }


    public FormalDeclList getFormalDeclList() {
        return formalDeclList;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }
    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return varDecl.getIdent().getIdentStr() + "(" + formalDeclList.toString()+")"+"{"+stmtList.toString()+"}";
    }
}