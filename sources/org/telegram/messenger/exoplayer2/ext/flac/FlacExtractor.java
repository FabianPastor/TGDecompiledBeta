package org.telegram.messenger.exoplayer2.ext.flac;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ext.flac.FlacDecoderJni.FlacFrameDecodeException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.Id3Peeker;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class FlacExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C06471();
    private static final byte[] FLAC_SIGNATURE = new byte[]{(byte) 102, (byte) 76, (byte) 97, (byte) 67, (byte) 0, (byte) 0, (byte) 0, (byte) 34};
    public static final int FLAG_DISABLE_ID3_METADATA = 1;
    private FlacDecoderJni decoderJni;
    private ExtractorOutput extractorOutput;
    private FlacBinarySearchSeeker flacBinarySearchSeeker;
    private Metadata id3Metadata;
    private final Id3Peeker id3Peeker;
    private final boolean isId3MetadataDisabled;
    private ParsableByteArray outputBuffer;
    private ByteBuffer outputByteBuffer;
    private boolean readPastStreamInfo;
    private FlacStreamInfo streamInfo;
    private TrackOutput trackOutput;

    /* renamed from: org.telegram.messenger.exoplayer2.ext.flac.FlacExtractor$1 */
    static class C06471 implements ExtractorsFactory {
        C06471() {
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

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public FlacExtractor() {
        this(0);
    }

    public FlacExtractor(int flags) {
        this.id3Peeker = new Id3Peeker();
        this.isId3MetadataDisabled = (flags & 1) != 0;
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
        if (input.getPosition() == 0) {
            this.id3Metadata = peekId3Data(input);
        }
        return peekFlacSignature(input);
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (input.getPosition() == 0 && !this.isId3MetadataDisabled && this.id3Metadata == null) {
            this.id3Metadata = peekId3Data(input);
        }
        this.decoderJni.setData(input);
        readPastStreamInfo(input);
        if (this.flacBinarySearchSeeker != null && this.flacBinarySearchSeeker.hasPendingSeek()) {
            return handlePendingSeek(input, seekPosition);
        }
        long lastDecodePosition = this.decoderJni.getDecodePosition();
        try {
            this.decoderJni.decodeSampleWithBacktrackPosition(this.outputByteBuffer, lastDecodePosition);
            int outputSize = this.outputByteBuffer.limit();
            if (outputSize == 0) {
                return -1;
            }
            writeLastSampleToOutput(outputSize, this.decoderJni.getLastFrameTimestamp());
            if (this.decoderJni.isEndOfData()) {
                return -1;
            }
            return 0;
        } catch (FlacFrameDecodeException e) {
            throw new IOException("Cannot read frame at position " + lastDecodePosition, e);
        }
    }

    public void seek(long position, long timeUs) {
        if (position == 0) {
            this.readPastStreamInfo = false;
        }
        if (this.decoderJni != null) {
            this.decoderJni.reset(position);
        }
        if (this.flacBinarySearchSeeker != null) {
            this.flacBinarySearchSeeker.setSeekTargetUs(timeUs);
        }
    }

    public void release() {
        this.flacBinarySearchSeeker = null;
        if (this.decoderJni != null) {
            this.decoderJni.release();
            this.decoderJni = null;
        }
    }

    private Metadata peekId3Data(ExtractorInput input) throws IOException, InterruptedException {
        input.resetPeekPosition();
        return this.id3Peeker.peekId3Data(input, this.isId3MetadataDisabled ? Id3Decoder.NO_FRAMES_PREDICATE : null);
    }

    private boolean peekFlacSignature(ExtractorInput input) throws IOException, InterruptedException {
        byte[] header = new byte[FLAC_SIGNATURE.length];
        input.peekFully(header, 0, FLAC_SIGNATURE.length);
        return Arrays.equals(header, FLAC_SIGNATURE);
    }

    private void readPastStreamInfo(ExtractorInput input) throws InterruptedException, IOException {
        if (!this.readPastStreamInfo) {
            FlacStreamInfo streamInfo = decodeStreamInfo(input);
            this.readPastStreamInfo = true;
            if (this.streamInfo == null) {
                updateFlacStreamInfo(input, streamInfo);
            }
        }
    }

    private void updateFlacStreamInfo(ExtractorInput input, FlacStreamInfo streamInfo) {
        this.streamInfo = streamInfo;
        outputSeekMap(input, streamInfo);
        outputFormat(streamInfo);
        this.outputBuffer = new ParsableByteArray(streamInfo.maxDecodedFrameSize());
        this.outputByteBuffer = ByteBuffer.wrap(this.outputBuffer.data);
    }

    private FlacStreamInfo decodeStreamInfo(ExtractorInput input) throws InterruptedException, IOException {
        try {
            FlacStreamInfo streamInfo = this.decoderJni.decodeMetadata();
            if (streamInfo != null) {
                return streamInfo;
            }
            throw new IOException("Metadata decoding failed");
        } catch (IOException e) {
            this.decoderJni.reset(0);
            input.setRetryPosition(0, e);
            throw e;
        }
    }

    private void outputSeekMap(ExtractorInput input, FlacStreamInfo streamInfo) {
        SeekMap seekMap;
        if (this.decoderJni.getSeekPosition(0) != -1) {
            seekMap = new FlacSeekMap(streamInfo.durationUs(), this.decoderJni);
        } else {
            seekMap = getSeekMapForNonSeekTableFlac(input, streamInfo);
        }
        this.extractorOutput.seekMap(seekMap);
    }

    private SeekMap getSeekMapForNonSeekTableFlac(ExtractorInput input, FlacStreamInfo streamInfo) {
        long inputLength = input.getLength();
        if (inputLength == -1) {
            return new Unseekable(streamInfo.durationUs());
        }
        this.flacBinarySearchSeeker = new FlacBinarySearchSeeker(streamInfo, this.decoderJni.getDecodePosition(), inputLength, this.decoderJni);
        return this.flacBinarySearchSeeker.getSeekMap();
    }

    private void outputFormat(FlacStreamInfo streamInfo) {
        Metadata metadata;
        String str = MimeTypes.AUDIO_RAW;
        int bitRate = streamInfo.bitRate();
        int maxDecodedFrameSize = streamInfo.maxDecodedFrameSize();
        int i = streamInfo.channels;
        int i2 = streamInfo.sampleRate;
        int pcmEncoding = Util.getPcmEncoding(streamInfo.bitsPerSample);
        if (this.isId3MetadataDisabled) {
            metadata = null;
        } else {
            metadata = this.id3Metadata;
        }
        this.trackOutput.format(Format.createAudioSampleFormat(null, str, null, bitRate, maxDecodedFrameSize, i, i2, pcmEncoding, 0, 0, null, null, 0, null, metadata));
    }

    private int handlePendingSeek(ExtractorInput input, PositionHolder seekPosition) throws InterruptedException, IOException {
        int seekResult = this.flacBinarySearchSeeker.handlePendingSeek(input, seekPosition, this.outputByteBuffer);
        if (seekResult == 0 && this.outputByteBuffer.limit() > 0) {
            writeLastSampleToOutput(this.outputByteBuffer.limit(), this.decoderJni.getLastFrameTimestamp());
        }
        return seekResult;
    }

    private void writeLastSampleToOutput(int size, long lastSampleTimestamp) {
        this.outputBuffer.setPosition(0);
        this.trackOutput.sampleData(this.outputBuffer, size);
        this.trackOutput.sampleMetadata(lastSampleTimestamp, 1, size, 0, null);
    }
}
