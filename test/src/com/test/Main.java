package com.test;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        ArrayList<Integer> a = new ArrayList<>(20);
        a.add(3);
        a.add(5);

        a.remove((Integer) 3);

        System.out.println(20 - a.size());
    }
}
