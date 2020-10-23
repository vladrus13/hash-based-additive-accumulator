package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.*;

/**
 * Smart backlines realization of accumulator
 */
public class SmartBackLinesAccumulator implements Accumulator {
    /**
     * Size of structure
     */
    private long size;
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
    public long size() {
        return size;
    }

    @Override
    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return S.get(AccumulatorUtils.zeros(position));
        }
    }

    @Override
    public void add(byte[] element) {
        if ((size & (size - 1)) == 0) {
            S.add(null);
        }
        size++;
        byte[] sum = AccumulatorUtils.concatDigits(element, get(size - 1), get(size - AccumulatorUtils.d(size)));
        byte[] result = AccumulatorUtils.getSha256(sum);
        S.set(AccumulatorUtils.zeros(size), result);
        elements.put((int) size, element);
        R.put((int) size, result);
    }

    @Override
    public LinkedList<byte[]> prove(long position) {
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
    public boolean verify(byte[] R, long i, long j, LinkedList<byte[]> w, byte[] x) {
        if (i < j) {
            throw new IllegalArgumentException("Third less than second");
        }
        if (w.size() < 3) {
            throw new IllegalArgumentException("Size of hash-array less than three");
        }
        byte[] it = w.removeFirst();
        byte[] R_previous = w.removeFirst();
        byte[] R_pred = w.removeFirst();
        if (!Arrays.equals(AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(it, R_previous, R_pred)), R)) {
            return false;
        }
        if (i == j) {
            return Arrays.equals(it, x);
        } else {
            if (AccumulatorUtils.pred(i) >= j) {
                return verify(R_pred, AccumulatorUtils.pred(i), j, w, x);
            } else {
                return verify(R_previous, i - 1, j, w, x);
            }
        }
    }

    /**
     * Get proves from i to j
     *
     * @param j      position start
     * @param i      position finish
     * @param answer list with answer
     */
    private void prove(long i, long j, LinkedList<byte[]> answer) {
        if (i > j) {
            throw new IllegalArgumentException("Second argument more than first");
        }
        answer.add(elements.get((int) j));
        answer.add(R.get((int) (j - 1)));
        answer.add(R.get((int) AccumulatorUtils.pred(j)));
        if (j > i) {
            if (AccumulatorUtils.pred(j) >= i) {
                prove(i, AccumulatorUtils.pred(j), answer);
            } else {
                prove(i, j - 1, answer);
            }
        }
    }


}
