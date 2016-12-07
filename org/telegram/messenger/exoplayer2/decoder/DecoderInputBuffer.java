package org.telegram.messenger.exoplayer2.decoder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;

public class DecoderInputBuffer extends Buffer {
    public static final int BUFFER_REPLACEMENT_MODE_DIRECT = 2;
    public static final int BUFFER_REPLACEMENT_MODE_DISABLED = 0;
    public static final int BUFFER_REPLACEMENT_MODE_NORMAL = 1;
    private final int bufferReplacementMode;
    public final CryptoInfo cryptoInfo = new CryptoInfo();
    public ByteBuffer data;
    public long timeUs;

    @Retention(RetentionPolicy.SOURCE)
    public @interface BufferReplacementMode {
    }

    public DecoderInputBuffer(int bufferReplacementMode) {
        this.bufferReplacementMode = bufferReplacementMode;
    }

    public void ensureSpaceForWrite(int length) throws IllegalStateException {
        if (this.data == null) {
            this.data = createReplacementByteBuffer(length);
            return;
        }
        int capacity = this.data.capacity();
        int position = this.data.position();
        int requiredCapacity = position + length;
        if (capacity < requiredCapacity) {
            ByteBuffer newData = createReplacementByteBuffer(requiredCapacity);
            if (position > 0) {
                this.data.position(0);
                this.data.limit(position);
                newData.put(this.data);
            }
            this.data = newData;
        }
    }

    public final boolean isEncrypted() {
        return getFlag(NUM);
    }

    public final void flip() {
        this.data.flip();
    }

    public void clear() {
        super.clear();
        if (this.data != null) {
            this.data.clear();
        }
    }

    private ByteBuffer createReplacementByteBuffer(int requiredCapacity) {
        if (this.bufferReplacementMode == 1) {
            return ByteBuffer.allocate(requiredCapacity);
        }
        if (this.bufferReplacementMode == 2) {
            return ByteBuffer.allocateDirect(requiredCapacity);
        }
        throw new IllegalStateException("Buffer too small (" + (this.data == null ? 0 : this.data.capacity()) + " < " + requiredCapacity + ")");
    }
}
