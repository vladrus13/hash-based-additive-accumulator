package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    List<byte[]> hashed_data;
    List<byte[]> original_data;
    int capacity;

    public MerkleTree(List<byte[]> source) {
        if (source == null || source.size() == 0) {
            clear();
            return;
        }

        original_data = new ArrayList<>(source);
        int size =  AccumulatorUtils.max_leq_pow2(4 * source.size() - 1);
        //assert (size/2 >= source.size());
        capacity = source.size();
        size--;

        hashed_data = new ArrayList<>(Collections.nCopies(size, null));
        for (int i = 0; i < source.size(); i++) {
            hashed_data.set(i + size / 2, getLeafHash(source.get(i)));
        }
        for (int i = size / 2 - 1; i >= 0; i--) {
            hashed_data.set(i, getVertexesHash(i));
        }
    }

    public MerkleTree() {
        capacity = 0;
        original_data = new ArrayList<>();
        hashed_data = new ArrayList<>(Collections.nCopies(1, null));
    }

    public void clear() {
        capacity = 0;
        original_data = new ArrayList<>();
        hashed_data = new ArrayList<>(Collections.nCopies(1, null));
    }


    private void expand() {
        List<byte[]> new_storage = new ArrayList<>(Collections.nCopies(2 * hashed_data.size() + 1, null));
        for (int i = 0; i < hashed_data.size(); i++) {
            new_storage.set(i +  AccumulatorUtils.max_leq_pow2( i + 1), hashed_data.get(i));
        }

        new_storage.set(0, getVertexesHash(0));
        hashed_data = new_storage;
    }

    private boolean checkCapacity(int index) {
        return index + hashed_data.size() / 2 >= hashed_data.size();
    }

    public void set(int index, byte[] value) {
        while (checkCapacity(index)) {
            expand();
        }

        original_data.add(value);
        hashed_data.set(index + hashed_data.size() / 2, getLeafHash(value));
        for (int currentState = (index + hashed_data.size() / 2 - 1) / 2; ;
             currentState = (currentState - 1) / 2) {
            hashed_data.set(currentState, getVertexesHash(currentState));
            if (currentState == 0) break;
        }

        capacity++;
    }

    public byte[] getRoot() {
        return hashed_data.get(0);
    }

    public byte[] getLeaf(int index) {
        return hashed_data.get(index + hashed_data.size() / 2);
    }

    public byte[] getOriginal(int index) {
        return original_data.get(index);
    }


    /**
     * Generate proof by leaf's index
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<byte[]> proof(int index) {
        List<byte[]> ans = new ArrayList<>();
        ans.add(getLeaf(index));

        for (int currentState = index + hashed_data.size() / 2; currentState != 0;
             currentState = (currentState - 1) / 2) {
            ans.add(hashed_data.get(getNeighbour(currentState)));
        }
        return ans;
    }
    /*
    public boolean verify(String rootHash, int index, String leafHash, List<String> neighboursHashes) {
        if (getRoot().equals(rootHash) && getLeaf(index).equals(leafHash)) {
            List<String> expected = proof(index);
            int cur = 1;
            for (String got : neighboursHashes) {
                if (!got.equals(expected.get(cur))) {
                    return false;
                }
                cur++;
            }
            return true;
        }
        return false;
    }*/

    public static boolean verify(byte[] rootHash, int index, byte[] leafHash, List<byte[]> neighboursHashes) {
        byte[] current = leafHash;
        for (byte[] neighbourString : neighboursHashes) {
            if (index % 2 == 0) {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(current, neighbourString));
            } else {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(neighbourString, current));
            }
            index /= 2;
        }
        return rootHash == current;
    }

    private int getNeighbour(int index) {
        int par = (index - 1) / 2;
        return 4 * par + 3 - index;
    }

    private byte[] getLeafHash(byte[] value) {
        return AccumulatorUtils.getSha256(value);
    }

    private byte[] getVertexesHash(int index) {
        if (index < hashed_data.size() / 2) {
            return AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(
                    hashed_data.get(2 * index + 1), hashed_data.get(2 * index + 2)));
        } else return hashed_data.get(index);
    }
}
