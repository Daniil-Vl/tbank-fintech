package org.example.lesson_3;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();

        // 1
        list.add(1);
        System.out.println(list);
        System.out.println("---------------------");

        // 2
        System.out.println(list.get(0));
        System.out.println(list);
        System.out.println("---------------------");

        // 3
        list.remove(0);
        System.out.println(list);
        System.out.println("---------------------");

        // 4
        System.out.println(list.contains(1));
        System.out.println(list);
        System.out.println("---------------------");

        // 5
        list.addAll(List.of(1, 2, 3));
        System.out.println(list);
        System.out.println("---------------------");
    }
}
