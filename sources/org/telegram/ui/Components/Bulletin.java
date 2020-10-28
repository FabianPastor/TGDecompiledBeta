package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.core.util.Preconditions;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

public final class Bulletin {
    /* access modifiers changed from: private */
    public static final HashMap<FrameLayout, Delegate> delegates = new HashMap<>();
    @SuppressLint({"StaticFieldLeak"})
    private static Bulletin visibleBulletin;
    private boolean canHide;
    /* access modifiers changed from: private */
    public final FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public int currentBottomOffset;
    /* access modifiers changed from: private */
    public Delegate currentDelegate;
    private final int duration;
    private final Runnable hideRunnable = new Runnable() {
        public final void run() {
            Bulletin.this.hide();
        }
    };
    /* access modifiers changed from: private */
    public final Layout layout;
    /* access modifiers changed from: private */
    public Layout.Transition layoutTransition;
    /* access modifiers changed from: private */
    public final ParentLayout parentLayout;
    /* access modifiers changed from: private */
    public boolean showing;

    public interface Delegate {

        /* renamed from: org.telegram.ui.Components.Bulletin$Delegate$-CC  reason: invalid class name */
        public final /* synthetic */ class CC {
            public static int $default$getBottomOffset(Delegate delegate) {
                return 0;
            }

            public static void $default$onOffsetChange(Delegate delegate, float f) {
            }
        }

        int getBottomOffset();

        void onOffsetChange(float f);
    }

    public static Bulletin make(FrameLayout frameLayout, Layout layout2, int i) {
        return new Bulletin(frameLayout, layout2, i);
    }

