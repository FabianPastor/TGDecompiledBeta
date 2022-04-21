package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.ui.Components.PopupSwipeBackLayout;

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

    public interface onSizeChangedListener {
        void onSizeChanged();
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
        public static final int FLAG_USE_SWIPEBACK = 1;
        private boolean animationEnabled;
        private int backAlpha;
        private float backScaleX;
        /* access modifiers changed from: private */
        public float backScaleY;
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
        private onSizeChangedListener onSizeChangedListener;
        /* access modifiers changed from: private */
        public HashMap<View, Integer> positions;
        private final Theme.ResourcesProvider resourcesProvider;
        private ScrollView scrollView;
        /* access modifiers changed from: private */
        public boolean shownFromBotton;
        public boolean swipeBackGravityRight;
        private PopupSwipeBackLayout swipeBackLayout;
        private View topView;
        public boolean updateAnimation;

        public ActionBarPopupWindowLayout(Context context) {
            this(context, (Theme.ResourcesProvider) null);
        }

        public ActionBarPopupWindowLayout(Context context, Theme.ResourcesProvider resourcesProvider2) {
            this(context, NUM, resourcesProvider2);
        }

        public ActionBarPopupWindowLayout(Context context, int resId, Theme.ResourcesProvider resourcesProvider2) {
            this(context, resId, resourcesProvider2, 0);
        }

        public ActionBarPopupWindowLayout(Context context, int resId, Theme.ResourcesProvider resourcesProvider2, int flags) {
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
            if (resId != 0) {
                this.backgroundDrawable = getResources().getDrawable(resId).mutate();
                setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
                setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
            }
            setWillNotDraw(false);
            if ((flags & 1) > 0) {
                PopupSwipeBackLayout popupSwipeBackLayout = new PopupSwipeBackLayout(context, resourcesProvider2);
                this.swipeBackLayout = popupSwipeBackLayout;
                addView(popupSwipeBackLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
            try {
                ScrollView scrollView2 = new ScrollView(context);
                this.scrollView = scrollView2;
                scrollView2.setVerticalScrollBarEnabled(false);
                PopupSwipeBackLayout popupSwipeBackLayout2 = this.swipeBackLayout;
                if (popupSwipeBackLayout2 != null) {
                    popupSwipeBackLayout2.addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
                } else {
                    addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
                }
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
                                Object fitToWidth = view.getTag(NUM);
                                if (tag != null) {
                                    view.getLayoutParams().width = -2;
                                }
                                measureChildWithMargins(view, widthMeasureSpec, 0, heightMeasureSpec, 0);
                                if (fitToWidth == null) {
                                    if (!(tag instanceof Integer) && tag2 == null) {
                                        maxWidth = Math.max(maxWidth, view.getMeasuredWidth());
                                    } else if ((tag instanceof Integer) != 0) {
                                        fixWidth = Math.max(((Integer) tag).intValue(), view.getMeasuredWidth());
                                        int unused3 = ActionBarPopupWindowLayout.this.gapStartY = view.getMeasuredHeight();
                                        ActionBarPopupWindowLayout actionBarPopupWindowLayout = ActionBarPopupWindowLayout.this;
                                        int unused4 = actionBarPopupWindowLayout.gapEndY = actionBarPopupWindowLayout.gapStartY + AndroidUtilities.dp(6.0f);
                                    }
                                }
                                if (viewsToFix == null) {
                                    viewsToFix = new ArrayList<>();
                                }
                                viewsToFix.add(view);
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
                return;
            }
            PopupSwipeBackLayout popupSwipeBackLayout3 = this.swipeBackLayout;
            if (popupSwipeBackLayout3 != null) {
                popupSwipeBackLayout3.addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public PopupSwipeBackLayout getSwipeBack() {
            return this.swipeBackLayout;
        }

        public int addViewToSwipeBack(View v) {
            this.swipeBackLayout.addView(v, LayoutHelper.createFrame(-2, -2.0f));
            return this.swipeBackLayout.getChildCount() - 1;
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
            Drawable drawable;
            if (this.backgroundColor != color && (drawable = this.backgroundDrawable) != null) {
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
            if (this.backScaleY != value) {
                this.backScaleY = value;
                this.backScaleX = value;
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        public void setBackScaleY(float value) {
            if (this.backScaleY != value) {
                this.backScaleY = value;
                if (this.animationEnabled && this.updateAnimation) {
                    int height = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                    if (this.shownFromBotton) {
                        for (int a = this.lastStartedChild; a >= 0; a--) {
                            View child = getItemAt(a);
                            if (child.getVisibility() == 0 && !(child instanceof GapView)) {
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
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
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
        public void dispatchDraw(Canvas canvas) {
            if (this.swipeBackGravityRight) {
                setTranslationX(((float) getMeasuredWidth()) * (1.0f - this.backScaleX));
                View view = this.topView;
                if (view != null) {
                    view.setTranslationX(((float) getMeasuredWidth()) * (1.0f - this.backScaleX));
                    this.topView.setAlpha(1.0f - this.swipeBackLayout.transitionProgress);
                    float yOffset = (-((float) (this.topView.getMeasuredHeight() - AndroidUtilities.dp(16.0f)))) * this.swipeBackLayout.transitionProgress;
                    this.topView.setTranslationY(yOffset);
                    setTranslationY(yOffset);
                }
            }
            super.dispatchDraw(canvas);
        }

        /* JADX WARNING: type inference failed for: r5v5, types: [android.view.ViewParent] */
        /* access modifiers changed from: protected */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r20) {
            /*
                r19 = this;
                r0 = r19
                r8 = r20
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                if (r1 == 0) goto L_0x01d0
                int r1 = r0.gapStartY
                android.widget.ScrollView r2 = r0.scrollView
                int r2 = r2.getScrollY()
                int r9 = r1 - r2
                int r1 = r0.gapEndY
                android.widget.ScrollView r2 = r0.scrollView
                int r2 = r2.getScrollY()
                int r10 = r1 - r2
                r1 = 0
                r2 = 0
            L_0x001e:
                android.widget.LinearLayout r3 = r0.linearLayout
                int r3 = r3.getChildCount()
                if (r2 >= r3) goto L_0x0036
                android.widget.LinearLayout r3 = r0.linearLayout
                android.view.View r3 = r3.getChildAt(r2)
                boolean r3 = r3 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.GapView
                if (r3 == 0) goto L_0x0033
                r1 = 1
                r11 = r1
                goto L_0x0037
            L_0x0033:
                int r2 = r2 + 1
                goto L_0x001e
            L_0x0036:
                r11 = r1
            L_0x0037:
                r1 = 0
                r12 = r1
            L_0x0039:
                r1 = 2
                if (r12 >= r1) goto L_0x01d0
                r13 = 1098907648(0x41800000, float:16.0)
                r1 = 1
                if (r12 != r1) goto L_0x004a
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r1 = -r1
                if (r9 >= r1) goto L_0x004a
                goto L_0x01d0
            L_0x004a:
                r14 = 0
                r15 = 1
                r7 = 255(0xff, float:3.57E-43)
                r5 = 0
                if (r11 == 0) goto L_0x007d
                int r1 = r0.backAlpha
                if (r1 == r7) goto L_0x007d
                r2 = 0
                android.graphics.Rect r1 = r0.bgPaddings
                int r1 = r1.top
                float r3 = (float) r1
                int r1 = r19.getMeasuredWidth()
                float r4 = (float) r1
                int r1 = r19.getMeasuredHeight()
                float r1 = (float) r1
                int r6 = r0.backAlpha
                r17 = 31
                r18 = r1
                r1 = r20
                r13 = 0
                r5 = r18
                r13 = -1000000(0xfffffffffff0bdc0, float:NaN)
                r16 = 255(0xff, float:3.57E-43)
                r7 = r17
                r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                r14 = 1
                r15 = 0
                goto L_0x009a
            L_0x007d:
                r13 = -1000000(0xfffffffffff0bdc0, float:NaN)
                r16 = 255(0xff, float:3.57E-43)
                int r1 = r0.gapStartY
                if (r1 == r13) goto L_0x009a
                r14 = 1
                r20.save()
                android.graphics.Rect r1 = r0.bgPaddings
                int r1 = r1.top
                int r2 = r19.getMeasuredWidth()
                int r3 = r19.getMeasuredHeight()
                r4 = 0
                r8.clipRect(r4, r1, r2, r3)
            L_0x009a:
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                if (r15 == 0) goto L_0x00a1
                int r7 = r0.backAlpha
                goto L_0x00a3
            L_0x00a1:
                r7 = 255(0xff, float:3.57E-43)
            L_0x00a3:
                r1.setAlpha(r7)
                boolean r1 = r0.shownFromBotton
                r2 = 1065353216(0x3var_, float:1.0)
                if (r1 == 0) goto L_0x00c9
                int r1 = r19.getMeasuredHeight()
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                float r4 = (float) r1
                float r5 = r0.backScaleY
                float r2 = r2 - r5
                float r4 = r4 * r2
                int r2 = (int) r4
                int r4 = r19.getMeasuredWidth()
                float r4 = (float) r4
                float r5 = r0.backScaleX
                float r4 = r4 * r5
                int r4 = (int) r4
                r5 = 0
                r3.setBounds(r5, r2, r4, r1)
                goto L_0x0155
            L_0x00c9:
                r1 = 1098907648(0x41800000, float:16.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = -r3
                if (r9 <= r1) goto L_0x012e
                int r1 = r19.getMeasuredHeight()
                float r1 = (float) r1
                float r3 = r0.backScaleY
                float r1 = r1 * r3
                int r1 = (int) r1
                if (r12 != 0) goto L_0x0112
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                android.widget.ScrollView r4 = r0.scrollView
                int r4 = r4.getScrollY()
                int r4 = -r4
                int r5 = r0.gapStartY
                if (r5 == r13) goto L_0x00f0
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r2)
                goto L_0x00f1
            L_0x00f0:
                r5 = 0
            L_0x00f1:
                int r4 = r4 + r5
                int r2 = r19.getMeasuredWidth()
                float r2 = (float) r2
                float r5 = r0.backScaleX
                float r2 = r2 * r5
                int r2 = (int) r2
                int r5 = r0.gapStartY
                if (r5 == r13) goto L_0x010c
                r5 = 1098907648(0x41800000, float:16.0)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
                int r5 = r5 + r9
                int r5 = java.lang.Math.min(r1, r5)
                goto L_0x010d
            L_0x010c:
                r5 = r1
            L_0x010d:
                r6 = 0
                r3.setBounds(r6, r4, r2, r5)
                goto L_0x012d
            L_0x0112:
                if (r1 >= r10) goto L_0x011d
                int r2 = r0.gapStartY
                if (r2 == r13) goto L_0x01cc
                r20.restore()
                goto L_0x01cc
            L_0x011d:
                android.graphics.drawable.Drawable r2 = r0.backgroundDrawable
                int r3 = r19.getMeasuredWidth()
                float r3 = (float) r3
                float r4 = r0.backScaleX
                float r3 = r3 * r4
                int r3 = (int) r3
                r4 = 0
                r2.setBounds(r4, r10, r3, r1)
            L_0x012d:
                goto L_0x0155
            L_0x012e:
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                int r2 = r0.gapStartY
                if (r2 >= 0) goto L_0x0136
                r5 = 0
                goto L_0x013d
            L_0x0136:
                r2 = 1098907648(0x41800000, float:16.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r5 = -r2
            L_0x013d:
                int r2 = r19.getMeasuredWidth()
                float r2 = (float) r2
                float r3 = r0.backScaleX
                float r2 = r2 * r3
                int r2 = (int) r2
                int r3 = r19.getMeasuredHeight()
                float r3 = (float) r3
                float r4 = r0.backScaleY
                float r3 = r3 * r4
                int r3 = (int) r3
                r4 = 0
                r1.setBounds(r4, r5, r2, r3)
            L_0x0155:
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                r1.draw(r8)
                if (r11 == 0) goto L_0x01c7
                r20.save()
                android.graphics.Rect r1 = org.telegram.messenger.AndroidUtilities.rectTmp2
                android.graphics.drawable.Drawable r2 = r0.backgroundDrawable
                android.graphics.Rect r2 = r2.getBounds()
                r1.set(r2)
                android.graphics.Rect r1 = org.telegram.messenger.AndroidUtilities.rectTmp2
                r2 = 1090519040(0x41000000, float:8.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r1.inset(r3, r2)
                android.graphics.Rect r1 = org.telegram.messenger.AndroidUtilities.rectTmp2
                r8.clipRect(r1)
                r1 = 0
            L_0x017f:
                android.widget.LinearLayout r2 = r0.linearLayout
                int r2 = r2.getChildCount()
                if (r1 >= r2) goto L_0x01c4
                android.widget.LinearLayout r2 = r0.linearLayout
                android.view.View r2 = r2.getChildAt(r1)
                boolean r2 = r2 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.GapView
                if (r2 == 0) goto L_0x01c1
                r20.save()
                r2 = 0
                r3 = 0
                android.widget.LinearLayout r4 = r0.linearLayout
                android.view.View r4 = r4.getChildAt(r1)
            L_0x019c:
                if (r4 == r0) goto L_0x01b2
                float r5 = r4.getX()
                float r2 = r2 + r5
                float r5 = r4.getY()
                float r3 = r3 + r5
                android.view.ViewParent r5 = r4.getParent()
                r4 = r5
                android.view.View r4 = (android.view.View) r4
                if (r4 != 0) goto L_0x019c
                return
            L_0x01b2:
                r8.translate(r2, r3)
                android.widget.LinearLayout r5 = r0.linearLayout
                android.view.View r5 = r5.getChildAt(r1)
                r5.draw(r8)
                r20.restore()
            L_0x01c1:
                int r1 = r1 + 1
                goto L_0x017f
            L_0x01c4:
                r20.restore()
            L_0x01c7:
                if (r14 == 0) goto L_0x01cc
                r20.restore()
            L_0x01cc:
                int r12 = r12 + 1
                goto L_0x0039
            L_0x01d0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout.onDraw(android.graphics.Canvas):void");
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

        public void setOnSizeChangedListener(onSizeChangedListener onSizeChangedListener2) {
            this.onSizeChangedListener = onSizeChangedListener2;
        }

        public int getVisibleHeight() {
            return (int) (((float) getMeasuredHeight()) * this.backScaleY);
        }

        public void setTopView(View topView2) {
            this.topView = topView2;
        }

        public void setSwipeBackForegroundColor(int color) {
            getSwipeBack().setForegroundColor(color);
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

    private void dismissDim() {
        View container = getContentView().getRootView();
        WindowManager wm = (WindowManager) getContentView().getContext().getSystemService("window");
        if (container.getLayoutParams() != null && (container.getLayoutParams() instanceof WindowManager.LayoutParams)) {
            WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
            try {
                if ((p.flags & 2) != 0) {
                    p.flags &= -3;
                    p.dimAmount = 0.0f;
                    wm.updateViewLayout(container, p);
                }
            } catch (Exception e) {
            }
        }
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
            r12 = this;
            boolean r0 = r12.animationEnabled
            if (r0 == 0) goto L_0x00d7
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            if (r0 == 0) goto L_0x0009
            return
        L_0x0009:
            android.view.View r0 = r12.getContentView()
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
            boolean r5 = r1.shownFromBotton
            r6 = 0
            if (r5 == 0) goto L_0x007f
            int r5 = r3 + -1
            int unused = r1.lastStartedChild = r5
            goto L_0x0082
        L_0x007f:
            int unused = r1.lastStartedChild = r6
        L_0x0082:
            r5 = 1065353216(0x3var_, float:1.0)
            org.telegram.ui.Components.PopupSwipeBackLayout r7 = r1.getSwipeBack()
            if (r7 == 0) goto L_0x0095
            org.telegram.ui.Components.PopupSwipeBackLayout r7 = r1.getSwipeBack()
            r7.invalidateTransforms()
            float r5 = r1.backScaleY
        L_0x0095:
            android.animation.AnimatorSet r7 = new android.animation.AnimatorSet
            r7.<init>()
            r12.windowAnimatorSet = r7
            r8 = 2
            android.animation.Animator[] r9 = new android.animation.Animator[r8]
            float[] r10 = new float[r8]
            r10[r6] = r2
            r2 = 1
            r10[r2] = r5
            java.lang.String r11 = "backScaleY"
            android.animation.ObjectAnimator r10 = android.animation.ObjectAnimator.ofFloat(r1, r11, r10)
            r9[r6] = r10
            int[] r6 = new int[r8]
            r6 = {0, 255} // fill-array
            java.lang.String r8 = "backAlpha"
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofInt(r1, r8, r6)
            r9[r2] = r6
            r7.playTogether(r9)
            android.animation.AnimatorSet r2 = r12.windowAnimatorSet
            int r6 = r4 * 16
            int r6 = r6 + 150
            long r6 = (long) r6
            r2.setDuration(r6)
            android.animation.AnimatorSet r2 = r12.windowAnimatorSet
            org.telegram.ui.ActionBar.ActionBarPopupWindow$1 r6 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$1
            r6.<init>()
            r2.addListener(r6)
            android.animation.AnimatorSet r2 = r12.windowAnimatorSet
            r2.start()
        L_0x00d7:
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
            r12.dismissDim()
            android.animation.AnimatorSet r1 = r12.windowAnimatorSet
            r2 = 0
            if (r1 == 0) goto L_0x0018
            if (r13 == 0) goto L_0x0013
            boolean r3 = r12.isClosingAnimated
            if (r3 == 0) goto L_0x0013
            return
        L_0x0013:
            r1.cancel()
            r12.windowAnimatorSet = r2
        L_0x0018:
            r12.isClosingAnimated = r0
            boolean r1 = r12.animationEnabled
            if (r1 == 0) goto L_0x00fc
            if (r13 == 0) goto L_0x00fc
            r1 = 1
            r12.isClosingAnimated = r1
            android.view.View r3 = r12.getContentView()
            android.view.ViewGroup r3 = (android.view.ViewGroup) r3
            r4 = 0
            r5 = 0
        L_0x002b:
            int r6 = r3.getChildCount()
            if (r5 >= r6) goto L_0x0043
            android.view.View r6 = r3.getChildAt(r5)
            boolean r6 = r6 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout
            if (r6 == 0) goto L_0x0040
            android.view.View r6 = r3.getChildAt(r5)
            r4 = r6
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r4 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout) r4
        L_0x0040:
            int r5 = r5 + 1
            goto L_0x002b
        L_0x0043:
            if (r4 == 0) goto L_0x007a
            java.util.ArrayList r5 = r4.itemAnimators
            if (r5 == 0) goto L_0x007a
            java.util.ArrayList r5 = r4.itemAnimators
            boolean r5 = r5.isEmpty()
            if (r5 != 0) goto L_0x007a
            r5 = 0
            java.util.ArrayList r6 = r4.itemAnimators
            int r6 = r6.size()
        L_0x005e:
            if (r5 >= r6) goto L_0x0073
            java.util.ArrayList r7 = r4.itemAnimators
            java.lang.Object r7 = r7.get(r5)
            android.animation.AnimatorSet r7 = (android.animation.AnimatorSet) r7
            r7.removeAllListeners()
            r7.cancel()
            int r5 = r5 + 1
            goto L_0x005e
        L_0x0073:
            java.util.ArrayList r5 = r4.itemAnimators
            r5.clear()
        L_0x007a:
            android.animation.AnimatorSet r5 = new android.animation.AnimatorSet
            r5.<init>()
            r12.windowAnimatorSet = r5
            long r6 = r12.outEmptyTime
            r8 = 0
            r10 = 2
            int r11 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r11 <= 0) goto L_0x00a2
            android.animation.Animator[] r1 = new android.animation.Animator[r1]
            float[] r6 = new float[r10]
            r6 = {0, NUM} // fill-array
            android.animation.ValueAnimator r6 = android.animation.ValueAnimator.ofFloat(r6)
            r1[r0] = r6
            r5.playTogether(r1)
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            long r5 = r12.outEmptyTime
            r0.setDuration(r5)
            goto L_0x00da
        L_0x00a2:
            android.animation.Animator[] r6 = new android.animation.Animator[r10]
            android.util.Property r7 = android.view.View.TRANSLATION_Y
            float[] r8 = new float[r1]
            if (r4 == 0) goto L_0x00b3
            boolean r9 = r4.shownFromBotton
            if (r9 == 0) goto L_0x00b3
            r9 = 1084227584(0x40a00000, float:5.0)
            goto L_0x00b5
        L_0x00b3:
            r9 = -1063256064(0xffffffffc0a00000, float:-5.0)
        L_0x00b5:
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
        L_0x00da:
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            org.telegram.ui.ActionBar.ActionBarPopupWindow$2 r1 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$2
            r1.<init>()
            r0.addListener(r1)
            boolean r0 = r12.pauseNotifications
            if (r0 == 0) goto L_0x00f6
            int r0 = r12.currentAccount
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getInstance(r0)
            int r1 = r12.popupAnimationIndex
            int r0 = r0.setAnimationInProgress(r1, r2)
            r12.popupAnimationIndex = r0
        L_0x00f6:
            android.animation.AnimatorSet r0 = r12.windowAnimatorSet
            r0.start()
            goto L_0x0104
        L_0x00fc:
            super.dismiss()     // Catch:{ Exception -> 0x0100 }
            goto L_0x0101
        L_0x0100:
            r0 = move-exception
        L_0x0101:
            r12.unregisterListener()
        L_0x0104:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarPopupWindow.dismiss(boolean):void");
    }

    public void setEmptyOutAnimation(long time) {
        this.outEmptyTime = time;
    }

    public static class GapView extends FrameLayout {
        int color = 0;
        String colorKey;
        Paint paint = new Paint();

        public GapView(Context context, String colorKey2) {
            super(context);
            this.colorKey = colorKey2;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i = this.color;
            if (i == 0) {
                this.paint.setColor(Theme.getColor(this.colorKey));
            } else {
                this.paint.setColor(i);
            }
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight(), this.paint);
        }

        public void setColor(int color2) {
            this.color = color2;
        }
    }
}
