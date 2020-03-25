package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.InsetDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;

public final class Bulletin {
    /* access modifiers changed from: private */
    public static final HashMap<FrameLayout, OffsetProvider> offsetProviders = new HashMap<>();
    @SuppressLint({"StaticFieldLeak"})
    private static Bulletin visibleBulletin;
    /* access modifiers changed from: private */
    public final FrameLayout containerLayout;
    /* access modifiers changed from: private */
    public final int duration;
    /* access modifiers changed from: private */
    public Runnable exitRunnable;
    /* access modifiers changed from: private */
    public final Layout layout;
    /* access modifiers changed from: private */
    public final Layout.Transition layoutTransition;
    /* access modifiers changed from: private */
    public boolean showing;

    public interface OffsetProvider {
        int getBottomOffset();
    }

    public static Bulletin make(FrameLayout frameLayout, Layout layout2, int i) {
        return new Bulletin(frameLayout, layout2, i);
    }

    public static Bulletin make(BaseFragment baseFragment, Layout layout2, int i) {
        return new Bulletin(baseFragment.getParentLayout(), layout2, i);
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

    private Bulletin(FrameLayout frameLayout, Layout layout2, int i) {
        this.layout = layout2;
        this.layoutTransition = layout2.createTransition();
        this.containerLayout = frameLayout;
        this.duration = i;
    }

    public void show() {
        if (!this.showing) {
            this.showing = true;
            if (this.layout.getParent() == null) {
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
                            if (Bulletin.isTransitionsEnabled()) {
                                OffsetProvider offsetProvider = (OffsetProvider) Bulletin.offsetProviders.get(Bulletin.this.containerLayout);
                                Layout.Transition access$500 = Bulletin.this.layoutTransition;
                                Layout access$000 = Bulletin.this.layout;
                                Layout access$0002 = Bulletin.this.layout;
                                access$0002.getClass();
                                access$500.animateEnter(access$000, new Runnable() {
                                    public final void run() {
                                        Bulletin.Layout.this.onEnterTransitionStart();
                                    }
                                }, new Runnable() {
                                    public final void run() {
                                        Bulletin.AnonymousClass1.this.lambda$onLayoutChange$0$Bulletin$1();
                                    }
                                }, offsetProvider != null ? offsetProvider.getBottomOffset() : 0);
                                return;
                            }
                            Bulletin.this.layout.onEnterTransitionStart();
                            Bulletin.this.layout.onEnterTransitionEnd();
                            Layout access$0003 = Bulletin.this.layout;
                            Bulletin bulletin = Bulletin.this;
                            $$Lambda$wTVigIqq1dtB15QAWctG67JZ4x8 r3 = new Runnable() {
                                public final void run() {
                                    Bulletin.this.hide();
                                }
                            };
                            Runnable unused = bulletin.exitRunnable = r3;
                            access$0003.postDelayed(r3, (long) Bulletin.this.duration);
                        }
                    }

                    public /* synthetic */ void lambda$onLayoutChange$0$Bulletin$1() {
                        Bulletin.this.layout.onEnterTransitionEnd();
                        Layout access$000 = Bulletin.this.layout;
                        Bulletin bulletin = Bulletin.this;
                        $$Lambda$wTVigIqq1dtB15QAWctG67JZ4x8 r2 = new Runnable() {
                            public final void run() {
                                Bulletin.this.hide();
                            }
                        };
                        Runnable unused = bulletin.exitRunnable = r2;
                        access$000.postDelayed(r2, (long) Bulletin.this.duration);
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
                this.containerLayout.addView(this.layout);
                return;
            }
            throw new IllegalStateException("Layout already has a parent");
        }
    }

    public void hide() {
        hide(isTransitionsEnabled());
    }

