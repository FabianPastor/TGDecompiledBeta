package org.telegram.ui.ActionBar;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;

public class AdjustPanLayoutHelper {
    public static final Interpolator keyboardInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    ValueAnimator animator;
    boolean checkHierarchyHeight;
    /* access modifiers changed from: private */
    public ViewGroup contentView;
    private Runnable delayedAnimationRunnable = new Runnable() {
        public void run() {
            ValueAnimator valueAnimator = AdjustPanLayoutHelper.this.animator;
            if (valueAnimator != null && !valueAnimator.isRunning()) {
                AdjustPanLayoutHelper.this.animator.start();
            }
        }
    };
    protected float keyboardSize;
    private boolean needDelay;
    int notificationsIndex;
    ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        public boolean onPreDraw() {
            boolean z = true;
            if (!SharedConfig.smoothKeyboard) {
                AdjustPanLayoutHelper.this.onDetach();
                return true;
            }
            int height = AdjustPanLayoutHelper.this.parent.getHeight();
            int startOffset = height - AdjustPanLayoutHelper.this.startOffset();
            AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
            int i = adjustPanLayoutHelper.previousHeight;
            if (startOffset == i - adjustPanLayoutHelper.previousStartOffset || height == i || adjustPanLayoutHelper.animator != null) {
                if (adjustPanLayoutHelper.animator == null) {
                    adjustPanLayoutHelper.previousHeight = height;
                    adjustPanLayoutHelper.previousContentHeight = adjustPanLayoutHelper.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper2.previousStartOffset = adjustPanLayoutHelper2.startOffset();
                }
                return true;
            } else if (!adjustPanLayoutHelper.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - height) < AndroidUtilities.dp(20.0f)) {
                AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper3.previousHeight = height;
                adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                return true;
            } else {
                AdjustPanLayoutHelper adjustPanLayoutHelper5 = AdjustPanLayoutHelper.this;
                if (adjustPanLayoutHelper5.previousHeight == -1 || adjustPanLayoutHelper5.previousContentHeight != adjustPanLayoutHelper5.contentView.getHeight()) {
                    AdjustPanLayoutHelper adjustPanLayoutHelper6 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper6.previousHeight = height;
                    adjustPanLayoutHelper6.previousContentHeight = adjustPanLayoutHelper6.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper7 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper7.previousStartOffset = adjustPanLayoutHelper7.startOffset();
                    return false;
                }
                if (height >= AdjustPanLayoutHelper.this.contentView.getBottom()) {
                    z = false;
                }
                AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper8.animateHeight(adjustPanLayoutHelper8.previousHeight, height, z);
                AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper9.previousHeight = height;
                adjustPanLayoutHelper9.previousContentHeight = adjustPanLayoutHelper9.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper10 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper10.previousStartOffset = adjustPanLayoutHelper10.startOffset();
                return false;
            }
        }
    };
    /* access modifiers changed from: private */
    public final View parent;
    View parentForListener;
    int previousContentHeight = -1;
    int previousHeight = -1;
    int previousStartOffset = -1;
    /* access modifiers changed from: private */
    public View resizableView;
    private View resizableViewToSet;
    ArrayList<View> viewsToHeightSet = new ArrayList<>();

    /* access modifiers changed from: protected */
    public boolean heightAnimationEnabled() {
        throw null;
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(float f, float f2, boolean z) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionEnd() {
    }

    /* access modifiers changed from: protected */
    public void onTransitionStart(boolean z, int i) {
    }

    /* access modifiers changed from: protected */
    public int startOffset() {
        return 0;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void animateHeight(int r6, int r7, final boolean r8) {
        /*
            r5 = this;
            android.animation.ValueAnimator r0 = r5.animator
            if (r0 == 0) goto L_0x0007
            r0.cancel()
        L_0x0007:
            int r0 = r5.startOffset()
            android.view.View r1 = r5.parent
            r5.getViewsToSetHeight(r1)
            boolean r1 = r5.checkHierarchyHeight
            r2 = 0
            if (r1 == 0) goto L_0x0027
            android.view.View r1 = r5.parent
            android.view.ViewParent r1 = r1.getParent()
            boolean r3 = r1 instanceof android.view.View
            if (r3 == 0) goto L_0x0027
            android.view.View r1 = (android.view.View) r1
            int r1 = r1.getHeight()
            int r1 = r1 - r7
            goto L_0x0028
        L_0x0027:
            r1 = 0
        L_0x0028:
            int r1 = r1 + r7
            int r1 = java.lang.Math.max(r6, r1)
            r5.setViewHeight(r1)
            android.view.View r1 = r5.resizableView
            r1.requestLayout()
            r5.onTransitionStart(r8, r7)
            int r1 = r7 - r6
            float r1 = (float) r1
            float r3 = java.lang.Math.abs(r1)
            r5.keyboardSize = r3
            r3 = 2
            r4 = 0
            if (r7 <= r6) goto L_0x005f
            float r6 = (float) r0
            float r1 = r1 - r6
            android.view.View r6 = r5.parent
            float r7 = -r1
            r6.setTranslationY(r7)
            r6 = 1065353216(0x3var_, float:1.0)
            r5.onPanTranslationUpdate(r1, r6, r8)
            float[] r6 = new float[r3]
            r6 = {NUM, 0} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
            r5.animator = r6
            r1 = r7
            goto L_0x007d
        L_0x005f:
            android.view.View r6 = r5.parent
            int r7 = r5.previousStartOffset
            float r7 = (float) r7
            r6.setTranslationY(r7)
            int r6 = r5.previousStartOffset
            int r6 = -r6
            float r6 = (float) r6
            r5.onPanTranslationUpdate(r6, r4, r8)
            int r6 = r5.previousStartOffset
            int r6 = -r6
            float r4 = (float) r6
            float[] r6 = new float[r3]
            r6 = {0, NUM} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
            r5.animator = r6
        L_0x007d:
            android.animation.ValueAnimator r6 = r5.animator
            org.telegram.ui.ActionBar.AdjustPanLayoutHelper$$ExternalSyntheticLambda0 r7 = new org.telegram.ui.ActionBar.AdjustPanLayoutHelper$$ExternalSyntheticLambda0
            r7.<init>(r5, r1, r4, r8)
            r6.addUpdateListener(r7)
            r6 = 1
            r5.animationInProgress = r6
            int r6 = org.telegram.messenger.UserConfig.selectedAccount
            android.animation.ValueAnimator r7 = r5.animator
            org.telegram.ui.ActionBar.AdjustPanLayoutHelper$3 r0 = new org.telegram.ui.ActionBar.AdjustPanLayoutHelper$3
            r0.<init>(r6, r8)
            r7.addListener(r0)
            android.animation.ValueAnimator r7 = r5.animator
            r0 = 250(0xfa, double:1.235E-321)
            r7.setDuration(r0)
            android.animation.ValueAnimator r7 = r5.animator
            android.view.animation.Interpolator r8 = keyboardInterpolator
            r7.setInterpolator(r8)
            org.telegram.messenger.NotificationCenter r6 = org.telegram.messenger.NotificationCenter.getInstance(r6)
            int r7 = r5.notificationsIndex
            r8 = 0
            int r6 = r6.setAnimationInProgress(r7, r8)
            r5.notificationsIndex = r6
            boolean r6 = r5.needDelay
            if (r6 == 0) goto L_0x00bf
            r5.needDelay = r2
            java.lang.Runnable r6 = r5.delayedAnimationRunnable
            r7 = 100
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r6, r7)
            goto L_0x00c4
        L_0x00bf:
            android.animation.ValueAnimator r6 = r5.animator
            r6.start()
        L_0x00c4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.animateHeight(int, int, boolean):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateHeight$0(float f, float f2, boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f3 = (float) ((int) ((f * floatValue) + (f2 * (1.0f - floatValue))));
        this.parent.setTranslationY(f3);
        onPanTranslationUpdate(-f3, floatValue, z);
    }

    /* access modifiers changed from: private */
    public void setViewHeight(int i) {
        for (int i2 = 0; i2 < this.viewsToHeightSet.size(); i2++) {
            this.viewsToHeightSet.get(i2).getLayoutParams().height = i;
            this.viewsToHeightSet.get(i2).requestLayout();
        }
    }

    private void getViewsToSetHeight(View view) {
        this.viewsToHeightSet.clear();
        while (view != null) {
            this.viewsToHeightSet.add(view);
            if (view != this.resizableView) {
                view = view.getParent() instanceof View ? (View) view.getParent() : null;
            } else {
                return;
            }
        }
    }

    public AdjustPanLayoutHelper(View view) {
        this.parent = view;
        onAttach();
    }

    public void onAttach() {
        if (SharedConfig.smoothKeyboard) {
            onDetach();
            Activity activity = getActivity(this.parent.getContext());
            if (activity != null) {
                this.contentView = (ViewGroup) ((ViewGroup) activity.getWindow().getDecorView()).findViewById(16908290);
            }
            View findResizableView = findResizableView(this.parent);
            this.resizableView = findResizableView;
            if (findResizableView != null) {
                this.parentForListener = findResizableView;
                findResizableView.getViewTreeObserver().addOnPreDrawListener(this.onPreDrawListener);
            }
        }
    }

    private Activity getActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextThemeWrapper) {
            return getActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    private View findResizableView(View view) {
        View view2 = this.resizableViewToSet;
        if (view2 != null) {
            return view2;
        }
        while (view != null) {
            if (!(view.getParent() instanceof DrawerLayoutContainer)) {
                if (!(view.getParent() instanceof View)) {
                    break;
                }
                view = (View) view.getParent();
            } else {
                return view;
            }
        }
        return null;
    }

    public void onDetach() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.parentForListener;
        if (view != null) {
            view.getViewTreeObserver().removeOnPreDrawListener(this.onPreDrawListener);
            this.parentForListener = null;
        }
    }

    public void setResizableView(FrameLayout frameLayout) {
        this.resizableViewToSet = frameLayout;
    }

    public boolean animationInProgress() {
        return this.animationInProgress;
    }

    public void setCheckHierarchyHeight(boolean z) {
        this.checkHierarchyHeight = z;
    }

    public void delayAnimation() {
        this.needDelay = true;
    }

    public void runDelayedAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.delayedAnimationRunnable);
        this.delayedAnimationRunnable.run();
    }
}
