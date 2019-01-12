package interpreter;

import java.math.BigDecimal;
import java.util.Scanner;


import lexer.token.Tag;
import interpreter.symbol.SymbolTable;
import interpreter.symbol.SymbolTableElement;
import parser.node.TreeNode;

/**
 * CMM语义分析
 * 
 * @author Leeham
 *
 */
public class Interpreter extends Thread {
	/* 语义分析时的符号表 */
	private SymbolTable table = new SymbolTable();
	/* 语法分析得到的抽象语法树 */
	private TreeNode root;
	/* 语义分析错误信息 */
	private String errorInfo = "";
	/* 语义分析错误个数 */
	private int errorNum = 0;
	/* 语义分析标识符作用域 */
	private int level = 0;

	public Interpreter(TreeNode root) {
		this.root = root;
	}

	public void error(String error, int line) {
		errorNum++;
		String s = "error in L" + line + " : " + error + "\n";
		errorInfo += s;
	}


	private static boolean matchInteger(String input) {
		if (input.matches("^-?\\d+$") && !input.matches("^-?0{1,}\\d+$"))
			return true;
		else
			return false;
	}


	private static boolean matchReal(String input) {
		if (input.matches("^(-?\\d+)(\\.\\d+)+$")
				&& !input.matches("^(-?0{2,}+)(\\.\\d+)+$"))
			return true;
		else
			return false;
	}


	public void program() {
		table.removeAll();
		statement(root);
		System.out.println("------------error list------------");
		if (errorNum != 0) {
			System.out.printf(errorInfo);
		} else {
			System.out.println("no error");
		}
		System.out.println("----------------end----------------");
	}


	private void statement(TreeNode root) {
		for (TreeNode currentNode:root.getChildren()) {
			switch(currentNode.type) {
			case Tag.DECLARENODE:		
				forDeclare(currentNode);
				break;
			case Tag.ASSIGNNODE:	
				forAssign(currentNode);
				break;
			case Tag.IFNODE:		
				level++;
				forIf(currentNode);
				level--;table.update(level);
				break;
			case Tag.WHILENODE:		
				level++;
				forWhile(currentNode);
				level--;table.update(level);
				break;
				/*
				 * 
				 * 
				 * read和write语句等待修复
				 * 
				 * 
				 * 
				 * 
				 */
			case Tag.READNODE:		
				forRead(currentNode);
				break;
			case Tag.WRITENODE:		
				forWrite(currentNode);
				break;
			default:
				break;
			}

		}
	}

