package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ogg.VorbisUtil.CommentHeader;
import org.telegram.messenger.exoplayer2.extractor.ogg.VorbisUtil.Mode;
import org.telegram.messenger.exoplayer2.extractor.ogg.VorbisUtil.VorbisIdHeader;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class VorbisReader extends StreamReader {
    private CommentHeader commentHeader;
    private int previousPacketBlockSize;
    private boolean seenFirstAudioPacket;
    private VorbisIdHeader vorbisIdHeader;
    private VorbisSetup vorbisSetup;

    static final class VorbisSetup {
        public final CommentHeader commentHeader;
        public final int iLogModes;
        public final VorbisIdHeader idHeader;
        public final Mode[] modes;
        public final byte[] setupHeaderData;

        public VorbisSetup(VorbisIdHeader vorbisIdHeader, CommentHeader commentHeader, byte[] bArr, Mode[] modeArr, int i) {
            this.idHeader = vorbisIdHeader;
            this.commentHeader = commentHeader;
            this.setupHeaderData = bArr;
            this.modes = modeArr;
            this.iLogModes = i;
        }
    }

    static int readBits(byte b, int i, int i2) {
        return (b >> i2) & (255 >>> (8 - i));
    }

    VorbisReader() {
    }

    public static boolean verifyBitstreamType(org.telegram.messenger.exoplayer2.util.ParsableByteArray r1) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = 1;
        r1 = org.telegram.messenger.exoplayer2.extractor.ogg.VorbisUtil.verifyVorbisHeaderCapturePattern(r0, r1, r0);	 Catch:{ ParserException -> 0x0006 }
        return r1;
    L_0x0006:
        r1 = 0;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.extractor.ogg.VorbisReader.verifyBitstreamType(org.telegram.messenger.exoplayer2.util.ParsableByteArray):boolean");
    }

    protected void reset(boolean z) {
        super.reset(z);
        if (z) {
            this.vorbisSetup = null;
            this.vorbisIdHeader = null;
            this.commentHeader = null;
        }
        this.previousPacketBlockSize = 0;
        this.seenFirstAudioPacket = false;
    }

    protected void onSeekEnd(long j) {
        super.onSeekEnd(j);
        int i = (j > 0 ? 1 : (j == 0 ? 0 : -1));
        j = null;
        this.seenFirstAudioPacket = i != 0;
        if (this.vorbisIdHeader != null) {
            j = this.vorbisIdHeader.blockSize0;
        }
        this.previousPacketBlockSize = j;
    }

    protected long preparePayload(ParsableByteArray parsableByteArray) {
        int i = 0;
        if ((parsableByteArray.data[0] & 1) == 1) {
            return -1;
        }
        int decodeBlockSize = decodeBlockSize(parsableByteArray.data[0], this.vorbisSetup);
        if (this.seenFirstAudioPacket) {
            i = (this.previousPacketBlockSize + decodeBlockSize) / 4;
        }
        long j = (long) i;
        appendNumberOfSamples(parsableByteArray, j);
        this.seenFirstAudioPacket = true;
        this.previousPacketBlockSize = decodeBlockSize;
        return j;
    }

    protected boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException {
        if (this.vorbisSetup != null) {
            return null;
        }
        this.vorbisSetup = readSetupHeaders(parsableByteArray);
        if (this.vorbisSetup == null) {
            return true;
        }
        List arrayList = new ArrayList();
        arrayList.add(this.vorbisSetup.idHeader.data);
        arrayList.add(this.vorbisSetup.setupHeaderData);
        setupData.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_VORBIS, null, this.vorbisSetup.idHeader.bitrateNominal, -1, this.vorbisSetup.idHeader.channels, (int) this.vorbisSetup.idHeader.sampleRate, arrayList, null, 0, null);
        return true;
    }

    VorbisSetup readSetupHeaders(ParsableByteArray parsableByteArray) throws IOException {
        if (this.vorbisIdHeader == null) {
            this.vorbisIdHeader = VorbisUtil.readVorbisIdentificationHeader(parsableByteArray);
            return null;
        } else if (this.commentHeader == null) {
            this.commentHeader = VorbisUtil.readVorbisCommentHeader(parsableByteArray);
            return null;
        } else {
            Object obj = new byte[parsableByteArray.limit()];
            System.arraycopy(parsableByteArray.data, 0, obj, 0, parsableByteArray.limit());
            Mode[] readVorbisModes = VorbisUtil.readVorbisModes(parsableByteArray, this.vorbisIdHeader.channels);
            return new VorbisSetup(this.vorbisIdHeader, this.commentHeader, obj, readVorbisModes, VorbisUtil.iLog(readVorbisModes.length - 1));
        }
    }

    static void appendNumberOfSamples(ParsableByteArray parsableByteArray, long j) {
        parsableByteArray.setLimit(parsableByteArray.limit() + 4);
        parsableByteArray.data[parsableByteArray.limit() - 4] = (byte) ((int) (j & 255));
        parsableByteArray.data[parsableByteArray.limit() - 3] = (byte) ((int) ((j >>> 8) & 255));
        parsableByteArray.data[parsableByteArray.limit() - 2] = (byte) ((int) ((j >>> 16) & 255));
        parsableByteArray.data[parsableByteArray.limit() - 1] = (byte) ((int) ((j >>> 24) & 255));
    }

    private static int decodeBlockSize(byte b, VorbisSetup vorbisSetup) {
        if (vorbisSetup.modes[readBits(b, vorbisSetup.iLogModes, 1)].blockFlag == (byte) 0) {
            return vorbisSetup.idHeader.blockSize0;
        }
        return vorbisSetup.idHeader.blockSize1;
    }
}
