package ast;

public class FormalDeclList extends ASTNode {

    final NeFormalDeclList neFormalDeclList;


    public FormalDeclList(NeFormalDeclList neFormalDeclList, Location loc) {
        super(loc);
        this.neFormalDeclList = neFormalDeclList;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return neFormalDeclList;
    }

    @Override
    public String toString() {
        return neFormalDeclList.toString();
    }
}