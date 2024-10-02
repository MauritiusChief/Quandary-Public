package interpreter;

import java.io.*;
import java.util.Random;

import parser.ParserWrapper;
import ast.*;

import java.util.HashMap;
import java.util.Map;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
        return evaluateFuncDefList(astRoot.getFuncDefList(),arg);
    }

    Object evaluateFuncDefList(FuncDefList funcDefList, long arg) {
        FuncDef mainFunc = funcDefList.getFuncDef();
        String id = funcDefList.getFuncDef().getVarDecl().getIdent().getIdentStr();
        // System.out.println(id);
        if (id.equals("main")) {
            mainFunc = funcDefList.getFuncDef();
            Map<String, Long> variablesMap = new HashMap<>();
            return evaluateFuncDef(mainFunc, arg, variablesMap);
        } else {
            throw new RuntimeException("no main method");
        }
    }

    Long evaluateFuncDef(FuncDef funcDef, long arg, Map<String, Long> variablesMap) {
        if (funcDef.getFormalDeclList() != null) {
            evaluateFormalDeclList(funcDef.getFormalDeclList(), arg, variablesMap);
        }
        return evaluateStmtList(funcDef.getStmtList(), variablesMap);
    }

    Long evaluateStmtList(StmtList stmtList, Map<String, Long> variablesMap){
        // System.out.println(stmtList.getStmt().toString());
        Long stmt = evaluateStmt(stmtList.getStmt(), variablesMap);
        return stmt;
    }

    void evaluateFormalDeclList(FormalDeclList formalDeclList, long arg, Map<String, Long> variablesMap){
        evaluateNeFormalDeclList(formalDeclList.getNeFormalDeclList(), arg, variablesMap);
    }

    void evaluateNeFormalDeclList(NeFormalDeclList neFormalDeclList, long arg, Map<String, Long> variablesMap){
        variablesMap.put(neFormalDeclList.getVarDecl().getIdent().getIdentStr(), arg);
    }

    Long evaluateExprList(ExprList exprList, long arg, Map<String, Long> variablesMap) {
        Long value = evaluateNeExprList(exprList.getNeExprList(), arg, variablesMap);
        if (exprList.getNeExprList() != null){
            return evaluateNeExprList(exprList.getNeExprList(), arg, variablesMap);   
        }
        return value;
    }

    Long evaluateNeExprList(NeExprList neExprList, Long arg, Map<String, Long> variablesMap){
        Long exprValue = evaluateExpr(neExprList.getExpr(),variablesMap);
        if (neExprList.getNeExprList() != null) {
            return evaluateNeExprList(neExprList.getNeExprList(), arg, variablesMap);
        }
        return exprValue; 
    }

    boolean evaluateCond(Cond cond, Map<String, Long> variablesMap){
            switch(cond.getOperator()){
                case Cond.LE: return evaluateExpr(cond.getLeftExpr(),variablesMap) <= evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.GE: return evaluateExpr(cond.getLeftExpr(),variablesMap) >= evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.EQ: return evaluateExpr(cond.getLeftExpr(),variablesMap) == evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.NE: return evaluateExpr(cond.getLeftExpr(),variablesMap) != evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.LT: return evaluateExpr(cond.getLeftExpr(),variablesMap) < evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.GT: return evaluateExpr(cond.getLeftExpr(),variablesMap) > evaluateExpr(cond.getRightExpr(),variablesMap);
                case Cond.AND: return evaluateCond((Cond)cond.getLeftExpr(),variablesMap) && evaluateCond((Cond)cond.getRightExpr(),variablesMap);
                case Cond.OR: return evaluateCond((Cond)cond.getLeftExpr(),variablesMap) || evaluateCond((Cond)cond.getRightExpr(),variablesMap);
                case Cond.NOT: return !(evaluateCond((Cond)cond.getLeftExpr(),variablesMap));

                default: throw new RuntimeException("Unhandled operator");
            }
    }

    Long evaluateStmt(Stmt stmt, Map<String, Long> variablesMap){
        if (stmt instanceof DeclStmt){
            DeclStmt declStmt = (DeclStmt)stmt;
            String varName = declStmt.getVarDecl().getIdent().getIdentStr();
            Long value = evaluateExpr(declStmt.getExpr(),variablesMap);
            variablesMap.put(varName, value);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStatement = (IfStmt)stmt;
            boolean condition = evaluateCond(ifStatement.getCond(),variablesMap);
            Long value = null;
            if (condition){
                value = evaluateStmt(ifStatement.getStmt(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfElseStmt){
            IfElseStmt ifElseStmt = (IfElseStmt)stmt;
            boolean condition = evaluateCond(ifElseStmt.getCond(),variablesMap);
            Long value = null;
            if (condition){
                value = evaluateStmt(ifElseStmt.getStmt1(), variablesMap);
            } else {
                value = evaluateStmt(ifElseStmt.getStmt2(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof PrintStmt) {
            PrintStmt printStmt = (PrintStmt)stmt;
            Long value = evaluateExpr(printStmt.getExpr(), variablesMap);
            System.out.println(value);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt)stmt;
            Long value = evaluateExpr(returnStmt.getExpr(),variablesMap);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof StmtBlock) {
            StmtBlock stmtBlock = (StmtBlock)stmt;
            Long value = evaluateStmtList(stmtBlock.getStmtList(), variablesMap);
            // System.out.println(value);
            return value;
        } else {
            throw new RuntimeException("Unhandled Stmt type");
        }
    }

    Long evaluateExpr(Expr expr, Map<String, Long> variablesMap) {
        if (expr instanceof ConstExpr) {
            return (Long) ((ConstExpr)expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return (Long)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap) + (Long)evaluateExpr(binaryExpr.getRightExpr(),variablesMap);
                case BinaryExpr.MINUS: return (Long)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap) - (Long)evaluateExpr(binaryExpr.getRightExpr(),variablesMap);
                case BinaryExpr.TIMES: return (Long)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap) * (Long)evaluateExpr(binaryExpr.getRightExpr(),variablesMap);
                default: throw new RuntimeException("Unhandled Binary operator");
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            switch (unaryExpr.getOperator()) {
                case UnaryExpr.NEGATE: return - (Long)evaluateExpr(unaryExpr.getExpr(),variablesMap);
                default: throw new RuntimeException("Unhandled Unary operator");
            }
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
