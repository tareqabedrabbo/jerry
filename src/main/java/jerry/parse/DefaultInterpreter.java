package jerry.parse;

import jerry.http.Get;
import jerry.http.HttpCommand;
import jerry.http.Post;
import jerry.http.Put;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Tareq Abedrabbo
 */
public class DefaultInterpreter implements Interpreter, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Parser parser;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public HttpCommand interpret(String input) {
        Assert.hasText(input);

        List<Token> tokens = parser.parse(input);
        String command = tokens.get(0).value;

        if (command.equals("get")) {
            String url = getRequiredUrl(tokens, 1);
            Get get = applicationContext.getBean(Get.class);
            get.setUrl(url);
            return get;
        }

        if (command.equals("put")) {
            String url = getRequiredUrl(tokens, 1);
            String body = getData(tokens, 2);

            Put put = applicationContext.getBean(Put.class);
            put.setUrl(url);
            put.setBody(body);
            return put;
        }

        if (command.equals("post")) {
            String url = getRequiredUrl(tokens, 1);
            String body = getData(tokens, 2);

            Post post = applicationContext.getBean(Post.class);
            post.setUrl(url);
            post.setBody(body);
            return post;
        }

        throw new ParsingException("unable to parse [" + input + "]");
    }

    private String getData(List<Token> tokens, int position) {
        if (tokens.size() <= position) {
            return null;
        }
        return tokens.get(position).value;
    }

    private String getRequiredUrl(List<Token> tokens, int position) {
        if (tokens.size() < position + 1) {
            throw new ParsingException("incomplete command [" + tokens.get(0).value + "]");
        }
        return tokens.get(position).value;
    }

}
