package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.PaddedListAdapter;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ContentPreviewViewer;

public class MentionsContainerView extends BlurredFrameLayout {
    /* access modifiers changed from: private */
    public MentionsAdapter adapter;
    private int animationIndex = -1;
    ChatActivity chatActivity;
    private Integer color;
    private float containerBottom;
    private float containerPadding;
    private float containerTop;
    /* access modifiers changed from: private */
    public ExtendedGridLayoutManager gridLayoutManager;
    private float hideT = 0.0f;
    /* access modifiers changed from: private */
    public boolean ignoreLayout = false;
    /* access modifiers changed from: private */
    public LinearLayoutManager linearLayoutManager;
    /* access modifiers changed from: private */
    public MentionsListView listView;
    private boolean listViewHiding = false;
    /* access modifiers changed from: private */
    public float listViewPadding;
    private SpringAnimation listViewTranslationAnimator;
    /* access modifiers changed from: private */
    public PaddedListAdapter paddedAdapter;
    private Paint paint;
    private Path path;
    private Rect rect = new Rect();
    private final Theme.ResourcesProvider resourcesProvider;
    /* access modifiers changed from: private */
    public boolean scrollToFirst = false;
    private boolean shouldLiftMentions = false;
    /* access modifiers changed from: private */
    public boolean shown = false;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;
    /* access modifiers changed from: private */
    public boolean switchLayoutManagerOnEnd = false;
    /* access modifiers changed from: private */
    public Runnable updateVisibilityRunnable = new MentionsContainerView$$ExternalSyntheticLambda3(this);

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MentionsContainerView(android.content.Context r17, long r18, int r20, org.telegram.ui.ChatActivity r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22) {
        /*
            r16 = this;
            r6 = r16
            r15 = r17
            r14 = r21
            r13 = r22
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r14.contentView
            r6.<init>(r15, r0)
            r12 = 0
            r6.shouldLiftMentions = r12
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.rect = r0
            r6.ignoreLayout = r12
            r6.scrollToFirst = r12
            r6.shown = r12
            org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda3 r0 = new org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda3
            r0.<init>(r6)
            r6.updateVisibilityRunnable = r0
            r10 = -1
            r6.animationIndex = r10
            r6.listViewHiding = r12
            r0 = 0
            r6.hideT = r0
            r6.switchLayoutManagerOnEnd = r12
            r6.chatActivity = r14
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r14.contentView
            r6.sizeNotifierFrameLayout = r0
            r6.resourcesProvider = r13
            r6.drawBlur = r12
            r6.isTopView = r12
            r0 = 8
            r6.setVisibility(r0)
            r6.setWillNotDraw(r12)
            r0 = 1123811328(0x42fCLASSNAME, float:126.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            android.graphics.Point r1 = org.telegram.messenger.AndroidUtilities.displaySize
            int r1 = r1.y
            float r1 = (float) r1
            r2 = 1046562734(0x3e6147ae, float:0.22)
            float r1 = r1 * r2
            float r0 = java.lang.Math.min(r0, r1)
            int r0 = (int) r0
            float r0 = (float) r0
            r6.listViewPadding = r0
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r0 = new org.telegram.ui.Components.MentionsContainerView$MentionsListView
            r0.<init>(r15, r13)
            r6.listView = r0
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.Components.MentionsContainerView$1 r0 = new org.telegram.ui.Components.MentionsContainerView$1
            r0.<init>(r15)
            r6.linearLayoutManager = r0
            r1 = 1
            r0.setOrientation(r1)
            org.telegram.ui.Components.MentionsContainerView$2 r7 = new org.telegram.ui.Components.MentionsContainerView$2
            r3 = 100
            r4 = 0
            r5 = 0
            r0 = r7
            r1 = r16
            r2 = r17
            r0.<init>(r2, r3, r4, r5)
            r6.gridLayoutManager = r7
            org.telegram.ui.Components.MentionsContainerView$3 r0 = new org.telegram.ui.Components.MentionsContainerView$3
            r0.<init>()
            r7.setSpanSizeLookup(r0)
            androidx.recyclerview.widget.DefaultItemAnimator r0 = new androidx.recyclerview.widget.DefaultItemAnimator
            r0.<init>()
            r1 = 150(0x96, double:7.4E-322)
            r0.setAddDuration(r1)
            r0.setMoveDuration(r1)
            r0.setChangeDuration(r1)
            r0.setRemoveDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r1)
            r0.setDelayAnimations(r12)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r1 = r6.listView
            r1.setItemAnimator(r0)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r1 = r6.listView
            r1.setClipToPadding(r12)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r1 = r6.listView
            androidx.recyclerview.widget.LinearLayoutManager r2 = r6.linearLayoutManager
            r1.setLayoutManager(r2)
            org.telegram.ui.Adapters.MentionsAdapter r1 = new org.telegram.ui.Adapters.MentionsAdapter
            org.telegram.ui.Components.MentionsContainerView$4 r2 = new org.telegram.ui.Components.MentionsContainerView$4
            r2.<init>(r14)
            r9 = 0
            r7 = r1
            r8 = r17
            r3 = -1
            r10 = r18
            r4 = 0
            r12 = r20
            r13 = r2
            r14 = r22
            r7.<init>(r8, r9, r10, r12, r13, r14)
            r6.adapter = r1
            org.telegram.ui.Adapters.PaddedListAdapter r1 = new org.telegram.ui.Adapters.PaddedListAdapter
            org.telegram.ui.Adapters.MentionsAdapter r2 = r6.adapter
            r1.<init>(r2)
            r6.paddedAdapter = r1
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r2 = r6.listView
            r2.setAdapter(r1)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r1 = r6.listView
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r2)
            r6.addView(r1, r2)
            r6.setReversed(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.<init>(android.content.Context, long, int, org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    /* access modifiers changed from: protected */
    public boolean canOpen() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onOpen() {
    }

    /* access modifiers changed from: protected */
    public void onClose() {
    }

    /* access modifiers changed from: protected */
    public void onContextSearch(boolean searching) {
    }

    /* access modifiers changed from: protected */
    public void onContextClick(TLRPC.BotInlineResult result) {
    }

    public void onPanTransitionStart() {
        this.shouldLiftMentions = isReversed();
    }

    public void onPanTransitionUpdate(float translationY) {
        if (this.shouldLiftMentions) {
            setTranslationY(translationY);
        }
    }

    public void onPanTransitionEnd() {
    }

    public MentionsListView getListView() {
        return this.listView;
    }

    public MentionsAdapter getAdapter() {
        return this.adapter;
    }

    public void setReversed(boolean reversed) {
        this.scrollToFirst = true;
        this.linearLayoutManager.setReverseLayout(reversed);
        this.adapter.setIsReversed(reversed);
    }

    public boolean isReversed() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager2 = this.linearLayoutManager;
        return layoutManager == linearLayoutManager2 && linearLayoutManager2.getReverseLayout();
    }

