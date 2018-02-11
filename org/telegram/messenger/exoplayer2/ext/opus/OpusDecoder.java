package org.telegram.messenger.exoplayer2.ext.opus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.decoder.CryptoInfo;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.drm.DecryptionException;
import org.telegram.messenger.exoplayer2.drm.ExoMediaCrypto;

final class OpusDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, OpusDecoderException> {
    private static final int DECODE_ERROR = -1;
    private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
    private static final int DRM_ERROR = -2;
    private static final int NO_ERROR = 0;
    private static final int SAMPLE_RATE = 48000;
    private final int channelCount;
    private final ExoMediaCrypto exoMediaCrypto;
    private final int headerSeekPreRollSamples;
    private final int headerSkipSamples;
    private final long nativeDecoderContext;
    private int skipSamples;

    private native void opusClose(long j);

    private native int opusDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer);

    private native int opusGetErrorCode(long j);

    private native String opusGetErrorMessage(long j);

    private native long opusInit(int i, int i2, int i3, int i4, int i5, byte[] bArr);

    private native void opusReset(long j);

    private native int opusSecureDecode(long j, long j2, ByteBuffer byteBuffer, int i, SimpleOutputBuffer simpleOutputBuffer, int i2, ExoMediaCrypto exoMediaCrypto, int i3, byte[] bArr, byte[] bArr2, int i4, int[] iArr, int[] iArr2);

    public OpusDecoder(int numInputBuffers, int numOutputBuffers, int initialInputBufferSize, List<byte[]> initializationData, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        this.exoMediaCrypto = exoMediaCrypto;
        if (exoMediaCrypto == null || OpusLibrary.opusIsSecureDecodeSupported()) {
            byte[] headerBytes = (byte[]) initializationData.get(0);
            if (headerBytes.length < 19) {
                throw new OpusDecoderException("Header size is too small.");
            }
            this.channelCount = headerBytes[9] & 255;
            if (this.channelCount > 8) {
                throw new OpusDecoderException("Invalid channel count: " + this.channelCount);
            }
            int numStreams;
            int numCoupled;
            int preskip = readLittleEndian16(headerBytes, 10);
            int gain = readLittleEndian16(headerBytes, 16);
            byte[] streamMap = new byte[8];
            if (headerBytes[18] == (byte) 0) {
                if (this.channelCount > 2) {
                    throw new OpusDecoderException("Invalid Header, missing stream map.");
                }
                numStreams = 1;
                numCoupled = this.channelCount == 2 ? 1 : 0;
                streamMap[0] = (byte) 0;
                streamMap[1] = (byte) 1;
            } else if (headerBytes.length < this.channelCount + 21) {
                throw new OpusDecoderException("Header size is too small.");
            } else {
                numStreams = headerBytes[19] & 255;
                numCoupled = headerBytes[20] & 255;
                System.arraycopy(headerBytes, 21, streamMap, 0, this.channelCount);
            }
            if (initializationData.size() != 3) {
                this.headerSkipSamples = preskip;
                this.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
            } else if (((byte[]) initializationData.get(1)).length == 8 && ((byte[]) initializationData.get(2)).length == 8) {
                long codecDelayNs = ByteBuffer.wrap((byte[]) initializationData.get(1)).order(ByteOrder.nativeOrder()).getLong();
                long seekPreRollNs = ByteBuffer.wrap((byte[]) initializationData.get(2)).order(ByteOrder.nativeOrder()).getLong();
                this.headerSkipSamples = nsToSamples(codecDelayNs);
                this.headerSeekPreRollSamples = nsToSamples(seekPreRollNs);
            } else {
                throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
            }
            this.nativeDecoderContext = opusInit(SAMPLE_RATE, this.channelCount, numStreams, numCoupled, gain, streamMap);
            if (this.nativeDecoderContext == 0) {
                throw new OpusDecoderException("Failed to initialize decoder");
            }
            setInitialInputBufferSize(initialInputBufferSize);
            return;
        }
        throw new OpusDecoderException("Opus decoder does not support secure decode.");
    }

    public String getName() {
        return "libopus" + OpusLibrary.getVersion();
    }

    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(2);
    }

    protected SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    protected OpusDecoderException createUnexpectedDecodeException(Throwable error) {
        return new OpusDecoderException("Unexpected decode error", error);
    }

    protected OpusDecoderException decode(DecoderInputBuffer inputBuffer, SimpleOutputBuffer outputBuffer, boolean reset) {
        int result;
        if (reset) {
            opusReset(this.nativeDecoderContext);
            this.skipSamples = inputBuffer.timeUs == 0 ? this.headerSkipSamples : this.headerSeekPreRollSamples;
        }
        ByteBuffer inputData = inputBuffer.data;
        CryptoInfo cryptoInfo = inputBuffer.cryptoInfo;
        if (inputBuffer.isEncrypted()) {
            result = opusSecureDecode(this.nativeDecoderContext, inputBuffer.timeUs, inputData, inputData.limit(), outputBuffer, SAMPLE_RATE, this.exoMediaCrypto, cryptoInfo.mode, cryptoInfo.key, cryptoInfo.iv, cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
        } else {
            result = opusDecode(this.nativeDecoderContext, inputBuffer.timeUs, inputData, inputData.limit(), outputBuffer);
        }
        if (result >= 0) {
            ByteBuffer outputData = outputBuffer.data;
            outputData.position(0);
            outputData.limit(result);
            if (this.skipSamples > 0) {
                int bytesPerSample = this.channelCount * 2;
                int skipBytes = this.skipSamples * bytesPerSample;
                if (result <= skipBytes) {
                    this.skipSamples -= result / bytesPerSample;
                    outputBuffer.addFlag(Integer.MIN_VALUE);
                    outputData.position(result);
                } else {
                    this.skipSamples = 0;
                    outputData.position(skipBytes);
                }
            }
            return null;
        } else if (result == -2) {
            String message = "Drm error: " + opusGetErrorMessage(this.nativeDecoderContext);
            return new OpusDecoderException(message, new DecryptionException(opusGetErrorCode(this.nativeDecoderContext), message));
        } else {
            return new OpusDecoderException("Decode error: " + opusGetErrorMessage((long) result));
        }
    }

    public void release() {
        super.release();
        opusClose(this.nativeDecoderContext);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    private static int nsToSamples(long ns) {
        return (int) ((48000 * ns) / C.NANOS_PER_SECOND);
    }

    private static int readLittleEndian16(byte[] input, int offset) {
        return (input[offset] & 255) | ((input[offset + 1] & 255) << 8);
    }
}
