package org.telegram.ui.Components;

import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import org.telegram.messenger.DocumentObject;

public class ThemePreviewDrawable extends BitmapDrawable {
    public ThemePreviewDrawable(File file, DocumentObject.ThemeDocument themeDocument) {
        super(createPreview(file, themeDocument));
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v41, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v51, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r27v2, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r3v36 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap createPreview(java.io.File r33, org.telegram.messenger.DocumentObject.ThemeDocument r34) {
        /*
            r0 = r33
            r1 = r34
            android.graphics.RectF r2 = new android.graphics.RectF
            r2.<init>()
            android.graphics.Paint r2 = new android.graphics.Paint
            r2.<init>()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            r4 = 560(0x230, float:7.85E-43)
            r5 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap r9 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r3)
            android.graphics.Canvas r10 = new android.graphics.Canvas
            r10.<init>(r9)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.baseTheme
            java.lang.String r3 = r3.assetName
            r6 = 0
            java.util.HashMap r3 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r6, r3, r6)
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>(r3)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r1.accent
            r7.fillAccentColors(r3, r6)
            java.lang.String r3 = "actionBarDefault"
            int r3 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r3)
            java.lang.String r7 = "actionBarDefaultIcon"
            int r7 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r7)
            java.lang.String r8 = "chat_messagePanelBackground"
            int r11 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r8)
            java.lang.String r8 = "chat_messagePanelIcons"
            int r8 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r8)
            java.lang.String r12 = "chat_inBubble"
            int r12 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r12)
            java.lang.String r13 = "chat_outBubble"
            int r13 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r13)
            java.lang.String r14 = "chat_wallpaper"
            java.lang.Object r14 = r6.get(r14)
            java.lang.Integer r14 = (java.lang.Integer) r14
            java.lang.String r15 = "chat_wallpaper_gradient_to"
            java.lang.Object r15 = r6.get(r15)
            java.lang.Integer r15 = (java.lang.Integer) r15
            java.lang.String r4 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r4 = r6.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            java.lang.String r5 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r5 = r6.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            r18 = r12
            java.lang.String r12 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r12 = r6.get(r12)
            java.lang.Integer r12 = (java.lang.Integer) r12
            if (r12 != 0) goto L_0x0086
            r12 = 45
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
        L_0x0086:
            android.content.Context r19 = org.telegram.messenger.ApplicationLoader.applicationContext
            r20 = r13
            android.content.res.Resources r13 = r19.getResources()
            r19 = r11
            r11 = 2131165983(0x7var_f, float:1.7946199E38)
            android.graphics.drawable.Drawable r11 = r13.getDrawable(r11)
            android.graphics.drawable.Drawable r11 = r11.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r11, r7)
            android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r13 = r13.getResources()
            r21 = r11
            r11 = 2131165985(0x7var_, float:1.7946203E38)
            android.graphics.drawable.Drawable r11 = r13.getDrawable(r11)
            android.graphics.drawable.Drawable r11 = r11.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r11, r7)
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r7 = r7.getResources()
            r13 = 2131165988(0x7var_, float:1.7946209E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r13)
            android.graphics.drawable.Drawable r13 = r7.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r13, r8)
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r7 = r7.getResources()
            r22 = r13
            r13 = 2131165986(0x7var_, float:1.7946205E38)
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r13)
            android.graphics.drawable.Drawable r13 = r7.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r13, r8)
            r8 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r7 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r8]
            r23 = r13
            r8 = 0
        L_0x00e4:
            r13 = 2
            if (r8 >= r13) goto L_0x0111
            org.telegram.ui.Components.ThemePreviewDrawable$1 r13 = new org.telegram.ui.Components.ThemePreviewDrawable$1
            r24 = r11
            r11 = 1
            r25 = r2
            r26 = r3
            r2 = 0
            r3 = 2
            if (r8 != r11) goto L_0x00f5
            goto L_0x00f6
        L_0x00f5:
            r11 = 0
        L_0x00f6:
            r13.<init>(r3, r11, r2, r6)
            r7[r8] = r13
            r2 = r7[r8]
            r3 = 1
            if (r8 != r3) goto L_0x0103
            r3 = r20
            goto L_0x0105
        L_0x0103:
            r3 = r18
        L_0x0105:
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r2, r3)
            int r8 = r8 + 1
            r11 = r24
            r2 = r25
            r3 = r26
            goto L_0x00e4
        L_0x0111:
            r25 = r2
            r26 = r3
            r24 = r11
            r2 = 120(0x78, float:1.68E-43)
            if (r14 == 0) goto L_0x0274
            if (r15 != 0) goto L_0x012f
            android.graphics.drawable.ColorDrawable r3 = new android.graphics.drawable.ColorDrawable
            int r4 = r14.intValue()
            r3.<init>(r4)
            int r4 = r14.intValue()
            int r4 = org.telegram.messenger.AndroidUtilities.getPatternColor(r4)
            goto L_0x0181
        L_0x012f:
            int r3 = r4.intValue()
            if (r3 == 0) goto L_0x014f
            org.telegram.ui.Components.MotionBackgroundDrawable r3 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r28 = r14.intValue()
            int r29 = r15.intValue()
            int r30 = r4.intValue()
            int r31 = r5.intValue()
            r32 = 1
            r27 = r3
            r27.<init>(r28, r29, r30, r31, r32)
            goto L_0x0171
        L_0x014f:
            r3 = 2
            int[] r4 = new int[r3]
            int r3 = r14.intValue()
            r5 = 0
            r4[r5] = r3
            int r3 = r15.intValue()
            r5 = 1
            r4[r5] = r3
            int r3 = r12.intValue()
            int r5 = r9.getWidth()
            int r6 = r9.getHeight()
            int r6 = r6 - r2
            android.graphics.drawable.BitmapDrawable r3 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r3, (int[]) r4, (int) r5, (int) r6)
        L_0x0171:
            int r4 = r14.intValue()
            int r5 = r15.intValue()
            int r4 = org.telegram.messenger.AndroidUtilities.getAverageColor(r4, r5)
            int r4 = org.telegram.messenger.AndroidUtilities.getPatternColor(r4)
        L_0x0181:
            int r5 = r9.getWidth()
            int r6 = r9.getHeight()
            int r6 = r6 - r2
            r8 = 0
            r3.setBounds(r8, r2, r5, r6)
            r3.draw(r10)
            if (r0 == 0) goto L_0x0271
            java.lang.String r3 = r1.mime_type
            java.lang.String r5 = "application/x-tgwallpattern"
            boolean r3 = r5.equals(r3)
            if (r3 == 0) goto L_0x01a6
            r3 = 678(0x2a6, float:9.5E-43)
            r5 = 560(0x230, float:7.85E-43)
            android.graphics.Bitmap r0 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r5, (int) r3, (boolean) r8)
            goto L_0x0204
        L_0x01a6:
            android.graphics.BitmapFactory$Options r3 = new android.graphics.BitmapFactory$Options
            r3.<init>()
            r5 = 1
            r3.inSampleSize = r5
            r3.inJustDecodeBounds = r5
            java.lang.String r5 = r33.getAbsolutePath()
            android.graphics.BitmapFactory.decodeFile(r5, r3)
            int r5 = r3.outWidth
            float r5 = (float) r5
            int r6 = r3.outHeight
            float r6 = (float) r6
            r8 = 560(0x230, float:7.85E-43)
            float r11 = (float) r8
            float r8 = r5 / r11
            r11 = 678(0x2a6, float:9.5E-43)
            float r12 = (float) r11
            float r11 = r6 / r12
            float r8 = java.lang.Math.min(r8, r11)
            r11 = 1067030938(0x3var_a, float:1.2)
            r12 = 1065353216(0x3var_, float:1.0)
            int r11 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r11 >= 0) goto L_0x01d6
            r8 = 1065353216(0x3var_, float:1.0)
        L_0x01d6:
            r11 = 0
            r3.inJustDecodeBounds = r11
            int r11 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r11 <= 0) goto L_0x01f9
            r11 = 560(0x230, float:7.85E-43)
            float r12 = (float) r11
            int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r5 > 0) goto L_0x01eb
            r5 = 678(0x2a6, float:9.5E-43)
            float r11 = (float) r5
            int r5 = (r6 > r11 ? 1 : (r6 == r11 ? 0 : -1))
            if (r5 <= 0) goto L_0x01f9
        L_0x01eb:
            r5 = 1
        L_0x01ec:
            r6 = 2
            int r5 = r5 * 2
            int r6 = r5 * 2
            float r6 = (float) r6
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 < 0) goto L_0x01ec
            r3.inSampleSize = r5
            goto L_0x01fc
        L_0x01f9:
            int r5 = (int) r8
            r3.inSampleSize = r5
        L_0x01fc:
            java.lang.String r0 = r33.getAbsolutePath()
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r3)
        L_0x0204:
            if (r0 == 0) goto L_0x0271
            android.graphics.Paint r3 = new android.graphics.Paint
            r5 = 2
            r3.<init>(r5)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r1.accent
            float r5 = r5.patternIntensity
            r6 = 0
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r5 < 0) goto L_0x021f
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r5.<init>(r4, r8)
            r3.setColorFilter(r5)
        L_0x021f:
            r4 = 1132396544(0x437var_, float:255.0)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r1 = r1.accent
            float r1 = r1.patternIntensity
            float r1 = java.lang.Math.abs(r1)
            float r1 = r1 * r4
            int r1 = (int) r1
            r3.setAlpha(r1)
            r1 = 1141637120(0x440CLASSNAME, float:560.0)
            int r4 = r0.getWidth()
            float r4 = (float) r4
            float r1 = r1 / r4
            r4 = 1143570432(0x44298000, float:678.0)
            int r5 = r0.getHeight()
            float r5 = (float) r5
            float r4 = r4 / r5
            float r1 = java.lang.Math.max(r1, r4)
            int r4 = r0.getWidth()
            float r4 = (float) r4
            float r4 = r4 * r1
            int r4 = (int) r4
            int r5 = r0.getHeight()
            float r5 = (float) r5
            float r5 = r5 * r1
            int r5 = (int) r5
            r8 = 560(0x230, float:7.85E-43)
            int r4 = 560 - r4
            r8 = 2
            int r4 = r4 / r8
            r11 = 678(0x2a6, float:9.5E-43)
            int r5 = 678 - r5
            int r5 = r5 / r8
            r10.save()
            float r4 = (float) r4
            float r5 = (float) r5
            r10.translate(r4, r5)
            r10.scale(r1, r1)
            r10.drawBitmap(r0, r6, r6, r3)
            r10.restore()
            goto L_0x0272
        L_0x0271:
            r8 = 2
        L_0x0272:
            r0 = 1
            goto L_0x0276
        L_0x0274:
            r8 = 2
            r0 = 0
        L_0x0276:
            if (r0 != 0) goto L_0x0296
            int r0 = r9.getWidth()
            int r1 = r9.getHeight()
            int r1 = r1 - r2
            android.graphics.drawable.Drawable r0 = org.telegram.ui.ActionBar.Theme.createDefaultWallpaper(r0, r1)
            int r1 = r9.getWidth()
            int r3 = r9.getHeight()
            int r3 = r3 - r2
            r11 = 0
            r0.setBounds(r11, r2, r1, r3)
            r0.draw(r10)
            goto L_0x0297
        L_0x0296:
            r11 = 0
        L_0x0297:
            r0 = r25
            r1 = r26
            r0.setColor(r1)
            r4 = 0
            r5 = 0
            int r1 = r9.getWidth()
            float r6 = (float) r1
            r1 = 1123024896(0x42var_, float:120.0)
            r3 = r10
            r12 = r7
            r7 = r1
            r1 = 0
            r11 = 2
            r8 = r0
            r3.drawRect(r4, r5, r6, r7, r8)
            if (r21 == 0) goto L_0x02cd
            r3 = 13
            int r4 = r21.getIntrinsicHeight()
            int r4 = 120 - r4
            int r4 = r4 / r11
            int r5 = r21.getIntrinsicWidth()
            int r5 = r5 + r3
            int r6 = r21.getIntrinsicHeight()
            int r6 = r6 + r4
            r7 = r21
            r7.setBounds(r3, r4, r5, r6)
            r7.draw(r10)
        L_0x02cd:
            if (r24 == 0) goto L_0x02f3
            int r3 = r9.getWidth()
            int r4 = r24.getIntrinsicWidth()
            int r3 = r3 - r4
            int r3 = r3 + -10
            int r4 = r24.getIntrinsicHeight()
            int r4 = 120 - r4
            int r4 = r4 / r11
            int r5 = r24.getIntrinsicWidth()
            int r5 = r5 + r3
            int r6 = r24.getIntrinsicHeight()
            int r6 = r6 + r4
            r7 = r24
            r7.setBounds(r3, r4, r5, r6)
            r7.draw(r10)
        L_0x02f3:
            r3 = 1
            r4 = r12[r3]
            r5 = 216(0xd8, float:3.03E-43)
            int r6 = r9.getWidth()
            r7 = 20
            int r6 = r6 - r7
            r8 = 308(0x134, float:4.32E-43)
            r13 = 161(0xa1, float:2.26E-43)
            r4.setBounds(r13, r5, r6, r8)
            r24 = r12[r3]
            r25 = 0
            r26 = 560(0x230, float:7.85E-43)
            r27 = 522(0x20a, float:7.31E-43)
            r28 = 0
            r29 = 0
            r24.setTop(r25, r26, r27, r28, r29)
            r3 = 1
            r4 = r12[r3]
            r4.draw(r10)
            r4 = r12[r3]
            r5 = 430(0x1ae, float:6.03E-43)
            int r6 = r9.getWidth()
            int r6 = r6 - r7
            r8 = 522(0x20a, float:7.31E-43)
            r4.setBounds(r13, r5, r6, r8)
            r24 = r12[r3]
            r25 = 430(0x1ae, float:6.03E-43)
            r24.setTop(r25, r26, r27, r28, r29)
            r3 = 1
            r3 = r12[r3]
            r3.draw(r10)
            r3 = r12[r1]
            r4 = 323(0x143, float:4.53E-43)
            r5 = 399(0x18f, float:5.59E-43)
            r6 = 415(0x19f, float:5.82E-43)
            r3.setBounds(r7, r4, r5, r6)
            r13 = r12[r1]
            r14 = 323(0x143, float:4.53E-43)
            r15 = 560(0x230, float:7.85E-43)
            r16 = 522(0x20a, float:7.31E-43)
            r17 = 0
            r18 = 0
            r13.setTop(r14, r15, r16, r17, r18)
            r1 = r12[r1]
            r1.draw(r10)
            r1 = r19
            r0.setColor(r1)
            r4 = 0
            int r1 = r9.getHeight()
            int r1 = r1 - r2
            float r5 = (float) r1
            int r1 = r9.getWidth()
            float r6 = (float) r1
            int r1 = r9.getHeight()
            float r7 = (float) r1
            r3 = r10
            r8 = r0
            r3.drawRect(r4, r5, r6, r7, r8)
            r0 = 22
            if (r22 == 0) goto L_0x0393
            int r1 = r9.getHeight()
            int r1 = r1 - r2
            int r3 = r22.getIntrinsicHeight()
            int r3 = 120 - r3
            int r3 = r3 / r11
            int r1 = r1 + r3
            int r3 = r22.getIntrinsicWidth()
            int r3 = r3 + r0
            int r4 = r22.getIntrinsicHeight()
            int r4 = r4 + r1
            r5 = r22
            r5.setBounds(r0, r1, r3, r4)
            r5.draw(r10)
        L_0x0393:
            if (r23 == 0) goto L_0x03bd
            int r1 = r9.getWidth()
            int r3 = r23.getIntrinsicWidth()
            int r1 = r1 - r3
            int r1 = r1 - r0
            int r0 = r9.getHeight()
            int r0 = r0 - r2
            int r3 = r23.getIntrinsicHeight()
            int r2 = r2 - r3
            int r2 = r2 / r11
            int r0 = r0 + r2
            int r2 = r23.getIntrinsicWidth()
            int r2 = r2 + r1
            int r3 = r23.getIntrinsicHeight()
            int r3 = r3 + r0
            r4 = r23
            r4.setBounds(r1, r0, r2, r3)
            r4.draw(r10)
        L_0x03bd:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemePreviewDrawable.createPreview(java.io.File, org.telegram.messenger.DocumentObject$ThemeDocument):android.graphics.Bitmap");
    }
}
