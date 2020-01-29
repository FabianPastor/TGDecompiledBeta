package org.telegram.ui.Components;

import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import java.lang.reflect.Constructor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class StaticLayoutEx {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
    public static Layout.Alignment[] alignments = Layout.Alignment.values();
    private static boolean initialized;
    private static Constructor<StaticLayout> sConstructor;
    private static Object[] sConstructorArgs;
    private static Object sTextDirection;

    public static Layout.Alignment ALIGN_RIGHT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[4] : Layout.Alignment.ALIGN_OPPOSITE;
    }

    public static Layout.Alignment ALIGN_LEFT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[3] : Layout.Alignment.ALIGN_NORMAL;
    }

    public static void init() {
        Class cls;
        if (!initialized) {
            try {
                if (Build.VERSION.SDK_INT >= 18) {
                    cls = TextDirectionHeuristic.class;
                    sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                } else {
                    ClassLoader classLoader = StaticLayoutEx.class.getClassLoader();
                    Class<?> loadClass = classLoader.loadClass("android.text.TextDirectionHeuristic");
                    Class<?> loadClass2 = classLoader.loadClass("android.text.TextDirectionHeuristics");
                    sTextDirection = loadClass2.getField("FIRSTSTRONG_LTR").get(loadClass2);
                    cls = loadClass;
                }
                Class[] clsArr = {CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Layout.Alignment.class, cls, Float.TYPE, Float.TYPE, Boolean.TYPE, TextUtils.TruncateAt.class, Integer.TYPE, Integer.TYPE};
                sConstructor = StaticLayout.class.getDeclaredConstructor(clsArr);
                sConstructor.setAccessible(true);
                sConstructorArgs = new Object[clsArr.length];
                initialized = true;
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
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
        StaticLayout staticLayout2;
        int i7;
        CharSequence ellipsize;
        StaticLayout staticLayout3;
        CharSequence charSequence2 = charSequence;
        TextPaint textPaint2 = textPaint;
        int i8 = i3;
        Layout.Alignment alignment2 = alignment;
        float f3 = f;
        float f4 = f2;
        boolean z3 = z;
        int i9 = i4;
        int i10 = i5;
        if (i10 == 1) {
            try {
                ellipsize = TextUtils.ellipsize(charSequence2, textPaint2, (float) i9, TextUtils.TruncateAt.END);
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
            if (Build.VERSION.SDK_INT >= 23) {
                staticLayout2 = StaticLayout.Builder.obtain(charSequence2, 0, charSequence.length(), textPaint2, i8).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z3).setEllipsize((TextUtils.TruncateAt) null).setEllipsizedWidth(i9).setMaxLines(i10).setBreakStrategy(1).setHyphenationFrequency(0).build();
                i6 = i10;
            } else {
                staticLayout = null;
                i6 = i10;
                try {
                    StaticLayout staticLayout4 = new StaticLayout(charSequence, textPaint, i3, alignment, f, f2, z);
                    staticLayout2 = staticLayout4;
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e((Throwable) e);
                    return staticLayout;
                }
            }
            if (staticLayout2.getLineCount() <= i6) {
                return staticLayout2;
            }
            int i11 = i6 - 1;
            float lineLeft = staticLayout2.getLineLeft(i11);
            float lineWidth = staticLayout2.getLineWidth(i11);
            if (lineLeft != 0.0f) {
                i7 = staticLayout2.getOffsetForHorizontal(i11, lineLeft);
            } else {
                i7 = staticLayout2.getOffsetForHorizontal(i11, lineWidth);
            }
            if (lineWidth < ((float) (i9 - AndroidUtilities.dp(10.0f)))) {
                i7 += 3;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence2.subSequence(0, Math.max(0, i7 - 3)));
            spannableStringBuilder.append("…");
            if (Build.VERSION.SDK_INT >= 23) {
                return StaticLayout.Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), textPaint2, i8).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(i9).setMaxLines(i6).setBreakStrategy(z2 ? 1 : 0).setHyphenationFrequency(0).build();
            }
            boolean z4 = z;
            return new StaticLayout(spannableStringBuilder, textPaint, i3, alignment, f, f2, z);
        }
    }
}
