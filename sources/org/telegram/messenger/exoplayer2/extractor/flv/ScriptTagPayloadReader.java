package org.telegram.messenger.exoplayer2.extractor.flv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class ScriptTagPayloadReader extends TagPayloadReader {
    private static final int AMF_TYPE_BOOLEAN = 1;
    private static final int AMF_TYPE_DATE = 11;
    private static final int AMF_TYPE_ECMA_ARRAY = 8;
    private static final int AMF_TYPE_END_MARKER = 9;
    private static final int AMF_TYPE_NUMBER = 0;
    private static final int AMF_TYPE_OBJECT = 3;
    private static final int AMF_TYPE_STRICT_ARRAY = 10;
    private static final int AMF_TYPE_STRING = 2;
    private static final String KEY_DURATION = "duration";
    private static final String NAME_METADATA = "onMetaData";
    private long durationUs = C0542C.TIME_UNSET;

    protected boolean parseHeader(ParsableByteArray parsableByteArray) {
        return true;
    }

    public void seek() {
    }

    public ScriptTagPayloadReader() {
        super(null);
    }

    public long getDurationUs() {
        return this.durationUs;
    }

    protected void parsePayload(ParsableByteArray parsableByteArray, long j) throws ParserException {
        if (readAmfType(parsableByteArray) != 2) {
            throw new ParserException();
        }
        if (NAME_METADATA.equals(readAmfString(parsableByteArray)) != null && readAmfType(parsableByteArray) == 8) {
            parsableByteArray = readAmfEcmaArray(parsableByteArray);
            if (parsableByteArray.containsKey(KEY_DURATION) != null) {
                parsableByteArray = ((Double) parsableByteArray.get(KEY_DURATION)).doubleValue();
                if (parsableByteArray > 0.0d) {
                    this.durationUs = (long) (parsableByteArray * 1000000.0d);
                }
            }
        }
    }

    private static int readAmfType(ParsableByteArray parsableByteArray) {
        return parsableByteArray.readUnsignedByte();
    }

    private static Boolean readAmfBoolean(ParsableByteArray parsableByteArray) {
        boolean z = true;
        if (parsableByteArray.readUnsignedByte() != 1) {
            z = false;
        }
        return Boolean.valueOf(z);
    }

    private static Double readAmfDouble(ParsableByteArray parsableByteArray) {
        return Double.valueOf(Double.longBitsToDouble(parsableByteArray.readLong()));
    }

    private static String readAmfString(ParsableByteArray parsableByteArray) {
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int position = parsableByteArray.getPosition();
        parsableByteArray.skipBytes(readUnsignedShort);
        return new String(parsableByteArray.data, position, readUnsignedShort);
    }

    private static ArrayList<Object> readAmfStrictArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        ArrayList<Object> arrayList = new ArrayList(readUnsignedIntToInt);
        for (int i = 0; i < readUnsignedIntToInt; i++) {
            arrayList.add(readAmfData(parsableByteArray, readAmfType(parsableByteArray)));
        }
        return arrayList;
    }

    private static HashMap<String, Object> readAmfObject(ParsableByteArray parsableByteArray) {
        HashMap<String, Object> hashMap = new HashMap();
        while (true) {
            String readAmfString = readAmfString(parsableByteArray);
            int readAmfType = readAmfType(parsableByteArray);
            if (readAmfType == 9) {
                return hashMap;
            }
            hashMap.put(readAmfString, readAmfData(parsableByteArray, readAmfType));
        }
    }

    private static HashMap<String, Object> readAmfEcmaArray(ParsableByteArray parsableByteArray) {
        int readUnsignedIntToInt = parsableByteArray.readUnsignedIntToInt();
        HashMap<String, Object> hashMap = new HashMap(readUnsignedIntToInt);
        for (int i = 0; i < readUnsignedIntToInt; i++) {
            hashMap.put(readAmfString(parsableByteArray), readAmfData(parsableByteArray, readAmfType(parsableByteArray)));
        }
        return hashMap;
    }

    private static Date readAmfDate(ParsableByteArray parsableByteArray) {
        Date date = new Date((long) readAmfDouble(parsableByteArray).doubleValue());
        parsableByteArray.skipBytes(2);
        return date;
    }

    private static Object readAmfData(ParsableByteArray parsableByteArray, int i) {
        if (i == 8) {
            return readAmfEcmaArray(parsableByteArray);
        }
        switch (i) {
            case 0:
                return readAmfDouble(parsableByteArray);
            case 1:
                return readAmfBoolean(parsableByteArray);
            case 2:
                return readAmfString(parsableByteArray);
            case 3:
                return readAmfObject(parsableByteArray);
            default:
                switch (i) {
                    case 10:
                        return readAmfStrictArray(parsableByteArray);
                    case 11:
                        return readAmfDate(parsableByteArray);
                    default:
                        return null;
                }
        }
    }
}
