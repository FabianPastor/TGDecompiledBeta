package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DocumentObject;
import org.telegram.ui.ActionBar.Theme;

public class ThemePreviewDrawable extends BitmapDrawable {
    private DocumentObject.ThemeDocument themeDocument;

    public ThemePreviewDrawable(File file, DocumentObject.ThemeDocument themeDocument2) {
        super(createPreview(file, themeDocument2));
        this.themeDocument = themeDocument2;
    }

    public DocumentObject.ThemeDocument getThemeDocument() {
        return this.themeDocument;
    }

    /* JADX WARNING: type inference failed for: r12v5, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r12v6 */
    /* JADX WARNING: type inference failed for: r12v21 */
    private static Bitmap createPreview(File file, DocumentObject.ThemeDocument themeDocument2) {
        boolean z;
        ? r12;
        int i;
        int i2;
        Drawable drawable;
        Bitmap bitmap;
        File file2 = file;
        DocumentObject.ThemeDocument themeDocument3 = themeDocument2;
        RectF rectF = new RectF();
        Paint paint = new Paint();
        Bitmap createBitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        HashMap<String, Integer> themeFileValues = Theme.getThemeFileValues((File) null, themeDocument3.baseTheme.assetName, (String[]) null);
        final HashMap hashMap = new HashMap(themeFileValues);
        themeDocument3.accent.fillAccentColors(themeFileValues, hashMap);
        int previewColor = Theme.getPreviewColor(hashMap, "actionBarDefault");
        int previewColor2 = Theme.getPreviewColor(hashMap, "actionBarDefaultIcon");
        int previewColor3 = Theme.getPreviewColor(hashMap, "chat_messagePanelBackground");
        int previewColor4 = Theme.getPreviewColor(hashMap, "chat_messagePanelIcons");
        int previewColor5 = Theme.getPreviewColor(hashMap, "chat_inBubble");
        int previewColor6 = Theme.getPreviewColor(hashMap, "chat_outBubble");
        Integer num = (Integer) hashMap.get("chat_outBubbleGradient");
        Integer num2 = (Integer) hashMap.get("chat_wallpaper");
        Integer num3 = (Integer) hashMap.get("chat_serviceBackground");
        Integer num4 = (Integer) hashMap.get("chat_wallpaper_gradient_to");
        int i3 = previewColor5;
        Integer num5 = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
        if (num5 == null) {
            num5 = 45;
        }
        int i4 = previewColor6;
        int i5 = previewColor3;
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        RectF rectF2 = rectF;
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate2, previewColor2);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable drawable2 = mutate3;
        Drawable mutate4 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate4, previewColor4);
        Theme.MessageDrawable[] messageDrawableArr = new Theme.MessageDrawable[2];
        Drawable drawable3 = mutate4;
        int i6 = 0;
        while (i6 < 2) {
            Drawable drawable4 = mutate2;
            boolean z2 = true;
            int i7 = previewColor;
            Drawable drawable5 = mutate;
            if (i6 != 1) {
                z2 = false;
            }
            messageDrawableArr[i6] = new Theme.MessageDrawable(2, z2, false) {
                /* access modifiers changed from: protected */
                public int getColor(String str) {
                    Integer num = (Integer) hashMap.get(str);
                    if (num == null) {
                        return Theme.getColor(str);
                    }
                    return num.intValue();
                }

                /* access modifiers changed from: protected */
                public Integer getCurrentColor(String str) {
                    return (Integer) hashMap.get(str);
                }
            };
            Theme.setDrawableColor(messageDrawableArr[i6], i6 == 1 ? i4 : i3);
            i6++;
            mutate2 = drawable4;
            mutate = drawable5;
            previewColor = i7;
        }
        Drawable drawable6 = mutate2;
        int i8 = previewColor;
        Drawable drawable7 = mutate;
        if (num2 != null) {
            if (num4 == null) {
                drawable = new ColorDrawable(num2.intValue());
                i2 = AndroidUtilities.getPatternColor(num2.intValue());
            } else {
                drawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(num5.intValue(), new int[]{num2.intValue(), num4.intValue()}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                i2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(num2.intValue(), num4.intValue()));
            }
            drawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            drawable.draw(canvas);
            if (num3 == null) {
                num3 = Integer.valueOf(AndroidUtilities.calcDrawableColor(new ColorDrawable(num2.intValue()))[0]);
            }
            if (file2 != null) {
                if ("application/x-tgwallpattern".equals(themeDocument3.mime_type)) {
                    bitmap = SvgHelper.getBitmap(file2, 560, 678, false);
                } else {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                    float f = (float) options.outWidth;
                    float f2 = (float) options.outHeight;
                    float min = Math.min(f / ((float) 560), f2 / ((float) 678));
                    if (min < 1.2f) {
                        min = 1.0f;
                    }
                    options.inJustDecodeBounds = false;
                    if (min <= 1.0f || (f <= ((float) 560) && f2 <= ((float) 678))) {
                        options.inSampleSize = (int) min;
                    } else {
                        int i9 = 1;
                        do {
                            i9 *= 2;
                        } while (((float) (i9 * 2)) < min);
                        options.inSampleSize = i9;
                    }
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                if (bitmap != null) {
                    Paint paint2 = new Paint(2);
                    paint2.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.SRC_IN));
                    paint2.setAlpha((int) (themeDocument3.accent.patternIntensity * 255.0f));
                    float max = Math.max(560.0f / ((float) bitmap.getWidth()), 678.0f / ((float) bitmap.getHeight()));
                    canvas.save();
                    canvas.translate((float) ((560 - ((int) (((float) bitmap.getWidth()) * max))) / 2), (float) ((678 - ((int) (((float) bitmap.getHeight()) * max))) / 2));
                    canvas.scale(max, max);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
                    canvas.restore();
                    z = true;
                }
            }
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
            if (num3 == null) {
                i = 0;
                num3 = Integer.valueOf(AndroidUtilities.calcDrawableColor(bitmapDrawable)[0]);
            } else {
                i = 0;
            }
            Shader.TileMode tileMode = Shader.TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            bitmapDrawable.setBounds(i, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            bitmapDrawable.draw(canvas);
            r12 = i;
        } else {
            r12 = 0;
        }
        Integer num6 = num3;
        paint.setColor(i8);
        Theme.MessageDrawable[] messageDrawableArr2 = messageDrawableArr;
        canvas.drawRect(0.0f, 0.0f, (float) createBitmap.getWidth(), 120.0f, paint);
        if (drawable7 != null) {
            int intrinsicHeight = (120 - drawable7.getIntrinsicHeight()) / 2;
            Drawable drawable8 = drawable7;
            drawable8.setBounds(13, intrinsicHeight, drawable7.getIntrinsicWidth() + 13, drawable7.getIntrinsicHeight() + intrinsicHeight);
            drawable8.draw(canvas);
        }
        if (drawable6 != null) {
            int width = (createBitmap.getWidth() - drawable6.getIntrinsicWidth()) - 10;
            int intrinsicHeight2 = (120 - drawable6.getIntrinsicHeight()) / 2;
            Drawable drawable9 = drawable6;
            drawable9.setBounds(width, intrinsicHeight2, drawable6.getIntrinsicWidth() + width, drawable6.getIntrinsicHeight() + intrinsicHeight2);
            drawable9.draw(canvas);
        }
        messageDrawableArr2[1].setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        messageDrawableArr2[1].setTop(r12, 522, r12, r12);
        messageDrawableArr2[1].draw(canvas);
        messageDrawableArr2[1].setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        messageDrawableArr2[1].setTop(430, 522, r12, r12);
        messageDrawableArr2[1].draw(canvas);
        messageDrawableArr2[r12].setBounds(20, 323, 399, 415);
        messageDrawableArr2[r12].setTop(323, 522, r12, r12);
        messageDrawableArr2[r12].draw(canvas);
        if (num6 != null) {
            int width2 = (createBitmap.getWidth() - 126) / 2;
            RectF rectF3 = rectF2;
            rectF3.set((float) width2, (float) 150, (float) (width2 + 126), (float) 192);
            paint.setColor(num6.intValue());
            canvas.drawRoundRect(rectF3, 21.0f, 21.0f, paint);
        }
        paint.setColor(i5);
        canvas.drawRect(0.0f, (float) (createBitmap.getHeight() - 120), (float) createBitmap.getWidth(), (float) createBitmap.getHeight(), paint);
        if (drawable2 != null) {
            int height = (createBitmap.getHeight() - 120) + ((120 - drawable2.getIntrinsicHeight()) / 2);
            Drawable drawable10 = drawable2;
            drawable10.setBounds(22, height, drawable2.getIntrinsicWidth() + 22, drawable2.getIntrinsicHeight() + height);
            drawable10.draw(canvas);
        }
        if (drawable3 != null) {
            int width3 = (createBitmap.getWidth() - drawable3.getIntrinsicWidth()) - 22;
            int height2 = (createBitmap.getHeight() - 120) + ((120 - drawable3.getIntrinsicHeight()) / 2);
            Drawable drawable11 = drawable3;
            drawable11.setBounds(width3, height2, drawable3.getIntrinsicWidth() + width3, drawable3.getIntrinsicHeight() + height2);
            drawable11.draw(canvas);
        }
        return createBitmap;
    }
}
