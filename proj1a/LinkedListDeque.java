public class LinkedListDeque<Item> {

    private class Listing {
        private Item item;
        private Listing next;
        private Listing prev;

        private Listing(Item i, Listing n, Listing p) {
            item = i;
            next = n;
            prev = p;
        }
    }

    private Listing ghost;
    private int size;
    private Listing last;


    public LinkedListDeque() {
        ghost = new Listing(null, null, null);
        size = 0;
        last = ghost;
        ghost.next = ghost;
        ghost.prev = ghost;
    }



    public void addFirst(Item item) {
        size += 1;
        Listing p = new Listing(item, ghost.next, ghost);
        ghost.next.prev = p;
        ghost.next = p;

        if (last == ghost) {
            last = p;
        }

    }


    public void addLast(Item item) {
        size += 1;
        Listing p = new Listing(item, ghost, last);
        last.next = p;
        last = p;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Listing p = ghost.next;
        while (p != ghost) {

            if (p.next == ghost) {
                System.out.println(p.item);
            } else {
                System.out.print(p.item + " ");
            }

            p = p.next;
        }

    }


    public Item removeFirst() {

        if (isEmpty()) {
            return null;
        }

        size -= 1;
        Listing f = ghost.next;
        ghost.next = ghost.next.next;
        if (ghost.next == ghost) {
            last = ghost;}
        return f.item;
    }


    public Item removeLast() {

        if (isEmpty()) {
            return null;
        }

        size -= 1;
        Listing l = last;
        last = last.prev;
        last.next = ghost;
        return l.item;
    }


    public Item get(int index) {
        int count = 0;
        Listing p = ghost.next;

        if (size < index) {
            return null;
        }

        while (count != index) {
            p = p.next;
            count += 1;
        }

        return p.item;
    }


    private Item getRhelper(int index, Listing p) {

        if (index == 0) {
            return p.item;
        }

        return getRhelper(index - 1, p.next);
    }


    public Item getRecursive(int index) {

        if (size < index) {
            return null;
        }

        return getRhelper(index, ghost.next);
    }

}

