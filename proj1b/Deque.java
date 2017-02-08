/**
 * Created by Thuy-Du on 2/6/2017.
 */
public interface Deque<Item> {
    public void printDeque();

    public Item getRecursive(int i);

    public Item removeFirst();

    public Item removeLast();

    public void addFirst(Item x);

    public void addLast(Item x);
}
