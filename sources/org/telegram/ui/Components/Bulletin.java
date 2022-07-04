package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.DialogsActivity;

public class Bulletin {
    public static final int DURATION_LONG = 2750;
    public static final int DURATION_SHORT = 1500;
    public static final int TYPE_APP_ICON = 5;
    public static final int TYPE_BIO_CHANGED = 2;
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_ERROR_SUBTITLE = 4;
    public static final int TYPE_NAME_CHANGED = 3;
    public static final int TYPE_STICKER = 0;
    /* access modifiers changed from: private */
    public static final HashMap<FrameLayout, Delegate> delegates = new HashMap<>();
    private static Bulletin visibleBulletin;
    private boolean canHide;
    /* access modifiers changed from: private */
    public final FrameLayout containerLayout;
    public int currentBottomOffset;
    /* access modifiers changed from: private */
    public Delegate currentDelegate;
    private int duration;
    public int hash;
    private final Runnable hideRunnable;
    /* access modifiers changed from: private */
    public final Layout layout;
    /* access modifiers changed from: private */
    public Layout.Transition layoutTransition;
    private final ParentLayout parentLayout;
    /* access modifiers changed from: private */
    public boolean showing;
    public int tag;

    @Retention(RetentionPolicy.SOURCE)
    private @interface GravityDef {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface WidthDef {
    }

    public static Bulletin make(FrameLayout containerLayout2, Layout contentLayout, int duration2) {
        return new Bulletin(containerLayout2, contentLayout, duration2);
    }

    public static Bulletin make(BaseFragment fragment, Layout contentLayout, int duration2) {
        if (fragment instanceof ChatActivity) {
            contentLayout.setWideScreenParams(-2, 5);
        } else if (fragment instanceof DialogsActivity) {
            contentLayout.setWideScreenParams(-1, 0);
        }
        return new Bulletin(fragment.getLayoutContainer(), contentLayout, duration2);
    }

    public static Bulletin find(FrameLayout containerLayout2) {
        int size = containerLayout2.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = containerLayout2.getChildAt(i);
            if (view instanceof Layout) {
                return ((Layout) view).bulletin;
            }
        }
        return null;
    }

    public static void hide(FrameLayout containerLayout2) {
        hide(containerLayout2, true);
    }

    public static void hide(FrameLayout containerLayout2, boolean animated) {
        Bulletin bulletin = find(containerLayout2);
        if (bulletin != null) {
            bulletin.hide(animated && isTransitionsEnabled(), 0);
        }
    }

    private Bulletin() {
        this.hideRunnable = new Bulletin$$ExternalSyntheticLambda2(this);
        this.layout = null;
        this.parentLayout = null;
        this.containerLayout = null;
    }

    private Bulletin(final FrameLayout containerLayout2, Layout layout2, int duration2) {
        this.hideRunnable = new Bulletin$$ExternalSyntheticLambda2(this);
        this.layout = layout2;
        this.parentLayout = new ParentLayout(layout2) {
            /* access modifiers changed from: protected */
            public void onPressedStateChanged(boolean pressed) {
                Bulletin.this.setCanHide(!pressed);
                if (containerLayout2.getParent() != null) {
                    containerLayout2.getParent().requestDisallowInterceptTouchEvent(pressed);
                }
            }

            /* access modifiers changed from: protected */
            public void onHide() {
                Bulletin.this.hide();
            }
        };
        this.containerLayout = containerLayout2;
        this.duration = duration2;
    }

    public static Bulletin getVisibleBulletin() {
        return visibleBulletin;
    }

    public void setDuration(int duration2) {
        this.duration = duration2;
    }

