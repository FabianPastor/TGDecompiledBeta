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

    public int getSampleRate() {
        return SAMPLE_RATE;
    }

    public OpusDecoder(int i, int i2, int i3, List<byte[]> list, ExoMediaCrypto exoMediaCrypto) throws OpusDecoderException {
        super(new DecoderInputBuffer[i], new SimpleOutputBuffer[i2]);
        this.exoMediaCrypto = exoMediaCrypto;
        if (exoMediaCrypto == null || OpusLibrary.opusIsSecureDecodeSupported() != 0) {
            byte[] bArr = (byte[]) list.get(0);
            if (bArr.length < 19) {
                throw new OpusDecoderException("Header size is too small.");
            }
            this.channelCount = bArr[9] & 255;
            if (this.channelCount > 8) {
                i2 = new StringBuilder();
                i2.append("Invalid channel count: ");
                i2.append(this.channelCount);
                throw new OpusDecoderException(i2.toString());
            }
            int i4;
            int i5;
            int readLittleEndian16 = readLittleEndian16(bArr, 10);
            int readLittleEndian162 = readLittleEndian16(bArr, 16);
            Object obj = new byte[8];
            if (bArr[18] == (byte) 0) {
                if (this.channelCount > 2) {
                    throw new OpusDecoderException("Invalid Header, missing stream map.");
                }
                i2 = this.channelCount == 2 ? 1 : 0;
                obj[0] = null;
                obj[1] = 1;
                i4 = i2;
                i5 = 1;
            } else if (bArr.length < this.channelCount + 21) {
                throw new OpusDecoderException("Header size is too small.");
            } else {
                exoMediaCrypto = bArr[19] & 255;
                int i6 = bArr[20] & 255;
                System.arraycopy(bArr, 21, obj, 0, this.channelCount);
                i5 = exoMediaCrypto;
                i4 = i6;
            }
            if (list.size() == 3) {
                if (((byte[]) list.get(1)).length == 8) {
                    if (((byte[]) list.get(2)).length == 8) {
                        i = ByteBuffer.wrap((byte[]) list.get(1)).order(ByteOrder.nativeOrder()).getLong();
                        list = ByteBuffer.wrap((byte[]) list.get(2)).order(ByteOrder.nativeOrder()).getLong();
                        this.headerSkipSamples = nsToSamples(i);
                        this.headerSeekPreRollSamples = nsToSamples(list);
                    }
                }
                throw new OpusDecoderException("Invalid Codec Delay or Seek Preroll");
            }
            this.headerSkipSamples = readLittleEndian16;
            this.headerSeekPreRollSamples = DEFAULT_SEEK_PRE_ROLL_SAMPLES;
            this.nativeDecoderContext = opusInit(SAMPLE_RATE, this.channelCount, i5, i4, readLittleEndian162, obj);
            if (this.nativeDecoderContext == null) {
                throw new OpusDecoderException("Failed to initialize decoder");
            }
            setInitialInputBufferSize(i3);
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

    protected OpusDecoderException createUnexpectedDecodeException(Throwable th) {
        return new OpusDecoderException("Unexpected decode error", th);
    }

    protected OpusDecoderException decode(DecoderInputBuffer decoderInputBuffer, SimpleOutputBuffer simpleOutputBuffer, boolean z) {
        int opusSecureDecode;
        OpusDecoder opusDecoder = this;
        DecoderInputBuffer decoderInputBuffer2 = decoderInputBuffer;
        SimpleOutputBuffer simpleOutputBuffer2 = simpleOutputBuffer;
        if (z) {
            opusReset(opusDecoder.nativeDecoderContext);
            opusDecoder.skipSamples = decoderInputBuffer2.timeUs == 0 ? opusDecoder.headerSkipSamples : opusDecoder.headerSeekPreRollSamples;
        }
        ByteBuffer byteBuffer = decoderInputBuffer2.data;
        CryptoInfo cryptoInfo = decoderInputBuffer2.cryptoInfo;
        if (decoderInputBuffer.isEncrypted()) {
            opusSecureDecode = opusSecureDecode(opusDecoder.nativeDecoderContext, decoderInputBuffer2.timeUs, byteBuffer, byteBuffer.limit(), simpleOutputBuffer2, SAMPLE_RATE, opusDecoder.exoMediaCrypto, cryptoInfo.mode, cryptoInfo.key, cryptoInfo.iv, cryptoInfo.numSubSamples, cryptoInfo.numBytesOfClearData, cryptoInfo.numBytesOfEncryptedData);
            OpusDecoder opusDecoder2 = this;
        } else {
            opusSecureDecode = opusDecode(opusDecoder.nativeDecoderContext, decoderInputBuffer2.timeUs, byteBuffer, byteBuffer.limit(), simpleOutputBuffer);
        }
        if (opusSecureDecode >= 0) {
            SimpleOutputBuffer simpleOutputBuffer3 = simpleOutputBuffer;
            ByteBuffer byteBuffer2 = simpleOutputBuffer3.data;
            byteBuffer2.position(0);
            byteBuffer2.limit(opusSecureDecode);
            if (opusDecoder2.skipSamples > 0) {
                int i = opusDecoder2.channelCount * 2;
                int i2 = opusDecoder2.skipSamples * i;
                if (opusSecureDecode <= i2) {
                    opusDecoder2.skipSamples -= opusSecureDecode / i;
                    simpleOutputBuffer3.addFlag(Integer.MIN_VALUE);
                    byteBuffer2.position(opusSecureDecode);
                } else {
                    opusDecoder2.skipSamples = 0;
                    byteBuffer2.position(i2);
                }
            }
            return null;
        } else if (opusSecureDecode == -2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Drm error: ");
            stringBuilder.append(opusDecoder2.opusGetErrorMessage(opusDecoder2.nativeDecoderContext));
            String stringBuilder2 = stringBuilder.toString();
            return new OpusDecoderException(stringBuilder2, new DecryptionException(opusDecoder2.opusGetErrorCode(opusDecoder2.nativeDecoderContext), stringBuilder2));
        } else {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Decode error: ");
            stringBuilder3.append(opusDecoder2.opusGetErrorMessage((long) opusSecureDecode));
            return new OpusDecoderException(stringBuilder3.toString());
        }
    }

    public void release() {
        super.release();
        opusClose(this.nativeDecoderContext);
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    private static int nsToSamples(long j) {
        return (int) ((j * 48000) / C0542C.NANOS_PER_SECOND);
    }

    private static int readLittleEndian16(byte[] bArr, int i) {
        return ((bArr[i + 1] & 255) << 8) | (bArr[i] & 255);
    }
}
