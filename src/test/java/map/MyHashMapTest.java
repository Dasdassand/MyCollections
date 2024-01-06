package map;

import entity.TestEntity;
import org.example.map.MyHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MyHashMapTest {
    MyHashMap<Integer, TestEntity> map = new MyHashMap<>();
    Random random = new Random();

    @BeforeEach
    public void setUp() {
        for (int i = 0; i < 1000; i++) {
            map.put(random.nextInt(1000), TestEntity.build());
        }
    }

    @Test
    public void testConstructors() {
        MyHashMap<Integer, Integer> mapOne = new MyHashMap<>();
        assertTrue(mapOne.isEmpty());
        assertEquals(0.75, mapOne.getLoadFactor(), 0.0);
        assertThrows(IllegalArgumentException.class, () -> new MyHashMap<Integer, Integer>(1, 10, new MyHashMap.Node[11]));
        mapOne = new MyHashMap<>(10, 0.5f);
        assertEquals(0.5f, mapOne.getLoadFactor(), 0.0);

        try {
            Field field = mapOne.getClass().getDeclaredField("table");
            field.setAccessible(true);
            var data = (Object[]) field.get(mapOne);
            assertEquals(10, data.length);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCRUDMethods() {
        var keys = map.keySet();
        var values = map.values();
        var entrySet = map.entrySet();
        assertEquals(entrySet.size(), keys.size());
        for (Integer key :
                keys) {
            assertTrue(values.contains(map.get(key)));
        }
        var testEntity = new TestEntity(18, TestEntity.Sex.M);
        map.put(100000, testEntity);
        assertEquals(testEntity, map.get(100000));
        map.remove(100000);
        assertFalse(map.containsKey(100000));
        Map<Integer, TestEntity> map1 = new HashMap<>();
        for (int i = 0; i < random.nextInt(100); i++) {
            map.put(random.nextInt(100), TestEntity.build());
        }
        map.putAll(map1);
        keys = map1.keySet();
        for (Integer key :
                keys) {
            assertEquals(map.get(key), map1.get(key));
        }

        map.clear();
        assertTrue(map.isEmpty());
        assertTrue(map.keySet().isEmpty());
        assertTrue(map.values().isEmpty());
        assertTrue(map.entrySet().isEmpty());
    }

    @Test
    public void testContainsMethods() {
        var testEntity = new TestEntity(18, TestEntity.Sex.M);
        map.put(100000, testEntity);
        assertTrue(map.containsKey(100000));
        assertTrue(map.containsValue(testEntity));
        map.remove(100000);
        assertFalse(map.containsKey(100000));
        assertFalse(map.containsValue(testEntity));
    }
}
