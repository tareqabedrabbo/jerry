package jerry.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class Delete extends HttpCommand {

    @Override
    protected ResponseEntity<Map<String, Object>> runInternal() {
        restOperations.delete(url);
        return null;
    }
}
