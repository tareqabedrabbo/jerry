package jerry.http;

import jerry.Buffer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public abstract class AbstractHttpCommand implements HttpCommand {

    protected static final ArrayList<MediaType> ACCEPTABLE_MEDIA_TYPES = new ArrayList<MediaType>();

    static {
        ACCEPTABLE_MEDIA_TYPES.add(MediaType.APPLICATION_JSON);
    }

    private Buffer buffer;

    protected RestOperations restOperations;

    protected String url;

    protected String body;

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public final ResponseEntity<Map<String, Object>> run() {
        buffer.put("_url", url);
        ResponseEntity<Map<String, Object>> response = null;
        try {
            response = runInternal();
        } finally {
            buffer.put("_response", response);
        }
        return response;
    }

    protected abstract ResponseEntity<Map<String, Object>> runInternal();

    protected HttpEntity<String> createRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(ACCEPTABLE_MEDIA_TYPES);
        return new HttpEntity<String>(body, headers);
    }
}
