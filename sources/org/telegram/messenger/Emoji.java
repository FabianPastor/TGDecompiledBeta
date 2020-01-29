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
import java.util.Map;
import org.telegram.messenger.Emoji;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    /* access modifiers changed from: private */
    public static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    /* access modifiers changed from: private */
    public static int drawImgSize = AndroidUtilities.dp(20.0f);
    /* access modifiers changed from: private */
    public static Bitmap[][] emojiBmp = new Bitmap[8][];
    public static HashMap<String, String> emojiColor = new HashMap<>();
    private static int[] emojiCounts = {1620, 184, 115, 328, 125, 206, 288, 258};
    public static HashMap<String, Integer> emojiUseHistory = new HashMap<>();
    private static boolean inited = false;
    private static Runnable invalidateUiRunnable = $$Lambda$Emoji$evx_fdAb4NaXQ9KHlOHuPrE3OTE.INSTANCE;
    /* access modifiers changed from: private */
    public static boolean[][] loadingEmoji = new boolean[8][];
    /* access modifiers changed from: private */
    public static Paint placeholderPaint = new Paint();
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
        placeholderPaint.setColor(0);
    }

    /* access modifiers changed from: private */
    public static void loadEmoji(byte b, short s) {
        int i;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                i = 2;
            } else {
                if (AndroidUtilities.density > 1.5f) {
                    int i2 = (AndroidUtilities.density > 2.0f ? 1 : (AndroidUtilities.density == 2.0f ? 0 : -1));
                }
                i = 1;
            }
            for (int i3 = 13; i3 < 16; i3++) {
                File fileStreamPath = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(i3), Float.valueOf(2.0f), Byte.valueOf(b)}));
                if (fileStreamPath.exists()) {
                    fileStreamPath.delete();
                }
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
            InputStream open = assets.open("emoji/" + String.format(Locale.US, "%d_%d.png", new Object[]{Byte.valueOf(b), Short.valueOf(s)}));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i;
            bitmap = BitmapFactory.decodeStream(open, (Rect) null, options);
            open.close();
        } catch (Throwable th2) {
            FileLog.e(th2);
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
        String str2 = str;
        int i2 = 0;
        while (i < length) {
            char charAt = str2.charAt(i);
            if (charAt < 55356 || charAt > 55358) {
                if (charAt == 8419) {
                    return str2;
                }
                if (charAt >= 8252 && charAt <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    StringBuilder sb = new StringBuilder();
                    i++;
                    sb.append(str2.substring(0, i));
                    sb.append("️");
                    sb.append(str2.substring(i));
                    str2 = sb.toString();
                }
                i2 = i + 1;
            } else if (charAt != 55356 || i >= length - 1) {
                i++;
                i2 = i + 1;
            } else {
                int i3 = i + 1;
                char charAt2 = str2.charAt(i3);
                if (charAt2 == 56879 || charAt2 == 56324 || charAt2 == 56858 || charAt2 == 56703) {
                    StringBuilder sb2 = new StringBuilder();
                    i += 2;
                    sb2.append(str2.substring(0, i));
                    sb2.append("️");
                    sb2.append(str2.substring(i));
                    str2 = sb2.toString();
                } else {
                    i = i3;
                    i2 = i + 1;
                }
            }
            length++;
            i2 = i + 1;
        }
        return str2;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence charSequence) {
        CharSequence charSequence2;
        DrawableInfo drawableInfo = rects.get(charSequence);
        if (drawableInfo == null && (charSequence2 = EmojiData.emojiAliasMap.get(charSequence)) != null) {
            drawableInfo = rects.get(charSequence2);
        }
        if (drawableInfo != null) {
            EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
            int i = drawImgSize;
            emojiDrawable.setBounds(0, 0, i, i);
            return emojiDrawable;
        } else if (!BuildVars.LOGS_ENABLED) {
            return null;
        } else {
            FileLog.d("No drawable for emoji " + charSequence);
            return null;
        }
    }

    public static boolean isValidEmoji(CharSequence charSequence) {
        CharSequence charSequence2;
        DrawableInfo drawableInfo = rects.get(charSequence);
        if (drawableInfo == null && (charSequence2 = EmojiData.emojiAliasMap.get(charSequence)) != null) {
            drawableInfo = rects.get(charSequence2);
        }
        return drawableInfo != null;
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
            Rect rect2;
            Bitmap[][] access$300 = Emoji.emojiBmp;
            DrawableInfo drawableInfo = this.info;
            if (access$300[drawableInfo.page][drawableInfo.page2] == null) {
                boolean[][] access$400 = Emoji.loadingEmoji;
                DrawableInfo drawableInfo2 = this.info;
                if (!access$400[drawableInfo2.page][drawableInfo2.page2]) {
                    boolean[][] access$4002 = Emoji.loadingEmoji;
                    DrawableInfo drawableInfo3 = this.info;
                    access$4002[drawableInfo3.page][drawableInfo3.page2] = true;
                    Utilities.globalQueue.postRunnable(new Runnable() {
                        public final void run() {
                            Emoji.EmojiDrawable.this.lambda$draw$0$Emoji$EmojiDrawable();
                        }
                    });
                    canvas.drawRect(getBounds(), Emoji.placeholderPaint);
                    return;
                }
                return;
            }
            if (this.fullSize) {
                rect2 = getDrawRect();
            } else {
                rect2 = getBounds();
            }
            Bitmap[][] access$3002 = Emoji.emojiBmp;
            DrawableInfo drawableInfo4 = this.info;
            canvas.drawBitmap(access$3002[drawableInfo4.page][drawableInfo4.page2], (Rect) null, rect2, paint);
        }

        public /* synthetic */ void lambda$draw$0$Emoji$EmojiDrawable() {
            DrawableInfo drawableInfo = this.info;
            Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
            boolean[][] access$400 = Emoji.loadingEmoji;
            DrawableInfo drawableInfo2 = this.info;
            access$400[drawableInfo2.page][drawableInfo2.page2] = false;
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

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, (int[]) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:113:0x0194 A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:149:0x0206 A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0240 A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0258 A[LOOP:0: B:11:0x003e->B:165:0x0258, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0269 A[EDGE_INSN: B:172:0x0269->B:169:0x0269 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007a A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x010f A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0117 A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0138 A[Catch:{ Exception -> 0x0264 }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0150 A[Catch:{ Exception -> 0x0264 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r23, android.graphics.Paint.FontMetricsInt r24, int r25, boolean r26, int[] r27) {
        /*
            r1 = r23
            boolean r0 = org.telegram.messenger.SharedConfig.useSystemEmoji
            if (r0 != 0) goto L_0x026a
            if (r1 == 0) goto L_0x026a
            int r0 = r23.length()
            if (r0 != 0) goto L_0x0010
            goto L_0x026a
        L_0x0010:
            if (r26 != 0) goto L_0x001a
            boolean r0 = r1 instanceof android.text.Spannable
            if (r0 == 0) goto L_0x001a
            r0 = r1
            android.text.Spannable r0 = (android.text.Spannable) r0
            goto L_0x0026
        L_0x001a:
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            java.lang.String r2 = r23.toString()
            android.text.Spannable r0 = r0.newSpannable(r2)
        L_0x0026:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 16
            r2.<init>(r3)
            int r4 = r23.length()
            r5 = 0
            r15 = r27
            r10 = r5
            r9 = 0
            r12 = -1
            r13 = 0
            r14 = 0
            r16 = 0
            r17 = 0
        L_0x003e:
            if (r9 >= r4) goto L_0x0269
            char r8 = r1.charAt(r9)     // Catch:{ Exception -> 0x0264 }
            r3 = 55356(0xd83c, float:7.757E-41)
            r7 = 1
            if (r8 < r3) goto L_0x0052
            r3 = 55358(0xd83e, float:7.7573E-41)
            if (r8 <= r3) goto L_0x0050
            goto L_0x0052
        L_0x0050:
            r3 = -1
            goto L_0x0078
        L_0x0052:
            int r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x0088
            r19 = -4294967296(0xfffffffvar_, double:NaN)
            long r19 = r10 & r19
            int r3 = (r19 > r5 ? 1 : (r19 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0088
            r19 = 65535(0xffff, double:3.23786E-319)
            long r19 = r10 & r19
            r21 = 55356(0xd83c, double:2.73495E-319)
            int r3 = (r19 > r21 ? 1 : (r19 == r21 ? 0 : -1))
            if (r3 != 0) goto L_0x0088
            r3 = 56806(0xdde6, float:7.9602E-41)
            if (r8 < r3) goto L_0x0088
            r3 = 56831(0xddff, float:7.9637E-41)
            if (r8 > r3) goto L_0x0088
            goto L_0x0050
        L_0x0078:
            if (r12 != r3) goto L_0x007b
            r12 = r9
        L_0x007b:
            r2.append(r8)     // Catch:{ Exception -> 0x0264 }
            int r3 = r13 + 1
            r18 = 16
            long r10 = r10 << r18
            long r13 = (long) r8     // Catch:{ Exception -> 0x0264 }
            long r10 = r10 | r13
            goto L_0x0124
        L_0x0088:
            r18 = 16
            int r3 = r2.length()     // Catch:{ Exception -> 0x0264 }
            if (r3 <= 0) goto L_0x00a6
            r3 = 9792(0x2640, float:1.3722E-41)
            if (r8 == r3) goto L_0x009c
            r3 = 9794(0x2642, float:1.3724E-41)
            if (r8 == r3) goto L_0x009c
            r3 = 9877(0x2695, float:1.384E-41)
            if (r8 != r3) goto L_0x00a6
        L_0x009c:
            r2.append(r8)     // Catch:{ Exception -> 0x0264 }
            int r3 = r13 + 1
            r10 = r5
        L_0x00a2:
            r16 = 1
            goto L_0x0124
        L_0x00a6:
            int r3 = (r10 > r5 ? 1 : (r10 == r5 ? 0 : -1))
            if (r3 <= 0) goto L_0x00bb
            r3 = 61440(0xvar_, float:8.6096E-41)
            r3 = r3 & r8
            r5 = 53248(0xd000, float:7.4616E-41)
            if (r3 != r5) goto L_0x00bb
            r2.append(r8)     // Catch:{ Exception -> 0x0264 }
            int r3 = r13 + 1
            r10 = 0
            goto L_0x00a2
        L_0x00bb:
            r3 = 8419(0x20e3, float:1.1798E-41)
            if (r8 != r3) goto L_0x00e3
            if (r9 <= 0) goto L_0x0123
            char r3 = r1.charAt(r14)     // Catch:{ Exception -> 0x0264 }
            r5 = 48
            if (r3 < r5) goto L_0x00cd
            r5 = 57
            if (r3 <= r5) goto L_0x00d5
        L_0x00cd:
            r5 = 35
            if (r3 == r5) goto L_0x00d5
            r5 = 42
            if (r3 != r5) goto L_0x0123
        L_0x00d5:
            int r5 = r9 - r14
            int r5 = r5 + r7
            r2.append(r3)     // Catch:{ Exception -> 0x0264 }
            r2.append(r8)     // Catch:{ Exception -> 0x0264 }
            r13 = r5
            r12 = r14
            r16 = 1
            goto L_0x0123
        L_0x00e3:
            r3 = 169(0xa9, float:2.37E-43)
            if (r8 == r3) goto L_0x00f6
            r3 = 174(0xae, float:2.44E-43)
            if (r8 == r3) goto L_0x00f6
            r3 = 8252(0x203c, float:1.1564E-41)
            if (r8 < r3) goto L_0x00f4
            r3 = 12953(0x3299, float:1.8151E-41)
            if (r8 > r3) goto L_0x00f4
            goto L_0x00f6
        L_0x00f4:
            r3 = -1
            goto L_0x010d
        L_0x00f6:
            java.util.HashMap<java.lang.Character, java.lang.Boolean> r3 = org.telegram.messenger.EmojiData.dataCharsMap     // Catch:{ Exception -> 0x0264 }
            java.lang.Character r5 = java.lang.Character.valueOf(r8)     // Catch:{ Exception -> 0x0264 }
            boolean r3 = r3.containsKey(r5)     // Catch:{ Exception -> 0x0264 }
            if (r3 == 0) goto L_0x00f4
            r3 = -1
            if (r12 != r3) goto L_0x0106
            r12 = r9
        L_0x0106:
            int r5 = r13 + 1
            r2.append(r8)     // Catch:{ Exception -> 0x0264 }
            r3 = r5
            goto L_0x00a2
        L_0x010d:
            if (r12 == r3) goto L_0x0117
            r3 = 0
            r2.setLength(r3)     // Catch:{ Exception -> 0x0264 }
            r12 = -1
            r16 = 0
            goto L_0x0124
        L_0x0117:
            r3 = 0
            r5 = 65039(0xfe0f, float:9.1139E-41)
            if (r8 == r5) goto L_0x0123
            if (r15 == 0) goto L_0x0123
            r15[r3] = r3     // Catch:{ Exception -> 0x0264 }
            r3 = 0
            r15 = r3
        L_0x0123:
            r3 = r13
        L_0x0124:
            r6 = 57339(0xdffb, float:8.0349E-41)
            if (r16 == 0) goto L_0x018d
            int r13 = r9 + 2
            if (r13 >= r4) goto L_0x018d
            int r14 = r9 + 1
            char r7 = r1.charAt(r14)     // Catch:{ Exception -> 0x0264 }
            r5 = 55356(0xd83c, float:7.757E-41)
            if (r7 != r5) goto L_0x0150
            char r5 = r1.charAt(r13)     // Catch:{ Exception -> 0x0264 }
            if (r5 < r6) goto L_0x018d
            r7 = 57343(0xdfff, float:8.0355E-41)
            if (r5 > r7) goto L_0x018d
            int r9 = r9 + 3
            java.lang.CharSequence r5 = r1.subSequence(r14, r9)     // Catch:{ Exception -> 0x0264 }
            r2.append(r5)     // Catch:{ Exception -> 0x0264 }
            int r3 = r3 + 2
            r14 = r13
            goto L_0x018e
        L_0x0150:
            int r5 = r2.length()     // Catch:{ Exception -> 0x0264 }
            r13 = 2
            if (r5 < r13) goto L_0x018d
            r5 = 0
            char r6 = r2.charAt(r5)     // Catch:{ Exception -> 0x0264 }
            r5 = 55356(0xd83c, float:7.757E-41)
            if (r6 != r5) goto L_0x018d
            r5 = 1
            char r6 = r2.charAt(r5)     // Catch:{ Exception -> 0x0264 }
            r5 = 57332(0xdff4, float:8.0339E-41)
            if (r6 != r5) goto L_0x018d
            r5 = 56128(0xdb40, float:7.8652E-41)
            if (r7 != r5) goto L_0x018d
        L_0x0170:
            int r6 = r14 + 2
            java.lang.CharSequence r7 = r1.subSequence(r14, r6)     // Catch:{ Exception -> 0x0264 }
            r2.append(r7)     // Catch:{ Exception -> 0x0264 }
            int r3 = r3 + r13
            int r7 = r23.length()     // Catch:{ Exception -> 0x0264 }
            if (r6 >= r7) goto L_0x0189
            char r7 = r1.charAt(r6)     // Catch:{ Exception -> 0x0264 }
            if (r7 == r5) goto L_0x0187
            goto L_0x0189
        L_0x0187:
            r14 = r6
            goto L_0x0170
        L_0x0189:
            int r6 = r6 + -1
            r14 = r6
            goto L_0x018e
        L_0x018d:
            r14 = r9
        L_0x018e:
            r6 = r3
            r5 = r14
            r3 = 0
        L_0x0191:
            r7 = 3
            if (r3 >= r7) goto L_0x01d8
            int r7 = r5 + 1
            if (r7 >= r4) goto L_0x01d2
            char r9 = r1.charAt(r7)     // Catch:{ Exception -> 0x0264 }
            r13 = 1
            if (r3 != r13) goto L_0x01b5
            r13 = 8205(0x200d, float:1.1498E-41)
            if (r9 != r13) goto L_0x01d2
            int r13 = r2.length()     // Catch:{ Exception -> 0x0264 }
            if (r13 <= 0) goto L_0x01d2
            r2.append(r9)     // Catch:{ Exception -> 0x0264 }
            int r6 = r6 + 1
            r5 = r7
            r13 = 65039(0xfe0f, float:9.1139E-41)
            r16 = 0
            goto L_0x01d5
        L_0x01b5:
            r13 = -1
            if (r12 != r13) goto L_0x01c4
            r13 = 42
            if (r8 == r13) goto L_0x01c4
            r13 = 49
            if (r8 < r13) goto L_0x01d2
            r13 = 57
            if (r8 > r13) goto L_0x01d2
        L_0x01c4:
            r13 = 65024(0xfe00, float:9.1118E-41)
            if (r9 < r13) goto L_0x01d2
            r13 = 65039(0xfe0f, float:9.1139E-41)
            if (r9 > r13) goto L_0x01d5
            int r6 = r6 + 1
            r5 = r7
            goto L_0x01d5
        L_0x01d2:
            r13 = 65039(0xfe0f, float:9.1139E-41)
        L_0x01d5:
            int r3 = r3 + 1
            goto L_0x0191
        L_0x01d8:
            if (r16 == 0) goto L_0x0203
            int r3 = r5 + 2
            if (r3 >= r4) goto L_0x0203
            int r7 = r5 + 1
            char r8 = r1.charAt(r7)     // Catch:{ Exception -> 0x0264 }
            r9 = 55356(0xd83c, float:7.757E-41)
            if (r8 != r9) goto L_0x0203
            char r8 = r1.charAt(r3)     // Catch:{ Exception -> 0x0264 }
            r9 = 57339(0xdffb, float:8.0349E-41)
            if (r8 < r9) goto L_0x0203
            r9 = 57343(0xdfff, float:8.0355E-41)
            if (r8 > r9) goto L_0x0203
            int r5 = r5 + 3
            java.lang.CharSequence r5 = r1.subSequence(r7, r5)     // Catch:{ Exception -> 0x0264 }
            r2.append(r5)     // Catch:{ Exception -> 0x0264 }
            int r6 = r6 + 2
            goto L_0x0204
        L_0x0203:
            r3 = r5
        L_0x0204:
            if (r16 == 0) goto L_0x0240
            if (r15 == 0) goto L_0x0210
            r5 = 0
            r7 = r15[r5]     // Catch:{ Exception -> 0x0264 }
            r8 = 1
            int r7 = r7 + r8
            r15[r5] = r7     // Catch:{ Exception -> 0x0264 }
            goto L_0x0211
        L_0x0210:
            r5 = 0
        L_0x0211:
            int r7 = r2.length()     // Catch:{ Exception -> 0x0264 }
            java.lang.CharSequence r7 = r2.subSequence(r5, r7)     // Catch:{ Exception -> 0x0264 }
            org.telegram.messenger.Emoji$EmojiDrawable r7 = getEmojiDrawable(r7)     // Catch:{ Exception -> 0x0264 }
            if (r7 == 0) goto L_0x0232
            org.telegram.messenger.Emoji$EmojiSpan r8 = new org.telegram.messenger.Emoji$EmojiSpan     // Catch:{ Exception -> 0x0264 }
            r9 = r24
            r13 = r25
            r8.<init>(r7, r5, r13, r9)     // Catch:{ Exception -> 0x0264 }
            int r6 = r6 + r12
            r5 = 33
            r0.setSpan(r8, r12, r6, r5)     // Catch:{ Exception -> 0x0264 }
            int r17 = r17 + 1
            r5 = 0
            goto L_0x0236
        L_0x0232:
            r9 = r24
            r13 = r25
        L_0x0236:
            r2.setLength(r5)     // Catch:{ Exception -> 0x0264 }
            r7 = r17
            r6 = 0
            r12 = -1
            r16 = 0
            goto L_0x0247
        L_0x0240:
            r9 = r24
            r13 = r25
            r5 = 0
            r7 = r17
        L_0x0247:
            int r8 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0264 }
            r5 = 23
            if (r8 < r5) goto L_0x0253
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0264 }
            r8 = 29
            if (r5 < r8) goto L_0x0258
        L_0x0253:
            r5 = 50
            if (r7 < r5) goto L_0x0258
            goto L_0x0269
        L_0x0258:
            r5 = 1
            int r3 = r3 + r5
            r9 = r3
            r13 = r6
            r17 = r7
            r3 = 16
            r5 = 0
            goto L_0x003e
        L_0x0264:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return r1
        L_0x0269:
            return r0
        L_0x026a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.replaceEmoji(java.lang.CharSequence, android.graphics.Paint$FontMetricsInt, int, boolean, int[]):java.lang.CharSequence");
    }

    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(EmojiDrawable emojiDrawable, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            super(emojiDrawable, i);
            this.fontMetrics = fontMetricsInt;
            if (fontMetricsInt != null) {
                this.size = Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent);
                if (this.size == 0) {
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
            if (fontMetricsInt != null) {
                fontMetricsInt.ascent = fontMetricsInt2.ascent;
                fontMetricsInt.descent = fontMetricsInt2.descent;
                fontMetricsInt.top = fontMetricsInt2.top;
                fontMetricsInt.bottom = fontMetricsInt2.bottom;
            }
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i5 = this.size;
                drawable.setBounds(0, 0, i5, i5);
            }
            return this.size;
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
            recentEmoji.add(key.getKey());
        }
        Collections.sort(recentEmoji, $$Lambda$Emoji$IRtAaHh32YY7tgie8_WycuV8i0.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    static /* synthetic */ int lambda$sortEmoji$1(String str, String str2) {
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
            char c = 0;
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
                            long longValue = Utilities.parseLong(split2[c]).longValue();
                            StringBuilder sb = new StringBuilder();
                            String[] strArr = split;
                            long j = longValue;
                            int i3 = 0;
                            while (true) {
                                if (i3 >= i) {
                                    break;
                                }
                                sb.insert(0, (char) ((int) j));
                                j >>= 16;
                                if (j == 0) {
                                    break;
                                }
                                i3++;
                                i = 4;
                            }
                            if (sb.length() > 0) {
                                emojiUseHistory.put(sb.toString(), Utilities.parseInt(split2[1]));
                            }
                            i2++;
                            split = strArr;
                            c = 0;
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
                            emojiUseHistory.put(split4[0], Utilities.parseInt(split4[1]));
                        }
                    }
                }
                if (emojiUseHistory.isEmpty() && !globalEmojiSettings.getBoolean("filled_default", false)) {
                    String[] strArr2 = {"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                    for (int i4 = 0; i4 < strArr2.length; i4++) {
                        emojiUseHistory.put(strArr2[i4], Integer.valueOf(strArr2.length - i4));
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
