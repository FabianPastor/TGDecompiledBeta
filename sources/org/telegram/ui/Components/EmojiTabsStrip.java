package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.PremiumLockIconView;

public class EmojiTabsStrip extends ScrollableHorizontalScrollView {
    /* access modifiers changed from: private */
    public static int[] emojiTabsAnimatedDrawableIds = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    /* access modifiers changed from: private */
    public static int[] emojiTabsDrawableIds = {NUM, NUM, NUM, NUM, NUM, NUM, NUM, NUM};
    private ValueAnimator appearAnimation;
    private int appearCount;
    /* access modifiers changed from: private */
    public LinearLayout contentView;
    /* access modifiers changed from: private */
    public EmojiTabsView emojiTabs;
    /* access modifiers changed from: private */
    public ArrayList<EmojiTabButton> emojipackTabs;
    boolean first = true;
    private boolean includeAnimated;
    private Runnable onSettingsOpenRunnable;
    private EmojiView parent;
    private int recentDrawableId = NUM;
    private EmojiTabButton recentTab;
    /* access modifiers changed from: private */
    public Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public float selectAnimationT = 0.0f;
    private ValueAnimator selectAnimator;
    /* access modifiers changed from: private */
    public float selectT = 0.0f;
    private int selected = 0;
    private int settingsDrawableId = NUM;
    /* access modifiers changed from: private */
    public EmojiTabButton settingsTab;
    /* access modifiers changed from: private */
    public boolean wasDrawn;
    private int wasIndex = 0;

    /* access modifiers changed from: protected */
    public boolean onTabClick(int i) {
        throw null;
    }

