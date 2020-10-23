package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    List<String> hashed_data;
    List<String> original_data;
    int capacity;

    public MerkleTree(List<String> source) {
        if (source == null || source.size() == 0) {
            clear();
            return;
        }

        original_data = source;
        int size = (int) AccumulatorUtils.max_leq_pow2(4 * source.size() - 1);
        //assert (size/2 >= source.size());
        capacity = source.size();
        size--;
        hashed_data = new ArrayList<>(Collections.nCopies(size, ""));
        for (int i = 0; i < source.size(); i++) {
            hashed_data.set(i + size / 2, getLeafHash(source.get(i)));
        }
        for (int i = size / 2 - 1; i >= 0; i--) {
            hashed_data.set(i, getVertexesHash(i));
        }
    }

    public MerkleTree() {
        capacity = 0;
        original_data = Collections.emptyList();
        hashed_data = new ArrayList<>(Collections.nCopies(1, ""));
    }

    public void clear() {
        capacity = 0;
        original_data = Collections.emptyList();
        hashed_data = new ArrayList<>(Collections.nCopies(1, ""));
    }


    private void expand() {
        List<String> new_storage = new ArrayList<>(Collections.nCopies(2 * hashed_data.size() + 1, ""));
        for (int i = 0; i < hashed_data.size(); i++) {
            new_storage.set(i + (int) AccumulatorUtils.max_leq_pow2((long) i + 1), hashed_data.get(i));
        }

        new_storage.set(0, getVertexesHash(0));
        hashed_data = new_storage;
    }

    private boolean checkCapacity(int index) {
        return index + hashed_data.size() / 2 >= hashed_data.size();
    }

    public void set(int index, String value) {
        while (checkCapacity(index)) {
            expand();
        }

        hashed_data.set(index + hashed_data.size() / 2, getLeafHash(value));
        for (int currentState = (index + hashed_data.size() / 2 - 1) / 2; ;
             currentState = (currentState - 1) / 2) {
            hashed_data.set(currentState, getVertexesHash(currentState));
            if (currentState == 0) break;
        }

        capacity++;
    }

    public String getRoot() {
        return hashed_data.get(0);
    }

    public String getLeaf(int index) {
        return hashed_data.get(index + hashed_data.size() / 2);
    }

    public String getOriginal(int index) {
        return original_data.get(index);
    }


    /**
     * Generate proof by leaf's index
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<String> proof(int index) {
        List<String> ans = new ArrayList<>();
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

    public static boolean verify(byte[] rootHash, long index, byte[] leafHash, List<String> neighboursHashes) {
        byte[] current = leafHash;
        for (String neighbourString : neighboursHashes) {
            byte[] neighbour = AccumulatorUtils.toByteArray(neighbourString);
            if (index % 2 == 0) {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(current, neighbour));
            } else {
                current = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(neighbour, current));
            }
            index /= 2;
        }
        return rootHash == current;
    }

    private int getNeighbour(int index) {
        int par = (index - 1) / 2;
        return 4 * par + 3 - index;
    }

    private String getLeafHash(String value) {
        return Arrays.toString(AccumulatorUtils.getSha256(value.getBytes()));
    }

    private String getVertexesHash(int index) {
        if (index < hashed_data.size() / 2) {
            return Arrays.toString(AccumulatorUtils.getSha256((
                    hashed_data.get(2 * index + 1) + hashed_data.get(2 * index + 2)).getBytes()));
        } else return hashed_data.get(index);
    }
}
