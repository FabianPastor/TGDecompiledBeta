package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    private static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    private static final int[][] cols;
    private static int drawImgSize = AndroidUtilities.dp(20.0f);
    private static Bitmap[][] emojiBmp = ((Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{8, 4}));
    public static HashMap<String, String> emojiColor = new HashMap();
    public static HashMap<String, Integer> emojiUseHistory = new HashMap();
    private static boolean inited = false;
    private static boolean[][] loadingEmoji = ((boolean[][]) Array.newInstance(boolean.class, new int[]{8, 4}));
    private static Paint placeholderPaint = new Paint();
    public static ArrayList<String> recentEmoji = new ArrayList();
    private static boolean recentEmojiLoaded = false;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
    private static final int splitCount = 4;

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
        private boolean fullSize = false;
        private DrawableInfo info;

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
            Bitmap[][] access$300 = Emoji.emojiBmp;
            DrawableInfo drawableInfo = this.info;
            if (access$300[drawableInfo.page][drawableInfo.page2] == null) {
                boolean[][] access$400 = Emoji.loadingEmoji;
                drawableInfo = this.info;
                if (!access$400[drawableInfo.page][drawableInfo.page2]) {
                    access$400 = Emoji.loadingEmoji;
                    drawableInfo = this.info;
                    access$400[drawableInfo.page][drawableInfo.page2] = true;
                    Utilities.globalQueue.postRunnable(new -$$Lambda$Emoji$EmojiDrawable$tIn098DVTEVbhZUc7ywBHxfGQOU(this));
                    canvas.drawRect(getBounds(), Emoji.placeholderPaint);
                    return;
                }
                return;
            }
            Rect drawRect;
            if (this.fullSize) {
                drawRect = getDrawRect();
            } else {
                drawRect = getBounds();
            }
            Bitmap[][] access$3002 = Emoji.emojiBmp;
            DrawableInfo drawableInfo2 = this.info;
            canvas.drawBitmap(access$3002[drawableInfo2.page][drawableInfo2.page2], drawableInfo2.rect, drawRect, paint);
        }

        public /* synthetic */ void lambda$draw$0$Emoji$EmojiDrawable() {
            DrawableInfo drawableInfo = this.info;
            Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
            boolean[][] access$400 = Emoji.loadingEmoji;
            DrawableInfo drawableInfo2 = this.info;
            access$400[drawableInfo2.page][drawableInfo2.page2] = false;
        }
    }

    public static class EmojiSpan extends ImageSpan {
        private FontMetricsInt fontMetrics;
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
            FontMetricsInt fontMetricsInt2 = this.fontMetrics;
            int dp;
            if (fontMetricsInt2 == null) {
                int size = super.getSize(paint, charSequence, i, i2, fontMetricsInt);
                dp = AndroidUtilities.dp(8.0f);
                i = AndroidUtilities.dp(10.0f);
                i2 = (-i) - dp;
                fontMetricsInt.top = i2;
                i -= dp;
                fontMetricsInt.bottom = i;
                fontMetricsInt.ascent = i2;
                fontMetricsInt.leading = 0;
                fontMetricsInt.descent = i;
                return size;
            }
            if (fontMetricsInt != null) {
                fontMetricsInt.ascent = fontMetricsInt2.ascent;
                fontMetricsInt.descent = fontMetricsInt2.descent;
                fontMetricsInt.top = fontMetricsInt2.top;
                fontMetricsInt.bottom = fontMetricsInt2.bottom;
            }
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                dp = this.size;
                drawable.setBounds(0, 0, dp, dp);
            }
            return this.size;
        }
    }

    static {
        int i;
        r1 = new int[8][];
        int i2 = 2;
        r1[2] = new int[]{5, 5, 5, 5};
        r1[3] = new int[]{7, 7, 7, 7};
        r1[4] = new int[]{5, 5, 5, 5};
        r1[5] = new int[]{7, 7, 7, 7};
        r1[6] = new int[]{8, 8, 8, 8};
        r1[7] = new int[]{8, 8, 8, 8};
        cols = r1;
        float f = AndroidUtilities.density;
        int i3 = 66;
        if (f <= 1.0f) {
            i3 = 33;
            i2 = 1;
        } else if (f > 1.5f) {
            i = (f > 2.0f ? 1 : (f == 2.0f ? 0 : -1));
        }
        i = 0;
        while (true) {
            String[][] strArr = EmojiData.data;
            if (i < strArr.length) {
                int ceil = (int) Math.ceil((double) (((float) strArr[i].length) / 4.0f));
                for (int i4 = 0; i4 < EmojiData.data[i].length; i4++) {
                    int i5 = i4 / ceil;
                    int i6 = i4 - (i5 * ceil);
                    int[][] iArr = cols;
                    int i7 = i6 % iArr[i][i5];
                    i6 /= iArr[i][i5];
                    int i8 = i7 * i2;
                    int i9 = i6 * i2;
                    rects.put(EmojiData.data[i][i4], new DrawableInfo(new Rect((i7 * i3) + i8, (i6 * i3) + i9, ((i7 + 1) * i3) + i8, ((i6 + 1) * i3) + i9), (byte) i, (byte) i5, i4));
                }
                i++;
            } else {
                placeholderPaint.setColor(0);
                return;
            }
        }
    }

    private static void loadEmoji(int i, int i2) {
        int i3;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                i3 = 2;
            } else {
                if (AndroidUtilities.density > 1.5f) {
                    i3 = (AndroidUtilities.density > 2.0f ? 1 : (AndroidUtilities.density == 2.0f ? 0 : -1));
                }
                i3 = 1;
            }
            for (int i4 = 12; i4 < 14; i4++) {
                File fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(i4), Float.valueOf(2.0f), Integer.valueOf(i)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        } catch (Throwable th) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error loading emoji", th);
                return;
            }
            return;
        }
        Bitmap bitmap = null;
        try {
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("emoji/");
            stringBuilder.append(String.format(Locale.US, "v14_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(2.0f), Integer.valueOf(i), Integer.valueOf(i2)}));
            InputStream open = assets.open(stringBuilder.toString());
            Options options = new Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i3;
            if (VERSION.SDK_INT >= 26) {
                options.inPreferredConfig = Config.HARDWARE;
            }
            bitmap = BitmapFactory.decodeStream(open, null, options);
            open.close();
        } catch (Throwable th2) {
            FileLog.e(th2);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$Emoji$8PuKHwVD-cYGVxmUFCHOFHCQ5bM(i, i2, bitmap));
    }

    static /* synthetic */ void lambda$loadEmoji$0(int i, int i2, Bitmap bitmap) {
        emojiBmp[i][i2] = bitmap;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoad, new Object[0]);
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
        int i = 0;
        while (i < length) {
            char charAt = str2.charAt(i);
            String str3 = "️";
            StringBuilder stringBuilder;
            if (charAt < 55356 || charAt > 55358) {
                if (charAt == 8419) {
                    return str2;
                }
                if (charAt >= 8252 && charAt <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    stringBuilder = new StringBuilder();
                    i++;
                    stringBuilder.append(str2.substring(0, i));
                    stringBuilder.append(str3);
                    stringBuilder.append(str2.substring(i));
                    str2 = stringBuilder.toString();
                }
                i++;
            } else if (charAt != 55356 || i >= length - 1) {
                i++;
                i++;
            } else {
                int i2 = i + 1;
                char charAt2 = str2.charAt(i2);
                if (charAt2 == 56879 || charAt2 == 56324 || charAt2 == 56858 || charAt2 == 56703) {
                    stringBuilder = new StringBuilder();
                    i += 2;
                    stringBuilder.append(str2.substring(0, i));
                    stringBuilder.append(str3);
                    stringBuilder.append(str2.substring(i));
                    str2 = stringBuilder.toString();
                } else {
                    i = i2;
                    i++;
                }
            }
            length++;
            i++;
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
                FileLog.d(stringBuilder.toString());
            }
            return null;
        }
        EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
        int i = drawImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
        return emojiDrawable;
    }

    public static boolean isValidEmoji(String str) {
        DrawableInfo drawableInfo = (DrawableInfo) rects.get(str);
        if (drawableInfo == null) {
            CharSequence charSequence = (CharSequence) EmojiData.emojiAliasMap.get(str);
            if (charSequence != null) {
                drawableInfo = (DrawableInfo) rects.get(charSequence);
            }
        }
        return drawableInfo != null;
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
        int i = bigImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
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

    /* JADX WARNING: Removed duplicated region for block: B:154:0x024f A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0215 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x019e A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x01e9 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0215 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x024f A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0263 A:{SYNTHETIC, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x019e A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x01e9 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x024f A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0215 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x025e  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0263 A:{SYNTHETIC, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x019e A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x01e9 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x0215 A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x024f A:{Catch:{ Exception -> 0x0271 }} */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0263 A:{SYNTHETIC, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x025e  */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r24, android.graphics.Paint.FontMetricsInt r25, int r26, boolean r27, int[] r28) {
        /*
        r1 = r24;
        r0 = org.telegram.messenger.SharedConfig.useSystemEmoji;
        if (r0 != 0) goto L_0x0277;
    L_0x0006:
        if (r1 == 0) goto L_0x0277;
    L_0x0008:
        r0 = r24.length();
        if (r0 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x0277;
    L_0x0010:
        if (r27 != 0) goto L_0x001a;
    L_0x0012:
        r0 = r1 instanceof android.text.Spannable;
        if (r0 == 0) goto L_0x001a;
    L_0x0016:
        r0 = r1;
        r0 = (android.text.Spannable) r0;
        goto L_0x0026;
    L_0x001a:
        r0 = android.text.Spannable.Factory.getInstance();
        r2 = r24.toString();
        r0 = r0.newSpannable(r2);
    L_0x0026:
        r2 = new java.lang.StringBuilder;
        r3 = 16;
        r2.<init>(r3);
        r4 = new java.lang.StringBuilder;
        r5 = 2;
        r4.<init>(r5);
        r4 = r24.length();
        r6 = 0;
        r8 = -1;
        r16 = r28;
        r11 = r6;
        r10 = 0;
        r13 = -1;
        r14 = 0;
        r15 = 0;
        r17 = 0;
        r18 = 0;
    L_0x0045:
        if (r10 >= r4) goto L_0x0276;
    L_0x0047:
        r5 = r1.charAt(r10);	 Catch:{ Exception -> 0x0271 }
        r9 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r3 = 1;
        if (r5 < r9) goto L_0x0056;
    L_0x0051:
        r9 = 55358; // 0xd83e float:7.7573E-41 double:2.73505E-319;
        if (r5 <= r9) goto L_0x007b;
    L_0x0056:
        r9 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r9 == 0) goto L_0x008b;
    L_0x005a:
        r20 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
        r20 = r11 & r20;
        r9 = (r20 > r6 ? 1 : (r20 == r6 ? 0 : -1));
        if (r9 != 0) goto L_0x008b;
    L_0x0065:
        r20 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r20 = r11 & r20;
        r22 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r9 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1));
        if (r9 != 0) goto L_0x008b;
    L_0x0071:
        r9 = 56806; // 0xdde6 float:7.9602E-41 double:2.8066E-319;
        if (r5 < r9) goto L_0x008b;
    L_0x0076:
        r9 = 56831; // 0xddff float:7.9637E-41 double:2.8078E-319;
        if (r5 > r9) goto L_0x008b;
    L_0x007b:
        if (r13 != r8) goto L_0x007e;
    L_0x007d:
        r13 = r10;
    L_0x007e:
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        r9 = r14 + 1;
        r19 = 16;
        r11 = r11 << r19;
        r14 = (long) r5;	 Catch:{ Exception -> 0x0271 }
        r11 = r11 | r14;
        goto L_0x0129;
    L_0x008b:
        r19 = 16;
        r9 = r2.length();	 Catch:{ Exception -> 0x0271 }
        if (r9 <= 0) goto L_0x00a9;
    L_0x0093:
        r9 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        if (r5 == r9) goto L_0x009f;
    L_0x0097:
        r9 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        if (r5 == r9) goto L_0x009f;
    L_0x009b:
        r9 = 9877; // 0x2695 float:1.384E-41 double:4.88E-320;
        if (r5 != r9) goto L_0x00a9;
    L_0x009f:
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        r9 = r14 + 1;
        r11 = r6;
    L_0x00a5:
        r17 = 1;
        goto L_0x0129;
    L_0x00a9:
        r9 = (r11 > r6 ? 1 : (r11 == r6 ? 0 : -1));
        if (r9 <= 0) goto L_0x00be;
    L_0x00ad:
        r9 = 61440; // 0xvar_ float:8.6096E-41 double:3.03554E-319;
        r9 = r9 & r5;
        r6 = 53248; // 0xd000 float:7.4616E-41 double:2.6308E-319;
        if (r9 != r6) goto L_0x00be;
    L_0x00b6:
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        r9 = r14 + 1;
        r11 = 0;
        goto L_0x00a5;
    L_0x00be:
        r6 = 8419; // 0x20e3 float:1.1798E-41 double:4.1595E-320;
        if (r5 != r6) goto L_0x00eb;
    L_0x00c2:
        if (r10 <= 0) goto L_0x0128;
    L_0x00c4:
        r6 = r1.charAt(r15);	 Catch:{ Exception -> 0x0271 }
        r7 = 48;
        if (r6 < r7) goto L_0x00d0;
    L_0x00cc:
        r7 = 57;
        if (r6 <= r7) goto L_0x00dc;
    L_0x00d0:
        r7 = 35;
        if (r6 == r7) goto L_0x00dc;
    L_0x00d4:
        r7 = 42;
        if (r6 != r7) goto L_0x00d9;
    L_0x00d8:
        goto L_0x00dc;
    L_0x00d9:
        r9 = r17;
        goto L_0x00e8;
    L_0x00dc:
        r7 = r10 - r15;
        r7 = r7 + r3;
        r2.append(r6);	 Catch:{ Exception -> 0x0271 }
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        r14 = r7;
        r13 = r15;
        r9 = 1;
    L_0x00e8:
        r17 = r9;
        goto L_0x0128;
    L_0x00eb:
        r6 = 169; // 0xa9 float:2.37E-43 double:8.35E-322;
        if (r5 == r6) goto L_0x00fb;
    L_0x00ef:
        r6 = 174; // 0xae float:2.44E-43 double:8.6E-322;
        if (r5 == r6) goto L_0x00fb;
    L_0x00f3:
        r6 = 8252; // 0x203c float:1.1564E-41 double:4.077E-320;
        if (r5 < r6) goto L_0x0110;
    L_0x00f7:
        r6 = 12953; // 0x3299 float:1.8151E-41 double:6.3996E-320;
        if (r5 > r6) goto L_0x0110;
    L_0x00fb:
        r6 = org.telegram.messenger.EmojiData.dataCharsMap;	 Catch:{ Exception -> 0x0271 }
        r7 = java.lang.Character.valueOf(r5);	 Catch:{ Exception -> 0x0271 }
        r6 = r6.containsKey(r7);	 Catch:{ Exception -> 0x0271 }
        if (r6 == 0) goto L_0x0110;
    L_0x0107:
        if (r13 != r8) goto L_0x010a;
    L_0x0109:
        r13 = r10;
    L_0x010a:
        r9 = r14 + 1;
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        goto L_0x00a5;
    L_0x0110:
        if (r13 == r8) goto L_0x011b;
    L_0x0112:
        r6 = 0;
        r2.setLength(r6);	 Catch:{ Exception -> 0x0271 }
        r9 = 0;
        r13 = -1;
        r17 = 0;
        goto L_0x0129;
    L_0x011b:
        r6 = 0;
        r7 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r5 == r7) goto L_0x0128;
    L_0x0121:
        if (r16 == 0) goto L_0x0128;
    L_0x0123:
        r16[r6] = r6;	 Catch:{ Exception -> 0x0271 }
        r6 = 0;
        r16 = r6;
    L_0x0128:
        r9 = r14;
    L_0x0129:
        r6 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        r7 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r17 == 0) goto L_0x0194;
    L_0x0131:
        r14 = r10 + 2;
        if (r14 >= r4) goto L_0x0194;
    L_0x0135:
        r15 = r10 + 1;
        r8 = r1.charAt(r15);	 Catch:{ Exception -> 0x0271 }
        r3 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r8 != r3) goto L_0x0156;
    L_0x0140:
        r3 = r1.charAt(r14);	 Catch:{ Exception -> 0x0271 }
        if (r3 < r7) goto L_0x0194;
    L_0x0146:
        if (r3 > r6) goto L_0x0194;
    L_0x0148:
        r10 = r10 + 3;
        r3 = r1.subSequence(r15, r10);	 Catch:{ Exception -> 0x0271 }
        r2.append(r3);	 Catch:{ Exception -> 0x0271 }
        r9 = r9 + 2;
        r15 = r14;
        r14 = 2;
        goto L_0x0196;
    L_0x0156:
        r3 = r2.length();	 Catch:{ Exception -> 0x0271 }
        r14 = 2;
        if (r3 < r14) goto L_0x0195;
    L_0x015d:
        r3 = 0;
        r14 = r2.charAt(r3);	 Catch:{ Exception -> 0x0271 }
        r3 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r14 != r3) goto L_0x0194;
    L_0x0167:
        r3 = 1;
        r14 = r2.charAt(r3);	 Catch:{ Exception -> 0x0271 }
        r3 = 57332; // 0xdff4 float:8.0339E-41 double:2.8326E-319;
        if (r14 != r3) goto L_0x0194;
    L_0x0171:
        r3 = 56128; // 0xdb40 float:7.8652E-41 double:2.7731E-319;
        if (r8 != r3) goto L_0x0194;
    L_0x0176:
        r8 = r15 + 2;
        r10 = r1.subSequence(r15, r8);	 Catch:{ Exception -> 0x0271 }
        r2.append(r10);	 Catch:{ Exception -> 0x0271 }
        r14 = 2;
        r9 = r9 + r14;
        r10 = r24.length();	 Catch:{ Exception -> 0x0271 }
        if (r8 >= r10) goto L_0x0190;
    L_0x0187:
        r10 = r1.charAt(r8);	 Catch:{ Exception -> 0x0271 }
        if (r10 == r3) goto L_0x018e;
    L_0x018d:
        goto L_0x0190;
    L_0x018e:
        r15 = r8;
        goto L_0x0176;
    L_0x0190:
        r8 = r8 + -1;
        r15 = r8;
        goto L_0x0196;
    L_0x0194:
        r14 = 2;
    L_0x0195:
        r15 = r10;
    L_0x0196:
        r10 = r9;
        r8 = r15;
        r9 = r17;
        r3 = 0;
    L_0x019b:
        r14 = 3;
        if (r3 >= r14) goto L_0x01e7;
    L_0x019e:
        r14 = r8 + 1;
        if (r14 >= r4) goto L_0x01db;
    L_0x01a2:
        r6 = r1.charAt(r14);	 Catch:{ Exception -> 0x0271 }
        r7 = 1;
        if (r3 != r7) goto L_0x01be;
    L_0x01a9:
        r7 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        if (r6 != r7) goto L_0x01db;
    L_0x01ad:
        r7 = r2.length();	 Catch:{ Exception -> 0x0271 }
        if (r7 <= 0) goto L_0x01db;
    L_0x01b3:
        r2.append(r6);	 Catch:{ Exception -> 0x0271 }
        r10 = r10 + 1;
        r8 = r14;
        r7 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        r9 = 0;
        goto L_0x01de;
    L_0x01be:
        r7 = -1;
        if (r13 != r7) goto L_0x01cd;
    L_0x01c1:
        r7 = 42;
        if (r5 == r7) goto L_0x01cd;
    L_0x01c5:
        r7 = 49;
        if (r5 < r7) goto L_0x01db;
    L_0x01c9:
        r7 = 57;
        if (r5 > r7) goto L_0x01db;
    L_0x01cd:
        r7 = 65024; // 0xfe00 float:9.1118E-41 double:3.2126E-319;
        if (r6 < r7) goto L_0x01db;
    L_0x01d2:
        r7 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r6 > r7) goto L_0x01de;
    L_0x01d7:
        r10 = r10 + 1;
        r8 = r14;
        goto L_0x01de;
    L_0x01db:
        r7 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
    L_0x01de:
        r3 = r3 + 1;
        r6 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        r7 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        goto L_0x019b;
    L_0x01e7:
        if (r9 == 0) goto L_0x0212;
    L_0x01e9:
        r3 = r8 + 2;
        if (r3 >= r4) goto L_0x0212;
    L_0x01ed:
        r5 = r8 + 1;
        r6 = r1.charAt(r5);	 Catch:{ Exception -> 0x0271 }
        r7 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r6 != r7) goto L_0x0212;
    L_0x01f8:
        r6 = r1.charAt(r3);	 Catch:{ Exception -> 0x0271 }
        r7 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r6 < r7) goto L_0x0212;
    L_0x0201:
        r7 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r6 > r7) goto L_0x0212;
    L_0x0206:
        r8 = r8 + 3;
        r5 = r1.subSequence(r5, r8);	 Catch:{ Exception -> 0x0271 }
        r2.append(r5);	 Catch:{ Exception -> 0x0271 }
        r10 = r10 + 2;
        goto L_0x0213;
    L_0x0212:
        r3 = r8;
    L_0x0213:
        if (r9 == 0) goto L_0x024f;
    L_0x0215:
        if (r16 == 0) goto L_0x021f;
    L_0x0217:
        r5 = 0;
        r6 = r16[r5];	 Catch:{ Exception -> 0x0271 }
        r7 = 1;
        r6 = r6 + r7;
        r16[r5] = r6;	 Catch:{ Exception -> 0x0271 }
        goto L_0x0220;
    L_0x021f:
        r5 = 0;
    L_0x0220:
        r6 = r2.length();	 Catch:{ Exception -> 0x0271 }
        r6 = r2.subSequence(r5, r6);	 Catch:{ Exception -> 0x0271 }
        r6 = getEmojiDrawable(r6);	 Catch:{ Exception -> 0x0271 }
        if (r6 == 0) goto L_0x0241;
    L_0x022e:
        r7 = new org.telegram.messenger.Emoji$EmojiSpan;	 Catch:{ Exception -> 0x0271 }
        r8 = r25;
        r14 = r26;
        r7.<init>(r6, r5, r14, r8);	 Catch:{ Exception -> 0x0271 }
        r10 = r10 + r13;
        r5 = 33;
        r0.setSpan(r7, r13, r10, r5);	 Catch:{ Exception -> 0x0271 }
        r18 = r18 + 1;
        r5 = 0;
        goto L_0x0245;
    L_0x0241:
        r8 = r25;
        r14 = r26;
    L_0x0245:
        r2.setLength(r5);	 Catch:{ Exception -> 0x0271 }
        r6 = r18;
        r10 = 0;
        r13 = -1;
        r17 = 0;
        goto L_0x0258;
    L_0x024f:
        r8 = r25;
        r14 = r26;
        r5 = 0;
        r17 = r9;
        r6 = r18;
    L_0x0258:
        r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0271 }
        r9 = 23;
        if (r7 >= r9) goto L_0x0263;
    L_0x025e:
        r7 = 50;
        if (r6 < r7) goto L_0x0263;
    L_0x0262:
        goto L_0x0276;
    L_0x0263:
        r7 = 1;
        r3 = r3 + r7;
        r18 = r6;
        r14 = r10;
        r5 = 2;
        r6 = 0;
        r8 = -1;
        r10 = r3;
        r3 = 16;
        goto L_0x0045;
    L_0x0271:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        return r1;
    L_0x0276:
        return r0;
    L_0x0277:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.replaceEmoji(java.lang.CharSequence, android.graphics.Paint$FontMetricsInt, int, boolean, int[]):java.lang.CharSequence");
    }

    public static void addRecentEmoji(String str) {
        Integer num = (Integer) emojiUseHistory.get(str);
        if (num == null) {
            num = Integer.valueOf(0);
        }
        if (num.intValue() == 0 && emojiUseHistory.size() >= 48) {
            ArrayList arrayList = recentEmoji;
            emojiUseHistory.remove((String) arrayList.get(arrayList.size() - 1));
            arrayList = recentEmoji;
            arrayList.set(arrayList.size() - 1, str);
        }
        emojiUseHistory.put(str, Integer.valueOf(num.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Entry key : emojiUseHistory.entrySet()) {
            recentEmoji.add(key.getKey());
        }
        Collections.sort(recentEmoji, -$$Lambda$Emoji$IRtAaHh32-YY7tgie8_WycuV8i0.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    static /* synthetic */ int lambda$sortEmoji$1(String str, String str2) {
        Integer num = (Integer) emojiUseHistory.get(str);
        Integer num2 = (Integer) emojiUseHistory.get(str2);
        Integer valueOf = Integer.valueOf(0);
        if (num == null) {
            num = valueOf;
        }
        if (num2 == null) {
            num2 = valueOf;
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
        String str = "filled_default";
        String str2 = "=";
        String str3 = ",";
        String str4 = "";
        String str5 = "emojis";
        if (!recentEmojiLoaded) {
            recentEmojiLoaded = true;
            SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
            int i = 0;
            try {
                emojiUseHistory.clear();
                int i2 = 4;
                String string;
                String[] split;
                if (globalEmojiSettings.contains(str5)) {
                    string = globalEmojiSettings.getString(str5, str4);
                    if (string != null && string.length() > 0) {
                        split = string.split(str3);
                        int length = split.length;
                        int i3 = 0;
                        while (i3 < length) {
                            String[] split2 = split[i3].split(str2);
                            long longValue = Utilities.parseLong(split2[i]).longValue();
                            StringBuilder stringBuilder = new StringBuilder();
                            String[] strArr = split;
                            long j = longValue;
                            int i4 = 0;
                            for (i2 = 
/*
Method generation error in method: org.telegram.messenger.Emoji.loadRecentEmoji():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r10_1 'i2' int) = (r10_0 'i2' int), (r10_7 'i2' int) binds: {(r10_0 'i2' int)=B:11:0x0031, (r10_7 'i2' int)=B:22:0x0080} in method: org.telegram.messenger.Emoji.loadRecentEmoji():void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:220)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeTryCatch(RegionGen.java:280)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:65)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 42 more

*/

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
        globalEmojiSettings.edit().putString("color", stringBuilder.toString()).commit();
    }
}
