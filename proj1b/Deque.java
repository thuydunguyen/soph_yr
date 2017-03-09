/**
 * Created by Thuy-Du on 2/6/2017.
 */
public interface Deque<Item> {
    void printDeque();

    Item getRecursive(int i);

    Item removeFirst();

    Item removeLast();

    void addFirst(Item x);

    void addLast(Item x);
}
