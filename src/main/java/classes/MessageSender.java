package classes;

public interface MessageSender<T> {
    boolean sendData(T t);
}
