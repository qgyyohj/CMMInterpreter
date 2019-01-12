package Entry;

import java.io.IOException;

import lexer.Lexer;
import lexer.token.BinaryOperator;
import lexer.token.Int;
import lexer.token.Word;
import lexer.token.Real;
import lexer.token.Tag;
import lexer.token.Token;
import lexer.token.Unary;
import lexer.token.Word;

public class LexerEntry {
	
	public static Token token;
	public static int tempLine = 0;
	public static Lexer lexer;
	
	public LexerEntry(String filename) throws IOException{
		lexer= new Lexer(filename);     //�ļ�����
		Token token;
		for (; ; ) {
			token = lexer.scan();

			if (token == null) {
				return;
			}

			if (token.tag == Tag.ANNOTATION)
				continue;
			
			if (token.tag == Tag.EXCEPTION)
				continue;

			if (token.tag == Tag.END) {
				System.out.println();
				System.out.println();
				System.out.println("------------------------end------------------------");
				return;
			}
			
			Output(token);
	}
	}
		public static void Output(Token token) {
			
			if (tempLine != lexer.line) {
				System.out.println();
				System.out.printf("[L" + lexer.line + "] ");
				tempLine = lexer.line;
			}
			
			if (token instanceof Int) {
				System.out.printf("<INT, " + ((Int) token).value + "> ");
			} else if (token instanceof Real) {
				System.out.printf("<REAL, " + ((Real) token).RealNum + "> ");
			} else if (token instanceof Word) {
				if (token.tag==Tag.IF||token.tag==Tag.ELSE
						||token.tag==Tag.WHILE||token.tag==Tag.BREAK
						||token.tag==Tag.READ||token.tag==Tag.WRITE
						||token.tag==Tag.INT||token.tag==Tag.REAL) {
					System.out.printf("<KEYWORD, " + ((Word) token).value + "> ");
				} else {
					System.out.printf("<IDENTIFIER, " + ((Word) token).value + "> ");
				}
			} else if (token.tag==Tag.BINARY_OPERATOR||token.tag==Tag.EQEQ||token.tag==Tag.NE
					||token.tag==Tag.GE||token.tag==Tag.LE) {
				System.out.printf("< " + ((BinaryOperator) token).value + " > ");
			}else {
				System.out.printf("< " + ((Unary) token).op + " > ");
			}
		}
}
