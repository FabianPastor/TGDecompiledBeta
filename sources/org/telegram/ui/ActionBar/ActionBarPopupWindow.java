package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class ActionBarPopupWindow extends PopupWindow {
    private static final ViewTreeObserver.OnScrollChangedListener NOP = ActionBarPopupWindow$$ExternalSyntheticLambda0.INSTANCE;
    /* access modifiers changed from: private */
    public static final boolean allowAnimation = (Build.VERSION.SDK_INT >= 18);
    /* access modifiers changed from: private */
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static Method layoutInScreenMethod;
    private static final Field superListenerField;
    private boolean animationEnabled = allowAnimation;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    private int dismissAnimationDuration = 150;
    /* access modifiers changed from: private */
    public boolean isClosingAnimated;
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    private long outEmptyTime = -1;
    /* access modifiers changed from: private */
    public boolean pauseNotifications;
    /* access modifiers changed from: private */
    public int popupAnimationIndex = -1;
    /* access modifiers changed from: private */
    public AnimatorSet windowAnimatorSet;

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    static {
        Field f = null;
        try {
            f = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        superListenerField = f;
    }

    static /* synthetic */ void lambda$static$0() {
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
        private boolean animationEnabled;
        private int backAlpha;
        private float backScaleX;
        private float backScaleY;
        private int backgroundColor;
        protected Drawable backgroundDrawable;
        private Rect bgPaddings;
        /* access modifiers changed from: private */
        public boolean fitItems;
        /* access modifiers changed from: private */
        public int gapEndY;
        /* access modifiers changed from: private */
        public int gapStartY;
        /* access modifiers changed from: private */
        public ArrayList<AnimatorSet> itemAnimators;
        /* access modifiers changed from: private */
        public int lastStartedChild;
        protected LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        /* access modifiers changed from: private */
        public HashMap<View, Integer> positions;
        private final Theme.ResourcesProvider resourcesProvider;
        private ScrollView scrollView;
        /* access modifiers changed from: private */
        public boolean shownFromBotton;

        public ActionBarPopupWindowLayout(Context context) {
            this(context, (Theme.ResourcesProvider) null);
        }

        public ActionBarPopupWindowLayout(Context context, Theme.ResourcesProvider resourcesProvider2) {
            this(context, NUM, resourcesProvider2);
        }

        public ActionBarPopupWindowLayout(Context context, int resId, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.backScaleX = 1.0f;
            this.backScaleY = 1.0f;
            this.backAlpha = 255;
            this.lastStartedChild = 0;
            this.animationEnabled = ActionBarPopupWindow.allowAnimation;
            this.positions = new HashMap<>();
            this.gapStartY = -1000000;
            this.gapEndY = -1000000;
            this.bgPaddings = new Rect();
            this.backgroundColor = -1;
            this.resourcesProvider = resourcesProvider2;
            Drawable mutate = getResources().getDrawable(resId).mutate();
            this.backgroundDrawable = mutate;
            if (mutate != null) {
                mutate.getPadding(this.bgPaddings);
            }
            setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
            try {
                ScrollView scrollView2 = new ScrollView(context);
                this.scrollView = scrollView2;
                scrollView2.setVerticalScrollBarEnabled(false);
                addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
            } catch (Throwable e) {
                FileLog.e(e);
            }
            AnonymousClass1 r0 = new LinearLayout(context) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    if (ActionBarPopupWindowLayout.this.fitItems) {
                        int unused = ActionBarPopupWindowLayout.this.gapStartY = -1000000;
                        int unused2 = ActionBarPopupWindowLayout.this.gapEndY = -1000000;
                        int N = getChildCount();
                        int maxWidth = 0;
                        int fixWidth = 0;
                        ArrayList<View> viewsToFix = null;
                        for (int a = 0; a < N; a++) {
                            View view = getChildAt(a);
                            if (view.getVisibility() != 8) {
                                Object tag = view.getTag(NUM);
                                Object tag2 = view.getTag(NUM);
                                if (tag != null) {
                                    view.getLayoutParams().width = -2;
                                }
                                measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
                                if ((tag instanceof Integer) || tag2 != null) {
                                    if ((tag instanceof Integer) != 0) {
                                        fixWidth = Math.max(((Integer) tag).intValue(), view.getMeasuredWidth());
                                        int unused3 = ActionBarPopupWindowLayout.this.gapStartY = view.getMeasuredHeight();
                                        ActionBarPopupWindowLayout actionBarPopupWindowLayout = ActionBarPopupWindowLayout.this;
                                        int unused4 = actionBarPopupWindowLayout.gapEndY = actionBarPopupWindowLayout.gapStartY + AndroidUtilities.dp(6.0f);
                                    }
                                    if (viewsToFix == null) {
                                        viewsToFix = new ArrayList<>();
                                    }
                                    viewsToFix.add(view);
                                } else {
                                    maxWidth = Math.max(maxWidth, view.getMeasuredWidth());
                                }
                            }
                        }
                        if (viewsToFix != null) {
                            int N2 = viewsToFix.size();
                            for (int a2 = 0; a2 < N2; a2++) {
                                viewsToFix.get(a2).getLayoutParams().width = Math.max(maxWidth, fixWidth);
                            }
                        }
                    }
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            };
            this.linearLayout = r0;
            r0.setOrientation(1);
            ScrollView scrollView3 = this.scrollView;
            if (scrollView3 != null) {
                scrollView3.addView(this.linearLayout, new FrameLayout.LayoutParams(-2, -2));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public void setFitItems(boolean value) {
            this.fitItems = value;
        }

        public void setShownFromBotton(boolean value) {
            this.shownFromBotton = value;
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener listener) {
            this.mOnDispatchKeyEventListener = listener;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        public void setBackgroundColor(int color) {
            if (this.backgroundColor != color) {
                Drawable drawable = this.backgroundDrawable;
                this.backgroundColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        }

        public void setBackAlpha(int value) {
            this.backAlpha = value;
        }

        public int getBackAlpha() {
            return this.backAlpha;
        }

        public void setBackScaleX(float value) {
            this.backScaleX = value;
            invalidate();
        }

        public void setBackScaleY(float value) {
            this.backScaleY = value;
            if (this.animationEnabled) {
                int height = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                if (this.shownFromBotton) {
                    for (int a = this.lastStartedChild; a >= 0; a--) {
                        View child = getItemAt(a);
                        if (child.getVisibility() == 0) {
                            Integer position = this.positions.get(child);
                            if (position != null && ((float) (height - ((position.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)))) > ((float) height) * value) {
                                break;
                            }
                            this.lastStartedChild = a - 1;
                            startChildAnimation(child);
                        }
                    }
                } else {
                    int count = getItemsCount();
                    int h = 0;
                    for (int a2 = 0; a2 < count; a2++) {
                        View child2 = getItemAt(a2);
                        if (child2.getVisibility() == 0) {
                            h += child2.getMeasuredHeight();
                            if (a2 >= this.lastStartedChild) {
                                if (this.positions.get(child2) != null && ((float) (h - AndroidUtilities.dp(24.0f))) > ((float) height) * value) {
                                    break;
                                }
                                this.lastStartedChild = a2 + 1;
                                startChildAnimation(child2);
                            } else {
                                continue;
                            }
                        }
                    }
                }
            }
            invalidate();
        }

        public void setBackgroundDrawable(Drawable drawable) {
            this.backgroundColor = -1;
            this.backgroundDrawable = drawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
            }
        }

        private void startChildAnimation(View child) {
            if (this.animationEnabled) {
                final AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                Property property = View.ALPHA;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = child.isEnabled() ? 1.0f : 0.5f;
                animatorArr[0] = ObjectAnimator.ofFloat(child, property, fArr);
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = (float) AndroidUtilities.dp(this.shownFromBotton ? 6.0f : -6.0f);
                fArr2[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(child, property2, fArr2);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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

        public void setAnimationEnabled(boolean value) {
            this.animationEnabled = value;
        }

        public void addView(View child) {
            this.linearLayout.addView(child);
        }

        public void addView(View child, LinearLayout.LayoutParams layoutParams) {
            this.linearLayout.addView(child, layoutParams);
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

        public boolean dispatchKeyEvent(KeyEvent event) {
            OnDispatchKeyEventListener onDispatchKeyEventListener = this.mOnDispatchKeyEventListener;
            if (onDispatchKeyEventListener != null) {
                onDispatchKeyEventListener.onDispatchKeyEvent(event);
            }
            return super.dispatchKeyEvent(event);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.backgroundDrawable != null) {
                int start = this.gapStartY - this.scrollView.getScrollY();
                int end = this.gapEndY - this.scrollView.getScrollY();
                int a = 0;
                while (a < 2) {
                    if (a != 1 || start >= (-AndroidUtilities.dp(16.0f))) {
                        if (this.gapStartY != -1000000) {
                            canvas.save();
                            canvas.clipRect(0, this.bgPaddings.top, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.backgroundDrawable.setAlpha(this.backAlpha);
                        if (this.shownFromBotton) {
                            int height = getMeasuredHeight();
                            this.backgroundDrawable.setBounds(0, (int) (((float) height) * (1.0f - this.backScaleY)), (int) (((float) getMeasuredWidth()) * this.backScaleX), height);
                        } else if (start > (-AndroidUtilities.dp(16.0f))) {
                            int h = (int) (((float) getMeasuredHeight()) * this.backScaleY);
                            if (a == 0) {
                                this.backgroundDrawable.setBounds(0, (-this.scrollView.getScrollY()) + (this.gapStartY != -1000000 ? AndroidUtilities.dp(1.0f) : 0), (int) (((float) getMeasuredWidth()) * this.backScaleX), this.gapStartY != -1000000 ? Math.min(h, AndroidUtilities.dp(16.0f) + start) : h);
                            } else if (h < end) {
                                if (this.gapStartY != -1000000) {
                                    canvas.restore();
                                }
                                a++;
                            } else {
                                this.backgroundDrawable.setBounds(0, end, (int) (((float) getMeasuredWidth()) * this.backScaleX), h);
                            }
                        } else {
                            this.backgroundDrawable.setBounds(0, this.gapStartY < 0 ? 0 : -AndroidUtilities.dp(16.0f), (int) (((float) getMeasuredWidth()) * this.backScaleX), (int) (((float) getMeasuredHeight()) * this.backScaleY));
                        }
                        this.backgroundDrawable.draw(canvas);
                        if (this.gapStartY != -1000000) {
                            canvas.restore();
                        }
                        a++;
                    } else {
                        return;
                    }
                }
            }
        }

        public Drawable getBackgroundDrawable() {
            return this.backgroundDrawable;
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        public View getItemAt(int index) {
            return this.linearLayout.getChildAt(index);
        }

        public void scrollToTop() {
            ScrollView scrollView2 = this.scrollView;
            if (scrollView2 != null) {
                scrollView2.scrollTo(0, 0);
            }
        }

        public void setupRadialSelectors(int color) {
            int count = this.linearLayout.getChildCount();
            int a = 0;
            while (a < count) {
                View child = this.linearLayout.getChildAt(a);
                int i = 6;
                int i2 = a == 0 ? 6 : 0;
                if (a != count - 1) {
                    i = 0;
                }
                child.setBackground(Theme.createRadSelectorDrawable(color, i2, i));
                a++;
            }
        }

        public void updateRadialSelectors() {
            int count = this.linearLayout.getChildCount();
            View firstVisible = null;
            View lastVisible = null;
            for (int a = 0; a < count; a++) {
                View child = this.linearLayout.getChildAt(a);
                if (child.getVisibility() == 0) {
                    if (firstVisible == null) {
                        firstVisible = child;
                    }
                    lastVisible = child;
                }
            }
            boolean prevGap = false;
            for (int a2 = 0; a2 < count; a2++) {
                View child2 = this.linearLayout.getChildAt(a2);
                if (child2.getVisibility() == 0) {
                    Object tag = child2.getTag(NUM);
                    if (child2 instanceof ActionBarMenuSubItem) {
                        ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) child2;
                        boolean z = false;
                        boolean z2 = child2 == firstVisible || prevGap;
                        if (child2 == lastVisible) {
                            z = true;
                        }
                        actionBarMenuSubItem.updateSelectorBackground(z2, z);
                    }
                    if (tag != null) {
                        prevGap = true;
                    } else {
                        prevGap = false;
                    }
                }
            }
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public ActionBarPopupWindow() {
        init();
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        init();
    }

    public ActionBarPopupWindow(int width, int height) {
        super(width, height);
        init();
    }

    public ActionBarPopupWindow(View contentView) {
        super(contentView);
        init();
    }

    public ActionBarPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        init();
    }

    public ActionBarPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        init();
    }

    public void setAnimationEnabled(boolean value) {
        this.animationEnabled = value;
    }

    public void setLayoutInScreen(boolean value) {
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
                field.set(this, NOP);
            } catch (Exception e) {
                this.mSuperScrollListener = null;
            }
        }
    }

    public void setDismissAnimationDuration(int value) {
        this.dismissAnimationDuration = value;
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

    private void registerListener(View anchor) {
        if (this.mSuperScrollListener != null) {
            ViewTreeObserver vto = anchor.getWindowToken() != null ? anchor.getViewTreeObserver() : null;
            ViewTreeObserver viewTreeObserver = this.mViewTreeObserver;
            if (vto != viewTreeObserver) {
                if (viewTreeObserver != null && viewTreeObserver.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = vto;
                if (vto != null) {
                    vto.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    public void dimBehind() {
        View container = getContentView().getRootView();
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= 2;
        p.dimAmount = 0.2f;
        ((WindowManager) getContentView().getContext().getSystemService("window")).updateViewLayout(container, p);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {
        try {
            super.showAsDropDown(anchor, xoff, yoff);
            registerListener(anchor);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* JADX WARNING: type inference failed for: r3v7, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startAnimation() {
        /*
            r10 = this;
            boolean r0 = r10.animationEnabled
            if (r0 == 0) goto L_0x00c3
            android.animation.AnimatorSet r0 = r10.windowAnimatorSet
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            android.view.View r0 = r10.getContentView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            r1 = 0
            boolean r2 = r0 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout
            if (r2 == 0) goto L_0x0018
            r1 = r0
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r1 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout) r1
            goto L_0x0031
        L_0x0018:
            r2 = 0
        L_0x0019:
            int r3 = r0.getChildCount()
            if (r2 >= r3) goto L_0x0031
            android.view.View r3 = r0.getChildAt(r2)
            boolean r3 = r3 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout
            if (r3 == 0) goto L_0x002e
            android.view.View r3 = r0.getChildAt(r2)
            r1 = r3
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r1 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout) r1
        L_0x002e:
            int r2 = r2 + 1
            goto L_0x0019
        L_0x0031:
            r2 = 0
            r1.setTranslationY(r2)
            r3 = 1065353216(0x3var_, float:1.0)
            r1.setAlpha(r3)
            int r3 = r1.getMeasuredWidth()
            float r3 = (float) r3
            r1.setPivotX(r3)
            r1.setPivotY(r2)
            int r3 = r1.getItemsCount()
            java.util.HashMap r4 = r1.positions
            r4.clear()
            r4 = 0
            r5 = 0
        L_0x0052:
            if (r5 >= r3) goto L_0x0072
            android.view.View r6 = r1.getItemAt(r5)
            r6.setAlpha(r2)
            int r7 = r6.getVisibility()
            if (r7 == 0) goto L_0x0062
            goto L_0x006f
        L_0x0062:
            java.util.HashMap r7 = r1.positions
            java.lang.Integer r8 = java.lang.Integer.valueOf(r4)
            r7.put(r6, r8)
            int r4 = r4 + 1
        L_0x006f:
            int r5 = r5 + 1
            goto L_0x0052
        L_0x0072:
            boolean r2 = r1.shownFromBotton
            r5 = 0
            if (r2 == 0) goto L_0x007f
            int r2 = r3 + -1
            int unused = r1.lastStartedChild = r2
            goto L_0x0082
        L_0x007f:
            int unused = r1.lastStartedChild = r5
        L_0x0082:
            android.animation.AnimatorSet r2 = new android.animation.AnimatorSet
            r2.<init>()
            r10.windowAnimatorSet = r2
            r6 = 2
            android.animation.Animator[] r7 = new android.animation.Animator[r6]
            float[] r8 = new float[r6]
            r8 = {0, NUM} // fill-array
            java.lang.String r9 = "backScaleY"
            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r1, r9, r8)
            r7[r5] = r8
            int[] r5 = new int[r6]
            r5 = {0, 255} // fill-array
            java.lang.String r6 = "backAlpha"
            android.animation.ObjectAnimator r5 = android.animation.ObjectAnimator.ofInt(r1, r6, r5)
            r6 = 1
            r7[r6] = r5
            r2.playTogether(r7)
            android.animation.AnimatorSet r2 = r10.windowAnimatorSet
            int r5 = r4 * 16
            int r5 = r5 + 150
            long r5 = (long) r5
            r2.setDuration(r5)
            android.animation.AnimatorSet r2 = r10.windowAnimatorSet
            org.telegram.ui.ActionBar.ActionBarPopupWindow$1 r5 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$1
            r5.<init>()
            r2.addListener(r5)
            android.animation.AnimatorSet r2 = r10.windowAnimatorSet
            r2.start()
        L_0x00c3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.startAnimation():void");
    }

    public void update(View anchor, int xoff, int yoff, int width, int height) {
        super.update(anchor, xoff, yoff, width, height);
        registerListener(anchor);
    }

    public void update(View anchor, int width, int height) {
        super.update(anchor, width, height);
        registerListener(anchor);
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        unregisterListener();
    }

    public void dismiss() {
        dismiss(true);
    }

    public void setPauseNotifications(boolean value) {
        this.pauseNotifications = value;
    }

    /* JADX WARNING: type inference failed for: r6v9, types: [android.view.View] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dismiss(boolean r13) {
        /*
            r12 = this;
            r0 = 0
            r12.setFocusable(r0)
            android.animation.AnimatorSet r1 = r12.windowAnimatorSet
            r2 = 0
            if (r1 == 0) goto L_0x0015
            if (r13 == 0) goto L_0x0010
            boolean r3 = r12.isClosingAnimated
            if (r3 == 0) goto L_0x0010
            return
        L_0x0010:
            r1.cancel()
            r12.windowAnimatorSet = r2
        L_0x0015:
            r12.isClosingAnimated = r0
            boolean r1 = r12.animationEnabled
            if (r1 == 0) goto L_0x00f9
            if (r13 == 0) goto L_0x00f9
            r1 = 1
            r12.isClosingAnimated = r1
            android.view.View r3 = r12.getContentView()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            r4 = 0
            r5 = 0
        L_0x0028:
            int r6 = r3.getChildCount()
            if (r5 >= r6) goto L_0x0040
            android.view.View r6 = r3.getChildAt(r5)
            boolean r6 = r6 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout
            if (r6 == 0) goto L_0x003d
            android.view.View r6 = r3.getChildAt(r5)
            r4 = r6
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r4 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout) r4
        L_0x003d:
            int r5 = r5 + 1
            goto L_0x0028
        L_0x0040:
            if (r4 == 0) goto L_0x0077
            java.util.ArrayList r5 = r4.itemAnimators
            if (r5 == 0) goto L_0x0077
            java.util.ArrayList r5 = r4.itemAnimators
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x0077
            r5 = 0
            java.util.ArrayList r6 = r4.itemAnimators
            int r6 = r6.size()
        L_0x005b:
            if (r5 >= r6) goto L_0x0070
            java.util.ArrayList r7 = r4.itemAnimators
            java.lang.Object r7 = r7.get(r5)
            android.animation.AnimatorSet r7 = (android.animation.AnimatorSet) r7
            r7.removeAllListeners()
            r7.cancel()
            int r5 = r5 + 1
            goto L_0x005b
        L_0x0070:
            java.util.ArrayList r5 = r4.itemAnimators
            r5.clear()
        L_0x0077:
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            r12.windowAnimatorSet = r5
            long r6 = r12.outEmptyTime
            r8 = 0
            r10 = 2
            int r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r11 <= 0) goto L_0x009f
            android.animation.Animator[] r1 = new android.animation.Animator[r1]
            float[] r6 = new float[r10]
            r6 = {0, NUM} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
            r1[r0] = r6
            r5.playTogether(r1)
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            long r5 = r12.outEmptyTime
            r0.setDuration(r5)
            goto L_0x00d7
        L_0x009f:
            android.animation.Animator[] r6 = new android.animation.Animator[r10]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r1]
            if (r4 == 0) goto L_0x00b0
            boolean r9 = r4.shownFromBotton
            if (r9 == 0) goto L_0x00b0
            r9 = 1084227584(0x40a00000, float:5.0)
            goto L_0x00b2
        L_0x00b0:
            r9 = -1063256064(0xffffffffc0a00000, float:-5.0)
        L_0x00b2:
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r8[r0] = r9
            android.animation.ObjectAnimator r7 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r6[r0] = r7
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r1]
            r9 = 0
            r8[r0] = r9
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r3, r7, r8)
            r6[r1] = r0
            r5.playTogether(r6)
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            int r1 = r12.dismissAnimationDuration
            long r5 = (long) r1
            r0.setDuration(r5)
        L_0x00d7:
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            org.telegram.ui.ActionBar.ActionBarPopupWindow$2 r1 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$2
            r1.<init>()
            r0.addListener(r1)
            boolean r0 = r12.pauseNotifications
            if (r0 == 0) goto L_0x00f3
            int r0 = r12.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = r12.popupAnimationIndex
            int r0 = r0.setAnimationInProgress(r1, r2)
            r12.popupAnimationIndex = r0
        L_0x00f3:
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            r0.start()
            goto L_0x0101
        L_0x00f9:
            super.dismiss()     // Catch:{ Exception -> 0x00fd }
            goto L_0x00fe
        L_0x00fd:
            r0 = move-exception
        L_0x00fe:
            r12.unregisterListener()
        L_0x0101:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.dismiss(boolean):void");
    }

    public void setEmptyOutAnimation(long time) {
        this.outEmptyTime = time;
    }
}
