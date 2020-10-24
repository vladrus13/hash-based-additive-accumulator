package ru.accumulator;

import ru.util.AccumulatorUtils;

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
                S.add(R.get( index));
            } else {
                S.add(this.S.getLeaf(AccumulatorUtils.lastZeroCount(index)));
            }
        }
        return new MerkleTree(S);
    }

    @Override
    public LinkedList<byte[]> prove(int position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(size, position, answer);
        return answer;
    }

    @Override
    public void clear() {
        size = 0;
        S.clear();
        R.clear();
        elements.clear();
    }

    @Override
    public boolean verify(byte[] R, int i, int j, LinkedList<byte[]> w, byte[] x) {
        if (!(1 <= j && j <= i)) {
            throw new IllegalArgumentException("Third argument less then second, or less than second");
        }
        if (w.size() < 2) {
            throw new IllegalArgumentException("Size of hash-array less than three");
        }
        byte[] it = w.removeFirst();
        byte[] M_root = w.removeFirst();
        if (!Arrays.equals(AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it, M_root)), R)) {
            return false;
        }
        if (i == j) {
            return Arrays.equals(it, x);
        } else {
            int i_n = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_n);
            byte[] real_leaf = w.removeFirst();
            int merkle_size = AccumulatorUtils.maxNotLargerPowerOfTwo(i - 1); // TODO: Really?
            ArrayList<byte[]> merkle = new ArrayList<>();
            for (int k = 0; k < merkle_size; k++) {
                merkle.add(w.removeFirst());
            }
            if (!MerkleTree.verify(M_root, leaf, real_leaf, merkle)) {
                return false;
            }
            return verify(real_leaf, i_n, j, w, x);
        }
    }

    /**
     * Get proves from i to j
     *
     * @param j      position start
     * @param i      position finish
     * @param answer list with answer
     */
    public void prove(int i, int j, LinkedList<byte[]> answer) {
        if (j > i) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        MerkleTree previous = makeTree(i - 1);
        answer.add(elements.get(i));
        answer.add(previous.getRoot());
        if (i > j) {
            int i_next = AccumulatorUtils.rpred(i - 1, j);
            int leaf = AccumulatorUtils.lastZeroCount(i_next);
            answer.add(previous.getLeaf( leaf));
            answer.addAll(new ArrayList<>(previous.proof( leaf)));
            prove(j, i_next, answer);
        }
    }
}
