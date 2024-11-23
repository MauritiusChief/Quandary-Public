package ast;

import java.util.HashMap;
import java.util.Map;

public class FuncDefList extends ASTNode {

    final FuncDefList funcDefList;
    final FuncDef funcDef;

    public  FuncDefList(FuncDef funcDef,  FuncDefList  funcDefList, Location loc) {
        super(loc);
        this.funcDefList = funcDefList;
        this.funcDef = funcDef;
    }

    Map<String, FuncDef> getMethods() {
        Map<String, FuncDef> functions;
        if (this.funcDefList == null) {
            functions = new HashMap<>();
        } else {
            functions = funcDefList.getMethods();
        }
        functions.put(funcDef.getName(), funcDef);
        return functions;
    }

    public  FuncDefList getFuncDefList() {
        return  funcDefList;
    }
    public FuncDef getFuncDef() {
        return funcDef;
    }

    @Override
    public String toString() {
        if(funcDefList == null){
            return funcDef.toString();
        }
        return funcDef.toString() +  funcDefList.toString();
    }
}