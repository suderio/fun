package net.technearts.lang.fun;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static net.technearts.lang.fun.Nil.NULL;
import static net.technearts.lang.fun.TestUtils.assertNumbersEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FunListenerImplTest {

    private ExecutionEnvironment env;

    private Object evaluate(String code) {
        CharStream input = CharStreams.fromString(code);
        FunLexer lexer = new FunLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunParser parser = new FunParser(tokens);
        ParseTree tree = parser.file();
        env = new ExecutionEnvironment();
        FunListenerImpl listener = new FunListenerImpl(env);
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);
        return env.peek();
    }

    @Test
    void testAssignments() {
        String code = """
                    x : 42;
                    y : x + 8;
                """;
        evaluate(code);
        assertNumbersEqual(42, env.get("x"));
        assertNumbersEqual(50, env.get("y"));
    }

    @Test
    void testUnaryOperators() {
        String code = """
                    x : -10;
                    y : +20;
                    z : ~true;
                """;
        evaluate(code);
        assertNumbersEqual(-10, env.get("x"));
        assertNumbersEqual(20, env.get("y"));
        assertEquals(false, env.get("z"));
    }

    @Test
    void testArithmeticExpressions() {
        String code = """
                    x : 2 + 3 * 4;
                    y : (2 + 3) * 4;
                    z : 10 ** 2;
                """;
        evaluate(code);
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
        evaluate(code);
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
        evaluate(code);
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
        evaluate(code);
        assertNumbersEqual(42, env.get("x"));
        assertNumbersEqual(10, env.get("y"));
    }

    @Test
    void testNullFallbackExpressions() {
        String code = """
                    x : null ? 42;
                    y : 10 ? 42;
                """;
        evaluate(code);
        assertEquals(NULL, env.get("x")); // null retorna o valor da direita
        assertNumbersEqual(10, env.get("y")); // valor não nulo retorna ele mesmo
    }

    @Test
    void testTableConstruction() {
        String code = """
                    t : [1 2 3 "hello"];
                """;
        evaluate(code);
        Table table = (Table) env.get("t");
        assertNumbersEqual(1, table.get(BigInteger.ZERO));
        assertNumbersEqual(2, table.get(BigInteger.ONE));
        assertNumbersEqual(3, table.get(BigInteger.TWO));
        assertEquals("hello", table.get(BigInteger.valueOf(3)));
    }

    @Test
    void testTableConcatenation() {
        String code = """
                    t : 1, 2, 3, 4;
                """;
        evaluate(code);
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
        evaluate(code);
        assertNumbersEqual(2, env.get("x"));
    }

    @Test
    void testNestedExpressions() {
        String code = """
                    x : 10 + (20 * (5 - 2));
                """;
        evaluate(code);
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
        evaluate(code);
        assertNumbersEqual(-10, env.get("x"));
        assertNumbersEqual(20, env.get("y"));
        assertEquals(false, env.get("z"));
    }
}
