public class LinkedListDeque<Item> {

	private class Listing {
		public Item item;
		public Listing next;
		public Listing prev;

		public Listing(Item i, Listing n) {
			item = i;
			next = n;
		} }

	private Listing ghost;
	private int size;
	private Listing last;
	private Listing first;

	public LinkedListDeque() {
		Listing ghost = new Listing(null, null);
		size = 0; }

	public LinkedListDeque(Item x) {
		Listing ghost = new Listing(null, null);
		ghost.next = new Listing(x, null);
		first = ghost.next;
		last = ghost.next;
		
		} 


	public void addFirst(Item item) {
		size += 1;
		Listing p = new Listing(item, first);
		if (first != null) {
			first.prev = p;}
		first = p;
		if (last == null) {
			last = first;}

	}
		
		

	public void addLast(Item item) {
		size += 1;
		Listing p = new Listing(item, null);
		if (first == null) {
			first = p;}
		p.prev = last;
		last.next = p;
		last = p;
		}


	public boolean isEmpty() {
		return size == 0;}

	public int size() {
		return size;}

	public void printDeque() {
		Listing p = first;
		while (p != null){
			if (p.next == null) {
				System.out.println(p.item);}
				else{
					System.out.print(p.item + " ");}
			p = p.next;}

		}


	public Item removeFirst() {
			if (isEmpty()){
				return null;}
			size -= 1;
			Listing f = first;
			first = first.next;
			return f.item;}

	public Item removeLast() {
			if(isEmpty()) {
				return null;}
			size -= 1;
			Listing l = last;
			last = last.prev;
			last.next = null;

			return l.item;}
			


	public Item get(int index) {
		int count = 0;
		Listing p = first;
		if (size < index) {
			return null;}
		while (count != index) {
			p = p.next;
			count += 1;
		}
		return p.item;}



	private Item getRhelper(int index, Listing p) {
		if (index == 0) {
			return p.item;}
		return getRhelper(index-1, p.next);}


	public Item getRecursive(int index) {
		return getRhelper(index, first);}


	


}