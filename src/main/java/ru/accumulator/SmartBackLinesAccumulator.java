package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.BitSet;

public class SmartBackLinesAccumulator implements Accumulator {
    private BitSet size;
    private final ArrayList<BitSet> S;

    SmartBackLinesAccumulator() {
        S = new ArrayList<>();
        size = new BitSet(0);
    }

    public BitSet size() {
        return size;
    }

    public BitSet get(BitSet position) {
        if (position == 0) {
            return null;
        } else {
            return S.get(position);
        }
    }

    @Override
    public void add(BitSet element) {
        if ((size & (size - 1)) == 0) {
            S.add(null);
            size++;
        }
        BitSet sum = AccumulatorUtils.concatDigits(element, get(size - 1), get(size - AccumulatorUtils.d(new BitSet(size)).))
    }
}
