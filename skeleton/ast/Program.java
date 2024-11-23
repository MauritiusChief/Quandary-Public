package ast;

import java.io.PrintStream;
import java.util.Map;

public class Program extends ASTNode {

    final FuncDefList functions;

    public Program(FuncDefList funcDefList, Location loc) {
        super(loc);
        this.functions = funcDefList;
    }

    public Map<String, FuncDef> getMethods() {
        return functions.getMethods();
    }

    public FuncDefList getFuncDefList() {
        return functions;
    }

    public void println(PrintStream ps) {
        ps.println(functions);
    }
}
