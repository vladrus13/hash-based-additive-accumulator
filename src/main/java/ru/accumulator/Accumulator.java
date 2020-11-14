package ru.accumulator;

import java.util.LinkedList;

/**
 * Interface of accumulators
 */
public interface Accumulator<T> {

    /**
     * Get size
     *
     * @return size of accumulator
     */
    int size();

    /**
     * Add element to accumulator
     *
     * @param element element
     */
    void add(byte[] element);

    /**
     * Get list of proves for position
     *
     * @param position position
     * @return list of proves
     */
    LinkedList<T> prove(int position);

    /**
     * Clear all accumulator
     */
    void clear();

    /**
     * Verify proof. Return true if proof is correct and false if incorrect.
     * @param startIndex index of the start of proof
     * @param provableIndex index of provable data
     * @param witness proof
     * @param element provable data
     * @return true if proof is correct, else false
     */
    boolean verify(int startIndex, int provableIndex, LinkedList<T> witness, byte[] element);
}
