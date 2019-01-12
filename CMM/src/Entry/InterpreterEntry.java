package Entry;

import java.io.IOException;

import interpreter.Interpreter;
import parser.Parser;

public class InterpreterEntry {

	public InterpreterEntry(String filename) throws IOException {
		Parser p = new Parser(filename);
		p.execute();
		(new Interpreter(p.getRoot())).program();
	}
	
}
