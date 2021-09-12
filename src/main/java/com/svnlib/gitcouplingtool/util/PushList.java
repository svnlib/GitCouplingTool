package com.svnlib.gitcouplingtool.util;

import java.util.LinkedList;
import java.util.List;

/**
 * A special linked list that is able to keep a given number of entries order by {@link Comparable}. If the added item
 * increases the size over the given max size, the last and smallest element gets removed.
 *
 * @param <T> the type of element to store. Must implement {@link Comparable}.
 */
public class PushList<T extends Comparable<T>> {

    private final long maxSize;
    private       long size = 0;
    private       Node first;
    private       Node last;

    public PushList(final long maxSize) {this.maxSize = maxSize;}

    /**
     * Adds an element to the list and sorts it in.
     *
     * @param elem the element to add.
     */
    public void add(final T elem) {
        this.size++;

        final Node newNode = new Node(elem);

        if (this.first == null) {
            this.first = newNode;
            this.last = newNode;
        } else {
            Node node = this.first;
            while (node != null) {
                if (newNode.value.compareTo(node.value) >= 0) {
                    break;
                }
                node = node.next;
            }

            newNode.prev = node == null ? this.last : node.prev;
            newNode.next = node;
            if (newNode.hasNext()) {
                newNode.next.prev = newNode;
            } else {
                this.last = newNode;
            }
            if (newNode.hasPrev()) {
                newNode.prev.next = newNode;
            } else {
                this.first = newNode;
            }
        }

        // delete the last elem if the list is full
        if (this.size > this.maxSize) {
            if (this.last == this.first) {
                this.first = null;
                this.last = null;
            } else {
                this.last = this.last.prev;
                this.last.next = null;
            }
            this.size--;
        }
    }

    /**
     * Converts all elements to a regular {@link LinkedList}.
     *
     * @return the {@link LinkedList} of all elements
     */
    public List<T> toList() {
        final LinkedList<T> list = new LinkedList<>();

        Node node = this.first;
        while (node != null) {
            list.add(node.value);
            node = node.next;
        }

        return list;
    }

    private class Node {

        final T value;
        Node next;
        Node prev;

        boolean hasNext() {
            return this.next != null;
        }

        boolean hasPrev() {
            return this.prev != null;
        }

        private Node(final T value) {this.value = value;}

        @Override
        public String toString() {
            return this.value.toString();
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PushList{");

        Node n = this.first;
        while (n != null) {
            sb.append(n).append(",");
            n = n.next;
        }
        sb.deleteCharAt(sb.lastIndexOf(","));

        return sb.append("}").toString();
    }

}
