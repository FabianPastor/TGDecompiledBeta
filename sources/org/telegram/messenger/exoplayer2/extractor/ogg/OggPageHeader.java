package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

final class OggPageHeader {
    public static final int EMPTY_PAGE_HEADER_SIZE = 27;
    public static final int MAX_PAGE_PAYLOAD = 65025;
    public static final int MAX_PAGE_SIZE = 65307;
    public static final int MAX_SEGMENT_COUNT = 255;
    private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");
    public int bodySize;
    public long granulePosition;
    public int headerSize;
    public final int[] laces = new int[255];
    public long pageChecksum;
    public int pageSegmentCount;
    public long pageSequenceNumber;
    public int revision;
    private final ParsableByteArray scratch = new ParsableByteArray(255);
    public long streamSerialNumber;
    public int type;

    OggPageHeader() {
    }

    public void reset() {
        this.revision = 0;
        this.type = 0;
        this.granulePosition = 0;
        this.streamSerialNumber = 0;
        this.pageSequenceNumber = 0;
        this.pageChecksum = 0;
        this.pageSegmentCount = 0;
        this.headerSize = 0;
        this.bodySize = 0;
    }

    public boolean populate(ExtractorInput extractorInput, boolean z) throws IOException, InterruptedException {
        int i;
        this.scratch.reset();
        reset();
        int i2 = 0;
        if (extractorInput.getLength() != -1) {
            if (extractorInput.getLength() - extractorInput.getPeekPosition() < 27) {
                i = 0;
                if (i != 0) {
                    if (!extractorInput.peekFully(this.scratch.data, 0, 27, true)) {
                        if (this.scratch.readUnsignedInt() != ((long) TYPE_OGGS)) {
                            this.revision = this.scratch.readUnsignedByte();
                            if (this.revision != 0) {
                                this.type = this.scratch.readUnsignedByte();
                                this.granulePosition = this.scratch.readLittleEndianLong();
                                this.streamSerialNumber = this.scratch.readLittleEndianUnsignedInt();
                                this.pageSequenceNumber = this.scratch.readLittleEndianUnsignedInt();
                                this.pageChecksum = this.scratch.readLittleEndianUnsignedInt();
                                this.pageSegmentCount = this.scratch.readUnsignedByte();
                                this.headerSize = 27 + this.pageSegmentCount;
                                this.scratch.reset();
                                extractorInput.peekFully(this.scratch.data, 0, this.pageSegmentCount);
                                while (i2 < this.pageSegmentCount) {
                                    this.laces[i2] = this.scratch.readUnsignedByte();
                                    this.bodySize += this.laces[i2];
                                    i2++;
                                }
                                return true;
                            } else if (z) {
                                return false;
                            } else {
                                throw new ParserException("unsupported bit stream revision");
                            }
                        } else if (z) {
                            return false;
                        } else {
                            throw new ParserException("expected OggS capture pattern at begin of page");
                        }
                    }
                }
                if (z) {
                    return false;
                }
                throw new EOFException();
            }
        }
        i = true;
        if (i != 0) {
            if (!extractorInput.peekFully(this.scratch.data, 0, 27, true)) {
                if (this.scratch.readUnsignedInt() != ((long) TYPE_OGGS)) {
                    this.revision = this.scratch.readUnsignedByte();
                    if (this.revision != 0) {
                        this.type = this.scratch.readUnsignedByte();
                        this.granulePosition = this.scratch.readLittleEndianLong();
                        this.streamSerialNumber = this.scratch.readLittleEndianUnsignedInt();
                        this.pageSequenceNumber = this.scratch.readLittleEndianUnsignedInt();
                        this.pageChecksum = this.scratch.readLittleEndianUnsignedInt();
                        this.pageSegmentCount = this.scratch.readUnsignedByte();
                        this.headerSize = 27 + this.pageSegmentCount;
                        this.scratch.reset();
                        extractorInput.peekFully(this.scratch.data, 0, this.pageSegmentCount);
                        while (i2 < this.pageSegmentCount) {
                            this.laces[i2] = this.scratch.readUnsignedByte();
                            this.bodySize += this.laces[i2];
                            i2++;
                        }
                        return true;
                    } else if (z) {
                        return false;
                    } else {
                        throw new ParserException("unsupported bit stream revision");
                    }
                } else if (z) {
                    return false;
                } else {
                    throw new ParserException("expected OggS capture pattern at begin of page");
                }
            }
        }
        if (z) {
            return false;
        }
        throw new EOFException();
    }
}
