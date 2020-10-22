package ru.util;

import java.security.MessageDigest;
import java.util.Base64;

public class AccumulatorUtils {
    public static long max_leq_pow2(long n) {
        long ans = 1;
        while (ans << 1 <= n) {
            ans <<= 1;
        }
        return ans;
    }

    //Return the maximum power of two that divides n. Return 0 for n == 0."""
    public static long d(long n) {
        return n & (-n);
    }

    public static int zeros(long n) {
        int ans = 0;
        while ((n & 1) == 0) {
            n /= 2;
            ans++;
        }
        return ans;
    }

    public static long pred(long n) {
        return n - d(n);
    }

    public static long hook_index(long n, long t) {
        long d = 1 << t;
        long r = n & -d;
        if ((n & d) != 0) {
            return r;
        } else {
            return (r - 1) & -d;
        }
    }

    public static long rpred(long i, long n) {
        while (pred(i) >= n) {
            i = pred(i);
        }
        return i;
    }

    public static byte[] concatDigits(byte[] a, byte[] b, byte[] c) {
        if (c.length == 0) return concatDigits(a, b);
        if (a.length == 0) return concatDigits(b, c);
        if (b.length == 0) return concatDigits(a, c);
        byte[] ans = new byte[a.length + b.length + c.length];
        merge(ans, c, 0);
        merge(ans, b, c.length);
        merge(ans, a, b.length + c.length);
        return ans;
    }

    public static byte[] concatDigits(byte[] a, byte[] b) {
        if (a.length == 0) return concatDigits(b);
        if (b.length == 0) return concatDigits(a);
        byte[] ans = new byte[a.length + b.length];
        merge(ans, b, 0);
        merge(ans, a, b.length);
        return ans;
    }

    public static byte[] concatDigits(byte[] a) {
        if (a.length == 0) {
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

    private static void merge(byte[] origin, byte[] added, int startInd) {
        System.arraycopy(added, 0, origin, startInd, added.length);
    }

    public static byte[] toByteArray(String s) {
        return Base64.getDecoder().decode(s);
    }

    public static String toString(byte[] b) {
        return new String(Base64.getEncoder().encode(b));
    }
}
