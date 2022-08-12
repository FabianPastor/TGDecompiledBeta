package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.ActionBar.Theme;

public class ThemePreviewDrawable extends BitmapDrawable {
    public ThemePreviewDrawable(File file, DocumentObject.ThemeDocument themeDocument) {
        super(createPreview(file, themeDocument));
    }

    private static Bitmap createPreview(File file, DocumentObject.ThemeDocument themeDocument) {
        boolean z;
        int i;
        MotionBackgroundDrawable motionBackgroundDrawable;
        Drawable drawable;
        boolean z2;
        Bitmap bitmap;
        Bitmap decodeFile;
        File file2 = file;
        DocumentObject.ThemeDocument themeDocument2 = themeDocument;
        new RectF();
        Paint paint = new Paint();
        Bitmap createBitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        HashMap<String, Integer> themeFileValues = Theme.getThemeFileValues((File) null, themeDocument2.baseTheme.assetName, (String[]) null);
        final HashMap hashMap = new HashMap(themeFileValues);
        themeDocument2.accent.fillAccentColors(themeFileValues, hashMap);
        int previewColor = Theme.getPreviewColor(hashMap, "actionBarDefault");
        int previewColor2 = Theme.getPreviewColor(hashMap, "actionBarDefaultIcon");
        int previewColor3 = Theme.getPreviewColor(hashMap, "chat_messagePanelBackground");
        int previewColor4 = Theme.getPreviewColor(hashMap, "chat_messagePanelIcons");
        int previewColor5 = Theme.getPreviewColor(hashMap, "chat_inBubble");
        int previewColor6 = Theme.getPreviewColor(hashMap, "chat_outBubble");
        Integer num = (Integer) hashMap.get("chat_wallpaper");
        Integer num2 = (Integer) hashMap.get("chat_wallpaper_gradient_to");
        Integer num3 = (Integer) hashMap.get("key_chat_wallpaper_gradient_to2");
        Integer num4 = (Integer) hashMap.get("key_chat_wallpaper_gradient_to3");
        int i2 = previewColor5;
        Integer num5 = (Integer) hashMap.get("chat_wallpaper_gradient_rotation");
        if (num5 == null) {
            num5 = 45;
        }
        int i3 = previewColor6;
        int i4 = previewColor3;
        Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_back).mutate();
        Theme.setDrawableColor(mutate, previewColor2);
        Drawable drawable2 = mutate;
        Drawable mutate2 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_dots).mutate();
        Theme.setDrawableColor(mutate2, previewColor2);
        Drawable mutate3 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_smile).mutate();
        Theme.setDrawableColor(mutate3, previewColor4);
        Drawable drawable3 = mutate3;
        Drawable mutate4 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_mic).mutate();
        Theme.setDrawableColor(mutate4, previewColor4);
        Theme.MessageDrawable[] messageDrawableArr = new Theme.MessageDrawable[2];
        Drawable drawable4 = mutate4;
        int i5 = 0;
        while (i5 < 2) {
            Drawable drawable5 = mutate2;
            boolean z3 = true;
            Paint paint2 = paint;
            int i6 = previewColor;
            if (i5 != 1) {
                z3 = false;
            }
            messageDrawableArr[i5] = new Theme.MessageDrawable(2, z3, false) {
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
            Theme.setDrawableColor(messageDrawableArr[i5], i5 == 1 ? i3 : i2);
            i5++;
            mutate2 = drawable5;
            paint = paint2;
            previewColor = i6;
        }
        Paint paint3 = paint;
        int i7 = previewColor;
        Drawable drawable6 = mutate2;
        if (num != null) {
            if (num2 == null) {
                drawable = new ColorDrawable(num.intValue());
                i = AndroidUtilities.getPatternColor(num.intValue());
                motionBackgroundDrawable = null;
            } else {
                if (num3.intValue() != 0) {
                    motionBackgroundDrawable = new MotionBackgroundDrawable(num.intValue(), num2.intValue(), num3.intValue(), num4.intValue(), true);
                    drawable = null;
                } else {
                    drawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(num5.intValue(), new int[]{num.intValue(), num2.intValue()}, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                    motionBackgroundDrawable = null;
                }
                i = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(num.intValue(), num2.intValue()));
            }
            if (drawable != null) {
                z2 = false;
                drawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                drawable.draw(canvas);
            } else {
                z2 = false;
            }
            if (file2 != null) {
                if ("application/x-tgwallpattern".equals(themeDocument2.mime_type)) {
                    decodeFile = SvgHelper.getBitmap(file2, 560, 678, z2);
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
                        int i8 = 1;
                        do {
                            i8 *= 2;
                        } while (((float) (i8 * 2)) < min);
                        options.inSampleSize = i8;
                    }
                    decodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                }
                bitmap = decodeFile;
                if (bitmap != null) {
                    if (motionBackgroundDrawable != null) {
                        motionBackgroundDrawable.setPatternBitmap((int) (themeDocument2.accent.patternIntensity * 100.0f), bitmap);
                        motionBackgroundDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                        motionBackgroundDrawable.draw(canvas);
                    } else {
                        Paint paint4 = new Paint(2);
                        if (themeDocument2.accent.patternIntensity >= 0.0f) {
                            paint4.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                        }
                        paint4.setAlpha(255);
                        float max = Math.max(((float) 560) / ((float) bitmap.getWidth()), ((float) 678) / ((float) bitmap.getHeight()));
                        canvas.save();
                        canvas.translate((float) ((560 - ((int) (((float) bitmap.getWidth()) * max))) / 2), (float) ((678 - ((int) (((float) bitmap.getHeight()) * max))) / 2));
                        canvas.scale(max, max);
                        canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint4);
                        canvas.restore();
                    }
                }
            } else {
                bitmap = null;
            }
            if (bitmap == null && motionBackgroundDrawable != null) {
                motionBackgroundDrawable.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
                motionBackgroundDrawable.draw(canvas);
            }
            z = true;
        } else {
            z = false;
        }
        if (!z) {
            Drawable createDefaultWallpaper = Theme.createDefaultWallpaper(createBitmap.getWidth(), createBitmap.getHeight() - 120);
            createDefaultWallpaper.setBounds(0, 120, createBitmap.getWidth(), createBitmap.getHeight() - 120);
            createDefaultWallpaper.draw(canvas);
        }
        Paint paint5 = paint3;
        paint5.setColor(i7);
        Theme.MessageDrawable[] messageDrawableArr2 = messageDrawableArr;
        canvas.drawRect(0.0f, 0.0f, (float) createBitmap.getWidth(), 120.0f, paint5);
        if (drawable2 != null) {
            int intrinsicHeight = (120 - drawable2.getIntrinsicHeight()) / 2;
            Drawable drawable7 = drawable2;
            drawable7.setBounds(13, intrinsicHeight, drawable2.getIntrinsicWidth() + 13, drawable2.getIntrinsicHeight() + intrinsicHeight);
            drawable7.draw(canvas);
        }
        if (drawable6 != null) {
            int width = (createBitmap.getWidth() - drawable6.getIntrinsicWidth()) - 10;
            int intrinsicHeight2 = (120 - drawable6.getIntrinsicHeight()) / 2;
            Drawable drawable8 = drawable6;
            drawable8.setBounds(width, intrinsicHeight2, drawable6.getIntrinsicWidth() + width, drawable6.getIntrinsicHeight() + intrinsicHeight2);
            drawable8.draw(canvas);
        }
        messageDrawableArr2[1].setBounds(161, 216, createBitmap.getWidth() - 20, 308);
        messageDrawableArr2[1].setTop(0, 560, 522, false, false);
        messageDrawableArr2[1].draw(canvas);
        messageDrawableArr2[1].setBounds(161, 430, createBitmap.getWidth() - 20, 522);
        messageDrawableArr2[1].setTop(430, 560, 522, false, false);
        messageDrawableArr2[1].draw(canvas);
        messageDrawableArr2[0].setBounds(20, 323, 399, 415);
        messageDrawableArr2[0].setTop(323, 560, 522, false, false);
        messageDrawableArr2[0].draw(canvas);
        paint5.setColor(i4);
        canvas.drawRect(0.0f, (float) (createBitmap.getHeight() - 120), (float) createBitmap.getWidth(), (float) createBitmap.getHeight(), paint5);
        if (drawable3 != null) {
            int height = (createBitmap.getHeight() - 120) + ((120 - drawable3.getIntrinsicHeight()) / 2);
            Drawable drawable9 = drawable3;
            drawable9.setBounds(22, height, drawable3.getIntrinsicWidth() + 22, drawable3.getIntrinsicHeight() + height);
            drawable9.draw(canvas);
        }
        if (drawable4 != null) {
            int width2 = (createBitmap.getWidth() - drawable4.getIntrinsicWidth()) - 22;
            int height2 = (createBitmap.getHeight() - 120) + ((120 - drawable4.getIntrinsicHeight()) / 2);
            Drawable drawable10 = drawable4;
            drawable10.setBounds(width2, height2, drawable4.getIntrinsicWidth() + width2, drawable4.getIntrinsicHeight() + height2);
            drawable10.draw(canvas);
        }
        return createBitmap;
    }
}
