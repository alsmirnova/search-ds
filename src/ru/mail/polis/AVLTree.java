package ru.mail.polis;

import java.util.*;

//TODO: write code here
public class AVLTree<E extends Comparable<E>> implements ISortedSet<E> {

    class Node {

        Node(E value) {
            this.value = value;
        }

        E value;
        int height;
        Node left;
        Node right;
        Node parent;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append("h=").append(height);
            sb.append('}');
            return sb.toString();
        }
    }

    private Node root;
    private final static int K = 1; //for AVLTree
    private int size;
    private final Comparator<E> comparator;

    public AVLTree() {
        this.comparator = null;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public boolean contains(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    @Override
    public List<E> inorderTraverse() {
        List<E> list = new ArrayList<E>(size);
        inorderTraverse(root, list);
        return list;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }

    public int getHeight() {
        return getHeight(root);
    }

    private int getHeight(Node node) {
        if (node == null) return 0;
        return Math.max(getHeight(node.left), getHeight(node.right)) + 1;
    }

    public Node find(E value) {
        return find(root, value);
    }

    private Node find(Node curr, E val) {
        if (curr == null || curr.value.equals(val)) {
            return curr;
        }
        if (curr.value.compareTo(val) > 0) {
            return find(curr.left, val);
        } else {
            return find(curr.right, val);
        }
    }


    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);
            root.height = 1;
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node(value);
                        curr.right.parent = curr;
                        curr.right.height = 1;
                        break;
                    }
                } else if (cmp > 0) {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value);
                        curr.left.parent = curr;
                        curr.left.height = 1;
                        break;
                    }
                }
            }
            balancingAdd(curr);
        }
        size++;


        return true;
    }


    public void balancingAdd(Node curr) {
        int dh;
        while (true) {
            dh = diff(curr);
            if (dh == 0) break;
            else if (Math.abs(dh) == 1) {
                curr.height++;
                if (curr != root) curr = curr.parent;
                else break;
            } else if (Math.abs(dh) == 2) rotate(curr, dh);
        }

    }

    public void rotate(Node a, int dh) {
        Node b;
        int diffB;
        if (dh == -2) {
            b = a.right;
            diffB = diff(b);
            if (diffB != 1) rotateLeft(a);
            else bigRotateLeft(a);
        } else if (dh == 2) {
            b = a.left;
            diffB = diff(b);
            if (diffB != -1) rotateRight(a);
            else bigRotateRight(a);
        }
    }

    public int diff(Node b) {
        int dh;
        if (b.left == null && b.right == null) dh=1;
        else if (b.left == null) dh = -b.right.height;
        else if (b.right == null) dh = b.left.height;
        else {
            dh = b.left.height - b.right.height;
        }
        return dh;
    }


    public void rotateLeft(Node a) {
        Node b = a.right;
        a.right = b.left;
        b.left = a;
        a.height=setHeight(a);
        b.height = setHeight(b);
    }

    public int setHeight(Node a) {
        int h;
        if (a.left != null && a.right != null) {
            h = Math.max(a.left.height, a.right.height) + 1;
        } else {
            h = diff(a)+1;
        }
        return h;
    }

    public void bigRotateLeft(Node a) {
        rotateRight(a.right);
        rotateLeft(a);
    }

    public void rotateRight(Node a) {
        Node b = a.left;
        a.left = b.right;
        b.right = a;
        a.height=setHeight(a);
        b.height = setHeight(b);
    }

    public void bigRotateRight(Node a) {
        rotateLeft(a.left);
        rotateRight(a);
    }


    public void balancingRemove(Node curr) {
        int dh;
        while (true) {
            dh = diff(curr);
            if (Math.abs(dh) == 1) break;
            else if (dh == 0) {
                curr.height--;
                if (curr != root) curr = curr.parent;
                else break;
            } else if (Math.abs(dh) == 2) rotate(curr, dh);
        }

    }


    @Override
    public boolean remove(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            return false;
        }
        Node parent = root;
        Node curr = root;
        Node pRemoved; //parent removed
        int cmp;
        while ((cmp = compare(curr.value, value)) != 0) {
            parent = curr;
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == null) {
                return false; // ничего не нашли
            }
        }
        if (curr.left != null && curr.right != null) {
            Node next = curr.right;
            Node pNext = curr;
            while (next.left != null) {
                pNext = next;
                next = next.left;
            } //next = наименьший из больших
            curr.value = next.value;
            next.value = null;
            //у правого поддерева нет левых потомков
            if (pNext == curr) {
                curr.right = next.right;
            } else {
                pNext.left = next.right;
            }
            pRemoved=pNext;
            next.right = null;
        } else {
            pRemoved=curr;
            if (curr.left != null) {
                reLink(parent, curr, curr.left);
            } else if (curr.right != null) {
                reLink(parent, curr, curr.right);
            } else {
                reLink(parent, curr, null);
                if (curr!=root) pRemoved=curr.parent;
            }
        }
        balancingRemove(pRemoved);
        size--;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == curr) {
            root = child;
        } else if (parent.left == curr) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        curr.value = null;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }


    public static void main(String[] args) {
        ISortedSet<Integer> set = new AVLTree<>();
        for (int i = 0; i < 3; i++) {
            boolean add = set.add(i);
            boolean contains = set.contains(i);
            System.out.println("i = " + i + ", add = " + add + ", contains = " + contains);
        }
    }
}
