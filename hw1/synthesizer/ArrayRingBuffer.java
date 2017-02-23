// TODO: Make sure to make this class a part of the synthesizer package
// package <package name>;
package synthesizer;
import java.util.Iterator;
import java.util.Objects;


//TODO: Make sure to make this class and all of its methods public
//TODO: Make sure to make this class extend AbstractBoundedQueue<t>
public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;


    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        // TODO: Create new array with capacity elements.
        //       first, last, and fillCount should all be set to 0.
        //       this.capacity should be set appropriately. Note that the local variable
        //       here shadows the field we inherit from AbstractBoundedQueue, so
        //       you'll need to use this.capacity to set the capacity.
        rb = (T[]) new Object[capacity];
        fillCount = 0;
        first = 0;
        last = 0;
        this.capacity = capacity;
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */
    public void enqueue(T x) {
        // TODO: Enqueue the item. Don't forget to increase fillCount and update last.
        if (isFull()) {
            throw new RuntimeException("Ring buffer overflow)");
        }
        rb[last] = x;
        if (last+1 == capacity) {
            last = 0;
        }
        else {
        last = last+1;}

        fillCount += 1;

    }


    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        // TODO: Dequeue the first item. Don't forget to decrease fillCount and update
        if (isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        T item = rb[first];
        rb[first] = null;
        if (first == capacity - 1) {
            first = 0;
        }
        else {
            first = first+1;}

        fillCount -= 1;
        return item;
    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        // TODO: Return the first item. None of your instance variables should change.
        return rb[first];
    }

    // TODO: When you get to part 5, implement the needed code to support iteration.

    private class iter implements Iterator<T> {
        private int index;

        public iter() {
            index = 0;
        }

        public boolean hasNext() {
            return (index < capacity);
        }

        public T next() {
            T current = rb[index];
            index += 1;
            return current;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new iter();

    }
}
