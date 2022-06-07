package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Insets;
import android.os.Build;
import android.os.CancellationSignal;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.WindowInsetsAnimationControlListener;
import android.view.WindowInsetsAnimationController;
import android.view.animation.LinearInterpolator;
import androidx.core.math.MathUtils;
import androidx.core.view.WindowInsetsCompat;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RecyclerListView;

public class KeyboardHideHelper {
    public static boolean ENABLED = false;
    private int bottomNavBarSize;
    /* access modifiers changed from: private */
    public boolean endingMovingKeyboard = false;
    private float fromY;
    /* access modifiers changed from: private */
    public WindowInsetsAnimationController insetsController;
    /* access modifiers changed from: private */
    public boolean isKeyboard = false;
    private int keyboardSize;
    private float lastDifferentT;
    private float lastT;
    private boolean movingKeyboard = false;
    private AdjustPanLayoutHelper panLayoutHelper;
    /* access modifiers changed from: private */
    public float rawT;
    private boolean startedAtBottom = false;
    private boolean startedOutsideView = false;
    private float t;
    private VelocityTracker tracker;
    private View view;

    public boolean onTouch(AdjustPanLayoutHelper adjustPanLayoutHelper, View view2, RecyclerListView recyclerListView, ChatActivityEnterView chatActivityEnterView, ChatActivity chatActivity, MotionEvent motionEvent) {
        int i;
        AdjustPanLayoutHelper adjustPanLayoutHelper2 = adjustPanLayoutHelper;
        View view3 = view2;
        ChatActivity chatActivity2 = chatActivity;
        if (!ENABLED) {
            return false;
        }
        this.panLayoutHelper = adjustPanLayoutHelper2;
        this.view = view3;
        if (!(view3 == null || chatActivityEnterView == null || Build.VERSION.SDK_INT < 30)) {
            boolean z = view2.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom > 0;
            if (!this.movingKeyboard && !z && !this.endingMovingKeyboard) {
                return false;
            }
            boolean z2 = motionEvent.getY() >= ((float) chatActivityEnterView.getTop());
            if (motionEvent.getAction() == 0) {
                this.startedOutsideView = !z2;
                this.startedAtBottom = !recyclerListView.canScrollVertically(1);
            } else {
                RecyclerListView recyclerListView2 = recyclerListView;
            }
            float f = 0.0f;
            if (!this.movingKeyboard && z2 && this.startedOutsideView && motionEvent.getAction() == 2) {
                this.movingKeyboard = true;
                boolean z3 = !chatActivityEnterView.isPopupShowing();
                this.isKeyboard = z3;
                if (z3) {
                    i = view2.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom;
                } else {
                    i = chatActivityEnterView.getEmojiPadding();
                }
                this.keyboardSize = i;
                this.bottomNavBarSize = view2.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.navigationBars()).bottom;
                view2.getWindowInsetsController().controlWindowInsetsAnimation(WindowInsetsCompat.Type.ime(), -1, new LinearInterpolator(), new CancellationSignal(), new WindowInsetsAnimationControlListener() {
                    public void onReady(WindowInsetsAnimationController windowInsetsAnimationController, int i) {
                        WindowInsetsAnimationController unused = KeyboardHideHelper.this.insetsController = windowInsetsAnimationController;
                    }

                    public void onFinished(WindowInsetsAnimationController windowInsetsAnimationController) {
                        WindowInsetsAnimationController unused = KeyboardHideHelper.this.insetsController = null;
                    }

                    public void onCancelled(WindowInsetsAnimationController windowInsetsAnimationController) {
                        WindowInsetsAnimationController unused = KeyboardHideHelper.this.insetsController = null;
                    }
                });
                this.fromY = motionEvent.getRawY();
                adjustPanLayoutHelper2.setEnabled(false);
                update(0.0f, false);
                recyclerListView.stopScroll();
                this.lastDifferentT = 0.0f;
                this.lastT = 0.0f;
                this.rawT = 0.0f;
                this.t = 0.0f;
                adjustPanLayoutHelper2.OnTransitionStart(true, view2.getHeight());
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.clear();
            }
            if (this.movingKeyboard) {
                this.tracker.addMovement(motionEvent);
                float rawY = (motionEvent.getRawY() - this.fromY) / ((float) this.keyboardSize);
                this.rawT = rawY;
                this.t = MathUtils.clamp(rawY, 0.0f, 1.0f);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.movingKeyboard = false;
                    this.endingMovingKeyboard = true;
                    this.tracker.computeCurrentVelocity(1000);
                    float f2 = this.t;
                    boolean z4 = (f2 > 0.15f && f2 >= this.lastDifferentT) || f2 > 0.8f;
                    if (z4) {
                        f = 1.0f;
                    }
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f2, f});
                    ofFloat.addUpdateListener(new KeyboardHideHelper$$ExternalSyntheticLambda0(this));
                    final boolean z5 = z4;
                    final float f3 = f;
                    final AdjustPanLayoutHelper adjustPanLayoutHelper3 = adjustPanLayoutHelper;
                    final View view4 = view2;
                    ofFloat.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (KeyboardHideHelper.this.insetsController != null && KeyboardHideHelper.this.isKeyboard) {
                                KeyboardHideHelper.this.insetsController.finish(!z5);
                            }
                            KeyboardHideHelper.this.update(1.0f, false);
                            float unused = KeyboardHideHelper.this.rawT = f3;
                            adjustPanLayoutHelper3.OnTransitionEnd();
                            view4.post(new KeyboardHideHelper$2$$ExternalSyntheticLambda0(this, adjustPanLayoutHelper3));
                        }

                        /* access modifiers changed from: private */
                        public /* synthetic */ void lambda$onAnimationEnd$0(AdjustPanLayoutHelper adjustPanLayoutHelper) {
                            adjustPanLayoutHelper.setEnabled(true);
                            boolean unused = KeyboardHideHelper.this.endingMovingKeyboard = false;
                        }
                    });
                    ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    ofFloat.setDuration(200);
                    ofFloat.start();
                    if (z4 && this.startedAtBottom && chatActivity2 != null) {
                        chatActivity2.scrollToLastMessage(true);
                    }
                    this.startedOutsideView = false;
                    return true;
                }
                update(this.t, true);
                float f4 = this.lastT;
                float f5 = this.t;
                if (f4 != f5) {
                    this.lastDifferentT = f4;
                }
                this.lastT = f5;
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onTouch$0(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.t = floatValue;
        update(floatValue, true);
    }

    public boolean disableScrolling() {
        return ENABLED && (this.movingKeyboard || this.endingMovingKeyboard) && this.rawT >= 0.0f;
    }

    /* access modifiers changed from: private */
    public void update(float f, boolean z) {
        WindowInsetsAnimationController windowInsetsAnimationController;
        if (this.isKeyboard) {
            float f2 = 1.0f - f;
            float max = Math.max(((((float) this.keyboardSize) * f2) - ((float) this.bottomNavBarSize)) - 1.0f, 0.0f);
            this.panLayoutHelper.OnPanTranslationUpdate(max, f, true);
            ((View) ((View) this.view.getParent()).getParent()).setTranslationY(-max);
            if (z && (windowInsetsAnimationController = this.insetsController) != null && Build.VERSION.SDK_INT >= 30) {
                windowInsetsAnimationController.setInsetsAndAlpha(Insets.of(0, 0, 0, (int) (((float) this.keyboardSize) * f2)), 1.0f, f);
                return;
            }
            return;
        }
        this.panLayoutHelper.OnPanTranslationUpdate((1.0f - f) * ((float) this.keyboardSize), f, true);
    }
}
