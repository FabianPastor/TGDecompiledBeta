package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
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
        Exception e;
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
        } catch (IOException e2) {
            e = e2;
            throw new IllegalStateException(e);
        } catch (InterruptedException e3) {
            e = e3;
            throw new IllegalStateException(e);
        }
    }

    public String getName() {
        return "libflac";
    }

    public DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(1);
    }

    public SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    public FlacDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        Exception e;
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
        } catch (IOException e2) {
            e = e2;
            throw new IllegalStateException(e);
        } catch (InterruptedException e3) {
            e = e3;
            throw new IllegalStateException(e);
        }
    }

    public void release() {
        super.release();
        this.decoderJni.release();
    }
}
