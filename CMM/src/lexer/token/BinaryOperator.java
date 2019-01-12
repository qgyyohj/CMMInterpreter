package lexer.token;

public class BinaryOperator extends Token{
	
	public BinaryOperator(String bo,int line) {
		super(Tag.BINARY_OPERATOR,line,bo);
		switch(this.value){
			case "==":this.tag=Tag.EQEQ;
			case "<=":this.tag=Tag.LE;
			case ">=":this.tag=Tag.GE;
			case "<>":this.tag=Tag.NE;
			default:
				break;
		}
	}
}
