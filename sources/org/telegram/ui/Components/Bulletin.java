package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
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

public final class Bulletin {
    /* access modifiers changed from: private */
    public static final HashMap<FrameLayout, Delegate> delegates = new HashMap<>();
    @SuppressLint({"StaticFieldLeak"})
    private static Bulletin visibleBulletin;
    private boolean canHide;
    /* access modifiers changed from: private */
    public final FrameLayout containerLayout;
    public int currentBottomOffset;
    /* access modifiers changed from: private */
    public Delegate currentDelegate;
    private final int duration;
    private final Runnable hideRunnable = new Bulletin$$ExternalSyntheticLambda2(this);
    /* access modifiers changed from: private */
    public final Layout layout;
    /* access modifiers changed from: private */
    public Layout.Transition layoutTransition;
    private final ParentLayout parentLayout;
    /* access modifiers changed from: private */
    public boolean showing;

    public interface Delegate {

        /* renamed from: org.telegram.ui.Components.Bulletin$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static int $default$getBottomOffset(Delegate delegate) {
                return 0;
            }

            public static void $default$onHide(Delegate delegate, Bulletin bulletin) {
            }

            public static void $default$onOffsetChange(Delegate delegate, float f) {
            }

            public static void $default$onShow(Delegate delegate, Bulletin bulletin) {
            }
        }

        int getBottomOffset();

        void onHide(Bulletin bulletin);

        void onOffsetChange(float f);

        void onShow(Bulletin bulletin);
    }

    public static Bulletin make(FrameLayout frameLayout, Layout layout2, int i) {
        return new Bulletin(frameLayout, layout2, i);
    }

    @SuppressLint({"RtlHardcoded"})
    public static Bulletin make(BaseFragment baseFragment, Layout layout2, int i) {
        if (baseFragment instanceof ChatActivity) {
            layout2.setWideScreenParams(-2, 5);
        } else if (baseFragment instanceof DialogsActivity) {
            layout2.setWideScreenParams(-1, 0);
        }
        return new Bulletin(baseFragment.getLayoutContainer(), layout2, i);
    }

    public static Bulletin find(FrameLayout frameLayout) {
        int childCount = frameLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = frameLayout.getChildAt(i);
            if (childAt instanceof Layout) {
                return ((Layout) childAt).bulletin;
            }
        }
        return null;
    }

    public static void hide(FrameLayout frameLayout) {
        hide(frameLayout, true);
    }

    public static void hide(FrameLayout frameLayout, boolean z) {
        Bulletin find = find(frameLayout);
        if (find != null) {
            find.hide(z && isTransitionsEnabled(), 0);
        }
    }

    private Bulletin(final FrameLayout frameLayout, Layout layout2, int i) {
        this.layout = layout2;
        this.parentLayout = new ParentLayout(layout2) {
            /* access modifiers changed from: protected */
            public void onPressedStateChanged(boolean z) {
                Bulletin.this.setCanHide(!z);
                if (frameLayout.getParent() != null) {
                    frameLayout.getParent().requestDisallowInterceptTouchEvent(z);
                }
            }