	/*
	 * DeclStmt
	 *    ├─Type
	 *    │  └─int|real
	 *    ├─Identifier
	 *    │  └─id
	 *    ……
	 */
	private void forDeclare(TreeNode root) {

		// 结点显示的内容,即声明变量的类型int|real
		String Type = root.getChild(0).getChild(0).getValue();

		//变量名字
		String name;
		//获取多个变量
		for(int index = 1;index<root.getChildCount();index++) {
			TreeNode temp = root.getChild(index);
			name=temp.getChild(0).getValue();
			if(table.getCurrentLevel(name, level)==null) {//判断是否重定义
				if(temp.getChildCount()==1) {//普通变量
					SymbolTableElement element = new SymbolTableElement(name,Type,temp.getChild(0).tok.line,level);//参数依次为变量名字，类型，行所在行号，作用域
					if(index+1<root.getChildCount()&&root.getChildren().get(index+1).getType().equals("Init")) {//判断是否初始化
						TreeNode valueNode = root.getChild(index+1).getChild(0).getChild(0);
						String value=valueNode.getValue();
						if(Type.equals("int")) {//int类型初始化
							if (matchInteger(value)) {//检查常量类型
								element.setIntValue(value);
								element.setRealValue(String.valueOf(Double
										.parseDouble(value)));
							} else if (matchReal(value)) {
								String error = "can not init "+name+" with real number!";
								error(error, valueNode.tok.line);
							}else if (valueNode.getType().equals("identifier")) {//用其他变量来初始化
								if (checkID(root.getChild(index+1).getChild(0), level)) {
									if (table.getAllLevel(
											valueNode.getValue(), level)
											.getKind().equals("int")) {
										element.setIntValue(table.getAllLevel(
												valueNode.getValue(), level)
												.getIntValue());
										element.setRealValue(table.getAllLevel(
												valueNode.getValue(), level)
												.getRealValue());
									} else if (table.getAllLevel(
											valueNode.getValue(), level)
											.getKind().equals("real")) {
										String error = "can not init "+name+" with real number!";
										error(error, valueNode.tok.line);
									}
								} else {
									return;
								}
							}else if (value.equals("+")
									|| value.equals("-")
									|| value.equals("*")
									|| value.equals("/")) {
								String result = forExpression(valueNode);
								if (result != null) {
									if (matchInteger(result)) {
										element.setIntValue(result);
										element.setRealValue(String
												.valueOf(Double
														.parseDouble(result)));
									} else if (matchReal(result)) {
										String error = "can not init "+name+" with real number!";
										error(error, valueNode.tok.line);
										return;
									} else {
										return;
									}
								} else {
									return;
								}
							}
						}else if(Type.equals("real")) {//real类型初始化
							if (matchInteger(value)) {
								element.setRealValue(String.valueOf(Double.parseDouble(value)));
							}else if (matchReal(value)) {
								element.setRealValue(value);
							}else if (valueNode.tok.tag==Tag.IDENTIFIER) {
								if (checkID(root.getChild(index+1).getChild(0), level)) {
									if (table.getAllLevel(
											valueNode.getValue(), level)
											.getKind().equals("int")
											|| table.getAllLevel(
													valueNode.getValue(),
													level).getKind().equals(
															"real")) {
										element.setRealValue(table.getAllLevel(
												valueNode.getValue(), level)
												.getRealValue());
									}
								} else {
									return;
								}
							} else if (value.equals("+")
									|| value.equals("-")
									|| value.equals("*")
									|| value.equals("/")) {
								String result = forExpression(valueNode);
								if (result != null) {
									if (matchInteger(result)) {
										element.setRealValue(String
												.valueOf(Double
														.parseDouble(result)));
									} else if (matchReal(result)) {
										element.setRealValue(result);
									}
								} else {
									return;
								}
							}

						}
						index++;table.add(element);
					}else {
						table.add(element);
					}
				}else {//不为0就是数组了
					/*
					 * DeclStmt
					 *    ├─identifier
					 *    │  	├─array
					 *    │     └─identifier
					 *    │		     └─index
					 *    ……
					 */
					SymbolTableElement element = new SymbolTableElement(name, Type, temp.getChild(0).tok.line, level);
					String sizeValue = temp.getChild(1).getValue();//这里应该为一个const
					switch(sizeValue) {
					case "Constant":
						if(temp.getChild(1).getChild(0).tok.tag==Tag.INT) {
							sizeValue=temp.getChild(1).getChild(0).tok.value;
							int i = Integer.parseInt(sizeValue);
							if (i < 1) {
								String error = "index of array must greater than 0";
								error(error, temp.getChild(1).getChild(0).tok.line);
								return;
							}
						}
						break;
					case "Identifier":
						if(checkID(temp.getChild(1),level)) {
							SymbolTableElement tempElement = table.getAllLevel(temp.getChild(1).getChild(0).getValue(), level);
							if (tempElement.getKind().equals("int")) {
								int i = Integer.parseInt(tempElement.getIntValue());
								if (i < 1) {
									String error = "index of array must greater than 0";
									error(error, root.tok.line);
									return;
								} else {
									sizeValue = tempElement.getIntValue();
								}
							}else {
								String error = "type error,the size of array must be integer";
								error(error, root.tok.line);
								return;
							}
						}else {
							return;
						}
						break;
					case "Operator":
						sizeValue=forExpression(temp.getChild(1));
						if (sizeValue != null) {
							if (matchInteger(sizeValue)) {
								int i = Integer.parseInt(sizeValue);
								if (i < 1) {
									String error = "index of array must greater than 0";
									error(error, root.tok.line);
									return;
								}
							} else {
								String error = "type error,the size of array must be integer";
								error(error, root.tok.line);
								return;
							}
						} else {
							return;
						}
						break;
					}
					element.setArrayElementsNum(Integer.parseInt(sizeValue));
					table.add(element);
					index++;
					for (int j = 0; j < Integer.parseInt(sizeValue); j++) {
						String s = temp.getChild(0).getValue() + "@" + j;
						SymbolTableElement ste = new SymbolTableElement(s,Type, temp.getChild(0).tok.line, level);
						table.add(ste);
					}
				} 



			}else {
				String error = "variable" + name + "is already declared";
				error(error, temp.getChild(0).tok.line);
				return;
			}

		}



	}

