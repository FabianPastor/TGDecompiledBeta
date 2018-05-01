package org.telegram.messenger.exoplayer2.extractor.flv;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class FlvExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C18371();
    private static final int FLV_HEADER_SIZE = 9;
    private static final int FLV_TAG = Util.getIntegerCodeForString("FLV");
    private static final int FLV_TAG_HEADER_SIZE = 11;
    private static final int STATE_READING_FLV_HEADER = 1;
    private static final int STATE_READING_TAG_DATA = 4;
    private static final int STATE_READING_TAG_HEADER = 3;
    private static final int STATE_SKIPPING_TO_TAG_HEADER = 2;
    private static final int TAG_TYPE_AUDIO = 8;
    private static final int TAG_TYPE_SCRIPT_DATA = 18;
    private static final int TAG_TYPE_VIDEO = 9;
    private AudioTagPayloadReader audioReader;
    private int bytesToNextTagHeader;
    private ExtractorOutput extractorOutput;
    private final ParsableByteArray headerBuffer = new ParsableByteArray(9);
    private long mediaTagTimestampOffsetUs = C0542C.TIME_UNSET;
    private final ScriptTagPayloadReader metadataReader = new ScriptTagPayloadReader();
    private boolean outputSeekMap;
    private final ParsableByteArray scratch = new ParsableByteArray(4);
    private int state = 1;
    private final ParsableByteArray tagData = new ParsableByteArray();
    private int tagDataSize;
    private final ParsableByteArray tagHeaderBuffer = new ParsableByteArray(11);
    private long tagTimestampUs;
    private int tagType;
    private VideoTagPayloadReader videoReader;

    @Retention(RetentionPolicy.SOURCE)
    private @interface States {
    }

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.flv.FlvExtractor$1 */
    static class C18371 implements ExtractorsFactory {
        C18371() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new FlvExtractor()};
        }
    }

    public void release() {
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        boolean z = false;
        extractorInput.peekFully(this.scratch.data, 0, 3);
        this.scratch.setPosition(0);
        if (this.scratch.readUnsignedInt24() != FLV_TAG) {
            return false;
        }
        extractorInput.peekFully(this.scratch.data, 0, 2);
        this.scratch.setPosition(0);
        if ((this.scratch.readUnsignedShort() & Callback.DEFAULT_SWIPE_ANIMATION_DURATION) != 0) {
            return false;
        }
        extractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        int readInt = this.scratch.readInt();
        extractorInput.resetPeekPosition();
        extractorInput.advancePeekPosition(readInt);
        extractorInput.peekFully(this.scratch.data, 0, 4);
        this.scratch.setPosition(0);
        if (this.scratch.readInt() == null) {
            z = true;
        }
        return z;
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    public void seek(long j, long j2) {
        this.state = 1;
        this.mediaTagTimestampOffsetUs = C0542C.TIME_UNSET;
        this.bytesToNextTagHeader = 0;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        while (true) {
            switch (this.state) {
                case 1:
                    if (readFlvHeader(extractorInput) != null) {
                        break;
                    }
                    return -1;
                case 2:
                    skipToTagHeader(extractorInput);
                    break;
                case 3:
                    if (readTagHeader(extractorInput) != null) {
                        break;
                    }
                    return -1;
                case 4:
                    if (readTagData(extractorInput) == null) {
                        break;
                    }
                    return null;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private boolean readFlvHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        boolean z = false;
        if (extractorInput.readFully(this.headerBuffer.data, 0, 9, true) == null) {
            return false;
        }
        this.headerBuffer.setPosition(0);
        this.headerBuffer.skipBytes(4);
        extractorInput = this.headerBuffer.readUnsignedByte();
        boolean z2 = (extractorInput & 4) != 0;
        if ((extractorInput & 1) != null) {
            z = true;
        }
        if (z2 && this.audioReader == null) {
            this.audioReader = new AudioTagPayloadReader(this.extractorOutput.track(8, 1));
        }
        if (z && this.videoReader == null) {
            this.videoReader = new VideoTagPayloadReader(this.extractorOutput.track(9, 2));
        }
        this.extractorOutput.endTracks();
        this.bytesToNextTagHeader = (this.headerBuffer.readInt() - 9) + 4;
        this.state = 2;
        return true;
    }

    private void skipToTagHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        extractorInput.skipFully(this.bytesToNextTagHeader);
        this.bytesToNextTagHeader = null;
        this.state = 3;
    }

    private boolean readTagHeader(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (extractorInput.readFully(this.tagHeaderBuffer.data, 0, 11, true) == null) {
            return false;
        }
        this.tagHeaderBuffer.setPosition(0);
        this.tagType = this.tagHeaderBuffer.readUnsignedByte();
        this.tagDataSize = this.tagHeaderBuffer.readUnsignedInt24();
        this.tagTimestampUs = (long) this.tagHeaderBuffer.readUnsignedInt24();
        this.tagTimestampUs = (((long) (this.tagHeaderBuffer.readUnsignedByte() << 24)) | this.tagTimestampUs) * 1000;
        this.tagHeaderBuffer.skipBytes(3);
        this.state = 4;
        return true;
    }

    private boolean readTagData(ExtractorInput extractorInput) throws IOException, InterruptedException {
        boolean z = true;
        if (this.tagType == 8 && this.audioReader != null) {
            ensureReadyForMediaOutput();
            this.audioReader.consume(prepareTagData(extractorInput), this.mediaTagTimestampOffsetUs + this.tagTimestampUs);
        } else if (this.tagType == 9 && this.videoReader != null) {
            ensureReadyForMediaOutput();
            this.videoReader.consume(prepareTagData(extractorInput), this.mediaTagTimestampOffsetUs + this.tagTimestampUs);
        } else if (this.tagType != 18 || this.outputSeekMap) {
            extractorInput.skipFully(this.tagDataSize);
            z = false;
        } else {
            this.metadataReader.consume(prepareTagData(extractorInput), this.tagTimestampUs);
            long durationUs = this.metadataReader.getDurationUs();
            if (durationUs != C0542C.TIME_UNSET) {
                this.extractorOutput.seekMap(new Unseekable(durationUs));
                this.outputSeekMap = true;
            }
        }
        this.bytesToNextTagHeader = 4;
        this.state = 2;
        return z;
    }

    private ParsableByteArray prepareTagData(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (this.tagDataSize > this.tagData.capacity()) {
            this.tagData.reset(new byte[Math.max(this.tagData.capacity() * 2, this.tagDataSize)], 0);
        } else {
            this.tagData.setPosition(0);
        }
        this.tagData.setLimit(this.tagDataSize);
        extractorInput.readFully(this.tagData.data, 0, this.tagDataSize);
        return this.tagData;
    }

    private void ensureReadyForMediaOutput() {
        if (!this.outputSeekMap) {
            this.extractorOutput.seekMap(new Unseekable(C0542C.TIME_UNSET));
            this.outputSeekMap = true;
        }
        if (this.mediaTagTimestampOffsetUs == C0542C.TIME_UNSET) {
            this.mediaTagTimestampOffsetUs = this.metadataReader.getDurationUs() == C0542C.TIME_UNSET ? -this.tagTimestampUs : 0;
        }
    }
}
