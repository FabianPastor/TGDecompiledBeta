package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

abstract class StreamReader {
    private static final int STATE_END_OF_INPUT = 3;
    private static final int STATE_READ_HEADERS = 0;
    private static final int STATE_READ_PAYLOAD = 2;
    private static final int STATE_SKIP_HEADERS = 1;
    private long currentGranule;
    private ExtractorOutput extractorOutput;
    private boolean formatSet;
    private long lengthOfReadPacket;
    private final OggPacket oggPacket = new OggPacket();
    private OggSeeker oggSeeker;
    private long payloadStartPosition;
    private int sampleRate;
    private boolean seekMapSet;
    private SetupData setupData;
    private int state;
    private long targetGranule;
    private TrackOutput trackOutput;

    static class SetupData {
        Format format;
        OggSeeker oggSeeker;

        SetupData() {
        }
    }

    private static final class UnseekableOggSeeker implements OggSeeker {
        public long read(ExtractorInput extractorInput) throws IOException, InterruptedException {
            return -1;
        }

        public long startSeek(long j) {
            return 0;
        }

        private UnseekableOggSeeker() {
        }

        public SeekMap createSeekMap() {
            return new Unseekable(C0542C.TIME_UNSET);
        }
    }

    protected abstract long preparePayload(ParsableByteArray parsableByteArray);

    protected abstract boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException;

    void init(ExtractorOutput extractorOutput, TrackOutput trackOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = trackOutput;
        reset(true);
    }

    protected void reset(boolean z) {
        if (z) {
            this.setupData = new SetupData();
            this.payloadStartPosition = 0;
            this.state = false;
        } else {
            this.state = true;
        }
        this.targetGranule = -1;
        this.currentGranule = 0;
    }

    final void seek(long j, long j2) {
        this.oggPacket.reset();
        if (j == 0) {
            reset(this.seekMapSet ^ 1);
        } else if (this.state != null) {
            this.targetGranule = this.oggSeeker.startSeek(j2);
            this.state = 2;
        }
    }

    final int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        switch (this.state) {
            case 0:
                return readHeaders(extractorInput);
            case 1:
                extractorInput.skipFully((int) this.payloadStartPosition);
                this.state = 2;
                return null;
            case 2:
                return readPayload(extractorInput, positionHolder);
            default:
                throw new IllegalStateException();
        }
    }

    private int readHeaders(ExtractorInput extractorInput) throws IOException, InterruptedException {
        boolean z = true;
        while (z) {
            if (this.oggPacket.populate(extractorInput)) {
                this.lengthOfReadPacket = extractorInput.getPosition() - this.payloadStartPosition;
                z = readHeaders(this.oggPacket.getPayload(), this.payloadStartPosition, this.setupData);
                if (z) {
                    this.payloadStartPosition = extractorInput.getPosition();
                }
            } else {
                this.state = 3;
                return -1;
            }
        }
        this.sampleRate = this.setupData.format.sampleRate;
        if (!this.formatSet) {
            this.trackOutput.format(this.setupData.format);
            this.formatSet = true;
        }
        if (this.setupData.oggSeeker != null) {
            this.oggSeeker = this.setupData.oggSeeker;
        } else if (extractorInput.getLength() == -1) {
            this.oggSeeker = new UnseekableOggSeeker();
        } else {
            OggPageHeader pageHeader = this.oggPacket.getPageHeader();
            this.oggSeeker = new DefaultOggSeeker(this.payloadStartPosition, extractorInput.getLength(), this, pageHeader.headerSize + pageHeader.bodySize, pageHeader.granulePosition);
        }
        this.setupData = null;
        this.state = 2;
        this.oggPacket.trimPayload();
        return null;
    }

    private int readPayload(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        ExtractorInput extractorInput2 = extractorInput;
        long read = this.oggSeeker.read(extractorInput2);
        if (read >= 0) {
            positionHolder.position = read;
            return 1;
        }
        if (read < -1) {
            onSeekEnd(-(read + 2));
        }
        if (!r0.seekMapSet) {
            r0.extractorOutput.seekMap(r0.oggSeeker.createSeekMap());
            r0.seekMapSet = true;
        }
        if (r0.lengthOfReadPacket <= 0) {
            if (!r0.oggPacket.populate(extractorInput2)) {
                r0.state = 3;
                return -1;
            }
        }
        r0.lengthOfReadPacket = 0;
        ParsableByteArray payload = r0.oggPacket.getPayload();
        read = preparePayload(payload);
        if (read >= 0 && r0.currentGranule + read >= r0.targetGranule) {
            long convertGranuleToTime = convertGranuleToTime(r0.currentGranule);
            r0.trackOutput.sampleData(payload, payload.limit());
            r0.trackOutput.sampleMetadata(convertGranuleToTime, 1, payload.limit(), 0, null);
            r0.targetGranule = -1;
        }
        r0.currentGranule += read;
        return 0;
    }

    protected long convertGranuleToTime(long j) {
        return (j * C0542C.MICROS_PER_SECOND) / ((long) this.sampleRate);
    }

    protected long convertTimeToGranule(long j) {
        return (((long) this.sampleRate) * j) / C0542C.MICROS_PER_SECOND;
    }

    protected void onSeekEnd(long j) {
        this.currentGranule = j;
    }
}
