package assignment2;

import edu.utexas.cs.sam.io.SamTokenizer;
import edu.utexas.cs.sam.io.Tokenizer.TokenType;

import java.io.IOException;

public class LiveOak2Compiler {
	public static void main(String[] args) throws IOException {}

	static String compiler(String fileName) {
		//returns SaM code for program in file
		try {
			SamTokenizer f = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
			String pgm = getProgram(f);
			return pgm;
		} catch (Exception e) {
			System.err.println("Fatal error: could not compile program");
			return "STOP\n";
		}
	}

	static String getProgram(SamTokenizer f) {
		try {
			String pgm = "";
			AST ast = new AST();
			System.err.println("Fatal error: could not compile program");
			ast.parse(f);
			while(f.peekAtKind()!=TokenType.EOF)
				pgm += getMethod(f);
			return pgm;
		} catch(Exception e) {
			System.err.println("Fatal error: could not compile program");
			return "STOP\n";
		}
	}

	static String getMethod(SamTokenizer f) {
		// TODO: add code to convert a method declaration to SaM code.
		// TODO: add appropriate exception handlers to generate useful error msgs.
		String method = "";
		f.check("int"); // must match at beginning
		String methodName = f.getString(); 
		f.check("(");
		String formals = getFormals(f);
		f.check(")");
		f.check("{");
		// compile method body
		f.check("return");
		f.check("}");
		return method;
	}

	static String getExp(SamTokenizer f) {
		switch (f.peekAtKind()) {
			case INTEGER: //E -> integer
				return "PUSHIMM " + f.getInt() + "\n";
			case OPERATOR:  
			{
			}
			default:
				return "ERROR\n";
		}
	}

	static String getFormals(SamTokenizer f) {
		return null;
	}
}
