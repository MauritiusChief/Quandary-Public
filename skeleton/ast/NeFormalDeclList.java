package ast;

import java.util.LinkedList;
import java.util.List;
import ast.TypeDecl.Type;

public class NeFormalDeclList extends ASTNode {

    final VarDecl varDecl;
    final NeFormalDeclList restNeFormalDeclList;

    public NeFormalDeclList(VarDecl varDecl, NeFormalDeclList neFormalDeclList, Location loc) {
        super(loc);
        this.restNeFormalDeclList = neFormalDeclList;
        this.varDecl = varDecl;
    }

    public List<String> getNames() {
        List<String> names = new LinkedList<>();
        if (restNeFormalDeclList == null)  {
            // the right most parameter
            names = new LinkedList<>();
            names.add(varDecl.getName());
        } else {
            names = restNeFormalDeclList.getNames();
            names.add(0, varDecl.getName());
        }
        return names;
    }

    public List<Type> getTypes() {
        List<Type> types = new LinkedList<>();
        if (restNeFormalDeclList == null)  {
            // the right most parameter
            types = new LinkedList<>();
        } else {
            types = restNeFormalDeclList.getTypes();
        }
        types.add(0, varDecl.getType());
        return types;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return restNeFormalDeclList;
    }
    public VarDecl getVarDecl() {
        return varDecl;
    }

    @Override
    public String toString() {
        if (restNeFormalDeclList == null){
            return varDecl.toString();
        }
        return varDecl.toString() + "," + restNeFormalDeclList.toString();
    }
}