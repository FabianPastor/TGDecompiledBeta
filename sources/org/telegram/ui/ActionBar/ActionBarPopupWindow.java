package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import android.view.ViewGroup;
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
import org.telegram.messenger.NotificationCenter;
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
    private boolean scaleOut;
    /* access modifiers changed from: private */
    public AnimatorSet windowAnimatorSet;

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    public interface onSizeChangedListener {
        void onSizeChanged();
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$static$0() {
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

    public void setScaleOut(boolean z) {
        this.scaleOut = z;
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
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
        public boolean shownFromBottom;
        /* access modifiers changed from: private */
        public boolean startAnimationPending;
        public int subtractBackgroundHeight;
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

        public ActionBarPopupWindowLayout(Context context, int i, Theme.ResourcesProvider resourcesProvider2) {
            this(context, i, resourcesProvider2, 0);
        }

        public ActionBarPopupWindowLayout(Context context, int i, Theme.ResourcesProvider resourcesProvider2, int i2) {
            super(context);
            this.backScaleX = 1.0f;
            this.backScaleY = 1.0f;
            this.startAnimationPending = false;
            this.backAlpha = 255;
            this.lastStartedChild = 0;
            this.animationEnabled = ActionBarPopupWindow.allowAnimation;
            this.positions = new HashMap<>();
            this.gapStartY = -1000000;
            this.gapEndY = -1000000;
            this.bgPaddings = new Rect();
            this.backgroundColor = -1;
            this.resourcesProvider = resourcesProvider2;
            if (i != 0) {
                this.backgroundDrawable = getResources().getDrawable(i).mutate();
                setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
                setBackgroundColor(getThemedColor("actionBarDefaultSubmenuBackground"));
            }
            setWillNotDraw(false);
            if ((i2 & 2) > 0) {
                this.shownFromBottom = true;
            }
            if ((i2 & 1) > 0) {
                PopupSwipeBackLayout popupSwipeBackLayout = new PopupSwipeBackLayout(context, resourcesProvider2);
                this.swipeBackLayout = popupSwipeBackLayout;
                addView(popupSwipeBackLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
            int i3 = 80;
            try {
                ScrollView scrollView2 = new ScrollView(context);
                this.scrollView = scrollView2;
                scrollView2.setVerticalScrollBarEnabled(false);
                PopupSwipeBackLayout popupSwipeBackLayout2 = this.swipeBackLayout;
                if (popupSwipeBackLayout2 != null) {
                    popupSwipeBackLayout2.addView(this.scrollView, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
                } else {
                    addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
            AnonymousClass1 r0 = new LinearLayout(context) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    if (ActionBarPopupWindowLayout.this.fitItems) {
                        int unused = ActionBarPopupWindowLayout.this.gapStartY = -1000000;
                        int unused2 = ActionBarPopupWindowLayout.this.gapEndY = -1000000;
                        int childCount = getChildCount();
                        ArrayList arrayList = null;
                        int i3 = 0;
                        int i4 = 0;
                        for (int i5 = 0; i5 < childCount; i5++) {
                            View childAt = getChildAt(i5);
                            if (childAt.getVisibility() != 8) {
                                Object tag = childAt.getTag(NUM);
                                Object tag2 = childAt.getTag(NUM);
                                Object tag3 = childAt.getTag(NUM);
                                if (tag != null) {
                                    childAt.getLayoutParams().width = -2;
                                }
                                measureChildWithMargins(childAt, i, 0, i2, 0);
                                if (tag3 == null) {
                                    boolean z = tag instanceof Integer;
                                    if (!z && tag2 == null) {
                                        i3 = Math.max(i3, childAt.getMeasuredWidth());
                                    } else if (z) {
                                        int max = Math.max(((Integer) tag).intValue(), childAt.getMeasuredWidth());
                                        int unused3 = ActionBarPopupWindowLayout.this.gapStartY = childAt.getMeasuredHeight();
                                        ActionBarPopupWindowLayout actionBarPopupWindowLayout = ActionBarPopupWindowLayout.this;
                                        int unused4 = actionBarPopupWindowLayout.gapEndY = actionBarPopupWindowLayout.gapStartY + AndroidUtilities.dp(6.0f);
                                        i4 = max;
                                    }
                                }
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                }
                                arrayList.add(childAt);
                            }
                        }
                        if (arrayList != null) {
                            int size = arrayList.size();
                            for (int i6 = 0; i6 < size; i6++) {
                                ((View) arrayList.get(i6)).getLayoutParams().width = Math.max(i3, i4);
                            }
                        }
                    }
                    super.onMeasure(i, i2);
                }

                /* access modifiers changed from: protected */
                public boolean drawChild(Canvas canvas, View view, long j) {
                    if (view instanceof GapView) {
                        return false;
                    }
                    return super.drawChild(canvas, view, j);
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
                popupSwipeBackLayout3.addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, !this.shownFromBottom ? 48 : i3));
            } else {
                addView(this.linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public PopupSwipeBackLayout getSwipeBack() {
            return this.swipeBackLayout;
        }

        public int addViewToSwipeBack(View view) {
            this.swipeBackLayout.addView(view, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
            return this.swipeBackLayout.getChildCount() - 1;
        }

        public void setFitItems(boolean z) {
            this.fitItems = z;
        }

        public void setShownFromBottom(boolean z) {
            this.shownFromBottom = z;
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener onDispatchKeyEventListener) {
            this.mOnDispatchKeyEventListener = onDispatchKeyEventListener;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        public void setBackgroundColor(int i) {
            Drawable drawable;
            if (this.backgroundColor != i && (drawable = this.backgroundDrawable) != null) {
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
            if (this.backScaleX != f) {
                this.backScaleX = f;
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        @Keep
        public void setBackScaleY(float f) {
            if (this.backScaleY != f) {
                this.backScaleY = f;
                if (this.animationEnabled && this.updateAnimation) {
                    int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                    if (this.shownFromBottom) {
                        for (int i = this.lastStartedChild; i >= 0; i--) {
                            View itemAt = getItemAt(i);
                            if (itemAt.getVisibility() == 0 && !(itemAt instanceof GapView)) {
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
                        int i2 = 0;
                        for (int i3 = 0; i3 < itemsCount; i3++) {
                            View itemAt2 = getItemAt(i3);
                            if (itemAt2.getVisibility() == 0) {
                                i2 += itemAt2.getMeasuredHeight();
                                if (i3 >= this.lastStartedChild) {
                                    if (this.positions.get(itemAt2) != null && ((float) (i2 - AndroidUtilities.dp(24.0f))) > ((float) measuredHeight) * f) {
                                        break;
                                    }
                                    this.lastStartedChild = i3 + 1;
                                    startChildAnimation(itemAt2);
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

        private void startChildAnimation(View view) {
            if (this.animationEnabled) {
                final AnimatorSet animatorSet = new AnimatorSet();
                Animator[] animatorArr = new Animator[2];
                Property property = View.ALPHA;
                float[] fArr = new float[2];
                fArr[0] = 0.0f;
                fArr[1] = view.isEnabled() ? 1.0f : 0.5f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                Property property2 = View.TRANSLATION_Y;
                float[] fArr2 = new float[2];
                fArr2[0] = (float) AndroidUtilities.dp(this.shownFromBottom ? 6.0f : -6.0f);
                fArr2[1] = 0.0f;
                animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
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

        public void addView(View view, LinearLayout.LayoutParams layoutParams) {
            this.linearLayout.addView(view, layoutParams);
        }

        public int getViewsCount() {
            return this.linearLayout.getChildCount();
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
        public void dispatchDraw(Canvas canvas) {
            if (this.swipeBackGravityRight) {
                setTranslationX(((float) getMeasuredWidth()) * (1.0f - this.backScaleX));
                View view = this.topView;
                if (view != null) {
                    view.setTranslationX(((float) getMeasuredWidth()) * (1.0f - this.backScaleX));
                    this.topView.setAlpha(1.0f - this.swipeBackLayout.transitionProgress);
                    float f = (-((float) (this.topView.getMeasuredHeight() - AndroidUtilities.dp(16.0f)))) * this.swipeBackLayout.transitionProgress;
                    this.topView.setTranslationY(f);
                    setTranslationY(f);
                }
            }
            super.dispatchDraw(canvas);
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x009d  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x00a0  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00ab  */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00c7  */
        /* JADX WARNING: Removed duplicated region for block: B:58:0x0158  */
        /* JADX WARNING: Removed duplicated region for block: B:71:0x01c4  */
        /* JADX WARNING: Removed duplicated region for block: B:80:0x01c7 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r19) {
            /*
                r18 = this;
                r0 = r18
                r8 = r19
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                if (r1 == 0) goto L_0x01cc
                int r1 = r0.gapStartY
                android.widget.ScrollView r2 = r0.scrollView
                int r2 = r2.getScrollY()
                int r9 = r1 - r2
                int r1 = r0.gapEndY
                android.widget.ScrollView r2 = r0.scrollView
                int r2 = r2.getScrollY()
                int r10 = r1 - r2
                r11 = 0
                r1 = 0
            L_0x001e:
                android.widget.LinearLayout r2 = r0.linearLayout
                int r2 = r2.getChildCount()
                r12 = 1
                if (r1 >= r2) goto L_0x0036
                android.widget.LinearLayout r2 = r0.linearLayout
                android.view.View r2 = r2.getChildAt(r1)
                boolean r2 = r2 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.GapView
                if (r2 == 0) goto L_0x0033
                r13 = 1
                goto L_0x0037
            L_0x0033:
                int r1 = r1 + 1
                goto L_0x001e
            L_0x0036:
                r13 = 0
            L_0x0037:
                r14 = 0
            L_0x0038:
                r1 = 2
                if (r14 >= r1) goto L_0x01cc
                r15 = 1098907648(0x41800000, float:16.0)
                if (r14 != r12) goto L_0x0048
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r1 = -r1
                if (r9 >= r1) goto L_0x0048
                goto L_0x01cc
            L_0x0048:
                r7 = 255(0xff, float:3.57E-43)
                r6 = -1000000(0xfffffffffff0bdc0, float:NaN)
                if (r13 == 0) goto L_0x0079
                int r1 = r0.backAlpha
                if (r1 == r7) goto L_0x0079
                r2 = 0
                android.graphics.Rect r1 = r0.bgPaddings
                int r1 = r1.top
                float r3 = (float) r1
                int r1 = r18.getMeasuredWidth()
                float r4 = (float) r1
                int r1 = r18.getMeasuredHeight()
                float r5 = (float) r1
                int r1 = r0.backAlpha
                r16 = 31
                r17 = r1
                r1 = r19
                r12 = -1000000(0xfffffffffff0bdc0, float:NaN)
                r6 = r17
                r17 = 255(0xff, float:3.57E-43)
                r7 = r16
                r1.saveLayerAlpha(r2, r3, r4, r5, r6, r7)
                r1 = 0
                goto L_0x0095
            L_0x0079:
                r12 = -1000000(0xfffffffffff0bdc0, float:NaN)
                r17 = 255(0xff, float:3.57E-43)
                int r1 = r0.gapStartY
                if (r1 == r12) goto L_0x0097
                r19.save()
                android.graphics.Rect r1 = r0.bgPaddings
                int r1 = r1.top
                int r2 = r18.getMeasuredWidth()
                int r3 = r18.getMeasuredHeight()
                r8.clipRect(r11, r1, r2, r3)
                r1 = 1
            L_0x0095:
                r2 = 1
                goto L_0x0099
            L_0x0097:
                r1 = 1
                r2 = 0
            L_0x0099:
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                if (r1 == 0) goto L_0x00a0
                int r7 = r0.backAlpha
                goto L_0x00a2
            L_0x00a0:
                r7 = 255(0xff, float:3.57E-43)
            L_0x00a2:
                r3.setAlpha(r7)
                boolean r1 = r0.shownFromBottom
                r3 = 1065353216(0x3var_, float:1.0)
                if (r1 == 0) goto L_0x00c7
                int r1 = r18.getMeasuredHeight()
                android.graphics.drawable.Drawable r4 = r0.backgroundDrawable
                float r5 = (float) r1
                float r6 = r0.backScaleY
                float r3 = r3 - r6
                float r5 = r5 * r3
                int r3 = (int) r5
                int r5 = r18.getMeasuredWidth()
                float r5 = (float) r5
                float r6 = r0.backScaleX
                float r5 = r5 * r6
                int r5 = (int) r5
                r4.setBounds(r11, r3, r5, r1)
                goto L_0x0151
            L_0x00c7:
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r1 = -r1
                if (r9 <= r1) goto L_0x012a
                int r1 = r18.getMeasuredHeight()
                float r1 = (float) r1
                float r4 = r0.backScaleY
                float r1 = r1 * r4
                int r1 = (int) r1
                if (r14 != 0) goto L_0x010c
                android.graphics.drawable.Drawable r4 = r0.backgroundDrawable
                android.widget.ScrollView r5 = r0.scrollView
                int r5 = r5.getScrollY()
                int r5 = -r5
                int r6 = r0.gapStartY
                if (r6 == r12) goto L_0x00ec
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                goto L_0x00ed
            L_0x00ec:
                r3 = 0
            L_0x00ed:
                int r5 = r5 + r3
                int r3 = r18.getMeasuredWidth()
                float r3 = (float) r3
                float r6 = r0.backScaleX
                float r3 = r3 * r6
                int r3 = (int) r3
                int r6 = r0.gapStartY
                if (r6 == r12) goto L_0x0105
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r6 = r6 + r9
                int r1 = java.lang.Math.min(r1, r6)
            L_0x0105:
                int r6 = r0.subtractBackgroundHeight
                int r1 = r1 - r6
                r4.setBounds(r11, r5, r3, r1)
                goto L_0x0151
            L_0x010c:
                if (r1 >= r10) goto L_0x0117
                int r1 = r0.gapStartY
                if (r1 == r12) goto L_0x01c7
                r19.restore()
                goto L_0x01c7
            L_0x0117:
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                int r4 = r18.getMeasuredWidth()
                float r4 = (float) r4
                float r5 = r0.backScaleX
                float r4 = r4 * r5
                int r4 = (int) r4
                int r5 = r0.subtractBackgroundHeight
                int r1 = r1 - r5
                r3.setBounds(r11, r10, r4, r1)
                goto L_0x0151
            L_0x012a:
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                int r3 = r0.gapStartY
                if (r3 >= 0) goto L_0x0132
                r3 = 0
                goto L_0x0137
            L_0x0132:
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r15)
                int r3 = -r3
            L_0x0137:
                int r4 = r18.getMeasuredWidth()
                float r4 = (float) r4
                float r5 = r0.backScaleX
                float r4 = r4 * r5
                int r4 = (int) r4
                int r5 = r18.getMeasuredHeight()
                float r5 = (float) r5
                float r6 = r0.backScaleY
                float r5 = r5 * r6
                int r5 = (int) r5
                int r6 = r0.subtractBackgroundHeight
                int r5 = r5 - r6
                r1.setBounds(r11, r3, r4, r5)
            L_0x0151:
                android.graphics.drawable.Drawable r1 = r0.backgroundDrawable
                r1.draw(r8)
                if (r13 == 0) goto L_0x01c2
                r19.save()
                android.graphics.Rect r1 = org.telegram.messenger.AndroidUtilities.rectTmp2
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                android.graphics.Rect r3 = r3.getBounds()
                r1.set(r3)
                r3 = 1090519040(0x41000000, float:8.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r1.inset(r4, r3)
                r8.clipRect(r1)
                r1 = 0
            L_0x0177:
                android.widget.LinearLayout r3 = r0.linearLayout
                int r3 = r3.getChildCount()
                if (r1 >= r3) goto L_0x01bf
                android.widget.LinearLayout r3 = r0.linearLayout
                android.view.View r3 = r3.getChildAt(r1)
                boolean r3 = r3 instanceof org.telegram.ui.ActionBar.ActionBarPopupWindow.GapView
                if (r3 == 0) goto L_0x01bc
                r19.save()
                android.widget.LinearLayout r3 = r0.linearLayout
                android.view.View r3 = r3.getChildAt(r1)
                org.telegram.ui.ActionBar.ActionBarPopupWindow$GapView r3 = (org.telegram.ui.ActionBar.ActionBarPopupWindow.GapView) r3
                r4 = 0
                r6 = r3
                r5 = 0
            L_0x0197:
                if (r6 == r0) goto L_0x01ab
                float r7 = r6.getX()
                float r4 = r4 + r7
                float r7 = r6.getY()
                float r5 = r5 + r7
                android.view.ViewParent r6 = r6.getParent()
                android.view.View r6 = (android.view.View) r6
                if (r6 != 0) goto L_0x0197
            L_0x01ab:
                android.widget.ScrollView r6 = r0.scrollView
                float r6 = r6.getScaleY()
                float r5 = r5 * r6
                r8.translate(r4, r5)
                r3.draw(r8)
                r19.restore()
            L_0x01bc:
                int r1 = r1 + 1
                goto L_0x0177
            L_0x01bf:
                r19.restore()
            L_0x01c2:
                if (r2 == 0) goto L_0x01c7
                r19.restore()
            L_0x01c7:
                int r14 = r14 + 1
                r12 = 1
                goto L_0x0038
            L_0x01cc:
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

        public View getItemAt(int i) {
            return this.linearLayout.getChildAt(i);
        }

        public void scrollToTop() {
            ScrollView scrollView2 = this.scrollView;
            if (scrollView2 != null) {
                scrollView2.scrollTo(0, 0);
            }
        }

        public void setupRadialSelectors(int i) {
            int childCount = this.linearLayout.getChildCount();
            int i2 = 0;
            while (i2 < childCount) {
                View childAt = this.linearLayout.getChildAt(i2);
                int i3 = 6;
                int i4 = i2 == 0 ? 6 : 0;
                if (i2 != childCount - 1) {
                    i3 = 0;
                }
                childAt.setBackground(Theme.createRadSelectorDrawable(i, i4, i3));
                i2++;
            }
        }

        public void updateRadialSelectors() {
            int childCount = this.linearLayout.getChildCount();
            View view = null;
            View view2 = null;
            for (int i = 0; i < childCount; i++) {
                View childAt = this.linearLayout.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    if (view == null) {
                        view = childAt;
                    }
                    view2 = childAt;
                }
            }
            boolean z = false;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt2 = this.linearLayout.getChildAt(i2);
                if (childAt2.getVisibility() == 0) {
                    Object tag = childAt2.getTag(NUM);
                    if (childAt2 instanceof ActionBarMenuSubItem) {
                        ((ActionBarMenuSubItem) childAt2).updateSelectorBackground(childAt2 == view || z, childAt2 == view2);
                    }
                    z = tag != null;
                }
            }
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }

        public void setOnSizeChangedListener(onSizeChangedListener onsizechangedlistener) {
            this.onSizeChangedListener = onsizechangedlistener;
        }

        public int getVisibleHeight() {
            return (int) (((float) getMeasuredHeight()) * this.backScaleY);
        }

        public void setTopView(View view) {
            this.topView = view;
        }

        public void setSwipeBackForegroundColor(int i) {
            getSwipeBack().setForegroundColor(i);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            PopupSwipeBackLayout popupSwipeBackLayout = this.swipeBackLayout;
            if (popupSwipeBackLayout != null) {
                popupSwipeBackLayout.invalidateTransforms(!this.startAnimationPending);
            }
        }
    }

    public ActionBarPopupWindow() {
        init();
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        init();
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
            layoutInScreenMethod.invoke(this, new Object[]{Boolean.TRUE});
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

    private void dismissDim() {
        View rootView = getContentView().getRootView();
        WindowManager windowManager = (WindowManager) getContentView().getContext().getSystemService("window");
        if (rootView.getLayoutParams() != null && (rootView.getLayoutParams() instanceof WindowManager.LayoutParams)) {
            WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
            try {
                int i = layoutParams.flags;
                if ((i & 2) != 0) {
                    layoutParams.flags = i & -3;
                    layoutParams.dimAmount = 0.0f;
                    windowManager.updateViewLayout(rootView, layoutParams);
                }
            } catch (Exception unused) {
            }
        }
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
        ActionBarPopupWindowLayout actionBarPopupWindowLayout;
        if (this.animationEnabled && this.windowAnimatorSet == null) {
            ViewGroup viewGroup = (ViewGroup) getContentView();
            ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = null;
            if (viewGroup instanceof ActionBarPopupWindowLayout) {
                actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) viewGroup;
                boolean unused = actionBarPopupWindowLayout.startAnimationPending = true;
            } else {
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                        actionBarPopupWindowLayout2 = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                        boolean unused2 = actionBarPopupWindowLayout2.startAnimationPending = true;
                    }
                }
                actionBarPopupWindowLayout = actionBarPopupWindowLayout2;
            }
            actionBarPopupWindowLayout.setTranslationY(0.0f);
            float f = 1.0f;
            actionBarPopupWindowLayout.setAlpha(1.0f);
            actionBarPopupWindowLayout.setPivotX((float) actionBarPopupWindowLayout.getMeasuredWidth());
            actionBarPopupWindowLayout.setPivotY(0.0f);
            int itemsCount = actionBarPopupWindowLayout.getItemsCount();
            actionBarPopupWindowLayout.positions.clear();
            int i2 = 0;
            for (int i3 = 0; i3 < itemsCount; i3++) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i3);
                itemAt.setAlpha(0.0f);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i2));
                    i2++;
                }
            }
            if (actionBarPopupWindowLayout.shownFromBottom) {
                int unused3 = actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
            } else {
                int unused4 = actionBarPopupWindowLayout.lastStartedChild = 0;
            }
            if (actionBarPopupWindowLayout.getSwipeBack() != null) {
                actionBarPopupWindowLayout.getSwipeBack().invalidateTransforms();
                f = actionBarPopupWindowLayout.backScaleY;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.windowAnimatorSet = animatorSet;
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", new float[]{0.0f, f}), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", new int[]{0, 255})});
            this.windowAnimatorSet.setDuration((long) ((i2 * 16) + 150));
            this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ActionBarPopupWindowLayout actionBarPopupWindowLayout;
                    ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = null;
                    AnimatorSet unused = ActionBarPopupWindow.this.windowAnimatorSet = null;
                    ViewGroup viewGroup = (ViewGroup) ActionBarPopupWindow.this.getContentView();
                    if (viewGroup instanceof ActionBarPopupWindowLayout) {
                        actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) viewGroup;
                        boolean unused2 = actionBarPopupWindowLayout.startAnimationPending = false;
                    } else {
                        for (int i = 0; i < viewGroup.getChildCount(); i++) {
                            if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                                actionBarPopupWindowLayout2 = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                                boolean unused3 = actionBarPopupWindowLayout2.startAnimationPending = false;
                            }
                        }
                        actionBarPopupWindowLayout = actionBarPopupWindowLayout2;
                    }
                    int itemsCount = actionBarPopupWindowLayout.getItemsCount();
                    for (int i2 = 0; i2 < itemsCount; i2++) {
                        View itemAt = actionBarPopupWindowLayout.getItemAt(i2);
                        if (!(itemAt instanceof GapView)) {
                            itemAt.setAlpha(itemAt.isEnabled() ? 1.0f : 0.5f);
                        }
                    }
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

    public void setPauseNotifications(boolean z) {
        this.pauseNotifications = z;
    }

    public void dismiss(boolean z) {
        setFocusable(false);
        dismissDim();
        AnimatorSet animatorSet = this.windowAnimatorSet;
        if (animatorSet != null) {
            if (!z || !this.isClosingAnimated) {
                animatorSet.cancel();
                this.windowAnimatorSet = null;
            } else {
                return;
            }
        }
        this.isClosingAnimated = false;
        if (!this.animationEnabled || !z) {
            try {
                super.dismiss();
            } catch (Exception unused) {
            }
            unregisterListener();
            return;
        }
        this.isClosingAnimated = true;
        ViewGroup viewGroup = (ViewGroup) getContentView();
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
            }
        }
        if (!(actionBarPopupWindowLayout == null || actionBarPopupWindowLayout.itemAnimators == null || actionBarPopupWindowLayout.itemAnimators.isEmpty())) {
            int size = actionBarPopupWindowLayout.itemAnimators.size();
            for (int i2 = 0; i2 < size; i2++) {
                AnimatorSet animatorSet2 = (AnimatorSet) actionBarPopupWindowLayout.itemAnimators.get(i2);
                animatorSet2.removeAllListeners();
                animatorSet2.cancel();
            }
            actionBarPopupWindowLayout.itemAnimators.clear();
        }
        AnimatorSet animatorSet3 = new AnimatorSet();
        this.windowAnimatorSet = animatorSet3;
        if (this.outEmptyTime > 0) {
            animatorSet3.playTogether(new Animator[]{ValueAnimator.ofFloat(new float[]{0.0f, 1.0f})});
            this.windowAnimatorSet.setDuration(this.outEmptyTime);
        } else if (this.scaleOut) {
            animatorSet3.playTogether(new Animator[]{ObjectAnimator.ofFloat(viewGroup, View.SCALE_Y, new float[]{0.8f}), ObjectAnimator.ofFloat(viewGroup, View.SCALE_X, new float[]{0.8f}), ObjectAnimator.ofFloat(viewGroup, View.ALPHA, new float[]{0.0f})});
            this.windowAnimatorSet.setDuration((long) this.dismissAnimationDuration);
        } else {
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) AndroidUtilities.dp((actionBarPopupWindowLayout == null || !actionBarPopupWindowLayout.shownFromBottom) ? -5.0f : 5.0f);
            animatorArr[0] = ObjectAnimator.ofFloat(viewGroup, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(viewGroup, View.ALPHA, new float[]{0.0f});
            animatorSet3.playTogether(animatorArr);
            this.windowAnimatorSet.setDuration((long) this.dismissAnimationDuration);
        }
        this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                AnimatorSet unused = ActionBarPopupWindow.this.windowAnimatorSet = null;
                boolean unused2 = ActionBarPopupWindow.this.isClosingAnimated = false;
                ActionBarPopupWindow.this.setFocusable(false);
                try {
                    ActionBarPopupWindow.super.dismiss();
                } catch (Exception unused3) {
                }
                ActionBarPopupWindow.this.unregisterListener();
                if (ActionBarPopupWindow.this.pauseNotifications) {
                    NotificationCenter.getInstance(ActionBarPopupWindow.this.currentAccount).onAnimationFinish(ActionBarPopupWindow.this.popupAnimationIndex);
                }
            }
        });
        if (this.pauseNotifications) {
            this.popupAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.popupAnimationIndex, (int[]) null);
        }
        this.windowAnimatorSet.start();
    }

    public static class GapView extends FrameLayout {
        Theme.ResourcesProvider resourcesProvider;

        public GapView(Context context, Theme.ResourcesProvider resourcesProvider2, String str) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            setBackgroundColor(getThemedColor(str));
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }

        public void setColor(int i) {
            setBackgroundColor(i);
        }
    }
}
