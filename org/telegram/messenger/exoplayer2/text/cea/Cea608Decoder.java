package org.telegram.messenger.exoplayer2.text.cea;

import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import org.telegram.messenger.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.telegram.messenger.exoplayer2.text.SubtitleOutputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class Cea608Decoder extends CeaDecoder {
    private static final int[] BASIC_CHARACTER_SET = new int[]{32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632};
    private static final int CC_FIELD_FLAG = 1;
    private static final int CC_MODE_PAINT_ON = 3;
    private static final int CC_MODE_POP_ON = 2;
    private static final int CC_MODE_ROLL_UP = 1;
    private static final int CC_MODE_UNKNOWN = 0;
    private static final int CC_TYPE_FLAG = 2;
    private static final int CC_VALID_608_ID = 4;
    private static final int CC_VALID_FLAG = 4;
    private static final int COUNTRY_CODE = 181;
    private static final byte CTRL_BACKSPACE = (byte) 33;
    private static final byte CTRL_CARRIAGE_RETURN = (byte) 45;
    private static final byte CTRL_END_OF_CAPTION = (byte) 47;
    private static final byte CTRL_ERASE_DISPLAYED_MEMORY = (byte) 44;
    private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = (byte) 46;
    private static final byte CTRL_MISC_CHAN_1 = (byte) 20;
    private static final byte CTRL_MISC_CHAN_2 = (byte) 28;
    private static final byte CTRL_RESUME_CAPTION_LOADING = (byte) 32;
    private static final byte CTRL_RESUME_DIRECT_CAPTIONING = (byte) 41;
    private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = (byte) 37;
    private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = (byte) 38;
    private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = (byte) 39;
    private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
    private static final int NTSC_CC_FIELD_1 = 0;
    private static final int NTSC_CC_FIELD_2 = 1;
    private static final int PAYLOAD_TYPE_CC = 4;
    private static final int PROVIDER_CODE = 49;
    private static final int[] SPECIAL_CHARACTER_SET = new int[]{174, 176, PsExtractor.PRIVATE_STREAM_1, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251};
    private static final int[] SPECIAL_ES_FR_CHARACTER_SET = new int[]{193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, PsExtractor.AUDIO_STREAM, 194, 199, Callback.DEFAULT_DRAG_ANIMATION_DURATION, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187};
    private static final int[] SPECIAL_PT_DE_CHARACTER_SET = new int[]{195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496};
    private static final String TAG = "Cea608Decoder";
    private static final int USER_DATA_TYPE_CODE = 3;
    private static final int USER_ID = NUM;
    private int captionMode;
    private int captionRowCount;
    private String captionString;
    private final StringBuilder captionStringBuilder = new StringBuilder();
    private final ParsableByteArray ccData = new ParsableByteArray();
    private String lastCaptionString;
    private byte repeatableControlCc1;
    private byte repeatableControlCc2;
    private boolean repeatableControlSet;
    private final int selectedField;

    public /* bridge */ /* synthetic */ SubtitleInputBuffer dequeueInputBuffer() throws SubtitleDecoderException {
        return super.dequeueInputBuffer();
    }

    public /* bridge */ /* synthetic */ SubtitleOutputBuffer dequeueOutputBuffer() throws SubtitleDecoderException {
        return super.dequeueOutputBuffer();
    }

    public /* bridge */ /* synthetic */ void queueInputBuffer(SubtitleInputBuffer subtitleInputBuffer) throws SubtitleDecoderException {
        super.queueInputBuffer(subtitleInputBuffer);
    }

    public /* bridge */ /* synthetic */ void setPositionUs(long j) {
        super.setPositionUs(j);
    }

    public Cea608Decoder(int accessibilityChannel) {
        switch (accessibilityChannel) {
            case 3:
            case 4:
                this.selectedField = 2;
                break;
            default:
                this.selectedField = 1;
                break;
        }
        setCaptionMode(0);
        this.captionRowCount = 4;
    }

    public String getName() {
        return TAG;
    }

    public void flush() {
        super.flush();
        setCaptionMode(0);
        this.captionRowCount = 4;
        this.captionStringBuilder.setLength(0);
        this.captionString = null;
        this.lastCaptionString = null;
        this.repeatableControlSet = false;
        this.repeatableControlCc1 = (byte) 0;
        this.repeatableControlCc2 = (byte) 0;
    }

    public void release() {
    }

    protected boolean isNewSubtitleDataAvailable() {
        return !TextUtils.equals(this.captionString, this.lastCaptionString);
    }

    protected Subtitle createSubtitle() {
        this.lastCaptionString = this.captionString;
        return new CeaSubtitle(new Cue(this.captionString));
    }

    protected void decode(SubtitleInputBuffer inputBuffer) {
        this.ccData.reset(inputBuffer.data.array(), inputBuffer.data.limit());
        boolean captionDataProcessed = false;
        boolean isRepeatableControl = false;
        while (this.ccData.bytesLeft() > 0) {
            byte ccDataHeader = (byte) this.ccData.readUnsignedByte();
            byte ccData1 = (byte) (this.ccData.readUnsignedByte() & 127);
            byte ccData2 = (byte) (this.ccData.readUnsignedByte() & 127);
            if ((ccDataHeader & 6) == 4 && ((this.selectedField != 1 || (ccDataHeader & 1) == 0) && ((this.selectedField != 2 || (ccDataHeader & 1) == 1) && !(ccData1 == (byte) 0 && ccData2 == (byte) 0)))) {
                captionDataProcessed = true;
                if ((ccData1 == (byte) 17 || ccData1 == (byte) 25) && (ccData2 & 112) == 48) {
                    this.captionStringBuilder.append(getSpecialChar(ccData2));
                } else {
                    if ((ccData2 & 96) == 32) {
                        if (ccData1 == (byte) 18 || ccData1 == (byte) 26) {
                            backspace();
                            this.captionStringBuilder.append(getExtendedEsFrChar(ccData2));
                        } else if (ccData1 == (byte) 19 || ccData1 == (byte) 27) {
                            backspace();
                            this.captionStringBuilder.append(getExtendedPtDeChar(ccData2));
                        }
                    }
                    if (ccData1 < CTRL_RESUME_CAPTION_LOADING) {
                        isRepeatableControl = handleCtrl(ccData1, ccData2);
                    } else {
                        this.captionStringBuilder.append(getChar(ccData1));
                        if (ccData2 >= CTRL_RESUME_CAPTION_LOADING) {
                            this.captionStringBuilder.append(getChar(ccData2));
                        }
                    }
                }
            }
        }
        if (captionDataProcessed) {
            if (!isRepeatableControl) {
                this.repeatableControlSet = false;
            }
            if (this.captionMode == 1 || this.captionMode == 3) {
                this.captionString = getDisplayCaption();
            }
        }
    }

    private boolean handleCtrl(byte cc1, byte cc2) {
        boolean isRepeatableControl = isRepeatable(cc1);
        if (isRepeatableControl) {
            if (this.repeatableControlSet && this.repeatableControlCc1 == cc1 && this.repeatableControlCc2 == cc2) {
                this.repeatableControlSet = false;
                return true;
            }
            this.repeatableControlSet = true;
            this.repeatableControlCc1 = cc1;
            this.repeatableControlCc2 = cc2;
        }
        if (isMiscCode(cc1, cc2)) {
            handleMiscCode(cc2);
            return isRepeatableControl;
        } else if (!isPreambleAddressCode(cc1, cc2)) {
            return isRepeatableControl;
        } else {
            maybeAppendNewline();
            return isRepeatableControl;
        }
    }

    private void handleMiscCode(byte cc2) {
        switch (cc2) {
            case (byte) 32:
                setCaptionMode(2);
                return;
            case (byte) 37:
                this.captionRowCount = 2;
                setCaptionMode(1);
                return;
            case (byte) 38:
                this.captionRowCount = 3;
                setCaptionMode(1);
                return;
            case (byte) 39:
                this.captionRowCount = 4;
                setCaptionMode(1);
                return;
            case (byte) 41:
                setCaptionMode(3);
                return;
            default:
                if (this.captionMode != 0) {
                    switch (cc2) {
                        case (byte) 33:
                            if (this.captionStringBuilder.length() > 0) {
                                this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
                                return;
                            }
                            return;
                        case (byte) 44:
                            this.captionString = null;
                            if (this.captionMode == 1 || this.captionMode == 3) {
                                this.captionStringBuilder.setLength(0);
                                return;
                            }
                            return;
                        case MotionEventCompat.AXIS_GENERIC_14 /*45*/:
                            maybeAppendNewline();
                            return;
                        case (byte) 46:
                            this.captionStringBuilder.setLength(0);
                            return;
                        case MotionEventCompat.AXIS_GENERIC_16 /*47*/:
                            this.captionString = getDisplayCaption();
                            this.captionStringBuilder.setLength(0);
                            return;
                        default:
                            return;
                    }
                }
                return;
        }
    }

    private void backspace() {
        if (this.captionStringBuilder.length() > 0) {
            this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
        }
    }

    private void maybeAppendNewline() {
        int buildLength = this.captionStringBuilder.length();
        if (buildLength > 0 && this.captionStringBuilder.charAt(buildLength - 1) != '\n') {
            this.captionStringBuilder.append('\n');
        }
    }

    private String getDisplayCaption() {
        int buildLength = this.captionStringBuilder.length();
        if (buildLength == 0) {
            return null;
        }
        boolean endsWithNewline;
        if (this.captionStringBuilder.charAt(buildLength - 1) == '\n') {
            endsWithNewline = true;
        } else {
            endsWithNewline = false;
        }
        if (buildLength == 1 && endsWithNewline) {
            return null;
        }
        int endIndex;
        if (endsWithNewline) {
            endIndex = buildLength - 1;
        } else {
            endIndex = buildLength;
        }
        if (this.captionMode != 1) {
            return this.captionStringBuilder.substring(0, endIndex);
        }
        int startIndex = 0;
        int searchBackwardFromIndex = endIndex;
        for (int i = 0; i < this.captionRowCount && searchBackwardFromIndex != -1; i++) {
            searchBackwardFromIndex = this.captionStringBuilder.lastIndexOf("\n", searchBackwardFromIndex - 1);
        }
        if (searchBackwardFromIndex != -1) {
            startIndex = searchBackwardFromIndex + 1;
        }
        this.captionStringBuilder.delete(0, startIndex);
        return this.captionStringBuilder.substring(0, endIndex - startIndex);
    }

    private void setCaptionMode(int captionMode) {
        if (this.captionMode != captionMode) {
            this.captionMode = captionMode;
            this.captionStringBuilder.setLength(0);
            if (captionMode == 1 || captionMode == 0) {
                this.captionString = null;
            }
        }
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

    private static boolean isMiscCode(byte cc1, byte cc2) {
        return (cc1 == CTRL_MISC_CHAN_1 || cc1 == CTRL_MISC_CHAN_2) && cc2 >= CTRL_RESUME_CAPTION_LOADING && cc2 <= CTRL_END_OF_CAPTION;
    }

    private static boolean isPreambleAddressCode(byte cc1, byte cc2) {
        return cc1 >= (byte) 16 && cc1 <= (byte) 31 && cc2 >= (byte) 64 && cc2 <= Byte.MAX_VALUE;
    }

    private static boolean isRepeatable(byte cc1) {
        return cc1 >= (byte) 16 && cc1 <= (byte) 31;
    }

    public static boolean isSeiMessageCea608(int payloadType, int payloadLength, ParsableByteArray payload) {
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
