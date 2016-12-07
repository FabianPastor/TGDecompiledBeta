package org.telegram.messenger.exoplayer2.upstream;

public final class Allocation {
    public final byte[] data;
    private final int offset;

    public Allocation(byte[] data, int offset) {
        this.data = data;
        this.offset = offset;
    }

    public int translateOffset(int offset) {
        return this.offset + offset;
    }
}
