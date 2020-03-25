package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Property;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import androidx.annotation.Keep;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarPopupWindow extends PopupWindow {
    private static final ViewTreeObserver.OnScrollChangedListener NOP = $$Lambda$ActionBarPopupWindow$u1KuFqdl4RQdFfyVDBUWk_fHAc.INSTANCE;
    /* access modifiers changed from: private */
    public static final boolean allowAnimation = (Build.VERSION.SDK_INT >= 18);
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static Method layoutInScreenMethod;
    private static final Field superListenerField;
    private boolean animationEnabled = allowAnimation;
    private int dismissAnimationDuration = 150;
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    /* access modifiers changed from: private */
    public AnimatorSet windowAnimatorSet;

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    static /* synthetic */ void lambda$static$0() {
    }

    static {
        Field field = null;
        try {
            field = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            field.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
        superListenerField = field;
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
        private boolean animationEnabled = ActionBarPopupWindow.allowAnimation;
        private int backAlpha = 255;
        private float backScaleX = 1.0f;
        private float backScaleY = 1.0f;
        private int backgroundColor = -1;
        protected Drawable backgroundDrawable = getResources().getDrawable(NUM).mutate();
        /* access modifiers changed from: private */
        public ArrayList<AnimatorSet> itemAnimators;
        /* access modifiers changed from: private */
        public int lastStartedChild = 0;
        protected LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        /* access modifiers changed from: private */
        public HashMap<View, Integer> positions = new HashMap<>();
        private ScrollView scrollView;
        /* access modifiers changed from: private */
        public boolean showedFromBotton;

        public ActionBarPopupWindowLayout(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("actionBarDefaultSubmenuBackground"));
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
            try {
                ScrollView scrollView2 = new ScrollView(context);
                this.scrollView = scrollView2;
                scrollView2.setVerticalScrollBarEnabled(false);
                addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
            } catch (Throwable th) {
                FileLog.e(th);
            }
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.linearLayout = linearLayout2;
            linearLayout2.setOrientation(1);
            ScrollView scrollView3 = this.scrollView;
            if (scrollView3 != null) {
                scrollView3.addView(this.linearLayout, new FrameLayout.LayoutParams(-2, -2));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public void setShowedFromBotton(boolean z) {
            this.showedFromBotton = z;
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener onDispatchKeyEventListener) {
            this.mOnDispatchKeyEventListener = onDispatchKeyEventListener;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        public void setBackgroundColor(int i) {
            if (this.backgroundColor != i) {
                Drawable drawable = this.backgroundDrawable;
                this.backgroundColor = i;
                drawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }

        @Keep
        public void setBackAlpha(int i) {
            this.backAlpha = i;
        }

        @Keep
        public int getBackAlpha() {
            return this.backAlpha;
        }

        @Keep
        public void setBackScaleX(float f) {
            this.backScaleX = f;
            invalidate();
        }

        @Keep
        public void setBackScaleY(float f) {
            this.backScaleY = f;
            if (this.animationEnabled) {
                int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                if (this.showedFromBotton) {
                    for (int i = this.lastStartedChild; i >= 0; i--) {
                        View itemAt = getItemAt(i);
                        if (itemAt.getVisibility() == 0) {
                            Integer num = this.positions.get(itemAt);
                            if (num != null && ((float) (measuredHeight - ((num.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)))) > ((float) measuredHeight) * f) {
                                break;
                            }
                            this.lastStartedChild = i - 1;
                            startChildAnimation(itemAt);
                        }
                    }
                } else {
                    int itemsCount = getItemsCount();
                    for (int i2 = this.lastStartedChild; i2 < itemsCount; i2++) {
                        View itemAt2 = getItemAt(i2);
                        if (itemAt2.getVisibility() == 0) {
                            Integer num2 = this.positions.get(itemAt2);
                            if (num2 != null && ((float) (((num2.intValue() + 1) * AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(24.0f))) > ((float) measuredHeight) * f) {
                                break;
                            }
                            this.lastStartedChild = i2 + 1;
                            startChildAnimation(itemAt2);
                        }
                    }
                }
            }
            invalidate();
        }

        public void setBackgroundDrawable(Drawable drawable) {
            this.backgroundColor = -1;
            this.backgroundDrawable = drawable;
        }

        private void startChildAnimation(View view) {
            if (this.animationEnabled) {
                final AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{0.0f, 1.0f});
                Property property = View.TRANSLATION_Y;
                float[] fArr = new float[2];
                fArr[0] = (float) AndroidUtilities.dp(this.showedFromBotton ? 6.0f : -6.0f);
                fArr[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property, fArr);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ActionBarPopupWindowLayout.this.itemAnimators.remove(animatorSet);
                    }
                });
                animatorSet.setInterpolator(ActionBarPopupWindow.decelerateInterpolator);
                animatorSet.start();
                if (this.itemAnimators == null) {
                    this.itemAnimators = new ArrayList<>();
                }
                this.itemAnimators.add(animatorSet);
            }
        }

        public void setAnimationEnabled(boolean z) {
            this.animationEnabled = z;
        }

        public void addView(View view) {
            this.linearLayout.addView(view);
        }

        public void removeInnerViews() {
            this.linearLayout.removeAllViews();
        }

        public float getBackScaleX() {
            return this.backScaleX;
        }

        public float getBackScaleY() {
            return this.backScaleY;
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            OnDispatchKeyEventListener onDispatchKeyEventListener = this.mOnDispatchKeyEventListener;
            if (onDispatchKeyEventListener != null) {
                onDispatchKeyEventListener.onDispatchKeyEvent(keyEvent);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable drawable = this.backgroundDrawable;
            if (drawable != null) {
                drawable.setAlpha(this.backAlpha);
                if (this.showedFromBotton) {
                    int measuredHeight = getMeasuredHeight();
                    this.backgroundDrawable.setBounds(0, (int) (((float) measuredHeight) * (1.0f - this.backScaleY)), (int) (((float) getMeasuredWidth()) * this.backScaleX), measuredHeight);
                } else {
                    this.backgroundDrawable.setBounds(0, 0, (int) (((float) getMeasuredWidth()) * this.backScaleX), (int) (((float) getMeasuredHeight()) * this.backScaleY));
                }
                this.backgroundDrawable.draw(canvas);
            }
        }

        public Drawable getBackgroundDrawable() {
            return this.backgroundDrawable;
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        public View getItemAt(int i) {
            return this.linearLayout.getChildAt(i);
        }

        public void scrollToTop() {
            ScrollView scrollView2 = this.scrollView;
            if (scrollView2 != null) {
                scrollView2.scrollTo(0, 0);
            }
        }
    }

    public ActionBarPopupWindow(View view, int i, int i2) {
        super(view, i, i2);
        init();
    }

    public void setAnimationEnabled(boolean z) {
        this.animationEnabled = z;
    }

    public void setLayoutInScreen(boolean z) {
        try {
            if (layoutInScreenMethod == null) {
                Method declaredMethod = PopupWindow.class.getDeclaredMethod("setLayoutInScreenEnabled", new Class[]{Boolean.TYPE});
                layoutInScreenMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            }
            layoutInScreenMethod.invoke(this, new Object[]{true});
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void init() {
        Field field = superListenerField;
        if (field != null) {
            try {
                this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) field.get(this);
                superListenerField.set(this, NOP);
            } catch (Exception unused) {
                this.mSuperScrollListener = null;
            }
        }
    }

    public void setDismissAnimationDuration(int i) {
        this.dismissAnimationDuration = i;
    }

    /* access modifiers changed from: private */
    public void unregisterListener() {
        ViewTreeObserver viewTreeObserver;
        if (this.mSuperScrollListener != null && (viewTreeObserver = this.mViewTreeObserver) != null) {
            if (viewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }
    }

    private void registerListener(View view) {
        if (this.mSuperScrollListener != null) {
            ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
            ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
            if (viewTreeObserver != viewTreeObserver2) {
                if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = viewTreeObserver;
                if (viewTreeObserver != null) {
                    viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    public void dimBehind() {
        View rootView = getContentView().getRootView();
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
        layoutParams.flags |= 2;
        layoutParams.dimAmount = 0.2f;
        ((WindowManager) getContentView().getContext().getSystemService("window")).updateViewLayout(rootView, layoutParams);
    }

    public void showAsDropDown(View view, int i, int i2) {
        try {
            super.showAsDropDown(view, i, i2);
            registerListener(view);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void startAnimation() {
        if (this.animationEnabled && this.windowAnimatorSet == null) {
            ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) getContentView();
            actionBarPopupWindowLayout.setTranslationY(0.0f);
            actionBarPopupWindowLayout.setAlpha(1.0f);
            actionBarPopupWindowLayout.setPivotX((float) actionBarPopupWindowLayout.getMeasuredWidth());
            actionBarPopupWindowLayout.setPivotY(0.0f);
            int itemsCount = actionBarPopupWindowLayout.getItemsCount();
            actionBarPopupWindowLayout.positions.clear();
            int i = 0;
            for (int i2 = 0; i2 < itemsCount; i2++) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i2);
                itemAt.setAlpha(0.0f);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i));
                    i++;
                }
            }
            if (actionBarPopupWindowLayout.showedFromBotton) {
                int unused = actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
            } else {
                int unused2 = actionBarPopupWindowLayout.lastStartedChild = 0;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.windowAnimatorSet = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", new int[]{0, 255})});
            this.windowAnimatorSet.setDuration((long) ((i * 16) + 150));
            this.windowAnimatorSet.addListener(new Animator.AnimatorListener() {
                public void onAnimationRepeat(Animator animator) {
                }

                public void onAnimationStart(Animator animator) {
                }

                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = ActionBarPopupWindow.this.windowAnimatorSet = null;
                    ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) ActionBarPopupWindow.this.getContentView();
                    int itemsCount = actionBarPopupWindowLayout.getItemsCount();
                    for (int i = 0; i < itemsCount; i++) {
                        actionBarPopupWindowLayout.getItemAt(i).setAlpha(1.0f);
                    }
                }

                public void onAnimationCancel(Animator animator) {
                    onAnimationEnd(animator);
                }
            });
            this.windowAnimatorSet.start();
        }
    }

    public void update(View view, int i, int i2, int i3, int i4) {
        super.update(view, i, i2, i3, i4);
        registerListener(view);
    }

    public void update(View view, int i, int i2) {
        super.update(view, i, i2);
        registerListener(view);
    }

    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        unregisterListener();
    }

    public void dismiss() {
        dismiss(true);
    }

    public void dismiss(boolean z) {
        setFocusable(false);
        if (!this.animationEnabled || !z) {
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
            return;
        }
        AnimatorSet animatorSet = this.windowAnimatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) getContentView();
        if (actionBarPopupWindowLayout.itemAnimators != null && !actionBarPopupWindowLayout.itemAnimators.isEmpty()) {
            int size = actionBarPopupWindowLayout.itemAnimators.size();
            for (int i = 0; i < size; i++) {
                AnimatorSet animatorSet2 = (AnimatorSet) actionBarPopupWindowLayout.itemAnimators.get(i);
                animatorSet2.removeAllListeners();
                animatorSet2.cancel();
            }
            actionBarPopupWindowLayout.itemAnimators.clear();
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.windowAnimatorSet = animatorSet3;
        Animator[] animatorArr = new Animator[2];
        Property property = View.TRANSLATION_Y;
        float[] fArr = new float[1];
        fArr[0] = (float) AndroidUtilities.dp(actionBarPopupWindowLayout.showedFromBotton ? 5.0f : -5.0f);
        animatorArr[0] = ObjectAnimator.ofFloat(actionBarPopupWindowLayout, property, fArr);
        animatorArr[1] = ObjectAnimator.ofFloat(actionBarPopupWindowLayout, View.ALPHA, new float[]{0.0f});
        animatorSet3.playTogether(animatorArr);
        this.windowAnimatorSet.setDuration((long) this.dismissAnimationDuration);
        this.windowAnimatorSet.addListener(new Animator.AnimatorListener() {
            public void onAnimationRepeat(Animator animator) {
            }

            public void onAnimationStart(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ActionBarPopupWindow.this.windowAnimatorSet = null;
                ActionBarPopupWindow.this.setFocusable(false);
                try {
                    ActionBarPopupWindow.super.dismiss();
                } catch (Exception unused2) {
                }
                ActionBarPopupWindow.this.unregisterListener();
            }

            public void onAnimationCancel(Animator animator) {
                onAnimationEnd(animator);
            }
        });
        this.windowAnimatorSet.start();
    }
}
