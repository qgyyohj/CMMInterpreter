package lexer;

import Entry.*;
/**
 * 异常处理机制
 */
public class LexerException {

    /**
     * 标识符异常
     */
    public static void identifierException() {
        System.out.println();
        System.out.println("[L" + LexerEntry.lexer.line + ": unknown identifier.]");
        System.out.println("[tips: identifier begins with letter,and can not end with '.','_']");
        System.out.println();
    }

    /**
     * 未定义的符号
     *
     * @param s
     */
    public static void unknownSymbolException(String s) {
        System.out.println();
        System.out.println("[L" + LexerEntry.lexer.line + ": \"" + s + "\" can not be recognized.]");
        System.out.println();
    }

    /**
     * 未闭合的多行注释
     */
    public static void unclosedMultiAnnotation() {
        System.out.println();
        System.out.println("[L" + LexerEntry.lexer.line + ": unclosed annotation.]");
        System.out.println();
    }
}