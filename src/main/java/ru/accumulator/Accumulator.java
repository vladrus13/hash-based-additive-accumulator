package ru.accumulator;

import java.util.BitSet;

public interface Accumulator {

    BitSet size();

    BitSet get(BitSet position);

    void add(BitSet element);
}
