package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.*;

/**
 * Smart backlines realization of accumulator
 */
public class SmartBackLinesAccumulator implements Accumulator<byte[]> {
    /**
     * Size of structure
     */
    private int size;
    /**
     * Hashing elements
     */
    private final ArrayList<byte[]> S;
    /**
     * Elements
     */
    private final Map<Integer, byte[]> elements;
    private final Map<Integer, byte[]> R;

    /**
     * Constructor for empty accumulator
     */
    public SmartBackLinesAccumulator() {
        S = new ArrayList<>(Collections.singleton(null));
        R = new HashMap<>();
        elements = new HashMap<>();
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    private byte[] get(int position) {
        if (position == 0) {
            return null;
        } else {
            return S.get(AccumulatorUtils.lastZeroCount(position));
        }
    }

    @Override
    public void add(byte[] element) {
        if ((size & (size - 1)) == 0) {
            S.add(null);
        }
        size++;
        byte[] sum = AccumulatorUtils.concatDigits(element, get(size - 1), get(size - AccumulatorUtils.maxDividingPowerOfTwo(size)));
        byte[] result = AccumulatorUtils.getSha256(sum);
        S.set(AccumulatorUtils.lastZeroCount(size), result);
        elements.put(size, element);
        R.put(size, result);
    }

    @Override
    public LinkedList<byte[]> prove(int position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    @Override
    public void clear() {
        S.clear();
        S.add(null);
        elements.clear();
        R.clear();
        size = 0;
    }

    @Override
    public boolean verify(int startIndex, int provableIndex, LinkedList<byte[]> witness, byte[] element) {
        if (startIndex < provableIndex) {
            throw new IllegalArgumentException("Start of the proof less than provable part");
        }
        if (witness.size() < 3) {
            throw new IllegalArgumentException("Size of hash-array less than three");
        }
        byte[] it = witness.removeFirst();
        byte[] previousR = witness.removeFirst();
        byte[] predR = witness.removeFirst();
        if (!Arrays.equals(AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it, previousR, predR)), this.R.get(startIndex))) {
            return false;
        }
        if (startIndex == provableIndex) {
            return Arrays.equals(it, element);
        } else {
            if (AccumulatorUtils.predecessor(startIndex) >= provableIndex) {
                return verify(AccumulatorUtils.predecessor(startIndex), provableIndex, witness, element);
            } else {
                return verify(startIndex - 1, provableIndex, witness, element);
            }
        }
    }

    /**
     * Get proves from provableIndex to startIndex
     *
     * @param startIndex    position start
     * @param provableIndex position finish
     * @param answer        list with answer
     */
    private void prove(int provableIndex, int startIndex, LinkedList<byte[]> answer) {
        if (startIndex < provableIndex) {
            throw new IllegalArgumentException("Start of the proof less than provable part");
        }
        answer.add(elements.get(startIndex));
        answer.add(R.get((startIndex - 1)));
        answer.add(R.get(AccumulatorUtils.predecessor(startIndex)));
        if (startIndex > provableIndex) {
            if (AccumulatorUtils.predecessor(startIndex) >= provableIndex) {
                prove(provableIndex, AccumulatorUtils.predecessor(startIndex), answer);
            } else {
                prove(provableIndex, startIndex - 1, answer);
            }
        }
    }
}
