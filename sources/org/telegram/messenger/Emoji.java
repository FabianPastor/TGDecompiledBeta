package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    private static int bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
    private static final int[][] cols = new int[][]{new int[]{16, 16, 16, 16}, new int[]{6, 6, 6, 6}, new int[]{5, 5, 5, 5}, new int[]{7, 7, 7, 7}, new int[]{5, 5, 5, 5}, new int[]{7, 7, 7, 7}, new int[]{8, 8, 8, 8}, new int[]{8, 8, 8, 8}};
    private static int drawImgSize = AndroidUtilities.dp(20.0f);
    private static Bitmap[][] emojiBmp = ((Bitmap[][]) Array.newInstance(Bitmap.class, new int[]{8, 4}));
    public static HashMap<String, String> emojiColor = new HashMap();
    public static HashMap<String, Integer> emojiUseHistory = new HashMap();
    private static boolean inited = false;
    private static boolean[][] loadingEmoji = ((boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{8, 4}));
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
                Utilities.globalQueue.postRunnable(new Emoji$EmojiDrawable$$Lambda$0(this));
                canvas.drawRect(getBounds(), Emoji.placeholderPaint);
            }
        }

        /* Access modifiers changed, original: final|synthetic */
        public final /* synthetic */ void lambda$draw$0$Emoji$EmojiDrawable() {
            Emoji.loadEmoji(this.info.page, this.info.page2);
            Emoji.loadingEmoji[this.info.page][this.info.page2] = false;
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
        private FontMetricsInt fontMetrics;
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

    public static native Object[] getSuggestion(String str);

    static {
        int emojiFullSize;
        int add = 2;
        if (AndroidUtilities.density <= 1.0f) {
            emojiFullSize = 33;
            add = 1;
        } else if (AndroidUtilities.density <= 1.5f) {
            emojiFullSize = 66;
        } else if (AndroidUtilities.density <= 2.0f) {
            emojiFullSize = 66;
        } else {
            emojiFullSize = 66;
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

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    private static void loadEmoji(int r20, int r21) {
        /*
        r8 = 1;
        r13 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x00df }
        r14 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 > 0) goto L_0x0048;
    L_0x0009:
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = 2;
    L_0x000c:
        r2 = 12;
    L_0x000e:
        r13 = 14;
        if (r2 >= r13) goto L_0x0065;
    L_0x0012:
        r13 = java.util.Locale.US;	 Catch:{ Exception -> 0x0061 }
        r14 = "v%d_emoji%.01fx_%d.png";
        r15 = 3;
        r15 = new java.lang.Object[r15];	 Catch:{ Exception -> 0x0061 }
        r16 = 0;
        r17 = java.lang.Integer.valueOf(r2);	 Catch:{ Exception -> 0x0061 }
        r15[r16] = r17;	 Catch:{ Exception -> 0x0061 }
        r16 = 1;
        r17 = java.lang.Float.valueOf(r11);	 Catch:{ Exception -> 0x0061 }
        r15[r16] = r17;	 Catch:{ Exception -> 0x0061 }
        r16 = 2;
        r17 = java.lang.Integer.valueOf(r20);	 Catch:{ Exception -> 0x0061 }
        r15[r16] = r17;	 Catch:{ Exception -> 0x0061 }
        r7 = java.lang.String.format(r13, r14, r15);	 Catch:{ Exception -> 0x0061 }
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0061 }
        r6 = r13.getFileStreamPath(r7);	 Catch:{ Exception -> 0x0061 }
        r13 = r6.exists();	 Catch:{ Exception -> 0x0061 }
        if (r13 == 0) goto L_0x0045;
    L_0x0042:
        r6.delete();	 Catch:{ Exception -> 0x0061 }
    L_0x0045:
        r2 = r2 + 1;
        goto L_0x000e;
    L_0x0048:
        r13 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x00df }
        r14 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 > 0) goto L_0x0053;
    L_0x0050:
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x000c;
    L_0x0053:
        r13 = org.telegram.messenger.AndroidUtilities.density;	 Catch:{ Throwable -> 0x00df }
        r14 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1));
        if (r13 > 0) goto L_0x005e;
    L_0x005b:
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x000c;
    L_0x005e:
        r11 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        goto L_0x000c;
    L_0x0061:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ Throwable -> 0x00df }
    L_0x0065:
        r3 = 0;
        r13 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Throwable -> 0x00da }
        r13 = r13.getAssets();	 Catch:{ Throwable -> 0x00da }
        r14 = new java.lang.StringBuilder;	 Catch:{ Throwable -> 0x00da }
        r14.<init>();	 Catch:{ Throwable -> 0x00da }
        r15 = "emoji/";
        r14 = r14.append(r15);	 Catch:{ Throwable -> 0x00da }
        r15 = java.util.Locale.US;	 Catch:{ Throwable -> 0x00da }
        r16 = "v14_emoji%.01fx_%d_%d.png";
        r17 = 3;
        r0 = r17;
        r0 = new java.lang.Object[r0];	 Catch:{ Throwable -> 0x00da }
        r17 = r0;
        r18 = 0;
        r19 = java.lang.Float.valueOf(r11);	 Catch:{ Throwable -> 0x00da }
        r17[r18] = r19;	 Catch:{ Throwable -> 0x00da }
        r18 = 1;
        r19 = java.lang.Integer.valueOf(r20);	 Catch:{ Throwable -> 0x00da }
        r17[r18] = r19;	 Catch:{ Throwable -> 0x00da }
        r18 = 2;
        r19 = java.lang.Integer.valueOf(r21);	 Catch:{ Throwable -> 0x00da }
        r17[r18] = r19;	 Catch:{ Throwable -> 0x00da }
        r15 = java.lang.String.format(r15, r16, r17);	 Catch:{ Throwable -> 0x00da }
        r14 = r14.append(r15);	 Catch:{ Throwable -> 0x00da }
        r14 = r14.toString();	 Catch:{ Throwable -> 0x00da }
        r9 = r13.open(r14);	 Catch:{ Throwable -> 0x00da }
        r10 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x00da }
        r10.<init>();	 Catch:{ Throwable -> 0x00da }
        r13 = 0;
        r10.inJustDecodeBounds = r13;	 Catch:{ Throwable -> 0x00da }
        r10.inSampleSize = r8;	 Catch:{ Throwable -> 0x00da }
        r13 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Throwable -> 0x00da }
        r14 = 26;
        if (r13 < r14) goto L_0x00c1;
    L_0x00bd:
        r13 = android.graphics.Bitmap.Config.HARDWARE;	 Catch:{ Throwable -> 0x00da }
        r10.inPreferredConfig = r13;	 Catch:{ Throwable -> 0x00da }
    L_0x00c1:
        r13 = 0;
        r3 = android.graphics.BitmapFactory.decodeStream(r9, r13, r10);	 Catch:{ Throwable -> 0x00da }
        r9.close();	 Catch:{ Throwable -> 0x00da }
        r3.prepareToDraw();	 Catch:{ Throwable -> 0x00da }
    L_0x00cc:
        r5 = r3;
        r13 = new org.telegram.messenger.Emoji$$Lambda$0;	 Catch:{ Throwable -> 0x00df }
        r0 = r20;
        r1 = r21;
        r13.<init>(r0, r1, r5);	 Catch:{ Throwable -> 0x00df }
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r13);	 Catch:{ Throwable -> 0x00df }
    L_0x00d9:
        return;
    L_0x00da:
        r4 = move-exception;
        org.telegram.messenger.FileLog.e(r4);	 Catch:{ Throwable -> 0x00df }
        goto L_0x00cc;
    L_0x00df:
        r12 = move-exception;
        r13 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r13 == 0) goto L_0x00d9;
    L_0x00e4:
        r13 = "Error loading emoji";
        org.telegram.messenger.FileLog.e(r13, r12);
        goto L_0x00d9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.loadEmoji(int, int):void");
    }

    static final /* synthetic */ void lambda$loadEmoji$0$Emoji(int page, int page2, Bitmap finalBitmap) {
        emojiBmp[page][page2] = finalBitmap;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiDidLoad, new Object[0]);
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
                    break;
                } else if (ch >= 8252 && ch <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    emoji = emoji.substring(0, a + 1) + "️" + emoji.substring(a + 1);
                    length++;
                    a++;
                }
            } else if (ch != 55356 || a >= length - 1) {
                a++;
            } else {
                ch = emoji.charAt(a + 1);
                if (ch == 56879 || ch == 56324 || ch == 56858 || ch == 56703) {
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
        DrawableInfo info = (DrawableInfo) rects.get(code);
        if (info == null) {
            CharSequence newCode = (CharSequence) EmojiData.emojiAliasMap.get(code);
            if (newCode != null) {
                info = (DrawableInfo) rects.get(newCode);
            }
        }
        if (info == null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("No drawable for emoji " + code);
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
        if (SharedConfig.useSystemEmoji || cs == null || cs.length() == 0) {
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
        StringBuilder stringBuilder = new StringBuilder(16);
        StringBuilder addionalCode = new StringBuilder(2);
        int length = cs.length();
        boolean doneEmoji = false;
        int i = 0;
        while (i < length) {
            try {
                char next;
                char c = cs.charAt(i);
                if ((c >= 55356 && c <= 55358) || (buf != 0 && (-4294967296L & buf) == 0 && (65535 & buf) == 55356 && c >= 56806 && c <= 56831)) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    stringBuilder.append(c);
                    startLength++;
                    buf = (buf << 16) | ((long) c);
                } else if (stringBuilder.length() > 0 && (c == 9792 || c == 9794 || c == 9877)) {
                    stringBuilder.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (buf > 0 && (61440 & c) == 53248) {
                    stringBuilder.append(c);
                    startLength++;
                    buf = 0;
                    doneEmoji = true;
                } else if (c == 8419) {
                    if (i > 0) {
                        char c2 = cs.charAt(previousGoodIndex);
                        if ((c2 >= '0' && c2 <= '9') || c2 == '#' || c2 == '*') {
                            startIndex = previousGoodIndex;
                            startLength = (i - previousGoodIndex) + 1;
                            stringBuilder.append(c2);
                            stringBuilder.append(c);
                            doneEmoji = true;
                        }
                    }
                } else if ((c == 169 || c == 174 || (c >= 8252 && c <= 12953)) && EmojiData.dataCharsMap.containsKey(Character.valueOf(c))) {
                    if (startIndex == -1) {
                        startIndex = i;
                    }
                    startLength++;
                    stringBuilder.append(c);
                    doneEmoji = true;
                } else if (startIndex != -1) {
                    stringBuilder.setLength(0);
                    startIndex = -1;
                    startLength = 0;
                    doneEmoji = false;
                } else if (!(c == 65039 || emojiOnly == null)) {
                    emojiOnly[0] = 0;
                    emojiOnly = null;
                }
                if (doneEmoji && i + 2 < length) {
                    next = cs.charAt(i + 1);
                    if (next == 55356) {
                        next = cs.charAt(i + 2);
                        if (next >= 57339 && next <= 57343) {
                            stringBuilder.append(cs.subSequence(i + 1, i + 3));
                            startLength += 2;
                            i += 2;
                        }
                    } else if (stringBuilder.length() >= 2 && stringBuilder.charAt(0) == 55356 && stringBuilder.charAt(1) == 57332 && next == 56128) {
                        i++;
                        do {
                            stringBuilder.append(cs.subSequence(i, i + 2));
                            startLength += 2;
                            i += 2;
                            if (i >= cs.length()) {
                                break;
                            }
                        } while (cs.charAt(i) == 56128);
                        i--;
                    }
                }
                previousGoodIndex = i;
                char prevCh = c;
                for (int a = 0; a < 3; a++) {
                    if (i + 1 < length) {
                        c = cs.charAt(i + 1);
                        if (a == 1) {
                            if (c == 8205 && stringBuilder.length() > 0) {
                                stringBuilder.append(c);
                                i++;
                                startLength++;
                                doneEmoji = false;
                            }
                        } else if ((startIndex != -1 || prevCh == '*' || (prevCh >= '1' && prevCh <= '9')) && c >= 65024 && c <= 65039) {
                            i++;
                            startLength++;
                        }
                    }
                }
                if (doneEmoji && i + 2 < length && cs.charAt(i + 1) == 55356) {
                    next = cs.charAt(i + 2);
                    if (next >= 57339 && next <= 57343) {
                        stringBuilder.append(cs.subSequence(i + 1, i + 3));
                        startLength += 2;
                        i += 2;
                    }
                }
                if (doneEmoji) {
                    if (emojiOnly != null) {
                        emojiOnly[0] = emojiOnly[0] + 1;
                    }
                    EmojiDrawable drawable = getEmojiDrawable(stringBuilder.subSequence(0, stringBuilder.length()));
                    if (drawable != null) {
                        s.setSpan(new EmojiSpan(drawable, 0, size, fontMetrics), startIndex, startIndex + startLength, 33);
                        emojiCount++;
                    }
                    startLength = 0;
                    startIndex = -1;
                    stringBuilder.setLength(0);
                    doneEmoji = false;
                }
                if (VERSION.SDK_INT < 23 && emojiCount >= 50) {
                    break;
                }
                i++;
            } catch (Exception e) {
                FileLog.e(e);
                return cs;
            }
        }
        return s;
    }

    public static void addRecentEmoji(String code) {
        Integer count = (Integer) emojiUseHistory.get(code);
        if (count == null) {
            count = Integer.valueOf(0);
        }
        if (count.intValue() == 0 && emojiUseHistory.size() >= 48) {
            emojiUseHistory.remove((String) recentEmoji.get(recentEmoji.size() - 1));
            recentEmoji.set(recentEmoji.size() - 1, code);
        }
        emojiUseHistory.put(code, Integer.valueOf(count.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, Emoji$$Lambda$1.$instance);
        while (recentEmoji.size() > 48) {
            recentEmoji.remove(recentEmoji.size() - 1);
        }
    }

    static final /* synthetic */ int lambda$sortEmoji$1$Emoji(String lhs, String rhs) {
        Integer count1 = (Integer) emojiUseHistory.get(lhs);
        Integer count2 = (Integer) emojiUseHistory.get(rhs);
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
        if (!recentEmojiLoaded) {
            String str;
            String[] args2;
            int a;
            recentEmojiLoaded = true;
            SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
            try {
                emojiUseHistory.clear();
                if (preferences.contains("emojis")) {
                    str = preferences.getString("emojis", "");
                    if (str != null && str.length() > 0) {
                        for (String arg : str.split(",")) {
                            args2 = arg.split("=");
                            long value = Utilities.parseLong(args2[0]).longValue();
                            StringBuilder string = new StringBuilder();
                            for (a = 0; a < 4; a++) {
                                string.insert(0, String.valueOf((char) ((int) value)));
                                value >>= 16;
                                if (value == 0) {
                                    break;
                                }
                            }
                            if (string.length() > 0) {
                                emojiUseHistory.put(string.toString(), Utilities.parseInt(args2[1]));
                            }
                        }
                    }
                    preferences.edit().remove("emojis").commit();
                    saveRecentEmoji();
                } else {
                    str = preferences.getString("emojis2", "");
                    if (str != null && str.length() > 0) {
                        for (String arg2 : str.split(",")) {
                            args2 = arg2.split("=");
                            emojiUseHistory.put(args2[0], Utilities.parseInt(args2[1]));
                        }
                    }
                }
                if (emojiUseHistory.isEmpty() && !preferences.getBoolean("filled_default", false)) {
                    String[] newRecent = new String[]{"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                    for (int i = 0; i < newRecent.length; i++) {
                        emojiUseHistory.put(newRecent[i], Integer.valueOf(newRecent.length - i));
                    }
                    preferences.edit().putBoolean("filled_default", true).commit();
                    saveRecentEmoji();
                }
                sortEmoji();
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                str = preferences.getString("color", "");
                if (str != null && str.length() > 0) {
                    String[] args = str.split(",");
                    for (String arg22 : args) {
                        args2 = arg22.split("=");
                        emojiColor.put(args2[0], args2[1]);
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
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
        preferences.edit().putString("color", stringBuilder.toString()).commit();
    }
}
