package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    private MerkleTree S;
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

    /**
     * Get size
     * @return size
     */
    public long size() {
        return size;
    }

    /**
     * Get element on position
     * @param position position
     * @return element
     */
    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return AccumulatorUtils.toByteArray(S.getLeaf(AccumulatorUtils.zeros(position)));
        }
    }

    /**
     * Add element to accumulator
     * @param element element
     */
    public void add(byte[] element) {
        byte[] root = AccumulatorUtils.toByteArray(S.getRoot());
        size++;
        byte[] result = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(element, root));
        S.set(AccumulatorUtils.zeros(size), AccumulatorUtils.toString(result));
        elements.add(element);
        R.add(result);
    }

    /**
     * Make tree from [0..n] elements
     * @param n position
     * @return {@link MerkleTree}
     */
    private MerkleTree makeTree(long n) {
        ArrayList<Long> I = new ArrayList<>();
        ArrayList<String> S = new ArrayList<>();
        long i = 0;
        long t = 1;
        while (t <= n) {
            I.add(AccumulatorUtils.hook_index(n, i));
            i++;
            t *= 2;
        }
        for (long index : I) {
            if (index > size) {
                S.add(AccumulatorUtils.toString(R.get((int) index)));
            } else {
                S.add(this.S.getLeaf(AccumulatorUtils.zeros(index)));
            }
        }
        return new MerkleTree(S);
    }

    /**
     * Get list of proves for position
     * @param position position
     * @return list of proves
     */
    public List<byte[]> prove(long position) {
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
    }

    /**
     * Get proves from i to j
     * @param j position start
     * @param i position finish
     * @param answer list with answer
     */
    public void prove(long j, long i, LinkedList<byte[]> answer) {
        if (!(size <= j && j <= i)) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        MerkleTree previous = makeTree(i - 1);
        answer.addAll(List.of(elements.get((int) i), AccumulatorUtils.toByteArray(previous.getRoot())));
        if (i > j) {
            long i_next = AccumulatorUtils.rpred(i - 1, j);
            long leaf = AccumulatorUtils.zeros(i_next);
            answer.add(AccumulatorUtils.toByteArray(previous.getLeaf((int) leaf)));
            answer.addAll(previous.proof((int) leaf).stream().map(AccumulatorUtils::toByteArray).collect(Collectors.toList()));
            prove(j, i_next, answer);
        }
    }
}
