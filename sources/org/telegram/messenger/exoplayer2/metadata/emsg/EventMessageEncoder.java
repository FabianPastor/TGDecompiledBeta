package org.telegram.messenger.exoplayer2.metadata.emsg;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

public final class EventMessageEncoder {
    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
    private final DataOutputStream dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);

    public byte[] encode(EventMessage eventMessage, long timescale) {
        Assertions.checkArgument(timescale >= 0);
        this.byteArrayOutputStream.reset();
        try {
            writeNullTerminatedString(this.dataOutputStream, eventMessage.schemeIdUri);
            writeNullTerminatedString(this.dataOutputStream, eventMessage.value != null ? eventMessage.value : TtmlNode.ANONYMOUS_REGION_ID);
            writeUnsignedInt(this.dataOutputStream, timescale);
            writeUnsignedInt(this.dataOutputStream, Util.scaleLargeTimestamp(eventMessage.presentationTimeUs, timescale, 1000000));
            writeUnsignedInt(this.dataOutputStream, Util.scaleLargeTimestamp(eventMessage.durationMs, timescale, 1000));
            writeUnsignedInt(this.dataOutputStream, eventMessage.id);
            this.dataOutputStream.write(eventMessage.messageData);
            this.dataOutputStream.flush();
            return this.byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeNullTerminatedString(DataOutputStream dataOutputStream, String value) throws IOException {
        dataOutputStream.writeBytes(value);
        dataOutputStream.writeByte(0);
    }

    private static void writeUnsignedInt(DataOutputStream outputStream, long value) throws IOException {
        outputStream.writeByte(((int) (value >>> 24)) & 255);
        outputStream.writeByte(((int) (value >>> 16)) & 255);
        outputStream.writeByte(((int) (value >>> 8)) & 255);
        outputStream.writeByte(((int) value) & 255);
    }
}
