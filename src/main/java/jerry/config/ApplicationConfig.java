package jerry.config;

import jerry.Application;
import jerry.Buffer;
import jerry.Settings;
import jerry.command.Interpreter;
import jerry.format.DefaultFormatter;
import jerry.format.Formatter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Tareq Abedrabbo
 */

@Configuration
public class ApplicationConfig {

    @Bean
    public ConsoleReader consoleReader() throws IOException {
        ConsoleReader reader = new ConsoleReader();
        reader.addCompletor(new SimpleCompletor(allCommands().toArray(new String[0])));
        return reader;
    }

    @Bean
    public List<String> allCommands() {
        List<String> commands = new ArrayList<String>();
        commands.addAll(simpleCommands());
        commands.addAll(generalCommands());
        commands.addAll(httpCommands());
        return commands;
    }

    @Bean
    public List<String> simpleCommands() {
        return unmodifiableList(asList("quit", "exit", "eval", "buffer"));
    }

    @Bean
    public List<String> httpCommands() {
        return unmodifiableList(asList("get", "post", "put", "delete", "head"));
    }

    @Bean
    public List<String> generalCommands() {
        return unmodifiableList(asList("set", "details"));
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
