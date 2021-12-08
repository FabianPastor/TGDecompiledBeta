package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;

public class AdjustPanLayoutHelper {
    public static final long keyboardDuration = 250;
    public static final Interpolator keyboardInterpolator = ChatListItemAnimator.DEFAULT_INTERPOLATOR;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    ValueAnimator animator;
    boolean checkHierarchyHeight;
    /* access modifiers changed from: private */
    public ViewGroup contentView;
    private Runnable delayedAnimationRunnable = new Runnable() {
        public void run() {
            if (AdjustPanLayoutHelper.this.animator != null && !AdjustPanLayoutHelper.this.animator.isRunning()) {
                AdjustPanLayoutHelper.this.animator.start();
            }
        }
    };
    protected float keyboardSize;
    private boolean needDelay;
    int notificationsIndex;
    ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        public boolean onPreDraw() {
            boolean isKeyboardVisible = true;
            if (!SharedConfig.smoothKeyboard) {
                AdjustPanLayoutHelper.this.onDetach();
                return true;
            }
            int contentHeight = AdjustPanLayoutHelper.this.parent.getHeight();
            if (contentHeight - AdjustPanLayoutHelper.this.startOffset() == AdjustPanLayoutHelper.this.previousHeight - AdjustPanLayoutHelper.this.previousStartOffset || contentHeight == AdjustPanLayoutHelper.this.previousHeight || AdjustPanLayoutHelper.this.animator != null) {
                if (AdjustPanLayoutHelper.this.animator == null) {
                    AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                    AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper.previousContentHeight = adjustPanLayoutHelper.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper2.previousStartOffset = adjustPanLayoutHelper2.startOffset();
                }
                return true;
            } else if (!AdjustPanLayoutHelper.this.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - contentHeight) < AndroidUtilities.dp(20.0f)) {
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                return true;
            } else if (AdjustPanLayoutHelper.this.previousHeight == -1 || AdjustPanLayoutHelper.this.previousContentHeight != AdjustPanLayoutHelper.this.contentView.getHeight()) {
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper5 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper5.previousContentHeight = adjustPanLayoutHelper5.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper6 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper6.previousStartOffset = adjustPanLayoutHelper6.startOffset();
                return false;
            } else {
                if (contentHeight >= AdjustPanLayoutHelper.this.contentView.getBottom()) {
                    isKeyboardVisible = false;
                }
                AdjustPanLayoutHelper adjustPanLayoutHelper7 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper7.animateHeight(adjustPanLayoutHelper7.previousHeight, contentHeight, isKeyboardVisible);
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper8.previousContentHeight = adjustPanLayoutHelper8.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper9.previousStartOffset = adjustPanLayoutHelper9.startOffset();
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

    /* access modifiers changed from: private */
    public void animateHeight(int previousHeight2, int contentHeight, final boolean isKeyboardVisible) {
        float to;
        float from;
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int startOffset = startOffset();
        getViewsToSetHeight(this.parent);
        int additionalContentHeight = 0;
        if (this.checkHierarchyHeight) {
            ViewParent viewParent = this.parent.getParent();
            if (viewParent instanceof View) {
                additionalContentHeight = ((View) viewParent).getHeight() - contentHeight;
            }
        }
        setViewHeight(Math.max(previousHeight2, contentHeight + additionalContentHeight));
        this.resizableView.requestLayout();
        onTransitionStart(isKeyboardVisible, contentHeight);
        float dy = (float) (contentHeight - previousHeight2);
        this.keyboardSize = Math.abs(dy);
        if (contentHeight > previousHeight2) {
            float dy2 = dy - ((float) startOffset);
            this.parent.setTranslationY(-dy2);
            onPanTranslationUpdate(dy2, 1.0f, isKeyboardVisible);
            from = -dy2;
            to = 0.0f;
            this.animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
        } else {
            this.parent.setTranslationY((float) this.previousStartOffset);
            onPanTranslationUpdate((float) (-this.previousStartOffset), 0.0f, isKeyboardVisible);
            to = (float) (-this.previousStartOffset);
            from = dy;
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        }
        this.animator.addUpdateListener(new AdjustPanLayoutHelper$$ExternalSyntheticLambda0(this, from, to, isKeyboardVisible));
        this.animationInProgress = true;
        final int selectedAccount = UserConfig.selectedAccount;
        this.animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                boolean unused = AdjustPanLayoutHelper.this.animationInProgress = false;
                NotificationCenter.getInstance(selectedAccount).onAnimationFinish(AdjustPanLayoutHelper.this.notificationsIndex);
                AdjustPanLayoutHelper.this.animator = null;
                AdjustPanLayoutHelper.this.setViewHeight(-1);
                AdjustPanLayoutHelper.this.viewsToHeightSet.clear();
                AdjustPanLayoutHelper.this.resizableView.requestLayout();
                AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                boolean z = isKeyboardVisible;
                adjustPanLayoutHelper.onPanTranslationUpdate(0.0f, z ? 1.0f : 0.0f, z);
                AdjustPanLayoutHelper.this.parent.setTranslationY(0.0f);
                AdjustPanLayoutHelper.this.onTransitionEnd();
            }
        });
        this.animator.setDuration(250);
        this.animator.setInterpolator(keyboardInterpolator);
        this.notificationsIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
        if (this.needDelay) {
            this.needDelay = false;
            AndroidUtilities.runOnUIThread(this.delayedAnimationRunnable, 100);
            return;
        }
        this.animator.start();
    }

    /* renamed from: lambda$animateHeight$0$org-telegram-ui-ActionBar-AdjustPanLayoutHelper  reason: not valid java name */
    public /* synthetic */ void m1308x3382a933(float from, float to, boolean isKeyboardVisible, ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        float y = (float) ((int) ((from * v) + ((1.0f - v) * to)));
        this.parent.setTranslationY(y);
        onPanTranslationUpdate(-y, v, isKeyboardVisible);
    }

    /* access modifiers changed from: private */
    public void setViewHeight(int height) {
        for (int i = 0; i < this.viewsToHeightSet.size(); i++) {
            this.viewsToHeightSet.get(i).getLayoutParams().height = height;
            this.viewsToHeightSet.get(i).requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public int startOffset() {
        return 0;
    }

    /* JADX WARNING: type inference failed for: r1v4, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getViewsToSetHeight(android.view.View r3) {
        /*
            r2 = this;
            java.util.ArrayList<android.view.View> r0 = r2.viewsToHeightSet
            r0.clear()
            r0 = r3
        L_0x0006:
            if (r0 == 0) goto L_0x0024
            java.util.ArrayList<android.view.View> r1 = r2.viewsToHeightSet
            r1.add(r0)
            android.view.View r1 = r2.resizableView
            if (r0 != r1) goto L_0x0012
            return
        L_0x0012:
            android.view.ViewParent r1 = r0.getParent()
            boolean r1 = r1 instanceof android.view.View
            if (r1 == 0) goto L_0x0022
            android.view.ViewParent r1 = r0.getParent()
            r0 = r1
            android.view.View r0 = (android.view.View) r0
            goto L_0x0006
        L_0x0022:
            r0 = 0
            goto L_0x0006
        L_0x0024:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.getViewsToSetHeight(android.view.View):void");
    }

    public AdjustPanLayoutHelper(View parent2) {
        this.parent = parent2;
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

    /* JADX WARNING: type inference failed for: r1v1, types: [android.view.ViewParent] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.view.View findResizableView(android.view.View r4) {
        /*
            r3 = this;
            android.view.View r0 = r3.resizableViewToSet
            if (r0 == 0) goto L_0x0005
            return r0
        L_0x0005:
            r0 = r4
        L_0x0006:
            r1 = 0
            if (r0 == 0) goto L_0x0023
            android.view.ViewParent r2 = r0.getParent()
            boolean r2 = r2 instanceof org.telegram.ui.ActionBar.DrawerLayoutContainer
            if (r2 == 0) goto L_0x0012
            return r0
        L_0x0012:
            android.view.ViewParent r2 = r0.getParent()
            boolean r2 = r2 instanceof android.view.View
            if (r2 == 0) goto L_0x0022
            android.view.ViewParent r1 = r0.getParent()
            r0 = r1
            android.view.View r0 = (android.view.View) r0
            goto L_0x0006
        L_0x0022:
            return r1
        L_0x0023:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.findResizableView(android.view.View):android.view.View");
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

    /* access modifiers changed from: protected */
    public boolean heightAnimationEnabled() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionStart(boolean keyboardVisible, int contentHeight) {
    }

    /* access modifiers changed from: protected */
    public void onTransitionEnd() {
    }

    public void setResizableView(FrameLayout windowView) {
        this.resizableViewToSet = windowView;
    }

    public boolean animationInProgress() {
        return this.animationInProgress;
    }

    public void setCheckHierarchyHeight(boolean checkHierarchyHeight2) {
        this.checkHierarchyHeight = checkHierarchyHeight2;
    }

    public void delayAnimation() {
        this.needDelay = true;
    }

    public void runDelayedAnimation() {
        AndroidUtilities.cancelRunOnUIThread(this.delayedAnimationRunnable);
        this.delayedAnimationRunnable.run();
    }
}
