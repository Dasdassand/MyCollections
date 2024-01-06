package org.example.map;

import java.io.Serializable;
import java.util.*;

public class MyHashMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    public static class Node<K, V> implements MyEntry<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

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

        @Override
        public int getHashCode() {
            return hash;
        }
    }

    public static class BinaryTreeNode<K, V> implements MyEntry<K, V> {
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

        @Override
        public int getHashCode() {
            return hash;
        }
    }

    private MyEntry<K, V>[] table;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private float loadFactor = 0.75f;
    private int size;
    private final Set<K> keys = new HashSet<>();
    private final Collection<V> values = new ArrayList<>();
    private boolean flagCast = false;

    public MyHashMap() {
        this.table = new Node[DEFAULT_INITIAL_CAPACITY];
    }

    public MyHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.table = new Node[initialCapacity];
    }

    public MyHashMap(float loadFactor, int size, MyEntry<K, V>[] table) {
        this.loadFactor = loadFactor;
        this.size = size;
        this.table = new MyEntry[table.length];
        if (!checkConstructorArgumentTable(table, size))
            throw new IllegalArgumentException();
        for (MyEntry e : table) {
            if (e != null) {
                put((K) e.getKey(), (V) e.getValue());
                keys.add((K) e.getKey());
                values.add((V) e.getValue());
            }
        }
    }

    private boolean checkConstructorArgumentTable(MyEntry<K, V>[] table, int size) {
        return size == getCountElementFromTable(table);
    }

    private int getCountElementFromTable(MyEntry<K, V>[] table) {
        if (isListTable())
            return getCountElementFromListTable(table);
        else
            return getCountElementFromTreeTable(table);
    }

    private int getCountElementFromListTable(MyEntry<K, V>[] table) {
        int count = 0;
        for (MyEntry<K, V> kvMyEntry : table) {
            if (kvMyEntry != null) {
                var node = (Node<K, V>) kvMyEntry;
                while (true) {
                    count++;
                    if (node.next != null) {
                        node = (Node<K, V>) node.next;
                    } else break;
                }
            }
        }
        return count;
    }

    private int getCountElementFromTreeTable(MyEntry<K, V>[] table) {
        int count = 0;
        for (MyEntry<K, V> kvMyEntry : table) {
            if (kvMyEntry != null) {
                var node = (BinaryTreeNode<K, V>) kvMyEntry;
                count = getCountElement(node);
            }
        }
        return count;
    }

    private int getCountElement(BinaryTreeNode<K, V> node) {
        if (node == null) {
            return 0;
        } else {
            int leftCount = getCountElement(node.left);
            int rightCount = getCountElement(node.right);
            return leftCount + rightCount + 1;
        }
    }

    public float getLoadFactor() {
        return loadFactor;
    }

    public int getSize() {
        return size;
    }

    private boolean isListTable() {
        return table.getClass().equals(Node[].class);
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
            if (node == null)
                return false;
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
            if (node == null) {
                return false;
            }
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
       return false;
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
        int index = getIndex(key), count = 0;
        if (table[index] == null) {
            table[index] = new Node<>(Objects.hashCode(key), key, value, null);
            size++;
            checkSizeArray();
            return null;
        } else {
            var node = (Node<K, V>) table[index];
            boolean flagPut = true;
            var hash = Objects.hashCode(key);
            while (true) {
                count++;
                if (node.hash == hash) {
                    if (node.key.equals(key)) {
                        flagPut = false;
                        node.value = value;
                    }
                }
                if (node.next != null) {
                    node = node.next;
                } else {
                    break;
                }
            }
            if (flagPut) {
                node.next = new Node<>(hash, key, value, null);
                size++;
                count++;
            }
            checkSizeBucket(count);
            checkSizeArray();
            return value;
        }
    }


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

    private void checkSizeBucket(int maxCount) {
        if (maxCount >= 8 && !flagCast) {
            castListTableToTreeTable();
            flagCast = true;
        } else if (maxCount <= 6 && flagCast) {
            castTreeTableToListTable();
            flagCast = false;
        }
    }

    private void castListTableToTreeTable() {
        BinaryTreeNode<K, V>[] newTable = new BinaryTreeNode[table.length];
        var oldTable = table;
        this.table = newTable;
        for (MyEntry<K, V> kvMyEntry : oldTable) {
            castListToTree((Node<K, V>) kvMyEntry);
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
        checkSizeBucket(getMaxElementFromBuckets());
    }

    private int getMaxElementFromBuckets() {
        int maxCount = 0, count = 0;
        if (isListTable()) {
            for (int i = 0; i < table.length; i++) {
                if (count > maxCount) {
                    maxCount = count;
                    count = 0;
                }
                if (table[i] != null) {
                    var node = (Node<K, V>) table[i];
                    while (true) {
                        count++;
                        if (node.next != null) {
                            node = node.next;
                        } else
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < table.length; i++) {
                if (table[i] != null) {
                    count = getCountElement((BinaryTreeNode<K, V>) table[i]);
                }
                if (count > maxCount) {
                    maxCount = count;
                    count = 0;
                }
            }
        }
        return maxCount;
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
        if (node != null) {
            put(node.key, node.value);
            if (node.left != null)
                putAllUseDFS(node.left);
            if (node.right != null)
                putAllUseDFS(node.right);
        }
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
        checkSizeBucket(getCountElement((BinaryTreeNode<K, V>) table[index]));
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
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> set = new HashSet<>();
        if (isListTable()) {
            for (K key :
                    keys) {
                set.add(getListNode(key));
            }
        } else {
            for (K key :
                    keys) {
                set.add(getTreeNode(key));
            }
        }
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

    public String getActualTypeTable() {
        return table.getClass().getTypeName();
    }

}
