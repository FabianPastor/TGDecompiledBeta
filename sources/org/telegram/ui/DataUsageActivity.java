package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.StatsController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
/* loaded from: classes3.dex */
public class DataUsageActivity extends BaseFragment {
    private static final Interpolator interpolator = DataUsageActivity$$ExternalSyntheticLambda1.INSTANCE;
    private boolean animatingForward;
    private boolean backAnimation;
    private int maximumVelocity;
    private ListAdapter mobileAdapter;
    private ListAdapter roamingAdapter;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private ListAdapter wifiAdapter;
    private Paint backgroundPaint = new Paint();
    private ViewPage[] viewPages = new ViewPage[2];
    private boolean swipeBackEnabled = true;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ViewPage extends FrameLayout {
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        private int selectedType;

        public ViewPage(DataUsageActivity dataUsageActivity, Context context) {
            super(context);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        RecyclerListView.Holder holder;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("NetworkUsage", R.string.NetworkUsage));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.DataUsageActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    DataUsageActivity.this.finishFragment();
                }
            }
        });
        this.hasOwnBackground = true;
        this.mobileAdapter = new ListAdapter(context, 0);
        this.wifiAdapter = new ListAdapter(context, 1);
        this.roamingAdapter = new ListAdapter(context, 2);
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip;
        scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() { // from class: org.telegram.ui.DataUsageActivity.2
            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageSelected(int i, boolean z2) {
                if (DataUsageActivity.this.viewPages[0].selectedType == i) {
                    return;
                }
                DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                dataUsageActivity.swipeBackEnabled = i == dataUsageActivity.scrollSlidingTextTabStrip.getFirstTabId();
                DataUsageActivity.this.viewPages[1].selectedType = i;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                DataUsageActivity.this.animatingForward = z2;
            }

            @Override // org.telegram.ui.Components.ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate
            public void onPageScrolled(float f) {
                if (f != 1.0f || DataUsageActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DataUsageActivity.this.animatingForward) {
                        DataUsageActivity.this.viewPages[0].setTranslationX((-f) * DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                        DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() - (DataUsageActivity.this.viewPages[0].getMeasuredWidth() * f));
                    } else {
                        DataUsageActivity.this.viewPages[0].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() * f);
                        DataUsageActivity.this.viewPages[1].setTranslationX((DataUsageActivity.this.viewPages[0].getMeasuredWidth() * f) - DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                    }
                    if (f != 1.0f) {
                        return;
                    }
                    ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                    DataUsageActivity.this.viewPages[1] = viewPage;
                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.DataUsageActivity.3
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z2) {
                int nextPageId = DataUsageActivity.this.scrollSlidingTextTabStrip.getNextPageId(z2);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                ((BaseFragment) DataUsageActivity.this).actionBar.setEnabled(false);
                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DataUsageActivity.this.viewPages[1].selectedType = nextPageId;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                DataUsageActivity.this.animatingForward = z2;
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                if (z2) {
                    DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DataUsageActivity.this.viewPages[1].setTranslationX(-DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                }
                return true;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChildWithMargins(((BaseFragment) DataUsageActivity.this).actionBar, i, 0, i2, 0);
                int measuredHeight = ((BaseFragment) DataUsageActivity.this).actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int i3 = 0; i3 < DataUsageActivity.this.viewPages.length; i3++) {
                    if (DataUsageActivity.this.viewPages[i3] != null && DataUsageActivity.this.viewPages[i3].listView != null) {
                        DataUsageActivity.this.viewPages[i3].listView.setPadding(0, measuredHeight, 0, AndroidUtilities.dp(4.0f));
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt != null && childAt.getVisibility() != 8 && childAt != ((BaseFragment) DataUsageActivity.this).actionBar) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (((BaseFragment) DataUsageActivity.this).parentLayout != null) {
                    ((BaseFragment) DataUsageActivity.this).parentLayout.drawHeaderShadow(canvas, ((BaseFragment) DataUsageActivity.this).actionBar.getMeasuredHeight() + ((int) ((BaseFragment) DataUsageActivity.this).actionBar.getTranslationY()));
                }
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.globalIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            public boolean checkTabsAnimationInProgress() {
                if (DataUsageActivity.this.tabsAnimationInProgress) {
                    int i = -1;
                    boolean z2 = true;
                    if (DataUsageActivity.this.backAnimation) {
                        if (Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) < 1.0f) {
                            DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                            ViewPage viewPage = DataUsageActivity.this.viewPages[1];
                            int measuredWidth = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                            if (DataUsageActivity.this.animatingForward) {
                                i = 1;
                            }
                            viewPage.setTranslationX(measuredWidth * i);
                        }
                        z2 = false;
                    } else {
                        if (Math.abs(DataUsageActivity.this.viewPages[1].getTranslationX()) < 1.0f) {
                            ViewPage viewPage2 = DataUsageActivity.this.viewPages[0];
                            int measuredWidth2 = DataUsageActivity.this.viewPages[0].getMeasuredWidth();
                            if (!DataUsageActivity.this.animatingForward) {
                                i = 1;
                            }
                            viewPage2.setTranslationX(measuredWidth2 * i);
                            DataUsageActivity.this.viewPages[1].setTranslationX(0.0f);
                        }
                        z2 = false;
                    }
                    if (z2) {
                        if (DataUsageActivity.this.tabsAnimation != null) {
                            DataUsageActivity.this.tabsAnimation.cancel();
                            DataUsageActivity.this.tabsAnimation = null;
                        }
                        DataUsageActivity.this.tabsAnimationInProgress = false;
                    }
                    return DataUsageActivity.this.tabsAnimationInProgress;
                }
                return false;
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DataUsageActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                DataUsageActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((BaseFragment) DataUsageActivity.this).actionBar.getMeasuredHeight() + ((BaseFragment) DataUsageActivity.this).actionBar.getTranslationY(), getMeasuredWidth(), getMeasuredHeight(), DataUsageActivity.this.backgroundPaint);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                float f;
                float f2;
                float measuredWidth;
                int measuredWidth2;
                boolean z2 = false;
                if (((BaseFragment) DataUsageActivity.this).parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (motionEvent != null) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                }
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.velocityTracker.clear();
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    int x = (int) (motionEvent.getX() - this.startedTrackingX);
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((DataUsageActivity.this.animatingForward && x > 0) || (!DataUsageActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                            DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.animatingForward ? DataUsageActivity.this.viewPages[0].getMeasuredWidth() : -DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                            DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                            if (x < 0) {
                                z2 = true;
                            }
                            prepareForMoving(motionEvent, z2);
                        }
                    } else if (this.startedTracking) {
                        if (DataUsageActivity.this.animatingForward) {
                            DataUsageActivity.this.viewPages[0].setTranslationX(x);
                            DataUsageActivity.this.viewPages[1].setTranslationX(DataUsageActivity.this.viewPages[0].getMeasuredWidth() + x);
                        } else {
                            DataUsageActivity.this.viewPages[0].setTranslationX(x);
                            DataUsageActivity.this.viewPages[1].setTranslationX(x - DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                        }
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, Math.abs(x) / DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, DataUsageActivity.this.maximumVelocity);
                    if (motionEvent == null || motionEvent.getAction() == 3) {
                        f = 0.0f;
                        f2 = 0.0f;
                    } else {
                        f = this.velocityTracker.getXVelocity();
                        f2 = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(f) >= 3000.0f && Math.abs(f) > Math.abs(f2)) {
                            prepareForMoving(motionEvent, f < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x2 = DataUsageActivity.this.viewPages[0].getX();
                        DataUsageActivity.this.tabsAnimation = new AnimatorSet();
                        DataUsageActivity.this.backAnimation = Math.abs(x2) < ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f) < 3500.0f || Math.abs(f) < Math.abs(f2));
                        if (!DataUsageActivity.this.backAnimation) {
                            measuredWidth = DataUsageActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, -DataUsageActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, DataUsageActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, 0.0f));
                            }
                        } else {
                            measuredWidth = Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, DataUsageActivity.this.viewPages[1].getMeasuredWidth()));
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, 0.0f), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, -DataUsageActivity.this.viewPages[1].getMeasuredWidth()));
                            }
                        }
                        DataUsageActivity.this.tabsAnimation.setInterpolator(DataUsageActivity.interpolator);
                        int measuredWidth3 = getMeasuredWidth();
                        float f3 = measuredWidth3 / 2;
                        float distanceInfluenceForSnapDuration = f3 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (measuredWidth * 1.0f) / measuredWidth3)) * f3);
                        float abs2 = Math.abs(f);
                        if (abs2 > 0.0f) {
                            measuredWidth2 = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs2) * 1000.0f) * 4;
                        } else {
                            measuredWidth2 = (int) (((measuredWidth / getMeasuredWidth()) + 1.0f) * 100.0f);
                        }
                        DataUsageActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(measuredWidth2, 600)));
                        DataUsageActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DataUsageActivity.3.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                DataUsageActivity.this.tabsAnimation = null;
                                if (DataUsageActivity.this.backAnimation) {
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                                    DataUsageActivity.this.viewPages[1] = viewPage;
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                                    dataUsageActivity.swipeBackEnabled = dataUsageActivity.viewPages[0].selectedType == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DataUsageActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass3.this.maybeStartTracking = false;
                                AnonymousClass3.this.startedTracking = false;
                                ((BaseFragment) DataUsageActivity.this).actionBar.setEnabled(true);
                                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DataUsageActivity.this.tabsAnimation.start();
                        DataUsageActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        ((BaseFragment) DataUsageActivity.this).actionBar.setEnabled(true);
                        DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = frameLayout;
        frameLayout.setWillNotDraw(false);
        int i = 0;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            if (i == 0 && viewPageArr[i] != null && viewPageArr[i].layoutManager != null) {
                i2 = this.viewPages[i].layoutManager.findFirstVisibleItemPosition();
                if (i2 == this.viewPages[i].layoutManager.getItemCount() - 1 || (holder = (RecyclerListView.Holder) this.viewPages[i].listView.findViewHolderForAdapterPosition(i2)) == null) {
                    i2 = -1;
                } else {
                    i3 = holder.itemView.getTop();
                }
            }
            ViewPage viewPage = new ViewPage(context) { // from class: org.telegram.ui.DataUsageActivity.4
                @Override // android.view.View
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (!DataUsageActivity.this.tabsAnimationInProgress || DataUsageActivity.this.viewPages[0] != this) {
                        return;
                    }
                    DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) / DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                }
            };
            frameLayout.addView(viewPage, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[i] = viewPage;
            LinearLayoutManager linearLayoutManager = viewPageArr2[i].layoutManager = new LinearLayoutManager(this, context, 1, false) { // from class: org.telegram.ui.DataUsageActivity.5
                @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            final RecyclerListView recyclerListView = new RecyclerListView(context);
            this.viewPages[i].listView = recyclerListView;
            this.viewPages[i].listView.setScrollingTouchSlop(1);
            this.viewPages[i].listView.setItemAnimator(null);
            this.viewPages[i].listView.setClipToPadding(false);
            this.viewPages[i].listView.setSectionsType(2);
            this.viewPages[i].listView.setLayoutManager(linearLayoutManager);
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[i].addView(viewPageArr3[i].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[i].listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DataUsageActivity$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i4) {
                    DataUsageActivity.this.lambda$createView$2(recyclerListView, view, i4);
                }
            });
            this.viewPages[i].listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.DataUsageActivity.6
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i4) {
                    if (i4 != 1) {
                        int i5 = (int) (-((BaseFragment) DataUsageActivity.this).actionBar.getTranslationY());
                        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (i5 == 0 || i5 == currentActionBarHeight) {
                            return;
                        }
                        if (i5 < currentActionBarHeight / 2) {
                            DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, -i5);
                        } else {
                            DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, currentActionBarHeight - i5);
                        }
                    }
                }

                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i4, int i5) {
                    if (recyclerView == DataUsageActivity.this.viewPages[0].listView) {
                        float translationY = ((BaseFragment) DataUsageActivity.this).actionBar.getTranslationY();
                        float f = translationY - i5;
                        if (f < (-ActionBar.getCurrentActionBarHeight())) {
                            f = -ActionBar.getCurrentActionBarHeight();
                        } else if (f > 0.0f) {
                            f = 0.0f;
                        }
                        if (f == translationY) {
                            return;
                        }
                        DataUsageActivity.this.setScrollY(f);
                    }
                }
            });
            if (i == 0 && i2 != -1) {
                linearLayoutManager.scrollToPositionWithOffset(i2, i3);
            }
            if (i != 0) {
                this.viewPages[i].setVisibility(8);
            }
            i++;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(RecyclerListView recyclerListView, View view, int i) {
        if (getParentActivity() == null) {
            return;
        }
        final ListAdapter listAdapter = (ListAdapter) recyclerListView.getAdapter();
        if (i != listAdapter.resetRow) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("ResetStatisticsAlertTitle", R.string.ResetStatisticsAlertTitle));
        builder.setMessage(LocaleController.getString("ResetStatisticsAlert", R.string.ResetStatisticsAlert));
        builder.setPositiveButton(LocaleController.getString("Reset", R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DataUsageActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                DataUsageActivity.this.lambda$createView$1(listAdapter, dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView == null) {
            return;
        }
        textView.setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(ListAdapter listAdapter, DialogInterface dialogInterface, int i) {
        StatsController.getInstance(this.currentAccount).resetStats(listAdapter.currentType);
        listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.mobileAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        ListAdapter listAdapter2 = this.wifiAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
        ListAdapter listAdapter3 = this.roamingAdapter;
        if (listAdapter3 != null) {
            listAdapter3.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i < viewPageArr.length) {
                viewPageArr[i].listView.setPinnedSectionOffsetY((int) f);
                i++;
            } else {
                this.fragmentView.invalidate();
                return;
            }
        }
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip == null) {
            return;
        }
        scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString("NetworkUsageMobileTab", R.string.NetworkUsageMobileTab));
        this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("NetworkUsageWiFiTab", R.string.NetworkUsageWiFiTab));
        this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("NetworkUsageRoamingTab", R.string.NetworkUsageRoamingTab));
        this.scrollSlidingTextTabStrip.setVisibility(0);
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
        if (currentTabId >= 0) {
            this.viewPages[0].selectedType = currentTabId;
        }
        this.scrollSlidingTextTabStrip.finishAddingTabs();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchToCurrentSelectedMode(boolean z) {
        ViewPage[] viewPageArr;
        int i = 0;
        while (true) {
            viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i].listView.stopScroll();
            i++;
        }
        RecyclerView.Adapter adapter = viewPageArr[z ? 1 : 0].listView.getAdapter();
        this.viewPages[z].listView.setPinnedHeaderShadowDrawable(null);
        if (this.viewPages[z].selectedType != 0) {
            if (this.viewPages[z].selectedType != 1) {
                if (this.viewPages[z].selectedType == 2 && adapter != this.roamingAdapter) {
                    this.viewPages[z].listView.setAdapter(this.roamingAdapter);
                }
            } else if (adapter != this.wifiAdapter) {
                this.viewPages[z].listView.setAdapter(this.wifiAdapter);
            }
        } else if (adapter != this.mobileAdapter) {
            this.viewPages[z].listView.setAdapter(this.mobileAdapter);
        }
        this.viewPages[z].listView.setVisibility(0);
        if (this.actionBar.getTranslationY() != 0.0f) {
            this.viewPages[z].layoutManager.scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int audiosBytesReceivedRow;
        private int audiosBytesSentRow;
        private int audiosReceivedRow;
        private int audiosSection2Row;
        private int audiosSectionRow;
        private int audiosSentRow;
        private int callsBytesReceivedRow;
        private int callsBytesSentRow;
        private int callsReceivedRow;
        private int callsSection2Row;
        private int callsSectionRow;
        private int callsSentRow;
        private int callsTotalTimeRow;
        private int currentType;
        private int filesBytesReceivedRow;
        private int filesBytesSentRow;
        private int filesReceivedRow;
        private int filesSection2Row;
        private int filesSectionRow;
        private int filesSentRow;
        private Context mContext;
        private int messagesBytesReceivedRow;
        private int messagesBytesSentRow;
        private int messagesSection2Row;
        private int messagesSectionRow;
        private int photosBytesReceivedRow;
        private int photosBytesSentRow;
        private int photosReceivedRow;
        private int photosSection2Row;
        private int photosSentRow;
        private int resetRow;
        private int resetSection2Row;
        private int rowCount;
        private int totalBytesReceivedRow;
        private int totalBytesSentRow;
        private int totalSection2Row;
        private int totalSectionRow;
        private int videosBytesReceivedRow;
        private int videosBytesSentRow;
        private int videosReceivedRow;
        private int videosSection2Row;
        private int videosSectionRow;
        private int videosSentRow;
        private int photosSectionRow = 0;
        private int messagesSentRow = -1;
        private int messagesReceivedRow = -1;

        public ListAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
            this.rowCount = 0;
            int i2 = 0 + 1;
            this.rowCount = i2;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.photosSentRow = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.photosReceivedRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.photosBytesSentRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.photosBytesReceivedRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.photosSection2Row = i6;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.videosSectionRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.videosSentRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.videosReceivedRow = i9;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.videosBytesSentRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.videosBytesReceivedRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.videosSection2Row = i12;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.audiosSectionRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.audiosSentRow = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.audiosReceivedRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.audiosBytesSentRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.audiosBytesReceivedRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.audiosSection2Row = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.filesSectionRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.filesSentRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.filesReceivedRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.filesBytesSentRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.filesBytesReceivedRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.filesSection2Row = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.callsSectionRow = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.callsSentRow = i26;
            int i28 = i27 + 1;
            this.rowCount = i28;
            this.callsReceivedRow = i27;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.callsBytesSentRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.callsBytesReceivedRow = i29;
            int i31 = i30 + 1;
            this.rowCount = i31;
            this.callsTotalTimeRow = i30;
            int i32 = i31 + 1;
            this.rowCount = i32;
            this.callsSection2Row = i31;
            int i33 = i32 + 1;
            this.rowCount = i33;
            this.messagesSectionRow = i32;
            int i34 = i33 + 1;
            this.rowCount = i34;
            this.messagesBytesSentRow = i33;
            int i35 = i34 + 1;
            this.rowCount = i35;
            this.messagesBytesReceivedRow = i34;
            int i36 = i35 + 1;
            this.rowCount = i36;
            this.messagesSection2Row = i35;
            int i37 = i36 + 1;
            this.rowCount = i37;
            this.totalSectionRow = i36;
            int i38 = i37 + 1;
            this.rowCount = i38;
            this.totalBytesSentRow = i37;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.totalBytesReceivedRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.totalSection2Row = i39;
            int i41 = i40 + 1;
            this.rowCount = i41;
            this.resetRow = i40;
            this.rowCount = i41 + 1;
            this.resetSection2Row = i41;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                if (i == this.resetSection2Row) {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    return;
                } else {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    return;
                }
            }
            int i2 = 3;
            boolean z = false;
            if (itemViewType != 1) {
                if (itemViewType != 2) {
                    if (itemViewType != 3) {
                        return;
                    }
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                    textInfoPrivacyCell.setText(LocaleController.formatString("NetworkUsageSince", R.string.NetworkUsageSince, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getResetStatsDate(this.currentType))));
                    return;
                }
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == this.totalSectionRow) {
                    headerCell.setText(LocaleController.getString("TotalDataUsage", R.string.TotalDataUsage));
                    return;
                } else if (i == this.callsSectionRow) {
                    headerCell.setText(LocaleController.getString("CallsDataUsage", R.string.CallsDataUsage));
                    return;
                } else if (i == this.filesSectionRow) {
                    headerCell.setText(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage));
                    return;
                } else if (i == this.audiosSectionRow) {
                    headerCell.setText(LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache));
                    return;
                } else if (i == this.videosSectionRow) {
                    headerCell.setText(LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache));
                    return;
                } else if (i == this.photosSectionRow) {
                    headerCell.setText(LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache));
                    return;
                } else if (i != this.messagesSectionRow) {
                    return;
                } else {
                    headerCell.setText(LocaleController.getString("MessagesDataUsage", R.string.MessagesDataUsage));
                    return;
                }
            }
            TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
            if (i == this.resetRow) {
                textSettingsCell.setTag("windowBackgroundWhiteRedText2");
                textSettingsCell.setText(LocaleController.getString("ResetStatistics", R.string.ResetStatistics), false);
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
                return;
            }
            textSettingsCell.setTag("windowBackgroundWhiteBlackText");
            textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            int i3 = this.callsSentRow;
            if (i == i3 || i == this.callsReceivedRow || i == this.callsBytesSentRow || i == this.callsBytesReceivedRow) {
                i2 = 0;
            } else if (i == this.messagesSentRow || i == this.messagesReceivedRow || i == this.messagesBytesSentRow || i == this.messagesBytesReceivedRow) {
                i2 = 1;
            } else if (i == this.photosSentRow || i == this.photosReceivedRow || i == this.photosBytesSentRow || i == this.photosBytesReceivedRow) {
                i2 = 4;
            } else if (i != this.audiosSentRow && i != this.audiosReceivedRow && i != this.audiosBytesSentRow && i != this.audiosBytesReceivedRow) {
                if (i == this.videosSentRow || i == this.videosReceivedRow || i == this.videosBytesSentRow || i == this.videosBytesReceivedRow) {
                    i2 = 2;
                } else {
                    i2 = (i == this.filesSentRow || i == this.filesReceivedRow || i == this.filesBytesSentRow || i == this.filesBytesReceivedRow) ? 5 : 6;
                }
            }
            if (i == i3) {
                textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", R.string.OutgoingCalls), String.format("%d", Integer.valueOf(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getSentItemsCount(this.currentType, i2))), true);
            } else if (i == this.callsReceivedRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", R.string.IncomingCalls), String.format("%d", Integer.valueOf(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getRecivedItemsCount(this.currentType, i2))), true);
            } else if (i == this.callsTotalTimeRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", R.string.CallsTotalTime), AndroidUtilities.formatShortDuration(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getCallsTotalTime(this.currentType)), false);
            } else if (i == this.messagesSentRow || i == this.photosSentRow || i == this.videosSentRow || i == this.audiosSentRow || i == this.filesSentRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", R.string.CountSent), String.format("%d", Integer.valueOf(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getSentItemsCount(this.currentType, i2))), true);
            } else if (i == this.messagesReceivedRow || i == this.photosReceivedRow || i == this.videosReceivedRow || i == this.audiosReceivedRow || i == this.filesReceivedRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", R.string.CountReceived), String.format("%d", Integer.valueOf(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getRecivedItemsCount(this.currentType, i2))), true);
            } else if (i == this.messagesBytesSentRow || i == this.photosBytesSentRow || i == this.videosBytesSentRow || i == this.audiosBytesSentRow || i == this.filesBytesSentRow || i == this.callsBytesSentRow || i == this.totalBytesSentRow) {
                textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", R.string.BytesSent), AndroidUtilities.formatFileSize(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getSentBytesCount(this.currentType, i2)), true);
            } else if (i != this.messagesBytesReceivedRow && i != this.photosBytesReceivedRow && i != this.videosBytesReceivedRow && i != this.audiosBytesReceivedRow && i != this.filesBytesReceivedRow && i != this.callsBytesReceivedRow && i != this.totalBytesReceivedRow) {
            } else {
                String string = LocaleController.getString("BytesReceived", R.string.BytesReceived);
                String formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(((BaseFragment) DataUsageActivity.this).currentAccount).getReceivedBytesCount(this.currentType, i2));
                if (i == this.callsBytesReceivedRow) {
                    z = true;
                }
                textSettingsCell.setTextAndValue(string, formatFileSize, z);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == this.resetRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1786onCreateViewHolder(ViewGroup viewGroup, int i) {
            View shadowSectionCell;
            if (i == 0) {
                shadowSectionCell = new ShadowSectionCell(this.mContext);
            } else if (i == 1) {
                shadowSectionCell = new TextSettingsCell(this.mContext);
                shadowSectionCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i == 2) {
                shadowSectionCell = new HeaderCell(this.mContext);
                shadowSectionCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else {
                shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
            }
            shadowSectionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(shadowSectionCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.resetSection2Row) {
                return 3;
            }
            if (i == this.callsSection2Row || i == this.filesSection2Row || i == this.audiosSection2Row || i == this.videosSection2Row || i == this.photosSection2Row || i == this.messagesSection2Row || i == this.totalSection2Row) {
                return 0;
            }
            return (i == this.totalSectionRow || i == this.callsSectionRow || i == this.filesSectionRow || i == this.audiosSectionRow || i == this.videosSectionRow || i == this.photosSectionRow || i == this.messagesSectionRow) ? 2 : 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, "actionBarTabSelector"));
        for (int i = 0; i < this.viewPages.length; i++) {
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        }
        return arrayList;
    }
}
