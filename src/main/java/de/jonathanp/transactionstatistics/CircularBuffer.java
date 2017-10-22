package de.jonathanp.transactionstatistics;

import java.util.List;
import java.util.ListIterator;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* Data structure representing a fixed size circular array */
class CircularBuffer<T> {

    private final List<T> dataBuffer;

    /* Initialise and fills the buffers with objects */
    public CircularBuffer(Class<T> type, int size) {
        dataBuffer = IntStream.range(0, size).mapToObj(i -> newElementInstance(type)).collect(Collectors.toList());
    }

    public T get(int index) {
        return dataBuffer.get(index);
    }

    /* Get an iterator from index start to index end(both inclusive) */
    public ListIterator<T> iterator(int start, int end) {
        return new ListIterator<T>() {

            int currentIndex = start - 1;

            @Override
            public boolean hasNext() {
                return currentIndex != end;
            }

            @Override
            public T next() {
                currentIndex = nextIndex();
                return dataBuffer.get(currentIndex);
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex != start;
            }

            @Override
            public T previous() {
                currentIndex = previousIndex();
                return dataBuffer.get(currentIndex);
            }

            @Override
            public int nextIndex() {
                return currentIndex < (dataBuffer.size() - 1) ? currentIndex + 1 : 0;
            }

            @Override
            public int previousIndex() {
                return currentIndex == 0 ? dataBuffer.size() - 1 : currentIndex - 1;
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
    }

    private T newElementInstance(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new UnsupportedOperationException(); //Should never happen
        }
    }

}