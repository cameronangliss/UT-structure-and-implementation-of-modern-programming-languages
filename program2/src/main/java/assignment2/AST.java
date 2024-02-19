package assignment2;

import edu.utexas.cs.sam.io.SamTokenizer;

public class AST {
    private String value;
    private Label label;
    private AST leftChild;
    private AST rightChild;

    public AST() {
        this.value = "";
        this.label = Label.PRGM;
        this.leftChild = null;
        this.rightChild = null;
    }

    public void parse(SamTokenizer f) {
        while(true) {
            switch (f.peekAtKind()) {
                case INTEGER:
                    break;
                case FLOAT:
                    break;
                case WORD:
                    break;
                case STRING:
                    break;
                case CHARACTER:
                    break;
                case OPERATOR:
                    break;
                case COMMENT:
                    break;
                case EOF:
                    return;
            }
            throw new Error();
        }
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
