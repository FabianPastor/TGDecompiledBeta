package org.telegram.messenger.exoplayer2.ext.flac;

import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;

final class FlacDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FlacDecoderException> {
    private final FlacDecoderJni decoderJni;
    private final int maxOutputBufferSize;

    public FlacDecoder(int numInputBuffers, int numOutputBuffers, List<byte[]> initializationData) throws FlacDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        if (initializationData.size() != 1) {
            throw new FlacDecoderException("Initialization data must be of length 1");
        }
        this.decoderJni = new FlacDecoderJni();
        this.decoderJni.setData(ByteBuffer.wrap((byte[]) initializationData.get(0)));
        try {
            FlacStreamInfo streamInfo = this.decoderJni.decodeMetadata();
            if (streamInfo == null) {
                throw new FlacDecoderException("Metadata decoding failed");
            }
            setInitialInputBufferSize(streamInfo.maxFrameSize);
            this.maxOutputBufferSize = streamInfo.maxDecodedFrameSize();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public String getName() {
        return "libflac";
    }

    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    protected SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    protected FlacDecoderException createUnexpectedDecodeException(Throwable error) {
        return new FlacDecoderException("Unexpected decode error", error);
    }

    protected FlacDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        if (reset) {
            this.decoderJni.flush();
        }
        this.decoderJni.setData(inputBuffer.data);
        ByteBuffer outputData = outputBuffer.init(inputBuffer.timeUs, this.maxOutputBufferSize);
        try {
            int result = this.decoderJni.decodeSample(outputData);
            if (result < 0) {
                return new FlacDecoderException("Frame decoding failed");
            }
            outputData.position(0);
            outputData.limit(result);
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void release() {
        super.release();
        this.decoderJni.release();
    }
}