    /* access modifiers changed from: private */
    public void hide(boolean z) {
        if (this.showing) {
            int i = 0;
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            if (ViewCompat.isLaidOut(this.layout)) {
                Runnable runnable = this.exitRunnable;
                if (runnable != null) {
                    this.layout.removeCallbacks(runnable);
                    this.exitRunnable = null;
                }
                if (z) {
                    OffsetProvider offsetProvider = offsetProviders.get(this.containerLayout);
                    Layout.Transition transition = this.layoutTransition;
                    Layout layout2 = this.layout;
                    layout2.getClass();
                    $$Lambda$Ig6osR7PXW8VgfIN_nLQPCt6qA r3 = new Runnable() {
                        public final void run() {
                            Bulletin.Layout.this.onExitTransitionStart();
                        }
                    };
                    $$Lambda$Bulletin$6NXrlSAHhDw4D1a2UGnfEdQd7k0 r4 = new Runnable() {
                        public final void run() {
                            Bulletin.this.lambda$hide$0$Bulletin();
                        }
                    };
                    if (offsetProvider != null) {
                        i = offsetProvider.getBottomOffset();
                    }
                    transition.animateExit(layout2, r3, r4, i);
                    return;
                }
            }
            this.layout.onExitTransitionStart();
            this.layout.onExitTransitionEnd();
            this.layout.onHide();
            this.containerLayout.removeView(this.layout);
            this.layout.onDetach();
        }
    }

    public /* synthetic */ void lambda$hide$0$Bulletin() {
        this.layout.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.layout);
        this.layout.onDetach();
    }

    public Layout getLayout() {
        return this.layout;
    }

    /* access modifiers changed from: private */
    public static boolean isTransitionsEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("view_animations", true);
    }

    public static void addOffsetProvider(FrameLayout frameLayout, OffsetProvider offsetProvider) {
        offsetProviders.put(frameLayout, offsetProvider);
    }

    public static void removeOffsetProvider(FrameLayout frameLayout) {
        offsetProviders.remove(frameLayout);
    }

    public static abstract class Layout extends FrameLayout {
        protected Bulletin bulletin;
        private final List<Callback> callbacks = new ArrayList();

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
            void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, int i);

            void animateExit(Layout layout, Runnable runnable, Runnable runnable2, int i);
        }

        public Layout(Context context) {
            super(context);
            setMinimumHeight(AndroidUtilities.dp(48.0f));
            setBackground(new InsetDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor("undo_background")), AndroidUtilities.dp(8.0f)));
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

        public boolean onTouchEvent(MotionEvent motionEvent) {
            super.onTouchEvent(motionEvent);
            return true;
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
            return new DefaultTransition();
        }

        public static class DefaultTransition implements Transition {
            public void animateEnter(Layout layout, final Runnable runnable, final Runnable runnable2, int i) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, View.TRANSLATION_Y, new float[]{(float) layout.getHeight(), (float) (-i)});
                ofFloat.setDuration(225);
                ofFloat.setInterpolator(Easings.easeOutQuad);
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
                ofFloat.start();
            }

            public void animateExit(Layout layout, final Runnable runnable, final Runnable runnable2, int i) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(layout, View.TRANSLATION_Y, new float[]{layout.getTranslationY(), (float) layout.getHeight()});
                ofFloat.setDuration(175);
                ofFloat.setInterpolator(Easings.easeInQuad);
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
                ofFloat.start();
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
            addView(this.imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setSingleLine();
            this.textView.setTextColor(color);
            this.textView.setTypeface(Typeface.SANS_SERIF);
            this.textView.setTextSize(1, 15.0f);
            addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
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
            this.titleTextView.setTextColor(color);
            this.titleTextView.setTextSize(1, 14.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(this.titleTextView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setMaxLines(2);
            this.subtitleTextView.setTextColor(color);
            this.subtitleTextView.setTypeface(Typeface.SANS_SERIF);
            this.subtitleTextView.setTextSize(1, 13.0f);
            linearLayout.addView(this.subtitleTextView);
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

        public UndoButton(Context context) {
            super(context);
            int color = Theme.getColor("undo_cancelColor");
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    Bulletin.UndoButton.this.lambda$new$0$Bulletin$UndoButton(view);
                }
            });
            imageView.setImageResource(NUM);
            imageView.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            imageView.setBackground(Theme.createSelectorDrawable((color & 16777215) | NUM));
            ViewHelper.setPaddingRelative(imageView, 0.0f, 12.0f, 0.0f, 12.0f);
            addView(imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
        }

        public /* synthetic */ void lambda$new$0$Bulletin$UndoButton(View view) {
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
