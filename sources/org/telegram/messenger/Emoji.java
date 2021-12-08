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
import android.os.Build;
import android.text.Spannable;
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
        int a = 0;
        while (true) {
            Bitmap[][] bitmapArr = emojiBmp;
            if (a >= bitmapArr.length) {
                break;
            }
            int[] iArr = emojiCounts;
            bitmapArr[a] = new Bitmap[iArr[a]];
            loadingEmoji[a] = new boolean[iArr[a]];
            a++;
        }
        for (int j = 0; j < EmojiData.data.length; j++) {
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                rects.put(EmojiData.data[j][i], new DrawableInfo((byte) j, (short) i, i));
            }
        }
        Paint paint = new Paint();
        placeholderPaint = paint;
        paint.setColor(0);
    }

    public static void preloadEmoji(CharSequence code) {
        DrawableInfo info = getDrawableInfo(code);
        if (info != null) {
            loadEmoji(info.page, info.page2);
        }
    }

    /* access modifiers changed from: private */
    public static void loadEmoji(byte page, short page2) {
        if (emojiBmp[page][page2] == null) {
            boolean[][] zArr = loadingEmoji;
            if (!zArr[page][page2]) {
                zArr[page][page2] = true;
                Utilities.globalQueue.postRunnable(new Emoji$$ExternalSyntheticLambda0(page, page2));
            }
        }
    }

    static /* synthetic */ void lambda$loadEmoji$1(byte page, short page2) {
        loadEmojiInternal(page, page2);
        loadingEmoji[page][page2] = false;
    }

    private static void loadEmojiInternal(byte page, short page2) {
        int imageResize;
        Bitmap bitmap;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                imageResize = 2;
            } else {
                imageResize = 1;
            }
            bitmap = null;
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            InputStream is = assets.open("emoji/" + String.format(Locale.US, "%d_%d.png", new Object[]{Byte.valueOf(page), Short.valueOf(page2)}));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            bitmap = BitmapFactory.decodeStream(is, (Rect) null, opts);
            is.close();
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error loading emoji", x);
                return;
            }
            return;
        }
        emojiBmp[page][page2] = bitmap;
        AndroidUtilities.cancelRunOnUIThread(invalidateUiRunnable);
        AndroidUtilities.runOnUIThread(invalidateUiRunnable);
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) view;
            for (int i = 0; i < g.getChildCount(); i++) {
                invalidateAll(g.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    public static String fixEmoji(String emoji) {
        int length = emoji.length();
        int a = 0;
        while (a < length) {
            char ch = emoji.charAt(a);
            if (ch < 55356 || ch > 55358) {
                if (ch == 8419) {
                    return emoji;
                }
                if (ch >= 8252 && ch <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    emoji = emoji.substring(0, a + 1) + "️" + emoji.substring(a + 1);
                    length++;
                    a++;
                }
            } else if (ch != 55356 || a >= length - 1) {
                a++;
            } else {
                char ch2 = emoji.charAt(a + 1);
                if (ch2 == 56879 || ch2 == 56324 || ch2 == 56858 || ch2 == 56703) {
                    emoji = emoji.substring(0, a + 2) + "️" + emoji.substring(a + 2);
                    length++;
                    a += 2;
                } else {
                    a++;
                }
            }
            a++;
        }
        return emoji;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo info = getDrawableInfo(code);
        if (info == null) {
            return null;
        }
        EmojiDrawable ed = new EmojiDrawable(info);
        int i = drawImgSize;
        ed.setBounds(0, 0, i, i);
        return ed;
    }

    private static DrawableInfo getDrawableInfo(CharSequence code) {
        CharSequence newCode;
        DrawableInfo info = rects.get(code);
        if (info != null || (newCode = EmojiData.emojiAliasMap.get(code)) == null) {
            return info;
        }
        return rects.get(newCode);
    }

    public static boolean isValidEmoji(CharSequence code) {
        CharSequence newCode;
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        DrawableInfo info = rects.get(code);
        if (info == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            info = rects.get(newCode);
        }
        if (info != null) {
            return true;
        }
        return false;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        CharSequence newCode;
        EmojiDrawable ed = getEmojiDrawable(code);
        if (ed == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            ed = getEmojiDrawable(newCode);
        }
        if (ed == null) {
            return null;
        }
        int i = bigImgSize;
        ed.setBounds(0, 0, i, i);
        boolean unused = ed.fullSize = true;
        return ed;
    }

    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        /* access modifiers changed from: private */
        public boolean fullSize = false;
        private DrawableInfo info;

        public EmojiDrawable(DrawableInfo i) {
            this.info = i;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public Rect getDrawRect() {
            Rect original = getBounds();
            int cX = original.centerX();
            int cY = original.centerY();
            rect.left = cX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cX;
            rect.top = cY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cY;
            return rect;
        }

        public void draw(Canvas canvas) {
            Rect b;
            if (!isLoaded()) {
                Emoji.loadEmoji(this.info.page, this.info.page2);
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
                return;
            }
            if (this.fullSize) {
                b = getDrawRect();
            } else {
                b = getBounds();
            }
            if (!canvas.quickReject((float) b.left, (float) b.top, (float) b.right, (float) b.bottom, Canvas.EdgeType.AA)) {
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], (Rect) null, b, paint);
            }
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        public void setColorFilter(ColorFilter cf) {
        }

        public boolean isLoaded() {
            return Emoji.emojiBmp[this.info.page][this.info.page2] != null;
        }

        public void preload() {
            if (!isLoaded()) {
                Emoji.loadEmoji(this.info.page, this.info.page2);
            }
        }
    }

    private static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public short page2;

        public DrawableInfo(byte p, short p2, int index) {
            this.page = p;
            this.page2 = p2;
            this.emojiIndex = index;
        }
    }

    private static boolean inArray(char c, char[] a) {
        for (char cc : a) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, size, createNew, (int[]) null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly) {
        Spannable s;
        int length;
        long buf;
        int[] emojiOnly2;
        char c2;
        CharSequence charSequence = cs;
        if (SharedConfig.useSystemEmoji || charSequence == null) {
            int i = size;
        } else if (cs.length() == 0) {
            int i2 = size;
        } else {
            if (createNew || !(charSequence instanceof Spannable)) {
                s = Spannable.Factory.getInstance().newSpannable(cs.toString());
            } else {
                s = (Spannable) charSequence;
            }
            StringBuilder emojiCode = new StringBuilder(16);
            new StringBuilder(2);
            int length2 = cs.length();
            boolean doneEmoji = false;
            int startLength = 0;
            int[] emojiOnly3 = emojiOnly;
            int emojiCount = 0;
            int i3 = 0;
            int previousGoodIndex = 0;
            int startIndex = -1;
            long buf2 = 0;
            while (true) {
                if (i3 >= length2) {
                    int[] iArr = emojiOnly3;
                    long j = buf2;
                    int i4 = size;
                    break;
                }
                try {
                    boolean notOnlyEmoji = false;
                    char c = charSequence.charAt(i3);
                    if ((c < 55356 || c > 55358) && (buf2 == 0 || (buf2 & -4294967296L) != 0 || (buf2 & 65535) != 55356 || c < 56806 || c > 56831)) {
                        length = length2;
                        try {
                            if (emojiCode.length() > 0 && (c == 9792 || c == 9794 || c == 9877)) {
                                try {
                                    emojiCode.append(c);
                                    startLength++;
                                    buf2 = 0;
                                    doneEmoji = true;
                                } catch (Exception e) {
                                    e = e;
                                    long j2 = buf2;
                                    int i5 = length;
                                    int i6 = size;
                                    FileLog.e((Throwable) e);
                                    return charSequence;
                                }
                            } else if (buf2 > 0 && (61440 & c) == 53248) {
                                emojiCode.append(c);
                                startLength++;
                                buf2 = 0;
                                doneEmoji = true;
                            } else if (c != 8419) {
                                if (c == 169 || c == 174 || (c >= 8252 && c <= 12953)) {
                                    if (EmojiData.dataCharsMap.containsKey(Character.valueOf(c))) {
                                        if (startIndex == -1) {
                                            startIndex = i3;
                                        }
                                        startLength++;
                                        emojiCode.append(c);
                                        doneEmoji = true;
                                    }
                                }
                                if (startIndex != -1) {
                                    emojiCode.setLength(0);
                                    startIndex = -1;
                                    startLength = 0;
                                    doneEmoji = false;
                                } else if (c != 65039) {
                                    notOnlyEmoji = true;
                                }
                            } else if (i3 > 0 && (((c2 = charSequence.charAt(previousGoodIndex)) >= '0' && c2 <= '9') || c2 == '#' || c2 == '*')) {
                                startIndex = previousGoodIndex;
                                startLength = (i3 - previousGoodIndex) + 1;
                                emojiCode.append(c2);
                                emojiCode.append(c);
                                doneEmoji = true;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            int[] iArr2 = emojiOnly3;
                            long j3 = buf2;
                            int i7 = length;
                            int i8 = size;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    } else {
                        if (startIndex == -1) {
                            startIndex = i3;
                        }
                        try {
                            emojiCode.append(c);
                            startLength++;
                            length = length2;
                            buf2 = (buf2 << 16) | ((long) c);
                        } catch (Exception e3) {
                            e = e3;
                            long j4 = buf2;
                            int i9 = size;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    }
                    if (doneEmoji) {
                        length2 = length;
                        if (i3 + 2 < length2) {
                            try {
                                char next = charSequence.charAt(i3 + 1);
                                buf = buf2;
                                if (next == 55356) {
                                    try {
                                        char next2 = charSequence.charAt(i3 + 2);
                                        if (next2 >= 57339 && next2 <= 57343) {
                                            emojiCode.append(charSequence.subSequence(i3 + 1, i3 + 3));
                                            startLength += 2;
                                            i3 += 2;
                                        }
                                    } catch (Exception e4) {
                                        e = e4;
                                        int i10 = size;
                                        FileLog.e((Throwable) e);
                                        return charSequence;
                                    }
                                } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == 55356 && emojiCode.charAt(1) == 57332 && next == 56128) {
                                    int i11 = i3 + 1;
                                    do {
                                        emojiCode.append(charSequence.subSequence(i11, i11 + 2));
                                        startLength += 2;
                                        i11 += 2;
                                        if (i11 >= cs.length() || charSequence.charAt(i11) != 56128) {
                                            i3 = i11 - 1;
                                        }
                                        emojiCode.append(charSequence.subSequence(i11, i11 + 2));
                                        startLength += 2;
                                        i11 += 2;
                                        break;
                                    } while (charSequence.charAt(i11) != 56128);
                                    i3 = i11 - 1;
                                }
                            } catch (Exception e5) {
                                e = e5;
                                long j5 = buf2;
                                int i12 = size;
                                FileLog.e((Throwable) e);
                                return charSequence;
                            }
                        } else {
                            buf = buf2;
                        }
                    } else {
                        buf = buf2;
                        length2 = length;
                    }
                    previousGoodIndex = i3;
                    char prevCh = c;
                    for (int a = 0; a < 3; a++) {
                        if (i3 + 1 < length2) {
                            c = charSequence.charAt(i3 + 1);
                            if (a != 1) {
                                if (startIndex == -1 && prevCh != '*' && prevCh != '#') {
                                    if (prevCh >= '0') {
                                        if (prevCh <= '9') {
                                        }
                                    }
                                }
                                if (c >= 65024 && c <= 65039) {
                                    i3++;
                                    startLength++;
                                    if (!doneEmoji) {
                                        doneEmoji = i3 + 1 >= length2;
                                    }
                                }
                            } else if (c == 8205 && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i3++;
                                startLength++;
                                doneEmoji = false;
                                notOnlyEmoji = false;
                            }
                        }
                    }
                    if (notOnlyEmoji && emojiOnly3 != null) {
                        emojiOnly3[0] = 0;
                        emojiOnly3 = null;
                    }
                    if (!doneEmoji || i3 + 2 >= length2 || charSequence.charAt(i3 + 1) != 55356) {
                    } else {
                        char next3 = charSequence.charAt(i3 + 2);
                        if (next3 < 57339 || next3 > 57343) {
                        } else {
                            char c3 = prevCh;
                            emojiCode.append(charSequence.subSequence(i3 + 1, i3 + 3));
                            startLength += 2;
                            i3 += 2;
                        }
                    }
                    if (doneEmoji) {
                        if (emojiOnly3 != null) {
                            emojiOnly3[0] = emojiOnly3[0] + 1;
                        }
                        try {
                            CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                            EmojiDrawable drawable = getEmojiDrawable(code);
                            if (drawable != null) {
                                emojiOnly2 = emojiOnly3;
                                CharSequence charSequence2 = code;
                                char c4 = c;
                                try {
                                    s.setSpan(new EmojiSpan(drawable, 0, size, fontMetrics), startIndex, startIndex + startLength, 33);
                                    emojiCount++;
                                } catch (Exception e6) {
                                    e = e6;
                                    int[] iArr3 = emojiOnly2;
                                    FileLog.e((Throwable) e);
                                    return charSequence;
                                }
                            } else {
                                emojiOnly2 = emojiOnly3;
                                CharSequence charSequence3 = code;
                                char c5 = c;
                                int i13 = size;
                            }
                            startLength = 0;
                            startIndex = -1;
                            emojiCode.setLength(0);
                            doneEmoji = false;
                        } catch (Exception e7) {
                            e = e7;
                            int i14 = size;
                            int[] iArr4 = emojiOnly3;
                            FileLog.e((Throwable) e);
                            return charSequence;
                        }
                    } else {
                        int i15 = size;
                        emojiOnly2 = emojiOnly3;
                        char c6 = c;
                    }
                    if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29) && !BuildVars.DEBUG_PRIVATE_VERSION && emojiCount >= 50) {
                        emojiOnly3 = emojiOnly2;
                        break;
                    }
                    i3++;
                    emojiOnly3 = emojiOnly2;
                    buf2 = buf;
                } catch (Exception e8) {
                    e = e8;
                    int[] iArr5 = emojiOnly3;
                    long j6 = buf2;
                    int i16 = size;
                    FileLog.e((Throwable) e);
                    return charSequence;
                }
            }
            if (!(emojiOnly3 == null || emojiCode.length() == 0)) {
                emojiOnly3[0] = 0;
            }
            return s;
        }
        return charSequence;
    }

    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(EmojiDrawable d, int verticalAlignment, int s, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            this.fontMetrics = original;
            if (original != null) {
                int abs = Math.abs(original.descent) + Math.abs(this.fontMetrics.ascent);
                this.size = abs;
                if (abs == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
            this.fontMetrics = newMetrics;
            this.size = newSize;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }
            Paint.FontMetricsInt fontMetricsInt = this.fontMetrics;
            if (fontMetricsInt == null) {
                int sz = super.getSize(paint, text, start, end, fm);
                int offset = AndroidUtilities.dp(8.0f);
                int w = AndroidUtilities.dp(10.0f);
                fm.top = (-w) - offset;
                fm.bottom = w - offset;
                fm.ascent = (-w) - offset;
                fm.leading = 0;
                fm.descent = w - offset;
                return sz;
            }
            if (fm != null) {
                fm.ascent = fontMetricsInt.ascent;
                fm.descent = this.fontMetrics.descent;
                fm.top = this.fontMetrics.top;
                fm.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i = this.size;
                drawable.setBounds(0, 0, i, i);
            }
            return this.size;
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            boolean restoreAlpha = false;
            if (paint.getAlpha() != 255) {
                restoreAlpha = true;
                getDrawable().setAlpha(paint.getAlpha());
            }
            boolean needRestore = false;
            if (Emoji.emojiDrawingYOffset != 0.0f) {
                needRestore = true;
                canvas.save();
                canvas.translate(0.0f, Emoji.emojiDrawingYOffset);
            }
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            if (needRestore) {
                canvas.restore();
            }
            if (restoreAlpha) {
                getDrawable().setAlpha(255);
            }
        }
    }

    public static void addRecentEmoji(String code) {
        Integer count = emojiUseHistory.get(code);
        if (count == null) {
            count = 0;
        }
        if (count.intValue() == 0 && emojiUseHistory.size() >= 48) {
            ArrayList<String> arrayList = recentEmoji;
            emojiUseHistory.remove(arrayList.get(arrayList.size() - 1));
            ArrayList<String> arrayList2 = recentEmoji;
            arrayList2.set(arrayList2.size() - 1, code);
        }
        HashMap<String, Integer> hashMap = emojiUseHistory;
        Integer valueOf = Integer.valueOf(count.intValue() + 1);
        Integer count2 = valueOf;
        hashMap.put(code, valueOf);
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, Emoji$$ExternalSyntheticLambda2.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    static /* synthetic */ int lambda$sortEmoji$2(String lhs, String rhs) {
        Integer count1 = emojiUseHistory.get(lhs);
        Integer count2 = emojiUseHistory.get(rhs);
        if (count1 == null) {
            count1 = 0;
        }
        if (count2 == null) {
            count2 = 0;
        }
        if (count1.intValue() > count2.intValue()) {
            return -1;
        }
        if (count1.intValue() < count2.intValue()) {
            return 1;
        }
        return 0;
    }

    public static void saveRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    public static void clearRecentEmoji() {
        MessagesController.getGlobalEmojiSettings().edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    /* JADX WARNING: Removed duplicated region for block: B:66:0x022b A[Catch:{ Exception -> 0x0249 }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void loadRecentEmoji() {
        /*
            java.lang.String r0 = "filled_default"
            java.lang.String r1 = "="
            java.lang.String r2 = ","
            java.lang.String r3 = ""
            java.lang.String r4 = "emojis"
            boolean r5 = recentEmojiLoaded
            if (r5 == 0) goto L_0x000f
            return
        L_0x000f:
            r5 = 1
            recentEmojiLoaded = r5
            android.content.SharedPreferences r6 = org.telegram.messenger.MessagesController.getGlobalEmojiSettings()
            r7 = 0
            java.util.HashMap<java.lang.String, java.lang.Integer> r8 = emojiUseHistory     // Catch:{ Exception -> 0x0218 }
            r8.clear()     // Catch:{ Exception -> 0x0218 }
            boolean r8 = r6.contains(r4)     // Catch:{ Exception -> 0x0218 }
            r10 = 4
            if (r8 == 0) goto L_0x00be
            java.lang.String r8 = r6.getString(r4, r3)     // Catch:{ Exception -> 0x00ba }
            if (r8 == 0) goto L_0x009e
            int r11 = r8.length()     // Catch:{ Exception -> 0x00ba }
            if (r11 <= 0) goto L_0x009e
            java.lang.String[] r11 = r8.split(r2)     // Catch:{ Exception -> 0x00ba }
            int r12 = r11.length     // Catch:{ Exception -> 0x00ba }
            r13 = 0
        L_0x0035:
            if (r13 >= r12) goto L_0x0099
            r14 = r11[r13]     // Catch:{ Exception -> 0x00ba }
            java.lang.String[] r15 = r14.split(r1)     // Catch:{ Exception -> 0x00ba }
            r16 = r15[r7]     // Catch:{ Exception -> 0x00ba }
            java.lang.Long r16 = org.telegram.messenger.Utilities.parseLong(r16)     // Catch:{ Exception -> 0x00ba }
            long r16 = r16.longValue()     // Catch:{ Exception -> 0x00ba }
            java.lang.StringBuilder r18 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ba }
            r18.<init>()     // Catch:{ Exception -> 0x00ba }
            r19 = r18
            r18 = 0
            r20 = r6
            r5 = r16
            r9 = r18
        L_0x0056:
            if (r9 >= r10) goto L_0x0074
            int r10 = (int) r5
            char r10 = (char) r10
            r18 = r8
            r8 = r19
            r8.insert(r7, r10)     // Catch:{ Exception -> 0x00b5 }
            r16 = 16
            long r5 = r5 >> r16
            r21 = 0
            int r19 = (r5 > r21 ? 1 : (r5 == r21 ? 0 : -1))
            if (r19 != 0) goto L_0x006c
            goto L_0x0078
        L_0x006c:
            int r9 = r9 + 1
            r19 = r8
            r8 = r18
            r10 = 4
            goto L_0x0056
        L_0x0074:
            r18 = r8
            r8 = r19
        L_0x0078:
            int r9 = r8.length()     // Catch:{ Exception -> 0x00b5 }
            if (r9 <= 0) goto L_0x008f
            java.util.HashMap<java.lang.String, java.lang.Integer> r9 = emojiUseHistory     // Catch:{ Exception -> 0x00b5 }
            java.lang.String r10 = r8.toString()     // Catch:{ Exception -> 0x00b5 }
            r19 = 1
            r21 = r15[r19]     // Catch:{ Exception -> 0x00b5 }
            java.lang.Integer r7 = org.telegram.messenger.Utilities.parseInt(r21)     // Catch:{ Exception -> 0x00b5 }
            r9.put(r10, r7)     // Catch:{ Exception -> 0x00b5 }
        L_0x008f:
            int r13 = r13 + 1
            r8 = r18
            r6 = r20
            r5 = 1
            r7 = 0
            r10 = 4
            goto L_0x0035
        L_0x0099:
            r20 = r6
            r18 = r8
            goto L_0x00a2
        L_0x009e:
            r20 = r6
            r18 = r8
        L_0x00a2:
            android.content.SharedPreferences$Editor r5 = r20.edit()     // Catch:{ Exception -> 0x00b5 }
            android.content.SharedPreferences$Editor r4 = r5.remove(r4)     // Catch:{ Exception -> 0x00b5 }
            r4.commit()     // Catch:{ Exception -> 0x00b5 }
            saveRecentEmoji()     // Catch:{ Exception -> 0x00b5 }
            r8 = r18
            r5 = r20
            goto L_0x00f2
        L_0x00b5:
            r0 = move-exception
            r5 = r20
            goto L_0x021a
        L_0x00ba:
            r0 = move-exception
            r5 = r6
            goto L_0x021a
        L_0x00be:
            r20 = r6
            java.lang.String r4 = "emojis2"
            r5 = r20
            java.lang.String r4 = r5.getString(r4, r3)     // Catch:{ Exception -> 0x0212 }
            r8 = r4
            if (r8 == 0) goto L_0x00f2
            int r4 = r8.length()     // Catch:{ Exception -> 0x0212 }
            if (r4 <= 0) goto L_0x00f2
            java.lang.String[] r4 = r8.split(r2)     // Catch:{ Exception -> 0x0212 }
            int r6 = r4.length     // Catch:{ Exception -> 0x0212 }
            r7 = 0
        L_0x00d7:
            if (r7 >= r6) goto L_0x00f2
            r9 = r4[r7]     // Catch:{ Exception -> 0x0212 }
            java.lang.String[] r10 = r9.split(r1)     // Catch:{ Exception -> 0x0212 }
            java.util.HashMap<java.lang.String, java.lang.Integer> r11 = emojiUseHistory     // Catch:{ Exception -> 0x0212 }
            r12 = 0
            r13 = r10[r12]     // Catch:{ Exception -> 0x0212 }
            r12 = 1
            r14 = r10[r12]     // Catch:{ Exception -> 0x0212 }
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r14)     // Catch:{ Exception -> 0x0212 }
            r11.put(r13, r12)     // Catch:{ Exception -> 0x0212 }
            int r7 = r7 + 1
            goto L_0x00d7
        L_0x00f2:
            java.util.HashMap<java.lang.String, java.lang.Integer> r4 = emojiUseHistory     // Catch:{ Exception -> 0x0212 }
            boolean r4 = r4.isEmpty()     // Catch:{ Exception -> 0x0212 }
            if (r4 == 0) goto L_0x020e
            r4 = 0
            boolean r6 = r5.getBoolean(r0, r4)     // Catch:{ Exception -> 0x0212 }
            if (r6 != 0) goto L_0x020e
            r4 = 34
            java.lang.String[] r4 = new java.lang.String[r4]     // Catch:{ Exception -> 0x0212 }
            java.lang.String r6 = "😂"
            r7 = 0
            r4[r7] = r6     // Catch:{ Exception -> 0x0212 }
            java.lang.String r6 = "😘"
            r7 = 1
            r4[r7] = r6     // Catch:{ Exception -> 0x0212 }
            r6 = 2
            java.lang.String r7 = "❤"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 3
            java.lang.String r7 = "😍"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            java.lang.String r6 = "😊"
            r7 = 4
            r4[r7] = r6     // Catch:{ Exception -> 0x0212 }
            r6 = 5
            java.lang.String r7 = "😁"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 6
            java.lang.String r7 = "👍"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 7
            java.lang.String r7 = "☺"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 8
            java.lang.String r7 = "😔"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 9
            java.lang.String r7 = "😄"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 10
            java.lang.String r7 = "😭"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 11
            java.lang.String r7 = "💋"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 12
            java.lang.String r7 = "😒"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 13
            java.lang.String r7 = "😳"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 14
            java.lang.String r7 = "😜"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 15
            java.lang.String r7 = "🙈"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            java.lang.String r6 = "😉"
            r7 = 16
            r4[r7] = r6     // Catch:{ Exception -> 0x0212 }
            r6 = 17
            java.lang.String r7 = "😃"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 18
            java.lang.String r7 = "😢"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 19
            java.lang.String r7 = "😝"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 20
            java.lang.String r7 = "😱"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 21
            java.lang.String r7 = "😡"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 22
            java.lang.String r7 = "😏"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 23
            java.lang.String r7 = "😞"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 24
            java.lang.String r7 = "😅"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 25
            java.lang.String r7 = "😚"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 26
            java.lang.String r7 = "🙊"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 27
            java.lang.String r7 = "😌"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 28
            java.lang.String r7 = "😀"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 29
            java.lang.String r7 = "😋"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 30
            java.lang.String r7 = "😆"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 31
            java.lang.String r7 = "👌"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 32
            java.lang.String r7 = "😐"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 33
            java.lang.String r7 = "😕"
            r4[r6] = r7     // Catch:{ Exception -> 0x0212 }
            r6 = 0
        L_0x01ec:
            int r7 = r4.length     // Catch:{ Exception -> 0x0212 }
            if (r6 >= r7) goto L_0x01ff
            java.util.HashMap<java.lang.String, java.lang.Integer> r7 = emojiUseHistory     // Catch:{ Exception -> 0x0212 }
            r9 = r4[r6]     // Catch:{ Exception -> 0x0212 }
            int r10 = r4.length     // Catch:{ Exception -> 0x0212 }
            int r10 = r10 - r6
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0212 }
            r7.put(r9, r10)     // Catch:{ Exception -> 0x0212 }
            int r6 = r6 + 1
            goto L_0x01ec
        L_0x01ff:
            android.content.SharedPreferences$Editor r6 = r5.edit()     // Catch:{ Exception -> 0x0212 }
            r7 = 1
            android.content.SharedPreferences$Editor r0 = r6.putBoolean(r0, r7)     // Catch:{ Exception -> 0x0212 }
            r0.commit()     // Catch:{ Exception -> 0x0212 }
            saveRecentEmoji()     // Catch:{ Exception -> 0x0212 }
        L_0x020e:
            sortEmoji()     // Catch:{ Exception -> 0x0212 }
            goto L_0x021d
        L_0x0212:
            r0 = move-exception
            goto L_0x021a
        L_0x0214:
            r0 = move-exception
            r5 = r20
            goto L_0x021a
        L_0x0218:
            r0 = move-exception
            r5 = r6
        L_0x021a:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021d:
            java.lang.String r0 = "color"
            java.lang.String r0 = r5.getString(r0, r3)     // Catch:{ Exception -> 0x0249 }
            if (r0 == 0) goto L_0x0248
            int r3 = r0.length()     // Catch:{ Exception -> 0x0249 }
            if (r3 <= 0) goto L_0x0248
            java.lang.String[] r2 = r0.split(r2)     // Catch:{ Exception -> 0x0249 }
            r3 = 0
        L_0x0230:
            int r4 = r2.length     // Catch:{ Exception -> 0x0249 }
            if (r3 >= r4) goto L_0x0248
            r4 = r2[r3]     // Catch:{ Exception -> 0x0249 }
            java.lang.String[] r6 = r4.split(r1)     // Catch:{ Exception -> 0x0249 }
            java.util.HashMap<java.lang.String, java.lang.String> r7 = emojiColor     // Catch:{ Exception -> 0x0249 }
            r8 = 0
            r9 = r6[r8]     // Catch:{ Exception -> 0x0249 }
            r10 = 1
            r11 = r6[r10]     // Catch:{ Exception -> 0x0249 }
            r7.put(r9, r11)     // Catch:{ Exception -> 0x0249 }
            int r3 = r3 + 1
            goto L_0x0230
        L_0x0248:
            goto L_0x024d
        L_0x0249:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x024d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.loadRecentEmoji():void");
    }

    public static void saveEmojiColors() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("color", stringBuilder.toString()).commit();
    }
}
