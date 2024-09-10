package org.example.lesson_3;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
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
        // With collect
        Stream<Integer> stream = IntStream.range(1, 11).boxed();
        CustomLinkedList<Integer> list1 = stream.collect(
                CustomLinkedList::new, // Supplier of initial collection
                CustomLinkedList::add, // Accumulator
                CustomLinkedList::addAll // Two collection combiner
        );
        System.out.println(list1);

        // With reduce
        stream = IntStream.range(1, 11).boxed();
        CustomLinkedList<Integer> list2 = stream.reduce(
                new CustomLinkedList<>(),
                new BiFunction<CustomLinkedList<Integer>, Integer, CustomLinkedList<Integer>>() {
                    @Override
                    public CustomLinkedList<Integer> apply(CustomLinkedList<Integer> list, Integer integer) {
                        list.add(integer);
                        return list;
                    }
                },
                new BinaryOperator<CustomLinkedList<Integer>>() {
                    @Override
                    public CustomLinkedList<Integer> apply(CustomLinkedList<Integer> firstList, CustomLinkedList<Integer> secondList) {
                        firstList.addAll(secondList);
                        return firstList;
                    }
                }
        );
        System.out.println(list2);
    }

    public static void main(String[] args) {
        firstTask();
        System.out.println("\n-------------------------------------------------\n");
        secondTask();
    }
}
