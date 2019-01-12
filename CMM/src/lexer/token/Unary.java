package lexer.token;

public class Unary extends Token {
	public char op;
	
	public Unary(char op,int line){
		super(Tag.UNARY,line,"");
		
		//����ÿ��������ʲô���ʷ�������û��ʲô��
		switch(op) {
		case '{':this.tag=Tag.LEFTBRACE;break;
		case '}':this.tag=Tag.RIGHTBRACE;break;
		case '+':this.tag=Tag.PLUS;break;
		case '-':this.tag=Tag.MINUS;break;
		case '*':this.tag=Tag.MULT;break;
		case '/':this.tag=Tag.DIVIDE;break;
		case '[':this.tag=Tag.LEFTBRACKET;break;
		case ']':this.tag=Tag.RIGHTBRACKET;break;
		case '(':this.tag=Tag.LEFTBRA;break;
		case ')':this.tag=Tag.RIGHTBRA;break;
		case ';':this.tag=Tag.SEMI;break;
		case ',':this.tag=Tag.COMM;break;
		case '=':this.tag=Tag.EQ;break;
		case '<':this.tag=Tag.LT;break;
		case '>':this.tag=Tag.GT;break;
		}
		this.op=op;
		this.value=op+"";
	}
}
