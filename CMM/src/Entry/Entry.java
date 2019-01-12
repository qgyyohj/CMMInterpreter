package Entry;

import java.io.IOException;
import java.util.Scanner;

public class Entry {

	public static void main(String[] args) throws IOException {
		String filename,flag;
		Scanner sc=new Scanner(System.in);

		System.out.println("------------------------使用方法------------------------");
		System.out.println("读取文件：输入文件完整路径和相应选项（输入为txt文件）:");
		System.out.println("\t词法分析加后缀'-l'，如：D:\\example.txt -l");
		System.out.println("\t语法分析加后缀'-p'，如：D:\\example.txt -p");
		System.out.println("\t解释执行加后缀'-i'，如：D:\\example.txt -i");
		System.out.println("退出请输入:exit");
		System.out.println("--------------------------------------------------------");
		filename=(String) sc.next();//输入文件路径
		if(filename.equals("exit"))
			return;
		flag=(String) sc.next();//运行选项		
		switch(flag) {
			case "-l":new LexerEntry(filename);break;
			case "-p":new ParserEntry(filename);break;
			case "-i":new InterpreterEntry(filename);break;
			default:break;
		}
		Entry.main(null);
		System.exit(0);
	}
}
