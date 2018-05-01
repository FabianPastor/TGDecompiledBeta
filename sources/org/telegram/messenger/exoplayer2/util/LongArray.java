package org.telegram.messenger.exoplayer2.util;

import java.util.Arrays;

public final class LongArray {
    private static final int DEFAULT_INITIAL_CAPACITY = 32;
    private int size;
    private long[] values;

    public LongArray() {
        this(32);
    }

    public LongArray(int i) {
        this.values = new long[i];
    }

    public void add(long j) {
        if (this.size == this.values.length) {
            this.values = Arrays.copyOf(this.values, this.size * 2);
        }
        long[] jArr = this.values;
        int i = this.size;
        this.size = i + 1;
        jArr[i] = j;
    }

    public long get(int i) {
        if (i >= 0) {
            if (i < this.size) {
                return this.values[i];
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid index ");
        stringBuilder.append(i);
        stringBuilder.append(", size is ");
        stringBuilder.append(this.size);
        throw new IndexOutOfBoundsException(stringBuilder.toString());
    }

    public int size() {
        return this.size;
    }

    public long[] toArray() {
        return Arrays.copyOf(this.values, this.size);
    }
}
