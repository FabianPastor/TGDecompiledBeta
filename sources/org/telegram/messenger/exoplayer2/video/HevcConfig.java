package org.telegram.messenger.exoplayer2.video;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class HevcConfig {
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;

    public static HevcConfig parse(ParsableByteArray data) throws ParserException {
        try {
            int csdLength;
            int nalUnitLength;
            data.skipBytes(21);
            int lengthSizeMinusOne = data.readUnsignedByte() & 3;
            int numberOfArrays = data.readUnsignedByte();
            int csdStartPosition = data.getPosition();
            int csdLength2 = 0;
            int i = 0;
            while (i < numberOfArrays) {
                data.skipBytes(1);
                int numberOfNalUnits = data.readUnsignedShort();
                csdLength = csdLength2;
                for (csdLength2 = 0; csdLength2 < numberOfNalUnits; csdLength2++) {
                    nalUnitLength = data.readUnsignedShort();
                    csdLength += 4 + nalUnitLength;
                    data.skipBytes(nalUnitLength);
                }
                i++;
                csdLength2 = csdLength;
            }
            data.setPosition(csdStartPosition);
            byte[] buffer = new byte[csdLength2];
            nalUnitLength = 0;
            csdLength = 0;
            while (csdLength < numberOfArrays) {
                data.skipBytes(1);
                int numberOfNalUnits2 = data.readUnsignedShort();
                int bufferPosition = nalUnitLength;
                for (nalUnitLength = 0; nalUnitLength < numberOfNalUnits2; nalUnitLength++) {
                    int nalUnitLength2 = data.readUnsignedShort();
                    System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, buffer, bufferPosition, NalUnitUtil.NAL_START_CODE.length);
                    bufferPosition += NalUnitUtil.NAL_START_CODE.length;
                    System.arraycopy(data.data, data.getPosition(), buffer, bufferPosition, nalUnitLength2);
                    bufferPosition += nalUnitLength2;
                    data.skipBytes(nalUnitLength2);
                }
                csdLength++;
                nalUnitLength = bufferPosition;
            }
            return new HevcConfig(csdLength2 == 0 ? null : Collections.singletonList(buffer), lengthSizeMinusOne + 1);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ParserException("Error parsing HEVC config", e);
        }
    }

    private HevcConfig(List<byte[]> initializationData, int nalUnitLengthFieldLength) {
        this.initializationData = initializationData;
        this.nalUnitLengthFieldLength = nalUnitLengthFieldLength;
    }
}
