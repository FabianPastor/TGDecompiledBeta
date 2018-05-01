package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.SeekPoints;
import org.telegram.messenger.exoplayer2.extractor.SeekPoint;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class DefaultOggSeeker implements OggSeeker {
    private static final int DEFAULT_OFFSET = 30000;
    public static final int MATCH_BYTE_RANGE = 100000;
    public static final int MATCH_RANGE = 72000;
    private static final int STATE_IDLE = 3;
    private static final int STATE_READ_LAST_PAGE = 1;
    private static final int STATE_SEEK = 2;
    private static final int STATE_SEEK_TO_END = 0;
    private long end;
    private long endGranule;
    private final long endPosition;
    private final OggPageHeader pageHeader = new OggPageHeader();
    private long positionBeforeSeekToEnd;
    private long start;
    private long startGranule;
    private final long startPosition;
    private int state;
    private final StreamReader streamReader;
    private long targetGranule;
    private long totalGranules;

    private class OggSeekMap implements SeekMap {
        public boolean isSeekable() {
            return true;
        }

        private OggSeekMap() {
        }

        public SeekPoints getSeekPoints(long j) {
            if (j == 0) {
                return new SeekPoints(new SeekPoint(0, DefaultOggSeeker.this.startPosition));
            }
            return new SeekPoints(new SeekPoint(j, DefaultOggSeeker.this.getEstimatedPosition(DefaultOggSeeker.this.startPosition, DefaultOggSeeker.this.streamReader.convertTimeToGranule(j), 30000)));
        }

        public long getDurationUs() {
            return DefaultOggSeeker.this.streamReader.convertGranuleToTime(DefaultOggSeeker.this.totalGranules);
        }
    }

    public DefaultOggSeeker(long j, long j2, StreamReader streamReader, int i, long j3) {
        boolean z = j >= 0 && j2 > j;
        Assertions.checkArgument(z);
        this.streamReader = streamReader;
        this.startPosition = j;
        this.endPosition = j2;
        if (((long) i) == j2 - j) {
            this.totalGranules = j3;
            this.state = 3;
            return;
        }
        this.state = 0;
    }

    public long read(ExtractorInput extractorInput) throws IOException, InterruptedException {
        long j;
        switch (this.state) {
            case 0:
                this.positionBeforeSeekToEnd = extractorInput.getPosition();
                this.state = 1;
                j = this.endPosition - 65307;
                if (j > this.positionBeforeSeekToEnd) {
                    return j;
                }
                break;
            case 1:
                break;
            case 2:
                long j2 = 0;
                if (this.targetGranule != 0) {
                    j = getNextSeekPosition(this.targetGranule, extractorInput);
                    if (j >= 0) {
                        return j;
                    }
                    j2 = skipToPageOfGranule(extractorInput, this.targetGranule, -(j + 2));
                }
                this.state = 3;
                return -(j2 + 2);
            case 3:
                return -1;
            default:
                throw new IllegalStateException();
        }
        this.totalGranules = readGranuleOfLastPage(extractorInput);
        this.state = 3;
        return this.positionBeforeSeekToEnd;
    }

    public long startSeek(long j) {
        boolean z;
        long j2;
        if (this.state != 3) {
            if (this.state != 2) {
                z = false;
                Assertions.checkArgument(z);
                j2 = 0;
                if (j == 0) {
                    j2 = this.streamReader.convertTimeToGranule(j);
                }
                this.targetGranule = j2;
                this.state = 2;
                resetSeeking();
                return this.targetGranule;
            }
        }
        z = true;
        Assertions.checkArgument(z);
        j2 = 0;
        if (j == 0) {
            j2 = this.streamReader.convertTimeToGranule(j);
        }
        this.targetGranule = j2;
        this.state = 2;
        resetSeeking();
        return this.targetGranule;
    }

    public OggSeekMap createSeekMap() {
        return this.totalGranules != 0 ? new OggSeekMap() : null;
    }

    public void resetSeeking() {
        this.start = this.startPosition;
        this.end = this.endPosition;
        this.startGranule = 0;
        this.endGranule = this.totalGranules;
    }

    public long getNextSeekPosition(long j, ExtractorInput extractorInput) throws IOException, InterruptedException {
        ExtractorInput extractorInput2 = extractorInput;
        long j2 = 2;
        if (this.start == this.end) {
            return -(r0.startGranule + 2);
        }
        long position = extractorInput.getPosition();
        if (skipToNextPage(extractorInput2, r0.end)) {
            r0.pageHeader.populate(extractorInput2, false);
            extractorInput.resetPeekPosition();
            long j3 = j - r0.pageHeader.granulePosition;
            int i = r0.pageHeader.headerSize + r0.pageHeader.bodySize;
            if (j3 >= 0) {
                if (j3 <= 72000) {
                    extractorInput2.skipFully(i);
                    return -(r0.pageHeader.granulePosition + 2);
                }
            }
            if (j3 < 0) {
                r0.end = position;
                r0.endGranule = r0.pageHeader.granulePosition;
            } else {
                long j4 = (long) i;
                r0.start = extractorInput.getPosition() + j4;
                r0.startGranule = r0.pageHeader.granulePosition;
                if ((r0.end - r0.start) + j4 < 100000) {
                    extractorInput2.skipFully(i);
                    return -(r0.startGranule + 2);
                }
            }
            if (r0.end - r0.start < 100000) {
                r0.end = r0.start;
                return r0.start;
            }
            position = (long) i;
            if (j3 > 0) {
                j2 = 1;
            }
            return Math.min(Math.max((extractorInput.getPosition() - (position * j2)) + ((j3 * (r0.end - r0.start)) / (r0.endGranule - r0.startGranule)), r0.start), r0.end - 1);
        } else if (r0.start != position) {
            return r0.start;
        } else {
            throw new IOException("No ogg page can be found.");
        }
    }

    private long getEstimatedPosition(long j, long j2, long j3) {
        j2 = j + (((j2 * (this.endPosition - this.startPosition)) / this.totalGranules) - j3);
        if (j2 < this.startPosition) {
            j2 = this.startPosition;
        }
        return j2 >= this.endPosition ? this.endPosition - 1 : j2;
    }

    void skipToNextPage(ExtractorInput extractorInput) throws IOException, InterruptedException {
        if (skipToNextPage(extractorInput, this.endPosition) == null) {
            throw new EOFException();
        }
    }

    boolean skipToNextPage(ExtractorInput extractorInput, long j) throws IOException, InterruptedException {
        j = Math.min(j + 3, this.endPosition);
        byte[] bArr = new byte[2048];
        int length = bArr.length;
        while (true) {
            int i;
            int i2 = 0;
            if (extractorInput.getPosition() + ((long) length) > j) {
                length = (int) (j - extractorInput.getPosition());
                if (length < 4) {
                    return false;
                }
            }
            extractorInput.peekFully(bArr, 0, length, false);
            while (true) {
                i = length - 3;
                if (i2 >= i) {
                    break;
                } else if (bArr[i2] == (byte) 79 && bArr[i2 + 1] == (byte) 103 && bArr[i2 + 2] == (byte) 103 && bArr[i2 + 3] == (byte) 83) {
                    extractorInput.skipFully(i2);
                    return true;
                } else {
                    i2++;
                }
            }
            extractorInput.skipFully(i);
        }
    }

    long readGranuleOfLastPage(ExtractorInput extractorInput) throws IOException, InterruptedException {
        skipToNextPage(extractorInput);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && extractorInput.getPosition() < this.endPosition) {
            this.pageHeader.populate(extractorInput, false);
            extractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    long skipToPageOfGranule(ExtractorInput extractorInput, long j, long j2) throws IOException, InterruptedException {
        this.pageHeader.populate(extractorInput, false);
        while (this.pageHeader.granulePosition < j) {
            extractorInput.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            j2 = this.pageHeader.granulePosition;
            this.pageHeader.populate(extractorInput, false);
        }
        extractorInput.resetPeekPosition();
        return j2;
    }
}
