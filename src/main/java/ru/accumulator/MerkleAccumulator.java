package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MerkleAccumulator implements Accumulator {
    private long size;
    private final MerkleTree S;
    private final ArrayList<byte[]> R;
    private final ArrayList<byte[]> elements;

    public MerkleAccumulator() {
        size = 0;
        S = new MerkleTree();
        R = new ArrayList<>();
        elements = new ArrayList<>();
    }


    public long size() {
        return size;
    }

    public byte[] get(long position) {
        if (position == 0) {
            return null;
        } else {
            return AccumulatorUtils.toByteArray(S.getLeaf(AccumulatorUtils.zeros(position)));
        }
    }

    public void add(byte[] element) {
        byte[] root = AccumulatorUtils.toByteArray(S.getRoot());
        size++;
        byte[] result = AccumulatorUtils.getSha256(AccumulatorUtils.concatDigits(element, root));
        // TODO S.set(zeros(size), result)
        elements.add(element);
        R.add(result);
    }

    public List<byte[]> prove(long position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    public void prove(long j, long i, LinkedList<byte[]> answer) {
        if (!(size <= j && j <= i)) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        // TODO code
    }
}
