package org.telegram.messenger.exoplayer2.extractor.amr;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0559C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

public final class AmrExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C19991();
    private static final int MAX_FRAME_SIZE_BYTES = frameSizeBytesByTypeWb[8];
    private static final int SAMPLE_RATE_NB = 8000;
    private static final int SAMPLE_RATE_WB = 16000;
    private static final int SAMPLE_TIME_PER_FRAME_US = 20000;
    private static final byte[] amrSignatureNb = Util.getUtf8Bytes("#!AMR\n");
    private static final byte[] amrSignatureWb = Util.getUtf8Bytes("#!AMR-WB\n");
    private static final int[] frameSizeBytesByTypeNb = new int[]{13, 14, 16, 18, 20, 21, 27, 32, 6, 7, 6, 6, 1, 1, 1, 1};
    private static final int[] frameSizeBytesByTypeWb = new int[]{18, 24, 33, 37, 41, 47, 51, 59, 61, 6, 1, 1, 1, 1, 1, 1};
    private int currentSampleBytesRemaining;
    private long currentSampleTimeUs;
    private int currentSampleTotalBytes;
    private boolean hasOutputFormat;
    private boolean isWideBand;
    private final byte[] scratch = new byte[1];
    private TrackOutput trackOutput;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.amr.AmrExtractor$1 */
    static class C19991 implements ExtractorsFactory {
        C19991() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new AmrExtractor()};
        }
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return readAmrHeader(input);
    }

    public void init(ExtractorOutput output) {
        output.seekMap(new Unseekable(C0559C.TIME_UNSET));
        this.trackOutput = output.track(0, 1);
        output.endTracks();
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (input.getPosition() != 0 || readAmrHeader(input)) {
            maybeOutputFormat();
            return readSample(input);
        }
        throw new ParserException("Could not find AMR header.");
    }

    public void seek(long position, long timeUs) {
        this.currentSampleTimeUs = 0;
        this.currentSampleTotalBytes = 0;
        this.currentSampleBytesRemaining = 0;
    }

    public void release() {
    }

    static int frameSizeBytesByTypeNb(int frameType) {
        return frameSizeBytesByTypeNb[frameType];
    }

    static int frameSizeBytesByTypeWb(int frameType) {
        return frameSizeBytesByTypeWb[frameType];
    }

    static byte[] amrSignatureNb() {
        return Arrays.copyOf(amrSignatureNb, amrSignatureNb.length);
    }

    static byte[] amrSignatureWb() {
        return Arrays.copyOf(amrSignatureWb, amrSignatureWb.length);
    }

    private boolean readAmrHeader(ExtractorInput input) throws IOException, InterruptedException {
        if (peekAmrSignature(input, amrSignatureNb)) {
            this.isWideBand = false;
            input.skipFully(amrSignatureNb.length);
            return true;
        } else if (!peekAmrSignature(input, amrSignatureWb)) {
            return false;
        } else {
            this.isWideBand = true;
            input.skipFully(amrSignatureWb.length);
            return true;
        }
    }

    private boolean peekAmrSignature(ExtractorInput input, byte[] amrSignature) throws IOException, InterruptedException {
        input.resetPeekPosition();
        byte[] header = new byte[amrSignature.length];
        input.peekFully(header, 0, amrSignature.length);
        return Arrays.equals(header, amrSignature);
    }

    private void maybeOutputFormat() {
        if (!this.hasOutputFormat) {
            this.hasOutputFormat = true;
            this.trackOutput.format(Format.createAudioSampleFormat(null, this.isWideBand ? MimeTypes.AUDIO_AMR_WB : MimeTypes.AUDIO_AMR_NB, null, -1, MAX_FRAME_SIZE_BYTES, 1, this.isWideBand ? SAMPLE_RATE_WB : 8000, -1, null, null, 0, null));
        }
    }

    private int readSample(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.currentSampleBytesRemaining == 0) {
            try {
                this.currentSampleTotalBytes = readNextSampleSize(extractorInput);
                this.currentSampleBytesRemaining = this.currentSampleTotalBytes;
            } catch (EOFException e) {
                return -1;
            }
        }
        int bytesAppended = this.trackOutput.sampleData(extractorInput, this.currentSampleBytesRemaining, true);
        if (bytesAppended == -1) {
            return -1;
        }
        this.currentSampleBytesRemaining -= bytesAppended;
        if (this.currentSampleBytesRemaining > 0) {
            return 0;
        }
        this.trackOutput.sampleMetadata(this.currentSampleTimeUs, 1, this.currentSampleTotalBytes, 0, null);
        this.currentSampleTimeUs += 20000;
        return 0;
    }

    private int readNextSampleSize(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.resetPeekPosition();
        extractorInput.peekFully(this.scratch, 0, 1);
        byte frameHeader = this.scratch[0];
        if ((frameHeader & 131) <= 0) {
            return getFrameSizeInBytes((frameHeader >> 3) & 15);
        }
        throw new ParserException("Invalid padding bits for frame header " + frameHeader);
    }

    private int getFrameSizeInBytes(int frameType) throws ParserException {
        if (isValidFrameType(frameType)) {
            return this.isWideBand ? frameSizeBytesByTypeWb[frameType] : frameSizeBytesByTypeNb[frameType];
        } else {
            throw new ParserException("Illegal AMR " + (this.isWideBand ? "WB" : "NB") + " frame type " + frameType);
        }
    }

    private boolean isValidFrameType(int frameType) {
        return frameType >= 0 && frameType <= 15 && (isWideBandValidFrameType(frameType) || isNarrowBandValidFrameType(frameType));
    }

    private boolean isWideBandValidFrameType(int frameType) {
        return this.isWideBand && (frameType < 10 || frameType > 13);
    }

    private boolean isNarrowBandValidFrameType(int frameType) {
        return !this.isWideBand && (frameType < 12 || frameType > 14);
    }
}
