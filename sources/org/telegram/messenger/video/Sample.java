package org.telegram.messenger.video;

public class Sample {
    private long offset;
    private long size;

    public Sample(long offset2, long size2) {
        this.offset = offset2;
        this.size = size2;
    }

    public long getOffset() {
        return this.offset;
    }

    public long getSize() {
        return this.size;
    }
}
