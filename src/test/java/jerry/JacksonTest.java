package jerry;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public class JacksonTest {

    private static final String JSON_1 =
            "{\"data\":\"http://192.168.1.3:7474/db/data/\",\"management\":\"http://192.168.1.3:7474/db/manage/\"}";
    private static final String JSON_2 = "{\n" +
            "    \"total_rows\": 3,\n" +
            "    \"offset\": 0,\n" +
            "    \"rows\": [{ \"type\":\"foo\", \"value\":\"aaa\" },{ \"type\":\"bar\", \"value\":\"bbb\" },{ \"value\":\"ccc\",\"type\":\"bar\"  }]\n" +
            "}";


    @Test
    public void map() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map map = objectMapper.readValue(JSON_2, Map.class);
        System.out.println(map);
    }
}
