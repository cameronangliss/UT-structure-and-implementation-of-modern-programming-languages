package assignment2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SamCoder {
	private int ifCounter;
	private String currentMethod;
	private Map<String, Integer> methodParamCounts;
	private Map<String, Pair<String, Integer>> variables;
	private StrOpCoder strOpCoder;

	public SamCoder() {
		this.ifCounter = 0;
		this.currentMethod = "";
		this.methodParamCounts = new HashMap<String, Integer>();
		this.variables = new HashMap<String, Pair<String, Integer>>();
		this.strOpCoder = new StrOpCoder();
	}

	public String generateSamCode(AST ast) throws Exception {
		for (Node methodDeclNode : ast.root.children) {
			String methodName = methodDeclNode.children.get(1).value;
			int numParams;
			if (methodDeclNode.children.size() == 3) {
				numParams = 0;
			} else {
				numParams = methodDeclNode.children.get(2).children.size() / 2;
			}
			this.methodParamCounts.put(methodName, numParams);
		}
		return generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) throws Exception {
		// System.out.println("start generateSamPRGM");
        String prgmStr = "";
		Node main = new Node("method", Label.EXPR);
		main.addChild("main", Label.METHOD);
		prgmStr = prgmStr.concat(this.generateSamEXPR(main));
		prgmStr = prgmStr.concat("STOP\n\n");
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
		if (methodDeclNode.children.size() == 4) {
			List<Node> formalTypesAndNames = methodDeclNode.children.get(2).children;
			for (int i = 0; i < formalTypesAndNames.size() / 2; i++) {
				String type = formalTypesAndNames.get(2 * i).value;
				String name = formalTypesAndNames.get(2 * i + 1).value;
				int loc = i - this.methodParamCounts.get(methodName);
				Pair<String, Integer> typeLocPair = new Pair<String, Integer>(type, loc);
				this.variables.put(name, typeLocPair);
			}
		}
		Node bodyNode = methodDeclNode.children.get(methodDeclNode.children.size() - 1);
        methodDeclStr = methodDeclStr.concat(generateSamBODY(bodyNode));
		methodDeclStr = methodDeclStr.concat(methodName + "DONE:\n");
		methodDeclStr = methodDeclStr.concat("STOREOFF " + -(this.methodParamCounts.get(methodName) + 1) + "\n");
		methodDeclStr = methodDeclStr.concat("ADDSP " + -(bodyNode.children.get(0).children.size() - 1) + "\n");
		methodDeclStr = methodDeclStr.concat("RST\n\n");
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
			Pair<String, Integer> typeLocPair = new Pair<String, Integer>(varDeclNode.children.get(0).value, i + 1);
			this.variables.put(varDeclNode.children.get(i).value, typeLocPair);
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
				this.ifCounter++;
				int currentIfCounter = this.ifCounter;
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMPC IFTRUE" + currentIfCounter + "\n");
				stmtStr = stmtStr.concat(generateSamBLOCK(stmtNode.children.get(2)));
				stmtStr = stmtStr.concat("JUMP AFTERIF" + currentIfCounter + "\n");
				stmtStr = stmtStr.concat("IFTRUE" + currentIfCounter + ":\n");
				stmtStr = stmtStr.concat(generateSamBLOCK(stmtNode.children.get(1)));
				stmtStr = stmtStr.concat("AFTERIF" + currentIfCounter + ":\n");
				return stmtStr;
			case "while":
				return null;
			case "break":
				return null;
			case "return":
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMP " + this.currentMethod + "DONE\n");
				return stmtStr;
			case "assign":
				stmtStr = stmtStr.concat(generateSamEXPR(stmtNode.children.get(1)));
				stmtStr = stmtStr.concat(generateSamVAR(stmtNode.children.get(0), false));
				return stmtStr;
			default:
				throw new Exception();
		}
	}

	private String generateSamEXPR(Node exprNode) throws Exception {
		// System.out.println("start generateSamEXPR");
        String exprStr = "";
    	switch (exprNode.value) {
			case "ternary":
				return null;
			case "binop":
				String fstOperandStr = generateSamEXPR(exprNode.children.get(0));
				exprStr = exprStr.concat(fstOperandStr);
				exprStr = exprStr.concat(generateSamEXPR(exprNode.children.get(2)));
				if (
					exprNode
						.getAllDescendantVars()
						.stream()
						.noneMatch(varName -> this.variables
							.getOrDefault(varName, new Pair<String, Integer>("", 0))
							.fst()
							.equals("String")
						)
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
				return exprStr;
            case "unop":
				String operandStr = generateSamEXPR(exprNode.children.get(1));
				exprStr = exprStr.concat(operandStr);
				if (
					exprNode
						.getAllDescendantVars()
						.stream()
						.noneMatch(varName -> this.variables
							.getOrDefault(varName, new Pair<String, Integer>("", 0))
							.fst()
							.equals("String")
						)
					&& !operandStr.contains("PUSHIMMSTR")
				) {
					exprStr = exprStr.concat(generateSamUNOP(exprNode.children.get(0)));
				} else if (exprNode.children.get(0).value.equals("~")) {
					exprStr = exprStr.concat(this.strOpCoder.strRev());
				} else {
					throw new Exception();
				}
				return exprStr;
			case "paren":
				return generateSamEXPR(exprNode.children.get(0));
			case "method":
				String methodName = exprNode.children.get(0).value;
				this.currentMethod = methodName;
				exprStr = exprStr.concat("PUSHIMM 0\n");
				if (exprNode.children.size() == 2) {
					exprStr = exprStr.concat(generateSamACTUALS(exprNode.children.get(1)));
				}
				exprStr = exprStr.concat("LINK\n");
				exprStr = exprStr.concat("JSR " + methodName + "\n");
				exprStr = exprStr.concat("UNLINK\n");
				if (exprNode.children.size() == 2) {
					exprStr = exprStr.concat("ADDSP " + -this.methodParamCounts.get(methodName) + "\n");
				}
				return exprStr;
			case "var":
				return this.generateSamVAR(exprNode.children.get(0), true);
			case "lit":
				return generateSamLIT(exprNode.children.get(0));
            default:
                throw new Exception();
        }
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
        String actualsStr = "";
		for (Node exprNode : actualsNode.children) {
			actualsStr = actualsStr.concat(generateSamEXPR(exprNode));
		}
		return actualsStr;
    }

	private String generateSamVAR(Node varNode, boolean isReading) throws Exception {
		// System.out.println("start generateSamVAR");
		int varLoc = this.variables.get(varNode.value).snd();
		if (isReading) {
			return "PUSHOFF " + varLoc + "\n";
		} else {
			return "STOREOFF " + varLoc + "\n";
		}
	}

	private String generateSamLIT(Node litNode) throws Exception {
		// System.out.println("start generateSamLIT");
		switch (litNode.value) {
			case "true":
				return "PUSHIMM 1\n";
			case "false":
				return "PUSHIMM 0\n";
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
		return "PUSHIMM " + numNode.value + "\n";
	}

	private String generateSamSTRING(Node stringNode) throws Exception {
		// System.out.println("start generateSamSTRING");
		return "PUSHIMMSTR \"" + stringNode.value + "\"\n";
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
