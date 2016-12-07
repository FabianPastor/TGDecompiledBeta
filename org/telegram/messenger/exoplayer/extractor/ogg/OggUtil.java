package org.telegram.messenger.exoplayer.extractor.ogg;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

final class OggUtil {
    public static final int PAGE_HEADER_SIZE = 27;
    private static final int TYPE_OGGS = Util.getIntegerCodeForString("OggS");

    public static class PacketInfoHolder {
        public int segmentCount;
        public int size;
    }

    public static final class PageHeader {
        public int bodySize;
        public long granulePosition;
        public int headerSize;
        public final int[] laces = new int[255];
        public long pageChecksum;
        public int pageSegmentCount;
        public long pageSequenceNumber;
        public int revision;
        public long streamSerialNumber;
        public int type;

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
    }

    OggUtil() {
    }

    public static int readBits(byte src, int length, int leastSignificantBitIndex) {
        return (src >> leastSignificantBitIndex) & (255 >>> (8 - length));
    }

    public static void skipToNextPage(ExtractorInput input) throws IOException, InterruptedException {
        byte[] buffer = new byte[2048];
        int peekLength = buffer.length;
        while (true) {
            if (input.getLength() != -1 && input.getPosition() + ((long) peekLength) > input.getLength()) {
                peekLength = (int) (input.getLength() - input.getPosition());
                if (peekLength < 4) {
                    throw new EOFException();
                }
            }
            input.peekFully(buffer, 0, peekLength, false);
            int i = 0;
            while (i < peekLength - 3) {
                if (buffer[i] == (byte) 79 && buffer[i + 1] == (byte) 103 && buffer[i + 2] == (byte) 103 && buffer[i + 3] == (byte) 83) {
                    input.skipFully(i);
                    return;
                }
                i++;
            }
            input.skipFully(peekLength - 3);
        }
    }

    public static boolean populatePageHeader(ExtractorInput input, PageHeader header, ParsableByteArray scratch, boolean quite) throws IOException, InterruptedException {
        boolean hasEnoughBytes;
        scratch.reset();
        header.reset();
        if (input.getLength() == -1 || input.getLength() - input.getPeekPosition() >= 27) {
            hasEnoughBytes = true;
        } else {
            hasEnoughBytes = false;
        }
        if (hasEnoughBytes && input.peekFully(scratch.data, 0, 27, true)) {
            if (scratch.readUnsignedInt() == ((long) TYPE_OGGS)) {
                header.revision = scratch.readUnsignedByte();
                if (header.revision == 0) {
                    header.type = scratch.readUnsignedByte();
                    header.granulePosition = scratch.readLittleEndianLong();
                    header.streamSerialNumber = scratch.readLittleEndianUnsignedInt();
                    header.pageSequenceNumber = scratch.readLittleEndianUnsignedInt();
                    header.pageChecksum = scratch.readLittleEndianUnsignedInt();
                    header.pageSegmentCount = scratch.readUnsignedByte();
                    scratch.reset();
                    header.headerSize = header.pageSegmentCount + 27;
                    input.peekFully(scratch.data, 0, header.pageSegmentCount);
                    for (int i = 0; i < header.pageSegmentCount; i++) {
                        header.laces[i] = scratch.readUnsignedByte();
                        header.bodySize += header.laces[i];
                    }
                    return true;
                } else if (quite) {
                    return false;
                } else {
                    throw new ParserException("unsupported bit stream revision");
                }
            } else if (quite) {
                return false;
            } else {
                throw new ParserException("expected OggS capture pattern at begin of page");
            }
        } else if (quite) {
            return false;
        } else {
            throw new EOFException();
        }
    }

    public static void calculatePacketSize(PageHeader header, int startSegmentIndex, PacketInfoHolder holder) {
        holder.segmentCount = 0;
        holder.size = 0;
        while (holder.segmentCount + startSegmentIndex < header.pageSegmentCount) {
            int[] iArr = header.laces;
            int i = holder.segmentCount;
            holder.segmentCount = i + 1;
            int segmentLength = iArr[i + startSegmentIndex];
            holder.size += segmentLength;
            if (segmentLength != 255) {
                return;
            }
        }
    }
}
