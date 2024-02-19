package assignment2;

import java.util.ArrayList;

import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.Tokenizer.TokenType;

public class AST {
    private Node root;
    private Node current;

    public AST() {
        this.root = new Node(null, Label.PRGM);
        this.current = root;
    }

    private Node swapOutCurrent(Node newCurrent) {
        Node currentNode = new Node(null, null);
        currentNode = this.current;
        this.current = newCurrent;
        return currentNode;
    }

    public void topDownParse(SamTokenizer f) throws Exception {
        this.current.label = Label.BODY;
        this.parseBODY(f);
    }

    private void parseBODY(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case WORD:
                while (f.peekAtKind() == TokenType.WORD) {
                    Node bodyNode = this.current.addChild(null, Label.VARDECL);
                    Node prevCurrent = this.swapOutCurrent(bodyNode);
                    parseVARDECL(f);
                    this.current = prevCurrent;
                }
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseVARDECL(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case WORD:
                Node typeNode = this.current.addChild(null, Label.TYPE);
                Node prevCurrent = this.swapOutCurrent(typeNode);
                this.parseTYPE(f);
                this.current = prevCurrent;
                while (f.peekAtKind() == TokenType.STRING) {
                    Node identNode = this.current.addChild(null, Label.IDENT);
                    prevCurrent = this.swapOutCurrent(identNode);
                    this.parseIDENT(f);
                    this.current = prevCurrent;
                }
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBLOCK(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case CHARACTER:
                if (f.getCharacter() != '{')
                    throw new Exception();

                do {
                    Node stmtNode = this.current.addChild(null, Label.STMT);
                    Node prevCurrent = this.swapOutCurrent(stmtNode);
                    this.parseSTMT(f);
                    this.current = prevCurrent;
                } while (f.peekAtKind() != TokenType.CHARACTER);

                if (f.getCharacter() != '{')
                    throw new Exception();
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseSTMT(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case STRING:
                Node varNode = this.current.addChild(null, Label.VAR);
                Node prevCurrent = this.swapOutCurrent(varNode);
                this.parseVAR(f);
                this.current = prevCurrent;

                if (f.getCharacter() != '=')
                    throw new Exception();
                
                Node exprNode = this.current.addChild(null, Label.EXPR);
                prevCurrent = this.swapOutCurrent(exprNode);
                this.parseEXPR(f);
                this.current = prevCurrent;
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseEXPR(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case CHARACTER:
                if (f.getCharacter() != '(')
                    throw new Exception();
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseBINOP(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case OPERATOR:
                char binop = f.getOp();
                if (!"+-*/%&|<>=".contains(Character.toString(binop)))
                    throw new Exception();
                this.current.value = Character.toString(binop);
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseUNOP(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case OPERATOR:
                char unop = f.getOp();
                if (!"~!".contains(Character.toString(unop)))
                    throw new Exception();
                this.current.value = Character.toString(unop);
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseTYPE(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case WORD:
                String type = f.getWord();
                if (type != "int" && type != "bool" && type != "string")
                    throw new Exception();
                this.current.value = type;
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseVAR(SamTokenizer f) throws Exception {
        this.current.label = Label.IDENT;
        this.parseIDENT(f);
    }

    private void parseLIT(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case INTEGER:
            case FLOAT:
                Node numNode = this.current.addChild(null, Label.NUM);
                Node prevCurrent = this.swapOutCurrent(numNode);
                this.parseNUM(f);
                this.current = prevCurrent;
                break;
            case STRING:
                Node stringNode = this.current.addChild(null, Label.STRING);
                prevCurrent = this.swapOutCurrent(stringNode);
                this.parseSTRING(f);
                this.current = prevCurrent;
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseNUM(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case INTEGER:
                int n = f.getInt();
                this.current.value = Integer.toString(n);
                break;
            case FLOAT:
                float flt = f.getFloat();
                this.current.value = Float.toString(flt);
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseSTRING(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case STRING:
                String str = f.getString();
                this.current.value = str;
                break;
            case COMMENT:
                f.skipToken();
                break;
            default:
                throw new Exception();
        }
    }

    private void parseIDENT(SamTokenizer f) throws Exception {
        switch (f.peekAtKind()) {
            case STRING:
                String ident = f.getString();
                for (int i = 0; i < ident.length(); i++) {
                    if (!"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_".contains(ident.substring(i, i + 1)))
                        throw new Exception();
                }
                this.current.value = ident;
                break;
            case COMMENT:
                f.skipToken();
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
    VAR,
    EXPR,
    BINOP,
    UNOP,
    LIT,
    NUM,
    STRING
}
