package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;

final class FlacDecoderJni {
    private static final int TEMP_BUFFER_SIZE = 8192;
    private ByteBuffer byteBufferData;
    private boolean endOfExtractorInput;
    private ExtractorInput extractorInput;
    private final long nativeDecoderContext = flacInit();
    private byte[] tempBuffer;

    private native FlacStreamInfo flacDecodeMetadata(long j) throws IOException, InterruptedException;

    private native int flacDecodeToArray(long j, byte[] bArr) throws IOException, InterruptedException;

    private native int flacDecodeToBuffer(long j, ByteBuffer byteBuffer) throws IOException, InterruptedException;

    private native void flacFlush(long j);

    private native long flacGetDecodePosition(long j);

    private native long flacGetLastTimestamp(long j);

    private native long flacGetSeekPosition(long j, long j2);

    private native String flacGetStateString(long j);

    private native long flacInit();

    private native void flacRelease(long j);

    private native void flacReset(long j, long j2);

    public FlacDecoderJni() throws FlacDecoderException {
        if (this.nativeDecoderContext == 0) {
            throw new FlacDecoderException("Failed to initialize decoder");
        }
    }

    public void setData(ByteBuffer byteBuffer) {
        this.byteBufferData = byteBuffer;
        this.extractorInput = null;
        this.tempBuffer = null;
    }

    public void setData(ExtractorInput extractorInput) {
        this.byteBufferData = null;
        this.extractorInput = extractorInput;
        if (this.tempBuffer == null) {
            this.tempBuffer = new byte[8192];
        }
        this.endOfExtractorInput = null;
    }

    public boolean isEndOfData() {
        boolean z = true;
        if (this.byteBufferData != null) {
            if (this.byteBufferData.remaining() != 0) {
                z = false;
            }
            return z;
        } else if (this.extractorInput != null) {
            return this.endOfExtractorInput;
        } else {
            return true;
        }
    }

    public int read(ByteBuffer byteBuffer) throws IOException, InterruptedException {
        int remaining = byteBuffer.remaining();
        if (this.byteBufferData != null) {
            remaining = Math.min(remaining, this.byteBufferData.remaining());
            int limit = this.byteBufferData.limit();
            this.byteBufferData.limit(this.byteBufferData.position() + remaining);
            byteBuffer.put(this.byteBufferData);
            this.byteBufferData.limit(limit);
        } else if (this.extractorInput == null) {
            return -1;
        } else {
            remaining = Math.min(remaining, 8192);
            int readFromExtractorInput = readFromExtractorInput(0, remaining);
            if (readFromExtractorInput < 4) {
                readFromExtractorInput += readFromExtractorInput(readFromExtractorInput, remaining - readFromExtractorInput);
            }
            remaining = readFromExtractorInput;
            byteBuffer.put(this.tempBuffer, 0, remaining);
        }
        return remaining;
    }

    public FlacStreamInfo decodeMetadata() throws IOException, InterruptedException {
        return flacDecodeMetadata(this.nativeDecoderContext);
    }

    public int decodeSample(ByteBuffer byteBuffer) throws IOException, InterruptedException {
        if (byteBuffer.isDirect()) {
            return flacDecodeToBuffer(this.nativeDecoderContext, byteBuffer);
        }
        return flacDecodeToArray(this.nativeDecoderContext, byteBuffer.array());
    }

    public long getDecodePosition() {
        return flacGetDecodePosition(this.nativeDecoderContext);
    }

    public long getLastSampleTimestamp() {
        return flacGetLastTimestamp(this.nativeDecoderContext);
    }

    public long getSeekPosition(long j) {
        return flacGetSeekPosition(this.nativeDecoderContext, j);
    }

    public String getStateString() {
        return flacGetStateString(this.nativeDecoderContext);
    }

    public void flush() {
        flacFlush(this.nativeDecoderContext);
    }

    public void reset(long j) {
        flacReset(this.nativeDecoderContext, j);
    }

    public void release() {
        flacRelease(this.nativeDecoderContext);
    }

    private int readFromExtractorInput(int i, int i2) throws IOException, InterruptedException {
        i = this.extractorInput.read(this.tempBuffer, i, i2);
        if (i != -1) {
            return i;
        }
        this.endOfExtractorInput = true;
        return 0;
    }
}
