package org.telegram.ui.Components;

import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
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
                Class<?> textDirClass;
                if (VERSION.SDK_INT >= 18) {
                    textDirClass = TextDirectionHeuristic.class;
                    sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                } else {
                    ClassLoader loader = StaticLayoutEx.class.getClassLoader();
                    Class<?> textDirClass2 = loader.loadClass(TEXT_DIR_CLASS);
                    Class<?> textDirsClass = loader.loadClass(TEXT_DIRS_CLASS);
                    sTextDirection = textDirsClass.getField(TEXT_DIR_FIRSTSTRONG_LTR).get(textDirsClass);
                    textDirClass = textDirClass2;
                }
                Class<?>[] signature = new Class[]{CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Alignment.class, textDirClass, Float.TYPE, Float.TYPE, Boolean.TYPE, TruncateAt.class, Integer.TYPE, Integer.TYPE};
                sConstructor = StaticLayout.class.getDeclaredConstructor(signature);
                sConstructor.setAccessible(true);
                sConstructorArgs = new Object[signature.length];
                initialized = true;
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    public static StaticLayout createStaticLayout(CharSequence source, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad, TruncateAt ellipsize, int ellipsisWidth, int maxLines) {
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines);
    }

    public static android.text.StaticLayout createStaticLayout(java.lang.CharSequence r22, int r23, int r24, android.text.TextPaint r25, int r26, android.text.Layout.Alignment r27, float r28, float r29, boolean r30, android.text.TextUtils.TruncateAt r31, int r32, int r33) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r16_2 'layout' android.text.StaticLayout) in PHI: PHI: (r16_3 'layout' android.text.StaticLayout) = (r16_1 'layout' android.text.StaticLayout), (r16_2 'layout' android.text.StaticLayout) binds: {(r16_1 'layout' android.text.StaticLayout)=B:30:0x0075, (r16_2 'layout' android.text.StaticLayout)=B:44:?}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r9 = r22;
        r8 = r25;
        r7 = r32;
        r6 = r33;
        r5 = 0;
        r1 = 1;
        if (r6 != r1) goto L_0x003c;
    L_0x000c:
        r1 = (float) r7;
        r2 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x002b }
        r1 = android.text.TextUtils.ellipsize(r9, r8, r1, r2);	 Catch:{ Exception -> 0x002b }
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x002b }
        r12 = 0;	 Catch:{ Exception -> 0x002b }
        r13 = r1.length();	 Catch:{ Exception -> 0x002b }
        r10 = r2;	 Catch:{ Exception -> 0x002b }
        r11 = r1;	 Catch:{ Exception -> 0x002b }
        r14 = r8;	 Catch:{ Exception -> 0x002b }
        r15 = r26;	 Catch:{ Exception -> 0x002b }
        r16 = r27;	 Catch:{ Exception -> 0x002b }
        r17 = r28;	 Catch:{ Exception -> 0x002b }
        r18 = r29;	 Catch:{ Exception -> 0x002b }
        r19 = r30;	 Catch:{ Exception -> 0x002b }
        r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x002b }
        return r2;
    L_0x002b:
        r0 = move-exception;
        r11 = r26;
    L_0x002e:
        r12 = r27;
    L_0x0030:
        r13 = r28;
        r14 = r29;
    L_0x0034:
        r15 = r30;
    L_0x0036:
        r1 = r0;
        r17 = r5;
        r10 = r6;
        goto L_0x0109;
    L_0x003c:
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x00fa }
        r3 = 23;
        r10 = 0;
        if (r2 < r3) goto L_0x0085;
    L_0x0043:
        r2 = r22.length();	 Catch:{ Exception -> 0x002b }
        r11 = r26;
        r2 = android.text.StaticLayout.Builder.obtain(r9, r10, r2, r8, r11);	 Catch:{ Exception -> 0x0083 }
        r12 = r27;
        r2 = r2.setAlignment(r12);	 Catch:{ Exception -> 0x0081 }
        r13 = r28;
        r14 = r29;
        r2 = r2.setLineSpacing(r14, r13);	 Catch:{ Exception -> 0x007f }
        r15 = r30;
        r2 = r2.setIncludePad(r15);	 Catch:{ Exception -> 0x007d }
        r2 = r2.setEllipsize(r5);	 Catch:{ Exception -> 0x007d }
        r2 = r2.setEllipsizedWidth(r7);	 Catch:{ Exception -> 0x007d }
        r2 = r2.setBreakStrategy(r1);	 Catch:{ Exception -> 0x007d }
        r1 = r2.setHyphenationFrequency(r1);	 Catch:{ Exception -> 0x007d }
        r2 = r1.build();	 Catch:{ Exception -> 0x007d }
        r1 = r2;
        r16 = r1;
        r17 = r5;
        r10 = r6;
        goto L_0x00a0;
    L_0x007d:
        r0 = move-exception;
        goto L_0x0036;
    L_0x007f:
        r0 = move-exception;
        goto L_0x0034;
    L_0x0081:
        r0 = move-exception;
        goto L_0x0030;
    L_0x0083:
        r0 = move-exception;
        goto L_0x002e;
    L_0x0085:
        r11 = r26;
        r12 = r27;
        r13 = r28;
        r14 = r29;
        r15 = r30;
        r16 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x00f8 }
        r1 = r16;
        r2 = r9;
        r3 = r8;
        r4 = r11;
        r17 = r5;
        r5 = r12;
        r10 = r6;
        r6 = r13;
        r7 = r14;
        r8 = r15;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x00f6 }
    L_0x00a0:
        r8 = r16;	 Catch:{ Exception -> 0x00f6 }
        r1 = r8.getLineCount();	 Catch:{ Exception -> 0x00f6 }
        if (r1 > r10) goto L_0x00a9;	 Catch:{ Exception -> 0x00f6 }
    L_0x00a8:
        return r8;	 Catch:{ Exception -> 0x00f6 }
    L_0x00a9:
        r1 = r10 + -1;	 Catch:{ Exception -> 0x00f6 }
        r1 = r8.getLineLeft(r1);	 Catch:{ Exception -> 0x00f6 }
        r7 = r1;	 Catch:{ Exception -> 0x00f6 }
        r1 = 0;	 Catch:{ Exception -> 0x00f6 }
        r1 = (r7 > r1 ? 1 : (r7 == r1 ? 0 : -1));	 Catch:{ Exception -> 0x00f6 }
        if (r1 == 0) goto L_0x00bc;	 Catch:{ Exception -> 0x00f6 }
    L_0x00b5:
        r1 = r10 + -1;	 Catch:{ Exception -> 0x00f6 }
        r1 = r8.getOffsetForHorizontal(r1, r7);	 Catch:{ Exception -> 0x00f6 }
        goto L_0x00c8;	 Catch:{ Exception -> 0x00f6 }
    L_0x00bc:
        r1 = r10 + -1;	 Catch:{ Exception -> 0x00f6 }
        r2 = r10 + -1;	 Catch:{ Exception -> 0x00f6 }
        r2 = r8.getLineWidth(r2);	 Catch:{ Exception -> 0x00f6 }
        r1 = r8.getOffsetForHorizontal(r1, r2);	 Catch:{ Exception -> 0x00f6 }
    L_0x00c8:
        r16 = r1;	 Catch:{ Exception -> 0x00f6 }
        r1 = new android.text.SpannableStringBuilder;	 Catch:{ Exception -> 0x00f6 }
        r2 = r16 + -1;	 Catch:{ Exception -> 0x00f6 }
        r3 = 0;	 Catch:{ Exception -> 0x00f6 }
        r2 = java.lang.Math.max(r3, r2);	 Catch:{ Exception -> 0x00f6 }
        r2 = r9.subSequence(r3, r2);	 Catch:{ Exception -> 0x00f6 }
        r1.<init>(r2);	 Catch:{ Exception -> 0x00f6 }
        r6 = r1;	 Catch:{ Exception -> 0x00f6 }
        r1 = "\u2026";	 Catch:{ Exception -> 0x00f6 }
        r6.append(r1);	 Catch:{ Exception -> 0x00f6 }
        r18 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x00f6 }
        r1 = r18;	 Catch:{ Exception -> 0x00f6 }
        r2 = r6;	 Catch:{ Exception -> 0x00f6 }
        r3 = r25;	 Catch:{ Exception -> 0x00f6 }
        r4 = r11;	 Catch:{ Exception -> 0x00f6 }
        r5 = r12;	 Catch:{ Exception -> 0x00f6 }
        r19 = r6;	 Catch:{ Exception -> 0x00f6 }
        r6 = r13;	 Catch:{ Exception -> 0x00f6 }
        r20 = r7;	 Catch:{ Exception -> 0x00f6 }
        r7 = r14;	 Catch:{ Exception -> 0x00f6 }
        r21 = r8;	 Catch:{ Exception -> 0x00f6 }
        r8 = r15;	 Catch:{ Exception -> 0x00f6 }
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);	 Catch:{ Exception -> 0x00f6 }
        return r18;
    L_0x00f6:
        r0 = move-exception;
        goto L_0x0108;
    L_0x00f8:
        r0 = move-exception;
        goto L_0x0105;
    L_0x00fa:
        r0 = move-exception;
        r11 = r26;
        r12 = r27;
        r13 = r28;
        r14 = r29;
        r15 = r30;
    L_0x0105:
        r17 = r5;
        r10 = r6;
    L_0x0108:
        r1 = r0;
    L_0x0109:
        org.telegram.messenger.FileLog.m3e(r1);
        return r17;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(java.lang.CharSequence, int, int, android.text.TextPaint, int, android.text.Layout$Alignment, float, float, boolean, android.text.TextUtils$TruncateAt, int, int):android.text.StaticLayout");
    }
}
