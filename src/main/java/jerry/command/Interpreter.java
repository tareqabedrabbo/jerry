package jerry.command;

/**
 * @author Tareq Abedrabbo
 */
public interface Interpreter<T extends Command> {

    T interpret(String input);
}
