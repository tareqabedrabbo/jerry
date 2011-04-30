package jerry.config;

import jerry.Buffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestOperations;
import jerry.http.HttpCommand;
import jerry.http.Get;
import jerry.http.Post;
import jerry.http.Put;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Constructor;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

/**
 * @author Tareq Abedrabbo
 */
@Configuration
public class CommandsConfig {

    @Autowired
    private Buffer buffer;

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Get get() throws Exception {
        return createCommand(Get.class);
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Put put() throws Exception {
        return createCommand(Put.class);
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public Post post() throws Exception {
        return createCommand(Post.class);
    }

    private <T extends HttpCommand> T createCommand(Class<T> commandClass) throws Exception {
        Constructor<T> constructor = commandClass.getConstructor();
        T instance = constructor.newInstance();
        instance.setRestOperations(restOperations());
        instance.setBuffer(buffer);
        return instance;
    }

}
