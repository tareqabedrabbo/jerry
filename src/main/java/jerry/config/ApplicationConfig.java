package jerry.config;

import jerry.Buffer;
import jerry.command.Interpreter;
import jerry.http.HttpCommand;
import jerry.http.HttpCommandInterpreter;
import jerry.parse.DefaultParser;
import jerry.parse.Parser;
import jline.ConsoleReader;
import jline.SimpleCompletor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import jerry.Application;
import jerry.Settings;
import jerry.format.DefaultFormatter;
import jerry.format.Formatter;

import java.io.IOException;

/**
 * @author Tareq Abedrabbo
 */

@Configuration
public class ApplicationConfig {

    @Bean
    public ConsoleReader consoleReader() throws IOException {
        ConsoleReader reader = new ConsoleReader();
        reader.addCompletor(new SimpleCompletor(commands()));
        return reader;
    }

    @Bean
    public String[] commands() {
        return new String[]{"get", "post", "put", "delete", "head", "quit", "exit", "buffer", "details", "eval"};
    }

    @Bean
    public Buffer buffer() {
        return new Buffer();
    }

    @Bean
    public Application application() throws IOException {
        Application application = new Application();
        application.setBuffer(buffer());
        application.setConsoleReader(consoleReader());
        application.setHttpInterpreter(httpInterpreter());
        application.setFormatter(formatter());
        application.setSettings(settings());
        application.setExpressionParser(expressionParser());
        return application;
    }

    @Bean
    public Interpreter<HttpCommand> httpInterpreter() {
        HttpCommandInterpreter interpreter = new HttpCommandInterpreter();
        return interpreter;
    }

    @Bean
    public Formatter formatter() {
        return new DefaultFormatter();
    }

    @Bean
    public Settings settings() {
        return new Settings();
    }

    @Bean
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    @Bean
    public Parser parser() {
        return new DefaultParser();
    }
}
