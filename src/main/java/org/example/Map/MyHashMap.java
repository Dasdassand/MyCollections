package org.example.Map;

import java.io.Serializable;
import java.util.*;

public class MyHashMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    static class Node<K, V> implements Map.Entry<K, V> {
        private final int hash;
        private final K key;
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
        private final int hash;
        private final K key;
        private V value;
        private BinaryTreeNode<K, V> left;
        private BinaryTreeNode<K, V> right;

        public BinaryTreeNode(int hash, K key, V value, BinaryTreeNode<K, V> left, BinaryTreeNode<K, V> right) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

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

    private Map.Entry<K, V>[] table;

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private float loadFactor = 0.75f;
    private int size;
    private final Set<K> keys = new HashSet<>();
    private final Collection<V> values = new ArrayList<>();

    public MyHashMap() {
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.table = new Node[initialCapacity];
    }

    public MyHashMap(float loadFactor, int size, Entry<K, V>[] table) {
        this.loadFactor = loadFactor;
        this.size = size;
        this.table = table;
        for (Entry e : table) {
            keys.add((K) e.getKey());
            values.add((V) e.getValue());
        }
    }

    private boolean isListTable() {
        return isListTable();
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
        if (isListTable())
            return containsKeyList(key);
        else
            return containsKeyTree(key);
    }

