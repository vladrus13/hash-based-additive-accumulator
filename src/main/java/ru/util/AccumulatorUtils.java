package ru.util;

public class AccumulatorUtils {
    protected static long d(long n) {
        if (n == 0) {
            return 0;
        } else {
            return (long) (1) << zeros(n);
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
        return n - zeros(n);
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

}