    public EmojiTabsStrip(Context context, Theme.ResourcesProvider resourcesProvider2, final boolean z, EmojiView emojiView, Runnable runnable) {
        super(context);
        this.parent = emojiView;
        this.includeAnimated = z;
        this.resourcesProvider = resourcesProvider2;
        this.onSettingsOpenRunnable = runnable;
        AnonymousClass1 r10 = new LinearLayout(context) {
            private RectF from = new RectF();
            HashMap<Integer, Integer> lastX = new HashMap<>();
            private Paint paint = new Paint(1);
            private Path path = new Path();
            private RectF rect = new RectF();
            private RectF to = new RectF();

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int i5 = (i4 - i2) / 2;
                int i6 = 0;
                if (z) {
                    int paddingLeft = getPaddingLeft();
                    while (i6 < getChildCount()) {
                        View childAt = getChildAt(i6);
                        if (!(childAt == EmojiTabsStrip.this.settingsTab || childAt == null)) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            Integer num = childAt instanceof EmojiTabButton ? ((EmojiTabButton) childAt).id : null;
                            if (num != null) {
                                if (!(this.lastX.get(num) == null || this.lastX.get(num).intValue() == paddingLeft)) {
                                    childAt.setTranslationX((float) (this.lastX.get(num).intValue() - paddingLeft));
                                    childAt.animate().translationX(0.0f).setDuration(250).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                                }
                                this.lastX.put(num, Integer.valueOf(paddingLeft));
                            }
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(3.0f);
                        }
                        i6++;
                    }
                    if (EmojiTabsStrip.this.settingsTab == null) {
                        return;
                    }
                    if (EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + paddingLeft + getPaddingRight() <= EmojiTabsStrip.this.getMeasuredWidth()) {
                        int i7 = i3 - i;
                        EmojiTabsStrip.this.settingsTab.layout((i7 - getPaddingRight()) - EmojiTabsStrip.this.settingsTab.getMeasuredWidth(), i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), i7 - getPaddingRight(), i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                        return;
                    }
                    EmojiTabsStrip.this.settingsTab.layout(paddingLeft, i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + paddingLeft, i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                    return;
                }
                int paddingLeft2 = (int) (((float) ((((i3 - i) - getPaddingLeft()) - getPaddingRight()) - (getChildCount() * AndroidUtilities.dp(30.0f)))) / ((float) Math.max(1, getChildCount() - 1)));
                int paddingLeft3 = getPaddingLeft();
                while (i6 < getChildCount()) {
                    View childAt2 = getChildAt(i6);
                    if (childAt2 != null) {
                        childAt2.layout(paddingLeft3, i5 - (childAt2.getMeasuredHeight() / 2), childAt2.getMeasuredWidth() + paddingLeft3, (childAt2.getMeasuredHeight() / 2) + i5);
                        paddingLeft3 += childAt2.getMeasuredWidth() + paddingLeft2;
                    }
                    i6++;
                }
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(99999999, Integer.MIN_VALUE);
                int paddingLeft = getPaddingLeft() + getPaddingRight();
                for (int i3 = 0; i3 < getChildCount(); i3++) {
                    View childAt = getChildAt(i3);
                    if (childAt != null) {
                        childAt.measure(makeMeasureSpec, i2);
                        paddingLeft += childAt.getMeasuredWidth() + (i3 + 1 < getChildCount() ? AndroidUtilities.dp(3.0f) : 0);
                    }
                }
                if (!z) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                } else {
                    setMeasuredDimension(Math.max(paddingLeft, View.MeasureSpec.getSize(i)), View.MeasureSpec.getSize(i2));
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                int floor = (int) Math.floor((double) EmojiTabsStrip.this.selectT);
                getChildBounds(floor, this.from);
                getChildBounds((int) Math.ceil((double) EmojiTabsStrip.this.selectT), this.to);
                AndroidUtilities.lerp(this.from, this.to, EmojiTabsStrip.this.selectT - ((float) floor), this.rect);
                float f = 0.0f;
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    f = MathUtils.clamp(1.0f - Math.abs(EmojiTabsStrip.this.selectT - 1.0f), 0.0f, 1.0f);
                }
                float access$300 = EmojiTabsStrip.this.selectAnimationT * 4.0f * (1.0f - EmojiTabsStrip.this.selectAnimationT);
                float width = (this.rect.width() / 2.0f) * ((0.3f * access$300) + 1.0f);
                float height = (this.rect.height() / 2.0f) * (1.0f - (access$300 * 0.05f));
                RectF rectF = this.rect;
                rectF.set(rectF.centerX() - width, this.rect.centerY() - height, this.rect.centerX() + width, this.rect.centerY() + height);
                float dp = (float) AndroidUtilities.dp(AndroidUtilities.lerp(8.0f, 16.0f, f));
                this.paint.setColor(EmojiTabsStrip.this.selectorColor());
                this.path.rewind();
                this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    this.path.addCircle((float) (EmojiTabsStrip.this.emojiTabs.getLeft() + AndroidUtilities.dp(15.0f)), ((float) (EmojiTabsStrip.this.emojiTabs.getTop() + EmojiTabsStrip.this.emojiTabs.getBottom())) / 2.0f, (float) AndroidUtilities.dp(15.0f), Path.Direction.CW);
                }
                super.dispatchDraw(canvas);
                boolean unused = EmojiTabsStrip.this.wasDrawn = true;
            }

            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view != EmojiTabsStrip.this.emojiTabs) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipPath(this.path);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            private void getChildBounds(int i, RectF rectF) {
                View childAt = getChildAt(MathUtils.clamp(i, 0, getChildCount() - 1));
                rectF.set((float) childAt.getLeft(), (float) childAt.getTop(), (float) childAt.getRight(), (float) childAt.getBottom());
            }
        };
        this.contentView = r10;
        r10.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(3.0f), 0);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        LinearLayout linearLayout = this.contentView;
        EmojiTabButton emojiTabButton = new EmojiTabButton(context, this.recentDrawableId, false, false);
        this.recentTab = emojiTabButton;
        linearLayout.addView(emojiTabButton);
        if (!z) {
            int i = 0;
            while (true) {
                int[] iArr = emojiTabsDrawableIds;
                if (i < iArr.length) {
                    this.contentView.addView(new EmojiTabButton(context, iArr[i], false, i == 0));
                    i++;
                } else {
                    updateClickListeners();
                    return;
                }
            }
        } else {
            if (runnable != null) {
                this.settingsTab = new EmojiTabButton(context, this.settingsDrawableId, false, true);
            }
            LinearLayout linearLayout2 = this.contentView;
            EmojiTabsView emojiTabsView = new EmojiTabsView(context);
            this.emojiTabs = emojiTabsView;
            linearLayout2.addView(emojiTabsView);
            updateEmojiPacks();
        }
    }

    private boolean isFreeEmojiPack(TLRPC$StickerSet tLRPC$StickerSet, ArrayList<TLRPC$Document> arrayList) {
        if (tLRPC$StickerSet == null || arrayList == null) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (!MessageObject.isFreeEmoji(arrayList.get(i))) {
                return false;
            }
        }
        return true;
    }

    private TLRPC$Document getThumbDocument(TLRPC$StickerSet tLRPC$StickerSet, ArrayList<TLRPC$Document> arrayList) {
        if (tLRPC$StickerSet == null) {
            return null;
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Document tLRPC$Document = arrayList.get(i);
                if (tLRPC$Document.id == tLRPC$StickerSet.thumb_document_id) {
                    return tLRPC$Document;
                }
            }
        }
        if (arrayList == null || arrayList.size() < 1) {
            return null;
        }
        return arrayList.get(0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e3, code lost:
        if (r1.getDocumentId() != r2.id) goto L_0x00e8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x018c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateEmojiPacks() {
        /*
            r19 = this;
            r7 = r19
            boolean r0 = r7.includeAnimated
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            boolean r0 = r7.first
            if (r0 == 0) goto L_0x0019
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            r1 = 5
            boolean r0 = r0.areStickersLoaded(r1)
            if (r0 != 0) goto L_0x0019
            return
        L_0x0019:
            r8 = 0
            r7.first = r8
            org.telegram.ui.Components.EmojiView r0 = r7.parent
            java.util.ArrayList r9 = r0.getEmojipacks()
            if (r9 != 0) goto L_0x0025
            return
        L_0x0025:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            if (r0 != 0) goto L_0x0030
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r7.emojipackTabs = r0
        L_0x0030:
            android.widget.LinearLayout r0 = r7.contentView
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r1 = r7.settingsTab
            r0.removeView(r1)
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0053
            int r0 = r9.size()
            if (r0 <= 0) goto L_0x0053
            int r0 = r7.appearCount
            int r1 = r9.size()
            if (r0 == r1) goto L_0x0053
            boolean r0 = r7.wasDrawn
            if (r0 == 0) goto L_0x0053
            r11 = 1
            goto L_0x0054
        L_0x0053:
            r11 = 0
        L_0x0054:
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r12 = 0
            if (r0 == 0) goto L_0x0068
            int r0 = r7.appearCount
            int r1 = r9.size()
            if (r0 == r1) goto L_0x0068
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r0.cancel()
            r7.appearAnimation = r12
        L_0x0068:
            int r0 = r9.size()
            r7.appearCount = r0
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r13 = r0.isPremium()
            r14 = 0
        L_0x0079:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            int r0 = r0.size()
            int r1 = r9.size()
            int r0 = java.lang.Math.max(r0, r1)
            r15 = 2
            if (r14 >= r0) goto L_0x0199
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            int r0 = r0.size()
            if (r14 < r0) goto L_0x0094
            r0 = r12
            goto L_0x009c
        L_0x0094:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            java.lang.Object r0 = r0.get(r14)
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = (org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton) r0
        L_0x009c:
            if (r0 != 0) goto L_0x00a0
            r1 = r12
            goto L_0x00a6
        L_0x00a0:
            android.graphics.drawable.Drawable r1 = r0.getDrawable()
            org.telegram.ui.Components.AnimatedEmojiDrawable r1 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r1
        L_0x00a6:
            int r2 = r9.size()
            if (r14 < r2) goto L_0x00c6
            if (r1 == 0) goto L_0x00b1
            r1.removeView((android.view.View) r0)
        L_0x00b1:
            android.widget.LinearLayout r0 = r7.contentView
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            java.lang.Object r1 = r1.remove(r14)
            android.view.View r1 = (android.view.View) r1
            r0.removeView(r1)
            int r14 = r14 + -1
            r1 = r12
            r16 = r13
        L_0x00c3:
            r0 = 1
            goto L_0x0193
        L_0x00c6:
            java.lang.Object r2 = r9.get(r14)
            r6 = r2
            org.telegram.ui.Components.EmojiView$EmojiPack r6 = (org.telegram.ui.Components.EmojiView.EmojiPack) r6
            org.telegram.tgnet.TLRPC$StickerSet r2 = r6.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r6.documents
            org.telegram.tgnet.TLRPC$Document r2 = r7.getThumbDocument(r2, r3)
            if (r2 == 0) goto L_0x00f0
            if (r1 == 0) goto L_0x00e6
            long r3 = r1.getDocumentId()
            r16 = r13
            long r12 = r2.id
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 == 0) goto L_0x00f2
            goto L_0x00e8
        L_0x00e6:
            r16 = r13
        L_0x00e8:
            int r1 = org.telegram.messenger.UserConfig.selectedAccount
            r3 = 3
            org.telegram.ui.Components.AnimatedEmojiDrawable r1 = org.telegram.ui.Components.AnimatedEmojiDrawable.make((int) r1, (int) r3, (org.telegram.tgnet.TLRPC$Document) r2)
            goto L_0x00f2
        L_0x00f0:
            r16 = r13
        L_0x00f2:
            r12 = r1
            org.telegram.tgnet.TLRPC$StickerSet r1 = r6.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r6.documents
            boolean r13 = r7.isFreeEmojiPack(r1, r2)
            if (r0 != 0) goto L_0x011e
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r17 = new org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton
            android.content.Context r2 = r19.getContext()
            r5 = 0
            r18 = 0
            r0 = r17
            r1 = r19
            r3 = r12
            r4 = r13
            r10 = r6
            r6 = r18
            r0.<init>((android.content.Context) r2, (android.graphics.drawable.Drawable) r3, (boolean) r4, (boolean) r5, (boolean) r6)
            if (r12 == 0) goto L_0x011b
            android.widget.ImageView r0 = r17.imageView
            r12.addView((android.view.View) r0)
        L_0x011b:
            r0 = r17
            goto L_0x0140
        L_0x011e:
            r10 = r6
            android.graphics.drawable.Drawable r1 = r0.getDrawable()
            boolean r1 = r1 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
            if (r1 == 0) goto L_0x0134
            android.graphics.drawable.Drawable r1 = r0.getDrawable()
            org.telegram.ui.Components.AnimatedEmojiDrawable r1 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r1
            android.widget.ImageView r2 = r0.imageView
            r1.removeView((android.view.View) r2)
        L_0x0134:
            r0.setDrawable(r12)
            if (r12 == 0) goto L_0x0140
            android.widget.ImageView r1 = r0.imageView
            r12.addView((android.view.View) r1)
        L_0x0140:
            java.lang.Object[] r1 = new java.lang.Object[r15]
            org.telegram.tgnet.TLRPC$StickerSet r2 = r10.set
            long r2 = r2.id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r1[r8] = r2
            boolean r2 = r10.featured
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
            r3 = 1
            r1[r3] = r2
            int r1 = java.util.Arrays.hashCode(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r0.id = r1
            if (r16 != 0) goto L_0x0169
            if (r13 != 0) goto L_0x0169
            java.lang.Boolean r1 = java.lang.Boolean.TRUE
            r0.setLock(r1)
            goto L_0x0172
        L_0x0169:
            boolean r1 = r10.installed
            if (r1 != 0) goto L_0x0174
            java.lang.Boolean r1 = java.lang.Boolean.FALSE
            r0.setLock(r1)
        L_0x0172:
            r1 = 0
            goto L_0x0178
        L_0x0174:
            r1 = 0
            r0.setLock(r1)
        L_0x0178:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r2 = r7.emojipackTabs
            int r2 = r2.size()
            if (r14 < r2) goto L_0x018c
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r2 = r7.emojipackTabs
            r2.add(r0)
            android.widget.LinearLayout r2 = r7.contentView
            r2.addView(r0)
            goto L_0x00c3
        L_0x018c:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r2 = r7.emojipackTabs
            r2.set(r14, r0)
            goto L_0x00c3
        L_0x0193:
            int r14 = r14 + r0
            r12 = r1
            r13 = r16
            goto L_0x0079
        L_0x0199:
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r7.settingsTab
            if (r0 == 0) goto L_0x01a2
            android.widget.LinearLayout r1 = r7.contentView
            r1.addView(r0)
        L_0x01a2:
            if (r11 == 0) goto L_0x0217
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            if (r0 == 0) goto L_0x01cb
            r0 = 0
        L_0x01a9:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x01cb
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            java.lang.Object r1 = r1.get(r0)
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r1 = (org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton) r1
            r2 = 0
            r1.setScaleX(r2)
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            java.lang.Object r1 = r1.get(r0)
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r1 = (org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton) r1
            r1.setScaleY(r2)
            int r0 = r0 + 1
            goto L_0x01a9
        L_0x01cb:
            float[] r0 = new float[r15]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r7.appearAnimation = r0
            android.view.animation.OvershootInterpolator r0 = new android.view.animation.OvershootInterpolator
            r1 = 1077936128(0x40400000, float:3.0)
            r0.<init>(r1)
            android.animation.ValueAnimator r1 = r7.appearAnimation
            org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1 r2 = new org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1
            r2.<init>(r7, r0)
            r1.addUpdateListener(r2)
            android.animation.ValueAnimator r0 = r7.appearAnimation
            org.telegram.ui.Components.EmojiTabsStrip$2 r1 = new org.telegram.ui.Components.EmojiTabsStrip$2
            r1.<init>()
            r0.addListener(r1)
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r1 = 150(0x96, double:7.4E-322)
            r0.setStartDelay(r1)
            android.animation.ValueAnimator r0 = r7.appearAnimation
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            if (r1 != 0) goto L_0x01ff
            goto L_0x0203
        L_0x01ff:
            int r8 = r1.size()
        L_0x0203:
            long r1 = (long) r8
            r3 = 75
            long r1 = r1 * r3
            r0.setDuration(r1)
            android.animation.ValueAnimator r0 = r7.appearAnimation
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT
            r0.setInterpolator(r1)
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r0.start()
        L_0x0217:
            r19.updateClickListeners()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiTabsStrip.updateEmojiPacks():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmojiPacks$0(OvershootInterpolator overshootInterpolator, ValueAnimator valueAnimator) {
        if (this.emojipackTabs != null) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            int size = this.emojipackTabs.size();
            float f = (float) size;
            float f2 = (1.0f / f) * 4.5f;
            for (int i = 0; i < size; i++) {
                float interpolation = overshootInterpolator.getInterpolation(MathUtils.clamp((floatValue - ((((float) i) / f) * (1.0f - f2))) / f2, 0.0f, 1.0f));
                this.emojipackTabs.get(i).setScaleX(interpolation);
                this.emojipackTabs.get(i).setScaleY(interpolation);
            }
        }
    }

    public void updateClickListeners() {
        int i = 0;
        int i2 = 0;
        while (i < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i3 = 0;
                while (i3 < emojiTabsView.contentView.getChildCount()) {
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda3(this, i2));
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda4(this, i2));
            }
            i++;
            i2++;
        }
        EmojiTabButton emojiTabButton = this.settingsTab;
        if (emojiTabButton != null) {
            emojiTabButton.setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda2(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$1(int i, View view) {
        onTabClick(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$2(int i, View view) {
        onTabClick(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$3(View view) {
        Runnable runnable = this.onSettingsOpenRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.contentView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
        super.onMeasure(i, i2);
    }

    public void updateColors() {
        EmojiTabButton emojiTabButton = this.recentTab;
        if (emojiTabButton != null) {
            emojiTabButton.updateColor();
        }
    }

    public void select(int i) {
        int i2;
        int i3 = this.selected;
        int i4 = 0;
        int i5 = 0;
        while (i4 < this.contentView.getChildCount()) {
            View childAt = this.contentView.getChildAt(i4);
            if (childAt instanceof EmojiTabsView) {
                EmojiTabsView emojiTabsView = (EmojiTabsView) childAt;
                int i6 = i5;
                int i7 = 0;
                while (i7 < emojiTabsView.contentView.getChildCount()) {
                    View childAt2 = emojiTabsView.contentView.getChildAt(i7);
                    if (childAt2 instanceof EmojiTabButton) {
                        ((EmojiTabButton) childAt2).updateSelect(i == i6, true);
                    }
                    i7++;
                    i6++;
                }
                i2 = i6 - 1;
            } else {
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateSelect(i == i5, true);
                }
                i2 = i5;
            }
            if (i >= i5 && i <= i2) {
                this.selected = i4;
            }
            i4++;
            i5 = i2 + 1;
        }
        if (i3 != this.selected) {
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.selectAnimator = ofFloat;
            ofFloat.addUpdateListener(new EmojiTabsStrip$$ExternalSyntheticLambda0(this, this.selectT, (float) this.selected));
            this.selectAnimator.setDuration(350);
            this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.selectAnimator.start();
            EmojiTabsView emojiTabsView2 = this.emojiTabs;
            if (emojiTabsView2 != null) {
                emojiTabsView2.show(this.selected == 1);
            }
            View childAt3 = this.contentView.getChildAt(this.selected);
            if (this.selected >= 2) {
                scrollToVisible(childAt3.getLeft(), childAt3.getRight());
            } else {
                scrollTo(0);
            }
        }
        if (this.wasIndex != i) {
            EmojiTabsView emojiTabsView3 = this.emojiTabs;
            if (emojiTabsView3 != null && this.selected == 1 && i >= 1 && i <= emojiTabsView3.contentView.getChildCount() + 1) {
                int i8 = ((i - 1) * 36) - 6;
                this.emojiTabs.scrollToVisible(AndroidUtilities.dp((float) i8), AndroidUtilities.dp((float) (i8 + 30)));
            }
            this.wasIndex = i;
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$select$4(float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.selectAnimationT = floatValue;
        this.selectT = AndroidUtilities.lerp(f, f2, floatValue);
        this.contentView.invalidate();
    }

    /* access modifiers changed from: private */
    public int selectorColor() {
        return Theme.getColor("chat_emojiPanelIcon", this.resourcesProvider) & NUM;
    }

    private class EmojiTabButton extends ViewGroup {
        /* access modifiers changed from: private */
        public boolean forceSelector;
        public Integer id;
        /* access modifiers changed from: private */
        public ImageView imageView;
        private ValueAnimator lockAnimator;
        private float lockT;
        /* access modifiers changed from: private */
        public PremiumLockIconView lockView;
        private RLottieDrawable lottieDrawable;
        /* access modifiers changed from: private */
        public boolean round;
        private ValueAnimator selectAnimator;
        private float selectT;
        private boolean selected;
        private boolean wasVisible;

        public EmojiTabButton(Context context, int i, int i2, boolean z, boolean z2) {
            super(context);
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            if (Build.VERSION.SDK_INT >= 23) {
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), false, (int[]) null);
                this.lottieDrawable = rLottieDrawable;
                rLottieDrawable.setBounds(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(27.0f), AndroidUtilities.dp(27.0f));
                this.lottieDrawable.setMasterParent(this);
                this.lottieDrawable.setAllowDecodeSingleFrame(true);
                this.lottieDrawable.start();
            } else {
                ImageView imageView2 = new ImageView(context);
                this.imageView = imageView2;
                imageView2.setImageDrawable(context.getResources().getDrawable(i).mutate());
                addView(this.imageView);
            }
            setColor(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider));
        }

        public EmojiTabButton(Context context, int i, boolean z, boolean z2) {
            super(context);
            this.round = z;
            this.forceSelector = z2;
            if (z) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z2) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageDrawable(context.getResources().getDrawable(i).mutate());
            setColor(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider));
            addView(this.imageView);
        }

        public EmojiTabButton(Context context, Drawable drawable, boolean z, boolean z2, boolean z3) {
            super(context);
            this.round = z2;
            this.forceSelector = z3;
            if (z2) {
                setBackground(Theme.createCircleSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 0, 0));
            } else if (z3) {
                setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
            }
            AnonymousClass1 r4 = new ImageView(context, EmojiTabsStrip.this) {
                public void invalidate() {
                    super.invalidate();
                    EmojiTabButton.this.updateLockImageReceiver();
                }

                /* access modifiers changed from: protected */
                public void dispatchDraw(Canvas canvas) {
                    if (getDrawable() != null) {
                        getDrawable().setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        getDrawable().setAlpha(255);
                        getDrawable().draw(canvas);
                    }
                }
            };
            this.imageView = r4;
            r4.setImageDrawable(drawable);
            addView(this.imageView);
            PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_STICKERS_PREMIUM_LOCKED);
            this.lockView = premiumLockIconView;
            premiumLockIconView.setAlpha(0.0f);
            this.lockView.setScaleX(0.0f);
            this.lockView.setScaleY(0.0f);
            updateLockImageReceiver();
            addView(this.lockView);
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.draw(canvas);
            }
        }

        public boolean performClick() {
            return super.performClick();
        }

        public void updateVisibilityInbounds(boolean z, boolean z2) {
            RLottieDrawable rLottieDrawable;
            if (!this.wasVisible && z && (rLottieDrawable = this.lottieDrawable) != null && !rLottieDrawable.isRunning() && !z2) {
                this.lottieDrawable.setProgress(0.0f);
                this.lottieDrawable.start();
            }
            this.wasVisible = z;
        }

        public void setLock(Boolean bool) {
            if (this.lockView != null) {
                if (bool == null) {
                    updateLock(false);
                    return;
                }
                updateLock(true);
                if (bool.booleanValue()) {
                    this.lockView.setImageResource(NUM);
                    return;
                }
                Drawable mutate = getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
                this.lockView.setImageDrawable(mutate);
            }
        }

        private void updateLock(final boolean z) {
            ValueAnimator valueAnimator = this.lockAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float f = 1.0f;
            if (Math.abs(this.lockT - (z ? 1.0f : 0.0f)) >= 0.01f) {
                this.lockView.setVisibility(0);
                float[] fArr = new float[2];
                fArr[0] = this.lockT;
                if (!z) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.lockAnimator = ofFloat;
                ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda1(this));
                this.lockAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (!z) {
                            EmojiTabButton.this.lockView.setVisibility(8);
                        }
                    }
                });
                this.lockAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.lockAnimator.setDuration(200);
                this.lockAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateLock$0(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.lockT = floatValue;
            this.lockView.setScaleX(floatValue);
            this.lockView.setScaleY(this.lockT);
            this.lockView.setAlpha(this.lockT);
        }

        public void updateLockImageReceiver() {
            ImageReceiver imageReceiver;
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null && !premiumLockIconView.ready() && (getDrawable() instanceof AnimatedEmojiDrawable) && (imageReceiver = ((AnimatedEmojiDrawable) getDrawable()).getImageReceiver()) != null) {
                this.lockView.setImageReceiver(imageReceiver);
                this.lockView.invalidate();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), NUM));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                premiumLockIconView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), NUM));
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                int i5 = (i3 - i) / 2;
                int i6 = (i4 - i2) / 2;
                imageView2.layout(i5 - (imageView2.getMeasuredWidth() / 2), i6 - (this.imageView.getMeasuredHeight() / 2), i5 + (this.imageView.getMeasuredWidth() / 2), i6 + (this.imageView.getMeasuredHeight() / 2));
            }
            PremiumLockIconView premiumLockIconView = this.lockView;
            if (premiumLockIconView != null) {
                int i7 = i3 - i;
                int i8 = i4 - i2;
                premiumLockIconView.layout(i7 - premiumLockIconView.getMeasuredWidth(), i8 - this.lockView.getMeasuredHeight(), i7, i8);
            }
        }

        public Drawable getDrawable() {
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                return imageView2.getDrawable();
            }
            return null;
        }

        public void setDrawable(Drawable drawable) {
            ImageReceiver imageReceiver;
            if (!(this.lockView == null || !(drawable instanceof AnimatedEmojiDrawable) || (imageReceiver = ((AnimatedEmojiDrawable) drawable).getImageReceiver()) == null)) {
                this.lockView.setImageReceiver(imageReceiver);
            }
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.setImageDrawable(drawable);
            }
        }

        public void updateSelect(final boolean z, boolean z2) {
            ImageView imageView2 = this.imageView;
            if ((imageView2 == null || imageView2.getDrawable() != null) && this.selected != z) {
                this.selected = z;
                ValueAnimator valueAnimator = this.selectAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.selectAnimator = null;
                }
                float f = 1.0f;
                if (z2) {
                    float[] fArr = new float[2];
                    fArr[0] = this.selectT;
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.selectAnimator = ofFloat;
                    ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabButton$$ExternalSyntheticLambda0(this));
                    this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (EmojiTabButton.this.round) {
                                return;
                            }
                            if (!z && !EmojiTabButton.this.forceSelector) {
                                EmojiTabButton.this.setBackground((Drawable) null);
                            } else if (EmojiTabButton.this.getBackground() == null) {
                                EmojiTabButton emojiTabButton = EmojiTabButton.this;
                                emojiTabButton.setBackground(Theme.createRadSelectorDrawable(EmojiTabsStrip.this.selectorColor(), 8, 8));
                            }
                        }
                    });
                    this.selectAnimator.setDuration(350);
                    this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.selectAnimator.start();
                    return;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.selectT = f;
                updateColor();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSelect$1(ValueAnimator valueAnimator) {
            this.selectT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        public void updateColor() {
            Theme.setSelectorDrawableColor(getBackground(), EmojiTabsStrip.this.selectorColor(), false);
            setColor(ColorUtils.blendARGB(Theme.getColor("chat_emojiPanelIcon", EmojiTabsStrip.this.resourcesProvider), Theme.getColor("chat_emojiPanelIconSelected", EmojiTabsStrip.this.resourcesProvider), this.selectT));
        }

        private void setColor(int i) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
            ImageView imageView2 = this.imageView;
            if (imageView2 != null) {
                imageView2.setColorFilter(porterDuffColorFilter);
                this.imageView.invalidate();
            }
            RLottieDrawable rLottieDrawable = this.lottieDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setColorFilter(porterDuffColorFilter);
                invalidate();
            }
        }
    }

    private class EmojiTabsView extends ScrollableHorizontalScrollView {
        /* access modifiers changed from: private */
        public LinearLayout contentView;
        private ValueAnimator showAnimator;
        private float showT = 0.0f;
        private boolean shown = false;
        private boolean touching = false;

        public EmojiTabsView(Context context) {
            super(context);
            setSmoothScrollingEnabled(true);
            setHorizontalScrollBarEnabled(false);
            setVerticalScrollBarEnabled(false);
            if (Build.VERSION.SDK_INT >= 21) {
                setNestedScrollingEnabled(true);
            }
            AnonymousClass1 r1 = new LinearLayout(context, EmojiTabsStrip.this) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int paddingLeft = getPaddingLeft();
                    int i5 = (i4 - i2) / 2;
                    for (int i6 = 0; i6 < getChildCount(); i6++) {
                        View childAt = getChildAt(i6);
                        if (!(childAt == EmojiTabsStrip.this.settingsTab || childAt == null)) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(2.0f);
                        }
                    }
                }

                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(Math.max(View.MeasureSpec.getSize(i), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((EmojiTabsView.this.contentView.getChildCount() * 32) - 2)), NUM)), i2);
                }
            };
            this.contentView = r1;
            r1.setOrientation(0);
            addView(this.contentView, new FrameLayout.LayoutParams(-2, -1));
            for (int i = 0; i < EmojiTabsStrip.emojiTabsDrawableIds.length; i++) {
                this.contentView.addView(new EmojiTabButton(context, EmojiTabsStrip.emojiTabsDrawableIds[i], EmojiTabsStrip.emojiTabsAnimatedDrawableIds[i], true, false, EmojiTabsStrip.this) {
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        EmojiTabsView.this.intercept(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }
                });
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.lerp(AndroidUtilities.dp(30.0f), maxWidth(), this.showT), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), NUM));
        }

        public int maxWidth() {
            return AndroidUtilities.dp((Math.min(5.7f, (float) this.contentView.getChildCount()) * 32.0f) - 2.0f);
        }

        /* access modifiers changed from: protected */
        public void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            if ((Math.abs(i2 - i4) < 2 || i2 >= getMeasuredHeight() || i2 == 0) && !this.touching) {
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(false);
            }
            updateButtonsVisibility();
        }

        private void updateButtonsVisibility() {
            ValueAnimator valueAnimator;
            int childCount = this.contentView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.contentView.getChildAt(i);
                if (childAt instanceof EmojiTabButton) {
                    EmojiTabButton emojiTabButton = (EmojiTabButton) childAt;
                    boolean z = true;
                    boolean z2 = childAt.getRight() - getScrollX() > 0 && childAt.getLeft() - getScrollX() < getMeasuredWidth();
                    if (!this.scrollingAnimation || ((valueAnimator = this.showAnimator) != null && valueAnimator.isRunning())) {
                        z = false;
                    }
                    emojiTabButton.updateVisibilityInbounds(z2, z);
                }
            }
        }

        /* access modifiers changed from: private */
        public void intercept(MotionEvent motionEvent) {
            if (this.shown && !this.scrollingAnimation) {
                int action = motionEvent.getAction();
                if (action != 0) {
                    if (action == 1) {
                        this.touching = false;
                        return;
                    } else if (action != 2) {
                        return;
                    }
                }
                this.touching = true;
                if (!this.scrollingAnimation) {
                    resetScrollTo();
                }
                EmojiTabsStrip.this.requestDisallowInterceptTouchEvent(true);
            }
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            intercept(motionEvent);
            return super.onTouchEvent(motionEvent);
        }

        public void show(boolean z) {
            if (z != this.shown) {
                this.shown = z;
                if (!z) {
                    scrollTo(0);
                }
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float[] fArr = new float[2];
                fArr[0] = this.showT;
                fArr[1] = z ? 1.0f : 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                this.showAnimator = ofFloat;
                ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabsView$$ExternalSyntheticLambda0(this));
                this.showAnimator.setDuration(475);
                this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.showAnimator.start();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$show$0(ValueAnimator valueAnimator) {
            this.showT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
            requestLayout();
            updateButtonsVisibility();
            EmojiTabsStrip.this.contentView.invalidate();
        }
    }
}
