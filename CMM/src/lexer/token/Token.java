package lexer.token;

/*
 * �ʷ���Ԫ���ʷ�������������
 */
public class Token {
	
	public int tag;	//��������token������
	public int line;		//��¼token���к�
	public String value;
	
	public Token(int tag,int line,String v) {
		this.tag = tag;
		this.line= line;
		this.value=v;
	}
}
