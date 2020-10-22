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

    public List<byte[]> prove(long position) {
        LinkedList<byte[]> answer = new LinkedList<>();
        prove(position, size, answer);
        return answer;
    }

    private MerkleTree getTree(long n) {
        long i = 0;
        long t = 1;
        ArrayList<Long> I = new ArrayList<>();
        while (t <= n) {
            long index = AccumulatorUtils.hook_index(n, i);
            I.add(index);
            i++;
            t *= 2;
        }
        ArrayList<String> S = new ArrayList<>();
        for (long index : I) {
            if (index > size) {
                S.add(AccumulatorUtils.toString(R.get((int) index)));
            } else {
                S.add(S.get(AccumulatorUtils.zeros(index)));
            }
        }
        return new MerkleTree(S);
    }

    private void prove(long i, long j, LinkedList<byte[]> answer) {
        if (!(size <= i && i <= j)) {
            throw new IllegalArgumentException("Size less than first second argument or second less than first");
        }
        MerkleTree previousMerkle = getTree(j - 1);
        answer.addAll(List.of(elements.get((int) j), AccumulatorUtils.toByteArray(previousMerkle.getRoot())));
        if (j > i) {
            long i_n = AccumulatorUtils.rpred(j - 1, i);
            long leaf = AccumulatorUtils.zeros(i_n);
            // TODO: answer.add(previousMerkle.get(leaf));
            answer.addAll(previousMerkle.proof((int) leaf).stream().map(AccumulatorUtils::toByteArray).collect(Collectors.toList()));
            prove(i, i_n, answer);
        }
    }
}
