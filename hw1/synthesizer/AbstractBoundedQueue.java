package synthesizer;

/**
 * Created by Thuy-Du on 2/22/2017.
 */
public abstract class AbstractBoundedQueue<T> implements BoundedQueue<T> {
    protected int fillCount;
    protected int capacity;

    public abstract T peek();

    public abstract T dequeue();

    public abstract void enqueue(T x);

    public int capacity() {
        return capacity;
    }

    public int fillCount() {
        return fillCount;
    }

    public boolean isEmpty() {
        return fillCount == 0;
    }

    public boolean isFull() {
        return fillCount == capacity;
    }
}
