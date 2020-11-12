package ru.accumulator;

import ru.util.AccumulatorUtils;
import ru.util.Prove;

import java.util.*;

/**
 * Merkle-tree realization of accumulator
 */
public class MerkleAccumulator implements Accumulator<Prove> {
    /**
     * Size of structure
     */
    private int size;
    /**
     * Merkle tree
     */
    private final MerkleTree S;
    /**
     * Hashing elements
     */
    private final Map<Integer, byte[]> R;
    /**
     * Elements
     */
    private final Map<Integer, byte[]> elements;

    /**
     * Constructor for empty accumulator
     */
    public MerkleAccumulator() {
        size = 0;
        S = new MerkleTree();
        R = new HashMap<>();
        elements = new HashMap<>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(byte[] element) {
        byte[] root = S.getRoot();
        size++;
        byte[] result = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(element, root));
        S.set(AccumulatorUtils.lastZeroCount(size), result);
        elements.put(size, element);
        R.put(size, result);
    }

    @Override
    public LinkedList<Prove> prove(int position) {
        return prove(size, position);
    }

    @Override
    public void clear() {
        size = 0;
        S.clear();
        R.clear();
        elements.clear();
    }

    @Override
    public boolean verify(int i, int j, LinkedList<Prove> w, byte[] x) {
        if (!(1 <= j && j <= i)) {
            throw new IllegalArgumentException("Third argument less then second, or less than second");
        }
        if (w.size() == 0) {
            throw new IllegalArgumentException("Size of hash-array less than one");
        }
        Prove it = w.removeFirst();
        byte[] temp = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it.x, it.rh));
        if (!Arrays.equals(temp, R.get(i))) {
            return false;
        }
        if (i == j) {
            return w.isEmpty() && Arrays.equals(x, it.x);
        } else {
            int i_n = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_n);
            ArrayList<byte[]> items = new ArrayList<>();
            for (int u = 0; (1 << u) < i; u++) {
                items.add(this.R.get(AccumulatorUtils.bitLift(i - 1, u)));
            }
            MerkleTree merkleTree = new MerkleTree(items);
            if (!merkleTree.verify(items.get(leaf), leaf, it.w)) {
                return false;
            }
            return verify(i_n, j, w, x);
        }
    }

    /**
     * Get proves from i to j
     *
     * @param j position start
     * @param i position finish
     */
    private LinkedList<Prove> prove(int i, int j) {
        if (j > i) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        ArrayList<byte[]> items = new ArrayList<>();
        for (int u = 0; (1 << u) < i; u++) {
            items.add(R.get(AccumulatorUtils.bitLift(i - 1, u)));
        }
        MerkleTree previous = new MerkleTree(items);
        Prove prove = new Prove(elements.get(i), previous.getRoot(), new LinkedList<>());
        if (i > j) {
            int i_next = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_next);
            prove.w.addAll(previous.proof(leaf));
            LinkedList<Prove> prove1 = new LinkedList<>(prove(i_next, j));
            prove1.add(0, prove);
            return prove1;
        } else {
            return new LinkedList<>(Collections.singleton(prove));
        }
    }
}
