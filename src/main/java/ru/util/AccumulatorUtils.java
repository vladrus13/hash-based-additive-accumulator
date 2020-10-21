package ru.util;

import java.security.MessageDigest;
import java.util.BitSet;

public class AccumulatorUtils {
    public static BitSet d(BitSet n) {
        if (n.isEmpty()) {
            return n;
        } else {
            BitSet ans = new BitSet(zeros(n));
            ans.set(ans.size() - 1, true);
            return ans;
        }
    }

    public static int zeros(BitSet n) {
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

    public static BitSet pred(BitSet n) {
        n.set(zeros(n), false);
        return n;
    }

    public static BitSet pred(int t, BitSet n) {
        if (t == 1) {
            return pred(n);
        } else {
            return pred(pred(t - 1, n));
        }
    }

    public static BitSet rpred(BitSet i, BitSet n) {
        //TODO: check realization
        for (int cur = i.nextClearBit(0); cur != -1; cur = i.nextSetBit(cur)) {
            int ind = 0;
            if ((ind = n.nextSetBit(ind)) != -1) {
                n.set(ind, false);
            }
        }
        return n;
    }

    public static BitSet concatDigits(BitSet a, BitSet b, BitSet c) {
        if (c.isEmpty()) return concatDigits(a, b);
        BitSet ans = new BitSet(a.length() + b.length() + c.length());
        merge(ans, c, 0);
        merge(ans, b, c.length());
        if (a.isEmpty()) return concatDigits(b, c);
        if (b.isEmpty()) return concatDigits(a, c);
        merge(ans, a, b.length() + c.length());
        return ans;
    }

    public static BitSet concatDigits(BitSet a, BitSet b) {
        if (a.isEmpty()) return concatDigits(b);
        if (b.isEmpty()) return concatDigits(a);
        BitSet ans = new BitSet(a.length() + b.length());
        merge(ans, b, 0);
        merge(ans, a, b.length());
        return ans;
    }

    public static BitSet concatDigits(BitSet a) {
        if (a.isEmpty()) {
            //Todo: Throw smth?
            System.out.println("Warning! Zero provided to concat");
        }
        return a;
    }

    public static byte[] getSha256(byte[] value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value);
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getSha256(BitSet value) {
        return getSha256(value.toByteArray());
    }

    private static void merge(BitSet origin, BitSet added, int startInd) {
        for (int i = 0; i < added.length(); i++) {
            origin.set(startInd + i, added.get(i));
        }
    }


}
