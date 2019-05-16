package org.telegram.ui.Components;

import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import java.lang.reflect.Constructor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class StaticLayoutEx {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
    private static boolean initialized;
    private static Constructor<StaticLayout> sConstructor;
    private static Object[] sConstructorArgs;
    private static Object sTextDirection;

    public static void init() {
        if (!initialized) {
            try {
                Class cls;
                if (VERSION.SDK_INT >= 18) {
                    cls = TextDirectionHeuristic.class;
                    sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                } else {
                    ClassLoader classLoader = StaticLayoutEx.class.getClassLoader();
                    Class loadClass = classLoader.loadClass("android.text.TextDirectionHeuristic");
                    cls = classLoader.loadClass("android.text.TextDirectionHeuristics");
                    sTextDirection = cls.getField("FIRSTSTRONG_LTR").get(cls);
                    cls = loadClass;
                }
                Class[] clsArr = new Class[]{CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Alignment.class, cls, Float.TYPE, Float.TYPE, Boolean.TYPE, TruncateAt.class, Integer.TYPE, Integer.TYPE};
                sConstructor = StaticLayout.class.getDeclaredConstructor(clsArr);
                sConstructor.setAccessible(true);
                sConstructorArgs = new Object[clsArr.length];
                initialized = true;
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    public static StaticLayout createStaticLayout2(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i2, int i3) {
        int i4 = i2;
        CharSequence charSequence2;
        if (VERSION.SDK_INT >= 23) {
            charSequence2 = charSequence;
            TextPaint textPaint2 = textPaint;
            return Builder.obtain(charSequence, 0, charSequence.length(), textPaint, i4).setAlignment(alignment).setLineSpacing(f2, f).setIncludePad(z).setEllipsize(TruncateAt.END).setEllipsizedWidth(i4).setMaxLines(i3).setBreakStrategy(1).setHyphenationFrequency(0).build();
        }
        charSequence2 = charSequence;
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, true);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i2, int i3) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, true);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i2, int i3, boolean z2) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3, z2);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5, boolean z2) {
        Throwable e;
        StaticLayout staticLayout;
        CharSequence charSequence2 = charSequence;
        TextPaint textPaint2 = textPaint;
        int i6 = i3;
        Alignment alignment2 = alignment;
        float f3 = f;
        float f4 = f2;
        boolean z3 = z;
        int i7 = i4;
        int i8 = i5;
        if (i8 == 1) {
            CharSequence ellipsize;
            try {
                ellipsize = TextUtils.ellipsize(charSequence2, textPaint2, (float) i7, TruncateAt.END);
                StaticLayout staticLayout2 = staticLayout2;
            } catch (Exception e2) {
                e = e2;
                staticLayout = null;
                FileLog.e(e);
                return staticLayout;
            }
            try {
                return new StaticLayout(ellipsize, 0, ellipsize.length(), textPaint, i3, alignment, f, f2, z);
            } catch (Exception e3) {
                e = e3;
                staticLayout = null;
                FileLog.e(e);
                return staticLayout;
            }
        }
        StaticLayout build;
        int i9;
        if (VERSION.SDK_INT >= 23) {
            build = Builder.obtain(charSequence2, 0, charSequence.length(), textPaint2, i6).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z3).setEllipsize(null).setEllipsizedWidth(i7).setMaxLines(i8).setBreakStrategy(1).setHyphenationFrequency(0).build();
            TruncateAt truncateAt2 = null;
            i9 = i8;
        } else {
            build = build;
            staticLayout = null;
            i9 = i8;
            try {
                build = new StaticLayout(charSequence, textPaint, i3, alignment, f, f2, z);
            } catch (Exception e4) {
                e = e4;
                FileLog.e(e);
                return staticLayout;
            }
        }
        if (build.getLineCount() <= i9) {
            return build;
        }
        int offsetForHorizontal;
        int i10 = i9 - 1;
        float lineLeft = build.getLineLeft(i10);
        float lineWidth = build.getLineWidth(i10);
        if (lineLeft != 0.0f) {
            offsetForHorizontal = build.getOffsetForHorizontal(i10, lineLeft);
        } else {
            offsetForHorizontal = build.getOffsetForHorizontal(i10, lineWidth);
        }
        if (lineWidth < ((float) (i7 - AndroidUtilities.dp(10.0f)))) {
            offsetForHorizontal += 3;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence2.subSequence(0, Math.max(0, offsetForHorizontal - 3)));
        spannableStringBuilder.append("…");
        if (VERSION.SDK_INT >= 23) {
            return Builder.obtain(spannableStringBuilder, 0, spannableStringBuilder.length(), textPaint2, i6).setAlignment(alignment2).setLineSpacing(f4, f3).setIncludePad(z).setEllipsize(TruncateAt.END).setEllipsizedWidth(i7).setMaxLines(i9).setBreakStrategy(z2 ? 1 : 0).setHyphenationFrequency(0).build();
        }
        boolean z4 = z;
        return new StaticLayout(spannableStringBuilder, textPaint, i3, alignment, f, f2, z);
    }
}
