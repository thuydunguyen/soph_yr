public class ArrayDeque<Item> {
    private Item[] items;
    private int size;

    public ArrayDeque() {
        items = (Item[]) new Object[1];
        size = 0;
    }

    private void resize(int nsize) {
        Item[] a = (Item[]) new Object[nsize];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }


    public void addFirst(Item x) {

        if (size == items.length) {
            resize(size + 1);
        }

        Item[] a = (Item[]) new Object[items.length];
        System.arraycopy(items, 0, a, 1, size);
        items = a;
        items[0] = x;
        size += 1;}


    public void addLast(Item x) {
        /* last item goes into position "size" */
        if (size == items.length) {
            resize(size + 1);
        }

        items[size] = x;
        size += 1;
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

        size -= 1;
        Item x = items[size];
        items[size] = null;
        return x;
    }

    public void printDeque() {
        int n = 0;

        while (n < size) {
            if (n == (size-1)) {
                System.out.println(items[n]);
            }
                else {
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
        size -= 1;
        Item[] a = (Item[]) new Object[size];
        System.arraycopy(items, 1, a, 0, size);
        items = a;
        return x;
    }

}





