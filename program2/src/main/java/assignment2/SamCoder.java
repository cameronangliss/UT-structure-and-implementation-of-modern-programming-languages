package assignment2;

import java.util.HashMap;
import java.util.Map;

class SamCoder {
	private int sp;
	private int fbr;
	private int if_counter;
	private Map<String, Pair<String, Integer>> variables;
	private StrOpCoder strOpCoder;

	public SamCoder() {
		this.sp = 0;
		this.fbr = 0;
		this.if_counter = 0;
		this.variables = new HashMap<String, Pair<String, Integer>>();
		this.strOpCoder = new StrOpCoder();
	}

	public String generateSamCode(AST ast) throws Exception {
		return generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) throws Exception {
		// System.out.println("start generateSamPRGM");
        String prgmStr = "";
        for (Node child : prgmNode.children) {
            prgmStr = prgmStr.concat(generateSamMETHODDECL(child));
        }
        return prgmStr;
	}

    private String generateSamMETHODDECL(Node methodDeclNode) throws Exception {
		// System.out.println("start generateSamMETHODDECL");
        String methodDeclStr = "";
		String methodName = methodDeclNode.children.get(1).value;
        methodDeclStr = methodDeclStr.concat(methodName + ":\n");
		if (methodName.equals("main")) {
			methodDeclStr = methodDeclStr.concat("PUSHIMM 0\n");
			this.sp++;
			if (methodDeclNode.children.get(2).label == Label.FORMALS) {
				throw new Exception();
			}
		}
        methodDeclStr = methodDeclStr.concat(generateSamBODY(methodDeclNode.children.get(methodDeclNode.children.size() - 1)));
		if (methodName.equals("main")) {
			methodDeclStr = methodDeclStr.concat("STOP\n");
		}
        return methodDeclStr;
    }

    private String generateSamBODY(Node bodyNode) throws Exception {
		// System.out.println("start generateSamBODY");
        String bodyStr = "";
        for (int i = 0; i < bodyNode.children.size() - 1; i++) {
            bodyStr = bodyStr.concat(generateSamVARDECL(bodyNode.children.get(i)));
        }
        bodyStr = bodyStr.concat(generateSamBLOCK(bodyNode.children.get(bodyNode.children.size() - 1)));
        return bodyStr;
	}

    private String generateSamVARDECL(Node varDeclNode) throws Exception {
		// System.out.println("start generateSamVARDECL");
		String varDeclStr = "";
		for (int i = 1; i < varDeclNode.children.size(); i++) {
			Pair<String, Integer> typeLocPair = new Pair<String, Integer>(varDeclNode.children.get(0).value, this.sp);
			this.variables.put(varDeclNode.children.get(i).value, typeLocPair);
			this.sp++;
		}
		if (varDeclNode.children.size() > 1) {
			varDeclStr = varDeclStr.concat("ADDSP " + (varDeclNode.children.size() - 1) + "\n");
		}
		return varDeclStr;
	}

	private String generateSamBLOCK(Node blockNode) throws Exception {
		// System.out.println("start generateSamBLOCK");
		String blockStr = "";
		for (Node child : blockNode.children) {
			blockStr = blockStr.concat(generateSamSTMT(child));
		}
		return blockStr;
	}

