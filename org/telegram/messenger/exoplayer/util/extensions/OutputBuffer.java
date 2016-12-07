package org.telegram.messenger.exoplayer.util.extensions;

public abstract class OutputBuffer extends Buffer {
    public long timestampUs;

    public abstract void release();
}
