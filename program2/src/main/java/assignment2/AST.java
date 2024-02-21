package assignment2;

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

    private void parseBODY() throws Exception {
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
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseMETHODDECL() throws Exception {
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
                if (this.tokenizer.getCharacter() != '(') {
                    throw new Exception();
                }
                if (this.tokenizer.peekAtKind() == TokenType.STRING) {
                    Node formalsNode = this.current.addChild(null, Label.FORMALS);
                    prevCurrent = this.swapOutCurrent(formalsNode);
                    this.parseFORMALS();
                    this.current = prevCurrent;
                }
                if (this.tokenizer.getCharacter() != ')') {
                    throw new Exception();
                }
                // {BODY}
                if (this.tokenizer.getCharacter() != '{') {
                    throw new Exception();
                }
                Node bodyNode = this.current.addChild(null, Label.BODY);
                prevCurrent = this.swapOutCurrent(bodyNode);
                this.parseBODY();
                this.current = prevCurrent;
                if (this.tokenizer.getCharacter() != '}') {
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

    private void parseVARDECL() throws Exception {
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
                while (this.tokenizer.peekAtKind() == TokenType.CHARACTER) {
                    if (this.tokenizer.getCharacter() != ',') {
                        throw new Exception();
                    }
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
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBLOCK() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            case CHARACTER:
                // {STMT+}
                if (this.tokenizer.getCharacter() != '{')
                    throw new Exception();
                do {
                    Node stmtNode = this.current.addChild(null, Label.STMT);
                    Node prevCurrent = this.swapOutCurrent(stmtNode);
                    this.parseSTMT();
                    this.current = prevCurrent;
                } while (this.tokenizer.peekAtKind() != TokenType.CHARACTER);

                if (this.tokenizer.getCharacter() != '}')
                    throw new Exception();
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseSTMT() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            // ;
            case CHARACTER:
                if (this.tokenizer.getCharacter() != ';') {
                    throw new Exception();
                }
                break;
            // VAR = EXPR;
            case STRING:
                // VAR
                Node varNode = this.current.addChild(null, Label.VAR);
                Node prevCurrent = this.swapOutCurrent(varNode);
                this.parseVAR();
                this.current = prevCurrent;
                // =
                if (this.tokenizer.getCharacter() != '=')
                    throw new Exception();
                // EXPR
                Node exprNode = this.current.addChild(null, Label.EXPR);
                prevCurrent = this.swapOutCurrent(exprNode);
                this.parseEXPR();
                this.current = prevCurrent;
                // ;
                if (this.tokenizer.getCharacter() != ';') {
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

    private void parseEXPR() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            case CHARACTER:
                if (this.tokenizer.getCharacter() != '(')
                    throw new Exception();
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBINOP() throws Exception {
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

    }

    private void parseACTUALS() throws Exception {

    }

    private void parseTYPE() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            case WORD:
                // int | bool | string
                String type = this.tokenizer.getWord();
                if (type != "int" && type != "bool" && type != "string")
                    throw new Exception();
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
        // IDENT
        this.current.label = Label.IDENT;
        this.parseIDENT();
    }

    private void parseVAR() throws Exception {
        // IDENT
        this.current.label = Label.IDENT;
        this.parseIDENT();
    }

    private void parseLIT() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            case INTEGER:
                // NUM
                Node numNode = this.current.addChild(null, Label.NUM);
                Node prevCurrent = this.swapOutCurrent(numNode);
                this.parseNUM();
                this.current = prevCurrent;
                break;
            case STRING:
                // true | false | STRING
                Node stringNode = this.current.addChild(null, Label.STRING);
                prevCurrent = this.swapOutCurrent(stringNode);
                this.parseSTRING();
                this.current = prevCurrent;
                break;
            case COMMENT:
                this.tokenizer.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseNUM() throws Exception {
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
        switch (this.tokenizer.peekAtKind()) {
            case CHARACTER:
                // "[ASCII character]*"
                if (this.tokenizer.getCharacter() != '"') {
                    throw new Exception();
                }
                String str = this.tokenizer.getString();
                this.current.value = str;
                if (this.tokenizer.getCharacter() != '"') {
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

    private void parseIDENT() throws Exception {
        switch (this.tokenizer.peekAtKind()) {
            case STRING:
                // [a-zA-Z]
                String ident = this.tokenizer.getString();
                if (ident.isBlank() || !"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".contains(ident.substring(0, 1))) {
                    throw new Exception();
                }
                // ([a-zA-Z0-9_]*)
                for (int i = 1; i < ident.length(); i++) {
                    if (!"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".contains(ident.substring(i, i + 1)))
                        throw new Exception();
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
    public ArrayList<Node> children;

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
