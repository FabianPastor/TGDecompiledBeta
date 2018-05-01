package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.util.FlacStreamInfo;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class FlacReader extends StreamReader {
    private static final byte AUDIO_PACKET_TYPE = (byte) -1;
    private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
    private static final byte SEEKTABLE_PACKET_TYPE = (byte) 3;
    private FlacOggSeeker flacOggSeeker;
    private FlacStreamInfo streamInfo;

    private class FlacOggSeeker implements SeekMap, OggSeeker {
        private static final int METADATA_LENGTH_OFFSET = 1;
        private static final int SEEK_POINT_SIZE = 18;
        private long firstFrameOffset = -1;
        private long pendingSeekGranule = -1;
        private long[] seekPointGranules;
        private long[] seekPointOffsets;

        public SeekMap createSeekMap() {
            return this;
        }

        public boolean isSeekable() {
            return true;
        }

        public void setFirstFrameOffset(long j) {
            this.firstFrameOffset = j;
        }

        public void parseSeekTable(ParsableByteArray parsableByteArray) {
            parsableByteArray.skipBytes(1);
            int readUnsignedInt24 = parsableByteArray.readUnsignedInt24() / 18;
            this.seekPointGranules = new long[readUnsignedInt24];
            this.seekPointOffsets = new long[readUnsignedInt24];
            for (int i = 0; i < readUnsignedInt24; i++) {
                this.seekPointGranules[i] = parsableByteArray.readLong();
                this.seekPointOffsets[i] = parsableByteArray.readLong();
                parsableByteArray.skipBytes(2);
            }
        }

        public long read(ExtractorInput extractorInput) throws IOException, InterruptedException {
            if (this.pendingSeekGranule < 0) {
                return -1;
            }
            long j = -(this.pendingSeekGranule + 2);
            this.pendingSeekGranule = -1;
            return j;
        }

        public long startSeek(long j) {
            j = FlacReader.this.convertTimeToGranule(j);
            this.pendingSeekGranule = this.seekPointGranules[Util.binarySearchFloor(this.seekPointGranules, j, true, true)];
            return j;
        }

        public SeekPoints getSeekPoints(long j) {
            int binarySearchFloor = Util.binarySearchFloor(this.seekPointGranules, FlacReader.this.convertTimeToGranule(j), true, true);
            long convertGranuleToTime = FlacReader.this.convertGranuleToTime(this.seekPointGranules[binarySearchFloor]);
            SeekPoint seekPoint = new SeekPoint(convertGranuleToTime, this.firstFrameOffset + this.seekPointOffsets[binarySearchFloor]);
            if (convertGranuleToTime < j) {
                if (binarySearchFloor != this.seekPointGranules.length - 1) {
                    binarySearchFloor++;
                    return new SeekPoints(seekPoint, new SeekPoint(FlacReader.this.convertGranuleToTime(this.seekPointGranules[binarySearchFloor]), this.firstFrameOffset + this.seekPointOffsets[binarySearchFloor]));
                }
            }
            return new SeekPoints(seekPoint);
        }

        public long getDurationUs() {
            return FlacReader.this.streamInfo.durationUs();
        }
    }

    FlacReader() {
    }

    public static boolean verifyBitstreamType(ParsableByteArray parsableByteArray) {
        return (parsableByteArray.bytesLeft() >= 5 && parsableByteArray.readUnsignedByte() == 127 && parsableByteArray.readUnsignedInt() == NUM) ? true : null;
    }

    protected void reset(boolean z) {
        super.reset(z);
        if (z) {
            this.streamInfo = null;
            this.flacOggSeeker = null;
        }
    }

    private static boolean isAudioPacket(byte[] bArr) {
        return bArr[0] == -1;
    }

    protected long preparePayload(ParsableByteArray parsableByteArray) {
        if (isAudioPacket(parsableByteArray.data)) {
            return (long) getFlacFrameBlockSize(parsableByteArray);
        }
        return -1;
    }

    protected boolean readHeaders(ParsableByteArray parsableByteArray, long j, SetupData setupData) throws IOException, InterruptedException {
        byte[] bArr = parsableByteArray.data;
        if (this.streamInfo == null) {
            this.streamInfo = new FlacStreamInfo(bArr, 17);
            parsableByteArray = Arrays.copyOfRange(bArr, 9, parsableByteArray.limit());
            parsableByteArray[4] = Byte.MIN_VALUE;
            setupData.format = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_FLAC, null, -1, this.streamInfo.bitRate(), this.streamInfo.channels, this.streamInfo.sampleRate, Collections.singletonList(parsableByteArray), null, 0, null);
        } else if ((bArr[0] & 127) == 3) {
            this.flacOggSeeker = new FlacOggSeeker();
            this.flacOggSeeker.parseSeekTable(parsableByteArray);
        } else if (isAudioPacket(bArr) != null) {
            if (this.flacOggSeeker != null) {
                this.flacOggSeeker.setFirstFrameOffset(j);
                setupData.oggSeeker = this.flacOggSeeker;
            }
            return false;
        }
        return true;
    }

    private int getFlacFrameBlockSize(ParsableByteArray parsableByteArray) {
        int i = (parsableByteArray.data[2] & 255) >> 4;
        switch (i) {
            case 1:
                return PsExtractor.AUDIO_STREAM;
            case 2:
            case 3:
            case 4:
            case 5:
                return 576 << (i - 2);
            case 6:
            case 7:
                parsableByteArray.skipBytes(4);
                parsableByteArray.readUtf8EncodedLong();
                i = i == 6 ? parsableByteArray.readUnsignedByte() : parsableByteArray.readUnsignedShort();
                parsableByteArray.setPosition(0);
                return i + 1;
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return 256 << (i - 8);
            default:
                return -1;
        }
    }
}
