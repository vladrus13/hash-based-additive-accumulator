package ru.util;

import java.security.MessageDigest;
import java.util.Base64;

public class AccumulatorUtils {
    /**
     * Return x, where x is power of two and x not larger than number
     *
     * @param number upper bound for x
     * @return maximum not larger power of two
     */
    public static int maxNotLargerPowerOfTwo(int number) {
        int result = 1;
        while (2 * result <= number) {
            result *= 2;
        }
        return result;
    }

    /**
     * Return x, where x is power of two and number divides on x
     *
     * @param number number for which x is the divisor
     * @return
     */
    public static int maxDividingPowerOfTwo(int number) {
        return number & (-number);
    }

    /**
     * Retunrs count of consecutive zeros at end for a number in binary notation
     *
     * @return count of consecutive zeros at end
     */
    public static int lastZeroCount(int number) {
        int result = 0;
        while (number % 2 == 0) {
            number /= 2;
            result++;
        }
        return result;
    }

    /**
     * Returns x, where x is number with with the rightmost zeroed one
     *
     * @param number
     * @return
     */
    public static int predecessor(int number) {
        return number - maxDividingPowerOfTwo(number);
    }

    /**
     * Returns applied predecessor maximum number of times, before predecessor of i
     * won't get smaller than n
     *
     * @param i predecessor argument
     * @param n lower bound for predecessor(i)
     * @return i, where predecessor(i) < n
     */
    public static int rpred(int i, int n) {
        while (predecessor(i) >= n) {
            i = predecessor(i);
        }
        return i;
    }

    /**
     * Concatenate three byte arrays
     *
     * @param a first array
     * @param b second array
     * @param c third array
     * @return concatenation of arrays
     */
    public static byte[] concatDigits(byte[] a, byte[] b, byte[] c) {
        if (c == null || c.length == 0) {
            return concatDigits(a, b);
        }
        if (a == null || a.length == 0) {
            return concatDigits(b, c);
        }
        if (b == null || b.length == 0) {
            return concatDigits(a, c);
        }
        byte[] ans = new byte[a.length + b.length + c.length];
        merge(ans, c, 0);
        merge(ans, b, c.length);
        merge(ans, a, b.length + c.length);
        return ans;
    }

    /**
     * Concatenate two byte arrays
     *
     * @param a first array
     * @param b second array
     * @return concatenation of arrays
     */
    public static byte[] concatDigits(byte[] a, byte[] b) {
        if (a == null || a.length == 0) {
            return b;
        }
        if (b == null || b.length == 0) {
            return a;
        }
        byte[] ans = new byte[a.length + b.length];
        merge(ans, b, 0);
        merge(ans, a, b.length);
        return ans;
    }

    /**
     * Compute sha256 hash of value
     *
     * @param value parameter to be hashed
     * @return hash of value
     */
    public static byte[] getSha256(byte[] value) {
        if (value == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(value);
            return md.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add added array to end of origin
     *
     * @param origin     destination
     * @param added      copied part
     * @param startIndex index to start copying to
     */
    private static void merge(byte[] origin, byte[] added, int startIndex) {
        System.arraycopy(added, 0, origin, startIndex, added.length);
    }

    /**
     * Return base64 byte representation of string
     *
     * @param s string to be encoded
     * @return base64 encoding of s
     */
    public static byte[] toByteArray(String s) {
        return Base64.getDecoder().decode(s);
    }

    /**
     * Computes max x, not larger than i, which divides on 2 in power of u, but not on 2 in power of u + 1
     *
     * @param i x upper bound
     * @param u upper dividing bound
     */
    public static int bitLift(int i, int u) {
        if (i == 0) {
            return 0;
        }
        if (i % (1 << u) != 0) {
            return bitLift((i >> u) << u, u);
        }
        if (i % (1 << (u + 1)) != 0) {
            return i;
        }
        return ((i >> u) - 1) << u;
    }
}
