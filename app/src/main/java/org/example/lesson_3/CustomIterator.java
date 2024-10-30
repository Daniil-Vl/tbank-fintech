package org.example.lesson_3;

import java.util.Iterator;
import java.util.function.Consumer;

public class CustomIterator<T> implements Iterator<T> {

    private CustomLinkedList.Node<T> currNode;

    CustomIterator(CustomLinkedList.Node<T> currNode) {
        this.currNode = currNode;
    }

    @Override
    public boolean hasNext() {
        return currNode != null;
    }

    @Override
    public T next() {
        T nextValue = currNode.getValue();
        currNode = currNode.getNext();
        return nextValue;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        while (hasNext()) {
            action.accept(next());
        }
    }
}
