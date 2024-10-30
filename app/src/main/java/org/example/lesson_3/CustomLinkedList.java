package org.example.lesson_3;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Iterator;

public class CustomLinkedList<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    public void add(T item) {
        Node<T> node = new Node<>(item);

        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.previous = tail;
        }

        tail = node;

        size++;
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
            if (head != null)
                head.previous = null;
            return;
        }

        if (node == tail) {
            tail = tail.previous;
            if (tail != null)
                tail.next = null;
            return;
        }

        node.previous.next = node.next;
        node.next.previous = node.previous;
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
            throw new IndexOutOfBoundsException(String.format("Index = %d, Size = %d", index, size));
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

    public Iterator<T> iterator() {
        return new CustomIterator<>(this.head);
    }

    @Getter
    @Setter
    static class Node<T> {
        T value;
        Node<T> next;
        Node<T> previous;

        public Node(T value) {
            this.value = value;
        }
    }

}
