package net.technearts.lang.fun;

import io.quarkus.logging.Log;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import static io.quarkus.logging.Log.info;
import static java.lang.System.out;

@Command(name = "fun-lang",
        description = "Interpreter for the Fun programming language",
        mixinStandardHelpOptions = true)
public class Main implements Runnable {

    @Parameters(paramLabel = "<script>", description = "Script file to execute")
    private String script;

    @Override
    public void run() {
        if (script == null || script.isBlank()) {
            startRepl();
        } else {
            executeScript(script);
        }
    }

    private void startRepl() {
        info("Welcome to the Fun REPL!");
        info("Type your expressions below. Type 'exit' to quit.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        ExecutionEnvironment env = new ExecutionEnvironment();
        FunVisitorImpl visitor = new FunVisitorImpl(env);

        while (true) {
            try {
                out.print("fun> ");
                String line = reader.readLine();
                if (line == null || line.equalsIgnoreCase("exit")) {
                    out.println("Goodbye!");
                    break;
                }

                Object result = evaluate(line, visitor);
                out.println(result);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void executeScript(String scriptPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptPath))) {
            StringBuilder code = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
            ExecutionEnvironment env = new ExecutionEnvironment();
            FunVisitorImpl visitor = new FunVisitorImpl(env);
            Object result = evaluate(code.toString(), visitor);
            info("Script executed successfully. Result:");
            out.println(result);
        } catch (Exception e) {
            info("Error reading or executing script: " + e.getMessage());
        }
    }

    private Object evaluate(String code, FunVisitorImpl visitor) {
        try {
            CharStream input = CharStreams.fromString(code);
            FunLexer lexer = new FunLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FunParser parser = new FunParser(tokens);
            return visitor.visit(parser.file());
        } catch (Exception e) {
            throw new RuntimeException("Failed to evaluate code: " + e.getMessage());
        }
    }

}
