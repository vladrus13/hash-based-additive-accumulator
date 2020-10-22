package ru.accumulator;

import ru.util.AccumulatorUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    private MerkleTree makeTree(long n) {
        ArrayList<Long> I = new ArrayList<>();
        ArrayList<String> S = new ArrayList<>();
        long i = 0;
        long t = 1;
        while (t <= n) {
            I.add(AccumulatorUtils.hook_index(n, i));
            i++;
            t *= 2;
        }
        for (long index : I) {
            if (index > size) {
                S.add(AccumulatorUtils.toString(R.get((int) index)));
            } else {
                S.add(this.S.getLeaf(AccumulatorUtils.zeros(index)));
            }
        }
        return new MerkleTree(S);
    }

    public List<byte[]> prove(long position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    @Override
    public void clear() {
        size = 0;
        // TODO: S.clear();
        R.clear();
        elements.clear();
    }

    public void prove(long j, long i, LinkedList<byte[]> answer) {
        if (!(size <= j && j <= i)) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        MerkleTree previous = makeTree(i - 1);
        answer.addAll(List.of(elements.get((int) i), AccumulatorUtils.toByteArray(previous.getRoot())));
        if (i > j) {
            long i_next = AccumulatorUtils.rpred(i - 1, j);
            long leaf = AccumulatorUtils.zeros(i_next);
            answer.add(AccumulatorUtils.toByteArray(previous.getLeaf((int) leaf)));
            answer.addAll(previous.proof((int) leaf).stream().map(AccumulatorUtils::toByteArray).collect(Collectors.toList()));
            prove(j, i_next, answer);
        }
    }
}
