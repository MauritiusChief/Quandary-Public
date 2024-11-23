package ast;

import java.util.LinkedList;
import java.util.List;

public class FuncDef extends ASTNode {

    final VarDecl varDecl;
    // final String name;
    final FormalDeclList formalDeclList;
    final StmtList body;

    public FuncDef(VarDecl varDecl, FormalDeclList formalDeclList,StmtList stmtList, Location loc) {
    // public FuncDef(String name, FormalDeclList formalDeclList,StmtList stmtList, Location loc) {
        super(loc);
        this.formalDeclList = formalDeclList;
        this.varDecl = varDecl;
        // this.name = name;
        this.body = stmtList;
    }

    public String getName() {
        return this.varDecl.getName();
        // return this.name;
    }
    public List<String> getParamNames(){
        List<String> names;
        if (this.formalDeclList == null) {
            // There arer no parameters
            names = new LinkedList<>();
        } else {
            names = this.formalDeclList.getNeFormalDeclList().getNames();
        }
        return names;
    }

    public FormalDeclList getFormalDeclList() {
        return formalDeclList;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }
    public StmtList getStmtList() {
        return body;
    }

    @Override
    public String toString() {
        if (formalDeclList == null){
            return varDecl.getName() + "() {" + body.toString()+"}" ;
        }
        return varDecl.getName() + "(" + formalDeclList.toString()+") {"+body.toString()+"}";
    }
}