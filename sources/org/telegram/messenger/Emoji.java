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
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Cells.ChatMessageCell;

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

    public static AnimatedEmojiDrawable getAnimatedEmojiDrawable(TLRPC$Document tLRPC$Document, Drawable drawable, AtomicReference<WeakReference<View>> atomicReference) {
        if (tLRPC$Document == null) {
            return null;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = new AnimatedEmojiDrawable(tLRPC$Document, drawable, atomicReference);
        int i = drawImgSize;
        animatedEmojiDrawable.setBounds(0, 0, i, i);
        return animatedEmojiDrawable;
    }

    public static Drawable getAnimatedEmojiBigDrawable(TLRPC$Document tLRPC$Document, Drawable drawable, AtomicReference<WeakReference<View>> atomicReference) {
        AnimatedEmojiDrawable animatedEmojiDrawable = getAnimatedEmojiDrawable(tLRPC$Document, drawable, atomicReference);
        if (animatedEmojiDrawable == null) {
            return null;
        }
        int i = bigImgSize;
        animatedEmojiDrawable.setBounds(0, 0, i, i);
        animatedEmojiDrawable.fullSize = true;
        return animatedEmojiDrawable;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r2 = org.telegram.messenger.EmojiData.emojiAliasMap.get(r2);
     */
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

    public static class AnimatedEmojiDrawable extends Drawable {
        public boolean fullSize = false;
        private ImageReceiver imageReceiver = new ImageReceiver();
        private WeakReference<View> lastView;
        private Rect rect = new Rect();
        private AtomicReference<WeakReference<View>> viewRef;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public AnimatedEmojiDrawable(TLRPC$Document tLRPC$Document, Drawable drawable, AtomicReference<WeakReference<View>> atomicReference) {
            SvgHelper.SvgDrawable svgDrawable = drawable;
            if (drawable == null) {
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f);
                svgDrawable = svgThumb;
                if (svgThumb != null) {
                    svgThumb.overrideWidthAndHeight(512, 512);
                    svgDrawable = svgThumb;
                }
            }
            this.imageReceiver.setImage(ImageLocation.getForDocument(tLRPC$Document), "32_32", svgDrawable, "tgs", tLRPC$Document, 1);
            this.imageReceiver.setInvalidateAll(true);
            this.imageReceiver.setAllowStartLottieAnimation(true);
            this.imageReceiver.setAutoRepeat(1);
            this.imageReceiver.startAnimation();
            this.viewRef = atomicReference;
            updateViewRef();
        }

        public Rect getDrawRect() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            this.rect.left = centerX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            this.rect.right = centerX + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            this.rect.top = centerY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            this.rect.bottom = centerY + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            return this.rect;
        }

        /* access modifiers changed from: private */
        public View getParentView() {
            updateViewRef();
            WeakReference<View> weakReference = this.lastView;
            if (weakReference != null) {
                return (View) weakReference.get();
            }
            return null;
        }

        private void updateViewRef() {
            AtomicReference<WeakReference<View>> atomicReference = this.viewRef;
            if (!(atomicReference == null || atomicReference.get() == this.lastView)) {
                WeakReference<View> weakReference = this.viewRef.get();
                this.lastView = weakReference;
                if (!(weakReference == null || weakReference.get() == null)) {
                    this.imageReceiver.setParentView((View) this.lastView.get());
                }
            }
            if (this.imageReceiver.getLottieAnimation() != null && this.imageReceiver.getLottieAnimation().getCallback() == null) {
                this.imageReceiver.getLottieAnimation().setCallback(new Drawable.Callback() {
                    public void invalidateDrawable(Drawable drawable) {
                        View access$300 = AnimatedEmojiDrawable.this.getParentView();
                        if (access$300 != null) {
                            if (access$300 instanceof ChatMessageCell) {
                                access$300.invalidate();
                            }
                            access$300.invalidateDrawable(drawable);
                        }
                    }

                    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                        View access$300 = AnimatedEmojiDrawable.this.getParentView();
                        if (access$300 != null) {
                            access$300.scheduleDrawable(drawable, runnable, j);
                        }
                    }

                    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                        View access$300 = AnimatedEmojiDrawable.this.getParentView();
                        if (access$300 != null) {
                            access$300.unscheduleDrawable(drawable, runnable);
                        }
                    }
                });
            }
        }

        public void draw(Canvas canvas) {
            updateViewRef();
            Rect drawRect = this.fullSize ? getDrawRect() : getBounds();
            Rect rect2 = AndroidUtilities.rectTmp2;
            rect2.set(drawRect);
            rect2.inset(-((int) (((float) rect2.width()) * 0.1f)), -((int) (((float) rect2.height()) * 0.1f)));
            this.imageReceiver.setImageCoords(rect2);
            this.imageReceiver.draw(canvas);
            View parentView = getParentView();
            if (parentView != null) {
                if (parentView instanceof ChatMessageCell) {
                    parentView.invalidate();
                }
                parentView.invalidateDrawable(this);
            }
        }
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
                Bitmap[][] access$600 = Emoji.emojiBmp;
                DrawableInfo drawableInfo2 = this.info;
                canvas.drawBitmap(access$600[drawableInfo2.page][drawableInfo2.page2], (Rect) null, rect2, paint);
            }
        }

        public void setAlpha(int i) {
            paint.setAlpha(i);
        }

        public boolean isLoaded() {
            Bitmap[][] access$600 = Emoji.emojiBmp;
            DrawableInfo drawableInfo = this.info;
            return access$600[drawableInfo.page][drawableInfo.page2] != null;
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

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, (int[]) null, false, (AtomicReference<WeakReference<View>>) null);
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z, boolean z2, AtomicReference<WeakReference<View>> atomicReference) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, (int[]) null, z2, atomicReference);
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z, int[] iArr) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, iArr, false, (AtomicReference<WeakReference<View>>) null);
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x018f A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0197 A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x022a A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x0266 A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x027b A[LOOP:0: B:11:0x0042->B:178:0x027b, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x028b A[EDGE_INSN: B:197:0x028b->B:182:0x028b ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0080 A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0114 A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x011e A[Catch:{ Exception -> 0x0286 }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x012a A[Catch:{ Exception -> 0x0286 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.CharSequence replaceEmoji(java.lang.CharSequence r26, android.graphics.Paint.FontMetricsInt r27, int r28, boolean r29, int[] r30, boolean r31, java.util.concurrent.atomic.AtomicReference<java.lang.ref.WeakReference<android.view.View>> r32) {
        /*
            r1 = r26
            boolean r0 = org.telegram.messenger.SharedConfig.useSystemEmoji
            if (r0 != 0) goto L_0x02df
            if (r1 == 0) goto L_0x02df
            int r0 = r26.length()
            if (r0 != 0) goto L_0x0010
            goto L_0x02df
        L_0x0010:
            if (r29 != 0) goto L_0x001a
            boolean r0 = r1 instanceof android.text.Spannable
            if (r0 == 0) goto L_0x001a
            r0 = r1
            android.text.Spannable r0 = (android.text.Spannable) r0
            goto L_0x0026
        L_0x001a:
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            java.lang.String r2 = r26.toString()
            android.text.Spannable r0 = r0.newSpannable(r2)
        L_0x0026:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 16
            r2.<init>(r3)
            int r4 = r26.length()
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r10 = r30
            r9 = 0
            r11 = 0
            r12 = 0
            r14 = -1
            r15 = 0
            r16 = 0
            r17 = 0
        L_0x0042:
            if (r11 >= r4) goto L_0x028b
            char r3 = r1.charAt(r11)     // Catch:{ Exception -> 0x0286 }
            r8 = 55356(0xd83c, float:7.757E-41)
            r6 = 1
            if (r3 < r8) goto L_0x0056
            r7 = 55358(0xd83e, float:7.7573E-41)
            if (r3 <= r7) goto L_0x0054
            goto L_0x0056
        L_0x0054:
            r7 = -1
            goto L_0x007e
        L_0x0056:
            r18 = 0
            int r7 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1))
            if (r7 == 0) goto L_0x008e
            r20 = -4294967296(0xfffffffvar_, double:NaN)
            long r20 = r12 & r20
            int r7 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1))
            if (r7 != 0) goto L_0x008e
            r20 = 65535(0xffff, double:3.23786E-319)
            long r20 = r12 & r20
            r22 = 55356(0xd83c, double:2.73495E-319)
            int r7 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1))
            if (r7 != 0) goto L_0x008e
            r7 = 56806(0xdde6, float:7.9602E-41)
            if (r3 < r7) goto L_0x008e
            r7 = 56831(0xddff, float:7.9637E-41)
            if (r3 > r7) goto L_0x008e
            goto L_0x0054
        L_0x007e:
            if (r14 != r7) goto L_0x0081
            r14 = r11
        L_0x0081:
            r2.append(r3)     // Catch:{ Exception -> 0x0286 }
            int r15 = r15 + 1
            r7 = 16
            long r12 = r12 << r7
            long r7 = (long) r3     // Catch:{ Exception -> 0x0286 }
            long r12 = r12 | r7
        L_0x008b:
            r7 = 0
            goto L_0x0124
        L_0x008e:
            int r7 = r2.length()     // Catch:{ Exception -> 0x0286 }
            if (r7 <= 0) goto L_0x00ac
            r7 = 9792(0x2640, float:1.3722E-41)
            if (r3 == r7) goto L_0x00a0
            r7 = 9794(0x2642, float:1.3724E-41)
            if (r3 == r7) goto L_0x00a0
            r7 = 9877(0x2695, float:1.384E-41)
            if (r3 != r7) goto L_0x00ac
        L_0x00a0:
            r2.append(r3)     // Catch:{ Exception -> 0x0286 }
        L_0x00a3:
            int r15 = r15 + 1
            r7 = 0
            r12 = 0
        L_0x00a8:
            r16 = 1
            goto L_0x0124
        L_0x00ac:
            r7 = 0
            int r18 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r18 <= 0) goto L_0x00c0
            r18 = 61440(0xvar_, float:8.6096E-41)
            r7 = r3 & r18
            r8 = 53248(0xd000, float:7.4616E-41)
            if (r7 != r8) goto L_0x00c0
            r2.append(r3)     // Catch:{ Exception -> 0x0286 }
            goto L_0x00a3
        L_0x00c0:
            r7 = 8419(0x20e3, float:1.1798E-41)
            if (r3 != r7) goto L_0x00e8
            if (r11 <= 0) goto L_0x008b
            char r7 = r1.charAt(r9)     // Catch:{ Exception -> 0x0286 }
            r8 = 48
            if (r7 < r8) goto L_0x00d2
            r8 = 57
            if (r7 <= r8) goto L_0x00da
        L_0x00d2:
            r8 = 35
            if (r7 == r8) goto L_0x00da
            r8 = 42
            if (r7 != r8) goto L_0x008b
        L_0x00da:
            int r8 = r11 - r9
            int r15 = r8 + 1
            r2.append(r7)     // Catch:{ Exception -> 0x0286 }
            r2.append(r3)     // Catch:{ Exception -> 0x0286 }
            r14 = r9
            r16 = 1
            goto L_0x008b
        L_0x00e8:
            r7 = 169(0xa9, float:2.37E-43)
            if (r3 == r7) goto L_0x00fb
            r7 = 174(0xae, float:2.44E-43)
            if (r3 == r7) goto L_0x00fb
            r7 = 8252(0x203c, float:1.1564E-41)
            if (r3 < r7) goto L_0x00f9
            r7 = 12953(0x3299, float:1.8151E-41)
            if (r3 > r7) goto L_0x00f9
            goto L_0x00fb
        L_0x00f9:
            r7 = -1
            goto L_0x0112
        L_0x00fb:
            java.util.HashMap<java.lang.Character, java.lang.Boolean> r7 = org.telegram.messenger.EmojiData.dataCharsMap     // Catch:{ Exception -> 0x0286 }
            java.lang.Character r8 = java.lang.Character.valueOf(r3)     // Catch:{ Exception -> 0x0286 }
            boolean r7 = r7.containsKey(r8)     // Catch:{ Exception -> 0x0286 }
            if (r7 == 0) goto L_0x00f9
            r7 = -1
            if (r14 != r7) goto L_0x010b
            r14 = r11
        L_0x010b:
            int r15 = r15 + 1
            r2.append(r3)     // Catch:{ Exception -> 0x0286 }
            r7 = 0
            goto L_0x00a8
        L_0x0112:
            if (r14 == r7) goto L_0x011e
            r7 = 0
            r2.setLength(r7)     // Catch:{ Exception -> 0x0286 }
            r7 = 0
            r14 = -1
            r15 = 0
            r16 = 0
            goto L_0x0124
        L_0x011e:
            r7 = 65039(0xfe0f, float:9.1139E-41)
            if (r3 == r7) goto L_0x008b
            r7 = 1
        L_0x0124:
            if (r16 == 0) goto L_0x018f
            int r6 = r11 + 2
            if (r6 >= r4) goto L_0x018f
            int r8 = r11 + 1
            char r9 = r1.charAt(r8)     // Catch:{ Exception -> 0x0286 }
            r24 = r7
            r7 = 55356(0xd83c, float:7.757E-41)
            if (r9 != r7) goto L_0x0152
            char r7 = r1.charAt(r6)     // Catch:{ Exception -> 0x0286 }
            r9 = 57339(0xdffb, float:8.0349E-41)
            if (r7 < r9) goto L_0x0191
            r9 = 57343(0xdfff, float:8.0355E-41)
            if (r7 > r9) goto L_0x0191
            int r11 = r11 + 3
            java.lang.CharSequence r7 = r1.subSequence(r8, r11)     // Catch:{ Exception -> 0x0286 }
            r2.append(r7)     // Catch:{ Exception -> 0x0286 }
            int r15 = r15 + 2
            r9 = r6
            goto L_0x0192
        L_0x0152:
            int r6 = r2.length()     // Catch:{ Exception -> 0x0286 }
            r7 = 2
            if (r6 < r7) goto L_0x0191
            r6 = 0
            char r7 = r2.charAt(r6)     // Catch:{ Exception -> 0x0286 }
            r6 = 55356(0xd83c, float:7.757E-41)
            if (r7 != r6) goto L_0x0191
            r6 = 1
            char r7 = r2.charAt(r6)     // Catch:{ Exception -> 0x0286 }
            r6 = 57332(0xdff4, float:8.0339E-41)
            if (r7 != r6) goto L_0x0191
            r6 = 56128(0xdb40, float:7.8652E-41)
            if (r9 != r6) goto L_0x0191
        L_0x0172:
            int r7 = r8 + 2
            java.lang.CharSequence r8 = r1.subSequence(r8, r7)     // Catch:{ Exception -> 0x0286 }
            r2.append(r8)     // Catch:{ Exception -> 0x0286 }
            r8 = 2
            int r15 = r15 + r8
            int r9 = r26.length()     // Catch:{ Exception -> 0x0286 }
            if (r7 >= r9) goto L_0x018c
            char r9 = r1.charAt(r7)     // Catch:{ Exception -> 0x0286 }
            if (r9 == r6) goto L_0x018a
            goto L_0x018c
        L_0x018a:
            r8 = r7
            goto L_0x0172
        L_0x018c:
            int r11 = r7 + -1
            goto L_0x0191
        L_0x018f:
            r24 = r7
        L_0x0191:
            r9 = r11
        L_0x0192:
            r7 = r9
            r6 = 0
        L_0x0194:
            r8 = 3
            if (r6 >= r8) goto L_0x01f2
            int r8 = r7 + 1
            if (r8 >= r4) goto L_0x01e8
            char r11 = r1.charAt(r8)     // Catch:{ Exception -> 0x0286 }
            r25 = r9
            r9 = 1
            if (r6 != r9) goto L_0x01bc
            r9 = 8205(0x200d, float:1.1498E-41)
            if (r11 != r9) goto L_0x01ea
            int r9 = r2.length()     // Catch:{ Exception -> 0x0286 }
            if (r9 <= 0) goto L_0x01ea
            r2.append(r11)     // Catch:{ Exception -> 0x0286 }
            int r15 = r15 + 1
            r7 = r8
            r9 = 65039(0xfe0f, float:9.1139E-41)
            r16 = 0
            r24 = 0
            goto L_0x01ed
        L_0x01bc:
            r9 = -1
            if (r14 != r9) goto L_0x01cf
            r9 = 42
            if (r3 == r9) goto L_0x01cf
            r9 = 35
            if (r3 == r9) goto L_0x01cf
            r9 = 48
            if (r3 < r9) goto L_0x01ea
            r9 = 57
            if (r3 > r9) goto L_0x01ea
        L_0x01cf:
            r9 = 65024(0xfe00, float:9.1118E-41)
            if (r11 < r9) goto L_0x01ea
            r9 = 65039(0xfe0f, float:9.1139E-41)
            if (r11 > r9) goto L_0x01ed
            int r15 = r15 + 1
            if (r16 != 0) goto L_0x01e6
            int r7 = r8 + 1
            if (r7 < r4) goto L_0x01e3
            r7 = 1
            goto L_0x01e4
        L_0x01e3:
            r7 = 0
        L_0x01e4:
            r16 = r7
        L_0x01e6:
            r7 = r8
            goto L_0x01ed
        L_0x01e8:
            r25 = r9
        L_0x01ea:
            r9 = 65039(0xfe0f, float:9.1139E-41)
        L_0x01ed:
            int r6 = r6 + 1
            r9 = r25
            goto L_0x0194
        L_0x01f2:
            r25 = r9
            if (r24 == 0) goto L_0x01fd
            if (r10 == 0) goto L_0x01fd
            r3 = 0
            r10[r3] = r3     // Catch:{ Exception -> 0x0286 }
            r3 = 0
            r10 = r3
        L_0x01fd:
            if (r16 == 0) goto L_0x0228
            int r3 = r7 + 2
            if (r3 >= r4) goto L_0x0228
            int r6 = r7 + 1
            char r8 = r1.charAt(r6)     // Catch:{ Exception -> 0x0286 }
            r9 = 55356(0xd83c, float:7.757E-41)
            if (r8 != r9) goto L_0x0228
            char r8 = r1.charAt(r3)     // Catch:{ Exception -> 0x0286 }
            r9 = 57339(0xdffb, float:8.0349E-41)
            if (r8 < r9) goto L_0x0228
            r9 = 57343(0xdfff, float:8.0355E-41)
            if (r8 > r9) goto L_0x0228
            int r7 = r7 + 3
            java.lang.CharSequence r6 = r1.subSequence(r6, r7)     // Catch:{ Exception -> 0x0286 }
            r2.append(r6)     // Catch:{ Exception -> 0x0286 }
            int r15 = r15 + 2
            r7 = r3
        L_0x0228:
            if (r16 == 0) goto L_0x0266
            if (r10 == 0) goto L_0x0234
            r3 = 0
            r6 = r10[r3]     // Catch:{ Exception -> 0x0286 }
            r8 = 1
            int r6 = r6 + r8
            r10[r3] = r6     // Catch:{ Exception -> 0x0286 }
            goto L_0x0235
        L_0x0234:
            r3 = 0
        L_0x0235:
            int r6 = r2.length()     // Catch:{ Exception -> 0x0286 }
            java.lang.CharSequence r6 = r2.subSequence(r3, r6)     // Catch:{ Exception -> 0x0286 }
            org.telegram.messenger.Emoji$DrawableInfo r3 = getDrawableInfo(r6)     // Catch:{ Exception -> 0x0286 }
            if (r3 == 0) goto L_0x025b
            android.util.Pair r3 = new android.util.Pair     // Catch:{ Exception -> 0x0286 }
            android.util.Pair r8 = new android.util.Pair     // Catch:{ Exception -> 0x0286 }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0286 }
            int r14 = r14 + r15
            java.lang.Integer r11 = java.lang.Integer.valueOf(r14)     // Catch:{ Exception -> 0x0286 }
            r8.<init>(r9, r11)     // Catch:{ Exception -> 0x0286 }
            r3.<init>(r8, r6)     // Catch:{ Exception -> 0x0286 }
            r5.add(r3)     // Catch:{ Exception -> 0x0286 }
            int r17 = r17 + 1
        L_0x025b:
            r3 = 0
            r2.setLength(r3)     // Catch:{ Exception -> 0x0286 }
            r3 = r17
            r14 = -1
            r15 = 0
            r16 = 0
            goto L_0x0268
        L_0x0266:
            r3 = r17
        L_0x0268:
            int r6 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0286 }
            r8 = 23
            if (r6 < r8) goto L_0x0272
            r8 = 29
            if (r6 < r8) goto L_0x027b
        L_0x0272:
            boolean r6 = org.telegram.messenger.BuildVars.DEBUG_PRIVATE_VERSION     // Catch:{ Exception -> 0x0286 }
            if (r6 != 0) goto L_0x027b
            r6 = 50
            if (r3 < r6) goto L_0x027b
            goto L_0x028b
        L_0x027b:
            r6 = 1
            int r11 = r7 + 1
            r17 = r3
            r9 = r25
            r3 = 16
            goto L_0x0042
        L_0x0286:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            return r1
        L_0x028b:
            r7 = 0
        L_0x028c:
            int r1 = r5.size()
            if (r7 >= r1) goto L_0x02d3
            java.lang.Object r1 = r5.get(r7)
            android.util.Pair r1 = (android.util.Pair) r1
            java.lang.Object r1 = r1.first
            android.util.Pair r1 = (android.util.Pair) r1
            java.lang.Object r3 = r5.get(r7)
            android.util.Pair r3 = (android.util.Pair) r3
            java.lang.Object r3 = r3.second
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            org.telegram.messenger.Emoji$EmojiDrawable r3 = getEmojiDrawable(r3)
            if (r3 == 0) goto L_0x02cc
            org.telegram.messenger.Emoji$EmojiSpan r4 = new org.telegram.messenger.Emoji$EmojiSpan
            r6 = r27
            r8 = r28
            r9 = 0
            r4.<init>(r3, r9, r8, r6)
            java.lang.Object r3 = r1.first
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            java.lang.Object r1 = r1.second
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r9 = 33
            r0.setSpan(r4, r3, r1, r9)
            goto L_0x02d0
        L_0x02cc:
            r6 = r27
            r8 = r28
        L_0x02d0:
            int r7 = r7 + 1
            goto L_0x028c
        L_0x02d3:
            if (r10 == 0) goto L_0x02de
            int r1 = r2.length()
            if (r1 == 0) goto L_0x02de
            r1 = 0
            r10[r1] = r1
        L_0x02de:
            return r0
        L_0x02df:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.replaceEmoji(java.lang.CharSequence, android.graphics.Paint$FontMetricsInt, int, boolean, int[], boolean, java.util.concurrent.atomic.AtomicReference):java.lang.CharSequence");
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
