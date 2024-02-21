package assignment2;

import java.util.HashMap;
import java.util.Map;

class SamCoder {
	private int counter;
	private Map<String, Integer> variables;

	public SamCoder() {
		this.counter = 0;
		this.variables = new HashMap<String, Integer>();
	}

	public String generateSamCode(AST ast) {
		return generateSamPRGM(ast.root);
	}

	private String generateSamPRGM(Node prgmNode) {
		return generateSamBODY(prgmNode.children.get(0));
	}

	private String generateSamBODY(Node bodyNode) {
		return null;
	}

	private String generateSamVARDECL(Node varDeclNode) {
		String varDeclStr = "";
		varDeclStr.concat(generateSamTYPE(varDeclNode.children.get(0)));
		for (int i = 1; i < varDeclNode.children.size(); i++) {
			this.counter++;
			this.variables.put(varDeclNode.children.get(i).value, this.counter);
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
		if (!stmtNode.children.isEmpty()) {
			Node varNode = stmtNode.children.get(0);
			stmtStr.concat(generateSamVAR(varNode));
			Node exprNode = stmtNode.children.get(1);
			stmtStr.concat(generateSamEXPR(exprNode));
			int varLoc = this.variables.get(varNode.value);
			String varStr = "STOREOFF " + varLoc + "\n";
			stmtStr.concat(varStr);
		}
		return stmtStr;
	}

	private String generateSamEXPR(Node exprNode) {
		return null;
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

	private String generateSamTYPE(Node typeNode) {
		return "";
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
