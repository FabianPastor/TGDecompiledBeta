package org.telegram.messenger;

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
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.volley.DefaultRetryPolicy;

public class Emoji {
    private static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
    private static final int[][] cols = new int[][]{new int[]{15, 15, 15, 15}, new int[]{6, 6, 6, 6}, new int[]{8, 8, 8, 8}, new int[]{9, 9, 9, 9}, new int[]{10, 10, 10, 10}};
    private static int drawImgSize = AndroidUtilities.dp(20.0f);
    private static Bitmap[][] emojiBmp = ((Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{5, 4}));
    private static boolean inited = false;
    private static boolean[][] loadingEmoji = ((boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{5, 4}));
    private static Paint placeholderPaint = new Paint();
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
    private static final int splitCount = 4;

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
                Utilities.globalQueue.postRunnable(new Runnable() {
                    public void run() {
                        Emoji.loadEmoji(EmojiDrawable.this.info.page, EmojiDrawable.this.info.page2);
                        Emoji.loadingEmoji[EmojiDrawable.this.info.page][EmojiDrawable.this.info.page2] = false;
                    }
                });
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

    static {
        int emojiFullSize;
        int add = 2;
        if (AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
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

    private static void loadEmoji(int page, int page2) {
        float scale;
        int imageResize = 1;
        try {
            int a;
            File imageFile;
            if (AndroidUtilities.density <= DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                scale = 2.0f;
                imageResize = 2;
            } else if (AndroidUtilities.density <= 1.5f) {
                scale = 2.0f;
            } else if (AndroidUtilities.density <= 2.0f) {
                scale = 2.0f;
            } else {
                scale = 2.0f;
            }
            for (a = 4; a < 7; a++) {
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.jpg", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_a_%d.jpg", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
            for (a = 8; a < 11; a++) {
                imageFile = ApplicationLoader.applicationContext.getFileStreamPath(String.format(Locale.US, "v%d_emoji%.01fx_%d.png", new Object[]{Integer.valueOf(a), Float.valueOf(scale), Integer.valueOf(page)}));
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        } catch (Throwable x) {
            FileLog.e("tmessages", "Error loading emoji", x);
            return;
        }
        Bitmap bitmap = null;
        try {
            InputStream is = ApplicationLoader.applicationContext.getAssets().open("emoji/" + String.format(Locale.US, "v11_emoji%.01fx_%d_%d.png", new Object[]{Float.valueOf(scale), Integer.valueOf(page), Integer.valueOf(page2)}));
            Options opts = new Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            bitmap = BitmapFactory.decodeStream(is, null, opts);
            is.close();
        } catch (Throwable e2) {
            FileLog.e("tmessages", e2);
        }
        final Bitmap finalBitmap = bitmap;
        final int i = page;
        final int i2 = page2;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                Emoji.emojiBmp[i][i2] = finalBitmap;
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.emojiDidLoaded, new Object[0]);
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
        int a = 0;
        while (a < lenght) {
            char ch = emoji.charAt(a);
            if (ch < '?' || ch > '?') {
                if (ch == '⃣') {
                    break;
                } else if (ch >= '‼' && ch <= '㊙' && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    emoji = emoji.substring(0, a + 1) + "️" + emoji.substring(a + 1);
                    lenght++;
                    a++;
                }
            } else if (ch != '?' || a >= lenght - 1) {
                a++;
            } else {
                ch = emoji.charAt(a + 1);
                if (ch == '?' || ch == '?' || ch == '?' || ch == '?') {
                    emoji = emoji.substring(0, a + 2) + "️" + emoji.substring(a + 2);
                    lenght++;
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
        DrawableInfo info = (DrawableInfo) rects.get(code);
        if (info == null) {
            FileLog.e("tmessages", "No drawable for emoji " + code);
            return null;
        }
        EmojiDrawable ed = new EmojiDrawable(info);
        ed.setBounds(0, 0, drawImgSize, drawImgSize);
        return ed;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        EmojiDrawable ed = getEmojiDrawable(code);
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
        if (MessagesController.getInstance().useSystemEmoji || cs == null || cs.length() == 0) {
            return cs;
        }
        Spannable s;
        if (createNew || !(cs instanceof Spannable)) {
            s = Factory.getInstance().newSpannable(cs.toString());
        } else {
            s = (Spannable) cs;
        }
        long buf = 0;
        int emojiCount = 0;
        int startIndex = -1;
        int startLength = 0;
        int previousGoodIndex = 0;
        StringBuilder emojiCode = new StringBuilder(16);
        StringBuilder addionalCode = new StringBuilder(2);
        int length = cs.length();
        boolean doneEmoji = false;
        int i = 0;
        while (i < length) {
            try {
                char c = cs.charAt(i);
                if ((c >= '?' && c <= '?') || (buf != 0 && (-4294967296L & buf) == 0 && (65535 & buf) == 55356 && c >= '?' && c <= '?')) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    emojiCode.append(c);
                    startLength++;
                    buf = (buf << 16) | ((long) c);
                } else if (emojiCode.length() > 0 && (c == '♀' || c == '♂' || c == '⚕')) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (buf > 0 && (61440 & c) == 53248) {
                    emojiCode.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (c == '⃣') {
                    if (i > 0) {
                        char c2 = cs.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = (i - previousGoodIndex) + 1;
                            emojiCode.append(c2);
                            emojiCode.append(c);
                            doneEmoji = true;
                        }
                    }
                } else if ((c == '©' || c == '®' || (c >= '‼' && c <= '㊙')) && EmojiData.dataCharsMap.containsKey(Character.valueOf(c))) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    startLength++;
                    emojiCode.append(c);
                    doneEmoji = true;
                } else if (startIndex != -1) {
                    emojiCode.setLength(0);
                    startIndex = -1;
                    startLength = 0;
                    doneEmoji = false;
                } else if (!(c == '️' || emojiOnly == null)) {
                    emojiOnly[0] = 0;
                    emojiOnly = null;
                }
                if (doneEmoji && i + 2 < length && cs.charAt(i + 1) == '?') {
                    char next = cs.charAt(i + 2);
                    if (next >= '?' && next <= '?') {
                        emojiCode.append(cs.subSequence(i + 1, i + 3));
                        startLength += 2;
                        i += 2;
                    }
                }
                previousGoodIndex = i;
                for (int a = 0; a < 3; a++) {
                    if (i + 1 < length) {
                        c = cs.charAt(i + 1);
                        if (a == 1) {
                            if (c == '‍' && emojiCode.length() > 0) {
                                emojiCode.append(c);
                                i++;
                                startLength++;
                                doneEmoji = false;
                            }
                        } else if (c >= '︀' && c <= '️') {
                            i++;
                            startLength++;
                        }
                    }
                }
                if (doneEmoji) {
                    if (emojiOnly != null) {
                        emojiOnly[0] = emojiOnly[0] + 1;
                    }
                    EmojiDrawable drawable = getEmojiDrawable(emojiCode.subSequence(0, emojiCode.length()));
                    if (drawable != null) {
                        s.setSpan(new EmojiSpan(drawable, 0, size, fontMetrics), startIndex, startIndex + startLength, 33);
                        emojiCount++;
                    }
                    startLength = 0;
                    startIndex = -1;
                    emojiCode.setLength(0);
                    doneEmoji = false;
                }
                if (VERSION.SDK_INT < 23 && emojiCount >= 50) {
                    break;
                }
                i++;
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
                return cs;
            }
        }
        return s;
    }
}
