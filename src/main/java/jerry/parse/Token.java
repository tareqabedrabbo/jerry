package jerry.parse;

/**
 * @author Tareq Abedrabbo
 */
public class Token {

    public final Type type;
    public final String value;
    public Object evaluated;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "evaluated=" + evaluated +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    static enum Type {
        COMMAND, JSON, EXP, URL, STRING;
    }
}
