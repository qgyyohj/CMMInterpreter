package lexer.token;

public class Real extends Token{
	public final Int number;
    public final String fraction;
    public final String realNum;
    public final double RealNum;

    public Real(Int n, String f) {
        super(Tag.REAL,n.line,"");
        number = n;
        fraction = "."+f;
        realNum=number.value+fraction;
        this.value=realNum;
        RealNum=Double.parseDouble(realNum);
    }
}
