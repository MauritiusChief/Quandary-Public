package ast;

public class FuncDefList extends ASTNode {

    final FuncDefList funcDefList;
    final FuncDef funcDef;

    public  FuncDefList(FuncDef funcDef,  FuncDefList  funcDefList, Location loc) {
        super(loc);
        this.funcDefList = funcDefList;
        this.funcDef = funcDef;
    }


    public  FuncDefList getFuncDefList() {
        return  funcDefList;
    }
    public FuncDef getFuncDef() {
        return funcDef;
    }

    @Override
    public String toString() {
        return funcDef.toString() +  funcDefList.toString();
    }
}