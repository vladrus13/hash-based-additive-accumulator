package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MerkleTree {
    List<String> hashed_data;
    int capacity;

    public MerkleTree(List<String> source) {
        int size = 1;
        while (size <= source.size()) {
            size <<= 1;
        }

        capacity = source.size();
        size--;
        hashed_data = new ArrayList<>(Collections.nCopies(size, ""));

        for (int i = 0; i < source.size(); i++) {
            hashed_data.set(i + size / 2, getLeafHash(source.get(i)));
        }
        for (int i = size / 2 - 1; i >= 0; i--) {
            hashed_data.set(i, getInnerVertexesHash(i));
        }
    }

    public MerkleTree() {
        capacity = 0;
        hashed_data = new ArrayList<>();
    }


    private void expand() {
        List<String> new_storage = new ArrayList<>(Collections.nCopies(2 * hashed_data.size() + 1, ""));
        for (int i = 0; i < hashed_data.size(); i++) {
            new_storage.set(i + (int) AccumulatorUtils.max_leq_pow2((long) i + 1), hashed_data.get(i));
        }
        new_storage.set(0, getInnerVertexesHash(0));
        hashed_data = new_storage;
    }

    public void add(String value) {
        if (capacity == 1 + hashed_data.size() / 2) {
            expand();
        }

        hashed_data.set(capacity + hashed_data.size() / 2, getLeafHash(value));
        for (int currentState = (capacity + hashed_data.size() / 2 - 1) / 2; currentState >= 0;
             currentState = (currentState - 1) / 2) {
            hashed_data.set(currentState, getInnerVertexesHash(currentState));
        }
        capacity++;
    }

    public String getRoot() {
        return hashed_data.get(0);
    }

    public String getLeaf(int index) {
        return hashed_data.get(index + hashed_data.size() / 2);
    }


    /**
     * Generate proof by leaf's index
     *
     * @param index - index of some leaf
     * @return {@link List} of {@link String} where first element if leaf's data and then data of upcoming neighbours
     */
    public List<String> proof(int index) {
        List<String> ans = new ArrayList<>();
        ans.set(0, getLeaf(index));

        for (int currentState = index + hashed_data.size() / 2; currentState >= 0;
             currentState = (currentState - 1) / 2) {
            ans.add(getInnerVertexesHash(getNeighbour(currentState)));
        }

        return ans;
    }

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
    }

    private int getNeighbour(int index) {
        int par = (index - 1) / 2;
        return 4 * par + 3 - index;
    }

    private String getLeafHash(String value) {
        return Arrays.toString(AccumulatorUtils.getSha256(value.getBytes()));
    }

    private String getInnerVertexesHash(int index) {
        if (index * 2 + 1 < hashed_data.size()) {
            return Arrays.toString(AccumulatorUtils.getSha256((
                    hashed_data.get(2 * index + 1) + hashed_data.get(2 * index + 2)).getBytes()));
        } else return getLeaf(index);
    }


    //йопта здарова


}
