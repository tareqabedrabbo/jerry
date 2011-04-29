package jerry.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class Put extends AbstractHttpCommand {

    @Override
    public ResponseEntity<Map<String, Object>> runInternal() {
        HttpEntity<Object> request = createRequest();
        ResponseEntity response = restOperations.exchange(url, HttpMethod.PUT, request, Map.class);
        return response;
    }

}