	/*
	 * AssignStmt
	 *    ├─identifier
	 *    │  	└─id
	 *    ├─constant|Expr
	 */
	private void forAssign(TreeNode root) {
		// 赋值语句左半部分
		TreeNode id = root.getChild(0).getChild(0);
		// 赋值语句左半部分标识符
		String idName = id.getValue();
		if (table.getAllLevel(idName, level) != null) {//检查有没有声明
			if (root.getChild(0).getChildCount() != 1) {
				String s = forArray(root.getChild(0), table.getAllLevel(idName, level).getArrayElementsNum());
				if (s != null)
					idName += "@" + s;
				else
					return;
			}
		} else {
			String error = "variable" + idName + "is undefined brfore using";
			error(error, id.tok.line);
			return;
		}
		// 赋值语句左半部分标识符类型
		String idKind = table.getAllLevel(idName, level).getKind();
		// 赋值语句右半部分
		//右半部分的节点
		TreeNode Value;
		if(root.getChildCount() != 2) {
			Value = root.getChild(2);
		}else {
			Value = root.getChild(1);
		}

		String ValueKind = Value.getType();
		String value = Value.getValue();
		// 赋值语句右半部分的值
		String sValue = "";
		if (ValueKind.equals("Constant")) { //常量
			if(Value.getChild(0).tok.tag==Tag.INT) {
				sValue=Value.getChild(0).tok.value;
				ValueKind="int";
			}else if(Value.getChild(0).tok.tag==Tag.REAL) {
				sValue = Value.getChild(0).tok.value;
				ValueKind="real";
			}

		}  else if (ValueKind.equals("Identifier")) { // 标识符
			if (checkID(Value, level)) {
				if (Value.getChild(0).getChildCount() != 1) {
					String s = forArray(Value.getChild(0), table.getAllLevel(value, level).getArrayElementsNum());
					if (s != null)
						value += "@" + s;
					else
						return;
				}
				SymbolTableElement temp = table.getAllLevel(value, level);
				if (temp.getKind().equals("int")) {
					value = temp.getIntValue();
				} else if (temp.getKind().equals("real")) {
					value = temp.getRealValue();
				} 
				ValueKind = table.getAllLevel(value, level).getKind();
			} else {
				return;
			}
		} else if (ValueKind.equals("Operator")
				&&(Value.getChild(0).tok.value.equals("+")
				|| Value.getChild(0).tok.value.equals("-")
				|| Value.getChild(0).tok.value.equals("*")
				|| Value.getChild(0).tok.value.equals("/"))) { // 表达式
			String result = forExpression(Value);
			if (result != null) {
				if (matchInteger(result))
					ValueKind = "int";
				else if (matchReal(result))
					ValueKind = "real";
				sValue = result;
			} else {
				return;
			}
		} else if (ValueKind.equals("==")
				|| ValueKind.equals("<>")
				|| ValueKind.equals("<")
				|| ValueKind.equals(">")) { // 逻辑表达式
			boolean result = forCondition(Value);
			ValueKind = "bool";
			sValue = String.valueOf(result);
		}
		if (idKind.equals("int")) {
			if (ValueKind.equals("int")) {
				table.getAllLevel(idName, level).setIntValue(sValue);
				table.getAllLevel(idName, level).setRealValue(
						String.valueOf(Double.parseDouble(sValue)));
			} else if (ValueKind.equals("real")) {
				String error = "can not assign with float";
				error(error, id.tok.line);
				return;
			} 
		} else if(idKind.equals("real")) {
			table.getAllLevel(idName, level).setRealValue(
					String.valueOf(Double.parseDouble(sValue)));
		}
	}

	/*
	 * IfStmt
	 *   ├─if
	 *   ├─Expr
	 *   ├─BlockStmt
	 *   ├─else
	 *   ├─BlockStmt 
	 *  end
	 */
	private void forIf(TreeNode root) {
		int count = root.getChildCount();
		// 根结点Condition
		TreeNode conditionNode = root.getChild(1);
		// 根结点Statements
		TreeNode statementNode = root.getChild(2);
		// 条件为真
		if (forCondition(conditionNode)) {
			statement(statementNode);
		} else if (!forCondition(conditionNode)&&count == 5) { // 条件为假且有else语句
			TreeNode elseNode = root.getChild(4);
			level++;
			statement(elseNode);
			level--;
			table.update(level);
		} else { // 条件为假同时没有else语句
			return;
		}
	}

