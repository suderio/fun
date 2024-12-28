package net.technearts.lang.fun;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

@Command(name = "fun-lang",
        description = "Interpreter for the Fun programming language",
        mixinStandardHelpOptions = true)
public class GreetingCommand implements Runnable {

    @CommandLine.Option(names = {"-s", "--script"}, description = "Script file to execute")
    private String script;

    @Override
    public void run() {
        if (script == null) {
            startRepl();
        } else {
            executeScript(script);
        }
    }

    private void startRepl() {
        System.out.println("Welcome to the Fun REPL!");
        System.out.println("Type your expressions below. Type 'exit' to quit.");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        FunVisitorImpl visitor = new FunVisitorImpl();

        while (true) {
            try {
                System.out.print("fun> ");
                String line = reader.readLine();
                if (line == null || line.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }

                Object result = evaluate(line, visitor);
                System.out.println(result);
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

            FunVisitorImpl visitor = new FunVisitorImpl();
            Object result = evaluate(code.toString(), visitor);
            System.out.println("Script executed successfully. Result:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error reading or executing script: " + e.getMessage());
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