            /* access modifiers changed from: protected */
            public void onHide() {
                Bulletin.this.hide();
            }
        };
        this.containerLayout = frameLayout;
        this.duration = i;
    }

    public static Bulletin getVisibleBulletin() {
        return visibleBulletin;
    }

    public Bulletin show() {
        if (!this.showing && this.containerLayout != null) {
            this.showing = true;
            if (this.layout.getParent() == this.parentLayout) {
                Bulletin bulletin = visibleBulletin;
                if (bulletin != null) {
                    bulletin.hide();
                }
                visibleBulletin = this;
                this.layout.onAttach(this);
                this.layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                        Bulletin.this.layout.removeOnLayoutChangeListener(this);
                        if (Bulletin.this.showing) {
                            Bulletin.this.layout.onShow();
                            Delegate unused = Bulletin.this.currentDelegate = (Delegate) Bulletin.delegates.get(Bulletin.this.containerLayout);
                            Bulletin bulletin = Bulletin.this;
                            bulletin.currentBottomOffset = bulletin.currentDelegate != null ? Bulletin.this.currentDelegate.getBottomOffset() : 0;
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

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onLayoutChange$0() {
                        Bulletin.this.layout.transitionRunning = false;
                        Bulletin.this.layout.onEnterTransitionEnd();
                        Bulletin.this.setCanHide(true);
                    }

                    /* access modifiers changed from: private */
                    public /* synthetic */ void lambda$onLayoutChange$1(Float f) {
                        if (Bulletin.this.currentDelegate != null) {
                            Bulletin.this.currentDelegate.onOffsetChange(((float) Bulletin.this.layout.getHeight()) - f.floatValue());
                        }
                    }
                });
                this.layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    public void onViewAttachedToWindow(View view) {
                    }

                    public void onViewDetachedFromWindow(View view) {
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
    public void setCanHide(boolean z) {
        if (this.canHide != z) {
            this.canHide = z;
            if (z) {
                this.layout.postDelayed(this.hideRunnable, (long) this.duration);
            } else {
                this.layout.removeCallbacks(this.hideRunnable);
            }
        }
    }

    /* access modifiers changed from: private */
    public void ensureLayoutTransitionCreated() {
        if (this.layoutTransition == null) {
            this.layoutTransition = this.layout.createTransition();
        }
    }

    public void hide() {
        hide(isTransitionsEnabled(), 0);
    }

    public void hide(long j) {
        hide(isTransitionsEnabled(), j);
    }

    public void hide(boolean z, long j) {
        if (this.showing) {
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            int i = this.currentBottomOffset;
            this.currentBottomOffset = 0;
            if (ViewCompat.isLaidOut(this.layout)) {
                this.layout.removeCallbacks(this.hideRunnable);
                if (z) {
                    Layout layout2 = this.layout;
                    layout2.transitionRunning = true;
                    layout2.delegate = this.currentDelegate;
                    layout2.invalidate();
                    if (j >= 0) {
                        Layout.DefaultTransition defaultTransition = new Layout.DefaultTransition();
                        defaultTransition.duration = j;
                        this.layoutTransition = defaultTransition;
                    } else {
                        ensureLayoutTransitionCreated();
                    }
                    Layout.Transition transition = this.layoutTransition;
                    Layout layout3 = this.layout;
                    layout3.getClass();
                    transition.animateExit(layout3, new Bulletin$$ExternalSyntheticLambda1(layout3), new Bulletin$$ExternalSyntheticLambda3(this), new Bulletin$$ExternalSyntheticLambda0(this), i);
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$0() {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(0.0f);
            this.currentDelegate.onHide(this);
        }
        Layout layout2 = this.layout;
        layout2.transitionRunning = false;
        layout2.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.parentLayout);
        this.layout.onDetach();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$1(Float f) {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(((float) this.layout.getHeight()) - f.floatValue());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$hide$2() {
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
        this.layout.updatePosition();
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

        static /* synthetic */ float access$1424(ParentLayout parentLayout, float f) {
            float f2 = parentLayout.translationX - f;
            parentLayout.translationX = f2;
            return f2;
        }

        public ParentLayout(final Layout layout2) {
            super(layout2.getContext());
            this.layout = layout2;
            GestureDetector gestureDetector2 = new GestureDetector(layout2.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onDown(MotionEvent motionEvent) {
                    if (ParentLayout.this.hideAnimationRunning) {
                        return false;
                    }
                    boolean unused = ParentLayout.this.needLeftAlphaAnimation = layout2.isNeedSwipeAlphaAnimation(true);
                    boolean unused2 = ParentLayout.this.needRightAlphaAnimation = layout2.isNeedSwipeAlphaAnimation(false);
                    return true;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    layout2.setTranslationX(ParentLayout.access$1424(ParentLayout.this, f));
                    if (ParentLayout.this.translationX != 0.0f && ((ParentLayout.this.translationX >= 0.0f || !ParentLayout.this.needLeftAlphaAnimation) && (ParentLayout.this.translationX <= 0.0f || !ParentLayout.this.needRightAlphaAnimation))) {
                        return true;
                    }
                    layout2.setAlpha(1.0f - (Math.abs(ParentLayout.this.translationX) / ((float) layout2.getWidth())));
                    return true;
                }

                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    boolean z = false;
                    if (Math.abs(f) <= 2000.0f) {
                        return false;
                    }
                    if ((f < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (f > 0.0f && ParentLayout.this.needRightAlphaAnimation)) {
                        z = true;
                    }
                    SpringAnimation springAnimation = new SpringAnimation(layout2, DynamicAnimation.TRANSLATION_X, Math.signum(f) * ((float) layout2.getWidth()) * 2.0f);
                    if (!z) {
                        springAnimation.addEndListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda0(this));
                        springAnimation.addUpdateListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda2(layout2));
                    }
                    springAnimation.getSpring().setDampingRatio(1.0f);
                    springAnimation.getSpring().setStiffness(100.0f);
                    springAnimation.setStartVelocity(f);
                    springAnimation.start();
                    if (z) {
                        SpringAnimation springAnimation2 = new SpringAnimation(layout2, DynamicAnimation.ALPHA, 0.0f);
                        springAnimation2.addEndListener(new Bulletin$ParentLayout$1$$ExternalSyntheticLambda1(this));
                        springAnimation2.addUpdateListener(Bulletin$ParentLayout$1$$ExternalSyntheticLambda3.INSTANCE);
                        springAnimation.getSpring().setDampingRatio(1.0f);
                        springAnimation.getSpring().setStiffness(10.0f);
                        springAnimation.setStartVelocity(f);
                        springAnimation2.start();
                    }
                    boolean unused = ParentLayout.this.hideAnimationRunning = true;
                    return true;
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFling$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    ParentLayout.this.onHide();
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onFling$1(Layout layout, DynamicAnimation dynamicAnimation, float f, float f2) {
                    if (Math.abs(f) > ((float) layout.getWidth())) {
                        dynamicAnimation.cancel();
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onFling$2(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    ParentLayout.this.onHide();
                }

                /* access modifiers changed from: private */
                public static /* synthetic */ void lambda$onFling$3(DynamicAnimation dynamicAnimation, float f, float f2) {
                    if (f <= 0.0f) {
                        dynamicAnimation.cancel();
                    }
                }
            });
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            addView(layout2);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.pressed && !inLayoutHitRect(motionEvent.getX(), motionEvent.getY())) {
                return false;
            }
            this.gestureDetector.onTouchEvent(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
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
                        float signum = Math.signum(this.translationX) * ((float) this.layout.getWidth());
                        float f2 = this.translationX;
                        boolean z = (f2 < 0.0f && this.needLeftAlphaAnimation) || (f2 > 0.0f && this.needRightAlphaAnimation);
                        ViewPropertyAnimator translationX2 = this.layout.animate().translationX(signum);
                        if (z) {
                            f = 0.0f;
                        }
                        translationX2.alpha(f).setDuration(200).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Bulletin$ParentLayout$$ExternalSyntheticLambda0(this, signum)).start();
                    } else {
                        this.layout.animate().translationX(0.0f).alpha(1.0f).setDuration(200).start();
                    }
                }
                this.pressed = false;
                onPressedStateChanged(false);
            }
            return true;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onTouchEvent$0(float f) {
            if (this.layout.getTranslationX() == f) {
                onHide();
            }
        }

        private boolean inLayoutHitRect(float f, float f2) {
            this.layout.getHitRect(this.rect);
            return this.rect.contains((int) f, (int) f2);
        }
    }

    public static void addDelegate(BaseFragment baseFragment, Delegate delegate) {
        FrameLayout layoutContainer = baseFragment.getLayoutContainer();
        if (layoutContainer != null) {
            addDelegate(layoutContainer, delegate);
        }
    }

    public static void addDelegate(FrameLayout frameLayout, Delegate delegate) {
        delegates.put(frameLayout, delegate);
    }

    public static void removeDelegate(BaseFragment baseFragment) {
        FrameLayout layoutContainer = baseFragment.getLayoutContainer();
        if (layoutContainer != null) {
            removeDelegate(layoutContainer);
        }
    }

    public static void removeDelegate(FrameLayout frameLayout) {
        delegates.remove(frameLayout);
    }

    public static abstract class Layout extends FrameLayout {
        public static final FloatPropertyCompat<Layout> IN_OUT_OFFSET_Y = new FloatPropertyCompat<Layout>("offsetY") {
            public float getValue(Layout layout) {
                return layout.inOutOffset;
            }

            public void setValue(Layout layout, float f) {
                layout.setInOutOffset(f);
            }
        };
        public static final Property<Layout, Float> IN_OUT_OFFSET_Y2 = new AnimationProperties.FloatProperty<Layout>("offsetY") {
            public Float get(Layout layout) {
                return Float.valueOf(layout.inOutOffset);
            }

            public void setValue(Layout layout, float f) {
                layout.setInOutOffset(f);
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
        public void setBackground(int i) {
            this.background = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), i);
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
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
            if (!AndroidUtilities.isTablet()) {
                Point point = AndroidUtilities.displaySize;
                return point.x >= point.y;
            }
        }

        /* access modifiers changed from: private */
        public void setWideScreenParams(int i, int i2) {
            boolean z;
            boolean z2 = true;
            if (this.wideScreenWidth != i) {
                this.wideScreenWidth = i;
                z = true;
            } else {
                z = false;
            }
            if (this.wideScreenGravity != i2) {
                this.wideScreenGravity = i2;
            } else {
                z2 = z;
            }
            if (isWideScreen() && z2) {
                updateSize();
            }
        }

        /* access modifiers changed from: private */
        @SuppressLint({"RtlHardcoded"})
        public boolean isNeedSwipeAlphaAnimation(boolean z) {
            if (!isWideScreen() || this.wideScreenWidth == -1) {
                return false;
            }
            int i = this.wideScreenGravity;
            if (i == 1) {
                return true;
            }
            if (z) {
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

        public Bulletin getBulletin() {
            return this.bulletin;
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
            Delegate delegate2 = this.delegate;
            float f = 0.0f;
            if (delegate2 != null) {
                f = 0.0f + ((float) delegate2.getBottomOffset());
            }
            setTranslationY((-f) + this.inOutOffset);
        }

        public Transition createTransition() {
            return new SpringTransition();
        }

        public static class DefaultTransition implements Transition {
            long duration = 255;

            public void animateEnter(Layout layout, final Runnable runnable, final Runnable runnable2, Consumer<Float> consumer, int i) {
                layout.setInOutOffset((float) layout.getMeasuredHeight());
                if (consumer != null) {
                    consumer.accept(Float.valueOf(layout.getTranslationY()));
                }
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, new float[]{0.0f});
                ofFloat.setDuration(this.duration);
                ofFloat.setInterpolator(Easings.easeOutQuad);
                if (!(runnable == null && runnable2 == null)) {
                    ofFloat.addListener(new AnimatorListenerAdapter(this) {
                        public void onAnimationStart(Animator animator) {
                            Runnable runnable = runnable;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            Runnable runnable = runnable2;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (consumer != null) {
                    ofFloat.addUpdateListener(new Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0(consumer, layout));
                }
                ofFloat.start();
            }

            public void animateExit(Layout layout, final Runnable runnable, final Runnable runnable2, Consumer<Float> consumer, int i) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, new float[]{(float) layout.getHeight()});
                ofFloat.setDuration(175);
                ofFloat.setInterpolator(Easings.easeInQuad);
                if (!(runnable == null && runnable2 == null)) {
                    ofFloat.addListener(new AnimatorListenerAdapter(this) {
                        public void onAnimationStart(Animator animator) {
                            Runnable runnable = runnable;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        public void onAnimationEnd(Animator animator) {
                            Runnable runnable = runnable2;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (consumer != null) {
                    ofFloat.addUpdateListener(new Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda1(consumer, layout));
                }
                ofFloat.start();
            }
        }

        public static class SpringTransition implements Transition {
            public void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i) {
                layout.setInOutOffset((float) layout.getMeasuredHeight());
                if (consumer != null) {
                    consumer.accept(Float.valueOf(layout.getTranslationY()));
                }
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, 0.0f);
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (runnable2 != null) {
                    springAnimation.addEndListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1(layout, runnable2));
                }
                if (consumer != null) {
                    springAnimation.addUpdateListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3(consumer, layout));
                }
                springAnimation.start();
                if (runnable != null) {
                    runnable.run();
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$animateEnter$0(Layout layout, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                layout.setInOutOffset(0.0f);
                if (!z) {
                    runnable.run();
                }
            }

            public void animateExit(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i) {
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, (float) layout.getHeight());
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (runnable2 != null) {
                    springAnimation.addEndListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0(runnable2));
                }
                if (consumer != null) {
                    springAnimation.addUpdateListener(new Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda2(consumer, layout));
                }
                springAnimation.start();
                if (runnable != null) {
                    runnable.run();
                }
            }

            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$animateExit$2(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                if (!z) {
                    runnable.run();
                }
            }
        }

        /* access modifiers changed from: private */
        public void setInOutOffset(float f) {
            this.inOutOffset = f;
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
            int measuredHeight = ((View) getParent()).getMeasuredHeight() - this.delegate.getBottomOffset();
            canvas.save();
            canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - (((int) (getY() + ((float) getMeasuredHeight()))) - measuredHeight));
            this.background.draw(canvas);
            super.dispatchDraw(canvas);
            canvas.restore();
            invalidate();
        }

        /* access modifiers changed from: protected */
        public int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    @SuppressLint({"ViewConstructor"})
    public static class ButtonLayout extends Layout {
        private Button button;
        private int childrenMeasuredWidth;

        public ButtonLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            this.childrenMeasuredWidth = 0;
            super.onMeasure(i, i2);
            if (this.button != null && View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
                setMeasuredDimension(this.childrenMeasuredWidth + this.button.getMeasuredWidth(), getMeasuredHeight());
            }
        }

        /* access modifiers changed from: protected */
        public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
            Button button2 = this.button;
            if (!(button2 == null || view == button2)) {
                i2 += button2.getMeasuredWidth() - AndroidUtilities.dp(12.0f);
            }
            super.measureChildWithMargins(view, i, i2, i3, i4);
            if (view != this.button) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                this.childrenMeasuredWidth = Math.max(this.childrenMeasuredWidth, marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + view.getMeasuredWidth());
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
            int themedColor = getThemedColor("undo_infoColor");
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            addView(imageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setSingleLine();
            textView2.setTextColor(themedColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 15.0f);
            addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }
    }

    @SuppressLint({"ViewConstructor"})
    public static class TwoLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView subtitleTextView;
        public final TextView titleTextView;

        public TwoLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int themedColor = getThemedColor("undo_infoColor");
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrameRelatively(29.0f, 29.0f, 8388627, 12.0f, 12.0f, 12.0f, 12.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 54.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(themedColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setMaxLines(2);
            textView2.setTextColor(themedColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
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
            int themedColor = getThemedColor("undo_infoColor");
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(themedColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setTextColor(themedColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        /* access modifiers changed from: protected */
        public void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int i, int i2, int i3, String... strArr) {
            this.imageView.setAnimation(i, i2, i3);
            for (String str : strArr) {
                RLottieImageView rLottieImageView = this.imageView;
                rLottieImageView.setLayerColor(str + ".**", this.textColor);
            }
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

        public LottieLayout(Context context, Theme.ResourcesProvider resourcesProvider, int i, int i2) {
            this(context, resourcesProvider);
            setBackground(i);
            setTextColor(i2);
        }

        public void setTextColor(int i) {
            this.textColor = i;
            this.textView.setTextColor(i);
        }

        /* access modifiers changed from: protected */
        public void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int i, String... strArr) {
            setAnimation(i, 32, 32, strArr);
        }

        public void setAnimation(int i, int i2, int i3, String... strArr) {
            this.imageView.setAnimation(i, i2, i3);
            for (String str : strArr) {
                RLottieImageView rLottieImageView = this.imageView;
                rLottieImageView.setLayerColor(str + ".**", this.textColor);
            }
        }

        public void setIconPaddingBottom(int i) {
            this.imageView.setLayoutParams(LayoutHelper.createFrameRelatively(56.0f, (float) (48 - i), 8388627, 0.0f, 0.0f, 0.0f, (float) i));
        }
    }

    @SuppressLint({"ViewConstructor"})
    public static abstract class Button extends FrameLayout implements Layout.Callback {
        public void onEnterTransitionEnd(Layout layout) {
        }

        public void onEnterTransitionStart(Layout layout) {
        }

        public void onExitTransitionEnd(Layout layout) {
        }

        public void onExitTransitionStart(Layout layout) {
        }

        public void onHide(Layout layout) {
        }

        public void onShow(Layout layout) {
        }

        public Button(Context context) {
            super(context);
        }
    }

    @SuppressLint({"ViewConstructor"})
    public static final class UndoButton extends Button {
        private Bulletin bulletin;
        private Runnable delayedAction;
        private boolean isUndone;
        private final Theme.ResourcesProvider resourcesProvider;
        private Runnable undoAction;

        public UndoButton(Context context, boolean z) {
            this(context, z, (Theme.ResourcesProvider) null);
        }

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public UndoButton(Context context, boolean z, Theme.ResourcesProvider resourcesProvider2) {
            super(context);
            this.resourcesProvider = resourcesProvider2;
            int themedColor = getThemedColor("undo_cancelColor");
            if (z) {
                TextView textView = new TextView(context);
                textView.setOnClickListener(new Bulletin$UndoButton$$ExternalSyntheticLambda0(this));
                textView.setBackground(Theme.createCircleSelectorDrawable(NUM | (16777215 & themedColor), LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0, !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0));
                textView.setTextSize(1, 14.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setTextColor(themedColor);
                textView.setText(LocaleController.getString("Undo", NUM));
                textView.setGravity(16);
                ViewHelper.setPaddingRelative(textView, 16.0f, 0.0f, 16.0f, 0.0f);
                addView(textView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
                return;
            }
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new Bulletin$UndoButton$$ExternalSyntheticLambda1(this));
            imageView.setImageResource(NUM);
            imageView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            imageView.setBackground(Theme.createSelectorDrawable((themedColor & 16777215) | NUM));
            ViewHelper.setPaddingRelative(imageView, 0.0f, 12.0f, 0.0f, 12.0f);
            addView(imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            undo();
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(View view) {
            undo();
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

        public UndoButton setUndoAction(Runnable runnable) {
            this.undoAction = runnable;
            return this;
        }

        public UndoButton setDelayedAction(Runnable runnable) {
            this.delayedAction = runnable;
            return this;
        }

        private int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
            Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }
}
