package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.PaddedListAdapter;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ContentPreviewViewer;

public class MentionsContainerView extends BlurredFrameLayout {
    /* access modifiers changed from: private */
    public MentionsAdapter adapter;
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

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateListViewTranslation$3(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
    }

    /* access modifiers changed from: protected */
    public boolean canOpen() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onClose() {
    }

    /* access modifiers changed from: protected */
    public void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
    }

    /* access modifiers changed from: protected */
    public void onContextSearch(boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onOpen() {
    }

    public void onPanTransitionEnd() {
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MentionsContainerView(android.content.Context r17, long r18, int r20, org.telegram.ui.ChatActivity r21, org.telegram.ui.ActionBar.Theme.ResourcesProvider r22) {
        /*
            r16 = this;
            r6 = r16
            r8 = r17
            r7 = r21
            r14 = r22
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.contentView
            r6.<init>(r8, r0)
            r15 = 0
            r6.shouldLiftMentions = r15
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            r6.rect = r0
            r6.ignoreLayout = r15
            r6.scrollToFirst = r15
            r6.shown = r15
            org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda3 r0 = new org.telegram.ui.Components.MentionsContainerView$$ExternalSyntheticLambda3
            r0.<init>(r6)
            r6.updateVisibilityRunnable = r0
            r6.listViewHiding = r15
            r0 = 0
            r6.hideT = r0
            r6.switchLayoutManagerOnEnd = r15
            r6.chatActivity = r7
            org.telegram.ui.Components.SizeNotifierFrameLayout r0 = r7.contentView
            r6.sizeNotifierFrameLayout = r0
            r6.resourcesProvider = r14
            r6.drawBlur = r15
            r6.isTopView = r15
            r0 = 8
            r6.setVisibility(r0)
            r6.setWillNotDraw(r15)
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
            r0.<init>(r8, r14)
            r6.listView = r0
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            r0.setTranslationY(r1)
            org.telegram.ui.Components.MentionsContainerView$1 r0 = new org.telegram.ui.Components.MentionsContainerView$1
            r0.<init>(r8)
            r6.linearLayoutManager = r0
            r1 = 1
            r0.setOrientation(r1)
            org.telegram.ui.Components.MentionsContainerView$2 r9 = new org.telegram.ui.Components.MentionsContainerView$2
            r3 = 100
            r4 = 0
            r5 = 0
            r0 = r9
            r1 = r16
            r2 = r17
            r0.<init>(r2, r3, r4, r5)
            r6.gridLayoutManager = r9
            org.telegram.ui.Components.MentionsContainerView$3 r0 = new org.telegram.ui.Components.MentionsContainerView$3
            r0.<init>()
            r9.setSpanSizeLookup(r0)
            androidx.recyclerview.widget.DefaultItemAnimator r0 = new androidx.recyclerview.widget.DefaultItemAnimator
            r0.<init>()
            r1 = 150(0x96, double:7.4E-322)
            r0.setAddDuration(r1)
            r0.setMoveDuration(r1)
            r0.setChangeDuration(r1)
            r0.setRemoveDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r1)
            r0.setDelayAnimations(r15)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r1 = r6.listView
            r1.setItemAnimator(r0)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r0 = r6.listView
            r0.setClipToPadding(r15)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r0 = r6.listView
            androidx.recyclerview.widget.LinearLayoutManager r1 = r6.linearLayoutManager
            r0.setLayoutManager(r1)
            org.telegram.ui.Adapters.MentionsAdapter r0 = new org.telegram.ui.Adapters.MentionsAdapter
            org.telegram.ui.Components.MentionsContainerView$4 r13 = new org.telegram.ui.Components.MentionsContainerView$4
            r13.<init>(r7)
            r9 = 0
            r7 = r0
            r10 = r18
            r12 = r20
            r7.<init>(r8, r9, r10, r12, r13, r14)
            r6.adapter = r0
            org.telegram.ui.Adapters.PaddedListAdapter r1 = new org.telegram.ui.Adapters.PaddedListAdapter
            r1.<init>(r0)
            r6.paddedAdapter = r1
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r0 = r6.listView
            r0.setAdapter(r1)
            org.telegram.ui.Components.MentionsContainerView$MentionsListView r0 = r6.listView
            r1 = -1
            r2 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2)
            r6.addView(r0, r1)
            r6.setReversed(r15)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.MentionsContainerView.<init>(android.content.Context, long, int, org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public void onPanTransitionStart() {
        this.shouldLiftMentions = isReversed();
    }

    public void onPanTransitionUpdate(float f) {
        if (this.shouldLiftMentions) {
            setTranslationY(f);
        }
    }

    public MentionsListView getListView() {
        return this.listView;
    }

    public MentionsAdapter getAdapter() {
        return this.adapter;
    }

    public void setReversed(boolean z) {
        this.scrollToFirst = true;
        this.linearLayoutManager.setReverseLayout(z);
        this.adapter.setIsReversed(z);
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
        float f;
        boolean isReversed = isReversed();
        this.containerPadding = (float) AndroidUtilities.dp((float) (((this.adapter.isStickers() || this.adapter.isBotContext()) && this.adapter.isMediaLayout() && this.adapter.getBotContextSwitch() == null ? 2 : 0) + 2));
        float dp = (float) AndroidUtilities.dp(4.0f);
        if (isReversed) {
            PaddedListAdapter paddedListAdapter = this.paddedAdapter;
            float min = Math.min(Math.max(0.0f, ((float) (paddedListAdapter.paddingViewAttached ? paddedListAdapter.paddingView.getTop() : getHeight())) + this.listView.getTranslationY()) + this.containerPadding, (1.0f - this.hideT) * ((float) getHeight()));
            Rect rect2 = this.rect;
            this.containerTop = 0.0f;
            int measuredWidth = getMeasuredWidth();
            this.containerBottom = min;
            rect2.set(0, (int) 0.0f, measuredWidth, (int) min);
            f = Math.min(dp, Math.abs(((float) getMeasuredHeight()) - this.containerBottom));
            if (f > 0.0f) {
                this.rect.top -= (int) f;
            }
        } else {
            if (this.listView.getLayoutManager() == this.gridLayoutManager) {
                this.containerPadding += (float) AndroidUtilities.dp(2.0f);
                dp += (float) AndroidUtilities.dp(2.0f);
            }
            PaddedListAdapter paddedListAdapter2 = this.paddedAdapter;
            float max = Math.max(0.0f, ((float) (paddedListAdapter2.paddingViewAttached ? paddedListAdapter2.paddingView.getBottom() : 0)) + this.listView.getTranslationY()) - this.containerPadding;
            this.containerTop = max;
            float max2 = Math.max(max, this.hideT * ((float) getHeight()));
            Rect rect3 = this.rect;
            this.containerTop = max2;
            int measuredWidth2 = getMeasuredWidth();
            float measuredHeight = (float) getMeasuredHeight();
            this.containerBottom = measuredHeight;
            rect3.set(0, (int) max2, measuredWidth2, (int) measuredHeight);
            f = Math.min(dp, Math.abs(this.containerTop));
            if (f > 0.0f) {
                this.rect.bottom += (int) f;
            }
        }
        float f2 = f;
        if (this.paint == null) {
            Paint paint2 = new Paint(1);
            this.paint = paint2;
            paint2.setShadowLayer((float) AndroidUtilities.dp(4.0f), 0.0f, 0.0f, NUM);
        }
        Paint paint3 = this.paint;
        Integer num = this.color;
        paint3.setColor(num != null ? num.intValue() : getThemedColor("chat_messagePanelBackground"));
        if (!SharedConfig.chatBlurEnabled() || this.sizeNotifierFrameLayout == null) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.rect);
            canvas.drawRoundRect(rectF, f2, f2, this.paint);
        } else {
            if (f2 > 0.0f) {
                canvas.save();
                Path path2 = this.path;
                if (path2 == null) {
                    this.path = new Path();
                } else {
                    path2.reset();
                }
                RectF rectF2 = AndroidUtilities.rectTmp;
                rectF2.set(this.rect);
                this.path.addRoundRect(rectF2, f2, f2, Path.Direction.CW);
                canvas.clipPath(this.path);
            }
            this.sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rect, this.paint, isReversed);
            if (f2 > 0.0f) {
                canvas.restore();
            }
        }
        canvas.save();
        canvas.clipRect(this.rect);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    public void setOverrideColor(int i) {
        this.color = Integer.valueOf(i);
        invalidate();
    }

    public void setIgnoreLayout(boolean z) {
        this.ignoreLayout = z;
    }

    public void requestLayout() {
        if (!this.ignoreLayout) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateListViewTranslation(!this.shown, true);
    }

    public void updateVisibility(boolean z) {
        if (z) {
            boolean isReversed = isReversed();
            if (!this.shown) {
                this.scrollToFirst = true;
                RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
                LinearLayoutManager linearLayoutManager2 = this.linearLayoutManager;
                if (layoutManager == linearLayoutManager2) {
                    linearLayoutManager2.scrollToPositionWithOffset(0, isReversed ? -100000 : 100000);
                }
                if (getVisibility() == 8) {
                    this.hideT = 1.0f;
                    MentionsListView mentionsListView = this.listView;
                    mentionsListView.setTranslationY(isReversed ? -(this.listViewPadding + ((float) AndroidUtilities.dp(12.0f))) : ((float) mentionsListView.computeVerticalScrollOffset()) + this.listViewPadding);
                }
            }
            setVisibility(0);
        } else {
            this.scrollToFirst = false;
        }
        this.shown = z;
        AndroidUtilities.cancelRunOnUIThread(this.updateVisibilityRunnable);
        SpringAnimation springAnimation = this.listViewTranslationAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        AndroidUtilities.runOnUIThread(this.updateVisibilityRunnable, this.chatActivity.fragmentOpened ? 0 : 100);
        if (z) {
            onOpen();
        } else {
            onClose();
        }
    }

    public boolean isOpen() {
        return this.shown;
    }

    private void updateListViewTranslation(boolean z, boolean z2) {
        float f;
        SpringAnimation springAnimation;
        if (this.listView != null && this.paddedAdapter != null) {
            if (!this.listViewHiding || (springAnimation = this.listViewTranslationAnimator) == null || !springAnimation.isRunning() || !z) {
                boolean isReversed = isReversed();
                if (z) {
                    f = (-this.containerPadding) - ((float) AndroidUtilities.dp(6.0f));
                } else {
                    f = ((float) (this.listView.computeVerticalScrollRange() - this.paddedAdapter.getPadding())) + this.containerPadding;
                }
                float f2 = 0.0f;
                float f3 = this.listViewPadding;
                float max = isReversed ? -Math.max(0.0f, f3 - f) : Math.max(0.0f, f3 - f) + (-f3);
                if (z && !isReversed) {
                    max += (float) this.listView.computeVerticalScrollOffset();
                }
                float f4 = max;
                setVisibility(0);
                SpringAnimation springAnimation2 = this.listViewTranslationAnimator;
                if (springAnimation2 != null) {
                    springAnimation2.cancel();
                }
                int i = 8;
                if (z2) {
                    this.listViewHiding = z;
                    float translationY = this.listView.getTranslationY();
                    float f5 = this.hideT;
                    float f6 = z ? 1.0f : 0.0f;
                    if (translationY == f4) {
                        this.listViewTranslationAnimator = null;
                        if (!z) {
                            i = 0;
                        }
                        setVisibility(i);
                        if (this.switchLayoutManagerOnEnd && z) {
                            this.switchLayoutManagerOnEnd = false;
                            this.listView.setLayoutManager(getNeededLayoutManager());
                            this.shown = true;
                            updateVisibility(true);
                            return;
                        }
                        return;
                    }
                    int i2 = UserConfig.selectedAccount;
                    SpringAnimation spring = new SpringAnimation(new FloatValueHolder(translationY)).setSpring(new SpringForce(f4).setDampingRatio(1.0f).setStiffness(550.0f));
                    this.listViewTranslationAnimator = spring;
                    spring.addUpdateListener(new MentionsContainerView$$ExternalSyntheticLambda2(this, f5, f6, translationY, f4));
                    if (z) {
                        this.listViewTranslationAnimator.addEndListener(new MentionsContainerView$$ExternalSyntheticLambda0(this, z));
                    }
                    this.listViewTranslationAnimator.addEndListener(MentionsContainerView$$ExternalSyntheticLambda1.INSTANCE);
                    this.listViewTranslationAnimator.start();
                    return;
                }
                if (z) {
                    f2 = 1.0f;
                }
                this.hideT = f2;
                this.listView.setTranslationY(f4);
                if (z) {
                    setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateListViewTranslation$1(float f, float f2, float f3, float f4, DynamicAnimation dynamicAnimation, float f5, float f6) {
        this.listView.setTranslationY(f5);
        this.hideT = AndroidUtilities.lerp(f, f2, (f5 - f3) / (f4 - f3));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateListViewTranslation$2(boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (!z2) {
            this.listViewTranslationAnimator = null;
            setVisibility(z ? 8 : 0);
            if (this.switchLayoutManagerOnEnd && z) {
                this.switchLayoutManagerOnEnd = false;
                this.listView.setLayoutManager(getNeededLayoutManager());
                this.shown = true;
                updateVisibility(true);
            }
        }
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
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    boolean z = false;
                    boolean unused = MentionsListView.this.isScrolling = i != 0;
                    MentionsListView mentionsListView = MentionsListView.this;
                    if (i == 1) {
                        z = true;
                    }
                    boolean unused2 = mentionsListView.isDragging = z;
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    int i3;
                    if (MentionsListView.this.getLayoutManager() == MentionsContainerView.this.gridLayoutManager) {
                        i3 = MentionsContainerView.this.gridLayoutManager.findLastVisibleItemPosition();
                    } else {
                        i3 = MentionsContainerView.this.linearLayoutManager.findLastVisibleItemPosition();
                    }
                    if ((i3 == -1 ? 0 : i3) > 0 && i3 > MentionsContainerView.this.adapter.getLastItemCount() - 5) {
                        MentionsContainerView.this.adapter.searchForContextBotForNextOffset();
                    }
                }
            });
            addItemDecoration(new RecyclerView.ItemDecoration(MentionsContainerView.this) {
                public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                    int childAdapterPosition;
                    int i = 0;
                    rect.left = 0;
                    rect.right = 0;
                    rect.top = 0;
                    rect.bottom = 0;
                    if (recyclerView.getLayoutManager() == MentionsContainerView.this.gridLayoutManager && (childAdapterPosition = recyclerView.getChildAdapterPosition(view)) != 0) {
                        int i2 = childAdapterPosition - 1;
                        if (!MentionsContainerView.this.adapter.isStickers()) {
                            if (MentionsContainerView.this.adapter.getBotContextSwitch() == null) {
                                rect.top = AndroidUtilities.dp(2.0f);
                            } else if (i2 != 0) {
                                i2--;
                                if (!MentionsContainerView.this.gridLayoutManager.isFirstRow(i2)) {
                                    rect.top = AndroidUtilities.dp(2.0f);
                                }
                            } else {
                                return;
                            }
                            if (!MentionsContainerView.this.gridLayoutManager.isLastInRow(i2)) {
                                i = AndroidUtilities.dp(2.0f);
                            }
                            rect.right = i;
                        }
                    }
                }
            });
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            boolean z;
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() > ((float) MentionsContainerView.this.paddedAdapter.paddingView.getTop())) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() < ((float) MentionsContainerView.this.paddedAdapter.paddingView.getBottom())) {
                return false;
            }
            if (!this.isScrolling) {
                if (ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, MentionsContainerView.this.listView, 0, (ContentPreviewViewer.ContentPreviewViewerDelegate) null, this.resourcesProvider)) {
                    z = true;
                    if ((MentionsContainerView.this.adapter.isStickers() && motionEvent.getAction() == 0) || motionEvent.getAction() == 2) {
                        MentionsContainerView.this.adapter.doSomeStickersAction();
                    }
                    if (!super.onInterceptTouchEvent(motionEvent) || z) {
                        return true;
                    }
                    return false;
                }
            }
            z = false;
            MentionsContainerView.this.adapter.doSomeStickersAction();
            if (!super.onInterceptTouchEvent(motionEvent)) {
            }
            return true;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (MentionsContainerView.this.linearLayoutManager.getReverseLayout()) {
                if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() > ((float) MentionsContainerView.this.paddedAdapter.paddingView.getTop())) {
                    return false;
                }
            } else if (!this.isDragging && MentionsContainerView.this.paddedAdapter != null && MentionsContainerView.this.paddedAdapter.paddingView != null && MentionsContainerView.this.paddedAdapter.paddingViewAttached && motionEvent.getY() < ((float) MentionsContainerView.this.paddedAdapter.paddingView.getBottom())) {
                return false;
            }
            return super.onTouchEvent(motionEvent);
        }

        public void requestLayout() {
            if (!MentionsContainerView.this.ignoreLayout) {
                super.requestLayout();
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int i7 = i4 - i2;
            boolean isReversed = MentionsContainerView.this.isReversed();
            LinearLayoutManager currentLayoutManager = MentionsContainerView.this.getCurrentLayoutManager();
            int findFirstVisibleItemPosition = isReversed ? currentLayoutManager.findFirstVisibleItemPosition() : currentLayoutManager.findLastVisibleItemPosition();
            View findViewByPosition = currentLayoutManager.findViewByPosition(findFirstVisibleItemPosition);
            if (findViewByPosition != null) {
                i5 = findViewByPosition.getTop() - (isReversed ? 0 : this.lastHeight - i7);
            } else {
                i5 = 0;
            }
            super.onLayout(z, i, i2, i3, i4);
            if (MentionsContainerView.this.scrollToFirst) {
                boolean unused = MentionsContainerView.this.ignoreLayout = true;
                currentLayoutManager.scrollToPositionWithOffset(0, 100000);
                super.onLayout(false, i, i2, i3, i4);
                boolean unused2 = MentionsContainerView.this.ignoreLayout = false;
                boolean unused3 = MentionsContainerView.this.scrollToFirst = false;
            } else if (!(findFirstVisibleItemPosition == -1 || i6 != this.lastWidth || i7 - this.lastHeight == 0)) {
                boolean unused4 = MentionsContainerView.this.ignoreLayout = true;
                currentLayoutManager.scrollToPositionWithOffset(findFirstVisibleItemPosition, i5, false);
                super.onLayout(false, i, i2, i3, i4);
                boolean unused5 = MentionsContainerView.this.ignoreLayout = false;
            }
            this.lastHeight = i7;
            this.lastWidth = i6;
        }

        public void setTranslationY(float f) {
            super.setTranslationY(f);
            MentionsContainerView.this.invalidate();
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i2);
            if (MentionsContainerView.this.paddedAdapter != null) {
                MentionsContainerView.this.paddedAdapter.setPadding(size);
            }
            float unused = MentionsContainerView.this.listViewPadding = (float) ((int) Math.min((float) AndroidUtilities.dp(126.0f), ((float) AndroidUtilities.displaySize.y) * 0.22f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size + ((int) MentionsContainerView.this.listViewPadding), NUM));
        }

        public void onScrolled(int i, int i2) {
            super.onScrolled(i, i2);
            MentionsContainerView.this.invalidate();
        }
    }

    private int getThemedColor(String str) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color2 = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
        return color2 != null ? color2.intValue() : Theme.getColor(str);
    }
}
