package jerry.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class Post extends HttpCommand {

    @Override
    protected ResponseEntity<Map<String, Object>> runInternal() {
        HttpEntity<Object> request = createRequest();
        ResponseEntity response = restOperations.exchange(url, HttpMethod.POST, request, Map.class);
        return response;
    }
}
