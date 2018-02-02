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
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() {
        public Extractor[] createExtractors() {
            return new Extractor[]{new FlacExtractor()};
        }
    };
    private static final byte[] FLAC_SIGNATURE = new byte[]{(byte) 102, (byte) 76, (byte) 97, (byte) 67, (byte) 0, (byte) 0, (byte) 0, (byte) 34};
    private FlacDecoderJni decoderJni;
    private ExtractorOutput extractorOutput;
    private boolean metadataParsed;
    private ParsableByteArray outputBuffer;
    private ByteBuffer outputByteBuffer;
    private TrackOutput trackOutput;

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
        this.decoderJni.setData(input);
        if (!this.metadataParsed) {
            try {
                FlacStreamInfo streamInfo = this.decoderJni.decodeMetadata();
                if (streamInfo == null) {
                    throw new IOException("Metadata decoding failed");
                }
                SeekMap flacSeekMap;
                this.metadataParsed = true;
                boolean isSeekable = this.decoderJni.getSeekPosition(0) != -1;
                ExtractorOutput extractorOutput = this.extractorOutput;
                if (isSeekable) {
                    flacSeekMap = new FlacSeekMap(streamInfo.durationUs(), this.decoderJni);
                } else {
                    flacSeekMap = new Unseekable(streamInfo.durationUs(), 0);
                }
                extractorOutput.seekMap(flacSeekMap);
                this.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, streamInfo.bitRate(), -1, streamInfo.channels, streamInfo.sampleRate, Util.getPcmEncoding(streamInfo.bitsPerSample), null, null, 0, null));
                this.outputBuffer = new ParsableByteArray(streamInfo.maxDecodedFrameSize());
                this.outputByteBuffer = ByteBuffer.wrap(this.outputBuffer.data);
            } catch (Throwable e) {
                this.decoderJni.reset(0);
                input.setRetryPosition(0, e);
                throw e;
            }
        }
        this.outputBuffer.reset();
        long lastDecodePosition = this.decoderJni.getDecodePosition();
        try {
            int size = this.decoderJni.decodeSample(this.outputByteBuffer);
            if (size <= 0) {
                return -1;
            }
            this.trackOutput.sampleData(this.outputBuffer, size);
            this.trackOutput.sampleMetadata(this.decoderJni.getLastSampleTimestamp(), 1, size, 0, null);
            return this.decoderJni.isEndOfData() ? -1 : 0;
        } catch (Throwable e2) {
            if (lastDecodePosition >= 0) {
                this.decoderJni.reset(lastDecodePosition);
                input.setRetryPosition(lastDecodePosition, e2);
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