    public LinearLayoutManager getCurrentLayoutManager() {
        RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        LinearLayoutManager linearLayoutManager2 = this.linearLayoutManager;
        return layoutManager == linearLayoutManager2 ? linearLayoutManager2 : this.gridLayoutManager;
    }

    public LinearLayoutManager getNeededLayoutManager() {
        return ((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout()) ? this.gridLayoutManager : this.linearLayoutManager;
    }

    public float clipBottom() {
        if (getVisibility() == 0 && !isReversed()) {
            return ((float) getMeasuredHeight()) - this.containerTop;
        }
        return 0.0f;
    }

    public float clipTop() {
        if (getVisibility() == 0 && isReversed()) {
            return this.containerBottom;
        }
        return 0.0f;
    }

    public void dispatchDraw(Canvas canvas) {
        float r;
        boolean reversed = isReversed();
        this.containerPadding = (float) AndroidUtilities.dp((float) (((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout() && this.adapter.getBotContextSwitch() == null ? 2 : 0) + 2));
        float r2 = (float) AndroidUtilities.dp(4.0f);
        if (reversed) {
            float top = Math.min(Math.max(0.0f, ((float) (this.paddedAdapter.paddingViewAttached ? this.paddedAdapter.paddingView.getTop() : getHeight())) + this.listView.getTranslationY()) + this.containerPadding, (1.0f - this.hideT) * ((float) getHeight()));
            Rect rect2 = this.rect;
            this.containerTop = 0.0f;
            int measuredWidth = getMeasuredWidth();
            this.containerBottom = top;
            rect2.set(0, (int) 0.0f, measuredWidth, (int) top);
            float r3 = Math.min(r2, Math.abs(((float) getMeasuredHeight()) - this.containerBottom));
            if (r3 > 0.0f) {
                this.rect.top -= (int) r3;
            }
            r = r3;
        } else {
            if (this.listView.getLayoutManager() == this.gridLayoutManager) {
                this.containerPadding += (float) AndroidUtilities.dp(2.0f);
                r2 += (float) AndroidUtilities.dp(2.0f);
            }
            float top2 = Math.max(0.0f, ((float) (this.paddedAdapter.paddingViewAttached ? this.paddedAdapter.paddingView.getBottom() : 0)) + this.listView.getTranslationY()) - this.containerPadding;
            this.containerTop = top2;
            float top3 = Math.max(top2, this.hideT * ((float) getHeight()));
            Rect rect3 = this.rect;
            this.containerTop = top3;
            int measuredWidth2 = getMeasuredWidth();
            float measuredHeight = (float) getMeasuredHeight();
            this.containerBottom = measuredHeight;
            rect3.set(0, (int) top3, measuredWidth2, (int) measuredHeight);
            float r4 = Math.min(r2, Math.abs(this.containerTop));
            if (r4 > 0.0f) {
                this.rect.bottom += (int) r4;
            }
            r = r4;
        }
        if (this.paint == null) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setShadowLayer((float) AndroidUtilities.dp(4.0f), 0.0f, 0.0f, NUM);
        }
        Paint paint3 = this.paint;
        Integer num = this.color;
        paint3.setColor(num != null ? num.intValue() : getThemedColor("chat_messagePanelBackground"));
        if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierFrameLayout == null) {
            AndroidUtilities.rectTmp.set(this.rect);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, r, r, this.paint);
        } else {
            if (r > 0.0f) {
                canvas.save();
                Path path2 = this.path;
                if (path2 == null) {
                    this.path = new Path();
                } else {
                    path2.reset();
                }
                AndroidUtilities.rectTmp.set(this.rect);
                this.path.addRoundRect(AndroidUtilities.rectTmp, r, r, Path.Direction.CW);
                canvas.clipPath(this.path);
            }
            this.sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rect, this.paint, reversed);
            if (r > 0.0f) {
                canvas.restore();
            }
        }
        canvas.save();
        canvas.clipRect(this.rect);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void setOverrideColor(int color2) {
        this.color = Integer.valueOf(color2);
        invalidate();
    }

    public void setIgnoreLayout(boolean ignore) {
        this.ignoreLayout = ignore;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-MentionsContainerView  reason: not valid java name */
    public /* synthetic */ void m1113lambda$new$0$orgtelegramuiComponentsMentionsContainerView() {
        updateListViewTranslation(!this.shown, true);
    }

    public void updateVisibility(boolean show) {
        if (show) {
            boolean reversed = isReversed();
            if (!this.shown) {
                this.scrollToFirst = true;
                RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
                LinearLayoutManager linearLayoutManager2 = this.linearLayoutManager;
                if (layoutManager == linearLayoutManager2) {
                    linearLayoutManager2.scrollToPositionWithOffset(0, reversed ? -100000 : 100000);
                }
                if (getVisibility() == 8) {
                    this.hideT = 1.0f;
                    MentionsListView mentionsListView = this.listView;
                    mentionsListView.setTranslationY(reversed ? -(this.listViewPadding + ((float) AndroidUtilities.dp(12.0f))) : ((float) mentionsListView.computeVerticalScrollOffset()) + this.listViewPadding);
                }
            }
            setVisibility(0);
        } else {
            this.scrollToFirst = false;
        }
        this.shown = show;
        AndroidUtilities.cancelRunOnUIThread(this.updateVisibilityRunnable);
        SpringAnimation springAnimation = this.listViewTranslationAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        AndroidUtilities.runOnUIThread(this.updateVisibilityRunnable, this.chatActivity.fragmentOpened ? 0 : 100);
        if (show) {
            onOpen();
        } else {
            onClose();
        }
    }

    public boolean isOpen() {
        return this.shown;
    }

    private void updateListViewTranslation(boolean forceZeroHeight, boolean animated) {
        float itemHeight;
        float newTranslationY;
        SpringAnimation springAnimation;
        boolean z = forceZeroHeight;
        if (this.listView != null && this.paddedAdapter != null) {
            if (!this.listViewHiding || (springAnimation = this.listViewTranslationAnimator) == null || !springAnimation.isRunning() || !z) {
                boolean reversed = isReversed();
                if (z) {
                    itemHeight = (-this.containerPadding) - ((float) AndroidUtilities.dp(6.0f));
                } else {
                    itemHeight = ((float) (this.listView.computeVerticalScrollRange() - this.paddedAdapter.getPadding())) + this.containerPadding;
                }
                float f = 0.0f;
                float f2 = this.listViewPadding;
                float newTranslationY2 = reversed ? -Math.max(0.0f, f2 - itemHeight) : Math.max(0.0f, f2 - itemHeight) + (-f2);
                if (!z || reversed) {
                    newTranslationY = newTranslationY2;
                } else {
                    newTranslationY = newTranslationY2 + ((float) this.listView.computeVerticalScrollOffset());
                }
                setVisibility(0);
                SpringAnimation springAnimation2 = this.listViewTranslationAnimator;
                if (springAnimation2 != null) {
                    springAnimation2.cancel();
                }
                int i = 8;
                if (animated) {
                    this.listViewHiding = z;
                    float fromTranslation = this.listView.getTranslationY();
                    float toTranslation = newTranslationY;
                    float fromHideT = this.hideT;
                    float toHideT = z ? 1.0f : 0.0f;
                    if (fromTranslation == toTranslation) {
                        this.listViewTranslationAnimator = null;
                        if (!z) {
                            i = 0;
                        }
                        setVisibility(i);
                        if (!this.switchLayoutManagerOnEnd || !z) {
                            boolean z2 = reversed;
                            return;
                        }
                        this.switchLayoutManagerOnEnd = false;
                        this.listView.setLayoutManager(getNeededLayoutManager());
                        this.shown = true;
                        updateVisibility(true);
                        boolean z3 = reversed;
                        return;
                    }
                    int account = UserConfig.selectedAccount;
                    this.animationIndex = NotificationCenter.getInstance(account).setAnimationInProgress(this.animationIndex, (int[]) null);
                    SpringAnimation spring = new SpringAnimation(new FloatValueHolder(fromTranslation)).setSpring(new SpringForce(toTranslation).setDampingRatio(1.0f).setStiffness(550.0f));
                    this.listViewTranslationAnimator = spring;
                    boolean z4 = reversed;
                    MentionsContainerView$$ExternalSyntheticLambda2 mentionsContainerView$$ExternalSyntheticLambda2 = r0;
                    MentionsContainerView$$ExternalSyntheticLambda2 mentionsContainerView$$ExternalSyntheticLambda22 = new MentionsContainerView$$ExternalSyntheticLambda2(this, fromHideT, toHideT, fromTranslation, toTranslation);
                    spring.addUpdateListener(mentionsContainerView$$ExternalSyntheticLambda2);
                    if (z) {
                        this.listViewTranslationAnimator.addEndListener(new MentionsContainerView$$ExternalSyntheticLambda1(this, z));
                    }
                    this.listViewTranslationAnimator.addEndListener(new MentionsContainerView$$ExternalSyntheticLambda0(this, account));
                    this.listViewTranslationAnimator.start();
                    return;
                }
                if (z) {
                    f = 1.0f;
                }
                this.hideT = f;
                this.listView.setTranslationY(newTranslationY);
                if (z) {
                    setVisibility(8);
                }
            }
        }
    }

    /* renamed from: lambda$updateListViewTranslation$1$org-telegram-ui-Components-MentionsContainerView  reason: not valid java name */
    public /* synthetic */ void m1114xb07e35ab(float fromHideT, float toHideT, float fromTranslation, float toTranslation, DynamicAnimation anm, float val, float vel) {
        this.listView.setTranslationY(val);
        this.hideT = AndroidUtilities.lerp(fromHideT, toHideT, (val - fromTranslation) / (toTranslation - fromTranslation));
    }

    /* renamed from: lambda$updateListViewTranslation$2$org-telegram-ui-Components-MentionsContainerView  reason: not valid java name */
    public /* synthetic */ void m1115xa227dbca(boolean forceZeroHeight, DynamicAnimation a, boolean cancelled, float b, float c) {
        if (!cancelled) {
            this.listViewTranslationAnimator = null;
            setVisibility(forceZeroHeight ? 8 : 0);
            if (this.switchLayoutManagerOnEnd && forceZeroHeight) {
                this.switchLayoutManagerOnEnd = false;
                this.listView.setLayoutManager(getNeededLayoutManager());
                this.shown = true;
                updateVisibility(true);
            }
        }
    }

    /* renamed from: lambda$updateListViewTranslation$3$org-telegram-ui-Components-MentionsContainerView  reason: not valid java name */
    public /* synthetic */ void m1116x93d181e9(int account, DynamicAnimation animation, boolean canceled, float value, float velocity) {
        NotificationCenter.getInstance(account).onAnimationFinish(this.animationIndex);
    }

    public class MentionsListView extends RecyclerListView {
        /* access modifiers changed from: private */
        public boolean isDragging;
        /* access modifiers changed from: private */
        public boolean isScrolling;
        private int lastHeight;
        private int lastWidth;

        public MentionsListView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            setOnScrollListener(new RecyclerView.OnScrollListener(MentionsContainerView.this) {
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    boolean z = false;
                    boolean unused = MentionsListView.this.isScrolling = newState != 0;
                    MentionsListView mentionsListView = MentionsListView.this;
                    if (newState == 1) {
                        z = true;
                    }
                    boolean unused2 = mentionsListView.isDragging = z;
                }

                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    int lastVisibleItem;
                    if (MentionsListView.this.getLayoutManager() == MentionsContainerView.this.gridLayoutManager) {
                        lastVisibleItem = MentionsContainerView.this.gridLayoutManager.findLastVisibleItemPosition();
                    } else {
                        lastVisibleItem = MentionsContainerView.this.linearLayoutManager.findLastVisibleItemPosition();
                    }
                    if ((lastVisibleItem == -1 ? 0 : lastVisibleItem) > 0 && lastVisibleItem > MentionsContainerView.this.adapter.getLastItemCount() - 5) {
                        MentionsContainerView.this.adapter.searchForContextBotForNextOffset();
                    }
                }
            });
            addItemDecoration(new RecyclerView.ItemDecoration(MentionsContainerView.this) {
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position;
                    int i = 0;
                    outRect.left = 0;
                    outRect.right = 0;
                    outRect.top = 0;
                    outRect.bottom = 0;
                    if (parent.getLayoutManager() == MentionsContainerView.this.gridLayoutManager && (position = parent.getChildAdapterPosition(view)) != 0) {
                        int position2 = position - 1;
                        if (!MentionsContainerView.this.adapter.isStickers()) {
                            if (MentionsContainerView.this.adapter.getBotContextSwitch() == null) {
                                outRect.top = AndroidUtilities.dp(2.0f);
                            } else if (position2 != 0) {
                                position2--;
                                if (!MentionsContainerView.this.gridLayoutManager.isFirstRow(position2)) {
                                    outRect.top = AndroidUtilities.dp(2.0f);
                                }
                            } else {
                                return;
                            }
                            if (!MentionsContainerView.this.gridLayoutManager.isLastInRow(position2)) {
                                i = AndroidUtilities.dp(2.0f);
                            }
                            outRect.right = i;
                        }
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            boolean result;
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() > ((float) MentionsContainerView.this.paddedAdapter.paddingView.getTop())) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() < ((float) MentionsContainerView.this.paddedAdapter.paddingView.getBottom())) {
                return false;
            }
            if (!this.isScrolling) {
                if (ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, MentionsContainerView.this.listView, 0, (ContentPreviewViewer.ContentPreviewViewerDelegate) null, this.resourcesProvider)) {
                    result = true;
                    if ((MentionsContainerView.this.adapter.isStickers() && event.getAction() == 0) || event.getAction() == 2) {
                        MentionsContainerView.this.adapter.doSomeStickersAction();
                    }
                    if (!super.onInterceptTouchEvent(event) || result) {
                        return true;
                    }
                    return false;
                }
            }
            result = false;
            MentionsContainerView.this.adapter.doSomeStickersAction();
            if (!super.onInterceptTouchEvent(event)) {
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() > ((float) MentionsContainerView.this.paddedAdapter.paddingView.getTop())) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && event.getY() < ((float) MentionsContainerView.this.paddedAdapter.paddingView.getBottom())) {
                return false;
            }
            return super.onTouchEvent(event);
        }

        public void requestLayout() {
            if (!MentionsContainerView.this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int offset;
            int width = r - l;
            int height = b - t;
            boolean reversed = MentionsContainerView.this.isReversed();
            LinearLayoutManager layoutManager = MentionsContainerView.this.getCurrentLayoutManager();
            int position = reversed ? layoutManager.findFirstVisibleItemPosition() : layoutManager.findLastVisibleItemPosition();
            View child = layoutManager.findViewByPosition(position);
            if (child != null) {
                offset = child.getTop() - (reversed ? 0 : this.lastHeight - height);
            } else {
                offset = 0;
            }
            super.onLayout(changed, l, t, r, b);
            if (MentionsContainerView.this.scrollToFirst) {
                boolean unused = MentionsContainerView.this.ignoreLayout = true;
                layoutManager.scrollToPositionWithOffset(0, 100000);
                super.onLayout(false, l, t, r, b);
                boolean unused2 = MentionsContainerView.this.ignoreLayout = false;
                boolean unused3 = MentionsContainerView.this.scrollToFirst = false;
            } else if (!(position == -1 || width != this.lastWidth || height - this.lastHeight == 0)) {
                boolean unused4 = MentionsContainerView.this.ignoreLayout = true;
                layoutManager.scrollToPositionWithOffset(position, offset, false);
                super.onLayout(false, l, t, r, b);
                boolean unused5 = MentionsContainerView.this.ignoreLayout = false;
            }
            this.lastHeight = height;
            this.lastWidth = width;
        }

        public void setTranslationY(float translationY) {
            super.setTranslationY(translationY);
            MentionsContainerView.this.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthSpec, int heightSpec) {
            int height = View.MeasureSpec.getSize(heightSpec);
            if (MentionsContainerView.this.paddedAdapter != null) {
                MentionsContainerView.this.paddedAdapter.setPadding(height);
            }
            float unused = MentionsContainerView.this.listViewPadding = (float) ((int) Math.min((float) AndroidUtilities.dp(126.0f), ((float) AndroidUtilities.displaySize.y) * 0.22f));
            super.onMeasure(widthSpec, View.MeasureSpec.makeMeasureSpec(((int) MentionsContainerView.this.listViewPadding) + height, NUM));
        }

        public void onScrolled(int dx, int dy) {
            super.onScrolled(dx, dy);
            MentionsContainerView.this.invalidate();
        }
    }

    private Paint getThemedPaint(String paintKey) {
        Paint paint2 = this.resourcesProvider.getPaint(paintKey);
        return paint2 != null ? paint2 : Theme.getThemePaint(paintKey);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color2 = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color2 != null ? color2.intValue() : Theme.getColor(key);
    }
}
