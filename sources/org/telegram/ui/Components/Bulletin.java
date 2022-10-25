package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes3.dex */
public class Bulletin {
    private static final HashMap<FrameLayout, Delegate> delegates = new HashMap<>();
    @SuppressLint({"StaticFieldLeak"})
    private static Bulletin visibleBulletin;
    private boolean canHide;
    private final FrameLayout containerLayout;
    public int currentBottomOffset;
    private Delegate currentDelegate;
    private int duration;
    public int hash;
    private final Runnable hideRunnable;
    private final Layout layout;
    private Layout.Transition layoutTransition;
    private final ParentLayout parentLayout;
    private boolean showing;
    public int tag;

    /* loaded from: classes3.dex */
    public interface Delegate {

        /* renamed from: org.telegram.ui.Components.Bulletin$Delegate$-CC */
        /* loaded from: classes3.dex */
        public final /* synthetic */ class CC {
            public static int $default$getBottomOffset(Delegate delegate, int i) {
                return 0;
            }

            public static int $default$getTopOffset(Delegate delegate, int i) {
                return 0;
            }

            public static void $default$onBottomOffsetChange(Delegate delegate, float f) {
            }

            public static void $default$onHide(Delegate delegate, Bulletin bulletin) {
            }

            public static void $default$onShow(Delegate delegate, Bulletin bulletin) {
            }
        }

        int getBottomOffset(int i);

        int getTopOffset(int i);

        void onBottomOffsetChange(float f);

        void onHide(Bulletin bulletin);

        void onShow(Bulletin bulletin);
    }

    public static Bulletin make(FrameLayout frameLayout, Layout layout, int i) {
        return new Bulletin(frameLayout, layout, i);
    }

