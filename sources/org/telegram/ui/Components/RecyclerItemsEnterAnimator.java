package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewTreeObserver;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;

public class RecyclerItemsEnterAnimator {
    boolean alwaysCheckItemsAlpha;
    public boolean animateAlphaProgressView = true;
    ArrayList<AnimatorSet> currentAnimations = new ArrayList<>();
    HashSet<View> ignoreView = new HashSet<>();
    boolean invalidateAlpha;
    /* access modifiers changed from: private */
    public final SparseArray<Float> listAlphaItems = new SparseArray<>();
    /* access modifiers changed from: private */
    public final RecyclerListView listView;
    ArrayList<ViewTreeObserver.OnPreDrawListener> preDrawListeners = new ArrayList<>();

    public RecyclerItemsEnterAnimator(RecyclerListView listView2, boolean alwaysCheckItemsAlpha2) {
        this.listView = listView2;
        this.alwaysCheckItemsAlpha = alwaysCheckItemsAlpha2;
        listView2.setItemsEnterAnimator(this);
    }

    public void dispatchDraw() {
        if (this.invalidateAlpha || this.alwaysCheckItemsAlpha) {
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View child = this.listView.getChildAt(i);
                int position = this.listView.getChildAdapterPosition(child);
                if (position >= 0 && !this.ignoreView.contains(child)) {
                    Float alpha = this.listAlphaItems.get(position, (Object) null);
                    if (alpha == null) {
                        child.setAlpha(1.0f);
                    } else {
                        child.setAlpha(alpha.floatValue());
                    }
                }
            }
            this.invalidateAlpha = false;
        }
    }

    public void showItemsAnimated(int from) {
        Animator animator;
        final View finalProgressView = getProgressView();
        final RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        if (!(finalProgressView == null || layoutManager == null)) {
            this.listView.removeView(finalProgressView);
            this.ignoreView.add(finalProgressView);
            this.listView.addView(finalProgressView);
            layoutManager.ignoreView(finalProgressView);
            if (this.animateAlphaProgressView) {
                animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
            } else {
                animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            }
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    finalProgressView.setAlpha(1.0f);
                    layoutManager.stopIgnoringView(finalProgressView);
                    RecyclerItemsEnterAnimator.this.ignoreView.remove(finalProgressView);
                    RecyclerItemsEnterAnimator.this.listView.removeView(finalProgressView);
                }
            });
            animator.start();
            from--;
        }
        final int finalFrom = from;
        ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                RecyclerItemsEnterAnimator.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                RecyclerItemsEnterAnimator.this.preDrawListeners.remove(this);
                int n = RecyclerItemsEnterAnimator.this.listView.getChildCount();
                final AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < n; i++) {
                    View child = RecyclerItemsEnterAnimator.this.listView.getChildAt(i);
                    final int position = RecyclerItemsEnterAnimator.this.listView.getChildAdapterPosition(child);
                    if (child != finalProgressView && position >= finalFrom - 1 && RecyclerItemsEnterAnimator.this.listAlphaItems.get(position, (Object) null) == null) {
                        RecyclerItemsEnterAnimator.this.listAlphaItems.put(position, Float.valueOf(0.0f));
                        RecyclerItemsEnterAnimator.this.invalidateAlpha = true;
                        RecyclerItemsEnterAnimator.this.listView.invalidate();
                        ValueAnimator a = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        a.addUpdateListener(new RecyclerItemsEnterAnimator$2$$ExternalSyntheticLambda0(this, position));
                        a.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                RecyclerItemsEnterAnimator.this.listAlphaItems.remove(position);
                                RecyclerItemsEnterAnimator.this.invalidateAlpha = true;
                                RecyclerItemsEnterAnimator.this.listView.invalidate();
                            }
                        });
                        a.setStartDelay((long) ((int) ((((float) Math.min(RecyclerItemsEnterAnimator.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) RecyclerItemsEnterAnimator.this.listView.getMeasuredHeight())) * 100.0f)));
                        a.setDuration(200);
                        animatorSet.playTogether(new Animator[]{a});
                    }
                }
                RecyclerItemsEnterAnimator.this.currentAnimations.add(animatorSet);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        RecyclerItemsEnterAnimator.this.currentAnimations.remove(animatorSet);
                        if (RecyclerItemsEnterAnimator.this.currentAnimations.isEmpty()) {
                            RecyclerItemsEnterAnimator.this.listAlphaItems.clear();
                            RecyclerItemsEnterAnimator.this.invalidateAlpha = true;
                            RecyclerItemsEnterAnimator.this.listView.invalidate();
                        }
                    }
                });
                return false;
            }

            /* renamed from: lambda$onPreDraw$0$org-telegram-ui-Components-RecyclerItemsEnterAnimator$2  reason: not valid java name */
            public /* synthetic */ void m4290x18071e0(int position, ValueAnimator valueAnimator) {
                RecyclerItemsEnterAnimator.this.listAlphaItems.put(position, (Float) valueAnimator.getAnimatedValue());
                RecyclerItemsEnterAnimator.this.invalidateAlpha = true;
                RecyclerItemsEnterAnimator.this.listView.invalidate();
            }
        };
        this.preDrawListeners.add(preDrawListener);
        this.listView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    public View getProgressView() {
        View progressView = null;
        int n = this.listView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = this.listView.getChildAt(i);
            if (this.listView.getChildAdapterPosition(child) >= 0 && (child instanceof FlickerLoadingView)) {
                progressView = child;
            }
        }
        return progressView;
    }

    public void onDetached() {
        cancel();
    }

    public void cancel() {
        if (!this.currentAnimations.isEmpty()) {
            ArrayList<AnimatorSet> animations = new ArrayList<>(this.currentAnimations);
            for (int i = 0; i < animations.size(); i++) {
                animations.get(i).end();
                animations.get(i).cancel();
            }
        }
        this.currentAnimations.clear();
        for (int i2 = 0; i2 < this.preDrawListeners.size(); i2++) {
            this.listView.getViewTreeObserver().removeOnPreDrawListener(this.preDrawListeners.get(i2));
        }
        this.preDrawListeners.clear();
        this.listAlphaItems.clear();
        this.listView.invalidate();
        this.invalidateAlpha = true;
    }
}
