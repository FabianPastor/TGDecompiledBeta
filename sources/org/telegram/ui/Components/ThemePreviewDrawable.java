package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DocumentObject.ThemeDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.MessageDrawable;

public class ThemePreviewDrawable extends BitmapDrawable {
    private ThemeDocument themeDocument;

    public ThemePreviewDrawable(File file, ThemeDocument themeDocument) {
        super(createPreview(file, themeDocument));
        this.themeDocument = themeDocument;
    }

    public ThemeDocument getThemeDocument() {
        return this.themeDocument;
    }

    private static Bitmap createPreview(File file, ThemeDocument themeDocument) {
        Drawable drawable;
        int i;
        Drawable drawable2;
        Object obj;
        int intrinsicHeight;
        Drawable drawable3;
        File file2 = file;
        ThemeDocument themeDocument2 = themeDocument;
        RectF rectF = new RectF();
        Paint paint = new Paint();
        Bitmap createBitmap = Bitmaps.createBitmap(560, 678, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        HashMap themeFileValues = Theme.getThemeFileValues(null, themeDocument2.baseTheme.assetName, null);
        final HashMap hashMap = new HashMap(themeFileValues);
        themeDocument2.accent.fillAccentColors(themeFileValues, hashMap);
        int previewColor = Theme.getPreviewColor(hashMap, "actionBarDefault");
        int previewColor2 = Theme.getPreviewColor(hashMap, "actionBarDefaultIcon");
        int previewColor3 = Theme.getPreviewColor(hashMap, "chat_messagePanelBackground");
        int previewColor4 = Theme.getPreviewColor(hashMap, "chat_messagePanelIcons");
        int previewColor5 = Theme.getPreviewColor(hashMap, "chat_inBubble");
        int previewColor6 = Theme.getPreviewColor(hashMap, "chat_outBubble");
        Integer num = (Integer) hashMap.get("chat_outBubbleGradient");
        num = (Integer) hashMap.get("chat_wallpaper");
        Integer num2 = (Integer) hashMap.get("chat_serviceBackground");
        Integer num3 = (Integer) hashMap.get("chat_wallpaper_gradient_to");
        int i2 = previewColor5;
        Integer num4 = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
        if (num4 == null) {
            num4 = Integer.valueOf(45);
        }
        int i3 = previewColor6;
        int i4 = previewColor3;
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        RectF rectF2 = rectF;
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate2, previewColor2);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable drawable4 = mutate3;
        mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable[] drawableArr = new MessageDrawable[2];
        Drawable drawable5 = mutate3;
        previewColor4 = 0;
        while (previewColor4 < 2) {
            drawable = mutate2;
            boolean z = true;
            i = previewColor;
            drawable2 = mutate;
            if (previewColor4 != 1) {
                z = false;
            }
            drawableArr[previewColor4] = new MessageDrawable(2, z, false) {
                /* Access modifiers changed, original: protected */
                public int getColor(String str) {
                    Integer num = (Integer) hashMap.get(str);
                    if (num == null) {
                        return Theme.getColor(str);
                    }
                    return num.intValue();
                }

                /* Access modifiers changed, original: protected */
                public Integer getCurrentColor(String str) {
                    return (Integer) hashMap.get(str);
                }
            };
            Theme.setDrawableColor(drawableArr[previewColor4], previewColor4 == 1 ? i3 : i2);
            previewColor4++;
            mutate2 = drawable;
            mutate = drawable2;
            previewColor = i;
        }
        drawable = mutate2;
        i = previewColor;
        drawable2 = mutate;
        if (num != null) {
            Drawable colorDrawable;
            int patternColor;
            if (num3 == null) {
                colorDrawable = new ColorDrawable(num.intValue());
                patternColor = AndroidUtilities.getPatternColor(num.intValue());
            } else {
                colorDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(num4.intValue(), new int[]{num.intValue(), num3.intValue()}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(num.intValue(), num3.intValue()));
            }
            colorDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            colorDrawable.draw(canvas);
            if (num2 == null) {
                num2 = Integer.valueOf(AndroidUtilities.calcDrawableColor(new ColorDrawable(num.intValue()))[0]);
            }
            if (file2 != null) {
                Bitmap bitmap;
                int i5;
                if ("application/x-tgwallpattern".equals(themeDocument2.mime_type)) {
                    bitmap = SvgHelper.getBitmap(file2, 560, 678, false);
                } else {
                    Options options = new Options();
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
                        i5 = 1;
                        do {
                            i5 *= 2;
                        } while (((float) (i5 * 2)) < min);
                        options.inSampleSize = i5;
                    }
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                if (bitmap != null) {
                    Paint paint2 = new Paint(2);
                    paint2.setColorFilter(new PorterDuffColorFilter(patternColor, Mode.SRC_IN));
                    paint2.setAlpha((int) (themeDocument2.accent.patternIntensity * 255.0f));
                    float max = Math.max(560.0f / ((float) bitmap.getWidth()), 678.0f / ((float) bitmap.getHeight()));
                    patternColor = (560 - ((int) (((float) bitmap.getWidth()) * max))) / 2;
                    i5 = (678 - ((int) (((float) bitmap.getHeight()) * max))) / 2;
                    canvas.save();
                    canvas.translate((float) patternColor, (float) i5);
                    canvas.scale(max, max);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint2);
                    canvas.restore();
                    obj = 1;
                }
            }
            obj = 1;
        } else {
            obj = null;
        }
        if (obj == null) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
            if (num2 == null) {
                previewColor3 = 0;
                num2 = Integer.valueOf(AndroidUtilities.calcDrawableColor(bitmapDrawable)[0]);
            } else {
                previewColor3 = 0;
            }
            TileMode tileMode = TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            bitmapDrawable.setBounds(previewColor3, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            bitmapDrawable.draw(canvas);
        } else {
            previewColor3 = 0;
        }
        Integer num5 = num2;
        paint.setColor(i);
        Drawable[] drawableArr2 = drawableArr;
        canvas.drawRect(0.0f, 0.0f, (float) createBitmap.getWidth(), 120.0f, paint);
        if (drawable2 != null) {
            intrinsicHeight = (120 - drawable2.getIntrinsicHeight()) / 2;
            drawable3 = drawable2;
            drawable3.setBounds(13, intrinsicHeight, drawable2.getIntrinsicWidth() + 13, drawable2.getIntrinsicHeight() + intrinsicHeight);
            drawable3.draw(canvas);
        }
        if (drawable != null) {
            previewColor = (createBitmap.getWidth() - drawable.getIntrinsicWidth()) - 10;
            intrinsicHeight = (120 - drawable.getIntrinsicHeight()) / 2;
            drawable3 = drawable;
            drawable3.setBounds(previewColor, intrinsicHeight, drawable.getIntrinsicWidth() + previewColor, drawable.getIntrinsicHeight() + intrinsicHeight);
            drawable3.draw(canvas);
        }
        drawableArr2[1].setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        drawableArr2[1].setTop(previewColor3, 522, previewColor3, previewColor3);
        drawableArr2[1].draw(canvas);
        drawableArr2[1].setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        drawableArr2[1].setTop(430, 522, previewColor3, previewColor3);
        drawableArr2[1].draw(canvas);
        drawableArr2[previewColor3].setBounds(20, 323, 399, 415);
        drawableArr2[previewColor3].setTop(323, 522, previewColor3, previewColor3);
        drawableArr2[previewColor3].draw(canvas);
        if (num5 != null) {
            previewColor = (createBitmap.getWidth() - 126) / 2;
            RectF rectF3 = rectF2;
            rectF3.set((float) previewColor, (float) 150, (float) (previewColor + 126), (float) 192);
            paint.setColor(num5.intValue());
            canvas.drawRoundRect(rectF3, 21.0f, 21.0f, paint);
        }
        paint.setColor(i4);
        canvas.drawRect(0.0f, (float) (createBitmap.getHeight() - 120), (float) createBitmap.getWidth(), (float) createBitmap.getHeight(), paint);
        if (drawable4 != null) {
            previewColor = (createBitmap.getHeight() - 120) + ((120 - drawable4.getIntrinsicHeight()) / 2);
            Drawable drawable6 = drawable4;
            drawable6.setBounds(22, previewColor, drawable4.getIntrinsicWidth() + 22, drawable4.getIntrinsicHeight() + previewColor);
            drawable6.draw(canvas);
        }
        if (drawable5 != null) {
            previewColor = (createBitmap.getWidth() - drawable5.getIntrinsicWidth()) - 22;
            int height = (createBitmap.getHeight() - 120) + ((120 - drawable5.getIntrinsicHeight()) / 2);
            Drawable drawable7 = drawable5;
            drawable7.setBounds(previewColor, height, drawable5.getIntrinsicWidth() + previewColor, drawable5.getIntrinsicHeight() + height);
            drawable7.draw(canvas);
        }
        return createBitmap;
    }
}
