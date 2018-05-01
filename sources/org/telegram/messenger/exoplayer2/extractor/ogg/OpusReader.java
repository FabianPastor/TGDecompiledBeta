package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class OpusReader extends StreamReader {
    private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
    private static final int OPUS_CODE = Util.getIntegerCodeForString("Opus");
    private static final byte[] OPUS_SIGNATURE = new byte[]{(byte) 79, (byte) 112, (byte) 117, (byte) 115, (byte) 72, (byte) 101, (byte) 97, (byte) 100};
    private static final int SAMPLE_RATE = 48000;
    private boolean headerRead;

    OpusReader() {
    }

    public static boolean verifyBitstreamType(ParsableByteArray parsableByteArray) {
        if (parsableByteArray.bytesLeft() < OPUS_SIGNATURE.length) {
            return false;
        }
        byte[] bArr = new byte[OPUS_SIGNATURE.length];
        parsableByteArray.readBytes(bArr, 0, OPUS_SIGNATURE.length);
        return Arrays.equals(bArr, OPUS_SIGNATURE);
    }

    protected void reset(boolean z) {
        super.reset(z);
        if (z) {
            this.headerRead = false;
        }
    }

    protected long preparePayload(ParsableByteArray parsableByteArray) {
        return convertTimeToGranule(getPacketDurationUs(parsableByteArray.data));
    }

    protected boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException {
        boolean z = true;
        if (this.headerRead == null) {
            parsableByteArray = Arrays.copyOf(parsableByteArray.data, parsableByteArray.limit());
            int i = parsableByteArray[9] & 255;
            j = ((parsableByteArray[11] & 255) << 8) | (parsableByteArray[10] & 255);
            List arrayList = new ArrayList(3);
            arrayList.add(parsableByteArray);
            putNativeOrderLong(arrayList, j);
            putNativeOrderLong(arrayList, DEFAULT_SEEK_PRE_ROLL_SAMPLES);
            setupData.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_OPUS, null, -1, -1, i, SAMPLE_RATE, arrayList, null, 0, null);
            this.headerRead = true;
            return true;
        }
        if (parsableByteArray.readInt() != OPUS_CODE) {
            z = false;
        }
        parsableByteArray.setPosition(0);
        return z;
    }

    private void putNativeOrderLong(List<byte[]> list, int i) {
        list.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong((((long) i) * C0542C.NANOS_PER_SECOND) / 48000).array());
    }

    private long getPacketDurationUs(byte[] bArr) {
        int i = bArr[0] & 255;
        switch (i & 3) {
            case 0:
                bArr = 1;
                break;
            case 1:
            case 2:
                bArr = 2;
                break;
            default:
                bArr = bArr[1] & 63;
                break;
        }
        i >>= 3;
        int i2 = i & 3;
        i = i >= 16 ? DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS << i2 : i >= 12 ? 10000 << (i2 & 1) : i2 == 3 ? 60000 : 10000 << i2;
        return (long) (bArr * i);
    }
}
