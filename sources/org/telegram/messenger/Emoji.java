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
    private static final int[][] cols = new int[][]{new int[]{16, 16, 16, 16}, new int[]{6, 6, 6, 6}, new int[]{9, 9, 9, 9}, new int[]{9, 9, 9, 9}, new int[]{10, 10, 10, 10}};
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

        public int compare(String lhs, String rhs) {
            Integer count1 = (Integer) Emoji.emojiUseHistory.get(lhs);
            Integer count2 = (Integer) Emoji.emojiUseHistory.get(rhs);
            if (count1 == null) {
                count1 = Integer.valueOf(0);
            }
            if (count2 == null) {
                count2 = Integer.valueOf(0);
            }
            if (count1.intValue() > count2.intValue()) {
                return -1;
            }
            if (count1.intValue() < count2.intValue()) {
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

        public DrawableInfo(Rect r, byte p, byte p2, int index) {
            this.rect = r;
            this.page = p;
            this.page2 = p2;
            this.emojiIndex = index;
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
            if (Emoji.emojiBmp[this.info.page][this.info.page2] != null) {
                Rect b;
                if (this.fullSize) {
                    b = getDrawRect();
                } else {
                    b = getBounds();
                }
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, b, paint);
            } else if (!Emoji.loadingEmoji[this.info.page][this.info.page2]) {
                Emoji.loadingEmoji[this.info.page][this.info.page2] = true;
                Utilities.globalQueue.postRunnable(new C01491());
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
            }
        }

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter cf) {
        }
    }

    public static class EmojiSpan extends ImageSpan {
        private FontMetricsInt fontMetrics = null;
        private int size = AndroidUtilities.dp(20.0f);

        public EmojiSpan(EmojiDrawable d, int verticalAlignment, int s, FontMetricsInt original) {
            super(d, verticalAlignment);
            this.fontMetrics = original;
            if (original != null) {
                this.size = Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent);
                if (this.size == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(FontMetricsInt newMetrics, int newSize) {
            this.fontMetrics = newMetrics;
            this.size = newSize;
        }

        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
            if (fm == null) {
                fm = new FontMetricsInt();
            }
            if (this.fontMetrics == null) {
                int sz = super.getSize(paint, text, start, end, fm);
                int offset = AndroidUtilities.dp(NUM);
                int w = AndroidUtilities.dp(NUM);
                fm.top = (-w) - offset;
                fm.bottom = w - offset;
                fm.ascent = (-w) - offset;
                fm.leading = 0;
                fm.descent = w - offset;
                return sz;
            }
            if (fm != null) {
                fm.ascent = this.fontMetrics.ascent;
                fm.descent = this.fontMetrics.descent;
                fm.top = this.fontMetrics.top;
                fm.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, this.size, this.size);
            }
            return this.size;
        }
    }

    public static native Object[] getSuggestion(String str);

    static {
        int emojiFullSize;
        int add = 2;
        if (AndroidUtilities.density <= 1.0f) {
            emojiFullSize = 32;
            add = 1;
        } else if (AndroidUtilities.density <= 1.5f) {
            emojiFullSize = 64;
        } else if (AndroidUtilities.density <= 2.0f) {
            emojiFullSize = 64;
        } else {
            emojiFullSize = 64;
        }
        for (int j = 0; j < EmojiData.data.length; j++) {
            int count2 = (int) Math.ceil((double) (((float) EmojiData.data[j].length) / 4.0f));
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                int page = i / count2;
                int position = i - (page * count2);
                int row = position % cols[j][page];
                int col = position / cols[j][page];
                rects.put(EmojiData.data[j][i], new DrawableInfo(new Rect((row * emojiFullSize) + (row * add), (col * emojiFullSize) + (col * add), ((row + 1) * emojiFullSize) + (row * add), ((col + 1) * emojiFullSize) + (col * add)), (byte) j, (byte) page, i));
            }
        }
        placeholderPaint.setColor(0);
    }

    private static void loadEmoji(final int page, final int page2) {
        int imageResize = 1;
        try {
            File imageFile;
            float scale = 2.0f;
            if (AndroidUtilities.density <= 1.0f) {
                scale = 2.0f;
                imageResize = 2;
            } else if (AndroidUtilities.density <= 1.5f) {
                scale = 2.0f;
            } else if (AndroidUtilities.density <= 2.0f) {
                scale = 2.0f;
            }
            for (int a = 4; a < 7; a++) {
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            for (Exception e = 8; e < 12; e++) {
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(e), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        } catch (Throwable e2) {
            FileLog.m3e(e2);
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m2e("Error loading emoji", x);
                return;
            }
            return;
        }
        Bitmap bitmap = null;
        try {
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("emoji/");
            stringBuilder.append(String.format(Locale.US, "v12_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(scale), Integer.valueOf(page), Integer.valueOf(page2)}));
            InputStream is = assets.open(stringBuilder.toString());
            Options opts = new Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            is.close();
        } catch (Throwable e22) {
            FileLog.m3e(e22);
        }
        final Bitmap finalBitmap = bitmap;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                Emoji.emojiBmp[page][page2] = finalBitmap;
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
            }
        });
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
        int lenght = emoji.length();
        String emoji2 = emoji;
        int a = 0;
        while (a < lenght) {
            char ch = emoji2.charAt(a);
            StringBuilder stringBuilder;
            if (ch < '\ud83c' || ch > '\ud83e') {
                if (ch == '\u20e3') {
                    return emoji2;
                }
                if (ch >= '\u203c' && ch <= '\u3299' && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(emoji2.substring(0, a + 1));
                    stringBuilder.append("\ufe0f");
                    stringBuilder.append(emoji2.substring(a + 1));
                    emoji2 = stringBuilder.toString();
                    lenght++;
                    a++;
                }
            } else if (ch != '\ud83c' || a >= lenght - 1) {
                a++;
            } else {
                ch = emoji2.charAt(a + 1);
                if (!(ch == '\ude2f' || ch == '\udc04' || ch == '\ude1a')) {
                    if (ch != '\udd7f') {
                        a++;
                    }
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(emoji2.substring(0, a + 2));
                stringBuilder.append("\ufe0f");
                stringBuilder.append(emoji2.substring(a + 2));
                emoji2 = stringBuilder.toString();
                lenght++;
                a += 2;
            }
            a++;
        }
        return emoji2;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo info = (DrawableInfo) rects.get(code);
        if (info == null) {
            CharSequence newCode = (CharSequence) EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                info = (DrawableInfo) rects.get(newCode);
            }
        }
        if (info == null) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No drawable for emoji ");
                stringBuilder.append(code);
                FileLog.m0d(stringBuilder.toString());
            }
            return null;
        }
        EmojiDrawable ed = new EmojiDrawable(info);
        ed.setBounds(0, 0, drawImgSize, drawImgSize);
        return ed;
    }

    public static boolean isValidEmoji(String code) {
        DrawableInfo info = (DrawableInfo) rects.get(code);
        if (info == null) {
            CharSequence newCode = (CharSequence) EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                info = (DrawableInfo) rects.get(newCode);
            }
        }
        return info != null;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        EmojiDrawable ed = getEmojiDrawable(code);
        if (ed == null) {
            CharSequence newCode = (CharSequence) EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                ed = getEmojiDrawable(newCode);
            }
        }
        if (ed == null) {
            return null;
        }
        ed.setBounds(0, 0, bigImgSize, bigImgSize);
        ed.fullSize = true;
        return ed;
    }

    private static boolean inArray(char c, char[] a) {
        for (char cc : a) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence replaceEmoji(CharSequence cs, FontMetricsInt fontMetrics, int size, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, size, createNew, null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly) {
        Throwable e;
        CharSequence charSequence = cs;
        if (!(SharedConfig.useSystemEmoji || charSequence == null)) {
            if (cs.length() != 0) {
                Spannable s;
                if (createNew || !(charSequence instanceof Spannable)) {
                    s = Factory.getInstance().newSpannable(cs.toString());
                } else {
                    s = (Spannable) charSequence;
                }
                int startIndex = -1;
                int startLength = 0;
                int previousGoodIndex = 0;
                StringBuilder emojiCode = new StringBuilder(16);
                StringBuilder addionalCode = new StringBuilder(2);
                int length = cs.length();
                boolean doneEmoji = false;
                int[] emojiOnly2 = emojiOnly;
                int emojiCount = 0;
                long buf = 0;
                int i = 0;
                while (i < length) {
                    long j;
                    char c2;
                    int a;
                    int emojiCount2;
                    char c = charSequence.charAt(i);
                    if ((c >= '\ud83c' && c <= '\ud83e') || (buf != 0 && (buf & -4294967296L) == 0 && (buf & 65535) == 55356 && c >= '\udde6' && c <= '\uddff')) {
                        if (startIndex == -1) {
                            startIndex = i;
                        }
                        try {
                            emojiCode.append(c);
                            buf = (buf << 16) | ((long) c);
                            startIndex = startIndex;
                            startLength++;
                        } catch (Throwable e2) {
                            int i2 = startIndex;
                            e = e2;
                            j = buf;
                            buf = size;
                        }
                    } else if (emojiCode.length() > 0 && (c == '\u2640' || c == '\u2642' || c == '\u2695')) {
                        emojiCode.append(c);
                        startLength++;
                        buf = 0;
                        doneEmoji = true;
                    } else if (buf > 0 && (61440 & c) == 53248) {
                        emojiCode.append(c);
                        startLength++;
                        buf = 0;
                        doneEmoji = true;
                    } else if (c != '\u20e3') {
                        if (c == '\u00a9' || c == '\u00ae' || (c >= '\u203c' && c <= '\u3299')) {
                            try {
                                if (EmojiData.dataCharsMap.containsKey(Character.valueOf(c))) {
                                    if (startIndex == -1) {
                                        startIndex = i;
                                    }
                                    startLength++;
                                    try {
                                        emojiCode.append(c);
                                        doneEmoji = true;
                                    } catch (Throwable e22) {
                                        e = e22;
                                        j = buf;
                                        buf = size;
                                    }
                                }
                            } catch (Throwable e222) {
                                j = buf;
                                buf = size;
                                e = e222;
                            }
                        }
                        if (startIndex != -1) {
                            emojiCode.setLength(0);
                            startIndex = -1;
                            startLength = 0;
                            doneEmoji = false;
                        } else if (!(c == '\ufe0f' || emojiOnly2 == null)) {
                            emojiOnly2[0] = 0;
                            emojiOnly2 = null;
                        }
                    } else if (i > 0) {
                        c2 = charSequence.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = (i - previousGoodIndex) + 1;
                            emojiCode.append(c2);
                            emojiCode.append(c);
                            doneEmoji = true;
                        }
                    }
                    if (doneEmoji && i + 2 < length) {
                        c2 = charSequence.charAt(i + 1);
                        if (c2 == '\ud83c') {
                            c2 = charSequence.charAt(i + 2);
                            if (c2 >= '\udffb' && c2 <= '\udfff') {
                                emojiCode.append(charSequence.subSequence(i + 1, i + 3));
                                startLength += 2;
                                i += 2;
                            }
                        } else if (emojiCode.length() >= 2 && emojiCode.charAt(0) == '\ud83c' && emojiCode.charAt(1) == '\udff4' && c2 == '\udb40') {
                            i++;
                            do {
                                emojiCode.append(charSequence.subSequence(i, i + 2));
                                startLength += 2;
                                i += 2;
                                if (i >= cs.length()) {
                                    break;
                                }
                            } while (charSequence.charAt(i) == '\udb40');
                            i--;
                        }
                    }
                    previousGoodIndex = i;
                    for (a = 0; a < 3; a++) {
                        if (i + 1 < length) {
                            c = charSequence.charAt(i + 1);
                            if (a == 1) {
                                if (c == '\u200d' && emojiCode.length() > 0) {
                                    emojiCode.append(c);
                                    i++;
                                    startLength++;
                                    doneEmoji = false;
                                }
                            } else if (c >= '\ufe00') {
                                if (c <= '\ufe0f') {
                                    i++;
                                    startLength++;
                                }
                            }
                        }
                    }
                    if (doneEmoji && i + 2 < length && charSequence.charAt(i + 1) == '\ud83c') {
                        char next = charSequence.charAt(i + 2);
                        if (next >= '\udffb' && next <= '\udfff') {
                            emojiCode.append(charSequence.subSequence(i + 1, i + 3));
                            startLength += 2;
                            i += 2;
                        }
                    }
                    if (doneEmoji) {
                        if (emojiOnly2 != null) {
                            emojiOnly2[0] = emojiOnly2[0] + 1;
                        }
                        CharSequence code = emojiCode.subSequence(0, emojiCode.length());
                        EmojiDrawable drawable = getEmojiDrawable(code);
                        if (drawable != null) {
                            j = buf;
                            try {
                                s.setSpan(new EmojiSpan(drawable, 0, size, fontMetrics), startIndex, startIndex + startLength, 33);
                                emojiCount++;
                            } catch (Throwable e2222) {
                                e = e2222;
                            }
                        } else {
                            j = buf;
                            buf = size;
                        }
                        startLength = 0;
                        startIndex = -1;
                        a = 0;
                        emojiCode.setLength(0);
                        doneEmoji = false;
                        emojiCount2 = emojiCount;
                    } else {
                        j = buf;
                        a = 0;
                        buf = size;
                        emojiCount2 = emojiCount;
                    }
                    try {
                        if (VERSION.SDK_INT < 23 && emojiCount2 >= 50) {
                            emojiCount = emojiCount2;
                            break;
                        }
                        i++;
                        int i3 = a;
                        emojiCount = emojiCount2;
                        buf = j;
                    } catch (Throwable e22222) {
                        e = e22222;
                        emojiCount = emojiCount2;
                    }
                }
                buf = size;
                return s;
            }
        }
        int i4 = size;
        return charSequence;
        FileLog.m3e(e);
        return charSequence;
    }

    public static void addRecentEmoji(String code) {
        Integer count = (Integer) emojiUseHistory.get(code);
        if (count == null) {
            count = Integer.valueOf(0);
        }
        if (count.intValue() == 0 && emojiUseHistory.size() > 50) {
            for (int a = recentEmoji.size() - 1; a >= 0; a--) {
                emojiUseHistory.remove((String) recentEmoji.get(a));
                recentEmoji.remove(a);
                if (emojiUseHistory.size() <= 50) {
                    break;
                }
            }
        }
        HashMap hashMap = emojiUseHistory;
        Integer valueOf = Integer.valueOf(count.intValue() + 1);
        count = valueOf;
        hashMap.put(code, valueOf);
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, new C01482());
        while (recentEmoji.size() > 50) {
            recentEmoji.remove(recentEmoji.size() - 1);
        }
    }

    public static void saveRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
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

    public static void loadRecentEmoji() {
        Throwable e;
        SharedPreferences preferences;
        String str;
        String[] args;
        int a;
        if (!recentEmojiLoaded) {
            String[] args2;
            recentEmojiLoaded = true;
            SharedPreferences preferences2 = MessagesController.getGlobalEmojiSettings();
            try {
                emojiUseHistory.clear();
                long j = 16;
                String str2;
                if (preferences2.contains("emojis")) {
                    str2 = preferences2.getString("emojis", TtmlNode.ANONYMOUS_REGION_ID);
                    if (str2 != null) {
                        try {
                            if (str2.length() > 0) {
                                args2 = str2.split(",");
                                int length = args2.length;
                                int i = 0;
                                while (i < length) {
                                    String[] args22 = args2[i].split("=");
                                    long value = Utilities.parseLong(args22[0]).longValue();
                                    StringBuilder string = new StringBuilder();
                                    SharedPreferences preferences3 = preferences2;
                                    long value2 = value;
                                    int a2 = 0;
                                    while (a2 < 4) {
                                        try {
                                            string.insert(0, String.valueOf((char) ((int) value2)));
                                            value2 >>= j;
                                            if (value2 == 0) {
                                                break;
                                            }
                                            a2++;
                                        } catch (Throwable e2) {
                                            e = e2;
                                            preferences = preferences3;
                                        }
                                    }
                                    if (string.length() > 0) {
                                        emojiUseHistory.put(string.toString(), Utilities.parseInt(args22[1]));
                                    }
                                    i++;
                                    preferences2 = preferences3;
                                    j = 16;
                                }
                            }
                        } catch (Throwable e22) {
                            preferences = preferences2;
                            e = e22;
                            FileLog.m3e(e);
                            str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                            args = str.split(",");
                            for (String arg : args) {
                                args2 = arg.split("=");
                                emojiColor.put(args2[0], args2[1]);
                            }
                        }
                    }
                    preferences = preferences2;
                    try {
                        preferences.edit().remove("emojis").commit();
                        saveRecentEmoji();
                    } catch (Throwable e222) {
                        e = e222;
                        FileLog.m3e(e);
                        str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                        args = str.split(",");
                        while (a < args.length) {
                            args2 = arg.split("=");
                            emojiColor.put(args2[0], args2[1]);
                        }
                    }
                }
                preferences = preferences2;
                str2 = preferences.getString("emojis2", TtmlNode.ANONYMOUS_REGION_ID);
                if (str2 != null && str2.length() > 0) {
                    for (String arg2 : str2.split(",")) {
                        String[] args23 = arg2.split("=");
                        emojiUseHistory.put(args23[0], Utilities.parseInt(args23[1]));
                    }
                }
                if (emojiUseHistory.isEmpty() && !preferences.getBoolean("filled_default", false)) {
                    String[] newRecent = new String[]{"\ud83d\ude02", "\ud83d\ude18", "\u2764", "\ud83d\ude0d", "\ud83d\ude0a", "\ud83d\ude01", "\ud83d\udc4d", "\u263a", "\ud83d\ude14", "\ud83d\ude04", "\ud83d\ude2d", "\ud83d\udc8b", "\ud83d\ude12", "\ud83d\ude33", "\ud83d\ude1c", "\ud83d\ude48", "\ud83d\ude09", "\ud83d\ude03", "\ud83d\ude22", "\ud83d\ude1d", "\ud83d\ude31", "\ud83d\ude21", "\ud83d\ude0f", "\ud83d\ude1e", "\ud83d\ude05", "\ud83d\ude1a", "\ud83d\ude4a", "\ud83d\ude0c", "\ud83d\ude00", "\ud83d\ude0b", "\ud83d\ude06", "\ud83d\udc4c", "\ud83d\ude10", "\ud83d\ude15"};
                    for (a = 0; a < newRecent.length; a++) {
                        emojiUseHistory.put(newRecent[a], Integer.valueOf(newRecent.length - a));
                    }
                    preferences.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
                sortEmoji();
            } catch (Throwable e2222) {
                preferences = preferences2;
                e = e2222;
                FileLog.m3e(e);
                str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                args = str.split(",");
                while (a < args.length) {
                    args2 = arg.split("=");
                    emojiColor.put(args2[0], args2[1]);
                }
            }
            try {
                str = preferences.getString(TtmlNode.ATTR_TTS_COLOR, TtmlNode.ANONYMOUS_REGION_ID);
                if (str != null && str.length() > 0) {
                    args = str.split(",");
                    while (a < args.length) {
                        args2 = arg.split("=");
                        emojiColor.put(args2[0], args2[1]);
                    }
                }
            } catch (Throwable e22222) {
                FileLog.m3e(e22222);
            }
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append((String) entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append((String) entry.getValue());
        }
        preferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }
}
