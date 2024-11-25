package interpreter;

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

    QdryVal executeRoot(Program astRoot, long arg) {
        return evaluateFuncDefList(astRoot.getFuncDefList(),arg);
    }

    QdryVal evaluateFuncDefList(FuncDefList funcDefList, long arg) {
        ArrayList<QdryVal> args = new ArrayList<>();
        args.add(new QdryInt(arg));
        FuncDef mainFunc = funcDefList.getFuncDef();
        String id = funcDefList.getFuncDef().getVarDecl().getIdent().getIdentStr();
        // System.out.println(id);
        if (id.equals("main")) {
            mainFunc = funcDefList.getFuncDef();
        }
        functionMapping.put(id, funcDefList.getFuncDef());
        while (funcDefList.getFuncDefList() != null) {
            funcDefList = funcDefList.getFuncDefList();
            id = funcDefList.getFuncDef().getVarDecl().getIdent().getIdentStr();
            //System.out.println(id);
            if (id.equals("main")) {
                mainFunc = funcDefList.getFuncDef();
            }
            functionMapping.put(id, funcDefList.getFuncDef());
        }

        if (mainFunc.getVarDecl().getIdent().getIdentStr().equals("main")) {
            Map<String, QdryVal> variablesMap = new HashMap<>();
            // returnFlag[0] = false;
            returnFlag = false;
            return evaluateFuncDef(mainFunc, args, variablesMap);
        } else {
            throw new RuntimeException("no main method");
        }
    }

    QdryVal evaluateFuncDef(FuncDef funcDef, ArrayList<QdryVal> args, Map<String, QdryVal> variablesMap) {
        if (funcDef.getFormalDeclList() != null) {
            evaluateFormalDeclList(funcDef.getFormalDeclList(), args, variablesMap);
        }
        // Object result = interpreter.evaluateStmtList(funcDef.getStmtList(), variablesMap);
        // if (result == null) {
        //     throw new NullPointerException("evaluateStmtList returned null");
        // }
        return evaluateStmtList(funcDef.getStmtList(), variablesMap);
    }

    QdryVal evaluateStmtList(StmtList stmtList, Map<String, QdryVal> variablesMap){
        QdryVal stmt = evaluateStmt(stmtList.getStmt(), variablesMap);
        if (stmt == null) {
            throw new NullPointerException("evaluateStmt returned null");
        }
        // if (returnFlag[0]) {
        if (returnFlag == true) {
            return stmt;
        }
        while (stmtList.getStmtList() != null)
        {
            stmtList = stmtList.getStmtList();
            stmt = evaluateStmt(stmtList.getStmt(), variablesMap);

            if(returnFlag == true){
            // if(returnFlag[0]){
                break;
            }
        }
        return stmt;
    }

    void evaluateFormalDeclList(FormalDeclList formalDeclList, ArrayList<QdryVal> args, Map<String, QdryVal> variablesMap){
        evaluateNeFormalDeclList(formalDeclList.getNeFormalDeclList(), args, variablesMap);
    }

    void evaluateNeFormalDeclList(NeFormalDeclList neFormalDeclList, ArrayList<QdryVal> args, Map<String, QdryVal> variablesMap){
        int i = 0;
        variablesMap.put(neFormalDeclList.getVarDecl().getIdent().getIdentStr(), args.get(i));
        while (neFormalDeclList.getNeFormalDeclList() != null){
            neFormalDeclList = neFormalDeclList.getNeFormalDeclList();
            i++;
            variablesMap.put(neFormalDeclList.getVarDecl().getIdent().getIdentStr(), args.get(i));
        }
    }

    QdryVal evaluateExprList(ExprList exprList, ArrayList<QdryVal> args, Map<String, QdryVal> variablesMap) {
        QdryVal value = evaluateNeExprList(exprList.getNeExprList(), args, variablesMap);
        if (exprList.getNeExprList() != null){
            return evaluateNeExprList(exprList.getNeExprList(), args, variablesMap);   
        }
        return value;
    }

    QdryVal evaluateNeExprList(NeExprList neExprList, ArrayList<QdryVal> args, Map<String, QdryVal> variablesMap){
        QdryVal exprValueFirst = evaluateExpr(neExprList.getExpr(),variablesMap);
        if (neExprList.getNeExprList() != null) {
            return evaluateNeExprList(neExprList.getNeExprList(), args, variablesMap);
        }
        return exprValueFirst; 
    }

    boolean evaluateCond(Cond cond, Map<String, QdryVal> variablesMap){
            if(cond instanceof RelationalCond) {
                RelationalCond relCond = (RelationalCond) cond;
                Long leftExpr = ((QdryInt)evaluateExpr(relCond.getLeftExpr(),variablesMap)).getInt();
                Long rightExpr = ((QdryInt) evaluateExpr(relCond.getRightExpr(), variablesMap)).getInt();
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

    QdryVal evaluateStmt(Stmt stmt, Map<String, QdryVal> variablesMap){
        if (stmt instanceof DeclStmt){
            DeclStmt declStmt = (DeclStmt)stmt;
            String varName = declStmt.getVarDecl().getIdent().getIdentStr();
            QdryVal value = evaluateExpr(declStmt.getExpr(),variablesMap);
            variablesMap.put(varName, value);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfStmt) {
            IfStmt ifStatement = (IfStmt)stmt;
            boolean condition = evaluateCond(ifStatement.getCond(),variablesMap);
            QdryVal value = null;
            if (condition){
                value = evaluateStmt(ifStatement.getStmt(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof IfElseStmt){
            IfElseStmt ifElseStmt = (IfElseStmt)stmt;
            boolean condition = evaluateCond(ifElseStmt.getCond(),variablesMap);
            QdryVal value = null;
            if (condition){
                value = evaluateStmt(ifElseStmt.getStmt1(), variablesMap);
            } else {
                value = evaluateStmt(ifElseStmt.getStmt2(), variablesMap);
            }
            // System.out.println(value);
            return value;
        } else if (stmt instanceof PrintStmt) {
            PrintStmt printStmt = (PrintStmt)stmt;
            QdryVal value = evaluateExpr(printStmt.getExpr(), variablesMap);
            // System.out.println(value);
            return value;
        } else if (stmt instanceof AssignStmt) {
            variablesMap.put(((AssignStmt)stmt).getIdentStr(), evaluateExpr(((AssignStmt)stmt).getExpr(), variablesMap));
            return new QdryInt(1);
        } else if (stmt instanceof WhileStmt) {
            QdryVal value = new QdryInt(0);
            while (evaluateCond(((WhileStmt)stmt).getCond(), variablesMap)) {
                value = evaluateStmt(((WhileStmt)stmt).getStmt(), variablesMap);
                if (returnFlag) {
                // if (returnFlag[0]) {
                    break;
                }
            }
            return value;
        } else if (stmt instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt)stmt;
            QdryVal value = evaluateExpr(returnStmt.getExpr(),variablesMap);
            returnFlag = true;
            // returnFlag[0] = true;
            // System.out.println(value);
            return value;
        } else if (stmt instanceof StmtBlock) {
            StmtBlock stmtBlock = (StmtBlock)stmt;
            QdryVal value = evaluateStmtList(stmtBlock.getStmtList(), variablesMap);
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

    public QdryVal evaluateCallExpr(CallExpr callExpr, Map<String, QdryVal> variablesMap) {
        List<QdryVal> actual = new LinkedList<>();
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
        QdryRef ref;
        if (value == null) {
            throw new IllegalStateException("value is null in CallExpr");
        }
        switch ((callExpr.getIdent().getIdentStr())) {
            case "randomInt":
                Random random = new Random();
                QdryInt randomInt = new QdryInt((long)random.nextInt((int)((QdryInt)(actual.get(0))).getInt()));
                return randomInt;
            case "isNil":
                // System.out.println(" ((QdryRef)value).getRef() == QdryNil.getInstance(): "+ (((QdryRef)value).getRef() == QdryNil.getInstance()));
                if (value instanceof QdryRef && ((QdryRef)value).getRef() == QdryNil.getInstance()) {
                    return new QdryInt(1);
                }
                return new QdryInt(0);
            case "right":
                // System.out.println(" right value: "+value.toString());
                // System.out.println(" right QdryQ: "+((QdryRef)value).getRef().toString());
                // if (((QdryRef)value).getRef() == QdryNil.getInstance()) {
                //     throw new IllegalStateException("right QdryQ nil ");
                // }
                return ((QdryRef)value).getRef().getRight();
                // return new QdryInt(0);
            case "left":
                // System.out.println(" left value: "+value.toString());
                // System.out.println(" left QdryQ: "+((QdryRef)value).getRef().toString());
                // if (((QdryRef)value).getRef() == QdryNil.getInstance()) {
                //     throw new IllegalStateException("left QdryQ nil ");
                // }
                return ((QdryRef)value).getRef().getLeft();
                // return new QdryInt(0);
            case "isAtom":
                if (value instanceof QdryInt && ((QdryRef)value).getRef() == QdryNil.getInstance()) {
                    return new QdryInt(1);
                }
                return new QdryInt(0);
            case "setRight":
                ref = (QdryRef)actual.get(0);
                ref.qrdyQ.right = (actual.get(1));
                return new QdryInt(1);
            case "setLeft":
                ref = (QdryRef)actual.get(0);
                ref.qrdyQ.left = (actual.get(1));
                return new QdryInt(1);
            default: 
                FuncDef funcDef = functionMapping.get((callExpr).getIdent().getIdentStr());
                if (funcDef == null) {
                    throw new IllegalStateException("Function definition null for: " + (callExpr).getIdent().getIdentStr());
                }
                List<String> formalParamsNames = funcDef.getParamNames();
                Map<String, QdryVal> callContexMap = new TreeMap<>();
                for (int i=0; i<actual.size(); i++) {
                    callContexMap.put(formalParamsNames.get(i), actual.get(i));
                }
                return evaluateStmtList(funcDef.getStmtList(), callContexMap);
        }
    }


    public QdryVal evaluateExpr(Expr expr, Map<String, QdryVal> variablesMap) {
        // System.out.println(expr.toString())
        if (expr instanceof ConstExpr) {
            return new QdryInt((Long)((ConstExpr)expr).getValue());
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            if (binaryExpr.getOperator() == BinaryExpr.DOT) {
                return new QdryRef(new QdryQ(((QdryVal)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap)), ((QdryVal)evaluateExpr(binaryExpr.getRightExpr(),variablesMap))));
            } else {
                Long leftInt = ((QdryInt)evaluateExpr(binaryExpr.getLeftExpr(),variablesMap)).getInt();
                Long rightInt = ((QdryInt)evaluateExpr(binaryExpr.getRightExpr(),variablesMap)).getInt();
                switch (binaryExpr.getOperator()) {
                    case BinaryExpr.PLUS: return new QdryInt(leftInt + rightInt);
                    case BinaryExpr.MINUS: return new QdryInt(leftInt - rightInt);
                    case BinaryExpr.TIMES: return new QdryInt(leftInt * rightInt);
                    
                    default: throw new RuntimeException("Unhandled Binary operator");
                }
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr)expr;
            switch (unaryExpr.getOperator()) {
                case UnaryExpr.NEGATE: return new QdryInt( - ((QdryInt)evaluateExpr(unaryExpr.getExpr(),variablesMap)).getInt());
                default: throw new RuntimeException("Unhandled Unary operator");
            }
        } else if(expr instanceof IdentExpr){        
            IdentExpr identExpr = (IdentExpr)expr;
            return variablesMap.get(identExpr.getIdentStr());
        } else if(expr instanceof CallExpr){
            return evaluateCallExpr((CallExpr)expr, variablesMap);
        } else if (expr instanceof TypeCastExpr) {
            return evaluateExpr(((TypeCastExpr)expr).getExpr(), variablesMap);
        } else if (expr instanceof NilExpr) {
            return new QdryRef(QdryNil.getInstance());
        } else if (expr instanceof ConcurrencyExpr) {
            BinaryExpr concurrentBinaryExpr = ((ConcurrencyExpr)expr).getBinaryExpr();
            QdryVal leftVal;
            QdryVal rightVal;
            MyThread t1 = new MyThread(concurrentBinaryExpr.getLeftExpr(), variablesMap);
            MyThread t2 = new MyThread(concurrentBinaryExpr.getRightExpr(), variablesMap);
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
            leftVal = t1.getVal();
            rightVal = t2.getVal();
            if (concurrentBinaryExpr.getOperator() == BinaryExpr.DOT) {
                return new QdryRef(new QdryQ((leftVal), (rightVal)));
            } else {
                Long leftInt = ((QdryInt)(leftVal)).getInt();
                Long rightInt = ((QdryInt)(rightVal)).getInt();
                switch (concurrentBinaryExpr.getOperator()) {
                    case BinaryExpr.PLUS: return new QdryInt(leftInt + rightInt);
                    case BinaryExpr.MINUS: return new QdryInt(leftInt - rightInt);
                    case BinaryExpr.TIMES: return new QdryInt(leftInt * rightInt);
                    
                    default: throw new RuntimeException("Unhandled Binary operator");
                }
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
