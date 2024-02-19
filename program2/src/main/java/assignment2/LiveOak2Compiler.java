package assignment2;

import java.io.IOException;

public class LiveOak2Compiler {
	public static void main(String[] args) throws IOException {}

	static String compiler(String fileName) {
		AST ast = new AST(fileName);
		String pgm = ast.compile();
		return pgm;
	}
}
