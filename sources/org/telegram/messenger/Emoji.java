package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class Emoji {
    private static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
    private static final int[][] cols;
    private static int drawImgSize = AndroidUtilities.dp(20.0f);
    private static Bitmap[][] emojiBmp = ((Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{5, 4}));
    public static HashMap<String, String> emojiColor = new HashMap();
    public static HashMap<String, Integer> emojiUseHistory = new HashMap();
    private static boolean inited = false;
    private static boolean[][] loadingEmoji = ((boolean[][]) Array.newInstance(boolean.class, new int[]{5, 4}));
    private static Paint placeholderPaint = new Paint();
    public static ArrayList<String> recentEmoji = new ArrayList();
    private static boolean recentEmojiLoaded = false;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
    private static final int splitCount = 4;

    /* renamed from: org.telegram.messenger.Emoji$2 */
    static class C01482 implements Comparator<String> {
        C01482() {
        }

        public int compare(String str, String str2) {
            str = (Integer) Emoji.emojiUseHistory.get(str);
            str2 = (Integer) Emoji.emojiUseHistory.get(str2);
            if (str == null) {
                str = Integer.valueOf(0);
            }
            if (str2 == null) {
                str2 = Integer.valueOf(0);
            }
            if (str.intValue() > str2.intValue()) {
                return -1;
            }
            if (str.intValue() < str2.intValue()) {
                return 1;
            }
            return 0;
        }
    }

    private static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public byte page2;
        public Rect rect;

        public DrawableInfo(Rect rect, byte b, byte b2, int i) {
            this.rect = rect;
            this.page = b;
            this.page2 = b2;
            this.emojiIndex = i;
        }
    }

    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        private static TextPaint textPaint = new TextPaint(1);
        private boolean fullSize = false;
        private DrawableInfo info;

        /* renamed from: org.telegram.messenger.Emoji$EmojiDrawable$1 */
        class C01491 implements Runnable {
            C01491() {
            }

            public void run() {
                Emoji.loadEmoji(EmojiDrawable.this.info.page, EmojiDrawable.this.info.page2);
                Emoji.loadingEmoji[EmojiDrawable.this.info.page][EmojiDrawable.this.info.page2] = false;
            }
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public EmojiDrawable(DrawableInfo drawableInfo) {
            this.info = drawableInfo;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public Rect getDrawRect() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            rect.left = centerX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = centerX + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.top = centerY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = centerY + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            return rect;
        }

        public void draw(Canvas canvas) {
            if (Emoji.emojiBmp[this.info.page][this.info.page2] != null) {
                Rect drawRect;
                if (this.fullSize) {
                    drawRect = getDrawRect();
                } else {
                    drawRect = getBounds();
                }
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, drawRect, paint);
            } else if (!Emoji.loadingEmoji[this.info.page][this.info.page2]) {
                Emoji.loadingEmoji[this.info.page][this.info.page2] = true;
                Utilities.globalQueue.postRunnable(new C01491());
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
            }
        }
    }

    public static class EmojiSpan extends ImageSpan {
        private FontMetricsInt fontMetrics = null;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(EmojiDrawable emojiDrawable, int i, int i2, FontMetricsInt fontMetricsInt) {
            super(emojiDrawable, i);
            this.fontMetrics = fontMetricsInt;
            if (fontMetricsInt != null) {
                this.size = Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent);
                if (this.size == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(FontMetricsInt fontMetricsInt, int i) {
            this.fontMetrics = fontMetricsInt;
            this.size = i;
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, FontMetricsInt fontMetricsInt) {
            if (fontMetricsInt == null) {
                fontMetricsInt = new FontMetricsInt();
            }
            if (this.fontMetrics == null) {
                paint = super.getSize(paint, charSequence, i, i2, fontMetricsInt);
                charSequence = AndroidUtilities.dp(8.0f);
                i = AndroidUtilities.dp(NUM);
                i2 = (-i) - charSequence;
                fontMetricsInt.top = i2;
                i -= charSequence;
                fontMetricsInt.bottom = i;
                fontMetricsInt.ascent = i2;
                fontMetricsInt.leading = 0;
                fontMetricsInt.descent = i;
                return paint;
            }
            if (fontMetricsInt != null) {
                fontMetricsInt.ascent = this.fontMetrics.ascent;
                fontMetricsInt.descent = this.fontMetrics.descent;
                fontMetricsInt.top = this.fontMetrics.top;
                fontMetricsInt.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, this.size, this.size);
            }
            return this.size;
        }
    }

    public static native Object[] getSuggestion(String str);

    static {
        r0 = new int[5][];
        int i = 2;
        r0[2] = new int[]{9, 9, 9, 9};
        r0[3] = new int[]{9, 9, 9, 9};
        r0[4] = new int[]{10, 10, 10, 10};
        cols = r0;
        int i2 = 64;
        if (AndroidUtilities.density <= 1.0f) {
            i2 = 32;
            i = 1;
        } else if (AndroidUtilities.density > 1.5f) {
            int i3 = (AndroidUtilities.density > 2.0f ? 1 : (AndroidUtilities.density == 2.0f ? 0 : -1));
        }
        for (i3 = 0; i3 < EmojiData.data.length; i3++) {
            int ceil = (int) Math.ceil((double) (((float) EmojiData.data[i3].length) / 4.0f));
            for (int i4 = 0; i4 < EmojiData.data[i3].length; i4++) {
                int i5 = i4 / ceil;
                int i6 = i4 - (i5 * ceil);
                int i7 = i6 % cols[i3][i5];
                i6 /= cols[i3][i5];
                int i8 = i7 * i;
                int i9 = i6 * i;
                rects.put(EmojiData.data[i3][i4], new DrawableInfo(new Rect((i7 * i2) + i8, (i6 * i2) + i9, ((i7 + 1) * i2) + i8, ((i6 + 1) * i2) + i9), (byte) i3, (byte) i5, i4));
            }
        }
        placeholderPaint.setColor(0);
    }

    private static void loadEmoji(final int i, final int i2) {
        int i3;
        Bitmap decodeStream;
        Throwable th;
        try {
            int i4;
            File fileStreamPath;
            if (AndroidUtilities.density <= 1.0f) {
                i3 = 2;
            } else {
                if (AndroidUtilities.density > 1.5f) {
                    i3 = (AndroidUtilities.density > 2.0f ? 1 : (AndroidUtilities.density == 2.0f ? 0 : -1));
                }
                i3 = 1;
            }
            for (i4 = 4; i4 < 7; i4++) {
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(2.0f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[]{Integer.valueOf(i4), Float.valueOf(2.0f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
            for (i4 = 8; i4 < 12; i4++) {
                fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(i4), Float.valueOf(2.0f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        } catch (int i5) {
            if (BuildVars.LOGS_ENABLED != 0) {
                FileLog.m2e("Error loading emoji", i5);
                return;
            }
            return;
        }
        try {
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("emoji/");
            stringBuilder.append(String.format(Locale.US, "v12_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(2.0f), Integer.valueOf(i5), Integer.valueOf(i2)}));
            InputStream open = assets.open(stringBuilder.toString());
            Options options = new Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i3;
            decodeStream = BitmapFactory.decodeStream(open, null, options);
            try {
                open.close();
            } catch (Throwable th2) {
                th = th2;
                FileLog.m3e(th);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        Emoji.emojiBmp[i5][i2] = decodeStream;
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
                    }
                });
            }
        } catch (Throwable th3) {
            th = th3;
            decodeStream = null;
            FileLog.m3e(th);
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
        }
        AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateAll(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    public static String fixEmoji(String str) {
        int length = str.length();
        String str2 = str;
        str = null;
        while (str < length) {
            char charAt = str2.charAt(str);
            StringBuilder stringBuilder;
            if (charAt < '\ud83c' || charAt > '\ud83e') {
                if (charAt == '\u20e3') {
                    return str2;
                }
                if (charAt >= '\u203c' && charAt <= '\u3299' && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    stringBuilder = new StringBuilder();
                    str++;
                    stringBuilder.append(str2.substring(0, str));
                    stringBuilder.append("\ufe0f");
                    stringBuilder.append(str2.substring(str));
                    str2 = stringBuilder.toString();
                    length++;
                }
            } else if (charAt != '\ud83c' || str >= length - 1) {
                str++;
            } else {
                int i = str + 1;
                char charAt2 = str2.charAt(i);
                if (!(charAt2 == '\ude2f' || charAt2 == '\udc04' || charAt2 == '\ude1a')) {
                    if (charAt2 != '\udd7f') {
                        str = i;
                    }
                }
                stringBuilder = new StringBuilder();
                str += 2;
                stringBuilder.append(str2.substring(0, str));
                stringBuilder.append("\ufe0f");
                stringBuilder.append(str2.substring(str));
                str2 = stringBuilder.toString();
                length++;
            }
            str++;
        }
        return str2;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence charSequence) {
        DrawableInfo drawableInfo = (DrawableInfo) rects.get(charSequence);
        if (drawableInfo == null) {
            CharSequence charSequence2 = (CharSequence) EmojiData.emojiAliasMap.get(charSequence);
            if (charSequence2 != null) {
                drawableInfo = (DrawableInfo) rects.get(charSequence2);
            }
        }
        if (drawableInfo == null) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No drawable for emoji ");
                stringBuilder.append(charSequence);
                FileLog.m0d(stringBuilder.toString());
            }
            return null;
        }
        charSequence = new EmojiDrawable(drawableInfo);
        charSequence.setBounds(0, 0, drawImgSize, drawImgSize);
        return charSequence;
    }

    public static boolean isValidEmoji(String str) {
        DrawableInfo drawableInfo = (DrawableInfo) rects.get(str);
        if (drawableInfo == null) {
            CharSequence charSequence = (CharSequence) EmojiData.emojiAliasMap.get(str);
            if (charSequence != null) {
                drawableInfo = (DrawableInfo) rects.get(charSequence);
            }
        }
        return drawableInfo != null ? true : null;
    }

    public static Drawable getEmojiBigDrawable(String str) {
        Drawable emojiDrawable = getEmojiDrawable(str);
        if (emojiDrawable == null) {
            CharSequence charSequence = (CharSequence) EmojiData.emojiAliasMap.get(str);
            if (charSequence != null) {
                emojiDrawable = getEmojiDrawable(charSequence);
            }
        }
        if (emojiDrawable == null) {
            return null;
        }
        emojiDrawable.setBounds(0, 0, bigImgSize, bigImgSize);
        emojiDrawable.fullSize = true;
        return emojiDrawable;
    }

    private static boolean inArray(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, null);
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, FontMetricsInt fontMetricsInt, int i, boolean z, int[] iArr) {
        CharSequence charSequence2 = charSequence;
        if (!(SharedConfig.useSystemEmoji || charSequence2 == null)) {
            if (charSequence.length() != 0) {
                CharSequence newSpannable;
                if (z || !(charSequence2 instanceof Spannable)) {
                    newSpannable = Factory.getInstance().newSpannable(charSequence.toString());
                } else {
                    newSpannable = (Spannable) charSequence2;
                }
                StringBuilder stringBuilder = new StringBuilder(16);
                StringBuilder stringBuilder2 = new StringBuilder(2);
                int length = charSequence.length();
                long j = 0;
                int[] iArr2 = iArr;
                long j2 = 0;
                int i2 = 0;
                int i3 = 0;
                int i4 = -1;
                int i5 = 0;
                int i6 = 0;
                int i7 = 0;
                while (i3 < length) {
                    try {
                        Object obj;
                        int i8;
                        int i9;
                        char charAt;
                        int i10;
                        int i11;
                        char charAt2;
                        EmojiDrawable emojiDrawable;
                        FontMetricsInt fontMetricsInt2;
                        Object obj2;
                        char charAt3 = charSequence2.charAt(i3);
                        if (charAt3 >= '\ud83c') {
                            if (charAt3 > '\ud83e') {
                            }
                            if (i4 == -1) {
                                i4 = i3;
                            }
                            stringBuilder.append(charAt3);
                            obj = 16;
                            i8 = i5 + 1;
                            j2 = (j2 << 16) | ((long) charAt3);
                            if (i6 != 0) {
                                i5 = i3 + 2;
                                if (i5 < length) {
                                    i9 = i3 + 1;
                                    charAt = charSequence2.charAt(i9);
                                    if (charAt == '\ud83c') {
                                        charAt = charSequence2.charAt(i5);
                                        if (charAt >= '\udffb' && charAt <= '\udfff') {
                                            stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                            i8 += 2;
                                            i3 = i5;
                                        }
                                    } else {
                                        if (stringBuilder.length() < 2) {
                                            i9 = 2;
                                        } else if (stringBuilder.charAt(0) == '\ud83c' && stringBuilder.charAt(1) == '\udff4' && charAt == '\udb40') {
                                            while (true) {
                                                i10 = i9 + 2;
                                                stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                i8 += 2;
                                                if (i10 >= charSequence.length()) {
                                                    break;
                                                } else if (charSequence2.charAt(i10) != '\udb40') {
                                                    break;
                                                } else {
                                                    i9 = i10;
                                                }
                                            }
                                            i3 = i10 - 1;
                                        }
                                        i5 = i8;
                                        i11 = i3;
                                        i8 = i6;
                                        for (i10 = 0; i10 < 3; i10++) {
                                            i9 = i11 + 1;
                                            if (i9 < length) {
                                                charAt2 = charSequence2.charAt(i9);
                                                if (i10 == 1) {
                                                    if (charAt2 == '\u200d' && stringBuilder.length() > 0) {
                                                        stringBuilder.append(charAt2);
                                                        i5++;
                                                        i11 = i9;
                                                        i8 = 0;
                                                    }
                                                } else if (charAt2 >= '\ufe00') {
                                                    if (charAt2 <= '\ufe0f') {
                                                        i5++;
                                                        i11 = i9;
                                                    }
                                                }
                                            }
                                        }
                                        if (i8 != 0) {
                                            i9 = i11 + 2;
                                            if (i9 < length) {
                                                i2 = i11 + 1;
                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                    charAt = charSequence2.charAt(i9);
                                                    if (charAt >= '\udffb' && charAt <= '\udfff') {
                                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                        i5 += 2;
                                                        if (i8 == 0) {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            } else {
                                                                i2 = 0;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            } else {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        }
                                                        if (VERSION.SDK_INT >= 23 && i10 >= 50) {
                                                            break;
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    }
                                                }
                                            }
                                        }
                                        i9 = i11;
                                        if (i8 == 0) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        } else {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                            } else {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            } else {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                }
                            }
                            i5 = i8;
                            i11 = i3;
                            i8 = i6;
                            for (i10 = 0; i10 < 3; i10++) {
                                i9 = i11 + 1;
                                if (i9 < length) {
                                    charAt2 = charSequence2.charAt(i9);
                                    if (i10 == 1) {
                                        stringBuilder.append(charAt2);
                                        i5++;
                                        i11 = i9;
                                        i8 = 0;
                                    } else if (charAt2 >= '\ufe00') {
                                        if (charAt2 <= '\ufe0f') {
                                            i5++;
                                            i11 = i9;
                                        }
                                    }
                                }
                            }
                            if (i8 != 0) {
                                i9 = i11 + 2;
                                if (i9 < length) {
                                    i2 = i11 + 1;
                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                        charAt = charSequence2.charAt(i9);
                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                        i5 += 2;
                                        if (i8 == 0) {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            } else {
                                                i2 = 0;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            } else {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        } else {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                }
                            }
                            i9 = i11;
                            if (i8 == 0) {
                                fontMetricsInt2 = fontMetricsInt;
                                i6 = i8;
                                i10 = i7;
                            } else {
                                if (iArr2 == null) {
                                    i2 = 0;
                                } else {
                                    i2 = 0;
                                    iArr2[0] = iArr2[0] + 1;
                                }
                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                if (emojiDrawable == null) {
                                    fontMetricsInt2 = fontMetricsInt;
                                    i8 = i;
                                } else {
                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                    i7++;
                                    i2 = 0;
                                }
                                stringBuilder.setLength(i2);
                                i5 = i2;
                                i6 = i5;
                                i10 = i7;
                                i4 = -1;
                            }
                            if (VERSION.SDK_INT >= 23) {
                            }
                            i7 = i10;
                            i2 = i3;
                            j = 0;
                            i3 = i9 + 1;
                            obj2 = obj;
                        }
                        if (j2 == j || (j2 & -4294967296L) != j || (j2 & 65535) != 55356 || charAt3 < '\udde6' || charAt3 > '\uddff') {
                            obj = 16;
                            if (stringBuilder.length() > 0 && (charAt3 == '\u2640' || charAt3 == '\u2642' || charAt3 == '\u2695')) {
                                stringBuilder.append(charAt3);
                                i8 = i5 + 1;
                            } else if (j2 <= j || (61440 & charAt3) != 53248) {
                                int i12;
                                if (charAt3 != '\u20e3') {
                                    if (!(charAt3 == '\u00a9' || charAt3 == '\u00ae')) {
                                        if (charAt3 >= '\u203c' && charAt3 <= '\u3299') {
                                        }
                                        if (i4 == -1) {
                                            stringBuilder.setLength(0);
                                            i4 = -1;
                                            i8 = 0;
                                            i6 = i8;
                                            if (i6 != 0) {
                                                i5 = i3 + 2;
                                                if (i5 < length) {
                                                    i9 = i3 + 1;
                                                    charAt = charSequence2.charAt(i9);
                                                    if (charAt == '\ud83c') {
                                                        if (stringBuilder.length() < 2) {
                                                            i9 = 2;
                                                        } else {
                                                            while (true) {
                                                                i10 = i9 + 2;
                                                                stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                                i8 += 2;
                                                                if (i10 >= charSequence.length()) {
                                                                    if (charSequence2.charAt(i10) != '\udb40') {
                                                                        break;
                                                                    }
                                                                    i9 = i10;
                                                                } else {
                                                                    break;
                                                                }
                                                            }
                                                            i3 = i10 - 1;
                                                        }
                                                        i5 = i8;
                                                        i11 = i3;
                                                        i8 = i6;
                                                        for (i10 = 0; i10 < 3; i10++) {
                                                            i9 = i11 + 1;
                                                            if (i9 < length) {
                                                                charAt2 = charSequence2.charAt(i9);
                                                                if (i10 == 1) {
                                                                    stringBuilder.append(charAt2);
                                                                    i5++;
                                                                    i11 = i9;
                                                                    i8 = 0;
                                                                } else if (charAt2 >= '\ufe00') {
                                                                    if (charAt2 <= '\ufe0f') {
                                                                        i5++;
                                                                        i11 = i9;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (i8 != 0) {
                                                            i9 = i11 + 2;
                                                            if (i9 < length) {
                                                                i2 = i11 + 1;
                                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                                    charAt = charSequence2.charAt(i9);
                                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                                    i5 += 2;
                                                                    if (i8 == 0) {
                                                                        if (iArr2 == null) {
                                                                            i2 = 0;
                                                                            iArr2[0] = iArr2[0] + 1;
                                                                        } else {
                                                                            i2 = 0;
                                                                        }
                                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                                        if (emojiDrawable == null) {
                                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                            i7++;
                                                                            i2 = 0;
                                                                        } else {
                                                                            fontMetricsInt2 = fontMetricsInt;
                                                                            i8 = i;
                                                                        }
                                                                        stringBuilder.setLength(i2);
                                                                        i5 = i2;
                                                                        i6 = i5;
                                                                        i10 = i7;
                                                                        i4 = -1;
                                                                    } else {
                                                                        fontMetricsInt2 = fontMetricsInt;
                                                                        i6 = i8;
                                                                        i10 = i7;
                                                                    }
                                                                    if (VERSION.SDK_INT >= 23) {
                                                                    }
                                                                    i7 = i10;
                                                                    i2 = i3;
                                                                    j = 0;
                                                                    i3 = i9 + 1;
                                                                    obj2 = obj;
                                                                }
                                                            }
                                                        }
                                                        i9 = i11;
                                                        if (i8 == 0) {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        } else {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                            } else {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            } else {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        }
                                                        if (VERSION.SDK_INT >= 23) {
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    } else {
                                                        charAt = charSequence2.charAt(i5);
                                                        stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                                        i8 += 2;
                                                        i3 = i5;
                                                    }
                                                }
                                            }
                                            i5 = i8;
                                            i11 = i3;
                                            i8 = i6;
                                            for (i10 = 0; i10 < 3; i10++) {
                                                i9 = i11 + 1;
                                                if (i9 < length) {
                                                    charAt2 = charSequence2.charAt(i9);
                                                    if (i10 == 1) {
                                                        stringBuilder.append(charAt2);
                                                        i5++;
                                                        i11 = i9;
                                                        i8 = 0;
                                                    } else if (charAt2 >= '\ufe00') {
                                                        if (charAt2 <= '\ufe0f') {
                                                            i5++;
                                                            i11 = i9;
                                                        }
                                                    }
                                                }
                                            }
                                            if (i8 != 0) {
                                                i9 = i11 + 2;
                                                if (i9 < length) {
                                                    i2 = i11 + 1;
                                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                                        charAt = charSequence2.charAt(i9);
                                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                        i5 += 2;
                                                        if (i8 == 0) {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            } else {
                                                                i2 = 0;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            } else {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        }
                                                        if (VERSION.SDK_INT >= 23) {
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    }
                                                }
                                            }
                                            i9 = i11;
                                            if (i8 == 0) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i6 = i8;
                                                i10 = i7;
                                            } else {
                                                if (iArr2 == null) {
                                                    i2 = 0;
                                                } else {
                                                    i2 = 0;
                                                    iArr2[0] = iArr2[0] + 1;
                                                }
                                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                if (emojiDrawable == null) {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i8 = i;
                                                } else {
                                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                    i7++;
                                                    i2 = 0;
                                                }
                                                stringBuilder.setLength(i2);
                                                i5 = i2;
                                                i6 = i5;
                                                i10 = i7;
                                                i4 = -1;
                                            }
                                            if (VERSION.SDK_INT >= 23) {
                                            }
                                            i7 = i10;
                                            i2 = i3;
                                            j = 0;
                                            i3 = i9 + 1;
                                            obj2 = obj;
                                        } else if (!(charAt3 == '\ufe0f' || iArr2 == null)) {
                                            iArr2[0] = 0;
                                            iArr2 = null;
                                        }
                                    }
                                    if (EmojiData.dataCharsMap.containsKey(Character.valueOf(charAt3))) {
                                        i12 = i4 == -1 ? i3 : i4;
                                        i2 = i5 + 1;
                                        stringBuilder.append(charAt3);
                                        i8 = i2;
                                        i4 = i12;
                                        i6 = 1;
                                        if (i6 != 0) {
                                            i5 = i3 + 2;
                                            if (i5 < length) {
                                                i9 = i3 + 1;
                                                charAt = charSequence2.charAt(i9);
                                                if (charAt == '\ud83c') {
                                                    charAt = charSequence2.charAt(i5);
                                                    stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                                    i8 += 2;
                                                    i3 = i5;
                                                } else {
                                                    if (stringBuilder.length() < 2) {
                                                        while (true) {
                                                            i10 = i9 + 2;
                                                            stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                            i8 += 2;
                                                            if (i10 >= charSequence.length()) {
                                                                break;
                                                            } else if (charSequence2.charAt(i10) != '\udb40') {
                                                                break;
                                                            } else {
                                                                i9 = i10;
                                                            }
                                                        }
                                                        i3 = i10 - 1;
                                                    } else {
                                                        i9 = 2;
                                                    }
                                                    i5 = i8;
                                                    i11 = i3;
                                                    i8 = i6;
                                                    for (i10 = 0; i10 < 3; i10++) {
                                                        i9 = i11 + 1;
                                                        if (i9 < length) {
                                                            charAt2 = charSequence2.charAt(i9);
                                                            if (i10 == 1) {
                                                                stringBuilder.append(charAt2);
                                                                i5++;
                                                                i11 = i9;
                                                                i8 = 0;
                                                            } else if (charAt2 >= '\ufe00') {
                                                                if (charAt2 <= '\ufe0f') {
                                                                    i5++;
                                                                    i11 = i9;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (i8 != 0) {
                                                        i9 = i11 + 2;
                                                        if (i9 < length) {
                                                            i2 = i11 + 1;
                                                            if (charSequence2.charAt(i2) == '\ud83c') {
                                                                charAt = charSequence2.charAt(i9);
                                                                stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                                i5 += 2;
                                                                if (i8 == 0) {
                                                                    if (iArr2 == null) {
                                                                        i2 = 0;
                                                                        iArr2[0] = iArr2[0] + 1;
                                                                    } else {
                                                                        i2 = 0;
                                                                    }
                                                                    emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                                    if (emojiDrawable == null) {
                                                                        newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                        i7++;
                                                                        i2 = 0;
                                                                    } else {
                                                                        fontMetricsInt2 = fontMetricsInt;
                                                                        i8 = i;
                                                                    }
                                                                    stringBuilder.setLength(i2);
                                                                    i5 = i2;
                                                                    i6 = i5;
                                                                    i10 = i7;
                                                                    i4 = -1;
                                                                } else {
                                                                    fontMetricsInt2 = fontMetricsInt;
                                                                    i6 = i8;
                                                                    i10 = i7;
                                                                }
                                                                if (VERSION.SDK_INT >= 23) {
                                                                }
                                                                i7 = i10;
                                                                i2 = i3;
                                                                j = 0;
                                                                i3 = i9 + 1;
                                                                obj2 = obj;
                                                            }
                                                        }
                                                    }
                                                    i9 = i11;
                                                    if (i8 == 0) {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    } else {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                        } else {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        } else {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                }
                                            }
                                        }
                                        i5 = i8;
                                        i11 = i3;
                                        i8 = i6;
                                        for (i10 = 0; i10 < 3; i10++) {
                                            i9 = i11 + 1;
                                            if (i9 < length) {
                                                charAt2 = charSequence2.charAt(i9);
                                                if (i10 == 1) {
                                                    stringBuilder.append(charAt2);
                                                    i5++;
                                                    i11 = i9;
                                                    i8 = 0;
                                                } else if (charAt2 >= '\ufe00') {
                                                    if (charAt2 <= '\ufe0f') {
                                                        i5++;
                                                        i11 = i9;
                                                    }
                                                }
                                            }
                                        }
                                        if (i8 != 0) {
                                            i9 = i11 + 2;
                                            if (i9 < length) {
                                                i2 = i11 + 1;
                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                    charAt = charSequence2.charAt(i9);
                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                    i5 += 2;
                                                    if (i8 == 0) {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        } else {
                                                            i2 = 0;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    } else {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                }
                                            }
                                        }
                                        i9 = i11;
                                        if (i8 == 0) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        } else {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                            } else {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            } else {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                    if (i4 == -1) {
                                        iArr2[0] = 0;
                                        iArr2 = null;
                                    } else {
                                        stringBuilder.setLength(0);
                                        i4 = -1;
                                        i8 = 0;
                                        i6 = i8;
                                        if (i6 != 0) {
                                            i5 = i3 + 2;
                                            if (i5 < length) {
                                                i9 = i3 + 1;
                                                charAt = charSequence2.charAt(i9);
                                                if (charAt == '\ud83c') {
                                                    if (stringBuilder.length() < 2) {
                                                        i9 = 2;
                                                    } else {
                                                        while (true) {
                                                            i10 = i9 + 2;
                                                            stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                            i8 += 2;
                                                            if (i10 >= charSequence.length()) {
                                                                if (charSequence2.charAt(i10) != '\udb40') {
                                                                    break;
                                                                }
                                                                i9 = i10;
                                                            } else {
                                                                break;
                                                            }
                                                        }
                                                        i3 = i10 - 1;
                                                    }
                                                    i5 = i8;
                                                    i11 = i3;
                                                    i8 = i6;
                                                    for (i10 = 0; i10 < 3; i10++) {
                                                        i9 = i11 + 1;
                                                        if (i9 < length) {
                                                            charAt2 = charSequence2.charAt(i9);
                                                            if (i10 == 1) {
                                                                stringBuilder.append(charAt2);
                                                                i5++;
                                                                i11 = i9;
                                                                i8 = 0;
                                                            } else if (charAt2 >= '\ufe00') {
                                                                if (charAt2 <= '\ufe0f') {
                                                                    i5++;
                                                                    i11 = i9;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (i8 != 0) {
                                                        i9 = i11 + 2;
                                                        if (i9 < length) {
                                                            i2 = i11 + 1;
                                                            if (charSequence2.charAt(i2) == '\ud83c') {
                                                                charAt = charSequence2.charAt(i9);
                                                                stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                                i5 += 2;
                                                                if (i8 == 0) {
                                                                    if (iArr2 == null) {
                                                                        i2 = 0;
                                                                        iArr2[0] = iArr2[0] + 1;
                                                                    } else {
                                                                        i2 = 0;
                                                                    }
                                                                    emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                                    if (emojiDrawable == null) {
                                                                        newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                        i7++;
                                                                        i2 = 0;
                                                                    } else {
                                                                        fontMetricsInt2 = fontMetricsInt;
                                                                        i8 = i;
                                                                    }
                                                                    stringBuilder.setLength(i2);
                                                                    i5 = i2;
                                                                    i6 = i5;
                                                                    i10 = i7;
                                                                    i4 = -1;
                                                                } else {
                                                                    fontMetricsInt2 = fontMetricsInt;
                                                                    i6 = i8;
                                                                    i10 = i7;
                                                                }
                                                                if (VERSION.SDK_INT >= 23) {
                                                                }
                                                                i7 = i10;
                                                                i2 = i3;
                                                                j = 0;
                                                                i3 = i9 + 1;
                                                                obj2 = obj;
                                                            }
                                                        }
                                                    }
                                                    i9 = i11;
                                                    if (i8 == 0) {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    } else {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                        } else {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        } else {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                } else {
                                                    charAt = charSequence2.charAt(i5);
                                                    stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                                    i8 += 2;
                                                    i3 = i5;
                                                }
                                            }
                                        }
                                        i5 = i8;
                                        i11 = i3;
                                        i8 = i6;
                                        for (i10 = 0; i10 < 3; i10++) {
                                            i9 = i11 + 1;
                                            if (i9 < length) {
                                                charAt2 = charSequence2.charAt(i9);
                                                if (i10 == 1) {
                                                    stringBuilder.append(charAt2);
                                                    i5++;
                                                    i11 = i9;
                                                    i8 = 0;
                                                } else if (charAt2 >= '\ufe00') {
                                                    if (charAt2 <= '\ufe0f') {
                                                        i5++;
                                                        i11 = i9;
                                                    }
                                                }
                                            }
                                        }
                                        if (i8 != 0) {
                                            i9 = i11 + 2;
                                            if (i9 < length) {
                                                i2 = i11 + 1;
                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                    charAt = charSequence2.charAt(i9);
                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                    i5 += 2;
                                                    if (i8 == 0) {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        } else {
                                                            i2 = 0;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    } else {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                }
                                            }
                                        }
                                        i9 = i11;
                                        if (i8 == 0) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        } else {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                            } else {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            } else {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                } else if (i3 > 0) {
                                    char charAt4 = charSequence2.charAt(i2);
                                    if ((charAt4 < '0' || charAt4 > '9') && charAt4 != '#') {
                                        if (charAt4 != '*') {
                                            i12 = i5;
                                            i8 = i6;
                                            i6 = i8;
                                            i8 = i12;
                                            if (i6 != 0) {
                                                i5 = i3 + 2;
                                                if (i5 < length) {
                                                    i9 = i3 + 1;
                                                    charAt = charSequence2.charAt(i9);
                                                    if (charAt == '\ud83c') {
                                                        if (stringBuilder.length() < 2) {
                                                            i9 = 2;
                                                        } else {
                                                            while (true) {
                                                                i10 = i9 + 2;
                                                                stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                                i8 += 2;
                                                                if (i10 >= charSequence.length()) {
                                                                    if (charSequence2.charAt(i10) != '\udb40') {
                                                                        break;
                                                                    }
                                                                    i9 = i10;
                                                                } else {
                                                                    break;
                                                                }
                                                            }
                                                            i3 = i10 - 1;
                                                        }
                                                        i5 = i8;
                                                        i11 = i3;
                                                        i8 = i6;
                                                        for (i10 = 0; i10 < 3; i10++) {
                                                            i9 = i11 + 1;
                                                            if (i9 < length) {
                                                                charAt2 = charSequence2.charAt(i9);
                                                                if (i10 == 1) {
                                                                    stringBuilder.append(charAt2);
                                                                    i5++;
                                                                    i11 = i9;
                                                                    i8 = 0;
                                                                } else if (charAt2 >= '\ufe00') {
                                                                    if (charAt2 <= '\ufe0f') {
                                                                        i5++;
                                                                        i11 = i9;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (i8 != 0) {
                                                            i9 = i11 + 2;
                                                            if (i9 < length) {
                                                                i2 = i11 + 1;
                                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                                    charAt = charSequence2.charAt(i9);
                                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                                    i5 += 2;
                                                                    if (i8 == 0) {
                                                                        if (iArr2 == null) {
                                                                            i2 = 0;
                                                                            iArr2[0] = iArr2[0] + 1;
                                                                        } else {
                                                                            i2 = 0;
                                                                        }
                                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                                        if (emojiDrawable == null) {
                                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                            i7++;
                                                                            i2 = 0;
                                                                        } else {
                                                                            fontMetricsInt2 = fontMetricsInt;
                                                                            i8 = i;
                                                                        }
                                                                        stringBuilder.setLength(i2);
                                                                        i5 = i2;
                                                                        i6 = i5;
                                                                        i10 = i7;
                                                                        i4 = -1;
                                                                    } else {
                                                                        fontMetricsInt2 = fontMetricsInt;
                                                                        i6 = i8;
                                                                        i10 = i7;
                                                                    }
                                                                    if (VERSION.SDK_INT >= 23) {
                                                                    }
                                                                    i7 = i10;
                                                                    i2 = i3;
                                                                    j = 0;
                                                                    i3 = i9 + 1;
                                                                    obj2 = obj;
                                                                }
                                                            }
                                                        }
                                                        i9 = i11;
                                                        if (i8 == 0) {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        } else {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                            } else {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            } else {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        }
                                                        if (VERSION.SDK_INT >= 23) {
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    } else {
                                                        charAt = charSequence2.charAt(i5);
                                                        stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                                        i8 += 2;
                                                        i3 = i5;
                                                    }
                                                }
                                            }
                                            i5 = i8;
                                            i11 = i3;
                                            i8 = i6;
                                            for (i10 = 0; i10 < 3; i10++) {
                                                i9 = i11 + 1;
                                                if (i9 < length) {
                                                    charAt2 = charSequence2.charAt(i9);
                                                    if (i10 == 1) {
                                                        stringBuilder.append(charAt2);
                                                        i5++;
                                                        i11 = i9;
                                                        i8 = 0;
                                                    } else if (charAt2 >= '\ufe00') {
                                                        if (charAt2 <= '\ufe0f') {
                                                            i5++;
                                                            i11 = i9;
                                                        }
                                                    }
                                                }
                                            }
                                            if (i8 != 0) {
                                                i9 = i11 + 2;
                                                if (i9 < length) {
                                                    i2 = i11 + 1;
                                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                                        charAt = charSequence2.charAt(i9);
                                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                        i5 += 2;
                                                        if (i8 == 0) {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            } else {
                                                                i2 = 0;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            } else {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        }
                                                        if (VERSION.SDK_INT >= 23) {
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    }
                                                }
                                            }
                                            i9 = i11;
                                            if (i8 == 0) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i6 = i8;
                                                i10 = i7;
                                            } else {
                                                if (iArr2 == null) {
                                                    i2 = 0;
                                                } else {
                                                    i2 = 0;
                                                    iArr2[0] = iArr2[0] + 1;
                                                }
                                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                if (emojiDrawable == null) {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i8 = i;
                                                } else {
                                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                    i7++;
                                                    i2 = 0;
                                                }
                                                stringBuilder.setLength(i2);
                                                i5 = i2;
                                                i6 = i5;
                                                i10 = i7;
                                                i4 = -1;
                                            }
                                            if (VERSION.SDK_INT >= 23) {
                                            }
                                            i7 = i10;
                                            i2 = i3;
                                            j = 0;
                                            i3 = i9 + 1;
                                            obj2 = obj;
                                        }
                                    }
                                    i12 = (i3 - i2) + 1;
                                    stringBuilder.append(charAt4);
                                    stringBuilder.append(charAt3);
                                    i4 = i2;
                                    i8 = 1;
                                    i6 = i8;
                                    i8 = i12;
                                    if (i6 != 0) {
                                        i5 = i3 + 2;
                                        if (i5 < length) {
                                            i9 = i3 + 1;
                                            charAt = charSequence2.charAt(i9);
                                            if (charAt == '\ud83c') {
                                                charAt = charSequence2.charAt(i5);
                                                stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                                i8 += 2;
                                                i3 = i5;
                                            } else {
                                                if (stringBuilder.length() < 2) {
                                                    while (true) {
                                                        i10 = i9 + 2;
                                                        stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                        i8 += 2;
                                                        if (i10 >= charSequence.length()) {
                                                            break;
                                                        } else if (charSequence2.charAt(i10) != '\udb40') {
                                                            break;
                                                        } else {
                                                            i9 = i10;
                                                        }
                                                    }
                                                    i3 = i10 - 1;
                                                } else {
                                                    i9 = 2;
                                                }
                                                i5 = i8;
                                                i11 = i3;
                                                i8 = i6;
                                                for (i10 = 0; i10 < 3; i10++) {
                                                    i9 = i11 + 1;
                                                    if (i9 < length) {
                                                        charAt2 = charSequence2.charAt(i9);
                                                        if (i10 == 1) {
                                                            stringBuilder.append(charAt2);
                                                            i5++;
                                                            i11 = i9;
                                                            i8 = 0;
                                                        } else if (charAt2 >= '\ufe00') {
                                                            if (charAt2 <= '\ufe0f') {
                                                                i5++;
                                                                i11 = i9;
                                                            }
                                                        }
                                                    }
                                                }
                                                if (i8 != 0) {
                                                    i9 = i11 + 2;
                                                    if (i9 < length) {
                                                        i2 = i11 + 1;
                                                        if (charSequence2.charAt(i2) == '\ud83c') {
                                                            charAt = charSequence2.charAt(i9);
                                                            stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                            i5 += 2;
                                                            if (i8 == 0) {
                                                                if (iArr2 == null) {
                                                                    i2 = 0;
                                                                    iArr2[0] = iArr2[0] + 1;
                                                                } else {
                                                                    i2 = 0;
                                                                }
                                                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                                if (emojiDrawable == null) {
                                                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                    i7++;
                                                                    i2 = 0;
                                                                } else {
                                                                    fontMetricsInt2 = fontMetricsInt;
                                                                    i8 = i;
                                                                }
                                                                stringBuilder.setLength(i2);
                                                                i5 = i2;
                                                                i6 = i5;
                                                                i10 = i7;
                                                                i4 = -1;
                                                            } else {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i6 = i8;
                                                                i10 = i7;
                                                            }
                                                            if (VERSION.SDK_INT >= 23) {
                                                            }
                                                            i7 = i10;
                                                            i2 = i3;
                                                            j = 0;
                                                            i3 = i9 + 1;
                                                            obj2 = obj;
                                                        }
                                                    }
                                                }
                                                i9 = i11;
                                                if (i8 == 0) {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i6 = i8;
                                                    i10 = i7;
                                                } else {
                                                    if (iArr2 == null) {
                                                        i2 = 0;
                                                    } else {
                                                        i2 = 0;
                                                        iArr2[0] = iArr2[0] + 1;
                                                    }
                                                    emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                    if (emojiDrawable == null) {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i8 = i;
                                                    } else {
                                                        newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                        i7++;
                                                        i2 = 0;
                                                    }
                                                    stringBuilder.setLength(i2);
                                                    i5 = i2;
                                                    i6 = i5;
                                                    i10 = i7;
                                                    i4 = -1;
                                                }
                                                if (VERSION.SDK_INT >= 23) {
                                                }
                                                i7 = i10;
                                                i2 = i3;
                                                j = 0;
                                                i3 = i9 + 1;
                                                obj2 = obj;
                                            }
                                        }
                                    }
                                    i5 = i8;
                                    i11 = i3;
                                    i8 = i6;
                                    for (i10 = 0; i10 < 3; i10++) {
                                        i9 = i11 + 1;
                                        if (i9 < length) {
                                            charAt2 = charSequence2.charAt(i9);
                                            if (i10 == 1) {
                                                stringBuilder.append(charAt2);
                                                i5++;
                                                i11 = i9;
                                                i8 = 0;
                                            } else if (charAt2 >= '\ufe00') {
                                                if (charAt2 <= '\ufe0f') {
                                                    i5++;
                                                    i11 = i9;
                                                }
                                            }
                                        }
                                    }
                                    if (i8 != 0) {
                                        i9 = i11 + 2;
                                        if (i9 < length) {
                                            i2 = i11 + 1;
                                            if (charSequence2.charAt(i2) == '\ud83c') {
                                                charAt = charSequence2.charAt(i9);
                                                stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                i5 += 2;
                                                if (i8 == 0) {
                                                    if (iArr2 == null) {
                                                        i2 = 0;
                                                        iArr2[0] = iArr2[0] + 1;
                                                    } else {
                                                        i2 = 0;
                                                    }
                                                    emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                    if (emojiDrawable == null) {
                                                        newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                        i7++;
                                                        i2 = 0;
                                                    } else {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i8 = i;
                                                    }
                                                    stringBuilder.setLength(i2);
                                                    i5 = i2;
                                                    i6 = i5;
                                                    i10 = i7;
                                                    i4 = -1;
                                                } else {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i6 = i8;
                                                    i10 = i7;
                                                }
                                                if (VERSION.SDK_INT >= 23) {
                                                }
                                                i7 = i10;
                                                i2 = i3;
                                                j = 0;
                                                i3 = i9 + 1;
                                                obj2 = obj;
                                            }
                                        }
                                    }
                                    i9 = i11;
                                    if (i8 == 0) {
                                        fontMetricsInt2 = fontMetricsInt;
                                        i6 = i8;
                                        i10 = i7;
                                    } else {
                                        if (iArr2 == null) {
                                            i2 = 0;
                                        } else {
                                            i2 = 0;
                                            iArr2[0] = iArr2[0] + 1;
                                        }
                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                        if (emojiDrawable == null) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i8 = i;
                                        } else {
                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                            i7++;
                                            i2 = 0;
                                        }
                                        stringBuilder.setLength(i2);
                                        i5 = i2;
                                        i6 = i5;
                                        i10 = i7;
                                        i4 = -1;
                                    }
                                    if (VERSION.SDK_INT >= 23) {
                                    }
                                    i7 = i10;
                                    i2 = i3;
                                    j = 0;
                                    i3 = i9 + 1;
                                    obj2 = obj;
                                }
                                i8 = i5;
                                if (i6 != 0) {
                                    i5 = i3 + 2;
                                    if (i5 < length) {
                                        i9 = i3 + 1;
                                        charAt = charSequence2.charAt(i9);
                                        if (charAt == '\ud83c') {
                                            charAt = charSequence2.charAt(i5);
                                            stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                            i8 += 2;
                                            i3 = i5;
                                        } else {
                                            if (stringBuilder.length() < 2) {
                                                while (true) {
                                                    i10 = i9 + 2;
                                                    stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                    i8 += 2;
                                                    if (i10 >= charSequence.length()) {
                                                        break;
                                                    } else if (charSequence2.charAt(i10) != '\udb40') {
                                                        break;
                                                    } else {
                                                        i9 = i10;
                                                    }
                                                }
                                                i3 = i10 - 1;
                                            } else {
                                                i9 = 2;
                                            }
                                            i5 = i8;
                                            i11 = i3;
                                            i8 = i6;
                                            for (i10 = 0; i10 < 3; i10++) {
                                                i9 = i11 + 1;
                                                if (i9 < length) {
                                                    charAt2 = charSequence2.charAt(i9);
                                                    if (i10 == 1) {
                                                        stringBuilder.append(charAt2);
                                                        i5++;
                                                        i11 = i9;
                                                        i8 = 0;
                                                    } else if (charAt2 >= '\ufe00') {
                                                        if (charAt2 <= '\ufe0f') {
                                                            i5++;
                                                            i11 = i9;
                                                        }
                                                    }
                                                }
                                            }
                                            if (i8 != 0) {
                                                i9 = i11 + 2;
                                                if (i9 < length) {
                                                    i2 = i11 + 1;
                                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                                        charAt = charSequence2.charAt(i9);
                                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                        i5 += 2;
                                                        if (i8 == 0) {
                                                            if (iArr2 == null) {
                                                                i2 = 0;
                                                                iArr2[0] = iArr2[0] + 1;
                                                            } else {
                                                                i2 = 0;
                                                            }
                                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                            if (emojiDrawable == null) {
                                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                                i7++;
                                                                i2 = 0;
                                                            } else {
                                                                fontMetricsInt2 = fontMetricsInt;
                                                                i8 = i;
                                                            }
                                                            stringBuilder.setLength(i2);
                                                            i5 = i2;
                                                            i6 = i5;
                                                            i10 = i7;
                                                            i4 = -1;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i6 = i8;
                                                            i10 = i7;
                                                        }
                                                        if (VERSION.SDK_INT >= 23) {
                                                        }
                                                        i7 = i10;
                                                        i2 = i3;
                                                        j = 0;
                                                        i3 = i9 + 1;
                                                        obj2 = obj;
                                                    }
                                                }
                                            }
                                            i9 = i11;
                                            if (i8 == 0) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i6 = i8;
                                                i10 = i7;
                                            } else {
                                                if (iArr2 == null) {
                                                    i2 = 0;
                                                } else {
                                                    i2 = 0;
                                                    iArr2[0] = iArr2[0] + 1;
                                                }
                                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                if (emojiDrawable == null) {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i8 = i;
                                                } else {
                                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                    i7++;
                                                    i2 = 0;
                                                }
                                                stringBuilder.setLength(i2);
                                                i5 = i2;
                                                i6 = i5;
                                                i10 = i7;
                                                i4 = -1;
                                            }
                                            if (VERSION.SDK_INT >= 23) {
                                            }
                                            i7 = i10;
                                            i2 = i3;
                                            j = 0;
                                            i3 = i9 + 1;
                                            obj2 = obj;
                                        }
                                    }
                                }
                                i5 = i8;
                                i11 = i3;
                                i8 = i6;
                                for (i10 = 0; i10 < 3; i10++) {
                                    i9 = i11 + 1;
                                    if (i9 < length) {
                                        charAt2 = charSequence2.charAt(i9);
                                        if (i10 == 1) {
                                            stringBuilder.append(charAt2);
                                            i5++;
                                            i11 = i9;
                                            i8 = 0;
                                        } else if (charAt2 >= '\ufe00') {
                                            if (charAt2 <= '\ufe0f') {
                                                i5++;
                                                i11 = i9;
                                            }
                                        }
                                    }
                                }
                                if (i8 != 0) {
                                    i9 = i11 + 2;
                                    if (i9 < length) {
                                        i2 = i11 + 1;
                                        if (charSequence2.charAt(i2) == '\ud83c') {
                                            charAt = charSequence2.charAt(i9);
                                            stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                            i5 += 2;
                                            if (i8 == 0) {
                                                if (iArr2 == null) {
                                                    i2 = 0;
                                                    iArr2[0] = iArr2[0] + 1;
                                                } else {
                                                    i2 = 0;
                                                }
                                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                if (emojiDrawable == null) {
                                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                    i7++;
                                                    i2 = 0;
                                                } else {
                                                    fontMetricsInt2 = fontMetricsInt;
                                                    i8 = i;
                                                }
                                                stringBuilder.setLength(i2);
                                                i5 = i2;
                                                i6 = i5;
                                                i10 = i7;
                                                i4 = -1;
                                            } else {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i6 = i8;
                                                i10 = i7;
                                            }
                                            if (VERSION.SDK_INT >= 23) {
                                            }
                                            i7 = i10;
                                            i2 = i3;
                                            j = 0;
                                            i3 = i9 + 1;
                                            obj2 = obj;
                                        }
                                    }
                                }
                                i9 = i11;
                                if (i8 == 0) {
                                    fontMetricsInt2 = fontMetricsInt;
                                    i6 = i8;
                                    i10 = i7;
                                } else {
                                    if (iArr2 == null) {
                                        i2 = 0;
                                    } else {
                                        i2 = 0;
                                        iArr2[0] = iArr2[0] + 1;
                                    }
                                    emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                    if (emojiDrawable == null) {
                                        fontMetricsInt2 = fontMetricsInt;
                                        i8 = i;
                                    } else {
                                        newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                        i7++;
                                        i2 = 0;
                                    }
                                    stringBuilder.setLength(i2);
                                    i5 = i2;
                                    i6 = i5;
                                    i10 = i7;
                                    i4 = -1;
                                }
                                if (VERSION.SDK_INT >= 23) {
                                }
                                i7 = i10;
                                i2 = i3;
                                j = 0;
                                i3 = i9 + 1;
                                obj2 = obj;
                            } else {
                                stringBuilder.append(charAt3);
                                i8 = i5 + 1;
                            }
                            j2 = j;
                            i6 = 1;
                            if (i6 != 0) {
                                i5 = i3 + 2;
                                if (i5 < length) {
                                    i9 = i3 + 1;
                                    charAt = charSequence2.charAt(i9);
                                    if (charAt == '\ud83c') {
                                        if (stringBuilder.length() < 2) {
                                            i9 = 2;
                                        } else {
                                            while (true) {
                                                i10 = i9 + 2;
                                                stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                i8 += 2;
                                                if (i10 >= charSequence.length()) {
                                                    if (charSequence2.charAt(i10) != '\udb40') {
                                                        break;
                                                    }
                                                    i9 = i10;
                                                } else {
                                                    break;
                                                }
                                            }
                                            i3 = i10 - 1;
                                        }
                                        i5 = i8;
                                        i11 = i3;
                                        i8 = i6;
                                        for (i10 = 0; i10 < 3; i10++) {
                                            i9 = i11 + 1;
                                            if (i9 < length) {
                                                charAt2 = charSequence2.charAt(i9);
                                                if (i10 == 1) {
                                                    stringBuilder.append(charAt2);
                                                    i5++;
                                                    i11 = i9;
                                                    i8 = 0;
                                                } else if (charAt2 >= '\ufe00') {
                                                    if (charAt2 <= '\ufe0f') {
                                                        i5++;
                                                        i11 = i9;
                                                    }
                                                }
                                            }
                                        }
                                        if (i8 != 0) {
                                            i9 = i11 + 2;
                                            if (i9 < length) {
                                                i2 = i11 + 1;
                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                    charAt = charSequence2.charAt(i9);
                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                    i5 += 2;
                                                    if (i8 == 0) {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        } else {
                                                            i2 = 0;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    } else {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                }
                                            }
                                        }
                                        i9 = i11;
                                        if (i8 == 0) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        } else {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                            } else {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            } else {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    } else {
                                        charAt = charSequence2.charAt(i5);
                                        stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                        i8 += 2;
                                        i3 = i5;
                                    }
                                }
                            }
                            i5 = i8;
                            i11 = i3;
                            i8 = i6;
                            for (i10 = 0; i10 < 3; i10++) {
                                i9 = i11 + 1;
                                if (i9 < length) {
                                    charAt2 = charSequence2.charAt(i9);
                                    if (i10 == 1) {
                                        stringBuilder.append(charAt2);
                                        i5++;
                                        i11 = i9;
                                        i8 = 0;
                                    } else if (charAt2 >= '\ufe00') {
                                        if (charAt2 <= '\ufe0f') {
                                            i5++;
                                            i11 = i9;
                                        }
                                    }
                                }
                            }
                            if (i8 != 0) {
                                i9 = i11 + 2;
                                if (i9 < length) {
                                    i2 = i11 + 1;
                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                        charAt = charSequence2.charAt(i9);
                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                        i5 += 2;
                                        if (i8 == 0) {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            } else {
                                                i2 = 0;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            } else {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        } else {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                }
                            }
                            i9 = i11;
                            if (i8 == 0) {
                                fontMetricsInt2 = fontMetricsInt;
                                i6 = i8;
                                i10 = i7;
                            } else {
                                if (iArr2 == null) {
                                    i2 = 0;
                                } else {
                                    i2 = 0;
                                    iArr2[0] = iArr2[0] + 1;
                                }
                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                if (emojiDrawable == null) {
                                    fontMetricsInt2 = fontMetricsInt;
                                    i8 = i;
                                } else {
                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                    i7++;
                                    i2 = 0;
                                }
                                stringBuilder.setLength(i2);
                                i5 = i2;
                                i6 = i5;
                                i10 = i7;
                                i4 = -1;
                            }
                            if (VERSION.SDK_INT >= 23) {
                            }
                            i7 = i10;
                            i2 = i3;
                            j = 0;
                            i3 = i9 + 1;
                            obj2 = obj;
                        } else {
                            if (i4 == -1) {
                                i4 = i3;
                            }
                            stringBuilder.append(charAt3);
                            obj = 16;
                            i8 = i5 + 1;
                            j2 = (j2 << 16) | ((long) charAt3);
                            if (i6 != 0) {
                                i5 = i3 + 2;
                                if (i5 < length) {
                                    i9 = i3 + 1;
                                    charAt = charSequence2.charAt(i9);
                                    if (charAt == '\ud83c') {
                                        charAt = charSequence2.charAt(i5);
                                        stringBuilder.append(charSequence2.subSequence(i9, i3 + 3));
                                        i8 += 2;
                                        i3 = i5;
                                    } else {
                                        if (stringBuilder.length() < 2) {
                                            while (true) {
                                                i10 = i9 + 2;
                                                stringBuilder.append(charSequence2.subSequence(i9, i10));
                                                i8 += 2;
                                                if (i10 >= charSequence.length()) {
                                                    break;
                                                } else if (charSequence2.charAt(i10) != '\udb40') {
                                                    break;
                                                } else {
                                                    i9 = i10;
                                                }
                                            }
                                            i3 = i10 - 1;
                                        } else {
                                            i9 = 2;
                                        }
                                        i5 = i8;
                                        i11 = i3;
                                        i8 = i6;
                                        for (i10 = 0; i10 < 3; i10++) {
                                            i9 = i11 + 1;
                                            if (i9 < length) {
                                                charAt2 = charSequence2.charAt(i9);
                                                if (i10 == 1) {
                                                    stringBuilder.append(charAt2);
                                                    i5++;
                                                    i11 = i9;
                                                    i8 = 0;
                                                } else if (charAt2 >= '\ufe00') {
                                                    if (charAt2 <= '\ufe0f') {
                                                        i5++;
                                                        i11 = i9;
                                                    }
                                                }
                                            }
                                        }
                                        if (i8 != 0) {
                                            i9 = i11 + 2;
                                            if (i9 < length) {
                                                i2 = i11 + 1;
                                                if (charSequence2.charAt(i2) == '\ud83c') {
                                                    charAt = charSequence2.charAt(i9);
                                                    stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                                    i5 += 2;
                                                    if (i8 == 0) {
                                                        if (iArr2 == null) {
                                                            i2 = 0;
                                                            iArr2[0] = iArr2[0] + 1;
                                                        } else {
                                                            i2 = 0;
                                                        }
                                                        emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                                        if (emojiDrawable == null) {
                                                            newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                            i7++;
                                                            i2 = 0;
                                                        } else {
                                                            fontMetricsInt2 = fontMetricsInt;
                                                            i8 = i;
                                                        }
                                                        stringBuilder.setLength(i2);
                                                        i5 = i2;
                                                        i6 = i5;
                                                        i10 = i7;
                                                        i4 = -1;
                                                    } else {
                                                        fontMetricsInt2 = fontMetricsInt;
                                                        i6 = i8;
                                                        i10 = i7;
                                                    }
                                                    if (VERSION.SDK_INT >= 23) {
                                                    }
                                                    i7 = i10;
                                                    i2 = i3;
                                                    j = 0;
                                                    i3 = i9 + 1;
                                                    obj2 = obj;
                                                }
                                            }
                                        }
                                        i9 = i11;
                                        if (i8 == 0) {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        } else {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                            } else {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            } else {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                }
                            }
                            i5 = i8;
                            i11 = i3;
                            i8 = i6;
                            for (i10 = 0; i10 < 3; i10++) {
                                i9 = i11 + 1;
                                if (i9 < length) {
                                    charAt2 = charSequence2.charAt(i9);
                                    if (i10 == 1) {
                                        stringBuilder.append(charAt2);
                                        i5++;
                                        i11 = i9;
                                        i8 = 0;
                                    } else if (charAt2 >= '\ufe00') {
                                        if (charAt2 <= '\ufe0f') {
                                            i5++;
                                            i11 = i9;
                                        }
                                    }
                                }
                            }
                            if (i8 != 0) {
                                i9 = i11 + 2;
                                if (i9 < length) {
                                    i2 = i11 + 1;
                                    if (charSequence2.charAt(i2) == '\ud83c') {
                                        charAt = charSequence2.charAt(i9);
                                        stringBuilder.append(charSequence2.subSequence(i2, i11 + 3));
                                        i5 += 2;
                                        if (i8 == 0) {
                                            if (iArr2 == null) {
                                                i2 = 0;
                                                iArr2[0] = iArr2[0] + 1;
                                            } else {
                                                i2 = 0;
                                            }
                                            emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                            if (emojiDrawable == null) {
                                                newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                                i7++;
                                                i2 = 0;
                                            } else {
                                                fontMetricsInt2 = fontMetricsInt;
                                                i8 = i;
                                            }
                                            stringBuilder.setLength(i2);
                                            i5 = i2;
                                            i6 = i5;
                                            i10 = i7;
                                            i4 = -1;
                                        } else {
                                            fontMetricsInt2 = fontMetricsInt;
                                            i6 = i8;
                                            i10 = i7;
                                        }
                                        if (VERSION.SDK_INT >= 23) {
                                        }
                                        i7 = i10;
                                        i2 = i3;
                                        j = 0;
                                        i3 = i9 + 1;
                                        obj2 = obj;
                                    }
                                }
                            }
                            i9 = i11;
                            if (i8 == 0) {
                                fontMetricsInt2 = fontMetricsInt;
                                i6 = i8;
                                i10 = i7;
                            } else {
                                if (iArr2 == null) {
                                    i2 = 0;
                                } else {
                                    i2 = 0;
                                    iArr2[0] = iArr2[0] + 1;
                                }
                                emojiDrawable = getEmojiDrawable(stringBuilder.subSequence(i2, stringBuilder.length()));
                                if (emojiDrawable == null) {
                                    fontMetricsInt2 = fontMetricsInt;
                                    i8 = i;
                                } else {
                                    newSpannable.setSpan(new EmojiSpan(emojiDrawable, i2, i, fontMetricsInt), i4, i5 + i4, 33);
                                    i7++;
                                    i2 = 0;
                                }
                                stringBuilder.setLength(i2);
                                i5 = i2;
                                i6 = i5;
                                i10 = i7;
                                i4 = -1;
                            }
                            if (VERSION.SDK_INT >= 23) {
                            }
                            i7 = i10;
                            i2 = i3;
                            j = 0;
                            i3 = i9 + 1;
                            obj2 = obj;
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        return charSequence2;
                    }
                }
                return newSpannable;
            }
        }
        return charSequence2;
    }

    public static void addRecentEmoji(String str) {
        Integer num = (Integer) emojiUseHistory.get(str);
        if (num == null) {
            num = Integer.valueOf(0);
        }
        if (num.intValue() == 0 && emojiUseHistory.size() > 50) {
            for (int size = recentEmoji.size() - 1; size >= 0; size--) {
                emojiUseHistory.remove((String) recentEmoji.get(size));
                recentEmoji.remove(size);
                if (emojiUseHistory.size() <= 50) {
                    break;
                }
            }
        }
        emojiUseHistory.put(str, Integer.valueOf(num.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Entry key : emojiUseHistory.entrySet()) {
            recentEmoji.add(key.getKey());
        }
        Collections.sort(recentEmoji, new C01482());
        while (recentEmoji.size() > 50) {
            recentEmoji.remove(recentEmoji.size() - 1);
        }
    }

    public static void saveRecentEmoji() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        globalEmojiSettings.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    public static void clearRecentEmoji() {
        MessagesController.getGlobalEmojiSettings().edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    public static void loadRecentEmoji() {
        if (!recentEmojiLoaded) {
            recentEmojiLoaded = true;
            SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
            try {
                emojiUseHistory.clear();
                String string;
                String[] split;
                if (globalEmojiSettings.contains("emojis")) {
                    string = globalEmojiSettings.getString("emojis", TtmlNode.ANONYMOUS_REGION_ID);
                    if (string != null && string.length() > 0) {
                        for (String split2 : string.split(",")) {
                            split = split2.split("=");
                            long longValue = Utilities.parseLong(split[0]).longValue();
                            StringBuilder stringBuilder = new StringBuilder();
                            long j = longValue;
                            for (int i = 0; i < 4; i++) {
                                stringBuilder.insert(0, String.valueOf((char) ((int) j)));
                                j >>= 16;
                                if (j == 0) {
                                    break;
                                }
                            }
                            if (stringBuilder.length() > 0) {
                                emojiUseHistory.put(stringBuilder.toString(), Utilities.parseInt(split[1]));
                            }
                        }
                    }
                    globalEmojiSettings.edit().remove("emojis").commit();
                    saveRecentEmoji();
                } else {
                    string = globalEmojiSettings.getString("emojis2", TtmlNode.ANONYMOUS_REGION_ID);
                    if (string != null && string.length() > 0) {
                        for (String split22 : string.split(",")) {
                            split = split22.split("=");
                            emojiUseHistory.put(split[0], Utilities.parseInt(split[1]));
                        }
                    }
                }
                if (emojiUseHistory.isEmpty() && !globalEmojiSettings.getBoolean("filled_default", false)) {
                    String[] strArr = new String[]{"\ud83d\ude02", "\ud83d\ude18", "\u2764", "\ud83d\ude0d", "\ud83d\ude0a", "\ud83d\ude01", "\ud83d\udc4d", "\u263a", "\ud83d\ude14", "\ud83d\ude04", "\ud83d\ude2d", "\ud83d\udc8b", "\ud83d\ude12", "\ud83d\ude33", "\ud83d\ude1c", "\ud83d\ude48", "\ud83d\ude09", "\ud83d\ude03", "\ud83d\ude22", "\ud83d\ude1d", "\ud83d\ude31", "\ud83d\ude21", "\ud83d\ude0f", "\ud83d\ude1e", "\ud83d\ude05", "\ud83d\ude1a", "\ud83d\ude4a", "\ud83d\ude0c", "\ud83d\ude00", "\ud83d\ude0b", "\ud83d\ude06", "\ud83d\udc4c", "\ud83d\ude10", "\ud83d\ude15"};
                    for (int i2 = 0; i2 < strArr.length; i2++) {
                        emojiUseHistory.put(strArr[i2], Integer.valueOf(strArr.length - i2));
                    }
                    globalEmojiSettings.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
                sortEmoji();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            try {
                String string2 = globalEmojiSettings.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                if (string2 != null && string2.length() > 0) {
                    String[] split3 = string2.split(",");
                    for (String split4 : split3) {
                        String[] split5 = split4.split("=");
                        emojiColor.put(split5[0], split5[1]);
                    }
                }
            } catch (Throwable e2) {
                FileLog.m3e(e2);
            }
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        globalEmojiSettings.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }
}
