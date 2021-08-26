package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    boolean animateToExpanded;
    int currentDragPosition;
    SparseArray<StickerTabView> currentPlayingImages;
    SparseArray<StickerTabView> currentPlayingImagesTmp;
    private int currentPosition;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    float dragDx;
    float draggindViewDxOnScreen;
    float draggindViewXOnScreen;
    View draggingView;
    float draggingViewOutProgress;
    /* access modifiers changed from: private */
    public float expandOffset;
    float expandProgress;
    ValueAnimator expandStickerAnimator;
    boolean expanded;
    private SparseArray<View> futureTabsPositions = new SparseArray<>();
    private int indicatorColor = -10066330;
    private GradientDrawable indicatorDrawable = new GradientDrawable();
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX;
    Runnable longClickRunnable;
    boolean longClickRunning;
    private float positionAnimationProgress;
    float pressedX;
    float pressedY;
    private HashMap<String, View> prevTypes = new HashMap<>();
    private Paint rectPaint;
    /* access modifiers changed from: private */
    public int scrollByOnNextMeasure;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    boolean scrollRight;
    Runnable scrollRunnable;
    private boolean shouldExpand;
    private float startAnimationPosition;
    int startDragFromPosition;
    float startDragFromX;
    /* access modifiers changed from: private */
    public float stickerTabExpandedWidth;
    /* access modifiers changed from: private */
    public float stickerTabWidth;
    private int tabCount;
    private HashMap<String, View> tabTypes = new HashMap<>();
    /* access modifiers changed from: private */
    public LinearLayout tabsContainer;
    private float touchSlop;
    private Type type = Type.LINE;
    private int underlineColor = NUM;
    private int underlineHeight = AndroidUtilities.dp(2.0f);

    public interface ScrollSlidingTabStripDelegate {
        void onPageSelected(int i);
    }

    public enum Type {
        LINE,
        TAB
    }

    /* access modifiers changed from: private */
    public boolean canSwap(int i) {
        return i < 0 || i >= 3;
    }

    /* access modifiers changed from: protected */
    public void invalidateOverlays() {
    }

    /* access modifiers changed from: protected */
    public void stickerSetPositionChanged(int i, int i2) {
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
    }

    public ScrollSlidingTabStrip(Context context) {
        super(context);
        AndroidUtilities.dp(12.0f);
        AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        this.currentPlayingImages = new SparseArray<>();
        this.currentPlayingImagesTmp = new SparseArray<>();
        this.expanded = false;
        this.stickerTabExpandedWidth = (float) AndroidUtilities.dp(86.0f);
        this.stickerTabWidth = (float) AndroidUtilities.dp(52.0f);
        this.scrollByOnNextMeasure = -1;
        this.longClickRunnable = new Runnable() {
            public void run() {
                ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip.longClickRunning = false;
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip.startDragFromX = ((float) scrollSlidingTabStrip.getScrollX()) + scrollSlidingTabStrip2.pressedX;
                scrollSlidingTabStrip2.dragDx = 0.0f;
                int ceil = ((int) Math.ceil((double) (scrollSlidingTabStrip2.startDragFromX / ((float) scrollSlidingTabStrip2.getTabSize())))) - 1;
                ScrollSlidingTabStrip scrollSlidingTabStrip3 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip3.currentDragPosition = ceil;
                scrollSlidingTabStrip3.startDragFromPosition = ceil;
                if (scrollSlidingTabStrip3.canSwap(ceil) && ceil >= 0 && ceil < ScrollSlidingTabStrip.this.tabsContainer.getChildCount()) {
                    ScrollSlidingTabStrip.this.performHapticFeedback(0);
                    ScrollSlidingTabStrip scrollSlidingTabStrip4 = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip4.draggindViewDxOnScreen = 0.0f;
                    scrollSlidingTabStrip4.draggingViewOutProgress = 0.0f;
                    scrollSlidingTabStrip4.draggingView = scrollSlidingTabStrip4.tabsContainer.getChildAt(ceil);
                    ScrollSlidingTabStrip scrollSlidingTabStrip5 = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip5.draggindViewXOnScreen = scrollSlidingTabStrip5.draggingView.getX() - ((float) ScrollSlidingTabStrip.this.getScrollX());
                    ScrollSlidingTabStrip.this.draggingView.invalidate();
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.invalidateOverlays();
                }
            }
        };
        this.scrollRunnable = new Runnable() {
            public void run() {
                int dp = AndroidUtilities.dp(4.0f);
                ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip.scrollBy(dp * (scrollSlidingTabStrip.scrollRight ? 1 : -1), 0);
                AndroidUtilities.runOnUIThread(ScrollSlidingTabStrip.this.scrollRunnable, 16);
            }
        };
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        AnonymousClass1 r4 = new LinearLayout(context) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (view instanceof StickerTabView) {
                    ((StickerTabView) view).updateExpandProgress(ScrollSlidingTabStrip.this.expandProgress);
                }
                if (view == ScrollSlidingTabStrip.this.draggingView) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        this.tabsContainer = r4;
        r4.setOrientation(0);
        this.tabsContainer.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
        addView(this.tabsContainer);
        Paint paint = new Paint();
        this.rectPaint = paint;
        paint.setAntiAlias(true);
        this.rectPaint.setStyle(Paint.Style.FILL);
        this.defaultTabLayoutParams = new LinearLayout.LayoutParams(AndroidUtilities.dp(52.0f), -1);
        this.defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, -1, 1.0f);
    }

    public void setDelegate(ScrollSlidingTabStripDelegate scrollSlidingTabStripDelegate) {
        this.delegate = scrollSlidingTabStripDelegate;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type2) {
        if (type2 != null && this.type != type2) {
            this.type = type2;
            int i = AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[type2.ordinal()];
            if (i == 1) {
                this.indicatorDrawable.setCornerRadius(0.0f);
            } else if (i == 2) {
                float dpf2 = AndroidUtilities.dpf2(3.0f);
                this.indicatorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$7  reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */
        /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        static {
            /*
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type[] r0 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type = r0
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.LINE     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type     // Catch:{ NoSuchFieldError -> 0x001d }
                org.telegram.ui.Components.ScrollSlidingTabStrip$Type r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.Type.TAB     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass7.<clinit>():void");
        }
    }

    public void removeTabs() {
        this.tabsContainer.removeAllViews();
        this.tabTypes.clear();
        this.prevTypes.clear();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        this.currentPosition = 0;
        this.animateFromPosition = false;
    }

    public void beginUpdate(boolean z) {
        this.prevTypes = this.tabTypes;
        this.tabTypes = new HashMap<>();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        if (z && Build.VERSION.SDK_INT >= 19) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(250);
            autoTransition.setOrdering(0);
            autoTransition.addTransition(new Transition() {
                public void captureEndValues(TransitionValues transitionValues) {
                }

                public void captureStartValues(TransitionValues transitionValues) {
                }

                public Animator createAnimator(ViewGroup viewGroup, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ScrollSlidingTabStrip$2$$ExternalSyntheticLambda0(this));
                    return ofFloat;
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$createAnimator$0(ValueAnimator valueAnimator) {
                    ScrollSlidingTabStrip.this.invalidate();
                }
            });
            TransitionManager.beginDelayedTransition(this.tabsContainer, autoTransition);
        }
    }

    public void commitUpdate() {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            for (Map.Entry<String, View> value : hashMap.entrySet()) {
                this.tabsContainer.removeView((View) value.getValue());
            }
            this.prevTypes.clear();
        }
        int size = this.futureTabsPositions.size();
        for (int i = 0; i < size; i++) {
            int keyAt = this.futureTabsPositions.keyAt(i);
            View valueAt = this.futureTabsPositions.valueAt(i);
            if (this.tabsContainer.indexOfChild(valueAt) != keyAt) {
                this.tabsContainer.removeView(valueAt);
                this.tabsContainer.addView(valueAt, keyAt);
            }
        }
        this.futureTabsPositions.clear();
    }

    public void selectTab(int i) {
        if (i >= 0 && i < this.tabCount) {
            this.tabsContainer.getChildAt(i).performClick();
        }
    }

    private void checkViewIndex(String str, View view, int i) {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            hashMap.remove(str);
        }
        this.futureTabsPositions.put(i, view);
    }

    public ImageView addIconTab(int i, Drawable drawable) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        ImageView imageView = (ImageView) this.prevTypes.get(str);
        boolean z = true;
        if (imageView != null) {
            checkViewIndex(str, imageView, i2);
        } else {
            imageView = new ImageView(getContext());
            imageView.setFocusable(true);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda4(this));
            this.tabsContainer.addView(imageView, i2);
        }
        imageView.setTag(NUM, Integer.valueOf(i2));
        if (i2 != this.currentPosition) {
            z = false;
        }
        imageView.setSelected(z);
        this.tabTypes.put(str, imageView);
        return imageView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addIconTab$1(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public StickerTabView addStickerIconTab(int i, Drawable drawable) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        boolean z = true;
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i2);
        } else {
            stickerTabView = new StickerTabView(getContext(), true);
            stickerTabView.iconView.setImageDrawable(drawable);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda3(this));
            this.tabsContainer.addView(stickerTabView, i2);
        }
        stickerTabView.setTag(NUM, Integer.valueOf(i2));
        if (i2 != this.currentPosition) {
            z = false;
        }
        stickerTabView.setSelected(z);
        this.tabTypes.put(str, stickerTabView);
        return stickerTabView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStickerIconTab$2(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public void addStickerTab(TLRPC$Chat tLRPC$Chat) {
        String str = "chat" + tLRPC$Chat.id;
        int i = this.tabCount;
        this.tabCount = i + 1;
        FrameLayout frameLayout = (FrameLayout) this.prevTypes.get(str);
        boolean z = true;
        if (frameLayout != null) {
            checkViewIndex(str, frameLayout, i);
        } else {
            frameLayout = new FrameLayout(getContext());
            frameLayout.setFocusable(true);
            frameLayout.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda5(this));
            this.tabsContainer.addView(frameLayout, i);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(tLRPC$Chat);
            BackupImageView backupImageView = new BackupImageView(getContext());
            backupImageView.setLayerNum(1);
            backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
            backupImageView.setForUserOrChat(tLRPC$Chat, avatarDrawable);
            backupImageView.setAspectFit(true);
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(30, 30, 17));
        }
        frameLayout.setTag(NUM, Integer.valueOf(i));
        if (i != this.currentPosition) {
            z = false;
        }
        frameLayout.setSelected(z);
        this.tabTypes.put(str, frameLayout);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStickerTab$3(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public View addStickerTab(TLObject tLObject, SvgHelper.SvgDrawable svgDrawable, TLRPC$Document tLRPC$Document, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        StringBuilder sb = new StringBuilder();
        sb.append("set");
        sb.append(tLRPC$TL_messages_stickerSet == null ? tLRPC$Document.id : tLRPC$TL_messages_stickerSet.set.id);
        String sb2 = sb.toString();
        int i = this.tabCount;
        this.tabCount = i + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(sb2);
        boolean z = false;
        if (stickerTabView != null) {
            checkViewIndex(sb2, stickerTabView, i);
        } else {
            stickerTabView = new StickerTabView(getContext(), false);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda2(this));
            this.tabsContainer.addView(stickerTabView, i);
        }
        stickerTabView.setTag(tLObject);
        stickerTabView.setTag(NUM, Integer.valueOf(i));
        stickerTabView.setTag(NUM, tLRPC$TL_messages_stickerSet);
        stickerTabView.setTag(NUM, tLRPC$Document);
        stickerTabView.setTag(NUM, svgDrawable);
        if (i == this.currentPosition) {
            z = true;
        }
        stickerTabView.setSelected(z);
        this.tabTypes.put(sb2, stickerTabView);
        return stickerTabView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStickerTab$4(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public void expandStickers(final float f, final boolean z) {
        if (this.expanded != z) {
            this.expanded = z;
            if (!z) {
                fling(0);
            }
            ValueAnimator valueAnimator = this.expandStickerAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.expandStickerAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.expandProgress;
            fArr[1] = z ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.expandStickerAnimator = ofFloat;
            ofFloat.addUpdateListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda1(this, z, f));
            this.expandStickerAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                    scrollSlidingTabStrip.expandStickerAnimator = null;
                    scrollSlidingTabStrip.expandProgress = z ? 1.0f : 0.0f;
                    for (int i = 0; i < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i++) {
                        ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i).invalidate();
                    }
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.updatePosition();
                    if (!z) {
                        float access$100 = ScrollSlidingTabStrip.this.stickerTabWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount());
                        float scrollX = (((float) ScrollSlidingTabStrip.this.getScrollX()) + f) / (ScrollSlidingTabStrip.this.stickerTabExpandedWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount()));
                        float measuredWidth = (access$100 - ((float) ScrollSlidingTabStrip.this.getMeasuredWidth())) / access$100;
                        float f = f;
                        if (scrollX > measuredWidth) {
                            scrollX = measuredWidth;
                            f = 0.0f;
                        }
                        float f2 = access$100 * scrollX;
                        if (f2 - f < 0.0f) {
                            f2 = f;
                        }
                        ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
                        float unused = scrollSlidingTabStrip2.expandOffset = (((float) scrollSlidingTabStrip2.getScrollX()) + f) - f2;
                        int unused2 = ScrollSlidingTabStrip.this.scrollByOnNextMeasure = (int) (f2 - f);
                        if (ScrollSlidingTabStrip.this.scrollByOnNextMeasure < 0) {
                            int unused3 = ScrollSlidingTabStrip.this.scrollByOnNextMeasure = 0;
                        }
                        for (int i2 = 0; i2 < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i2++) {
                            View childAt = ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i2);
                            if (childAt instanceof StickerTabView) {
                                ((StickerTabView) childAt).setExpanded(false);
                            }
                            childAt.getLayoutParams().width = AndroidUtilities.dp(52.0f);
                        }
                        ScrollSlidingTabStrip scrollSlidingTabStrip3 = ScrollSlidingTabStrip.this;
                        scrollSlidingTabStrip3.animateToExpanded = false;
                        scrollSlidingTabStrip3.getLayoutParams().height = AndroidUtilities.dp(48.0f);
                        ScrollSlidingTabStrip.this.tabsContainer.requestLayout();
                    }
                }
            });
            this.expandStickerAnimator.start();
            if (z) {
                this.animateToExpanded = true;
                for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
                    View childAt = this.tabsContainer.getChildAt(i);
                    if (childAt instanceof StickerTabView) {
                        ((StickerTabView) childAt).setExpanded(true);
                    }
                    childAt.getLayoutParams().width = AndroidUtilities.dp(86.0f);
                }
                this.tabsContainer.requestLayout();
                getLayoutParams().height = AndroidUtilities.dp(98.0f);
            }
            if (z) {
                float childCount = this.stickerTabExpandedWidth * ((float) this.tabsContainer.getChildCount()) * ((((float) getScrollX()) + f) / (this.stickerTabWidth * ((float) this.tabsContainer.getChildCount())));
                this.expandOffset = childCount - (((float) getScrollX()) + f);
                this.scrollByOnNextMeasure = (int) (childCount - f);
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$expandStickers$5(boolean z, float f, ValueAnimator valueAnimator) {
        if (!z) {
            float childCount = this.stickerTabWidth * ((float) this.tabsContainer.getChildCount());
            float scrollX = (((float) getScrollX()) + f) / (this.stickerTabExpandedWidth * ((float) this.tabsContainer.getChildCount()));
            float measuredWidth = (childCount - ((float) getMeasuredWidth())) / childCount;
            if (scrollX > measuredWidth) {
                scrollX = measuredWidth;
                f = 0.0f;
            }
            float f2 = childCount * scrollX;
            if (f2 - f < 0.0f) {
                f2 = f;
            }
            this.expandOffset = (((float) getScrollX()) + f) - f2;
        }
        this.expandProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
        this.tabsContainer.invalidate();
        updatePosition();
    }

    public float getExpandedOffset() {
        if (this.animateToExpanded) {
            return ((float) AndroidUtilities.dp(50.0f)) * this.expandProgress;
        }
        return 0.0f;
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View childAt = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                childAt.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                childAt.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int i) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(i) != null) {
            int left = this.tabsContainer.getChildAt(i).getLeft();
            if (i > 0) {
                left -= this.scrollOffset;
            }
            int scrollX = getScrollX();
            if (left == this.lastScrollX) {
                return;
            }
            if (left < scrollX) {
                this.lastScrollX = left;
                smoothScrollTo(left, 0);
            } else if (this.scrollOffset + left > (scrollX + getWidth()) - (this.scrollOffset * 2)) {
                int width = (left - getWidth()) + (this.scrollOffset * 3);
                this.lastScrollX = width;
                smoothScrollTo(width, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        setImages();
        int i5 = this.scrollByOnNextMeasure;
        if (i5 >= 0) {
            scrollTo(i5, 0);
            this.scrollByOnNextMeasure = -1;
        }
    }

    public void setImages() {
        ImageLocation imageLocation;
        float f = this.expandProgress;
        float dp = ((float) AndroidUtilities.dp(52.0f)) + (((float) AndroidUtilities.dp(34.0f)) * f);
        int scrollX = (int) ((((float) getScrollX()) - (this.animateToExpanded ? this.expandOffset * (1.0f - f) : 0.0f)) / dp);
        int min = Math.min(this.tabsContainer.getChildCount(), ((int) Math.ceil((double) (((float) getMeasuredWidth()) / dp))) + scrollX + 1);
        if (this.animateToExpanded) {
            scrollX -= 2;
            min += 2;
            if (scrollX < 0) {
                scrollX = 0;
            }
            if (min > this.tabsContainer.getChildCount()) {
                min = this.tabsContainer.getChildCount();
            }
        }
        this.currentPlayingImagesTmp.clear();
        for (int i = 0; i < this.currentPlayingImages.size(); i++) {
            this.currentPlayingImagesTmp.put(this.currentPlayingImages.valueAt(i).index, this.currentPlayingImages.valueAt(i));
        }
        this.currentPlayingImages.clear();
        while (true) {
            String str = null;
            if (scrollX >= min) {
                break;
            }
            View childAt = this.tabsContainer.getChildAt(scrollX);
            if (childAt instanceof StickerTabView) {
                StickerTabView stickerTabView = (StickerTabView) childAt;
                Object tag = childAt.getTag();
                Object tag2 = childAt.getTag(NUM);
                SvgHelper.SvgDrawable svgDrawable = (SvgHelper.SvgDrawable) childAt.getTag(NUM);
                TLRPC$Document tLRPC$Document = (TLRPC$Document) childAt.getTag(NUM);
                boolean z = tag instanceof TLRPC$Document;
                if (z) {
                    imageLocation = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document);
                } else if (tag instanceof TLRPC$PhotoSize) {
                    imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tag, tLRPC$Document, tag2 instanceof TLRPC$TL_messages_stickerSet ? ((TLRPC$TL_messages_stickerSet) tag2).set.thumb_version : 0);
                }
                if (imageLocation != null) {
                    BackupImageView backupImageView = stickerTabView.imageView;
                    if (!z || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                        if (imageLocation.imageType == 1) {
                            backupImageView.setImage(imageLocation, "40_40", "tgs", (Drawable) svgDrawable, tag2);
                        } else {
                            backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) svgDrawable, tag2);
                        }
                    } else if (svgDrawable != null) {
                        backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "40_40", (Drawable) svgDrawable, 0, tag2);
                    } else {
                        backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "40_40", imageLocation, (String) null, 0, tag2);
                    }
                    if (tag2 instanceof TLRPC$TL_messages_stickerSet) {
                        str = ((TLRPC$TL_messages_stickerSet) tag2).set.title;
                    }
                    stickerTabView.textView.setText(str);
                    this.currentPlayingImages.put(stickerTabView.index, stickerTabView);
                    this.currentPlayingImagesTmp.remove(stickerTabView.index);
                }
            }
            scrollX++;
        }
        for (int i2 = 0; i2 < this.currentPlayingImagesTmp.size(); i2++) {
            if (this.currentPlayingImagesTmp.valueAt(i2) != this.draggingView) {
                this.currentPlayingImagesTmp.valueAt(i2).imageView.setImageDrawable((Drawable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public int getTabSize() {
        return AndroidUtilities.dp(this.animateToExpanded ? 86.0f : 52.0f);
    }

    /* access modifiers changed from: protected */
    public void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        setImages();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        float f;
        float f2 = this.stickerTabWidth - this.stickerTabExpandedWidth;
        float f3 = this.expandOffset * (1.0f - this.expandProgress);
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            if (this.tabsContainer.getChildAt(i) instanceof StickerTabView) {
                StickerTabView stickerTabView = (StickerTabView) this.tabsContainer.getChildAt(i);
                stickerTabView.animateIfPositionChanged(this);
                if (this.animateToExpanded) {
                    stickerTabView.setTranslationX((((float) i) * f2 * (1.0f - this.expandProgress)) + f3 + stickerTabView.dragOffset);
                } else {
                    stickerTabView.setTranslationX(stickerTabView.dragOffset);
                }
            }
        }
        super.dispatchDraw(canvas);
        if (!isInEditMode() && this.tabCount != 0) {
            float height = (float) getHeight();
            if (this.animateToExpanded) {
                height = ((float) getHeight()) - (((float) AndroidUtilities.dp(50.0f)) * (1.0f - this.expandProgress));
            }
            if (this.underlineHeight > 0) {
                this.rectPaint.setColor(this.underlineColor);
                canvas.drawRect(0.0f, height - ((float) this.underlineHeight), (float) this.tabsContainer.getWidth(), height, this.rectPaint);
            }
            if (this.indicatorHeight >= 0) {
                View childAt = this.tabsContainer.getChildAt(this.currentPosition);
                float f4 = 0.0f;
                if (childAt != null) {
                    f4 = childAt.getX();
                    f = (float) childAt.getMeasuredWidth();
                } else {
                    f = 0.0f;
                }
                if (this.animateToExpanded) {
                    float f5 = this.stickerTabWidth;
                    f = f5 + ((this.stickerTabExpandedWidth - f5) * this.expandProgress);
                }
                if (this.animateFromPosition) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long j = elapsedRealtime - this.lastAnimationTime;
                    this.lastAnimationTime = elapsedRealtime;
                    float f6 = this.positionAnimationProgress + (((float) j) / 150.0f);
                    this.positionAnimationProgress = f6;
                    if (f6 >= 1.0f) {
                        this.positionAnimationProgress = 1.0f;
                        this.animateFromPosition = false;
                    }
                    float f7 = this.startAnimationPosition;
                    f4 = ((f4 - f7) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.positionAnimationProgress)) + f7;
                    invalidate();
                }
                int i2 = AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[this.type.ordinal()];
                if (i2 == 1) {
                    int i3 = this.indicatorHeight;
                    if (i3 == 0) {
                        this.indicatorDrawable.setBounds((int) f4, 0, (int) (f4 + f), (int) height);
                    } else {
                        this.indicatorDrawable.setBounds((int) f4, (int) (height - ((float) i3)), (int) (f4 + f), (int) height);
                    }
                } else if (i2 == 2) {
                    this.indicatorDrawable.setBounds(((int) f4) + AndroidUtilities.dp(6.0f), (int) (height - ((float) AndroidUtilities.dp(3.0f))), (int) ((f4 + f) - ((float) AndroidUtilities.dp(6.0f))), (int) height);
                }
                this.indicatorDrawable.setColor(this.indicatorColor);
                this.indicatorDrawable.draw(canvas);
            }
        }
    }

    public void drawOverlays(Canvas canvas) {
        if (this.draggingView != null) {
            canvas.save();
            float f = this.draggindViewXOnScreen - this.draggindViewDxOnScreen;
            float f2 = this.draggingViewOutProgress;
            if (f2 > 0.0f) {
                f = (f * (1.0f - f2)) + ((this.draggingView.getX() - ((float) getScrollX())) * this.draggingViewOutProgress);
            }
            canvas.translate(f, 0.0f);
            this.draggingView.draw(canvas);
            canvas.restore();
        }
    }

    public void setShouldExpand(boolean z) {
        this.shouldExpand = z;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void onPageScrolled(int i, int i2) {
        int i3 = this.currentPosition;
        if (i3 != i) {
            View childAt = this.tabsContainer.getChildAt(i3);
            if (childAt != null) {
                this.startAnimationPosition = (float) childAt.getLeft();
                this.positionAnimationProgress = 0.0f;
                this.animateFromPosition = true;
                this.lastAnimationTime = SystemClock.elapsedRealtime();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = i;
            if (i < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                int i4 = 0;
                while (i4 < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(i4).setSelected(i4 == i);
                    i4++;
                }
                if (this.expandStickerAnimator == null) {
                    if (i2 != i || i <= 1) {
                        scrollToChild(i);
                    } else {
                        scrollToChild(i - 1);
                    }
                }
                invalidate();
            }
        }
    }

    public void invalidateTabs() {
        int childCount = this.tabsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
    }

    public void setCurrentPosition(int i) {
        this.currentPosition = i;
    }

    public void setIndicatorHeight(int i) {
        this.indicatorHeight = i;
        invalidate();
    }

    public void setIndicatorColor(int i) {
        this.indicatorColor = i;
        invalidate();
    }

    public void setUnderlineColor(int i) {
        this.underlineColor = i;
        invalidate();
    }

    public void setUnderlineColorResource(int i) {
        this.underlineColor = getResources().getColor(i);
        invalidate();
    }

    public void setUnderlineHeight(int i) {
        this.underlineHeight = i;
        invalidate();
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return checkLongPress(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return checkLongPress(motionEvent) || super.onTouchEvent(motionEvent);
    }

    public boolean checkLongPress(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0 && this.draggingView == null) {
            this.longClickRunning = true;
            AndroidUtilities.runOnUIThread(this.longClickRunnable, 500);
            this.pressedX = motionEvent.getX();
            this.pressedY = motionEvent.getY();
        }
        if (this.longClickRunning && motionEvent.getAction() == 2 && (Math.abs(motionEvent.getX() - this.pressedX) > this.touchSlop || Math.abs(motionEvent.getY() - this.pressedY) > this.touchSlop)) {
            this.longClickRunning = false;
            AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
        }
        if (motionEvent.getAction() != 2 || this.draggingView == null) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                stopScroll();
                AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
                if (this.draggingView != null) {
                    int i = this.startDragFromPosition;
                    int i2 = this.currentDragPosition;
                    if (i != i2) {
                        stickerSetPositionChanged(i, i2);
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    ofFloat.addUpdateListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda0(this));
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                            if (scrollSlidingTabStrip.draggingView != null) {
                                scrollSlidingTabStrip.invalidateOverlays();
                                ScrollSlidingTabStrip.this.draggingView.invalidate();
                                ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                                ScrollSlidingTabStrip.this.draggingView = null;
                            }
                        }
                    });
                    ofFloat.start();
                }
                this.longClickRunning = false;
                invalidateOverlays();
            }
            return false;
        }
        int ceil = ((int) Math.ceil((double) ((((float) getScrollX()) + motionEvent.getX()) / ((float) getTabSize())))) - 1;
        int i3 = this.currentDragPosition;
        if (ceil != i3) {
            if (ceil < i3) {
                while (!canSwap(ceil) && ceil != this.currentDragPosition) {
                    ceil++;
                }
            } else {
                while (!canSwap(ceil) && ceil != this.currentDragPosition) {
                    ceil--;
                }
            }
        }
        if (this.currentDragPosition != ceil && canSwap(ceil)) {
            for (int i4 = 0; i4 < this.tabsContainer.getChildCount(); i4++) {
                if (i4 != this.currentDragPosition) {
                    ((StickerTabView) this.tabsContainer.getChildAt(i4)).saveXPosition();
                }
            }
            this.startDragFromX += (float) ((ceil - this.currentDragPosition) * getTabSize());
            this.currentDragPosition = ceil;
            this.tabsContainer.removeView(this.draggingView);
            this.tabsContainer.addView(this.draggingView, this.currentDragPosition);
            invalidate();
        }
        this.draggindViewDxOnScreen = this.pressedX - motionEvent.getX();
        float x = motionEvent.getX();
        if (x < ((float) this.draggingView.getMeasuredWidth()) / 2.0f) {
            startScroll(false);
        } else if (x > ((float) getMeasuredWidth()) - (((float) this.draggingView.getMeasuredWidth()) / 2.0f)) {
            startScroll(true);
        } else {
            stopScroll();
        }
        this.tabsContainer.invalidate();
        invalidateOverlays();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$checkLongPress$6(ValueAnimator valueAnimator) {
        this.draggingViewOutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateOverlays();
    }

    private void startScroll(boolean z) {
        this.scrollRight = z;
        AndroidUtilities.runOnUIThread(this.scrollRunnable, 16);
    }

    private void stopScroll() {
        AndroidUtilities.cancelRunOnUIThread(this.scrollRunnable);
    }

    /* access modifiers changed from: package-private */
    public boolean isDragging() {
        return this.draggingView != null;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.longClickRunning = false;
        AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
    }
}
