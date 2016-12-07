package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.text.cea.Cea608Decoder;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class SeiReader {
    private final TrackOutput output;

    public SeiReader(TrackOutput output) {
        this.output = output;
        output.format(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, null, -1, 0, null, null));
    }

    public void consume(long pesTimeUs, ParsableByteArray seiBuffer) {
        while (seiBuffer.bytesLeft() > 1) {
            int b;
            int payloadType = 0;
            do {
                b = seiBuffer.readUnsignedByte();
                payloadType += b;
            } while (b == 255);
            int payloadSize = 0;
            do {
                b = seiBuffer.readUnsignedByte();
                payloadSize += b;
            } while (b == 255);
            if (Cea608Decoder.isSeiMessageCea608(payloadType, payloadSize, seiBuffer)) {
                seiBuffer.skipBytes(8);
                int ccCount = seiBuffer.readUnsignedByte() & 31;
                seiBuffer.skipBytes(1);
                int sampleBytes = 0;
                for (int i = 0; i < ccCount; i++) {
                    if ((seiBuffer.peekUnsignedByte() & 7) != 4) {
                        seiBuffer.skipBytes(3);
                    } else {
                        sampleBytes += 3;
                        this.output.sampleData(seiBuffer, 3);
                    }
                }
                this.output.sampleMetadata(pesTimeUs, 1, sampleBytes, 0, null);
                seiBuffer.skipBytes(payloadSize - ((ccCount * 3) + 10));
            } else {
                seiBuffer.skipBytes(payloadSize);
            }
        }
    }
}
