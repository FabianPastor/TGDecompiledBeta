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
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;

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
    private boolean dragEnabled;
    float draggindViewDxOnScreen;
    float draggindViewXOnScreen;
    View draggingView;
    float draggingViewIndicatorOutProgress;
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
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int scrollByOnNextMeasure;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    boolean scrollRight;
    Runnable scrollRunnable;
    long scrollStartTime;
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

    /* access modifiers changed from: protected */
    public void invalidateOverlays() {
    }

    /* access modifiers changed from: protected */
    public void stickerSetPositionChanged(int i, int i2) {
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
    }

    public ScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        AndroidUtilities.dp(12.0f);
        AndroidUtilities.dp(24.0f);
        this.lastScrollX = 0;
        this.currentPlayingImages = new SparseArray<>();
        this.currentPlayingImagesTmp = new SparseArray<>();
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
                    ScrollSlidingTabStrip.this.invalidate();
                }
            }
        };
        this.expanded = false;
        this.stickerTabExpandedWidth = (float) AndroidUtilities.dp(86.0f);
        this.stickerTabWidth = (float) AndroidUtilities.dp(52.0f);
        this.scrollByOnNextMeasure = -1;
        this.scrollRunnable = new Runnable() {
            /* JADX WARNING: Code restructure failed: missing block: B:11:0x004a, code lost:
                if (r7.this$0.scrollRight != false) goto L_0x0021;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:3:0x001f, code lost:
                if (r7.this$0.scrollRight != false) goto L_0x0021;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:9:0x0039, code lost:
                if (r7.this$0.scrollRight != false) goto L_0x0021;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r7 = this;
                    long r0 = java.lang.System.currentTimeMillis()
                    org.telegram.ui.Components.ScrollSlidingTabStrip r2 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    long r2 = r2.scrollStartTime
                    long r0 = r0 - r2
                    r2 = -1
                    r3 = 1
                    r4 = 3000(0xbb8, double:1.482E-320)
                    int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r6 >= 0) goto L_0x0025
                    r0 = 1065353216(0x3var_, float:1.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r0 = java.lang.Math.max(r3, r0)
                    org.telegram.ui.Components.ScrollSlidingTabStrip r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    boolean r1 = r1.scrollRight
                    if (r1 == 0) goto L_0x0022
                L_0x0021:
                    r2 = 1
                L_0x0022:
                    int r0 = r0 * r2
                    goto L_0x004d
                L_0x0025:
                    r4 = 5000(0x1388, double:2.4703E-320)
                    int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r6 >= 0) goto L_0x003c
                    r0 = 1073741824(0x40000000, float:2.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r0 = java.lang.Math.max(r3, r0)
                    org.telegram.ui.Components.ScrollSlidingTabStrip r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    boolean r1 = r1.scrollRight
                    if (r1 == 0) goto L_0x0022
                    goto L_0x0021
                L_0x003c:
                    r0 = 1082130432(0x40800000, float:4.0)
                    int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                    int r0 = java.lang.Math.max(r3, r0)
                    org.telegram.ui.Components.ScrollSlidingTabStrip r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    boolean r1 = r1.scrollRight
                    if (r1 == 0) goto L_0x0022
                    goto L_0x0021
                L_0x004d:
                    org.telegram.ui.Components.ScrollSlidingTabStrip r1 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    r2 = 0
                    r1.scrollBy(r0, r2)
                    org.telegram.ui.Components.ScrollSlidingTabStrip r0 = org.telegram.ui.Components.ScrollSlidingTabStrip.this
                    java.lang.Runnable r0 = r0.scrollRunnable
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r0)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass6.run():void");
            }
        };
        this.resourcesProvider = resourcesProvider2;
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        AnonymousClass2 r3 = new LinearLayout(context) {
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
        this.tabsContainer = r3;
        r3.setOrientation(0);
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
                    ofFloat.addUpdateListener(new ScrollSlidingTabStrip$3$$ExternalSyntheticLambda0(this));
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
            imageView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda3(this));
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
            stickerTabView = new StickerTabView(getContext(), 1);
            stickerTabView.iconView.setImageDrawable(drawable);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda2(this));
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i2);
        }
        stickerTabView.isChatSticker = false;
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
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        boolean z = false;
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i);
        } else {
            stickerTabView = new StickerTabView(getContext(), 0);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda6(this));
            this.tabsContainer.addView(stickerTabView, i);
            stickerTabView.setRoundImage();
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(tLRPC$Chat);
            BackupImageView backupImageView = stickerTabView.imageView;
            backupImageView.setLayerNum(1);
            backupImageView.setForUserOrChat(tLRPC$Chat, avatarDrawable);
            backupImageView.setAspectFit(true);
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            stickerTabView.textView.setText(tLRPC$Chat.title);
        }
        stickerTabView.isChatSticker = true;
        stickerTabView.setTag(NUM, Integer.valueOf(i));
        if (i == this.currentPosition) {
            z = true;
        }
        stickerTabView.setSelected(z);
        this.tabTypes.put(str, stickerTabView);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStickerTab$3(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public View addEmojiTab(int i, Emoji.EmojiDrawable emojiDrawable, TLRPC$Document tLRPC$Document) {
        String str = "tab" + i;
        int i2 = this.tabCount;
        this.tabCount = i2 + 1;
        StickerTabView stickerTabView = (StickerTabView) this.prevTypes.get(str);
        boolean z = true;
        if (stickerTabView != null) {
            checkViewIndex(str, stickerTabView, i2);
        } else {
            stickerTabView = new StickerTabView(getContext(), 2);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda5(this));
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i2);
        }
        stickerTabView.isChatSticker = false;
        stickerTabView.setTag(NUM, Integer.valueOf(i2));
        stickerTabView.setTag(NUM, emojiDrawable);
        stickerTabView.setTag(NUM, tLRPC$Document);
        if (i2 != this.currentPosition) {
            z = false;
        }
        stickerTabView.setSelected(z);
        this.tabTypes.put(str, stickerTabView);
        return stickerTabView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addEmojiTab$4(View view) {
        this.delegate.onPageSelected(((Integer) view.getTag(NUM)).intValue());
    }

    public View addStickerTab(TLObject tLObject, TLRPC$Document tLRPC$Document, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
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
            stickerTabView = new StickerTabView(getContext(), 0);
            stickerTabView.setFocusable(true);
            stickerTabView.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda4(this));
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(stickerTabView, i);
        }
        stickerTabView.isChatSticker = false;
        stickerTabView.setTag(tLObject);
        stickerTabView.setTag(NUM, Integer.valueOf(i));
        stickerTabView.setTag(NUM, tLRPC$TL_messages_stickerSet);
        stickerTabView.setTag(NUM, tLRPC$Document);
        if (i == this.currentPosition) {
            z = true;
        }
        stickerTabView.setSelected(z);
        this.tabTypes.put(sb2, stickerTabView);
        return stickerTabView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$addStickerTab$5(View view) {
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
                        float access$300 = ScrollSlidingTabStrip.this.stickerTabWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount());
                        float scrollX = (((float) ScrollSlidingTabStrip.this.getScrollX()) + f) / (ScrollSlidingTabStrip.this.stickerTabExpandedWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount()));
                        float measuredWidth = (access$300 - ((float) ScrollSlidingTabStrip.this.getMeasuredWidth())) / access$300;
                        float f = f;
                        if (scrollX > measuredWidth) {
                            scrollX = measuredWidth;
                            f = 0.0f;
                        }
                        float f2 = access$300 * scrollX;
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
    public /* synthetic */ void lambda$expandStickers$6(boolean z, float f, ValueAnimator valueAnimator) {
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
                if (stickerTabView.type == 2) {
                    Object tag = stickerTabView.getTag(NUM);
                    Object tag2 = stickerTabView.getTag(NUM);
                    Drawable drawable = tag instanceof Drawable ? (Drawable) tag : null;
                    if (tag2 instanceof TLRPC$Document) {
                        stickerTabView.imageView.setImage(ImageLocation.getForDocument((TLRPC$Document) tag2), "36_36_nolimit", drawable, (Object) null);
                    } else {
                        stickerTabView.imageView.setImageDrawable(drawable);
                    }
                } else {
                    Object tag3 = childAt.getTag();
                    Object tag4 = childAt.getTag(NUM);
                    TLRPC$Document tLRPC$Document = (TLRPC$Document) childAt.getTag(NUM);
                    boolean z = tag3 instanceof TLRPC$Document;
                    if (z) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90);
                        if (!stickerTabView.inited) {
                            stickerTabView.svgThumb = DocumentObject.getSvgThumb((TLRPC$Document) tag3, "emptyListPlaceholder", 0.2f);
                        }
                        imageLocation = ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document);
                    } else if (tag3 instanceof TLRPC$PhotoSize) {
                        imageLocation = ImageLocation.getForSticker((TLRPC$PhotoSize) tag3, tLRPC$Document, tag4 instanceof TLRPC$TL_messages_stickerSet ? ((TLRPC$TL_messages_stickerSet) tag4).set.thumb_version : 0);
                    }
                    if (imageLocation != null) {
                        stickerTabView.inited = true;
                        SvgHelper.SvgDrawable svgDrawable = stickerTabView.svgThumb;
                        BackupImageView backupImageView = stickerTabView.imageView;
                        if (!z || !MessageObject.isAnimatedStickerDocument(tLRPC$Document, true)) {
                            if (imageLocation.imageType == 1) {
                                backupImageView.setImage(imageLocation, "40_40", "tgs", (Drawable) svgDrawable, tag4);
                            } else {
                                backupImageView.setImage(imageLocation, (String) null, "webp", (Drawable) svgDrawable, tag4);
                            }
                        } else if (svgDrawable != null) {
                            backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "40_40", (Drawable) svgDrawable, 0, tag4);
                        } else {
                            backupImageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "40_40", imageLocation, (String) null, 0, tag4);
                        }
                        if (tag4 instanceof TLRPC$TL_messages_stickerSet) {
                            str = ((TLRPC$TL_messages_stickerSet) tag4).set.title;
                        }
                        stickerTabView.textView.setText(str);
                    }
                }
                this.currentPlayingImages.put(stickerTabView.index, stickerTabView);
                this.currentPlayingImagesTmp.remove(stickerTabView.index);
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
    /* JADX WARNING: Removed duplicated region for block: B:52:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x015b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r11) {
        /*
            r10 = this;
            float r0 = r10.stickerTabWidth
            float r1 = r10.stickerTabExpandedWidth
            float r0 = r0 - r1
            float r1 = r10.expandOffset
            float r2 = r10.expandProgress
            r3 = 1065353216(0x3var_, float:1.0)
            float r2 = r3 - r2
            float r1 = r1 * r2
            r2 = 0
            r4 = 0
        L_0x0011:
            android.widget.LinearLayout r5 = r10.tabsContainer
            int r5 = r5.getChildCount()
            if (r4 >= r5) goto L_0x004b
            android.widget.LinearLayout r5 = r10.tabsContainer
            android.view.View r5 = r5.getChildAt(r4)
            boolean r5 = r5 instanceof org.telegram.ui.Components.StickerTabView
            if (r5 == 0) goto L_0x0048
            android.widget.LinearLayout r5 = r10.tabsContainer
            android.view.View r5 = r5.getChildAt(r4)
            org.telegram.ui.Components.StickerTabView r5 = (org.telegram.ui.Components.StickerTabView) r5
            r5.animateIfPositionChanged(r10)
            boolean r6 = r10.animateToExpanded
            if (r6 == 0) goto L_0x0043
            float r6 = (float) r4
            float r6 = r6 * r0
            float r7 = r10.expandProgress
            float r7 = r3 - r7
            float r6 = r6 * r7
            float r6 = r6 + r1
            float r7 = r5.dragOffset
            float r6 = r6 + r7
            r5.setTranslationX(r6)
            goto L_0x0048
        L_0x0043:
            float r6 = r5.dragOffset
            r5.setTranslationX(r6)
        L_0x0048:
            int r4 = r4 + 1
            goto L_0x0011
        L_0x004b:
            super.dispatchDraw(r11)
            boolean r0 = r10.isInEditMode()
            if (r0 != 0) goto L_0x0182
            int r0 = r10.tabCount
            if (r0 != 0) goto L_0x005a
            goto L_0x0182
        L_0x005a:
            int r0 = r10.getHeight()
            float r0 = (float) r0
            boolean r1 = r10.animateToExpanded
            if (r1 == 0) goto L_0x0076
            int r0 = r10.getHeight()
            float r0 = (float) r0
            r1 = 1112014848(0x42480000, float:50.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            float r4 = r10.expandProgress
            float r4 = r3 - r4
            float r1 = r1 * r4
            float r0 = r0 - r1
        L_0x0076:
            int r1 = r10.underlineHeight
            if (r1 <= 0) goto L_0x0095
            android.graphics.Paint r1 = r10.rectPaint
            int r4 = r10.underlineColor
            r1.setColor(r4)
            r5 = 0
            int r1 = r10.underlineHeight
            float r1 = (float) r1
            float r6 = r0 - r1
            android.widget.LinearLayout r1 = r10.tabsContainer
            int r1 = r1.getWidth()
            float r7 = (float) r1
            android.graphics.Paint r9 = r10.rectPaint
            r4 = r11
            r8 = r0
            r4.drawRect(r5, r6, r7, r8, r9)
        L_0x0095:
            int r1 = r10.indicatorHeight
            if (r1 < 0) goto L_0x0182
            android.widget.LinearLayout r1 = r10.tabsContainer
            int r4 = r10.currentPosition
            android.view.View r1 = r1.getChildAt(r4)
            r4 = 0
            if (r1 == 0) goto L_0x00ae
            float r5 = r1.getX()
            int r1 = r1.getMeasuredWidth()
            float r1 = (float) r1
            goto L_0x00b0
        L_0x00ae:
            r1 = 0
            r5 = 0
        L_0x00b0:
            boolean r6 = r10.animateToExpanded
            if (r6 == 0) goto L_0x00be
            float r1 = r10.stickerTabWidth
            float r6 = r10.stickerTabExpandedWidth
            float r6 = r6 - r1
            float r7 = r10.expandProgress
            float r6 = r6 * r7
            float r1 = r1 + r6
        L_0x00be:
            boolean r6 = r10.animateFromPosition
            if (r6 == 0) goto L_0x00ee
            long r6 = android.os.SystemClock.elapsedRealtime()
            long r8 = r10.lastAnimationTime
            long r8 = r6 - r8
            r10.lastAnimationTime = r6
            float r6 = r10.positionAnimationProgress
            float r7 = (float) r8
            r8 = 1125515264(0x43160000, float:150.0)
            float r7 = r7 / r8
            float r6 = r6 + r7
            r10.positionAnimationProgress = r6
            int r6 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1))
            if (r6 < 0) goto L_0x00dd
            r10.positionAnimationProgress = r3
            r10.animateFromPosition = r2
        L_0x00dd:
            float r6 = r10.startAnimationPosition
            float r5 = r5 - r6
            org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r8 = r10.positionAnimationProgress
            float r7 = r7.getInterpolation(r8)
            float r5 = r5 * r7
            float r5 = r5 + r6
            r10.invalidate()
        L_0x00ee:
            android.view.View r6 = r10.draggingView
            r7 = 1037726734(0x3dda740e, float:0.10666667)
            if (r6 == 0) goto L_0x0109
            float r8 = r10.draggingViewIndicatorOutProgress
            int r9 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r9 == 0) goto L_0x0109
            float r8 = r8 + r7
            r10.draggingViewIndicatorOutProgress = r8
            int r4 = (r8 > r3 ? 1 : (r8 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0105
            r10.draggingViewIndicatorOutProgress = r3
            goto L_0x011e
        L_0x0105:
            r10.invalidate()
            goto L_0x011e
        L_0x0109:
            if (r6 != 0) goto L_0x011e
            float r3 = r10.draggingViewIndicatorOutProgress
            int r6 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r6 == 0) goto L_0x011e
            float r3 = r3 - r7
            r10.draggingViewIndicatorOutProgress = r3
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 >= 0) goto L_0x011b
            r10.draggingViewIndicatorOutProgress = r4
            goto L_0x011e
        L_0x011b:
            r10.invalidate()
        L_0x011e:
            int[] r3 = org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r4 = r10.type
            int r4 = r4.ordinal()
            r3 = r3[r4]
            r4 = 1
            if (r3 == r4) goto L_0x015b
            r2 = 2
            if (r3 == r2) goto L_0x012f
            goto L_0x0176
        L_0x012f:
            r2 = 1077936128(0x40400000, float:3.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r3 = (float) r3
            float r4 = r10.draggingViewIndicatorOutProgress
            float r3 = r3 * r4
            android.graphics.drawable.GradientDrawable r4 = r10.indicatorDrawable
            int r6 = (int) r5
            r7 = 1086324736(0x40CLASSNAME, float:6.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r6 = r6 + r8
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r2 = r0 - r2
            float r2 = r2 + r3
            int r2 = (int) r2
            float r5 = r5 + r1
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r1 = (float) r1
            float r5 = r5 - r1
            int r1 = (int) r5
            float r0 = r0 + r3
            int r0 = (int) r0
            r4.setBounds(r6, r2, r1, r0)
            goto L_0x0176
        L_0x015b:
            int r3 = r10.indicatorHeight
            if (r3 != 0) goto L_0x0169
            android.graphics.drawable.GradientDrawable r3 = r10.indicatorDrawable
            int r4 = (int) r5
            float r5 = r5 + r1
            int r1 = (int) r5
            int r0 = (int) r0
            r3.setBounds(r4, r2, r1, r0)
            goto L_0x0176
        L_0x0169:
            android.graphics.drawable.GradientDrawable r2 = r10.indicatorDrawable
            int r4 = (int) r5
            float r3 = (float) r3
            float r3 = r0 - r3
            int r3 = (int) r3
            float r5 = r5 + r1
            int r1 = (int) r5
            int r0 = (int) r0
            r2.setBounds(r4, r3, r1, r0)
        L_0x0176:
            android.graphics.drawable.GradientDrawable r0 = r10.indicatorDrawable
            int r1 = r10.indicatorColor
            r0.setColor(r1)
            android.graphics.drawable.GradientDrawable r0 = r10.indicatorDrawable
            r0.draw(r11)
        L_0x0182:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.dispatchDraw(android.graphics.Canvas):void");
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
                                ScrollSlidingTabStrip.this.invalidate();
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
    public /* synthetic */ void lambda$checkLongPress$7(ValueAnimator valueAnimator) {
        this.draggingViewOutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateOverlays();
    }

    /* access modifiers changed from: private */
    public boolean canSwap(int i) {
        if (this.dragEnabled && i >= 0 && i < this.tabsContainer.getChildCount()) {
            View childAt = this.tabsContainer.getChildAt(i);
            if (childAt instanceof StickerTabView) {
                StickerTabView stickerTabView = (StickerTabView) childAt;
                if (stickerTabView.type != 0 || stickerTabView.isChatSticker) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    private void startScroll(boolean z) {
        this.scrollRight = z;
        if (this.scrollStartTime <= 0) {
            this.scrollStartTime = System.currentTimeMillis();
        }
        AndroidUtilities.runOnUIThread(this.scrollRunnable, 16);
    }

    private void stopScroll() {
        this.scrollStartTime = -1;
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

    public void setDragEnabled(boolean z) {
        this.dragEnabled = z;
    }
}
