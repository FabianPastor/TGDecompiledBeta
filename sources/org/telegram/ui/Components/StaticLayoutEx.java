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
                    Class loadClass = classLoader.loadClass(TEXT_DIR_CLASS);
                    cls = classLoader.loadClass(TEXT_DIRS_CLASS);
                    sTextDirection = cls.getField(TEXT_DIR_FIRSTSTRONG_LTR).get(cls);
                    cls = loadClass;
                }
                Class[] clsArr = new Class[]{CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Alignment.class, cls, Float.TYPE, Float.TYPE, Boolean.TYPE, TruncateAt.class, Integer.TYPE, Integer.TYPE};
                sConstructor = StaticLayout.class.getDeclaredConstructor(clsArr);
                sConstructor.setAccessible(true);
                sConstructorArgs = new Object[clsArr.length];
                initialized = true;
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
        }
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i2, int i3) {
        return createStaticLayout(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z, truncateAt, i2, i3);
    }

    public static StaticLayout createStaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Alignment alignment, float f, float f2, boolean z, TruncateAt truncateAt, int i4, int i5) {
        CharSequence charSequence2 = charSequence;
        TextPaint textPaint2 = textPaint;
        int i6 = i4;
        int i7 = i5;
        if (i7 == 1) {
            try {
                CharSequence ellipsize = TextUtils.ellipsize(charSequence2, textPaint2, (float) i6, TruncateAt.END);
                return new StaticLayout(ellipsize, 0, ellipsize.length(), textPaint2, i3, alignment, f, f2, z);
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
            }
        }
        int i8;
        StaticLayout build;
        if (VERSION.SDK_INT >= 23) {
            i8 = i3;
            build = Builder.obtain(charSequence2, 0, charSequence.length(), textPaint2, i8).setAlignment(alignment).setLineSpacing(f2, f).setIncludePad(z).setEllipsize(null).setEllipsizedWidth(i6).setBreakStrategy(1).setHyphenationFrequency(1).build();
        } else {
            i8 = i3;
            float f3 = f2;
            boolean z2 = z;
            build = new StaticLayout(charSequence2, textPaint2, i8, alignment, f, f2, z);
        }
        if (build.getLineCount() <= i7) {
            return build;
        }
        int i9 = i7 - 1;
        float lineLeft = build.getLineLeft(i9);
        if (lineLeft != 0.0f) {
            i6 = build.getOffsetForHorizontal(i9, lineLeft);
        } else {
            i6 = build.getOffsetForHorizontal(i9, build.getLineWidth(i9));
        }
        ellipsize = new SpannableStringBuilder(charSequence2.subSequence(0, Math.max(0, i6 - 1)));
        ellipsize.append("\u2026");
        return new StaticLayout(ellipsize, textPaint2, i8, alignment, f, f2, z);
    }
}
