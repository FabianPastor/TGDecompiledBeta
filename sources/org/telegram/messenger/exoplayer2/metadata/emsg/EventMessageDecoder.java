package org.telegram.messenger.exoplayer2.metadata.emsg;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessageDecoder implements MetadataDecoder {
    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        ParsableByteArray emsgData = new ParsableByteArray(buffer.array(), buffer.limit());
        String schemeIdUri = emsgData.readNullTerminatedString();
        String value = emsgData.readNullTerminatedString();
        long readUnsignedInt = emsgData.readUnsignedInt();
        long presentationTimeUs = Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), C0542C.MICROS_PER_SECOND, readUnsignedInt);
        long durationMs = Util.scaleLargeTimestamp(emsgData.readUnsignedInt(), 1000, readUnsignedInt);
        long id = emsgData.readUnsignedInt();
        return new Metadata(new EventMessage(schemeIdUri, value, durationMs, id, Arrays.copyOfRange(data, emsgData.getPosition(), size), presentationTimeUs));
    }
}
