package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    /* access modifiers changed from: private */
    public static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    /* access modifiers changed from: private */
    public static int drawImgSize = AndroidUtilities.dp(20.0f);
    /* access modifiers changed from: private */
    public static Bitmap[][] emojiBmp = new Bitmap[8][];
    public static HashMap<String, String> emojiColor = new HashMap<>();
    private static int[] emojiCounts = {1906, 199, 123, 332, 128, 222, 292, 259};
    public static boolean emojiDrawingUseAlpha = true;
    public static float emojiDrawingYOffset;
    public static HashMap<String, Integer> emojiUseHistory = new HashMap<>();
    private static boolean inited = false;
    private static Runnable invalidateUiRunnable = Emoji$$ExternalSyntheticLambda1.INSTANCE;
    private static boolean[][] loadingEmoji = new boolean[8][];
    /* access modifiers changed from: private */
    public static Paint placeholderPaint;
    public static ArrayList<String> recentEmoji = new ArrayList<>();
    private static boolean recentEmojiLoaded;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();

    static {
        int i = 0;
        while (true) {
            Bitmap[][] bitmapArr = emojiBmp;
            if (i >= bitmapArr.length) {
                break;
            }
            int[] iArr = emojiCounts;
            bitmapArr[i] = new Bitmap[iArr[i]];
            loadingEmoji[i] = new boolean[iArr[i]];
            i++;
        }
        for (int i2 = 0; i2 < EmojiData.data.length; i2++) {
            int i3 = 0;
            while (true) {
                String[][] strArr = EmojiData.data;
                if (i3 >= strArr[i2].length) {
                    break;
                }
                rects.put(strArr[i2][i3], new DrawableInfo((byte) i2, (short) i3, i3));
                i3++;
            }
        }
        Paint paint = new Paint();
        placeholderPaint = paint;
        paint.setColor(0);
    }

    public static void preloadEmoji(CharSequence charSequence) {
        DrawableInfo drawableInfo = getDrawableInfo(charSequence);
        if (drawableInfo != null) {
            loadEmoji(drawableInfo.page, drawableInfo.page2);
        }
    }

    /* access modifiers changed from: private */
    public static void loadEmoji(byte b, short s) {
        if (emojiBmp[b][s] == null) {
            boolean[][] zArr = loadingEmoji;
            if (!zArr[b][s]) {
                zArr[b][s] = true;
                Utilities.globalQueue.postRunnable(new Emoji$$ExternalSyntheticLambda0(b, s));
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$loadEmoji$1(byte b, short s) {
        loadEmojiInternal(b, s);
        loadingEmoji[b][s] = false;
    }

    private static void loadEmojiInternal(byte b, short s) {
        Bitmap bitmap;
        try {
            int i = AndroidUtilities.density <= 1.0f ? 2 : 1;
            bitmap = null;
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            InputStream open = assets.open("emoji/" + String.format(Locale.US, "%d_%d.png", new Object[]{Byte.valueOf(b), Short.valueOf(s)}));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i;
            bitmap = BitmapFactory.decodeStream(open, (Rect) null, options);
            open.close();
        } catch (Throwable th) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error loading emoji", th);
                return;
            }
            return;
        }
        emojiBmp[b][s] = bitmap;
        AndroidUtilities.cancelRunOnUIThread(invalidateUiRunnable);
        AndroidUtilities.runOnUIThread(invalidateUiRunnable);
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
        int i;
        int length = str.length();
        int i2 = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt < 55356 || charAt > 55358) {
                if (charAt == 8419) {
                    return str;
                }
                if (charAt >= 8252 && charAt <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    StringBuilder sb = new StringBuilder();
                    i++;
                    sb.append(str.substring(0, i));
                    sb.append("️");
                    sb.append(str.substring(i));
                    str = sb.toString();
                }
                i2 = i + 1;
            } else if (charAt != 55356 || i >= length - 1) {
                i++;
                i2 = i + 1;
            } else {
                int i3 = i + 1;
                char charAt2 = str.charAt(i3);
                if (charAt2 == 56879 || charAt2 == 56324 || charAt2 == 56858 || charAt2 == 56703) {
                    StringBuilder sb2 = new StringBuilder();
                    i += 2;
                    sb2.append(str.substring(0, i));
                    sb2.append("️");
                    sb2.append(str.substring(i));
                    str = sb2.toString();
                } else {
                    i = i3;
                    i2 = i + 1;
                }
            }
            length++;
            i2 = i + 1;
        }
        return str;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence charSequence) {
        DrawableInfo drawableInfo = getDrawableInfo(charSequence);
        if (drawableInfo == null) {
            return null;
        }
        EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
        int i = drawImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
        return emojiDrawable;
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.telegram.messenger.Emoji.DrawableInfo getDrawableInfo(java.lang.CharSequence r2) {
        /*
            java.util.HashMap<java.lang.CharSequence, org.telegram.messenger.Emoji$DrawableInfo> r0 = rects
            java.lang.Object r0 = r0.get(r2)
            org.telegram.messenger.Emoji$DrawableInfo r0 = (org.telegram.messenger.Emoji.DrawableInfo) r0
            if (r0 != 0) goto L_0x001d
            java.util.HashMap<java.lang.CharSequence, java.lang.CharSequence> r1 = org.telegram.messenger.EmojiData.emojiAliasMap
            java.lang.Object r2 = r1.get(r2)
            java.lang.CharSequence r2 = (java.lang.CharSequence) r2
            if (r2 == 0) goto L_0x001d
            java.util.HashMap<java.lang.CharSequence, org.telegram.messenger.Emoji$DrawableInfo> r0 = rects
            java.lang.Object r2 = r0.get(r2)
            r0 = r2
            org.telegram.messenger.Emoji$DrawableInfo r0 = (org.telegram.messenger.Emoji.DrawableInfo) r0
        L_0x001d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.getDrawableInfo(java.lang.CharSequence):org.telegram.messenger.Emoji$DrawableInfo");
    }

    public static boolean isValidEmoji(CharSequence charSequence) {
        CharSequence charSequence2;
        if (TextUtils.isEmpty(charSequence)) {
            return false;
        }
        DrawableInfo drawableInfo = rects.get(charSequence);
        if (drawableInfo == null && (charSequence2 = EmojiData.emojiAliasMap.get(charSequence)) != null) {
            drawableInfo = rects.get(charSequence2);
        }
        if (drawableInfo != null) {
            return true;
        }
        return false;
    }

    public static Drawable getEmojiBigDrawable(String str) {
        CharSequence charSequence;
        EmojiDrawable emojiDrawable = getEmojiDrawable(str);
        if (emojiDrawable == null && (charSequence = EmojiData.emojiAliasMap.get(str)) != null) {
            emojiDrawable = getEmojiDrawable(charSequence);
        }
        if (emojiDrawable == null) {
            return null;
        }
        int i = bigImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
        boolean unused = emojiDrawable.fullSize = true;
        return emojiDrawable;
    }

    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        /* access modifiers changed from: private */
        public boolean fullSize = false;
        private DrawableInfo info;
        public int placeholderColor = NUM;

        public int getOpacity() {
            return -2;
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
            Rect rect2;
            if (!isLoaded()) {
                DrawableInfo drawableInfo = this.info;
                Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
                Emoji.placeholderPaint.setColor(this.placeholderColor);
                Rect bounds = getBounds();
                canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), ((float) bounds.width()) * 0.4f, Emoji.placeholderPaint);
                return;
            }
            if (this.fullSize) {
                rect2 = getDrawRect();
            } else {
                rect2 = getBounds();
            }
            if (!canvas.quickReject((float) rect2.left, (float) rect2.top, (float) rect2.right, (float) rect2.bottom, Canvas.EdgeType.AA)) {
                Bitmap[][] access$500 = Emoji.emojiBmp;
                DrawableInfo drawableInfo2 = this.info;
                canvas.drawBitmap(access$500[drawableInfo2.page][drawableInfo2.page2], (Rect) null, rect2, paint);
            }
        }

        public void setAlpha(int i) {
            paint.setAlpha(i);
        }

        public boolean isLoaded() {
            Bitmap[][] access$500 = Emoji.emojiBmp;
            DrawableInfo drawableInfo = this.info;
            return access$500[drawableInfo.page][drawableInfo.page2] != null;
        }

        public void preload() {
            if (!isLoaded()) {
                DrawableInfo drawableInfo = this.info;
                Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
            }
        }
    }

    private static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public short page2;

        public DrawableInfo(byte b, short s, int i) {
            this.page = b;
            this.page2 = s;
            this.emojiIndex = i;
        }
    }

    private static boolean inArray(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    public static class EmojiSpanRange {
        CharSequence code;
        int end;
        int start;

        public EmojiSpanRange(int i, int i2, CharSequence charSequence) {
            this.start = i;
            this.end = i2;
            this.code = charSequence;
        }
    }

    public static boolean fullyConsistsOfEmojis(CharSequence charSequence) {
        int[] iArr = new int[1];
        parseEmojis(charSequence, iArr);
        if (iArr[0] > 0) {
            return true;
        }
        return false;
    }

    public static ArrayList<EmojiSpanRange> parseEmojis(CharSequence charSequence) {
        return parseEmojis(charSequence, (int[]) null);
    }

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<org.telegram.messenger.Emoji.EmojiSpanRange> parseEmojis(java.lang.CharSequence r24, int[] r25) {
        /*
            r0 = r24
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            if (r0 == 0) goto L_0x0253
            int r2 = r24.length()
            if (r2 > 0) goto L_0x0011
            goto L_0x0253
        L_0x0011:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 16
            r2.<init>(r3)
            int r4 = r24.length()
            r5 = 0
            r9 = r25
            r11 = r5
            r10 = 0
            r13 = -1
            r14 = 0
            r15 = 0
            r16 = 0
        L_0x0027:
            if (r10 >= r4) goto L_0x0248
            char r8 = r0.charAt(r10)     // Catch:{ Exception -> 0x0244 }
            r3 = 55356(0xd83c, float:7.757E-41)
            r7 = 1
            if (r8 < r3) goto L_0x003b
            r3 = 55358(0xd83e, float:7.7573E-41)
            if (r8 <= r3) goto L_0x0039
            goto L_0x003b
        L_0x0039:
            r3 = -1
            goto L_0x0061
        L_0x003b:
            int r3 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x0073
            r18 = -4294967296(0xfffffffvar_, double:NaN)
            long r18 = r11 & r18
            int r3 = (r18 > r5 ? 1 : (r18 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0073
            r18 = 65535(0xffff, double:3.23786E-319)
            long r18 = r11 & r18
            r20 = 55356(0xd83c, double:2.73495E-319)
            int r3 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1))
            if (r3 != 0) goto L_0x0073
            r3 = 56806(0xdde6, float:7.9602E-41)
            if (r8 < r3) goto L_0x0073
            r3 = 56831(0xddff, float:7.9637E-41)
            if (r8 > r3) goto L_0x0073
            goto L_0x0039
        L_0x0061:
            if (r13 != r3) goto L_0x0064
            r13 = r10
        L_0x0064:
            r2.append(r8)     // Catch:{ Exception -> 0x0244 }
            int r14 = r14 + 1
            r3 = 16
            long r11 = r11 << r3
            r17 = r4
            long r3 = (long) r8     // Catch:{ Exception -> 0x0244 }
            long r11 = r11 | r3
        L_0x0070:
            r3 = 0
            goto L_0x0106
        L_0x0073:
            r17 = r4
            int r3 = r2.length()     // Catch:{ Exception -> 0x0244 }
            if (r3 <= 0) goto L_0x0092
            r3 = 9792(0x2640, float:1.3722E-41)
            if (r8 == r3) goto L_0x0087
            r3 = 9794(0x2642, float:1.3724E-41)
            if (r8 == r3) goto L_0x0087
            r3 = 9877(0x2695, float:1.384E-41)
            if (r8 != r3) goto L_0x0092
        L_0x0087:
            r2.append(r8)     // Catch:{ Exception -> 0x0244 }
        L_0x008a:
            int r14 = r14 + 1
            r11 = r5
        L_0x008d:
            r3 = 0
            r16 = 1
            goto L_0x0106
        L_0x0092:
            int r3 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x00a3
            r3 = 61440(0xvar_, float:8.6096E-41)
            r3 = r3 & r8
            r4 = 53248(0xd000, float:7.4616E-41)
            if (r3 != r4) goto L_0x00a3
            r2.append(r8)     // Catch:{ Exception -> 0x0244 }
            goto L_0x008a
        L_0x00a3:
            r3 = 8419(0x20e3, float:1.1798E-41)
            if (r8 != r3) goto L_0x00cb
            if (r10 <= 0) goto L_0x0070
            char r3 = r0.charAt(r15)     // Catch:{ Exception -> 0x0244 }
            r4 = 48
            if (r3 < r4) goto L_0x00b5
            r4 = 57
            if (r3 <= r4) goto L_0x00bd
        L_0x00b5:
            r4 = 35
            if (r3 == r4) goto L_0x00bd
            r4 = 42
            if (r3 != r4) goto L_0x0070
        L_0x00bd:
            int r4 = r10 - r15
            int r14 = r4 + 1
            r2.append(r3)     // Catch:{ Exception -> 0x0244 }
            r2.append(r8)     // Catch:{ Exception -> 0x0244 }
            r13 = r15
            r16 = 1
            goto L_0x0070
        L_0x00cb:
            r3 = 169(0xa9, float:2.37E-43)
            if (r8 == r3) goto L_0x00de
            r3 = 174(0xae, float:2.44E-43)
            if (r8 == r3) goto L_0x00de
            r3 = 8252(0x203c, float:1.1564E-41)
            if (r8 < r3) goto L_0x00dc
            r3 = 12953(0x3299, float:1.8151E-41)
            if (r8 > r3) goto L_0x00dc
            goto L_0x00de
        L_0x00dc:
            r3 = -1
            goto L_0x00f4
        L_0x00de:
            java.util.HashMap<java.lang.Character, java.lang.Boolean> r3 = org.telegram.messenger.EmojiData.dataCharsMap     // Catch:{ Exception -> 0x0244 }
            java.lang.Character r4 = java.lang.Character.valueOf(r8)     // Catch:{ Exception -> 0x0244 }
            boolean r3 = r3.containsKey(r4)     // Catch:{ Exception -> 0x0244 }
            if (r3 == 0) goto L_0x00dc
            r3 = -1
            if (r13 != r3) goto L_0x00ee
            r13 = r10
        L_0x00ee:
            int r14 = r14 + 1
            r2.append(r8)     // Catch:{ Exception -> 0x0244 }
            goto L_0x008d
        L_0x00f4:
            if (r13 == r3) goto L_0x0100
            r3 = 0
            r2.setLength(r3)     // Catch:{ Exception -> 0x0244 }
            r3 = 0
            r13 = -1
            r14 = 0
            r16 = 0
            goto L_0x0106
        L_0x0100:
            r3 = 65039(0xfe0f, float:9.1139E-41)
            if (r8 == r3) goto L_0x0070
            r3 = 1
        L_0x0106:
            if (r16 == 0) goto L_0x0171
            int r5 = r10 + 2
            r6 = r17
            if (r5 >= r6) goto L_0x0173
            int r7 = r10 + 1
            char r4 = r0.charAt(r7)     // Catch:{ Exception -> 0x0244 }
            r15 = 55356(0xd83c, float:7.757E-41)
            if (r4 != r15) goto L_0x0134
            char r4 = r0.charAt(r5)     // Catch:{ Exception -> 0x0244 }
            r15 = 57339(0xdffb, float:8.0349E-41)
            if (r4 < r15) goto L_0x0173
            r15 = 57343(0xdfff, float:8.0355E-41)
            if (r4 > r15) goto L_0x0173
            int r10 = r10 + 3
            java.lang.CharSequence r4 = r0.subSequence(r7, r10)     // Catch:{ Exception -> 0x0244 }
            r2.append(r4)     // Catch:{ Exception -> 0x0244 }
            int r14 = r14 + 2
            r15 = r5
            goto L_0x0174
        L_0x0134:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0244 }
            r15 = 2
            if (r5 < r15) goto L_0x0173
            r5 = 0
            char r15 = r2.charAt(r5)     // Catch:{ Exception -> 0x0244 }
            r5 = 55356(0xd83c, float:7.757E-41)
            if (r15 != r5) goto L_0x0173
            r5 = 1
            char r15 = r2.charAt(r5)     // Catch:{ Exception -> 0x0244 }
            r5 = 57332(0xdff4, float:8.0339E-41)
            if (r15 != r5) goto L_0x0173
            r5 = 56128(0xdb40, float:7.8652E-41)
            if (r4 != r5) goto L_0x0173
        L_0x0154:
            int r4 = r7 + 2
            java.lang.CharSequence r7 = r0.subSequence(r7, r4)     // Catch:{ Exception -> 0x0244 }
            r2.append(r7)     // Catch:{ Exception -> 0x0244 }
            r7 = 2
            int r14 = r14 + r7
            int r10 = r24.length()     // Catch:{ Exception -> 0x0244 }
            if (r4 >= r10) goto L_0x016e
            char r10 = r0.charAt(r4)     // Catch:{ Exception -> 0x0244 }
            if (r10 == r5) goto L_0x016c
            goto L_0x016e
        L_0x016c:
            r7 = r4
            goto L_0x0154
        L_0x016e:
            int r10 = r4 + -1
            goto L_0x0173
        L_0x0171:
            r6 = r17
        L_0x0173:
            r15 = r10
        L_0x0174:
            r4 = r3
            r5 = r15
            r3 = 0
        L_0x0177:
            r7 = 3
            if (r3 >= r7) goto L_0x01dc
            int r7 = r5 + 1
            if (r7 >= r6) goto L_0x01d0
            char r10 = r0.charAt(r7)     // Catch:{ Exception -> 0x0244 }
            r22 = r11
            r11 = 1
            if (r3 != r11) goto L_0x01a0
            r11 = 8205(0x200d, float:1.1498E-41)
            if (r10 != r11) goto L_0x01d2
            int r11 = r2.length()     // Catch:{ Exception -> 0x0244 }
            if (r11 <= 0) goto L_0x01d2
            r2.append(r10)     // Catch:{ Exception -> 0x0244 }
            int r14 = r14 + 1
            r5 = r7
            r4 = 0
            r11 = 65039(0xfe0f, float:9.1139E-41)
            r12 = 42
            r16 = 0
            goto L_0x01d7
        L_0x01a0:
            r11 = -1
            r12 = 42
            if (r13 != r11) goto L_0x01b3
            if (r8 == r12) goto L_0x01b3
            r11 = 35
            if (r8 == r11) goto L_0x01b3
            r11 = 48
            if (r8 < r11) goto L_0x01cc
            r11 = 57
            if (r8 > r11) goto L_0x01cc
        L_0x01b3:
            r11 = 65024(0xfe00, float:9.1118E-41)
            if (r10 < r11) goto L_0x01cc
            r11 = 65039(0xfe0f, float:9.1139E-41)
            if (r10 > r11) goto L_0x01d7
            int r14 = r14 + 1
            if (r16 != 0) goto L_0x01ca
            int r5 = r7 + 1
            if (r5 < r6) goto L_0x01c7
            r5 = 1
            goto L_0x01c8
        L_0x01c7:
            r5 = 0
        L_0x01c8:
            r16 = r5
        L_0x01ca:
            r5 = r7
            goto L_0x01d7
        L_0x01cc:
            r11 = 65039(0xfe0f, float:9.1139E-41)
            goto L_0x01d7
        L_0x01d0:
            r22 = r11
        L_0x01d2:
            r11 = 65039(0xfe0f, float:9.1139E-41)
            r12 = 42
        L_0x01d7:
            int r3 = r3 + 1
            r11 = r22
            goto L_0x0177
        L_0x01dc:
            r22 = r11
            if (r4 == 0) goto L_0x01e7
            if (r9 == 0) goto L_0x01e7
            r3 = 0
            r9[r3] = r3     // Catch:{ Exception -> 0x0244 }
            r3 = 0
            r9 = r3
        L_0x01e7:
            if (r16 == 0) goto L_0x0212
            int r3 = r5 + 2
            if (r3 >= r6) goto L_0x0212
            int r4 = r5 + 1
            char r7 = r0.charAt(r4)     // Catch:{ Exception -> 0x0244 }
            r8 = 55356(0xd83c, float:7.757E-41)
            if (r7 != r8) goto L_0x0212
            char r7 = r0.charAt(r3)     // Catch:{ Exception -> 0x0244 }
            r8 = 57339(0xdffb, float:8.0349E-41)
            if (r7 < r8) goto L_0x0212
            r8 = 57343(0xdfff, float:8.0355E-41)
            if (r7 > r8) goto L_0x0212
            int r5 = r5 + 3
            java.lang.CharSequence r4 = r0.subSequence(r4, r5)     // Catch:{ Exception -> 0x0244 }
            r2.append(r4)     // Catch:{ Exception -> 0x0244 }
            int r14 = r14 + 2
            r5 = r3
        L_0x0212:
            if (r16 == 0) goto L_0x0238
            if (r9 == 0) goto L_0x021d
            r3 = 0
            r4 = r9[r3]     // Catch:{ Exception -> 0x0244 }
            r7 = 1
            int r4 = r4 + r7
            r9[r3] = r4     // Catch:{ Exception -> 0x0244 }
        L_0x021d:
            org.telegram.messenger.Emoji$EmojiSpanRange r3 = new org.telegram.messenger.Emoji$EmojiSpanRange     // Catch:{ Exception -> 0x0244 }
            int r14 = r14 + r13
            int r4 = r2.length()     // Catch:{ Exception -> 0x0244 }
            r7 = 0
            java.lang.CharSequence r4 = r2.subSequence(r7, r4)     // Catch:{ Exception -> 0x0244 }
            r3.<init>(r13, r14, r4)     // Catch:{ Exception -> 0x0244 }
            r1.add(r3)     // Catch:{ Exception -> 0x0244 }
            r2.setLength(r7)     // Catch:{ Exception -> 0x0244 }
            r3 = 1
            r13 = -1
            r14 = 0
            r16 = 0
            goto L_0x0239
        L_0x0238:
            r3 = 1
        L_0x0239:
            int r10 = r5 + 1
            r4 = r6
            r11 = r22
            r3 = 16
            r5 = 0
            goto L_0x0027
        L_0x0244:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0248:
            if (r9 == 0) goto L_0x0253
            int r0 = r2.length()
            if (r0 == 0) goto L_0x0253
            r2 = 0
            r9[r2] = r2
        L_0x0253:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.parseEmojis(java.lang.CharSequence, int[]):java.util.ArrayList");
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, (int[]) null);
    }

    /*  JADX ERROR: IF instruction can be used only in fallback mode
        jadx.core.utils.exceptions.CodegenException: IF instruction can be used only in fallback mode
        	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:579)
        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:485)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
        	at java.util.ArrayList.forEach(ArrayList.java:1259)
        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
        */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r7, android.graphics.Paint.FontMetricsInt r8, int r9, boolean r10, int[] r11) {
        /*
            boolean r0 = org.telegram.messenger.SharedConfig.useSystemEmoji
            if (r0 != 0) goto L_0x008f
            if (r7 == 0) goto L_0x008f
            int r0 = r7.length()
            if (r0 != 0) goto L_0x000e
            goto L_0x008f
        L_0x000e:
            if (r10 != 0) goto L_0x0017
            boolean r10 = r7 instanceof android.text.Spannable
            if (r10 == 0) goto L_0x0017
            android.text.Spannable r7 = (android.text.Spannable) r7
            goto L_0x0023
        L_0x0017:
            android.text.Spannable$Factory r10 = android.text.Spannable.Factory.getInstance()
            java.lang.String r7 = r7.toString()
            android.text.Spannable r7 = r10.newSpannable(r7)
        L_0x0023:
            java.util.ArrayList r10 = parseEmojis(r7, r11)
            int r11 = r7.length()
            java.lang.Class<org.telegram.ui.Components.AnimatedEmojiSpan> r0 = org.telegram.ui.Components.AnimatedEmojiSpan.class
            r1 = 0
            java.lang.Object[] r11 = r7.getSpans(r1, r11, r0)
            org.telegram.ui.Components.AnimatedEmojiSpan[] r11 = (org.telegram.ui.Components.AnimatedEmojiSpan[]) r11
            r0 = 0
        L_0x0035:
            int r2 = r10.size()
            if (r0 >= r2) goto L_0x008f
            java.lang.Object r2 = r10.get(r0)     // Catch:{ Exception -> 0x007b }
            org.telegram.messenger.Emoji$EmojiSpanRange r2 = (org.telegram.messenger.Emoji.EmojiSpanRange) r2     // Catch:{ Exception -> 0x007b }
            if (r11 == 0) goto L_0x0064
            r3 = 0
        L_0x0044:
            int r4 = r11.length     // Catch:{ Exception -> 0x007b }
            if (r3 >= r4) goto L_0x0060
            r4 = r11[r3]     // Catch:{ Exception -> 0x007b }
            if (r4 == 0) goto L_0x005d
            int r5 = r7.getSpanStart(r4)     // Catch:{ Exception -> 0x007b }
            int r6 = r2.start     // Catch:{ Exception -> 0x007b }
            if (r5 != r6) goto L_0x005d
            int r4 = r7.getSpanEnd(r4)     // Catch:{ Exception -> 0x007b }
            int r5 = r2.end     // Catch:{ Exception -> 0x007b }
            if (r4 != r5) goto L_0x005d
            r3 = 1
            goto L_0x0061
        L_0x005d:
            int r3 = r3 + 1
            goto L_0x0044
        L_0x0060:
            r3 = 0
        L_0x0061:
            if (r3 == 0) goto L_0x0064
            goto L_0x008c
        L_0x0064:
            java.lang.CharSequence r3 = r2.code     // Catch:{ Exception -> 0x007b }
            org.telegram.messenger.Emoji$EmojiDrawable r3 = getEmojiDrawable(r3)     // Catch:{ Exception -> 0x007b }
            if (r3 == 0) goto L_0x007f
            org.telegram.messenger.Emoji$EmojiSpan r4 = new org.telegram.messenger.Emoji$EmojiSpan     // Catch:{ Exception -> 0x007b }
            r4.<init>(r3, r1, r9, r8)     // Catch:{ Exception -> 0x007b }
            int r3 = r2.start     // Catch:{ Exception -> 0x007b }
            int r2 = r2.end     // Catch:{ Exception -> 0x007b }
            r5 = 33
            r7.setSpan(r4, r3, r2, r5)     // Catch:{ Exception -> 0x007b }
            goto L_0x007f
        L_0x007b:
            r2 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r2)
        L_0x007f:
            int r2 = org.telegram.messenger.SharedConfig.getDevicePerformanceClass()
            if (r2 != 0) goto L_0x008c
            int r2 = r0 + 1
            r3 = 50
            if (r2 < r3) goto L_0x008c
            goto L_0x008f
        L_0x008c:
            int r0 = r0 + 1
            goto L_0x0035
        L_0x008f:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.replaceEmoji(java.lang.CharSequence, android.graphics.Paint$FontMetricsInt, int, boolean, int[]):java.lang.CharSequence");
    }

    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(Drawable drawable, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            super(drawable, i);
            this.fontMetrics = fontMetricsInt;
            if (fontMetricsInt != null) {
                int abs = Math.abs(fontMetricsInt.descent) + Math.abs(this.fontMetrics.ascent);
                this.size = abs;
                if (abs == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i) {
            this.fontMetrics = fontMetricsInt;
            this.size = i;
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            if (fontMetricsInt == null) {
                fontMetricsInt = new Paint.FontMetricsInt();
            }
            Paint.FontMetricsInt fontMetricsInt2 = this.fontMetrics;
            if (fontMetricsInt2 == null) {
                int size2 = super.getSize(paint, charSequence, i, i2, fontMetricsInt);
                int dp = AndroidUtilities.dp(8.0f);
                int dp2 = AndroidUtilities.dp(10.0f);
                int i3 = (-dp2) - dp;
                fontMetricsInt.top = i3;
                int i4 = dp2 - dp;
                fontMetricsInt.bottom = i4;
                fontMetricsInt.ascent = i3;
                fontMetricsInt.leading = 0;
                fontMetricsInt.descent = i4;
                return size2;
            }
            fontMetricsInt.ascent = fontMetricsInt2.ascent;
            fontMetricsInt.descent = fontMetricsInt2.descent;
            fontMetricsInt.top = fontMetricsInt2.top;
            fontMetricsInt.bottom = fontMetricsInt2.bottom;
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i5 = this.size;
                drawable.setBounds(0, 0, i5, i5);
            }
            return this.size;
        }

        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            boolean z;
            boolean z2 = true;
            if (paint.getAlpha() == 255 || !Emoji.emojiDrawingUseAlpha) {
                z = false;
            } else {
                getDrawable().setAlpha(paint.getAlpha());
                z = true;
            }
            if (Emoji.emojiDrawingYOffset != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, Emoji.emojiDrawingYOffset);
            } else {
                z2 = false;
            }
            super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
            if (z2) {
                canvas.restore();
            }
            if (z) {
                getDrawable().setAlpha(255);
            }
        }

        public void updateDrawState(TextPaint textPaint) {
            if (getDrawable() instanceof EmojiDrawable) {
                ((EmojiDrawable) getDrawable()).placeholderColor = NUM & textPaint.getColor();
            }
            super.updateDrawState(textPaint);
        }
    }

    public static void addRecentEmoji(String str) {
        Integer num = emojiUseHistory.get(str);
        if (num == null) {
            num = 0;
        }
        if (num.intValue() == 0 && emojiUseHistory.size() >= 48) {
            ArrayList<String> arrayList = recentEmoji;
            emojiUseHistory.remove(arrayList.get(arrayList.size() - 1));
            ArrayList<String> arrayList2 = recentEmoji;
            arrayList2.set(arrayList2.size() - 1, str);
        }
        emojiUseHistory.put(str, Integer.valueOf(num.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Map.Entry<String, Integer> key : emojiUseHistory.entrySet()) {
            recentEmoji.add((String) key.getKey());
        }
        Collections.sort(recentEmoji, Emoji$$ExternalSyntheticLambda2.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$sortEmoji$2(String str, String str2) {
        Integer num = emojiUseHistory.get(str);
        Integer num2 = emojiUseHistory.get(str2);
        if (num == null) {
            num = 0;
        }
        if (num2 == null) {
            num2 = 0;
        }
        if (num.intValue() > num2.intValue()) {
            return -1;
        }
        if (num.intValue() < num2.intValue()) {
            return 1;
        }
        return 0;
    }

    public static void saveRecentEmoji() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry next : emojiUseHistory.entrySet()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append((String) next.getKey());
            sb.append("=");
            sb.append(next.getValue());
        }
        globalEmojiSettings.edit().putString("emojis2", sb.toString()).commit();
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
                int i = 4;
                if (globalEmojiSettings.contains("emojis")) {
                    String string = globalEmojiSettings.getString("emojis", "");
                    if (string != null && string.length() > 0) {
                        String[] split = string.split(",");
                        int length = split.length;
                        int i2 = 0;
                        while (i2 < length) {
                            String[] split2 = split[i2].split("=");
                            long longValue = Utilities.parseLong(split2[0]).longValue();
                            StringBuilder sb = new StringBuilder();
                            int i3 = 0;
                            while (true) {
                                if (i3 >= i) {
                                    break;
                                }
                                sb.insert(0, (char) ((int) longValue));
                                longValue >>= 16;
                                if (longValue == 0) {
                                    break;
                                }
                                i3++;
                                i = 4;
                            }
                            if (sb.length() > 0) {
                                emojiUseHistory.put(sb.toString(), Utilities.parseInt((CharSequence) split2[1]));
                            }
                            i2++;
                            i = 4;
                        }
                    }
                    globalEmojiSettings.edit().remove("emojis").commit();
                    saveRecentEmoji();
                } else {
                    String string2 = globalEmojiSettings.getString("emojis2", "");
                    if (string2 != null && string2.length() > 0) {
                        for (String split3 : string2.split(",")) {
                            String[] split4 = split3.split("=");
                            emojiUseHistory.put(split4[0], Utilities.parseInt((CharSequence) split4[1]));
                        }
                    }
                }
                if (emojiUseHistory.isEmpty() && !globalEmojiSettings.getBoolean("filled_default", false)) {
                    String[] strArr = {"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                    for (int i4 = 0; i4 < 34; i4++) {
                        emojiUseHistory.put(strArr[i4], Integer.valueOf(34 - i4));
                    }
                    globalEmojiSettings.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
                sortEmoji();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                String string3 = globalEmojiSettings.getString("color", "");
                if (string3 != null && string3.length() > 0) {
                    String[] split5 = string3.split(",");
                    for (String split6 : split5) {
                        String[] split7 = split6.split("=");
                        emojiColor.put(split7[0], split7[1]);
                    }
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry next : emojiColor.entrySet()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append((String) next.getKey());
            sb.append("=");
            sb.append((String) next.getValue());
        }
        globalEmojiSettings.edit().putString("color", sb.toString()).commit();
    }
}
