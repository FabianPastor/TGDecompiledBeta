package org.telegram.messenger.exoplayer2.metadata.emsg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0600C;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessageDecoder implements MetadataDecoder {
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        ParsableByteArray parsableByteArray = new ParsableByteArray(buffer.array(), buffer.limit());
        String schemeIdUri = parsableByteArray.readNullTerminatedString();
        String value = parsableByteArray.readNullTerminatedString();
        long timescale = parsableByteArray.readUnsignedInt();
        long presentationTimeUs = Util.scaleLargeTimestamp(parsableByteArray.readUnsignedInt(), C0600C.MICROS_PER_SECOND, timescale);
        long durationMs = Util.scaleLargeTimestamp(parsableByteArray.readUnsignedInt(), 1000, timescale);
        long id = parsableByteArray.readUnsignedInt();
        int position = parsableByteArray.getPosition();
        return new Metadata(new EventMessage(schemeIdUri, value, durationMs, id, Arrays.copyOfRange(data, position, size), presentationTimeUs));
    }
}
