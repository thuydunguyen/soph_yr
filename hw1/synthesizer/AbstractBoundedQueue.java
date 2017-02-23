package synthesizer;

/**
 * Created by Thuy-Du on 2/22/2017.
 */
public abstract class AbstractBoundedQueue<T> implements BoundedQueue<T> {
    protected int fillCount;
    protected int capacity;

    public int capacity() {
        return capacity;
    }

    public int fillCount() {
        return fillCount;
    }

}
