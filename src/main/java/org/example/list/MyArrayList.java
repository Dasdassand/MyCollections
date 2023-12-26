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

    public MyArrayList(Object[] objects, int size) {
        elementData = objects;
        this.size = size;
    }

    /**
     * @return кол-во элементов в листе
     */
    @Override

    public int size() {
        return size;
    }

    /**
     * @return пуст ли лист
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @param e искомый элемент
     * @return {@code true} если list содержит данный элемент
     */
    @Override
    public boolean contains(Object e) {
        if (size == 0)
            return false;
        if (e == null) {
            for (Object o :
                    elementData) {
                if (o == null)
                    return true;
            }
        } else {
            for (Object o :
                    elementData) {
                if (o.equals(e))
                    return true;
            }
        }
        return false;
    }

    /**
     * @return new Iterator<E>
     */
    @Override
    public Iterator<E> iterator() {
        return new MyListIterator(0);
    }

    /**
     * @return array as list Object type
     */
    @Override
    public Object[] toArray() {
        Object[] newArray = new Object[size];
        System.arraycopy(elementData, 0, newArray, 0, size);
        return newArray;
    }

    /**
     * @return массив из list
     */
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

    /**
     * @param e добавляемый элемент
     * @return true
     */
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

    /**
     * @param o удаляемый элемент
     * @return {@code true} если элемент найден и удалён
     */
    @Override
    public boolean remove(Object o) {
        var index = getElementIndex(0);
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
                moveElements(index);
                return true;
            }
        }
    }

    /**
     * @param o искомый элемент
     * @return индекс искомого элемента, если такой есть
     */
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

    /**
     * @param start индекс удалённого элемента
     * @TODO tetsing this method
     */
    private void moveElements(int start) {
        for (int i = start; i < size - 1; i++) {
            elementData[i] = elementData[i + 1];
        }
    }

    /**
     * @param c сравниваемые коллекция
     * @return соответствует ли элементы коллекции друг другу
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o :
                c) {
            if (!contains(c))
                return false;
        }
        return true;
    }

    /**
     * @param c добавляемые элементы
     * @return {@code true}
     */
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
        var oldElementData = elementData;
        elementData = new Object[oldElementData.length + size];
        System.arraycopy(oldElementData, 0, elementData, 0, oldElementData.length);
    }

    /**
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c     collection containing elements to be added to this list
     * @return {@code true}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index > size || index < 0) {
            return false;
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
            elementData[size + n] = elementData[index];
            n++;
        }
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection (optional operation).
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException if the {@code removeAll} operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of this list
     *                                       is incompatible with the specified collection
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this list contains a null element and the
     *                                       specified collection does not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
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

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection (optional operation).  In other words, removes
     * from this list all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException if the {@code retainAll} operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of an element of this list
     *                                       is incompatible with the specified collection
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this list contains a null element and the
     *                                       specified collection does not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
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

    /**
     * Replaces each element of this list with the result of applying the
     * operator to that element.  Errors or runtime exceptions thrown by
     * the operator are relayed to the caller.
     *
     * @param operator the operator to apply to each element
     * @throws UnsupportedOperationException if this list is unmodifiable.
     *                                       Implementations may throw this exception if an element
     *                                       cannot be replaced or if, in general, modification is not
     *                                       supported
     * @throws NullPointerException          if the specified operator is null or
     *                                       if the operator result is a null value and this list does
     *                                       not permit null elements
     *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
     * @implSpec The default implementation is equivalent to, for this {@code list}:
     * <pre>{@code
     *     final ListIterator<E> li = list.listIterator();
     *     while (li.hasNext()) {
     *         li.set(operator.apply(li.next()));
     *     }
     * }</pre>
     * <p>
     * If the list's list-iterator does not support the {@code set} operation
     * then an {@code UnsupportedOperationException} will be thrown when
     * replacing the first element.
     * @since 1.8
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        List.super.replaceAll(operator);
    }

    /**
     * @param c the {@code Comparator} used to compare list elements.
     */
    @Override
    public void sort(Comparator<? super E> c) {
        List.super.sort(c);
    }

    /**
     *
     */
    @Override
    public void clear() {
        elementData = new Object[DEFAULT_CAPACITY];
    }

    /**
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
    @Override
    public E get(int index) {
        if (index > size || index < 0) {
            return null;
        }
        return (E) elementData[index];
    }

    /**
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
    @Override
    public E set(int index, E element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        var tmpElement = elementData[index];
        elementData[index] = element;
        return (E) tmpElement;
    }

    /**
     * Inserts the specified element at the specified position in this list
     * (optional operation).  Shifts the element currently at that position
     * (if any) and any subsequent elements to the right (adds one to their
     * indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException if the {@code add} operation
     *                                       is not supported by this list
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this list
     * @throws NullPointerException          if the specified element is null and
     *                                       this list does not permit null elements
     * @throws IllegalArgumentException      if some property of the specified
     *                                       element prevents it from being added to this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index > size()})
     */
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

    /**
     * Removes the element at the specified position in this list (optional
     * operation).  Shifts any subsequent elements to the left (subtracts one
     * from their indices).  Returns the element that was removed from the
     * list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the {@code remove} operation
     *                                       is not supported by this list
     * @throws IndexOutOfBoundsException     if the index is out of range
     *                                       ({@code index < 0 || index >= size()})
     */
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

    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
    @Override
    public int indexOf(Object o) {
        return getElementIndex(o);
    }

    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     *
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in
     * this list, or -1 if this list does not contain the element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this list
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              list does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     */
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

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     *
     * @return a list iterator over the elements in this list (in proper
     * sequence)
     */
    @Override
    public ListIterator<E> listIterator() {
        return new MyListIterator(0);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @param index index of the first element to be returned from the
     *              list iterator (by a call to {@link ListIterator#next next})
     * @return a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index > size()})
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new MyListIterator(index);
    }

    /**
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex   high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if (fromIndex > size || fromIndex < 0 || toIndex > size || toIndex < 0 || fromIndex > toIndex) {
            return null;
        }
        int size = toIndex - fromIndex + 1;
        Object[] objects = new Object[size];
        for (int i = fromIndex; i < toIndex; i++) {
            objects[i] = get(i);
        }
        return new MyArrayList<E>(objects, size);
    }

    /**
     * @return a {@code Spliterator} over the elements in this list
     */
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
            return cursor < size;
        }

        /**
         * Returns the next element in the list and advances the cursor position.
         * This method may be called repeatedly to iterate through the list,
         * or intermixed with calls to {@link #previous} to go back and forth.
         * (Note that alternating calls to {@code next} and {@code previous}
         * will return the same element repeatedly.)
         *
         * @return the next element in the list
         * @throws NoSuchElementException if the iteration has no next element
         */
        @Override
        public E next() {
            if (hasNext()) {
                cursor++;
                return (E) elementData[cursor--];
            }
            throw new NoSuchElementException();
        }

        /**
         * Returns {@code true} if this list iterator has more elements when
         * traversing the list in the reverse direction.  (In other words,
         * returns {@code true} if {@link #previous} would return an element
         * rather than throwing an exception.)
         *
         * @return {@code true} if the list iterator has more elements when
         * traversing the list in the reverse direction
         */
        @Override
        public boolean hasPrevious() {
            return cursor >= 0 && hasNext();
        }

        /**
         * Returns the previous element in the list and moves the cursor
         * position backwards.  This method may be called repeatedly to
         * iterate through the list backwards, or intermixed with calls to
         * {@link #next} to go back and forth.  (Note that alternating calls
         * to {@code next} and {@code previous} will return the same
         * element repeatedly.)
         *
         * @return the previous element in the list
         * @throws NoSuchElementException if the iteration has no previous
         *                                element
         */
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

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #next}. (Returns list size if the list
         * iterator is at the end of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code next}, or list size if the list
         * iterator is at the end of the list
         */
        @Override
        public int nextIndex() {
            return cursor++;
        }

        /**
         * Returns the index of the element that would be returned by a
         * subsequent call to {@link #previous}. (Returns -1 if the list
         * iterator is at the beginning of the list.)
         *
         * @return the index of the element that would be returned by a
         * subsequent call to {@code previous}, or -1 if the list
         * iterator is at the beginning of the list
         */
        @Override
        public int previousIndex() {
            return cursor--;
        }

        /**
         * Removes from the list the last element that was returned by {@link
         * #next} or {@link #previous} (optional operation).  This call can
         * only be made once per call to {@code next} or {@code previous}.
         * It can be made only if {@link #add} has not been
         * called after the last call to {@code next} or {@code previous}.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this list iterator
         * @throws IllegalStateException         if neither {@code next} nor
         *                                       {@code previous} have been called, or {@code remove} or
         *                                       {@code add} have been called after the last call to
         *                                       {@code next} or {@code previous}
         */
        @Override
        public void remove() {
            try {
                if (hasNext()) {
                    elementData[cursor] = null;
                    moveElements(cursor);
                }
            } catch (UnsupportedOperationException | IllegalStateException ex) {
                ex.printStackTrace();
            }

        }

        /**
         * Replaces the last element returned by {@link #next} or
         * {@link #previous} with the specified element (optional operation).
         * This call can be made only if neither {@link #remove} nor {@link
         * #add} have been called after the last call to {@code next} or
         * {@code previous}.
         *
         * @param e the element with which to replace the last element returned by
         *          {@code next} or {@code previous}
         * @throws UnsupportedOperationException if the {@code set} operation
         *                                       is not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of the specified
         *                                       element prevents it from being added to this list
         * @throws IllegalStateException         if neither {@code next} nor
         *                                       {@code previous} have been called, or {@code remove} or
         *                                       {@code add} have been called after the last call to
         *                                       {@code next} or {@code previous}
         */
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

        /**
         * Inserts the specified element into the list (optional operation).
         * The element is inserted immediately before the element that
         * would be returned by {@link #next}, if any, and after the element
         * that would be returned by {@link #previous}, if any.  (If the
         * list contains no elements, the new element becomes the sole element
         * on the list.)  The new element is inserted before the implicit
         * cursor: a subsequent call to {@code next} would be unaffected, and a
         * subsequent call to {@code previous} would return the new element.
         * (This call increases by one the value that would be returned by a
         * call to {@code nextIndex} or {@code previousIndex}.)
         *
         * @param e the element to insert
         * @throws UnsupportedOperationException if the {@code add} method is
         *                                       not supported by this list iterator
         * @throws ClassCastException            if the class of the specified element
         *                                       prevents it from being added to this list
         * @throws IllegalArgumentException      if some aspect of this element
         *                                       prevents it from being added to this list
         */
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
