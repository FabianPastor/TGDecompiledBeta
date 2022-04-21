package org.telegram.ui.Components;

import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import org.telegram.messenger.DocumentObject;

public class ThemePreviewDrawable extends BitmapDrawable {
    private DocumentObject.ThemeDocument themeDocument;

    public ThemePreviewDrawable(File pattern, DocumentObject.ThemeDocument document) {
        super(createPreview(pattern, document));
        this.themeDocument = document;
    }

    public DocumentObject.ThemeDocument getThemeDocument() {
        return this.themeDocument;
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap createPreview(java.io.File r43, org.telegram.messenger.DocumentObject.ThemeDocument r44) {
        /*
            r0 = r43
            r1 = r44
            android.graphics.RectF r2 = new android.graphics.RectF
            r2.<init>()
            android.graphics.Paint r3 = new android.graphics.Paint
            r3.<init>()
            android.graphics.Bitmap$Config r4 = android.graphics.Bitmap.Config.ARGB_8888
            r5 = 560(0x230, float:7.85E-43)
            r6 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap r10 = org.telegram.messenger.Bitmaps.createBitmap(r5, r6, r4)
            android.graphics.Canvas r4 = new android.graphics.Canvas
            r4.<init>(r10)
            r11 = r4
            org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.baseTheme
            java.lang.String r4 = r4.assetName
            r7 = 0
            java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r7, r4, r7)
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>(r12)
            r13 = r4
            org.telegram.ui.ActionBar.Theme$ThemeAccent r4 = r1.accent
            r4.fillAccentColors(r12, r13)
            java.lang.String r4 = "actionBarDefault"
            int r14 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "actionBarDefaultIcon"
            int r15 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "chat_messagePanelBackground"
            int r9 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "chat_messagePanelIcons"
            int r8 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "chat_inBubble"
            int r16 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "chat_outBubble"
            int r17 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r13, r4)
            java.lang.String r4 = "chat_wallpaper"
            java.lang.Object r4 = r13.get(r4)
            r18 = r4
            java.lang.Integer r18 = (java.lang.Integer) r18
            java.lang.String r4 = "chat_wallpaper_gradient_to"
            java.lang.Object r4 = r13.get(r4)
            r19 = r4
            java.lang.Integer r19 = (java.lang.Integer) r19
            java.lang.String r4 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r4 = r13.get(r4)
            r20 = r4
            java.lang.Integer r20 = (java.lang.Integer) r20
            java.lang.String r4 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r4 = r13.get(r4)
            r21 = r4
            java.lang.Integer r21 = (java.lang.Integer) r21
            java.lang.String r4 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r4 = r13.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 != 0) goto L_0x0091
            r7 = 45
            java.lang.Integer r4 = java.lang.Integer.valueOf(r7)
            r22 = r4
            goto L_0x0093
        L_0x0091:
            r22 = r4
        L_0x0093:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r7 = 2131166062(0x7var_e, float:1.7946359E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r7, r15)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131166064(0x7var_, float:1.7946363E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r5, r15)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r6 = 2131166067(0x7var_, float:1.7946369E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r6)
            android.graphics.drawable.Drawable r6 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r6, r8)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r25 = r2
            r2 = 2131166065(0x7var_, float:1.7946365E38)
            android.graphics.drawable.Drawable r2 = r4.getDrawable(r2)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r2, r8)
            r4 = 2
            r26 = r8
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r8 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r4]
            r27 = 0
            r4 = r27
        L_0x00ee:
            r27 = r6
            r29 = r5
            r5 = 1
            r6 = 2
            if (r4 >= r6) goto L_0x011f
            org.telegram.ui.Components.ThemePreviewDrawable$1 r6 = new org.telegram.ui.Components.ThemePreviewDrawable$1
            if (r4 != r5) goto L_0x00fb
            goto L_0x00fc
        L_0x00fb:
            r5 = 0
        L_0x00fc:
            r32 = r7
            r33 = r9
            r7 = 0
            r9 = 2
            r6.<init>(r9, r5, r7, r13)
            r8[r4] = r6
            r5 = r8[r4]
            r6 = 1
            if (r4 != r6) goto L_0x010f
            r6 = r17
            goto L_0x0111
        L_0x010f:
            r6 = r16
        L_0x0111:
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r5, r6)
            int r4 = r4 + 1
            r6 = r27
            r5 = r29
            r7 = r32
            r9 = r33
            goto L_0x00ee
        L_0x011f:
            r32 = r7
            r33 = r9
            r4 = 0
            if (r18 == 0) goto L_0x0309
            r5 = 0
            r6 = 0
            if (r19 != 0) goto L_0x013f
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            int r9 = r18.intValue()
            r7.<init>(r9)
            r5 = r7
            int r7 = r18.intValue()
            int r7 = org.telegram.messenger.AndroidUtilities.getPatternColor(r7)
            r35 = r4
            goto L_0x019e
        L_0x013f:
            int r7 = r20.intValue()
            if (r7 == 0) goto L_0x0162
            org.telegram.ui.Components.MotionBackgroundDrawable r7 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r36 = r18.intValue()
            int r37 = r19.intValue()
            int r38 = r20.intValue()
            int r39 = r21.intValue()
            r40 = 1
            r35 = r7
            r35.<init>(r36, r37, r38, r39, r40)
            r6 = r7
            r35 = r4
            goto L_0x018e
        L_0x0162:
            r7 = 2
            int[] r9 = new int[r7]
            int r7 = r18.intValue()
            r30 = 0
            r9[r30] = r7
            int r7 = r19.intValue()
            r31 = 1
            r9[r31] = r7
            r7 = r9
            int r9 = r22.intValue()
            r35 = r4
            int r4 = r10.getWidth()
            int r36 = r10.getHeight()
            r37 = r5
            r34 = 120(0x78, float:1.68E-43)
            int r5 = r36 + -120
            android.graphics.drawable.BitmapDrawable r5 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r9, (int[]) r7, (int) r4, (int) r5)
        L_0x018e:
            int r4 = r18.intValue()
            int r7 = r19.intValue()
            int r4 = org.telegram.messenger.AndroidUtilities.getAverageColor(r4, r7)
            int r7 = org.telegram.messenger.AndroidUtilities.getPatternColor(r4)
        L_0x019e:
            if (r5 == 0) goto L_0x01b7
            int r4 = r10.getWidth()
            int r9 = r10.getHeight()
            r36 = r8
            r8 = 120(0x78, float:1.68E-43)
            int r9 = r9 - r8
            r37 = r12
            r12 = 0
            r5.setBounds(r12, r8, r4, r9)
            r5.draw(r11)
            goto L_0x01bb
        L_0x01b7:
            r36 = r8
            r37 = r12
        L_0x01bb:
            r4 = 0
            if (r0 == 0) goto L_0x02e7
            java.lang.String r8 = r1.mime_type
            java.lang.String r9 = "application/x-tgwallpattern"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x01d7
            r8 = 678(0x2a6, float:9.5E-43)
            r9 = 560(0x230, float:7.85E-43)
            r12 = 0
            android.graphics.Bitmap r4 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r9, (int) r8, (boolean) r12)
            r24 = r5
            r38 = r13
            goto L_0x0259
        L_0x01d7:
            android.graphics.BitmapFactory$Options r8 = new android.graphics.BitmapFactory$Options
            r8.<init>()
            r9 = 1
            r8.inSampleSize = r9
            r8.inJustDecodeBounds = r9
            java.lang.String r12 = r43.getAbsolutePath()
            android.graphics.BitmapFactory.decodeFile(r12, r8)
            int r12 = r8.outWidth
            float r12 = (float) r12
            int r9 = r8.outHeight
            float r9 = (float) r9
            r0 = 560(0x230, float:7.85E-43)
            r23 = r4
            r4 = 678(0x2a6, float:9.5E-43)
            if (r0 < r4) goto L_0x0209
            int r24 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r24 <= 0) goto L_0x0209
            r24 = r5
            float r5 = (float) r0
            float r5 = r12 / r5
            r38 = r13
            float r13 = (float) r4
            float r13 = r9 / r13
            float r5 = java.lang.Math.max(r5, r13)
            goto L_0x0217
        L_0x0209:
            r24 = r5
            r38 = r13
            float r5 = (float) r0
            float r5 = r12 / r5
            float r13 = (float) r4
            float r13 = r9 / r13
            float r5 = java.lang.Math.min(r5, r13)
        L_0x0217:
            r13 = 1067030938(0x3var_a, float:1.2)
            int r13 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r13 >= 0) goto L_0x0220
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x0220:
            r13 = 0
            r8.inJustDecodeBounds = r13
            r13 = 1065353216(0x3var_, float:1.0)
            int r13 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r13 <= 0) goto L_0x024b
            float r13 = (float) r0
            int r13 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1))
            if (r13 > 0) goto L_0x0237
            float r13 = (float) r4
            int r13 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r13 <= 0) goto L_0x0234
            goto L_0x0237
        L_0x0234:
            r39 = r0
            goto L_0x024d
        L_0x0237:
            r13 = 1
        L_0x0238:
            r28 = 2
            int r13 = r13 * 2
            r39 = r0
            int r0 = r13 * 2
            float r0 = (float) r0
            int r0 = (r0 > r5 ? 1 : (r0 == r5 ? 0 : -1))
            if (r0 < 0) goto L_0x0248
            r8.inSampleSize = r13
            goto L_0x0250
        L_0x0248:
            r0 = r39
            goto L_0x0238
        L_0x024b:
            r39 = r0
        L_0x024d:
            int r0 = (int) r5
            r8.inSampleSize = r0
        L_0x0250:
            java.lang.String r0 = r43.getAbsolutePath()
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r8)
            r4 = r0
        L_0x0259:
            if (r4 == 0) goto L_0x02e2
            if (r6 == 0) goto L_0x0280
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r1.accent
            float r0 = r0.patternIntensity
            r5 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r5
            int r0 = (int) r0
            r6.setPatternBitmap(r0, r4)
            int r0 = r10.getWidth()
            int r5 = r10.getHeight()
            r8 = 120(0x78, float:1.68E-43)
            int r5 = r5 - r8
            r9 = 0
            r6.setBounds(r9, r8, r0, r5)
            r6.draw(r11)
            r39 = r7
            r28 = 2
            goto L_0x02f1
        L_0x0280:
            android.graphics.Paint r0 = new android.graphics.Paint
            r5 = 2
            r0.<init>(r5)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r5 = r1.accent
            float r5 = r5.patternIntensity
            r8 = 0
            int r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1))
            if (r5 < 0) goto L_0x0299
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.SRC_IN
            r5.<init>(r7, r9)
            r0.setColorFilter(r5)
        L_0x0299:
            r5 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r5)
            r5 = 1141637120(0x440CLASSNAME, float:560.0)
            int r9 = r4.getWidth()
            float r9 = (float) r9
            float r5 = r5 / r9
            r9 = 1143570432(0x44298000, float:678.0)
            int r12 = r4.getHeight()
            float r12 = (float) r12
            float r9 = r9 / r12
            float r5 = java.lang.Math.max(r5, r9)
            int r9 = r4.getWidth()
            float r9 = (float) r9
            float r9 = r9 * r5
            int r9 = (int) r9
            int r12 = r4.getHeight()
            float r12 = (float) r12
            float r12 = r12 * r5
            int r12 = (int) r12
            int r13 = 560 - r9
            r28 = 2
            int r13 = r13 / 2
            int r8 = 678 - r12
            int r8 = r8 / 2
            r11.save()
            float r1 = (float) r13
            r39 = r7
            float r7 = (float) r8
            r11.translate(r1, r7)
            r11.scale(r5, r5)
            r1 = 0
            r11.drawBitmap(r4, r1, r1, r0)
            r11.restore()
            goto L_0x02f1
        L_0x02e2:
            r39 = r7
            r28 = 2
            goto L_0x02f1
        L_0x02e7:
            r23 = r4
            r24 = r5
            r39 = r7
            r38 = r13
            r28 = 2
        L_0x02f1:
            if (r4 != 0) goto L_0x0307
            if (r6 == 0) goto L_0x0307
            int r0 = r10.getWidth()
            int r1 = r10.getHeight()
            r5 = 120(0x78, float:1.68E-43)
            int r1 = r1 - r5
            r7 = 0
            r6.setBounds(r7, r5, r0, r1)
            r6.draw(r11)
        L_0x0307:
            r0 = 1
            goto L_0x0315
        L_0x0309:
            r35 = r4
            r36 = r8
            r37 = r12
            r38 = r13
            r28 = 2
            r0 = r35
        L_0x0315:
            if (r0 != 0) goto L_0x0337
            int r1 = r10.getWidth()
            int r4 = r10.getHeight()
            r9 = 120(0x78, float:1.68E-43)
            int r4 = r4 - r9
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createDefaultWallpaper(r1, r4)
            int r4 = r10.getWidth()
            int r5 = r10.getHeight()
            int r5 = r5 - r9
            r6 = 0
            r1.setBounds(r6, r9, r4, r5)
            r1.draw(r11)
            goto L_0x033a
        L_0x0337:
            r6 = 0
            r9 = 120(0x78, float:1.68E-43)
        L_0x033a:
            r3.setColor(r14)
            r5 = 0
            r1 = 0
            int r4 = r10.getWidth()
            float r7 = (float) r4
            r8 = 1123024896(0x42var_, float:120.0)
            r12 = 2
            r4 = r11
            r13 = r29
            r23 = 1
            r41 = r27
            r24 = 0
            r6 = r1
            r1 = r32
            r27 = r36
            r42 = r33
            r28 = 120(0x78, float:1.68E-43)
            r9 = r3
            r4.drawRect(r5, r6, r7, r8, r9)
            if (r1 == 0) goto L_0x0378
            r4 = 13
            int r5 = r1.getIntrinsicHeight()
            int r9 = 120 - r5
            int r9 = r9 / r12
            int r5 = r1.getIntrinsicWidth()
            int r5 = r5 + r4
            int r6 = r1.getIntrinsicHeight()
            int r6 = r6 + r9
            r1.setBounds(r4, r9, r5, r6)
            r1.draw(r11)
        L_0x0378:
            if (r13 == 0) goto L_0x039c
            int r4 = r10.getWidth()
            int r5 = r13.getIntrinsicWidth()
            int r4 = r4 - r5
            int r4 = r4 + -10
            int r5 = r13.getIntrinsicHeight()
            int r9 = 120 - r5
            int r9 = r9 / r12
            int r5 = r13.getIntrinsicWidth()
            int r5 = r5 + r4
            int r6 = r13.getIntrinsicHeight()
            int r6 = r6 + r9
            r13.setBounds(r4, r9, r5, r6)
            r13.draw(r11)
        L_0x039c:
            r4 = r27[r23]
            r5 = 216(0xd8, float:3.03E-43)
            int r6 = r10.getWidth()
            r7 = 20
            int r6 = r6 - r7
            r8 = 308(0x134, float:4.32E-43)
            r9 = 161(0xa1, float:2.26E-43)
            r4.setBounds(r9, r5, r6, r8)
            r29 = r27[r23]
            r30 = 0
            r31 = 560(0x230, float:7.85E-43)
            r32 = 522(0x20a, float:7.31E-43)
            r33 = 0
            r34 = 0
            r29.setTop(r30, r31, r32, r33, r34)
            r4 = r27[r23]
            r4.draw(r11)
            r4 = r27[r23]
            r5 = 430(0x1ae, float:6.03E-43)
            int r6 = r10.getWidth()
            int r6 = r6 - r7
            r8 = 522(0x20a, float:7.31E-43)
            r4.setBounds(r9, r5, r6, r8)
            r29 = r27[r23]
            r30 = 430(0x1ae, float:6.03E-43)
            r29.setTop(r30, r31, r32, r33, r34)
            r4 = r27[r23]
            r4.draw(r11)
            r4 = r27[r24]
            r5 = 323(0x143, float:4.53E-43)
            r6 = 399(0x18f, float:5.59E-43)
            r8 = 415(0x19f, float:5.82E-43)
            r4.setBounds(r7, r5, r6, r8)
            r29 = r27[r24]
            r30 = 323(0x143, float:4.53E-43)
            r29.setTop(r30, r31, r32, r33, r34)
            r4 = r27[r24]
            r4.draw(r11)
            r9 = r42
            r3.setColor(r9)
            r5 = 0
            int r4 = r10.getHeight()
            int r4 = r4 + -120
            float r6 = (float) r4
            int r4 = r10.getWidth()
            float r7 = (float) r4
            int r4 = r10.getHeight()
            float r8 = (float) r4
            r4 = r11
            r23 = r9
            r9 = r3
            r4.drawRect(r5, r6, r7, r8, r9)
            r4 = r41
            if (r4 == 0) goto L_0x0435
            r5 = 22
            int r6 = r10.getHeight()
            int r6 = r6 + -120
            int r7 = r4.getIntrinsicHeight()
            int r9 = 120 - r7
            int r9 = r9 / r12
            int r6 = r6 + r9
            int r7 = r4.getIntrinsicWidth()
            int r7 = r7 + r5
            int r8 = r4.getIntrinsicHeight()
            int r8 = r8 + r6
            r4.setBounds(r5, r6, r7, r8)
            r4.draw(r11)
        L_0x0435:
            if (r2 == 0) goto L_0x0460
            int r5 = r10.getWidth()
            int r6 = r2.getIntrinsicWidth()
            int r5 = r5 - r6
            int r5 = r5 + -22
            int r6 = r10.getHeight()
            int r6 = r6 + -120
            int r7 = r2.getIntrinsicHeight()
            int r9 = 120 - r7
            int r9 = r9 / r12
            int r6 = r6 + r9
            int r7 = r2.getIntrinsicWidth()
            int r7 = r7 + r5
            int r8 = r2.getIntrinsicHeight()
            int r8 = r8 + r6
            r2.setBounds(r5, r6, r7, r8)
            r2.draw(r11)
        L_0x0460:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemePreviewDrawable.createPreview(java.io.File, org.telegram.messenger.DocumentObject$ThemeDocument):android.graphics.Bitmap");
    }
}
