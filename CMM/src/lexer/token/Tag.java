package lexer.token;


public class Tag {
	    
    public final static int IF = 1;

    public final static int ELSE = 2;

    public final static int WHILE = 3;
    
    public final static int READ = 4;

    public final static int WRITE = 5;
    
    public final static int INT = 6;
    
    public final static int REAL = 7;
    
    public final static int IDENTIFIER = 8;
    
    public final static int KEYWORD = 9;
    
    public final static int TRUE = 10;

    public final static int FALSE = 11;

    public final static int INDEX = 12;        //参数

    public final static int MINUS = 13;

    public final static int AND = 14;
	
	public final static int OR = 15;
	//=
	public final static int EQ = 16;
	//==
	public final static int EQEQ=60;
	//<>
	public final static int NE = 17;
	//<
    public final static int LT = 18;
	//<=
    public final static int LE = 19;
    //>
    public final static int GT = 20;
    //>=
    public final static int GE = 21;
    //int real bool
    public final static int BASIC = 22;

    public final static int BREAK = 23;

    public final static int TEMP = 24;     

    public final static int ANNOTATION = 25;//注释
    
    public final static int EXCEPTION = 26;  //异常
    
    public final static int BINARY_OPERATOR = 27;
    
    public final static int UNARY = 28;
    
    public final static int END = 29;      //文本末尾
    
    public final static int BOOL = 30;
    
    /*
     * 上面试词法分析用的tag
     * -----------------LINE------------------
     * 下面是语法分析用到的tag
     */
    
    public final static int PROGRAM = 31;
    
    public final static int IDNODE = 32;
    
    public final static int CONSTNODE = 33;
    
    public final static int BREAKNODE = 34;
    
    public final static int TYPENODE = 35;
    
    public final static int DECLARENODE = 36;
    
    public final static int BLOCKNODE = 37;
    
    public final static int IFNODE = 55;
    
    public final static int WHILENODE = 38;
    
    public final static int ASSIGNNODE = 39;
    
    public final static int VALUENODE = 40;
    
    public final static int READNODE = 41;
    
    public final static int WRITENODE = 42;
    
    public final static int OPTNODE = 43;
    
    public final static int NEGATIVENODE = 44;
    
    public final static int EXPRNODE = 56;
    
    public final static int STMT = 57;
    
    public final static int ELSENODE = 58;
    
    
    /*
     * 上面试语法分析用的tag
     * -----------------LINE------------------
     * 下面是语我随便加的.....
     */
    //{
    public final static int LEFTBRACE = 45;
    //}
    public final static int RIGHTBRACE =46;
    
    public final static int PLUS = 47;
    
    public final static int DIVIDE = 48;
    
    public final static int MULT = 49;
    //[
    public final static int LEFTBRACKET = 50;
    //]
    public final static int RIGHTBRACKET = 61;
    //(
    public final static int LEFTBRA = 51;
    //)
    public final static int RIGHTBRA = 52;
    //;
    public final static int SEMI = 53;
    //,
    public final static int COMM = 54;
    
    public final static int INIT = 70;
    
    public final static int CONDITION = 71; 
    
    
}
