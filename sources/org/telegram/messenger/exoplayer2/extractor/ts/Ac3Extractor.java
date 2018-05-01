package org.telegram.messenger.exoplayer2.extractor.ts;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.audio.Ac3Util;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Ac3Extractor implements Extractor {
    private static final int AC3_SYNC_WORD = 2935;
    public static final ExtractorsFactory FACTORY = new C18431();
    private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int MAX_SNIFF_BYTES = 8192;
    private static final int MAX_SYNC_FRAME_SIZE = 2786;
    private final long firstSampleTimestampUs;
    private final Ac3Reader reader;
    private final ParsableByteArray sampleData;
    private boolean startedPacket;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor$1 */
    static class C18431 implements ExtractorsFactory {
        C18431() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new Ac3Extractor()};
        }
    }

    public void release() {
    }

    public Ac3Extractor() {
        this(0);
    }

    public Ac3Extractor(long j) {
        this.firstSampleTimestampUs = j;
        this.reader = new Ac3Reader();
        this.sampleData = new ParsableByteArray((int) MAX_SYNC_FRAME_SIZE);
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        int readSynchSafeInt;
        ParsableByteArray parsableByteArray = new ParsableByteArray(10);
        int i = 0;
        while (true) {
            extractorInput.peekFully(parsableByteArray.data, 0, 10);
            parsableByteArray.setPosition(0);
            if (parsableByteArray.readUnsignedInt24() != ID3_TAG) {
                break;
            }
            parsableByteArray.skipBytes(3);
            readSynchSafeInt = parsableByteArray.readSynchSafeInt();
            i += 10 + readSynchSafeInt;
            extractorInput.advancePeekPosition(readSynchSafeInt);
        }
        extractorInput.resetPeekPosition();
        extractorInput.advancePeekPosition(i);
        readSynchSafeInt = 0;
        int i2 = i;
        while (true) {
            extractorInput.peekFully(parsableByteArray.data, 0, 5);
            parsableByteArray.setPosition(0);
            if (parsableByteArray.readUnsignedShort() != AC3_SYNC_WORD) {
                extractorInput.resetPeekPosition();
                i2++;
                if (i2 - i >= 8192) {
                    return false;
                }
                extractorInput.advancePeekPosition(i2);
                readSynchSafeInt = 0;
            } else {
                readSynchSafeInt++;
                if (readSynchSafeInt >= 4) {
                    return true;
                }
                int parseAc3SyncframeSize = Ac3Util.parseAc3SyncframeSize(parsableByteArray.data);
                if (parseAc3SyncframeSize == -1) {
                    return false;
                }
                extractorInput.advancePeekPosition(parseAc3SyncframeSize - 5);
            }
        }
    }

    public void init(ExtractorOutput extractorOutput) {
        this.reader.createTracks(extractorOutput, new TrackIdGenerator(0, 1));
        extractorOutput.endTracks();
        extractorOutput.seekMap(new Unseekable(C0542C.TIME_UNSET));
    }

    public void seek(long j, long j2) {
        this.startedPacket = 0;
        this.reader.seek();
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        extractorInput = extractorInput.read(this.sampleData.data, 0, MAX_SYNC_FRAME_SIZE);
        if (extractorInput == -1) {
            return -1;
        }
        this.sampleData.setPosition(0);
        this.sampleData.setLimit(extractorInput);
        if (this.startedPacket == null) {
            this.reader.packetStarted(this.firstSampleTimestampUs, true);
            this.startedPacket = true;
        }
        this.reader.consume(this.sampleData);
        return 0;
    }
}
