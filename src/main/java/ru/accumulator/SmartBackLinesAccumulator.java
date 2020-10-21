package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.BitSet;

public class SmartBackLinesAccumulator implements Accumulator {
    private long size;
    private final ArrayList<byte[]> S;

    SmartBackLinesAccumulator() {
        S = new ArrayList<>();
        size = 0;
    }

    public long size() {
        return size;
    }

    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return S.get((int) position);
        }
    }

    public void add(byte[] element) {
        if ((size & (size - 1)) == 0) {
            S.add(null);
            size++;
        }
        byte[] sum = AccumulatorUtils.concatDigits(element, get(size - 1), get(AccumulatorUtils.d(size)));
        byte[] result = AccumulatorUtils.getSha256(sum);
        S.set((int) size, result);
    }
}
