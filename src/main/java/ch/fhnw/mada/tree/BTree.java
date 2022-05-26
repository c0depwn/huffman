package ch.fhnw.mada.tree;

/**
 * A very simple generic Binary Tree implementation.
 * @param <T>
 */
public class BTree<T> {
    private Node<T> root;
    private Node<T> current;

    public BTree(Node<T> root) {
        this.root = root;
        this.reset();
    }

    /**
     * Search the tree recursively by providing an int array which specifies how the tree is traversed.
     * A 0 indicates that the left node should be traversed next and a 1 indicates that the right node should be traversed next.
     * Example: [0,1,0,1] -> left, right, left, right
     * @param bits
     * @return Node<T>
     */
    public Node<T> search(int[] bits) {
        return this.searchRecursively(this.root, bits, 0);
    }

    /**
     * Insert a value into the tree at the path specified by the supplied int array.
     * A 0 indicates that the left node should be traversed next and a 1 indicates that the right node should be traversed next.
     * When the destination specified by the bits array is reached the value will be set to that final node.
     * Example: [0,1,0,1] -> left, right, left, right.
     * @param bits
     * @param value
     */
    public void insert(int[] bits, T value) {
        this.insertRecursively(this.root, bits, value, 0);
    }

    public Node<T> step(int bit) {
        if (bit == 0) {
            this.current = current.getLeft();
            return this.current;
        }
        if (bit == 1) {
            this.current = current.getRight();
            return this.current;
        }
        return null;
    }

    public boolean hasNext() {
        return this.current.getData() == null;
    }

    public void reset() {
        this.current = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    private void insertRecursively(Node<T> node, int[] bits, T value, int index) {
        if (index == bits.length) {
            node.setData(value);
            return;
        }
        var currentBit = bits[index];
        if (currentBit == 0) {
            if (node.getLeft() == null) node.addLeft(new Node<>());
            insertRecursively(node.getLeft(), bits, value, index + 1);
            return;
        }
        if (currentBit == 1) {
            if (node.getRight() == null) node.addRight(new Node<>());
            insertRecursively(node.getRight(), bits, value, index + 1);
            return;
        }
    }

    private Node<T> searchRecursively(Node<T> node, int[] bits, int index) {
        if (bits.length == index) return node;

        var currentBit = bits[index];
        if (currentBit == 0) {
            return searchRecursively(node.getLeft(), bits, index + 1);
        }
        if (currentBit == 1) {
            return searchRecursively(node.getRight(), bits, index + 1);
        }

        return null;
    }
}
