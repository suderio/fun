package net.technearts.lang.fun;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static net.technearts.lang.fun.TestUtils.assertNumbersEqual;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunVisitorImplTest {

    private ExecutionEnvironment env;

    private Object evaluate(String code) {
        CharStream input = CharStreams.fromString(code);
        FunLexer lexer = new FunLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunParser parser = new FunParser(tokens);
        ParseTree tree = parser.file();
        env = new ExecutionEnvironment();
        FunVisitorImpl visitor = new FunVisitorImpl(env);
        return visitor.visit(tree);
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
            negate : { -it };
            pos : { +it };
            inv : { ~it };
            x : negate 10;
            y : pos 20;
            z : inv true;
        """;
        evaluate(code);
        assertEquals(-10, env.get("x"));
        assertEquals(20, env.get("y"));
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
        assertNumbersEqual(100.0, env.get("z"));
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
        assertEquals(42, env.get("x"));
        assertEquals(10, env.get("y"));
    }

    @Test
    void testNullFallbackExpressions() {
        String code = """
            x : null ? 42;
            y : 10 ? 42;
        """;
        evaluate(code);
        assertNumbersEqual(42, (BigDecimal)env.get("x")); // null retorna o valor da direita
        assertNumbersEqual(10, (BigDecimal)env.get("y")); // valor não nulo retorna ele mesmo
    }

    @Test
    void testTableConstruction() {
        String code = """
            t : [1, 2, 3, "hello"];
        """;
        Object result = evaluate(code);
        Map<Object, Object> table = (Map<Object, Object>) result;
        assertEquals(1, table.get(0));
        assertEquals(2, table.get(1));
        assertEquals(3, table.get(2));
        assertEquals("hello", table.get(3));
    }

    @Test
    void testTableConcatenation() {
        String code = """
            t : 1, 2, 3, 4;
        """;
        Object result = evaluate(code);
        Map<Object, Object> table = (Map<Object, Object>) result;
        assertNumbersEqual(1, table.get(0));
        assertNumbersEqual(2, table.get(1));
        assertNumbersEqual(3, table.get(2));
        assertNumbersEqual(4, table.get(3));
    }

    @Test
    void testDereferencing() {
        String code = """
            t : [1, 2, 3];
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
}
