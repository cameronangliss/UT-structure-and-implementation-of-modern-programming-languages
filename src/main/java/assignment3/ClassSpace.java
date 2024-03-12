package assignment3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassSpace extends HashMap<String, Pair<MethodSpace, Set<String>>> {
	public ClassSpace(AST ast) throws Exception {
		for (Node classDeclNode : ast.root.children) {
			List<Node> methodDeclNodes = classDeclNode.children.stream().filter(node -> node.label == Label.METHODDECL).collect(Collectors.toList());
            MethodSpace methodSpace = new MethodSpace(methodDeclNodes);
			this.put(classDeclNode.value, new Pair<MethodSpace, Set<String>>(methodSpace, new HashSet<String>()));
		}
	}

	public int numParams(String className, String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(className).fst().get(methodName).snd().values()) {
			if (pair.snd() < 0) {
				c++;
			}
		}
		return c;
	}

	public int numLocals(String className, String methodName) {
		int c = 0;
		for (Pair<String, Integer> pair : this.get(className).fst().get(methodName).snd().values()) {
			if (pair.snd() > 0) {
				c++;
			}
		}
		return c;
	}

	public String getParamFromIndex(String className, String methodName, int paramIndex) throws Exception {
        VarSpace varSpace = this.get(className).fst().get(methodName).snd();
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
	public MethodSpace(List<Node> methodDeclNodes) throws Exception {
		for (Node methodDeclNode : methodDeclNodes) {
			String type = methodDeclNode.children.get(0).value;
			VarSpace varSpace = new VarSpace(methodDeclNode);
			Pair<String, VarSpace> typeVarSpacePair = new Pair<String, VarSpace>(type, varSpace);
			this.put(methodDeclNode.value, typeVarSpacePair);
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
