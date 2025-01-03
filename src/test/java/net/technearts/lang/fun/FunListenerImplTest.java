package net.technearts.lang.fun;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static net.technearts.lang.fun.ElementWrapper.Nil.NULL;
import static net.technearts.lang.fun.TestUtils.assertNumbersEqual;
import static net.technearts.lang.fun.TestUtils.assertSizeEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunListenerImplTest {

    private ExecutionEnvironment env;

    private Object evaluate(String code) {
        CharStream input = CharStreams.fromString(code);
        FunLexer lexer = new FunLexer(input);
        lexer.addErrorListener(ConsoleErrorListener.INSTANCE);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunParser parser = new FunParser(tokens);
        parser.addErrorListener(ConsoleErrorListener.INSTANCE);
        parser.getInterpreter().setPredictionMode(PredictionMode.LL_EXACT_AMBIG_DETECTION);
        //parser.addParseListener(new FunListenerImpl());
        ParseTree tree = parser.file();
        env = new ExecutionEnvironment();
        FunVisitorImpl visitor = new FunVisitorImpl(env);
        System.out.println(tree.toStringTree(parser));
        return visitor.visit(tree);
    }

    @Test
    void testAssignments() {
        String code = """
                    x : 42;
                    y : x + 8;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(42, env.get("x"));
        assertNumbersEqual(50, env.get("y"));
    }

    @Test
    void testUnaryOperators() {
        String code = """
                    x : -10;
                    y : +20;
                    z : ~true;
                    a : --x;
                    b : ++ [1 2 y];
                    c : ~8;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(-10, env.get("x"));
        assertNumbersEqual(20, env.get("y"));
        assertEquals(false, env.get("z"));
        assertNumbersEqual(-11, env.get("a"));
        assertNumbersEqual(4, env.get("b"));
        assertEquals(false, env.get("c"));
    }

    @Test
    void testArithmeticExpressions() {
        String code = """
                    x : 2 + 3 * 4;
                    y : (2 + 3) * 4;
                    z : 10 ** 2;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(14, env.get("x"));
        assertNumbersEqual(20, env.get("y"));
        assertTrue(Double.MIN_VALUE < BigDecimal.valueOf(100.0).subtract((BigDecimal) env.get("z"), env.getMathContext()).abs().doubleValue());
    }

    @Test
    void testLogicalExpressions() {
        String code = """
                    x : true && false;
                    y : true || false;
                    z : true ^ false;
                """;
        System.out.println(evaluate(code));
        assertEquals(false, env.get("x"));
        assertEquals(true, env.get("y"));
        assertEquals(true, env.get("z"));
    }

    @Test
    void testComparisons() {
        String code = """
                    a : 10 = 10;
                    b : 10 <> 5;
                    c : 10 > 5;
                    d : 10 >= 10;
                    e : 10 < 15;
                    f : 10 <= 10;
                """;
        System.out.println(evaluate(code));
        assertEquals(true, env.get("a"));
        assertEquals(true, env.get("b"));
        assertEquals(true, env.get("c"));
        assertEquals(true, env.get("d"));
        assertEquals(true, env.get("e"));
        assertEquals(true, env.get("f"));
    }

    @Test
    void testNullTestExpressions() {
        String code = """
                    x : null ?? 42;
                    y : 10 ?? 42;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(42, env.get("x"));
        assertNumbersEqual(10, env.get("y"));
    }

    @Test
    void testTestExpressions() {
        String code = """
                    x : null ? 42;
                    y : 10 ? 42;
                    z: (1 = 0) ? 2;
                """;
        System.out.println(evaluate(code));
        assertEquals(NULL, env.get("x"));
        assertEquals(true, env.get("y"));
        assertEquals(true, env.get("z"));
    }

    @Test
    void testTableConstruction() {
        String code = """
                    t : [1 2 "hello" 3];
                """;
        System.out.println(evaluate(code));
        Table table = (Table) env.get("t");
        assertNumbersEqual(1, table.get(BigInteger.ZERO));
        assertNumbersEqual(2, table.get(BigInteger.ONE));
        assertEquals("hello", table.get(BigInteger.TWO));
    }

    @Test
    void testTableConcatenation() {
        String code = """
                    t : 1, 2, 3, 4;
                """;
        System.out.println(evaluate(code));
        Table table = (Table) env.get("t");
        assertNumbersEqual(1, table.get(BigInteger.ZERO));
        assertNumbersEqual(2, table.get(BigInteger.ONE));
        assertNumbersEqual(3, table.get(BigInteger.TWO));
        assertNumbersEqual(4, table.get(BigInteger.valueOf(3)));
    }

    @Test
    void testDereferencing() {
        String code = """
                    t : [1 2 3];
                    x : t.1;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(2, env.get("x"));
    }

    @Test
    void testNestedExpressions() {
        String code = """
                    x : 10 + (20 * (5 - 2));
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(70, env.get("x"));
    }

    @Test
    void testCallOperators() {
        String code = """
                    negate : { -it };
                    pos : { +it };
                    inv : { ~it };
                    x : negate 10;
                    y : pos 20;
                    z : inv true;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(-10, env.get("x"));
        assertNumbersEqual(20, env.get("y"));
        assertEquals(false, env.get("z"));
    }

    @Test
    void testShift() {
        String code = """
                    x : 16 << 2;
                    y : 128 >> 3;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(64, env.get("x"));
        assertNumbersEqual(16, env.get("y"));
    }

    @Test
    void testOperators() {
        String code = """
                    sq: { it * it };
                    x : sq 4;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(16, env.get("x"));
    }

    @Test
    void testRange() {
        String code = """
                    x : 1..5;
                    y : 8..5;
                    z : 3..3;
                """;
        System.out.println(evaluate(code));
        assertSizeEquals(5, env.get("x"));
        assertSizeEquals(4, env.get("y"));
        assertSizeEquals(1, env.get("z"));
    }

    @Test
    void testAssignOpExpression() {
        String code = """
                    x : 10;
                    y : 20;
                    x += 5;
                    y *= 2;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(15, env.get("x"));
        assertNumbersEqual(40, env.get("y"));
    }

    @Test
    void testSubstExpression() {
        String code = """
                    x : 10;
                    y : "x: ${x}";
                    z : "0: $0, 1: $1" $ [3 6];
                """;
        System.out.println(evaluate(code));
        var y = (String) env.get("y");
        assertEquals("x: 10", y);
        var z = (String) env.get("z");
        assertTrue(z.startsWith("0: 3"));
        assertTrue(z.endsWith("1: 6"));
    }

    @Test
    void testFibonacci() {
        String code = """
                    fib : { [1 1].it ?? (this(it - 1)) + (this(it - 2)) };
                    x : fib 0;
                    y : fib 5;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(1, env.get("x"));
        assertNumbersEqual(8, env.get("y"));
    }

    @Test
    void testFatorial() {
        String code = """
                    fat : { [1 1].it ?? (this(it - 1)) * it };
                    x : fat 1;
                    y : fat 3;
                    z : fat 5;
                """;
        System.out.println(evaluate(code));
        assertNumbersEqual(1, env.get("x"));
        assertNumbersEqual(6, env.get("y"));
        assertNumbersEqual(120, env.get("z"));
    }
}
