package core.basesyntax;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private int capacity;
    private float threshold;
    private int size;
    private Node<K, V>[] table;

    public MyHashMap() {
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
        capacity = DEFAULT_CAPACITY;
        threshold = calculateThreshold();
    }

    @Override
    public void put(K key, V value) {
        ensureCapacity();
        int hash = key == null ? 0 : key.hashCode();
        Node<K, V> node = new Node<>(key, value, hash);
        boolean isAdded = addToBucket(node, table);
        if (isAdded) {
            size++;
        }
    }

    @Override
    public V getValue(K key) {
        for (Node<K, V> node : table) {
            if (node != null) {
                if (node.key == key || (node.key != null && node.key.equals(key))) {
                    return node.value;
                }
                Node<K, V> foundNode = getNodeFromCollision(node, key);
                if (foundNode != null) {
                    return foundNode.value;
                }
            }
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    private boolean addToBucket(Node<K, V> node, Node<K, V>[] table) {
        int index = node.key == null ? 0 : Math.abs(node.hash % capacity);
        if (table[index] == null) {
            table[index] = node;
            return true;
        }

        if (table[index].key == node.key
                || (table[index].key != null && table[index].key.equals(node.key))) {
            table[index].value = node.value;
            return false;
        }

        return addToBucketWithCollision(table[index], node);
    }

    private boolean addToBucketWithCollision(Node<K, V> backetNode, Node<K, V> node) {
        if (backetNode.next == null) {
            backetNode.next = node;
            return true;
        }
        if (backetNode.next.key == node.key
                || (backetNode.next.key != null && backetNode.next.key.equals(node.key))) {
            backetNode.next.value = node.value;
            return false;
        }
        return addToBucketWithCollision(backetNode.next, node);
    }

    private void ensureCapacity() {
        if (size >= threshold) {
            resize();
        }
    }

    private void resize() {
        capacity = capacity * 2;
        threshold = calculateThreshold();
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[capacity];
        for (Node<K, V> node : table) {
            if (node != null) {
                checkCollisionAndAddToBucket(node, newTable);
                addToBucket(node, newTable);
            }
        }
        table = newTable;
    }

    private void checkCollisionAndAddToBucket(Node<K, V> node, Node<K, V>[] table) {
        if (node.next != null) {
            checkCollisionAndAddToBucket(node.next, table);
            addToBucket(node.next, table);
            node.next = null;
        }
    }

    private float calculateThreshold() {
        return capacity * LOAD_FACTOR;
    }

    private Node<K, V> getNodeFromCollision(Node<K, V> node, K key) {
        if (node.next != null) {
            if (node.next.key == key || (node.next.key != null && node.next.key.equals(key))) {
                return node.next;
            }
            return getNodeFromCollision(node.next, key);
        }
        return null;
    }

    private static class Node<K, V> {
        final K key;
        V value;
        final int hash;
        Node<K, V> next;

        public Node(K key, V value, int hash) {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }
    }
}