	private String generateSamSTMT(Node stmtNode) throws Exception {
		// System.out.println("start generateSamSTMT");
		String stmtStr = "";
		switch (stmtNode.value) {
			case "if":
				this.if_counter++;
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMPC IFTRUE" + this.if_counter + "\n");
				stmtStr = stmtStr.concat(generateSamBLOCK(stmtNode.children.get(2)));
				stmtStr = stmtStr.concat("JUMP AFTERIF" + this.if_counter + "\n");
				stmtStr = stmtStr.concat("IFTRUE" + this.if_counter + ":\n");
				stmtStr = stmtStr.concat(generateSamBLOCK(stmtNode.children.get(1)));
				stmtStr = stmtStr.concat("AFTERIF" + this.if_counter + ":\n");
				break;
			case "while":
				break;
			case "break":
				break;
			case "return":
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("STOREOFF " + this.fbr + "\n");
				this.sp--;
				stmtStr = stmtStr.concat("ADDSP " + (-this.sp) + "\n");
				break;
			case "assign":
				stmtStr = stmtStr.concat(generateSamVAR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(1)));
				Pair<String, Integer> typeLocPair = this.variables.get(stmtNode.children.get(0).value);
				stmtStr = stmtStr.concat("STOREOFF " + typeLocPair.snd() + "\n");
				this.sp--;
				break;
			default:
				break;
		}
		return stmtStr;
	}

	private String generateSamEXPR(Node exprNode) throws Exception {
		// System.out.println("start generateSamEXPR");
        String exprStr = "";
    	switch (exprNode.value) {
			case "ternary":
				break;
			case "binop":
				String fstOperandStr = generateSamEXPR(exprNode.children.get(0));
				exprStr = exprStr.concat(fstOperandStr);
				exprStr = exprStr.concat(generateSamEXPR(exprNode.children.get(2)));
				if (
					exprNode
						.getAllDescendantVars()
						.stream()
						.noneMatch(varName -> this.variables.get(varName).fst().equals("String"))
					&& !fstOperandStr.contains("PUSHIMMSTR")
				) {
					exprStr = exprStr.concat(generateSamBINOP(exprNode.children.get(1)));
				} else if (exprNode.children.get(1).value.equals("+")) {
					exprStr = exprStr.concat(this.strOpCoder.strConcat());
				} else if (exprNode.children.get(1).value.equals("*")) {
					exprStr = exprStr.concat(this.strOpCoder.strRepeat());
				} else if (exprNode.children.get(1).value.equals(">")) {
					exprStr = exprStr.concat(this.strOpCoder.strCmp());
					exprStr = exprStr.concat("PUSHIMM -1\nEQUAL\n");
				} else if (exprNode.children.get(1).value.equals("=")) {
					exprStr = exprStr.concat(this.strOpCoder.strCmp());
					exprStr = exprStr.concat("PUSHIMM 0\nEQUAL\n");
				} else if (exprNode.children.get(1).value.equals("<")) {
					exprStr = exprStr.concat(this.strOpCoder.strCmp());
					exprStr = exprStr.concat("PUSHIMM 1\nEQUAL\n");
				} else {
					throw new Exception();
				}
				break;
            case "unop":
				String operandStr = generateSamEXPR(exprNode.children.get(1));
				exprStr = exprStr.concat(operandStr);
				if (
					exprNode
						.getAllDescendantVars()
						.stream()
						.noneMatch(varName -> this.variables.get(varName).fst().equals("String"))
					&& !operandStr.contains("PUSHIMMSTR")
				) {
					exprStr = exprStr.concat(generateSamUNOP(exprNode.children.get(0)));
				} else if (exprNode.children.get(0).value.equals("~")) {
					exprStr = exprStr.concat(this.strOpCoder.strRev());
				} else {
					throw new Exception();
				}
				break;
			case "paren":
				exprStr = exprStr.concat(generateSamEXPR(exprNode.children.get(0)));
				break;
			case "method":
				exprStr = exprStr.concat(generateSamACTUALS(exprNode.children.get(1)));
				exprStr = exprStr.concat("LINK\n");
				exprStr = exprStr.concat("JSR " + exprNode.children.get(0).value + "\n");
				exprStr = exprStr.concat("UNLINK\n");
				case "var":
				exprStr = "PUSHOFF " + this.variables.get(exprNode.children.get(0).value).snd() + "\n";
				break;
			case "lit":
				exprStr = exprStr.concat(generateSamLIT(exprNode.children.get(0)));
				break;
            default:
                throw new Exception();
        }
        return exprStr;
	}

	private String generateSamBINOP(Node binopNode) throws Exception {
		// System.out.println("start generateSamBINOP");
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
				throw new Exception();
		}
	}

	private String generateSamUNOP(Node unopNode) throws Exception {
		// System.out.println("start generateSamUNOP");
		switch (unopNode.value) {
			case "~":
				return "PUSHIMM -1\nTIMES\n";
			case "!":
				return "NOT\n";
			default:
				throw new Exception();
		}
	}

    private String generateSamACTUALS(Node actualsNode) throws Exception {
		// System.out.println("start generateSamACTUALS");
        return null;
    }

	private String generateSamVAR(Node varNode) throws Exception {
		// System.out.println("start generateSamVAR");
		return generateSamIDENT(varNode.children.get(0));
	}

	private String generateSamLIT(Node litNode) throws Exception {
		// System.out.println("start generateSamLIT");
		this.sp++;
		switch (litNode.value) {
			case "bool":
				if (litNode.value == "true") {
					return "PUSHIMM 1\n";
				} else if (litNode.value == "false") {
					return "PUSHIMM 0\n";
				}
			case "num":
				return generateSamNUM(litNode.children.get(0));
			case "string":
				return generateSamSTRING(litNode.children.get(0));
			default:
				throw new Exception();
		}
	}

	private String generateSamNUM(Node numNode) throws Exception {
		// System.out.println("start generateSamNUM");
		this.sp++;
		return "PUSHIMM " + numNode.value + "\n";
	}

	private String generateSamSTRING(Node stringNode) throws Exception {
		// System.out.println("start generateSamSTRING");
		this.sp++;
		return "PUSHIMMSTR \"" + stringNode.value + "\"\n";
	}

	private String generateSamIDENT(Node identNode) throws Exception {
		// System.out.println("start generateSamIDENT");
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

	public String toString() {
		return "(" + this.a + ", " + this.b + ")";
	}
}
