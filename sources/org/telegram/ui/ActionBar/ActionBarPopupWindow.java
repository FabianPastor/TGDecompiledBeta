package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarPopupWindow extends PopupWindow {
    private static final OnScrollChangedListener NOP = new C07291();
    private static final boolean allowAnimation;
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static final Field superListenerField;
    private boolean animationEnabled = allowAnimation;
    private OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    private AnimatorSet windowAnimatorSet;

    /* renamed from: org.telegram.ui.ActionBar.ActionBarPopupWindow$1 */
    static class C07291 implements OnScrollChangedListener {
        public void onScrollChanged() {
        }

        C07291() {
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarPopupWindow$2 */
    class C07302 implements AnimatorListener {
        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        C07302() {
        }

        public void onAnimationEnd(Animator animator) {
            ActionBarPopupWindow.this.windowAnimatorSet = null;
        }

        public void onAnimationCancel(Animator animator) {
            onAnimationEnd(animator);
        }
    }

    /* renamed from: org.telegram.ui.ActionBar.ActionBarPopupWindow$3 */
    class C07313 implements AnimatorListener {
        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
        }

        C07313() {
        }

        public void onAnimationEnd(android.animation.Animator r2) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r1 = this;
            r2 = org.telegram.ui.ActionBar.ActionBarPopupWindow.this;
            r0 = 0;
            r2.windowAnimatorSet = r0;
            r2 = org.telegram.ui.ActionBar.ActionBarPopupWindow.this;
            r0 = 0;
            r2.setFocusable(r0);
            r2 = org.telegram.ui.ActionBar.ActionBarPopupWindow.this;	 Catch:{ Exception -> 0x0011 }
            super.dismiss();	 Catch:{ Exception -> 0x0011 }
        L_0x0011:
            r2 = org.telegram.ui.ActionBar.ActionBarPopupWindow.this;
            r2.unregisterListener();
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.3.onAnimationEnd(android.animation.Animator):void");
        }

        public void onAnimationCancel(Animator animator) {
            onAnimationEnd(animator);
        }
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
        private boolean animationEnabled = ActionBarPopupWindow.allowAnimation;
        private int backAlpha = 255;
        private float backScaleX = 1.0f;
        private float backScaleY = 1.0f;
        protected Drawable backgroundDrawable = getResources().getDrawable(C0446R.drawable.popup_fixed).mutate();
        private int lastStartedChild = 0;
        protected LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        private HashMap<View, Integer> positions = new HashMap();
        private ScrollView scrollView;
        private boolean showedFromBotton;

        public ActionBarPopupWindowLayout(Context context) {
            super(context);
            this.backgroundDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), Mode.MULTIPLY));
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
            try {
                this.scrollView = new ScrollView(context);
                this.scrollView.setVerticalScrollBarEnabled(false);
                addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
            } catch (Throwable th) {
                FileLog.m3e(th);
            }
            this.linearLayout = new LinearLayout(context);
            this.linearLayout.setOrientation(1);
            if (this.scrollView != null) {
                this.scrollView.addView(this.linearLayout, new LayoutParams(-2, -2));
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
                int i;
                int visibility;
                int itemsCount = getItemsCount();
                for (i = 0; i < itemsCount; i++) {
                    visibility = getItemAt(i).getVisibility();
                }
                i = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                if (this.showedFromBotton) {
                    for (itemsCount = this.lastStartedChild; itemsCount >= 0; itemsCount--) {
                        View itemAt = getItemAt(itemsCount);
                        if (itemAt.getVisibility() == 0) {
                            Integer num = (Integer) this.positions.get(itemAt);
                            if (num != null && ((float) (i - ((num.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)))) > ((float) i) * f) {
                                break;
                            }
                            this.lastStartedChild = itemsCount - 1;
                            startChildAnimation(itemAt);
                        }
                    }
                } else {
                    for (visibility = this.lastStartedChild; visibility < itemsCount; visibility++) {
                        View itemAt2 = getItemAt(visibility);
                        if (itemAt2.getVisibility() == 0) {
                            Integer num2 = (Integer) this.positions.get(itemAt2);
                            if (num2 != null && ((float) (((num2.intValue() + 1) * AndroidUtilities.dp(48.0f)) - AndroidUtilities.dp(24.0f))) > ((float) i) * f) {
                                break;
                            }
                            this.lastStartedChild = visibility + 1;
                            startChildAnimation(itemAt2);
                        }
                    }
                }
            }
            invalidate();
        }

        public void setBackgroundDrawable(Drawable drawable) {
            this.backgroundDrawable = drawable;
        }

        private void startChildAnimation(View view) {
            if (this.animationEnabled) {
                AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                animatorArr[0] = ObjectAnimator.ofFloat(view, "alpha", new float[]{0.0f, 1.0f});
                String str = "translationY";
                float[] fArr = new float[2];
                fArr[0] = (float) AndroidUtilities.dp(this.showedFromBotton ? 6.0f : -6.0f);
                fArr[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, str, fArr);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(ActionBarPopupWindow.decelerateInterpolator);
                animatorSet.start();
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
            if (this.mOnDispatchKeyEventListener != null) {
                this.mOnDispatchKeyEventListener.onDispatchKeyEvent(keyEvent);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        protected void onDraw(Canvas canvas) {
            if (this.backgroundDrawable != null) {
                this.backgroundDrawable.setAlpha(this.backAlpha);
                getMeasuredHeight();
                if (this.showedFromBotton) {
                    this.backgroundDrawable.setBounds(0, (int) (((float) getMeasuredHeight()) * (1.0f - this.backScaleY)), (int) (((float) getMeasuredWidth()) * this.backScaleX), getMeasuredHeight());
                } else {
                    this.backgroundDrawable.setBounds(0, 0, (int) (((float) getMeasuredWidth()) * this.backScaleX), (int) (((float) getMeasuredHeight()) * this.backScaleY));
                }
                this.backgroundDrawable.draw(canvas);
            }
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        public View getItemAt(int i) {
            return this.linearLayout.getChildAt(i);
        }

        public void scrollToTop() {
            if (this.scrollView != null) {
                this.scrollView.scrollTo(0, 0);
            }
        }
    }

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    static {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 1;
        r2 = 18;
        if (r0 < r2) goto L_0x0009;
    L_0x0007:
        r0 = r1;
        goto L_0x000a;
    L_0x0009:
        r0 = 0;
    L_0x000a:
        allowAnimation = r0;
        r0 = new android.view.animation.DecelerateInterpolator;
        r0.<init>();
        decelerateInterpolator = r0;
        r0 = 0;
        r2 = android.widget.PopupWindow.class;	 Catch:{ NoSuchFieldException -> 0x0020 }
        r3 = "mOnScrollChangedListener";	 Catch:{ NoSuchFieldException -> 0x0020 }
        r2 = r2.getDeclaredField(r3);	 Catch:{ NoSuchFieldException -> 0x0020 }
        r2.setAccessible(r1);	 Catch:{ NoSuchFieldException -> 0x0021 }
        goto L_0x0021;
    L_0x0020:
        r2 = r0;
    L_0x0021:
        superListenerField = r2;
        r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$1;
        r0.<init>();
        NOP = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.<clinit>():void");
    }

    public ActionBarPopupWindow() {
        init();
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        init();
    }

    public ActionBarPopupWindow(int i, int i2) {
        super(i, i2);
        init();
    }

    public ActionBarPopupWindow(View view) {
        super(view);
        init();
    }

    public ActionBarPopupWindow(View view, int i, int i2, boolean z) {
        super(view, i, i2, z);
        init();
    }

    public ActionBarPopupWindow(View view, int i, int i2) {
        super(view, i, i2);
        init();
    }

    public void setAnimationEnabled(boolean z) {
        this.animationEnabled = z;
    }

    private void init() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r2 = this;
        r0 = superListenerField;
        if (r0 == 0) goto L_0x0019;
    L_0x0004:
        r0 = superListenerField;	 Catch:{ Exception -> 0x0016 }
        r0 = r0.get(r2);	 Catch:{ Exception -> 0x0016 }
        r0 = (android.view.ViewTreeObserver.OnScrollChangedListener) r0;	 Catch:{ Exception -> 0x0016 }
        r2.mSuperScrollListener = r0;	 Catch:{ Exception -> 0x0016 }
        r0 = superListenerField;	 Catch:{ Exception -> 0x0016 }
        r1 = NOP;	 Catch:{ Exception -> 0x0016 }
        r0.set(r2, r1);	 Catch:{ Exception -> 0x0016 }
        goto L_0x0019;
    L_0x0016:
        r0 = 0;
        r2.mSuperScrollListener = r0;
    L_0x0019:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.init():void");
    }

    private void unregisterListener() {
        if (this.mSuperScrollListener != null && this.mViewTreeObserver != null) {
            if (this.mViewTreeObserver.isAlive()) {
                this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
            }
            this.mViewTreeObserver = null;
        }
    }

    private void registerListener(View view) {
        if (this.mSuperScrollListener != null) {
            view = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
            if (view != this.mViewTreeObserver) {
                if (this.mViewTreeObserver != null && this.mViewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = view;
                if (view != null) {
                    view.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    public void showAsDropDown(View view, int i, int i2) {
        try {
            super.showAsDropDown(view, i, i2);
            registerListener(view);
        } catch (Throwable e) {
            FileLog.m3e(e);
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
            int i2 = i;
            while (i < itemsCount) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i2));
                    itemAt.setAlpha(0.0f);
                    i2++;
                }
                i++;
            }
            if (actionBarPopupWindowLayout.showedFromBotton) {
                actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
            } else {
                actionBarPopupWindowLayout.lastStartedChild = 0;
            }
            this.windowAnimatorSet = new AnimatorSet();
            this.windowAnimatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", new int[]{0, 255})});
            this.windowAnimatorSet.setDuration((long) (150 + (16 * i2)));
            this.windowAnimatorSet.addListener(new C07302());
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

    public void dismiss(boolean r8) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r0 = 0;
        r7.setFocusable(r0);
        r1 = r7.animationEnabled;
        if (r1 == 0) goto L_0x0069;
    L_0x0008:
        if (r8 == 0) goto L_0x0069;
    L_0x000a:
        r8 = r7.windowAnimatorSet;
        if (r8 == 0) goto L_0x0013;
    L_0x000e:
        r8 = r7.windowAnimatorSet;
        r8.cancel();
    L_0x0013:
        r8 = r7.getContentView();
        r8 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout) r8;
        r1 = new android.animation.AnimatorSet;
        r1.<init>();
        r7.windowAnimatorSet = r1;
        r1 = r7.windowAnimatorSet;
        r2 = 2;
        r2 = new android.animation.Animator[r2];
        r3 = "translationY";
        r4 = 1;
        r5 = new float[r4];
        r6 = r8.showedFromBotton;
        if (r6 == 0) goto L_0x0033;
    L_0x0030:
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        goto L_0x0035;
    L_0x0033:
        r6 = -NUM; // 0xffffffffc0a00000 float:-5.0 double:NaN;
    L_0x0035:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5[r0] = r6;
        r3 = android.animation.ObjectAnimator.ofFloat(r8, r3, r5);
        r2[r0] = r3;
        r3 = "alpha";
        r5 = new float[r4];
        r6 = 0;
        r5[r0] = r6;
        r8 = android.animation.ObjectAnimator.ofFloat(r8, r3, r5);
        r2[r4] = r8;
        r1.playTogether(r2);
        r8 = r7.windowAnimatorSet;
        r0 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r8.setDuration(r0);
        r8 = r7.windowAnimatorSet;
        r0 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$3;
        r0.<init>();
        r8.addListener(r0);
        r8 = r7.windowAnimatorSet;
        r8.start();
        goto L_0x006f;
    L_0x0069:
        super.dismiss();	 Catch:{ Exception -> 0x006c }
    L_0x006c:
        r7.unregisterListener();
    L_0x006f:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.dismiss(boolean):void");
    }
}
