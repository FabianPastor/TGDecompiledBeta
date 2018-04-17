package org.telegram.messenger.exoplayer2.extractor.mkv;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class Sniffer {
    private static final int ID_EBML = 440786851;
    private static final int SEARCH_LENGTH = 1024;
    private int peekLength;
    private final ParsableByteArray scratch = new ParsableByteArray(8);

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        Sniffer sniffer = this;
        ExtractorInput extractorInput = input;
        long inputLength = input.getLength();
        long j = 1024;
        if (inputLength != -1) {
            if (inputLength <= 1024) {
                j = inputLength;
            }
        }
        int bytesToSearch = (int) j;
        boolean z = false;
        extractorInput.peekFully(sniffer.scratch.data, 0, 4);
        sniffer.peekLength = 4;
        for (long tag = sniffer.scratch.readUnsignedInt(); tag != 440786851; tag = ((tag << 8) & -256) | ((long) (sniffer.scratch.data[0] & 255))) {
            int i = sniffer.peekLength + 1;
            sniffer.peekLength = i;
            if (i == bytesToSearch) {
                return false;
            }
            extractorInput.peekFully(sniffer.scratch.data, 0, 1);
        }
        long headerSize = readUint(input);
        long headerStart = (long) sniffer.peekLength;
        if (headerSize == Long.MIN_VALUE) {
        } else if (inputLength == -1 || headerStart + headerSize < inputLength) {
            while (((long) sniffer.peekLength) < headerStart + headerSize) {
                long id = readUint(input);
                if (id == Long.MIN_VALUE) {
                    return z;
                }
                j = readUint(input);
                long j2;
                if (j < 0) {
                    j2 = id;
                } else if (j > 2147483647L) {
                    r24 = inputLength;
                    j2 = id;
                } else {
                    if (j != 0) {
                        extractorInput.advancePeekPosition((int) j);
                        r24 = inputLength;
                        sniffer.peekLength = (int) (((long) sniffer.peekLength) + j);
                    } else {
                        r24 = inputLength;
                    }
                    inputLength = r24;
                    extractorInput = input;
                    z = false;
                }
                return false;
            }
            return ((long) sniffer.peekLength) == headerStart + headerSize;
        } else {
            r24 = inputLength;
        }
        return false;
    }

    private long readUint(ExtractorInput input) throws IOException, InterruptedException {
        int i = 0;
        input.peekFully(this.scratch.data, 0, 1);
        int value = this.scratch.data[0] & 255;
        if (value == 0) {
            return Long.MIN_VALUE;
        }
        int mask = 128;
        int length = 0;
        while ((value & mask) == 0) {
            mask >>= 1;
            length++;
        }
        value &= mask ^ -1;
        input.peekFully(this.scratch.data, 1, length);
        while (i < length) {
            value = (value << 8) + (this.scratch.data[i + 1] & 255);
            i++;
        }
        this.peekLength += length + 1;
        return (long) value;
    }
}
