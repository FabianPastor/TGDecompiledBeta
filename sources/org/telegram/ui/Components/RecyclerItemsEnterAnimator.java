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

    public RecyclerItemsEnterAnimator(RecyclerListView recyclerListView, boolean z) {
        this.listView = recyclerListView;
        this.alwaysCheckItemsAlpha = z;
        recyclerListView.setItemsEnterAnimator(this);
    }

    public void dispatchDraw() {
        if (this.invalidateAlpha || this.alwaysCheckItemsAlpha) {
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View childAt = this.listView.getChildAt(i);
                int childAdapterPosition = this.listView.getChildAdapterPosition(childAt);
                if (childAdapterPosition >= 0 && !this.ignoreView.contains(childAt)) {
                    Float f = this.listAlphaItems.get(childAdapterPosition, (Object) null);
                    if (f == null) {
                        childAt.setAlpha(1.0f);
                    } else {
                        childAt.setAlpha(f.floatValue());
                    }
                }
            }
            this.invalidateAlpha = false;
        }
    }

    public void showItemsAnimated(final int i) {
        Animator animator;
        final View progressView = getProgressView();
        final RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        if (!(progressView == null || layoutManager == null)) {
            this.listView.removeView(progressView);
            this.ignoreView.add(progressView);
            this.listView.addView(progressView);
            layoutManager.ignoreView(progressView);
            if (this.animateAlphaProgressView) {
                animator = ObjectAnimator.ofFloat(progressView, View.ALPHA, new float[]{progressView.getAlpha(), 0.0f});
            } else {
                animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            }
            animator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    progressView.setAlpha(1.0f);
                    layoutManager.stopIgnoringView(progressView);
                    RecyclerItemsEnterAnimator.this.ignoreView.remove(progressView);
                    RecyclerItemsEnterAnimator.this.listView.removeView(progressView);
                }
            });
            animator.start();
            i--;
        }
        AnonymousClass2 r1 = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                RecyclerItemsEnterAnimator.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                RecyclerItemsEnterAnimator.this.preDrawListeners.remove(this);
                int childCount = RecyclerItemsEnterAnimator.this.listView.getChildCount();
                final AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = RecyclerItemsEnterAnimator.this.listView.getChildAt(i);
                    final int childAdapterPosition = RecyclerItemsEnterAnimator.this.listView.getChildAdapterPosition(childAt);
                    if (childAt != progressView && childAdapterPosition >= i - 1 && RecyclerItemsEnterAnimator.this.listAlphaItems.get(childAdapterPosition, (Object) null) == null) {
                        RecyclerItemsEnterAnimator.this.listAlphaItems.put(childAdapterPosition, Float.valueOf(0.0f));
                        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                        recyclerItemsEnterAnimator.invalidateAlpha = true;
                        recyclerItemsEnterAnimator.listView.invalidate();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.addUpdateListener(new RecyclerItemsEnterAnimator$2$$ExternalSyntheticLambda0(this, childAdapterPosition));
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                RecyclerItemsEnterAnimator.this.listAlphaItems.remove(childAdapterPosition);
                                RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                                recyclerItemsEnterAnimator.invalidateAlpha = true;
                                recyclerItemsEnterAnimator.listView.invalidate();
                            }
                        });
                        ofFloat.setStartDelay((long) ((int) ((((float) Math.min(RecyclerItemsEnterAnimator.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) RecyclerItemsEnterAnimator.this.listView.getMeasuredHeight())) * 100.0f)));
                        ofFloat.setDuration(200);
                        animatorSet.playTogether(new Animator[]{ofFloat});
                    }
                }
                RecyclerItemsEnterAnimator.this.currentAnimations.add(animatorSet);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        RecyclerItemsEnterAnimator.this.currentAnimations.remove(animatorSet);
                        if (RecyclerItemsEnterAnimator.this.currentAnimations.isEmpty()) {
                            RecyclerItemsEnterAnimator.this.listAlphaItems.clear();
                            RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                            recyclerItemsEnterAnimator.invalidateAlpha = true;
                            recyclerItemsEnterAnimator.listView.invalidate();
                        }
                    }
                });
                return false;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onPreDraw$0(int i, ValueAnimator valueAnimator) {
                RecyclerItemsEnterAnimator.this.listAlphaItems.put(i, (Float) valueAnimator.getAnimatedValue());
                RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                recyclerItemsEnterAnimator.invalidateAlpha = true;
                recyclerItemsEnterAnimator.listView.invalidate();
            }
        };
        this.preDrawListeners.add(r1);
        this.listView.getViewTreeObserver().addOnPreDrawListener(r1);
    }

    public View getProgressView() {
        int childCount = this.listView.getChildCount();
        View view = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (this.listView.getChildAdapterPosition(childAt) >= 0 && (childAt instanceof FlickerLoadingView)) {
                view = childAt;
            }
        }
        return view;
    }

    public void onDetached() {
        cancel();
    }

    public void cancel() {
        if (!this.currentAnimations.isEmpty()) {
            ArrayList arrayList = new ArrayList(this.currentAnimations);
            for (int i = 0; i < arrayList.size(); i++) {
                ((AnimatorSet) arrayList.get(i)).end();
                ((AnimatorSet) arrayList.get(i)).cancel();
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
