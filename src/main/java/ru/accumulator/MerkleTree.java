package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    List<byte[]> hashed_data;
    int first_leaf;

    /**
     * Build new MerkleTree on given source data.
     *
     * @param source data on which the MerkleTree built
     */
    public MerkleTree(List<byte[]> source) {
        if (source == null || source.size() == 0) {
            clear();
            return;
        }

        int size = AccumulatorUtils.maxNotLargerPowerOfTwo(4 * source.size() - 1);
        assert (size / 2 >= source.size());
        size--;
        first_leaf = size / 2;

        hashed_data = new ArrayList<>(Collections.nCopies(size, null));
        for (int i = 0; i < source.size(); i++) {
            hashed_data.set(i + first_leaf, getLeafHash(source.get(i)));
        }
        for (int i = first_leaf - 1; i >= 0; i--) {
            hashed_data.set(i, getVertexesHash(i));
        }
    }

    /**
     * Build new empty MerkleTree. Probides one leaf.
     */
    public MerkleTree() {
        hashed_data = new ArrayList<>(Collections.singleton(null));
        first_leaf = 0;
    }

    /**
     * Generate proof by leaf's index.
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<byte[]> proof(int index) {
        List<byte[]> ans = new ArrayList<>();

        for (int currentState = index + first_leaf; currentState != 0;
             currentState = (currentState - 1) / 2) {
            ans.add(hashed_data.get(getNeighbour(currentState)));
        }
        return ans;
    }

    /**
     * Return hash of the MerkleTree root
     *
     * @return hash of the root
     */
    public byte[] getRoot() {
        return hashed_data.get(0);
    }

    /**
     * Verify proof. Return true if proof is correct, and false if incorrect.
     *
     * @param leafValue        leaf hash
     * @param index            leaf index
     * @param neighboursHashes neighbours hashes to compute a hash of the root
     * @return true if proof is correct, else false
     */
    public boolean verify(byte[] leafValue, int index, List<byte[]> neighboursHashes) {
        byte[] current = leafValue;
        for (byte[] neighbourString : neighboursHashes) {
            if (index % 2 == 0) {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(current, neighbourString));
            } else {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(neighbourString, current));
            }
            index /= 2;
        }
        return Arrays.equals(getRoot(), current);
    }

    /**
     * Makes tree empty by deleting all data.
     */
    public void clear() {
        hashed_data = new ArrayList<>(Collections.singleton(null));
        first_leaf = 0;
    }

    /**
     * Set value to leaf.
     *
     * @param index leaf index
     * @param value new value
     */
    public void set(int index, byte[] value) {
        while (checkCapacity(index)) {
            expand();
        }
        hashed_data.set(index + first_leaf, getLeafHash(value));
        for (int currentState = (index + first_leaf - 1) / 2; ;
             currentState = (currentState - 1) / 2) {
            hashed_data.set(currentState, getVertexesHash(currentState));
            if (currentState == 0) break;
        }
    }

    private boolean checkCapacity(int index) {
        return index + first_leaf >= hashed_data.size();
    }

    private void expand() {
        List<byte[]> new_storage = new ArrayList<>(Collections.nCopies(2 * hashed_data.size() + 1, null));
        for (int i = 0; i < hashed_data.size(); i++) {
            new_storage.set(i + AccumulatorUtils.maxNotLargerPowerOfTwo(i + 1), hashed_data.get(i));
        }
        hashed_data = new_storage;
        first_leaf = 2 * first_leaf + 1;
        hashed_data.set(0, getVertexesHash(0));
    }

    private int getNeighbour(int index) {
        int par = (index - 1) / 2;
        return 4 * par + 3 - index;
    }

    private byte[] getLeafHash(byte[] value) {
        return value;
    }

    private byte[] getVertexesHash(int index) {
        if (index < first_leaf) {
            return AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(
                    hashed_data.get(2 * index + 1), hashed_data.get(2 * index + 2)));
        } else return hashed_data.get(index);
    }
}