	/*
	 * WhileStmt
	 *   ├─while
	 *   ├─Expr
	 *   ├─BlockStmt
	 *  end
	 */
	private void forWhile(TreeNode root) {
		// 根结点Condition
		TreeNode conditionNode = root.getChild(1);
		// 根结点Statements
		TreeNode statementNode = root.getChild(2);
		while (forCondition(conditionNode)) {
			statement(statementNode);
			level--;
			table.update(level);
			level++;
		}
	}

	//	先忽略这些
	private void forRead(TreeNode root) {

		// 要读取的变量的名字
		String idName = root.getChild(1).getChild(0).getValue();
		// 查找变量
		SymbolTableElement element = table.getAllLevel(idName, level);
		// 判断变量是否已经声明
		if (element != null) {
			if (root.getChild(1).getChildCount() != 1) {
				String s = forArray(root.getChild(1), element.getArrayElementsNum());
				if (s != null) {
					idName += "@" + s;
				} else {
					return;
				}
			}
			String value = readInput();
			if (element.getKind().equals("int")) {
				if (matchInteger(value)) {
					table.getAllLevel(idName, level).setIntValue(value);
					table.getAllLevel(idName, level).setRealValue(
							String.valueOf(Double.parseDouble(value)));
				} else { // 报错
					String error = "can not assign \"" + value + "\" to " + idName;
					error(error,0);//这个错误不需要行号吧
				}
			} else if (element.getKind().equals("real")) {
				if (matchReal(value)) {
					table.getAllLevel(idName, level).setRealValue(value);
				} else if (matchInteger(value)) {
					table.getAllLevel(idName, level).setRealValue(
							String.valueOf(Double.parseDouble(value)));
				} else { // 报错
					String error = "can not assign \"" + value + "\" to " + idName;
					error(error,0);//这个错误不需要行号吧
				}
			}  
		} else { // 报错
			String error = "variable" + idName + "is undefined before use";
			error(error, root.tok.line);
		}
	}


	private String readInput() {
		// TODO 自动生成的方法存根
		Scanner sc = new Scanner(System.in);

		return sc.next();
	}

	private void forWrite(TreeNode root) {
		// 结点显示的内容
		TreeNode node = root.getChild(1);
		String content = node.getChild(0).getValue();
		// 结点的类型
		String kind = node.getType();
		if (kind.equals("Constant")) { // 常量
			System.out.println(node.getChild(0).getValue());
		}  else if (kind.equals("Identifier")) { // 标识符
			if (checkID(node, level)) {
				if (node.getChildCount() != 1) {//数组元素
					String s = forArray(node, table.getAllLevel(content, level).getArrayElementsNum());
					if (s != null)
						content += "@" + s;
					else
						return;
				}
				SymbolTableElement temp = table.getAllLevel(content, level);
				if (temp.getKind().equals("int")) {
					System.out.println(temp.getIntValue());
				} else if (temp.getKind().equals("real")) {
					System.out.println(temp.getRealValue());
				} else {
					return;
				}
			} else {
				return;
			}
		} else if (content.equals("+")
				|| content.equals("-")
				|| content.equals("*")
				|| content.equals("/")) { // 表达式
			String value = forExpression(root.getChild(1));
			if (value != null) {
				System.out.println(value);
			}
		}
	}


	private boolean forCondition(TreeNode root) {
		// > < <> == 
		if(root.getChild(0).getChildCount()!=3) {
			String error = "unexpected condition expression!";
			error(error,root.getChild(0).tok.line);
			//System.exit(0);
			return false;
		}
		TreeNode ExprNode = root.getChild(0);
		TreeNode opNode= ExprNode.getChild(0);
		TreeNode arg1=ExprNode.getChild(1);
		TreeNode arg2=ExprNode.getChild(2);
		//获取参数1、2的类型和数值
		String arg1Value=exprArg(arg1)[0];
		String arg1Type=exprArg(arg1)[1];
		String arg2Value=exprArg(arg2)[0];
		String arg2Type=exprArg(arg2)[1];
		//int类型直接比较
		if(arg1Type.equals("int")&&arg2Type.equals("int")) {
			int ia1 = Integer.parseInt(arg1Value.trim());
			int ia2 = Integer.parseInt(arg2Value.trim());
			switch(opNode.tok.value) {
			case ">":return ia1>ia2;
			case "<":return ia1<ia2;
			case "==":return ia1==ia2;
			case "<>":return ia1!=ia2;
			case ">=":return ia1>=ia2;
			case "<=":return ia1<=ia2;
			}
		}else {
			Double da1=Double.parseDouble(arg1Value);
			Double da2=Double.parseDouble(arg2Value);
			switch(opNode.tok.value) {
			case ">":return da1>da2;
			case "<":return da1<da2;
			case "==":return da1==da2;
			case "<>":return da1!=da2;
			case ">=":return da1>=da2;
			case "<=":return da1<=da2;
			}
		}
		// 语义分析出错或者分析条件结果为假返回false
		return false;
	}

