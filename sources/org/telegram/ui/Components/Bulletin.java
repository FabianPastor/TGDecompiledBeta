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
import org.telegram.ui.Components.Bulletin;
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
    private final Runnable hideRunnable = new Runnable() {
        public final void run() {
            Bulletin.this.hide();
        }
    };
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
                                access$900.animateEnter(access$200, new Runnable() {
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
                            Bulletin.this.updatePosition();
                            Bulletin.this.layout.onEnterTransitionStart();
                            Bulletin.this.layout.onEnterTransitionEnd();
                            Bulletin.this.setCanHide(true);
                        }
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onLayoutChange$0 */
                    public /* synthetic */ void lambda$onLayoutChange$0$Bulletin$2() {
                        Bulletin.this.layout.transitionRunning = false;
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
                    transition.animateExit(layout3, new Runnable() {
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
                this.currentDelegate.onHide(this);
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
    /* renamed from: lambda$hide$1 */
    public /* synthetic */ void lambda$hide$1$Bulletin(Float f) {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(((float) this.layout.getHeight()) - f.floatValue());
        }
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
                    Layout layout = layout2;
                    ParentLayout parentLayout = ParentLayout.this;
                    float access$1400 = parentLayout.translationX - f;
                    float unused = parentLayout.translationX = access$1400;
                    layout.setTranslationX(access$1400);
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
                        springAnimation.addEndListener(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0047: INVOKE  
                              (r0v1 'springAnimation' androidx.dynamicanimation.animation.SpringAnimation)
                              (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o : 0x0044: CONSTRUCTOR  (r1v5 org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o) = 
                              (r5v0 'this' org.telegram.ui.Components.Bulletin$ParentLayout$1 A[THIS])
                             call: org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o.<init>(org.telegram.ui.Components.Bulletin$ParentLayout$1):void type: CONSTRUCTOR)
                             androidx.dynamicanimation.animation.DynamicAnimation.addEndListener(androidx.dynamicanimation.animation.DynamicAnimation$OnAnimationEndListener):androidx.dynamicanimation.animation.DynamicAnimation type: VIRTUAL in method: org.telegram.ui.Components.Bulletin.ParentLayout.1.onFling(android.view.MotionEvent, android.view.MotionEvent, float, float):boolean, dex: classes3.dex
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
                            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
                            	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:640)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
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
                            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0044: CONSTRUCTOR  (r1v5 org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o) = 
                              (r5v0 'this' org.telegram.ui.Components.Bulletin$ParentLayout$1 A[THIS])
                             call: org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o.<init>(org.telegram.ui.Components.Bulletin$ParentLayout$1):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.ParentLayout.1.onFling(android.view.MotionEvent, android.view.MotionEvent, float, float):boolean, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 85 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 91 more
                            */
                        /*
                            this = this;
                            float r6 = java.lang.Math.abs(r8)
                            r7 = 0
                            r9 = 1157234688(0x44fa0000, float:2000.0)
                            int r6 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                            if (r6 <= 0) goto L_0x00a0
                            r6 = 1
                            r9 = 0
                            int r0 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                            if (r0 >= 0) goto L_0x0019
                            org.telegram.ui.Components.Bulletin$ParentLayout r0 = org.telegram.ui.Components.Bulletin.ParentLayout.this
                            boolean r0 = r0.needLeftAlphaAnimation
                            if (r0 != 0) goto L_0x0025
                        L_0x0019:
                            int r0 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
                            if (r0 <= 0) goto L_0x0026
                            org.telegram.ui.Components.Bulletin$ParentLayout r0 = org.telegram.ui.Components.Bulletin.ParentLayout.this
                            boolean r0 = r0.needRightAlphaAnimation
                            if (r0 == 0) goto L_0x0026
                        L_0x0025:
                            r7 = 1
                        L_0x0026:
                            androidx.dynamicanimation.animation.SpringAnimation r0 = new androidx.dynamicanimation.animation.SpringAnimation
                            org.telegram.ui.Components.Bulletin$Layout r1 = r4
                            androidx.dynamicanimation.animation.DynamicAnimation$ViewProperty r2 = androidx.dynamicanimation.animation.DynamicAnimation.TRANSLATION_X
                            float r3 = java.lang.Math.signum(r8)
                            org.telegram.ui.Components.Bulletin$Layout r4 = r4
                            int r4 = r4.getWidth()
                            float r4 = (float) r4
                            float r3 = r3 * r4
                            r4 = 1073741824(0x40000000, float:2.0)
                            float r3 = r3 * r4
                            r0.<init>(r1, r2, r3)
                            if (r7 != 0) goto L_0x0054
                            org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o r1 = new org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$E98MnwO2ioNHH3ZnVEycMqxKe0o
                            r1.<init>(r5)
                            r0.addEndListener(r1)
                            org.telegram.ui.Components.Bulletin$Layout r1 = r4
                            org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$m0OywU-snF9bOOQahuDxEZglXSA r2 = new org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$m0OywU-snF9bOOQahuDxEZglXSA
                            r2.<init>(r1)
                            r0.addUpdateListener(r2)
                        L_0x0054:
                            androidx.dynamicanimation.animation.SpringForce r1 = r0.getSpring()
                            r2 = 1065353216(0x3var_, float:1.0)
                            r1.setDampingRatio(r2)
                            androidx.dynamicanimation.animation.SpringForce r1 = r0.getSpring()
                            r3 = 1120403456(0x42CLASSNAME, float:100.0)
                            r1.setStiffness(r3)
                            r0.setStartVelocity(r8)
                            r0.start()
                            if (r7 == 0) goto L_0x009a
                            androidx.dynamicanimation.animation.SpringAnimation r7 = new androidx.dynamicanimation.animation.SpringAnimation
                            org.telegram.ui.Components.Bulletin$Layout r1 = r4
                            androidx.dynamicanimation.animation.DynamicAnimation$ViewProperty r3 = androidx.dynamicanimation.animation.DynamicAnimation.ALPHA
                            r7.<init>(r1, r3, r9)
                            org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$GYGdDI-s_X29Uj63Pb1hENTHhE0 r9 = new org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$GYGdDI-s_X29Uj63Pb1hENTHhE0
                            r9.<init>(r5)
                            r7.addEndListener(r9)
                            org.telegram.ui.Components.-$$Lambda$Bulletin$ParentLayout$1$4Z0QOBLayZ_luJNmvYzO7RQ-9tk r9 = org.telegram.ui.Components.$$Lambda$Bulletin$ParentLayout$1$4Z0QOBLayZ_luJNmvYzO7RQ9tk.INSTANCE
                            r7.addUpdateListener(r9)
                            androidx.dynamicanimation.animation.SpringForce r9 = r0.getSpring()
                            r9.setDampingRatio(r2)
                            androidx.dynamicanimation.animation.SpringForce r9 = r0.getSpring()
                            r1 = 1092616192(0x41200000, float:10.0)
                            r9.setStiffness(r1)
                            r0.setStartVelocity(r8)
                            r7.start()
                        L_0x009a:
                            org.telegram.ui.Components.Bulletin$ParentLayout r7 = org.telegram.ui.Components.Bulletin.ParentLayout.this
                            boolean unused = r7.hideAnimationRunning = r6
                            return r6
                        L_0x00a0:
                            return r7
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.ParentLayout.AnonymousClass1.onFling(android.view.MotionEvent, android.view.MotionEvent, float, float):boolean");
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onFling$0 */
                    public /* synthetic */ void lambda$onFling$0$Bulletin$ParentLayout$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        ParentLayout.this.onHide();
                    }

                    static /* synthetic */ void lambda$onFling$1(Layout layout, DynamicAnimation dynamicAnimation, float f, float f2) {
                        if (Math.abs(f) > ((float) layout.getWidth())) {
                            dynamicAnimation.cancel();
                        }
                    }

                    /* access modifiers changed from: private */
                    /* renamed from: lambda$onFling$2 */
                    public /* synthetic */ void lambda$onFling$2$Bulletin$ParentLayout$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                        ParentLayout.this.onHide();
                    }

                    static /* synthetic */ void lambda$onFling$3(DynamicAnimation dynamicAnimation, float f, float f2) {
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
                            translationX2.alpha(f).setDuration(200).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable(signum) {
                                public final /* synthetic */ float f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run() {
                                    Bulletin.ParentLayout.this.lambda$onTouchEvent$0$Bulletin$ParentLayout(this.f$1);
                                }
                            }).start();
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
            /* renamed from: lambda$onTouchEvent$0 */
            public /* synthetic */ void lambda$onTouchEvent$0$Bulletin$ParentLayout(float f) {
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
            private final List<Callback> callbacks;
            Delegate delegate;
            public float inOutOffset;
            public boolean transitionRunning;
            private int wideScreenGravity;
            private int wideScreenWidth;

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
                this.wideScreenWidth = -2;
                this.wideScreenGravity = 1;
                setMinimumHeight(AndroidUtilities.dp(48.0f));
                this.background = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), i);
                updateSize();
                setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                setWillNotDraw(false);
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
                        ofFloat.addUpdateListener(
                        /*  JADX ERROR: Method code generation error
                            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x003f: INVOKE  
                              (r8v4 'ofFloat' android.animation.ObjectAnimator)
                              (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ : 0x003c: CONSTRUCTOR  (r5v1 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ) = 
                              (r7v0 'consumer' androidx.core.util.Consumer<java.lang.Float>)
                              (r4v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                             call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ.<init>(androidx.core.util.Consumer, org.telegram.ui.Components.Bulletin$Layout):void type: CONSTRUCTOR)
                             android.animation.ObjectAnimator.addUpdateListener(android.animation.ValueAnimator$AnimatorUpdateListener):void type: VIRTUAL in method: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
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
                            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x003c: CONSTRUCTOR  (r5v1 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ) = 
                              (r7v0 'consumer' androidx.core.util.Consumer<java.lang.Float>)
                              (r4v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                             call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ.<init>(androidx.core.util.Consumer, org.telegram.ui.Components.Bulletin$Layout):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                            	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                            	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                            	... 64 more
                            Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ, state: NOT_LOADED
                            	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                            	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                            	... 70 more
                            */
                        /*
                            this = this;
                            int r8 = r4.getMeasuredHeight()
                            float r8 = (float) r8
                            r4.setInOutOffset(r8)
                            if (r7 == 0) goto L_0x0015
                            float r8 = r4.getTranslationY()
                            java.lang.Float r8 = java.lang.Float.valueOf(r8)
                            r7.accept(r8)
                        L_0x0015:
                            android.util.Property<org.telegram.ui.Components.Bulletin$Layout, java.lang.Float> r8 = org.telegram.ui.Components.Bulletin.Layout.IN_OUT_OFFSET_Y2
                            r0 = 1
                            float[] r0 = new float[r0]
                            r1 = 0
                            r2 = 0
                            r0[r1] = r2
                            android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r4, r8, r0)
                            long r0 = r3.duration
                            r8.setDuration(r0)
                            android.view.animation.Interpolator r0 = org.telegram.ui.Components.Easings.easeOutQuad
                            r8.setInterpolator(r0)
                            if (r5 != 0) goto L_0x0030
                            if (r6 == 0) goto L_0x0038
                        L_0x0030:
                            org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$1 r0 = new org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$1
                            r0.<init>(r3, r5, r6)
                            r8.addListener(r0)
                        L_0x0038:
                            if (r7 == 0) goto L_0x0042
                            org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ r5 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$A0CEx-wYn1rX0vZEUnXvSh66axQ
                            r5.<init>(r7, r4)
                            r8.addUpdateListener(r5)
                        L_0x0042:
                            r8.start()
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void");
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
                            ofFloat.addUpdateListener(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002e: INVOKE  
                                  (r8v2 'ofFloat' android.animation.ObjectAnimator)
                                  (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI : 0x002b: CONSTRUCTOR  (r5v1 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI) = 
                                  (r7v0 'consumer' androidx.core.util.Consumer<java.lang.Float>)
                                  (r4v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                                 call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI.<init>(androidx.core.util.Consumer, org.telegram.ui.Components.Bulletin$Layout):void type: CONSTRUCTOR)
                                 android.animation.ObjectAnimator.addUpdateListener(android.animation.ValueAnimator$AnimatorUpdateListener):void type: VIRTUAL in method: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
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
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x002b: CONSTRUCTOR  (r5v1 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI) = 
                                  (r7v0 'consumer' androidx.core.util.Consumer<java.lang.Float>)
                                  (r4v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                                 call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI.<init>(androidx.core.util.Consumer, org.telegram.ui.Components.Bulletin$Layout):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 64 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 70 more
                                */
                            /*
                                this = this;
                                android.util.Property<org.telegram.ui.Components.Bulletin$Layout, java.lang.Float> r8 = org.telegram.ui.Components.Bulletin.Layout.IN_OUT_OFFSET_Y2
                                r0 = 1
                                float[] r0 = new float[r0]
                                int r1 = r4.getHeight()
                                float r1 = (float) r1
                                r2 = 0
                                r0[r2] = r1
                                android.animation.ObjectAnimator r8 = android.animation.ObjectAnimator.ofFloat(r4, r8, r0)
                                r0 = 175(0xaf, double:8.65E-322)
                                r8.setDuration(r0)
                                android.view.animation.Interpolator r0 = org.telegram.ui.Components.Easings.easeInQuad
                                r8.setInterpolator(r0)
                                if (r5 != 0) goto L_0x001f
                                if (r6 == 0) goto L_0x0027
                            L_0x001f:
                                org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$2 r0 = new org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$2
                                r0.<init>(r3, r5, r6)
                                r8.addListener(r0)
                            L_0x0027:
                                if (r7 == 0) goto L_0x0031
                                org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI r5 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$DefaultTransition$x0-bEmqh8bD08OOIq4iDLiXteYI
                                r5.<init>(r7, r4)
                                r8.addUpdateListener(r5)
                            L_0x0031:
                                r8.start()
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void");
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
                                springAnimation.addEndListener(
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0037: INVOKE  
                                      (r7v3 'springAnimation' androidx.dynamicanimation.animation.SpringAnimation)
                                      (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw : 0x0034: CONSTRUCTOR  (r0v3 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw) = 
                                      (r3v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                                      (r5v0 'runnable2' java.lang.Runnable)
                                     call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw.<init>(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable):void type: CONSTRUCTOR)
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
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0034: CONSTRUCTOR  (r0v3 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw) = 
                                      (r3v0 'layout' org.telegram.ui.Components.Bulletin$Layout)
                                      (r5v0 'runnable2' java.lang.Runnable)
                                     call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw.<init>(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 64 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 70 more
                                    */
                                /*
                                    this = this;
                                    int r7 = r3.getMeasuredHeight()
                                    float r7 = (float) r7
                                    r3.setInOutOffset(r7)
                                    if (r6 == 0) goto L_0x0015
                                    float r7 = r3.getTranslationY()
                                    java.lang.Float r7 = java.lang.Float.valueOf(r7)
                                    r6.accept(r7)
                                L_0x0015:
                                    androidx.dynamicanimation.animation.SpringAnimation r7 = new androidx.dynamicanimation.animation.SpringAnimation
                                    androidx.dynamicanimation.animation.FloatPropertyCompat<org.telegram.ui.Components.Bulletin$Layout> r0 = org.telegram.ui.Components.Bulletin.Layout.IN_OUT_OFFSET_Y
                                    r1 = 0
                                    r7.<init>(r3, r0, r1)
                                    androidx.dynamicanimation.animation.SpringForce r0 = r7.getSpring()
                                    r1 = 1061997773(0x3f4ccccd, float:0.8)
                                    r0.setDampingRatio(r1)
                                    androidx.dynamicanimation.animation.SpringForce r0 = r7.getSpring()
                                    r1 = 1137180672(0x43CLASSNAME, float:400.0)
                                    r0.setStiffness(r1)
                                    if (r5 == 0) goto L_0x003a
                                    org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw r0 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ACLASSNAMEsbLq3A__eTV-TtpyxS1icfw
                                    r0.<init>(r3, r5)
                                    r7.addEndListener(r0)
                                L_0x003a:
                                    if (r6 == 0) goto L_0x0044
                                    org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$HB-DLkVbAMzVF4W_tAjCIwsxtGw r5 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$HB-DLkVbAMzVF4W_tAjCIwsxtGw
                                    r5.<init>(r6, r3)
                                    r7.addUpdateListener(r5)
                                L_0x0044:
                                    r7.start()
                                    if (r4 == 0) goto L_0x004c
                                    r4.run()
                                L_0x004c:
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateEnter(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void");
                            }

                            static /* synthetic */ void lambda$animateEnter$0(Layout layout, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
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
                                    springAnimation.addEndListener(
                                    /*  JADX ERROR: Method code generation error
                                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0026: INVOKE  
                                          (r7v1 'springAnimation' androidx.dynamicanimation.animation.SpringAnimation)
                                          (wrap: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI : 0x0023: CONSTRUCTOR  (r0v3 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI) = (r5v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI.<init>(java.lang.Runnable):void type: CONSTRUCTOR)
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
                                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0023: CONSTRUCTOR  (r0v3 org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI) = (r5v0 'runnable2' java.lang.Runnable) call: org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI.<init>(java.lang.Runnable):void type: CONSTRUCTOR in method: org.telegram.ui.Components.Bulletin.Layout.SpringTransition.animateExit(org.telegram.ui.Components.Bulletin$Layout, java.lang.Runnable, java.lang.Runnable, androidx.core.util.Consumer, int):void, dex: classes3.dex
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
                                        androidx.dynamicanimation.animation.SpringAnimation r7 = new androidx.dynamicanimation.animation.SpringAnimation
                                        androidx.dynamicanimation.animation.FloatPropertyCompat<org.telegram.ui.Components.Bulletin$Layout> r0 = org.telegram.ui.Components.Bulletin.Layout.IN_OUT_OFFSET_Y
                                        int r1 = r3.getHeight()
                                        float r1 = (float) r1
                                        r7.<init>(r3, r0, r1)
                                        androidx.dynamicanimation.animation.SpringForce r0 = r7.getSpring()
                                        r1 = 1061997773(0x3f4ccccd, float:0.8)
                                        r0.setDampingRatio(r1)
                                        androidx.dynamicanimation.animation.SpringForce r0 = r7.getSpring()
                                        r1 = 1137180672(0x43CLASSNAME, float:400.0)
                                        r0.setStiffness(r1)
                                        if (r5 == 0) goto L_0x0029
                                        org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI r0 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$ylZh0_jci79BZjVSNAMl5t5aJlI
                                        r0.<init>(r5)
                                        r7.addEndListener(r0)
                                    L_0x0029:
                                        if (r6 == 0) goto L_0x0033
                                        org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$FPrD8PSlb9yjsdgcvdjmlotjRV0 r5 = new org.telegram.ui.Components.-$$Lambda$Bulletin$Layout$SpringTransition$FPrD8PSlb9yjsdgcvdjmlotjRV0
                                        r5.<init>(r6, r3)
                                        r7.addUpdateListener(r5)
                                    L_0x0033:
                                        r7.start()
                                        if (r4 == 0) goto L_0x003b
                                        r4.run()
                                    L_0x003b:
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
                                rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
                                addView(rLottieImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
                                int color = Theme.getColor("undo_infoColor");
                                LinearLayout linearLayout = new LinearLayout(context);
                                linearLayout.setOrientation(1);
                                addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 8.0f, 12.0f, 8.0f));
                                TextView textView = new TextView(context);
                                this.titleTextView = textView;
                                textView.setSingleLine();
                                textView.setTextColor(color);
                                textView.setTextSize(1, 14.0f);
                                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                linearLayout.addView(textView);
                                TextView textView2 = new TextView(context);
                                this.subtitleTextView = textView2;
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

                            public void setAnimation(int i, int i2, int i3, String... strArr) {
                                this.imageView.setAnimation(i, i2, i3);
                                for (String str : strArr) {
                                    RLottieImageView rLottieImageView = this.imageView;
                                    rLottieImageView.setLayerColor(str + ".**", this.textColor);
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
                                rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
                                addView(rLottieImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
                                TextView textView2 = new TextView(context);
                                this.textView = textView2;
                                textView2.setSingleLine();
                                textView2.setTextColor(i2);
                                textView2.setTypeface(Typeface.SANS_SERIF);
                                textView2.setTextSize(1, 15.0f);
                                textView2.setEllipsize(TextUtils.TruncateAt.END);
                                textView2.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                                addView(textView2, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
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
                            private Runnable undoAction;

                            /* JADX INFO: super call moved to the top of the method (can break code semantics) */
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
                                    textView.setBackground(Theme.createCircleSelectorDrawable(NUM | (16777215 & color), LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0, !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0));
                                    textView.setTextSize(1, 14.0f);
                                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                    textView.setTextColor(color);
                                    textView.setText(LocaleController.getString("Undo", NUM));
                                    textView.setGravity(16);
                                    ViewHelper.setPaddingRelative(textView, 16.0f, 0.0f, 16.0f, 0.0f);
                                    addView(textView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
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
