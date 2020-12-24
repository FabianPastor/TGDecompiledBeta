package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class AdjustPanLayoutHelper {
    public static final Interpolator keyboardInterpolator = CubicBezierInterpolator.DEFAULT;
    /* access modifiers changed from: private */
    public boolean animationInProgress;
    ValueAnimator animator;
    /* access modifiers changed from: private */
    public ViewGroup contentView;
    protected float keyboardSize;
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
    public void onTransitionStart(boolean z) {
    }

    /* access modifiers changed from: protected */
    public int startOffset() {
        return 0;
    }

    /* access modifiers changed from: private */
    public void animateHeight(int i, int i2, final boolean z) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        int startOffset = startOffset();
        getViewsToSetHeight(this.parent);
        setViewHeight(Math.max(i, i2));
        this.resizableView.requestLayout();
        onTransitionStart(z);
        float f = (float) (i2 - i);
        this.keyboardSize = Math.abs(f);
        float f2 = 0.0f;
        if (i2 > i) {
            float f3 = f - ((float) startOffset);
            float f4 = -f3;
            this.parent.setTranslationY(f4);
            onPanTranslationUpdate(f3, 1.0f, z);
            this.animator = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            f = f4;
        } else {
            this.parent.setTranslationY((float) this.previousStartOffset);
            onPanTranslationUpdate((float) (-this.previousStartOffset), 0.0f, z);
            f2 = (float) (-this.previousStartOffset);
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        }
        this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, f2, z) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AdjustPanLayoutHelper.this.lambda$animateHeight$0$AdjustPanLayoutHelper(this.f$1, this.f$2, this.f$3, valueAnimator);
            }
        });
        this.animationInProgress = true;
        final int i3 = UserConfig.selectedAccount;
        this.animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                boolean unused = AdjustPanLayoutHelper.this.animationInProgress = false;
                NotificationCenter.getInstance(i3).onAnimationFinish(AdjustPanLayoutHelper.this.notificationsIndex);
                AdjustPanLayoutHelper adjustPanLayoutHelper = AdjustPanLayoutHelper.this;
                adjustPanLayoutHelper.animator = null;
                adjustPanLayoutHelper.setViewHeight(-1);
                AdjustPanLayoutHelper.this.viewsToHeightSet.clear();
                AdjustPanLayoutHelper.this.resizableView.requestLayout();
                AdjustPanLayoutHelper adjustPanLayoutHelper2 = AdjustPanLayoutHelper.this;
                boolean z = z;
                adjustPanLayoutHelper2.onPanTranslationUpdate(0.0f, z ? 1.0f : 0.0f, z);
                AdjustPanLayoutHelper.this.parent.setTranslationY(0.0f);
                AdjustPanLayoutHelper.this.onTransitionEnd();
            }
        });
        this.animator.setDuration(220);
        this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.notificationsIndex = NotificationCenter.getInstance(i3).setAnimationInProgress(this.notificationsIndex, (int[]) null);
        this.animator.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateHeight$0 */
    public /* synthetic */ void lambda$animateHeight$0$AdjustPanLayoutHelper(float f, float f2, boolean z, ValueAnimator valueAnimator) {
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
}
