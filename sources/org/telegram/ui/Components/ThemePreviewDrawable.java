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
        Object obj;
        int intrinsicHeight;
        int width;
        int height;
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
        int i = previewColor3;
        Integer num4 = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
        if (num4 == null) {
            num4 = Integer.valueOf(45);
        }
        RectF rectF2 = rectF;
        int i2 = previewColor;
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        Drawable drawable = mutate;
        mutate = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate2, previewColor4);
        Drawable drawable2 = mutate2;
        mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate2, previewColor4);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(NUM).mutate();
        Theme.setDrawableColor(mutate3, previewColor5);
        Drawable drawable3 = mutate2;
        AnonymousClass1 anonymousClass1 = new MessageDrawable(2, false) {
            /* Access modifiers changed, original: protected */
            public int getColor(String str) {
                return ((Integer) hashMap.get(str)).intValue();
            }

            /* Access modifiers changed, original: protected */
            public Integer getCurrentColor(String str) {
                return (Integer) hashMap.get(str);
            }
        };
        Theme.setDrawableColor(anonymousClass1, previewColor6);
        if (num != null) {
            Drawable drawable4;
            int i3;
            if (num3 == null) {
                Drawable colorDrawable = new ColorDrawable(num.intValue());
                previewColor3 = AndroidUtilities.getPatternColor(num.intValue());
                drawable4 = colorDrawable;
                i3 = 120;
            } else {
                i3 = 120;
                drawable4 = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(num4.intValue(), new int[]{num.intValue(), num3.intValue()}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                previewColor3 = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(num.intValue(), num3.intValue()));
            }
            drawable4.setBounds(0, i3, createBitmap.getWidth(), createBitmap.getHeight() - i3);
            drawable4.draw(canvas);
            if (num2 == null) {
                num2 = Integer.valueOf(AndroidUtilities.calcDrawableColor(new ColorDrawable(num.intValue()))[0]);
            }
            if (file2 != null) {
                Bitmap bitmap;
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
                        i3 = 1;
                        do {
                            i3 *= 2;
                        } while (((float) (i3 * 2)) < min);
                        options.inSampleSize = i3;
                    }
                    bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                if (bitmap != null) {
                    Paint paint2 = new Paint(2);
                    paint2.setColorFilter(new PorterDuffColorFilter(previewColor3, Mode.SRC_IN));
                    paint2.setAlpha((int) (themeDocument2.accent.patternIntensity * 255.0f));
                    float max = Math.max(560.0f / ((float) bitmap.getWidth()), 678.0f / ((float) bitmap.getHeight()));
                    int width2 = (560 - ((int) (((float) bitmap.getWidth()) * max))) / 2;
                    i3 = (678 - ((int) (((float) bitmap.getHeight()) * max))) / 2;
                    canvas.save();
                    canvas.translate((float) width2, (float) i3);
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
                previewColor = 0;
                num2 = Integer.valueOf(AndroidUtilities.calcDrawableColor(bitmapDrawable)[0]);
            } else {
                previewColor = 0;
            }
            TileMode tileMode = TileMode.REPEAT;
            bitmapDrawable.setTileModeXY(tileMode, tileMode);
            bitmapDrawable.setBounds(previewColor, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            bitmapDrawable.draw(canvas);
        }
        Integer num5 = num2;
        paint.setColor(i2);
        Drawable drawable5 = drawable2;
        Drawable drawable6 = drawable3;
        Drawable drawable7 = mutate3;
        canvas.drawRect(0.0f, 0.0f, (float) createBitmap.getWidth(), 120.0f, paint);
        if (drawable != null) {
            intrinsicHeight = (120 - drawable.getIntrinsicHeight()) / 2;
            mutate2 = drawable;
            mutate2.setBounds(13, intrinsicHeight, drawable.getIntrinsicWidth() + 13, drawable.getIntrinsicHeight() + intrinsicHeight);
            mutate2.draw(canvas);
        }
        if (mutate != null) {
            previewColor = (createBitmap.getWidth() - mutate.getIntrinsicWidth()) - 10;
            intrinsicHeight = (120 - mutate.getIntrinsicHeight()) / 2;
            mutate.setBounds(previewColor, intrinsicHeight, mutate.getIntrinsicWidth() + previewColor, mutate.getIntrinsicHeight() + intrinsicHeight);
            mutate.draw(canvas);
        }
        anonymousClass1.setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        anonymousClass1.setTop(0, 522);
        anonymousClass1.draw(canvas);
        anonymousClass1.setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        anonymousClass1.setTop(430, 522);
        anonymousClass1.draw(canvas);
        if (drawable7 != null) {
            drawable7.setBounds(20, 323, 399, 415);
            drawable7.draw(canvas);
        }
        if (num5 != null) {
            width = (createBitmap.getWidth() - 126) / 2;
            RectF rectF3 = rectF2;
            rectF3.set((float) width, (float) 150, (float) (width + 126), (float) 192);
            paint.setColor(num5.intValue());
            canvas.drawRoundRect(rectF3, 21.0f, 21.0f, paint);
        }
        paint.setColor(i);
        canvas.drawRect(0.0f, (float) (createBitmap.getHeight() - 120), (float) createBitmap.getWidth(), (float) createBitmap.getHeight(), paint);
        if (drawable5 != null) {
            height = (createBitmap.getHeight() - 120) + ((120 - drawable5.getIntrinsicHeight()) / 2);
            drawable5.setBounds(22, height, drawable5.getIntrinsicWidth() + 22, drawable5.getIntrinsicHeight() + height);
            drawable5.draw(canvas);
        }
        if (drawable6 != null) {
            height = (createBitmap.getWidth() - drawable6.getIntrinsicWidth()) - 22;
            width = (createBitmap.getHeight() - 120) + ((120 - drawable6.getIntrinsicHeight()) / 2);
            drawable6.setBounds(height, width, drawable6.getIntrinsicWidth() + height, drawable6.getIntrinsicHeight() + width);
            drawable6.draw(canvas);
        }
        return createBitmap;
    }
}
