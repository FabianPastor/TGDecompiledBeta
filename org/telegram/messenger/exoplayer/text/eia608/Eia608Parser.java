package org.telegram.messenger.exoplayer.text.eia608;

import java.util.ArrayList;
import org.telegram.messenger.exoplayer.SampleHolder;
import org.telegram.messenger.exoplayer.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer.util.MimeTypes;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class Eia608Parser {
    private static final int[] BASIC_CHARACTER_SET = new int[]{32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632};
    private static final int COUNTRY_CODE = 181;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE = 49;
    private static final int[] SPECIAL_CHARACTER_SET = new int[]{174, 176, PsExtractor.PRIVATE_STREAM_1, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251};
    private static final int[] SPECIAL_ES_FR_CHARACTER_SET = new int[]{193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, PsExtractor.AUDIO_STREAM, 194, 199, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187};
    private static final int[] SPECIAL_PT_DE_CHARACTER_SET = new int[]{195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496};
    private static final int USER_DATA_TYPE_CODE = 3;
    private static final int USER_ID = NUM;
    private final ArrayList<ClosedCaption> captions = new ArrayList();
    private final ParsableBitArray seiBuffer = new ParsableBitArray();
    private final StringBuilder stringBuilder = new StringBuilder();

    Eia608Parser() {
    }

    boolean canParse(String mimeType) {
        return mimeType.equals(MimeTypes.APPLICATION_EIA608);
    }

    ClosedCaptionList parse(SampleHolder sampleHolder) {
        if (sampleHolder.size < 10) {
            return null;
        }
        this.captions.clear();
        this.stringBuilder.setLength(0);
        this.seiBuffer.reset(sampleHolder.data.array());
        this.seiBuffer.skipBits(67);
        int ccCount = this.seiBuffer.readBits(5);
        this.seiBuffer.skipBits(8);
        for (int i = 0; i < ccCount; i++) {
            this.seiBuffer.skipBits(5);
            if (!this.seiBuffer.readBit()) {
                this.seiBuffer.skipBits(18);
            } else if (this.seiBuffer.readBits(2) != 0) {
                this.seiBuffer.skipBits(16);
            } else {
                this.seiBuffer.skipBits(1);
                byte ccData1 = (byte) this.seiBuffer.readBits(7);
                this.seiBuffer.skipBits(1);
                byte ccData2 = (byte) this.seiBuffer.readBits(7);
                if (ccData1 != (byte) 0 || ccData2 != (byte) 0) {
                    if ((ccData1 == ClosedCaptionCtrl.MID_ROW_CHAN_1 || ccData1 == ClosedCaptionCtrl.MID_ROW_CHAN_2) && (ccData2 & 112) == 48) {
                        this.stringBuilder.append(getSpecialChar(ccData2));
                    } else if ((ccData1 == (byte) 18 || ccData1 == (byte) 26) && (ccData2 & 96) == 32) {
                        backspace();
                        this.stringBuilder.append(getExtendedEsFrChar(ccData2));
                    } else if ((ccData1 == (byte) 19 || ccData1 == (byte) 27) && (ccData2 & 96) == 32) {
                        backspace();
                        this.stringBuilder.append(getExtendedPtDeChar(ccData2));
                    } else if (ccData1 < ClosedCaptionCtrl.RESUME_CAPTION_LOADING) {
                        addCtrl(ccData1, ccData2);
                    } else {
                        this.stringBuilder.append(getChar(ccData1));
                        if (ccData2 >= ClosedCaptionCtrl.RESUME_CAPTION_LOADING) {
                            this.stringBuilder.append(getChar(ccData2));
                        }
                    }
                }
            }
        }
        addBufferedText();
        if (this.captions.isEmpty()) {
            return null;
        }
        ClosedCaption[] captionArray = new ClosedCaption[this.captions.size()];
        this.captions.toArray(captionArray);
        return new ClosedCaptionList(sampleHolder.timeUs, sampleHolder.isDecodeOnly(), captionArray);
    }

    private static char getChar(byte ccData) {
        return (char) BASIC_CHARACTER_SET[(ccData & 127) - 32];
    }

    private static char getSpecialChar(byte ccData) {
        return (char) SPECIAL_CHARACTER_SET[ccData & 15];
    }

    private static char getExtendedEsFrChar(byte ccData) {
        return (char) SPECIAL_ES_FR_CHARACTER_SET[ccData & 31];
    }

    private static char getExtendedPtDeChar(byte ccData) {
        return (char) SPECIAL_PT_DE_CHARACTER_SET[ccData & 31];
    }

    private void addBufferedText() {
        if (this.stringBuilder.length() > 0) {
            this.captions.add(new ClosedCaptionText(this.stringBuilder.toString()));
            this.stringBuilder.setLength(0);
        }
    }

    private void addCtrl(byte ccData1, byte ccData2) {
        addBufferedText();
        this.captions.add(new ClosedCaptionCtrl(ccData1, ccData2));
    }

    private void backspace() {
        addCtrl(ClosedCaptionCtrl.MISC_CHAN_1, ClosedCaptionCtrl.BACKSPACE);
    }

    public static boolean isSeiMessageEia608(int payloadType, int payloadLength, ParsableByteArray payload) {
        if (payloadType != 4 || payloadLength < 8) {
            return false;
        }
        int startPosition = payload.getPosition();
        int countryCode = payload.readUnsignedByte();
        int providerCode = payload.readUnsignedShort();
        int userIdentifier = payload.readInt();
        int userDataTypeCode = payload.readUnsignedByte();
        payload.setPosition(startPosition);
        if (countryCode == COUNTRY_CODE && providerCode == 49 && userIdentifier == USER_ID && userDataTypeCode == 3) {
            return true;
        }
        return false;
    }
}