    public static Bulletin make(BaseFragment baseFragment, Layout layout2, int i) {
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
            find.hide(z && isTransitionsEnabled());
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
        };
        this.containerLayout = frameLayout;
        this.duration = i;
    }

    public Bulletin show() {
        if (!this.showing) {
            boolean z = true;
            this.showing = true;
            if (this.layout.getParent() != this.parentLayout) {
                z = false;
            }
            Preconditions.checkState(z, "Layout has incorrect parent");
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
                        int unused2 = bulletin.currentBottomOffset = bulletin.currentDelegate != null ? Bulletin.this.currentDelegate.getBottomOffset() : 0;
                        if (Bulletin.isTransitionsEnabled()) {
                            if (Bulletin.this.currentBottomOffset != 0) {
                                ViewCompat.setClipBounds(Bulletin.this.parentLayout, new Rect(i, i2 - Bulletin.this.currentBottomOffset, i3, i4 - Bulletin.this.currentBottomOffset));
                            } else {
                                ViewCompat.setClipBounds(Bulletin.this.parentLayout, (Rect) null);
                            }
                            Bulletin.this.ensureLayoutTransitionCreated();
                            Layout.Transition access$1000 = Bulletin.this.layoutTransition;
                            Layout access$100 = Bulletin.this.layout;
                            Layout access$1002 = Bulletin.this.layout;
                            access$1002.getClass();
                            access$1000.animateEnter(access$100, new Runnable() {
                                public final void run() {
                                    Bulletin.Layout.this.onEnterTransitionStart();
                                }
                            }, new Runnable() {
                                public final void run() {
                                    Bulletin.AnonymousClass2.this.lambda$onLayoutChange$0$Bulletin$2();
                                }
                            }, new Consumer() {
                                public final void accept(Object obj) {
                                    Bulletin.AnonymousClass2.this.lambda$onLayoutChange$1$Bulletin$2((Float) obj);
                                }
                            }, Bulletin.this.currentBottomOffset);
                            return;
                        }
                        if (Bulletin.this.currentDelegate != null) {
                            Bulletin.this.currentDelegate.onOffsetChange((float) (Bulletin.this.layout.getHeight() - Bulletin.this.currentBottomOffset));
                        }
                        Bulletin.this.layout.setTranslationY((float) (-Bulletin.this.currentBottomOffset));
                        Bulletin.this.layout.onEnterTransitionStart();
                        Bulletin.this.layout.onEnterTransitionEnd();
                        Bulletin.this.setCanHide(true);
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onLayoutChange$0 */
                public /* synthetic */ void lambda$onLayoutChange$0$Bulletin$2() {
                    Bulletin.this.layout.onEnterTransitionEnd();
                    Bulletin.this.setCanHide(true);
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$onLayoutChange$1 */
                public /* synthetic */ void lambda$onLayoutChange$1$Bulletin$2(Float f) {
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
                    Bulletin.this.hide(false);
                }
            });
            this.containerLayout.addView(this.parentLayout);
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
        hide(isTransitionsEnabled());
    }

    public void hide(boolean z) {
        if (this.showing && this.canHide) {
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            int i = this.currentBottomOffset;
            this.currentBottomOffset = 0;
            if (ViewCompat.isLaidOut(this.layout)) {
                this.layout.removeCallbacks(this.hideRunnable);
                if (z) {
                    ensureLayoutTransitionCreated();
                    Layout.Transition transition = this.layoutTransition;
                    Layout layout2 = this.layout;
                    layout2.getClass();
                    transition.animateExit(layout2, new Runnable() {
                        public final void run() {
                            Bulletin.Layout.this.onExitTransitionStart();
                        }
                    }, new Runnable() {
                        public final void run() {
                            Bulletin.this.lambda$hide$0$Bulletin();
                        }
                    }, new Consumer() {
                        public final void accept(Object obj) {
                            Bulletin.this.lambda$hide$1$Bulletin((Float) obj);
                        }
                    }, i);
                    return;
                }
            }
            Delegate delegate = this.currentDelegate;
            if (delegate != null) {
                delegate.onOffsetChange(0.0f);
            }
            this.layout.onExitTransitionStart();
            this.layout.onExitTransitionEnd();
            this.layout.onHide();
            FrameLayout frameLayout = this.containerLayout;
            if (frameLayout != null) {
                frameLayout.removeView(this.parentLayout);
            }
            this.layout.onDetach();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hide$0 */
    public /* synthetic */ void lambda$hide$0$Bulletin() {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(0.0f);
        }
        this.layout.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.parentLayout);
        this.layout.onDetach();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hide$1 */
    public /* synthetic */ void lambda$hide$1$Bulletin(Float f) {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(((float) this.layout.getHeight()) - f.floatValue());
        }
    }

    /* access modifiers changed from: private */
    public static boolean isTransitionsEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && Build.VERSION.SDK_INT >= 18;
    }

    private static abstract class ParentLayout extends FrameLayout {
        private final GestureDetector gestureDetector;
        private final Layout layout;
        private boolean pressed;
        private final Rect rect = new Rect();
        /* access modifiers changed from: private */
        public SpringAnimation springAnimation;
        /* access modifiers changed from: private */
        public float translationX;

        /* access modifiers changed from: protected */
        public abstract void onPressedStateChanged(boolean z);

        public ParentLayout(final Layout layout2) {
            super(layout2.getContext());
            this.layout = layout2;
            GestureDetector gestureDetector2 = new GestureDetector(layout2.getContext(), new GestureDetector.SimpleOnGestureListener() {
                public boolean onDown(MotionEvent motionEvent) {
                    return ParentLayout.this.springAnimation == null;
                }

                public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    Layout layout = layout2;
                    ParentLayout parentLayout = ParentLayout.this;
                    float access$1200 = parentLayout.translationX - f;
                    float unused = parentLayout.translationX = access$1200;
                    layout.setTranslationX(access$1200);
                    return true;
                }

                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    if (Math.abs(f) <= 2000.0f) {
                        return false;
                    }
                    SpringAnimation unused = ParentLayout.this.springAnimation = new SpringAnimation(layout2, DynamicAnimation.TRANSLATION_X, Math.signum(f) * ((float) layout2.getWidth()) * 2.0f);
                    ParentLayout.this.springAnimation.getSpring().setDampingRatio(1.0f);
                    ParentLayout.this.springAnimation.getSpring().setStiffness(100.0f);
                    ParentLayout.this.springAnimation.setStartVelocity(f);
                    ParentLayout.this.springAnimation.start();
                    return true;
                }
            });
            this.gestureDetector = gestureDetector2;
            gestureDetector2.setIsLongpressEnabled(false);
            addView(layout2, LayoutHelper.createFrame(-1, -2, 80));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!this.pressed && !inLayoutHitRect(motionEvent.getX(), motionEvent.getY())) {
                return false;
            }
            this.gestureDetector.onTouchEvent(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                if (!this.pressed && this.springAnimation == null) {
                    this.layout.animate().cancel();
                    this.translationX = this.layout.getTranslationX();
                    this.pressed = true;
                    onPressedStateChanged(true);
                }
            } else if ((actionMasked == 1 || actionMasked == 3) && this.pressed) {
                if (this.springAnimation == null) {
                    if (Math.abs(this.translationX) > ((float) (this.layout.getWidth() / 2))) {
                        this.layout.animate().translationX(Math.signum(this.translationX) * ((float) this.layout.getWidth())).setDuration(200).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
                    } else {
                        this.layout.animate().translationX(0.0f).setDuration(200).start();
                    }
                }
                this.pressed = false;
                onPressedStateChanged(false);
            }
            return true;
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
        protected Bulletin bulletin;
        private final List<Callback> callbacks;

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

        public Layout(Context context) {
            this(context, Theme.getColor("undo_background"));
        }

        public Layout(Context context, int i) {
            super(context);
            this.callbacks = new ArrayList();
            setMinimumHeight(AndroidUtilities.dp(48.0f));
            setBackground(new InsetDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), i), AndroidUtilities.dp(8.0f)));
            updateSize();
        }

        /* access modifiers changed from: protected */
        public void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            updateSize();
        }

        private void updateSize() {
            Point point = AndroidUtilities.displaySize;
            boolean z = true;
            int i = 0;
            boolean z2 = point.x < point.y;
            if (AndroidUtilities.isTablet() || !z2) {
                z = false;
            }
            if (!z) {
                i = AndroidUtilities.dp(344.0f);
            }
            setMinimumWidth(i);
            setLayoutParams(LayoutHelper.createFrame(z ? -1 : -2, -2, 81));
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

        public Transition createTransition() {
            return new SpringTransition();
        }

        public static class SpringTransition implements Transition {
            public void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i) {
                float height = (float) (layout.getHeight() - i);
                layout.setTranslationY(height);
                consumer.accept(Float.valueOf(height));
                SpringAnimation springAnimation = new SpringAnimation(layout, DynamicAnimation.TRANSLATION_Y, (float) (-i));
                springAnimation.getSpring().setDampingRatio(0.8f);
                springAnimation.getSpring().setStiffness(400.0f);
                if (runnable2 != null) {
                    springAnimation.addEndListener(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0033: INVOKE  
                          (r0v4 'springAnimation' androidx.dynamicanimation.animation.SpringAnimation)
                          (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU : 0x0030: CONSTRUCTOR  (r3v4 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU) = (r5v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU.<init>(java.lang.Runnable):void type: CONSTRUCTOR)
                         androidx.dynamicanimation.animation.DynamicAnimation.addEndListener(androidx.dynamicanimation.animation.DynamicAnimation$OnAnimationEndListener):androidx.dynamicanimation.animation.DynamicAnimation type: VIRTUAL in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                        	at java.util.ArrayList.forEach(ArrayList.java:1259)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0030: CONSTRUCTOR  (r3v4 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU) = (r5v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU.<init>(java.lang.Runnable):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 64 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 70 more
                        */
                    /*
                        this = this;
                        int r0 = r3.getHeight()
                        int r0 = r0 - r7
                        float r0 = (float) r0
                        r3.setTranslationY(r0)
                        java.lang.Float r0 = java.lang.Float.valueOf(r0)
                        r6.accept(r0)
                        androidx.dynamicanimation.animation.SpringAnimation r0 = new androidx.dynamicanimation.animation.SpringAnimation
                        androidx.dynamicanimation.animation.DynamicAnimation$ViewProperty r1 = androidx.dynamicanimation.animation.DynamicAnimation.TRANSLATION_Y
                        int r7 = -r7
                        float r7 = (float) r7
                        r0.<init>(r3, r1, r7)
                        androidx.dynamicanimation.animation.SpringForce r3 = r0.getSpring()
                        r7 = 1061997773(0x3f4ccccd, float:0.8)
                        r3.setDampingRatio(r7)
                        androidx.dynamicanimation.animation.SpringForce r3 = r0.getSpring()
                        r7 = 1137180672(0x43CLASSNAME, float:400.0)
                        r3.setStiffness(r7)
                        if (r5 == 0) goto L_0x0036
                        org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU r3 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$2Pc9D8px-PbkM4NQCBWeYRI_LcU
                        r3.<init>(r5)
                        r0.addEndListener(r3)
                    L_0x0036:
                        if (r6 == 0) goto L_0x0040
                        org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$yAIzcIUeM0Hya8bGgnNWpp7S4UI r3 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$yAIzcIUeM0Hya8bGgnNWpp7S4UI
                        r3.<init>(r6)
                        r0.addUpdateListener(r3)
                    L_0x0040:
                        r0.start()
                        if (r4 == 0) goto L_0x0048
                        r4.run()
                    L_0x0048:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void");
                }

                static /* synthetic */ void lambda$animateEnter$0(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                    if (!z) {
                        runnable.run();
                    }
                }

                public void animateExit(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i) {
                    SpringAnimation springAnimation = new SpringAnimation(layout, DynamicAnimation.TRANSLATION_Y, (float) (layout.getHeight() - i));
                    springAnimation.getSpring().setDampingRatio(0.8f);
                    springAnimation.getSpring().setStiffness(400.0f);
                    if (runnable2 != null) {
                        springAnimation.addEndListener(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0027: INVOKE  
                              (r0v0 'springAnimation' androidx.dynamicanimation.animation.SpringAnimation)
                              (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI : 0x0024: CONSTRUCTOR  (r4v4 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI) = (r6v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI.<init>(java.lang.Runnable):void type: CONSTRUCTOR)
                             androidx.dynamicanimation.animation.DynamicAnimation.addEndListener(androidx.dynamicanimation.animation.DynamicAnimation$OnAnimationEndListener):androidx.dynamicanimation.animation.DynamicAnimation type: VIRTUAL in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                            	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                            	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                            	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                            	at java.util.ArrayList.forEach(ArrayList.java:1259)
                            	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                            	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                            	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                            	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                            	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                            	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                            	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                            	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                            	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0024: CONSTRUCTOR  (r4v4 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI) = (r6v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI.<init>(java.lang.Runnable):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 64 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 70 more
                            */
                        /*
                            this = this;
                            androidx.dynamicanimation.animation.SpringAnimation r0 = new androidx.dynamicanimation.animation.SpringAnimation
                            androidx.dynamicanimation.animation.DynamicAnimation$ViewProperty r1 = androidx.dynamicanimation.animation.DynamicAnimation.TRANSLATION_Y
                            int r2 = r4.getHeight()
                            int r2 = r2 - r8
                            float r8 = (float) r2
                            r0.<init>(r4, r1, r8)
                            androidx.dynamicanimation.animation.SpringForce r4 = r0.getSpring()
                            r8 = 1061997773(0x3f4ccccd, float:0.8)
                            r4.setDampingRatio(r8)
                            androidx.dynamicanimation.animation.SpringForce r4 = r0.getSpring()
                            r8 = 1137180672(0x43CLASSNAME, float:400.0)
                            r4.setStiffness(r8)
                            if (r6 == 0) goto L_0x002a
                            org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI r4 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI
                            r4.<init>(r6)
                            r0.addEndListener(r4)
                        L_0x002a:
                            if (r7 == 0) goto L_0x0034
                            org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$nU9LcFRMmlWxNVTPxWrA096Aif0 r4 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$nU9LcFRMmlWxNVTPxWrA096Aif0
                            r4.<init>(r7)
                            r0.addUpdateListener(r4)
                        L_0x0034:
                            r0.start()
                            if (r5 == 0) goto L_0x003c
                            r5.run()
                        L_0x003c:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void");
                    }

                    static /* synthetic */ void lambda$animateExit$2(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        if (!z) {
                            runnable.run();
                        }
                    }
                }
            }

            @SuppressLint({"ViewConstructor"})
            public static class ButtonLayout extends Layout {
                private Button button;
                private int childrenMeasuredWidth;

                public ButtonLayout(Context context) {
                    super(context);
                }

                public ButtonLayout(Context context, int i) {
                    super(context, i);
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

                public SimpleLayout(Context context) {
                    super(context);
                    int color = Theme.getColor("undo_infoColor");
                    ImageView imageView2 = new ImageView(context);
                    this.imageView = imageView2;
                    imageView2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    addView(imageView2, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
                    TextView textView2 = new TextView(context);
                    this.textView = textView2;
                    textView2.setSingleLine();
                    textView2.setTextColor(color);
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

                public TwoLineLayout(Context context) {
                    super(context);
                    int color = Theme.getColor("undo_infoColor");
                    BackupImageView backupImageView = new BackupImageView(context);
                    this.imageView = backupImageView;
                    addView(backupImageView, LayoutHelper.createFrameRelatively(29.0f, 29.0f, 8388627, 12.0f, 12.0f, 12.0f, 12.0f));
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(1);
                    addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 54.0f, 8.0f, 12.0f, 8.0f));
                    TextView textView = new TextView(context);
                    this.titleTextView = textView;
                    textView.setSingleLine();
                    textView.setTextColor(color);
                    textView.setTextSize(1, 14.0f);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(textView);
                    TextView textView2 = new TextView(context);
                    this.subtitleTextView = textView2;
                    textView2.setMaxLines(2);
                    textView2.setTextColor(color);
                    textView2.setTypeface(Typeface.SANS_SERIF);
                    textView2.setTextSize(1, 13.0f);
                    linearLayout.addView(textView2);
                }
            }

            public static class TwoLineLottieLayout extends ButtonLayout {
                public final RLottieImageView imageView;
                public final TextView subtitleTextView;
                private final int textColor;
                public final TextView titleTextView;

                public TwoLineLottieLayout(Context context) {
                    this(context, Theme.getColor("undo_background"), Theme.getColor("undo_infoColor"));
                }

                public TwoLineLottieLayout(Context context, int i, int i2) {
                    super(context, i);
                    this.textColor = i2;
                    RLottieImageView rLottieImageView = new RLottieImageView(context);
                    this.imageView = rLottieImageView;
                    addView(rLottieImageView, LayoutHelper.createFrameRelatively(28.0f, 28.0f, 8388627, 14.0f, 10.0f, 14.0f, 10.0f));
                    int color = Theme.getColor("undo_infoColor");
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(1);
                    addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 54.0f, 8.0f, 12.0f, 8.0f));
                    TextView textView = new TextView(context);
                    this.titleTextView = textView;
                    textView.setSingleLine();
                    textView.setTextColor(color);
                    textView.setTextSize(1, 14.0f);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    linearLayout.addView(textView);
                    TextView textView2 = new TextView(context);
                    this.subtitleTextView = textView2;
                    textView2.setMaxLines(2);
                    textView2.setTextColor(color);
                    textView2.setTypeface(Typeface.SANS_SERIF);
                    textView2.setTextSize(1, 13.0f);
                    linearLayout.addView(textView2);
                }

                /* access modifiers changed from: protected */
                public void onShow() {
                    super.onShow();
                    this.imageView.playAnimation();
                }

                public void setAnimation(int i, String... strArr) {
                    this.imageView.setAnimation(i, 28, 28);
                    for (int i2 = 0; i2 < strArr.length; i2++) {
                        RLottieImageView rLottieImageView = this.imageView;
                        rLottieImageView.setLayerColor(strArr[i2] + ".**", this.textColor);
                    }
                }
            }

            public static class LottieLayout extends ButtonLayout {
                public final RLottieImageView imageView;
                private final int textColor;
                public final TextView textView;

                public LottieLayout(Context context) {
                    this(context, Theme.getColor("undo_background"), Theme.getColor("undo_infoColor"));
                }

                public LottieLayout(Context context, int i, int i2) {
                    super(context, i);
                    this.textColor = i2;
                    RLottieImageView rLottieImageView = new RLottieImageView(context);
                    this.imageView = rLottieImageView;
                    addView(rLottieImageView, LayoutHelper.createFrameRelatively(28.0f, 28.0f, 8388627, 14.0f, 10.0f, 14.0f, 10.0f));
                    TextView textView2 = new TextView(context);
                    this.textView = textView2;
                    textView2.setSingleLine();
                    textView2.setTextColor(i2);
                    textView2.setTypeface(Typeface.SANS_SERIF);
                    textView2.setTextSize(1, 15.0f);
                    addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
                }

                /* access modifiers changed from: protected */
                public void onShow() {
                    super.onShow();
                    this.imageView.playAnimation();
                }

                public void setAnimation(int i, String... strArr) {
                    this.imageView.setAnimation(i, 28, 28);
                    for (int i2 = 0; i2 < strArr.length; i2++) {
                        RLottieImageView rLottieImageView = this.imageView;
                        rLottieImageView.setLayerColor(strArr[i2] + ".**", this.textColor);
                    }
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

            public static final class UndoButton extends Button {
                private Bulletin bulletin;
                private Runnable delayedAction;
                private boolean isUndone;
                private Runnable undoAction;

                public UndoButton(Context context, boolean z) {
                    super(context);
                    int color = Theme.getColor("undo_cancelColor");
                    if (z) {
                        TextView textView = new TextView(context);
                        textView.setOnClickListener(new View.OnClickListener() {
                            public final void onClick(View view) {
                                Bulletin.UndoButton.this.lambda$new$0$Bulletin$UndoButton(view);
                            }
                        });
                        textView.setBackground(Theme.createSelectorDrawable((color & 16777215) | NUM, 7));
                        textView.setTextSize(1, 14.0f);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                        textView.setTextColor(color);
                        textView.setText(LocaleController.getString("Undo", NUM));
                        textView.setGravity(16);
                        ViewHelper.setPaddingRelative(textView, 16.0f, 0.0f, 16.0f, 0.0f);
                        addView(textView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
                        return;
                    }
                    ImageView imageView = new ImageView(getContext());
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            Bulletin.UndoButton.this.lambda$new$1$Bulletin$UndoButton(view);
                        }
                    });
                    imageView.setImageResource(NUM);
                    imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                    imageView.setBackground(Theme.createSelectorDrawable((color & 16777215) | NUM));
                    ViewHelper.setPaddingRelative(imageView, 0.0f, 12.0f, 0.0f, 12.0f);
                    addView(imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$new$0 */
                public /* synthetic */ void lambda$new$0$Bulletin$UndoButton(View view) {
                    undo();
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$new$1 */
                public /* synthetic */ void lambda$new$1$Bulletin$UndoButton(View view) {
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
            }
        }
