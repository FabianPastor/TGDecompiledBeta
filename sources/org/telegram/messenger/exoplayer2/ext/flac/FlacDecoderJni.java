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

    public static final class FlacFrameDecodeException extends Exception {
        public final int errorCode;

        public FlacFrameDecodeException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }
    }

    private native FlacStreamInfo flacDecodeMetadata(long j) throws IOException, InterruptedException;

    private native int flacDecodeToArray(long j, byte[] bArr) throws IOException, InterruptedException;

    private native int flacDecodeToBuffer(long j, ByteBuffer byteBuffer) throws IOException, InterruptedException;

    private native void flacFlush(long j);

    private native long flacGetDecodePosition(long j);

    private native long flacGetLastFrameFirstSampleIndex(long j);

    private native long flacGetLastFrameTimestamp(long j);

    private native long flacGetNextFrameFirstSampleIndex(long j);

    private native long flacGetSeekPosition(long j, long j2);

    private native String flacGetStateString(long j);

    private native long flacInit();

    private native boolean flacIsDecoderAtEndOfStream(long j);

    private native void flacRelease(long j);

    private native void flacReset(long j, long j2);

    public FlacDecoderJni() throws FlacDecoderException {
        if (this.nativeDecoderContext == 0) {
            throw new FlacDecoderException("Failed to initialize decoder");
        }
    }

    public void setData(ByteBuffer byteBufferData) {
        this.byteBufferData = byteBufferData;
        this.extractorInput = null;
        this.tempBuffer = null;
    }

    public void setData(ExtractorInput extractorInput) {
        this.byteBufferData = null;
        this.extractorInput = extractorInput;
        if (this.tempBuffer == null) {
            this.tempBuffer = new byte[8192];
        }
        this.endOfExtractorInput = false;
    }

    public boolean isEndOfData() {
        if (this.byteBufferData != null) {
            if (this.byteBufferData.remaining() == 0) {
                return true;
            }
            return false;
        } else if (this.extractorInput != null) {
            return this.endOfExtractorInput;
        } else {
            return true;
        }
    }

    public int read(ByteBuffer target) throws IOException, InterruptedException {
        int byteCount = target.remaining();
        if (this.byteBufferData != null) {
            byteCount = Math.min(byteCount, this.byteBufferData.remaining());
            int originalLimit = this.byteBufferData.limit();
            this.byteBufferData.limit(this.byteBufferData.position() + byteCount);
            target.put(this.byteBufferData);
            this.byteBufferData.limit(originalLimit);
        } else if (this.extractorInput != null) {
            byteCount = Math.min(byteCount, 8192);
            int read = readFromExtractorInput(0, byteCount);
            if (read < 4) {
                read += readFromExtractorInput(read, byteCount - read);
            }
            byteCount = read;
            target.put(this.tempBuffer, 0, byteCount);
        } else {
            int i = byteCount;
            return -1;
        }
        return byteCount;
    }

    public FlacStreamInfo decodeMetadata() throws IOException, InterruptedException {
        return flacDecodeMetadata(this.nativeDecoderContext);
    }

    public void decodeSampleWithBacktrackPosition(ByteBuffer output, long retryPosition) throws InterruptedException, IOException, FlacFrameDecodeException {
        try {
            decodeSample(output);
        } catch (IOException e) {
            if (retryPosition >= 0) {
                reset(retryPosition);
                if (this.extractorInput != null) {
                    this.extractorInput.setRetryPosition(retryPosition, e);
                }
            }
            throw e;
        }
    }

    public void decodeSample(ByteBuffer output) throws IOException, InterruptedException, FlacFrameDecodeException {
        int frameSize;
        output.clear();
        if (output.isDirect()) {
            frameSize = flacDecodeToBuffer(this.nativeDecoderContext, output);
        } else {
            frameSize = flacDecodeToArray(this.nativeDecoderContext, output.array());
        }
        if (frameSize >= 0) {
            output.limit(frameSize);
        } else if (isDecoderAtEndOfInput()) {
            output.limit(0);
        } else {
            throw new FlacFrameDecodeException("Cannot decode FLAC frame", frameSize);
        }
    }

    public long getDecodePosition() {
        return flacGetDecodePosition(this.nativeDecoderContext);
    }

    public long getLastFrameTimestamp() {
        return flacGetLastFrameTimestamp(this.nativeDecoderContext);
    }

    public long getLastFrameFirstSampleIndex() {
        return flacGetLastFrameFirstSampleIndex(this.nativeDecoderContext);
    }

    public long getNextFrameFirstSampleIndex() {
        return flacGetNextFrameFirstSampleIndex(this.nativeDecoderContext);
    }

    public long getSeekPosition(long timeUs) {
        return flacGetSeekPosition(this.nativeDecoderContext, timeUs);
    }

    public String getStateString() {
        return flacGetStateString(this.nativeDecoderContext);
    }

    public boolean isDecoderAtEndOfInput() {
        return flacIsDecoderAtEndOfStream(this.nativeDecoderContext);
    }

    public void flush() {
        flacFlush(this.nativeDecoderContext);
    }

    public void reset(long newPosition) {
        flacReset(this.nativeDecoderContext, newPosition);
    }

    public void release() {
        flacRelease(this.nativeDecoderContext);
    }

    private int readFromExtractorInput(int offset, int length) throws IOException, InterruptedException {
        int read = this.extractorInput.read(this.tempBuffer, offset, length);
        if (read != -1) {
            return read;
        }
        this.endOfExtractorInput = true;
        return 0;
    }
}
