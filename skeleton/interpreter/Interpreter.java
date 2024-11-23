package interpreter;

import interpreter.SExpression;
import java.io.*;
import java.util.Random;
import java.util.TreeMap;

import parser.ParserWrapper;
import ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
    private static boolean returnFlag = false;
    // Boolean[] returnFlag = {false};
    private static HashMap<String, FuncDef> functionMapping = new HashMap<String, FuncDef>();

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
        astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;
    private Map<String, FuncDef> methods;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
        this.methods = astRoot.getMethods();
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
        FuncDef mainFunc = this.methods.get("main");
        String id = mainFunc.getParamNames().get(0);

        if (mainFunc.getName().equals("main")) {
            Map<String, Object> variablesMap = new HashMap<>();
            variablesMap.put(id, arg);
            return evaluateStmtList(mainFunc.getStmtList(), variablesMap);
        } else {
            throw new RuntimeException("no main method");
        }
    }

    Object evaluateStmtList(StmtList stmtList, Map<String, Object> variablesMap){
        Object stmt = evaluateStmt(stmtList.getStmt(), variablesMap);
        if (stmt == null) {
            return evaluateStmtList(stmtList.getStmtList(), variablesMap);
        } else {
            return stmt;
        }
    }

    void evaluateFormalDeclList(FormalDeclList formalDeclList, ArrayList<Object> args, Map<String, Object> variablesMap){
        evaluateNeFormalDeclList(formalDeclList.getNeFormalDeclList(), args, variablesMap);
    }

    void evaluateNeFormalDeclList(NeFormalDeclList neFormalDeclList, ArrayList<Object> args, Map<String, Object> variablesMap){
        int i = 0;
        variablesMap.put(neFormalDeclList.getVarDecl().getName(), args.get(i));
        while (neFormalDeclList.getNeFormalDeclList() != null){
            neFormalDeclList = neFormalDeclList.getNeFormalDeclList();
            i++;
            variablesMap.put(neFormalDeclList.getVarDecl().getName(), args.get(i));
        }
    }

    Object evaluateExprList(ExprList exprList, ArrayList<Object> args, Map<String, Object> variablesMap) {
        Object value = evaluateNeExprList(exprList.getNeExprList(), args, variablesMap);
        if (exprList.getNeExprList() != null){
            return evaluateNeExprList(exprList.getNeExprList(), args, variablesMap);   
        }
        return value;
    }

    Object evaluateNeExprList(NeExprList neExprList, ArrayList<Object> args, Map<String, Object> variablesMap){
        Object exprValueFirst = evaluateExpr(neExprList.getExpr(),variablesMap);
        if (neExprList.getNeExprList() != null) {
            return evaluateNeExprList(neExprList.getNeExprList(), args, variablesMap);
        }
        return exprValueFirst; 
    }

    boolean evaluateCond(Cond cond, Map<String, Object> variablesMap){
            if(cond instanceof RelationalCond) {
                RelationalCond relCond = (RelationalCond) cond;
                Long leftExpr = (Long)evaluateExpr(relCond.getLeftExpr(),variablesMap);
                Long rightExpr = (Long)evaluateExpr(relCond.getRightExpr(), variablesMap);
                switch (relCond.getOperator()) {
                    case LE: return leftExpr <= rightExpr;
                    case GE: return leftExpr >= rightExpr;
                    case EQ: return leftExpr == rightExpr;
                    case NE: return leftExpr != rightExpr;
                    case LT: return leftExpr < rightExpr;
                    case GT: return leftExpr > rightExpr;
                    default: throw new RuntimeException("Unhandled relational condition operator");
                }

            } else if (cond instanceof BinaryCond) {
                BinaryCond binCond = (BinaryCond) cond;
                boolean leftCond = evaluateCond(binCond.getLeftCond(), variablesMap);
                boolean rightCond = evaluateCond(binCond.getRightCond(), variablesMap);
                switch (binCond.getOperator()) {
                    case AND: return leftCond && rightCond;
                    case OR: return leftCond || rightCond;
                    default: throw new RuntimeException("Unhandled binary condition operator");
                }
            } else if (cond instanceof NegationCond) {
                return !(evaluateCond(((NegationCond)cond).getCond(),variablesMap));
            } else {
                throw new RuntimeException("Unhandled condition type");
            }
    }

    Object evaluateStmt(Stmt stmt, Map<String, Object> variablesMap){
        if (stmt instanceof DeclStmt){
            DeclStmt declStmt = (DeclStmt)stmt;
            String varName = declStmt.getVarDecl().getName();
            Object value = evaluateExpr(declStmt.getExpr(),variablesMap);
            variablesMap.put(varName, value);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStatement = (IfStmt)stmt;
            boolean condition = evaluateCond(ifStatement.getCond(),variablesMap);
            Object value = null;
            if (condition){
                value = evaluateStmt(ifStatement.getStmt(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfElseStmt){
            IfElseStmt ifElseStmt = (IfElseStmt)stmt;
            boolean condition = evaluateCond(ifElseStmt.getCond(),variablesMap);
            Object value = null;
            if (condition){
                value = evaluateStmt(ifElseStmt.getStmt1(), variablesMap);
            } else {
                value = evaluateStmt(ifElseStmt.getStmt2(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof PrintStmt) {
            PrintStmt printStmt = (PrintStmt)stmt;
            Object value = evaluateExpr(printStmt.getExpr(), variablesMap);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt)stmt;
            String name = assignStmt.getName();
            Object value = evaluateExpr(assignStmt.getExpr(), variablesMap);
            variablesMap.put(name, value);
            return null;
        } else if (stmt instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt)stmt;
            boolean test = evaluateCond(whileStmt.getCond(), variablesMap);
            while (test) {
                Object value = evaluateStmt(whileStmt.getStmt(), variablesMap);
                if (value != null) return value;
                test = evaluateCond(whileStmt.getCond(), variablesMap);
            }
            return null;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt)stmt;
            Object value = evaluateExpr(returnStmt.getExpr(),variablesMap);
            returnFlag = true;
            // returnFlag[0] = true;
            // System.out.println(value);
            return value;
        } else if (stmt instanceof StmtBlock) {
            StmtBlock stmtBlock = (StmtBlock)stmt;
            Object value = evaluateStmtList(stmtBlock.getStmtList(), variablesMap);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof CallStmt) {
            CallExpr call = ((CallStmt) stmt).getCall();
            evaluateCallExpr(call, variablesMap);
            return null;
        } else {
            throw new RuntimeException("Unhandled Stmt type");
        }
    }

    Object evaluateCallExpr(CallExpr callExpr, Map<String, Object> variablesMap) {
        List<Object> actual = new LinkedList<>();
        for (Expr e : callExpr.getArguments()) {
            actual.add(evaluateExpr(e, variablesMap));
        }
        ExprList exprList = callExpr.getExprList();
        if (exprList == null) {
            throw new IllegalStateException("exprList is null in CallExpr");
        }
        // if (exprList != null) {
        //     NeExprList neExprList = exprList.getNeExprList();
        //     // if (neExprList == null) {
        //     //     throw new IllegalStateException("neExprList is null in CallExpr");
        //     // }
        //     args.add(neExprList.getExpr());
        //     while (neExprList.getNeExprList() != null) {
        //         neExprList = neExprList.getNeExprList();
        //         args.add(neExprList.getExpr());
        //     }
        // }
        Object value = evaluateExpr((callExpr).getExprList().getNeExprList().getExpr(), variablesMap);
        if (value == null) {
            throw new IllegalStateException("value is null in CallExpr");
        }
        switch ((callExpr.getName())) {
            case "randomInt":
                Long randomInt = (long)random.nextInt((int)(actual.get(0)));
                return randomInt;
            case "right":
                return ((SExpression) actual.get(0)).right();
            case "left":
                return ((SExpression) actual.get(0)).left();
            case "setRight":
                ((SExpression) actual.get(0)).setRight(actual.get(1));
                return 1L;
            case "setLeft":
                ((SExpression) actual.get(0)).setLeft(actual.get(1));
                return 1L;
            case "isAtom":
                if (actual.get(0) == SExpression.NIL || actual.get(0) instanceof Long) return 1L;
                return 0L;
            case "isNil":
                if (actual.get(0) == SExpression.NIL) return 1L;
                return 0L;
            default: 
                FuncDef method = this.methods.get(callExpr.getName());
                List<String> formalParamsNames = method.getParamNames();
                Map<String, Object> callContexMap = new TreeMap<>();
                for (int i=0; i<actual.size(); i++) {
                    callContexMap.put(formalParamsNames.get(i), actual.get(i));
                }
                return evaluateStmtList(method.getStmtList(), callContexMap);
        }
    }

    Object evaluateExpr(Expr expr, Map<String, Object> variablesMap) {
        // System.out.println(expr.toString())
        if (expr instanceof ConstExpr) {
            return (Long)((ConstExpr)expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            if (binaryExpr.getOperator() == BinaryExpr.DOT) {
                return new SExpression(evaluateExpr(binaryExpr.getLeftExpr(), variablesMap), evaluateExpr(binaryExpr.getRightExpr(), variablesMap));
            } else {
                Long leftInt = (Long)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap);
                Long rightInt = (Long)evaluateExpr(binaryExpr.getRightExpr(),variablesMap);
                switch (binaryExpr.getOperator()) {
                    case BinaryExpr.PLUS: return (Long)(leftInt + rightInt);
                    case BinaryExpr.MINUS: return (Long)(leftInt - rightInt);
                    case BinaryExpr.TIMES: return (Long)(leftInt * rightInt);
                    default: throw new RuntimeException("Unhandled Binary operator");
                }
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            switch (unaryExpr.getOperator()) {
                case UnaryExpr.NEGATE: return  - ((Long)evaluateExpr(unaryExpr.getExpr(),variablesMap));
                default: throw new RuntimeException("Unhandled Unary operator");
            }
        } else if(expr instanceof IdentExpr){        
            IdentExpr identExpr = (IdentExpr)expr;
            return variablesMap.get(identExpr.getName());
        } else if(expr instanceof CallExpr){
            return evaluateCallExpr((CallExpr)expr, variablesMap);
        } else if (expr instanceof TypeCastExpr) {
            TypeCastExpr typeCastExpr = (TypeCastExpr)expr;
            Object result = evaluateExpr(typeCastExpr.getExpr(), variablesMap);
            switch (typeCastExpr.getType()) {
                case INT:
                    if (!(result instanceof Long)) fatalError("Cannot cast int", EXIT_DYNAMIC_TYPE_ERROR);
                    return result;
                case REF:
                    if (!(result instanceof SExpression)) fatalError("Cannot cast Ref", EXIT_DYNAMIC_TYPE_ERROR);
                    return result;
                case Q:
                    return result;
                default:
                    fatalError("Unimplemented type", EXIT_DYNAMIC_TYPE_ERROR);
                    return result;
            }
        } else if (expr instanceof NilExpr) {
            return SExpression.NIL;
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
