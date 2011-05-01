package jerry.config;

import jerry.Application;
import jerry.Buffer;
import jerry.Settings;
import jerry.command.Interpreter;
import jerry.format.DefaultFormatter;
import jerry.format.Formatter;
import jerry.general.GeneralCommandInterpreter;
import jerry.http.HttpCommandInterpreter;
import jerry.parse.DefaultParser;
import jerry.parse.Parser;
import jline.ConsoleReader;
import jline.SimpleCompletor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        commands.addAll(generalCommands());
        commands.addAll(httpCommands());
        return commands;
    }

    @Bean
    public List<String> generalCommands() {
        return unmodifiableList(asList("quit", "exit", "eval", "buffer", "set", "details"));
    }

    @Bean
    public List<String> httpCommands() {
        return unmodifiableList(asList("get", "post", "put", "delete", "head"));
    }

    @Bean
    public Buffer buffer() {
        return new Buffer();
    }

    @Bean
    public Application application() throws IOException {
        Application application = new Application();
        application.setConsoleReader(consoleReader());
        application.setHttpCommandInterpreter(httpCommandInterpreter());
        application.setFormatter(formatter());
        application.setSettings(settings());
        application.setExpressionParser(expressionParser());
        application.setParser(parser());
        application.setGeneralCommandInterpreter(generalCommandInterpreter());
        return application;
    }

    @Bean
    public Interpreter<ResponseEntity<Map<String, Object>>> httpCommandInterpreter() {
        return new HttpCommandInterpreter();
    }

    @Bean
    public Interpreter<String> generalCommandInterpreter() {
        return new GeneralCommandInterpreter();
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
    public EvaluationContext evaluationContext() {
        return new StandardEvaluationContext(buffer());
    }

    @Bean
    public Parser parser() {
        return new DefaultParser();
    }
}
