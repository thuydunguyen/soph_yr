public class ArrayDeque<Item> {
    private Item[] items;
    private int size;
    private int NextFirst;
    private int NextLast;

    private int nextMove(int n, int m) {
        n += m;
        if (n < 0) {
            n = items.length - 1;
        }
        if (n == items.length) {
            n = 0;
        }
        return n;
    }

    public ArrayDeque() {
        items = (Item[]) new Object[10];
        size = 0;
        NextFirst = 0;
        NextLast = 1;
    }

    private void resize(int nsize) {
        Item[] a = (Item[]) new Object[nsize];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
        NextFirst = items.length - 1;
        NextLast = size + 1;

    }


    public void addFirst(Item x) {

        if (size == items.length) {
            resize(items.length * 2);
        }

        items[NextFirst] = x;
        NextFirst = nextMove(NextFirst, -1);
        size += 1;
    }


    public void addLast(Item x) {
        /* last item goes into position "size" */
        if (size == items.length) {
            resize(items.length * 2);
        }

        items[NextLast] = x;
        NextLast = nextMove(NextLast, 1);
        size += 1;
    }


    public Item get(int index) {

        if (size < index) {
            return null;
        }
        int n = 0;
        int i = 0;
        while (n <= index) {
            n += 1;
            i += 1;
            if ((NextFirst + i) == items.length) {
                i = -NextFirst;
            }
        }
        return items[NextFirst + i];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }


    public void printDeque() {
        int n = 0;
        int i = 0;
        while (n < size) {
            n += 1;
            i += 1;
            if ((NextFirst + i) == items.length) {
                i = -NextFirst;
            }

            System.out.print(items[NextFirst + i] + " ");
        }
    }


    public Item removeFirst() {

        if (isEmpty()) {
            return null;
        }
        NextFirst = nextMove(NextFirst, 1);
        items[NextFirst] = null;
        Item x = items[0];
        size -= 1;
        return x;
    }

    public Item removeLast() {

        if (isEmpty()) {
            return null;
        }

        NextLast = nextMove(NextLast, -1);
        if (NextLast < 0) {
            NextLast = items.length - 1;
        }
        size -= 1;
        Item x = items[NextLast];
        items[NextLast] = null;
        return x;
    }

}





