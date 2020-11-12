package ru.accumulator;

import java.util.LinkedList;
import java.util.List;

/**
 * Interface of accumulators
 */
public interface Accumulator<T> {

    /**
     * Get size
     * @return size of accumulator
     */
    int size();

    /**
     * Get element on position
     * @param position position
     * @return element
     */
    byte[] get(int position);

    /**
     * Add element to accumulator
     * @param element element
     */
    void add(byte[] element);

    /**
     * Get list of proves for position
     * @param position position
     * @return list of proves
     */
    LinkedList<T> prove(int position);

    /**
     * Clear all accumulator
     */
    void clear();

    boolean verify(byte[] R, int i, int j, LinkedList<T> w, byte[] x);
}
