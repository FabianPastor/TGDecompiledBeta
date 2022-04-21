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
import android.widget.TextView;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ScrollSlidingTabStrip extends HorizontalScrollView {
    private boolean animateFromPosition;
    boolean animateToExpanded;
    int currentDragPosition;
    SparseArray<StickerTabView> currentPlayingImages = new SparseArray<>();
    SparseArray<StickerTabView> currentPlayingImagesTmp = new SparseArray<>();
    private int currentPosition;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private ScrollSlidingTabStripDelegate delegate;
    private int dividerPadding = AndroidUtilities.dp(12.0f);
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
    boolean expanded = false;
    private SparseArray<View> futureTabsPositions = new SparseArray<>();
    private int indicatorColor = -10066330;
    private GradientDrawable indicatorDrawable = new GradientDrawable();
    private int indicatorHeight;
    private long lastAnimationTime;
    private int lastScrollX = 0;
    Runnable longClickRunnable = new Runnable() {
        public void run() {
            ScrollSlidingTabStrip.this.longClickRunning = false;
            ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
            scrollSlidingTabStrip.startDragFromX = ((float) scrollSlidingTabStrip.getScrollX()) + ScrollSlidingTabStrip.this.pressedX;
            ScrollSlidingTabStrip.this.dragDx = 0.0f;
            int p = ((int) Math.ceil((double) (ScrollSlidingTabStrip.this.startDragFromX / ((float) ScrollSlidingTabStrip.this.getTabSize())))) - 1;
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = ScrollSlidingTabStrip.this;
            scrollSlidingTabStrip2.currentDragPosition = p;
            scrollSlidingTabStrip2.startDragFromPosition = p;
            if (ScrollSlidingTabStrip.this.canSwap(p) && p >= 0 && p < ScrollSlidingTabStrip.this.tabsContainer.getChildCount()) {
                ScrollSlidingTabStrip.this.performHapticFeedback(0);
                ScrollSlidingTabStrip.this.draggindViewDxOnScreen = 0.0f;
                ScrollSlidingTabStrip.this.draggingViewOutProgress = 0.0f;
                ScrollSlidingTabStrip scrollSlidingTabStrip3 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip3.draggingView = scrollSlidingTabStrip3.tabsContainer.getChildAt(p);
                ScrollSlidingTabStrip scrollSlidingTabStrip4 = ScrollSlidingTabStrip.this;
                scrollSlidingTabStrip4.draggindViewXOnScreen = scrollSlidingTabStrip4.draggingView.getX() - ((float) ScrollSlidingTabStrip.this.getScrollX());
                ScrollSlidingTabStrip.this.draggingView.invalidate();
                ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                ScrollSlidingTabStrip.this.invalidateOverlays();
                ScrollSlidingTabStrip.this.invalidate();
            }
        }
    };
    boolean longClickRunning;
    private float positionAnimationProgress;
    float pressedX;
    float pressedY;
    private HashMap<String, View> prevTypes = new HashMap<>();
    private Paint rectPaint;
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public int scrollByOnNextMeasure = -1;
    private int scrollOffset = AndroidUtilities.dp(52.0f);
    boolean scrollRight;
    Runnable scrollRunnable = new Runnable() {
        public void run() {
            int dx;
            long currentTime = System.currentTimeMillis() - ScrollSlidingTabStrip.this.scrollStartTime;
            int i = -1;
            if (currentTime < 3000) {
                int max = Math.max(1, AndroidUtilities.dp(1.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max * i;
            } else if (currentTime < 5000) {
                int max2 = Math.max(1, AndroidUtilities.dp(2.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max2 * i;
            } else {
                int max3 = Math.max(1, AndroidUtilities.dp(4.0f));
                if (ScrollSlidingTabStrip.this.scrollRight) {
                    i = 1;
                }
                dx = max3 * i;
            }
            ScrollSlidingTabStrip.this.scrollBy(dx, 0);
            AndroidUtilities.runOnUIThread(ScrollSlidingTabStrip.this.scrollRunnable);
        }
    };
    long scrollStartTime;
    private boolean shouldExpand;
    private float startAnimationPosition;
    int startDragFromPosition;
    float startDragFromX;
    /* access modifiers changed from: private */
    public float stickerTabExpandedWidth = ((float) AndroidUtilities.dp(86.0f));
    /* access modifiers changed from: private */
    public float stickerTabWidth = ((float) AndroidUtilities.dp(52.0f));
    private int tabCount;
    private int tabPadding = AndroidUtilities.dp(24.0f);
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

    public ScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.resourcesProvider = resourcesProvider2;
        this.touchSlop = (float) ViewConfiguration.get(context).getScaledTouchSlop();
        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        AnonymousClass2 r4 = new LinearLayout(context) {
            /* access modifiers changed from: protected */
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (child instanceof StickerTabView) {
                    ((StickerTabView) child).updateExpandProgress(ScrollSlidingTabStrip.this.expandProgress);
                }
                if (child == ScrollSlidingTabStrip.this.draggingView) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
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
            switch (AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[type2.ordinal()]) {
                case 1:
                    this.indicatorDrawable.setCornerRadius(0.0f);
                    return;
                case 2:
                    float rad = AndroidUtilities.dpf2(3.0f);
                    this.indicatorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$7  reason: invalid class name */
    static /* synthetic */ class AnonymousClass7 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type;

        static {
            int[] iArr = new int[Type.values().length];
            $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type = iArr;
            try {
                iArr[Type.LINE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type[Type.TAB.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
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

    public void beginUpdate(boolean animated) {
        this.prevTypes = this.tabTypes;
        this.tabTypes = new HashMap<>();
        this.futureTabsPositions.clear();
        this.tabCount = 0;
        if (animated && Build.VERSION.SDK_INT >= 19) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(250);
            transition.setOrdering(0);
            transition.addTransition(new Transition() {
                public void captureStartValues(TransitionValues transitionValues) {
                }

                public void captureEndValues(TransitionValues transitionValues) {
                }

                public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
                    ValueAnimator invalidateAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    invalidateAnimator.addUpdateListener(new ScrollSlidingTabStrip$3$$ExternalSyntheticLambda0(this));
                    return invalidateAnimator;
                }

                /* renamed from: lambda$createAnimator$0$org-telegram-ui-Components-ScrollSlidingTabStrip$3  reason: not valid java name */
                public /* synthetic */ void m4304xddfcbcca(ValueAnimator a) {
                    ScrollSlidingTabStrip.this.invalidate();
                }
            });
            TransitionManager.beginDelayedTransition(this.tabsContainer, transition);
        }
    }

    public void commitUpdate() {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            for (Map.Entry<String, View> entry : hashMap.entrySet()) {
                this.tabsContainer.removeView(entry.getValue());
            }
            this.prevTypes.clear();
        }
        int N = this.futureTabsPositions.size();
        for (int a = 0; a < N; a++) {
            int index = this.futureTabsPositions.keyAt(a);
            View view = this.futureTabsPositions.valueAt(a);
            if (this.tabsContainer.indexOfChild(view) != index) {
                this.tabsContainer.removeView(view);
                this.tabsContainer.addView(view, index);
            }
        }
        this.futureTabsPositions.clear();
    }

    public void selectTab(int num) {
        if (num >= 0 && num < this.tabCount) {
            this.tabsContainer.getChildAt(num).performClick();
        }
    }

    private void checkViewIndex(String key, View view, int index) {
        HashMap<String, View> hashMap = this.prevTypes;
        if (hashMap != null) {
            hashMap.remove(key);
        }
        this.futureTabsPositions.put(index, view);
    }

    public TextView addIconTabWithCounter(int id, Drawable drawable) {
        TextView textView;
        String key = "textTab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        FrameLayout tab = (FrameLayout) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            textView = (TextView) tab.getChildAt(1);
            checkViewIndex(key, tab, position);
            Drawable drawable2 = drawable;
        } else {
            tab = new FrameLayout(getContext());
            tab.setFocusable(true);
            this.tabsContainer.addView(tab, position);
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda4(this));
            tab.addView(imageView, LayoutHelper.createFrame(-1, -1.0f));
            TextView textView2 = new TextView(getContext());
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setTextSize(1, 12.0f);
            textView2.setTextColor(getThemedColor("chat_emojiPanelBadgeText"));
            textView2.setGravity(17);
            textView2.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.0f), getThemedColor("chat_emojiPanelBadgeBackground")));
            textView2.setMinWidth(AndroidUtilities.dp(18.0f));
            textView2.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(1.0f));
            tab.addView(textView2, LayoutHelper.createFrame(-2, 18.0f, 51, 26.0f, 6.0f, 0.0f, 0.0f));
            textView = textView2;
        }
        tab.setTag(NUM, Integer.valueOf(position));
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return textView;
    }

    /* renamed from: lambda$addIconTabWithCounter$0$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4298x4b831afd(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public ImageView addIconTab(int id, Drawable drawable) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        ImageView tab = (ImageView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new ImageView(getContext());
            tab.setFocusable(true);
            tab.setImageDrawable(drawable);
            tab.setScaleType(ImageView.ScaleType.CENTER);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda3(this));
            this.tabsContainer.addView(tab, position);
        }
        tab.setTag(NUM, Integer.valueOf(position));
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addIconTab$1$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4297xcb3eCLASSNAME(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public StickerTabView addStickerIconTab(int id, Drawable drawable) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 1);
            tab.iconView.setImageDrawable(drawable);
            tab.setFocusable(true);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda5(this));
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(NUM, Integer.valueOf(position));
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addStickerIconTab$2$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4299x30882916(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public void addStickerTab(TLRPC.Chat chat) {
        String key = "chat" + chat.id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            StickerTabView stickerTabView = new StickerTabView(getContext(), 0);
            tab = stickerTabView;
            tab.setFocusable(true);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda6(this));
            this.tabsContainer.addView(tab, position);
            stickerTabView.setRoundImage();
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            avatarDrawable.setInfo(chat);
            BackupImageView imageView = stickerTabView.imageView;
            imageView.setLayerNum(1);
            imageView.setForUserOrChat(chat, avatarDrawable);
            imageView.setAspectFit(true);
            stickerTabView.setExpanded(this.expanded);
            stickerTabView.updateExpandProgress(this.expandProgress);
            stickerTabView.textView.setText(chat.title);
        }
        tab.isChatSticker = true;
        tab.setTag(NUM, Integer.valueOf(position));
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
    }

    /* renamed from: lambda$addStickerTab$3$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4300x80889fdc(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public View addEmojiTab(int id, Emoji.EmojiDrawable emojiDrawable, TLRPC.Document emojiSticker) {
        String key = "tab" + id;
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = true;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 2);
            tab.setFocusable(true);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda2(this));
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(NUM, Integer.valueOf(position));
        tab.setTag(NUM, emojiDrawable);
        tab.setTag(NUM, emojiSticker);
        if (position != this.currentPosition) {
            z = false;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addEmojiTab$4$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4296xc5ead344(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public View addStickerTab(TLObject thumb, TLRPC.Document sticker, TLRPC.TL_messages_stickerSet parentObject) {
        StringBuilder sb = new StringBuilder();
        sb.append("set");
        sb.append(parentObject == null ? sticker.id : parentObject.set.id);
        String key = sb.toString();
        int position = this.tabCount;
        this.tabCount = position + 1;
        StickerTabView tab = (StickerTabView) this.prevTypes.get(key);
        boolean z = false;
        if (tab != null) {
            checkViewIndex(key, tab, position);
        } else {
            tab = new StickerTabView(getContext(), 0);
            tab.setFocusable(true);
            tab.setOnClickListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda7(this));
            tab.setExpanded(this.expanded);
            tab.updateExpandProgress(this.expandProgress);
            this.tabsContainer.addView(tab, position);
        }
        tab.isChatSticker = false;
        tab.setTag(thumb);
        tab.setTag(NUM, Integer.valueOf(position));
        tab.setTag(NUM, parentObject);
        tab.setTag(NUM, sticker);
        if (position == this.currentPosition) {
            z = true;
        }
        tab.setSelected(z);
        this.tabTypes.put(key, tab);
        return tab;
    }

    /* renamed from: lambda$addStickerTab$5$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4301x63dbec1a(View v) {
        this.delegate.onPageSelected(((Integer) v.getTag(NUM)).intValue());
    }

    public void expandStickers(final float x, final boolean expanded2) {
        if (this.expanded != expanded2) {
            this.expanded = expanded2;
            if (!expanded2) {
                fling(0);
            }
            ValueAnimator valueAnimator = this.expandStickerAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.expandStickerAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.expandProgress;
            fArr[1] = expanded2 ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.expandStickerAnimator = ofFloat;
            ofFloat.addUpdateListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda1(this, expanded2, x));
            this.expandStickerAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ScrollSlidingTabStrip.this.expandStickerAnimator = null;
                    ScrollSlidingTabStrip.this.expandProgress = expanded2 ? 1.0f : 0.0f;
                    for (int i = 0; i < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i++) {
                        ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i).invalidate();
                    }
                    ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                    ScrollSlidingTabStrip.this.updatePosition();
                    if (!expanded2) {
                        float allSize = ScrollSlidingTabStrip.this.stickerTabWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount());
                        float totalXRelative = (((float) ScrollSlidingTabStrip.this.getScrollX()) + x) / (ScrollSlidingTabStrip.this.stickerTabExpandedWidth * ((float) ScrollSlidingTabStrip.this.tabsContainer.getChildCount()));
                        float maxXRelative = (allSize - ((float) ScrollSlidingTabStrip.this.getMeasuredWidth())) / allSize;
                        float additionalX = x;
                        if (totalXRelative > maxXRelative) {
                            totalXRelative = maxXRelative;
                            additionalX = 0.0f;
                        }
                        float scrollToX = allSize * totalXRelative;
                        if (scrollToX - additionalX < 0.0f) {
                            scrollToX = additionalX;
                        }
                        ScrollSlidingTabStrip scrollSlidingTabStrip = ScrollSlidingTabStrip.this;
                        float unused = scrollSlidingTabStrip.expandOffset = (((float) scrollSlidingTabStrip.getScrollX()) + additionalX) - scrollToX;
                        int unused2 = ScrollSlidingTabStrip.this.scrollByOnNextMeasure = (int) (scrollToX - additionalX);
                        if (ScrollSlidingTabStrip.this.scrollByOnNextMeasure < 0) {
                            int unused3 = ScrollSlidingTabStrip.this.scrollByOnNextMeasure = 0;
                        }
                        for (int i2 = 0; i2 < ScrollSlidingTabStrip.this.tabsContainer.getChildCount(); i2++) {
                            View child = ScrollSlidingTabStrip.this.tabsContainer.getChildAt(i2);
                            if (child instanceof StickerTabView) {
                                ((StickerTabView) child).setExpanded(false);
                            }
                            child.getLayoutParams().width = AndroidUtilities.dp(52.0f);
                        }
                        ScrollSlidingTabStrip.this.animateToExpanded = false;
                        ScrollSlidingTabStrip.this.getLayoutParams().height = AndroidUtilities.dp(48.0f);
                        ScrollSlidingTabStrip.this.tabsContainer.requestLayout();
                    }
                }
            });
            this.expandStickerAnimator.start();
            if (expanded2) {
                this.animateToExpanded = true;
                for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
                    View child = this.tabsContainer.getChildAt(i);
                    if (child instanceof StickerTabView) {
                        ((StickerTabView) child).setExpanded(true);
                    }
                    child.getLayoutParams().width = AndroidUtilities.dp(86.0f);
                }
                this.tabsContainer.requestLayout();
                getLayoutParams().height = AndroidUtilities.dp(98.0f);
            }
            if (expanded2) {
                float scrollToX = this.stickerTabExpandedWidth * ((float) this.tabsContainer.getChildCount()) * ((((float) getScrollX()) + x) / (this.stickerTabWidth * ((float) this.tabsContainer.getChildCount())));
                this.expandOffset = scrollToX - (((float) getScrollX()) + x);
                this.scrollByOnNextMeasure = (int) (scrollToX - x);
            }
        }
    }

    /* renamed from: lambda$expandStickers$6$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4303xe446e308(boolean expanded2, float x, ValueAnimator valueAnimator) {
        if (!expanded2) {
            float allSize = this.stickerTabWidth * ((float) this.tabsContainer.getChildCount());
            float totalXRelative = (((float) getScrollX()) + x) / (this.stickerTabExpandedWidth * ((float) this.tabsContainer.getChildCount()));
            float maxXRelative = (allSize - ((float) getMeasuredWidth())) / allSize;
            float additionalX = x;
            if (totalXRelative > maxXRelative) {
                totalXRelative = maxXRelative;
                additionalX = 0.0f;
            }
            float scrollToX = allSize * totalXRelative;
            if (scrollToX - additionalX < 0.0f) {
                scrollToX = additionalX;
            }
            this.expandOffset = (((float) getScrollX()) + additionalX) - scrollToX;
        }
        this.expandProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.tabsContainer.getChildCount(); i++) {
            this.tabsContainer.getChildAt(i).invalidate();
        }
        this.tabsContainer.invalidate();
        updatePosition();
    }

    /* access modifiers changed from: protected */
    public void updatePosition() {
    }

    public float getExpandedOffset() {
        if (this.animateToExpanded) {
            return ((float) AndroidUtilities.dp(50.0f)) * this.expandProgress;
        }
        return 0.0f;
    }

    public void updateTabStyles() {
        for (int i = 0; i < this.tabCount; i++) {
            View v = this.tabsContainer.getChildAt(i);
            if (this.shouldExpand) {
                v.setLayoutParams(this.defaultExpandLayoutParams);
            } else {
                v.setLayoutParams(this.defaultTabLayoutParams);
            }
        }
    }

    private void scrollToChild(int position) {
        if (this.tabCount != 0 && this.tabsContainer.getChildAt(position) != null) {
            int newScrollX = this.tabsContainer.getChildAt(position).getLeft();
            if (position > 0) {
                newScrollX -= this.scrollOffset;
            }
            int currentScrollX = getScrollX();
            if (newScrollX == this.lastScrollX) {
                return;
            }
            if (newScrollX < currentScrollX) {
                this.lastScrollX = newScrollX;
                smoothScrollTo(newScrollX, 0);
            } else if (this.scrollOffset + newScrollX > (getWidth() + currentScrollX) - (this.scrollOffset * 2)) {
                int width = (newScrollX - getWidth()) + (this.scrollOffset * 3);
                this.lastScrollX = width;
                smoothScrollTo(width, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setImages();
        int i = this.scrollByOnNextMeasure;
        if (i >= 0) {
            scrollTo(i, 0);
            this.scrollByOnNextMeasure = -1;
        }
    }

    public void setImages() {
        ImageLocation imageLocation;
        float f = this.expandProgress;
        float tabSize = ((float) AndroidUtilities.dp(52.0f)) + (((float) AndroidUtilities.dp(34.0f)) * f);
        int start = (int) ((((float) getScrollX()) - (this.animateToExpanded ? this.expandOffset * (1.0f - f) : 0.0f)) / tabSize);
        int end = Math.min(this.tabsContainer.getChildCount(), ((int) Math.ceil((double) (((float) getMeasuredWidth()) / tabSize))) + start + 1);
        if (this.animateToExpanded) {
            start -= 2;
            end += 2;
            if (start < 0) {
                start = 0;
            }
            if (end > this.tabsContainer.getChildCount()) {
                end = this.tabsContainer.getChildCount();
            }
        }
        this.currentPlayingImagesTmp.clear();
        for (int i = 0; i < this.currentPlayingImages.size(); i++) {
            this.currentPlayingImagesTmp.put(this.currentPlayingImages.valueAt(i).index, this.currentPlayingImages.valueAt(i));
        }
        this.currentPlayingImages.clear();
        for (int a = start; a < end; a++) {
            View child = this.tabsContainer.getChildAt(a);
            if (child instanceof StickerTabView) {
                StickerTabView tabView = (StickerTabView) child;
                if (tabView.type == 2) {
                    Object thumb = tabView.getTag(NUM);
                    Object sticker = tabView.getTag(NUM);
                    Drawable thumbDrawable = null;
                    if (thumb instanceof Drawable) {
                        thumbDrawable = (Drawable) thumb;
                    }
                    if (sticker instanceof TLRPC.Document) {
                        tabView.imageView.setImage(ImageLocation.getForDocument((TLRPC.Document) sticker), "36_36_nolimit", thumbDrawable, (Object) null);
                    } else {
                        tabView.imageView.setImageDrawable(thumbDrawable);
                    }
                } else {
                    Object object = child.getTag();
                    Object parentObject = child.getTag(NUM);
                    TLRPC.Document sticker2 = (TLRPC.Document) child.getTag(NUM);
                    if (object instanceof TLRPC.Document) {
                        TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(sticker2.thumbs, 90);
                        if (!tabView.inited) {
                            tabView.svgThumb = DocumentObject.getSvgThumb((TLRPC.Document) object, "emptyListPlaceholder", 0.2f);
                        }
                        imageLocation = ImageLocation.getForDocument(thumb2, sticker2);
                    } else if (object instanceof TLRPC.PhotoSize) {
                        TLRPC.PhotoSize thumb3 = (TLRPC.PhotoSize) object;
                        int thumbVersion = 0;
                        if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
                            thumbVersion = ((TLRPC.TL_messages_stickerSet) parentObject).set.thumb_version;
                        }
                        imageLocation = ImageLocation.getForSticker(thumb3, sticker2, thumbVersion);
                    }
                    if (imageLocation != null) {
                        tabView.inited = true;
                        SvgHelper.SvgDrawable svgThumb = tabView.svgThumb;
                        BackupImageView imageView = tabView.imageView;
                        if (!(object instanceof TLRPC.Document) || !MessageObject.isVideoSticker(sticker2)) {
                            BackupImageView imageView2 = imageView;
                            SvgHelper.SvgDrawable svgThumb2 = svgThumb;
                            if (!(object instanceof TLRPC.Document) || !MessageObject.isAnimatedStickerDocument(sticker2, true)) {
                                if (imageLocation.imageType == 1) {
                                    imageView2.setImage(imageLocation, "40_40", "tgs", (Drawable) svgThumb2, parentObject);
                                } else {
                                    imageView2.setImage(imageLocation, (String) null, "webp", (Drawable) svgThumb2, parentObject);
                                }
                            } else if (svgThumb2 != null) {
                                imageView2.setImage(ImageLocation.getForDocument(sticker2), "40_40", (Drawable) svgThumb2, 0, parentObject);
                            } else {
                                imageView2.setImage(ImageLocation.getForDocument(sticker2), "40_40", imageLocation, (String) null, 0, parentObject);
                            }
                        } else if (svgThumb != null) {
                            BackupImageView backupImageView = imageView;
                            imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", (Drawable) svgThumb, 0, parentObject);
                        } else {
                            SvgHelper.SvgDrawable svgDrawable = svgThumb;
                            imageView.setImage(ImageLocation.getForDocument(sticker2), "40_40", imageLocation, (String) null, 0, parentObject);
                        }
                        String title = null;
                        if (parentObject instanceof TLRPC.TL_messages_stickerSet) {
                            title = ((TLRPC.TL_messages_stickerSet) parentObject).set.title;
                        }
                        tabView.textView.setText(title);
                    }
                }
                this.currentPlayingImages.put(tabView.index, tabView);
                this.currentPlayingImagesTmp.remove(tabView.index);
            }
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
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setImages();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0134  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0162  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchDraw(android.graphics.Canvas r17) {
        /*
            r16 = this;
            r0 = r16
            float r1 = r0.stickerTabWidth
            float r2 = r0.stickerTabExpandedWidth
            float r1 = r1 - r2
            float r2 = r0.expandOffset
            float r3 = r0.expandProgress
            r4 = 1065353216(0x3var_, float:1.0)
            float r3 = r4 - r3
            float r2 = r2 * r3
            r3 = 0
        L_0x0012:
            android.widget.LinearLayout r5 = r0.tabsContainer
            int r5 = r5.getChildCount()
            if (r3 >= r5) goto L_0x004c
            android.widget.LinearLayout r5 = r0.tabsContainer
            android.view.View r5 = r5.getChildAt(r3)
            boolean r5 = r5 instanceof org.telegram.ui.Components.StickerTabView
            if (r5 == 0) goto L_0x0049
            android.widget.LinearLayout r5 = r0.tabsContainer
            android.view.View r5 = r5.getChildAt(r3)
            org.telegram.ui.Components.StickerTabView r5 = (org.telegram.ui.Components.StickerTabView) r5
            r5.animateIfPositionChanged(r0)
            boolean r6 = r0.animateToExpanded
            if (r6 == 0) goto L_0x0044
            float r6 = (float) r3
            float r6 = r6 * r1
            float r7 = r0.expandProgress
            float r7 = r4 - r7
            float r6 = r6 * r7
            float r6 = r6 + r2
            float r7 = r5.dragOffset
            float r6 = r6 + r7
            r5.setTranslationX(r6)
            goto L_0x0049
        L_0x0044:
            float r6 = r5.dragOffset
            r5.setTranslationX(r6)
        L_0x0049:
            int r3 = r3 + 1
            goto L_0x0012
        L_0x004c:
            super.dispatchDraw(r17)
            boolean r3 = r16.isInEditMode()
            if (r3 != 0) goto L_0x0193
            int r3 = r0.tabCount
            if (r3 != 0) goto L_0x005d
            r8 = r17
            goto L_0x0195
        L_0x005d:
            int r3 = r16.getHeight()
            float r3 = (float) r3
            boolean r5 = r0.animateToExpanded
            if (r5 == 0) goto L_0x007a
            int r5 = r16.getHeight()
            float r5 = (float) r5
            r6 = 1112014848(0x42480000, float:50.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r7 = r0.expandProgress
            float r7 = r4 - r7
            float r6 = r6 * r7
            float r3 = r5 - r6
        L_0x007a:
            int r5 = r0.underlineHeight
            if (r5 <= 0) goto L_0x009a
            android.graphics.Paint r5 = r0.rectPaint
            int r6 = r0.underlineColor
            r5.setColor(r6)
            r6 = 0
            int r5 = r0.underlineHeight
            float r5 = (float) r5
            float r7 = r3 - r5
            android.widget.LinearLayout r5 = r0.tabsContainer
            int r5 = r5.getWidth()
            float r8 = (float) r5
            android.graphics.Paint r10 = r0.rectPaint
            r5 = r17
            r9 = r3
            r5.drawRect(r6, r7, r8, r9, r10)
        L_0x009a:
            int r5 = r0.indicatorHeight
            if (r5 < 0) goto L_0x0190
            android.widget.LinearLayout r5 = r0.tabsContainer
            int r6 = r0.currentPosition
            android.view.View r5 = r5.getChildAt(r6)
            r6 = 0
            r7 = 0
            if (r5 == 0) goto L_0x00b3
            float r6 = r5.getX()
            int r8 = r5.getMeasuredWidth()
            float r7 = (float) r8
        L_0x00b3:
            boolean r8 = r0.animateToExpanded
            if (r8 == 0) goto L_0x00c2
            float r8 = r0.stickerTabWidth
            float r9 = r0.stickerTabExpandedWidth
            float r9 = r9 - r8
            float r10 = r0.expandProgress
            float r9 = r9 * r10
            float r7 = r8 + r9
        L_0x00c2:
            boolean r8 = r0.animateFromPosition
            r9 = 0
            if (r8 == 0) goto L_0x00f5
            long r10 = android.os.SystemClock.elapsedRealtime()
            long r12 = r0.lastAnimationTime
            long r12 = r10 - r12
            r0.lastAnimationTime = r10
            float r8 = r0.positionAnimationProgress
            float r14 = (float) r12
            r15 = 1125515264(0x43160000, float:150.0)
            float r14 = r14 / r15
            float r8 = r8 + r14
            r0.positionAnimationProgress = r8
            int r8 = (r8 > r4 ? 1 : (r8 == r4 ? 0 : -1))
            if (r8 < 0) goto L_0x00e2
            r0.positionAnimationProgress = r4
            r0.animateFromPosition = r9
        L_0x00e2:
            float r8 = r0.startAnimationPosition
            float r14 = r6 - r8
            org.telegram.ui.Components.CubicBezierInterpolator r15 = org.telegram.ui.Components.CubicBezierInterpolator.EASE_OUT_QUINT
            float r9 = r0.positionAnimationProgress
            float r9 = r15.getInterpolation(r9)
            float r14 = r14 * r9
            float r6 = r8 + r14
            r16.invalidate()
        L_0x00f5:
            android.view.View r8 = r0.draggingView
            r9 = 1037726734(0x3dda740e, float:0.10666667)
            if (r8 == 0) goto L_0x0110
            float r10 = r0.draggingViewIndicatorOutProgress
            int r11 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r11 == 0) goto L_0x0110
            float r10 = r10 + r9
            r0.draggingViewIndicatorOutProgress = r10
            int r8 = (r10 > r4 ? 1 : (r10 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x010c
            r0.draggingViewIndicatorOutProgress = r4
            goto L_0x0126
        L_0x010c:
            r16.invalidate()
            goto L_0x0126
        L_0x0110:
            if (r8 != 0) goto L_0x0126
            float r4 = r0.draggingViewIndicatorOutProgress
            r8 = 0
            int r10 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x0126
            float r4 = r4 - r9
            r0.draggingViewIndicatorOutProgress = r4
            int r4 = (r4 > r8 ? 1 : (r4 == r8 ? 0 : -1))
            if (r4 >= 0) goto L_0x0123
            r0.draggingViewIndicatorOutProgress = r8
            goto L_0x0126
        L_0x0123:
            r16.invalidate()
        L_0x0126:
            int[] r4 = org.telegram.ui.Components.ScrollSlidingTabStrip.AnonymousClass7.$SwitchMap$org$telegram$ui$Components$ScrollSlidingTabStrip$Type
            org.telegram.ui.Components.ScrollSlidingTabStrip$Type r8 = r0.type
            int r8 = r8.ordinal()
            r4 = r4[r8]
            switch(r4) {
                case 1: goto L_0x0162;
                case 2: goto L_0x0134;
                default: goto L_0x0133;
            }
        L_0x0133:
            goto L_0x0181
        L_0x0134:
            r4 = 1077936128(0x40400000, float:3.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r8 = (float) r8
            float r9 = r0.draggingViewIndicatorOutProgress
            float r8 = r8 * r9
            android.graphics.drawable.GradientDrawable r9 = r0.indicatorDrawable
            int r10 = (int) r6
            r11 = 1086324736(0x40CLASSNAME, float:6.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = r10 + r12
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r4 = r3 - r4
            float r4 = r4 + r8
            int r4 = (int) r4
            float r12 = r6 + r7
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            float r11 = (float) r11
            float r12 = r12 - r11
            int r11 = (int) r12
            float r12 = r3 + r8
            int r12 = (int) r12
            r9.setBounds(r10, r4, r11, r12)
            goto L_0x0181
        L_0x0162:
            int r4 = r0.indicatorHeight
            if (r4 != 0) goto L_0x0172
            android.graphics.drawable.GradientDrawable r4 = r0.indicatorDrawable
            int r8 = (int) r6
            float r9 = r6 + r7
            int r9 = (int) r9
            int r10 = (int) r3
            r11 = 0
            r4.setBounds(r8, r11, r9, r10)
            goto L_0x0181
        L_0x0172:
            android.graphics.drawable.GradientDrawable r8 = r0.indicatorDrawable
            int r9 = (int) r6
            float r4 = (float) r4
            float r4 = r3 - r4
            int r4 = (int) r4
            float r10 = r6 + r7
            int r10 = (int) r10
            int r11 = (int) r3
            r8.setBounds(r9, r4, r10, r11)
        L_0x0181:
            android.graphics.drawable.GradientDrawable r4 = r0.indicatorDrawable
            int r8 = r0.indicatorColor
            r4.setColor(r8)
            android.graphics.drawable.GradientDrawable r4 = r0.indicatorDrawable
            r8 = r17
            r4.draw(r8)
            goto L_0x0192
        L_0x0190:
            r8 = r17
        L_0x0192:
            return
        L_0x0193:
            r8 = r17
        L_0x0195:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ScrollSlidingTabStrip.dispatchDraw(android.graphics.Canvas):void");
    }

    public void drawOverlays(Canvas canvas) {
        if (this.draggingView != null) {
            canvas.save();
            float x = this.draggindViewXOnScreen - this.draggindViewDxOnScreen;
            float f = this.draggingViewOutProgress;
            if (f > 0.0f) {
                x = ((1.0f - f) * x) + ((this.draggingView.getX() - ((float) getScrollX())) * this.draggingViewOutProgress);
            }
            canvas.translate(x, 0.0f);
            this.draggingView.draw(canvas);
            canvas.restore();
        }
    }

    public void setShouldExpand(boolean value) {
        this.shouldExpand = value;
        requestLayout();
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public void cancelPositionAnimation() {
        this.animateFromPosition = false;
        this.positionAnimationProgress = 1.0f;
    }

    public void onPageScrolled(int position, int first) {
        int i = this.currentPosition;
        if (i != position) {
            View currentTab = this.tabsContainer.getChildAt(i);
            if (currentTab != null) {
                this.startAnimationPosition = (float) currentTab.getLeft();
                this.positionAnimationProgress = 0.0f;
                this.animateFromPosition = true;
                this.lastAnimationTime = SystemClock.elapsedRealtime();
            } else {
                this.animateFromPosition = false;
            }
            this.currentPosition = position;
            if (position < this.tabsContainer.getChildCount()) {
                this.positionAnimationProgress = 0.0f;
                int a = 0;
                while (a < this.tabsContainer.getChildCount()) {
                    this.tabsContainer.getChildAt(a).setSelected(a == position);
                    a++;
                }
                if (this.expandStickerAnimator == null) {
                    if (first != position || position <= 1) {
                        scrollToChild(position);
                    } else {
                        scrollToChild(position - 1);
                    }
                }
                invalidate();
            }
        }
    }

    public void invalidateTabs() {
        int N = this.tabsContainer.getChildCount();
        for (int a = 0; a < N; a++) {
            this.tabsContainer.getChildAt(a).invalidate();
        }
    }

    public void setCurrentPosition(int currentPosition2) {
        this.currentPosition = currentPosition2;
    }

    public void setIndicatorHeight(int value) {
        this.indicatorHeight = value;
        invalidate();
    }

    public void setIndicatorColor(int value) {
        this.indicatorColor = value;
        invalidate();
    }

    public void setUnderlineColor(int value) {
        this.underlineColor = value;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public void setUnderlineHeight(int value) {
        this.underlineHeight = value;
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void invalidateOverlays() {
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return checkLongPress(ev) || super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return checkLongPress(ev) || super.onTouchEvent(ev);
    }

    public boolean checkLongPress(MotionEvent ev) {
        if (ev.getAction() == 0 && this.draggingView == null) {
            this.longClickRunning = true;
            AndroidUtilities.runOnUIThread(this.longClickRunnable, 500);
            this.pressedX = ev.getX();
            this.pressedY = ev.getY();
        }
        if (this.longClickRunning && ev.getAction() == 2 && (Math.abs(ev.getX() - this.pressedX) > this.touchSlop || Math.abs(ev.getY() - this.pressedY) > this.touchSlop)) {
            this.longClickRunning = false;
            AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
        }
        if (ev.getAction() != 2 || this.draggingView == null) {
            if (ev.getAction() == 1 || ev.getAction() == 3) {
                stopScroll();
                AndroidUtilities.cancelRunOnUIThread(this.longClickRunnable);
                if (this.draggingView != null) {
                    int i = this.startDragFromPosition;
                    int i2 = this.currentDragPosition;
                    if (i != i2) {
                        stickerSetPositionChanged(i, i2);
                    }
                    ValueAnimator dragViewOutAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    dragViewOutAnimator.addUpdateListener(new ScrollSlidingTabStrip$$ExternalSyntheticLambda0(this));
                    dragViewOutAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (ScrollSlidingTabStrip.this.draggingView != null) {
                                ScrollSlidingTabStrip.this.invalidateOverlays();
                                ScrollSlidingTabStrip.this.draggingView.invalidate();
                                ScrollSlidingTabStrip.this.tabsContainer.invalidate();
                                ScrollSlidingTabStrip.this.invalidate();
                                ScrollSlidingTabStrip.this.draggingView = null;
                            }
                        }
                    });
                    dragViewOutAnimator.start();
                }
                this.longClickRunning = false;
                invalidateOverlays();
            }
            return false;
        }
        float x = ((float) getScrollX()) + ev.getX();
        int p = ((int) Math.ceil((double) (x / ((float) getTabSize())))) - 1;
        int i3 = this.currentDragPosition;
        if (p != i3) {
            if (p < i3) {
                while (!canSwap(p) && p != this.currentDragPosition) {
                    p++;
                }
            } else {
                while (!canSwap(p) && p != this.currentDragPosition) {
                    p--;
                }
            }
        }
        if (this.currentDragPosition != p && canSwap(p)) {
            for (int i4 = 0; i4 < this.tabsContainer.getChildCount(); i4++) {
                if (i4 != this.currentDragPosition) {
                    ((StickerTabView) this.tabsContainer.getChildAt(i4)).saveXPosition();
                }
            }
            this.startDragFromX += (float) ((p - this.currentDragPosition) * getTabSize());
            this.currentDragPosition = p;
            this.tabsContainer.removeView(this.draggingView);
            this.tabsContainer.addView(this.draggingView, this.currentDragPosition);
            invalidate();
        }
        this.dragDx = x - this.startDragFromX;
        this.draggindViewDxOnScreen = this.pressedX - ev.getX();
        float viewScreenX = ev.getX();
        if (viewScreenX < ((float) this.draggingView.getMeasuredWidth()) / 2.0f) {
            startScroll(false);
        } else if (viewScreenX > ((float) getMeasuredWidth()) - (((float) this.draggingView.getMeasuredWidth()) / 2.0f)) {
            startScroll(true);
        } else {
            stopScroll();
        }
        this.tabsContainer.invalidate();
        invalidateOverlays();
        return true;
    }

    /* renamed from: lambda$checkLongPress$7$org-telegram-ui-Components-ScrollSlidingTabStrip  reason: not valid java name */
    public /* synthetic */ void m4302x18a44158(ValueAnimator valueAnimator) {
        this.draggingViewOutProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidateOverlays();
    }

    /* access modifiers changed from: protected */
    public void stickerSetPositionChanged(int fromPosition, int toPosition) {
    }

    /* access modifiers changed from: private */
    public boolean canSwap(int p) {
        if (!this.dragEnabled || p < 0 || p >= this.tabsContainer.getChildCount()) {
            return false;
        }
        View child = this.tabsContainer.getChildAt(p);
        if (!(child instanceof StickerTabView) || ((StickerTabView) child).type != 0 || ((StickerTabView) child).isChatSticker) {
            return false;
        }
        return true;
    }

    private void startScroll(boolean scrollRight2) {
        this.scrollRight = scrollRight2;
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

    public void setDragEnabled(boolean enabled) {
        this.dragEnabled = enabled;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
