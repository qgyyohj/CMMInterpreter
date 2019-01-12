package lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import lexer.token.BinaryOperator;
import lexer.token.Int;
import lexer.token.Word;
import lexer.token.Real;
import lexer.token.Tag;
import lexer.token.Token;
import lexer.token.Unary;
import lexer.token.Word;
/*
 * �ʷ���������keyword�洢�����ֵȣ�bo&unary�洢����
 */
public class Lexer {
	public char peek = ' ';
	private Reader reader;
	
	private ArrayList<String> keyword 	 = new ArrayList<String>();
	private ArrayList<Character> unary	 = new ArrayList<Character>();
	private ArrayList<String> bo		 = new ArrayList<String>();
	//private ArrayList<String> identifier = new ArrayList<String>();
	
	/*
	 * �к�
	 */
	public static int line = 1;
	
	public Lexer(String filename) throws FileNotFoundException {
		init();
		File file = new File(filename);
		reader = new InputStreamReader(new FileInputStream(file));
	}
	public int readChar() throws IOException {
		return reader.read();
	}
	

	public void init() {
		//��ӱ�����
		keyword.add("if");
		keyword.add("else");
		keyword.add("while");
		keyword.add("read");
		keyword.add("write");
		keyword.add("int");
		keyword.add("real");
		keyword.add("bool");

		//��ӵ�Ŀ�����
		unary.add('+');
		unary.add('-');
		unary.add('*');
		unary.add('/');
		unary.add('=');
		unary.add('>');
		unary.add('<');
		unary.add('(');
		unary.add(')');
		unary.add('[');
		unary.add(']');
		unary.add('{');
		unary.add('}');
		unary.add(';');
		unary.add(',');
		
		//���˫Ŀ�������
		bo.add(">=");
		bo.add("<=");
		bo.add("<>");
		bo.add("[]");

        //reserve(Word.True);
        //reserve(Word.False);
        
	}
	
	
	//�ʷ������ķ��ض���
	public Token scan() throws IOException {
		
		
		/*
		 * �����հ�
		 */
		for (; ; peek = (char) readChar()) {
            if (peek == ' ' || peek == '\t')
                continue;
            else if (peek == '\r'&&(char)readChar()=='\n')
                line++;
            else if (peek == (char) (-1))
                return new Token(Tag.END,line,"eof");
            else break;
        }

        /*
         * �������ֳ���
         */
        if (Character.isDigit(peek)) {
            int v = 0;
            do {
                v = 10 * v + Character.digit(peek, 10);
                peek = (char) readChar();
            } while (Character.isDigit(peek));

            /*
             * ��������
             */
            if (peek != '.')
                return new Int(line,v);
            
            else if (peek=='.') {
            	/*
            	 * ��������
            	 */
                peek = (char) readChar();
                String f="";
                if (Character.isDigit(peek)) {
                    do {
                    	if(peek=='0') {
                    		f+="0";
                    	}else {
                    		f+=String.valueOf(peek);
                    	}
                        
                        peek = (char) readChar();
                    } while (Character.isDigit(peek));
                }
                /*
                 * �����ĵ���޷�ʶ��
                 */
                if (f.equals("")) {     
                    LexerException.unknownSymbolException(".");
                    peek=(char)readChar();
                    return new Token(Tag.EXCEPTION,line,"Exception");
                }

                Int number = new Int(line,v);
                String fraction = f;
                return new Real(number, fraction);
            }
        }
		
      //�������ֺͱ�ʶ��
        if (Character.isLetter(peek)||peek=='_') {
            StringBuffer b = new StringBuffer();        //ͨ��������ʵ��Ԥ��
            do {
                b.append(peek);
                peek = (char) readChar();
            } while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');

            String s = b.toString();

            //�ж����һ���ַ��Ƿ�Ϊ�»���
            char c = s.charAt(s.length() - 1);
            if (c == '_') {
                LexerException.identifierException();
                peek=(char)readChar();
                return new Token(Tag.EXCEPTION,line,"");
            }

            //�ʷ���ԪΪ�ؼ���
            if (keyword.contains(s))
            {
            	switch(s) {
            	case "if":return new Word(Tag.IF,line,s);
            	case "else":return new Word(Tag.ELSE,line,s);
            	case "while":return new Word(Tag.WHILE,line,s);
            	case "read":return new Word(Tag.READ,line,s);
            	case "write":return new Word(Tag.WRITE,line,s);
            	case "int":return new Word(Tag.INT,line,s);
            	case "real":return new Word(Tag.REAL,line,s);
            	case "bool":return new Word(Tag.BOOL,line,s);
            	default:
            		return null;
            	}
            }
                

            //���ǹؼ��־��Ǳ�ʶ��
            return new Word(Tag.IDENTIFIER,line,s);
        }

        //�����»���
        if (peek == '_') {
            LexerException.identifierException();
            peek=(char)readChar();
            return new Token(Tag.EXCEPTION,line,"Exception");
        }

        //���������
        if (peek == '#') {
            return new Token(Tag.END,line,"");
        }

        //�������������˴�ʱ��˵���������������
        Unary u;
        if (!unary.contains(peek)) {      //δ����ķ���
            LexerException.unknownSymbolException(String.valueOf(peek));
            peek=(char)readChar();
            return new Token(Tag.EXCEPTION,line,"Exception");
        }else {
        	u=new Unary(peek,line);
        }

        //Ԥ����һ���ַ�
        String s = String.valueOf(peek);
        peek = (char) readChar();
        s += String.valueOf(peek);

        //����ע��
        if (s.equals("//")) {
            int r;
            do {
                r = readChar();
            } while (r != '\n');

            line++;
            peek = ' ';
            return new Token(Tag.ANNOTATION,line,"annotation");
        }

        /*
         * ����ע��
         */
        if (s.equals("/*")) {
            for (; ; ) {
                peek = (char) readChar();
                if(peek == '\n'){
                    line++;
                }
                /*
                 * δ�պϵĶ���ע�ʹ���
                 */
                if (peek == (char) (-1)) {
                    LexerException.unclosedMultiAnnotation();
                    return new Token(Tag.ANNOTATION,line,"Exception");
                }
                if (peek == '*') {
                    /*
                     * Ԥ����һ���ַ�
                     */
                    s = String.valueOf(peek);
                    peek = (char) readChar();
                    s += String.valueOf(peek);

                    if (s.equals("*/")) {
                        peek = ' ';
                        return new Token(Tag.ANNOTATION,line,"");
                    }
                    continue;
                }
                continue;
            }
        }

        /*
         * �ж��Ƿ�Ϊ˫Ŀ�����
         */
        if (bo.contains(s)) {
            peek = ' ';
            return new BinaryOperator(s,line);
        }
		return u;
	}
}
