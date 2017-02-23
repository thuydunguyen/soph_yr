package synthesizer;

/**
 * Created by Thuy-Du on 2/22/2017.
 */
public interface BoundedQueue<T> {
    int capacity();
    int fillCount();
    void enqueue(T x);
    T dequeue();
    T peek();
    default boolean isEmpty() {
        return fillCount() == 0;
    }
    default boolean isFull() {
        return fillCount() == capacity();
    }

}
