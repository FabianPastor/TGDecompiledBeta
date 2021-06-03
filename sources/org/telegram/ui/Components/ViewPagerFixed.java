package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ViewPagerFixed;

public class ViewPagerFixed extends FrameLayout {
    private static final Interpolator interpolator = $$Lambda$ViewPagerFixed$mWct406_sj9__iff3gN92HXHIkk.INSTANCE;
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
                float abs = Math.abs(ViewPagerFixed.this.viewPages[0].getTranslationX()) / ((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                TabsView tabsView = viewPagerFixed.tabsView;
                if (tabsView != null) {
                    tabsView.selectTab(viewPagerFixed.nextPosition, viewPagerFixed.currentPosition, 1.0f - abs);
                }
            }
        }
    };
    private VelocityTracker velocityTracker;
    /* access modifiers changed from: private */
    public View[] viewPages;
    /* access modifiers changed from: private */
    public int[] viewTypes;
    protected SparseArray<View> viewsByType = new SparseArray<>();

    public static abstract class Adapter {
        public abstract void bindView(View view, int i, int i2);

        public abstract View createView(int i);

        public abstract int getItemCount();

        public int getItemId(int i) {
            return i;
        }

        public abstract String getItemTitle(int i);

        public abstract int getItemViewType(int i);
    }

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    /* access modifiers changed from: protected */
    public void onItemSelected(View view, View view2, int i, int i2) {
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
            public void onSamePageSelected() {
            }

            public void onPageSelected(int i, boolean z) {
                boolean unused = ViewPagerFixed.this.animatingForward = z;
                ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                viewPagerFixed.nextPosition = i;
                viewPagerFixed.updateViewForIndex(1);
                if (z) {
                    ViewPagerFixed.this.viewPages[1].setTranslationX((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth());
                } else {
                    ViewPagerFixed.this.viewPages[1].setTranslationX((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth()));
                }
            }

            public void onPageScrolled(float f) {
                if (f == 1.0f) {
                    if (ViewPagerFixed.this.viewPages[1] != null) {
                        ViewPagerFixed.this.swapViews();
                        ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                        viewPagerFixed.viewsByType.put(viewPagerFixed.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                        ViewPagerFixed viewPagerFixed2 = ViewPagerFixed.this;
                        viewPagerFixed2.removeView(viewPagerFixed2.viewPages[1]);
                        ViewPagerFixed.this.viewPages[0].setTranslationX(0.0f);
                        ViewPagerFixed.this.viewPages[1] = null;
                    }
                } else if (ViewPagerFixed.this.viewPages[1] != null) {
                    if (ViewPagerFixed.this.animatingForward) {
                        ViewPagerFixed.this.viewPages[1].setTranslationX(((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * (1.0f - f));
                        ViewPagerFixed.this.viewPages[0].setTranslationX(((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth())) * f);
                        return;
                    }
                    ViewPagerFixed.this.viewPages[1].setTranslationX(((float) (-ViewPagerFixed.this.viewPages[0].getMeasuredWidth())) * (1.0f - f));
                    ViewPagerFixed.this.viewPages[0].setTranslationX(((float) ViewPagerFixed.this.viewPages[0].getMeasuredWidth()) * f);
                }
            }

            public boolean canPerformActions() {
                return !ViewPagerFixed.this.tabsAnimationInProgress && !ViewPagerFixed.this.startedTracking;
            }
        });
        fillTabs();
        return this.tabsView;
    }

    /* access modifiers changed from: private */
    public void updateViewForIndex(int i) {
        int i2 = i == 0 ? this.currentPosition : this.nextPosition;
        if (this.viewPages[i] == null) {
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view = this.viewsByType.get(this.viewTypes[i]);
            if (view == null) {
                view = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            if (view.getParent() != null) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
            addView(view);
            View[] viewArr = this.viewPages;
            viewArr[i] = view;
            this.adapter.bindView(viewArr[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else if (this.viewTypes[i] == this.adapter.getItemViewType(i2)) {
            this.adapter.bindView(this.viewPages[i], i2, this.viewTypes[i]);
            this.viewPages[i].setVisibility(0);
        } else {
            this.viewsByType.put(this.viewTypes[i], this.viewPages[i]);
            this.viewPages[i].setVisibility(8);
            removeView(this.viewPages[i]);
            this.viewTypes[i] = this.adapter.getItemViewType(i2);
            View view2 = this.viewsByType.get(this.viewTypes[i]);
            if (view2 == null) {
                view2 = this.adapter.createView(this.viewTypes[i]);
            } else {
                this.viewsByType.remove(this.viewTypes[i]);
            }
            addView(view2);
            View[] viewArr2 = this.viewPages;
            viewArr2[i] = view2;
            viewArr2[i].setVisibility(0);
            Adapter adapter2 = this.adapter;
            adapter2.bindView(this.viewPages[i], i2, adapter2.getItemViewType(i2));
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

    private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
        if ((!z && this.currentPosition == 0) || (z && this.currentPosition == this.adapter.getItemCount() - 1)) {
            return false;
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        this.maybeStartTracking = false;
        this.startedTracking = true;
        this.startedTrackingX = (int) (motionEvent.getX() + this.additionalOffset);
        TabsView tabsView2 = this.tabsView;
        if (tabsView2 != null) {
            tabsView2.setEnabled(false);
        }
        this.animatingForward = z;
        this.nextPosition = this.currentPosition + (z ? 1 : -1);
        updateViewForIndex(1);
        if (z) {
            View[] viewArr = this.viewPages;
            viewArr[1].setTranslationX((float) viewArr[0].getMeasuredWidth());
        } else {
            View[] viewArr2 = this.viewPages;
            viewArr2[1].setTranslationX((float) (-viewArr2[0].getMeasuredWidth()));
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        TabsView tabsView2 = this.tabsView;
        if (tabsView2 != null && tabsView2.isAnimatingIndicator()) {
            return false;
        }
        if (checkTabsAnimationInProgress()) {
            return true;
        }
        onTouchEvent(motionEvent);
        return this.startedTracking;
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent((MotionEvent) null);
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float f;
        float f2;
        float f3;
        int i;
        boolean z;
        View findScrollingChild;
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
        if (motionEvent != null && motionEvent.getAction() == 0 && checkTabsAnimationInProgress()) {
            this.startedTracking = true;
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            int x = (int) motionEvent.getX();
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
        } else if (motionEvent != null && motionEvent.getAction() == 0) {
            this.additionalOffset = 0.0f;
        }
        if (!this.startedTracking && motionEvent != null && (findScrollingChild = findScrollingChild(this, motionEvent.getX(), motionEvent.getY())) != null && (findScrollingChild.canScrollHorizontally(1) || findScrollingChild.canScrollHorizontally(-1))) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
            this.startedTrackingPointerId = motionEvent.getPointerId(0);
            this.maybeStartTracking = true;
            this.startedTrackingX = (int) motionEvent.getX();
            this.startedTrackingY = (int) motionEvent.getY();
        } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
            int x2 = (int) ((motionEvent.getX() - ((float) this.startedTrackingX)) + this.additionalOffset);
            int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
            if (this.startedTracking && (((z = this.animatingForward) && x2 > 0) || (!z && x2 < 0))) {
                if (!prepareForMoving(motionEvent, x2 < 0)) {
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
                int x3 = (int) (motionEvent.getX() - ((float) this.startedTrackingX));
                if (((float) Math.abs(x3)) >= this.touchSlop && Math.abs(x3) > abs) {
                    prepareForMoving(motionEvent, x2 < 0);
                }
            } else if (this.startedTracking) {
                this.viewPages[0].setTranslationX((float) x2);
                if (this.animatingForward) {
                    View[] viewArr2 = this.viewPages;
                    viewArr2[1].setTranslationX((float) (viewArr2[0].getMeasuredWidth() + x2));
                } else {
                    View[] viewArr3 = this.viewPages;
                    viewArr3[1].setTranslationX((float) (x2 - viewArr3[0].getMeasuredWidth()));
                }
                float abs2 = ((float) Math.abs(x2)) / ((float) this.viewPages[0].getMeasuredWidth());
                TabsView tabsView4 = this.tabsView;
                if (tabsView4 != null) {
                    tabsView4.selectTab(this.nextPosition, this.currentPosition, 1.0f - abs2);
                }
            }
        } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
            this.velocityTracker.computeCurrentVelocity(1000, (float) this.maximumVelocity);
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
                float x4 = this.viewPages[0].getX();
                this.tabsAnimation = new AnimatorSet();
                if (this.additionalOffset == 0.0f) {
                    this.backAnimation = Math.abs(x4) < ((float) this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f2) < 3500.0f || Math.abs(f2) < Math.abs(f));
                } else if (Math.abs(f2) > 1500.0f) {
                    this.backAnimation = !this.animatingForward ? f2 < 0.0f : f2 > 0.0f;
                } else if (this.animatingForward) {
                    this.backAnimation = this.viewPages[1].getX() > ((float) (this.viewPages[0].getMeasuredWidth() >> 1));
                } else {
                    this.backAnimation = this.viewPages[0].getX() < ((float) (this.viewPages[0].getMeasuredWidth() >> 1));
                }
                if (this.backAnimation) {
                    f3 = Math.abs(x4);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet = this.tabsAnimation;
                        View[] viewArr4 = this.viewPages;
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(viewArr4[1], View.TRANSLATION_X, new float[]{(float) viewArr4[1].getMeasuredWidth()})});
                    } else {
                        AnimatorSet animatorSet2 = this.tabsAnimation;
                        View[] viewArr5 = this.viewPages;
                        animatorSet2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.viewPages[0], View.TRANSLATION_X, new float[]{0.0f}), ObjectAnimator.ofFloat(viewArr5[1], View.TRANSLATION_X, new float[]{(float) (-viewArr5[1].getMeasuredWidth())})});
                    }
                } else {
                    f3 = ((float) this.viewPages[0].getMeasuredWidth()) - Math.abs(x4);
                    if (this.animatingForward) {
                        AnimatorSet animatorSet3 = this.tabsAnimation;
                        View[] viewArr6 = this.viewPages;
                        animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewArr6[0], View.TRANSLATION_X, new float[]{(float) (-viewArr6[0].getMeasuredWidth())}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    } else {
                        AnimatorSet animatorSet4 = this.tabsAnimation;
                        View[] viewArr7 = this.viewPages;
                        animatorSet4.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewArr7[0], View.TRANSLATION_X, new float[]{(float) viewArr7[0].getMeasuredWidth()}), ObjectAnimator.ofFloat(this.viewPages[1], View.TRANSLATION_X, new float[]{0.0f})});
                    }
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                ofFloat.addUpdateListener(this.updateTabProgress);
                this.tabsAnimation.playTogether(new Animator[]{ofFloat});
                this.tabsAnimation.setInterpolator(interpolator);
                int measuredWidth = getMeasuredWidth();
                float f4 = (float) (measuredWidth / 2);
                float distanceInfluenceForSnapDuration = f4 + (distanceInfluenceForSnapDuration(Math.min(1.0f, (f3 * 1.0f) / ((float) measuredWidth))) * f4);
                float abs3 = Math.abs(f2);
                if (abs3 > 0.0f) {
                    i = Math.round(Math.abs(distanceInfluenceForSnapDuration / abs3) * 1000.0f) * 4;
                } else {
                    i = (int) (((f3 / ((float) getMeasuredWidth())) + 1.0f) * 100.0f);
                }
                this.tabsAnimation.setDuration((long) Math.max(150, Math.min(i, 600)));
                this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        AnimatorSet unused = ViewPagerFixed.this.tabsAnimation = null;
                        if (ViewPagerFixed.this.viewPages[1] != null) {
                            if (!ViewPagerFixed.this.backAnimation) {
                                ViewPagerFixed.this.swapViews();
                            }
                            ViewPagerFixed viewPagerFixed = ViewPagerFixed.this;
                            viewPagerFixed.viewsByType.put(viewPagerFixed.viewTypes[1], ViewPagerFixed.this.viewPages[1]);
                            ViewPagerFixed viewPagerFixed2 = ViewPagerFixed.this;
                            viewPagerFixed2.removeView(viewPagerFixed2.viewPages[1]);
                            ViewPagerFixed.this.viewPages[1].setVisibility(8);
                            ViewPagerFixed.this.viewPages[1] = null;
                        }
                        boolean unused2 = ViewPagerFixed.this.tabsAnimationInProgress = false;
                        boolean unused3 = ViewPagerFixed.this.maybeStartTracking = false;
                        TabsView tabsView = ViewPagerFixed.this.tabsView;
                        if (tabsView != null) {
                            tabsView.setEnabled(true);
                        }
                    }
                });
                this.tabsAnimation.start();
                this.tabsAnimationInProgress = true;
                this.startedTracking = false;
            } else {
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
        if (this.startedTracking || this.maybeStartTracking) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void swapViews() {
        View[] viewArr = this.viewPages;
        View view = viewArr[0];
        viewArr[0] = viewArr[1];
        viewArr[1] = view;
        int i = this.currentPosition;
        int i2 = this.nextPosition;
        this.currentPosition = i2;
        this.nextPosition = i;
        int[] iArr = this.viewTypes;
        int i3 = iArr[0];
        iArr[0] = iArr[1];
        iArr[1] = i3;
        onItemSelected(viewArr[0], viewArr[1], i2, i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x006c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkTabsAnimationInProgress() {
        /*
            r7 = this;
            boolean r0 = r7.tabsAnimationInProgress
            r1 = 0
            if (r0 == 0) goto L_0x007b
            boolean r0 = r7.backAnimation
            r2 = -1
            r3 = 0
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 1
            if (r0 == 0) goto L_0x003b
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r1]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0069
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r1]
            r0.setTranslationX(r3)
            android.view.View[] r0 = r7.viewPages
            r3 = r0[r5]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r4 = r7.animatingForward
            if (r4 == 0) goto L_0x0034
            r2 = 1
        L_0x0034:
            int r0 = r0 * r2
            float r0 = (float) r0
            r3.setTranslationX(r0)
            goto L_0x006a
        L_0x003b:
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r5]
            float r0 = r0.getTranslationX()
            float r0 = java.lang.Math.abs(r0)
            int r0 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r0 >= 0) goto L_0x0069
            android.view.View[] r0 = r7.viewPages
            r4 = r0[r1]
            r0 = r0[r1]
            int r0 = r0.getMeasuredWidth()
            boolean r6 = r7.animatingForward
            if (r6 == 0) goto L_0x005a
            goto L_0x005b
        L_0x005a:
            r2 = 1
        L_0x005b:
            int r0 = r0 * r2
            float r0 = (float) r0
            r4.setTranslationX(r0)
            android.view.View[] r0 = r7.viewPages
            r0 = r0[r5]
            r0.setTranslationX(r3)
            goto L_0x006a
        L_0x0069:
            r5 = 0
        L_0x006a:
            if (r5 == 0) goto L_0x0078
            android.animation.AnimatorSet r0 = r7.tabsAnimation
            if (r0 == 0) goto L_0x0076
            r0.cancel()
            r0 = 0
            r7.tabsAnimation = r0
        L_0x0076:
            r7.tabsAnimationInProgress = r1
        L_0x0078:
            boolean r0 = r7.tabsAnimationInProgress
            return r0
        L_0x007b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.checkTabsAnimationInProgress():boolean");
    }

    public static float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    public void setPosition(int i) {
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
        int i2 = this.currentPosition;
        if (i2 != i) {
            this.currentPosition = i;
            View view = this.viewPages[0];
            updateViewForIndex(0);
            onItemSelected(this.viewPages[0], view, this.currentPosition, i2);
            this.viewPages[0].setTranslationX(0.0f);
            TabsView tabsView2 = this.tabsView;
            if (tabsView2 != null) {
                tabsView2.selectTab(i, 0, 1.0f);
            }
        }
    }

    public boolean canScrollHorizontally(int i) {
        if (i == 0) {
            return false;
        }
        if (!this.tabsAnimationInProgress && !this.startedTracking) {
            boolean z = i > 0;
            return (z || this.currentPosition != 0) && (!z || this.currentPosition != this.adapter.getItemCount() - 1);
        }
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
                    long elapsedRealtime = SystemClock.elapsedRealtime() - TabsView.this.lastAnimationTime;
                    if (elapsedRealtime > 17) {
                        elapsedRealtime = 17;
                    }
                    TabsView.access$2816(TabsView.this, ((float) elapsedRealtime) / 200.0f);
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
        /* access modifiers changed from: private */
        public float animationTime;
        /* access modifiers changed from: private */
        public String backgroundColorKey = "actionBarDefault";
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

            void onPageScrolled(float f);

            void onPageSelected(int i, boolean z);

            void onSamePageSelected();
        }

        static /* synthetic */ void lambda$setIsEditing$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        static /* synthetic */ float access$2816(TabsView tabsView, float f) {
            float f2 = tabsView.animationTime + f;
            tabsView.animationTime = f2;
            return f2;
        }

        private static class Tab {
            public int counter;
            public int id;
            public String title;
            public int titleWidth;

            public Tab(int i, String str) {
                this.id = i;
                this.title = str;
            }

            public int getWidth(boolean z, TextPaint textPaint) {
                int ceil = (int) Math.ceil((double) textPaint.measureText(this.title));
                this.titleWidth = ceil;
                return Math.max(AndroidUtilities.dp(40.0f), ceil);
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

            public void setTab(Tab tab, int i) {
                this.currentTab = tab;
                this.currentPosition = i;
                setContentDescription(tab.title);
                requestLayout();
            }

            public int getId() {
                return this.currentTab.id;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                setMeasuredDimension(this.currentTab.getWidth(false, TabsView.this.textPaint) + AndroidUtilities.dp(32.0f) + TabsView.this.additionalTabWidth, View.MeasureSpec.getSize(i2));
            }

            /* access modifiers changed from: protected */
            @SuppressLint({"DrawAllocation"})
            public void onDraw(Canvas canvas) {
                int i;
                int i2;
                String str;
                String str2;
                int i3;
                int i4;
                String str3;
                int i5;
                int i6;
                int i7;
                Canvas canvas2 = canvas;
                if (!(this.currentTab.id == Integer.MAX_VALUE || TabsView.this.editingAnimationProgress == 0.0f)) {
                    canvas.save();
                    float access$1300 = TabsView.this.editingAnimationProgress * (this.currentPosition % 2 == 0 ? 1.0f : -1.0f);
                    canvas2.translate(((float) AndroidUtilities.dp(0.66f)) * access$1300, 0.0f);
                    canvas2.rotate(access$1300, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
                }
                if (TabsView.this.manualScrollingToId != -1) {
                    i2 = TabsView.this.manualScrollingToId;
                    i = TabsView.this.selectedTabId;
                } else {
                    i2 = TabsView.this.selectedTabId;
                    i = TabsView.this.previousId;
                }
                String str4 = "chats_tabUnreadActiveBackground";
                String str5 = "chats_tabUnreadUnactiveBackground";
                if (this.currentTab.id == i2) {
                    str2 = TabsView.this.activeTextColorKey;
                    str = TabsView.this.unactiveTextColorKey;
                } else {
                    str2 = TabsView.this.unactiveTextColorKey;
                    str = TabsView.this.activeTextColorKey;
                    String str6 = str5;
                    str5 = str4;
                    str4 = str6;
                }
                if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToId != -1) && ((i7 = this.currentTab.id) == i2 || i7 == i)) {
                    TabsView.this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str), Theme.getColor(str2), TabsView.this.animatingIndicatorProgress));
                } else {
                    TabsView.this.textPaint.setColor(Theme.getColor(str2));
                }
                int i8 = this.currentTab.counter;
                if (i8 > 0) {
                    str3 = String.format("%d", new Object[]{Integer.valueOf(i8)});
                    i4 = (int) Math.ceil((double) TabsView.this.textCounterPaint.measureText(str3));
                    i3 = Math.max(AndroidUtilities.dp(10.0f), i4) + AndroidUtilities.dp(10.0f);
                } else {
                    str3 = null;
                    i4 = 0;
                    i3 = 0;
                }
                if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                    i3 = (int) (((float) i3) + (((float) (AndroidUtilities.dp(20.0f) - i3)) * TabsView.this.editingStartAnimationProgress));
                }
                int i9 = this.currentTab.titleWidth;
                if (i3 != 0) {
                    i5 = AndroidUtilities.dp((str3 != null ? 1.0f : TabsView.this.editingStartAnimationProgress) * 6.0f) + i3;
                } else {
                    i5 = 0;
                }
                this.tabWidth = i9 + i5;
                int measuredWidth = (getMeasuredWidth() - this.tabWidth) / 2;
                if (!TextUtils.equals(this.currentTab.title, this.currentText)) {
                    String str7 = this.currentTab.title;
                    this.currentText = str7;
                    StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(str7, TabsView.this.textPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), TabsView.this.textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.textLayout = staticLayout;
                    this.textHeight = staticLayout.getHeight();
                    this.textOffsetX = (int) (-this.textLayout.getLineLeft(0));
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas2.translate((float) (this.textOffsetX + measuredWidth), (float) (((getMeasuredHeight() - this.textHeight) / 2) + 1));
                    this.textLayout.draw(canvas2);
                    canvas.restore();
                }
                if (str3 != null || (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f))) {
                    TabsView.this.textCounterPaint.setColor(Theme.getColor(TabsView.this.backgroundColorKey));
                    if (!Theme.hasThemeKey(str4) || !Theme.hasThemeKey(str5)) {
                        TabsView.this.counterPaint.setColor(TabsView.this.textPaint.getColor());
                    } else {
                        int color = Theme.getColor(str4);
                        if ((TabsView.this.animatingIndicator || TabsView.this.manualScrollingToPosition != -1) && ((i6 = this.currentTab.id) == i2 || i6 == i)) {
                            TabsView.this.counterPaint.setColor(ColorUtils.blendARGB(Theme.getColor(str5), color, TabsView.this.animatingIndicatorProgress));
                        } else {
                            TabsView.this.counterPaint.setColor(color);
                        }
                    }
                    int dp = measuredWidth + this.currentTab.titleWidth + AndroidUtilities.dp(6.0f);
                    int measuredHeight = (getMeasuredHeight() - AndroidUtilities.dp(20.0f)) / 2;
                    if (this.currentTab.id == Integer.MAX_VALUE || ((!TabsView.this.isEditing && TabsView.this.editingStartAnimationProgress == 0.0f) || str3 != null)) {
                        TabsView.this.counterPaint.setAlpha(255);
                    } else {
                        TabsView.this.counterPaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                    }
                    this.rect.set((float) dp, (float) measuredHeight, (float) (dp + i3), (float) (AndroidUtilities.dp(20.0f) + measuredHeight));
                    RectF rectF = this.rect;
                    float f = AndroidUtilities.density;
                    canvas2.drawRoundRect(rectF, f * 11.5f, f * 11.5f, TabsView.this.counterPaint);
                    if (str3 != null) {
                        if (this.currentTab.id != Integer.MAX_VALUE) {
                            TabsView.this.textCounterPaint.setAlpha((int) ((1.0f - TabsView.this.editingStartAnimationProgress) * 255.0f));
                        }
                        RectF rectF2 = this.rect;
                        canvas2.drawText(str3, rectF2.left + ((rectF2.width() - ((float) i4)) / 2.0f), (float) (measuredHeight + AndroidUtilities.dp(14.5f)), TabsView.this.textCounterPaint);
                    }
                    if (this.currentTab.id != Integer.MAX_VALUE && (TabsView.this.isEditing || TabsView.this.editingStartAnimationProgress != 0.0f)) {
                        TabsView.this.deletePaint.setColor(TabsView.this.textCounterPaint.getColor());
                        TabsView.this.deletePaint.setAlpha((int) (TabsView.this.editingStartAnimationProgress * 255.0f));
                        float dp2 = (float) AndroidUtilities.dp(3.0f);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() - dp2, this.rect.centerX() + dp2, this.rect.centerY() + dp2, TabsView.this.deletePaint);
                        canvas.drawLine(this.rect.centerX() - dp2, this.rect.centerY() + dp2, this.rect.centerX() + dp2, this.rect.centerY() - dp2, TabsView.this.deletePaint);
                    }
                }
                if (this.currentTab.id != Integer.MAX_VALUE && TabsView.this.editingAnimationProgress != 0.0f) {
                    canvas.restore();
                }
            }

            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setSelected((this.currentTab == null || TabsView.this.selectedTabId == -1 || this.currentTab.id != TabsView.this.selectedTabId) ? false : true);
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
            float dpf2 = AndroidUtilities.dpf2(3.0f);
            this.selectorDrawable.setCornerRadii(new float[]{dpf2, dpf2, dpf2, dpf2, 0.0f, 0.0f, 0.0f, 0.0f});
            this.selectorDrawable.setColor(Theme.getColor(this.tabLineColorKey));
            setHorizontalScrollBarEnabled(false);
            AnonymousClass2 r3 = new RecyclerListView(context) {
                public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
                    super.addView(view, i, layoutParams);
                    if (TabsView.this.isInHiddenMode) {
                        view.setScaleX(0.3f);
                        view.setScaleY(0.3f);
                        view.setAlpha(0.0f);
                        return;
                    }
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    view.setAlpha(1.0f);
                }

                public void setAlpha(float f) {
                    super.setAlpha(f);
                    TabsView.this.invalidate();
                }

                /* access modifiers changed from: protected */
                public boolean canHighlightChildAt(View view, float f, float f2) {
                    if (TabsView.this.isEditing) {
                        TabView tabView = (TabView) view;
                        float dp = (float) AndroidUtilities.dp(6.0f);
                        if (tabView.rect.left - dp < f && tabView.rect.right + dp > f) {
                            return false;
                        }
                    }
                    return super.canHighlightChildAt(view, f, f2);
                }
            };
            this.listView = r3;
            ((DefaultItemAnimator) r3.getItemAnimator()).setDelayAnimations(false);
            this.listView.setSelectorType(7);
            this.listView.setSelectorDrawableColor(Theme.getColor(this.selectorColorKey));
            RecyclerListView recyclerListView = this.listView;
            AnonymousClass3 r32 = new LinearLayoutManager(context, 0, false) {
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
                    AnonymousClass1 r2 = new LinearSmoothScroller(recyclerView.getContext()) {
                        /* access modifiers changed from: protected */
                        public void onTargetFound(View view, RecyclerView.State state, RecyclerView.SmoothScroller.Action action) {
                            int calculateDxToMakeVisible = calculateDxToMakeVisible(view, getHorizontalSnapPreference());
                            if (calculateDxToMakeVisible > 0 || (calculateDxToMakeVisible == 0 && view.getLeft() - AndroidUtilities.dp(21.0f) < 0)) {
                                calculateDxToMakeVisible += AndroidUtilities.dp(60.0f);
                            } else if (calculateDxToMakeVisible < 0 || (calculateDxToMakeVisible == 0 && view.getRight() + AndroidUtilities.dp(21.0f) > TabsView.this.getMeasuredWidth())) {
                                calculateDxToMakeVisible -= AndroidUtilities.dp(60.0f);
                            }
                            int calculateDyToMakeVisible = calculateDyToMakeVisible(view, getVerticalSnapPreference());
                            int max = Math.max(180, calculateTimeForDeceleration((int) Math.sqrt((double) ((calculateDxToMakeVisible * calculateDxToMakeVisible) + (calculateDyToMakeVisible * calculateDyToMakeVisible)))));
                            if (max > 0) {
                                action.update(-calculateDxToMakeVisible, -calculateDyToMakeVisible, max, this.mDecelerateInterpolator);
                            }
                        }
                    };
                    r2.setTargetPosition(i);
                    startSmoothScroll(r2);
                }

                public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    return super.scrollHorizontallyBy(i, recycler, state);
                }

                public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler recycler, RecyclerView.State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
                    super.onInitializeAccessibilityNodeInfo(recycler, state, accessibilityNodeInfoCompat);
                    if (TabsView.this.isInHiddenMode) {
                        accessibilityNodeInfoCompat.setVisibleToUser(false);
                    }
                }
            };
            this.layoutManager = r32;
            recyclerListView.setLayoutManager(r32);
            this.listView.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), 0);
            this.listView.setClipToPadding(false);
            this.listView.setDrawSelectorBehind(true);
            RecyclerListView recyclerListView2 = this.listView;
            ListAdapter listAdapter = new ListAdapter(context);
            this.adapter = listAdapter;
            recyclerListView2.setAdapter(listAdapter);
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
                public final void onItemClick(View view, int i, float f, float f2) {
                    ViewPagerFixed.TabsView.this.lambda$new$0$ViewPagerFixed$TabsView(view, i, f, f2);
                }
            });
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    TabsView.this.invalidate();
                }
            });
            addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$ViewPagerFixed$TabsView(View view, int i, float f, float f2) {
            TabsViewDelegate tabsViewDelegate;
            if (this.delegate.canPerformActions()) {
                TabView tabView = (TabView) view;
                if (i != this.currentPosition || (tabsViewDelegate = this.delegate) == null) {
                    scrollToTab(tabView.currentTab.id, i);
                } else {
                    tabsViewDelegate.onSamePageSelected();
                }
            }
        }

        public void setDelegate(TabsViewDelegate tabsViewDelegate) {
            this.delegate = tabsViewDelegate;
        }

        public boolean isAnimatingIndicator() {
            return this.animatingIndicator;
        }

        public void scrollToTab(int i, int i2) {
            int i3 = this.currentPosition;
            boolean z = i3 < i2;
            this.scrollingToChild = -1;
            this.previousPosition = i3;
            this.previousId = this.selectedTabId;
            this.currentPosition = i2;
            this.selectedTabId = i;
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
                tabsViewDelegate.onPageSelected(i, z);
            }
            scrollToChild(i2);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.tabsAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    TabsView.this.setAnimationIdicatorProgress(floatValue);
                    if (TabsView.this.delegate != null) {
                        TabsView.this.delegate.onPageScrolled(floatValue);
                    }
                }
            });
            this.tabsAnimator.setDuration(250);
            this.tabsAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.tabsAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
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

        public void setAnimationIdicatorProgress(float f) {
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            TabsViewDelegate tabsViewDelegate = this.delegate;
            if (tabsViewDelegate != null) {
                tabsViewDelegate.onPageScrolled(f);
            }
        }

        public Drawable getSelectorDrawable() {
            return this.selectorDrawable;
        }

        public RecyclerListView getTabsContainer() {
            return this.listView;
        }

        public void addTab(int i, String str) {
            int size = this.tabs.size();
            if (size == 0 && this.selectedTabId == -1) {
                this.selectedTabId = i;
            }
            this.positionToId.put(size, i);
            this.idToPosition.put(i, size);
            int i2 = this.selectedTabId;
            if (i2 != -1 && i2 == i) {
                this.currentPosition = size;
            }
            Tab tab = new Tab(i, str);
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

        public int getCurrentTabId() {
            return this.selectedTabId;
        }

        public int getFirstTabId() {
            return this.positionToId.get(0, 0);
        }

        private void updateTabsWidths() {
            this.positionToX.clear();
            this.positionToWidth.clear();
            int dp = AndroidUtilities.dp(7.0f);
            int size = this.tabs.size();
            for (int i = 0; i < size; i++) {
                int width = this.tabs.get(i).getWidth(false, this.textPaint);
                this.positionToWidth.put(i, width);
                this.positionToX.put(i, (this.additionalTabWidth / 2) + dp);
                dp += width + AndroidUtilities.dp(32.0f) + this.additionalTabWidth;
            }
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:21:0x005b  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0089  */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00fb  */
        /* JADX WARNING: Removed duplicated region for block: B:42:0x012a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
            /*
                r9 = this;
                boolean r12 = super.drawChild(r10, r11, r12)
                org.telegram.ui.Components.RecyclerListView r13 = r9.listView
                if (r11 != r13) goto L_0x013b
                int r11 = r9.getMeasuredHeight()
                boolean r13 = r9.isInHiddenMode
                r0 = 0
                if (r13 == 0) goto L_0x0029
                float r1 = r9.hideProgress
                r2 = 1065353216(0x3var_, float:1.0)
                int r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r3 == 0) goto L_0x0029
                r13 = 1036831949(0x3dcccccd, float:0.1)
                float r1 = r1 + r13
                r9.hideProgress = r1
                int r13 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r13 <= 0) goto L_0x0025
                r9.hideProgress = r2
            L_0x0025:
                r9.invalidate()
                goto L_0x0040
            L_0x0029:
                if (r13 != 0) goto L_0x0040
                float r13 = r9.hideProgress
                int r1 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
                if (r1 == 0) goto L_0x0040
                r1 = 1039516303(0x3df5CLASSNAMEf, float:0.12)
                float r13 = r13 - r1
                r9.hideProgress = r13
                int r13 = (r13 > r0 ? 1 : (r13 == r0 ? 0 : -1))
                if (r13 >= 0) goto L_0x003d
                r9.hideProgress = r0
            L_0x003d:
                r9.invalidate()
            L_0x0040:
                android.graphics.drawable.GradientDrawable r13 = r9.selectorDrawable
                org.telegram.ui.Components.RecyclerListView r1 = r9.listView
                float r1 = r1.getAlpha()
                r2 = 1132396544(0x437var_, float:255.0)
                float r1 = r1 * r2
                int r1 = (int) r1
                r13.setAlpha(r1)
                boolean r13 = r9.animatingIndicator
                r1 = -1
                r3 = 0
                if (r13 != 0) goto L_0x0089
                int r13 = r9.manualScrollingToPosition
                if (r13 == r1) goto L_0x005b
                goto L_0x0089
            L_0x005b:
                org.telegram.ui.Components.RecyclerListView r13 = r9.listView
                int r1 = r9.currentPosition
                androidx.recyclerview.widget.RecyclerView$ViewHolder r13 = r13.findViewHolderForAdapterPosition(r1)
                if (r13 == 0) goto L_0x0087
                android.view.View r13 = r13.itemView
                org.telegram.ui.Components.ViewPagerFixed$TabsView$TabView r13 = (org.telegram.ui.Components.ViewPagerFixed.TabsView.TabView) r13
                r1 = 1109393408(0x42200000, float:40.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r3 = r13.tabWidth
                int r3 = java.lang.Math.max(r1, r3)
                float r1 = r13.getX()
                int r13 = r13.getMeasuredWidth()
                int r13 = r13 - r3
                int r13 = r13 / 2
                float r13 = (float) r13
                float r1 = r1 + r13
                int r13 = (int) r1
                goto L_0x00f9
            L_0x0087:
                r13 = 0
                goto L_0x00f9
            L_0x0089:
                androidx.recyclerview.widget.LinearLayoutManager r13 = r9.layoutManager
                int r13 = r13.findFirstVisibleItemPosition()
                if (r13 == r1) goto L_0x0087
                org.telegram.ui.Components.RecyclerListView r1 = r9.listView
                androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findViewHolderForAdapterPosition(r13)
                if (r1 == 0) goto L_0x0087
                boolean r3 = r9.animatingIndicator
                if (r3 == 0) goto L_0x00a2
                int r3 = r9.previousPosition
                int r4 = r9.currentPosition
                goto L_0x00a6
            L_0x00a2:
                int r3 = r9.currentPosition
                int r4 = r9.manualScrollingToPosition
            L_0x00a6:
                android.util.SparseIntArray r5 = r9.positionToX
                int r5 = r5.get(r3)
                android.util.SparseIntArray r6 = r9.positionToX
                int r6 = r6.get(r4)
                android.util.SparseIntArray r7 = r9.positionToWidth
                int r3 = r7.get(r3)
                android.util.SparseIntArray r7 = r9.positionToWidth
                int r4 = r7.get(r4)
                int r7 = r9.additionalTabWidth
                r8 = 1098907648(0x41800000, float:16.0)
                if (r7 == 0) goto L_0x00d3
                float r13 = (float) r5
                int r6 = r6 - r5
                float r1 = (float) r6
                float r5 = r9.animatingIndicatorProgress
                float r1 = r1 * r5
                float r13 = r13 + r1
                int r13 = (int) r13
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r13 = r13 + r1
                goto L_0x00ef
            L_0x00d3:
                android.util.SparseIntArray r7 = r9.positionToX
                int r13 = r7.get(r13)
                float r7 = (float) r5
                int r6 = r6 - r5
                float r5 = (float) r6
                float r6 = r9.animatingIndicatorProgress
                float r5 = r5 * r6
                float r7 = r7 + r5
                int r5 = (int) r7
                android.view.View r1 = r1.itemView
                int r1 = r1.getLeft()
                int r13 = r13 - r1
                int r5 = r5 - r13
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r13 = r13 + r5
            L_0x00ef:
                float r1 = (float) r3
                int r4 = r4 - r3
                float r3 = (float) r4
                float r4 = r9.animatingIndicatorProgress
                float r3 = r3 * r4
                float r1 = r1 + r3
                int r1 = (int) r1
                r3 = r1
            L_0x00f9:
                if (r3 == 0) goto L_0x0126
                android.graphics.drawable.GradientDrawable r1 = r9.selectorDrawable
                r4 = 1082130432(0x40800000, float:4.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dpr(r4)
                int r5 = r11 - r5
                float r5 = (float) r5
                float r6 = r9.hideProgress
                int r7 = org.telegram.messenger.AndroidUtilities.dpr(r4)
                float r7 = (float) r7
                float r6 = r6 * r7
                float r5 = r5 + r6
                int r5 = (int) r5
                int r3 = r3 + r13
                float r11 = (float) r11
                float r6 = r9.hideProgress
                int r4 = org.telegram.messenger.AndroidUtilities.dpr(r4)
                float r4 = (float) r4
                float r6 = r6 * r4
                float r11 = r11 + r6
                int r11 = (int) r11
                r1.setBounds(r13, r5, r3, r11)
                android.graphics.drawable.GradientDrawable r11 = r9.selectorDrawable
                r11.draw(r10)
            L_0x0126:
                android.graphics.Bitmap r11 = r9.crossfadeBitmap
                if (r11 == 0) goto L_0x013b
                android.graphics.Paint r11 = r9.crossfadePaint
                float r13 = r9.crossfadeAlpha
                float r13 = r13 * r2
                int r13 = (int) r13
                r11.setAlpha(r13)
                android.graphics.Bitmap r11 = r9.crossfadeBitmap
                android.graphics.Paint r13 = r9.crossfadePaint
                r10.drawBitmap(r11, r0, r0, r13)
            L_0x013b:
                return r12
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ViewPagerFixed.TabsView.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            if (!this.tabs.isEmpty()) {
                int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(7.0f)) - AndroidUtilities.dp(7.0f);
                int i3 = this.additionalTabWidth;
                int i4 = this.allTabsWidth;
                int size2 = i4 < size ? (size - i4) / this.tabs.size() : 0;
                this.additionalTabWidth = size2;
                if (i3 != size2) {
                    this.ignoreLayout = true;
                    this.adapter.notifyDataSetChanged();
                    this.ignoreLayout = false;
                }
                updateTabsWidths();
                this.invalidated = false;
            }
            super.onMeasure(i, i2);
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

        private void scrollToChild(int i) {
            if (!this.tabs.isEmpty() && this.scrollingToChild != i && i >= 0 && i < this.tabs.size()) {
                this.scrollingToChild = i;
                this.listView.smoothScrollToPosition(i);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int i5 = i3 - i;
            if (this.prevLayoutWidth != i5) {
                this.prevLayoutWidth = i5;
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

        public void selectTab(int i, int i2, float f) {
            if (f < 0.0f) {
                f = 0.0f;
            } else if (f > 1.0f) {
                f = 1.0f;
            }
            this.currentPosition = i;
            this.selectedTabId = this.positionToId.get(i);
            if (f > 0.0f) {
                this.manualScrollingToPosition = i2;
                this.manualScrollingToId = this.positionToId.get(i2);
            } else {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
            }
            this.animatingIndicatorProgress = f;
            this.listView.invalidateViews();
            invalidate();
            scrollToChild(i);
            if (f >= 1.0f) {
                this.manualScrollingToPosition = -1;
                this.manualScrollingToId = -1;
                this.currentPosition = i2;
                this.selectedTabId = this.positionToId.get(i2);
            }
        }

        public void selectTabWithId(int i, float f) {
            int i2 = this.idToPosition.get(i, -1);
            if (i2 >= 0) {
                if (f < 0.0f) {
                    f = 0.0f;
                } else if (f > 1.0f) {
                    f = 1.0f;
                }
                if (f > 0.0f) {
                    this.manualScrollingToPosition = i2;
                    this.manualScrollingToId = i;
                } else {
                    this.manualScrollingToPosition = -1;
                    this.manualScrollingToId = -1;
                }
                this.animatingIndicatorProgress = f;
                this.listView.invalidateViews();
                invalidate();
                scrollToChild(i2);
                if (f >= 1.0f) {
                    this.manualScrollingToPosition = -1;
                    this.manualScrollingToId = -1;
                    this.currentPosition = i2;
                    this.selectedTabId = i;
                }
            }
        }

        public void setIsEditing(boolean z) {
            this.isEditing = z;
            this.editingForwardAnimation = true;
            this.listView.invalidateViews();
            invalidate();
            if (!this.isEditing && this.orderChanged) {
                MessagesStorage.getInstance(UserConfig.selectedAccount).saveDialogFiltersOrder();
                TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
                ArrayList<MessagesController.DialogFilter> arrayList = MessagesController.getInstance(UserConfig.selectedAccount).dialogFilters;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    MessagesController.DialogFilter dialogFilter = arrayList.get(i);
                    tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).id));
                }
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, $$Lambda$ViewPagerFixed$TabsView$sd9SljbBaEZvlgDYYBs16aydM.INSTANCE);
                this.orderChanged = false;
            }
        }

        private class ListAdapter extends RecyclerListView.SelectionAdapter {
            private Context mContext;

            public long getItemId(int i) {
                return (long) i;
            }

            public int getItemViewType(int i) {
                return 0;
            }

            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            public ListAdapter(Context context) {
                this.mContext = context;
            }

            public int getItemCount() {
                return TabsView.this.tabs.size();
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new RecyclerListView.Holder(new TabView(this.mContext));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((TabView) viewHolder.itemView).setTab((Tab) TabsView.this.tabs.get(i), i);
            }
        }

        public void hide(boolean z, boolean z2) {
            this.isInHiddenMode = z;
            int i = 0;
            float f = 1.0f;
            if (z2) {
                while (i < this.listView.getChildCount()) {
                    this.listView.getChildAt(i).animate().alpha(z ? 0.0f : 1.0f).scaleX(z ? 0.0f : 1.0f).scaleY(z ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(220).start();
                    i++;
                }
            } else {
                while (i < this.listView.getChildCount()) {
                    View childAt = this.listView.getChildAt(i);
                    childAt.setScaleX(z ? 0.0f : 1.0f);
                    childAt.setScaleY(z ? 0.0f : 1.0f);
                    childAt.setAlpha(z ? 0.0f : 1.0f);
                    i++;
                }
                if (!z) {
                    f = 0.0f;
                }
                this.hideProgress = f;
            }
            invalidate();
        }
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else if (childAt.canScrollHorizontally(-1)) {
                    return childAt;
                } else {
                    if (childAt instanceof ViewGroup) {
                        Rect rect2 = this.rect;
                        View findScrollingChild = findScrollingChild((ViewGroup) childAt, f - ((float) rect2.left), f2 - ((float) rect2.top));
                        if (findScrollingChild != null) {
                            return findScrollingChild;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }
}
