package ast;

import java.util.LinkedList;
import java.util.List;

public class NeFormalDeclList extends ASTNode {

    final VarDecl varDecl;
    final NeFormalDeclList neFormalDeclList;


    public NeFormalDeclList(VarDecl varDecl, NeFormalDeclList neFormalDeclList, Location loc) {
        super(loc);
        this.neFormalDeclList = neFormalDeclList;
        this.varDecl = varDecl;
    }

    public List<String> getNames() {
        List<String> names = new LinkedList<>();
        if (neFormalDeclList == null)  {
            // the right most parameter
            names = new LinkedList<>();
            names.add(varDecl.getIdent().getIdentStr());
        } else {
            names = neFormalDeclList.getNames();
            names.add(0, varDecl.getIdent().getIdentStr());
        }
        return names;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return neFormalDeclList;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }

    @Override
    public String toString() {
        if (neFormalDeclList == null){
            return varDecl.toString();
        }
        return varDecl.toString() + "," + neFormalDeclList.toString();
    }
}