package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import java.nio.ByteBuffer;
import java.util.List;
import org.telegram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.telegram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.telegram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class FfmpegDecoder extends SimpleDecoder<DecoderInputBuffer, SimpleOutputBuffer, FfmpegDecoderException> {
    private static final int OUTPUT_BUFFER_SIZE_16BIT = 49152;
    private static final int OUTPUT_BUFFER_SIZE_32BIT = 98304;
    private volatile int channelCount;
    private final String codecName;
    private final int encoding;
    private final byte[] extraData;
    private boolean hasOutputFormat;
    private long nativeContext;
    private final int outputBufferSize;
    private volatile int sampleRate;

    private native int ffmpegDecode(long j, ByteBuffer byteBuffer, int i, ByteBuffer byteBuffer2, int i2);

    private native int ffmpegGetChannelCount(long j);

    private native int ffmpegGetSampleRate(long j);

    private native long ffmpegInitialize(String str, byte[] bArr, boolean z);

    private native void ffmpegRelease(long j);

    private native long ffmpegReset(long j, byte[] bArr);

    public FfmpegDecoder(int i, int i2, int i3, String str, List<byte[]> list, boolean z) throws FfmpegDecoderException {
        super(new DecoderInputBuffer[i], new SimpleOutputBuffer[i2]);
        this.codecName = FfmpegLibrary.getCodecName(str);
        this.extraData = getExtraData(str, list);
        this.encoding = z ? 4 : 2;
        this.outputBufferSize = z ? OUTPUT_BUFFER_SIZE_32BIT : OUTPUT_BUFFER_SIZE_16BIT;
        this.nativeContext = ffmpegInitialize(this.codecName, this.extraData, z);
        if (this.nativeContext == null) {
            throw new FfmpegDecoderException("Initialization failed.");
        }
        setInitialInputBufferSize(i3);
    }

    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ffmpeg");
        stringBuilder.append(FfmpegLibrary.getVersion());
        stringBuilder.append("-");
        stringBuilder.append(this.codecName);
        return stringBuilder.toString();
    }

    protected DecoderInputBuffer createInputBuffer() {
        return new DecoderInputBuffer(2);
    }

    protected SimpleOutputBuffer createOutputBuffer() {
        return new SimpleOutputBuffer(this);
    }

    protected FfmpegDecoderException createUnexpectedDecodeException(Throwable th) {
        return new FfmpegDecoderException("Unexpected decode error", th);
    }

    protected FfmpegDecoderException decode(DecoderInputBuffer decoderInputBuffer, SimpleOutputBuffer simpleOutputBuffer, boolean z) {
        if (z) {
            this.nativeContext = ffmpegReset(this.nativeContext, this.extraData);
            if (this.nativeContext == 0) {
                return new FfmpegDecoderException("Error resetting (see logcat).");
            }
        }
        ByteBuffer byteBuffer = decoderInputBuffer.data;
        decoderInputBuffer = ffmpegDecode(this.nativeContext, byteBuffer, byteBuffer.limit(), simpleOutputBuffer.init(decoderInputBuffer.timeUs, this.outputBufferSize), this.outputBufferSize);
        if (decoderInputBuffer < null) {
            z = new StringBuilder();
            z.append("Error decoding (see logcat). Code: ");
            z.append(decoderInputBuffer);
            return new FfmpegDecoderException(z.toString());
        }
        if (!this.hasOutputFormat) {
            this.channelCount = ffmpegGetChannelCount(this.nativeContext);
            this.sampleRate = ffmpegGetSampleRate(this.nativeContext);
            if (!this.sampleRate && "alac".equals(this.codecName)) {
                z = new ParsableByteArray(this.extraData);
                z.setPosition(this.extraData.length - 4);
                this.sampleRate = z.readUnsignedIntToInt();
            }
            this.hasOutputFormat = true;
        }
        simpleOutputBuffer.data.position(0);
        simpleOutputBuffer.data.limit(decoderInputBuffer);
        return null;
    }

    public void release() {
        super.release();
        ffmpegRelease(this.nativeContext);
        this.nativeContext = 0;
    }

    public int getChannelCount() {
        return this.channelCount;
    }

    public int getSampleRate() {
        return this.sampleRate;
    }

    public int getEncoding() {
        return this.encoding;
    }

    private static byte[] getExtraData(String str, List<byte[]> list) {
        byte[] bArr;
        byte[] bArr2;
        Object obj;
        int hashCode = str.hashCode();
        if (hashCode != -NUM) {
            if (hashCode != -53558318) {
                if (hashCode != NUM) {
                    if (hashCode == NUM) {
                        if (str.equals(MimeTypes.AUDIO_OPUS) != null) {
                            str = 2;
                            switch (str) {
                                case null:
                                case 1:
                                case 2:
                                    return (byte[]) list.get(0);
                                case 3:
                                    bArr = (byte[]) list.get(0);
                                    bArr2 = (byte[]) list.get(1);
                                    obj = new byte[((bArr.length + bArr2.length) + 6)];
                                    obj[0] = (byte) (bArr.length >> 8);
                                    obj[1] = (byte) (bArr.length & 255);
                                    System.arraycopy(bArr, 0, obj, 2, bArr.length);
                                    obj[bArr.length + 2] = null;
                                    obj[bArr.length + 3] = null;
                                    obj[bArr.length + 4] = (byte) (bArr2.length >> 8);
                                    obj[bArr.length + 5] = (byte) (bArr2.length & 255);
                                    System.arraycopy(bArr2, 0, obj, bArr.length + 6, bArr2.length);
                                    return obj;
                                default:
                                    return null;
                            }
                        }
                    }
                } else if (str.equals(MimeTypes.AUDIO_ALAC) != null) {
                    str = 1;
                    switch (str) {
                        case null:
                        case 1:
                        case 2:
                            return (byte[]) list.get(0);
                        case 3:
                            bArr = (byte[]) list.get(0);
                            bArr2 = (byte[]) list.get(1);
                            obj = new byte[((bArr.length + bArr2.length) + 6)];
                            obj[0] = (byte) (bArr.length >> 8);
                            obj[1] = (byte) (bArr.length & 255);
                            System.arraycopy(bArr, 0, obj, 2, bArr.length);
                            obj[bArr.length + 2] = null;
                            obj[bArr.length + 3] = null;
                            obj[bArr.length + 4] = (byte) (bArr2.length >> 8);
                            obj[bArr.length + 5] = (byte) (bArr2.length & 255);
                            System.arraycopy(bArr2, 0, obj, bArr.length + 6, bArr2.length);
                            return obj;
                        default:
                            return null;
                    }
                }
            } else if (str.equals(MimeTypes.AUDIO_AAC) != null) {
                str = null;
                switch (str) {
                    case null:
                    case 1:
                    case 2:
                        return (byte[]) list.get(0);
                    case 3:
                        bArr = (byte[]) list.get(0);
                        bArr2 = (byte[]) list.get(1);
                        obj = new byte[((bArr.length + bArr2.length) + 6)];
                        obj[0] = (byte) (bArr.length >> 8);
                        obj[1] = (byte) (bArr.length & 255);
                        System.arraycopy(bArr, 0, obj, 2, bArr.length);
                        obj[bArr.length + 2] = null;
                        obj[bArr.length + 3] = null;
                        obj[bArr.length + 4] = (byte) (bArr2.length >> 8);
                        obj[bArr.length + 5] = (byte) (bArr2.length & 255);
                        System.arraycopy(bArr2, 0, obj, bArr.length + 6, bArr2.length);
                        return obj;
                    default:
                        return null;
                }
            }
        } else if (str.equals(MimeTypes.AUDIO_VORBIS) != null) {
            str = 3;
            switch (str) {
                case null:
                case 1:
                case 2:
                    return (byte[]) list.get(0);
                case 3:
                    bArr = (byte[]) list.get(0);
                    bArr2 = (byte[]) list.get(1);
                    obj = new byte[((bArr.length + bArr2.length) + 6)];
                    obj[0] = (byte) (bArr.length >> 8);
                    obj[1] = (byte) (bArr.length & 255);
                    System.arraycopy(bArr, 0, obj, 2, bArr.length);
                    obj[bArr.length + 2] = null;
                    obj[bArr.length + 3] = null;
                    obj[bArr.length + 4] = (byte) (bArr2.length >> 8);
                    obj[bArr.length + 5] = (byte) (bArr2.length & 255);
                    System.arraycopy(bArr2, 0, obj, bArr.length + 6, bArr2.length);
                    return obj;
                default:
                    return null;
            }
        }
        str = -1;
        switch (str) {
            case null:
            case 1:
            case 2:
                return (byte[]) list.get(0);
            case 3:
                bArr = (byte[]) list.get(0);
                bArr2 = (byte[]) list.get(1);
                obj = new byte[((bArr.length + bArr2.length) + 6)];
                obj[0] = (byte) (bArr.length >> 8);
                obj[1] = (byte) (bArr.length & 255);
                System.arraycopy(bArr, 0, obj, 2, bArr.length);
                obj[bArr.length + 2] = null;
                obj[bArr.length + 3] = null;
                obj[bArr.length + 4] = (byte) (bArr2.length >> 8);
                obj[bArr.length + 5] = (byte) (bArr2.length & 255);
                System.arraycopy(bArr2, 0, obj, bArr.length + 6, bArr2.length);
                return obj;
            default:
                return null;
        }
    }
}