	//表达式的参数
	public String[] exprArg(TreeNode node) {
		String[] res=new String[2];//第一项存值，第二项存类型
		res[0] = node.getChild(0).getValue();
		String kind = node.getType();
		if (kind.equals("Constant")) { // 常量
			if(node.getChild(0).tok.tag==Tag.INT) {//整数
				res[0] = node.getChild(0).tok.value;
				res[1]="int";
				return res;
			}else if(node.getChild(0).tok.tag==Tag.REAL) {//浮点数
				res[0] = node.getChild(0).tok.value;
				res[1] ="real";
				return res;
			}

		}  else if (kind.equals("Identifier")) { // 标识符
			if (checkID(node, level)) {
				if (node.getChildCount() != 1) {
					String s = forArray(node, table.getAllLevel(res[0], level).getArrayElementsNum());
					if (s != null) {
						res[1] = table.getAllLevel(res[0], level).getKind();
						res[0]=table.getAllLevel(res[0] += "@" + s, level).getIntValue();						
						return res;
					}else
						return null;
				}
				SymbolTableElement temp = table.getAllLevel(res[0], level);
				if (temp.getKind().equals("int")) {
					res[0] = temp.getIntValue();
					res[1]="int";
					return res;
				} else if (temp.getKind().equals("real")) {
					res[0] = temp.getRealValue();
					res[1]="real";
					return res;
				}
			} else {
				return null;
			}
		} else if (res[0].equals("Operator")
				&&(node.getChild(0).equals("+")
						|| node.getChild(0).equals("-")
						|| node.getChild(0).equals("*")
						|| node.getChild(0).equals("/"))) { // 表达式
			String result = forExpression(node);
			if (result != null) {
				res[0] = result;
				res[1]="int";			
				if (matchReal(result))
					res[1]="real";
				return res;
			} else
				return null;
		}
		return null;
	}

	/*
	 * Expr
	 *   ├─Op
	 *   ├─Expr|Constant
	 *   ├─Expr|Constant
	 *  end
	 */
	private String forExpression(TreeNode root) {

		TreeNode opNode = root.getChild(0);//操作符节点
		TreeNode arg1=root.getChild(1);//参数1节点
		TreeNode arg2=root.getChild(2);//参数2节点

		//获取两个参数的值和类型
		String content = root.getValue();
		String arg1Value=exprArg(arg1)[0];
		String arg1Type=exprArg(arg1)[1];
		String arg2Value=exprArg(arg2)[0];
		String arg2Type=exprArg(arg2)[1];

		//计算
		if(arg1Type.equals("int")&&arg2Type.equals("int")) {
			int arg_1 = Integer.parseInt(arg1Value.trim());
			int arg_2 = Integer.parseInt(arg2Value.trim());
			switch(opNode.tok.value) {
			case "+":return String.valueOf(arg_1 + arg_2);
			case "-":return String.valueOf(arg_1 - arg_2);
			case "*":return String.valueOf(arg_1 * arg_2);
			case "/":
				if(arg_2!=0)
					return String.valueOf(arg_1 / arg_2);
				else {
					String error = "arg2 con not be '0' when dividing";
					error(error,arg2.tok.line);
				}
			default:
				return null;
			}
		}else {
			double arg_1 = Double.parseDouble(arg1Value);
			double arg_2 = Double.parseDouble(arg2Value);
			BigDecimal bd1 = new BigDecimal(arg_1);
			BigDecimal bd2 = new BigDecimal(arg_2);
			switch(opNode.getChild(0).getValue()) {
			case "+":return String.valueOf(bd1.add(bd2).floatValue());
			case "-":return String.valueOf(bd1.subtract(bd2).floatValue());
			case "*":return String.valueOf(bd1.multiply(bd2).floatValue());
			case "/":
				if(!bd2.equals(0.0))
					return String.valueOf(bd1.divide(bd2, 3,BigDecimal.ROUND_HALF_UP).floatValue());
				else {
					String error = "arg2 con not be '0.0' when dividing";
					error(error,arg2.tok.line);
				}
			default:
				return null;
			}
		}
	}

