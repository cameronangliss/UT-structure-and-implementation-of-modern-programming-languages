package assignment2;

import java.io.FileWriter;
import java.io.IOException;

import edu.utexas.cs.sam.io.SamTokenizer;

public class LiveOak2Compiler {
	public static void main(String[] args) throws IOException {
        FileWriter writer = new FileWriter(args[1]);
        String samProgram = LiveOak2Compiler.compiler(args[0]);
        writer.write(samProgram);
        writer.close();
    }

	static String compiler(String fileName) {
		try {
            SamTokenizer tokenizer = new SamTokenizer(fileName, SamTokenizer.TokenizerOptions.PROCESS_STRINGS);
			AST ast = new AST(tokenizer);
            ast.topDownParse();
			SamCoder coder = new SamCoder();
            String samCode = coder.generateSamCode(ast);
            return samCode;
        } catch (Exception e) {
            System.err.println("Failed to compile " + fileName);
			throw new Error();
        }
	}
}