    public Bulletin show() {
        if (!this.showing && this.containerLayout != null) {
            this.showing = true;
            CharSequence text = this.layout.getAccessibilityText();
            if (text != null) {
                AndroidUtilities.makeAccessibilityAnnouncement(text);
            }
            if (this.layout.getParent() == this.parentLayout) {
                Bulletin bulletin = visibleBulletin;
                if (bulletin != null) {
                    bulletin.hide();
                }
                visibleBulletin = this;
                this.layout.onAttach(this);
                this.layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        Bulletin.this.layout.removeOnLayoutChangeListener(this);
                        if (Bulletin.this.showing) {
                            Bulletin.this.layout.onShow();
                            Delegate unused = Bulletin.this.currentDelegate = (Delegate) Bulletin.delegates.get(Bulletin.this.containerLayout);
                            Bulletin bulletin = Bulletin.this;
                            bulletin.currentBottomOffset = bulletin.currentDelegate != null ? Bulletin.this.currentDelegate.getBottomOffset(Bulletin.this.tag) : 0;
                            if (Bulletin.this.currentDelegate != null) {
                                Bulletin.this.currentDelegate.onShow(Bulletin.this);
                            }
                            if (Bulletin.isTransitionsEnabled()) {
                                Bulletin.this.ensureLayoutTransitionCreated();
                                Bulletin.this.layout.transitionRunning = true;
                                Bulletin.this.layout.delegate = Bulletin.this.currentDelegate;
                                Bulletin.this.layout.invalidate();
                                Layout.Transition access$900 = Bulletin.this.layoutTransition;
                                Layout access$200 = Bulletin.this.layout;
                                Layout access$2002 = Bulletin.this.layout;
                                access$2002.getClass();
                                access$900.animateEnter(access$200, new Bulletin$2$$ExternalSyntheticLambda2(access$2002), new Bulletin$2$$ExternalSyntheticLambda1(this), new Bulletin$2$$ExternalSyntheticLambda0(this), Bulletin.this.currentBottomOffset);
                                return;
                            }
                            if (Bulletin.this.currentDelegate != null) {
                                Bulletin.this.currentDelegate.onOffsetChange((float) (Bulletin.this.layout.getHeight() - Bulletin.this.currentBottomOffset));
                            }
                            Bulletin.this.updatePosition();
                            Bulletin.this.layout.onEnterTransitionStart();
                            Bulletin.this.layout.onEnterTransitionEnd();
                            Bulletin.this.setCanHide(true);
                        }
                    }

                    /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-Bulletin$2  reason: not valid java name */
                    public /* synthetic */ void m639lambda$onLayoutChange$0$orgtelegramuiComponentsBulletin$2() {
                        Bulletin.this.layout.transitionRunning = false;
                        Bulletin.this.layout.onEnterTransitionEnd();
                        Bulletin.this.setCanHide(true);
                    }

                    /* renamed from: lambda$onLayoutChange$1$org-telegram-ui-Components-Bulletin$2  reason: not valid java name */
                    public /* synthetic */ void m640lambda$onLayoutChange$1$orgtelegramuiComponentsBulletin$2(Float offset) {
                        if (Bulletin.this.currentDelegate != null) {
                            Bulletin.this.currentDelegate.onOffsetChange(((float) Bulletin.this.layout.getHeight()) - offset.floatValue());
                        }
                    }
                });
                this.layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    public void onViewAttachedToWindow(View v) {
                    }

