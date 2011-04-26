package jerry.format;

import org.fusesource.jansi.Ansi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Tareq Abedrabbo
 */
public class DefaultFormatter implements Formatter {

    //adapting jackson output to Jansi

    private final static char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    final static int[] sOutputEscapes;

    static {
        int[] table = new int[256];
        // Control chars need generic escape sequence
        for (int i = 0; i < 32; ++i) {
            table[i] = -(i + 1);
        }
        /* Others (and some within that range too) have explicit shorter
         * sequences
         */
        table['"'] = '"';
        table['\\'] = '\\';
        // Escaping of slash is optional, so let's not add it
        table[0x08] = 'b';
        table[0x09] = 't';
        table[0x0C] = 'f';
        table[0x0A] = 'n';
        table[0x0D] = 'r';
        sOutputEscapes = table;
    }

    private int tabSize = 2;
    private char[] tabs = new char[tabSize];

    public DefaultFormatter() {
        Arrays.fill(tabs, ' ');
    }

    public String format(Map<String, Object> json) {
        Ansi ansi = ansi();
        appendValue(ansi, json, 0);
        return ansi.toString();
    }

    private void appendValue(Ansi ansi, Object value, int shift) {
        if (value == null) {
            appendNullValue(ansi);
        } else if (value instanceof String) {
            appendStringValue(ansi, (String) value);
        } else if (value instanceof Boolean) {
            appendBooleanValue(ansi, value);
        } else if (value instanceof Number) {
            appendNumberValue(ansi, (Number) value);
        } else if (value instanceof Collection) {
            appendArrayValue(ansi, (Collection) value, shift);
        } else if (value instanceof Map) {
            appendObjectValue(ansi, (Map) value, shift);
        } else {
            throw new IllegalArgumentException("Unsupported data type [" + value.getClass() + "]");
        }
    }

    private void appendNullValue(Ansi ansi) {
        ansi.a((Object) null);
    }

    private void appendObjectValue(Ansi ansi, Map<String, Object> json, int shift) {
        ansi.fg(RED).a("{").newline().reset();
        shift++;
        if (json != null) {
            int count = 0;
            for (Map.Entry<String, Object> element : json.entrySet()) {
                String name = element.getKey();
                Object value = element.getValue();
                if (count > 0) {
                    ansi.fg(RED).a(",\n").reset();
                }
                count++;
                tab(ansi, shift);
                ansi.fg(YELLOW).a('"');
                appendQuoted(ansi, name);
                ansi.a('"').reset();

                ansi.fg(RED).a(" : ").reset().fg(BLUE);
                appendValue(ansi, value, shift);
                ansi.reset();
                if (count == json.size()) {
                    ansi.newline();
                }
            }
        }
        shift--;
        tab(ansi, shift);
        ansi.fg(RED).a("}").reset();
    }

    private void appendNumberValue(Ansi ansi, Number value) {
        ansi.a(value);
    }

    private void appendArrayValue(Ansi ansi, Collection value, int shift) {
        ansi.a("[");
        Iterator iterator = ((Collection) value).iterator();
        while (iterator.hasNext()) {
            appendValue(ansi, iterator.next(), shift);
            if (iterator.hasNext()) {
                ansi.a(',').a(' ');
            }
        }
        ansi.a("]");
    }

    private void appendBooleanValue(Ansi ansi, Object value) {
        ansi.a(value);
    }

    private void appendStringValue(Ansi ansi, String value) {
        ansi.a('"');
        appendQuoted(ansi, value);
        ansi.a('"');
    }

    private void appendQuoted(Ansi ansi, String content) {
        final int[] escCodes = sOutputEscapes;
        int escLen = escCodes.length;
        for (int i = 0, len = content.length(); i < len; ++i) {
            char c = content.charAt(i);
            if (c >= escLen || escCodes[c] == 0) {
                ansi.a(c);
                continue;
            }
            ansi.a('\\');
            int escCode = escCodes[c];
            if (escCode < 0) { // generic quoting (hex value)
                // We know that it has to fit in just 2 hex chars
                ansi.a('u').a('0').a('0');
                int value = -(escCode + 1);
                ansi.a(HEX_CHARS[value >> 4]);
                ansi.a(HEX_CHARS[value & 0xF]);
            } else { // "named", i.e. prepend with slash
                ansi.a((char) escCode);
            }
        }
    }

    private void tab(Ansi ansi, int shift) {
        for (int i = 0; i < shift; i++) {
            ansi.a(tabs);
        }
    }

}
