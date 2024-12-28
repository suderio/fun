package net.technearts.lang.fun;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FunVisitorImplTest {

    private Object evaluate(String code) {
        CharStream input = CharStreams.fromString(code);
        FunLexer lexer = new FunLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunParser parser = new FunParser(tokens);
        ParseTree tree = parser.file();
        FunVisitorImpl visitor = new FunVisitorImpl();
        return visitor.visit(tree);
    }

    @Test
    void testAssignments() {
        String code = """
            x : 42;
            y : x + 8;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(42, visitor.variables.get("x"));
        assertEquals(50, visitor.variables.get("y"));
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
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(-10, visitor.variables.get("x"));
        assertEquals(20, visitor.variables.get("y"));
        assertEquals(false, visitor.variables.get("z"));
    }

    @Test
    void testArithmeticExpressions() {
        String code = """
            x : 2 + 3 * 4;
            y : (2 + 3) * 4;
            z : 10 ** 2;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(14, visitor.variables.get("x"));
        assertEquals(20, visitor.variables.get("y"));
        assertEquals(100.0, visitor.variables.get("z"));
    }

    @Test
    void testLogicalExpressions() {
        String code = """
            x : true && false;
            y : true || false;
            z : true ^ false;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(false, visitor.variables.get("x"));
        assertEquals(true, visitor.variables.get("y"));
        assertEquals(true, visitor.variables.get("z"));
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
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(true, visitor.variables.get("a"));
        assertEquals(true, visitor.variables.get("b"));
        assertEquals(true, visitor.variables.get("c"));
        assertEquals(true, visitor.variables.get("d"));
        assertEquals(true, visitor.variables.get("e"));
        assertEquals(true, visitor.variables.get("f"));
    }

    @Test
    void testNullTestExpressions() {
        String code = """
            x : null ?? 42;
            y : 10 ?? 42;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(42, visitor.variables.get("x"));
        assertEquals(10, visitor.variables.get("y"));
    }

    @Test
    void testNullFallbackExpressions() {
        String code = """
            x : null ? 42;
            y : 10 ? 42;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(42, visitor.variables.get("x")); // null retorna o valor da direita
        assertEquals(10, visitor.variables.get("y")); // valor não nulo retorna ele mesmo
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
        assertEquals(1, table.get(0));
        assertEquals(2, table.get(1));
        assertEquals(3, table.get(2));
        assertEquals(4, table.get(3));
    }

    @Test
    void testDereferencing() {
        String code = """
            t : [1, 2, 3];
            x : t.1;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(2, visitor.variables.get("x"));
    }

    @Test
    void testNestedExpressions() {
        String code = """
            x : 10 + (20 * (5 - 2));
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(70, visitor.variables.get("x"));
    }
}
