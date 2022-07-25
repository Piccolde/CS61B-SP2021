package deque;

import org.apache.commons.collections.iterators.ArrayIterator;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ArrayDeque<T> implements Deque<T>{
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }
    /** double the size of the current array */
    private void expand(int capacity) {
        T[] a = (T[]) new Object[capacity];
        if(nextFirst == 0) {
            System.arraycopy(items, 0, a, 0, size);
            nextFirst = size * 2 - 1;
            nextLast = size;
        } else {
            if(nextFirst == size - 1) {
                nextFirst = (size - 1)/2;
                nextLast = (size - 1)/2 + 1;
            }
            System.arraycopy(items, 0, a, 0, nextLast);
            System.arraycopy(items, nextLast, a, nextLast + size, size - nextLast);
            nextFirst += size;
        }
        items = a;
    }

    public void contract() {
        T[] a = (T[]) new Object[items.length / 4 + 1];
        if(nextFirst < nextLast) {
            System.arraycopy(items, nextFirst + 1, a, 0, size);
        } else {
            System.arraycopy(items, nextFirst + 1, a, 0, items.length - nextFirst - 1);
            System.arraycopy(items, 0, a, items.length - nextFirst - 1, nextLast);
        }
        items = a;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            expand(size * 2);
        }
        items[nextFirst] = item;
        size += 1;
        nextFirst -= 1;
        if (nextFirst < 0) {
            nextFirst = items.length - 1;
        }
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            expand(size * 2);
        }
        items[nextLast] = item;
        size += 1;
        nextLast += 1;
        if (nextLast > items.length - 1) {
            nextLast = 0;
        }
    }

    @Override
    public boolean isEmpty() {
        return Deque.super.isEmpty();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for(int i = 0; i < size; i++) {
            System.out.print(get(i) + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        nextFirst += 1;
        if (nextFirst >= items.length) {
            nextFirst = 0;
        }
        T itemToRemove = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        if (items.length >= 16 && size <= items.length * 0.25) {
            contract();
        }
        return itemToRemove;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        nextLast -= 1;
        if (nextLast < 0) {
            nextLast = items.length - 1;
        }
        T itemToRemove = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        if (items.length >= 16 && size <= items.length * 0.25) {
            contract();
        }
        return itemToRemove;
    }

    @Override
    public T get(int index) {
        if(index > size - 1 || index < 0) {
            return null;
        }
        int location = nextFirst + 1 + index;
        if(location >= items.length) {//if the index starts from 0, location >= items.length - 1/
            location -= items.length;
        }
        return items[location];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements  Iterator<T> {
        private int wizPos;

        public ArrayDequeIterator() {
            wizPos = 0;
        }
        @Override
        public boolean hasNext() {
            return wizPos < size;
        }

        @Override
        public T next() {
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(!(o instanceof Deque))
            return false;
        Deque<T> newObj = (Deque<T>) o;
        if(this.size != newObj.size())
            return false;

        for(int i = 0; i < size; i++) {
            if(this.get(i) != newObj.get(i))
                return false;
        }

        return true;
    }
}
