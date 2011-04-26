package jerry.format;

import java.util.Map;

/**
 * @author Tareq Abedrabbo
 */
public interface Formatter {
    String format(Map<String, Object> json);
}
