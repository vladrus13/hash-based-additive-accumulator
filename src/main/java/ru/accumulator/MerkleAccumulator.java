package ru.accumulator;

import ru.util.AccumulatorUtils;
import ru.util.Prove;

import java.util.*;

/**
 * Merkle-tree realization of accumulator
 */
public class MerkleAccumulator implements Accumulator {
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
    public byte[] get(int position) {
        if (position == 0) {
            return null;
        } else {
            return S.getLeaf(AccumulatorUtils.lastZeroCount(position));
        }
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

    /**
     * Make tree from [0..n] elements
     *
     * @param n position
     * @return {@link MerkleTree}
     */
    private MerkleTree makeTree(int n) {
        ArrayList<Integer> I = new ArrayList<>();
        ArrayList<byte[]> S = new ArrayList<>();
        int i = 0;
        int t = 1;
        while (t <= n) {
            I.add(AccumulatorUtils.hook_index(n, i));
            i++;
            t *= 2;
        }
        for (int index : I) {
            if (index > size) {
                S.add(R.get(index));
            } else {
                S.add(this.S.getLeaf(AccumulatorUtils.lastZeroCount(index)));
            }
        }
        return new MerkleTree(S);
    }

    @Override
    public List<Prove> prove(int position) {
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
    public boolean verify(byte[] R, int i, int j, Object ww, byte[] x) {
        LinkedList<Prove> w = (LinkedList<Prove>) ww;
        if (!(1 <= j && j <= i)) {
            throw new IllegalArgumentException("Third argument less then second, or less than second");
        }
        if (w.size() < 2) {
            throw new IllegalArgumentException("Size of hash-array less than three");
        }
        Prove it = w.removeFirst();
        byte[] temp = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it.x, it.rh));
        if (!Arrays.equals(temp, R)) {
            return false;
        }
        if (i == j) {
            return w.isEmpty() && Arrays.equals(R, it.x);
        } else {
            int i_n = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_n);
            ArrayList<byte[]> items = new ArrayList<>();
            for (int u = 0; (1 << u) < i; u++) {
                items.add(this.R.get(AccumulatorUtils.bit_lift(i - 1, u)));
            }
            MerkleTree merkleTree = new MerkleTree(items);
            // TODO merkleTree.verify(p.w, leaf, items.get(leaf));
            return verify(R, i_n, j, w, x);
        }
    }

    /**
     * Get proves from i to j
     *
     * @param j      position start
     * @param i      position finish
     */
    private List<Prove> prove(int i, int j) {
        if (j > i) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        ArrayList<byte[]> items = new ArrayList<>();
        for (int u = 0; (1 << u) < i; u++) {
            items.add(R.get(AccumulatorUtils.bit_lift(i - 1, u)));
        }
        MerkleTree previous = new MerkleTree(items);
        Prove prove = new Prove(elements.get(i), previous.getRoot(), new LinkedList<>());
        if (i > j) {
            int i_next = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_next);
            prove.w.addAll(previous.proof(leaf));
            List<Prove> prove1 = new LinkedList<>(prove(i_next, j));
            prove1.add(prove);
            return prove1;
        } else {
            return List.of(prove);
        }
    }
}
