package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    /**
     * List of hashes, representation of the tree
     */
    List<byte[]> hashedData;
    /**
     * first leaf index
     */
    int firstLeafIndex;

    /**
     * Build new MerkleTree on given source data.
     *
     * @param source data on which the MerkleTree built
     */
    public MerkleTree(final List<byte[]> source) {
        if (source == null || source.size() == 0) {
            clear();
            return;
        }

        int size = AccumulatorUtils.maxNotLargerPowerOfTwo(4 * source.size() - 1);
        size--;
        firstLeafIndex = size / 2;
        hashedData = new ArrayList<>(Collections.nCopies(size, null));
        for (int i = 0; i < source.size(); i++) {
            hashedData.set(i + firstLeafIndex, getLeafHash(source.get(i)));
        }
        for (int i = firstLeafIndex - 1; i >= 0; i--) {
            hashedData.set(i, getVertexesHash(i));
        }
    }

    /**
     * Build new empty MerkleTree. Provides one leaf.
     */
    public MerkleTree() {
        hashedData = new ArrayList<>(Collections.singleton(null));
        firstLeafIndex = 0;
    }

    /**
     * Generate proof by leaf's index.
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<byte[]> proof(final int index) {
        final List<byte[]> proof = new ArrayList<>();
        for (int currentState = index + firstLeafIndex; currentState != 0;
             currentState = (currentState - 1) / 2) {
            proof.add(hashedData.get(getNeighbour(currentState)));
        }
        return proof;
    }

    /**
     * Return hash of the MerkleTree root
     *
     * @return hash of the root
     */
    public byte[] getRoot() {
        return hashedData.get(0);
    }

    /**
     * Verify proof. Return true if proof is correct, and false if incorrect.
     *
     * @param leafValue        leaf hash
     * @param index            leaf index
     * @param neighboursHashes neighbours hashes to compute a hash of the root
     * @return true if proof is correct, else false
     */
    public boolean verify(final byte[] leafValue, int index, final List<byte[]> neighboursHashes) {
        byte[] verifiableRootHash = leafValue;
        for (byte[] neighbourString : neighboursHashes) {
            if (index % 2 == 0) {
                verifiableRootHash = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(verifiableRootHash, neighbourString));
            } else {
                verifiableRootHash = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(neighbourString, verifiableRootHash));
            }
            index /= 2;
        }
        return Arrays.equals(getRoot(), verifiableRootHash);
    }

    /**
     * Makes tree empty by deleting all data.
     */
    public void clear() {
        hashedData = new ArrayList<>(Collections.singleton(null));
        firstLeafIndex = 0;
    }

    /**
     * Set value to leaf.
     *
     * @param index leaf index
     * @param value new value
     */
    public void set(final int index, final byte[] value) {
        while (checkCapacity(index)) {
            expand();
        }
        hashedData.set(index + firstLeafIndex, getLeafHash(value));
        for (int currentState = (index + firstLeafIndex - 1) / 2; ;
             currentState = (currentState - 1) / 2) {
            hashedData.set(currentState, getVertexesHash(currentState));
            if (currentState == 0) {
                break;
            }
        }
    }

    /**
     * Return true if index is available to use, else false
     *
     * @param index index of leaf
     * @return true if index is in tree
     */
    private boolean checkCapacity(final int index) {
        return index + firstLeafIndex >= hashedData.size();
    }

    /**
     * Increases the tree size by 2 times.
     */
    private void expand() {
        final List<byte[]> newHashedData = new ArrayList<>(Collections.nCopies(2 * hashedData.size() + 1, null));
        for (int i = 0; i < hashedData.size(); i++) {
            newHashedData.set(i + AccumulatorUtils.maxNotLargerPowerOfTwo(i + 1), hashedData.get(i));
        }
        hashedData = newHashedData;
        firstLeafIndex = 2 * firstLeafIndex + 1;
        hashedData.set(0, getVertexesHash(0));
    }

    /**
     * Returns the neighbour of with given index
     *
     * @param index index of leaf
     * @return neighbour of leaf
     */
    private int getNeighbour(final int index) {
        final int parent = (index - 1) / 2;
        return 4 * parent + 3 - index;
    }

    /**
     * Returns leaf hash
     *
     * @param value value to be hashed
     * @return hash of the leaf
     */
    private byte[] getLeafHash(final byte[] value) {
        return value;
    }

    /**
     * Compute and return inner vertex hash
     *
     * @param index index of inner vertex
     * @return inner vertex hash
     */
    private byte[] getVertexesHash(final int index) {
        if (index < firstLeafIndex) {
            return AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(
                    hashedData.get(2 * index + 1), hashedData.get(2 * index + 2)));
        } else {
            return hashedData.get(index);
        }
    }
}
