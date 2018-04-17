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

    public static void consume(long presentationTimeUs, ParsableByteArray seiBuffer, TrackOutput[] outputs) {
        ParsableByteArray parsableByteArray = seiBuffer;
        TrackOutput[] trackOutputArr = outputs;
        while (seiBuffer.bytesLeft() > 1) {
            int payloadType = readNon255TerminatedValue(seiBuffer);
            int payloadSize = readNon255TerminatedValue(seiBuffer);
            if (payloadSize != -1) {
                if (payloadSize <= seiBuffer.bytesLeft()) {
                    if (isSeiMessageCea608(payloadType, payloadSize, parsableByteArray)) {
                        parsableByteArray.skipBytes(8);
                        int ccCount = seiBuffer.readUnsignedByte() & 31;
                        parsableByteArray.skipBytes(1);
                        int sampleLength = ccCount * 3;
                        int sampleStartPosition = seiBuffer.getPosition();
                        for (TrackOutput output : trackOutputArr) {
                            parsableByteArray.setPosition(sampleStartPosition);
                            output.sampleData(parsableByteArray, sampleLength);
                            output.sampleMetadata(presentationTimeUs, 1, sampleLength, 0, null);
                        }
                        parsableByteArray.skipBytes(payloadSize - (10 + (ccCount * 3)));
                    } else {
                        parsableByteArray.skipBytes(payloadSize);
                    }
                }
            }
            Log.w(TAG, "Skipping remainder of malformed SEI NAL unit.");
            parsableByteArray.setPosition(seiBuffer.limit());
        }
    }

    private static int readNon255TerminatedValue(ParsableByteArray buffer) {
        int value = 0;
        while (buffer.bytesLeft() != 0) {
            int b = buffer.readUnsignedByte();
            value += b;
            if (b != 255) {
                return value;
            }
        }
        return -1;
    }

    private static boolean isSeiMessageCea608(int payloadType, int payloadLength, ParsableByteArray payload) {
        boolean z = false;
        if (payloadType == 4) {
            if (payloadLength >= 8) {
                int startPosition = payload.getPosition();
                int countryCode = payload.readUnsignedByte();
                int providerCode = payload.readUnsignedShort();
                int userIdentifier = payload.readInt();
                int userDataTypeCode = payload.readUnsignedByte();
                payload.setPosition(startPosition);
                if (countryCode == COUNTRY_CODE && providerCode == PROVIDER_CODE && userIdentifier == USER_ID && userDataTypeCode == 3) {
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
