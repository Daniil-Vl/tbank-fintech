package org.example.lesson_3;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    static void firstTask() {
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

    static void secondTask() {
        // 1
        Stream<Integer> stream = IntStream.range(1, 11).boxed();
        CustomLinkedList<Integer> list = stream.collect(
                CustomLinkedList::new, // Supplier of initial collection
                CustomLinkedList::add, // Accumulator
                CustomLinkedList::addAll // Two collection combiner
        );
        System.out.println(list);

        // 2
        stream = IntStream.range(1, 11).boxed();
        int sum = stream.reduce(0, Integer::sum);
        System.out.println(sum);
    }

    public static void main(String[] args) {
        secondTask();
    }
}
