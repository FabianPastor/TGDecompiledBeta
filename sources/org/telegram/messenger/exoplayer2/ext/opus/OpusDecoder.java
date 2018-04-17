package org.telegram.messenger.exoplayer2.ext.opus;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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
        List<byte[]> list = initializationData;
        ExoMediaCrypto exoMediaCrypto2 = exoMediaCrypto;
        super(new DecoderInputBuffer[numInputBuffers], new SimpleOutputBuffer[numOutputBuffers]);
        this.exoMediaCrypto = exoMediaCrypto2;
        if (exoMediaCrypto2 == null || OpusLibrary.opusIsSecureDecodeSupported()) {
            byte[] headerBytes = (byte[]) list.get(0);
            if (headerBytes.length < 19) {
                throw new OpusDecoderException("Header size is too small.");
            }
            r7.channelCount = headerBytes[9] & 255;
            if (r7.channelCount > 8) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid channel count: ");
                stringBuilder.append(r7.channelCount);
                throw new OpusDecoderException(stringBuilder.toString());
            }
            int numStreams;
            int numCoupled;
            int preskip = readLittleEndian16(headerBytes, 10);
            int gain = readLittleEndian16(headerBytes, 16);
            byte[] streamMap = new byte[8];
            if (headerBytes[18] == (byte) 0) {
                if (r7.channelCount > 2) {
                    throw new OpusDecoderException("Invalid Header, missing stream map.");
                }
                int numCoupled2 = r7.channelCount == 2 ? 1 : 0;
                streamMap[0] = (byte) 0;
                streamMap[1] = (byte) 1;
                numStreams = 1;
                numCoupled = numCoupled2;
            } else if (headerBytes.length < r7.channelCount + 21) {
                throw new OpusDecoderException("Header size is too small.");
            } else {
                int numStreams2 = headerBytes[19] & 255;
                int numCoupled3 = headerBytes[20] & 255;
                System.arraycopy(headerBytes, 21, streamMap, 0, r7.channelCount);
                numStreams = numStreams2;
                numCoupled = numCoupled3;
            }
            if (initializationData.size() == 3) {
                if (((byte[]) list.get(1)).length == 8) {
                    if (((byte[]) list.get(2)).length == 8) {
                        long codecDelayNs = ByteBuffer.wrap((byte[]) list.get(1)).order(ByteOrder.nativeOrder()).getLong();
                        long seekPreRollNs = ByteBuffer.wrap((byte[]) list.get(2)).order(ByteOrder.nativeOrder()).getLong();
                        r7.headerSkipSamples = nsToSamples(codecDelayNs);
                        r7.headerSeekPreRollSamples = nsToSamples(seekPreRollNs);
                    }
                }
                throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
            }
            r7.headerSkipSamples = preskip;
            r7.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
            r7.nativeDecoderContext = opusInit(SAMPLE_RATE, r7.channelCount, numStreams, numCoupled, gain, streamMap);
            if (r7.nativeDecoderContext == 0) {
                throw new OpusDecoderException("Failed to initialize decoder");
            }
            setInitialInputBufferSize(initialInputBufferSize);
            return;
        }
        throw new OpusDecoderException("Opus decoder does not support secure decode.");
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("libopus");
        stringBuilder.append(OpusLibrary.getVersion());
        return stringBuilder.toString();
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
        OpusDecoder opusDecoder = this;
        DecoderInputBuffer decoderInputBuffer = inputBuffer;
        SimpleOutputBuffer simpleOutputBuffer = outputBuffer;
        if (reset) {
            opusReset(opusDecoder.nativeDecoderContext);
            opusDecoder.skipSamples = decoderInputBuffer.timeUs == 0 ? opusDecoder.headerSkipSamples : opusDecoder.headerSeekPreRollSamples;
        }
        ByteBuffer inputData = decoderInputBuffer.data;
        CryptoInfo cryptoInfo = decoderInputBuffer.cryptoInfo;
        ByteBuffer byteBuffer;
        if (inputBuffer.isEncrypted()) {
            long j = opusDecoder.nativeDecoderContext;
            long j2 = decoderInputBuffer.timeUs;
            int limit = inputData.limit();
            ExoMediaCrypto exoMediaCrypto = opusDecoder.exoMediaCrypto;
            int i = cryptoInfo.mode;
            byte[] bArr = cryptoInfo.key;
            byte[] bArr2 = cryptoInfo.iv;
            byte[] bArr3 = bArr2;
            byte[] bArr4 = bArr;
            ByteBuffer inputData2 = inputData;
            result = opusSecureDecode(j, j2, inputData, limit, simpleOutputBuffer, SAMPLE_RATE, exoMediaCrypto, i, bArr4, bArr3, cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
            byteBuffer = inputData2;
            OpusDecoder opusDecoder2 = this;
            DecoderInputBuffer decoderInputBuffer2 = inputBuffer;
        } else {
            byteBuffer = inputData;
            result = opusDecode(this.nativeDecoderContext, inputBuffer.timeUs, byteBuffer, byteBuffer.limit(), outputBuffer);
        }
        if (result >= 0) {
            SimpleOutputBuffer simpleOutputBuffer2 = outputBuffer;
            ByteBuffer outputData = simpleOutputBuffer2.data;
            outputData.position(0);
            outputData.limit(result);
            if (opusDecoder2.skipSamples > 0) {
                int bytesPerSample = opusDecoder2.channelCount * 2;
                int skipBytes = opusDecoder2.skipSamples * bytesPerSample;
                if (result <= skipBytes) {
                    opusDecoder2.skipSamples -= result / bytesPerSample;
                    simpleOutputBuffer2.addFlag(Integer.MIN_VALUE);
                    outputData.position(result);
                } else {
                    opusDecoder2.skipSamples = 0;
                    outputData.position(skipBytes);
                }
            }
            return null;
        } else if (result == -2) {
            String message = new StringBuilder();
            message.append("Drm error: ");
            message.append(opusDecoder2.opusGetErrorMessage(opusDecoder2.nativeDecoderContext));
            message = message.toString();
            return new OpusDecoderException(message, new DecryptionException(opusDecoder2.opusGetErrorCode(opusDecoder2.nativeDecoderContext), message));
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Decode error: ");
            stringBuilder.append(opusDecoder2.opusGetErrorMessage((long) result));
            return new OpusDecoderException(stringBuilder.toString());
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
        return (int) ((48000 * ns) / C0542C.NANOS_PER_SECOND);
    }

    private static int readLittleEndian16(byte[] input, int offset) {
        return (input[offset] & 255) | ((input[offset + 1] & 255) << 8);
    }
}
