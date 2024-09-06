package org.example.lesson_3;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

public class CustomLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    public void add(T item) {
        Node<T> node = new Node<>(item);
        node.previous = tail;
        size++;

        if (head == null) {
            head = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    public T get(int index) {
        Node<T> node = findNode(index);
        return node.value;
    }

    public void remove(int index) {
        Node<T> node = findNode(index);
        size--;

        if (node == head) {
            head = head.next;
            return;
        }

        Node<T> previousNode = node.previous;
        Node<T> nextNode = node.next;

        if (previousNode != null) {
            previousNode.next = nextNode;
        }
        if (nextNode != null) {
            nextNode.previous = previousNode;
        }
    }

    public boolean contains(T item) {
        Node<T> node = head;
        while (node != null) {
            if (node.value == item) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    public void addAll(Collection<T> items) {
        for (T item : items) {
            add(item);
        }
    }

    public void addAll(CustomLinkedList<T> items) {
        Node<T> currentNode = items.head;
        while (currentNode != null) {
            add(currentNode.value);
            currentNode = currentNode.next;
        }
    }

    private Node<T> findNode(int index) {
        Node<T> node = head;

        for (int i = 0; i < index && node != null; i++) {
            node = node.next;
        }

        if (node == null) {
            throw new IndexOutOfBoundsException("Index = %d, Size = %d".formatted(index, size));
        }

        return node;
    }

    @Override
    public String toString() {
        if (head == null) {
            return "{}";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        Node<T> node = head;
        while (node.next != null) {
            builder.append(node.value).append("<->");
            node = node.next;
        }
        builder.append(node.value).append("}");

        return builder.toString();
    }

    public int size() {
        return size;
    }

    @Getter
    @Setter
    private static class Node<T> {
        T value;
        Node<T> next;
        Node<T> previous;

        public Node(T value) {
            this.value = value;
        }
    }

}
