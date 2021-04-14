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
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;

public class RecyclerItemsEnterAnimator {
    ArrayList<AnimatorSet> currentAnimations = new ArrayList<>();
    HashSet<View> ignoreView = new HashSet<>();
    boolean invalidateAlpha;
    /* access modifiers changed from: private */
    public final SparseArray<Float> listAlphaItems = new SparseArray<>();
    /* access modifiers changed from: private */
    public final RecyclerListView listView;
    ArrayList<ViewTreeObserver.OnPreDrawListener> preDrawListeners = new ArrayList<>();

    public RecyclerItemsEnterAnimator(RecyclerListView recyclerListView) {
        this.listView = recyclerListView;
    }

    public void dispatchDraw() {
        if (this.invalidateAlpha) {
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
        int childCount = this.listView.getChildCount();
        final View view = null;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (this.listView.getChildAdapterPosition(childAt) >= 0 && (childAt instanceof FlickerLoadingView)) {
                view = childAt;
            }
        }
        final RecyclerView.LayoutManager layoutManager = this.listView.getLayoutManager();
        if (!(view == null || layoutManager == null)) {
            this.listView.removeView(view);
            this.ignoreView.add(view);
            this.listView.addView(view);
            layoutManager.ignoreView(view);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, View.ALPHA, new float[]{view.getAlpha(), 0.0f});
            ofFloat.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    view.setAlpha(1.0f);
                    layoutManager.stopIgnoringView(view);
                    RecyclerItemsEnterAnimator.this.ignoreView.remove(view);
                    RecyclerItemsEnterAnimator.this.listView.removeView(view);
                }
            });
            ofFloat.start();
            i--;
        }
        AnonymousClass2 r0 = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                RecyclerItemsEnterAnimator.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                RecyclerItemsEnterAnimator.this.preDrawListeners.remove(this);
                int childCount = RecyclerItemsEnterAnimator.this.listView.getChildCount();
                final AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < childCount; i++) {
                    View childAt = RecyclerItemsEnterAnimator.this.listView.getChildAt(i);
                    final int childAdapterPosition = RecyclerItemsEnterAnimator.this.listView.getChildAdapterPosition(childAt);
                    if (childAt != view && childAdapterPosition >= i - 1 && RecyclerItemsEnterAnimator.this.listAlphaItems.get(childAdapterPosition, (Object) null) == null) {
                        RecyclerItemsEnterAnimator.this.listAlphaItems.put(childAdapterPosition, Float.valueOf(0.0f));
                        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                        recyclerItemsEnterAnimator.invalidateAlpha = true;
                        recyclerItemsEnterAnimator.listView.invalidate();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(childAdapterPosition) {
                            public final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                RecyclerItemsEnterAnimator.AnonymousClass2.this.lambda$onPreDraw$0$RecyclerItemsEnterAnimator$2(this.f$1, valueAnimator);
                            }
                        });
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
            /* renamed from: lambda$onPreDraw$0 */
            public /* synthetic */ void lambda$onPreDraw$0$RecyclerItemsEnterAnimator$2(int i, ValueAnimator valueAnimator) {
                RecyclerItemsEnterAnimator.this.listAlphaItems.put(i, (Float) valueAnimator.getAnimatedValue());
                RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = RecyclerItemsEnterAnimator.this;
                recyclerItemsEnterAnimator.invalidateAlpha = true;
                recyclerItemsEnterAnimator.listView.invalidate();
            }
        };
        this.preDrawListeners.add(r0);
        this.listView.getViewTreeObserver().addOnPreDrawListener(r0);
    }

    public void onDetached() {
        for (int i = 0; i < this.currentAnimations.size(); i++) {
            this.currentAnimations.get(i).cancel();
        }
        this.currentAnimations.clear();
        for (int i2 = 0; i2 < this.preDrawListeners.size(); i2++) {
            this.listView.getViewTreeObserver().removeOnPreDrawListener(this.preDrawListeners.get(i2));
        }
        this.preDrawListeners.clear();
        this.listAlphaItems.clear();
        this.invalidateAlpha = true;
    }
}
