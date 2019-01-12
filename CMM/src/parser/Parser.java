package parser;

import java.io.IOException;
import java.util.ArrayList;
import lexer.Lexer;
import lexer.token.Tag;
import lexer.token.Token;
import parser.node.TreeNode;

public class Parser {
	
	private ArrayList<Token> tokens = new ArrayList<Token>();
	private int index=0;//记录token的游标
	private Token currentToken=null;
	private static TreeNode root;
	private static Lexer lex;

	public Parser(String filename) throws IOException {
		lex=new Lexer(filename);		
		setTokens();
			
		if(tokens.size()!=0) {
			currentToken = tokens.get(0);
		}
	}
	
	private void setTokens() throws IOException {
		currentToken=lex.scan();
		for(;currentToken.tag!=Tag.END;) {
			if(currentToken.tag!=Tag.ANNOTATION
				&&currentToken.tag!=Tag.EXCEPTION)
			tokens.add(currentToken);
			currentToken=lex.scan();
		}
	}
	
	public TreeNode getRoot() {
		return root;
	}
	
	public void error(String str) {
		System.out.println("L"+currentToken.line+": "+str);
	}
	
	public TreeNode execute() {
		root = new TreeNode(Tag.PROGRAM);
		for(;index<tokens.size();) {
			root.addChildren(statement());
		}
		return root;
	}
	
	//下一个Token
	private void nextToken() {
		index++;
		if(index>tokens.size()-1) {
			currentToken=null;
			if(index>tokens.size())
                index--;
            return;
        }
        currentToken = tokens.get(index);
    }
	
