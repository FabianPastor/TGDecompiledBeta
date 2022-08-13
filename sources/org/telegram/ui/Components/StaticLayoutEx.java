package org.telegram.ui.Components;

import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v0, resolved type: android.text.StaticLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v1, resolved type: android.text.StaticLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v3, resolved type: android.text.TextUtils$TruncateAt} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v5, resolved type: android.text.StaticLayout} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r19v6, resolved type: android.text.StaticLayout} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.text.StaticLayout createStaticLayout(java.lang.CharSequence r20, int r21, int r22, android.text.TextPaint r23, int r24, android.text.Layout.Alignment r25, float r26, float r27, boolean r28, android.text.TextUtils.TruncateAt r29, int r30, int r31, boolean r32) {
        /*
            r0 = r20
            r9 = r23
            r10 = r24
            r11 = r25
            r12 = r26
            r13 = r27
            r14 = r28
            r15 = r30
            r8 = r31
            r7 = 0
            r6 = 1
            if (r8 != r6) goto L_0x003b
            float r1 = (float) r15
            android.text.TextUtils$TruncateAt r2 = android.text.TextUtils.TruncateAt.END     // Catch:{ Exception -> 0x013f }
            java.lang.CharSequence r1 = android.text.TextUtils.ellipsize(r0, r9, r1, r2)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout r15 = new android.text.StaticLayout     // Catch:{ Exception -> 0x013f }
            r2 = 0
            int r3 = r1.length()     // Catch:{ Exception -> 0x013f }
            r0 = r15
            r4 = r23
            r5 = r24
            r6 = r25
            r10 = r7
            r7 = r26
            r8 = r27
            r9 = r28
            r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x0036 }
            return r15
        L_0x0036:
            r0 = move-exception
            r19 = r10
            goto L_0x0142
        L_0x003b:
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x013f }
            r4 = 23
            r3 = 0
            if (r5 < r4) goto L_0x0076
            int r1 = r20.length()     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = android.text.StaticLayout.Builder.obtain(r0, r3, r1, r9, r10)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setAlignment(r11)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setLineSpacing(r13, r12)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setIncludePad(r14)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setEllipsize(r7)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setEllipsizedWidth(r15)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setMaxLines(r8)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setBreakStrategy(r6)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout$Builder r1 = r1.setHyphenationFrequency(r3)     // Catch:{ Exception -> 0x013f }
            android.text.StaticLayout r1 = r1.build()     // Catch:{ Exception -> 0x013f }
            r17 = r5
            r19 = r7
            r14 = r8
            r18 = 1
            goto L_0x0095
        L_0x0076:
            android.text.StaticLayout r16 = new android.text.StaticLayout     // Catch:{ Exception -> 0x013f }
            r1 = r16
            r2 = r20
            r14 = 0
            r3 = r23
            r4 = r24
            r17 = r5
            r5 = r25
            r18 = 1
            r6 = r26
            r19 = r7
            r7 = r27
            r14 = r8
            r8 = r28
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x013d }
            r1 = r16
        L_0x0095:
            int r2 = r1.getLineCount()     // Catch:{ Exception -> 0x013d }
            if (r2 > r14) goto L_0x009c
            return r1
        L_0x009c:
            int r2 = r14 + -1
            float r3 = r1.getLineLeft(r2)     // Catch:{ Exception -> 0x013d }
            float r4 = r1.getLineWidth(r2)     // Catch:{ Exception -> 0x013d }
            r5 = 0
            int r5 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r5 == 0) goto L_0x00b0
            int r1 = r1.getOffsetForHorizontal(r2, r3)     // Catch:{ Exception -> 0x013d }
            goto L_0x00b4
        L_0x00b0:
            int r1 = r1.getOffsetForHorizontal(r2, r4)     // Catch:{ Exception -> 0x013d }
        L_0x00b4:
            r2 = 1092616192(0x41200000, float:10.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ Exception -> 0x013d }
            int r2 = r15 - r2
            float r2 = (float) r2     // Catch:{ Exception -> 0x013d }
            int r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x00c3
            int r1 = r1 + 3
        L_0x00c3:
            android.text.SpannableStringBuilder r2 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x013d }
            int r1 = r1 + -3
            r3 = 0
            int r1 = java.lang.Math.max(r3, r1)     // Catch:{ Exception -> 0x013d }
            java.lang.CharSequence r0 = r0.subSequence(r3, r1)     // Catch:{ Exception -> 0x013d }
            r2.<init>(r0)     // Catch:{ Exception -> 0x013d }
            java.lang.String r0 = "…"
            r2.append(r0)     // Catch:{ Exception -> 0x013d }
            r0 = r17
            r1 = 23
            if (r0 < r1) goto L_0x0127
            int r0 = r2.length()     // Catch:{ Exception -> 0x013d }
            r1 = 0
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r2, r1, r0, r9, r10)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r11)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout$Builder r0 = r0.setLineSpacing(r13, r12)     // Catch:{ Exception -> 0x013d }
            r7 = r28
            android.text.StaticLayout$Builder r0 = r0.setIncludePad(r7)     // Catch:{ Exception -> 0x013d }
            int r3 = r2.length()     // Catch:{ Exception -> 0x013d }
            java.lang.Class<android.text.style.CharacterStyle> r4 = android.text.style.CharacterStyle.class
            java.lang.Object[] r2 = r2.getSpans(r1, r3, r4)     // Catch:{ Exception -> 0x013d }
            android.text.style.CharacterStyle[] r2 = (android.text.style.CharacterStyle[]) r2     // Catch:{ Exception -> 0x013d }
            int r2 = r2.length     // Catch:{ Exception -> 0x013d }
            if (r2 <= 0) goto L_0x0107
            r2 = r19
            goto L_0x0109
        L_0x0107:
            r2 = r29
        L_0x0109:
            android.text.StaticLayout$Builder r0 = r0.setEllipsize(r2)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout$Builder r0 = r0.setEllipsizedWidth(r15)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout$Builder r0 = r0.setMaxLines(r14)     // Catch:{ Exception -> 0x013d }
            if (r32 == 0) goto L_0x0119
            r6 = 1
            goto L_0x011a
        L_0x0119:
            r6 = 0
        L_0x011a:
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r6)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r1)     // Catch:{ Exception -> 0x013d }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x013d }
            return r0
        L_0x0127:
            r7 = r28
            android.text.StaticLayout r8 = new android.text.StaticLayout     // Catch:{ Exception -> 0x013d }
            r0 = r8
            r1 = r2
            r2 = r23
            r3 = r24
            r4 = r25
            r5 = r26
            r6 = r27
            r7 = r28
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x013d }
            return r8
        L_0x013d:
            r0 = move-exception
            goto L_0x0142
        L_0x013f:
            r0 = move-exception
            r19 = r7
        L_0x0142:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return r19
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(java.lang.CharSequence, int, int, android.text.TextPaint, int, android.text.Layout$Alignment, float, float, boolean, android.text.TextUtils$TruncateAt, int, int, boolean):android.text.StaticLayout");
    }
}
