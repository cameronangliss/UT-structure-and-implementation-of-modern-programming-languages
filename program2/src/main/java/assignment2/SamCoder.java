package assignment2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SamCoder {
	private NameSpace namespace;
	private Map<String, String> methodToType;
	private String currentMethod;
	private int ifCounter;
	private int whileCounter;
	private StrOpCoder strOpCoder;

	public SamCoder() {
		this.methodToType = new HashMap<String, String>();
		this.currentMethod = "";
		this.ifCounter = 0;
		this.whileCounter = 0;
		this.strOpCoder = new StrOpCoder();
	}

	public String generateSamCode(AST ast) throws Exception {
		this.namespace = new NameSpace(ast);
		for (Node methodDeclNode : ast.root.children) {
			String methodName = methodDeclNode.children.get(1).value;
			String methodType = methodDeclNode.children.get(0).value;
			this.methodToType.put(methodName, methodType);
		}
		return this.generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) throws Exception {
		// System.out.println("start generateSamPRGM");
        String prgmStr = "";
		Node main = new Node("method", Label.EXPR);
		main.addChild("main", Label.METHOD);
		prgmStr = prgmStr.concat(this.generateSamEXPR(main));
		prgmStr = prgmStr.concat("STOP\n\n");
        for (Node methodDeclNode : prgmNode.children) {
            prgmStr = prgmStr.concat(this.generateSamMETHODDECL(methodDeclNode));
        }
        return prgmStr;
	}

    private String generateSamMETHODDECL(Node methodDeclNode) throws Exception {
		// System.out.println("start generateSamMETHODDECL");
        String methodDeclStr = "";
		String methodName = methodDeclNode.children.get(1).value;
		this.currentMethod = methodName;
        methodDeclStr = methodDeclStr.concat(methodName + ":\n");
		Node bodyNode = methodDeclNode.children.get(methodDeclNode.children.size() - 1);
        methodDeclStr = methodDeclStr.concat(this.generateSamBODY(bodyNode));
		methodDeclStr = methodDeclStr.concat(methodName + "DONE:\n");
		methodDeclStr = methodDeclStr.concat("STOREOFF " + -(this.namespace.numParams(methodName) + 1) + "\n");
		methodDeclStr = methodDeclStr.concat("ADDSP " + -this.namespace.numLocals(methodName) + "\n");
		methodDeclStr = methodDeclStr.concat("RST\n\n");
        return methodDeclStr;
    }

    private String generateSamBODY(Node bodyNode) throws Exception {
		// System.out.println("start generateSamBODY");
        String bodyStr = "";
        for (int i = 0; i < bodyNode.children.size() - 1; i++) {
            bodyStr = bodyStr.concat(this.generateSamVARDECL(bodyNode.children.get(i)));
        }
		Node blockNode = bodyNode.children.get(bodyNode.children.size() - 1);
		if (blockNode.children.stream().noneMatch(stmtNode -> stmtNode.value.equals("return"))) {
			throw new Exception();
		}
        bodyStr = bodyStr.concat(this.generateSamBLOCK(blockNode, this.whileCounter));
        return bodyStr;
	}

    private String generateSamVARDECL(Node varDeclNode) throws Exception {
		// System.out.println("start generateSamVARDECL");
		String varDeclStr = "";
		if (varDeclNode.children.size() > 1) {
			varDeclStr = varDeclStr.concat("ADDSP " + (varDeclNode.children.size() - 1) + "\n");
		}
		return varDeclStr;
	}

	private String generateSamBLOCK(Node blockNode, int counter) throws Exception {
		// System.out.println("start generateSamBLOCK");
		String blockStr = "";
		for (Node child : blockNode.children) {
			blockStr = blockStr.concat(this.generateSamSTMT(child, counter));
		}
		return blockStr;
	}

	private String generateSamSTMT(Node stmtNode, int counter) throws Exception {
		// System.out.println("start generateSamSTMT - " + stmtNode.value);
		String stmtStr = "";
		switch (stmtNode.value) {
			case "if":
				this.ifCounter++;
				int currentIfCounter = this.ifCounter;
				stmtStr = stmtStr.concat(this.generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMPC IFTRUE" + currentIfCounter + "\n");
				stmtStr = stmtStr.concat(this.generateSamBLOCK(stmtNode.children.get(2), counter));
				stmtStr = stmtStr.concat("JUMP AFTERIF" + currentIfCounter + "\n");
				stmtStr = stmtStr.concat("IFTRUE" + currentIfCounter + ":\n");
				stmtStr = stmtStr.concat(this.generateSamBLOCK(stmtNode.children.get(1), counter));
				stmtStr = stmtStr.concat("AFTERIF" + currentIfCounter + ":\n");
				return stmtStr;
			case "while":
				this.whileCounter++;
				int currentWhileCounter = this.whileCounter;
				stmtStr = stmtStr.concat("STARTWHILE" + currentWhileCounter + ":\n");
				stmtStr = stmtStr.concat(this.generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("NOT\n");
				stmtStr = stmtStr.concat("JUMPC AFTERWHILE" + currentWhileCounter + "\n");
				stmtStr = stmtStr.concat(this.generateSamBLOCK(stmtNode.children.get(1), currentWhileCounter));
				stmtStr = stmtStr.concat("JUMP STARTWHILE" + currentWhileCounter + "\n");
				stmtStr = stmtStr.concat("AFTERWHILE" + currentWhileCounter + ":\n");
				return stmtStr;
			case "break":
				return "JUMP AFTERWHILE" + counter + "\n";
			case "return":
				String returnType = this.getExprType(stmtNode.children.get(0));
				String methodType = this.methodToType.get(this.currentMethod);
				if (!returnType.equals(methodType)) {
					throw new Exception();
				}
				stmtStr = stmtStr.concat(this.generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMP " + this.currentMethod + "DONE\n");
				return stmtStr;
			case "assign":
				stmtStr = stmtStr.concat(this.generateSamEXPR(stmtNode.children.get(1)));
				stmtStr = stmtStr.concat(this.generateSamVAR(stmtNode.children.get(0), false));
				return stmtStr;
			case "semicolon":
				return "";
			default:
				throw new Exception();
		}
	}

	private String generateSamEXPR(Node exprNode) throws Exception {
		// System.out.println("start generateSamEXPR - " + exprNode.value);
        String exprStr = "";
    	switch (exprNode.value) {
			case "ternary":
				this.ifCounter++;
				int currentIfCounter = this.ifCounter;
				exprStr = exprStr.concat(this.generateSamEXPR(exprNode.children.get(0)));
				exprStr = exprStr.concat("JUMPC IFTRUE" + currentIfCounter + "\n");
				exprStr = exprStr.concat(this.generateSamEXPR(exprNode.children.get(2)));
				exprStr = exprStr.concat("JUMP AFTERIF" + currentIfCounter + "\n");
				exprStr = exprStr.concat("IFTRUE" + currentIfCounter + ":\n");
				exprStr = exprStr.concat(this.generateSamEXPR(exprNode.children.get(1)));
				exprStr = exprStr.concat("AFTERIF" + currentIfCounter + ":\n");
				return exprStr;
			case "binop":
				String fstOperandStr = this.generateSamEXPR(exprNode.children.get(0));
				exprStr = exprStr.concat(fstOperandStr);
				exprStr = exprStr.concat(this.generateSamEXPR(exprNode.children.get(2)));
				if (!this.getExprType(exprNode.children.get(0)).equals("String")) {
					exprStr = exprStr.concat(this.generateSamBINOP(exprNode.children.get(1)));
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
				String operandStr = this.generateSamEXPR(exprNode.children.get(1));
				exprStr = exprStr.concat(operandStr);
				if (!this.getExprType(exprNode.children.get(1)).equals("String")) {
					exprStr = exprStr.concat(this.generateSamUNOP(exprNode.children.get(0)));
				} else if (exprNode.children.get(0).value.equals("~")) {
					exprStr = exprStr.concat(this.strOpCoder.strRev());
				} else {
					throw new Exception();
				}
				return exprStr;
			case "paren":
				return this.generateSamEXPR(exprNode.children.get(0));
			case "method":
				String methodName = exprNode.children.get(0).value;
				int numParams = this.namespace.numParams(methodName);
				if (exprNode.children.size() == 1) {
					if (numParams > 0) {
						throw new Exception();
					}
				} else {
					int numArgs = exprNode.children.get(1).children.size();
					if (numParams != numArgs) {
						throw new Exception();
					}
					for (Node subexprNode : exprNode.children.get(1).children) {
						// check that params and args have matching types
					}
				}
				exprStr = exprStr.concat("PUSHIMM 0\n");
				if (exprNode.children.size() == 2) {
					exprStr = exprStr.concat(this.generateSamACTUALS(exprNode.children.get(1)));
				}
				exprStr = exprStr.concat("LINK\n");
				exprStr = exprStr.concat("JSR " + methodName + "\n");
				exprStr = exprStr.concat("UNLINK\n");
				if (exprNode.children.size() == 2) {
					exprStr = exprStr.concat("ADDSP " + -numParams + "\n");
				}
				return exprStr;
			case "var":
				return this.generateSamVAR(exprNode.children.get(0), true);
			case "lit":
				return this.generateSamLIT(exprNode.children.get(0));
            default:
                throw new Exception();
        }
	}

	public String getExprType(Node exprNode) throws Exception {
        if (exprNode.label != Label.EXPR) {
            throw new Exception();
        }
        switch (exprNode.value) {
            case "ternary":
				String fstSubExprType = this.getExprType(exprNode.children.get(0));
				String sndSubExprType = this.getExprType(exprNode.children.get(1));
				String thdSubExprType = this.getExprType(exprNode.children.get(2));
				if (!fstSubExprType.equals("bool") || !sndSubExprType.equals(thdSubExprType)) {
					throw new Exception();
				}
				return sndSubExprType;
            case "binop":
				fstSubExprType = this.getExprType(exprNode.children.get(0));
				sndSubExprType = this.getExprType(exprNode.children.get(2));
				if (fstSubExprType.equals("bool")) {
					if (!"&|<>=".contains(exprNode.children.get(1).value) || !sndSubExprType.equals("bool")) {
						throw new Exception();
					}
					return "bool";
				} else if (fstSubExprType.equals("int")) {
					if ("+-*/%".contains(exprNode.children.get(1).value) && sndSubExprType.equals("int")) {
						return "int";
					} else if ("<>=".contains(exprNode.children.get(1).value) && sndSubExprType.equals("int")) {
						return "bool";
					} else {
						throw new Exception();
					}
				} else if (fstSubExprType.equals("String")) {
					if (exprNode.children.get(1).value.equals("+") && sndSubExprType.equals("String")) {
						return "String";
					} else if (exprNode.children.get(1).value.equals("*") && sndSubExprType.equals("int")) {
						return "String";
					} else if ("<>=".contains(exprNode.children.get(1).value) && sndSubExprType.equals("String")) {
						return "bool";
					} else {
						throw new Exception();
					}
				} else {
					throw new Exception();
				}
            case "unop":
                String subExprType = this.getExprType(exprNode.children.get(1));
				if (subExprType.equals("bool")) {
					if (!exprNode.children.get(0).value.equals("!")) {
						throw new Exception();
					}
					return "bool";
				} else if (subExprType.equals("int")) {
					if (!exprNode.children.get(0).value.equals("~")) {
						throw new Exception();
					}
					return "int";
				} else if (subExprType.equals("String")) {
					if (!exprNode.children.get(0).value.equals("~")) {
						throw new Exception();
					}
					return "String";
				} else {
					throw new Exception();
				}
            case "paren":
                return this.getExprType(exprNode.children.get(0));
            case "method":
				return this.methodToType.get(exprNode.children.get(0).value);
			case "var":
				return this.namespace.get(this.currentMethod).get(exprNode.children.get(0).value).fst();
			case "lit":
                String litVal = exprNode.children.get(0).value;
				if (litVal.equals("num")) {
					return "int";
				} else if (litVal.equals("string")) {
					return "String";
				} else if (litVal.equals("true") || litVal.equals("false")) {
					return "bool";
				} else {
					throw new Exception();
				}
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
			actualsStr = actualsStr.concat(this.generateSamEXPR(exprNode));
		}
		return actualsStr;
    }

	private String generateSamVAR(Node varNode, boolean isReading) throws Exception {
		// System.out.println("start generateSamVAR");
		int varLoc = this.namespace.get(this.currentMethod).get(varNode.value).snd();
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
				return this.generateSamNUM(litNode.children.get(0));
			case "string":
				return this.generateSamSTRING(litNode.children.get(0));
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

class NameSpace extends HashMap<String, Map<String, Pair<String, Integer>>> {
	public NameSpace(AST ast) throws Exception {
		for (Node methodDeclNode : ast.root.children) {
			Map<String, Pair<String, Integer>> paramVarToInfo = new HashMap<String, Pair<String, Integer>>();
			String methodName = methodDeclNode.children.get(1).value;
			// parameters
			if (methodDeclNode.children.size() == 4) {
				if (methodName.equals("main")) {
					throw new Exception();
				}
				Node formalsNode = methodDeclNode.children.get(2);
				int numParams = formalsNode.children.size() / 2;
				for (int i = 0; i < formalsNode.children.size() / 2; i++) {
					String paramVarName = formalsNode.children.get(2 * i + 1).value;
					String paramVarType = formalsNode.children.get(2 * i).value;
					paramVarToInfo.put(
						paramVarName,
						new Pair<String, Integer>(paramVarType, i - numParams)
					);
				}
			}
			// local variables
			Node bodyNode = methodDeclNode.children.get(methodDeclNode.children.size() - 1);
			Map<String, Pair<String, Integer>> localVarToInfo = new HashMap<String, Pair<String, Integer>>();
			List<Node> varTypeAndNameNodes = new ArrayList<Node>();
			for (int i = 0; i < bodyNode.children.size() - 1; i++) {
				Node varDeclNode = bodyNode.children.get(i);
				for (int j = 1; j < varDeclNode.children.size(); j++) {
					varTypeAndNameNodes.add(varDeclNode.children.get(0));
					varTypeAndNameNodes.add(varDeclNode.children.get(j));
				}
			}
			for (int i = 0; i < varTypeAndNameNodes.size() / 2; i++) {
				String localVarName = varTypeAndNameNodes.get(2 * i + 1).value;
				String localVarType = varTypeAndNameNodes.get(2 * i).value;
				localVarToInfo.put(
					localVarName,
					new Pair<String, Integer>(localVarType, i + 2)
				);
			}
			paramVarToInfo.putAll(localVarToInfo);
			this.put(methodName, paramVarToInfo);
		}
	}

	public int numParams(String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(methodName).values()) {
			if (pair.snd() < 0) {
				c++;
			}
		}
		return c;
	}

	public int numLocals(String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(methodName).values()) {
			if (pair.snd() > 0) {
				c++;
			}
		}
		return c;
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
