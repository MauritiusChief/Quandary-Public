package ast;

public class NeFormalDeclList extends ASTNode {

    final VarDecl varDecl;
    final NeFormalDeclList neFormalDeclList;


    public NeFormalDeclList(VarDecl varDecl, NeFormalDeclList neFormalDeclList, Location loc) {
        super(loc);
        this.neFormalDeclList = neFormalDeclList;
        this.varDecl = varDecl;
    }


    public NeFormalDeclList getNeFormalDeclList() {
        return neFormalDeclList;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }

    @Override
    public String toString() {
        return varDecl.toString() + "," + neFormalDeclList.toString();
    }
}