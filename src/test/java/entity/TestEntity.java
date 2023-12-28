package entity;

import java.util.Objects;
import java.util.Random;

public class TestEntity {
    private final int age;
    private final Sex sex;
    private static final Random random = new Random();

    public enum Sex {
        M, F;

        public static Sex randomSex() {
            Sex[] directions = values();
            return directions[random.nextInt(directions.length)];
        }

    }

    public TestEntity(int age, Sex sex) {
        this.age = age;
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public Sex getSex() {
        return sex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestEntity that)) return false;
        return age == that.age && sex == that.sex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, sex);
    }

    public static TestEntity build() {
        return new TestEntity(
                random.nextInt(99),
                Sex.randomSex()
        );
    }
}
