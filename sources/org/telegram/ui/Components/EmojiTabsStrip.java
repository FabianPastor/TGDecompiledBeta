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
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private ArrayList<EmojiTabButton> emojipackTabs;
    boolean first = true;
    private boolean includeAnimated;
    private Runnable onSettingsOpenRunnable;
    private EmojiView parent;
    private int recentDrawableId = NUM;
    private boolean recentFirstChange = true;
    /* access modifiers changed from: private */
    public boolean recentIsShown = true;
    /* access modifiers changed from: private */
    public EmojiTabButton recentTab;
    /* access modifiers changed from: private */
    public HashMap<View, Rect> removingViews = new HashMap<>();
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
        AnonymousClass1 r11 = new LinearLayout(context) {
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
                    int paddingLeft = getPaddingLeft() - (!EmojiTabsStrip.this.recentIsShown ? AndroidUtilities.dp(33.0f) : 0);
                    for (int i7 = 0; i7 < getChildCount(); i7++) {
                        View childAt = getChildAt(i7);
                        if (!(childAt == EmojiTabsStrip.this.settingsTab || EmojiTabsStrip.this.removingViews.containsKey(childAt) || childAt == null)) {
                            childAt.layout(paddingLeft, i5 - (childAt.getMeasuredHeight() / 2), childAt.getMeasuredWidth() + paddingLeft, (childAt.getMeasuredHeight() / 2) + i5);
                            boolean z2 = childAt instanceof EmojiTabButton;
                            Integer valueOf = z2 ? ((EmojiTabButton) childAt).id : childAt instanceof EmojiTabsView ? Integer.valueOf(((EmojiTabsView) childAt).id) : null;
                            if (z2) {
                                EmojiTabButton emojiTabButton = (EmojiTabButton) childAt;
                                if (emojiTabButton.newly) {
                                    emojiTabButton.newly = false;
                                    childAt.setScaleX(0.0f);
                                    childAt.setScaleY(0.0f);
                                    childAt.setAlpha(0.0f);
                                    childAt.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(200).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                                }
                            }
                            if (valueOf != null) {
                                if (!(this.lastX.get(valueOf) == null || this.lastX.get(valueOf).intValue() == paddingLeft)) {
                                    childAt.setTranslationX((float) (this.lastX.get(valueOf).intValue() - paddingLeft));
                                    childAt.animate().translationX(0.0f).setDuration(250).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                                }
                                this.lastX.put(valueOf, Integer.valueOf(paddingLeft));
                            }
                            paddingLeft += childAt.getMeasuredWidth() + AndroidUtilities.dp(3.0f);
                        }
                    }
                    if (EmojiTabsStrip.this.settingsTab != null) {
                        if (!EmojiTabsStrip.this.recentIsShown) {
                            i6 = AndroidUtilities.dp(33.0f);
                        }
                        int i8 = paddingLeft + i6;
                        Integer num = EmojiTabsStrip.this.settingsTab.id;
                        if (EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + i8 + getPaddingRight() <= EmojiTabsStrip.this.getMeasuredWidth()) {
                            EmojiTabButton access$100 = EmojiTabsStrip.this.settingsTab;
                            int i9 = i3 - i;
                            int paddingRight = (i9 - getPaddingRight()) - EmojiTabsStrip.this.settingsTab.getMeasuredWidth();
                            access$100.layout(paddingRight, i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), i9 - getPaddingRight(), i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                            i8 = paddingRight;
                        } else {
                            EmojiTabsStrip.this.settingsTab.layout(i8, i5 - (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2), EmojiTabsStrip.this.settingsTab.getMeasuredWidth() + i8, i5 + (EmojiTabsStrip.this.settingsTab.getMeasuredHeight() / 2));
                        }
                        if (num != null) {
                            if (!(this.lastX.get(num) == null || this.lastX.get(num).intValue() == i8)) {
                                EmojiTabsStrip.this.settingsTab.setTranslationX((float) (this.lastX.get(num).intValue() - i8));
                                EmojiTabsStrip.this.settingsTab.animate().translationX(0.0f).setDuration(350).start();
                            }
                            this.lastX.put(num, Integer.valueOf(i8));
                            return;
                        }
                        return;
                    }
                    return;
                }
                int childCount = getChildCount() - (EmojiTabsStrip.this.recentIsShown ^ true ? 1 : 0);
                int paddingLeft2 = (int) (((float) ((((i3 - i) - getPaddingLeft()) - getPaddingRight()) - (AndroidUtilities.dp(30.0f) * childCount))) / ((float) Math.max(1, childCount - 1)));
                int paddingLeft3 = getPaddingLeft();
                while (i6 < childCount) {
                    View childAt2 = getChildAt((EmojiTabsStrip.this.recentIsShown ^ true ? 1 : 0) + i6);
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
                int paddingLeft = (getPaddingLeft() + getPaddingRight()) - ((int) (EmojiTabsStrip.this.recentIsShown ? 0.0f : EmojiTabsStrip.this.recentTab.getAlpha() * ((float) AndroidUtilities.dp(33.0f))));
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
                for (Map.Entry entry : EmojiTabsStrip.this.removingViews.entrySet()) {
                    View view = (View) entry.getKey();
                    Rect rect2 = (Rect) entry.getValue();
                    canvas.save();
                    canvas.translate((float) rect2.left, (float) rect2.top);
                    canvas.scale(view.getScaleX(), view.getScaleY(), ((float) rect2.width()) / 2.0f, ((float) rect2.height()) / 2.0f);
                    view.draw(canvas);
                    canvas.restore();
                }
                int floor = (int) Math.floor((double) EmojiTabsStrip.this.selectT);
                int ceil = (int) Math.ceil((double) EmojiTabsStrip.this.selectT);
                getChildBounds(floor, this.from);
                getChildBounds(ceil, this.to);
                AndroidUtilities.lerp(this.from, this.to, EmojiTabsStrip.this.selectT - ((float) floor), this.rect);
                float f = 0.0f;
                if (EmojiTabsStrip.this.emojiTabs != null) {
                    f = MathUtils.clamp(1.0f - Math.abs(EmojiTabsStrip.this.selectT - 1.0f), 0.0f, 1.0f);
                }
                float access$600 = EmojiTabsStrip.this.selectAnimationT * 4.0f * (1.0f - EmojiTabsStrip.this.selectAnimationT);
                float width = (this.rect.width() / 2.0f) * ((0.3f * access$600) + 1.0f);
                float height = (this.rect.height() / 2.0f) * (1.0f - (access$600 * 0.05f));
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
        this.contentView = r11;
        r11.setClipToPadding(false);
        this.contentView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(3.0f), 0);
        this.contentView.setOrientation(0);
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        addView(this.contentView);
        LinearLayout linearLayout = this.contentView;
        EmojiTabButton emojiTabButton = new EmojiTabButton(context, this.recentDrawableId, false, false);
        this.recentTab = emojiTabButton;
        linearLayout.addView(emojiTabButton);
        this.recentTab.id = -NUM;
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
            LinearLayout linearLayout2 = this.contentView;
            EmojiTabsView emojiTabsView = new EmojiTabsView(context);
            this.emojiTabs = emojiTabsView;
            linearLayout2.addView(emojiTabsView);
            this.emojiTabs.id = 3552126;
            if (runnable != null) {
                LinearLayout linearLayout3 = this.contentView;
                EmojiTabButton emojiTabButton2 = new EmojiTabButton(context, this.settingsDrawableId, false, true);
                this.settingsTab = emojiTabButton2;
                linearLayout3.addView(emojiTabButton2);
                this.settingsTab.id = NUM;
                this.settingsTab.setAlpha(0.0f);
            }
            updateClickListeners();
            updateEmojiPacks();
        }
    }

    public void showRecent(boolean z) {
        if (this.recentIsShown != z) {
            this.recentIsShown = z;
            float f = 1.0f;
            if (this.recentFirstChange) {
                EmojiTabButton emojiTabButton = this.recentTab;
                if (!z) {
                    f = 0.0f;
                }
                emojiTabButton.setAlpha(f);
            } else {
                ViewPropertyAnimator animate = this.recentTab.animate();
                if (!z) {
                    f = 0.0f;
                }
                animate.alpha(f).setDuration(200).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            if ((!z && this.selected == 0) || (z && this.selected == 1)) {
                select(0, !this.recentFirstChange);
            }
            this.contentView.requestLayout();
            this.recentFirstChange = false;
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

    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0184, code lost:
        if (r0.getDocumentId() != r2.id) goto L_0x0189;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateEmojiPacks() {
        /*
            r21 = this;
            r7 = r21
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
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            int r0 = r0.size()
            if (r0 != 0) goto L_0x0048
            int r0 = r9.size()
            if (r0 <= 0) goto L_0x0048
            int r0 = r7.appearCount
            int r1 = r9.size()
            if (r0 == r1) goto L_0x0048
            boolean r0 = r7.wasDrawn
        L_0x0048:
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r10 = 0
            if (r0 == 0) goto L_0x005c
            int r0 = r7.appearCount
            int r1 = r9.size()
            if (r0 == r1) goto L_0x005c
            android.animation.ValueAnimator r0 = r7.appearAnimation
            r0.cancel()
            r7.appearAnimation = r10
        L_0x005c:
            int r0 = r9.size()
            r7.appearCount = r0
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r11 = r0.isPremium()
            r0 = 0
        L_0x006d:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            int r1 = r1.size()
            r12 = 200(0xc8, double:9.9E-322)
            r14 = 2
            r15 = 1
            if (r0 >= r1) goto L_0x0114
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            java.lang.Object r1 = r1.get(r0)
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r1 = (org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton) r1
            if (r1 == 0) goto L_0x00b8
            java.lang.Integer r2 = r1.id
            if (r2 == 0) goto L_0x00b8
            r2 = 0
        L_0x0088:
            int r3 = r9.size()
            if (r2 >= r3) goto L_0x00b8
            java.lang.Object r3 = r9.get(r2)
            org.telegram.ui.Components.EmojiView$EmojiPack r3 = (org.telegram.ui.Components.EmojiView.EmojiPack) r3
            java.lang.Object[] r4 = new java.lang.Object[r14]
            org.telegram.tgnet.TLRPC$StickerSet r5 = r3.set
            long r5 = r5.id
            java.lang.Long r5 = java.lang.Long.valueOf(r5)
            r4[r8] = r5
            boolean r5 = r3.featured
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)
            r4[r15] = r5
            int r4 = java.util.Arrays.hashCode(r4)
            java.lang.Integer r5 = r1.id
            int r5 = r5.intValue()
            if (r4 != r5) goto L_0x00b5
            goto L_0x00b9
        L_0x00b5:
            int r2 = r2 + 1
            goto L_0x0088
        L_0x00b8:
            r3 = r10
        L_0x00b9:
            if (r3 != 0) goto L_0x010c
            if (r1 == 0) goto L_0x010c
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            int r3 = r1.getLeft()
            int r4 = r1.getTop()
            int r5 = r1.getRight()
            int r6 = r1.getBottom()
            r2.set(r3, r4, r5, r6)
            java.util.HashMap<android.view.View, android.graphics.Rect> r3 = r7.removingViews
            r3.put(r1, r2)
            float[] r2 = new float[r14]
            float r3 = r1.getAlpha()
            r2[r8] = r3
            r3 = 0
            r2[r15] = r3
            android.animation.ValueAnimator r2 = android.animation.ValueAnimator.ofFloat(r2)
            org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1 r3 = new org.telegram.ui.Components.EmojiTabsStrip$$ExternalSyntheticLambda1
            r3.<init>(r7, r1)
            r2.addUpdateListener(r3)
            org.telegram.ui.Components.EmojiTabsStrip$2 r3 = new org.telegram.ui.Components.EmojiTabsStrip$2
            r3.<init>(r1)
            r2.addListener(r3)
            r2.setDuration(r12)
            org.telegram.ui.Components.CubicBezierInterpolator r3 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            r2.setInterpolator(r3)
            r2.start()
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r2 = r7.emojipackTabs
            int r3 = r0 + -1
            r2.remove(r0)
            r0 = r3
        L_0x010c:
            android.widget.LinearLayout r2 = r7.contentView
            r2.removeView(r1)
            int r0 = r0 + r15
            goto L_0x006d
        L_0x0114:
            r6 = 0
        L_0x0115:
            int r0 = r9.size()
            if (r6 >= r0) goto L_0x0233
            java.lang.Object r0 = r9.get(r6)
            r5 = r0
            org.telegram.ui.Components.EmojiView$EmojiPack r5 = (org.telegram.ui.Components.EmojiView.EmojiPack) r5
            java.lang.Object[] r0 = new java.lang.Object[r14]
            org.telegram.tgnet.TLRPC$StickerSet r1 = r5.set
            long r1 = r1.id
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            r0[r8] = r1
            boolean r1 = r5.featured
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)
            r0[r15] = r1
            int r4 = java.util.Arrays.hashCode(r0)
            r0 = 0
        L_0x013b:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x015b
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r1 = r7.emojipackTabs
            java.lang.Object r1 = r1.get(r0)
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r1 = (org.telegram.ui.Components.EmojiTabsStrip.EmojiTabButton) r1
            if (r1 == 0) goto L_0x0158
            java.lang.Integer r2 = r1.id
            if (r2 == 0) goto L_0x0158
            int r2 = r2.intValue()
            if (r2 != r4) goto L_0x0158
            goto L_0x015c
        L_0x0158:
            int r0 = r0 + 1
            goto L_0x013b
        L_0x015b:
            r1 = r10
        L_0x015c:
            org.telegram.tgnet.TLRPC$StickerSet r0 = r5.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r2 = r5.documents
            boolean r16 = r7.isFreeEmojiPack(r0, r2)
            if (r1 != 0) goto L_0x0168
            r0 = r10
            goto L_0x016e
        L_0x0168:
            android.graphics.drawable.Drawable r0 = r1.getDrawable()
            org.telegram.ui.Components.AnimatedEmojiDrawable r0 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r0
        L_0x016e:
            org.telegram.tgnet.TLRPC$StickerSet r2 = r5.set
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r3 = r5.documents
            org.telegram.tgnet.TLRPC$Document r2 = r7.getThumbDocument(r2, r3)
            if (r2 == 0) goto L_0x0191
            if (r0 == 0) goto L_0x0187
            long r17 = r0.getDocumentId()
            r19 = r9
            long r8 = r2.id
            int r3 = (r17 > r8 ? 1 : (r17 == r8 ? 0 : -1))
            if (r3 == 0) goto L_0x0193
            goto L_0x0189
        L_0x0187:
            r19 = r9
        L_0x0189:
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            r3 = 3
            org.telegram.ui.Components.AnimatedEmojiDrawable r0 = org.telegram.ui.Components.AnimatedEmojiDrawable.make((int) r0, (int) r3, (org.telegram.tgnet.TLRPC$Document) r2)
            goto L_0x0193
        L_0x0191:
            r19 = r9
        L_0x0193:
            r8 = r0
            if (r1 != 0) goto L_0x01c8
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r9 = new org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton
            android.content.Context r2 = r21.getContext()
            r17 = 0
            r18 = 0
            r0 = r9
            r1 = r21
            r3 = r8
            r20 = r4
            r4 = r16
            r14 = r5
            r5 = r17
            r17 = r6
            r6 = r18
            r0.<init>((android.content.Context) r2, (android.graphics.drawable.Drawable) r3, (boolean) r4, (boolean) r5, (boolean) r6)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r20)
            r9.id = r0
            if (r8 == 0) goto L_0x01c1
            android.widget.ImageView r0 = r9.imageView
            r8.addView((android.view.View) r0)
        L_0x01c1:
            java.util.ArrayList<org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton> r0 = r7.emojipackTabs
            r0.add(r9)
            r1 = r9
            goto L_0x01ec
        L_0x01c8:
            r14 = r5
            r17 = r6
            android.graphics.drawable.Drawable r0 = r1.getDrawable()
            boolean r0 = r0 instanceof org.telegram.ui.Components.AnimatedEmojiDrawable
            if (r0 == 0) goto L_0x01e0
            android.graphics.drawable.Drawable r0 = r1.getDrawable()
            org.telegram.ui.Components.AnimatedEmojiDrawable r0 = (org.telegram.ui.Components.AnimatedEmojiDrawable) r0
            android.widget.ImageView r2 = r1.imageView
            r0.removeView((android.view.View) r2)
        L_0x01e0:
            r1.setDrawable(r8)
            if (r8 == 0) goto L_0x01ec
            android.widget.ImageView r0 = r1.imageView
            r8.addView((android.view.View) r0)
        L_0x01ec:
            if (r11 != 0) goto L_0x01f6
            if (r16 != 0) goto L_0x01f6
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            r1.setLock(r0)
            goto L_0x0215
        L_0x01f6:
            boolean r0 = r14.installed
            if (r0 != 0) goto L_0x0212
            org.telegram.ui.Components.EmojiView r0 = r7.parent
            java.util.ArrayList<java.lang.Long> r0 = r0.installedEmojiSets
            org.telegram.tgnet.TLRPC$StickerSet r2 = r14.set
            long r2 = r2.id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            boolean r0 = r0.contains(r2)
            if (r0 != 0) goto L_0x0212
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
            r1.setLock(r0)
            goto L_0x0215
        L_0x0212:
            r1.setLock(r10)
        L_0x0215:
            android.view.ViewParent r0 = r1.getParent()
            boolean r0 = r0 instanceof android.view.ViewGroup
            if (r0 == 0) goto L_0x0226
            android.view.ViewParent r0 = r1.getParent()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r0.removeView(r1)
        L_0x0226:
            android.widget.LinearLayout r0 = r7.contentView
            r0.addView(r1)
            int r6 = r17 + 1
            r9 = r19
            r8 = 0
            r14 = 2
            goto L_0x0115
        L_0x0233:
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r7.settingsTab
            if (r0 == 0) goto L_0x025d
            r0.bringToFront()
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r7.settingsTab
            float r0 = r0.getAlpha()
            r1 = 1065353216(0x3var_, float:1.0)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 >= 0) goto L_0x025d
            org.telegram.ui.Components.EmojiTabsStrip$EmojiTabButton r0 = r7.settingsTab
            android.view.ViewPropertyAnimator r0 = r0.animate()
            android.view.ViewPropertyAnimator r0 = r0.alpha(r1)
            android.view.ViewPropertyAnimator r0 = r0.setDuration(r12)
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r0 = r0.setInterpolator(r1)
            r0.start()
        L_0x025d:
            r21.updateClickListeners()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiTabsStrip.updateEmojiPacks():void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateEmojiPacks$0(EmojiTabButton emojiTabButton, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        emojiTabButton.setAlpha(floatValue);
        emojiTabButton.setScaleX(floatValue);
        emojiTabButton.setScaleY(floatValue);
        this.contentView.invalidate();
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
                    emojiTabsView.contentView.getChildAt(i3).setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda4(this, i2));
                    i3++;
                    i2++;
                }
                i2--;
            } else if (childAt != null) {
                childAt.setOnClickListener(new EmojiTabsStrip$$ExternalSyntheticLambda3(this, i2));
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
    public /* synthetic */ void lambda$updateClickListeners$2(int i, View view) {
        onTabClick(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$3(int i, View view) {
        onTabClick(i);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateClickListeners$4(View view) {
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
        select(i, true);
    }

    public void select(int i, boolean z) {
        int i2;
        if (!this.recentIsShown) {
            i = Math.max(1, i);
        }
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
                        ((EmojiTabButton) childAt2).updateSelect(i == i6, z);
                    }
                    i7++;
                    i6++;
                }
                i2 = i6 - 1;
            } else {
                if (childAt instanceof EmojiTabButton) {
                    ((EmojiTabButton) childAt).updateSelect(i == i5, z);
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
            float f = this.selectT;
            float f2 = (float) this.selected;
            if (z) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.selectAnimator = ofFloat;
                ofFloat.addUpdateListener(new EmojiTabsStrip$$ExternalSyntheticLambda0(this, f, f2));
                this.selectAnimator.setDuration(350);
                this.selectAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.selectAnimator.start();
            } else {
                this.selectAnimationT = 1.0f;
                this.selectT = AndroidUtilities.lerp(f, f2, 1.0f);
                this.contentView.invalidate();
            }
            EmojiTabsView emojiTabsView2 = this.emojiTabs;
            if (emojiTabsView2 != null) {
                emojiTabsView2.show(this.selected == 1, z);
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
    public /* synthetic */ void lambda$select$5(float f, float f2, ValueAnimator valueAnimator) {
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
        public boolean newly;
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
            this.newly = true;
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
        public int id;
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

        public void show(boolean z, boolean z2) {
            if (z != this.shown) {
                this.shown = z;
                if (!z) {
                    scrollTo(0);
                }
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float f = 1.0f;
                if (z2) {
                    float[] fArr = new float[2];
                    fArr[0] = this.showT;
                    if (!z) {
                        f = 0.0f;
                    }
                    fArr[1] = f;
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
                    this.showAnimator = ofFloat;
                    ofFloat.addUpdateListener(new EmojiTabsStrip$EmojiTabsView$$ExternalSyntheticLambda0(this));
                    this.showAnimator.setDuration(475);
                    this.showAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.showAnimator.start();
                    return;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.showT = f;
                invalidate();
                requestLayout();
                updateButtonsVisibility();
                EmojiTabsStrip.this.contentView.invalidate();
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
