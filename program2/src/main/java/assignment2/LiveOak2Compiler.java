package assignment2;

import edu.utexas.cs.sam.io.SamTokenizer;

import java.io.IOException;

public class LiveOak2Compiler {
	public static void main(String[] args) throws IOException {}

	static String compiler(String fileName) {
		//returns SaM code for program in file
		try {
			SamTokenizer f = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
			String pgm = getProgram(f, fileName);
			return pgm;
		} catch (Exception e) {
			System.err.println("Failed to compile " + fileName);
			throw new Error();
		}
	}

	static String getProgram(SamTokenizer f, String fileName) throws Exception {
		AST ast = new AST();
		ast.parse(f);
		String pgm = ast.generateProgram();
		return pgm;
	}

	// static String getMethod(SamTokenizer f) {
	// 	// TODO: add code to convert a method declaration to SaM code.
	// 	// TODO: add appropriate exception handlers to generate useful error msgs.
	// 	String method = "";
	// 	f.check("int"); // must match at beginning
	// 	String methodName = f.getString(); 
	// 	f.check("(");
	// 	String formals = getFormals(f);
	// 	f.check(")");
	// 	f.check("{");
	// 	// compile method body
	// 	f.check("return");
	// 	f.check("}");
	// 	return method;
	// }

	// static String getExp(SamTokenizer f) {
	// 	switch (f.peekAtKind()) {
	// 		case INTEGER: //E -> integer
	// 			return "PUSHIMM " + f.getInt() + "\n";
	// 		case OPERATOR:  
	// 		{
	// 		}
	// 		default:
	// 			return "ERROR\n";
	// 	}
	// }

	// static String getFormals(SamTokenizer f) {
	// 	return null;
	// }
}
