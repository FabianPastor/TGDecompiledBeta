package org.telegram.ui.Components;

import android.graphics.drawable.BitmapDrawable;
import java.io.File;
import org.telegram.messenger.DocumentObject;

public class ThemePreviewDrawable extends BitmapDrawable {
    private DocumentObject.ThemeDocument themeDocument;

    public ThemePreviewDrawable(File file, DocumentObject.ThemeDocument themeDocument2) {
        super(createPreview(file, themeDocument2));
        this.themeDocument = themeDocument2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v44, resolved type: android.graphics.drawable.BitmapDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v54, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r26v1, resolved type: org.telegram.ui.Components.MotionBackgroundDrawable} */
    /* JADX WARNING: type inference failed for: r2v39 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static android.graphics.Bitmap createPreview(java.io.File r32, org.telegram.messenger.DocumentObject.ThemeDocument r33) {
        /*
            r0 = r32
            r1 = r33
            android.graphics.RectF r2 = new android.graphics.RectF
            r2.<init>()
            android.graphics.Paint r9 = new android.graphics.Paint
            r9.<init>()
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888
            r4 = 560(0x230, float:7.85E-43)
            r5 = 678(0x2a6, float:9.5E-43)
            android.graphics.Bitmap r10 = org.telegram.messenger.Bitmaps.createBitmap(r4, r5, r3)
            android.graphics.Canvas r11 = new android.graphics.Canvas
            r11.<init>(r10)
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
            int r12 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r8)
            java.lang.String r8 = "chat_messagePanelIcons"
            int r8 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r8)
            java.lang.String r13 = "chat_inBubble"
            int r13 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r13)
            java.lang.String r14 = "chat_outBubble"
            int r14 = org.telegram.ui.ActionBar.Theme.getPreviewColor(r6, r14)
            java.lang.String r15 = "chat_outBubbleGradient"
            java.lang.Object r15 = r6.get(r15)
            java.lang.Integer r15 = (java.lang.Integer) r15
            java.lang.String r15 = "chat_wallpaper"
            java.lang.Object r15 = r6.get(r15)
            java.lang.Integer r15 = (java.lang.Integer) r15
            java.lang.String r4 = "chat_serviceBackground"
            java.lang.Object r4 = r6.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            java.lang.String r5 = "chat_wallpaper_gradient_to"
            java.lang.Object r5 = r6.get(r5)
            java.lang.Integer r5 = (java.lang.Integer) r5
            r16 = r13
            java.lang.String r13 = "key_chat_wallpaper_gradient_to2"
            java.lang.Object r13 = r6.get(r13)
            java.lang.Integer r13 = (java.lang.Integer) r13
            r17 = r14
            java.lang.String r14 = "key_chat_wallpaper_gradient_to3"
            java.lang.Object r14 = r6.get(r14)
            java.lang.Integer r14 = (java.lang.Integer) r14
            r18 = r12
            java.lang.String r12 = "chat_wallpaper_gradient_rotation"
            java.lang.Object r12 = r6.get(r12)
            java.lang.Integer r12 = (java.lang.Integer) r12
            if (r12 != 0) goto L_0x009a
            r12 = 45
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
        L_0x009a:
            android.content.Context r19 = org.telegram.messenger.ApplicationLoader.applicationContext
            r20 = r2
            android.content.res.Resources r2 = r19.getResources()
            r19 = r3
            r3 = 2131165969(0x7var_, float:1.794617E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r2, r7)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r3 = r3.getResources()
            r21 = r2
            r2 = 2131165971(0x7var_, float:1.7946174E38)
            android.graphics.drawable.Drawable r2 = r3.getDrawable(r2)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r2, r7)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r3 = r3.getResources()
            r7 = 2131165974(0x7var_, float:1.794618E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r3.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r7, r8)
            android.content.Context r3 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r3 = r3.getResources()
            r22 = r7
            r7 = 2131165972(0x7var_, float:1.7946176E38)
            android.graphics.drawable.Drawable r3 = r3.getDrawable(r7)
            android.graphics.drawable.Drawable r7 = r3.mutate()
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r7, r8)
            r8 = 2
            org.telegram.ui.ActionBar.Theme$MessageDrawable[] r3 = new org.telegram.ui.ActionBar.Theme.MessageDrawable[r8]
            r23 = r7
            r24 = r2
            r7 = 0
        L_0x00fa:
            r2 = 1
            if (r7 >= r8) goto L_0x0121
            org.telegram.ui.Components.ThemePreviewDrawable$1 r8 = new org.telegram.ui.Components.ThemePreviewDrawable$1
            r25 = r9
            r1 = 0
            if (r7 != r2) goto L_0x0105
            goto L_0x0106
        L_0x0105:
            r2 = 0
        L_0x0106:
            r9 = 2
            r8.<init>(r9, r2, r1, r6)
            r3[r7] = r8
            r1 = r3[r7]
            r2 = 1
            if (r7 != r2) goto L_0x0114
            r2 = r17
            goto L_0x0116
        L_0x0114:
            r2 = r16
        L_0x0116:
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r1, r2)
            int r7 = r7 + 1
            r1 = r33
            r9 = r25
            r8 = 2
            goto L_0x00fa
        L_0x0121:
            r25 = r9
            r1 = 120(0x78, float:1.68E-43)
            if (r15 == 0) goto L_0x0298
            if (r5 != 0) goto L_0x013b
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            int r5 = r15.intValue()
            r2.<init>(r5)
            int r5 = r15.intValue()
            int r5 = org.telegram.messenger.AndroidUtilities.getPatternColor(r5)
            goto L_0x018d
        L_0x013b:
            int r2 = r13.intValue()
            if (r2 == 0) goto L_0x015b
            org.telegram.ui.Components.MotionBackgroundDrawable r2 = new org.telegram.ui.Components.MotionBackgroundDrawable
            int r27 = r15.intValue()
            int r28 = r5.intValue()
            int r29 = r13.intValue()
            int r30 = r14.intValue()
            r31 = 1
            r26 = r2
            r26.<init>(r27, r28, r29, r30, r31)
            goto L_0x017d
        L_0x015b:
            r2 = 2
            int[] r6 = new int[r2]
            int r2 = r15.intValue()
            r7 = 0
            r6[r7] = r2
            int r2 = r5.intValue()
            r7 = 1
            r6[r7] = r2
            int r2 = r12.intValue()
            int r7 = r10.getWidth()
            int r8 = r10.getHeight()
            int r8 = r8 - r1
            android.graphics.drawable.BitmapDrawable r2 = org.telegram.ui.Components.BackgroundGradientDrawable.createDitheredGradientBitmapDrawable((int) r2, (int[]) r6, (int) r7, (int) r8)
        L_0x017d:
            int r6 = r15.intValue()
            int r5 = r5.intValue()
            int r5 = org.telegram.messenger.AndroidUtilities.getAverageColor(r6, r5)
            int r5 = org.telegram.messenger.AndroidUtilities.getPatternColor(r5)
        L_0x018d:
            int r6 = r10.getWidth()
            int r7 = r10.getHeight()
            int r7 = r7 - r1
            r8 = 0
            r2.setBounds(r8, r1, r6, r7)
            r2.draw(r11)
            if (r4 != 0) goto L_0x01b3
            android.graphics.drawable.ColorDrawable r2 = new android.graphics.drawable.ColorDrawable
            int r4 = r15.intValue()
            r2.<init>(r4)
            int[] r2 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r2)
            r2 = r2[r8]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4 = r2
        L_0x01b3:
            if (r0 == 0) goto L_0x0295
            r2 = r33
            java.lang.String r6 = r2.mime_type
            java.lang.String r7 = "application/x-tgwallpattern"
            boolean r6 = r7.equals(r6)
            if (r6 == 0) goto L_0x01ca
            r6 = 678(0x2a6, float:9.5E-43)
            r7 = 560(0x230, float:7.85E-43)
            android.graphics.Bitmap r0 = org.telegram.messenger.SvgHelper.getBitmap((java.io.File) r0, (int) r7, (int) r6, (boolean) r8)
            goto L_0x0228
        L_0x01ca:
            android.graphics.BitmapFactory$Options r6 = new android.graphics.BitmapFactory$Options
            r6.<init>()
            r7 = 1
            r6.inSampleSize = r7
            r6.inJustDecodeBounds = r7
            java.lang.String r7 = r32.getAbsolutePath()
            android.graphics.BitmapFactory.decodeFile(r7, r6)
            int r7 = r6.outWidth
            float r7 = (float) r7
            int r8 = r6.outHeight
            float r8 = (float) r8
            r9 = 560(0x230, float:7.85E-43)
            float r12 = (float) r9
            float r9 = r7 / r12
            r12 = 678(0x2a6, float:9.5E-43)
            float r13 = (float) r12
            float r12 = r8 / r13
            float r9 = java.lang.Math.min(r9, r12)
            r12 = 1067030938(0x3var_a, float:1.2)
            r13 = 1065353216(0x3var_, float:1.0)
            int r12 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r12 >= 0) goto L_0x01fa
            r9 = 1065353216(0x3var_, float:1.0)
        L_0x01fa:
            r12 = 0
            r6.inJustDecodeBounds = r12
            int r12 = (r9 > r13 ? 1 : (r9 == r13 ? 0 : -1))
            if (r12 <= 0) goto L_0x021d
            r12 = 560(0x230, float:7.85E-43)
            float r13 = (float) r12
            int r7 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r7 > 0) goto L_0x020f
            r7 = 678(0x2a6, float:9.5E-43)
            float r12 = (float) r7
            int r7 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r7 <= 0) goto L_0x021d
        L_0x020f:
            r7 = 1
        L_0x0210:
            r8 = 2
            int r7 = r7 * 2
            int r8 = r7 * 2
            float r8 = (float) r8
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 < 0) goto L_0x0210
            r6.inSampleSize = r7
            goto L_0x0220
        L_0x021d:
            int r7 = (int) r9
            r6.inSampleSize = r7
        L_0x0220:
            java.lang.String r0 = r32.getAbsolutePath()
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeFile(r0, r6)
        L_0x0228:
            if (r0 == 0) goto L_0x0295
            android.graphics.Paint r6 = new android.graphics.Paint
            r7 = 2
            r6.<init>(r7)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r7 = r2.accent
            float r7 = r7.patternIntensity
            r8 = 0
            int r7 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r7 < 0) goto L_0x0243
            android.graphics.PorterDuffColorFilter r7 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r9 = android.graphics.PorterDuff.Mode.SRC_IN
            r7.<init>(r5, r9)
            r6.setColorFilter(r7)
        L_0x0243:
            r5 = 1132396544(0x437var_, float:255.0)
            org.telegram.ui.ActionBar.Theme$ThemeAccent r2 = r2.accent
            float r2 = r2.patternIntensity
            float r2 = java.lang.Math.abs(r2)
            float r2 = r2 * r5
            int r2 = (int) r2
            r6.setAlpha(r2)
            r2 = 1141637120(0x440CLASSNAME, float:560.0)
            int r5 = r0.getWidth()
            float r5 = (float) r5
            float r2 = r2 / r5
            r5 = 1143570432(0x44298000, float:678.0)
            int r7 = r0.getHeight()
            float r7 = (float) r7
            float r5 = r5 / r7
            float r2 = java.lang.Math.max(r2, r5)
            int r5 = r0.getWidth()
            float r5 = (float) r5
            float r5 = r5 * r2
            int r5 = (int) r5
            int r7 = r0.getHeight()
            float r7 = (float) r7
            float r7 = r7 * r2
            int r7 = (int) r7
            r9 = 560(0x230, float:7.85E-43)
            int r5 = 560 - r5
            r9 = 2
            int r5 = r5 / r9
            r12 = 678(0x2a6, float:9.5E-43)
            int r7 = 678 - r7
            int r7 = r7 / r9
            r11.save()
            float r5 = (float) r5
            float r7 = (float) r7
            r11.translate(r5, r7)
            r11.scale(r2, r2)
            r11.drawBitmap(r0, r8, r8, r6)
            r11.restore()
            goto L_0x0296
        L_0x0295:
            r9 = 2
        L_0x0296:
            r0 = 1
            goto L_0x029a
        L_0x0298:
            r9 = 2
            r0 = 0
        L_0x029a:
            if (r0 != 0) goto L_0x02d4
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext
            android.content.res.Resources r0 = r0.getResources()
            r2 = 2131165338(0x7var_a, float:1.794489E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r2)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.drawable.BitmapDrawable r0 = (android.graphics.drawable.BitmapDrawable) r0
            if (r4 != 0) goto L_0x02be
            int[] r2 = org.telegram.messenger.AndroidUtilities.calcDrawableColor(r0)
            r7 = 0
            r2 = r2[r7]
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r4 = r2
            goto L_0x02bf
        L_0x02be:
            r7 = 0
        L_0x02bf:
            android.graphics.Shader$TileMode r2 = android.graphics.Shader.TileMode.REPEAT
            r0.setTileModeXY(r2, r2)
            int r2 = r10.getWidth()
            int r5 = r10.getHeight()
            int r5 = r5 - r1
            r0.setBounds(r7, r1, r2, r5)
            r0.draw(r11)
            goto L_0x02d5
        L_0x02d4:
            r7 = 0
        L_0x02d5:
            r0 = r4
            r4 = r19
            r2 = r25
            r2.setColor(r4)
            r4 = 0
            r5 = 0
            int r6 = r10.getWidth()
            float r6 = (float) r6
            r8 = 1123024896(0x42var_, float:120.0)
            r12 = r3
            r3 = r11
            r13 = r22
            r14 = r23
            r15 = 0
            r7 = r8
            r8 = r2
            r3.drawRect(r4, r5, r6, r7, r8)
            if (r21 == 0) goto L_0x030f
            r3 = 13
            int r4 = r21.getIntrinsicHeight()
            int r4 = 120 - r4
            int r4 = r4 / r9
            int r5 = r21.getIntrinsicWidth()
            int r5 = r5 + r3
            int r6 = r21.getIntrinsicHeight()
            int r6 = r6 + r4
            r7 = r21
            r7.setBounds(r3, r4, r5, r6)
            r7.draw(r11)
        L_0x030f:
            if (r24 == 0) goto L_0x0335
            int r3 = r10.getWidth()
            int r4 = r24.getIntrinsicWidth()
            int r3 = r3 - r4
            int r3 = r3 + -10
            int r4 = r24.getIntrinsicHeight()
            int r4 = 120 - r4
            int r4 = r4 / r9
            int r5 = r24.getIntrinsicWidth()
            int r5 = r5 + r3
            int r6 = r24.getIntrinsicHeight()
            int r6 = r6 + r4
            r7 = r24
            r7.setBounds(r3, r4, r5, r6)
            r7.draw(r11)
        L_0x0335:
            r3 = 1
            r4 = r12[r3]
            r5 = 216(0xd8, float:3.03E-43)
            int r6 = r10.getWidth()
            r7 = 20
            int r6 = r6 - r7
            r8 = 308(0x134, float:4.32E-43)
            r1 = 161(0xa1, float:2.26E-43)
            r4.setBounds(r1, r5, r6, r8)
            r4 = r12[r3]
            r5 = 522(0x20a, float:7.31E-43)
            r4.setTop(r15, r5, r15, r15)
            r4 = r12[r3]
            r4.draw(r11)
            r4 = r12[r3]
            int r6 = r10.getWidth()
            int r6 = r6 - r7
            r8 = 430(0x1ae, float:6.03E-43)
            r4.setBounds(r1, r8, r6, r5)
            r1 = r12[r3]
            r1.setTop(r8, r5, r15, r15)
            r1 = r12[r3]
            r1.draw(r11)
            r1 = r12[r15]
            r3 = 399(0x18f, float:5.59E-43)
            r4 = 415(0x19f, float:5.82E-43)
            r6 = 323(0x143, float:4.53E-43)
            r1.setBounds(r7, r6, r3, r4)
            r1 = r12[r15]
            r1.setTop(r6, r5, r15, r15)
            r1 = r12[r15]
            r1.draw(r11)
            if (r0 == 0) goto L_0x03a3
            int r1 = r10.getWidth()
            int r1 = r1 + -126
            int r1 = r1 / r9
            r3 = 150(0x96, float:2.1E-43)
            float r4 = (float) r1
            float r3 = (float) r3
            int r1 = r1 + 126
            float r1 = (float) r1
            r5 = 192(0xc0, float:2.69E-43)
            float r5 = (float) r5
            r6 = r20
            r6.set(r4, r3, r1, r5)
            int r0 = r0.intValue()
            r2.setColor(r0)
            r0 = 1101529088(0x41a80000, float:21.0)
            r11.drawRoundRect(r6, r0, r0, r2)
        L_0x03a3:
            r0 = r18
            r2.setColor(r0)
            r4 = 0
            int r0 = r10.getHeight()
            r1 = 120(0x78, float:1.68E-43)
            int r0 = r0 - r1
            float r5 = (float) r0
            int r0 = r10.getWidth()
            float r6 = (float) r0
            int r0 = r10.getHeight()
            float r7 = (float) r0
            r3 = r11
            r8 = r2
            r3.drawRect(r4, r5, r6, r7, r8)
            r0 = 22
            if (r13 == 0) goto L_0x03e3
            int r1 = r10.getHeight()
            r2 = 120(0x78, float:1.68E-43)
            int r1 = r1 - r2
            int r3 = r13.getIntrinsicHeight()
            int r3 = 120 - r3
            int r3 = r3 / r9
            int r1 = r1 + r3
            int r2 = r13.getIntrinsicWidth()
            int r2 = r2 + r0
            int r3 = r13.getIntrinsicHeight()
            int r3 = r3 + r1
            r13.setBounds(r0, r1, r2, r3)
            r13.draw(r11)
        L_0x03e3:
            if (r14 == 0) goto L_0x040d
            int r1 = r10.getWidth()
            int r2 = r14.getIntrinsicWidth()
            int r1 = r1 - r2
            int r1 = r1 - r0
            int r0 = r10.getHeight()
            r2 = 120(0x78, float:1.68E-43)
            int r0 = r0 - r2
            int r3 = r14.getIntrinsicHeight()
            int r2 = r2 - r3
            int r2 = r2 / r9
            int r0 = r0 + r2
            int r2 = r14.getIntrinsicWidth()
            int r2 = r2 + r1
            int r3 = r14.getIntrinsicHeight()
            int r3 = r3 + r0
            r14.setBounds(r1, r0, r2, r3)
            r14.draw(r11)
        L_0x040d:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ThemePreviewDrawable.createPreview(java.io.File, org.telegram.messenger.DocumentObject$ThemeDocument):android.graphics.Bitmap");
    }
}
