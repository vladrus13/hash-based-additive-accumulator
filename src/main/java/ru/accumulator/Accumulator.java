package ru.accumulator;

import java.util.BitSet;

public interface Accumulator {

    long size();

    BitSet get(long position);

    void add(BitSet element);
}
