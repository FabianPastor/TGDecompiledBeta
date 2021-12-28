package org.telegram.ui.Components;

import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class StaticLayoutEx {
    public static Layout.Alignment[] alignments = Layout.Alignment.values();

    public static Layout.Alignment ALIGN_RIGHT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[4] : Layout.Alignment.ALIGN_OPPOSITE;
    }

    public static Layout.Alignment ALIGN_LEFT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[3] : Layout.Alignment.ALIGN_NORMAL;
    }

    public static StaticLayout createStaticLayout2(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i2, int i3) {
        int i4 = i2;
        if (Build.VERSION.SDK_INT >= 23) {
            CharSequence charSequence2 = charSequence;
            TextPaint textPaint2 = textPaint;
            return StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), textPaint, i4).setAlignment(alignment).setLineSpacing(f2, f).setIncludePad(z).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(i4).setMaxLines(i3).setBreakStrategy(1).setHyphenationFrequency(0).build();
        }
        CharSequence charSequence3 = charSequence;
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, true);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i2, int i3) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, true);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i2, int i3, boolean z2) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, z2);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4, int i5, boolean z2) {
        StaticLayout staticLayout;
        int i6;
        int i7;
        StaticLayout staticLayout2;
        int i8;
        CharSequence ellipsize;
        StaticLayout staticLayout3;
        CharSequence charSequence2 = charSequence;
        TextPaint textPaint2 = textPaint;
        int i9 = i3;
        Layout.Alignment alignment2 = alignment;
        float f3 = f;
        float f4 = f2;
        boolean z3 = z;
        int i10 = i4;
        int i11 = i5;
        if (i11 == 1) {
            try {
                ellipsize = TextUtils.ellipsize(charSequence2, textPaint2, (float) i10, TextUtils.TruncateAt.END);
            } catch (Exception e) {
                e = e;
                staticLayout = null;
                FileLog.e((Throwable) e);
                return staticLayout;
            }
            try {
                staticLayout3 = new StaticLayout(ellipsize, 0, ellipsize.length(), textPaint, i3, alignment, f, f2, z);
                return staticLayout3;
            } catch (Exception e2) {
                e = e2;
                staticLayout = null;
                FileLog.e((Throwable) e);
                return staticLayout;
            }
        } else {
            int i12 = Build.VERSION.SDK_INT;
            if (i12 >= 23) {
                staticLayout2 = StaticLayout.Builder.obtain(charSequence2, 0, charSequence.length(), textPaint2, i9).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z3).setEllipsize((TextUtils.TruncateAt) null).setEllipsizedWidth(i10).setMaxLines(i11).setBreakStrategy(1).setHyphenationFrequency(0).build();
                i6 = i12;
                i7 = i11;
            } else {
                i6 = i12;
                staticLayout = null;
                i7 = i11;
                try {
                    StaticLayout staticLayout4 = new StaticLayout(charSequence, textPaint, i3, alignment, f, f2, z);
                    staticLayout2 = staticLayout4;
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e((Throwable) e);
                    return staticLayout;
                }
            }
            if (staticLayout2.getLineCount() <= i7) {
                return staticLayout2;
            }
            int i13 = i7 - 1;
            float lineLeft = staticLayout2.getLineLeft(i13);
            float lineWidth = staticLayout2.getLineWidth(i13);
            if (lineLeft != 0.0f) {
                i8 = staticLayout2.getOffsetForHorizontal(i13, lineLeft);
            } else {
                i8 = staticLayout2.getOffsetForHorizontal(i13, lineWidth);
            }
            if (lineWidth < ((float) (i10 - AndroidUtilities.dp(10.0f)))) {
                i8 += 3;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence2.subSequence(0, Math.max(0, i8 - 3)));
            spannableStringBuilder.append("…");
            if (i6 >= 23) {
                return StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), textPaint2, i9).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z).setEllipsizedWidth(i10).setMaxLines(i7).setBreakStrategy(z2 ? 1 : 0).setHyphenationFrequency(0).build();
            }
            boolean z4 = z;
            return new StaticLayout(spannableStringBuilder, textPaint, i3, alignment, f, f2, z);
        }
    }
}
