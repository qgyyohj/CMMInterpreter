package lexer.token;

public class Int extends Token{
	
	public final int value;
	
	public Int(int line,int value) {
		super(Tag.INT,line,value+"");
		this.value=value;
	}
}