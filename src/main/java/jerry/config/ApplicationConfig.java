package jerry.config;

import jerry.Buffer;
import jline.ConsoleReader;
import jline.SimpleCompletor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import jerry.Application;
import jerry.Settings;
import jerry.format.DefaultFormatter;
import jerry.format.Formatter;
import jerry.parse.DefaultParser;
import jerry.parse.Parser;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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
        return new String[]{"get", "post", "put", "delete", "head", "quit", "exit", "buffer", "details"};
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
        application.setParser(parser());
        application.setFormatter(formatter());
        application.setSettings(settings());
        application.setExpressionParser(expressionParser());
        return application;
    }

    @Bean
    public Parser parser() {
        DefaultParser parser = new DefaultParser();
        parser.setBuffer(buffer());
        parser.setExpressionParser(expressionParser());
        return parser;
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
}
