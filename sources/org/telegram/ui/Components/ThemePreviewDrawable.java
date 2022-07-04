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

    /* JADX WARNING: type inference failed for: r7v19, types: [android.graphics.drawable.ColorDrawable] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap createPreview(java.io.File r44, org.telegram.messenger.DocumentObject.ThemeDocument r45) {
        /*
            r0 = r44
            r1 = r45
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
            r5 = 0
            java.util.HashMap r12 = org.telegram.ui.ActionBar.Theme.getThemeFileValues(r5, r4, r5)
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
            r5 = 45
            java.lang.Integer r4 = java.lang.Integer.valueOf(r5)
            r22 = r4
            goto L_0x0093
        L_0x0091:
            r22 = r4
        L_0x0093:
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131166089(0x7var_, float:1.7946414E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r7 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r7, r15)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131166091(0x7var_b, float:1.7946418E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r6 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r6, r15)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r5 = 2131166093(0x7var_d, float:1.7946422E38)
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)
            android.graphics.drawable.Drawable r5 = r4.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r5, r8)
            android.content.Context r4 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r4 = r4.getResources()
            r23 = r2
            r2 = 2131166092(0x7var_c, float:1.794642E38)
            android.graphics.drawable.Drawable r2 = r4.getDrawable(r2)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r2, r8)
            r4 = 2
            r24 = r8
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r8 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r4]
            r25 = 0
            r4 = r25
        L_0x00ee:
            r25 = r6
            r27 = r5
            r5 = 1
            r6 = 2
            if (r4 >= r6) goto L_0x011f
            org.telegram.ui.Components.ThemePreviewDrawable$1 r6 = new org.telegram.ui.Components.ThemePreviewDrawable$1
            if (r4 != r5) goto L_0x00fb
            goto L_0x00fc
        L_0x00fb:
            r5 = 0
        L_0x00fc:
            r30 = r7
            r31 = r9
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
            r6 = r25
            r5 = r27
            r7 = r30
            r9 = r31
            goto L_0x00ee
        L_0x011f:
            r30 = r7
            r31 = r9
            r4 = 0
            if (r18 == 0) goto L_0x0318
            r5 = 0
            r6 = 0
            if (r19 != 0) goto L_0x013f
            android.graphics.drawable.ColorDrawable r7 = new android.graphics.drawable.ColorDrawable
            int r9 = r18.intValue()
            r7.<init>(r9)
            r5 = r7
            int r7 = r18.intValue()
            int r7 = org.telegram.messenger.AndroidUtilities.getPatternColor(r7)
            r33 = r4
            goto L_0x019e
        L_0x013f:
            int r7 = r20.intValue()
            if (r7 == 0) goto L_0x0162
            org.telegram.ui.Components.MotionBackgroundDrawable r7 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r34 = r18.intValue()
            int r35 = r19.intValue()
            int r36 = r20.intValue()
            int r37 = r21.intValue()
            r38 = 1
            r33 = r7
            r33.<init>(r34, r35, r36, r37, r38)
            r6 = r7
            r33 = r4
            goto L_0x018e
        L_0x0162:
            r7 = 2
            int[] r9 = new int[r7]
            int r7 = r18.intValue()
            r28 = 0
            r9[r28] = r7
            int r7 = r19.intValue()
            r29 = 1
            r9[r29] = r7
            r7 = r9
            int r9 = r22.intValue()
            r33 = r4
            int r4 = r10.getWidth()
            int r34 = r10.getHeight()
            r35 = r5
            r32 = 120(0x78, float:1.68E-43)
            int r5 = r34 + -120
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
            r34 = r8
            r8 = 120(0x78, float:1.68E-43)
            int r9 = r9 - r8
            r35 = r12
            r12 = 0
            r5.setBounds(r12, r8, r4, r9)
            r5.draw(r11)
            goto L_0x01bb
        L_0x01b7:
            r34 = r8
            r35 = r12
        L_0x01bb:
            r4 = 0
            if (r0 == 0) goto L_0x02f2
            r8 = 560(0x230, float:7.85E-43)
            r9 = 678(0x2a6, float:9.5E-43)
            java.lang.String r12 = r1.mime_type
            r36 = r4
            java.lang.String r4 = "application/x-tgwallpattern"
            boolean r4 = r4.equals(r12)
            if (r4 == 0) goto L_0x01de
            r4 = 0
            android.graphics.Bitmap r12 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r8, (int) r9, (boolean) r4)
            r40 = r2
            r39 = r5
            r4 = r12
            r37 = r13
            r38 = r15
            goto L_0x0266
        L_0x01de:
            android.graphics.BitmapFactory$Options r4 = new android.graphics.BitmapFactory$Options
            r4.<init>()
            r12 = 1
            r4.inSampleSize = r12
            r4.inJustDecodeBounds = r12
            java.lang.String r12 = r44.getAbsolutePath()
            android.graphics.BitmapFactory.decodeFile(r12, r4)
            int r12 = r4.outWidth
            float r12 = (float) r12
            int r0 = r4.outHeight
            float r0 = (float) r0
            r37 = r8
            r38 = r9
            r39 = r5
            r5 = r37
            r37 = r13
            r13 = r38
            if (r5 < r13) goto L_0x0216
            int r38 = (r12 > r0 ? 1 : (r12 == r0 ? 0 : -1))
            if (r38 <= 0) goto L_0x0216
            r38 = r15
            float r15 = (float) r5
            float r15 = r12 / r15
            r40 = r2
            float r2 = (float) r13
            float r2 = r0 / r2
            float r2 = java.lang.Math.max(r15, r2)
            goto L_0x0224
        L_0x0216:
            r40 = r2
            r38 = r15
            float r2 = (float) r5
            float r2 = r12 / r2
            float r15 = (float) r13
            float r15 = r0 / r15
            float r2 = java.lang.Math.min(r2, r15)
        L_0x0224:
            r15 = 1067030938(0x3var_a, float:1.2)
            int r15 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r15 >= 0) goto L_0x022d
            r2 = 1065353216(0x3var_, float:1.0)
        L_0x022d:
            r15 = 0
            r4.inJustDecodeBounds = r15
            r15 = 1065353216(0x3var_, float:1.0)
            int r15 = (r2 > r15 ? 1 : (r2 == r15 ? 0 : -1))
            if (r15 <= 0) goto L_0x0258
            float r15 = (float) r5
            int r15 = (r12 > r15 ? 1 : (r12 == r15 ? 0 : -1))
            if (r15 > 0) goto L_0x0244
            float r15 = (float) r13
            int r15 = (r0 > r15 ? 1 : (r0 == r15 ? 0 : -1))
            if (r15 <= 0) goto L_0x0241
            goto L_0x0244
        L_0x0241:
            r41 = r0
            goto L_0x025a
        L_0x0244:
            r15 = 1
        L_0x0245:
            r26 = 2
            int r15 = r15 * 2
            r41 = r0
            int r0 = r15 * 2
            float r0 = (float) r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 < 0) goto L_0x0255
            r4.inSampleSize = r15
            goto L_0x025d
        L_0x0255:
            r0 = r41
            goto L_0x0245
        L_0x0258:
            r41 = r0
        L_0x025a:
            int r0 = (int) r2
            r4.inSampleSize = r0
        L_0x025d:
            java.lang.String r0 = r44.getAbsolutePath()
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r4)
            r4 = r0
        L_0x0266:
            if (r4 == 0) goto L_0x02ed
            if (r6 == 0) goto L_0x028e
            org.telegram.ui.ActionBar.Theme$ThemeAccent r0 = r1.accent
            float r0 = r0.patternIntensity
            r2 = 1120403456(0x42CLASSNAME, float:100.0)
            float r0 = r0 * r2
            int r0 = (int) r0
            r6.setPatternBitmap(r0, r4)
            int r0 = r10.getWidth()
            int r2 = r10.getHeight()
            r5 = 120(0x78, float:1.68E-43)
            int r2 = r2 - r5
            r12 = 0
            r6.setBounds(r12, r5, r0, r2)
            r6.draw(r11)
            r42 = r7
            r26 = 2
            goto L_0x0300
        L_0x028e:
            android.graphics.Paint r0 = new android.graphics.Paint
            r2 = 2
            r0.<init>(r2)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r1.accent
            float r2 = r2.patternIntensity
            r5 = 0
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 < 0) goto L_0x02a7
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r12 = android.graphics.PorterDuff.Mode.SRC_IN
            r2.<init>(r7, r12)
            r0.setColorFilter(r2)
        L_0x02a7:
            r2 = 255(0xff, float:3.57E-43)
            r0.setAlpha(r2)
            float r2 = (float) r8
            int r12 = r4.getWidth()
            float r12 = (float) r12
            float r2 = r2 / r12
            float r12 = (float) r9
            int r13 = r4.getHeight()
            float r13 = (float) r13
            float r12 = r12 / r13
            float r2 = java.lang.Math.max(r2, r12)
            int r12 = r4.getWidth()
            float r12 = (float) r12
            float r12 = r12 * r2
            int r12 = (int) r12
            int r13 = r4.getHeight()
            float r13 = (float) r13
            float r13 = r13 * r2
            int r13 = (int) r13
            int r15 = r8 - r12
            r26 = 2
            int r15 = r15 / 2
            int r36 = r9 - r13
            int r5 = r36 / 2
            r11.save()
            float r1 = (float) r15
            r42 = r7
            float r7 = (float) r5
            r11.translate(r1, r7)
            r11.scale(r2, r2)
            r1 = 0
            r11.drawBitmap(r4, r1, r1, r0)
            r11.restore()
            goto L_0x0300
        L_0x02ed:
            r42 = r7
            r26 = 2
            goto L_0x0300
        L_0x02f2:
            r40 = r2
            r36 = r4
            r39 = r5
            r42 = r7
            r37 = r13
            r38 = r15
            r26 = 2
        L_0x0300:
            if (r4 != 0) goto L_0x0316
            if (r6 == 0) goto L_0x0316
            int r0 = r10.getWidth()
            int r1 = r10.getHeight()
            r2 = 120(0x78, float:1.68E-43)
            int r1 = r1 - r2
            r5 = 0
            r6.setBounds(r5, r2, r0, r1)
            r6.draw(r11)
        L_0x0316:
            r0 = 1
            goto L_0x0328
        L_0x0318:
            r40 = r2
            r33 = r4
            r34 = r8
            r35 = r12
            r37 = r13
            r38 = r15
            r26 = 2
            r0 = r33
        L_0x0328:
            if (r0 != 0) goto L_0x034a
            int r1 = r10.getWidth()
            int r2 = r10.getHeight()
            r9 = 120(0x78, float:1.68E-43)
            int r2 = r2 - r9
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createDefaultWallpaper(r1, r2)
            int r2 = r10.getWidth()
            int r4 = r10.getHeight()
            int r4 = r4 - r9
            r6 = 0
            r1.setBounds(r6, r9, r2, r4)
            r1.draw(r11)
            goto L_0x034d
        L_0x034a:
            r6 = 0
            r9 = 120(0x78, float:1.68E-43)
        L_0x034d:
            r3.setColor(r14)
            r5 = 0
            r1 = 0
            int r2 = r10.getWidth()
            float r7 = (float) r2
            r8 = 1123024896(0x42var_, float:120.0)
            r2 = 2
            r4 = r11
            r12 = r27
            r13 = 1
            r15 = r25
            r25 = 0
            r6 = r1
            r1 = r30
            r26 = r34
            r43 = r31
            r27 = 120(0x78, float:1.68E-43)
            r9 = r3
            r4.drawRect(r5, r6, r7, r8, r9)
            if (r1 == 0) goto L_0x038a
            r4 = 13
            int r5 = r1.getIntrinsicHeight()
            int r9 = 120 - r5
            int r9 = r9 / r2
            int r5 = r1.getIntrinsicWidth()
            int r5 = r5 + r4
            int r6 = r1.getIntrinsicHeight()
            int r6 = r6 + r9
            r1.setBounds(r4, r9, r5, r6)
            r1.draw(r11)
        L_0x038a:
            if (r15 == 0) goto L_0x03ae
            int r4 = r10.getWidth()
            int r5 = r15.getIntrinsicWidth()
            int r4 = r4 - r5
            int r4 = r4 + -10
            int r5 = r15.getIntrinsicHeight()
            int r9 = 120 - r5
            int r9 = r9 / r2
            int r5 = r15.getIntrinsicWidth()
            int r5 = r5 + r4
            int r6 = r15.getIntrinsicHeight()
            int r6 = r6 + r9
            r15.setBounds(r4, r9, r5, r6)
            r15.draw(r11)
        L_0x03ae:
            r4 = r26[r13]
            r5 = 216(0xd8, float:3.03E-43)
            int r6 = r10.getWidth()
            r7 = 20
            int r6 = r6 - r7
            r8 = 308(0x134, float:4.32E-43)
            r9 = 161(0xa1, float:2.26E-43)
            r4.setBounds(r9, r5, r6, r8)
            r28 = r26[r13]
            r29 = 0
            r30 = 560(0x230, float:7.85E-43)
            r31 = 522(0x20a, float:7.31E-43)
            r32 = 0
            r33 = 0
            r28.setTop(r29, r30, r31, r32, r33)
            r4 = r26[r13]
            r4.draw(r11)
            r4 = r26[r13]
            r5 = 430(0x1ae, float:6.03E-43)
            int r6 = r10.getWidth()
            int r6 = r6 - r7
            r8 = 522(0x20a, float:7.31E-43)
            r4.setBounds(r9, r5, r6, r8)
            r28 = r26[r13]
            r29 = 430(0x1ae, float:6.03E-43)
            r28.setTop(r29, r30, r31, r32, r33)
            r4 = r26[r13]
            r4.draw(r11)
            r4 = r26[r25]
            r5 = 323(0x143, float:4.53E-43)
            r6 = 399(0x18f, float:5.59E-43)
            r8 = 415(0x19f, float:5.82E-43)
            r4.setBounds(r7, r5, r6, r8)
            r28 = r26[r25]
            r29 = 323(0x143, float:4.53E-43)
            r28.setTop(r29, r30, r31, r32, r33)
            r4 = r26[r25]
            r4.draw(r11)
            r13 = r43
            r3.setColor(r13)
            r5 = 0
            int r4 = r10.getHeight()
            int r4 = r4 + -120
            float r6 = (float) r4
            int r4 = r10.getWidth()
            float r7 = (float) r4
            int r4 = r10.getHeight()
            float r8 = (float) r4
            r4 = r11
            r9 = r3
            r4.drawRect(r5, r6, r7, r8, r9)
            if (r12 == 0) goto L_0x0443
            r4 = 22
            int r5 = r10.getHeight()
            int r5 = r5 + -120
            int r6 = r12.getIntrinsicHeight()
            int r9 = 120 - r6
            int r9 = r9 / r2
            int r5 = r5 + r9
            int r6 = r12.getIntrinsicWidth()
            int r6 = r6 + r4
            int r7 = r12.getIntrinsicHeight()
            int r7 = r7 + r5
            r12.setBounds(r4, r5, r6, r7)
            r12.draw(r11)
        L_0x0443:
            if (r40 == 0) goto L_0x0471
            int r4 = r10.getWidth()
            int r5 = r40.getIntrinsicWidth()
            int r4 = r4 - r5
            int r4 = r4 + -22
            int r5 = r10.getHeight()
            int r5 = r5 + -120
            int r6 = r40.getIntrinsicHeight()
            int r9 = 120 - r6
            int r9 = r9 / r2
            int r5 = r5 + r9
            int r2 = r40.getIntrinsicWidth()
            int r2 = r2 + r4
            int r6 = r40.getIntrinsicHeight()
            int r6 = r6 + r5
            r7 = r40
            r7.setBounds(r4, r5, r2, r6)
            r7.draw(r11)
            goto L_0x0473
        L_0x0471:
            r7 = r40
        L_0x0473:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemePreviewDrawable.createPreview(java.io.File, org.telegram.messenger.DocumentObject$ThemeDocument):android.graphics.Bitmap");
    }
}
