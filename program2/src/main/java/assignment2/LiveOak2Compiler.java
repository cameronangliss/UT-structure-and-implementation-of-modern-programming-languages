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
			System.out.println("Fatal error: could not compile program");
			return "STOP\n";
		}
	}

	static String getProgram(SamTokenizer f) {
		try {
			String pgm="";
			while(f.peekAtKind()!=TokenType.EOF)
				pgm += getMethod(f);
			return pgm;
		} catch(Exception e) {
			System.out.println("Fatal error: could not compile program");
			return "STOP\n";
		}		
	}

	static String getMethod(SamTokenizer f) {
		// TODO: add code to convert a method declaration to SaM code.
		// TODO: add appropriate exception handlers to generate useful error msgs.
		f.check("int"); // must match at beginning
		String methodName = f.getString(); 
		f.check ("(");
		String formals = getFormals(f);
		f.check(")");
		f.check("{");
		// compile method body
		f.check("}");
		return null;
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
