package jerry.command;

/**
 * @author Tareq Abedrabbo
 */
public interface Command<T> {

    T run();
}
