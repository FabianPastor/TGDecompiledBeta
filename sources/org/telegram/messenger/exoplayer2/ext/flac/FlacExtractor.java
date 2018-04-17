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

        public FlacSeekMap(long durationUs, FlacDecoderJni decoderJni) {
            this.durationUs = durationUs;
            this.decoderJni = decoderJni;
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            return new SeekPoints(new SeekPoint(timeUs, this.decoderJni.getSeekPosition(timeUs)));
        }

        public long getDurationUs() {
            return this.durationUs;
        }
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = this.extractorOutput.track(0, 1);
        this.extractorOutput.endTracks();
        try {
            this.decoderJni = new FlacDecoderJni();
        } catch (FlacDecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        byte[] header = new byte[FLAC_SIGNATURE.length];
        input.peekFully(header, 0, FLAC_SIGNATURE.length);
        return Arrays.equals(header, FLAC_SIGNATURE);
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        this.decoderJni.setData(extractorInput);
        int i = 0;
        if (!this.metadataParsed) {
            try {
                FlacStreamInfo streamInfo = r1.decoderJni.decodeMetadata();
                if (streamInfo == null) {
                    throw new IOException("Metadata decoding failed");
                }
                SeekMap flacSeekMap;
                boolean isSeekable = true;
                r1.metadataParsed = true;
                if (r1.decoderJni.getSeekPosition(0) == -1) {
                    isSeekable = false;
                }
                ExtractorOutput extractorOutput = r1.extractorOutput;
                if (isSeekable) {
                    flacSeekMap = new FlacSeekMap(streamInfo.durationUs(), r1.decoderJni);
                } else {
                    flacSeekMap = new Unseekable(streamInfo.durationUs(), 0);
                }
                extractorOutput.seekMap(flacSeekMap);
                r1.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, streamInfo.bitRate(), -1, streamInfo.channels, streamInfo.sampleRate, Util.getPcmEncoding(streamInfo.bitsPerSample), null, null, 0, null));
                r1.outputBuffer = new ParsableByteArray(streamInfo.maxDecodedFrameSize());
                r1.outputByteBuffer = ByteBuffer.wrap(r1.outputBuffer.data);
            } catch (IOException e) {
                IOException e2 = e;
                r1.decoderJni.reset(0);
                extractorInput.setRetryPosition(0, e2);
                throw e2;
            }
        }
        r1.outputBuffer.reset();
        long lastDecodePosition = r1.decoderJni.getDecodePosition();
        try {
            int size = r1.decoderJni.decodeSample(r1.outputByteBuffer);
            if (size <= 0) {
                return -1;
            }
            r1.trackOutput.sampleData(r1.outputBuffer, size);
            r1.trackOutput.sampleMetadata(r1.decoderJni.getLastSampleTimestamp(), 1, size, 0, null);
            if (r1.decoderJni.isEndOfData()) {
                i = -1;
            }
            return i;
        } catch (IOException e3) {
            e2 = e3;
            if (lastDecodePosition >= 0) {
                r1.decoderJni.reset(lastDecodePosition);
                extractorInput.setRetryPosition(lastDecodePosition, e2);
            }
            throw e2;
        }
    }

    public void seek(long position, long timeUs) {
        if (position == 0) {
            this.metadataParsed = false;
        }
        if (this.decoderJni != null) {
            this.decoderJni.reset(position);
        }
    }

    public void release() {
        if (this.decoderJni != null) {
            this.decoderJni.release();
            this.decoderJni = null;
        }
    }
}
