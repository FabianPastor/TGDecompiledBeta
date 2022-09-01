package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ReplacementSpan;
import android.util.LongSparseArray;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class AnimatedEmojiSpan extends ReplacementSpan {
    public int cacheType;
    public TLRPC$Document document;
    public long documentId;
    private Paint.FontMetricsInt fontMetrics;
    public boolean fromEmojiKeyboard;
    public boolean full;
    float lastDrawnCx;
    float lastDrawnCy;
    int measuredSize;
    /* access modifiers changed from: private */
    public boolean recordPositions;
    private float scale;
    private float size;
    boolean spanDrawn;
    public boolean standard;

    public AnimatedEmojiSpan(TLRPC$Document tLRPC$Document, Paint.FontMetricsInt fontMetricsInt) {
        this(tLRPC$Document.id, 1.2f, fontMetricsInt);
        this.document = tLRPC$Document;
    }

    public AnimatedEmojiSpan(long j, Paint.FontMetricsInt fontMetricsInt) {
        this(j, 1.2f, fontMetricsInt);
    }

    public AnimatedEmojiSpan(long j, float f, Paint.FontMetricsInt fontMetricsInt) {
        this.full = false;
        this.size = (float) AndroidUtilities.dp(20.0f);
        this.cacheType = -1;
        this.recordPositions = true;
        this.documentId = j;
        this.scale = f;
        this.fontMetrics = fontMetricsInt;
        if (fontMetricsInt != null) {
            float abs = (float) (Math.abs(fontMetricsInt.descent) + Math.abs(fontMetricsInt.ascent));
            this.size = abs;
            if (abs == 0.0f) {
                this.size = (float) AndroidUtilities.dp(20.0f);
            }
        }
    }

    public long getDocumentId() {
        TLRPC$Document tLRPC$Document = this.document;
        return tLRPC$Document != null ? tLRPC$Document.id : this.documentId;
    }

    public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2) {
        this.fontMetrics = fontMetricsInt;
        this.size = (float) i;
        this.cacheType = i2;
    }

    public void applyFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i) {
        this.fontMetrics = fontMetricsInt;
        this.cacheType = i;
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Paint.FontMetricsInt fontMetricsInt2 = this.fontMetrics;
        if (fontMetricsInt2 == null) {
            int i3 = (int) this.size;
            int dp = AndroidUtilities.dp(8.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            if (fontMetricsInt != null) {
                float f = (float) ((-dp2) - dp);
                float f2 = this.scale;
                fontMetricsInt.top = (int) (f * f2);
                float f3 = (float) (dp2 - dp);
                fontMetricsInt.bottom = (int) (f3 * f2);
                fontMetricsInt.ascent = (int) (f * f2);
                fontMetricsInt.descent = (int) (f3 * f2);
                fontMetricsInt.leading = 0;
            }
            this.measuredSize = (int) (((float) i3) * this.scale);
        } else {
            this.measuredSize = (int) (this.size * this.scale);
            if (fontMetricsInt != null) {
                if (!this.full) {
                    fontMetricsInt.ascent = fontMetricsInt2.ascent;
                    fontMetricsInt.descent = fontMetricsInt2.descent;
                    fontMetricsInt.top = fontMetricsInt2.top;
                    fontMetricsInt.bottom = fontMetricsInt2.bottom;
                } else {
                    float abs = (float) (Math.abs(fontMetricsInt2.bottom) + Math.abs(this.fontMetrics.top));
                    fontMetricsInt.ascent = (int) Math.ceil((double) ((((float) this.fontMetrics.top) / abs) * ((float) this.measuredSize)));
                    fontMetricsInt.descent = (int) Math.ceil((double) ((((float) this.fontMetrics.bottom) / abs) * ((float) this.measuredSize)));
                    fontMetricsInt.top = (int) Math.ceil((double) ((((float) this.fontMetrics.top) / abs) * ((float) this.measuredSize)));
                    fontMetricsInt.bottom = (int) Math.ceil((double) ((((float) this.fontMetrics.bottom) / abs) * ((float) this.measuredSize)));
                }
            }
        }
        return this.measuredSize - 1;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        if (this.recordPositions) {
            this.spanDrawn = true;
            float f2 = f + (((float) this.measuredSize) / 2.0f);
            float f3 = ((float) i3) + (((float) (i5 - i3)) / 2.0f);
            if (f2 != this.lastDrawnCx || f3 != this.lastDrawnCy) {
                this.lastDrawnCx = f2;
                this.lastDrawnCy = f3;
            }
        }
    }

    public static void drawAnimatedEmojis(Canvas canvas, Layout layout, EmojiGroupedSpans emojiGroupedSpans, float f, List<SpoilerEffect> list, float f2, float f3, float f4, float f5) {
        drawAnimatedEmojis(canvas, layout, emojiGroupedSpans, f, list, f2, f3, f4, f5, (ColorFilter) null);
    }

    public static void drawAnimatedEmojis(Canvas canvas, Layout layout, EmojiGroupedSpans emojiGroupedSpans, float f, List<SpoilerEffect> list, float f2, float f3, float f4, float f5, ColorFilter colorFilter) {
        boolean z;
        Layout layout2 = layout;
        EmojiGroupedSpans emojiGroupedSpans2 = emojiGroupedSpans;
        if (canvas != null && layout2 != null && emojiGroupedSpans2 != null) {
            int i = 0;
            if (Emoji.emojiDrawingYOffset == 0.0f && f == 0.0f) {
                z = false;
            } else {
                canvas.save();
                canvas.translate(0.0f, Emoji.emojiDrawingYOffset + ((float) AndroidUtilities.dp(20.0f * f)));
                z = true;
            }
            long currentTimeMillis = System.currentTimeMillis();
            while (true) {
                if (i >= emojiGroupedSpans2.backgroundDrawingArray.size()) {
                    break;
                }
                SpansChunk spansChunk = emojiGroupedSpans2.backgroundDrawingArray.get(i);
                if (spansChunk.layout == layout2) {
                    spansChunk.draw(canvas, list, currentTimeMillis, f2, f3, f4, f5, colorFilter);
                    break;
                }
                i++;
            }
            if (z) {
                canvas.restore();
            }
        }
    }

    private static boolean isInsideSpoiler(Layout layout, int i, int i2) {
        if (layout != null && (layout.getText() instanceof Spanned)) {
            TextStyleSpan[] textStyleSpanArr = (TextStyleSpan[]) ((Spanned) layout.getText()).getSpans(Math.max(0, i), Math.min(layout.getText().length() - 1, i2), TextStyleSpan.class);
            int i3 = 0;
            while (textStyleSpanArr != null && i3 < textStyleSpanArr.length) {
                if (textStyleSpanArr[i3] != null && textStyleSpanArr[i3].isSpoiler()) {
                    return true;
                }
                i3++;
            }
        }
        return false;
    }

    public static class AnimatedEmojiHolder {
        public float alpha;
        ImageReceiver.BackgroundThreadDrawHolder backgroundDrawHolder;
        public AnimatedEmojiDrawable drawable;
        public Rect drawableBounds;
        public float drawingYOffset;
        public boolean insideSpoiler;
        /* access modifiers changed from: private */
        public final boolean invalidateInParent;
        public Layout layout;
        public boolean skipDraw;
        public AnimatedEmojiSpan span;
        public SpansChunk spansChunk;
        /* access modifiers changed from: private */
        public final View view;

        public AnimatedEmojiHolder(View view2, boolean z) {
            this.view = view2;
            this.invalidateInParent = z;
        }

        public boolean outOfBounds(float f, float f2) {
            Rect rect = this.drawableBounds;
            return ((float) rect.bottom) < f || ((float) rect.top) > f2;
        }

        public void prepareForBackgroundDraw(long j) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.drawable;
            if (animatedEmojiDrawable != null) {
                ImageReceiver imageReceiver = animatedEmojiDrawable.getImageReceiver();
                this.drawable.update(j);
                this.drawable.setBounds(this.drawableBounds);
                if (imageReceiver != null) {
                    AnimatedEmojiSpan animatedEmojiSpan = this.span;
                    if (!(animatedEmojiSpan == null || animatedEmojiSpan.document != null || this.drawable.getDocument() == null)) {
                        this.span.document = this.drawable.getDocument();
                    }
                    imageReceiver.setAlpha(this.alpha);
                    imageReceiver.setImageCoords(this.drawableBounds);
                    ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = imageReceiver.setDrawInBackgroundThread(this.backgroundDrawHolder);
                    this.backgroundDrawHolder = drawInBackgroundThread;
                    drawInBackgroundThread.overrideAlpha = this.alpha;
                    drawInBackgroundThread.setBounds(this.drawableBounds);
                    this.backgroundDrawHolder.time = j;
                }
            }
        }

        public void releaseDrawInBackground() {
            ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder = this.backgroundDrawHolder;
            if (backgroundThreadDrawHolder != null) {
                backgroundThreadDrawHolder.release();
            }
        }

        public void draw(Canvas canvas, long j, float f, float f2, float f3) {
            if (!(f == 0.0f && f2 == 0.0f) && outOfBounds(f, f2)) {
                this.skipDraw = true;
                return;
            }
            this.skipDraw = false;
            if (this.drawable.getImageReceiver() != null) {
                this.drawable.setTime(j);
                this.drawable.draw(canvas, this.drawableBounds, f3 * this.alpha);
            }
        }

        public void invalidate() {
            View view2 = this.view;
            if (view2 == null) {
                return;
            }
            if (!this.invalidateInParent || view2.getParent() == null) {
                this.view.invalidate();
            } else {
                ((View) this.view.getParent()).invalidate();
            }
        }
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        return update(i, view, emojiGroupedSpans, arrayList, false);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList, boolean z) {
        return update(i, view, false, emojiGroupedSpans, arrayList, z);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        return update(i, view, z, emojiGroupedSpans, arrayList, false);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList, boolean z2) {
        Layout[] layoutArr = new Layout[(arrayList == null ? 0 : arrayList.size())];
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                layoutArr[i2] = arrayList.get(i2).textLayout;
            }
        }
        return update(i, view, z, emojiGroupedSpans, z2, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        return update(i, view, false, emojiGroupedSpans, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        return update(i, view, z, emojiGroupedSpans, false, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, boolean z2, Layout... layoutArr) {
        boolean z3;
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        boolean z4;
        AnimatedEmojiHolder animatedEmojiHolder;
        int i2;
        EmojiGroupedSpans emojiGroupedSpans2 = emojiGroupedSpans;
        Layout[] layoutArr2 = layoutArr;
        if (layoutArr2 == null || layoutArr2.length <= 0) {
            if (emojiGroupedSpans2 != null) {
                emojiGroupedSpans2.holders.clear();
                emojiGroupedSpans.release();
            }
            return null;
        }
        int i3 = 0;
        int i4 = 0;
        while (i4 < layoutArr2.length) {
            Layout layout = layoutArr2[i4];
            if (layout == null || !(layout.getText() instanceof Spanned)) {
                View view2 = view;
                boolean z5 = z;
                animatedEmojiSpanArr = null;
            } else {
                Spanned spanned = (Spanned) layout.getText();
                animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(i3, spanned.length(), AnimatedEmojiSpan.class);
                int i5 = 0;
                while (animatedEmojiSpanArr != null && i5 < animatedEmojiSpanArr.length) {
                    AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiSpanArr[i5];
                    if (animatedEmojiSpan == null) {
                        View view3 = view;
                        boolean z6 = z;
                    } else {
                        if (z2 && (layout.getText() instanceof Spannable)) {
                            int spanStart = spanned.getSpanStart(animatedEmojiSpan);
                            int spanEnd = spanned.getSpanEnd(animatedEmojiSpan);
                            Spannable spannable = (Spannable) spanned;
                            spannable.removeSpan(animatedEmojiSpan);
                            animatedEmojiSpan = cloneSpan(animatedEmojiSpan);
                            spannable.setSpan(animatedEmojiSpan, spanStart, spanEnd, 33);
                        }
                        if (emojiGroupedSpans2 == null) {
                            emojiGroupedSpans2 = new EmojiGroupedSpans();
                        }
                        int i6 = 0;
                        while (true) {
                            if (i6 < emojiGroupedSpans2.holders.size()) {
                                if (emojiGroupedSpans2.holders.get(i6).span == animatedEmojiSpan && emojiGroupedSpans2.holders.get(i6).layout == layout) {
                                    animatedEmojiHolder = emojiGroupedSpans2.holders.get(i6);
                                    break;
                                }
                                i6++;
                            } else {
                                animatedEmojiHolder = null;
                                break;
                            }
                        }
                        if (animatedEmojiHolder == null) {
                            AnimatedEmojiHolder animatedEmojiHolder2 = new AnimatedEmojiHolder(view, z);
                            animatedEmojiHolder2.layout = layout;
                            if (animatedEmojiSpan.standard) {
                                i2 = 8;
                            } else {
                                i2 = animatedEmojiSpan.cacheType;
                                if (i2 < 0) {
                                    i2 = i;
                                }
                            }
                            TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                            if (tLRPC$Document != null) {
                                animatedEmojiHolder2.drawable = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, tLRPC$Document);
                            } else {
                                animatedEmojiHolder2.drawable = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, animatedEmojiSpan.documentId);
                                spanned = spanned;
                            }
                            animatedEmojiHolder2.insideSpoiler = isInsideSpoiler(layout, spanned.getSpanStart(animatedEmojiSpan), spanned.getSpanEnd(animatedEmojiSpan));
                            animatedEmojiHolder2.drawableBounds = new Rect();
                            animatedEmojiHolder2.span = animatedEmojiSpan;
                            emojiGroupedSpans2.add(layout, animatedEmojiHolder2);
                        } else {
                            View view4 = view;
                            boolean z7 = z;
                            animatedEmojiHolder.insideSpoiler = isInsideSpoiler(layout, spanned.getSpanStart(animatedEmojiSpan), spanned.getSpanEnd(animatedEmojiSpan));
                        }
                    }
                    i5++;
                }
                View view5 = view;
                boolean z8 = z;
            }
            if (emojiGroupedSpans2 != null) {
                int i7 = 0;
                while (i7 < emojiGroupedSpans2.holders.size()) {
                    if (emojiGroupedSpans2.holders.get(i7).layout == layout) {
                        AnimatedEmojiSpan animatedEmojiSpan2 = emojiGroupedSpans2.holders.get(i7).span;
                        int i8 = 0;
                        while (true) {
                            if (animatedEmojiSpanArr == null || i8 >= animatedEmojiSpanArr.length) {
                                z4 = false;
                            } else if (animatedEmojiSpanArr[i8] == animatedEmojiSpan2) {
                                z4 = true;
                                break;
                            } else {
                                i8++;
                            }
                        }
                        if (!z4) {
                            emojiGroupedSpans2.remove(i7);
                            i7--;
                        }
                    }
                    i7++;
                }
            }
            i4++;
            i3 = 0;
        }
        if (emojiGroupedSpans2 != null) {
            int i9 = 0;
            while (i9 < emojiGroupedSpans2.holders.size()) {
                Layout layout2 = emojiGroupedSpans2.holders.get(i9).layout;
                int i10 = 0;
                while (true) {
                    if (i10 >= layoutArr2.length) {
                        z3 = false;
                        break;
                    } else if (layoutArr2[i10] == layout2) {
                        z3 = true;
                        break;
                    } else {
                        i10++;
                    }
                }
                if (!z3) {
                    emojiGroupedSpans2.remove(i9);
                    i9--;
                }
                i9++;
            }
        }
        return emojiGroupedSpans2;
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(int i, View view, AnimatedEmojiSpan[] animatedEmojiSpanArr, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        int i2;
        AnimatedEmojiDrawable animatedEmojiDrawable;
        boolean z;
        if (animatedEmojiSpanArr == null) {
            return longSparseArray;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
        }
        int i3 = 0;
        while (i3 < longSparseArray.size()) {
            long keyAt = longSparseArray.keyAt(i3);
            AnimatedEmojiDrawable animatedEmojiDrawable2 = longSparseArray.get(keyAt);
            if (animatedEmojiDrawable2 == null) {
                longSparseArray.remove(keyAt);
            } else {
                int i4 = 0;
                while (true) {
                    if (i4 < animatedEmojiSpanArr.length) {
                        if (animatedEmojiSpanArr[i4] != null && animatedEmojiSpanArr[i4].getDocumentId() == keyAt) {
                            z = true;
                            break;
                        }
                        i4++;
                    } else {
                        z = false;
                        break;
                    }
                }
                if (!z) {
                    animatedEmojiDrawable2.removeView((Drawable.Callback) view);
                    longSparseArray.remove(keyAt);
                } else {
                    i3++;
                }
            }
            i3--;
            i3++;
        }
        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
            if (animatedEmojiSpan != null && longSparseArray.get(animatedEmojiSpan.getDocumentId()) == null) {
                if (animatedEmojiSpan.standard) {
                    i2 = 8;
                } else {
                    i2 = animatedEmojiSpan.cacheType;
                    if (i2 < 0) {
                        i2 = i;
                    }
                }
                TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                if (tLRPC$Document != null) {
                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, tLRPC$Document);
                } else {
                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(UserConfig.selectedAccount, i2, animatedEmojiSpan.documentId);
                }
                animatedEmojiDrawable.addView(view);
                longSparseArray.put(animatedEmojiSpan.getDocumentId(), animatedEmojiDrawable);
            }
        }
        return longSparseArray;
    }

    public static void release(View view, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        if (longSparseArray != null) {
            for (int i = 0; i < longSparseArray.size(); i++) {
                AnimatedEmojiDrawable valueAt = longSparseArray.valueAt(i);
                if (valueAt != null) {
                    valueAt.removeView((Drawable.Callback) view);
                }
            }
            longSparseArray.clear();
        }
    }

    public static void release(View view, EmojiGroupedSpans emojiGroupedSpans) {
        if (emojiGroupedSpans != null) {
            emojiGroupedSpans.release();
        }
    }

    public static class EmojiGroupedSpans {
        ArrayList<SpansChunk> backgroundDrawingArray = new ArrayList<>();
        HashMap<Layout, SpansChunk> groupedByLayout = new HashMap<>();
        public ArrayList<AnimatedEmojiHolder> holders = new ArrayList<>();

        public void add(Layout layout, AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            SpansChunk spansChunk = this.groupedByLayout.get(layout);
            if (spansChunk == null) {
                spansChunk = new SpansChunk(animatedEmojiHolder.view, layout, animatedEmojiHolder.invalidateInParent);
                this.groupedByLayout.put(layout, spansChunk);
                this.backgroundDrawingArray.add(spansChunk);
            }
            spansChunk.add(animatedEmojiHolder);
            animatedEmojiHolder.drawable.addView(animatedEmojiHolder);
        }

        public void remove(int i) {
            AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i);
            this.holders.remove(i);
            SpansChunk spansChunk = this.groupedByLayout.get(animatedEmojiHolder.layout);
            if (spansChunk != null) {
                spansChunk.remove(animatedEmojiHolder);
                if (spansChunk.holders.isEmpty()) {
                    this.groupedByLayout.remove(animatedEmojiHolder.layout);
                    this.backgroundDrawingArray.remove(spansChunk);
                }
                animatedEmojiHolder.drawable.removeView(animatedEmojiHolder);
                return;
            }
            throw new RuntimeException("!!!");
        }

        public void release() {
            while (this.holders.size() > 0) {
                remove(0);
            }
        }

        public void clearPositions() {
            for (int i = 0; i < this.holders.size(); i++) {
                this.holders.get(i).span.spanDrawn = false;
            }
        }

        public void recordPositions(boolean z) {
            for (int i = 0; i < this.holders.size(); i++) {
                boolean unused = this.holders.get(i).span.recordPositions = z;
            }
        }

        public void replaceLayout(Layout layout, Layout layout2) {
            SpansChunk remove;
            if (layout2 != null && (remove = this.groupedByLayout.remove(layout2)) != null) {
                remove.layout = layout;
                for (int i = 0; i < remove.holders.size(); i++) {
                    remove.holders.get(i).layout = layout;
                }
                this.groupedByLayout.put(layout, remove);
            }
        }
    }

    private static class SpansChunk {
        private boolean allowBackgroundRendering;
        DrawingInBackgroundThreadDrawable backgroundThreadDrawable;
        ArrayList<AnimatedEmojiHolder> holders = new ArrayList<>();
        Layout layout;
        final View view;

        public SpansChunk(View view2, Layout layout2, boolean z) {
            this.layout = layout2;
            this.view = view2;
            this.allowBackgroundRendering = z;
        }

        public void add(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = this;
            checkBackgroundRendering();
        }

        public void remove(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.remove(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = null;
            checkBackgroundRendering();
        }

        private void checkBackgroundRendering() {
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable;
            if (this.allowBackgroundRendering && this.holders.size() >= 10 && this.backgroundThreadDrawable == null) {
                AnonymousClass1 r0 = new DrawingInBackgroundThreadDrawable() {
                    private final ArrayList<AnimatedEmojiHolder> backgroundHolders = new ArrayList<>();

                    public void drawInBackground(Canvas canvas) {
                        ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            AnimatedEmojiHolder animatedEmojiHolder = this.backgroundHolders.get(i);
                            if (!(animatedEmojiHolder == null || (backgroundThreadDrawHolder = animatedEmojiHolder.backgroundDrawHolder) == null)) {
                                animatedEmojiHolder.drawable.draw(canvas, backgroundThreadDrawHolder);
                            }
                        }
                    }

                    public void drawInUiThread(Canvas canvas, float f) {
                        long currentTimeMillis = System.currentTimeMillis();
                        for (int i = 0; i < SpansChunk.this.holders.size(); i++) {
                            AnimatedEmojiHolder animatedEmojiHolder = SpansChunk.this.holders.get(i);
                            if (animatedEmojiHolder.span.spanDrawn) {
                                animatedEmojiHolder.draw(canvas, currentTimeMillis, 0.0f, 0.0f, f);
                            }
                        }
                    }

                    public void prepareDraw(long j) {
                        this.backgroundHolders.clear();
                        this.backgroundHolders.addAll(SpansChunk.this.holders);
                        int i = 0;
                        while (i < this.backgroundHolders.size()) {
                            AnimatedEmojiHolder animatedEmojiHolder = this.backgroundHolders.get(i);
                            if (!animatedEmojiHolder.span.spanDrawn) {
                                this.backgroundHolders.remove(i);
                                i--;
                            } else {
                                animatedEmojiHolder.prepareForBackgroundDraw(j);
                            }
                            i++;
                        }
                    }

                    public void onFrameReady() {
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            if (this.backgroundHolders.get(i) != null) {
                                this.backgroundHolders.get(i).releaseDrawInBackground();
                            }
                        }
                        this.backgroundHolders.clear();
                        View view = SpansChunk.this.view;
                        if (view != null && view.getParent() != null) {
                            ((View) SpansChunk.this.view.getParent()).invalidate();
                        }
                    }

                    public void onPaused() {
                        super.onPaused();
                    }

                    public void onResume() {
                        View view = SpansChunk.this.view;
                        if (view != null && view.getParent() != null) {
                            ((View) SpansChunk.this.view.getParent()).invalidate();
                        }
                    }
                };
                this.backgroundThreadDrawable = r0;
                r0.padding = AndroidUtilities.dp(3.0f);
                this.backgroundThreadDrawable.onAttachToWindow();
            } else if (this.holders.size() < 10 && (drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable) != null) {
                drawingInBackgroundThreadDrawable.onDetachFromWindow();
                this.backgroundThreadDrawable = null;
            }
        }

        public void draw(Canvas canvas, List<SpoilerEffect> list, long j, float f, float f2, float f3, float f4, ColorFilter colorFilter) {
            AnimatedEmojiDrawable animatedEmojiDrawable;
            List<SpoilerEffect> list2 = list;
            for (int i = 0; i < this.holders.size(); i++) {
                AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i);
                if (animatedEmojiHolder == null || (animatedEmojiDrawable = animatedEmojiHolder.drawable) == null) {
                    float f5 = f3;
                    ColorFilter colorFilter2 = colorFilter;
                } else {
                    animatedEmojiDrawable.setColorFilter(colorFilter);
                    AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiHolder.span;
                    if (!animatedEmojiSpan.spanDrawn) {
                        float f6 = f3;
                    } else {
                        float f7 = ((float) animatedEmojiSpan.measuredSize) / 2.0f;
                        float f8 = animatedEmojiSpan.lastDrawnCx;
                        float f9 = animatedEmojiSpan.lastDrawnCy;
                        animatedEmojiHolder.drawableBounds.set((int) (f8 - f7), (int) (f9 - f7), (int) (f8 + f7), (int) (f9 + f7));
                        float var_ = 1.0f;
                        if (list2 != null && !list.isEmpty() && animatedEmojiHolder.insideSpoiler) {
                            var_ = Math.max(0.0f, list2.get(0).getRippleProgress());
                        }
                        animatedEmojiHolder.drawingYOffset = f3;
                        animatedEmojiHolder.alpha = var_;
                        if (this.backgroundThreadDrawable == null) {
                            animatedEmojiHolder.draw(canvas, j, f, f2, f4);
                        }
                    }
                }
            }
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable;
            if (drawingInBackgroundThreadDrawable != null) {
                drawingInBackgroundThreadDrawable.draw(canvas, j, this.layout.getWidth(), this.layout.getHeight() + AndroidUtilities.dp(2.0f), f4);
            }
        }
    }

    public static AnimatedEmojiSpan cloneSpan(AnimatedEmojiSpan animatedEmojiSpan) {
        TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
        if (tLRPC$Document != null) {
            return new AnimatedEmojiSpan(tLRPC$Document, animatedEmojiSpan.fontMetrics);
        }
        return new AnimatedEmojiSpan(animatedEmojiSpan.documentId, animatedEmojiSpan.scale, animatedEmojiSpan.fontMetrics);
    }

    public static CharSequence cloneSpans(CharSequence charSequence) {
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        Spanned spanned = (Spanned) charSequence;
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        SpannableString spannableString = charSequence;
        if (characterStyleArr != null) {
            spannableString = charSequence;
            if (characterStyleArr.length > 0) {
                AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(0, spanned.length(), AnimatedEmojiSpan.class);
                if (animatedEmojiSpanArr != null && animatedEmojiSpanArr.length <= 0) {
                    return charSequence;
                }
                SpannableString spannableString2 = new SpannableString(spanned);
                for (int i = 0; i < characterStyleArr.length; i++) {
                    if (characterStyleArr[i] != null && (characterStyleArr[i] instanceof AnimatedEmojiSpan)) {
                        int spanStart = spanned.getSpanStart(characterStyleArr[i]);
                        int spanEnd = spanned.getSpanEnd(characterStyleArr[i]);
                        AnimatedEmojiSpan animatedEmojiSpan = (AnimatedEmojiSpan) characterStyleArr[i];
                        spannableString2.removeSpan(animatedEmojiSpan);
                        spannableString2.setSpan(cloneSpan(animatedEmojiSpan), spanStart, spanEnd, 33);
                    }
                }
                spannableString = spannableString2;
            }
        }
        return spannableString;
    }
}
