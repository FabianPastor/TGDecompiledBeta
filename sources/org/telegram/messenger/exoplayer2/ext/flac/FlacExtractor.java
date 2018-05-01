package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class FlacExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C18351();
    private static final byte[] FLAC_SIGNATURE = new byte[]{(byte) 102, (byte) 76, (byte) 97, (byte) 67, (byte) 0, (byte) 0, (byte) 0, (byte) 34};
    private FlacDecoderJni decoderJni;
    private ExtractorOutput extractorOutput;
    private boolean metadataParsed;
    private ParsableByteArray outputBuffer;
    private ByteBuffer outputByteBuffer;
    private TrackOutput trackOutput;

    /* renamed from: org.telegram.messenger.exoplayer2.ext.flac.FlacExtractor$1 */
    static class C18351 implements ExtractorsFactory {
        C18351() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new FlacExtractor()};
        }
    }

    private static final class FlacSeekMap implements SeekMap {
        private final FlacDecoderJni decoderJni;
        private final long durationUs;

        public boolean isSeekable() {
            return true;
        }

        public FlacSeekMap(long j, FlacDecoderJni flacDecoderJni) {
            this.durationUs = j;
            this.decoderJni = flacDecoderJni;
        }

        public SeekPoints getSeekPoints(long j) {
            return new SeekPoints(new SeekPoint(j, this.decoderJni.getSeekPosition(j)));
        }

        public long getDurationUs() {
            return this.durationUs;
        }
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = this.extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
        try {
            this.decoderJni = new FlacDecoderJni();
        } catch (ExtractorOutput extractorOutput2) {
            throw new RuntimeException(extractorOutput2);
        }
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        byte[] bArr = new byte[FLAC_SIGNATURE.length];
        extractorInput.peekFully(bArr, 0, FLAC_SIGNATURE.length);
        return Arrays.equals(bArr, FLAC_SIGNATURE);
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        ExtractorInput extractorInput2 = extractorInput;
        this.decoderJni.setData(extractorInput2);
        if (!this.metadataParsed) {
            try {
                FlacStreamInfo decodeMetadata = r1.decoderJni.decodeMetadata();
                if (decodeMetadata == null) {
                    throw new IOException("Metadata decoding failed");
                }
                SeekMap flacSeekMap;
                boolean z = true;
                r1.metadataParsed = true;
                if (r1.decoderJni.getSeekPosition(0) == -1) {
                    z = false;
                }
                ExtractorOutput extractorOutput = r1.extractorOutput;
                if (z) {
                    flacSeekMap = new FlacSeekMap(decodeMetadata.durationUs(), r1.decoderJni);
                } else {
                    flacSeekMap = new Unseekable(decodeMetadata.durationUs(), 0);
                }
                extractorOutput.seekMap(flacSeekMap);
                r1.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, decodeMetadata.bitRate(), -1, decodeMetadata.channels, decodeMetadata.sampleRate, Util.getPcmEncoding(decodeMetadata.bitsPerSample), null, null, 0, null));
                r1.outputBuffer = new ParsableByteArray(decodeMetadata.maxDecodedFrameSize());
                r1.outputByteBuffer = ByteBuffer.wrap(r1.outputBuffer.data);
            } catch (Throwable e) {
                Throwable th = e;
                r1.decoderJni.reset(0);
                extractorInput2.setRetryPosition(0, th);
                throw th;
            }
        }
        r1.outputBuffer.reset();
        long decodePosition = r1.decoderJni.getDecodePosition();
        try {
            int decodeSample = r1.decoderJni.decodeSample(r1.outputByteBuffer);
            int i = -1;
            if (decodeSample <= 0) {
                return -1;
            }
            r1.trackOutput.sampleData(r1.outputBuffer, decodeSample);
            r1.trackOutput.sampleMetadata(r1.decoderJni.getLastSampleTimestamp(), 1, decodeSample, 0, null);
            if (!r1.decoderJni.isEndOfData()) {
                i = 0;
            }
            return i;
        } catch (Throwable e2) {
            th = e2;
            if (decodePosition >= 0) {
                r1.decoderJni.reset(decodePosition);
                extractorInput2.setRetryPosition(decodePosition, th);
            }
            throw th;
        }
    }

    public void seek(long j, long j2) {
        if (j == 0) {
            this.metadataParsed = 0;
        }
        if (this.decoderJni != null) {
            this.decoderJni.reset(j);
        }
    }

    public void release() {
        if (this.decoderJni != null) {
            this.decoderJni.release();
            this.decoderJni = null;
        }
    }
}
