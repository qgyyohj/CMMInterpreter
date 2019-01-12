package Entry;

import java.io.IOException;

import parser.Parser;
import parser.node.TreeNode;

public class ParserEntry {
	public ParserEntry(String filename) throws IOException {
		Parser p=new Parser(filename);
		p.execute();
		TreeNode root=p.getRoot();
		TreeNode.print(root, null);
	}
}
