package parser.node;

import java.util.ArrayList;

import lexer.token.Tag;
import lexer.token.Token;

public class TreeNode {
	
	private ArrayList<TreeNode> children;
	private TreeNode parent;
	public Token tok;
	
	public int type;
	public String error;
	
	private static String PREFIX_BRANCH = "├─";// 枝干
	private static String PREFIX_TRUNK = "│ ";// 树枝
	private static String PREFIX_LEAF = "└─";// 叶子
	private static String PREFIX_EMP = "  ";// 空
	
	//是否遍历
	public boolean visited=false;
	
	public TreeNode(int tag) {
		this.type=tag;
		this.children=new ArrayList<TreeNode>();
	}
	
	public TreeNode(Token tok) {
		this.tok=tok;
		this.type=tok.tag;
		this.children=new ArrayList<TreeNode>();
	}
	
	public TreeNode(String str) {
		this.error=str;
		this.children=new ArrayList<TreeNode>();
	}
	
	public void setParent(TreeNode parent) {
		this.parent=parent;
		if(!parent.children.contains(this)) {
			parent.children.add(this);
		}
	}
	
	public void addChildren(TreeNode child) {
		this.children.add(child);
	}
	
	public ArrayList<TreeNode> getChildren(){
		return this.children;
	}
	
	public TreeNode getParent() {
		return this.parent;
	}
	
	public String getType() {
		switch(this.type) {
		
		
		case Tag.PROGRAM:		return "Program";
		case Tag.STMT:			return "Stmt";
		case Tag.DECLARENODE:	return "DeclStmt";
		case Tag.IFNODE	:		return "IfStmt";
		case Tag.WHILENODE:		return "WhileStmt";
		case Tag.BREAKNODE:		return "BreakStmt";
		case Tag.ASSIGNNODE:	return "AssignStmt";
		case Tag.WRITENODE:		return "WriteStmt";
		case Tag.BLOCKNODE:		return "BlockStmt";
		case Tag.TYPENODE:		return "Type";
		case Tag.CONSTNODE:		return "Constant";		
		case Tag.CONDITION:		return "Condition";
		case Tag.IDNODE:		return "Identifier";		
		case Tag.OPTNODE:		return "Operator";
		case Tag.READNODE:		return "ReadStmt";
		case Tag.VALUENODE:		return "Value";
		case Tag.INIT:			return "Init";
		
		
		
		default:
			return this.getChild(0).getValue();
		}
	}
	
	public TreeNode getChild(int i) {
		return this.children.get(i);
	}
	
	public String getValue() {
		return this.tok == null ? getType() : this.tok.value;
	}
	
	public int getChildCount() {
		return this.children.size();
	}
	
	public void remove(int index) {
		children.remove(index);
	}
	
	public static void print(TreeNode node, String prefix) {
		if (prefix == null) {
			prefix = "";
			System.out.println(node.getValue());
		}
		prefix = prefix.replace(PREFIX_BRANCH, PREFIX_TRUNK);
		prefix = prefix.replace(PREFIX_LEAF, PREFIX_EMP);

		ArrayList<TreeNode> children = node.getChildren();
		for (int i = 0; i < children.size(); i++) {
			TreeNode child = children.get(i);
			if (i == children.size() - 1) {//最后一个是叶子
				System.out.println(prefix + PREFIX_LEAF + child.getValue());
				print(child, prefix + PREFIX_LEAF);
			} else {
				System.out.println(prefix + PREFIX_BRANCH + child.getValue());
				print(child, prefix + PREFIX_TRUNK);
			}
		}
	}
}
