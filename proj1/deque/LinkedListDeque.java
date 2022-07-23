package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private class Node {

        public T item;
        public Node next;
        public Node prev;
        public Node(T i, Node n, Node p) {
            this.item = i;
            this.next = n;
            this.prev = p;
        }

    }
    /* The first item (if it exists) is at sentinel.next. */

    private Node sentinel;
    private int size;
    /** Creates an empty timingtest.SLList. */
    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        size = 0;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    /** Adds x to the front of the list. */
    @Override
    public void addFirst(T x) {
        Node newNode = new Node(x, sentinel.next, sentinel);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        size += 1;
    }


    /** Adds x to the end of the list. */
    @Override
    public void addLast(T x) {
        Node newNode = new Node(x, sentinel, sentinel.prev);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        size += 1;
    }

    /** Returns the first item in the list. */
    public T getFirst() {
        return sentinel.next.item;
    }

    /** returns last item in the list */
    public T getLast() {
        return sentinel.prev.item;
    }


    /** Returns the size of the list. */
    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel;
        while(p.next != sentinel) {
            p = p.next;
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if(size == 0) {
            return null;
        }
        T firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return firstItem;
    }

    @Override
    public T removeLast() {
        if(size == 0) {
            return null;
        }
        T lastItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;

        return lastItem;
    }

    @Override
    public T get(int index) {
        if(index > size - 1 || index < 0) {
            return null;
        }

        Node p = sentinel.next;
        while(index > 0) {
            p = p.next;
            index -= 1;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if(index > size - 1) {
            return null;
        }
        return getRecursive(sentinel.next, index);
    }

    public T getRecursive(Node start, int index) {
        if(index == 0) {
            return start.item;
        } else {
            return getRecursive(start.next, index - 1);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator {
        private int wizPos;
        public LinkedListDequeIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public Object next() {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if(this == o){
            return true;
        }
        if(o == null){
            return false;
        }
        if(!(o instanceof Deque)) {
            return false;
        }
        Deque<T> newObject = (Deque<T>) o;
        if(this.size() != newObject.size()){
            return false;
        }
        for(int i = 0; i < this.size(); i++) {
            if(!get(i).equals(newObject.get(i)))
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        /* Creates a list of one integer, namely 10 */
        LinkedListDeque<String> L = new LinkedListDeque();
        L.addFirst("yo whats up man?");
        L.addFirst("Nothing much bro, you?");
        L.addFirst("same here bro. yo wanna grab some beer to drink?");
        L.addFirst("sounds great, let's do this!");
        L.addFirst("you got a preference?");
        System.out.println(L.size());
        System.out.println(L.getLast());
        System.out.println(L.getFirst());
        L.printDeque();
        System.out.println(L.get(3));
        System.out.println(L.getRecursive(3));
    }
}