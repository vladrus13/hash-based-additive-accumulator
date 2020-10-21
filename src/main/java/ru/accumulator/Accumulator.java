package ru.accumulator;

public interface Accumulator {

    long size();

    byte[] get(long position);

    void add(byte[] element);
}
