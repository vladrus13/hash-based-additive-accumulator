package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Merkle-tree realization of accumulator
 */
public class MerkleAccumulator implements Accumulator {
    /**
     * Size of structure
     */
    private long size;
    /**
     * Merkle tree
     */
    private final MerkleTree S;
    /**
     * Hashing elements
     */
    private final ArrayList<byte[]> R;
    /**
     * Elements
     */
    private final ArrayList<byte[]> elements;

    /**
     * Constructor for empty accumulator
     */
    public MerkleAccumulator() {
        size = 0;
        S = new MerkleTree();
        R = new ArrayList<>();
        elements = new ArrayList<>();
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return S.getLeaf(AccumulatorUtils.zeros(position));
        }
    }

    @Override
    public void add(byte[] element) {
        byte[] root = S.getRoot();
        size++;
        byte[] result = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(element, root));
        S.set(AccumulatorUtils.zeros(size), result);
        elements.add(element);
        R.add(result);
    }

    /**
     * Make tree from [0..n] elements
     *
     * @param n position
     * @return {@link MerkleTree}
     */
    private MerkleTree makeTree(long n) {
        ArrayList<Long> I = new ArrayList<>();
        ArrayList<byte[]> S = new ArrayList<>();
        long i = 0;
        long t = 1;
        while (t <= n) {
            I.add(AccumulatorUtils.hook_index(n, i));
            i++;
            t *= 2;
        }
        for (long index : I) {
            if (index > size) {
                S.add(R.get((int) index));
            } else {
                S.add(this.S.getLeaf(AccumulatorUtils.zeros(index)));
            }
        }
        return new MerkleTree(S);
    }

    @Override
    public LinkedList<byte[]> prove(long position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    @Override
    public void clear() {
        size = 0;
        S.clear();
        R.clear();
        elements.clear();
        elements.add(null);
    }

    @Override
    public boolean verify(byte[] R, long i, long j, LinkedList<byte[]> w, byte[] x) {
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
            long i_n = AccumulatorUtils.rpred(i - 1, j);
            long leaf = AccumulatorUtils.zeros(i_n);
            byte[] real_leaf = w.removeFirst();
            long merkle_size = AccumulatorUtils.max_leq_pow2(i - 1); // TODO: Really?
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
    public void prove(long j, long i, LinkedList<byte[]> answer) {
        if (j > i) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        MerkleTree previous = makeTree(i - 1);
        answer.addAll(List.of(elements.get((int) i), previous.getRoot()));
        if (i > j) {
            long i_next = AccumulatorUtils.rpred(i - 1, j);
            long leaf = AccumulatorUtils.zeros(i_next);
            answer.add(previous.getLeaf((int) leaf));
            answer.addAll(new ArrayList<>(previous.proof((int) leaf)));
            prove(j, i_next, answer);
        }
    }
}
