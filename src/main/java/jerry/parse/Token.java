package jerry.parse;

/**
 * @author Tareq Abedrabbo
 */
public class Token {

    final Type type;
    final String value;
    Object evaluated;

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