                    public void onViewDetachedFromWindow(View v) {
                        Bulletin.this.layout.removeOnAttachStateChangeListener(this);
                        Bulletin.this.hide(false, 0);
                    }
                });
                this.containerLayout.addView(this.parentLayout);
            } else {
                throw new IllegalStateException("Layout has incorrect parent");
            }
        }
        return this;
    }

    /* access modifiers changed from: private */
    public void setCanHide(boolean canHide2) {
        Layout layout2;
        if (this.canHide != canHide2 && (layout2 = this.layout) != null) {
            this.canHide = canHide2;
            if (canHide2) {
                layout2.postDelayed(this.hideRunnable, (long) this.duration);
            } else {
                layout2.removeCallbacks(this.hideRunnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void ensureLayoutTransitionCreated() {
        Layout layout2 = this.layout;
        if (layout2 != null && this.layoutTransition == null) {
            this.layoutTransition = layout2.createTransition();
        }
    }

    public void hide() {
        hide(isTransitionsEnabled(), 0);
    }

    public void hide(long duration2) {
        hide(isTransitionsEnabled(), duration2);
    }

    public void hide(boolean animated, long duration2) {
        Layout layout2 = this.layout;
        if (layout2 != null && this.showing) {
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            int bottomOffset = this.currentBottomOffset;
            this.currentBottomOffset = 0;
            if (ViewCompat.isLaidOut(layout2)) {
                this.layout.removeCallbacks(this.hideRunnable);
                if (animated) {
                    this.layout.transitionRunning = true;
                    this.layout.delegate = this.currentDelegate;
                    this.layout.invalidate();
                    if (duration2 >= 0) {
                        Layout.DefaultTransition transition = new Layout.DefaultTransition();
                        transition.duration = duration2;
                        this.layoutTransition = transition;
                    } else {
                        ensureLayoutTransitionCreated();
                    }
                    Layout.Transition transition2 = this.layoutTransition;
                    Layout layout3 = this.layout;
                    layout3.getClass();
                    transition2.animateExit(layout3, new Bulletin$$ExternalSyntheticLambda1(layout3), new Bulletin$$ExternalSyntheticLambda3(this), new Bulletin$$ExternalSyntheticLambda0(this), bottomOffset);
                    return;
                }
            }
            Delegate delegate = this.currentDelegate;
            if (delegate != null) {
                delegate.onOffsetChange(0.0f);
                this.currentDelegate.onHide(this);
            }
            this.layout.onExitTransitionStart();
            this.layout.onExitTransitionEnd();
            this.layout.onHide();
            if (this.containerLayout != null) {
                AndroidUtilities.runOnUIThread(new Bulletin$$ExternalSyntheticLambda4(this));
            }
            this.layout.onDetach();
        }
    }

    /* renamed from: lambda$hide$0$org-telegram-ui-Components-Bulletin  reason: not valid java name */
    public /* synthetic */ void m636lambda$hide$0$orgtelegramuiComponentsBulletin() {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(0.0f);
            this.currentDelegate.onHide(this);
        }
        this.layout.transitionRunning = false;
        this.layout.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.parentLayout);
        this.layout.onDetach();
    }

    /* renamed from: lambda$hide$1$org-telegram-ui-Components-Bulletin  reason: not valid java name */
    public /* synthetic */ void m637lambda$hide$1$orgtelegramuiComponentsBulletin(Float offset) {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(((float) this.layout.getHeight()) - offset.floatValue());
        }
    }

    /* renamed from: lambda$hide$2$org-telegram-ui-Components-Bulletin  reason: not valid java name */
    public /* synthetic */ void m638lambda$hide$2$orgtelegramuiComponentsBulletin() {
        this.containerLayout.removeView(this.parentLayout);
    }

    public boolean isShowing() {
        return this.showing;
    }

    public Layout getLayout() {
        return this.layout;
    }

    /* access modifiers changed from: private */
    public static boolean isTransitionsEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && Build.VERSION.SDK_INT >= 18;
    }

    public void updatePosition() {
        Layout layout2 = this.layout;
        if (layout2 != null) {
            layout2.updatePosition();
        }
    }

    private static abstract class ParentLayout extends FrameLayout {
        private final GestureDetector gestureDetector;
        /* access modifiers changed from: private */
        public boolean hideAnimationRunning;
        private final Layout layout;
        /* access modifiers changed from: private */
        public boolean needLeftAlphaAnimation;
        /* access modifiers changed from: private */
        public boolean needRightAlphaAnimation;
        private boolean pressed;
        private final Rect rect = new Rect();
        /* access modifiers changed from: private */
        public float translationX;

        /* access modifiers changed from: protected */
        public abstract void onHide();

        /* access modifiers changed from: protected */
        public abstract void onPressedStateChanged(boolean z);

        static /* synthetic */ float access$1424(ParentLayout x0, float x1) {
            float f = x0.translationX - x1;
            x0.translationX = f;
            return f;
        }

        public ParentLayout(final Layout layout2) {
            super(layout2.getContext());
            this.layout = layout2;
            GestureDetector gestureDetector2 = new GestureDetector(layout2.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onDown(MotionEvent e) {
                    if (ParentLayout.this.hideAnimationRunning) {
                        return false;
                    }
                    boolean unused = ParentLayout.this.needLeftAlphaAnimation = layout2.isNeedSwipeAlphaAnimation(true);
                    boolean unused2 = ParentLayout.this.needRightAlphaAnimation = layout2.isNeedSwipeAlphaAnimation(false);
                    return true;
                }

                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    layout2.setTranslationX(ParentLayout.access$1424(ParentLayout.this, distanceX));
                    if (ParentLayout.this.translationX != 0.0f && ((ParentLayout.this.translationX >= 0.0f || !ParentLayout.this.needLeftAlphaAnimation) && (ParentLayout.this.translationX <= 0.0f || !ParentLayout.this.needRightAlphaAnimation))) {
                        return true;
                    }
                    layout2.setAlpha(1.0f - (Math.abs(ParentLayout.this.translationX) / ((float) layout2.getWidth())));
                    return true;
                }

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    boolean needAlphaAnimation = false;
                    if (Math.abs(velocityX) <= 2000.0f) {
                        return false;
                    }
                    if ((velocityX < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (velocityX > 0.0f && ParentLayout.this.needRightAlphaAnimation)) {
                        needAlphaAnimation = true;
                    }
                    SpringAnimation springAnimation = new SpringAnimation(layout2, DynamicAnimation.TRANSLATION_X, Math.signum(velocityX) * ((float) layout2.getWidth()) * 2.0f);
                    if (!needAlphaAnimation) {
                        springAnimation.addEndListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda0(this));
                        springAnimation.addUpdateListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda2(layout2));
                    }
                    springAnimation.getSpring().setDampingRatio(1.0f);
                    springAnimation.getSpring().setStiffness(100.0f);
                    springAnimation.setStartVelocity(velocityX);
                    springAnimation.start();
                    if (needAlphaAnimation) {
                        SpringAnimation springAnimation2 = new SpringAnimation(layout2, DynamicAnimation.ALPHA, 0.0f);
                        springAnimation2.addEndListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda1(this));
                        springAnimation2.addUpdateListener(Bulletin$ParentLayout$1$$ExternalSyntheticLambda3.INSTANCE);
                        springAnimation.getSpring().setDampingRatio(1.0f);
                        springAnimation.getSpring().setStiffness(10.0f);
                        springAnimation.setStartVelocity(velocityX);
                        springAnimation2.start();
                    }
                    boolean unused = ParentLayout.this.hideAnimationRunning = true;
                    return true;
                }

                /* renamed from: lambda$onFling$0$org-telegram-ui-Components-Bulletin$ParentLayout$1  reason: not valid java name */
                public /* synthetic */ void m642x7bd22355(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    ParentLayout.this.onHide();
                }

                static /* synthetic */ void lambda$onFling$1(Layout layout, DynamicAnimation animation, float value, float velocity) {
                    if (Math.abs(value) > ((float) layout.getWidth())) {
                        animation.cancel();
                    }
                }

                /* renamed from: lambda$onFling$2$org-telegram-ui-Components-Bulletin$ParentLayout$1  reason: not valid java name */
                public /* synthetic */ void m643xd7835813(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                    ParentLayout.this.onHide();
                }

                static /* synthetic */ void lambda$onFling$3(DynamicAnimation animation, float value, float velocity) {
                    if (value <= 0.0f) {
                        animation.cancel();
                    }
                }
            });
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            addView(layout2);
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (!this.pressed && !inLayoutHitRect(event.getX(), event.getY())) {
                return false;
            }
            this.gestureDetector.onTouchEvent(event);
            int actionMasked = event.getActionMasked();
            if (actionMasked == 0) {
                if (!this.pressed && !this.hideAnimationRunning) {
                    this.layout.animate().cancel();
                    this.translationX = this.layout.getTranslationX();
                    this.pressed = true;
                    onPressedStateChanged(true);
                }
            } else if ((actionMasked == 1 || actionMasked == 3) && this.pressed) {
                if (!this.hideAnimationRunning) {
                    float f = 1.0f;
                    if (Math.abs(this.translationX) > ((float) this.layout.getWidth()) / 3.0f) {
                        float tx = Math.signum(this.translationX) * ((float) this.layout.getWidth());
                        float f2 = this.translationX;
                        boolean needAlphaAnimation = (f2 < 0.0f && this.needLeftAlphaAnimation) || (f2 > 0.0f && this.needRightAlphaAnimation);
                        ViewPropertyAnimator translationX2 = this.layout.animate().translationX(tx);
                        if (needAlphaAnimation) {
                            f = 0.0f;
                        }
                        translationX2.alpha(f).setDuration(200).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Bulletin$ParentLayout$$ExternalSyntheticLambda0(this, tx)).start();
                    } else {
                        this.layout.animate().translationX(0.0f).alpha(1.0f).setDuration(200).start();
                    }
                }
                this.pressed = false;
                onPressedStateChanged(false);
            }
            return true;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-Components-Bulletin$ParentLayout  reason: not valid java name */
        public /* synthetic */ void m641x9108f6b1(float tx) {
            if (this.layout.getTranslationX() == tx) {
                onHide();
            }
        }

        private boolean inLayoutHitRect(float x, float y) {
            this.layout.getHitRect(this.rect);
            return this.rect.contains((int) x, (int) y);
        }
    }

    public static void addDelegate(BaseFragment fragment, Delegate delegate) {
        FrameLayout containerLayout2 = fragment.getLayoutContainer();
        if (containerLayout2 != null) {
            addDelegate(containerLayout2, delegate);
        }
    }

    public static void addDelegate(FrameLayout containerLayout2, Delegate delegate) {
        delegates.put(containerLayout2, delegate);
    }

    public static void removeDelegate(BaseFragment fragment) {
        FrameLayout containerLayout2 = fragment.getLayoutContainer();
        if (containerLayout2 != null) {
            removeDelegate(containerLayout2);
        }
    }

    public static void removeDelegate(FrameLayout containerLayout2) {
        delegates.remove(containerLayout2);
    }

    public interface Delegate {
        int getBottomOffset(int i);

        void onHide(Bulletin bulletin);

        void onOffsetChange(float f);

        void onShow(Bulletin bulletin);

        /* renamed from: org.telegram.ui.Components.Bulletin$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static int $default$getBottomOffset(Delegate _this, int tag) {
                return 0;
            }

            public static void $default$onOffsetChange(Delegate _this, float offset) {
            }

            public static void $default$onShow(Delegate _this, Bulletin bulletin) {
            }

            public static void $default$onHide(Delegate _this, Bulletin bulletin) {
            }
        }
    }

    public static abstract class Layout extends FrameLayout {
        public static final FloatPropertyCompat<Layout> IN_OUT_OFFSET_Y = new FloatPropertyCompat<Layout>("offsetY") {
            public float getValue(Layout object) {
                return object.inOutOffset;
            }

            public void setValue(Layout object, float value) {
                object.setInOutOffset(value);
            }
        };
        public static final Property<Layout, Float> IN_OUT_OFFSET_Y2 = new AnimationProperties.FloatProperty<Layout>("offsetY") {
            public Float get(Layout layout) {
                return Float.valueOf(layout.inOutOffset);
            }

            public void setValue(Layout object, float value) {
                object.setInOutOffset(value);
            }
        };
        Drawable background;
        protected Bulletin bulletin;
        private final List<Callback> callbacks = new ArrayList();
        Delegate delegate;
        public float inOutOffset;
        private final Theme.ResourcesProvider resourcesProvider;
        public boolean transitionRunning;
        private int wideScreenGravity = 1;
        private int wideScreenWidth = -2;

        public interface Callback {
            void onAttach(Layout layout, Bulletin bulletin);

            void onDetach(Layout layout);

            void onEnterTransitionEnd(Layout layout);

            void onEnterTransitionStart(Layout layout);

            void onExitTransitionEnd(Layout layout);

            void onExitTransitionStart(Layout layout);

            void onHide(Layout layout);

            void onShow(Layout layout);
        }

        public interface Transition {
            void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);

            void animateExit(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);
        }

        public Layout(Context context, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            setMinimumHeight(AndroidUtilities.dp(48.0f));
            setBackground(getThemedColor("undo_background"));
            updateSize();
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
        }

        /* access modifiers changed from: protected */
        public void setBackground(int color) {
            this.background = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), color);
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateSize();
        }

        private void updateSize() {
            boolean isWideScreen = isWideScreen();
            int i = isWideScreen ? this.wideScreenWidth : -1;
            int i2 = 80;
            if (isWideScreen) {
                i2 = 80 | this.wideScreenGravity;
            }
            setLayoutParams(LayoutHelper.createFrame(i, -2, i2));
        }

        private boolean isWideScreen() {
            return AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y;
        }

        /* access modifiers changed from: private */
        public void setWideScreenParams(int width, int gravity) {
            boolean changed = false;
            if (this.wideScreenWidth != width) {
                this.wideScreenWidth = width;
                changed = true;
            }
            if (this.wideScreenGravity != gravity) {
                this.wideScreenGravity = gravity;
                changed = true;
            }
            if (isWideScreen() && changed) {
                updateSize();
            }
        }

        /* access modifiers changed from: private */
        public boolean isNeedSwipeAlphaAnimation(boolean swipeLeft) {
            if (!isWideScreen() || this.wideScreenWidth == -1) {
                return false;
            }
            int i = this.wideScreenGravity;
            if (i == 1) {
                return true;
            }
            if (swipeLeft) {
                if (i == 5) {
                    return true;
                }
                return false;
            } else if (i != 5) {
                return true;
            } else {
                return false;
            }
        }

        /* access modifiers changed from: protected */
        public CharSequence getAccessibilityText() {
            return null;
        }

        public Bulletin getBulletin() {
            return this.bulletin;
        }

        public boolean isAttachedToBulletin() {
            return this.bulletin != null;
        }

        /* access modifiers changed from: protected */
        public void onAttach(Bulletin bulletin2) {
            this.bulletin = bulletin2;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onAttach(this, bulletin2);
            }
        }

        /* access modifiers changed from: protected */
        public void onDetach() {
            this.bulletin = null;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onDetach(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onShow() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onShow(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onHide() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onHide(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onEnterTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionStart(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onEnterTransitionEnd() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionEnd(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onExitTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onExitTransitionStart(this);
            }
        }

        /* access modifiers changed from: protected */
        public void onExitTransitionEnd() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onExitTransitionEnd(this);
            }
        }

        public void addCallback(Callback callback) {
            this.callbacks.add(callback);
        }

        public void removeCallback(Callback callback) {
            this.callbacks.remove(callback);
        }

        public void updatePosition() {
            float tranlsation = 0.0f;
            Delegate delegate2 = this.delegate;
            if (delegate2 != null) {
                Bulletin bulletin2 = this.bulletin;
                tranlsation = 0.0f + ((float) delegate2.getBottomOffset(bulletin2 != null ? bulletin2.tag : 0));
            }
            setTranslationY((-tranlsation) + this.inOutOffset);
        }

        public Transition createTransition() {
            return new SpringTransition();
        }

        public static class DefaultTransition implements Transition {
            long duration = 255;

            public void animateEnter(Layout layout, final Runnable startAction, final Runnable endAction, Consumer<Float> onUpdate, int bottomOffset) {
                layout.setInOutOffset((float) layout.getMeasuredHeight());
                if (onUpdate != null) {
                    onUpdate.accept(Float.valueOf(layout.getTranslationY()));
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, new float[]{0.0f});
                animator.setDuration(this.duration);
                animator.setInterpolator(Easings.easeOutQuad);
                if (!(startAction == null && endAction == null)) {
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            Runnable runnable = startAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        public void onAnimationEnd(Animator animation) {
                            Runnable runnable = endAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (onUpdate != null) {
                    animator.addUpdateListener(new Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0(onUpdate, layout));
                }
                animator.start();
            }

            public void animateExit(Layout layout, final Runnable startAction, final Runnable endAction, Consumer<Float> onUpdate, int bottomOffset) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, new float[]{(float) layout.getHeight()});
                animator.setDuration(175);
                animator.setInterpolator(Easings.easeInQuad);
                if (!(startAction == null && endAction == null)) {
                    animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationStart(Animator animation) {
                            Runnable runnable = startAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        public void onAnimationEnd(Animator animation) {
                            Runnable runnable = endAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (onUpdate != null) {
                    animator.addUpdateListener(new Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda1(onUpdate, layout));
                }
                animator.start();
            }
        }

        public static class SpringTransition implements Transition {
            private static final float DAMPING_RATIO = 0.8f;
            private static final float STIFFNESS = 400.0f;

            public void animateEnter(Layout layout, Runnable startAction, Runnable endAction, Consumer<Float> onUpdate, int bottomOffset) {
                layout.setInOutOffset((float) layout.getMeasuredHeight());
                if (onUpdate != null) {
                    onUpdate.accept(Float.valueOf(layout.getTranslationY()));
                }
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, 0.0f);
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (endAction != null) {
                    springAnimation.addEndListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1(layout, endAction));
                }
                if (onUpdate != null) {
                    springAnimation.addUpdateListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda2(onUpdate, layout));
                }
                springAnimation.start();
                if (startAction != null) {
                    startAction.run();
                }
            }

            static /* synthetic */ void lambda$animateEnter$0(Layout layout, Runnable endAction, DynamicAnimation animation, boolean canceled, float value, float velocity) {
                layout.setInOutOffset(0.0f);
                if (!canceled) {
                    endAction.run();
                }
            }

            public void animateExit(Layout layout, Runnable startAction, Runnable endAction, Consumer<Float> onUpdate, int bottomOffset) {
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, (float) layout.getHeight());
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (endAction != null) {
                    springAnimation.addEndListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0(endAction));
                }
                if (onUpdate != null) {
                    springAnimation.addUpdateListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3(onUpdate, layout));
                }
                springAnimation.start();
                if (startAction != null) {
                    startAction.run();
                }
            }

            static /* synthetic */ void lambda$animateExit$2(Runnable endAction, DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (!canceled) {
                    endAction.run();
                }
            }
        }

        /* access modifiers changed from: private */
        public void setInOutOffset(float offset) {
            this.inOutOffset = offset;
            updatePosition();
        }

        /* access modifiers changed from: protected */
        public void dispatchDraw(Canvas canvas) {
            this.background.setBounds(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), getMeasuredWidth() - AndroidUtilities.dp(8.0f), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
            if (!this.transitionRunning || this.delegate == null) {
                this.background.draw(canvas);
                super.dispatchDraw(canvas);
                return;
            }
            int clipBottom = ((View) getParent()).getMeasuredHeight() - this.delegate.getBottomOffset(this.bulletin.tag);
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - (((int) (getY() + ((float) getMeasuredHeight()))) - clipBottom));
            this.background.draw(canvas);
            super.dispatchDraw(canvas);
            canvas.restore();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public static class ButtonLayout extends Layout {
        private Button button;
        private int childrenMeasuredWidth;

        public ButtonLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.childrenMeasuredWidth = 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.button != null && View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
                setMeasuredDimension(this.childrenMeasuredWidth + this.button.getMeasuredWidth(), getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            Button button2 = this.button;
            if (!(button2 == null || child == button2)) {
                widthUsed += button2.getMeasuredWidth() - AndroidUtilities.dp(12.0f);
            }
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            if (child != this.button) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                this.childrenMeasuredWidth = Math.max(this.childrenMeasuredWidth, lp.leftMargin + lp.rightMargin + child.getMeasuredWidth());
            }
        }

        public Button getButton() {
            return this.button;
        }

        public void setButton(Button button2) {
            Button button3 = this.button;
            if (button3 != null) {
                removeCallback(button3);
                removeView(this.button);
            }
            this.button = button2;
            if (button2 != null) {
                addCallback(button2);
                addView(button2, 0, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388629));
            }
        }
    }

    public static class SimpleLayout extends ButtonLayout {
        public final ImageView imageView;
        public final TextView textView;

        public SimpleLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int undoInfoColor = getThemedColor("undo_infoColor");
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setColorFilter(new PorterDuffColorFilter(undoInfoColor, PorterDuff.Mode.MULTIPLY));
            addView(imageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setSingleLine();
            textView2.setTextColor(undoInfoColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 15.0f);
            addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    public static class MultiLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView textView;

        public MultiLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            TextView textView2 = new TextView(getContext());
            this.textView = textView2;
            addView(backupImageView, LayoutHelper.createFrameRelatively(30.0f, 30.0f, 8388627, 12.0f, 8.0f, 12.0f, 8.0f));
            textView2.setGravity(8388611);
            textView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            textView2.setTextColor(getThemedColor("undo_infoColor"));
            textView2.setTextSize(1, 15.0f);
            textView2.setTypeface(Typeface.SANS_SERIF);
            addView(textView2, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    public static class TwoLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView subtitleTextView;
        public final TextView titleTextView;

        public TwoLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int undoInfoColor = getThemedColor("undo_infoColor");
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrameRelatively(29.0f, 29.0f, 8388627, 12.0f, 12.0f, 12.0f, 12.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 54.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(undoInfoColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setMaxLines(2);
            textView2.setTextColor(undoInfoColor);
            textView2.setLinkTextColor(getThemedColor("undo_cancelColor"));
            textView2.setMovementMethod(new LinkMovementMethod());
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        public CharSequence getAccessibilityText() {
            return this.titleTextView.getText() + ".\n" + this.subtitleTextView.getText();
        }
    }

    public static class TwoLineLottieLayout extends ButtonLayout {
        public final RLottieImageView imageView;
        public final TextView subtitleTextView;
        private final int textColor = getThemedColor("undo_infoColor");
        public final TextView titleTextView;

        public TwoLineLottieLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            setBackground(getThemedColor("undo_background"));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(rLottieImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
            int undoInfoColor = getThemedColor("undo_infoColor");
            int undoLinkColor = getThemedColor("voipgroup_overlayBlue1");
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(undoInfoColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setTextColor(undoInfoColor);
            textView2.setLinkTextColor(undoLinkColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        /* access modifiers changed from: protected */
        public void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int resId, String... layers) {
            setAnimation(resId, 32, 32, layers);
        }

        public void setAnimation(int resId, int w, int h, String... layers) {
            this.imageView.setAnimation(resId, w, h);
            for (String layer : layers) {
                RLottieImageView rLottieImageView = this.imageView;
                rLottieImageView.setLayerColor(layer + ".**", this.textColor);
            }
        }

        public CharSequence getAccessibilityText() {
            return this.titleTextView.getText() + ".\n" + this.subtitleTextView.getText();
        }
    }

    public static class LottieLayout extends ButtonLayout {
        public RLottieImageView imageView;
        private int textColor;
        public TextView textView;

        public LottieLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setSingleLine();
            this.textView.setTypeface(Typeface.SANS_SERIF);
            this.textView.setTextSize(1, 15.0f);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
            setTextColor(getThemedColor("undo_infoColor"));
            setBackground(getThemedColor("undo_background"));
        }

        public LottieLayout(Context context, Theme.ResourcesProvider resourcesProvider, int backgroundColor, int textColor2) {
            this(context, resourcesProvider);
            setBackground(backgroundColor);
            setTextColor(textColor2);
        }

        public void setTextColor(int textColor2) {
            this.textColor = textColor2;
            this.textView.setTextColor(textColor2);
        }

        /* access modifiers changed from: protected */
        public void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int resId, String... layers) {
            setAnimation(resId, 32, 32, layers);
        }

        public void setAnimation(int resId, int w, int h, String... layers) {
            this.imageView.setAnimation(resId, w, h);
            for (String layer : layers) {
                RLottieImageView rLottieImageView = this.imageView;
                rLottieImageView.setLayerColor(layer + ".**", this.textColor);
            }
        }

        public void setIconPaddingBottom(int paddingBottom) {
            this.imageView.setLayoutParams(LayoutHelper.createFrameRelatively(56.0f, (float) (48 - paddingBottom), 8388627, 0.0f, 0.0f, 0.0f, (float) paddingBottom));
        }

        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    public static abstract class Button extends FrameLayout implements Layout.Callback {
        public Button(Context context) {
            super(context);
        }

        public void onAttach(Layout layout, Bulletin bulletin) {
        }

        public void onDetach(Layout layout) {
        }

        public void onShow(Layout layout) {
        }

        public void onHide(Layout layout) {
        }

        public void onEnterTransitionStart(Layout layout) {
        }

        public void onEnterTransitionEnd(Layout layout) {
        }

        public void onExitTransitionStart(Layout layout) {
        }

        public void onExitTransitionEnd(Layout layout) {
        }
    }

    public static final class UndoButton extends Button {
        private Bulletin bulletin;
        private Runnable delayedAction;
        private boolean isUndone;
        private final Theme.ResourcesProvider resourcesProvider;
        private Runnable undoAction;
        private TextView undoTextView;

        public UndoButton(Context context, boolean text) {
            this(context, text, (Theme.ResourcesProvider) null);
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public UndoButton(Context context, boolean text, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            int undoCancelColor = getThemedColor("undo_cancelColor");
            if (text) {
                TextView textView = new TextView(context);
                this.undoTextView = textView;
                textView.setOnClickListener(new Bulletin$UndoButton$$ExternalSyntheticLambda0(this));
                this.undoTextView.setBackground(Theme.createCircleSelectorDrawable(NUM | (16777215 & undoCancelColor), LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0, !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0));
                this.undoTextView.setTextSize(1, 14.0f);
                this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.undoTextView.setTextColor(undoCancelColor);
                this.undoTextView.setText(LocaleController.getString("Undo", NUM));
                this.undoTextView.setGravity(16);
                ViewHelper.setPaddingRelative(this.undoTextView, 16.0f, 0.0f, 16.0f, 0.0f);
                addView(this.undoTextView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
                return;
            }
            Context context2 = context;
            ImageView undoImageView = new ImageView(getContext());
            undoImageView.setOnClickListener(new Bulletin$UndoButton$$ExternalSyntheticLambda1(this));
            undoImageView.setImageResource(NUM);
            undoImageView.setColorFilter(new PorterDuffColorFilter(undoCancelColor, PorterDuff.Mode.MULTIPLY));
            undoImageView.setBackground(Theme.createSelectorDrawable(NUM | (16777215 & undoCancelColor)));
            ViewHelper.setPaddingRelative(undoImageView, 0.0f, 12.0f, 0.0f, 12.0f);
            addView(undoImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-Bulletin$UndoButton  reason: not valid java name */
        public /* synthetic */ void m644lambda$new$0$orgtelegramuiComponentsBulletin$UndoButton(View v) {
            undo();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-Bulletin$UndoButton  reason: not valid java name */
        public /* synthetic */ void m645lambda$new$1$orgtelegramuiComponentsBulletin$UndoButton(View v) {
            undo();
        }

        public UndoButton setText(CharSequence text) {
            TextView textView = this.undoTextView;
            if (textView != null) {
                textView.setText(text);
            }
            return this;
        }

        public void undo() {
            if (this.bulletin != null) {
                this.isUndone = true;
                Runnable runnable = this.undoAction;
                if (runnable != null) {
                    runnable.run();
                }
                this.bulletin.hide();
            }
        }

        public void onAttach(Layout layout, Bulletin bulletin2) {
            this.bulletin = bulletin2;
        }

        public void onDetach(Layout layout) {
            this.bulletin = null;
            Runnable runnable = this.delayedAction;
            if (runnable != null && !this.isUndone) {
                runnable.run();
            }
        }

        public UndoButton setUndoAction(Runnable undoAction2) {
            this.undoAction = undoAction2;
            return this;
        }

        public UndoButton setDelayedAction(Runnable delayedAction2) {
            this.delayedAction = delayedAction2;
            return this;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public static class EmptyBulletin extends Bulletin {
        public EmptyBulletin() {
            super();
        }

        public Bulletin show() {
            return this;
        }
    }
}
