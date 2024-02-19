package assignment2;

import java.io.IOException;

import edu.utexas.cs.sam.io.SamTokenizer;

public class LiveOak2Compiler {
	public static void main(String[] args) throws IOException {}

	static String compiler(String fileName) {
		try {
            SamTokenizer tokenizer = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
			AST ast = new AST();
            ast.topDownParse(tokenizer);
            String samCode = generateSamCode(ast);
            return samCode;
        } catch (Exception e) {
            System.err.println("Failed to compile " + fileName);
			throw new Error();
        }
	}

	static String generateSamCode(AST ast) {
		return null;
	}
}
