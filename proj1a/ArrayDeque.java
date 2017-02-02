public class ArrayDeque<Item> {
    private Item[] items;
    private int size;

    public ArrayDeque() {
        items = (Item[]) new Object[10];
        size = 0;
    }

    private void resize(int nsize) {
        Item[] a = (Item[]) new Object[nsize];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }


    public void addFirst(Item x) {
        size += 1;
        if (size == items.length) {
            resize(size * 2);
        }

        Item[] a = (Item[]) new Object[items.length];
        System.arraycopy(items, 0, a, 1, size-1);
        items = a;
        items[0] = x;
    }


    public void addLast(Item x) {

        size += 1;
        if (size == items.length) {
            resize(size * 2);
        }

        items[size-1] = x;

    }


    public Item get(int index) {

        if (size < index) {
            return null;
        }
        return items[index];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public Item removeLast() {

        if (isEmpty()) {
            return null;
        }
        resize(size * 2);
        size -= 1;
        Item x = items[size];
        items[size] = null;
        return x;
    }

    public void printDeque() {
        int n = 0;

        while (n < size) {
            if (n == (size - 1)) {
                System.out.println(items[n]);
            } else {
                System.out.print(items[n] + " ");
            }
            n += 1;
        }
    }

    public Item removeFirst() {

        if (isEmpty()) {
            return null;
        }

        Item x = items[0];
        Item[] a = (Item[]) new Object[size * 2];
        size -= 1;
        System.arraycopy(items, 1, a, 0, size);
        items = a;
        return x;
    }

}