    @SuppressLint({"RtlHardcoded"})
    public static Bulletin make(BaseFragment baseFragment, Layout layout, int i) {
        if (!(baseFragment instanceof ChatActivity)) {
            if (baseFragment instanceof DialogsActivity) {
                layout.setWideScreenParams(-1, 0);
            }
        } else {
            layout.setWideScreenParams(-2, 5);
        }
        return new Bulletin(baseFragment.getLayoutContainer(), layout, i);
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
            find.hide(z && isTransitionsEnabled(), 0L);
        }
    }

    private Bulletin() {
        this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Bulletin.this.hide();
            }
        };
        this.layout = null;
        this.parentLayout = null;
        this.containerLayout = null;
    }

    private Bulletin(final FrameLayout frameLayout, Layout layout, int i) {
        this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Bulletin.this.hide();
            }
        };
        this.layout = layout;
        this.parentLayout = new ParentLayout(layout) { // from class: org.telegram.ui.Components.Bulletin.1
            {
                Bulletin.this = this;
            }

            @Override // org.telegram.ui.Components.Bulletin.ParentLayout
            protected void onPressedStateChanged(boolean z) {
                Bulletin.this.setCanHide(!z);
                if (frameLayout.getParent() != null) {
                    frameLayout.getParent().requestDisallowInterceptTouchEvent(z);
                }
            }

            @Override // org.telegram.ui.Components.Bulletin.ParentLayout
            protected void onHide() {
                Bulletin.this.hide();
            }
        };
        this.containerLayout = frameLayout;
        this.duration = i;
    }

    public static Bulletin getVisibleBulletin() {
        return visibleBulletin;
    }

    public static void hideVisible() {
        Bulletin bulletin = visibleBulletin;
        if (bulletin != null) {
            bulletin.hide();
        }
    }

    public void setDuration(int i) {
        this.duration = i;
    }

    public Bulletin show() {
        return show(false);
    }

    public Bulletin show(boolean z) {
        if (!this.showing && this.containerLayout != null) {
            this.showing = true;
            this.layout.setTop(z);
            CharSequence accessibilityText = this.layout.getAccessibilityText();
            if (accessibilityText != null) {
                AndroidUtilities.makeAccessibilityAnnouncement(accessibilityText);
            }
            if (this.layout.getParent() != this.parentLayout) {
                throw new IllegalStateException("Layout has incorrect parent");
            }
            Bulletin bulletin = visibleBulletin;
            if (bulletin != null) {
                bulletin.hide();
            }
            visibleBulletin = this;
            this.layout.onAttach(this);
            this.layout.addOnLayoutChangeListener(new AnonymousClass2(z));
            this.layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.Components.Bulletin.3
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View view) {
                }

                {
                    Bulletin.this = this;
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View view) {
                    Bulletin.this.layout.removeOnAttachStateChangeListener(this);
                    Bulletin.this.hide(false, 0L);
                }
            });
            this.containerLayout.addView(this.parentLayout);
        }
        return this;
    }

    /* renamed from: org.telegram.ui.Components.Bulletin$2 */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 implements View.OnLayoutChangeListener {
        final /* synthetic */ boolean val$top;

        AnonymousClass2(boolean z) {
            Bulletin.this = r1;
            this.val$top = z;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            Bulletin.this.layout.removeOnLayoutChangeListener(this);
            if (Bulletin.this.showing) {
                Bulletin.this.layout.onShow();
                Bulletin.this.currentDelegate = (Delegate) Bulletin.delegates.get(Bulletin.this.containerLayout);
                Bulletin bulletin = Bulletin.this;
                bulletin.currentBottomOffset = bulletin.currentDelegate != null ? Bulletin.this.currentDelegate.getBottomOffset(Bulletin.this.tag) : 0;
                if (Bulletin.this.currentDelegate != null) {
                    Bulletin.this.currentDelegate.onShow(Bulletin.this);
                }
                if (Bulletin.isTransitionsEnabled()) {
                    Bulletin.this.ensureLayoutTransitionCreated();
                    Bulletin.this.layout.transitionRunningEnter = true;
                    Bulletin.this.layout.delegate = Bulletin.this.currentDelegate;
                    Bulletin.this.layout.invalidate();
                    Layout.Transition transition = Bulletin.this.layoutTransition;
                    Layout layout = Bulletin.this.layout;
                    final Layout layout2 = Bulletin.this.layout;
                    layout2.getClass();
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.Layout.this.onEnterTransitionStart();
                        }
                    };
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.AnonymousClass2.this.lambda$onLayoutChange$0();
                        }
                    };
                    final boolean z = this.val$top;
                    transition.animateEnter(layout, runnable, runnable2, new Consumer() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda0
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            Bulletin.AnonymousClass2.this.lambda$onLayoutChange$1(z, (Float) obj);
                        }
                    }, Bulletin.this.currentBottomOffset);
                    return;
                }
                if (Bulletin.this.currentDelegate != null && !this.val$top) {
                    Bulletin.this.currentDelegate.onBottomOffsetChange(Bulletin.this.layout.getHeight() - Bulletin.this.currentBottomOffset);
                }
                Bulletin.this.updatePosition();
                Bulletin.this.layout.onEnterTransitionStart();
                Bulletin.this.layout.onEnterTransitionEnd();
                Bulletin.this.setCanHide(true);
            }
        }

        public /* synthetic */ void lambda$onLayoutChange$0() {
            Bulletin.this.layout.transitionRunningEnter = false;
            Bulletin.this.layout.onEnterTransitionEnd();
            Bulletin.this.setCanHide(true);
        }

        public /* synthetic */ void lambda$onLayoutChange$1(boolean z, Float f) {
            if (Bulletin.this.currentDelegate == null || z) {
                return;
            }
            Bulletin.this.currentDelegate.onBottomOffsetChange(Bulletin.this.layout.getHeight() - f.floatValue());
        }
    }

    public void setCanHide(boolean z) {
        Layout layout;
        if (this.canHide == z || (layout = this.layout) == null) {
            return;
        }
        this.canHide = z;
        if (z) {
            layout.postDelayed(this.hideRunnable, this.duration);
        } else {
            layout.removeCallbacks(this.hideRunnable);
        }
    }

    public void ensureLayoutTransitionCreated() {
        Layout layout = this.layout;
        if (layout == null || this.layoutTransition != null) {
            return;
        }
        this.layoutTransition = layout.createTransition();
    }

    public void hide() {
        hide(isTransitionsEnabled(), 0L);
    }

    public void hide(long j) {
        hide(isTransitionsEnabled(), j);
    }

    public void hide(boolean z, long j) {
        Layout layout = this.layout;
        if (layout != null && this.showing) {
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            int i = this.currentBottomOffset;
            this.currentBottomOffset = 0;
            if (ViewCompat.isLaidOut(layout)) {
                this.layout.removeCallbacks(this.hideRunnable);
                if (z) {
                    Layout layout2 = this.layout;
                    layout2.transitionRunningExit = true;
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
                    final Layout layout3 = this.layout;
                    layout3.getClass();
                    transition.animateExit(layout3, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.Layout.this.onExitTransitionStart();
                        }
                    }, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.this.lambda$hide$0();
                        }
                    }, new Consumer() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda0
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            Bulletin.this.lambda$hide$1((Float) obj);
                        }
                    }, i);
                    return;
                }
            }
            if (this.currentDelegate != null && !this.layout.top) {
                this.currentDelegate.onBottomOffsetChange(0.0f);
                this.currentDelegate.onHide(this);
            }
            this.layout.onExitTransitionStart();
            this.layout.onExitTransitionEnd();
            this.layout.onHide();
            if (this.containerLayout != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        Bulletin.this.lambda$hide$2();
                    }
                });
            }
            this.layout.onDetach();
        }
    }

    public /* synthetic */ void lambda$hide$0() {
        if (this.currentDelegate != null && !this.layout.top) {
            this.currentDelegate.onBottomOffsetChange(0.0f);
            this.currentDelegate.onHide(this);
        }
        Layout layout = this.layout;
        layout.transitionRunningExit = false;
        layout.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.parentLayout);
        this.layout.onDetach();
    }

    public /* synthetic */ void lambda$hide$1(Float f) {
        if (this.currentDelegate == null || this.layout.top) {
            return;
        }
        this.currentDelegate.onBottomOffsetChange(this.layout.getHeight() - f.floatValue());
    }

    public /* synthetic */ void lambda$hide$2() {
        this.containerLayout.removeView(this.parentLayout);
    }

    public boolean isShowing() {
        return this.showing;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public static boolean isTransitionsEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && Build.VERSION.SDK_INT >= 18;
    }

    public void updatePosition() {
        Layout layout = this.layout;
        if (layout != null) {
            layout.updatePosition();
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class ParentLayout extends FrameLayout {
        private final GestureDetector gestureDetector;
        private boolean hideAnimationRunning;
        private final Layout layout;
        private boolean needLeftAlphaAnimation;
        private boolean needRightAlphaAnimation;
        private boolean pressed;
        private final android.graphics.Rect rect;
        private float translationX;

        protected abstract void onHide();

        protected abstract void onPressedStateChanged(boolean z);

        static /* synthetic */ float access$1624(ParentLayout parentLayout, float f) {
            float f2 = parentLayout.translationX - f;
            parentLayout.translationX = f2;
            return f2;
        }

        public ParentLayout(Layout layout) {
            super(layout.getContext());
            this.rect = new android.graphics.Rect();
            this.layout = layout;
            GestureDetector gestureDetector = new GestureDetector(layout.getContext(), new AnonymousClass1(layout));
            this.gestureDetector = gestureDetector;
            gestureDetector.setIsLongpressEnabled(false);
            addView(layout);
        }

        /* renamed from: org.telegram.ui.Components.Bulletin$ParentLayout$1 */
        /* loaded from: classes3.dex */
        public class AnonymousClass1 extends GestureDetector.SimpleOnGestureListener {
            final /* synthetic */ Layout val$layout;

            AnonymousClass1(Layout layout) {
                ParentLayout.this = r1;
                this.val$layout = layout;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                if (!ParentLayout.this.hideAnimationRunning) {
                    ParentLayout.this.needLeftAlphaAnimation = this.val$layout.isNeedSwipeAlphaAnimation(true);
                    ParentLayout.this.needRightAlphaAnimation = this.val$layout.isNeedSwipeAlphaAnimation(false);
                    return true;
                }
                return false;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                this.val$layout.setTranslationX(ParentLayout.access$1624(ParentLayout.this, f));
                if (ParentLayout.this.translationX == 0.0f || ((ParentLayout.this.translationX < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (ParentLayout.this.translationX > 0.0f && ParentLayout.this.needRightAlphaAnimation))) {
                    this.val$layout.setAlpha(1.0f - (Math.abs(ParentLayout.this.translationX) / this.val$layout.getWidth()));
                    return true;
                }
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                boolean z = false;
                if (Math.abs(f) > 2000.0f) {
                    if ((f < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (f > 0.0f && ParentLayout.this.needRightAlphaAnimation)) {
                        z = true;
                    }
                    SpringAnimation springAnimation = new SpringAnimation(this.val$layout, DynamicAnimation.TRANSLATION_X, Math.signum(f) * this.val$layout.getWidth() * 2.0f);
                    if (!z) {
                        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda0
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
                                Bulletin.ParentLayout.AnonymousClass1.this.lambda$onFling$0(dynamicAnimation, z2, f3, f4);
                            }
                        });
                        final Layout layout = this.val$layout;
                        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda2
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f3, float f4) {
                                Bulletin.ParentLayout.AnonymousClass1.lambda$onFling$1(Bulletin.Layout.this, dynamicAnimation, f3, f4);
                            }
                        });
                    }
                    springAnimation.getSpring().setDampingRatio(1.0f);
                    springAnimation.getSpring().setStiffness(100.0f);
                    springAnimation.setStartVelocity(f);
                    springAnimation.start();
                    if (z) {
                        SpringAnimation springAnimation2 = new SpringAnimation(this.val$layout, DynamicAnimation.ALPHA, 0.0f);
                        springAnimation2.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda1
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
                                Bulletin.ParentLayout.AnonymousClass1.this.lambda$onFling$2(dynamicAnimation, z2, f3, f4);
                            }
                        });
                        springAnimation2.addUpdateListener(Bulletin$ParentLayout$1$$ExternalSyntheticLambda3.INSTANCE);
                        springAnimation.getSpring().setDampingRatio(1.0f);
                        springAnimation.getSpring().setStiffness(10.0f);
                        springAnimation.setStartVelocity(f);
                        springAnimation2.start();
                    }
                    ParentLayout.this.hideAnimationRunning = true;
                    return true;
                }
                return false;
            }

            public /* synthetic */ void lambda$onFling$0(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ParentLayout.this.onHide();
            }

            public static /* synthetic */ void lambda$onFling$1(Layout layout, DynamicAnimation dynamicAnimation, float f, float f2) {
                if (Math.abs(f) > layout.getWidth()) {
                    dynamicAnimation.cancel();
                }
            }

            public /* synthetic */ void lambda$onFling$2(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ParentLayout.this.onHide();
            }

            public static /* synthetic */ void lambda$onFling$3(DynamicAnimation dynamicAnimation, float f, float f2) {
                if (f <= 0.0f) {
                    dynamicAnimation.cancel();
                }
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.pressed || inLayoutHitRect(motionEvent.getX(), motionEvent.getY())) {
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
                        if (Math.abs(this.translationX) > this.layout.getWidth() / 3.0f) {
                            final float signum = Math.signum(this.translationX) * this.layout.getWidth();
                            float f2 = this.translationX;
                            boolean z = (f2 < 0.0f && this.needLeftAlphaAnimation) || (f2 > 0.0f && this.needRightAlphaAnimation);
                            ViewPropertyAnimator translationX = this.layout.animate().translationX(signum);
                            if (z) {
                                f = 0.0f;
                            }
                            translationX.alpha(f).setDuration(200L).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    Bulletin.ParentLayout.this.lambda$onTouchEvent$0(signum);
                                }
                            }).start();
                        } else {
                            this.layout.animate().translationX(0.0f).alpha(1.0f).setDuration(200L).start();
                        }
                    }
                    this.pressed = false;
                    onPressedStateChanged(false);
                }
                return true;
            }
            return false;
        }

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

    /* loaded from: classes3.dex */
    public static abstract class Layout extends FrameLayout {
        public static final FloatPropertyCompat<Layout> IN_OUT_OFFSET_Y = new FloatPropertyCompat<Layout>("offsetY") { // from class: org.telegram.ui.Components.Bulletin.Layout.1
            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public float getValue(Layout layout) {
                return layout.inOutOffset;
            }

            @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
            public void setValue(Layout layout, float f) {
                layout.setInOutOffset(f);
            }
        };
        public static final Property<Layout, Float> IN_OUT_OFFSET_Y2 = new AnimationProperties.FloatProperty<Layout>("offsetY") { // from class: org.telegram.ui.Components.Bulletin.Layout.2
            @Override // android.util.Property
            public Float get(Layout layout) {
                return Float.valueOf(layout.inOutOffset);
            }

            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(Layout layout, float f) {
                layout.setInOutOffset(f);
            }
        };
        Drawable background;
        protected Bulletin bulletin;
        private final List<Callback> callbacks;
        Delegate delegate;
        public float inOutOffset;
        private final Theme.ResourcesProvider resourcesProvider;
        private boolean top;
        public boolean transitionRunningEnter;
        public boolean transitionRunningExit;
        private int wideScreenGravity;
        private int wideScreenWidth;

        /* loaded from: classes3.dex */
        public interface Callback {

            /* renamed from: org.telegram.ui.Components.Bulletin$Layout$Callback$-CC */
            /* loaded from: classes3.dex */
            public final /* synthetic */ class CC {
                public static void $default$onAttach(Callback callback, Layout layout, Bulletin bulletin) {
                }

                public static void $default$onDetach(Callback callback, Layout layout) {
                }

                public static void $default$onEnterTransitionEnd(Callback callback, Layout layout) {
                }

                public static void $default$onEnterTransitionStart(Callback callback, Layout layout) {
                }

                public static void $default$onExitTransitionEnd(Callback callback, Layout layout) {
                }

                public static void $default$onExitTransitionStart(Callback callback, Layout layout) {
                }
            }

            void onAttach(Layout layout, Bulletin bulletin);

            void onDetach(Layout layout);

            void onEnterTransitionEnd(Layout layout);

            void onEnterTransitionStart(Layout layout);

            void onExitTransitionEnd(Layout layout);

            void onExitTransitionStart(Layout layout);

            void onHide(Layout layout);

            void onShow(Layout layout);
        }

        /* loaded from: classes3.dex */
        public interface Transition {
            void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);

            void animateExit(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);
        }

        protected CharSequence getAccessibilityText() {
            return null;
        }

        public boolean isTransitionRunning() {
            return this.transitionRunningEnter || this.transitionRunningExit;
        }

        public Layout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.callbacks = new ArrayList();
            this.wideScreenWidth = -2;
            this.wideScreenGravity = 1;
            this.resourcesProvider = resourcesProvider;
            setMinimumHeight(AndroidUtilities.dp(48.0f));
            setBackground(getThemedColor("undo_background"));
            updateSize();
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
        }

        protected void setBackground(int i) {
            this.background = Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), i);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            updateSize();
        }

        public void setTop(boolean z) {
            this.top = z;
            updateSize();
        }

        private void updateSize() {
            boolean isWideScreen = isWideScreen();
            int i = isWideScreen ? this.wideScreenWidth : -1;
            int i2 = 48;
            if (isWideScreen) {
                if (!this.top) {
                    i2 = 80;
                }
                i2 |= this.wideScreenGravity;
            } else if (!this.top) {
                i2 = 80;
            }
            setLayoutParams(LayoutHelper.createFrame(i, -2, i2));
        }

        private boolean isWideScreen() {
            if (!AndroidUtilities.isTablet()) {
                android.graphics.Point point = AndroidUtilities.displaySize;
                if (point.x < point.y) {
                    return false;
                }
            }
            return true;
        }

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
            if (!isWideScreen() || !z2) {
                return;
            }
            updateSize();
        }

        @SuppressLint({"RtlHardcoded"})
        public boolean isNeedSwipeAlphaAnimation(boolean z) {
            if (!isWideScreen() || this.wideScreenWidth == -1) {
                return false;
            }
            int i = this.wideScreenGravity;
            if (i == 1) {
                return true;
            }
            return z ? i == 5 : i != 5;
        }

        public Bulletin getBulletin() {
            return this.bulletin;
        }

        protected void onAttach(Bulletin bulletin) {
            this.bulletin = bulletin;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onAttach(this, bulletin);
            }
        }

        protected void onDetach() {
            this.bulletin = null;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onDetach(this);
            }
        }

        protected void onShow() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onShow(this);
            }
        }

        protected void onHide() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onHide(this);
            }
        }

        public void onEnterTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionStart(this);
            }
        }

        public void onEnterTransitionEnd() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionEnd(this);
            }
        }

        public void onExitTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onExitTransitionStart(this);
            }
        }

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
            Delegate delegate = this.delegate;
            float f = 0.0f;
            if (delegate != null) {
                int i = 0;
                if (this.top) {
                    Bulletin bulletin = this.bulletin;
                    if (bulletin != null) {
                        i = bulletin.tag;
                    }
                    f = 0.0f - delegate.getTopOffset(i);
                } else {
                    Bulletin bulletin2 = this.bulletin;
                    if (bulletin2 != null) {
                        i = bulletin2.tag;
                    }
                    f = 0.0f + delegate.getBottomOffset(i);
                }
            }
            setTranslationY((-f) + (this.inOutOffset * (this.top ? -1 : 1)));
        }

        public Transition createTransition() {
            return new SpringTransition();
        }

        /* loaded from: classes3.dex */
        public static class DefaultTransition implements Transition {
            long duration = 255;

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateEnter(final Layout layout, final Runnable runnable, final Runnable runnable2, final Consumer<Float> consumer, int i) {
                layout.setInOutOffset(layout.getMeasuredHeight());
                if (consumer != null) {
                    consumer.accept(Float.valueOf(layout.getTranslationY()));
                }
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, 0.0f);
                ofFloat.setDuration(this.duration);
                ofFloat.setInterpolator(Easings.easeOutQuad);
                if (runnable != null || runnable2 != null) {
                    ofFloat.addListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            Runnable runnable3 = runnable;
                            if (runnable3 != null) {
                                runnable3.run();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            Runnable runnable3 = runnable2;
                            if (runnable3 != null) {
                                runnable3.run();
                            }
                        }
                    });
                }
                if (consumer != null) {
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Bulletin.Layout.DefaultTransition.lambda$animateEnter$0(Consumer.this, layout, valueAnimator);
                        }
                    });
                }
                ofFloat.start();
            }

            public static /* synthetic */ void lambda$animateEnter$0(Consumer consumer, Layout layout, ValueAnimator valueAnimator) {
                consumer.accept(Float.valueOf(layout.getTranslationY()));
            }

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateExit(final Layout layout, final Runnable runnable, final Runnable runnable2, final Consumer<Float> consumer, int i) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, layout.getHeight());
                ofFloat.setDuration(175L);
                ofFloat.setInterpolator(Easings.easeInQuad);
                if (runnable != null || runnable2 != null) {
                    ofFloat.addListener(new AnimatorListenerAdapter(this) { // from class: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            Runnable runnable3 = runnable;
                            if (runnable3 != null) {
                                runnable3.run();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            Runnable runnable3 = runnable2;
                            if (runnable3 != null) {
                                runnable3.run();
                            }
                        }
                    });
                }
                if (consumer != null) {
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Bulletin.Layout.DefaultTransition.lambda$animateExit$1(Consumer.this, layout, valueAnimator);
                        }
                    });
                }
                ofFloat.start();
            }

            public static /* synthetic */ void lambda$animateExit$1(Consumer consumer, Layout layout, ValueAnimator valueAnimator) {
                consumer.accept(Float.valueOf(layout.getTranslationY()));
            }
        }

        /* loaded from: classes3.dex */
        public static class SpringTransition implements Transition {
            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateEnter(final Layout layout, Runnable runnable, final Runnable runnable2, final Consumer<Float> consumer, int i) {
                layout.setInOutOffset(layout.getMeasuredHeight());
                if (consumer != null) {
                    consumer.accept(Float.valueOf(layout.getTranslationY()));
                }
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, 0.0f);
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (runnable2 != null) {
                    springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateEnter$0(Bulletin.Layout.this, runnable2, dynamicAnimation, z, f, f2);
                        }
                    });
                }
                if (consumer != null) {
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateEnter$1(Consumer.this, layout, dynamicAnimation, f, f2);
                        }
                    });
                }
                springAnimation.start();
                if (runnable != null) {
                    runnable.run();
                }
            }

            public static /* synthetic */ void lambda$animateEnter$0(Layout layout, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                layout.setInOutOffset(0.0f);
                if (!z) {
                    runnable.run();
                }
            }

            public static /* synthetic */ void lambda$animateEnter$1(Consumer consumer, Layout layout, DynamicAnimation dynamicAnimation, float f, float f2) {
                consumer.accept(Float.valueOf(layout.getTranslationY()));
            }

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateExit(final Layout layout, Runnable runnable, final Runnable runnable2, final Consumer<Float> consumer, int i) {
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, layout.getHeight());
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (runnable2 != null) {
                    springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateExit$2(runnable2, dynamicAnimation, z, f, f2);
                        }
                    });
                }
                if (consumer != null) {
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda2
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateExit$3(Consumer.this, layout, dynamicAnimation, f, f2);
                        }
                    });
                }
                springAnimation.start();
                if (runnable != null) {
                    runnable.run();
                }
            }

            public static /* synthetic */ void lambda$animateExit$2(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                if (!z) {
                    runnable.run();
                }
            }

            public static /* synthetic */ void lambda$animateExit$3(Consumer consumer, Layout layout, DynamicAnimation dynamicAnimation, float f, float f2) {
                consumer.accept(Float.valueOf(layout.getTranslationY()));
            }
        }

        public void setInOutOffset(float f) {
            this.inOutOffset = f;
            updatePosition();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (this.bulletin == null) {
                return;
            }
            this.background.setBounds(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), getMeasuredWidth() - AndroidUtilities.dp(8.0f), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
            if (isTransitionRunning() && this.delegate != null) {
                canvas.save();
                canvas.clipRect(0.0f, this.delegate.getTopOffset(this.bulletin.tag) - getY(), getMeasuredWidth(), (((View) getParent()).getMeasuredHeight() - this.delegate.getBottomOffset(this.bulletin.tag)) - getY());
                this.background.draw(canvas);
                super.dispatchDraw(canvas);
                canvas.restore();
                invalidate();
                return;
            }
            this.background.draw(canvas);
            super.dispatchDraw(canvas);
        }

        public int getThemedColor(String str) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    @SuppressLint({"ViewConstructor"})
    /* loaded from: classes3.dex */
    public static class ButtonLayout extends Layout {
        private Button button;
        private int childrenMeasuredWidth;

        public ButtonLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            this.childrenMeasuredWidth = 0;
            super.onMeasure(i, i2);
            if (this.button == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
                return;
            }
            setMeasuredDimension(this.childrenMeasuredWidth + this.button.getMeasuredWidth(), getMeasuredHeight());
        }

        @Override // android.view.ViewGroup
        protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
            Button button = this.button;
            if (button != null && view != button) {
                i2 += button.getMeasuredWidth() - AndroidUtilities.dp(12.0f);
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

        public void setButton(Button button) {
            Button button2 = this.button;
            if (button2 != null) {
                removeCallback(button2);
                removeView(this.button);
            }
            this.button = button;
            if (button != null) {
                addCallback(button);
                addView(button, 0, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388629));
            }
        }
    }

    /* loaded from: classes3.dex */
    public static class SimpleLayout extends ButtonLayout {
        public final ImageView imageView;
        public final TextView textView;

        public SimpleLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int themedColor = getThemedColor("undo_infoColor");
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setSingleLine();
            textView.setTextColor(themedColor);
            textView.setTypeface(Typeface.SANS_SERIF);
            textView.setTextSize(1, 15.0f);
            addView(textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    @SuppressLint({"ViewConstructor"})
    /* loaded from: classes3.dex */
    public static class MultiLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView textView;

        public MultiLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            TextView textView = new TextView(getContext());
            this.textView = textView;
            addView(backupImageView, LayoutHelper.createFrameRelatively(30.0f, 30.0f, 8388627, 12.0f, 8.0f, 12.0f, 8.0f));
            textView.setGravity(8388611);
            textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            textView.setTextColor(getThemedColor("undo_infoColor"));
            textView.setTextSize(1, 15.0f);
            textView.setTypeface(Typeface.SANS_SERIF);
            addView(textView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    @SuppressLint({"ViewConstructor"})
    /* loaded from: classes3.dex */
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
            textView2.setLinkTextColor(getThemedColor("undo_cancelColor"));
            textView2.setMovementMethod(new LinkMovementMethod());
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return ((Object) this.titleTextView.getText()) + ".\n" + ((Object) this.subtitleTextView.getText());
        }
    }

    /* loaded from: classes3.dex */
    public static class TwoLineLottieLayout extends ButtonLayout {
        public final RLottieImageView imageView;
        public final TextView subtitleTextView;
        private final int textColor;
        public final TextView titleTextView;

        public TwoLineLottieLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.textColor = getThemedColor("undo_infoColor");
            setBackground(getThemedColor("undo_background"));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(rLottieImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
            int themedColor = getThemedColor("undo_infoColor");
            int themedColor2 = getThemedColor("voipgroup_overlayBlue1");
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
            textView2.setLinkTextColor(themedColor2);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        protected void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int i, String... strArr) {
            setAnimation(i, 32, 32, strArr);
        }

        public void setAnimation(int i, int i2, int i3, String... strArr) {
            this.imageView.setAnimation(i, i2, i3);
            for (String str : strArr) {
                this.imageView.setLayerColor(str + ".**", this.textColor);
            }
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return ((Object) this.titleTextView.getText()) + ".\n" + ((Object) this.subtitleTextView.getText());
        }
    }

    /* loaded from: classes3.dex */
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
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.textView = linksTextView;
            linksTextView.setSingleLine();
            this.textView.setTypeface(Typeface.SANS_SERIF);
            this.textView.setTextSize(1, 15.0f);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 8.0f, 0.0f));
            this.textView.setLinkTextColor(getThemedColor("undo_cancelColor"));
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

        @Override // org.telegram.ui.Components.Bulletin.Layout
        protected void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int i, String... strArr) {
            setAnimation(i, 32, 32, strArr);
        }

        public void setAnimation(int i, int i2, int i3, String... strArr) {
            this.imageView.setAnimation(i, i2, i3);
            for (String str : strArr) {
                this.imageView.setLayerColor(str + ".**", this.textColor);
            }
        }

        public void setAnimation(TLRPC$Document tLRPC$Document, int i, int i2, String... strArr) {
            this.imageView.setAnimation(tLRPC$Document, i, i2);
            for (String str : strArr) {
                this.imageView.setLayerColor(str + ".**", this.textColor);
            }
        }

        public void setIconPaddingBottom(int i) {
            this.imageView.setLayoutParams(LayoutHelper.createFrameRelatively(56.0f, 48 - i, 8388627, 0.0f, 0.0f, 0.0f, i));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    /* loaded from: classes3.dex */
    public static class UsersLayout extends ButtonLayout {
        public AvatarsImageView avatarsImageView;
        public TextView textView;

        public UsersLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
            this.avatarsImageView = avatarsImageView;
            avatarsImageView.setStyle(11);
            addView(this.avatarsImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627, 12.0f, 0.0f, 0.0f, 0.0f));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.textView = linksTextView;
            linksTextView.setSingleLine();
            this.textView.setTypeface(Typeface.SANS_SERIF);
            this.textView.setTextSize(1, 15.0f);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 70.0f, 0.0f, 8.0f, 0.0f));
            this.textView.setLinkTextColor(getThemedColor("undo_cancelColor"));
            setTextColor(getThemedColor("undo_infoColor"));
            setBackground(getThemedColor("undo_background"));
        }

        public void setTextColor(int i) {
            this.textView.setTextColor(i);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        protected void onShow() {
            super.onShow();
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    @SuppressLint({"ViewConstructor"})
    /* loaded from: classes3.dex */
    public static abstract class Button extends FrameLayout implements Layout.Callback {
        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onEnterTransitionEnd(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onEnterTransitionStart(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onExitTransitionEnd(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onExitTransitionStart(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onHide(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onShow(Layout layout) {
        }

        public Button(Context context) {
            super(context);
        }
    }

    @SuppressLint({"ViewConstructor"})
    /* loaded from: classes3.dex */
    public static final class UndoButton extends Button {
        private Bulletin bulletin;
        private Runnable delayedAction;
        private boolean isUndone;
        private final Theme.ResourcesProvider resourcesProvider;
        private Runnable undoAction;
        private TextView undoTextView;

        public UndoButton(Context context, boolean z) {
            this(context, z, null);
        }

        public UndoButton(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            int themedColor = getThemedColor("undo_cancelColor");
            if (z) {
                TextView textView = new TextView(context);
                this.undoTextView = textView;
                textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Bulletin$UndoButton$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        Bulletin.UndoButton.this.lambda$new$0(view);
                    }
                });
                this.undoTextView.setBackground(Theme.createCircleSelectorDrawable(NUM | (16777215 & themedColor), LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0, !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0));
                this.undoTextView.setTextSize(1, 14.0f);
                this.undoTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.undoTextView.setTextColor(themedColor);
                this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
                this.undoTextView.setGravity(16);
                ViewHelper.setPaddingRelative(this.undoTextView, 16.0f, 0.0f, 16.0f, 0.0f);
                addView(this.undoTextView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
                return;
            }
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Bulletin$UndoButton$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    Bulletin.UndoButton.this.lambda$new$1(view);
                }
            });
            imageView.setImageResource(R.drawable.chats_undo);
            imageView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
            imageView.setBackground(Theme.createSelectorDrawable((themedColor & 16777215) | NUM));
            ViewHelper.setPaddingRelative(imageView, 0.0f, 12.0f, 0.0f, 12.0f);
            addView(imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
        }

        public /* synthetic */ void lambda$new$0(View view) {
            undo();
        }

        public /* synthetic */ void lambda$new$1(View view) {
            undo();
        }

        public UndoButton setText(CharSequence charSequence) {
            TextView textView = this.undoTextView;
            if (textView != null) {
                textView.setText(charSequence);
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

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onAttach(Layout layout, Bulletin bulletin) {
            this.bulletin = bulletin;
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onDetach(Layout layout) {
            this.bulletin = null;
            Runnable runnable = this.delayedAction;
            if (runnable == null || this.isUndone) {
                return;
            }
            runnable.run();
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
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(str) : null;
            return color != null ? color.intValue() : Theme.getColor(str);
        }
    }

    /* loaded from: classes3.dex */
    public static class EmptyBulletin extends Bulletin {
        @Override // org.telegram.ui.Components.Bulletin
        public Bulletin show() {
            return this;
        }

        public EmptyBulletin() {
            super();
        }
    }
}
