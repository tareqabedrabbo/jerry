package jerry.http;

import jerry.Buffer;
import jerry.command.Command;
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
public abstract class HttpCommand implements Command<ResponseEntity<Map<String, Object>>> {

    protected static final ArrayList<MediaType> ACCEPTABLE_MEDIA_TYPES = new ArrayList<MediaType>();

    static {
        ACCEPTABLE_MEDIA_TYPES.add(MediaType.APPLICATION_JSON);
    }

    private Buffer buffer;

    protected RestOperations restOperations;

    protected String url;

    protected Object body;

    public void setBuffer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    @Override
    public final ResponseEntity<Map<String, Object>> run() {
        buffer.put("_url", url);
        ResponseEntity<Map<String, Object>> response = null;
        try {
            response = runInternal();
            return response;
        } finally {
            buffer.put("_response", response);
        }
    }

    protected abstract ResponseEntity<Map<String, Object>> runInternal();

    protected HttpEntity<Object> createRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(ACCEPTABLE_MEDIA_TYPES);
        return new HttpEntity<Object>(body, headers);
    }
}
