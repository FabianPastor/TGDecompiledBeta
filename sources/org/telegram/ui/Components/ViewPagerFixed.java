package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;

public class ViewPagerFixed extends FrameLayout {
    private static final Interpolator interpolator = ViewPagerFixed$$ExternalSyntheticLambda0.INSTANCE;
    private Adapter adapter;
    private float additionalOffset;
    /* access modifiers changed from: private */
    public boolean animatingForward;
    /* access modifiers changed from: private */
    public boolean backAnimation;
    int currentPosition;
    private int maximumVelocity;
    /* access modifiers changed from: private */
    public boolean maybeStartTracking;
    int nextPosition;
    private Rect rect = new Rect();
    /* access modifiers changed from: private */
    public boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    /* access modifiers changed from: private */
    public AnimatorSet tabsAnimation;
    /* access modifiers changed from: private */
    public boolean tabsAnimationInProgress;
    TabsView tabsView;
    private final float touchSlop = AndroidUtilities.getPixelsInCM(0.3f, true);
    ValueAnimator.AnimatorUpdateListener updateTabProgress = new ValueAnimator.AnimatorUpdateListener() {
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if (ViewPagerFixed.this.tabsAnimationInProgress) {
                float scrollProgress = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                if (ViewPagerFixed.this.tabsView != null) {
                    ViewPagerFixed.this.tabsView.selectTab(ViewPagerFixed.this.nextPosition, ViewPagerFixed.this.currentPosition, 1.0f - scrollProgress);
                }
            }
        }
    };
    private VelocityTracker velocityTracker;
    protected View[] viewPages;
    /* access modifiers changed from: private */
    public int[] viewTypes;
    protected SparseArray<View> viewsByType = new SparseArray<>();

    static /* synthetic */ float lambda$static$0(float t) {
        float t2 = t - 1.0f;
        return (t2 * t2 * t2 * t2 * t2) + 1.0f;
    }

    public ViewPagerFixed(Context context) {
        super(context);
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        this.viewTypes = new int[2];
        this.viewPages = new View[2];
        setClipChildren(true);
    }

    public void setAdapter(Adapter adapter2) {
        this.adapter = adapter2;
        this.viewTypes[0] = adapter2.getItemViewType(this.currentPosition);
        this.viewPages[0] = adapter2.createView(this.viewTypes[0]);
        adapter2.bindView(this.viewPages[0], this.currentPosition, this.viewTypes[0]);
        addView(this.viewPages[0]);
        this.viewPages[0].setVisibility(0);
        fillTabs();
    }

    public TabsView createTabsView() {
        TabsView tabsView2 = new TabsView(getContext());
        this.tabsView = tabsView2;
        tabsView2.setDelegate(new TabsView.TabsViewDelegate() {
            public void onPageSelected(int page, boolean forward) {
                boolean unused = ViewPagerFixed.this.animatingForward = forward;
                ViewPagerFixed.this.nextPosition = page;
                ViewPagerFixed.this.updateViewForIndex(1);
                if (forward) {
                    ViewPagerFixed.this.viewPages[1].setTranslationX((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                } else {
                    ViewPagerFixed.this.viewPages[1].setTranslationX((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth()));
                }
            }

            public void onPageScrolled(float progress) {
                if (progress == 1.0f) {
                    if (ViewPagerFixed.this.viewPages[1] != null) {
                        ViewPagerFixed.this.swapViews();
                        ViewPagerFixed.this.viewsByType.put(ViewPagerFixed.this.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                        ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                        viewPagerFixed.removeView(viewPagerFixed.viewPages[1]);
                        ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                        ViewPagerFixed.this.viewPages[1] = null;
                    }
                } else if (ViewPagerFixed.this.viewPages[1] != null) {
                    if (ViewPagerFixed.this.animatingForward) {
                        ViewPagerFixed.this.viewPages[1].setTranslationX(((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * (1.0f - progress));
                        ViewPagerFixed.this.viewPages[0].setTranslationX(((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth())) * progress);
                        return;
                    }
                    ViewPagerFixed.this.viewPages[1].setTranslationX(((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth())) * (1.0f - progress));
                    ViewPagerFixed.this.viewPages[0].setTranslationX(((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * progress);
                }
            }

            public void onSamePageSelected() {
            }

            public boolean canPerformActions() {
                return !ViewPagerFixed.this.tabsAnimationInProgress && !ViewPagerFixed.this.startedTracking;
            }

            public void invalidateBlur() {
                ViewPagerFixed.this.invalidateBlur();
            }
        });
        fillTabs();
        return this.tabsView;
    }

    /* access modifiers changed from: protected */
    public void invalidateBlur() {
    }

    /* access modifiers changed from: private */
    public void updateViewForIndex(int index) {
        int adapterPosition = index == 0 ? this.currentPosition : this.nextPosition;
        if (this.viewPages[index] == null) {
            this.viewTypes[index] = this.adapter.getItemViewType(adapterPosition);
            View v = this.viewsByType.get(this.viewTypes[index]);
            if (v == null) {
                v = this.adapter.createView(this.viewTypes[index]);
            } else {
                this.viewsByType.remove(this.viewTypes[index]);
            }
            if (v.getParent() != null) {
                ((ViewGroup) v.getParent()).removeView(v);
            }
            addView(v);
            View[] viewArr = this.viewPages;
            viewArr[index] = v;
            this.adapter.bindView(viewArr[index], adapterPosition, this.viewTypes[index]);
            this.viewPages[index].setVisibility(0);
        } else if (this.viewTypes[index] == this.adapter.getItemViewType(adapterPosition)) {
            this.adapter.bindView(this.viewPages[index], adapterPosition, this.viewTypes[index]);
            this.viewPages[index].setVisibility(0);
        } else {
            this.viewsByType.put(this.viewTypes[index], this.viewPages[index]);
            this.viewPages[index].setVisibility(8);
            removeView(this.viewPages[index]);
            this.viewTypes[index] = this.adapter.getItemViewType(adapterPosition);
            View v2 = this.viewsByType.get(this.viewTypes[index]);
            if (v2 == null) {
                v2 = this.adapter.createView(this.viewTypes[index]);
            } else {
                this.viewsByType.remove(this.viewTypes[index]);
            }
            addView(v2);
            View[] viewArr2 = this.viewPages;
            viewArr2[index] = v2;
            viewArr2[index].setVisibility(0);
            Adapter adapter2 = this.adapter;
            adapter2.bindView(this.viewPages[index], adapterPosition, adapter2.getItemViewType(adapterPosition));
        }
    }

    private void fillTabs() {
        TabsView tabsView2;
        if (this.adapter != null && (tabsView2 = this.tabsView) != null) {
            tabsView2.removeTabs();
            for (int i = 0; i < this.adapter.getItemCount(); i++) {
                this.tabsView.addTab(this.adapter.getItemId(i), this.adapter.getItemTitle(i));
            }
        }
    }

    private boolean prepareForMoving(MotionEvent ev, boolean forward) {
        if ((!forward && this.currentPosition == 0) || (forward && this.currentPosition == this.adapter.getItemCount() - 1)) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) (ev.getX() + this.additionalOffset);
        TabsView tabsView2 = this.tabsView;
        if (tabsView2 != null) {
            tabsView2.setEnabled(false);
        }
        this.animatingForward = forward;
        this.nextPosition = this.currentPosition + (forward ? 1 : -1);
        updateViewForIndex(1);
        if (forward) {
            View[] viewArr = this.viewPages;
            viewArr[1].setTranslationX((float) viewArr[0].getMeasuredWidth());
        } else {
            View[] viewArr2 = this.viewPages;
            viewArr2[1].setTranslationX((float) (-viewArr2[0].getMeasuredWidth()));
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        TabsView tabsView2 = this.tabsView;
        if (tabsView2 != null && tabsView2.isAnimatingIndicator()) {
            return false;
        }
        if (checkTabsAnimationInProgress()) {
            return true;
        }
        onTouchEvent(ev);
        return this.startedTracking;
    }

    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean z;
        float velY;
        float velX;
        float dx;
        int duration;
        boolean z2;
        View child;
        MotionEvent motionEvent = ev;
        TabsView tabsView2 = this.tabsView;
        if (tabsView2 != null && tabsView2.animatingIndicator) {
            return false;
        }
        if (motionEvent != null) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(motionEvent);
        }
        if (motionEvent != null && ev.getAction() == 0 && checkTabsAnimationInProgress()) {
            this.startedTracking = true;
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            int x = (int) ev.getX();
            this.startedTrackingX = x;
            if (this.animatingForward) {
                if (((float) x) < ((float) this.viewPages[0].getMeasuredWidth()) + this.viewPages[0].getTranslationX()) {
                    this.additionalOffset = this.viewPages[0].getTranslationX();
                } else {
                    swapViews();
                    this.animatingForward = false;
                    this.additionalOffset = this.viewPages[0].getTranslationX();
                }
            } else if (((float) x) < ((float) this.viewPages[1].getMeasuredWidth()) + this.viewPages[1].getTranslationX()) {
                swapViews();
                this.animatingForward = true;
                this.additionalOffset = this.viewPages[0].getTranslationX();
            } else {
                this.additionalOffset = this.viewPages[0].getTranslationX();
            }
            this.tabsAnimation.removeAllListeners();
            this.tabsAnimation.cancel();
            this.tabsAnimationInProgress = false;
        } else if (motionEvent != null && ev.getAction() == 0) {
            this.additionalOffset = 0.0f;
        }
        if (!this.startedTracking && motionEvent != null && (child = findScrollingChild(this, ev.getX(), ev.getY())) != null && (child.canScrollHorizontally(1) || child.canScrollHorizontally(-1))) {
            return false;
        }
        if (motionEvent == null || ev.getAction() != 0 || this.startedTracking || this.maybeStartTracking) {
            if (motionEvent != null && ev.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                int dx2 = (int) ((ev.getX() - ((float) this.startedTrackingX)) + this.additionalOffset);
                int dy = Math.abs(((int) ev.getY()) - this.startedTrackingY);
                if (this.startedTracking && (((z2 = this.animatingForward) && dx2 > 0) || (!z2 && dx2 < 0))) {
                    if (!prepareForMoving(motionEvent, dx2 < 0)) {
                        this.maybeStartTracking = true;
                        this.startedTracking = false;
                        this.viewPages[0].setTranslationX(0.0f);
                        View[] viewArr = this.viewPages;
                        viewArr[1].setTranslationX((float) (this.animatingForward ? viewArr[0].getMeasuredWidth() : -viewArr[0].getMeasuredWidth()));
                        TabsView tabsView3 = this.tabsView;
                        if (tabsView3 != null) {
                            tabsView3.selectTab(this.currentPosition, 0, 0.0f);
                        }
                    }
                }
                if (this.maybeStartTracking && !this.startedTracking) {
                    int dxLocal = (int) (ev.getX() - ((float) this.startedTrackingX));
                    if (((float) Math.abs(dxLocal)) >= this.touchSlop && Math.abs(dxLocal) > dy) {
                        prepareForMoving(motionEvent, dx2 < 0);
                    }
                } else if (this.startedTracking != 0) {
                    this.viewPages[0].setTranslationX((float) dx2);
                    if (this.animatingForward) {
                        View[] viewArr2 = this.viewPages;
                        viewArr2[1].setTranslationX((float) (viewArr2[0].getMeasuredWidth() + dx2));
                    } else {
                        View[] viewArr3 = this.viewPages;
                        viewArr3[1].setTranslationX((float) (dx2 - viewArr3[0].getMeasuredWidth()));
                    }
                    float scrollProgress = ((float) Math.abs(dx2)) / ((float) this.viewPages[0].getMeasuredWidth());
                    TabsView tabsView4 = this.tabsView;
                    if (tabsView4 != null) {
                        tabsView4.selectTab(this.nextPosition, this.currentPosition, 1.0f - scrollProgress);
                    }
                }
            } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                this.velocityTracker.computeCurrentVelocity(1000, (float) this.maximumVelocity);
                if (motionEvent == null || ev.getAction() == 3) {
                    velX = 0.0f;
                    velY = 0.0f;
                } else {
                    velX = this.velocityTracker.getXVelocity();
                    velY = this.velocityTracker.getYVelocity();
                    if (!this.startedTracking && Math.abs(velX) >= 3000.0f && Math.abs(velX) > Math.abs(velY)) {
                        prepareForMoving(motionEvent, velX < 0.0f);
                    }
                }
                if (this.startedTracking) {
                    float x2 = this.viewPages[0].getX();
                    this.tabsAnimation = new AnimatorSet();
                    if (this.additionalOffset == 0.0f) {
                        this.backAnimation = Math.abs(x2) < ((float) this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(velX) < 3500.0f || Math.abs(velX) < Math.abs(velY));
                    } else if (Math.abs(velX) > 1500.0f) {
                        this.backAnimation = !this.animatingForward ? velX < 0.0f : velX > 0.0f;
                    } else if (this.animatingForward) {
                        this.backAnimation = this.viewPages[1].getX() > ((float) (this.viewPages[0].getMeasuredWidth() >> 1));
                    } else {
                        this.backAnimation = this.viewPages[0].getX() < ((float) (this.viewPages[0].getMeasuredWidth() >> 1));
                    }
                    if (this.backAnimation) {
                        dx = Math.abs(x2);
                        if (this.animatingForward) {
                            this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{(float) this.viewPages[1].getMeasuredWidth()})});
                        } else {
                            this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{(float) (-this.viewPages[1].getMeasuredWidth())})});
                        }
                    } else {
                        dx = ((float) this.viewPages[0].getMeasuredWidth()) - Math.abs(x2);
                        if (this.animatingForward) {
                            this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{(float) (-this.viewPages[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        } else {
                            this.tabsAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{(float) this.viewPages[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                        }
                    }
                    ValueAnimator animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    animator.addUpdateListener(this.updateTabProgress);
                    this.tabsAnimation.playTogether(new Animator[]{animator});
                    this.tabsAnimation.setInterpolator(interpolator);
                    int width = getMeasuredWidth();
                    int halfWidth = width / 2;
                    float distance = ((float) halfWidth) + (((float) halfWidth) * distanceInfluenceForSnapDuration(Math.min(1.0f, (dx * 1.0f) / ((float) width))));
                    float velX2 = Math.abs(velX);
                    if (velX2 > 0.0f) {
                        duration = Math.round(Math.abs(distance / velX2) * 1000.0f) * 4;
                    } else {
                        duration = (int) ((1.0f + (dx / ((float) getMeasuredWidth()))) * 100.0f);
                    }
                    this.tabsAnimation.setDuration((long) Math.max(150, Math.min(duration, 600)));
                    this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet unused = ViewPagerFixed.this.tabsAnimation = null;
                            if (ViewPagerFixed.this.viewPages[1] != null) {
                                if (!ViewPagerFixed.this.backAnimation) {
                                    ViewPagerFixed.this.swapViews();
                                }
                                ViewPagerFixed.this.viewsByType.put(ViewPagerFixed.this.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                                viewPagerFixed.removeView(viewPagerFixed.viewPages[1]);
                                ViewPagerFixed.this.viewPages[1].setVisibility(8);
                                ViewPagerFixed.this.viewPages[1] = null;
                            }
                            boolean unused2 = ViewPagerFixed.this.tabsAnimationInProgress = false;
                            boolean unused3 = ViewPagerFixed.this.maybeStartTracking = false;
                            if (ViewPagerFixed.this.tabsView != null) {
                                ViewPagerFixed.this.tabsView.setEnabled(true);
                            }
                        }
                    });
                    this.tabsAnimation.start();
                    z = true;
                    this.tabsAnimationInProgress = true;
                    this.startedTracking = false;
                } else {
                    z = true;
                    this.maybeStartTracking = false;
                    TabsView tabsView5 = this.tabsView;
                    if (tabsView5 != null) {
                        tabsView5.setEnabled(true);
                    }
                }
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.recycle();
                    this.velocityTracker = null;
                }
            }
            z = true;
        } else {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) ev.getX();
            this.startedTrackingY = (int) ev.getY();
            z = true;
        }
        if (this.startedTracking || this.maybeStartTracking) {
            return z;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void swapViews() {
        View[] viewArr = this.viewPages;
        View page = viewArr[0];
        viewArr[0] = viewArr[1];
        viewArr[1] = page;
        int p = this.currentPosition;
        int i = this.nextPosition;
        this.currentPosition = i;
        this.nextPosition = p;
        int[] iArr = this.viewTypes;
        int p2 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = p2;
        onItemSelected(viewArr[0], viewArr[1], i, p);
    }

    public boolean checkTabsAnimationInProgress() {
        if (!this.tabsAnimationInProgress) {
            return false;
        }
        boolean cancel = false;
        int i = -1;
        if (this.backAnimation) {
            if (Math.abs(this.viewPages[0].getTranslationX()) < 1.0f) {
                this.viewPages[0].setTranslationX(0.0f);
                View[] viewArr = this.viewPages;
                View view = viewArr[1];
                int measuredWidth = viewArr[0].getMeasuredWidth();
                if (this.animatingForward) {
                    i = 1;
                }
                view.setTranslationX((float) (measuredWidth * i));
                cancel = true;
            }
        } else if (Math.abs(this.viewPages[1].getTranslationX()) < 1.0f) {
            View[] viewArr2 = this.viewPages;
            View view2 = viewArr2[0];
            int measuredWidth2 = viewArr2[0].getMeasuredWidth();
            if (!this.animatingForward) {
                i = 1;
            }
            view2.setTranslationX((float) (measuredWidth2 * i));
            this.viewPages[1].setTranslationX(0.0f);
            cancel = true;
        }
        if (cancel) {
            AnimatorSet animatorSet = this.tabsAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.tabsAnimation = null;
            }
            this.tabsAnimationInProgress = false;
        }
        return this.tabsAnimationInProgress;
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public void setPosition(int position) {
        AnimatorSet animatorSet = this.tabsAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        View[] viewArr = this.viewPages;
        if (viewArr[1] != null) {
            this.viewsByType.put(this.viewTypes[1], viewArr[1]);
            removeView(this.viewPages[1]);
            this.viewPages[1] = null;
        }
        if (this.currentPosition != position) {
            int oldPosition = this.currentPosition;
            this.currentPosition = position;
            View oldView = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], oldView, this.currentPosition, oldPosition);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView2 = this.tabsView;
            if (tabsView2 != null) {
                tabsView2.selectTab(position, 0, 1.0f);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onItemSelected(View currentPage, View oldPage, int position, int oldPosition) {
    }

    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int position) {
            return position;
        }

        public String getItemTitle(int position) {
            return "";
        }

        public int getItemViewType(int position) {
            return 0;
        }
    }

    public boolean canScrollHorizontally(int direction) {
        if (direction == 0) {
            return false;
        }
        if (this.tabsAnimationInProgress || this.startedTracking) {
            return true;
        }
        boolean forward = direction > 0;
        if ((forward || this.currentPosition != 0) && (!forward || this.currentPosition != this.adapter.getItemCount() - 1)) {
            return true;
        }
        return false;
    }

    public View getCurrentView() {
        return this.viewPages[0];
    }

    public int getCurrentPosition() {
        return this.currentPosition;
    }

    public static class TabsView extends FrameLayout {
        /* access modifiers changed from: private */
        public String activeTextColorKey = "profile_tabSelectedText";
        private ListAdapter adapter;
        /* access modifiers changed from: private */
        public int additionalTabWidth;
        private int allTabsWidth;
        /* access modifiers changed from: private */
        public boolean animatingIndicator;
        /* access modifiers changed from: private */
        public float animatingIndicatorProgress;
        /* access modifiers changed from: private */
        public Runnable animationRunnable = new Runnable() {
            public void run() {
                if (TabsView.this.animatingIndicator) {
                    long dt = SystemClock.elapsedRealtime() - TabsView.this.lastAnimationTime;
                    if (dt > 17) {
                        dt = 17;
                    }
                    TabsView.access$2716(TabsView.this, ((float) dt) / 200.0f);
                    TabsView tabsView = TabsView.this;
                    tabsView.setAnimationIdicatorProgress(tabsView.interpolator.getInterpolation(TabsView.this.animationTime));
                    if (TabsView.this.animationTime > 1.0f) {
                        float unused = TabsView.this.animationTime = 1.0f;
                    }
                    if (TabsView.this.animationTime < 1.0f) {
                        AndroidUtilities.runOnUIThread(TabsView.this.animationRunnable);
                        return;
                    }
                    boolean unused2 = TabsView.this.animatingIndicator = false;
                    TabsView.this.setEnabled(true);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(1.0f);
                    }
                }
            }
        };
        private boolean animationRunning;
        /* access modifiers changed from: private */
        public float animationTime;
        private float animationValue;
        /* access modifiers changed from: private */
        public String backgroundColorKey = "actionBarDefault";
        private boolean commitCrossfade;
        /* access modifiers changed from: private */
        public Paint counterPaint = new Paint(1);
        private float crossfadeAlpha;
        private Bitmap crossfadeBitmap;
        private Paint crossfadePaint = new Paint();
        private int currentPosition;
        /* access modifiers changed from: private */
        public TabsViewDelegate delegate;
        /* access modifiers changed from: private */
        public Paint deletePaint = new TextPaint(1);
        /* access modifiers changed from: private */
        public float editingAnimationProgress;
        private boolean editingForwardAnimation;
        /* access modifiers changed from: private */
        public float editingStartAnimationProgress;
        private float hideProgress;
        private SparseIntArray idToPosition = new SparseIntArray(5);
        private boolean ignoreLayout;
        /* access modifiers changed from: private */
        public CubicBezierInterpolator interpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        private boolean invalidated;
        /* access modifiers changed from: private */
        public boolean isEditing;
        /* access modifiers changed from: private */
        public boolean isInHiddenMode;
        /* access modifiers changed from: private */
        public long lastAnimationTime;
        private long lastEditingAnimationTime;
        private LinearLayoutManager layoutManager;
        private RecyclerListView listView;
        /* access modifiers changed from: private */
        public int manualScrollingToId = -1;
        /* access modifiers changed from: private */
        public int manualScrollingToPosition = -1;
        private boolean orderChanged;
        private SparseIntArray positionToId = new SparseIntArray(5);
        private SparseIntArray positionToWidth = new SparseIntArray(5);
        private SparseIntArray positionToX = new SparseIntArray(5);
        private int prevLayoutWidth;
        /* access modifiers changed from: private */
        public int previousId;
        private int previousPosition;
        private int scrollingToChild = -1;
        /* access modifiers changed from: private */
        public int selectedTabId = -1;
        private String selectorColorKey = "profile_tabSelector";
        private GradientDrawable selectorDrawable;
        private String tabLineColorKey = "profile_tabSelectedLine";
        /* access modifiers changed from: private */
        public ArrayList<Tab> tabs = new ArrayList<>();
        ValueAnimator tabsAnimator;
        /* access modifiers changed from: private */
        public TextPaint textCounterPaint = new TextPaint(1);
        /* access modifiers changed from: private */
        public TextPaint textPaint = new TextPaint(1);
        /* access modifiers changed from: private */
        public String unactiveTextColorKey = "profile_tabText";

        public interface TabsViewDelegate {
            boolean canPerformActions();

            void invalidateBlur();

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        static /* synthetic */ float access$2716(TabsView x0, float x1) {
            float f = x0.animationTime + x1;
            x0.animationTime = f;
            return f;
        }

        private static class Tab {
            public int counter;
            public int id;
            public String title;
            public int titleWidth;

            public Tab(int i, String t) {
                this.id = i;
                this.title = t;
            }

            public int getWidth(boolean store, TextPaint textPaint) {
                int width = (int) Math.ceil((double) textPaint.measureText(this.title));
                this.titleWidth = width;
                return Math.max(AndroidUtilities.dp(40.0f), width);
            }

            public boolean setTitle(String newTitle) {
                if (TextUtils.equals(this.title, newTitle)) {
                    return false;
                }
                this.title = newTitle;
                return true;
            }
        }

        public class TabView extends View {
            private int currentPosition;
            /* access modifiers changed from: private */
            public Tab currentTab;
            private String currentText;
            /* access modifiers changed from: private */
            public RectF rect = new RectF();
            /* access modifiers changed from: private */
            public int tabWidth;
            private int textHeight;
            private StaticLayout textLayout;
            private int textOffsetX;

            public TabView(Context context) {
                super(context);
            }

            public void setTab(Tab tab, int position) {
                this.currentTab = tab;
                this.currentPosition = position;
                setContentDescription(tab.title);
                requestLayout();
            }

            public int getId() {
                return this.currentTab.id;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                setMeasuredDimension(this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.dp(32.0f) + TabsView.this.additionalTabWidth, View.MeasureSpec.getSize(heightMeasureSpec));
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                int id2;
                int id1;
                String unreadOtherKey;
                String unreadKey;
                String otherKey;
                String key;
                String counterText;
                int counterWidth;
                int countWidth;
                int countWidth2;
                int i;
                Canvas canvas2 = canvas;
                if (!(this.currentTab.id == Integer.MAX_VALUE || TabsView.this.editingAnimationProgress == 0.0f)) {
                    canvas.save();
                    float p = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas2.translate(((float) AndroidUtilities.dp(0.66f)) * p, 0.0f);
                    canvas2.rotate(p, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    id1 = TabsView.this.manualScrollingToId;
                    id2 = TabsView.this.selectedTabId;
                } else {
                    id1 = TabsView.this.selectedTabId;
                    id2 = TabsView.this.previousId;
                }
                if (this.currentTab.id == id1) {
                    key = TabsView.this.activeTextColorKey;
                    otherKey = TabsView.this.unactiveTextColorKey;
                    unreadKey = "chats_tabUnreadActiveBackground";
                    unreadOtherKey = "chats_tabUnreadUnactiveBackground";
                } else {
                    key = TabsView.this.unactiveTextColorKey;
                    otherKey = TabsView.this.activeTextColorKey;
                    unreadKey = "chats_tabUnreadUnactiveBackground";
                    unreadOtherKey = "chats_tabUnreadActiveBackground";
                }
                if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && (this.currentTab.id == id1 || this.currentTab.id == id2)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(otherKey), Theme.getColor(key), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(key));
                }
                if (this.currentTab.counter > 0) {
                    String counterText2 = String.format("%d", new Object[]{Integer.valueOf(this.currentTab.counter)});
                    int counterWidth2 = (int) Math.ceil((double) TabsView.this.textCounterPaint.measureText(counterText2));
                    counterWidth = counterWidth2;
                    counterText = counterText2;
                    countWidth = Math.max(AndroidUtilities.dp(10.0f), counterWidth2) + AndroidUtilities.dp(10.0f);
                } else {
                    counterWidth = 0;
                    counterText = null;
                    countWidth = 0;
                }
                if (this.currentTab.id == Integer.MAX_VALUE || (!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f)) {
                    countWidth2 = countWidth;
                } else {
                    countWidth2 = (int) (((float) countWidth) + (((float) (AndroidUtilities.dp(20.0f) - countWidth)) * TabsView.this.editingStartAnimationProgress));
                }
                int i2 = this.currentTab.titleWidth;
                if (countWidth2 != 0) {
                    i = AndroidUtilities.dp((counterText != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + countWidth2;
                } else {
                    i = 0;
                }
                this.tabWidth = i2 + i;
                int textX = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    String str = this.currentTab.title;
                    this.currentText = str;
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(str, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), TabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.textHeight = staticLayout.getHeight();
                    this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas2.translate((float) (this.textOffsetX + textX), (float) (((getMeasuredHeight() - this.textHeight) / 2) + 1));
                    this.textLayout.draw(canvas2);
                    canvas.restore();
                }
                if (counterText != null || (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey));
                    if (!Theme.hasThemeKey(unreadKey) || !Theme.hasThemeKey(unreadOtherKey)) {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    } else {
                        int color1 = Theme.getColor(unreadKey);
                        if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToPosition != -1) && (this.currentTab.id == id1 || this.currentTab.id == id2)) {
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(unreadOtherKey), color1, TabsView.this.animatingIndicatorProgress));
                        } else {
                            TabsView.this.counterPaint.setColor(color1);
                        }
                    }
                    int x = this.currentTab.titleWidth + textX + AndroidUtilities.dp(6.0f);
                    int countTop = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                    if (this.currentTab.id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || counterText != null)) {
                        TabsView.this.counterPaint.setAlpha(255);
                    } else {
                        TabsView.this.counterPaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                    }
                    int i3 = textX;
                    int i4 = id1;
                    int x2 = x;
                    this.rect.set((float) x, (float) countTop, (float) (x + countWidth2), (float) (countTop + AndroidUtilities.dp(20.0f)));
                    canvas2.drawRoundRect(this.rect, AndroidUtilities.density * 11.5f, AndroidUtilities.density * 11.5f, TabsView.this.counterPaint);
                    if (counterText != null) {
                        if (this.currentTab.id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        canvas2.drawText(counterText, this.rect.left + ((this.rect.width() - ((float) counterWidth)) / 2.0f), (float) (AndroidUtilities.dp(14.5f) + countTop), TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.id == Integer.MAX_VALUE) {
                        int i5 = countTop;
                        String str2 = counterText;
                        int i6 = x2;
                    } else if (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        int side = AndroidUtilities.dp(3.0f);
                        int i7 = counterWidth;
                        int i8 = countTop;
                        int i9 = x2;
                        String str3 = counterText;
                        canvas.drawLine(this.rect.centerX() - ((float) side), this.rect.centerY() - ((float) side), ((float) side) + this.rect.centerX(), this.rect.centerY() + ((float) side), TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - ((float) side), ((float) side) + this.rect.centerY(), ((float) side) + this.rect.centerX(), this.rect.centerY() - ((float) side), TabsView.this.deletePaint);
                    } else {
                        int i10 = counterWidth;
                        String str4 = counterText;
                    }
                } else {
                    int i11 = counterWidth;
                    String str5 = counterText;
                    int i12 = textX;
                    int i13 = id1;
                }
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.restore();
                }
            }

            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.id != TabsView.this.selectedTabId) ? false : true);
            }
        }

        public TabsView(Context context) {
            super(context);
            this.textCounterPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            this.textCounterPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.textPaint.setTextSize((float) AndroidUtilities.dp(15.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.deletePaint.setStyle(Paint.Style.STROKE);
            this.deletePaint.setStrokeCap(Paint.Cap.ROUND);
            this.deletePaint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
            this.selectorDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, (int[]) null);
            float rad = AndroidUtilities.dpf2(3.0f);
            this.selectorDrawable.setCornerRadii(new float[]{rad, rad, rad, rad, 0.0f, 0.0f, 0.0f, 0.0f});
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
            setHorizontalScrollBarEnabled(false);
            AnonymousClass2 r4 = new RecyclerListView(context) {
                public void addView(View child, int index, ViewGroup.LayoutParams params) {
                    super.addView(child, index, params);
                    if (TabsView.this.isInHiddenMode) {
                        child.setScaleX(0.3f);
                        child.setScaleY(0.3f);
                        child.setAlpha(0.0f);
                        return;
                    }
                    child.setScaleX(1.0f);
                    child.setScaleY(1.0f);
                    child.setAlpha(1.0f);
                }

                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    TabsView.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public boolean canHighlightChildAt(View child, float x, float y) {
                    if (TabsView.this.isEditing) {
                        TabView tabView = (TabView) child;
                        int side = AndroidUtilities.dp(6.0f);
                        if (tabView.rect.left - ((float) side) < x && tabView.rect.right + ((float) side) > x) {
                            return false;
                        }
                    }
                    return super.canHighlightChildAt(child, x, y);
                }
            };
            this.listView = r4;
            ((DefaultItemAnimator) r4.getItemAnimator()).setDelayAnimations(false);
            this.listView.setSelectorType(7);
            this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
            RecyclerListView recyclerListView = this.listView;
            AnonymousClass3 r42 = new LinearLayoutManager(context, 0, false) {
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                    LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                        /* access modifiers changed from: protected */
                        public void onTargetFound(View targetView, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                            int dx = calculateDxToMakeVisible(targetView, getHorizontalSnapPreference());
                            if (dx > 0 || (dx == 0 && targetView.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                                dx += AndroidUtilities.dp(60.0f);
                            } else if (dx < 0 || (dx == 0 && targetView.getRight() + AndroidUtilities.dp(21.0f) > TabsView.this.getMeasuredWidth())) {
                                dx -= AndroidUtilities.dp(60.0f);
                            }
                            int dy = calculateDyToMakeVisible(targetView, getVerticalSnapPreference());
                            int time = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((double) ((dx * dx) + (dy * dy)))));
                            if (time > 0) {
                                action.update(-dx, -dy, time, this.mDecelerateInterpolator);
                            }
                        }
                    };
                    linearSmoothScroller.setTargetPosition(position);
                    startSmoothScroll(linearSmoothScroller);
                }

                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat info) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, info);
                    if (TabsView.this.isInHiddenMode) {
                        info.setVisibleToUser(false);
                    }
                }
            };
            this.layoutManager = r42;
            recyclerListView.setLayoutManager(r42);
            this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
            this.listView.setClipToPadding(false);
            this.listView.setDrawSelectorBehind(true);
            RecyclerListView recyclerListView2 = this.listView;
            ListAdapter listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            recyclerListView2.setAdapter(listAdapter);
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new ViewPagerFixed$TabsView$$ExternalSyntheticLambda1(this));
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    TabsView.this.invalidate();
                }
            });
            addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-ViewPagerFixed$TabsView  reason: not valid java name */
        public /* synthetic */ void m4524lambda$new$0$orgtelegramuiComponentsViewPagerFixed$TabsView(View view, int position, float x, float y) {
            TabsViewDelegate tabsViewDelegate;
            if (this.delegate.canPerformActions()) {
                TabView tabView = (TabView) view;
                if (position != this.currentPosition || (tabsViewDelegate = this.delegate) == null) {
                    scrollToTab(tabView.currentTab.id, position);
                } else {
                    tabsViewDelegate.onSamePageSelected();
                }
            }
        }

        public void setDelegate(TabsViewDelegate filterTabsViewDelegate) {
            this.delegate = filterTabsViewDelegate;
        }

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
        }

        public void scrollToTab(int id, int position) {
            int i = this.currentPosition;
            boolean scrollingForward = i < position;
            this.scrollingToChild = -1;
            this.previousPosition = i;
            this.previousId = this.selectedTabId;
            this.currentPosition = position;
            this.selectedTabId = id;
            ValueAnimator valueAnimator = this.tabsAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.animatingIndicator) {
                this.animatingIndicator = false;
            }
            this.animationTime = 0.0f;
            this.animatingIndicatorProgress = 0.0f;
            this.animatingIndicator = true;
            setEnabled(false);
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageSelected(id, scrollingForward);
            }
            scrollToChild(position);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    TabsView.this.setAnimationIdicatorProgress(progress);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(progress);
                    }
                }
            });
            this.tabsAnimator.setDuration(250);
            this.tabsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.tabsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    boolean unused = TabsView.this.animatingIndicator = false;
                    TabsView.this.setEnabled(true);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(1.0f);
                    }
                    TabsView.this.invalidate();
                }
            });
            this.tabsAnimator.start();
        }

        public void setAnimationIdicatorProgress(float value) {
            this.animatingIndicatorProgress = value;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(value);
            }
        }

        public Drawable getSelectorDrawable() {
            return this.selectorDrawable;
        }

        public RecyclerListView getTabsContainer() {
            return this.listView;
        }

        public int getNextPageId(boolean forward) {
            return this.positionToId.get(this.currentPosition + (forward ? 1 : -1), -1);
        }

        public void addTab(int id, String text) {
            int position = this.tabs.size();
            if (position == 0 && this.selectedTabId == -1) {
                this.selectedTabId = id;
            }
            this.positionToId.put(position, id);
            this.idToPosition.put(id, position);
            int i = this.selectedTabId;
            if (i != -1 && i == id) {
                this.currentPosition = position;
            }
            Tab tab = new Tab(id, text);
            this.allTabsWidth += tab.getWidth(true, this.textPaint) + AndroidUtilities.dp(32.0f);
            this.tabs.add(tab);
        }

        public void removeTabs() {
            this.tabs.clear();
            this.positionToId.clear();
            this.idToPosition.clear();
            this.positionToWidth.clear();
            this.positionToX.clear();
            this.allTabsWidth = 0;
        }

        public void finishAddingTabs() {
            this.adapter.notifyDataSetChanged();
        }

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int xOffset = AndroidUtilities.dp(7.0f);
            int N = this.tabs.size();
            for (int a = 0; a < N; a++) {
                int tabWidth = this.tabs.get(a).getWidth(false, this.textPaint);
                this.positionToWidth.put(a, tabWidth);
                this.positionToX.put(a, (this.additionalTabWidth / 2) + xOffset);
                xOffset += AndroidUtilities.dp(32.0f) + tabWidth + this.additionalTabWidth;
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0095  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x012c  */
        /* JADX WARNING: Removed duplicated region for block: B:45:0x015c  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean drawChild(android.graphics.Canvas r21, android.view.View r22, long r23) {
            /*
                r20 = this;
                r0 = r20
                r1 = r21
                boolean r2 = super.drawChild(r21, r22, r23)
                org.telegram.ui.Components.RecyclerListView r3 = r0.listView
                r4 = r22
                if (r4 != r3) goto L_0x0170
                int r3 = r20.getMeasuredHeight()
                r5 = 0
                boolean r6 = r0.isInHiddenMode
                r7 = 0
                if (r6 == 0) goto L_0x0030
                float r8 = r0.hideProgress
                r9 = 1065353216(0x3var_, float:1.0)
                int r10 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r10 == 0) goto L_0x0030
                r6 = 1036831949(0x3dcccccd, float:0.1)
                float r8 = r8 + r6
                r0.hideProgress = r8
                int r6 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                if (r6 <= 0) goto L_0x002c
                r0.hideProgress = r9
            L_0x002c:
                r20.invalidate()
                goto L_0x0047
            L_0x0030:
                if (r6 != 0) goto L_0x0047
                float r6 = r0.hideProgress
                int r8 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                if (r8 == 0) goto L_0x0047
                r8 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r6 = r6 - r8
                r0.hideProgress = r6
                int r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                if (r6 >= 0) goto L_0x0044
                r0.hideProgress = r7
            L_0x0044:
                r20.invalidate()
            L_0x0047:
                android.graphics.drawable.GradientDrawable r6 = r0.selectorDrawable
                org.telegram.ui.Components.RecyclerListView r8 = r0.listView
                float r8 = r8.getAlpha()
                r9 = 1132396544(0x437var_, float:255.0)
                float r8 = r8 * r9
                int r8 = (int) r8
                r6.setAlpha(r8)
                r6 = 0
                r8 = 0
                boolean r10 = r0.animatingIndicator
                r11 = -1
                if (r10 != 0) goto L_0x0095
                int r10 = r0.manualScrollingToPosition
                if (r10 == r11) goto L_0x0063
                goto L_0x0095
            L_0x0063:
                org.telegram.ui.Components.RecyclerListView r10 = r0.listView
                int r11 = r0.currentPosition
                androidx.recyclerview.widget.RecyclerView$ViewHolder r10 = r10.findViewHolderForAdapterPosition(r11)
                if (r10 == 0) goto L_0x0091
                android.view.View r11 = r10.itemView
                org.telegram.ui.Components.ViewPagerFixed$TabsView$TabView r11 = (org.telegram.ui.Components.ViewPagerFixed.TabsView.TabView) r11
                r12 = 1109393408(0x42200000, float:40.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                int r13 = r11.tabWidth
                int r8 = java.lang.Math.max(r12, r13)
                float r12 = r11.getX()
                int r13 = r11.getMeasuredWidth()
                int r13 = r13 - r8
                int r13 = r13 / 2
                float r13 = (float) r13
                float r12 = r12 + r13
                int r6 = (int) r12
                r17 = r5
                goto L_0x012a
            L_0x0091:
                r17 = r5
                goto L_0x012a
            L_0x0095:
                androidx.recyclerview.widget.LinearLayoutManager r10 = r0.layoutManager
                int r10 = r10.findFirstVisibleItemPosition()
                if (r10 == r11) goto L_0x011f
                org.telegram.ui.Components.RecyclerListView r11 = r0.listView
                androidx.recyclerview.widget.RecyclerView$ViewHolder r11 = r11.findViewHolderForAdapterPosition(r10)
                if (r11 == 0) goto L_0x0118
                boolean r12 = r0.animatingIndicator
                if (r12 == 0) goto L_0x00ae
                int r12 = r0.previousPosition
                int r13 = r0.currentPosition
                goto L_0x00b2
            L_0x00ae:
                int r12 = r0.currentPosition
                int r13 = r0.manualScrollingToPosition
            L_0x00b2:
                android.util.SparseIntArray r14 = r0.positionToX
                int r14 = r14.get(r12)
                android.util.SparseIntArray r15 = r0.positionToX
                int r15 = r15.get(r13)
                android.util.SparseIntArray r7 = r0.positionToWidth
                int r7 = r7.get(r12)
                android.util.SparseIntArray r9 = r0.positionToWidth
                int r9 = r9.get(r13)
                int r4 = r0.additionalTabWidth
                r16 = 1098907648(0x41800000, float:16.0)
                if (r4 == 0) goto L_0x00e6
                float r4 = (float) r14
                r17 = r5
                int r5 = r15 - r14
                float r5 = (float) r5
                r18 = r6
                float r6 = r0.animatingIndicatorProgress
                float r5 = r5 * r6
                float r4 = r4 + r5
                int r4 = (int) r4
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r4 = r4 + r5
                r19 = r8
                goto L_0x010b
            L_0x00e6:
                r17 = r5
                r18 = r6
                android.util.SparseIntArray r4 = r0.positionToX
                int r4 = r4.get(r10)
                float r5 = (float) r14
                int r6 = r15 - r14
                float r6 = (float) r6
                r19 = r8
                float r8 = r0.animatingIndicatorProgress
                float r6 = r6 * r8
                float r5 = r5 + r6
                int r5 = (int) r5
                android.view.View r6 = r11.itemView
                int r6 = r6.getLeft()
                int r6 = r4 - r6
                int r5 = r5 - r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r16)
                int r5 = r5 + r6
                r4 = r5
            L_0x010b:
                float r5 = (float) r7
                int r6 = r9 - r7
                float r6 = (float) r6
                float r8 = r0.animatingIndicatorProgress
                float r6 = r6 * r8
                float r5 = r5 + r6
                int r5 = (int) r5
                r6 = r4
                r8 = r5
                goto L_0x0129
            L_0x0118:
                r17 = r5
                r18 = r6
                r19 = r8
                goto L_0x0125
            L_0x011f:
                r17 = r5
                r18 = r6
                r19 = r8
            L_0x0125:
                r6 = r18
                r8 = r19
            L_0x0129:
            L_0x012a:
                if (r8 == 0) goto L_0x0158
                android.graphics.drawable.GradientDrawable r4 = r0.selectorDrawable
                r5 = 1082130432(0x40800000, float:4.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dpr(r5)
                int r7 = r3 - r7
                float r7 = (float) r7
                float r9 = r0.hideProgress
                int r10 = org.telegram.messenger.AndroidUtilities.dpr(r5)
                float r10 = (float) r10
                float r9 = r9 * r10
                float r7 = r7 + r9
                int r7 = (int) r7
                int r9 = r6 + r8
                float r10 = (float) r3
                float r11 = r0.hideProgress
                int r5 = org.telegram.messenger.AndroidUtilities.dpr(r5)
                float r5 = (float) r5
                float r11 = r11 * r5
                float r10 = r10 + r11
                int r5 = (int) r10
                r4.setBounds(r6, r7, r9, r5)
                android.graphics.drawable.GradientDrawable r4 = r0.selectorDrawable
                r4.draw(r1)
            L_0x0158:
                android.graphics.Bitmap r4 = r0.crossfadeBitmap
                if (r4 == 0) goto L_0x0170
                android.graphics.Paint r4 = r0.crossfadePaint
                float r5 = r0.crossfadeAlpha
                r7 = 1132396544(0x437var_, float:255.0)
                float r5 = r5 * r7
                int r5 = (int) r5
                r4.setAlpha(r5)
                android.graphics.Bitmap r4 = r0.crossfadeBitmap
                android.graphics.Paint r5 = r0.crossfadePaint
                r7 = 0
                r1.drawBitmap(r4, r7, r7, r5)
            L_0x0170:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (!this.tabs.isEmpty()) {
                int width = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
                int prevWidth = this.additionalTabWidth;
                int i = this.allTabsWidth;
                int size = i < width ? (width - i) / this.tabs.size() : 0;
                this.additionalTabWidth = size;
                if (prevWidth != size) {
                    this.ignoreLayout = true;
                    this.adapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
                this.invalidated = false;
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void updateColors() {
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
            this.listView.invalidateViews();
            this.listView.invalidate();
            invalidate();
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        private void scrollToChild(int position) {
            if (!this.tabs.isEmpty() && this.scrollingToChild != position && position >= 0 && position < this.tabs.size()) {
                this.scrollingToChild = position;
                this.listView.smoothScrollToPosition(position);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (this.prevLayoutWidth != r - l) {
                this.prevLayoutWidth = r - l;
                this.scrollingToChild = -1;
                if (this.animatingIndicator) {
                    AndroidUtilities.cancelRunOnUIThread(this.animationRunnable);
                    this.animatingIndicator = false;
                    setEnabled(true);
                    TabsViewDelegate tabsViewDelegate = this.delegate;
                    if (tabsViewDelegate != null) {
                        tabsViewDelegate.onPageScrolled(1.0f);
                    }
                }
            }
        }

        public void selectTab(int currentPosition2, int nextPosition, float progress) {
            if (progress < 0.0f) {
                progress = 0.0f;
            } else if (progress > 1.0f) {
                progress = 1.0f;
            }
            this.currentPosition = currentPosition2;
            this.selectedTabId = this.positionToId.get(currentPosition2);
            if (progress > 0.0f) {
                this.manualScrollingToPosition = nextPosition;
                this.manualScrollingToId = this.positionToId.get(nextPosition);
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = progress;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(currentPosition2);
            if (progress >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = nextPosition;
                this.selectedTabId = this.positionToId.get(nextPosition);
            }
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.invalidateBlur();
            }
        }

        public void selectTabWithId(int id, float progress) {
            int position = this.idToPosition.get(id, -1);
            if (position >= 0) {
                if (progress < 0.0f) {
                    progress = 0.0f;
                } else if (progress > 1.0f) {
                    progress = 1.0f;
                }
                if (progress > 0.0f) {
                    this.manualScrollingToPosition = position;
                    this.manualScrollingToId = id;
                } else {
                    this.manualScrollingToPosition = -1;
                    this.manualScrollingToId = -1;
                }
                this.animatingIndicatorProgress = progress;
                this.listView.invalidateViews();
                invalidate();
                scrollToChild(position);
                if (progress >= 1.0f) {
                    this.manualScrollingToPosition = -1;
                    this.manualScrollingToId = -1;
                    this.currentPosition = position;
                    this.selectedTabId = id;
                }
            }
        }

        private int getChildWidth(TextView child) {
            Layout layout = child.getLayout();
            if (layout == null) {
                return child.getMeasuredWidth();
            }
            int w = ((int) Math.ceil((double) layout.getLineWidth(0))) + AndroidUtilities.dp(2.0f);
            if (child.getCompoundDrawables()[2] != null) {
                return w + child.getCompoundDrawables()[2].getIntrinsicWidth() + AndroidUtilities.dp(6.0f);
            }
            return w;
        }

        public void onPageScrolled(int position, int first) {
            if (this.currentPosition != position) {
                this.currentPosition = position;
                if (position < this.tabs.size()) {
                    if (first != position || position <= 1) {
                        scrollToChild(position);
                    } else {
                        scrollToChild(position - 1);
                    }
                    invalidate();
                }
            }
        }

        public boolean isEditing() {
            return this.isEditing;
        }

        public void setIsEditing(boolean value) {
            this.isEditing = value;
            this.editingForwardAnimation = true;
            this.listView.invalidateViews();
            invalidate();
            if (!this.isEditing && this.orderChanged) {
                MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
                TLRPC.TL_messages_updateDialogFiltersOrder req = new TLRPC.TL_messages_updateDialogFiltersOrder();
                ArrayList<MessagesController.DialogFilter> filters = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                int N = filters.size();
                for (int a = 0; a < N; a++) {
                    MessagesController.DialogFilter dialogFilter = filters.get(a);
                    req.order.add(Integer.valueOf(filters.get(a).id));
                }
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(req, ViewPagerFixed$TabsView$$ExternalSyntheticLambda0.INSTANCE);
                this.orderChanged = false;
            }
        }

        static /* synthetic */ void lambda$setIsEditing$1(TLObject response, TLRPC.TL_error error) {
        }

        private class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            public ListAdapter(Context context) {
                this.mContext = context;
            }

            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            public long getItemId(int i) {
                return (long) i;
            }

            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RecyclerListView.Holder(new TabView(this.mContext));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((TabView) holder.itemView).setTab((Tab) TabsView.this.tabs.get(position), position);
            }

            public int getItemViewType(int i) {
                return 0;
            }
        }

        public void hide(boolean hide, boolean animated) {
            this.isInHiddenMode = hide;
            float f = 1.0f;
            if (animated) {
                for (int i = 0; i < this.listView.getChildCount(); i++) {
                    this.listView.getChildAt(i).animate().alpha(hide ? 0.0f : 1.0f).scaleX(hide ? 0.0f : 1.0f).scaleY(hide ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(220).start();
                }
            } else {
                for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                    View v = this.listView.getChildAt(i2);
                    v.setScaleX(hide ? 0.0f : 1.0f);
                    v.setScaleY(hide ? 0.0f : 1.0f);
                    v.setAlpha(hide ? 0.0f : 1.0f);
                }
                if (!hide) {
                    f = 0.0f;
                }
                this.hideProgress = f;
            }
            invalidate();
        }
    }

    private View findScrollingChild(ViewGroup parent, float x, float y) {
        View v;
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() == 0) {
                child.getHitRect(this.rect);
                if (!this.rect.contains((int) x, (int) y)) {
                    continue;
                } else if (child.canScrollHorizontally(-1)) {
                    return child;
                } else {
                    if ((child instanceof ViewGroup) && (v = findScrollingChild((ViewGroup) child, x - ((float) this.rect.left), y - ((float) this.rect.top))) != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    public void drawForBlur(Canvas blurCanvas) {
        RecyclerListView recyclerListView;
        int i = 0;
        while (true) {
            View[] viewArr = this.viewPages;
            if (i < viewArr.length) {
                if (!(viewArr[i] == null || viewArr[i].getVisibility() != 0 || (recyclerListView = findRecyclerView(this.viewPages[i])) == null)) {
                    for (int j = 0; j < recyclerListView.getChildCount(); j++) {
                        View child = recyclerListView.getChildAt(j);
                        if (child.getY() < ((float) (AndroidUtilities.dp(203.0f) + AndroidUtilities.dp(100.0f)))) {
                            int restore = blurCanvas.save();
                            blurCanvas.translate(this.viewPages[i].getX(), getY() + this.viewPages[i].getY() + recyclerListView.getY() + child.getY());
                            child.draw(blurCanvas);
                            blurCanvas.restoreToCount(restore);
                        }
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    private RecyclerListView findRecyclerView(View view) {
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof RecyclerListView) {
                return (RecyclerListView) child;
            }
            if (child instanceof ViewGroup) {
                findRecyclerView(child);
            }
        }
        return null;
    }
}
