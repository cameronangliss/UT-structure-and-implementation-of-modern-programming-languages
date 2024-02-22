package assignment2;

import java.util.HashMap;
import java.util.Map;

class SamCoder {
	private int counter;
	private Map<String, Pair<String, Integer>> variables;

	public SamCoder() {
		this.counter = 0;
		this.variables = new HashMap<String, Pair<String, Integer>>();
	}

	public String generateSamCode(AST ast) {
		return generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) {
        String prgmStr = "";
        for (Node child : prgmNode.children) {
            prgmStr.concat(generateSamMETHODDECL(child));
        }
        return prgmStr;
	}

    private String generateSamMETHODDECL(Node methodDeclNode) {
        String methodDeclStr = "";
        methodDeclStr.concat(generateSamMETHOD(methodDeclNode.children.get(1)));
        if (methodDeclNode.children.get(2).label == Label.FORMALS) {
            methodDeclStr.concat(generateSamFORMALS(methodDeclNode.children.get(2)));
        }
        methodDeclStr.concat(generateSamBODY(methodDeclNode.children.get(methodDeclNode.children.size() - 1)));
        return methodDeclStr;
    }

    private String generateSamBODY(Node bodyNode) {
        String bodyStr = "";
        for (int i = 0; i < bodyNode.children.size() - 1; i++) {
            bodyStr.concat(generateSamVARDECL(bodyNode.children.get(i)));
        }
        bodyStr.concat(generateSamBLOCK(bodyNode.children.get(bodyNode.children.size() - 1)));
        return bodyStr;
	}

    private String generateSamVARDECL(Node varDeclNode) {
		String varDeclStr = "";
		for (int i = 1; i < varDeclNode.children.size(); i++) {
			this.counter++;
			Pair<String, Integer> typeLocPair = new Pair<String, Integer>(varDeclNode.children.get(0).value, this.counter);
			this.variables.put(varDeclNode.children.get(i).value, typeLocPair);
			varDeclStr.concat("ADDSP 1");
		}
		return varDeclStr;
	}

	private String generateSamBLOCK(Node blockNode) {
		String blockStr = "";
		for (Node child : blockNode.children) {
			String stmtStr = generateSamSTMT(child);
			blockStr.concat(stmtStr);
		}
		return blockStr;
	}

	private String generateSamSTMT(Node stmtNode) {
		String stmtStr = "";
		switch (stmtNode.value) {
			case "if":
				break;
			case "while":
				break;
			case "break":
				break;
			case "return":
				break;
			case "assign":
				stmtStr.concat(generateSamVAR(stmtNode.children.get(0)));
				stmtStr.concat(generateSamEXPR(stmtNode.children.get(1)));
				Pair<String, Integer> typeLocPair = this.variables.get(stmtNode.children.get(0).value);
				String varStr = "STOREOFF " + typeLocPair.snd() + "\n";
				stmtStr.concat(varStr);
				break;
			default:
				break;
		}
		return stmtStr;
	}

	private String generateSamEXPR(Node exprNode) {
        String exprStr = "";
        if (exprNode.children.get(0).label == Label.METHOD) {

        } else switch (exprNode.value) {
			case "ternary":
				break;
			case "binop":
                exprStr.concat(generateSamEXPR(exprNode.children.get(0)));
                exprStr.concat(generateSamEXPR(exprNode.children.get(2)));
                exprStr.concat(generateSamBINOP(exprNode.children.get(1)));
                break;
            case "unop":
                exprStr.concat(generateSamEXPR(exprNode.children.get(1)));
                exprStr.concat(generateSamUNOP(exprNode.children.get(0)));
				break;
			case "paren":
                exprStr.concat(generateSamEXPR(exprNode.children.get(0)));
				break;
			case "method":
				exprStr.concat(generateSamACTUALS(exprNode.children.get(1)));
				exprStr.concat(generateSamMETHOD(exprNode.children.get(0)));
			case "var":
				exprStr.concat(generateSamVAR(exprNode.children.get(0)));
				break;
			case "lit":
				exprStr.concat(generateSamLIT(exprNode.children.get(0)));
				break;
            default:
                throw new Error();
        }
        return exprStr;
	}

	private String generateSamBINOP(Node binopNode) {
		switch (binopNode.value) {
			case "+":
				return "ADD\n";
			case "-":
				return "SUB\n";
			case "*":
				return "TIMES\n";
			case "/":
				return "DIV\n";
			case "%":
				return "MOD\n";
			case "&":
				return "AND\n";
			case "|":
				return "OR\n";
			case "<":
				return "LESS\n";
			case ">":
				return "GREATER\n";
			case "=":
				return "EQUAL\n";
			default:
				throw new Error();
		}
	}

	private String generateSamUNOP(Node unopNode) {
		switch (unopNode.value) {
			case "~":
				return "PUSHIMM -1\nTIMES\n";
			case "!":
				return "NOT\n";
			default:
				throw new Error();
		}
	}

    private String generateSamFORMALS(Node formalsNode) {
        String formalsStr = "";
        for (int i = 1; i < formalsNode.children.size(); i += 2) {
            formalsStr.concat(generateSamIDENT(formalsNode.children.get(i)));
        }
        return formalsStr;
    }

    private String generateSamACTUALS(Node actualsNode) {
        return null;
    }

    private String generateSamMETHOD(Node methodNode) {
        String methodStr = "";
        methodStr.concat("LINK");
        methodStr.concat("JSR " + methodNode.value);
        methodStr.concat("UNLINK");
        return methodStr;
    }

	private String generateSamVAR(Node varNode) {
		return generateSamIDENT(varNode.children.get(0));
	}

	private String generateSamLIT(Node litNode) {
		if (litNode.children.isEmpty()) {
			this.counter++;
			if (litNode.value == "true") {
				return "PUSHIMM 1\n";
			} else if (litNode.value == "false") {
				return "PUSHIMM 0\n";
			} else {
				throw new Error();
			}
		} else {
			Node child = litNode.children.get(0);
			switch (child.label) {
				case NUM:
					return generateSamNUM(child);
				case STRING:
					return generateSamSTRING(child);
				default:
					throw new Error();
			}
		}
	}

	private String generateSamNUM(Node numNode) {
		this.counter++;
		return "PUSHIMM " + numNode.value + "\n";
	}

	private String generateSamSTRING(Node stringNode) {
		this.counter++;
		return "PUSHIMMSTR " + stringNode.value + "\n";
	}

	private String generateSamIDENT(Node identNode) {
		return "";
	}
}

class Pair<T1, T2> {
	private T1 a;
	private T2 b;

	public Pair(T1 a, T2 b) {
		this.a = a;
		this.b = b;
	}

	public T1 fst() {
		return this.a;
	}

	public T2 snd() {
		return this.b;
	}
}
