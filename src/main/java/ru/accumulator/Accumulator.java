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
     * @param proofIndex index of provable data
     * @param j index of the beginning of proof
     * @param w proof
     * @param x provable data
     * @return true if proof is correct, else false
     */
    boolean verify(int proofIndex, int j, LinkedList<T> w, byte[] x);
}
