package org.telegram.messenger.exoplayer2.ext.flac;

import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;

final class FlacDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException> {
    private final FlacDecoderJni decoderJni;
    private final int maxOutputBufferSize;

    public String getName() {
        return "libflac";
    }

    public FlacDecoder(int i, int i2, List<byte[]> list) throws FlacDecoderException {
        super(new DecoderInputBuffer[i], new SimpleOutputBuffer[i2]);
        if (list.size() != 1) {
            throw new FlacDecoderException("Initialization data must be of length 1");
        }
        this.decoderJni = new FlacDecoderJni();
        this.decoderJni.setData(ByteBuffer.wrap((byte[]) list.get(0)));
        try {
            i = this.decoderJni.decodeMetadata();
            if (i == 0) {
                throw new FlacDecoderException("Metadata decoding failed");
            }
            setInitialInputBufferSize(i.maxFrameSize);
            this.maxOutputBufferSize = i.maxDecodedFrameSize();
        } catch (int i3) {
            throw new IllegalStateException(i3);
        }
    }

    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    protected SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    protected FlacDecoderException createUnexpectedDecodeException(Throwable th) {
        return new FlacDecoderException("Unexpected decode error", th);
    }

    protected FlacDecoderException decode(DecoderInputBuffer decoderInputBuffer, SimpleOutputBuffer simpleOutputBuffer, boolean z) {
        if (z) {
            this.decoderJni.flush();
        }
        this.decoderJni.setData(decoderInputBuffer.data);
        decoderInputBuffer = simpleOutputBuffer.init(decoderInputBuffer.timeUs, this.maxOutputBufferSize);
        try {
            simpleOutputBuffer = this.decoderJni.decodeSample(decoderInputBuffer);
            if (simpleOutputBuffer < null) {
                return new FlacDecoderException("Frame decoding failed");
            }
            decoderInputBuffer.position(false);
            decoderInputBuffer.limit(simpleOutputBuffer);
            return null;
        } catch (DecoderInputBuffer decoderInputBuffer2) {
            throw new IllegalStateException(decoderInputBuffer2);
        }
    }

    public void release() {
        super.release();
        this.decoderJni.release();
    }
}
