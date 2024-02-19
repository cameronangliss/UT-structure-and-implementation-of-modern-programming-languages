package assignment2;

import edu.utexas.cs.sam.io.SamTokenizer;

public class AST {
    private String fileName;
    private String value;
    private Label label;
    private AST leftChild;
    private AST rightChild;

    public AST(String fileName) {
        this.fileName = fileName;
        this.value = "";
        this.label = Label.PRGM;
        this.leftChild = null;
        this.rightChild = null;
    }

    public String compile() {
        try {
            SamTokenizer tokenizer = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
            this.parseProgram(tokenizer);
            String samCode = this.generateSamCode();
            return samCode;
        } catch (Exception e) {
            System.err.println("Failed to compile " + fileName);
			throw new Error();
        }
    }

    private void parseProgram(SamTokenizer f) throws Exception {
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
                    throw new Exception();
            }
            f.skipToken();
        }
    }

    public String generateSamCode() {
        return null;
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
