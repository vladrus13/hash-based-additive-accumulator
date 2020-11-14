package ru.util;

import java.util.List;

public class Prove {
    public final byte[] element;
    public final byte[] witnessHead;
    public final List<byte[]> witnessRest;

    public Prove(byte[] element, byte[] witnessHead, List<byte[]> witness) {
        this.element = element;
        this.witnessHead = witnessHead;
        this.witnessRest = witness;
    }
}
