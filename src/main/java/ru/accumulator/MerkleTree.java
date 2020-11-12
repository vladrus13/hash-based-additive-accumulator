package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    List<byte[]> hashed_data;
    int first_leaf;

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

    // empty Merkle Tree provides Tree for 2 vertexes
    public MerkleTree() {
        hashed_data = new ArrayList<>(Collections.nCopies(3, null));
        first_leaf = 1;
    }

    public void clear() {
        hashed_data = new ArrayList<>(Collections.nCopies(3, null));
        first_leaf = 1;
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

    private boolean checkCapacity(int index) {
        return index + first_leaf >= hashed_data.size();
    }

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

    public byte[] getRoot() {
        return hashed_data.get(0);
    }

    public byte[] getLeaf(int index) {
        return hashed_data.get(index + first_leaf);
    }

    public byte[] getOriginal(int index) {
        return getLeaf(index);
        //        return original_data.get(index);
    }


    /**
     * Generate proof by leaf's index
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<byte[]> proof(int index) {
        List<byte[]> ans = new ArrayList<>();
        //ans.add(getLeaf(index));

        for (int currentState = index + first_leaf; currentState != 0;
             currentState = (currentState - 1) / 2) {
            ans.add(hashed_data.get(getNeighbour(currentState)));
        }
        return ans;
    }
    /*
    public static boolean verify(byte[] rootHash, int index, List<byte[]> neighboursHashes) {
        if (getRoot().equals(rootHash)) {
            List<byte[]> expected = proof(index);
            int cur = 0;
            for (byte[] got : neighboursHashes) {
                if (!Arrays.equals(got, expected.get(cur))) {
                    return false;
                }
                cur++;
            }
            return true;
        }
        return false;
    }
    */

    public boolean verify(byte[] rootHash, int index, List<byte[]> neighboursHashes) {
        byte[] current = getLeaf(index);
        for (byte[] neighbourString : neighboursHashes) {
            if (index % 2 == 0) {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(current, neighbourString));
            } else {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(neighbourString, current));
            }
            index /= 2;
        }
        return Arrays.equals(rootHash, current);
    }

    private int getNeighbour(int index) {
        int par = (index - 1) / 2;
        return 4 * par + 3 - index;
    }

    private byte[] getLeafHash(byte[] value) {
        return value;
//        return AccumulatorUtils.getSha256(value);
    }

    private byte[] getVertexesHash(int index) {
        if (index < first_leaf) {
            return AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(
                    hashed_data.get(2 * index + 1), hashed_data.get(2 * index + 2)));
        } else return hashed_data.get(index);
    }
}
