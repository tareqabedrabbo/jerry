package jerry.http;

import jerry.command.Interpreter;
import jerry.parse.Parser;
import jerry.parse.ParsingException;
import jerry.parse.Token;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class HttpCommandInterpreter implements Interpreter<ResponseEntity<Map<String, Object>>>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private List<String> httpCommands;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Resource(name="httpCommands")
    public void setHttpCommands(List<String> httpCommands) {
        this.httpCommands = httpCommands;
    }

    public ResponseEntity<Map<String, Object>> interpret(List<Token> tokens) {
        String command = tokens.get(0).value;
        HttpCommand httpCommand = null;

        if (command.equals("get")) {
            String url = getRequiredUrl(tokens, 1);
            Get get = applicationContext.getBean(Get.class);
            get.setUrl(url);
            httpCommand = get;
        }

        if (command.equals("put")) {
            String url = getRequiredUrl(tokens, 1);
            Object body = getData(tokens, 2);

            Put put = applicationContext.getBean(Put.class);
            put.setUrl(url);
            put.setBody(body);
            httpCommand = put;
        }

        if (command.equals("post")) {
            String url = getRequiredUrl(tokens, 1);
            Object body = getData(tokens, 2);

            Post post = applicationContext.getBean(Post.class);
            post.setUrl(url);
            post.setBody(body);
            httpCommand = post;
        }
        
        return httpCommand.run();
    }

    @Override
    public boolean supports(Token command) {
        Assert.isTrue(command.type == Token.Type.COMMAND);
        return httpCommands.contains(command.value);
    }

    private Object getData(List<Token> tokens, int position) {
        if (tokens.size() <= position) {
            return null;
        }
        return tokens.get(position).evaluated;
    }

    private String getRequiredUrl(List<Token> tokens, int position) {
        if (tokens.size() < position + 1) {
            throw new ParsingException("incomplete command [" + tokens.get(0).value + "]");
        }
        return String.valueOf(tokens.get(position).evaluated);
    }

}
