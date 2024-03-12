package assignment3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class SamCoder {
	private ClassSpace namespace;
	private String currentClass;
	private String currentMethod;
	private int ifCounter;
	private int whileCounter;
	private StrOpCoder strOpCoder;

	public SamCoder() {
		this.currentClass = "";
		this.currentMethod = "";
		this.ifCounter = 0;
		this.whileCounter = 0;
		this.strOpCoder = new StrOpCoder();
	}

	public String generateSamCode(AST ast) throws Exception {
		this.namespace = new ClassSpace(ast);
		return this.generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) throws Exception {
		// System.out.println("start generateSamPRGM");
        String prgmStr = "";
		// adding artificial code to ensure Main.main() is entrypoint
		String entrypointObjectName = "mainObj";
		Node mainInstDecl = new Node(null, Label.VARDECL);
		mainInstDecl.addChild("Main", Label.TYPE);
		mainInstDecl.addChild(entrypointObjectName, Label.IDENT);
		this.namespace.put("", new Pair<VarSpace, MethodSpace>(new VarSpace(mainInstDecl, true), new MethodSpace()));
		Node entryPoint = new Node("dot", Label.EXPR);
		entryPoint.addChild(entrypointObjectName, Label.CLASS);
		entryPoint.addChild("main", Label.METHOD);
		prgmStr = prgmStr.concat(this.generateSamEXPR(entryPoint));
		prgmStr = prgmStr.concat("STOP\n\n");
		// generating code from provided program
        for (Node classDeclNode : prgmNode.children) {
            prgmStr = prgmStr.concat(this.generateSamCLASSDECL(classDeclNode));
        }
        return prgmStr;
	}

	private String generateSamCLASSDECL(Node classDeclNode) throws Exception {
		// System.out.println("start generateSamCLASSDECL");
		String classDeclStr = "";
		this.currentClass = classDeclNode.children.get(0).value;
		for (int i = 1; i < classDeclNode.children.size(); i++) {
			if (classDeclNode.children.get(i).label == Label.VARDECL) {
				classDeclStr = classDeclStr.concat(this.generateSamVARDECL(classDeclNode.children.get(i)));
			} else {
				classDeclStr = classDeclStr.concat(this.generateSamMETHODDECL(classDeclNode.children.get(i)));
			}
		}
		return classDeclStr;
	}

    private String generateSamMETHODDECL(Node methodDeclNode) throws Exception {
		// System.out.println("start generateSamMETHODDECL");
        String methodDeclStr = "";
		String methodName = methodDeclNode.children.get(1).value;
		this.currentMethod = methodName;
        methodDeclStr = methodDeclStr.concat(methodName + ":\n");
		Node bodyNode = methodDeclNode.children.get(methodDeclNode.children.size() - 1);
		String methodType = methodDeclNode.children.get(0).value;
        methodDeclStr = methodDeclStr.concat(this.generateSamBODY(bodyNode, methodType));
		methodDeclStr = methodDeclStr.concat(methodName + "DONE:\n");
		methodDeclStr = methodDeclStr.concat("STOREOFF " + -(this.namespace.numParams(this.currentClass, methodName) + 1) + "\n");
		methodDeclStr = methodDeclStr.concat("ADDSP " + -this.namespace.numLocals(this.currentClass, methodName) + "\n");
		methodDeclStr = methodDeclStr.concat("RST\n\n");
        return methodDeclStr;
    }

    private String generateSamBODY(Node bodyNode, String methodType) throws Exception {
		// System.out.println("start generateSamBODY");
        String bodyStr = "";
        for (int i = 0; i < bodyNode.children.size() - 1; i++) {
            bodyStr = bodyStr.concat(this.generateSamVARDECL(bodyNode.children.get(i)));
        }
		Node blockNode = bodyNode.children.get(bodyNode.children.size() - 1);
		if (!methodType.equals("void") && blockNode.children.stream().noneMatch(stmtNode -> stmtNode.value.equals("return"))) {
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
				String methodType = this.namespace.get(this.currentClass).snd().get(this.currentMethod).fst();
				if (!returnType.equals(methodType)) {
					throw new Exception();
				}
				stmtStr = stmtStr.concat(this.generateSamEXPR(stmtNode.children.get(0)));
				stmtStr = stmtStr.concat("JUMP " + this.currentMethod + "DONE\n");
				return stmtStr;
			case "assign":
				Node exprNode = stmtNode.children.get(1);
				stmtStr = stmtStr.concat(this.generateSamEXPR(exprNode));
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
			case "this":
				return exprStr;
			case "null":
				return exprStr;
			case "new":
				return exprStr;
			case "dot":
				String objectName = exprNode.children.get(0).value;
				String objectType = this.namespace.getVarInfo(this.currentClass, this.currentMethod, objectName).fst();
				String methodName = exprNode.children.get(1).value;
				int numParams = this.namespace.numParams(objectType, methodName);
				if (exprNode.children.size() == 2) {
					if (numParams > 0) {
						throw new Exception();
					}
				} else {
					Node actualsNode = exprNode.children.get(2);
					if (numParams != actualsNode.children.size()) {
						throw new Exception();
					}
					for (int i = 0; i < actualsNode.children.size(); i++) {
						Node subexprNode = actualsNode.children.get(i);
						String argType = this.getExprType(subexprNode);
						String paramName = this.namespace.getParamFromIndex(objectType, methodName, i);
						String paramType = this.namespace.getVarInfo(objectType, methodName, paramName).fst();
						if (!argType.equals(paramType)) {
							throw new Exception();
						}
					}
				}
				exprStr = exprStr.concat("PUSHIMM 0\n");
				if (exprNode.children.size() == 3) {
					exprStr = exprStr.concat(this.generateSamACTUALS(exprNode.children.get(2)));
				}
				exprStr = exprStr.concat("LINK\n");
				exprStr = exprStr.concat("JSR " + methodName + "\n");
				exprStr = exprStr.concat("UNLINK\n");
				if (exprNode.children.size() == 3) {
					exprStr = exprStr.concat("ADDSP " + -numParams + "\n");
				}
				return exprStr;
			case "method":
				methodName = exprNode.children.get(0).value;
				numParams = this.namespace.numParams("", methodName);
				if (exprNode.children.size() == 1) {
					if (numParams > 0) {
						throw new Exception();
					}
				} else {
					Node actualsNode = exprNode.children.get(1);
					if (numParams != actualsNode.children.size()) {
						throw new Exception();
					}
					for (int i = 0; i < actualsNode.children.size(); i++) {
						Node subexprNode = actualsNode.children.get(i);
						String argType = this.getExprType(subexprNode);
						String paramName = this.namespace.getParamFromIndex("", methodName, i);
						String paramType = this.namespace.get("").snd().get(paramName).fst();
						if (!argType.equals(paramType)) {
							throw new Exception();
						}
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
			case "this":
				return this.currentClass;
			case "null":
				return "null";
			case "new":
				return exprNode.children.get(0).value;
			case "dot":
				String objectName = exprNode.children.get(0).value;
				String objectType = this.namespace.getVarInfo(this.currentClass, this.currentMethod, objectName).fst();
				String methodName = exprNode.children.get(1).value;
				return this.namespace.get(objectType).snd().get(methodName).fst();
            case "method":
				methodName = exprNode.children.get(0).value;
				return this.namespace.get(this.currentClass).snd().get(methodName).fst();
			case "var":
				return this.namespace.getVarInfo(this.currentClass, this.currentMethod, exprNode.children.get(0).value).fst();
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
		int varLoc = this.namespace.getVarInfo(this.currentClass, this.currentMethod, varNode.value).snd();
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

class ClassSpace extends HashMap<String, Pair<VarSpace, MethodSpace>> {
	public ClassSpace(AST ast) throws Exception {
		for (Node classDeclNode : ast.root.children) {
			List<Node> varDeclNodes = classDeclNode.children.stream().filter(node -> node.label == Label.VARDECL).collect(Collectors.toList());
			VarSpace varSpace = new VarSpace(varDeclNodes);
			List<Node> methodDeclNodes = classDeclNode.children.stream().filter(node -> node.label == Label.METHODDECL).collect(Collectors.toList());
			MethodSpace methodSpace = new MethodSpace(methodDeclNodes);
			this.put(classDeclNode.children.get(0).value, new Pair<VarSpace, MethodSpace>(varSpace, methodSpace));
		}
	}

	public Pair<String, Integer> getVarInfo(String className, String methodName, String varName) {
		Pair<String, Integer> varInfo;
		varInfo = this.get(className).fst().get(varName);
		if (varInfo == null) {
			varInfo = this.get(className).snd().get(methodName).snd().get(varName);
		}
		return varInfo;
	}

	public int numParams(String className, String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(className).snd().get(methodName).snd().values()) {
			if (pair.snd() < 0) {
				c++;
			}
		}
		return c;
	}

	public int numLocals(String className, String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(className).snd().get(methodName).snd().values()) {
			if (pair.snd() > 0) {
				c++;
			}
		}
		return c;
	}

	public String getParamFromIndex(String className, String methodName, int paramIndex) throws Exception {
        VarSpace varSpace = this.get(className).snd().get(methodName).snd();
		int numParams = this.numParams(className, methodName);
		for (Map.Entry<String, Pair<String, Integer>> varEntry : varSpace.entrySet()) {
			if (varEntry.getValue().snd() == paramIndex - numParams) {
				return varEntry.getKey();
			}
		}
		throw new Exception();
	}
}

class MethodSpace extends HashMap<String, Pair<String, VarSpace>> {
	// dummy constructor if we just want an empty MethodSpace
	public MethodSpace() {}

	public MethodSpace(List<Node> methodDeclNodes) throws Exception {
		for (Node methodDeclNode : methodDeclNodes) {
			String type = methodDeclNode.children.get(0).value;
			VarSpace varSpace = new VarSpace(methodDeclNode);
			Pair<String, VarSpace> typeVarSpacePair = new Pair<String, VarSpace>(type, varSpace);
			this.put(methodDeclNode.children.get(1).value, typeVarSpacePair);
		}
	}
}

class VarSpace extends HashMap<String, Pair<String, Integer>> {
	public VarSpace(Node methodDeclNode) throws Exception {
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
				this.put(paramVarName, new Pair<String, Integer>(paramVarType, i - numParams));
			}
		}
		// local variables
		Node bodyNode = methodDeclNode.children.get(methodDeclNode.children.size() - 1);
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
			this.put(localVarName, new Pair<String, Integer>(localVarType, i + 2));
		}
	}

	public VarSpace(List<Node> varDeclNodes) {
		// class variables
		int counter = 0;
		for (int i = 0; i < varDeclNodes.size(); i++) {
			Node varDeclNode = varDeclNodes.get(i);
			String varType = varDeclNode.children.get(0).value;
			for (int j = 1; j < varDeclNode.children.size(); j++) {
				counter++;
				String varName = varDeclNode.children.get(j).value;
				this.put(varName, new Pair<String, Integer>(varType, counter));
			}
		}
	}

	// hack to differentiate constructor taking methodDeclNode from constructor taking varDeclNode
	public VarSpace(Node varDeclNode, boolean isVarDeclNode) {
		String varType = varDeclNode.children.get(0).value;
		for (int j = 1; j < varDeclNode.children.size(); j++) {
			String varName = varDeclNode.children.get(j).value;
			this.put(varName, new Pair<String, Integer>(varType, j));
		}
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
