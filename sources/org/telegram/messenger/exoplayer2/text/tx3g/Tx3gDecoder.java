package org.telegram.messenger.exoplayer2.text.tx3g;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import java.nio.charset.Charset;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Tx3gDecoder extends SimpleSubtitleDecoder {
    private static final char BOM_UTF16_BE = '\ufeff';
    private static final char BOM_UTF16_LE = '\ufffe';
    private static final int DEFAULT_COLOR = -1;
    private static final int DEFAULT_FONT_FACE = 0;
    private static final String DEFAULT_FONT_FAMILY = "sans-serif";
    private static final float DEFAULT_VERTICAL_PLACEMENT = 0.85f;
    private static final int FONT_FACE_BOLD = 1;
    private static final int FONT_FACE_ITALIC = 2;
    private static final int FONT_FACE_UNDERLINE = 4;
    private static final int SIZE_ATOM_HEADER = 8;
    private static final int SIZE_BOM_UTF16 = 2;
    private static final int SIZE_SHORT = 2;
    private static final int SIZE_STYLE_RECORD = 12;
    private static final int SPAN_PRIORITY_HIGH = 0;
    private static final int SPAN_PRIORITY_LOW = 16711680;
    private static final String TX3G_SERIF = "Serif";
    private static final int TYPE_STYL = Util.getIntegerCodeForString("styl");
    private static final int TYPE_TBOX = Util.getIntegerCodeForString("tbox");
    private int calculatedVideoTrackHeight;
    private boolean customVerticalPlacement;
    private int defaultColorRgba;
    private int defaultFontFace;
    private String defaultFontFamily;
    private float defaultVerticalPlacement;
    private final ParsableByteArray parsableByteArray = new ParsableByteArray();

    public Tx3gDecoder(List<byte[]> list) {
        super("Tx3gDecoder");
        decodeInitializationData(list);
    }

    private void decodeInitializationData(List<byte[]> list) {
        boolean z = false;
        if (list != null && list.size() == 1 && (((byte[]) list.get(0)).length == 48 || ((byte[]) list.get(0)).length == 53)) {
            byte[] bArr = (byte[]) list.get(0);
            this.defaultFontFace = bArr[24];
            this.defaultColorRgba = ((((bArr[26] & 255) << 24) | ((bArr[27] & 255) << 16)) | ((bArr[28] & 255) << 8)) | (bArr[29] & 255);
            this.defaultFontFamily = TX3G_SERIF.equals(new String(bArr, 43, bArr.length - 43)) ? C0542C.SERIF_NAME : "sans-serif";
            this.calculatedVideoTrackHeight = 20 * bArr[25];
            if ((bArr[0] & 32) != 0) {
                z = true;
            }
            this.customVerticalPlacement = z;
            if (this.customVerticalPlacement) {
                this.defaultVerticalPlacement = ((float) ((bArr[11] & 255) | ((bArr[10] & 255) << 8))) / ((float) this.calculatedVideoTrackHeight);
                this.defaultVerticalPlacement = Util.constrainValue(this.defaultVerticalPlacement, 0.0f, 0.95f);
                return;
            }
            this.defaultVerticalPlacement = DEFAULT_VERTICAL_PLACEMENT;
            return;
        }
        this.defaultFontFace = 0;
        this.defaultColorRgba = -1;
        this.defaultFontFamily = "sans-serif";
        this.customVerticalPlacement = false;
        this.defaultVerticalPlacement = DEFAULT_VERTICAL_PLACEMENT;
    }

    protected Subtitle decode(byte[] bArr, int i, boolean z) throws SubtitleDecoderException {
        this.parsableByteArray.reset(bArr, i);
        bArr = readSubtitleText(this.parsableByteArray);
        if (bArr.isEmpty() != 0) {
            return Tx3gSubtitle.EMPTY;
        }
        i = new SpannableStringBuilder(bArr);
        SpannableStringBuilder spannableStringBuilder = i;
        attachFontFace(spannableStringBuilder, this.defaultFontFace, 0, 0, i.length(), SPAN_PRIORITY_LOW);
        attachColor(spannableStringBuilder, this.defaultColorRgba, -1, 0, i.length(), SPAN_PRIORITY_LOW);
        attachFontFamily(spannableStringBuilder, this.defaultFontFamily, "sans-serif", 0, i.length(), SPAN_PRIORITY_LOW);
        float f = this.defaultVerticalPlacement;
        while (this.parsableByteArray.bytesLeft() >= true) {
            bArr = this.parsableByteArray.getPosition();
            z = this.parsableByteArray.readInt();
            int readInt = this.parsableByteArray.readInt();
            boolean z2 = false;
            boolean z3 = true;
            if (readInt == TYPE_STYL) {
                if (this.parsableByteArray.bytesLeft() < 2) {
                    z3 = false;
                }
                assertTrue(z3);
                readInt = this.parsableByteArray.readUnsignedShort();
                int i2;
                while (i2 < readInt) {
                    applyStyleRecord(this.parsableByteArray, i);
                    i2++;
                }
            } else if (readInt == TYPE_TBOX && this.customVerticalPlacement) {
                if (this.parsableByteArray.bytesLeft() >= 2) {
                    z2 = true;
                }
                assertTrue(z2);
                f = Util.constrainValue(((float) this.parsableByteArray.readUnsignedShort()) / ((float) this.calculatedVideoTrackHeight), 0.0f, 0.95f);
            }
            this.parsableByteArray.setPosition(bArr + z);
        }
        return new Tx3gSubtitle(new Cue(i, null, f, 0, 0, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE));
    }

    private static String readSubtitleText(ParsableByteArray parsableByteArray) throws SubtitleDecoderException {
        assertTrue(parsableByteArray.bytesLeft() >= 2);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        if (readUnsignedShort == 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (parsableByteArray.bytesLeft() >= 2) {
            char peekChar = parsableByteArray.peekChar();
            if (peekChar == BOM_UTF16_BE || peekChar == BOM_UTF16_LE) {
                return parsableByteArray.readString(readUnsignedShort, Charset.forName(C0542C.UTF16_NAME));
            }
        }
        return parsableByteArray.readString(readUnsignedShort, Charset.forName(C0542C.UTF8_NAME));
    }

    private void applyStyleRecord(ParsableByteArray parsableByteArray, SpannableStringBuilder spannableStringBuilder) throws SubtitleDecoderException {
        assertTrue(parsableByteArray.bytesLeft() >= 12);
        int readUnsignedShort = parsableByteArray.readUnsignedShort();
        int readUnsignedShort2 = parsableByteArray.readUnsignedShort();
        parsableByteArray.skipBytes(2);
        int readUnsignedByte = parsableByteArray.readUnsignedByte();
        parsableByteArray.skipBytes(1);
        parsableByteArray = parsableByteArray.readInt();
        SpannableStringBuilder spannableStringBuilder2 = spannableStringBuilder;
        int i = readUnsignedShort;
        int i2 = readUnsignedShort2;
        attachFontFace(spannableStringBuilder2, readUnsignedByte, this.defaultFontFace, i, i2, 0);
        attachColor(spannableStringBuilder2, parsableByteArray, this.defaultColorRgba, i, i2, 0);
    }

    private static void attachFontFace(SpannableStringBuilder spannableStringBuilder, int i, int i2, int i3, int i4, int i5) {
        if (i != i2) {
            i2 = 33 | i5;
            int i6 = 1;
            i5 = (i & 1) != 0 ? 1 : 0;
            int i7 = (i & 2) != 0 ? 1 : 0;
            if (i5 != 0) {
                if (i7 != 0) {
                    spannableStringBuilder.setSpan(new StyleSpan(3), i3, i4, i2);
                } else {
                    spannableStringBuilder.setSpan(new StyleSpan(1), i3, i4, i2);
                }
            } else if (i7 != 0) {
                spannableStringBuilder.setSpan(new StyleSpan(2), i3, i4, i2);
            }
            if ((i & 4) == 0) {
                i6 = 0;
            }
            if (i6 != 0) {
                spannableStringBuilder.setSpan(new UnderlineSpan(), i3, i4, i2);
            }
            if (i6 == 0 && i5 == 0 && i7 == 0) {
                spannableStringBuilder.setSpan(new StyleSpan(0), i3, i4, i2);
            }
        }
    }

    private static void attachColor(SpannableStringBuilder spannableStringBuilder, int i, int i2, int i3, int i4, int i5) {
        if (i != i2) {
            spannableStringBuilder.setSpan(new ForegroundColorSpan((i >>> 8) | ((i & 255) << 24)), i3, i4, 33 | i5);
        }
    }

    private static void attachFontFamily(SpannableStringBuilder spannableStringBuilder, String str, String str2, int i, int i2, int i3) {
        if (str != str2) {
            spannableStringBuilder.setSpan(new TypefaceSpan(str), i, i2, 33 | i3);
        }
    }

    private static void assertTrue(boolean z) throws SubtitleDecoderException {
        if (!z) {
            throw new SubtitleDecoderException("Unexpected subtitle format.");
        }
    }
}
