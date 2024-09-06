package org.example.lesson_3;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomLinkedListTest {
    @Test
    void addIntoEmptyList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertEquals(0, list.size());

        list.add(1);
        assertEquals(1, list.size());
        assertEquals(1, list.get(0));
    }

    @Test
    void addIntoNonEmptyList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();

        list.add(1);
        assertEquals(1, list.size());

        list.add(2);
        assertEquals(2, list.size());
        assertEquals(2, list.get(1));
    }

    @Test
    void getExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        assertEquals(2, list.get(1));
    }

    @Test
    void getNonExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void getInEmptyList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    }

    @Test
    void removeExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        list.remove(0);
        assertFalse(list.contains(1));
        assertEquals(1, list.size());
    }

    @Test
    void removeNonExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

    @Test
    void removeInEmptyList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
    }

    @Test
    void containsWithExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        assertTrue(list.contains(1));
    }

    @Test
    void containsWithNonExistingElement() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.add(1);
        list.add(2);
        assertFalse(list.contains(3));
    }

    @Test
    void containsInEmptyList() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        assertFalse(list.contains(1));
    }

    @Test
    void addAll() {
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addAll(List.of(1, 2, 3));
        assertEquals(3, list.size());
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }
}