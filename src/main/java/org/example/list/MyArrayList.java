package org.example.list;

import java.util.*;
import java.util.function.UnaryOperator;

public class MyArrayList<E> implements List<E> {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elementData;

    private int size = 0;

    public MyArrayList() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    public MyArrayList(int size) {
        elementData = new Object[size];
    }

    public MyArrayList(E[] objects) {
        elementData = objects;
        this.size = objects.length;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object e) {
        if (size == 0)
            return false;
        if (e == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null)
                    return true;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (elementData[i].equals(e))
                    return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyListIterator(0);
    }

    @Override
    public Object[] toArray() {
        Object[] newArray = new Object[size];
        System.arraycopy(elementData, 0, newArray, 0, size);
        return newArray;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        if (a.length > size) {
            for (int i = size; i < a.length; i++) {
                a[i] = null;
            }
        }
        System.arraycopy(elementData, 0, a, 0, size);
        return a;
    }

    @Override
    public boolean add(E e) {
        if (size == elementData.length) {
            resize();
        }
        elementData[size] = e;
        size++;
        return true;
    }

    private void resize() {
        var oldElementData = elementData;
        elementData = new Object[oldElementData.length * 2];
        System.arraycopy(oldElementData, 0, elementData, 0, oldElementData.length);
    }

    @Override
    public boolean remove(Object o) {
        var index = getElementIndex(o);
        switch (index) {
            case -1 -> {
                return false;
            }
            case 0 -> {
                elementData[0] = null;
                size--;
                if (size > 0)
                    moveElements(0);
                return true;
            }
            default -> {
                elementData[index] = null;
                size--;
                moveElements(index);
                return true;
            }
        }
    }

    private int getElementIndex(Object o) {
        if (size == 0)
            return -1;
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null)
                    return i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (elementData[i].equals(o))
                    return i;
            }
        }
        return -1;
    }

    private void moveElements(int start) {
        for (int i = start; i < size; i++) {
            elementData[i] = elementData[i + 1];
        }
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o :
                c) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        resize(c.size());
        for (E e : c) {
            elementData[size] = e;
            size++;
        }
        return true;
    }

    private void resize(int size) {
        elementData = Arrays.copyOf(elementData, elementData.length + size);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        resize(c.size());
        moveElementsAdd(index, c.size());
        for (E e : c) {
            elementData[index] = e;
            index++;
        }
        size += c.size();
        return true;
    }

    private void moveElementsAdd(int index, int size) {
        int n = 0;
        for (int i = index; i < size; i++) {
            elementData[this.size + n] = elementData[index];
            elementData[index] = null;
            n++;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (size == 0) {
            return false;
        }
        int count = 0;
        for (Object o :
                c) {
            if (remove(c))
                count++;
        }
        return count > 0;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (size == 0) {
            return false;
        }
        Object[] tmp = new Object[size];
        int count = 0, index;
        for (Object o :
                c) {
            index = getElementIndex(o);
            if (containsFromIndex(o, index)) {
                tmp[count] = o;
                count++;
            }
        }
        if (count > 0) {
            this.elementData = tmp;
            this.size = count;
        }
        return count > 0;
    }

    private boolean containsFromIndex(Object e, int index) {
        if (size == 0 || index == -1) {
            return false;
        }
        for (int i = index; i < size; i++) {
            if (e.equals(elementData[i]) || elementData[i] == e)
                return true;
        }
        return false;
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        List.super.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super E> c) {
        List.super.sort(c);
    }

    @Override
    public void clear() {
        size = 0;
        elementData = new Object[DEFAULT_CAPACITY];
    }

    @Override
    public E get(int index) {
        if (index > size || index < 0) {
            return null;
        }
        return (E) elementData[index];
    }

    @Override
    public E set(int index, E element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        var tmpElement = elementData[index];
        elementData[index] = element;
        return (E) tmpElement;
    }

    @Override
    public void add(int index, E element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (size == elementData.length) {
            resize();
        }
        moveElementsAdd(index, 0);
        elementData[index] = element;
        size++;
    }

    @Override
    public E remove(int index) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        var e = elementData[index];
        elementData[index] = null;
        moveElements(index);
        return (E) e;
    }

    @Override
    public int indexOf(Object o) {
        return getElementIndex(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        if (size == 0)
            return -1;
        int index = -1;
        if (o == null) {
            for (int i = 0; i < size; i++) {
                if (elementData[i] == null)
                    index = i;
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (elementData[i].equals(o))
                    index = i;
            }
        }
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyArrayList<?> that)) return false;
        return size == that.size && Arrays.equals(elementData, that.elementData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size);
        result = 31 * result + Arrays.hashCode(elementData);
        return result;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new MyListIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new MyListIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex > size || fromIndex < 0 || toIndex > size || toIndex < 0 || fromIndex > toIndex) {
            throw new IndexOutOfBoundsException();
        }
        int size = toIndex - fromIndex + 1;
        Object[] objects = new Object[size];
        for (int i = fromIndex; i <= toIndex; i++) {
            objects[i - fromIndex] = get(i);
        }
        return new MyArrayList<>((E[]) objects);
    }

    @Override
    public Spliterator<E> spliterator() {
        return List.super.spliterator();
    }

    private class MyListIterator implements ListIterator<E> {
        private int cursor;

        MyListIterator(int index) {
            cursor = index;
        }

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public E next() {
            if (hasNext()) {
                cursor++;
                return (E) elementData[cursor - 1];
            }
            throw new NoSuchElementException();
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0 && hasNext();
        }

        @Override
        public E previous() {
            if (cursor == 0)
                throw new NoSuchElementException();
            try {
                return (E) elementData[cursor];
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int nextIndex() {
            return cursor + 1;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public void remove() {
            try {
                if (hasNext()) {
                    elementData[cursor] = null;
                    size--;
                    moveElements(cursor);
                }
            } catch (UnsupportedOperationException | IllegalStateException ex) {
                ex.printStackTrace();
            }

        }

        @Override
        public void set(E e) {
            try {
                if (hasNext()) {
                    elementData[cursor] = e;
                }
            } catch (IllegalStateException | IllegalArgumentException | ClassCastException |
                     UnsupportedOperationException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(cursor);
        }

        @Override
        public void add(E e) {
            try {
                if (size == elementData.length) {
                    resize();
                }
                elementData[size] = e;
                size++;
            } catch (IllegalArgumentException | ClassCastException |
                     UnsupportedOperationException ex) {
                ex.printStackTrace();
            }

        }
    }
}
