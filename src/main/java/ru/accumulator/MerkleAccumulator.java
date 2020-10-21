package ru.accumulator;

import java.util.BitSet;

public class MerkleAccumulator implements Accumulator {
    public BitSet size() {
        return new BitSet(0);
    }

    public BitSet get(BitSet position) {
        return null;
    }

    @Override
    public void add(BitSet element) {
    }
}
