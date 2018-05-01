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
    public Metadata decode(MetadataInputBuffer metadataInputBuffer) {
        ByteBuffer byteBuffer = metadataInputBuffer.data;
        byte[] array = byteBuffer.array();
        int limit = byteBuffer.limit();
        ParsableByteArray parsableByteArray = new ParsableByteArray(array, limit);
        String readNullTerminatedString = parsableByteArray.readNullTerminatedString();
        String readNullTerminatedString2 = parsableByteArray.readNullTerminatedString();
        long readUnsignedInt = parsableByteArray.readUnsignedInt();
        long scaleLargeTimestamp = Util.scaleLargeTimestamp(parsableByteArray.readUnsignedInt(), C0542C.MICROS_PER_SECOND, readUnsignedInt);
        long scaleLargeTimestamp2 = Util.scaleLargeTimestamp(parsableByteArray.readUnsignedInt(), 1000, readUnsignedInt);
        long readUnsignedInt2 = parsableByteArray.readUnsignedInt();
        byte[] copyOfRange = Arrays.copyOfRange(array, parsableByteArray.getPosition(), limit);
        return new Metadata(new EventMessage(readNullTerminatedString, readNullTerminatedString2, scaleLargeTimestamp2, readUnsignedInt2, copyOfRange, scaleLargeTimestamp));
    }
}
