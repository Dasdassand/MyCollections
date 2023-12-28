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
    private MyArrayList<TestEntity> testList;

    @Before
    public void setUp() {
        System.out.println(
                "Method starting"
        );
        testList = new MyArrayList<>();
        testList.add(new TestEntity(18, TestEntity.Sex.F));
        testList.add(new TestEntity(27, TestEntity.Sex.M));
        testList.add(new TestEntity(34, TestEntity.Sex.F));
        testList.add(new TestEntity(89, TestEntity.Sex.M));
        testList.add(new TestEntity(34, TestEntity.Sex.F));
    }

    /**
     * Немного тёмной магии с Reflection API
     */
    @Test
    public void testConstructors() {
        MyArrayList<TestEntity> one = new MyArrayList<>();
        MyArrayList<TestEntity> two = new MyArrayList<>(5);
        MyArrayList<TestEntity> three = new MyArrayList<>(
                new TestEntity[]
                        {new TestEntity(18, TestEntity.Sex.F),
                                new TestEntity(27, TestEntity.Sex.M),
                                new TestEntity(34, TestEntity.Sex.F)});
        var actual = new TestEntity[]
                {new TestEntity(18, TestEntity.Sex.F),
                        new TestEntity(27, TestEntity.Sex.M),
                        new TestEntity(34, TestEntity.Sex.F)};

        assertEquals(one.size(), 0);


        try {
            Field field = two.getClass().getDeclaredField("elementData");
            field.setAccessible(true);
            var data = (Object[]) field.get(two);
            Assert.assertEquals(data.length, 5);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        assertEquals(three.size(), 3);
        assertArrayEquals(three.toArray(), actual);
    }

    @Test
    public void testGetMethods() {
        TestEntity[] arrTest = testList.toArray(new TestEntity[5]);
        TestEntity[] actual = {
                new TestEntity(18, TestEntity.Sex.F),
                new TestEntity(27, TestEntity.Sex.M),
                new TestEntity(34, TestEntity.Sex.F),
                new TestEntity(89, TestEntity.Sex.M),
                new TestEntity(34, TestEntity.Sex.F)
        };
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

        assertEquals(testList.get(3), new TestEntity(89, TestEntity.Sex.M));

        assertEquals(testList.indexOf(new TestEntity(34, TestEntity.Sex.F)), 2);
        assertEquals(testList.lastIndexOf(new TestEntity(34, TestEntity.Sex.F)), 4);

        assertEquals(testList.subList(0, 2), new MyArrayList<>(new TestEntity[]{
                new TestEntity(18, TestEntity.Sex.F),
                new TestEntity(27, TestEntity.Sex.M),
                new TestEntity(34, TestEntity.Sex.F)
        }));
    }

    @Test
    public void testContainsMethods() {
        assertTrue(testList.contains(new TestEntity(34, TestEntity.Sex.F)));
        assertFalse(testList.contains(new TestEntity(37, TestEntity.Sex.F)));

        var exList = new MyArrayList<TestEntity>(new TestEntity[]{
                new TestEntity(18, TestEntity.Sex.F),
                new TestEntity(27, TestEntity.Sex.M),
                new TestEntity(34, TestEntity.Sex.F)
        });
        assertTrue(testList.containsAll(exList));

        exList.set(2, new TestEntity(90, TestEntity.Sex.M));
        assertFalse(testList.containsAll(exList));
    }

    /**
     * C - Create
     * U - Update
     * D - Delete
     */
    @Test
    public void testCUDMethods() {
        /**
         testList.add(new TestEntity(29, TestEntity.Sex.F));
         assertEquals(testList.get(5),new TestEntity(29, TestEntity.Sex.F));
         assertEquals(testList.size(),6);

         assertFalse(testList.remove(new TestEntity(99, TestEntity.Sex.M)));
         assertTrue(testList.remove(new TestEntity(18, TestEntity.Sex.F)));
         assertTrue(testList.remove(new TestEntity(34, TestEntity.Sex.F)));
         assertTrue(testList.remove(new TestEntity(29, TestEntity.Sex.F)));
         */

//        setUp();
        var tmp = new MyArrayList<>(new TestEntity[]{
                new TestEntity(77, TestEntity.Sex.M),
                new TestEntity(67, TestEntity.Sex.F),
                new TestEntity(57, TestEntity.Sex.M)
        });
//        testList.addAll(tmp);
//        assertTrue(testList.containsAll(tmp));
//        assertEquals(testList.size(),8);

        setUp();
        testList.addAll(2, tmp);
        assertTrue(testList.containsAll(tmp));
        assertEquals(testList.size(), 8);
        var subList = testList.subList(2, 4);
        assertEquals(subList, tmp);
        assertEquals(new TestEntity(34, TestEntity.Sex.F), testList.get(7));


        /**
         removeAll(Collection < ? > c)
         removeAll(Collection < ? > c)
         retainAll(Collection < ? > c)
         add( int index, E element)
         set( int index, E element)
         */
    }

    /**
     * SI - start index
     */
    @Test(expected = NoSuchElementException.class)
    public void testListIterator() {
        var listIteratorSI0 = testList.listIterator();
        var listIteratorSI4 = testList.listIterator(4);

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
        assertFalse(testList.contains(new TestEntity(18, TestEntity.Sex.F)));
        assertEquals(testList.size(), 4);

        listIteratorSI4.set(new TestEntity(67, TestEntity.Sex.M));
        assertFalse(testList.contains(new TestEntity(34, TestEntity.Sex.M)));
        assertTrue(testList.contains(new TestEntity(67, TestEntity.Sex.M)));

        listIteratorSI0.add(new TestEntity(23, TestEntity.Sex.F));
        assertTrue(testList.contains(new TestEntity(23, TestEntity.Sex.F)));
        assertEquals(testList.size(), 5);

        assertEquals(listIteratorSI0.next(), testList.get(0));
    }

}
