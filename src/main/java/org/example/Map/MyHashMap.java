package org.example.Map;

import java.io.Serializable;
import java.util.*;

public class MyHashMap<K, V> implements Map<K, V>, Cloneable, Serializable {


    static class Node<K, V> implements Map.Entry<K, V> {
        private int hash;
        private K key;
        private V value;
        private Map.Entry<K, V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            var old = this.value;
            this.value = value;
            return old;
        }

        public void setNext(Node<K, V> next) {
            this.next = next;
        }
    }

    static class BinaryTreeNode<K, V> implements Map.Entry<K, V> {
        private int hash;
        private K key;
        private V value;
        private BinaryTreeNode<K, V> left;
        private BinaryTreeNode<K, V> right;

        public void setLeft(BinaryTreeNode<K, V> left) {
            this.left = left;
        }

        public void setRight(BinaryTreeNode<K, V> right) {
            this.right = right;
        }

        public int getHash() {
            return hash;
        }

        public BinaryTreeNode<K, V> getLeft() {
            return left;
        }

        public BinaryTreeNode<K, V> getRight() {
            return right;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            var oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }

    private Node<K, V>[] table;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private float loadFactor = 0.75f;
    private int size;

    public MyHashMap() {
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.table = new Node[initialCapacity];
    }

    public MyHashMap(float loadFactor, int size, Node<K, V>[] table) {
        this.loadFactor = loadFactor;
        this.size = size;
        this.table = table;
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
    public boolean containsKey(Object key) {
        if (table.getClass().equals(Node[].class))
            return containsKeyList(key);
        else
            return containsKeyTree(key);
    }

    private boolean containsKeyList(Object key) {
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                if (node.key == key)
                    return true;
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        return false;
    }

    private boolean containsKeyTree(Object key) {
    }

    @Override
    public boolean containsValue(Object value) {
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                if (node.value == value)
                    return true;
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        return false;
    }

    @Override
    public V get(Object key) {
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                if (node.key == key) {
                    return node.value;
                }
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        throw new NoSuchElementException();
    }

    @Override
    public V put(K key, V value) {
        int index = getIndex(key);
        if (table[index] == null) {
            table[index] = new Node<>(Objects.hashCode(key), key, value, null);
            size++;
            checkSizeArray();
            return null;
        } else {
            boolean flag = true;
            var node = table[index];
            do {
                if (Objects.hashCode(key) == node.hash && node.key.equals(key)) {
                    node.value = value;
                    flag = false;
                }
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);

            if (flag) {
                node.setNext(new Node<>(Objects.hashCode(key), key, value, null));
                size++;
            }
            checkSizeBucket(index);
            checkSizeArray();
            return value;
        }
    }

    private void checkSizeBucket(int index) {
        if (index == 1) {
            System.out.println();
        }
    }

    private void checkSizeArray() {
        if (size >= table.length * loadFactor)
            resize();
    }

    private void resize() {
        var tmp = table;
        table = new Node[tmp.length * 2];
        for (Node n : tmp) {
            put((K) n.key, (V) n.value);
        }
    }

    private int getIndex(K key) {
        return key == null
                ? 0
                : Objects.hashCode(key) & (table.length - 1);
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        table = new Node[DEFAULT_INITIAL_CAPACITY];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                set.add(node.key);
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        return set;
    }

    @Override
    public Collection<V> values() {
        Collection<V> collection = new ArrayList<>();
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                collection.add(node.value);
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        return collection;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();
        for (Node<K, V> kvNode : table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                set.add(node);
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        return set;
    }

    @Override
    public MyHashMap<K, V> clone() {
        MyHashMap<K, V> clone = new MyHashMap<>(this.loadFactor, this.size, this.table);
        return clone;
    }


}
