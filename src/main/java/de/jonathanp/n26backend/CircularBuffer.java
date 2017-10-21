package de.jonathanp.n26backend;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class CircularBuffer<T>{

    public CircularBuffer(int size) {
        this.dataBuffer = new ArrayList<T>(size);
    }

    T get(int index)
    {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        return dataBuffer.get(index);
    }

    public ListIterator<T> iterator(int start, int end) {
        ListIterator<T> it = new ListIterator<T>() {

            int currentIndex = start - 1;

            @Override
            public boolean hasNext() {
                return currentIndex + 1 != end;
            }

            @Override
            public T next() {
                currentIndex = nextIndex();
                return dataBuffer.get(currentIndex);
            }

            @Override
            public boolean hasPrevious() {
                return true;
            }

            @Override
            public T previous() {
                currentIndex = previousIndex();
                return dataBuffer.get(currentIndex);
            }

            @Override
            public int nextIndex() {
                return currentIndex < size ? currentIndex + 1 : 0;
            }

            @Override
            public int previousIndex() {
                return currentIndex == 0 ? size - 1 : currentIndex -1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T value) {
                dataBuffer.set(currentIndex, value);
            }

            @Override
            public void add(T value) {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }

    private ArrayList<T> dataBuffer;
    private int size;
}