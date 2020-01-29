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

    private static Bitmap createPreview(File file, DocumentObject.ThemeDocument themeDocument2) {
        boolean z;
        int i;
        int i2;
        int i3;
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
        int i4 = previewColor3;
        Integer num5 = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
        if (num5 == null) {
            num5 = 45;
        }
        RectF rectF2 = rectF;
        int i5 = previewColor;
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        Drawable drawable2 = mutate;
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate2, previewColor2);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable drawable3 = mutate3;
        Drawable mutate4 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate4, previewColor4);
        Drawable mutate5 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate5, previewColor5);
        Drawable drawable4 = mutate4;
        AnonymousClass1 r13 = new Theme.MessageDrawable(2, false) {
            /* access modifiers changed from: protected */
            public int getColor(String str) {
                return ((Integer) hashMap.get(str)).intValue();
            }

            /* access modifiers changed from: protected */
            public Integer getCurrentColor(String str) {
                return (Integer) hashMap.get(str);
            }
        };
        Theme.setDrawableColor(r13, previewColor6);
        if (num2 != null) {
            if (num4 == null) {
                ColorDrawable colorDrawable = new ColorDrawable(num2.intValue());
                i2 = AndroidUtilities.getPatternColor(num2.intValue());
                drawable = colorDrawable;
                i3 = 120;
            } else {
                i3 = 120;
                drawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(num5.intValue(), new int[]{num2.intValue(), num4.intValue()}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                i2 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(num2.intValue(), num4.intValue()));
            }
            drawable.setBounds(0, i3, createBitmap.getWidth(), createBitmap.getHeight() - i3);
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
                        int i6 = 1;
                        do {
                            i6 *= 2;
                        } while (((float) (i6 * 2)) < min);
                        options.inSampleSize = i6;
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
        }
        Integer num6 = num3;
        paint.setColor(i5);
        Drawable drawable5 = drawable3;
        Drawable drawable6 = drawable4;
        Drawable drawable7 = mutate5;
        canvas.drawRect(0.0f, 0.0f, (float) createBitmap.getWidth(), 120.0f, paint);
        if (drawable2 != null) {
            int intrinsicHeight = (120 - drawable2.getIntrinsicHeight()) / 2;
            Drawable drawable8 = drawable2;
            drawable8.setBounds(13, intrinsicHeight, drawable2.getIntrinsicWidth() + 13, drawable2.getIntrinsicHeight() + intrinsicHeight);
            drawable8.draw(canvas);
        }
        if (mutate2 != null) {
            int width = (createBitmap.getWidth() - mutate2.getIntrinsicWidth()) - 10;
            int intrinsicHeight2 = (120 - mutate2.getIntrinsicHeight()) / 2;
            mutate2.setBounds(width, intrinsicHeight2, mutate2.getIntrinsicWidth() + width, mutate2.getIntrinsicHeight() + intrinsicHeight2);
            mutate2.draw(canvas);
        }
        r13.setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        r13.setTop(0, 522);
        r13.draw(canvas);
        r13.setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        r13.setTop(430, 522);
        r13.draw(canvas);
        if (drawable7 != null) {
            drawable7.setBounds(20, 323, 399, 415);
            drawable7.draw(canvas);
        }
        if (num6 != null) {
            int width2 = (createBitmap.getWidth() - 126) / 2;
            RectF rectF3 = rectF2;
            rectF3.set((float) width2, (float) 150, (float) (width2 + 126), (float) 192);
            paint.setColor(num6.intValue());
            canvas.drawRoundRect(rectF3, 21.0f, 21.0f, paint);
        }
        paint.setColor(i4);
        canvas.drawRect(0.0f, (float) (createBitmap.getHeight() - 120), (float) createBitmap.getWidth(), (float) createBitmap.getHeight(), paint);
        if (drawable5 != null) {
            int height = (createBitmap.getHeight() - 120) + ((120 - drawable5.getIntrinsicHeight()) / 2);
            drawable5.setBounds(22, height, drawable5.getIntrinsicWidth() + 22, drawable5.getIntrinsicHeight() + height);
            drawable5.draw(canvas);
        }
        if (drawable6 != null) {
            int width3 = (createBitmap.getWidth() - drawable6.getIntrinsicWidth()) - 22;
            int height2 = (createBitmap.getHeight() - 120) + ((120 - drawable6.getIntrinsicHeight()) / 2);
            drawable6.setBounds(width3, height2, drawable6.getIntrinsicWidth() + width3, drawable6.getIntrinsicHeight() + height2);
            drawable6.draw(canvas);
        }
        return createBitmap;
    }
}
