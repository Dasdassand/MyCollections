package org.example.map;

import java.util.Map;

 interface MyEntry<K, V> extends Map.Entry<K, V> {
    int getHashCode();
}
