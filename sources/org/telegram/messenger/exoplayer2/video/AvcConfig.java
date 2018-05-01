package org.telegram.messenger.exoplayer2.video;

import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class AvcConfig {
    public final int height;
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthAspectRatio;
    public final int width;

    public static AvcConfig parse(ParsableByteArray parsableByteArray) throws ParserException {
        try {
            parsableByteArray.skipBytes(4);
            int readUnsignedByte = (parsableByteArray.readUnsignedByte() & 3) + 1;
            if (readUnsignedByte == 3) {
                throw new IllegalStateException();
            }
            int i;
            int i2;
            float f;
            int i3;
            List arrayList = new ArrayList();
            int readUnsignedByte2 = parsableByteArray.readUnsignedByte() & 31;
            for (i = 0; i < readUnsignedByte2; i++) {
                arrayList.add(buildNalUnitForChild(parsableByteArray));
            }
            i = parsableByteArray.readUnsignedByte();
            for (i2 = 0; i2 < i; i2++) {
                arrayList.add(buildNalUnitForChild(parsableByteArray));
            }
            if (readUnsignedByte2 > 0) {
                parsableByteArray = NalUnitUtil.parseSpsNalUnit((byte[]) arrayList.get(0), readUnsignedByte, ((byte[]) arrayList.get(0)).length);
                readUnsignedByte2 = parsableByteArray.width;
                int i4 = parsableByteArray.height;
                f = parsableByteArray.pixelWidthAspectRatio;
                i2 = readUnsignedByte2;
                i3 = i4;
            } else {
                f = 1.0f;
                i2 = -1;
                i3 = i2;
            }
            return new AvcConfig(arrayList, readUnsignedByte, i2, i3, f);
        } catch (ParsableByteArray parsableByteArray2) {
            throw new ParserException("Error parsing AVC config", parsableByteArray2);
        }
    }

    private AvcConfig(List<byte[]> list, int i, int i2, int i3, float f) {
        this.initializationData = list;
        this.nalUnitLengthFieldLength = i;
        this.width = i2;
        this.height = i3;
        this.pixelWidthAspectRatio = f;
    }

    private static byte[] buildNalUnitForChild(ParsableByteArray parsableByteArray) {
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition();
        parsableByteArray.skipBytes(readUnsignedShort);
        return CodecSpecificDataUtil.buildNalUnit(parsableByteArray.data, position, readUnsignedShort);
    }
}
