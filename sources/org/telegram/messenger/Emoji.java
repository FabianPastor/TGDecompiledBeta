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
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    private static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    private static int drawImgSize = AndroidUtilities.dp(20.0f);
    private static Bitmap[][] emojiBmp = new Bitmap[8][];
    public static HashMap<String, String> emojiColor = new HashMap();
    private static int[] emojiCounts = new int[]{1620, 184, 115, 328, 125, 206, 288, 258};
    public static HashMap<String, Integer> emojiUseHistory = new HashMap();
    private static boolean inited = false;
    private static Runnable invalidateUiRunnable = -$$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE.INSTANCE;
    private static boolean[][] loadingEmoji = new boolean[8][];
    private static Paint placeholderPaint = new Paint();
    public static ArrayList<String> recentEmoji = new ArrayList();
    private static boolean recentEmojiLoaded;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();

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
            canvas.drawBitmap(access$3002[drawableInfo2.page][drawableInfo2.page2], null, drawRect, paint);
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
        for (i = 0; i < EmojiData.data.length; i++) {
            int i2 = 0;
            while (true) {
                String[][] strArr = EmojiData.data;
                if (i2 >= strArr[i].length) {
                    break;
                }
                rects.put(strArr[i][i2], new DrawableInfo((byte) i, (short) i2, i2));
                i2++;
            }
        }
        placeholderPaint.setColor(0);
    }

    private static void loadEmoji(byte b, short s) {
        int i;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                i = 2;
            } else {
                if (AndroidUtilities.density > 1.5f) {
                    i = (AndroidUtilities.density > 2.0f ? 1 : (AndroidUtilities.density == 2.0f ? 0 : -1));
                }
                i = 1;
            }
            for (int i2 = 13; i2 < 16; i2++) {
                File fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(i2), Float.valueOf(2.0f), Byte.valueOf(b)}));
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
        Rect rect = null;
        try {
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("emoji/");
            stringBuilder.append(String.format(Locale.US, "%d_%d.png", new Object[]{Byte.valueOf(b), Short.valueOf(s)}));
            InputStream open = assets.open(stringBuilder.toString());
            Options options = new Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i;
            rect = BitmapFactory.decodeStream(open, null, options);
            open.close();
        } catch (Throwable th2) {
            FileLog.e(th2);
        }
        emojiBmp[b][s] = rect;
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

    public static boolean isValidEmoji(CharSequence charSequence) {
        DrawableInfo drawableInfo = (DrawableInfo) rects.get(charSequence);
        if (drawableInfo == null) {
            charSequence = (CharSequence) EmojiData.emojiAliasMap.get(charSequence);
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

    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0129 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0117 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x010f A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0129 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0129 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007a A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:85:0x0129 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x01da A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A:{Catch:{ Exception -> 0x0264 }} */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x024d A:{Catch:{ Exception -> 0x0264 }} */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r23, android.graphics.Paint.FontMetricsInt r24, int r25, boolean r26, int[] r27) {
        /*
        r1 = r23;
        r0 = org.telegram.messenger.SharedConfig.useSystemEmoji;
        if (r0 != 0) goto L_0x026a;
    L_0x0006:
        if (r1 == 0) goto L_0x026a;
    L_0x0008:
        r0 = r23.length();
        if (r0 != 0) goto L_0x0010;
    L_0x000e:
        goto L_0x026a;
    L_0x0010:
        if (r26 != 0) goto L_0x001a;
    L_0x0012:
        r0 = r1 instanceof android.text.Spannable;
        if (r0 == 0) goto L_0x001a;
    L_0x0016:
        r0 = r1;
        r0 = (android.text.Spannable) r0;
        goto L_0x0026;
    L_0x001a:
        r0 = android.text.Spannable.Factory.getInstance();
        r2 = r23.toString();
        r0 = r0.newSpannable(r2);
    L_0x0026:
        r2 = new java.lang.StringBuilder;
        r3 = 16;
        r2.<init>(r3);
        r4 = r23.length();
        r5 = 0;
        r15 = r27;
        r10 = r5;
        r9 = 0;
        r12 = -1;
        r13 = 0;
        r14 = 0;
        r16 = 0;
        r17 = 0;
    L_0x003e:
        if (r9 >= r4) goto L_0x0269;
    L_0x0040:
        r8 = r1.charAt(r9);	 Catch:{ Exception -> 0x0264 }
        r3 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r7 = 1;
        if (r8 < r3) goto L_0x0052;
    L_0x004a:
        r3 = 55358; // 0xd83e float:7.7573E-41 double:2.73505E-319;
        if (r8 <= r3) goto L_0x0050;
    L_0x004f:
        goto L_0x0052;
    L_0x0050:
        r3 = -1;
        goto L_0x0078;
    L_0x0052:
        r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1));
        if (r3 == 0) goto L_0x0088;
    L_0x0056:
        r19 = -NUM; // 0xfffffffvar_ float:0.0 double:NaN;
        r19 = r10 & r19;
        r3 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1));
        if (r3 != 0) goto L_0x0088;
    L_0x0061:
        r19 = 65535; // 0xffff float:9.1834E-41 double:3.23786E-319;
        r19 = r10 & r19;
        r21 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        r3 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1));
        if (r3 != 0) goto L_0x0088;
    L_0x006d:
        r3 = 56806; // 0xdde6 float:7.9602E-41 double:2.8066E-319;
        if (r8 < r3) goto L_0x0088;
    L_0x0072:
        r3 = 56831; // 0xddff float:7.9637E-41 double:2.8078E-319;
        if (r8 > r3) goto L_0x0088;
    L_0x0077:
        goto L_0x0050;
    L_0x0078:
        if (r12 != r3) goto L_0x007b;
    L_0x007a:
        r12 = r9;
    L_0x007b:
        r2.append(r8);	 Catch:{ Exception -> 0x0264 }
        r3 = r13 + 1;
        r18 = 16;
        r10 = r10 << r18;
        r13 = (long) r8;	 Catch:{ Exception -> 0x0264 }
        r10 = r10 | r13;
        goto L_0x0124;
    L_0x0088:
        r18 = 16;
        r3 = r2.length();	 Catch:{ Exception -> 0x0264 }
        if (r3 <= 0) goto L_0x00a6;
    L_0x0090:
        r3 = 9792; // 0x2640 float:1.3722E-41 double:4.838E-320;
        if (r8 == r3) goto L_0x009c;
    L_0x0094:
        r3 = 9794; // 0x2642 float:1.3724E-41 double:4.839E-320;
        if (r8 == r3) goto L_0x009c;
    L_0x0098:
        r3 = 9877; // 0x2695 float:1.384E-41 double:4.88E-320;
        if (r8 != r3) goto L_0x00a6;
    L_0x009c:
        r2.append(r8);	 Catch:{ Exception -> 0x0264 }
        r3 = r13 + 1;
        r10 = r5;
    L_0x00a2:
        r16 = 1;
        goto L_0x0124;
    L_0x00a6:
        r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1));
        if (r3 <= 0) goto L_0x00bb;
    L_0x00aa:
        r3 = 61440; // 0xvar_ float:8.6096E-41 double:3.03554E-319;
        r3 = r3 & r8;
        r5 = 53248; // 0xd000 float:7.4616E-41 double:2.6308E-319;
        if (r3 != r5) goto L_0x00bb;
    L_0x00b3:
        r2.append(r8);	 Catch:{ Exception -> 0x0264 }
        r3 = r13 + 1;
        r10 = 0;
        goto L_0x00a2;
    L_0x00bb:
        r3 = 8419; // 0x20e3 float:1.1798E-41 double:4.1595E-320;
        if (r8 != r3) goto L_0x00e3;
    L_0x00bf:
        if (r9 <= 0) goto L_0x0123;
    L_0x00c1:
        r3 = r1.charAt(r14);	 Catch:{ Exception -> 0x0264 }
        r5 = 48;
        if (r3 < r5) goto L_0x00cd;
    L_0x00c9:
        r5 = 57;
        if (r3 <= r5) goto L_0x00d5;
    L_0x00cd:
        r5 = 35;
        if (r3 == r5) goto L_0x00d5;
    L_0x00d1:
        r5 = 42;
        if (r3 != r5) goto L_0x0123;
    L_0x00d5:
        r5 = r9 - r14;
        r5 = r5 + r7;
        r2.append(r3);	 Catch:{ Exception -> 0x0264 }
        r2.append(r8);	 Catch:{ Exception -> 0x0264 }
        r13 = r5;
        r12 = r14;
        r16 = 1;
        goto L_0x0123;
    L_0x00e3:
        r3 = 169; // 0xa9 float:2.37E-43 double:8.35E-322;
        if (r8 == r3) goto L_0x00f6;
    L_0x00e7:
        r3 = 174; // 0xae float:2.44E-43 double:8.6E-322;
        if (r8 == r3) goto L_0x00f6;
    L_0x00eb:
        r3 = 8252; // 0x203c float:1.1564E-41 double:4.077E-320;
        if (r8 < r3) goto L_0x00f4;
    L_0x00ef:
        r3 = 12953; // 0x3299 float:1.8151E-41 double:6.3996E-320;
        if (r8 > r3) goto L_0x00f4;
    L_0x00f3:
        goto L_0x00f6;
    L_0x00f4:
        r3 = -1;
        goto L_0x010d;
    L_0x00f6:
        r3 = org.telegram.messenger.EmojiData.dataCharsMap;	 Catch:{ Exception -> 0x0264 }
        r5 = java.lang.Character.valueOf(r8);	 Catch:{ Exception -> 0x0264 }
        r3 = r3.containsKey(r5);	 Catch:{ Exception -> 0x0264 }
        if (r3 == 0) goto L_0x00f4;
    L_0x0102:
        r3 = -1;
        if (r12 != r3) goto L_0x0106;
    L_0x0105:
        r12 = r9;
    L_0x0106:
        r5 = r13 + 1;
        r2.append(r8);	 Catch:{ Exception -> 0x0264 }
        r3 = r5;
        goto L_0x00a2;
    L_0x010d:
        if (r12 == r3) goto L_0x0117;
    L_0x010f:
        r3 = 0;
        r2.setLength(r3);	 Catch:{ Exception -> 0x0264 }
        r12 = -1;
        r16 = 0;
        goto L_0x0124;
    L_0x0117:
        r3 = 0;
        r5 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r8 == r5) goto L_0x0123;
    L_0x011d:
        if (r15 == 0) goto L_0x0123;
    L_0x011f:
        r15[r3] = r3;	 Catch:{ Exception -> 0x0264 }
        r3 = 0;
        r15 = r3;
    L_0x0123:
        r3 = r13;
    L_0x0124:
        r6 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r16 == 0) goto L_0x018d;
    L_0x0129:
        r13 = r9 + 2;
        if (r13 >= r4) goto L_0x018d;
    L_0x012d:
        r14 = r9 + 1;
        r7 = r1.charAt(r14);	 Catch:{ Exception -> 0x0264 }
        r5 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r7 != r5) goto L_0x0150;
    L_0x0138:
        r5 = r1.charAt(r13);	 Catch:{ Exception -> 0x0264 }
        if (r5 < r6) goto L_0x018d;
    L_0x013e:
        r7 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r5 > r7) goto L_0x018d;
    L_0x0143:
        r9 = r9 + 3;
        r5 = r1.subSequence(r14, r9);	 Catch:{ Exception -> 0x0264 }
        r2.append(r5);	 Catch:{ Exception -> 0x0264 }
        r3 = r3 + 2;
        r14 = r13;
        goto L_0x018e;
    L_0x0150:
        r5 = r2.length();	 Catch:{ Exception -> 0x0264 }
        r13 = 2;
        if (r5 < r13) goto L_0x018d;
    L_0x0157:
        r5 = 0;
        r6 = r2.charAt(r5);	 Catch:{ Exception -> 0x0264 }
        r5 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r6 != r5) goto L_0x018d;
    L_0x0161:
        r5 = 1;
        r6 = r2.charAt(r5);	 Catch:{ Exception -> 0x0264 }
        r5 = 57332; // 0xdff4 float:8.0339E-41 double:2.8326E-319;
        if (r6 != r5) goto L_0x018d;
    L_0x016b:
        r5 = 56128; // 0xdb40 float:7.8652E-41 double:2.7731E-319;
        if (r7 != r5) goto L_0x018d;
    L_0x0170:
        r6 = r14 + 2;
        r7 = r1.subSequence(r14, r6);	 Catch:{ Exception -> 0x0264 }
        r2.append(r7);	 Catch:{ Exception -> 0x0264 }
        r3 = r3 + r13;
        r7 = r23.length();	 Catch:{ Exception -> 0x0264 }
        if (r6 >= r7) goto L_0x0189;
    L_0x0180:
        r7 = r1.charAt(r6);	 Catch:{ Exception -> 0x0264 }
        if (r7 == r5) goto L_0x0187;
    L_0x0186:
        goto L_0x0189;
    L_0x0187:
        r14 = r6;
        goto L_0x0170;
    L_0x0189:
        r6 = r6 + -1;
        r14 = r6;
        goto L_0x018e;
    L_0x018d:
        r14 = r9;
    L_0x018e:
        r6 = r3;
        r5 = r14;
        r3 = 0;
    L_0x0191:
        r7 = 3;
        if (r3 >= r7) goto L_0x01d8;
    L_0x0194:
        r7 = r5 + 1;
        if (r7 >= r4) goto L_0x01d2;
    L_0x0198:
        r9 = r1.charAt(r7);	 Catch:{ Exception -> 0x0264 }
        r13 = 1;
        if (r3 != r13) goto L_0x01b5;
    L_0x019f:
        r13 = 8205; // 0x200d float:1.1498E-41 double:4.054E-320;
        if (r9 != r13) goto L_0x01d2;
    L_0x01a3:
        r13 = r2.length();	 Catch:{ Exception -> 0x0264 }
        if (r13 <= 0) goto L_0x01d2;
    L_0x01a9:
        r2.append(r9);	 Catch:{ Exception -> 0x0264 }
        r6 = r6 + 1;
        r5 = r7;
        r13 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        r16 = 0;
        goto L_0x01d5;
    L_0x01b5:
        r13 = -1;
        if (r12 != r13) goto L_0x01c4;
    L_0x01b8:
        r13 = 42;
        if (r8 == r13) goto L_0x01c4;
    L_0x01bc:
        r13 = 49;
        if (r8 < r13) goto L_0x01d2;
    L_0x01c0:
        r13 = 57;
        if (r8 > r13) goto L_0x01d2;
    L_0x01c4:
        r13 = 65024; // 0xfe00 float:9.1118E-41 double:3.2126E-319;
        if (r9 < r13) goto L_0x01d2;
    L_0x01c9:
        r13 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
        if (r9 > r13) goto L_0x01d5;
    L_0x01ce:
        r6 = r6 + 1;
        r5 = r7;
        goto L_0x01d5;
    L_0x01d2:
        r13 = 65039; // 0xfe0f float:9.1139E-41 double:3.21335E-319;
    L_0x01d5:
        r3 = r3 + 1;
        goto L_0x0191;
    L_0x01d8:
        if (r16 == 0) goto L_0x0203;
    L_0x01da:
        r3 = r5 + 2;
        if (r3 >= r4) goto L_0x0203;
    L_0x01de:
        r7 = r5 + 1;
        r8 = r1.charAt(r7);	 Catch:{ Exception -> 0x0264 }
        r9 = 55356; // 0xd83c float:7.757E-41 double:2.73495E-319;
        if (r8 != r9) goto L_0x0203;
    L_0x01e9:
        r8 = r1.charAt(r3);	 Catch:{ Exception -> 0x0264 }
        r9 = 57339; // 0xdffb float:8.0349E-41 double:2.8329E-319;
        if (r8 < r9) goto L_0x0203;
    L_0x01f2:
        r9 = 57343; // 0xdfff float:8.0355E-41 double:2.8331E-319;
        if (r8 > r9) goto L_0x0203;
    L_0x01f7:
        r5 = r5 + 3;
        r5 = r1.subSequence(r7, r5);	 Catch:{ Exception -> 0x0264 }
        r2.append(r5);	 Catch:{ Exception -> 0x0264 }
        r6 = r6 + 2;
        goto L_0x0204;
    L_0x0203:
        r3 = r5;
    L_0x0204:
        if (r16 == 0) goto L_0x0240;
    L_0x0206:
        if (r15 == 0) goto L_0x0210;
    L_0x0208:
        r5 = 0;
        r7 = r15[r5];	 Catch:{ Exception -> 0x0264 }
        r8 = 1;
        r7 = r7 + r8;
        r15[r5] = r7;	 Catch:{ Exception -> 0x0264 }
        goto L_0x0211;
    L_0x0210:
        r5 = 0;
    L_0x0211:
        r7 = r2.length();	 Catch:{ Exception -> 0x0264 }
        r7 = r2.subSequence(r5, r7);	 Catch:{ Exception -> 0x0264 }
        r7 = getEmojiDrawable(r7);	 Catch:{ Exception -> 0x0264 }
        if (r7 == 0) goto L_0x0232;
    L_0x021f:
        r8 = new org.telegram.messenger.Emoji$EmojiSpan;	 Catch:{ Exception -> 0x0264 }
        r9 = r24;
        r13 = r25;
        r8.<init>(r7, r5, r13, r9);	 Catch:{ Exception -> 0x0264 }
        r6 = r6 + r12;
        r5 = 33;
        r0.setSpan(r8, r12, r6, r5);	 Catch:{ Exception -> 0x0264 }
        r17 = r17 + 1;
        r5 = 0;
        goto L_0x0236;
    L_0x0232:
        r9 = r24;
        r13 = r25;
    L_0x0236:
        r2.setLength(r5);	 Catch:{ Exception -> 0x0264 }
        r7 = r17;
        r6 = 0;
        r12 = -1;
        r16 = 0;
        goto L_0x0247;
    L_0x0240:
        r9 = r24;
        r13 = r25;
        r5 = 0;
        r7 = r17;
    L_0x0247:
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0264 }
        r5 = 23;
        if (r8 < r5) goto L_0x0253;
    L_0x024d:
        r5 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0264 }
        r8 = 29;
        if (r5 < r8) goto L_0x0258;
    L_0x0253:
        r5 = 50;
        if (r7 < r5) goto L_0x0258;
    L_0x0257:
        goto L_0x0269;
    L_0x0258:
        r5 = 1;
        r3 = r3 + r5;
        r9 = r3;
        r13 = r6;
        r17 = r7;
        r3 = 16;
        r5 = 0;
        goto L_0x003e;
    L_0x0264:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        return r1;
    L_0x0269:
        return r0;
    L_0x026a:
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
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r10_1 'i2' int) = (r10_0 'i2' int), (r10_7 'i2' int) binds: {(r10_0 'i2' int)=B:11:0x0031, (r10_7 'i2' int)=B:22:0x007c} in method: org.telegram.messenger.Emoji.loadRecentEmoji():void, dex: classes.dex
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
