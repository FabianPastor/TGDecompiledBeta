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
    public static boolean USE_ANDROID11_INSET_ANIMATOR = true;
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
                    boolean unused = AdjustPanLayoutHelper.this.usingInsetAnimator = false;
                }
                return true;
            } else if (!adjustPanLayoutHelper.heightAnimationEnabled() || Math.abs(AdjustPanLayoutHelper.this.previousHeight - height) < AndroidUtilities.dp(20.0f)) {
                AdjustPanLayoutHelper adjustPanLayoutHelper3 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper3.previousHeight = height;
                adjustPanLayoutHelper3.previousContentHeight = adjustPanLayoutHelper3.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper4 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper4.previousStartOffset = adjustPanLayoutHelper4.startOffset();
                boolean unused2 = AdjustPanLayoutHelper.this.usingInsetAnimator = false;
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
                AdjustPanLayoutHelper adjustPanLayoutHelper8 = AdjustPanLayoutHelper.this;
                if (height >= adjustPanLayoutHelper8.contentView.getBottom()) {
                    z = false;
                }
                adjustPanLayoutHelper8.isKeyboardVisible = z;
                AdjustPanLayoutHelper adjustPanLayoutHelper9 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper9.animateHeight(adjustPanLayoutHelper9.previousHeight, height, adjustPanLayoutHelper9.isKeyboardVisible);
                AdjustPanLayoutHelper adjustPanLayoutHelper10 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper10.previousHeight = height;
                adjustPanLayoutHelper10.previousContentHeight = adjustPanLayoutHelper10.contentView.getHeight();
                AdjustPanLayoutHelper adjustPanLayoutHelper11 = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper11.previousStartOffset = adjustPanLayoutHelper11.startOffset();
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
    public void animateHeight(int i, int i2, boolean z) {
        if (this.ignoreOnce) {
            this.ignoreOnce = false;
        } else if (this.enabled) {
            startTransition(i, i2, z);
            this.animator.addUpdateListener(new AdjustPanLayoutHelper$$ExternalSyntheticLambda0(this));
            int i3 = UserConfig.selectedAccount;
            this.animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    if (!AdjustPanLayoutHelper.this.usingInsetAnimator) {
                        AdjustPanLayoutHelper.this.stopTransition();
                    }
                }
            });
            this.animator.setDuration(250);
            this.animator.setInterpolator(keyboardInterpolator);
            this.notificationsIndex = NotificationCenter.getInstance(i3).setAnimationInProgress(this.notificationsIndex, (int[]) null);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateHeight$0(ValueAnimator valueAnimator) {
        if (!this.usingInsetAnimator) {
            updateTransition(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0047  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x005b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startTransition(int r6, int r7, boolean r8) {
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
            r3 = 1
            r5.animationInProgress = r3
            r4 = 0
            if (r7 <= r6) goto L_0x005b
            float r6 = (float) r0
            float r1 = r1 - r6
            android.view.View r6 = r5.parent
            float r7 = -r1
            r6.setTranslationY(r7)
            r6 = 1065353216(0x3var_, float:1.0)
            r5.onPanTranslationUpdate(r1, r6, r8)
            r5.from = r7
            r5.to = r4
            r5.inverse = r3
            goto L_0x0074
        L_0x005b:
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
            float r6 = (float) r6
            r5.to = r6
            r5.from = r1
            r5.inverse = r2
        L_0x0074:
            r6 = 2
            float[] r6 = new float[r6]
            r6 = {0, NUM} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
            r5.animator = r6
            r5.usingInsetAnimator = r2
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.AdjustPanLayoutHelper.startTransition(int, int, boolean):void");
    }

    public void updateTransition(float f) {
        if (this.inverse) {
            f = 1.0f - f;
        }
        float f2 = (float) ((int) ((this.from * f) + (this.to * (1.0f - f))));
        this.parent.setTranslationY(f2);
        onPanTranslationUpdate(-f2, f, this.isKeyboardVisible);
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

    public void setViewHeight(int i) {
        for (int i2 = 0; i2 < this.viewsToHeightSet.size(); i2++) {
            this.viewsToHeightSet.get(i2).getLayoutParams().height = i;
            this.viewsToHeightSet.get(i2).requestLayout();
        }
    }

    public void getViewsToSetHeight(View view) {
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

    public AdjustPanLayoutHelper(View view, boolean z) {
        boolean z2 = false;
        if (USE_ANDROID11_INSET_ANIMATOR && z) {
            z2 = true;
        }
        USE_ANDROID11_INSET_ANIMATOR = z2;
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
        View view2 = this.parent;
        if (view2 != null && USE_ANDROID11_INSET_ANIMATOR && Build.VERSION.SDK_INT >= 30) {
            view2.setWindowInsetsAnimationCallback((WindowInsetsAnimation.Callback) null);
        }
    }

    public void setEnabled(boolean z) {
        this.enabled = z;
    }

    public void ignoreOnce() {
        this.ignoreOnce = true;
    }

    public void OnPanTranslationUpdate(float f, float f2, boolean z) {
        onPanTranslationUpdate(f, f2, z);
    }

    public void OnTransitionStart(boolean z, int i) {
        onTransitionStart(z, i);
    }

    public void OnTransitionEnd() {
        onTransitionEnd();
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

    private void setupNewCallback() {
        View view = this.resizableView;
        if (view != null) {
            view.setWindowInsetsAnimationCallback(new WindowInsetsAnimation.Callback(1) {
                public WindowInsets onProgress(WindowInsets windowInsets, List<WindowInsetsAnimation> list) {
                    if (AdjustPanLayoutHelper.this.animationInProgress && AndroidUtilities.screenRefreshRate >= 90.0f) {
                        WindowInsetsAnimation windowInsetsAnimation = null;
                        Iterator<WindowInsetsAnimation> it = list.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                break;
                            }
                            WindowInsetsAnimation next = it.next();
                            if ((next.getTypeMask() & WindowInsetsCompat.Type.ime()) != 0) {
                                windowInsetsAnimation = next;
                                break;
                            }
                        }
                        if (windowInsetsAnimation != null) {
                            long elapsedRealtime = SystemClock.elapsedRealtime();
                            AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                            if (elapsedRealtime >= adjustPanLayoutHelper.startAfter) {
                                boolean unused = adjustPanLayoutHelper.usingInsetAnimator = true;
                                AdjustPanLayoutHelper.this.updateTransition(windowInsetsAnimation.getInterpolatedFraction());
                            }
                        }
                    }
                    return windowInsets;
                }

                public void onEnd(WindowInsetsAnimation windowInsetsAnimation) {
                    if (AdjustPanLayoutHelper.this.animationInProgress && AndroidUtilities.screenRefreshRate >= 90.0f) {
                        AdjustPanLayoutHelper.this.stopTransition();
                    }
                }
            });
        }
    }
}
