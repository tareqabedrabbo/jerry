package jerry;

import jerry.command.Interpreter;
import jerry.config.ApplicationConfig;
import jerry.config.CommandsConfig;
import jerry.format.Formatter;
import jerry.parse.Parser;
import jerry.parse.ParsingException;
import jerry.parse.Token;
import jline.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static org.fusesource.jansi.Ansi.Color.DEFAULT;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.MAGENTA;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;


/**
 * @author Tareq Abedrabbo
 */
public class Application {

    private ExpressionParser expressionParser;

    private ConsoleReader consoleReader;

    private Interpreter<ResponseEntity<Map<String, Object>>> httpCommandInterpreter;

    private Interpreter<String> generalCommandInterpreter;

    private Formatter formatter;

    private Settings settings;

    private String prompt = "jerry> ";

    private Parser parser;

    public void setExpressionParser(ExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    public void setFormatter(Formatter formatter) {
        this.formatter = formatter;
    }

    public void setConsoleReader(ConsoleReader consoleReader) {
        this.consoleReader = consoleReader;
    }

    public void setHttpCommandInterpreter(Interpreter<ResponseEntity<Map<String, Object>>> httpCommandInterpreter) {
        this.httpCommandInterpreter = httpCommandInterpreter;
    }

    public void setGeneralCommandInterpreter(Interpreter<String> generalCommandInterpreter) {
        this.generalCommandInterpreter = generalCommandInterpreter;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public void run() throws IOException {
        String line;
        PrintWriter out = new PrintWriter(System.out);

        while ((line = consoleReader.readLine(prompt)) != null) {
            if (StringUtils.hasText(line)) {
                try {
                    List<Token> tokens = parser.parse(line);
                    Token commandToken = tokens.get(0);
                    if (generalCommandInterpreter.supports(commandToken)) {
                        runGeneralCommand(tokens, out);
                    } else if (httpCommandInterpreter.supports(commandToken)) {
                        runHttpCommand(tokens, out);
                    } else {
                        formatError("Unknown command: " + commandToken.value);
                    }
                } catch (IllegalArgumentException e) {
                    out.println(formatError(e.getMessage()));
                } catch (ParsingException e) {
                    out.println(formatError(e.getMessage()));
                } catch (RestClientException e) {
                    Throwable cause = e.getRootCause() != null ? e.getRootCause() : e;
                    out.println(formatError(cause.getMessage()));
                } finally {
                    out.flush();
                }
            }
        }

    }

    private void runGeneralCommand(List<Token> tokens, PrintWriter out) {
        String result = generalCommandInterpreter.interpret(tokens);
        Ansi ansi = ansi();
        ansi.fg(GREEN).a(result).reset();
        out.println(ansi.toString());
    }

    private void runHttpCommand(List<Token> tokens, PrintWriter out) {
        ResponseEntity<Map<String, Object>> response = httpCommandInterpreter.interpret(tokens);
        if (response != null) {
            printResponse(out, response);
        } else {
            Ansi ansi = ansi();
            ansi.fg(GREEN).a("OK").reset();
            out.println(ansi.toString());
        }
    }

    private void printResponse(PrintWriter out, ResponseEntity<Map<String, Object>> response) {
        if (settings.printResponseDetails) {
            Ansi ansi = ansi();
            ansi.fg(YELLOW).a("http status: ").fg(GREEN).a(response.getStatusCode()).reset().newline();
            ansi.fg(MAGENTA).a("==== headers ====").reset().newline();
            HttpHeaders headers = response.getHeaders();
            for (String key : headers.keySet()) {
                String value = StringUtils.collectionToCommaDelimitedString(headers.get(key));
                ansi.fg(YELLOW).a(key).a(": ").fg(GREEN).a(value).newline().reset();
            }
            ansi.fg(MAGENTA).a("=================").reset();
            out.println(ansi);
        }
        out.println(formatter.format(response.getBody()));
    }

    private String formatError(String message) {
        Ansi ansi = ansi();
        return ansi.fg(RED).a("=== ").a(message).a(" ===").fg(DEFAULT).toString();
    }

    private static void printLogo() {
        Ansi ansi = ansi();

        ansi.newline().fg(GREEN).a("    __                     \n" +
                " __ / /___ __________ __    \n" +
                "/ // // -_) __/ __/ // /    \n" +
                "\\___/ \\__/_/ /_/  \\_, /     \n" +
                "                 /___/      ").newline().reset();
        System.out.println(ansi);
    }

    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ApplicationConfig.class, CommandsConfig.class);
        context.registerShutdownHook();
        Application application = context.getBean(Application.class);
        AnsiConsole.systemInstall();
        printLogo();
        application.run();
    }

}
