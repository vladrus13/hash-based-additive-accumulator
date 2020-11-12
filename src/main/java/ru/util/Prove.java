package ru.util;

import java.util.List;

public class Prove {
    public final byte[] x;
    public final byte[] rh;
    public final List<byte[]> w;

    public Prove(byte[] x, byte[] rh, List<byte[]> w) {
        this.x = x;
        this.rh = rh;
        this.w = w;
    }
}
