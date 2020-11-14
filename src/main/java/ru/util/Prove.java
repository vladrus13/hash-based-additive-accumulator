package ru.util;

import java.util.List;

public class Prove {
    public final byte[] element;
    public final byte[] witnessHead;
    public final List<byte[]> witnessRest;

    public Prove(byte[] element, byte[] withessHead, List<byte[]> witness) {
        this.element = element;
        this.witnessHead = withessHead;
        this.witnessRest = witness;
    }
}
