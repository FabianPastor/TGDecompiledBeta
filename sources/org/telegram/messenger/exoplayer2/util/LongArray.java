package org.telegram.messenger.exoplayer2.util;

import java.util.Arrays;

public final class LongArray {
    private static final int DEFAULT_INITIAL_CAPACITY = 32;
    private int size;
    private long[] values;

    public LongArray() {
        this(32);
    }

    public LongArray(int initialCapacity) {
        this.values = new long[initialCapacity];
    }

    public void add(long value) {
        if (this.size == this.values.length) {
            this.values = Arrays.copyOf(this.values, this.size * 2);
        }
        long[] jArr = this.values;
        int i = this.size;
        this.size = i + 1;
        jArr[i] = value;
    }

    public long get(int index) {
        if (index >= 0) {
            if (index < this.size) {
                return this.values[index];
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid index ");
        stringBuilder.append(index);
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
