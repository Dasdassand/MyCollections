package list;

import entity.TestEntity;
import org.example.list.MyArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

public class MyArrayListTest {
    private MyArrayList<TestEntity> testList = new MyArrayList<>();

    @Before
    public void setUp() throws Exception {
        testList.add(new TestEntity(18, TestEntity.Sex.F));
        testList.add(new TestEntity(27, TestEntity.Sex.M));
        testList.add(new TestEntity(34, TestEntity.Sex.F));
        testList.add(new TestEntity(89, TestEntity.Sex.M));
        testList.add(new TestEntity(34, TestEntity.Sex.M));
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

        Assert.assertEquals(one.size(), 0);


        try {
            Field field = two.getClass().getDeclaredField("elementData");
            field.setAccessible(true);
            var data = (Object[]) field.get(two);
            Assert.assertEquals(data.length, 5);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }


        Assert.assertEquals(three.size(), 3);
        Assert.assertArrayEquals(three.toArray(), actual);
    }

}
