package map;

import entity.TestEntity;
import org.example.map.MyHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
}
