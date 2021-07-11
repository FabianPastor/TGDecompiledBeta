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
import org.telegram.ui.DataUsageActivity;

public class DataUsageActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public static final Interpolator interpolator = $$Lambda$DataUsageActivity$P_yBFhbppGMoCdEu34gwLaeJwR4.INSTANCE;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    /* access modifiers changed from: private */
    public Paint backgroundPaint = new Paint();
    /* access modifiers changed from: private */
    public int maximumVelocity;
    private ListAdapter mobileAdapter;
    private ListAdapter roamingAdapter;
    /* access modifiers changed from: private */
    public ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    /* access modifiers changed from: private */
    public boolean swipeBackEnabled = true;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    /* access modifiers changed from: private */
    public ViewPage[] viewPages = new ViewPage[2];
    private ListAdapter wifiAdapter;

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    private class ViewPage extends FrameLayout {
        /* access modifiers changed from: private */
        public LinearLayoutManager layoutManager;
        /* access modifiers changed from: private */
        public RecyclerListView listView;
        /* access modifiers changed from: private */
        public int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    public View createView(Context context) {
        RecyclerListView.Holder holder;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("NetworkUsage", NUM));
        boolean z = false;
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
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
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip2;
        scrollSlidingTextTabStrip2.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            public /* synthetic */ void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }

            public void onPageSelected(int i, boolean z) {
                if (DataUsageActivity.this.viewPages[0].selectedType != i) {
                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                    boolean unused = dataUsageActivity.swipeBackEnabled = i == dataUsageActivity.scrollSlidingTextTabStrip.getFirstTabId();
                    int unused2 = DataUsageActivity.this.viewPages[1].selectedType = i;
                    DataUsageActivity.this.viewPages[1].setVisibility(0);
                    DataUsageActivity.this.switchToCurrentSelectedMode(true);
                    boolean unused3 = DataUsageActivity.this.animatingForward = z;
                }
            }

            public void onPageScrolled(float f) {
                if (f != 1.0f || DataUsageActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DataUsageActivity.this.animatingForward) {
                        DataUsageActivity.this.viewPages[0].setTranslationX((-f) * ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        DataUsageActivity.this.viewPages[1].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - (((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f));
                    } else {
                        DataUsageActivity.this.viewPages[0].setTranslationX(((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f);
                        DataUsageActivity.this.viewPages[1].setTranslationX((((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) * f) - ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                    if (f == 1.0f) {
                        ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                        DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                        DataUsageActivity.this.viewPages[1] = viewPage;
                        DataUsageActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        AnonymousClass3 r0 = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            /* access modifiers changed from: private */
            public boolean maybeStartTracking;
            /* access modifiers changed from: private */
            public boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
                int nextPageId = DataUsageActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                DataUsageActivity.this.actionBar.setEnabled(false);
                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                int unused = DataUsageActivity.this.viewPages[1].selectedType = nextPageId;
                DataUsageActivity.this.viewPages[1].setVisibility(0);
                boolean unused2 = DataUsageActivity.this.animatingForward = z;
                DataUsageActivity.this.switchToCurrentSelectedMode(true);
                if (z) {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth());
                } else {
                    DataUsageActivity.this.viewPages[1].setTranslationX((float) (-DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                }
                return true;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChildWithMargins(DataUsageActivity.this.actionBar, i, 0, i2, 0);
                int measuredHeight = DataUsageActivity.this.actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int i3 = 0; i3 < DataUsageActivity.this.viewPages.length; i3++) {
                    if (!(DataUsageActivity.this.viewPages[i3] == null || DataUsageActivity.this.viewPages[i3].listView == null)) {
                        DataUsageActivity.this.viewPages[i3].listView.setPadding(0, measuredHeight, 0, AndroidUtilities.dp(4.0f));
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DataUsageActivity.this.actionBar)) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                }
            }

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DataUsageActivity.this.parentLayout != null) {
                    DataUsageActivity.this.parentLayout.drawHeaderShadow(canvas, DataUsageActivity.this.actionBar.getMeasuredHeight() + ((int) DataUsageActivity.this.actionBar.getTranslationY()));
                }
            }

            public void requestLayout() {
                if (!this.globalIgnoreLayout) {
                    super.requestLayout();
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:18:0x00a0  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public boolean checkTabsAnimationInProgress() {
                /*
                    r7 = this;
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    r1 = 0
                    if (r0 == 0) goto L_0x00c3
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    boolean r0 = r0.backAnimation
                    r2 = -1
                    r3 = 0
                    r4 = 1065353216(0x3var_, float:1.0)
                    r5 = 1
                    if (r0 == 0) goto L_0x0059
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    r0.setTranslationX(r3)
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    org.telegram.ui.DataUsageActivity r3 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r3 = r3.viewPages
                    r3 = r3[r1]
                    int r3 = r3.getMeasuredWidth()
                    org.telegram.ui.DataUsageActivity r4 = org.telegram.ui.DataUsageActivity.this
                    boolean r4 = r4.animatingForward
                    if (r4 == 0) goto L_0x0052
                    r2 = 1
                L_0x0052:
                    int r3 = r3 * r2
                    float r2 = (float) r3
                    r0.setTranslationX(r2)
                    goto L_0x009e
                L_0x0059:
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    float r0 = r0.getTranslationX()
                    float r0 = java.lang.Math.abs(r0)
                    int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r0 >= 0) goto L_0x009d
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r1]
                    org.telegram.ui.DataUsageActivity r4 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r4 = r4.viewPages
                    r4 = r4[r1]
                    int r4 = r4.getMeasuredWidth()
                    org.telegram.ui.DataUsageActivity r6 = org.telegram.ui.DataUsageActivity.this
                    boolean r6 = r6.animatingForward
                    if (r6 == 0) goto L_0x008a
                    goto L_0x008b
                L_0x008a:
                    r2 = 1
                L_0x008b:
                    int r4 = r4 * r2
                    float r2 = (float) r4
                    r0.setTranslationX(r2)
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    org.telegram.ui.DataUsageActivity$ViewPage[] r0 = r0.viewPages
                    r0 = r0[r5]
                    r0.setTranslationX(r3)
                    goto L_0x009e
                L_0x009d:
                    r5 = 0
                L_0x009e:
                    if (r5 == 0) goto L_0x00bc
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    if (r0 == 0) goto L_0x00b7
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    android.animation.AnimatorSet r0 = r0.tabsAnimation
                    r0.cancel()
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    r2 = 0
                    android.animation.AnimatorSet unused = r0.tabsAnimation = r2
                L_0x00b7:
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    boolean unused = r0.tabsAnimationInProgress = r1
                L_0x00bc:
                    org.telegram.ui.DataUsageActivity r0 = org.telegram.ui.DataUsageActivity.this
                    boolean r0 = r0.tabsAnimationInProgress
                    return r0
                L_0x00c3:
                    return r1
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataUsageActivity.AnonymousClass3.checkTabsAnimationInProgress():boolean");
            }

            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DataUsageActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                DataUsageActivity.this.backgroundPaint.setColor(Theme.getColor("windowBackgroundGray"));
                canvas.drawRect(0.0f, ((float) DataUsageActivity.this.actionBar.getMeasuredHeight()) + DataUsageActivity.this.actionBar.getTranslationY(), (float) getMeasuredWidth(), (float) getMeasuredHeight(), DataUsageActivity.this.backgroundPaint);
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                float f;
                float f2;
                float f3;
                int i;
                boolean z = false;
                if (DataUsageActivity.this.parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
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
                    int x = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((DataUsageActivity.this.animatingForward && x > 0) || (!DataUsageActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DataUsageActivity.this.viewPages[0].setTranslationX(0.0f);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (DataUsageActivity.this.animatingForward ? DataUsageActivity.this.viewPages[0].getMeasuredWidth() : -DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                            DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (this.maybeStartTracking && !this.startedTracking) {
                        if (((float) Math.abs(x)) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                            if (x < 0) {
                                z = true;
                            }
                            prepareForMoving(motionEvent, z);
                        }
                    } else if (this.startedTracking) {
                        if (DataUsageActivity.this.animatingForward) {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) x);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (DataUsageActivity.this.viewPages[0].getMeasuredWidth() + x));
                        } else {
                            DataUsageActivity.this.viewPages[0].setTranslationX((float) x);
                            DataUsageActivity.this.viewPages[1].setTranslationX((float) (x - DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                        }
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, ((float) Math.abs(x)) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, (float) DataUsageActivity.this.maximumVelocity);
                    if (motionEvent == null || motionEvent.getAction() == 3) {
                        f2 = 0.0f;
                        f = 0.0f;
                    } else {
                        f2 = this.velocityTracker.getXVelocity();
                        f = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(f2) >= 3000.0f && Math.abs(f2) > Math.abs(f)) {
                            prepareForMoving(motionEvent, f2 < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x2 = DataUsageActivity.this.viewPages[0].getX();
                        AnimatorSet unused = DataUsageActivity.this.tabsAnimation = new AnimatorSet();
                        boolean unused2 = DataUsageActivity.this.backAnimation = Math.abs(x2) < ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                        if (DataUsageActivity.this.backAnimation) {
                            f3 = Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) DataUsageActivity.this.viewPages[1].getMeasuredWidth()})});
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-DataUsageActivity.this.viewPages[1].getMeasuredWidth())})});
                            }
                        } else {
                            f3 = ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                            if (DataUsageActivity.this.animatingForward) {
                                DataUsageActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-DataUsageActivity.this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            } else {
                                DataUsageActivity.this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[0], View.TRANSLATION_X, new float[]{(float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(DataUsageActivity.this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                            }
                        }
                        DataUsageActivity.this.tabsAnimation.setInterpolator(DataUsageActivity.interpolator);
                        int measuredWidth = getMeasuredWidth();
                        float f4 = (float) (measuredWidth / 2);
                        float distanceInfluenceForSnapDuration = f4 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (f3 * 1.0f) / ((float) measuredWidth))) * f4);
                        float abs2 = Math.abs(f2);
                        if (abs2 > 0.0f) {
                            i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs2) * 1000.0f) * 4;
                        } else {
                            i = (int) (((f3 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                        }
                        DataUsageActivity.this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                        DataUsageActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                AnimatorSet unused = DataUsageActivity.this.tabsAnimation = null;
                                if (DataUsageActivity.this.backAnimation) {
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage = DataUsageActivity.this.viewPages[0];
                                    DataUsageActivity.this.viewPages[0] = DataUsageActivity.this.viewPages[1];
                                    DataUsageActivity.this.viewPages[1] = viewPage;
                                    DataUsageActivity.this.viewPages[1].setVisibility(8);
                                    DataUsageActivity dataUsageActivity = DataUsageActivity.this;
                                    boolean unused2 = dataUsageActivity.swipeBackEnabled = dataUsageActivity.viewPages[0].selectedType == DataUsageActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                boolean unused3 = DataUsageActivity.this.tabsAnimationInProgress = false;
                                boolean unused4 = AnonymousClass3.this.maybeStartTracking = false;
                                boolean unused5 = AnonymousClass3.this.startedTracking = false;
                                DataUsageActivity.this.actionBar.setEnabled(true);
                                DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DataUsageActivity.this.tabsAnimation.start();
                        boolean unused3 = DataUsageActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        DataUsageActivity.this.actionBar.setEnabled(true);
                        DataUsageActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker2 = this.velocityTracker;
                    if (velocityTracker2 != null) {
                        velocityTracker2.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }
        };
        this.fragmentView = r0;
        r0.setWillNotDraw(false);
        int i = 0;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            if (!(i != 0 || viewPageArr[i] == null || viewPageArr[i].layoutManager == null)) {
                i2 = this.viewPages[i].layoutManager.findFirstVisibleItemPosition();
                if (i2 == this.viewPages[i].layoutManager.getItemCount() - 1 || (holder = (RecyclerListView.Holder) this.viewPages[i].listView.findViewHolderForAdapterPosition(i2)) == null) {
                    i2 = -1;
                } else {
                    i3 = holder.itemView.getTop();
                }
            }
            AnonymousClass4 r8 = new ViewPage(context) {
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (DataUsageActivity.this.tabsAnimationInProgress && DataUsageActivity.this.viewPages[0] == this) {
                        DataUsageActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DataUsageActivity.this.viewPages[1].selectedType, Math.abs(DataUsageActivity.this.viewPages[0].getTranslationX()) / ((float) DataUsageActivity.this.viewPages[0].getMeasuredWidth()));
                    }
                }
            };
            r0.addView(r8, LayoutHelper.createFrame(-1, -1.0f));
            ViewPage[] viewPageArr2 = this.viewPages;
            viewPageArr2[i] = r8;
            LinearLayoutManager access$2802 = viewPageArr2[i].layoutManager = new LinearLayoutManager(context, 1, false) {
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            RecyclerListView recyclerListView = new RecyclerListView(context);
            RecyclerListView unused = this.viewPages[i].listView = recyclerListView;
            this.viewPages[i].listView.setScrollingTouchSlop(1);
            this.viewPages[i].listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.viewPages[i].listView.setClipToPadding(false);
            this.viewPages[i].listView.setSectionsType(2);
            this.viewPages[i].listView.setLayoutManager(access$2802);
            ViewPage[] viewPageArr3 = this.viewPages;
            viewPageArr3[i].addView(viewPageArr3[i].listView, LayoutHelper.createFrame(-1, -1.0f));
            this.viewPages[i].listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(recyclerListView) {
                public final /* synthetic */ RecyclerListView f$1;

                {
                    this.f$1 = r2;
                }

                public final void onItemClick(View view, int i) {
                    DataUsageActivity.this.lambda$createView$2$DataUsageActivity(this.f$1, view, i);
                }
            });
            this.viewPages[i].listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    if (i != 1) {
                        int i2 = (int) (-DataUsageActivity.this.actionBar.getTranslationY());
                        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                        if (i2 != 0 && i2 != currentActionBarHeight) {
                            if (i2 < currentActionBarHeight / 2) {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, -i2);
                            } else {
                                DataUsageActivity.this.viewPages[0].listView.smoothScrollBy(0, currentActionBarHeight - i2);
                            }
                        }
                    }
                }

                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    if (recyclerView == DataUsageActivity.this.viewPages[0].listView) {
                        float translationY = DataUsageActivity.this.actionBar.getTranslationY();
                        float f = translationY - ((float) i2);
                        if (f < ((float) (-ActionBar.getCurrentActionBarHeight()))) {
                            f = (float) (-ActionBar.getCurrentActionBarHeight());
                        } else if (f > 0.0f) {
                            f = 0.0f;
                        }
                        if (f != translationY) {
                            DataUsageActivity.this.setScrollY(f);
                        }
                    }
                }
            });
            if (i == 0 && i2 != -1) {
                access$2802.scrollToPositionWithOffset(i2, i3);
            }
            if (i != 0) {
                this.viewPages[i].setVisibility(8);
            }
            i++;
        }
        r0.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        if (this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId()) {
            z = true;
        }
        this.swipeBackEnabled = z;
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$DataUsageActivity(RecyclerListView recyclerListView, View view, int i) {
        if (getParentActivity() != null) {
            ListAdapter listAdapter = (ListAdapter) recyclerListView.getAdapter();
            if (i == listAdapter.resetRow) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("ResetStatisticsAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("ResetStatisticsAlert", NUM));
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener(listAdapter) {
                    public final /* synthetic */ DataUsageActivity.ListAdapter f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataUsageActivity.this.lambda$null$1$DataUsageActivity(this.f$1, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$1 */
    public /* synthetic */ void lambda$null$1$DataUsageActivity(ListAdapter listAdapter, DialogInterface dialogInterface, int i) {
        StatsController.getInstance(this.currentAccount).resetStats(listAdapter.currentType);
        listAdapter.notifyDataSetChanged();
    }

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

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    /* access modifiers changed from: private */
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
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip2 = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip2 != null) {
            scrollSlidingTextTabStrip2.addTextTab(0, LocaleController.getString("NetworkUsageMobileTab", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString("NetworkUsageWiFiTab", NUM));
            this.scrollSlidingTextTabStrip.addTextTab(2, LocaleController.getString("NetworkUsageRoamingTab", NUM));
            this.scrollSlidingTextTabStrip.setVisibility(0);
            this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
            int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
            if (currentTabId >= 0) {
                int unused = this.viewPages[0].selectedType = currentTabId;
            }
            this.scrollSlidingTextTabStrip.finishAddingTabs();
        }
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [boolean] */
    /* access modifiers changed from: private */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void switchToCurrentSelectedMode(boolean r5) {
        /*
            r4 = this;
            r0 = 0
            r1 = 0
        L_0x0002:
            org.telegram.ui.DataUsageActivity$ViewPage[] r2 = r4.viewPages
            int r3 = r2.length
            if (r1 >= r3) goto L_0x0013
            r2 = r2[r1]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r2.stopScroll()
            int r1 = r1 + 1
            goto L_0x0002
        L_0x0013:
            r1 = r2[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            androidx.recyclerview.widget.RecyclerView$Adapter r1 = r1.getAdapter()
            org.telegram.ui.DataUsageActivity$ViewPage[] r2 = r4.viewPages
            r2 = r2[r5]
            org.telegram.ui.Components.RecyclerListView r2 = r2.listView
            r3 = 0
            r2.setPinnedHeaderShadowDrawable(r3)
            org.telegram.ui.DataUsageActivity$ViewPage[] r2 = r4.viewPages
            r2 = r2[r5]
            int r2 = r2.selectedType
            if (r2 != 0) goto L_0x0045
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.mobileAdapter
            if (r1 == r2) goto L_0x007e
            org.telegram.ui.DataUsageActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.mobileAdapter
            r1.setAdapter(r2)
            goto L_0x007e
        L_0x0045:
            org.telegram.ui.DataUsageActivity$ViewPage[] r2 = r4.viewPages
            r2 = r2[r5]
            int r2 = r2.selectedType
            r3 = 1
            if (r2 != r3) goto L_0x0062
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.wifiAdapter
            if (r1 == r2) goto L_0x007e
            org.telegram.ui.DataUsageActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.wifiAdapter
            r1.setAdapter(r2)
            goto L_0x007e
        L_0x0062:
            org.telegram.ui.DataUsageActivity$ViewPage[] r2 = r4.viewPages
            r2 = r2[r5]
            int r2 = r2.selectedType
            r3 = 2
            if (r2 != r3) goto L_0x007e
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.roamingAdapter
            if (r1 == r2) goto L_0x007e
            org.telegram.ui.DataUsageActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            org.telegram.ui.DataUsageActivity$ListAdapter r2 = r4.roamingAdapter
            r1.setAdapter(r2)
        L_0x007e:
            org.telegram.ui.DataUsageActivity$ViewPage[] r1 = r4.viewPages
            r1 = r1[r5]
            org.telegram.ui.Components.RecyclerListView r1 = r1.listView
            r1.setVisibility(r0)
            org.telegram.ui.ActionBar.ActionBar r1 = r4.actionBar
            float r1 = r1.getTranslationY()
            r2 = 0
            int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x00a6
            org.telegram.ui.DataUsageActivity$ViewPage[] r1 = r4.viewPages
            r5 = r1[r5]
            androidx.recyclerview.widget.LinearLayoutManager r5 = r5.layoutManager
            org.telegram.ui.ActionBar.ActionBar r1 = r4.actionBar
            float r1 = r1.getTranslationY()
            int r1 = (int) r1
            r5.scrollToPositionWithOffset(r0, r1)
        L_0x00a6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DataUsageActivity.switchToCurrentSelectedMode(boolean):void");
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
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
        /* access modifiers changed from: private */
        public int currentType;
        private int filesBytesReceivedRow;
        private int filesBytesSentRow;
        private int filesReceivedRow;
        private int filesSection2Row;
        private int filesSectionRow;
        private int filesSentRow;
        private Context mContext;
        private int messagesBytesReceivedRow;
        private int messagesBytesSentRow;
        private int messagesReceivedRow;
        private int messagesSection2Row;
        private int messagesSectionRow;
        private int messagesSentRow;
        private int photosBytesReceivedRow;
        private int photosBytesSentRow;
        private int photosReceivedRow;
        private int photosSection2Row;
        private int photosSectionRow;
        private int photosSentRow;
        /* access modifiers changed from: private */
        public int resetRow;
        private int resetSection2Row;
        private int rowCount = 0;
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

        public ListAdapter(Context context, int i) {
            this.mContext = context;
            this.currentType = i;
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.photosSectionRow = 0;
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
            this.messagesSentRow = -1;
            this.messagesReceivedRow = -1;
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

        public int getItemCount() {
            return this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                int i2 = 3;
                boolean z = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == this.resetRow) {
                        textSettingsCell.setTag("windowBackgroundWhiteRedText2");
                        textSettingsCell.setText(LocaleController.getString("ResetStatistics", NUM), false);
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
                    } else if (!(i == this.audiosSentRow || i == this.audiosReceivedRow || i == this.audiosBytesSentRow || i == this.audiosBytesReceivedRow)) {
                        i2 = (i == this.videosSentRow || i == this.videosReceivedRow || i == this.videosBytesSentRow || i == this.videosBytesReceivedRow) ? 2 : (i == this.filesSentRow || i == this.filesReceivedRow || i == this.filesBytesSentRow || i == this.filesBytesReceivedRow) ? 5 : 6;
                    }
                    if (i == i3) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.callsReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.callsTotalTimeRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", NUM), AndroidUtilities.formatShortDuration(StatsController.getInstance(DataUsageActivity.this.currentAccount).getCallsTotalTime(this.currentType)), false);
                    } else if (i == this.messagesSentRow || i == this.photosSentRow || i == this.videosSentRow || i == this.audiosSentRow || i == this.filesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountSent", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.messagesReceivedRow || i == this.photosReceivedRow || i == this.videosReceivedRow || i == this.audiosReceivedRow || i == this.filesReceivedRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", NUM), String.format("%d", new Object[]{Integer.valueOf(StatsController.getInstance(DataUsageActivity.this.currentAccount).getRecivedItemsCount(this.currentType, i2))}), true);
                    } else if (i == this.messagesBytesSentRow || i == this.photosBytesSentRow || i == this.videosBytesSentRow || i == this.audiosBytesSentRow || i == this.filesBytesSentRow || i == this.callsBytesSentRow || i == this.totalBytesSentRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", NUM), AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getSentBytesCount(this.currentType, i2)), true);
                    } else if (i == this.messagesBytesReceivedRow || i == this.photosBytesReceivedRow || i == this.videosBytesReceivedRow || i == this.audiosBytesReceivedRow || i == this.filesBytesReceivedRow || i == this.callsBytesReceivedRow || i == this.totalBytesReceivedRow) {
                        String string = LocaleController.getString("BytesReceived", NUM);
                        String formatFileSize = AndroidUtilities.formatFileSize(StatsController.getInstance(DataUsageActivity.this.currentAccount).getReceivedBytesCount(this.currentType, i2));
                        if (i == this.callsBytesReceivedRow) {
                            z = true;
                        }
                        textSettingsCell.setTextAndValue(string, formatFileSize, z);
                    }
                } else if (itemViewType == 2) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == this.totalSectionRow) {
                        headerCell.setText(LocaleController.getString("TotalDataUsage", NUM));
                    } else if (i == this.callsSectionRow) {
                        headerCell.setText(LocaleController.getString("CallsDataUsage", NUM));
                    } else if (i == this.filesSectionRow) {
                        headerCell.setText(LocaleController.getString("FilesDataUsage", NUM));
                    } else if (i == this.audiosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalAudioCache", NUM));
                    } else if (i == this.videosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalVideoCache", NUM));
                    } else if (i == this.photosSectionRow) {
                        headerCell.setText(LocaleController.getString("LocalPhotoCache", NUM));
                    } else if (i == this.messagesSectionRow) {
                        headerCell.setText(LocaleController.getString("MessagesDataUsage", NUM));
                    }
                } else if (itemViewType == 3) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    textInfoPrivacyCell.setText(LocaleController.formatString("NetworkUsageSince", NUM, LocaleController.getInstance().formatterStats.format(StatsController.getInstance(DataUsageActivity.this.currentAccount).getResetStatsDate(this.currentType))));
                }
            } else if (i == this.resetSection2Row) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == this.resetRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new ShadowSectionCell(this.mContext);
            } else if (i == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 2) {
                view = i != 3 ? null : new TextInfoPrivacyCell(this.mContext);
            } else {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabActiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabUnactiveText"));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarTabSelector"));
        for (int i = 0; i < this.viewPages.length; i++) {
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.viewPages[i].listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
            arrayList.add(new ThemeDescription(this.viewPages[i].listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.viewPages[i].listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.viewPages[i].listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
            arrayList.add(new ThemeDescription((View) this.viewPages[i].listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"));
        }
        return arrayList;
    }
}
