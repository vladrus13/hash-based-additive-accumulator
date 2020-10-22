package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SmartBackLinesAccumulator implements Accumulator {
    private long size;
    private final ArrayList<byte[]> S;
    private final ArrayList<byte[]> elements;

    SmartBackLinesAccumulator() {
        S = new ArrayList<>();
        elements = new ArrayList<>();
        size = 0;
    }

    public long size() {
        return size;
    }

    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return S.get(AccumulatorUtils.zeros(position));
        }
    }

    public void add(byte[] element) {
        if ((size & (size - 1)) == 0) {
            S.add(null);
            elements.add(null);
            size++;
        }
        byte[] sum = AccumulatorUtils.concatDigits(element, get(size - 1), get(AccumulatorUtils.d(size)));
        byte[] result = AccumulatorUtils.getSha256(sum);
        S.set((int) size, result);
        elements.set((int) size, element);
    }

    public LinkedList<byte[]> prove(long position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    public void prove(long i, long j, LinkedList<byte[]> answer) {
        if (i > j) {
            throw new IllegalArgumentException("Second argument more than first");
        }
        answer.addAll(List.of(elements.get((int) j), S.get((int) (j - 1)), S.get((int) AccumulatorUtils.pred(j))));
        if (j > i) {
            if (AccumulatorUtils.pred(j) >= i) {
                prove(i, AccumulatorUtils.pred(j), answer);
            } else {
                prove(i, j - 1, answer);
            }
        }
    }
}
