package ru.accumulator;

import java.util.BitSet;

public interface Accumulator {

    long size();

    byte[] get(long position);

    void add(byte[] element);
}
