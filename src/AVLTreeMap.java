/**
 * Class that implements an AVL tree which implements the MyMap interface.
 * @author Marissa Crevecoeur; mac2528
 * @version 1.0 October 28, 2022
 */
public class AVLTreeMap<K extends Comparable<K>, V> extends BSTMap<K, V>
        implements MyMap<K, V> {
    private static final int ALLOWED_IMBALANCE = 1;

    /**
     * Creates an empty AVL tree map.
     */
    public AVLTreeMap() { }

    public AVLTreeMap(Pair<K, V>[] elements) {
        insertElements(elements);
    }

    /**
     * Creates an AVL tree map of the given key-value pairs. If
     * sorted is true, a balanced tree will be created via a divide-and-conquer
     * approach. If sorted is false, the pairs will be inserted in the order
     * they are received, and the tree will be rotated to maintain the AVL tree
     * balance property.
     * @param elements an array of key-value pairs
     */
    public AVLTreeMap(Pair<K, V>[] elements, boolean sorted) {
        if (!sorted) {
            insertElements(elements);
        } else {
            root = createBST(elements, 0, elements.length - 1);
        }
    }

    /**
     * Recursively constructs a balanced binary search tree by inserting the
     * elements via a divide-snd-conquer approach. The middle element in the
     * array becomes the root. The middle of the left half becomes the root's
     * left child. The middle element of the right half becomes the root's right
     * child. This process continues until low > high, at which point the
     * method returns a null Node.
     * @param pairs an array of <K, V> pairs sorted by key
     * @param low   the low index of the array of elements
     * @param high  the high index of the array of elements
     * @return      the root of the balanced tree of pairs
     */
    protected Node<K, V> createBST(Pair<K, V>[] pairs, int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = low + (high - low) / 2;
        Pair<K, V> pair = pairs[mid];
        Node<K, V> parent = new Node<>(pair.key, pair.value);
        size++;
        parent.left = createBST(pairs, low, mid - 1);
        if (parent.left != null) {
            parent.left.parent = parent;
        }
        parent.right = createBST(pairs, mid + 1, high);
        if (parent.right != null) {
            parent.right.parent = parent;
        }
        // This line is critical for being able to add additional nodes or to
        // remove nodes. Forgetting this line leads to incorrectly balanced
        // trees.
        parent.height =
                Math.max(avlHeight(parent.left), avlHeight(parent.right)) + 1;
        return parent;
    }

    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for the key, the old value is replaced
     * by the specified value.
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    @Override
    public V put(K key, V value) {
        NodeOldValuePair nvp = new NodeOldValuePair(null, null);
        nvp = insertAndBalance(key, value, root, nvp);
        return nvp.oldValue;
    }

    /**
     * Calls a recursive method remove to delete the mapping for a key from this map if it is present.
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no
     *         mapping for key
     */
    public V remove(K key) {
        // Replace the line with the code required for proper removal from an
        // AVL tree. This task is extra credit.
        NodeOldValuePair node = new NodeOldValuePair(null, null);
        return remove(key, root, node).oldValue;
    }

    private NodeOldValuePair remove(K key, Node<K,V> t, NodeOldValuePair nvp){
        if (t == null) {
            return nvp;
        }
        int comparison = key.compareTo(t.key);
        if(comparison < 0){
            nvp = remove(key, t.left , nvp);
        }
        else if(comparison > 0){
            nvp = remove(key, t.right, nvp);
        }
        else if(t.left != null && t.right != null) {
            Node<K,V> hold = treeMinimum(t.right);
            if(nvp != null) nvp.oldValue = t.value;
            t.key = hold.key;
            t.value = hold.value;
            remove(hold.key, t.right, nvp);
        }
        else {
            if(t == root && (t.left == null && t.right == null)) root = null;
            if(t.left != null){
                if(nvp.oldValue == null && key.compareTo(t.key) == 0) nvp.oldValue = t.value;
                t.key = t.left.key;
                t.value = t.left.value;
                remove(t.left.key, t.left, nvp);
            }
            else if(t.right != null){
                if(nvp.oldValue == null && key.compareTo(t.key) == 0) nvp.oldValue = t.value;
                t.key = t.right.key;
                t.value = t.right.value;
                remove(t.right.key, t.right, nvp);
            }
            else{
                if(t.parent != null && t.parent.right == t) t.parent.right = null;
                if(t.parent != null && t.parent.left == t) t.parent.left = null;
                if(nvp.oldValue == null) nvp.oldValue = t.value;
                t.parent = null;
                --size;
            }
        }
        Node<K, V> n = balance(t);
        nvp.node = n;
        return nvp;
    }

    private NodeOldValuePair insertAndBalance(K key, V value, Node<K, V> t, NodeOldValuePair nvp) {
        if (t == null) {
            size++;
            nvp.node = new Node<K, V>(key, value);
            if (root == null) {
                root = nvp.node;
            }
            return nvp;
        }
        int comparison = key.compareTo(t.key);
        // TODO
        // Complete the missing section of code here.
        if(comparison < 0){
            nvp = insertAndBalance(key, value, t.left , nvp);
            t.left = nvp.node;
            nvp.node.parent = t;
        }
        else if(comparison > 0) {
            nvp = insertAndBalance(key, value, t.right, nvp);
            t.right = nvp.node;
            nvp.node.parent = t;
        }
        else {
            nvp.oldValue = t.value;
            t.value = value;
        }
        Node<K, V> n = balance(t);
        nvp.node = n;
        return nvp;
    }

    private Node<K, V> balance(Node<K, V> t) {
        // TODO
        if(t == null){
            return t;
        }
        if(height(t.left) - height(t.right) > ALLOWED_IMBALANCE){
            if(height(t.left.left) >= height(t.left.right)){
                t = rotateWithLeftChild(t);
            }
            else{
                t = doubleWithLeftChild(t);
            }
        }
        else if(height(t.right) - height(t.left) > ALLOWED_IMBALANCE){
            if(height(t.right.right) >= height(t.right.left)){
                t = rotateWithRightChild(t);
            }
            else {
                t = doubleWithRightChild(t);
            }
        }
        t.height = Math.max(height(t.left), height(t.right)) + 1;
        return t;
    }

    private int avlHeight(Node<K, V> t) {
        return t == null ? -1 : t.height;
    }

    private Node<K, V> rotateWithLeftChild(Node<K, V> k2) {
        // TODO
        Node<K,V> node = k2.left;
        node.parent = k2.parent;
        k2.left = node.right;
        if(k2.left!= null)k2.left.parent = k2;
        if(k2.parent != null) {
            if (k2.parent.left == k2) {
                k2.parent.left = node;
            } else k2.parent.right = node;
        }
        else{ root = node; }
        node.right = k2;
        k2.parent = node;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        node.height = Math.max(height(node.left), k2.height) + 1;
        return node;
    }

    private Node<K, V> rotateWithRightChild(Node<K, V> k1) {
        // TODO
        Node<K,V> node = k1.right;
        node.parent = k1.parent;
        k1.right = node.left;
        if(k1.right!= null) k1.right.parent = k1;
        if(k1.parent != null) {
            if (k1.parent.left == k1) {
                k1.parent.left = node;
            } else k1.parent.right = node;
        }
        else { root = node; }
        node.left = k1;
        k1.parent = node;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        node.height = Math.max(height(node.right), k1.height) +1;
        return node;
    }

    private Node<K, V> doubleWithLeftChild(Node<K, V> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    private Node<K, V> doubleWithRightChild(Node<K, V> k3) {
        k3.right = rotateWithLeftChild(k3.right);
        return rotateWithRightChild(k3);
    }

    private class NodeOldValuePair {
        Node<K, V> node;
        V oldValue;

        NodeOldValuePair(Node<K, V> n, V oldValue) {
            this.node = n;
            this.oldValue = oldValue;
        }
    }

    public static void main(String[] args) {
        boolean usingInts = true;
        if (args.length > 0) {
            try {
                Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                usingInts = false;
            }
        }

        AVLTreeMap avlTree;
        if (usingInts) {
            @SuppressWarnings("unchecked")
            Pair<Integer, Integer>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                try {
                    int val = Integer.parseInt(args[i]);
                    pairs[i] = new Pair<>(val, val);
                } catch (NumberFormatException nfe) {
                    System.err.println("Error: Invalid integer '" + args[i]
                            + "' found at index " + i + ".");
                    System.exit(1);
                }
            }
            avlTree = new AVLTreeMap<Integer, Integer>(pairs);
        } else {
            @SuppressWarnings("unchecked")
            Pair<String, String>[] pairs = new Pair[args.length];
            for (int i = 0; i < args.length; i++) {
                pairs[i] = new Pair<>(args[i], args[i]);
            }
            avlTree = new AVLTreeMap<String, String>(pairs);
        }

        System.out.println(avlTree.toAsciiDrawing());
        System.out.println();
        System.out.println("Height:                   " + avlTree.height());
        System.out.println("Total nodes:              " + avlTree.size());
        System.out.printf("Successful search cost:   %.3f\n",
                avlTree.successfulSearchCost());
        System.out.printf("Unsuccessful search cost: %.3f\n",
                avlTree.unsuccessfulSearchCost());
        avlTree.printTraversal(PREORDER);
        avlTree.printTraversal(INORDER);
        avlTree.printTraversal(POSTORDER);
    }
}