	//数组，参数1为数组根结点Identifier，参数2为数组大小
	private String forArray(TreeNode root, int arraySize) {
		TreeNode idNode=root.getChild(0);
		TreeNode indexNode=root.getChild(1);
		if (indexNode.getType().equals("Constant")) {
			if(indexNode.getChild(0).tok.tag==Tag.INT) {
				//数组下标索引
				int index = Integer.parseInt(indexNode.getChild(0).tok.value);
				if (index > -1 && index < arraySize) {
					return indexNode.getChild(0).tok.value;//返回数组下标
				} else if (index < 0) {
					String error = "index of array can not be negative";
					error(error, indexNode.getChild(0).tok.line);
					return null;
				} else {
					String error = "index out of range";
					error(error, indexNode.getChild(0).tok.line);
					return null;
				}
			}else {
				String error = "type error:the index of array must be integer!";
				error(error, indexNode.getChild(0).tok.line);
				return null;
			}			
		} else if (indexNode.getType().equals("Identifier")) {
			// 检查标识符
			if (checkID(indexNode, level)) {
				String idName=indexNode.getChild(0).getValue();
				if(indexNode.getChildCount()>1) {
					String s= forArray(indexNode,table.getAllLevel(idName, level).getArrayElementsNum());
					idName+="@"+s;
				}
				SymbolTableElement temp = table.getAllLevel(idName,level);
				if (temp.getKind().equals("int")) {
					int i = Integer.parseInt(temp.getIntValue());
					if (i > -1 && i < arraySize) {
						return temp.getIntValue();
					} else if (i < 0) {
						String error = "index of array can not be negative!";
						error(error, indexNode.getChild(0).tok.line);
						return null;
					} else {
						String error = "index out of range!";
						error(error, indexNode.getChild(0).tok.line);
						return null;
					}
				} else {
					String error = "type error:the index of array must be integer!";
					error(error, indexNode.getChild(0).tok.line);
					return null;
				}
			} else {
				return null;
			}
		} else if (indexNode.getType().equals("Operator")
				&&(indexNode.getChild(0).getValue().equals("+")
						|| indexNode.getChild(0).getValue().equals("-")
						|| indexNode.getChild(0).getValue().equals("*")
						|| indexNode.getChild(0).getValue().equals("/"))) { // 表达式
			String result = forExpression(indexNode);
			if (result != null) {
				if (matchInteger(result)) {
					int i = Integer.parseInt(result);
					if (i > -1 && i < arraySize) {
						return result;
					} else if (i < 0) {
						String error = "index of array can not be negative!";
						error(error, indexNode.getChild(0).tok.line);
						return null;
					} else {
						String error = "index out of range!";
						error(error, indexNode.getChild(0).tok.line);
						return null;
					}
				} else {
					String error = "type error:the index of array must be integer!";
					error(error, indexNode.getChild(0).tok.line);
					return null;
				}
			} else
				return null;
		}
		return null;
	}

	//参数为identifier子节点
	private boolean checkID(TreeNode root, int level) {
		// 标识符名字
		TreeNode idNode = root.getChild(0);
		String idName = idNode.getValue();
		// 标识符未声明
		if (table.getAllLevel(idName, level) == null) {
			String error = "undefined variable: " + idName;
			error(error, root.tok.line);
			return false;
		} else {
			if (root.getChildCount() > 1) {
				String tempString = forArray(root, table.getAllLevel(idName, level).getArrayElementsNum());
				if (tempString != null)
					idName += "@" + tempString;
				else
					return false;
			}
			SymbolTableElement temp = table.getAllLevel(idName, level);
			// 变量未初始化
			if (temp.getIntValue().equals("") && temp.getRealValue().equals("")) {
				String error = "variable" + idName + "is undefined before use";
				error(error, root.tok.line);
				return false;
			} else {
				return true;
			}
		}
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public int getErrorNum() {
		return errorNum;
	}

	public void setErrorNum(int errorNum) {
		this.errorNum = errorNum;
	}

}