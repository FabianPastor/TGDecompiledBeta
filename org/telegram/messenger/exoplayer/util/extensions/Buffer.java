package org.telegram.messenger.exoplayer.util.extensions;

public abstract class Buffer {
    public static final int FLAG_DECODE_ONLY = 2;
    public static final int FLAG_END_OF_STREAM = 1;
    private int flags;

    public void reset() {
        this.flags = 0;
    }

    public final void setFlag(int flag) {
        this.flags |= flag;
    }

    public final boolean getFlag(int flag) {
        return (this.flags & flag) == flag;
    }
}
