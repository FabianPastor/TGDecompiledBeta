package org.telegram.messenger.exoplayer2.text.cea;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class CeaUtil {
    private static final int COUNTRY_CODE = 181;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE = 49;
    private static final String TAG = "CeaUtil";
    private static final int USER_DATA_TYPE_CODE = 3;
    private static final int USER_ID = NUM;

    public static void consume(long j, ParsableByteArray parsableByteArray, TrackOutput[] trackOutputArr) {
        while (parsableByteArray.bytesLeft() > 1) {
            int readNon255TerminatedValue = readNon255TerminatedValue(parsableByteArray);
            int readNon255TerminatedValue2 = readNon255TerminatedValue(parsableByteArray);
            if (readNon255TerminatedValue2 != -1) {
                if (readNon255TerminatedValue2 <= parsableByteArray.bytesLeft()) {
                    if (isSeiMessageCea608(readNon255TerminatedValue, readNon255TerminatedValue2, parsableByteArray)) {
                        parsableByteArray.skipBytes(8);
                        readNon255TerminatedValue = parsableByteArray.readUnsignedByte() & 31;
                        parsableByteArray.skipBytes(1);
                        readNon255TerminatedValue *= 3;
                        int position = parsableByteArray.getPosition();
                        for (TrackOutput trackOutput : trackOutputArr) {
                            parsableByteArray.setPosition(position);
                            trackOutput.sampleData(parsableByteArray, readNon255TerminatedValue);
                            trackOutput.sampleMetadata(j, 1, readNon255TerminatedValue, 0, null);
                        }
                        parsableByteArray.skipBytes(readNon255TerminatedValue2 - (10 + readNon255TerminatedValue));
                    } else {
                        parsableByteArray.skipBytes(readNon255TerminatedValue2);
                    }
                }
            }
            Log.w(TAG, "Skipping remainder of malformed SEI NAL unit.");
            parsableByteArray.setPosition(parsableByteArray.limit());
        }
    }

    private static int readNon255TerminatedValue(ParsableByteArray parsableByteArray) {
        int i = 0;
        while (parsableByteArray.bytesLeft() != 0) {
            int readUnsignedByte = parsableByteArray.readUnsignedByte();
            i += readUnsignedByte;
            if (readUnsignedByte != 255) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isSeiMessageCea608(int i, int i2, ParsableByteArray parsableByteArray) {
        boolean z = false;
        if (i == 4) {
            if (i2 >= 8) {
                i = parsableByteArray.getPosition();
                i2 = parsableByteArray.readUnsignedByte();
                int readUnsignedShort = parsableByteArray.readUnsignedShort();
                int readInt = parsableByteArray.readInt();
                int readUnsignedByte = parsableByteArray.readUnsignedByte();
                parsableByteArray.setPosition(i);
                if (i2 == COUNTRY_CODE && readUnsignedShort == PROVIDER_CODE && readInt == USER_ID && readUnsignedByte == 3) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private CeaUtil() {
    }
}
