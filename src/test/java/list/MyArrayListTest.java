package list;

import entity.TestEntity;
import org.example.list.MyArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class MyArrayListTest {
    private final MyArrayList<TestEntity> testList = new MyArrayList<>(5);

    @Before
    public void setUp() {
        testList.clear();
        for (int i = 0; i < 5; i++) {
            testList.add(
                    TestEntity.build()
            );
        }
    }

    /**
     * Немного тёмной магии с Reflection API
     */
    @Test
    public void testConstructors() {
        MyArrayList<TestEntity> one = new MyArrayList<>();
        MyArrayList<TestEntity> two = new MyArrayList<>(5);
        MyArrayList<TestEntity> three = new MyArrayList<>(
                testList.subList(0, 3).toArray(new TestEntity[4])
        );

        assertEquals(0, one.size());


        try {
            Field field = two.getClass().getDeclaredField("elementData");
            field.setAccessible(true);
            var data = (Object[]) field.get(two);
            Assert.assertEquals(5, data.length);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        assertEquals(4, three.size());
        assertEquals(three, testList.subList(0, 3));
    }

    @Test
    public void testGetMethods() {
        TestEntity[] actual = testList.toArray(new TestEntity[5]);
        TestEntity[] arrTest = new TestEntity[5];
        for (int i = 0; i < actual.length; i++) {
            arrTest[i] = testList.get(i);
        }
        assertArrayEquals(arrTest, actual);

        var arrayObj = testList.toArray();
        int i = 0;
        for (Object o :
                arrayObj) {
            arrTest[i] = (TestEntity) o;
            i++;
        }
        assertArrayEquals(arrTest, actual);

        assertEquals(testList.size(), 5);
        assertFalse(testList.isEmpty());

        assertEquals(arrTest[3], testList.get(3));

        assertEquals(testList.indexOf(testList.get(4)), 4);
        testList.set(2, testList.get(4));
        assertEquals(testList.lastIndexOf(testList.get(4)), 4);

        assertEquals(testList.subList(0, 2), new MyArrayList<>(new TestEntity[]{
                testList.get(0),
                testList.get(1),
                testList.get(2)
        }));
    }

    @Test
    public void testContainsMethods() {
        assertTrue(testList.contains(testList.get(1)));
        assertFalse(testList.contains(new TestEntity(100, TestEntity.Sex.F)));

        var exList = new MyArrayList<>(new TestEntity[]{
                testList.get(1),
                testList.get(3),
                testList.get(0)
        });
        assertTrue(testList.containsAll(exList));

        exList.set(2, new TestEntity(101, TestEntity.Sex.M));
        assertFalse(testList.containsAll(exList));
    }

    /**
     * C - Create
     * U - Update
     * D - Delete
     */
    @Test
    public void testCUDMethods() {
        var newEntity = TestEntity.build();
        testList.add(newEntity);
        assertEquals(testList.get(5), newEntity);
        assertEquals(testList.size(), 6);
        assertFalse(testList.remove(new TestEntity(100, TestEntity.Sex.M)));
        assertTrue(testList.remove(testList.get(0)));
        assertTrue(testList.remove(testList.get(4)));
        assertTrue(testList.remove(testList.get(3)));

        var tmp = new MyArrayList<>(new TestEntity[]{
                TestEntity.build(),
                TestEntity.build(),
                TestEntity.build()
        });
        testList.addAll(tmp);
        assertTrue(testList.containsAll(tmp));
        assertEquals(testList.size(), 6);

        testList.addAll(2, tmp);
        var oldEntity = testList.get(8);
        assertTrue(testList.containsAll(tmp));
        assertEquals(testList.size(), 9);
        var subList = testList.subList(2, 4);
        assertEquals(subList, tmp);
        assertEquals(oldEntity, testList.get(8));

        assertTrue(testList.removeAll(tmp));
        assertTrue(testList.removeAll(tmp));
        assertFalse(testList.containsAll(tmp));

        testList.addAll(tmp);
        tmp.remove(1);
        tmp.add(testList.get(1));
        newEntity = TestEntity.build();
        tmp.add(newEntity);
        assertTrue(testList.retainAll(tmp));

        tmp.remove(newEntity);
        assertEquals(tmp, testList);

        newEntity = TestEntity.build();
        testList.add(0, newEntity);
        assertEquals(newEntity, testList.get(0));

        newEntity = TestEntity.build();
        testList.set(2, newEntity);
        assertEquals(newEntity, testList.get(2));
    }

    /**
     * SI - start index
     */
    @Test(expected = NoSuchElementException.class)
    public void testListIterator() {
        var listIteratorSI0 = testList.listIterator();
        var listIteratorSI4 = testList.listIterator(4);
        TestEntity e0 = testList.get(0);

        assertTrue(listIteratorSI0.hasNext());
        assertTrue(listIteratorSI4.hasNext());
        assertTrue(listIteratorSI4.hasPrevious());
        assertFalse(listIteratorSI0.hasPrevious());
        assertEquals(listIteratorSI0.nextIndex(), 1);
        assertEquals(listIteratorSI4.nextIndex(), 5);
        assertEquals(listIteratorSI0.previousIndex(), -1);
        assertEquals(listIteratorSI4.previousIndex(), 3);
        listIteratorSI0.previous();
        assertEquals(testList.listIterator().getClass(), testList.iterator().getClass());
        assertEquals(listIteratorSI4.previous(), testList.get(1));

        listIteratorSI0.remove();
        assertFalse(testList.contains(e0));
        assertEquals(testList.size(), 4);

        listIteratorSI4.set(new TestEntity(67, TestEntity.Sex.M));
        assertTrue(testList.contains(new TestEntity(67, TestEntity.Sex.M)));

        listIteratorSI0.add(new TestEntity(23, TestEntity.Sex.F));
        assertTrue(testList.contains(new TestEntity(23, TestEntity.Sex.F)));
        assertEquals(testList.size(), 5);

        assertEquals(listIteratorSI0.next(), testList.get(0));
    }

}
