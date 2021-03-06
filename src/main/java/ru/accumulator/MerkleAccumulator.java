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
    public boolean verify(int startIndex, int provableIndex, LinkedList<Prove> witness, byte[] element) {
        if (!(1 <= provableIndex && provableIndex <= startIndex)) {
            throw new IllegalArgumentException("Provable index out of range");
        }
        if (witness.size() == 0) {
            throw new IllegalArgumentException("Size of hash-array less than one");
        }
        Prove it = witness.removeFirst();
        byte[] temp = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it.element, it.witnessHead));
        if (!Arrays.equals(temp, R.get(startIndex))) {
            return false;
        }
        if (startIndex == provableIndex) {
            return witness.isEmpty() && Arrays.equals(element, it.element);
        } else {
            int jumpIndex = AccumulatorUtils.rpred(startIndex - 1, provableIndex);
            int leaf = AccumulatorUtils.lastZeroCount(jumpIndex);
            ArrayList<byte[]> items = new ArrayList<>();
            for (int u = 0; (1 << u) < startIndex; u++) {
                items.add(this.R.get(AccumulatorUtils.bitLift(startIndex - 1, u)));
            }
            MerkleTree merkleTree = new MerkleTree(items);
            if (!merkleTree.verify(items.get(leaf), leaf, it.witnessRest)) {
                return false;
            }
            return verify(jumpIndex, provableIndex, witness, element);
        }
    }

    /**
     * Get proves from provableIndex to startIndex
     *
     * @param startIndex position start
     * @param provableIndex position finish
     */
    private LinkedList<Prove> prove(int provableIndex, int startIndex) {
        if (startIndex > provableIndex) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        ArrayList<byte[]> items = new ArrayList<>();
        for (int u = 0; (1 << u) < provableIndex; u++) {
            items.add(R.get(AccumulatorUtils.bitLift(provableIndex - 1, u)));
        }
        MerkleTree previous = new MerkleTree(items);
        Prove prove = new Prove(elements.get(provableIndex), previous.getRoot(), new LinkedList<>());
        if (provableIndex > startIndex) {
            int nextI = AccumulatorUtils.rpred(provableIndex - 1, startIndex);
            int leaf = AccumulatorUtils.lastZeroCount(nextI);
            prove.witnessRest.addAll(previous.proof(leaf));
            LinkedList<Prove> prove1 = new LinkedList<>(prove(nextI, startIndex));
            prove1.add(0, prove);
            return prove1;
        } else {
            return new LinkedList<>(Collections.singleton(prove));
        }
    }
}