	private final TreeNode statement() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        // 赋值语句
        if (currentToken != null 
        		&& currentToken.tag==Tag.IDENTIFIER) {
            tempNode = assign_stm(false);
        }
        // 声明语句
        else if (currentToken != null
                && currentToken.tag==Tag.INT
                || currentToken.tag==Tag.REAL 
                || currentToken.tag==Tag.BOOL) {
            tempNode = declare_stm();
        }
        // If条件语句
        else if (currentToken != null&& currentToken.tag==Tag.IF) {
            tempNode = if_stm();
        }
        // While循环语句
        else if (currentToken != null&& currentToken.tag==Tag.WHILE) {
            tempNode = while_stm();
        }
        // read语句
        else if (currentToken != null&& currentToken.tag==Tag.READ) {
        	TreeNode read=new TreeNode(currentToken);
            TreeNode readNode = new TreeNode(Tag.READNODE);
            readNode.addChildren(read);
            readNode.addChildren(read_stm());
            tempNode = readNode;
        }
        // write语句
        else if (currentToken != null&& currentToken.tag==Tag.WRITE) {
        	TreeNode write=new TreeNode(currentToken);
            TreeNode writeNode = new TreeNode(Tag.WRITENODE);
            writeNode.addChildren(write);
            writeNode.addChildren(write_stm());
            tempNode = writeNode;
        }
        //block
        else if(currentToken != null&& currentToken.tag==Tag.LEFTBRACE) {
        	tempNode=block_stm();
        }
        // 出错处理
        else {
            String error = " 语句以错误的token开始" + "\n";
            error(error);
            tempNode=new TreeNode(error);
            nextToken();
        }
        return tempNode;
    }
	
	
	private final TreeNode block_stm() {
		//block语句的根节点
		TreeNode blockNode=new TreeNode(Tag.BLOCKNODE);
		if(currentToken !=null&&currentToken.tag==Tag.LEFTBRACE) {
			nextToken();
		}
		return null;
	}

    private final TreeNode if_stm() {
        // if语句是否有大括号,默认为true
        boolean hasIfBrace = true;
        // else语句是否有大括号,默认为true
        boolean hasElseBrace = true;
        // if函数返回结点的根结点
        TreeNode ifNode = new TreeNode(Tag.IFNODE);
        TreeNode If=new TreeNode(currentToken);
        ifNode.addChildren(If);
        nextToken();
        // 匹配左括号(
        if (currentToken != null&& currentToken.tag==Tag.LEFTBRA) {
            nextToken();
        } else { // 报错
            String error = " if条件语句缺少左括号\"(\"" + "\n";
            error(error);
            //ifNode.addChildren(null);
        }
        // condition
        //TreeNode condition=new TreeNode(currentToken);
        TreeNode conditionNode = new TreeNode(Tag.CONDITION);
        //conditionNode.addChildren(condition);
        
        conditionNode.addChildren(condition());
        ifNode.addChildren(conditionNode);
        // 匹配右括号)
        if (currentToken != null
                && currentToken.tag==Tag.RIGHTBRA) {
            nextToken();
        } else { // 报错
            String error = " if条件语句缺少右括号\")\"" + "\n";
            error(error);
            //ifNode.addChildren(null);
        }
        // 匹配左大括号{
        if (currentToken != null
                && currentToken.tag==Tag.LEFTBRACE) {
            nextToken();
        } else {
            hasIfBrace = false;
        }
        // statement
        //TreeNode statement = new TreeNode(currentToken);
        TreeNode statementNode = new TreeNode(Tag.BLOCKNODE);
        //statementNode.addChildren(statement);
        ifNode.addChildren(statementNode);
        if (hasIfBrace) {
            while (currentToken != null) {
                if (!(currentToken.tag==Tag.RIGHTBRACE))
                    statementNode.addChildren(statement());
                else if (statementNode.getChildCount() == 0) {
                    ifNode.remove(ifNode.getChildCount() - 1);
                    statementNode=new TreeNode("EmptyStmt");
                    //statementNode.setContent("EmptyStm");
                    ifNode.addChildren(statementNode);
                    break;
                } else {
                    break;
                }
            }
            // 匹配右大括号}
            if (currentToken != null
                    && currentToken.tag==Tag.RIGHTBRACE) {
                nextToken();
            } else { // 报错
                String error = " if条件语句缺少右大括号\"}\"" + "\n";
                error(error);
                //ifNode.addChildren(null);
            }
        } else {
            if (currentToken != null)
                statementNode.addChildren(statement());
        }
        if (currentToken != null
                && currentToken.tag==Tag.ELSE) {
        	
            TreeNode elseNode = new TreeNode(currentToken);
            ifNode.addChildren(elseNode);
            nextToken();
            // 匹配左大括号{
            if (currentToken.tag==Tag.LEFTBRACE) {
                nextToken();
            } else {
                hasElseBrace = false;
            }
            if (hasElseBrace) {
                // statement
                while (currentToken != null && !(currentToken.tag==Tag.RIGHTBRACE)) {
                	TreeNode block=new TreeNode(Tag.BLOCKNODE);
                	block.addChildren(statement());
                	ifNode.addChildren(block);
                }
                // 匹配右大括号}
                if (currentToken != null
                        && currentToken.tag==Tag.RIGHTBRACE) {
                    nextToken();
                } else { // 报错
                    String error = " else语句缺少右大括号\"}\"" + "\n";
                    error(error);
                    //elseNode.addChildren(null);
                }
            } else {
                if (currentToken != null)
                    elseNode.addChildren(statement());
            }
        }
        return ifNode;
    }


    private final TreeNode while_stm() {
        // 是否有大括号,默认为true
        boolean hasBrace = true;
        // while函数返回结点的根结点
        TreeNode whileNode = new TreeNode(Tag.WHILENODE);
        TreeNode While=new TreeNode(currentToken);
        whileNode.addChildren(While);
        nextToken();
        // 匹配左括号(
        if (currentToken != null&& currentToken.tag==Tag.LEFTBRA) {
            nextToken();
        } else { // 报错
            String error = " while循环缺少左括号\"(\"" + "\n";
            error(error);
            //whileNode.addChildren(null);
        }
        // condition
        //TreeNode condition = new TreeNode(currentToken);
        TreeNode conditionNode = new TreeNode(Tag.CONDITION);
        //conditionNode.addChildren(condition);
        
        conditionNode.addChildren(condition());
        whileNode.addChildren(conditionNode);
        // 匹配右括号)
        if (currentToken != null
                && currentToken.tag==Tag.RIGHTBRA) {
            nextToken();
        } else { // 报错
            String error = " while循环缺少右括号\")\"" + "\n";
            error(error);
            //whileNode.addChildren(null);
        }
        // 匹配左大括号{
        if (currentToken != null&& currentToken.tag==Tag.LEFTBRACE) {
            nextToken();
        } else {
            hasBrace = false;
        }
        // statement

        TreeNode statementNode = new TreeNode(Tag.BLOCKNODE);
        whileNode.addChildren(statementNode);
        if(hasBrace) {
            while (currentToken != null && !(currentToken.tag==Tag.RIGHTBRACE)) {
                if (!(currentToken.tag==Tag.RIGHTBRACE))
                    statementNode.addChildren(statement());
                else if (statementNode.getChildCount() == 0) {
                    whileNode.remove(whileNode.getChildCount() - 1);
                    statementNode =new TreeNode("EmptyStmt");
                    whileNode.addChildren(statementNode);
                    break;
                } else {
                    break;
                }
            }
            // 匹配右大括号}
            if (currentToken != null
                    && currentToken.tag==Tag.RIGHTBRACE) {
                nextToken();
            } else { // 报错
                String error = " while循环缺少右大括号\"}\"" + "\n";
                error(error);
                //whileNode.addChildren(null);
            }
        } else {
            if(currentToken != null)
                statementNode.addChildren(statement());
        }
        return whileNode;
    }


    private final TreeNode read_stm() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        nextToken();
        // 匹配左括号(
        if (currentToken != null && currentToken.tag==Tag.LEFTBRA) {
            nextToken();
        } else {
            String error = " read语句缺少左括号\"(\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // 匹配标识符
        if (currentToken != null && currentToken.tag==Tag.IDENTIFIER){
        	TreeNode idNode= new TreeNode(currentToken);

        	tempNode =new TreeNode(Tag.IDNODE);
            tempNode.addChildren(idNode);
            nextToken();
            // 判断是否是为数组赋值
            if (currentToken != null
                    && currentToken.tag==Tag.LEFTBRACKET) {
                tempNode.addChildren(array());
            }
        }else {
            String error = " read语句左括号后不是标识符" + "\n";
            error(error);
            nextToken();
            return new TreeNode(error);
        }
        // 匹配右括号)
        if (currentToken != null
                && currentToken.tag==Tag.RIGHTBRA) {
            nextToken();
        } else {
            String error = " read语句缺少右括号\")\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // 匹配分号;
        if (currentToken != null&& currentToken.tag==Tag.SEMI) {
            nextToken();
        } else {
            String error = " read语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        return tempNode;
    }


    private final TreeNode write_stm() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        nextToken();
        // 匹配左括号(
        if (currentToken != null
                && currentToken.tag==Tag.LEFTBRA) {
            nextToken();
        } else {
            String error = " write语句缺少左括号\"(\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // 调用expression函数匹配表达式
        tempNode = expression();
        // 匹配右括号)
        if (currentToken != null
                && currentToken.tag==Tag.RIGHTBRA) {
            nextToken();
        } else {
            String error = " write语句缺少右括号\")\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // 匹配分号;
        if (currentToken != null
                && currentToken.tag==Tag.SEMI) {
            nextToken();
        } else {
            String error = " write语句缺少分号\";\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        return tempNode;
    }


    private final TreeNode assign_stm(boolean isFor) {
        // assign函数返回结点的根结点
        TreeNode assignNode = new TreeNode(Tag.ASSIGNNODE);
        TreeNode idNode = new TreeNode(Tag.IDNODE);
        TreeNode id = new TreeNode(currentToken);
        idNode.addChildren(id);
        assignNode.addChildren(idNode);
        nextToken();
        // 判断是否是为数组赋值
        if (currentToken != null
                && currentToken.tag==Tag.LEFTBRACKET) {
        	idNode.addChildren(array());
        }
        // 匹配赋值符号=
        if (currentToken != null
                && currentToken.tag==Tag.EQ) {
            nextToken();
        } else { // 报错
            String error = " 赋值语句缺少\"=\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // expression
        assignNode.addChildren(condition());
        // 如果不是在for循环语句中调用声明语句,则匹配分号
        if (!isFor) {
            // 匹配分号;
            if (currentToken != null
                    && currentToken.tag==Tag.SEMI) {
                nextToken();
            } else { // 报错
                String error = " 赋值语句缺少分号\";\"" + "\n";
                error(error);
                //assignNode.addChildren(null);
            }
        }
        return assignNode;
    }


    private final TreeNode declare_stm() {
        TreeNode declareNode = new TreeNode(Tag.DECLARENODE);
        TreeNode typeNode = new TreeNode(Tag.TYPENODE);
        TreeNode type = new TreeNode(currentToken);
        typeNode.addChildren(type);
        declareNode.addChildren(typeNode);
        nextToken();
        // declare_aid
        declareNode = declare_aid(declareNode);
        // 处理同时声明多个变量的情况
        int next;
        while (currentToken != null) {
            next = currentToken.tag;
            if (next==Tag.COMM) {
                nextToken();
                declareNode = declare_aid(declareNode);
            } else {
                break;
            }
            if (currentToken != null)
                next = currentToken.tag;
        }
        // 匹配分号;
        if (currentToken != null
                && currentToken.tag==Tag.SEMI) {
            nextToken();
        } else { // 报错
            String error = " 声明语句缺少分号\";\"" + "\n";
            error(error);
            //declareNode.addChildren(null);
        }
        return declareNode;
    }


    private final TreeNode declare_aid(TreeNode root) {
        if (currentToken != null && currentToken.tag==Tag.IDENTIFIER) {
        	TreeNode idNode = new TreeNode(Tag.IDNODE);
        	TreeNode id =new TreeNode(currentToken);
        	idNode.addChildren(id);
            root.addChildren(idNode);
            nextToken();
            // 处理array的情况
            if (currentToken != null&& currentToken.tag==Tag.LEFTBRACKET) {
            	idNode.addChildren(array());
            } else if (currentToken != null
                    && !(currentToken.tag==Tag.EQ)
                    && !(currentToken.tag==Tag.SEMI)
                    && !(currentToken.tag==Tag.COMM)) {
                String error = " 声明语句出错,标识符后出现不正确的token" + "\n";
                error(error);
                //root.addChildren(null);
                nextToken();
            }
        } else { // 报错
            String error = " 声明语句中标识符出错" + "\n";
            error(error);
            //root.addChildren(null);
            nextToken();
        }
        // 匹配赋值符号=
        if (currentToken != null&& currentToken.tag==Tag.EQ) {
            //TreeNode assignNode = new TreeNode(currentToken,currentToken.tag);
            //TreeNode eq = new TreeNode(currentToken,currentToken.tag);
            //assignNode.addChildren(eq);
            //root.addChildren(assignNode);
        	TreeNode initNode=new TreeNode(Tag.INIT);
        	
            nextToken();
            initNode.addChildren(condition());
            root.addChildren(initNode);
        }
        return root;
    }


    private final TreeNode condition() {
        // 记录expression生成的结点
        TreeNode tempNode = expression();
        // 如果条件判断为比较表达式
        if (currentToken != null
                && (currentToken.tag==Tag.EQEQ
                || currentToken.tag==Tag.NE
                || currentToken.tag==Tag.LT 
                || currentToken.tag==Tag.GT
                || currentToken.tag==Tag.GE
                || currentToken.tag==Tag.LE)) {
        	TreeNode comparison = new TreeNode(currentToken);
            TreeNode comparisonNode =new TreeNode(Tag.OPTNODE);
            comparisonNode.addChildren(comparison);
            nextToken();
            comparisonNode.addChildren(tempNode);
            comparisonNode.addChildren(expression());
            return comparisonNode;
        }
        // 如果条件判断为bool变量
        return tempNode;
    }


    private final TreeNode expression() {
        // 记录term生成的结点
        TreeNode tempNode = term();

        // 如果下一个token为加号或减号
        while (currentToken != null
                && (currentToken.tag==Tag.PLUS || currentToken.tag==Tag.MINUS)) {
            // add_op
            TreeNode addNode = add_op();
            addNode.addChildren(tempNode);
            tempNode = addNode;
            tempNode.addChildren(term());
        }
        return tempNode;
    }


    private final TreeNode term() {
        // 记录factor生成的结点
        TreeNode tempNode = factor();

        // 如果下一个token为乘号或除号
        while (currentToken != null
                && (currentToken.tag==Tag.MULT || currentToken
                .tag==Tag.DIVIDE)) {
            // mul_op
            TreeNode mulNode = mul_op();
            mulNode.addChildren(tempNode);
            tempNode = mulNode;
            tempNode.addChildren(factor());
        }
        return tempNode;
    }


    private final TreeNode factor() {
        // 保存要返回的结点
    	TreeNode node=new TreeNode(Tag.CONSTNODE);
        TreeNode tempNode = null;
        if (currentToken != null && currentToken.tag==Tag.INT) {
            tempNode = new TreeNode(currentToken);
            nextToken();
        } else if (currentToken != null && currentToken.tag==Tag.REAL) {
            tempNode = new TreeNode(currentToken);
            nextToken();
        } else if (currentToken != null
                && currentToken.tag==Tag.BOOL) {
            tempNode = new TreeNode(currentToken);
            nextToken();
        } else if (currentToken != null
                && currentToken.tag==Tag.FALSE) {
            tempNode = new TreeNode(currentToken);
            nextToken();
        } else if (currentToken != null && currentToken.tag==Tag.IDENTIFIER) {
            tempNode = new TreeNode(Tag.IDNODE);
            TreeNode id=new TreeNode(currentToken);
            tempNode.addChildren(id);
            nextToken();
            // array
            if (currentToken != null
                    && currentToken.tag==Tag.LEFTBRACKET) {
                tempNode.addChildren(array());
            }
            return tempNode;
        } else if (currentToken != null
                && currentToken.tag==Tag.LEFTBRA) { // 匹配左括号(
            nextToken();
            tempNode = expression();
            // 匹配右括号)
            if (currentToken != null
                    && currentToken.tag==Tag.RIGHTBRA) {
                nextToken();
            } else { // 报错
                String error = " 算式因子缺少右括号\")\"" + "\n";
                error(error);
                return new TreeNode(error);
            }
        }  else { // 报错
            String error = " 算式因子存在错误" + "\n";
            error(error);
            if (currentToken != null
                    && !(currentToken.tag==Tag.SEMI)) {
                nextToken();
            }
            return new TreeNode(error);
        }
        node.addChildren(tempNode);
        return node;
    }


    private final TreeNode array() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.tag==Tag.LEFTBRACKET) {
            nextToken();
        } else {
            String error = " 缺少左中括号\"[\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        // 调用expression函数匹配表达式
        tempNode = expression();
        if (currentToken != null
                && currentToken.tag==Tag.RIGHTBRACKET) {
            nextToken();
        } else { // 报错
            String error = " 缺少右中括号\"]\"" + "\n";
            error(error);
            return new TreeNode(error);
        }
        return tempNode;
    }


    private final TreeNode add_op() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.tag==Tag.PLUS) {
        	TreeNode plus=new TreeNode(currentToken);
            tempNode = new TreeNode(Tag.OPTNODE);
            tempNode.addChildren(plus);
            nextToken();
        } else if (currentToken != null
                && currentToken.tag==Tag.MINUS) {
        	TreeNode minus=new TreeNode(currentToken);
            tempNode = new TreeNode(Tag.OPTNODE);
            tempNode.addChildren(minus);
            nextToken();
        } else { // 报错
            String error = " 加减符号出错" + "\n";
            error(error);
            return new TreeNode(error);
        }
        return tempNode;
    }


    private final TreeNode mul_op() {
        // 保存要返回的结点
        TreeNode tempNode = null;
        if (currentToken != null
                && currentToken.tag==Tag.MULT) {
        	TreeNode mult = new TreeNode(currentToken);
            tempNode = new TreeNode(Tag.MULT);
            tempNode.addChildren(mult);
            nextToken();
        } else if (currentToken != null
                && currentToken.tag==Tag.DIVIDE) {
        	TreeNode divi = new TreeNode(currentToken);
            tempNode = new TreeNode(Tag.OPTNODE);
            tempNode.addChildren(divi);
            nextToken();
        } else { // 报错
            String error = " 乘除符号出错" + "\n";
            error(error);
            return new TreeNode(error);
        }
        return tempNode;
    }
		
}
