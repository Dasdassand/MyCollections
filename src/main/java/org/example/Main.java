package org.example;

import org.example.map.MyHashMap;

import java.util.Random;

public class Main{
    public static void main(String[] args) {
        MyHashMap<Integer,Integer> map = new MyHashMap<>();
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            map.put(random.nextInt(1000), random.nextInt(100000));
        }
        System.out.println(map.getActualTypeTable());

    }
}