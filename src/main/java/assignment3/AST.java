package assignment3;

import java.util.List;
import java.util.ArrayList;

import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.Tokenizer.TokenType;

public class AST {
    public Node root;
    private SamTokenizer tokenizer;
    private Node current;

    public AST(SamTokenizer tokenizer) {
        this.root = new Node(null, Label.PRGM);
        this.tokenizer = tokenizer;
        this.current = root;
    }

    private Node swapOutCurrent(Node newCurrent) {
        Node currentNode = new Node(null, null);
        currentNode = this.current;
        this.current = newCurrent;
        return currentNode;
    }

    public void topDownParse() throws Exception {
        // System.out.println("start topDownParse");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // METHODDECL*
                while (this.tokenizer.peekAtKind() == TokenType.WORD) {
                    Node methodDeclNode = this.current.addChild(null, Label.METHODDECL);
                    Node prevCurrent = this.swapOutCurrent(methodDeclNode);
                    parseMETHODDECL();
                    this.current = prevCurrent;
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseMETHODDECL() throws Exception {
        // System.out.println("start parseMETHODDECL");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // TYPE
                Node typeNode = this.current.addChild(null, Label.TYPE);
                Node prevCurrent = this.swapOutCurrent(typeNode);
                this.parseTYPE();
                this.current = prevCurrent;
                // METHOD
                Node methodNode = this.current.addChild(null, Label.METHOD);
                prevCurrent = this.swapOutCurrent(methodNode);
                this.parseMETHOD();
                this.current = prevCurrent;
                // (FORMALS?)
                if (this.tokenizer.getOp() != '(') {
                    throw new Exception();
                }
                if (this.tokenizer.peekAtKind() == TokenType.WORD) {
                    Node formalsNode = this.current.addChild(null, Label.FORMALS);
                    prevCurrent = this.swapOutCurrent(formalsNode);
                    this.parseFORMALS();
                    this.current = prevCurrent;
                }
                if (this.tokenizer.getOp() != ')') {
                    throw new Exception();
                }
                // {BODY}
                if (this.tokenizer.getOp() != '{') {
                    throw new Exception();
                }
                Node bodyNode = this.current.addChild(null, Label.BODY);
                prevCurrent = this.swapOutCurrent(bodyNode);
                this.parseBODY();
                this.current = prevCurrent;
                if (this.tokenizer.getOp() != '}') {
                    throw new Exception();
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBODY() throws Exception {
        // System.out.println("start parseBODY");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // VARDECL*
                while (this.tokenizer.peekAtKind() == TokenType.WORD) {
                    Node bodyNode = this.current.addChild(null, Label.VARDECL);
                    Node prevCurrent = this.swapOutCurrent(bodyNode);
                    parseVARDECL();
                    this.current = prevCurrent;
                }
                // BLOCK
                Node blockNode = this.current.addChild(null, Label.BLOCK);
                Node prevCurrent = this.swapOutCurrent(blockNode);
                parseBLOCK();
                this.current = prevCurrent;
                break;
            case OPERATOR:
                // BLOCK
                blockNode = this.current.addChild(null, Label.BLOCK);
                prevCurrent = this.swapOutCurrent(blockNode);
                parseBLOCK();
                this.current = prevCurrent;
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseVARDECL() throws Exception {
        // System.out.println("start parseVARDECL");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // TYPE
                Node typeNode = this.current.addChild(null, Label.TYPE);
                Node prevCurrent = this.swapOutCurrent(typeNode);
                this.parseTYPE();
                this.current = prevCurrent;
                // IDENT
                Node identNode = this.current.addChild(null, Label.IDENT);
                prevCurrent = this.swapOutCurrent(identNode);
                this.parseIDENT();
                this.current = prevCurrent;
                // (, TYPE IDENTIFIER)*
                while (this.tokenizer.getOp() == ',') {
                    // IDENT
                    identNode = this.current.addChild(null, Label.IDENT);
                    prevCurrent = this.swapOutCurrent(identNode);
                    this.parseIDENT();
                    this.current = prevCurrent;
                }
                this.tokenizer.pushBack();
                if (this.tokenizer.getOp() != ';') {
                    throw new Exception();
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBLOCK() throws Exception {
        // System.out.println("start parseBLOCK");
        switch (this.tokenizer.peekAtKind()) {
            case OPERATOR:
                // {STMT+}
                if (this.tokenizer.getOp() != '{')
                    throw new Exception();
                do {
                    Node stmtNode = this.current.addChild(null, Label.STMT);
                    Node prevCurrent = this.swapOutCurrent(stmtNode);
                    this.parseSTMT();
                    this.current = prevCurrent;
                } while (this.tokenizer.peekAtKind() != TokenType.OPERATOR);
                if (this.tokenizer.getOp() != '}') {
                    throw new Exception();
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseSTMT() throws Exception {
        // System.out.println("start parseSTMT");
        switch (this.tokenizer.peekAtKind()) {
            // ;
            case OPERATOR:
                if (this.tokenizer.getOp() != ';') {
                    throw new Exception();
                }
                this.current.value = "semicolon";
                break;
            case WORD:
                // return EXPR;
                String token = this.tokenizer.getWord();
                // if (EXPR) BLOCK else BLOCK
                if (token.equals("if")) {
                    this.current.value = "if";
                    // (EXPR)
                    if (this.tokenizer.getOp() != '(') {
                        throw new Exception();
                    }
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    if (this.tokenizer.getOp() != ')') {
                        throw new Exception();
                    }
                    // BLOCK
                    Node blockNode = this.current.addChild(null, Label.BLOCK);
                    prevCurrent = this.swapOutCurrent(blockNode);
                    parseBLOCK();
                    this.current = prevCurrent;
                    // else
                    if (!this.tokenizer.getWord().equals("else")) {
                        throw new Exception();
                    }
                    // BLOCK
                    blockNode = this.current.addChild(null, Label.BLOCK);
                    prevCurrent = this.swapOutCurrent(blockNode);
                    parseBLOCK();
                    this.current = prevCurrent;
                // while (EXPR) BLOCK
                } else if (token.equals("while")) {
                    this.current.value = "while";
                    // (EXPR)
                    if (this.tokenizer.getOp() != '(') {
                        throw new Exception();
                    }
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    if (this.tokenizer.getOp() != ')') {
                        throw new Exception();
                    }
                    // BLOCK
                    Node blockNode = this.current.addChild(null, Label.BLOCK);
                    prevCurrent = this.swapOutCurrent(blockNode);
                    parseBLOCK();
                    this.current = prevCurrent;
                } else if (token.equals("break")) {
                    this.current.value = "break";
                    // ;
                    if (this.tokenizer.getOp() != ';') {
                        throw new Exception();
                    }
                } else if (token.equals("return")) {
                    this.current.value = "return";
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    // ;
                    if (this.tokenizer.getOp() != ';') {
                        throw new Exception();
                    }
                // VAR = EXPR;
                } else {
                    this.tokenizer.pushBack();
                    this.current.value = "assign";
                    // VAR
                    Node varNode = this.current.addChild(null, Label.VAR);
                    Node prevCurrent = this.swapOutCurrent(varNode);
                    this.parseVAR();
                    this.current = prevCurrent;
                    // =
                    if (this.tokenizer.getOp() != '=')
                        throw new Exception();
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    // ;
                    if (this.tokenizer.getOp() != ';') {
                        throw new Exception();
                    }
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseEXPR() throws Exception {
        // System.out.println("start parseEXPR");
        switch (this.tokenizer.peekAtKind()) {
            case OPERATOR:
                // (EXPR BINOP EXPR) | (UNOP EXPR) | (EXPR)
                if (this.tokenizer.getOp() != '(')
                    throw new Exception();
                String exprType = this.identifyParenExpr();
                if (exprType == "TERNARY") {
                    this.current.value = "ternary";
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    // ?
                    if (this.tokenizer.getOp() != '?') {
                        throw new Exception();
                    }
                    // EXPR
                    exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    // :
                    if (this.tokenizer.getOp() != ':') {
                        throw new Exception();
                    }
                    // EXPR
                    exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                } else if (exprType == "BINOP") {  // (EXPR BINOP EXPR)
                    this.current.value = "binop";
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                    // BINOP
                    Node binopNode = this.current.addChild(null, Label.BINOP);
                    prevCurrent = this.swapOutCurrent(binopNode);
                    this.parseBINOP();
                    this.current = prevCurrent;
                    // EXPR
                    exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                } else if (exprType == "UNOP") {  // (UNOP EXPR)
                    this.current.value = "unop";
                    // UNOP
                    Node unop = this.current.addChild(null, Label.UNOP);
                    Node prevCurrent = this.swapOutCurrent(unop);
                    this.parseUNOP();
                    this.current = prevCurrent;
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                } else if (exprType == "PAREN") {  // (EXPR)
                    this.current.value = "paren";
                    // EXPR
                    Node exprNode = this.current.addChild(null, Label.EXPR);
                    Node prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                }
                if (this.tokenizer.getOp() != ')') {
                    throw new Exception();
                }
                break;
            case INTEGER:
            case STRING:
                this.current.value = "lit";
                // LITERAL
                Node litNode = this.current.addChild(null, Label.LIT);
                Node prevCurrent = this.swapOutCurrent(litNode);
                this.parseLIT();
                this.current = prevCurrent;
                break;
            case WORD:
                // VAR | LITERAL | METHOD (ACTUALS?)
                String token = this.tokenizer.getWord();
                char nextToken = ' ';
                if (this.tokenizer.peekAtKind() == TokenType.OPERATOR) {
                    nextToken = this.tokenizer.getOp();
                    this.tokenizer.pushBack();
                }
                this.tokenizer.pushBack();
                if (nextToken == '(') {  // METHOD (ACTUALS?)
                    this.current.value = "method";
                    // METHOD
                    Node methodNode = this.current.addChild(null, Label.METHOD);
                    prevCurrent = this.swapOutCurrent(methodNode);
                    this.parseMETHOD();
                    this.current = prevCurrent;
                    // (ACTUALS?)
                    if (this.tokenizer.getOp() != '(') {
                        throw new Exception();
                    }
                    if (this.tokenizer.peekAtKind() == TokenType.OPERATOR) {
                        char op = this.tokenizer.getOp();
                        this.tokenizer.pushBack();
                        if (op != ')') {
                            Node actualsNode = this.current.addChild(null, Label.ACTUALS);
                            prevCurrent = this.swapOutCurrent(actualsNode);
                            this.parseACTUALS();
                            this.current = prevCurrent;
                        }
                    } else {
                        Node actualsNode = this.current.addChild(null, Label.ACTUALS);
                        prevCurrent = this.swapOutCurrent(actualsNode);
                        this.parseACTUALS();
                        this.current = prevCurrent;
                    }
                    if (this.tokenizer.getOp() != ')') {
                        throw new Exception();
                    }
                } else if (token.equals("true") || token.equals("false")) {  // LITERAL
                    this.current.value = "lit";
                    litNode = this.current.addChild(null, Label.LIT);
                    prevCurrent = this.swapOutCurrent(litNode);
                    this.parseLIT();
                    this.current = prevCurrent;
                } else {  // VAR
                    this.current.value = "var";
                    Node varNode = this.current.addChild(null, Label.VAR);
                    prevCurrent = this.swapOutCurrent(varNode);
                    this.parseVAR();
                    this.current = prevCurrent;
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private String identifyParenExpr() throws Exception {
        // Looking ahead to determine which type of EXPR it is
        int i = 0;
        int parenTracker = 1;
        String token;
        while (true) {
            if (this.tokenizer.peekAtKind() == TokenType.OPERATOR) {
                token = Character.toString(this.tokenizer.getOp());
                if ("?+-*/%&|<>=~!)".contains(token) && parenTracker == 1) {
                    break;
                } else if (token.equals("(")) {
                    parenTracker++;
                } else if (token.equals(")")) {
                    parenTracker--;
                }
            } else {
                this.tokenizer.skipToken();
            }
            i++;
        }
        i++;
        String exprType;
        if (token.equals("?")) {
            exprType = "TERNARY";
        } else if ("+-*/%&|<>=".contains(token)) {
            exprType = "BINOP";
        } else if ("~!".contains(token)) {
            exprType = "UNOP";
        } else if (token.equals(")")) {
            exprType = "PAREN";
        } else {
            throw new Exception();
        }
        // Look-ahead finished, now backing the tokenizer up to parse the EXPR
        while (i > 0) {
            this.tokenizer.pushBack();
            i--;
        }
        return exprType;
    }

    private void parseBINOP() throws Exception {
        // System.out.println("start parseBINOP");
        switch (this.tokenizer.peekAtKind()) {
            case OPERATOR:
                // [+-*/%&|<>=]
                char binop = this.tokenizer.getOp();
                if (!"+-*/%&|<>=".contains(Character.toString(binop)))
                    throw new Exception();
                this.current.value = Character.toString(binop);
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseUNOP() throws Exception {
        // System.out.println("start parseUNOP");
        switch (this.tokenizer.peekAtKind()) {
            case OPERATOR:
                // [~!]
                char unop = this.tokenizer.getOp();
                if (!"~!".contains(Character.toString(unop)))
                    throw new Exception();
                this.current.value = Character.toString(unop);
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseFORMALS() throws Exception {
        // System.out.println("start parseFORMALS");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // TYPE
                Node typeNode = this.current.addChild(null, Label.TYPE);
                Node prevCurrent = this.swapOutCurrent(typeNode);
                this.parseTYPE();
                this.current = prevCurrent;
                // IDENT
                Node identNode = this.current.addChild(null, Label.IDENT);
                prevCurrent = this.swapOutCurrent(identNode);
                this.parseIDENT();
                this.current = prevCurrent;
                // (, TYPE IDENTIFIER)*
                while (this.tokenizer.getOp() == ',') {
                    // TYPE
                    typeNode = this.current.addChild(null, Label.TYPE);
                    prevCurrent = this.swapOutCurrent(typeNode);
                    this.parseTYPE();
                    this.current = prevCurrent;
                    // IDENT
                    identNode = this.current.addChild(null, Label.IDENT);
                    prevCurrent = this.swapOutCurrent(identNode);
                    this.parseIDENT();
                    this.current = prevCurrent;
                }
                this.tokenizer.pushBack();
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseACTUALS() throws Exception {
        // System.out.println("start parseACTUALS");
        switch (this.tokenizer.peekAtKind()) {
            case INTEGER:
            case STRING:
            case OPERATOR:
            case WORD:
                // EXPR
                Node exprNode = this.current.addChild(null, Label.EXPR);
                Node prevCurrent = this.swapOutCurrent(exprNode);
                this.parseEXPR();
                this.current = prevCurrent;
                // (, EXPR)*
                while (this.tokenizer.peekAtKind() == TokenType.OPERATOR) {
                    char token = this.tokenizer.getOp();
                    if (token == ')') {
                        this.tokenizer.pushBack();
                        break;
                    } else if (token != ',') {
                        throw new Exception();
                    }
                    // EXPR
                    exprNode = this.current.addChild(null, Label.EXPR);
                    prevCurrent = this.swapOutCurrent(exprNode);
                    this.parseEXPR();
                    this.current = prevCurrent;
                }
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseTYPE() throws Exception {
        // System.out.println("start parseTYPE");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // int | bool | string
                String type = this.tokenizer.getWord();
                if (!type.equals("int") && !type.equals("bool") && !type.equals("String")) {
                    throw new Exception();
                }
                this.current.value = type;
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseMETHOD() throws Exception {
        // System.out.println("start parseMETHOD");
        String methodName = this.tokenizer.getWord();
        this.tokenizer.pushBack();
        this.current.value = methodName;
        // IDENT
        Node identNode = this.current.addChild(null, Label.IDENT);
        Node prevCurrent = this.swapOutCurrent(identNode);
        this.parseIDENT();
        this.current = prevCurrent;
    }

    private void parseVAR() throws Exception {
        // System.out.println("start parseVAR");
        String varName = this.tokenizer.getWord();
        this.tokenizer.pushBack();
        this.current.value = varName;
        // IDENT
        Node identNode = this.current.addChild(null, Label.IDENT);
        Node prevCurrent = this.swapOutCurrent(identNode);
        this.parseIDENT();
        this.current = prevCurrent;
    }

    private void parseLIT() throws Exception {
        // System.out.println("start parseLIT");
        switch (this.tokenizer.peekAtKind()) {
            case INTEGER:
                this.current.value = "num";
                // NUM
                Node numNode = this.current.addChild(null, Label.NUM);
                Node prevCurrent = this.swapOutCurrent(numNode);
                this.parseNUM();
                this.current = prevCurrent;
                break;
            case STRING:
                this.current.value = "string";
                // STRING
                Node stringNode = this.current.addChild(null, Label.STRING);
                prevCurrent = this.swapOutCurrent(stringNode);
                this.parseSTRING();
                this.current = prevCurrent;
                break;
            case WORD:
                // true | false
                this.current.value = this.tokenizer.getWord();
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseNUM() throws Exception {
        // System.out.println("start parseNUM");
        switch (this.tokenizer.peekAtKind()) {
            case INTEGER:
                // [0-9]*
                int n = this.tokenizer.getInt();
                this.current.value = Integer.toString(n);
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseSTRING() throws Exception {
        // System.out.println("start parseSTRING");
        switch (this.tokenizer.peekAtKind()) {
            case STRING:
                // "[ASCII character]*"
                String str = this.tokenizer.getString();
                this.current.value = str;
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseIDENT() throws Exception {
        // System.out.println("start parseIDENT");
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // [a-zA-Z]
                String ident = this.tokenizer.getWord();
                if (ident.isBlank() || !"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(ident.substring(0, 1))) {
                    throw new Exception();
                }
                // ([a-zA-Z0-9_]*)
                for (int i = 1; i < ident.length(); i++) {
                    if (!"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".contains(ident.substring(i, i + 1))) {
                        throw new Exception();
                    }
                }
                this.current.value = ident;
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }
}

class Node {
    public String value;
    public Label label;
    public List<Node> children;

    public Node(String value, Label label) {
        this.value = value;
        this.label = label;
        this.children = new ArrayList<Node>();
    }

    public Node addChild(String value, Label label) {
        Node newNode = new Node(value, label);
        this.children.add(newNode);
        return newNode;
    }
}

enum Label {
    PRGM,
    BODY,
    METHODDECL,
    VARDECL,
    BLOCK,
    FORMALS,
    ACTUALS,
    TYPE,
    IDENT,
    STMT,
    METHOD,
    VAR,
    EXPR,
    BINOP,
    UNOP,
    LIT,
    NUM,
    STRING
}
