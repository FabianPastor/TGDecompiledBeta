package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class RecyclerAnimationScrollHelper {
    public static final int SCROLL_DIRECTION_DOWN = 0;
    public static final int SCROLL_DIRECTION_UNSET = -1;
    public static final int SCROLL_DIRECTION_UP = 1;
    AnimationCallback animationCallback;
    ValueAnimator animator;
    private LinearLayoutManager layoutManager;
    private RecyclerListView recyclerView;
    private int scrollDirection;
    ScrollListener scrollListener;

    public static class AnimationCallback {
        public void onEndAnimation() {
        }

        public void onStartAnimation() {
        }
    }

    public interface ScrollListener {
        void onScroll();
    }

    public static abstract class AnimatableAdapter extends SelectionAdapter {
        public boolean animationRunning;
        private ArrayList<Integer> rangeInserted = new ArrayList();
        private ArrayList<Integer> rangeRemoved = new ArrayList();
        private boolean shouldNotifyDataSetChanged;

        public void notifyDataSetChanged() {
            if (this.animationRunning) {
                this.shouldNotifyDataSetChanged = true;
            } else {
                super.notifyDataSetChanged();
            }
        }

        public void notifyItemRangeInserted(int i, int i2) {
            if (this.animationRunning) {
                this.rangeInserted.add(Integer.valueOf(i));
                this.rangeInserted.add(Integer.valueOf(i2));
                return;
            }
            super.notifyItemRangeInserted(i, i2);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            if (this.animationRunning) {
                this.rangeRemoved.add(Integer.valueOf(i));
                this.rangeRemoved.add(Integer.valueOf(i2));
                return;
            }
            super.notifyItemRangeRemoved(i, i2);
        }

        public void onAnimationStart() {
            this.animationRunning = true;
            this.shouldNotifyDataSetChanged = false;
            this.rangeInserted.clear();
            this.rangeRemoved.clear();
        }

        public void onAnimationEnd() {
            this.animationRunning = false;
            if (this.shouldNotifyDataSetChanged || !this.rangeInserted.isEmpty() || !this.rangeRemoved.isEmpty()) {
                notifyDataSetChanged();
            }
        }
    }

    public RecyclerAnimationScrollHelper(RecyclerListView recyclerListView, LinearLayoutManager linearLayoutManager) {
        this.recyclerView = recyclerListView;
        this.layoutManager = linearLayoutManager;
    }

    public void scrollToPosition(int i, int i2) {
        scrollToPosition(i, i2, this.layoutManager.getReverseLayout(), false);
    }

    public void scrollToPosition(int i, int i2, boolean z) {
        scrollToPosition(i, i2, z, false);
    }

    public void scrollToPosition(int i, int i2, boolean z, boolean z2) {
        RecyclerListView recyclerListView = this.recyclerView;
        if (!recyclerListView.animationRunning) {
            if (!z2 || this.scrollDirection == -1) {
                this.layoutManager.scrollToPositionWithOffset(i, i2, z);
                return;
            }
            int childCount = recyclerListView.getChildCount();
            if (childCount == 0) {
                this.layoutManager.scrollToPositionWithOffset(i, i2, z);
                return;
            }
            int bottom;
            final boolean z3 = this.scrollDirection == 0;
            this.recyclerView.setScrollEnabled(false);
            final ArrayList arrayList = new ArrayList();
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = this.recyclerView.getChildAt(0);
                arrayList.add(childAt);
                bottom = childAt.getBottom();
                int top = childAt.getTop();
                if (bottom > i3) {
                    i3 = bottom;
                }
                if (top < i4) {
                    i4 = top;
                }
                if (childAt instanceof ChatMessageCell) {
                    ((ChatMessageCell) childAt).setAnimationRunning(true);
                }
                this.recyclerView.removeView(childAt);
            }
            if (z3) {
                bottom = i3;
            } else {
                bottom = this.recyclerView.getHeight() - i4;
            }
            Adapter adapter = this.recyclerView.getAdapter();
            AnimatableAdapter animatableAdapter = null;
            if (adapter instanceof AnimatableAdapter) {
                animatableAdapter = (AnimatableAdapter) adapter;
            }
            final AnimatableAdapter animatableAdapter2 = animatableAdapter;
            this.layoutManager.scrollToPositionWithOffset(i, i2, z);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            this.recyclerView.stopScroll();
            this.recyclerView.setVerticalScrollBarEnabled(false);
            AnimationCallback animationCallback = this.animationCallback;
            if (animationCallback != null) {
                animationCallback.onStartAnimation();
            }
            this.recyclerView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    ArrayList arrayList = new ArrayList();
                    RecyclerAnimationScrollHelper.this.recyclerView.stopScroll();
                    int childCount = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                    i2 = 0;
                    i3 = 0;
                    for (i = 0; i < childCount; i++) {
                        View childAt = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                        arrayList.add(childAt);
                        if (childAt.getTop() < i2) {
                            i2 = childAt.getTop();
                        }
                        if (childAt.getBottom() > i3) {
                            i3 = childAt.getBottom();
                        }
                        if (childAt instanceof ChatMessageCell) {
                            ((ChatMessageCell) childAt).setAnimationRunning(true);
                        }
                    }
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        View view2 = (View) it.next();
                        RecyclerAnimationScrollHelper.this.recyclerView.addView(view2);
                        if (view2 instanceof ChatMessageCell) {
                            ((ChatMessageCell) view2).setAnimationRunning(true);
                        }
                    }
                    RecyclerAnimationScrollHelper.this.recyclerView.animationRunning = true;
                    AnimatableAdapter animatableAdapter = animatableAdapter2;
                    if (animatableAdapter != null) {
                        animatableAdapter.onAnimationStart();
                    }
                    i7 = bottom + (z3 ? -i2 : i3 - RecyclerAnimationScrollHelper.this.recyclerView.getHeight());
                    ValueAnimator valueAnimator = RecyclerAnimationScrollHelper.this.animator;
                    if (valueAnimator != null) {
                        valueAnimator.removeAllListeners();
                        RecyclerAnimationScrollHelper.this.animator.cancel();
                    }
                    RecyclerAnimationScrollHelper.this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    RecyclerAnimationScrollHelper.this.animator.addUpdateListener(new -$$Lambda$RecyclerAnimationScrollHelper$1$M7MBIJuKHjMNW6Hbfy-3yn-W-EE(this, arrayList, z3, i7, arrayList));
                    RecyclerAnimationScrollHelper.this.animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            RecyclerAnimationScrollHelper.this.recyclerView.animationRunning = false;
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                View view = (View) it.next();
                                RecyclerAnimationScrollHelper.this.recyclerView.removeView(view);
                                if (view instanceof ChatMessageCell) {
                                    ((ChatMessageCell) view).setAnimationRunning(false);
                                }
                                view.setTranslationY(0.0f);
                            }
                            RecyclerAnimationScrollHelper.this.recyclerView.setVerticalScrollBarEnabled(true);
                            int childCount = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                View childAt = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                                if (childAt instanceof ChatMessageCell) {
                                    ((ChatMessageCell) childAt).setAnimationRunning(false);
                                }
                                childAt.setTranslationY(0.0f);
                            }
                            AnimatableAdapter animatableAdapter = animatableAdapter2;
                            if (animatableAdapter != null) {
                                animatableAdapter.onAnimationEnd();
                            }
                            AnimationCallback animationCallback = RecyclerAnimationScrollHelper.this.animationCallback;
                            if (animationCallback != null) {
                                animationCallback.onEndAnimation();
                            }
                            RecyclerAnimationScrollHelper.this.animator = null;
                        }
                    });
                    RecyclerAnimationScrollHelper.this.recyclerView.removeOnLayoutChangeListener(this);
                    RecyclerAnimationScrollHelper.this.animator.setDuration(Math.min((long) (((i7 / RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight()) + 1) * 200), 1300));
                    RecyclerAnimationScrollHelper.this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    RecyclerAnimationScrollHelper.this.animator.start();
                }

                public /* synthetic */ void lambda$onLayoutChange$0$RecyclerAnimationScrollHelper$1(ArrayList arrayList, boolean z, int i, ArrayList arrayList2, ValueAnimator valueAnimator) {
                    float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        View view = (View) it.next();
                        if (z) {
                            view.setTranslationY(((float) (-i)) * floatValue);
                        } else {
                            view.setTranslationY(((float) i) * floatValue);
                        }
                    }
                    it = arrayList2.iterator();
                    while (it.hasNext()) {
                        View view2 = (View) it.next();
                        if (z) {
                            view2.setTranslationY(((float) i) * (1.0f - floatValue));
                        } else {
                            view2.setTranslationY(((float) (-i)) * (1.0f - floatValue));
                        }
                    }
                    ScrollListener scrollListener = RecyclerAnimationScrollHelper.this.scrollListener;
                    if (scrollListener != null) {
                        scrollListener.onScroll();
                    }
                    RecyclerAnimationScrollHelper.this.recyclerView.invalidate();
                }
            });
        }
    }

    public void cancel() {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        clear();
    }

    private void clear() {
        this.recyclerView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView = this.recyclerView;
        recyclerListView.animationRunning = false;
        Adapter adapter = recyclerListView.getAdapter();
        if (adapter instanceof AnimatableAdapter) {
            ((AnimatableAdapter) adapter).onAnimationEnd();
        }
        this.animator = null;
        int childCount = this.recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.recyclerView.getChildAt(i);
            childAt.setTranslationY(0.0f);
            if (childAt instanceof ChatMessageCell) {
                ((ChatMessageCell) childAt).setAnimationRunning(false);
            }
        }
    }

    public void setScrollDirection(int i) {
        this.scrollDirection = i;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    public void setAnimationCallback(AnimationCallback animationCallback) {
        this.animationCallback = animationCallback;
    }

    public int getScrollDirection() {
        return this.scrollDirection;
    }
}
