import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FunVisitorTest {

    private Object evaluate(String code) {
        CharStream charStream = CharStreams.fromString(code);
        FunLexer lexer = new FunLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FunParser parser = new FunParser(tokens);
        ParseTree tree = parser.prog();
        FunVisitorImpl visitor = new FunVisitorImpl();
        return visitor.visit(tree);
    }

    @Test
    void testAssignment() {
        String code = """
            x: 10;
            y: x + 5;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(10, visitor.variables.get("x"));
        assertEquals(15, visitor.variables.get("y"));
    }

    @Test
    void testUnaryOperator() {
        String code = """
            succ : { it + 1 };
            pred : { it - 1 };
            x : succ 10;
            y : pred 5;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(11, visitor.variables.get("x"));
        assertEquals(4, visitor.variables.get("y"));
    }

    @Test
    void testTableConstructor() {
        String code = """
            t : ["A" test: 42, 3.14];
        """;
        Object result = evaluate(code);
        Map<Object, Object> table = (Map<Object, Object>) result;
        assertEquals("A", table.get(0));
        assertEquals(42, table.get("test"));
        assertEquals(3.14, table.get(1));
    }

    @Test
    void testTableConcatenation() {
        String code = """
            t : "A", 42, "B";
        """;
        Object result = evaluate(code);
        Map<Object, Object> table = (Map<Object, Object>) result;
        assertEquals("A", table.get(0));
        assertEquals(42, table.get(1));
        assertEquals("B", table.get(2));
    }

    @Test
    void testArithmeticOperations() {
        String code = """
            x : 2 + 3 * 4;
            y : (2 + 3) * 4;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(14, visitor.variables.get("x"));
        assertEquals(20, visitor.variables.get("y"));
    }

    @Test
    void testLogicalOperations() {
        String code = """
            x : true && false;
            y : true || false;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(false, visitor.variables.get("x"));
        assertEquals(true, visitor.variables.get("y"));
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
    void testLiteralsAndVariables() {
        String code = """
            x : 42;
            y : "Hello, World!";
            z : null;
        """;
        evaluate(code);
        FunVisitorImpl visitor = new FunVisitorImpl();
        assertEquals(42, visitor.variables.get("x"));
        assertEquals("Hello, World!", visitor.variables.get("y"));
        assertNull(visitor.variables.get("z"));
    }
}
