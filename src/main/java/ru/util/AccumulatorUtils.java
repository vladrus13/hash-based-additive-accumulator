package ru.util;

public class AccumulatorUtils {
    protected static long d(long n) {
        if (n == 0) {
            return 0;
        } else {
            return ((long) (1)) << zeros(n);
        }
    }

    protected static int zeros(long n) {
        if (n == 0) {
            return 0;
        } else {
            int ans = 0;
            while ((n & (1 << ans)) == 0) {
                ans++;
            }
            return ans;
        }
    }

    protected static long pred(long n) {
        //some optimisation kekw
        if (n % 2 == 0) {
            return n - 1;
        }
        return n - d(n);
    }

    protected static long pred(int t, long n) {
        if (t == 1) {
            return pred(n);
        } else {
            return pred(pred(t - 1, n));
        }
    }

    protected static long rpred(int i, long n) {
        //TODO: check realization
        int count = 0;
        for (long cur = 1; cur <= i; cur *= 2) {
            if ((i & cur) > 0) {
                count++;
            }
        }
        long ans = n;
        for (long cur = 1; cur <= ans && count > 0; cur *= 2) {
            if ((cur & ans) > 0) {
                ans ^= cur;
                count--;
            }
        }
        return ans;
    }

    protected static long concatDigits(long a, long b, long c) {
        if (a == 0) return concatDigits(b, c);
        if (b == 0) return concatDigits(a, c);
        if (c == 0) return concatDigits(a, b);
        int b_len = getLen(b), c_len = getLen(c);
        return a << (b_len + c_len) + b << (c_len) + c;
    }

    protected static long concatDigits(long a, long b) {
        if (a == 0) return concatDigits(b);
        if (b == 0) return concatDigits(a);
        int b_len = getLen(b);
        return a << (b_len) + b;
    }

    protected static long concatDigits(long a) {
        if (a == 0) {
            //Todo: Throw smth?
            a = -0;
        }
        return a;
    }


    private static int getLen(long x) {
        int ans = 0;
        for (long cur = 1; cur <= x; cur *= 2, ans++) ;
        return ans;
    }
}
