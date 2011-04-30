package jerry.http;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class Get extends HttpCommand {

    @Override
    public ResponseEntity<Map<String, Object>> runInternal() {
        ResponseEntity response = restOperations.getForEntity(url, Map.class);
        return response;
    }
}
