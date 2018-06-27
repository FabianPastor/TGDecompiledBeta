package org.telegram.messenger.exoplayer2.text.cea;

import android.util.Log;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CeaUtil {
    private static final int COUNTRY_CODE = 181;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE_ATSC = 49;
    private static final int PROVIDER_CODE_DIRECTV = 47;
    private static final String TAG = "CeaUtil";
    private static final int USER_DATA_TYPE_CODE = 3;
    private static final int USER_ID_DTG1 = Util.getIntegerCodeForString("DTG1");
    private static final int USER_ID_GA94 = Util.getIntegerCodeForString("GA94");

    public static void consume(long presentationTimeUs, ParsableByteArray seiBuffer, TrackOutput[] outputs) {
        while (seiBuffer.bytesLeft() > 1) {
            int payloadType = readNon255TerminatedValue(seiBuffer);
            int payloadSize = readNon255TerminatedValue(seiBuffer);
            int nextPayloadPosition = seiBuffer.getPosition() + payloadSize;
            if (payloadSize == -1 || payloadSize > seiBuffer.bytesLeft()) {
                Log.w(TAG, "Skipping remainder of malformed SEI NAL unit.");
                nextPayloadPosition = seiBuffer.limit();
            } else if (payloadType == 4 && payloadSize >= 8) {
                int countryCode = seiBuffer.readUnsignedByte();
                int providerCode = seiBuffer.readUnsignedShort();
                int userIdentifier = 0;
                if (providerCode == PROVIDER_CODE_ATSC) {
                    userIdentifier = seiBuffer.readInt();
                }
                int userDataTypeCode = seiBuffer.readUnsignedByte();
                if (providerCode == PROVIDER_CODE_DIRECTV) {
                    seiBuffer.skipBytes(1);
                }
                boolean messageIsSupportedCeaCaption = countryCode == COUNTRY_CODE && ((providerCode == PROVIDER_CODE_ATSC || providerCode == PROVIDER_CODE_DIRECTV) && userDataTypeCode == 3);
                if (providerCode == PROVIDER_CODE_ATSC) {
                    int i = (userIdentifier == USER_ID_GA94 || userIdentifier == USER_ID_DTG1) ? 1 : 0;
                    messageIsSupportedCeaCaption &= i;
                }
                if (messageIsSupportedCeaCaption) {
                    int ccCount = seiBuffer.readUnsignedByte() & 31;
                    seiBuffer.skipBytes(1);
                    int sampleLength = ccCount * 3;
                    int sampleStartPosition = seiBuffer.getPosition();
                    for (TrackOutput output : outputs) {
                        seiBuffer.setPosition(sampleStartPosition);
                        output.sampleData(seiBuffer, sampleLength);
                        output.sampleMetadata(presentationTimeUs, 1, sampleLength, 0, null);
                    }
                }
            }
            seiBuffer.setPosition(nextPayloadPosition);
        }
    }

    private static int readNon255TerminatedValue(ParsableByteArray buffer) {
        int value = 0;
        while (buffer.bytesLeft() != 0) {
            int b = buffer.readUnsignedByte();
            value += b;
            if (b != 255) {
                int i = value;
                return value;
            }
        }
        return -1;
    }

    private CeaUtil() {
    }
}
