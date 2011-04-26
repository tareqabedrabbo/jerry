package jerry;

import java.util.LinkedHashMap;

/**
 * @author Tareq Abedrabbo
 */
public class Buffer extends LinkedHashMap<String, Object> {

//    @Override
//    public Object put(String key, Object value) {
//        validateKey(key);
//        return super.put(key, value);
//    }
//
//    @Override
//    public void putAll(Map<? extends String, ? extends Object> m) {
//        for (String key : m.keySet()) {
//            validateKey(key);
//        }
//        super.putAll(m);
//    }
//
//    @Override
//    public Object remove(Object key) {
//        if (key instanceof String) {
//            validateKey((String) key);
//        }
//        return super.remove(key);
//    }
//
//    private void validateKey(String key) {
//        if (key.startsWith("_")) {
//            throw new IllegalArgumentException("keys starting with _ are read-only [" + key + "]");
//        }
//    }
}
