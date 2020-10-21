package ru.util;

import java.util.BitSet;

public class AccumulatorUtils {
    protected static BitSet d(BitSet n) {
        if (n.isEmpty()) {
            return n;
        } else {
            BitSet ans = new BitSet(zeros(n));
            ans.set(ans.size() - 1, true);
            return ans;
        }
    }

    protected static int zeros(BitSet n) {
        if (n.isEmpty()) {
            return 0;
        } else {
            int ans = 0;
            while (!n.get(ans)) {
                ans++;
            }
            return ans;
        }
    }

    protected static BitSet pred(BitSet n) {
        n.xor(d(n));
        return n;
    }

    protected static BitSet pred(int t, BitSet n) {
        if (t == 1) {
            return pred(n);
        } else {
            return pred(pred(t - 1, n));
        }
    }

    protected static BitSet rpred(BitSet i, BitSet n) {
        //TODO: check realization
        int count = 0;
        for (int cur = i.nextClearBit(0); cur != -1; cur = i.nextSetBit(cur)) {
            int ind = 0;
            if ((ind = n.nextSetBit(ind)) != -1) {
                n.set(ind, false);
            }
        }
        return n;
    }

    private static void merge(BitSet origin, BitSet added, int startInd) {
        for (int i = 0; i < added.length(); i++) {
            origin.set(startInd + i, added.get(i));
        }
    }

    protected static BitSet concatDigits(BitSet a, BitSet b, BitSet c) {
        if (a.isEmpty()) return concatDigits(b, c);
        if (b.isEmpty()) return concatDigits(a, c);
        if (c.isEmpty()) return concatDigits(a, b);
        BitSet ans = new BitSet(a.length() + b.length() + c.length());
        merge(ans, c, 0);
        merge(ans, b, c.length());
        merge(ans, a, b.length() + c.length());
        return ans;
    }

    protected static BitSet concatDigits(BitSet a, BitSet b) {
        if (a.isEmpty()) return concatDigits(b);
        if (b.isEmpty()) return concatDigits(a);
        BitSet ans = new BitSet(a.length() + b.length());
        merge(ans, b, 0);
        merge(ans, a, b.length());
        return ans;
    }

    protected static BitSet concatDigits(BitSet a) {
        if (a.isEmpty()) {
            //Todo: Throw smth?
            System.out.println("Warning! Zero provided to concat");
        }
        return a;
    }

}
