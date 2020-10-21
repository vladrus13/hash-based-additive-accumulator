package ru.accumulator;

import java.util.List;

public interface Accumulator {

    long size();

    byte[] get(long position);

    void add(byte[] element);

    List<byte[]> prove(long position);
}
