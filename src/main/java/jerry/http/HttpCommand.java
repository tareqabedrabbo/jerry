package jerry.http;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public interface HttpCommand {

    ResponseEntity<Map<String, Object>> run();
}
