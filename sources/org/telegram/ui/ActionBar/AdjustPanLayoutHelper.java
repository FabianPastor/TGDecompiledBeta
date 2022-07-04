package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.WindowInsetsAnimation;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;

public class AdjustPanLayoutHelper {
    public static boolean USE_ANDROID11_INSET_ANIMATOR = false;
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
    private boolean enabled = true;
    float from;
    private boolean ignoreOnce;
    boolean inverse;
    boolean isKeyboardVisible;
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
            int contentHeight = AdjustPanLayoutHelper.this.parent.getHeight();
            if (contentHeight - AdjustPanLayoutHelper.this.startOffset() == AdjustPanLayoutHelper.this.previousHeight - AdjustPanLayoutHelper.this.previousStartOffset || contentHeight == AdjustPanLayoutHelper.this.previousHeight || AdjustPanLayoutHelper.this.animator != null) {
                if (AdjustPanLayoutHelper.this.animator == null) {
                    AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                    AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper.previousContentHeight = adjustPanLayoutHelper.contentView.getHeight();
                    AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                    adjustPanLayoutHelper2.previousStartOffset = adjustPanLayoutHelper2.startOffset();
                    boolean unused = AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                }
                return true;
            } else if (!AdjustPanLayoutHelper.this.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - contentHeight) < AndroidUtilities.dp(20.0f)) {
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                boolean unused2 = AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                return true;
            } else if (AdjustPanLayoutHelper.this.previousHeight == -1 || AdjustPanLayoutHelper.this.previousContentHeight != AdjustPanLayoutHelper.this.contentView.getHeight()) {
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper5 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper5.previousContentHeight = adjustPanLayoutHelper5.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper6 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper6.previousStartOffset = adjustPanLayoutHelper6.startOffset();
                return false;
            } else {
                AdjustPanLayoutHelper adjustPanLayoutHelper7 = AdjustPanLayoutHelper.this;
                if (contentHeight >= adjustPanLayoutHelper7.contentView.getBottom()) {
                    z = false;
                }
                adjustPanLayoutHelper7.isKeyboardVisible = z;
                AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper8.animateHeight(adjustPanLayoutHelper8.previousHeight, contentHeight, AdjustPanLayoutHelper.this.isKeyboardVisible);
                AdjustPanLayoutHelper.this.previousHeight = contentHeight;
                AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
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
    private View resizableView;
    private View resizableViewToSet;
    long startAfter;
    float to;
    /* access modifiers changed from: private */
    public boolean usingInsetAnimator = false;
    ArrayList<View> viewsToHeightSet = new ArrayList<>();

    public View getAdjustingParent() {
        return this.parent;
    }

    public View getAdjustingContentView() {
        return this.contentView;
    }

    /* access modifiers changed from: private */
    public void animateHeight(int previousHeight2, int contentHeight, boolean isKeyboardVisible2) {
        if (this.ignoreOnce) {
            this.ignoreOnce = false;
        } else if (this.enabled) {
            startTransition(previousHeight2, contentHeight, isKeyboardVisible2);
            this.animator.addUpdateListener(new AdjustPanLayoutHelper$$ExternalSyntheticLambda0(this));
            int selectedAccount = UserConfig.selectedAccount;
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (!AdjustPanLayoutHelper.this.usingInsetAnimator) {
                        AdjustPanLayoutHelper.this.stopTransition();
                    }
                }
            });
            this.animator.setDuration(250);
            this.animator.setInterpolator(keyboardInterpolator);
            this.notificationsIndex = NotificationCenter.getInstance(selectedAccount).setAnimationInProgress(this.notificationsIndex, (int[]) null);
            if (this.needDelay) {
                this.needDelay = false;
                this.startAfter = SystemClock.elapsedRealtime() + 100;
                AndroidUtilities.runOnUIThread(this.delayedAnimationRunnable, 100);
                return;
            }
            this.animator.start();
            this.startAfter = -1;
        }
    }

    /* renamed from: lambda$animateHeight$0$org-telegram-ui-ActionBar-AdjustPanLayoutHelper  reason: not valid java name */
    public /* synthetic */ void m2551x3382a933(ValueAnimator animation) {
        if (!this.usingInsetAnimator) {
            updateTransition(((Float) animation.getAnimatedValue()).floatValue());
        }
    }

    public void startTransition(int previousHeight2, int contentHeight, boolean isKeyboardVisible2) {
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
        onTransitionStart(isKeyboardVisible2, contentHeight);
        float dy = (float) (contentHeight - previousHeight2);
        this.keyboardSize = Math.abs(dy);
        this.animationInProgress = true;
        if (contentHeight > previousHeight2) {
            float dy2 = dy - ((float) startOffset);
            this.parent.setTranslationY(-dy2);
            onPanTranslationUpdate(dy2, 1.0f, isKeyboardVisible2);
            this.from = -dy2;
            this.to = 0.0f;
            this.inverse = true;
        } else {
            this.parent.setTranslationY((float) this.previousStartOffset);
            onPanTranslationUpdate((float) (-this.previousStartOffset), 0.0f, isKeyboardVisible2);
            this.to = (float) (-this.previousStartOffset);
            this.from = dy;
            this.inverse = false;
        }
        this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.usingInsetAnimator = false;
    }

    public void updateTransition(float t) {
        if (this.inverse) {
            t = 1.0f - t;
        }
        float y = (float) ((int) ((this.from * t) + (this.to * (1.0f - t))));
        this.parent.setTranslationY(y);
        onPanTranslationUpdate(-y, t, this.isKeyboardVisible);
    }

    public void stopTransition() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animationInProgress = false;
        this.usingInsetAnimator = false;
        NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(this.notificationsIndex);
        this.animator = null;
        setViewHeight(-1);
        this.viewsToHeightSet.clear();
        this.resizableView.requestLayout();
        boolean z = this.isKeyboardVisible;
        onPanTranslationUpdate(0.0f, z ? 1.0f : 0.0f, z);
        this.parent.setTranslationY(0.0f);
        onTransitionEnd();
    }

    public void stopTransition(float t, boolean isKeyboardVisible2) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.animationInProgress = false;
        NotificationCenter.getInstance(UserConfig.selectedAccount).onAnimationFinish(this.notificationsIndex);
        this.animator = null;
        setViewHeight(-1);
        this.viewsToHeightSet.clear();
        this.resizableView.requestLayout();
        this.isKeyboardVisible = isKeyboardVisible2;
        onPanTranslationUpdate(0.0f, t, isKeyboardVisible2);
        this.parent.setTranslationY(0.0f);
        onTransitionEnd();
    }

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
    public void getViewsToSetHeight(android.view.View r3) {
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

    public AdjustPanLayoutHelper(View parent2, boolean useInsetsAnimator) {
        boolean z = false;
        if (USE_ANDROID11_INSET_ANIMATOR && useInsetsAnimator) {
            z = true;
        }
        USE_ANDROID11_INSET_ANIMATOR = z;
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
            if (USE_ANDROID11_INSET_ANIMATOR && Build.VERSION.SDK_INT >= 30) {
                setupNewCallback();
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
        if (this.parent != null && USE_ANDROID11_INSET_ANIMATOR && Build.VERSION.SDK_INT >= 30) {
            this.parent.setWindowInsetsAnimationCallback((WindowInsetsAnimation.Callback) null);
        }
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void ignoreOnce() {
        this.ignoreOnce = true;
    }

    /* access modifiers changed from: protected */
    public boolean heightAnimationEnabled() {
        return true;
    }

    public void OnPanTranslationUpdate(float y, float progress, boolean keyboardVisible) {
        onPanTranslationUpdate(y, progress, keyboardVisible);
    }

    public void OnTransitionStart(boolean keyboardVisible, int contentHeight) {
        onTransitionStart(keyboardVisible, contentHeight);
    }

    public void OnTransitionEnd() {
        onTransitionEnd();
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

    private void setupNewCallback() {
        View view = this.resizableView;
        if (view != null) {
            view.setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) {
                public WindowInsets onProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations) {
                    if (!AdjustPanLayoutHelper.this.animationInProgress || AndroidUtilities.screenRefreshRate < 90.0f) {
                        return insets;
                    }
                    WindowInsetsAnimation imeAnimation = null;
                    Iterator<WindowInsetsAnimation> it = runningAnimations.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        WindowInsetsAnimation animation = it.next();
                        if ((animation.getTypeMask() & WindowInsetsCompat.Type.ime()) != 0) {
                            imeAnimation = animation;
                            break;
                        }
                    }
                    if (imeAnimation != null && SystemClock.elapsedRealtime() >= AdjustPanLayoutHelper.this.startAfter) {
                        boolean unused = AdjustPanLayoutHelper.this.usingInsetAnimator = true;
                        AdjustPanLayoutHelper.this.updateTransition(imeAnimation.getInterpolatedFraction());
                    }
                    return insets;
                }

                public void onEnd(WindowInsetsAnimation animation) {
                    if (AdjustPanLayoutHelper.this.animationInProgress && AndroidUtilities.screenRefreshRate >= 90.0f) {
                        AdjustPanLayoutHelper.this.stopTransition();
                    }
                }
            });
        }
    }
}
