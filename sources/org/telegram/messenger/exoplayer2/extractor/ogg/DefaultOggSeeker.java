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
        private OggSeekMap() {
        }

        public boolean isSeekable() {
            return true;
        }

        public SeekPoints getSeekPoints(long timeUs) {
            if (timeUs == 0) {
                return new SeekPoints(new SeekPoint(0, DefaultOggSeeker.this.startPosition));
            }
            return new SeekPoints(new SeekPoint(timeUs, DefaultOggSeeker.this.getEstimatedPosition(DefaultOggSeeker.this.startPosition, DefaultOggSeeker.this.streamReader.convertTimeToGranule(timeUs), 30000)));
        }

        public long getDurationUs() {
            return DefaultOggSeeker.this.streamReader.convertGranuleToTime(DefaultOggSeeker.this.totalGranules);
        }
    }

    public DefaultOggSeeker(long startPosition, long endPosition, StreamReader streamReader, int firstPayloadPageSize, long firstPayloadPageGranulePosition) {
        boolean z = startPosition >= 0 && endPosition > startPosition;
        Assertions.checkArgument(z);
        this.streamReader = streamReader;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        if (((long) firstPayloadPageSize) == endPosition - startPosition) {
            this.totalGranules = firstPayloadPageGranulePosition;
            this.state = 3;
            return;
        }
        this.state = 0;
    }

    public long read(ExtractorInput input) throws IOException, InterruptedException {
        long lastPageSearchPosition;
        switch (this.state) {
            case 0:
                this.positionBeforeSeekToEnd = input.getPosition();
                this.state = 1;
                lastPageSearchPosition = this.endPosition - 65307;
                if (lastPageSearchPosition > this.positionBeforeSeekToEnd) {
                    return lastPageSearchPosition;
                }
                break;
            case 1:
                break;
            case 2:
                long currentGranule;
                if (this.targetGranule == 0) {
                    currentGranule = 0;
                } else {
                    lastPageSearchPosition = getNextSeekPosition(this.targetGranule, input);
                    if (lastPageSearchPosition >= 0) {
                        return lastPageSearchPosition;
                    }
                    currentGranule = skipToPageOfGranule(input, this.targetGranule, -(lastPageSearchPosition + 2));
                }
                this.state = 3;
                return -(currentGranule + 2);
            case 3:
                return -1;
            default:
                throw new IllegalStateException();
        }
        this.totalGranules = readGranuleOfLastPage(input);
        this.state = 3;
        return this.positionBeforeSeekToEnd;
    }

    public long startSeek(long timeUs) {
        boolean z;
        long j;
        if (this.state != 3) {
            if (this.state != 2) {
                z = false;
                Assertions.checkArgument(z);
                j = 0;
                if (timeUs == 0) {
                    j = this.streamReader.convertTimeToGranule(timeUs);
                }
                this.targetGranule = j;
                this.state = 2;
                resetSeeking();
                return this.targetGranule;
            }
        }
        z = true;
        Assertions.checkArgument(z);
        j = 0;
        if (timeUs == 0) {
            j = this.streamReader.convertTimeToGranule(timeUs);
        }
        this.targetGranule = j;
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

    public long getNextSeekPosition(long targetGranule, ExtractorInput input) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        if (this.start == this.end) {
            return -(r0.startGranule + 2);
        }
        long initialPosition = input.getPosition();
        if (skipToNextPage(extractorInput, r0.end)) {
            long j;
            r0.pageHeader.populate(extractorInput, false);
            input.resetPeekPosition();
            long granuleDistance = targetGranule - r0.pageHeader.granulePosition;
            int pageSize = r0.pageHeader.headerSize + r0.pageHeader.bodySize;
            if (granuleDistance >= 0) {
                if (granuleDistance <= 72000) {
                    extractorInput.skipFully(pageSize);
                    return -(r0.pageHeader.granulePosition + 2);
                }
            }
            if (granuleDistance < 0) {
                r0.end = initialPosition;
                r0.endGranule = r0.pageHeader.granulePosition;
                j = 2;
            } else {
                r0.start = input.getPosition() + ((long) pageSize);
                r0.startGranule = r0.pageHeader.granulePosition;
                if ((r0.end - r0.start) + ((long) pageSize) < 100000) {
                    extractorInput.skipFully(pageSize);
                    return -(r0.startGranule + 2);
                }
                j = 2;
            }
            if (r0.end - r0.start < 100000) {
                r0.end = r0.start;
                return r0.start;
            }
            long offset = (long) pageSize;
            if (granuleDistance > 0) {
                j = 1;
            }
            return Math.min(Math.max((input.getPosition() - (offset * j)) + (((r0.end - r0.start) * granuleDistance) / (r0.endGranule - r0.startGranule)), r0.start), r0.end - 1);
        } else if (r0.start != initialPosition) {
            return r0.start;
        } else {
            throw new IOException("No ogg page can be found.");
        }
    }

    private long getEstimatedPosition(long position, long granuleDistance, long offset) {
        long position2 = position + ((((this.endPosition - this.startPosition) * granuleDistance) / this.totalGranules) - offset);
        if (position2 < this.startPosition) {
            position2 = this.startPosition;
        }
        if (position2 >= this.endPosition) {
            return this.endPosition - 1;
        }
        return position2;
    }

    void skipToNextPage(ExtractorInput input) throws IOException, InterruptedException {
        if (!skipToNextPage(input, this.endPosition)) {
            throw new EOFException();
        }
    }

    boolean skipToNextPage(ExtractorInput input, long until) throws IOException, InterruptedException {
        until = Math.min(until + 3, this.endPosition);
        byte[] buffer = new byte[2048];
        int peekLength = buffer.length;
        while (true) {
            int i = 0;
            if (input.getPosition() + ((long) peekLength) > until) {
                peekLength = (int) (until - input.getPosition());
                if (peekLength < 4) {
                    return false;
                }
            }
            input.peekFully(buffer, 0, peekLength, false);
            while (true) {
                int i2 = i;
                if (i2 >= peekLength - 3) {
                    break;
                } else if (buffer[i2] == (byte) 79 && buffer[i2 + 1] == (byte) 103 && buffer[i2 + 2] == (byte) 103 && buffer[i2 + 3] == (byte) 83) {
                    input.skipFully(i2);
                    return true;
                } else {
                    i = i2 + 1;
                }
            }
            input.skipFully(peekLength - 3);
        }
    }

    long readGranuleOfLastPage(ExtractorInput input) throws IOException, InterruptedException {
        skipToNextPage(input);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && input.getPosition() < this.endPosition) {
            this.pageHeader.populate(input, false);
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    long skipToPageOfGranule(ExtractorInput input, long targetGranule, long currentGranule) throws IOException, InterruptedException {
        this.pageHeader.populate(input, false);
        while (this.pageHeader.granulePosition < targetGranule) {
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            currentGranule = this.pageHeader.granulePosition;
            this.pageHeader.populate(input, false);
        }
        input.resetPeekPosition();
        return currentGranule;
    }
}