    private boolean containsKeyList(Object key) {
        try {
            var hash = Objects.hashCode(key);
            var node = (Node<K, V>) table[getIndex((K) key)];
            while (true) {
                if (node.hash == hash) {
                    if (node.key.equals(key))
                        return true;
                } else if (!(node.next == null)) {
                    node = (Node<K, V>) node.next;
                }
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        return false;

    }


    private boolean containsKeyTree(Object key) {
        try {
            var hash = Objects.hashCode(key);
            var node = (BinaryTreeNode<K, V>) table[getIndex((K) key)];
            while (true) {
                if (node.hash == hash) {
                    if (node.key.equals(key))
                        return true;
                }
                if (node.hash > hash && node.left != null) {
                    node = node.left;
                } else if (node.right != null && hash > node.hash) {
                    node = node.right;
                } else break;
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (isListTable())
            return containsValueList(value);
        else
            return containsValueTree(value);
    }

    private boolean containsValueList(Object value) {
        for (Node<K, V> kvNode : (Node<K, V>[]) table) {
            if (kvNode == null)
                continue;
            var node = kvNode;
            do {
                if (node.value == value) {
                    return true;
                }
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);
        }
        throw new NoSuchElementException();
    }

    private boolean dfsValue(BinaryTreeNode<K, V> node, V value) {
        if (node == null) return false;
        if (node.value == value) return true;
        return dfsValue(node.getLeft(), value) || dfsValue(node.getRight(), value);
    }

    private boolean containsValueTree(Object value) {
        for (BinaryTreeNode<K, V> kvNode : (BinaryTreeNode<K, V>[]) table) {
            try {
                if (dfsValue(kvNode, (V) value))
                    return true;
            } catch (ClassCastException exception) {
                exception.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        if (isListTable()) {
            return getValueList(key);
        } else {
            try {
                return dfsGetValue(key);
            } catch (ClassCastException exception) {
                exception.printStackTrace();
            }
        }
        throw new NoSuchElementException();
    }

    private V getValueList(Object key) {
        return getListNode(key).value;
    }

    private V dfsGetValue(Object key) {
        return getTreeNode(key).value;
    }

    @Override
    public V put(K key, V value) {
        keys.add(key);
        values.add(value);
        if (isListTable()) {
            return putNode(key, value);
        } else {
            return putTree(key, value);
        }
    }

    private V putNode(K key, V value) {
        int index = getIndex(key);
        if (table[index] == null) {
            table[index] = new Node<>(Objects.hashCode(key), key, value, null);
            size++;
            checkSizeArray();
            return null;
        } else {
            boolean flag = true;
            var node = (Node<K, V>) table[index];
            do {
                if (Objects.hashCode(key) == node.hash && node.key.equals(key)) {
                    node.setValue(value);
                    flag = false;
                }
                if (node.next != null) {
                    node = (Node<K, V>) node.next;
                } else
                    break;
            } while (true);

            if (flag) {
                node.next = (new Node<>(Objects.hashCode(key), key, value, null));
                size++;
            }
            checkSizeBucket(index);
            checkSizeArray();
            return value;
        }
    }

    /**
     * Все значения в узлах левого дочернего поддерева меньше значения родительского узла
     * Все значения в узлах правого дочернего поддерева больше значения родительского узла
     * Каждый дочерний узел тоже является бинарным деревом поиска
     *
     * @param key
     * @param value
     * @return
     */
    private V putTree(K key, V value) {
        int index = getIndex(key);
        if (table[index] == null) {
            table[index] = new BinaryTreeNode<>(Objects.hashCode(key), key, value, null, null);
            size++;
            checkSizeArray();
            return null;
        } else {
            var hash = Objects.hashCode(key);
            var oldValue = putUseDFS((BinaryTreeNode<K, V>) table[index], hash, value, key);
            size++;
            checkSizeArray();
            return oldValue;
        }

    }

    private V putUseDFS(BinaryTreeNode<K, V> node, int hash, V value, K key) {
        if (node.hash == hash) {
            node.value = value;
            return value;
        } else {
            if (node.hash < hash) {
                if (node.left == null) {
                    node.left = new BinaryTreeNode<>(hash, key, value, null, null);
                    return value;
                } else {
                    return putUseDFS(node.left, hash, value, key);
                }
            } else {
                if (node.right == null) {
                    node.right = new BinaryTreeNode<>(hash, key, value, null, null);
                    return value;
                } else {
                    return putUseDFS(node.right, hash, value, key);
                }
            }

        }
    }

    private void checkSizeBucket(int index) {
        if (index == 8) {
            castListTableToTreeTable();
        } else if (index == 6) {
            castTreeTableToListTable();
        }
    }

    private void castListTableToTreeTable() {
        BinaryTreeNode<K, V>[] newTable = new BinaryTreeNode[table.length];
        var oldTable = table;
        this.table = newTable;
        for (int i = 0; i < table.length; i++) {
            castListToTree((Node<K, V>) oldTable[i]);
        }
    }

    private void castListToTree(Node<K, V> oldNode) {
        if (oldNode == null) {
            return;
        }
        while (true) {
            put(oldNode.key, oldNode.value);
            if (oldNode.next != null) {
                oldNode = (Node<K, V>) oldNode.next;
            } else
                break;
        }
    }

    private void castTreeTableToListTable() {
        Node<K, V>[] newTable = new Node[table.length];
        var oldTable = table;
        this.table = newTable;
        for (int i = 0; i < table.length; i++) {
            castTreeToList((BinaryTreeNode<K, V>) oldTable[i]);
        }
    }

    private void castTreeToList(BinaryTreeNode<K, V> oldNode) {
        if (oldNode == null)
            return;

        put(oldNode.key, oldNode.value);

        if (oldNode.left != null) {
            castTreeToList(oldNode.left);
        }
        if (oldNode.right != null) {
            castTreeToList(oldNode.right);
        }
    }

    private void checkSizeArray() {
        if (size >= table.length * loadFactor)
            resize();
    }

    private void resize() {
        var oldTable = table;
        if (isListTable()) {
            resizeListTable((Node<K, V>[]) oldTable);
        } else {
            resizeTreeTable((BinaryTreeNode<K, V>[]) oldTable);
        }
    }

    private void resizeListTable(Node<K, V>[] oldTable) {
        this.table = new Node[oldTable.length * 2];
        for (Node<K, V> kvNode : oldTable) {
            if (kvNode != null) {
                var node = (Node<K, V>) kvNode;
                while (true) {
                    put(node.key, node.value);
                    if (node.next != null) {
                        node = (Node<K, V>) node.next;
                    } else
                        break;
                }
            }
        }

    }

    private void resizeTreeTable(BinaryTreeNode<K, V>[] oldTable) {
        this.table = new BinaryTreeNode[oldTable.length * 2];
        for (BinaryTreeNode<K, V> node : oldTable) {
            if (node != null) {
                putAllUseDFS(node);
            }
        }
    }

    private void putAllUseDFS(BinaryTreeNode<K, V> node) {
        put(node.key, node.value);
        if (node.left != null)
            putAllUseDFS(node.left);
        if (node.right != null)
            putAllUseDFS(node.right);
    }

    private int getIndex(K key) {
        return key == null
                ? 0
                : Objects.hashCode(key) & (table.length - 1);
    }

    @Override
    public V remove(Object key) {
        keys.remove(key);
        values.remove(get(key));
        try {
            if (isListTable()) {
                return removeFromListTable((K) key);
            } else {
                return removeFromTreeTable((K) key);
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        throw new IllegalArgumentException();
    }

    private V removeFromListTable(K key) {
        var index = getIndex(key);
        var nodeList = (Node<K, V>) table[index];
        var hash = Objects.hashCode(key);
        Node<K, V> prev = null;
        int count = 0;
        while (true) {
            if (nodeList.hash == hash) {
                if (nodeList.key.equals(key)) {
                    count++;
                    break;
                }
            } else if (nodeList.next != null) {
                prev = nodeList;
                nodeList = (Node<K, V>) nodeList.next;
            } else break;
        }
        if (count == 0)
            throw new IllegalArgumentException();
        V value;
        value = nodeList.value;
        if (prev == null) {
            table[index] = nodeList.next;
        } else {
            prev.next = nodeList.next;
        }
        return value;
    }

    private V removeFromTreeTable(K key) {
        int hash = Objects.hashCode(key);
        int index = getIndex(key);
        var node = (BinaryTreeNode<K, V>) table[index];
        BinaryTreeNode<K, V> prev = null;
        int count = 0;
        while (true) {
            var tmpNode = node;
            if (node.hash == hash) {
                if (node.key.equals(key)) {
                    count++;
                    break;
                }
            }
            if (node.hash > hash && node.left != null) {
                count++;
                prev = node;
                node = node.left;
            }
            if (node.hash < hash && node.right != null) {
                count++;
                prev = node;
                node = node.right;
            }
            if (node.equals(tmpNode))
                throw new NoSuchElementException();
        }
        if (count == 0) {
            throw new NoSuchElementException();
        }
        V value = node.value;
        if (prev == null) {
            table[index] = null;
            prevIsNullTreeNode(node);
        } else {
            prevIsNotNullTreeNode(node, prev);
        }
        return value;
    }

    private void prevIsNullTreeNode(BinaryTreeNode<K, V> node) {
        putAllUseDFS(node.right);
        putAllUseDFS(node.left);
    }

    private void prevIsNotNullTreeNode(BinaryTreeNode<K, V> node, BinaryTreeNode<K, V> prev) {
        if (prev.getRight().equals(node)) {
            prev.setRight(null);
        }
        if (prev.getLeft().equals(node)) {
            prev.setLeft(null);
        }
        if (node.left != null)
            putAllUseDFS(node.left);
        if (node.right != null)
            putAllUseDFS(node.right);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        var keys = m.keySet();
        for (K key : keys) {
            var value = m.get(key);
            put(key, value);
        }
    }

    @Override
    public void clear() {
        table = new Node[DEFAULT_INITIAL_CAPACITY];
        size = 0;
        keys.clear();
        values.clear();
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public Collection<V> values() {
        return values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();
        V value;
        Collections.addAll(set, table);
        return set;
    }

    @Override
    public MyHashMap<K, V> clone() {
        MyHashMap<K, V> clone = new MyHashMap<>(this.loadFactor, this.size, this.table);
        return clone;
    }

    private Node<K, V> getListNode(Object key) {
        try {
            var hash = Objects.hashCode(key);
            var node = (Node<K, V>) table[getIndex((K) key)];
            while (true) {
                if (node.hash == hash) {
                    if (node.key.equals(key))
                        return node;
                } else if (!(node.next == null)) {
                    node = (Node<K, V>) node.next;
                } else break;
            }
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        throw new NoSuchElementException();
    }

    private BinaryTreeNode<K, V> getTreeNode(Object key) {
        try {
            var hash = Objects.hashCode(key);
            var root = (BinaryTreeNode<K, V>) table[getIndex((K) key)];
            return dfs(root, (K) key, hash);
        } catch (ClassCastException exception) {
            exception.printStackTrace();
        }
        throw new NoSuchElementException();
    }

    private BinaryTreeNode<K, V> dfs(BinaryTreeNode<K, V> node, K key, int hash) {
        if (node.getHash() == hash) {
            if (node.getKey().equals(key))
                return node;
        }
        if (node.hash > hash && node.left != null) {
            return dfs(node.left, key, hash);
        }
        if (node.hash < hash && node.right != null) {
            return dfs(node.right, key, hash);
        }
        throw new NoSuchElementException();
    }

}
