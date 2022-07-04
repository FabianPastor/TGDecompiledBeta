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
    private View enterView;
    private boolean exactlyMovingKeyboard = false;
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

    public boolean onTouch(AdjustPanLayoutHelper panLayoutHelper2, View view2, RecyclerListView listView, ChatActivityEnterView enterView2, ChatActivity ca, MotionEvent ev) {
        boolean z;
        int i;
        AdjustPanLayoutHelper adjustPanLayoutHelper = panLayoutHelper2;
        View view3 = view2;
        ChatActivityEnterView chatActivityEnterView = enterView2;
        ChatActivity chatActivity = ca;
        if (!ENABLED) {
            return false;
        }
        this.panLayoutHelper = adjustPanLayoutHelper;
        this.view = view3;
        this.enterView = chatActivityEnterView;
        if (view3 == null) {
            RecyclerListView recyclerListView = listView;
            return false;
        } else if (chatActivityEnterView == null) {
            RecyclerListView recyclerListView2 = listView;
            return false;
        } else if (Build.VERSION.SDK_INT >= 30) {
            boolean isKeyboardVisible = view2.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom > 0;
            if (!this.movingKeyboard && !isKeyboardVisible && !this.endingMovingKeyboard) {
                return false;
            }
            boolean insideEnterView = ev.getY() >= ((float) enterView2.getTop());
            if (ev.getAction() == 0) {
                this.startedOutsideView = !insideEnterView;
                this.startedAtBottom = !listView.canScrollVertically(1);
            } else {
                RecyclerListView recyclerListView3 = listView;
            }
            float f = 0.0f;
            if (!this.movingKeyboard && insideEnterView && this.startedOutsideView && ev.getAction() == 2) {
                this.movingKeyboard = true;
                boolean z2 = !enterView2.isPopupShowing();
                this.isKeyboard = z2;
                if (z2) {
                    i = view2.getRootWindowInsets().getInsets(WindowInsetsCompat.Type.ime()).bottom;
                } else {
                    i = enterView2.getEmojiPadding();
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
                this.fromY = ev.getRawY();
                this.exactlyMovingKeyboard = false;
                adjustPanLayoutHelper.setEnabled(false);
                update(0.0f, false);
                listView.stopScroll();
                this.lastDifferentT = 0.0f;
                this.lastT = 0.0f;
                this.rawT = 0.0f;
                this.t = 0.0f;
                adjustPanLayoutHelper.OnTransitionStart(true, view2.getHeight());
                if (this.tracker == null) {
                    this.tracker = VelocityTracker.obtain();
                }
                this.tracker.clear();
            }
            if (!this.movingKeyboard) {
                return false;
            }
            this.tracker.addMovement(ev);
            float rawY = (ev.getRawY() - this.fromY) / ((float) this.keyboardSize);
            this.rawT = rawY;
            this.t = MathUtils.clamp(rawY, 0.0f, 1.0f);
            if (ev.getAction() == 1 || ev.getAction() == 3) {
                this.movingKeyboard = false;
                this.exactlyMovingKeyboard = false;
                this.endingMovingKeyboard = true;
                this.tracker.computeCurrentVelocity(1000);
                float f2 = this.t;
                boolean end = (f2 > 0.15f && f2 >= this.lastDifferentT) || f2 > 0.8f;
                if (end) {
                    f = 1.0f;
                }
                float endT = f;
                ValueAnimator va = ValueAnimator.ofFloat(new float[]{f2, endT});
                va.addUpdateListener(new KeyboardHideHelper$$ExternalSyntheticLambda0(this));
                final boolean z3 = end;
                AnonymousClass2 r11 = r0;
                final float f3 = endT;
                ValueAnimator va2 = va;
                final AdjustPanLayoutHelper adjustPanLayoutHelper2 = panLayoutHelper2;
                final View view4 = view2;
                AnonymousClass2 r0 = new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (KeyboardHideHelper.this.insetsController != null && KeyboardHideHelper.this.isKeyboard) {
                            KeyboardHideHelper.this.insetsController.finish(!z3);
                        }
                        KeyboardHideHelper.this.update(1.0f, false);
                        float unused = KeyboardHideHelper.this.rawT = f3;
                        adjustPanLayoutHelper2.OnTransitionEnd();
                        view4.post(new KeyboardHideHelper$2$$ExternalSyntheticLambda0(this, adjustPanLayoutHelper2));
                    }

                    /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-KeyboardHideHelper$2  reason: not valid java name */
                    public /* synthetic */ void m3617lambda$onAnimationEnd$0$orgtelegramuiKeyboardHideHelper$2(AdjustPanLayoutHelper panLayoutHelper) {
                        panLayoutHelper.setEnabled(true);
                        boolean unused = KeyboardHideHelper.this.endingMovingKeyboard = false;
                    }
                };
                va2.addListener(r11);
                va2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                va2.setDuration(200);
                va2.start();
                if (!end || !this.startedAtBottom || chatActivity == null) {
                    z = true;
                } else {
                    z = true;
                    chatActivity.scrollToLastMessage(true);
                }
                this.startedOutsideView = false;
                return z;
            }
            update(this.t, true);
            float f4 = this.lastT;
            float f5 = this.t;
            if (f4 != f5) {
                this.lastDifferentT = f4;
            }
            this.lastT = f5;
            return true;
        } else {
            RecyclerListView recyclerListView4 = listView;
            return false;
        }
    }

    /* renamed from: lambda$onTouch$0$org-telegram-ui-KeyboardHideHelper  reason: not valid java name */
    public /* synthetic */ void m3616lambda$onTouch$0$orgtelegramuiKeyboardHideHelper(ValueAnimator a) {
        float floatValue = ((Float) a.getAnimatedValue()).floatValue();
        this.t = floatValue;
        update(floatValue, true);
    }

    public boolean disableScrolling() {
        return ENABLED && (this.movingKeyboard || this.endingMovingKeyboard) && this.rawT >= 0.0f;
    }

    /* access modifiers changed from: private */
    public void update(float t2, boolean withKeyboard) {
        if (this.isKeyboard) {
            float y = Math.max((((1.0f - t2) * ((float) this.keyboardSize)) - ((float) this.bottomNavBarSize)) - 1.0f, 0.0f);
            this.panLayoutHelper.OnPanTranslationUpdate(y, t2, true);
            ((View) ((View) this.view.getParent()).getParent()).setTranslationY(-y);
            if (withKeyboard && this.insetsController != null && Build.VERSION.SDK_INT >= 30) {
                this.insetsController.setInsetsAndAlpha(Insets.of(0, 0, 0, (int) (((float) this.keyboardSize) * (1.0f - t2))), 1.0f, t2);
                return;
            }
            return;
        }
        this.panLayoutHelper.OnPanTranslationUpdate((1.0f - t2) * ((float) this.keyboardSize), t2, true);
    }
}
