package org.telegram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.FramePredicate;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class Id3Peeker {
    private final ParsableByteArray scratch = new ParsableByteArray(10);

    public Metadata peekId3Data(ExtractorInput input, FramePredicate id3FramePredicate) throws IOException, InterruptedException {
        int peekedId3Bytes = 0;
        Metadata metadata = null;
        while (true) {
            try {
                input.peekFully(this.scratch.data, 0, 10);
                this.scratch.setPosition(0);
                if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG) {
                    break;
                }
                this.scratch.skipBytes(3);
                int framesLength = this.scratch.readSynchSafeInt();
                int tagLength = framesLength + 10;
                if (metadata == null) {
                    byte[] id3Data = new byte[tagLength];
                    System.arraycopy(this.scratch.data, 0, id3Data, 0, 10);
                    input.peekFully(id3Data, 10, framesLength);
                    metadata = new Id3Decoder(id3FramePredicate).decode(id3Data, tagLength);
                } else {
                    input.advancePeekPosition(framesLength);
                }
                peekedId3Bytes += tagLength;
            } catch (EOFException e) {
            }
        }
        input.resetPeekPosition();
        input.advancePeekPosition(peekedId3Bytes);
        return metadata;
    }
}
