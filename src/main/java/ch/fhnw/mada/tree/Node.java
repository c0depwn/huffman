package ch.fhnw.mada.tree;

/**
 * A generic Node used to implement a tree where each node can have 2 children and a generic data field.
 * @param <T>
 */
public class Node<T> {
    private T data;
    private Node<T> left;
    private Node<T> right;

    public Node() {
    }

    public Node(T data) {
        this.data = data;
    }

    public void addLeft(Node<T> child) {
        this.left = child;
    }
    public void addRight(Node<T> child) {
        this.right = child;
    }

    public Node<T> getLeft() {
        return left;
    }

    public Node<T> getRight() {
        return right;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isLeaf() {
        return this.data != null;
    }
}
