package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.RecyclerListView;

public class RecyclerAnimationScrollHelper {
    public static final int SCROLL_DIRECTION_DOWN = 0;
    public static final int SCROLL_DIRECTION_UNSET = -1;
    public static final int SCROLL_DIRECTION_UP = 1;
    /* access modifiers changed from: private */
    public AnimationCallback animationCallback;
    /* access modifiers changed from: private */
    public ValueAnimator animator;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public HashMap<Long, View> oldStableIds = new HashMap<>();
    public SparseArray<View> positionToOldView = new SparseArray<>();
    /* access modifiers changed from: private */
    public RecyclerListView recyclerView;
    private int scrollDirection;
    /* access modifiers changed from: private */
    public ScrollListener scrollListener;

    public interface ScrollListener {
        void onScroll();
    }

    public RecyclerAnimationScrollHelper(RecyclerListView recyclerView2, LinearLayoutManager layoutManager2) {
        this.recyclerView = recyclerView2;
        this.layoutManager = layoutManager2;
    }

    public void scrollToPosition(int position, int offset) {
        scrollToPosition(position, offset, this.layoutManager.getReverseLayout(), false);
    }

    public void scrollToPosition(int position, int offset, boolean bottom) {
        scrollToPosition(position, offset, bottom, false);
    }

    public void scrollToPosition(int position, int offset, boolean bottom, boolean smooth) {
        AnimatableAdapter animatableAdapter;
        int i = position;
        int i2 = offset;
        boolean z = bottom;
        if (this.recyclerView.fastScrollAnimationRunning) {
            return;
        }
        if (this.recyclerView.getItemAnimator() != null && this.recyclerView.getItemAnimator().isRunning()) {
            return;
        }
        if (!smooth || this.scrollDirection == -1) {
            this.layoutManager.scrollToPositionWithOffset(i, i2, z);
            return;
        }
        int n = this.recyclerView.getChildCount();
        if (n == 0) {
        } else if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            int i3 = n;
        } else {
            final boolean scrollDown = this.scrollDirection == 0;
            this.recyclerView.setScrollEnabled(false);
            ArrayList<View> oldViews = new ArrayList<>();
            this.positionToOldView.clear();
            RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
            this.oldStableIds.clear();
            for (int i4 = 0; i4 < n; i4++) {
                View child = this.recyclerView.getChildAt(i4);
                oldViews.add(child);
                this.positionToOldView.put(this.layoutManager.getPosition(child), child);
                if (adapter != null && adapter.hasStableIds()) {
                    this.oldStableIds.put(Long.valueOf(((RecyclerView.LayoutParams) child.getLayoutParams()).mViewHolder.getItemId()), child);
                }
                if (child instanceof ChatMessageCell) {
                    ((ChatMessageCell) child).setAnimationRunning(true, true);
                }
            }
            this.recyclerView.prepareForFastScroll();
            if (adapter instanceof AnimatableAdapter) {
                animatableAdapter = (AnimatableAdapter) adapter;
            } else {
                animatableAdapter = null;
            }
            this.layoutManager.scrollToPositionWithOffset(i, i2, z);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            AnimatableAdapter finalAnimatableAdapter = animatableAdapter;
            this.recyclerView.stopScroll();
            this.recyclerView.setVerticalScrollBarEnabled(false);
            AnimationCallback animationCallback2 = this.animationCallback;
            if (animationCallback2 != null) {
                animationCallback2.onStartAnimation();
            }
            this.recyclerView.fastScrollAnimationRunning = true;
            if (finalAnimatableAdapter != null) {
                finalAnimatableAdapter.onAnimationStart();
            }
            RecyclerListView recyclerListView = this.recyclerView;
            final RecyclerView.Adapter adapter2 = adapter;
            final ArrayList<View> arrayList = oldViews;
            int i5 = n;
            AnonymousClass1 r10 = r0;
            final AnimatableAdapter animatableAdapter2 = finalAnimatableAdapter;
            AnonymousClass1 r0 = new View.OnLayoutChangeListener() {
                public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
                    int oldT;
                    int scrollLength;
                    long duration;
                    View view;
                    final ArrayList<View> incomingViews = new ArrayList<>();
                    RecyclerAnimationScrollHelper.this.recyclerView.stopScroll();
                    int n = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                    int top = 0;
                    int bottom = 0;
                    int scrollDiff = 0;
                    boolean hasSameViews = false;
                    for (int i = 0; i < n; i++) {
                        View child = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                        incomingViews.add(child);
                        if (child.getTop() < top) {
                            top = child.getTop();
                        }
                        if (child.getBottom() > bottom) {
                            bottom = child.getBottom();
                        }
                        if (child instanceof ChatMessageCell) {
                            ((ChatMessageCell) child).setAnimationRunning(true, false);
                        }
                        RecyclerView.Adapter adapter = adapter2;
                        if (adapter != null && adapter.hasStableIds()) {
                            long stableId = adapter2.getItemId(RecyclerAnimationScrollHelper.this.recyclerView.getChildAdapterPosition(child));
                            if (RecyclerAnimationScrollHelper.this.oldStableIds.containsKey(Long.valueOf(stableId)) && (view = (View) RecyclerAnimationScrollHelper.this.oldStableIds.get(Long.valueOf(stableId))) != null) {
                                if (view instanceof ChatMessageCell) {
                                    ((ChatMessageCell) view).setAnimationRunning(false, false);
                                }
                                arrayList.remove(view);
                                if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                                    RecyclerAnimationScrollHelper.this.animationCallback.recycleView(view);
                                }
                                int dif = child.getTop() - view.getTop();
                                if (dif != 0) {
                                    hasSameViews = true;
                                    scrollDiff = dif;
                                } else {
                                    hasSameViews = true;
                                }
                            }
                        }
                    }
                    RecyclerAnimationScrollHelper.this.oldStableIds.clear();
                    int oldT2 = Integer.MAX_VALUE;
                    Iterator it = arrayList.iterator();
                    int oldH = 0;
                    while (it.hasNext() != 0) {
                        View view2 = (View) it.next();
                        int bot = view2.getBottom();
                        int topl = view2.getTop();
                        if (bot > oldH) {
                            oldH = bot;
                        }
                        if (topl < oldT2) {
                            oldT2 = topl;
                        }
                        if (view2.getParent() == null) {
                            RecyclerAnimationScrollHelper.this.recyclerView.addView(view2);
                            RecyclerAnimationScrollHelper.this.layoutManager.ignoreView(view2);
                        }
                        if (view2 instanceof ChatMessageCell) {
                            ((ChatMessageCell) view2).setAnimationRunning(true, true);
                        }
                    }
                    if (oldT2 == Integer.MAX_VALUE) {
                        oldT = 0;
                    } else {
                        oldT = oldT2;
                    }
                    if (arrayList.isEmpty()) {
                        scrollLength = Math.abs(scrollDiff);
                    } else {
                        scrollLength = (scrollDown ? -top : bottom - RecyclerAnimationScrollHelper.this.recyclerView.getHeight()) + (scrollDown != 0 ? oldH : RecyclerAnimationScrollHelper.this.recyclerView.getHeight() - oldT);
                    }
                    if (RecyclerAnimationScrollHelper.this.animator != null) {
                        RecyclerAnimationScrollHelper.this.animator.removeAllListeners();
                        RecyclerAnimationScrollHelper.this.animator.cancel();
                    }
                    ValueAnimator unused = RecyclerAnimationScrollHelper.this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    int i2 = n;
                    RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0 recyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0 = r0;
                    int i3 = top;
                    RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0 recyclerAnimationScrollHelper$1$$ExternalSyntheticLambda02 = new RecyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0(this, arrayList, scrollDown, scrollLength, incomingViews);
                    RecyclerAnimationScrollHelper.this.animator.addUpdateListener(recyclerAnimationScrollHelper$1$$ExternalSyntheticLambda0);
                    RecyclerAnimationScrollHelper.this.animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (RecyclerAnimationScrollHelper.this.animator != null) {
                                RecyclerAnimationScrollHelper.this.recyclerView.fastScrollAnimationRunning = false;
                                Iterator it = arrayList.iterator();
                                while (it.hasNext()) {
                                    View view = (View) it.next();
                                    if (view instanceof ChatMessageCell) {
                                        ((ChatMessageCell) view).setAnimationRunning(false, true);
                                    }
                                    view.setTranslationY(0.0f);
                                    RecyclerAnimationScrollHelper.this.layoutManager.stopIgnoringView(view);
                                    RecyclerAnimationScrollHelper.this.recyclerView.removeView(view);
                                    if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                                        RecyclerAnimationScrollHelper.this.animationCallback.recycleView(view);
                                    }
                                }
                                RecyclerAnimationScrollHelper.this.recyclerView.setScrollEnabled(true);
                                RecyclerAnimationScrollHelper.this.recyclerView.setVerticalScrollBarEnabled(true);
                                if (BuildVars.DEBUG_PRIVATE_VERSION) {
                                    if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getChildCount() != RecyclerAnimationScrollHelper.this.recyclerView.getChildCount()) {
                                        throw new RuntimeException("views count in child helper must be quals views count in recycler view");
                                    } else if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getHiddenChildCount() != 0) {
                                        throw new RuntimeException("hidden child count must be 0");
                                    }
                                }
                                int n = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                                for (int i = 0; i < n; i++) {
                                    View child = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                                    if (child instanceof ChatMessageCell) {
                                        ((ChatMessageCell) child).setAnimationRunning(false, false);
                                    }
                                    child.setTranslationY(0.0f);
                                }
                                Iterator it2 = incomingViews.iterator();
                                while (it2.hasNext()) {
                                    View v = (View) it2.next();
                                    if (v instanceof ChatMessageCell) {
                                        ((ChatMessageCell) v).setAnimationRunning(false, false);
                                    }
                                    v.setTranslationY(0.0f);
                                }
                                if (animatableAdapter2 != null) {
                                    animatableAdapter2.onAnimationEnd();
                                }
                                if (RecyclerAnimationScrollHelper.this.animationCallback != null) {
                                    RecyclerAnimationScrollHelper.this.animationCallback.onEndAnimation();
                                }
                                RecyclerAnimationScrollHelper.this.positionToOldView.clear();
                                ValueAnimator unused = RecyclerAnimationScrollHelper.this.animator = null;
                            }
                        }
                    });
                    RecyclerAnimationScrollHelper.this.recyclerView.removeOnLayoutChangeListener(this);
                    if (hasSameViews) {
                        duration = 600;
                    } else {
                        long duration2 = (long) (((((float) scrollLength) / ((float) RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight())) + 1.0f) * 200.0f);
                        if (duration2 < 300) {
                            duration2 = 300;
                        }
                        duration = Math.min(duration2, 1300);
                    }
                    RecyclerAnimationScrollHelper.this.animator.setDuration(duration);
                    RecyclerAnimationScrollHelper.this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    RecyclerAnimationScrollHelper.this.animator.start();
                }

                /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-RecyclerAnimationScrollHelper$1  reason: not valid java name */
                public /* synthetic */ void m1304xb4285cce(ArrayList oldViews, boolean scrollDown, int scrollLength, ArrayList incomingViews, ValueAnimator animation) {
                    float value = ((Float) animation.getAnimatedValue()).floatValue();
                    int size = oldViews.size();
                    for (int i = 0; i < size; i++) {
                        View view = (View) oldViews.get(i);
                        float viewTop = view.getY();
                        if (view.getY() + ((float) view.getMeasuredHeight()) >= 0.0f && viewTop <= ((float) RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight())) {
                            if (scrollDown) {
                                view.setTranslationY(((float) (-scrollLength)) * value);
                            } else {
                                view.setTranslationY(((float) scrollLength) * value);
                            }
                        }
                    }
                    int size2 = incomingViews.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        View view2 = (View) incomingViews.get(i2);
                        if (scrollDown) {
                            view2.setTranslationY(((float) scrollLength) * (1.0f - value));
                        } else {
                            view2.setTranslationY(((float) (-scrollLength)) * (1.0f - value));
                        }
                    }
                    RecyclerAnimationScrollHelper.this.recyclerView.invalidate();
                    if (RecyclerAnimationScrollHelper.this.scrollListener != null) {
                        RecyclerAnimationScrollHelper.this.scrollListener.onScroll();
                    }
                }
            };
            recyclerListView.addOnLayoutChangeListener(r10);
            return;
        }
        this.layoutManager.scrollToPositionWithOffset(i, i2, z);
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
        this.recyclerView.fastScrollAnimationRunning = false;
        RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
        if (adapter instanceof AnimatableAdapter) {
            ((AnimatableAdapter) adapter).onAnimationEnd();
        }
        this.animator = null;
        int n = this.recyclerView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = this.recyclerView.getChildAt(i);
            child.setTranslationY(0.0f);
            if (child instanceof ChatMessageCell) {
                ((ChatMessageCell) child).setAnimationRunning(false, false);
            }
        }
    }

    public void setScrollDirection(int scrollDirection2) {
        this.scrollDirection = scrollDirection2;
    }

    public void setScrollListener(ScrollListener listener) {
        this.scrollListener = listener;
    }

    public void setAnimationCallback(AnimationCallback animationCallback2) {
        this.animationCallback = animationCallback2;
    }

    public int getScrollDirection() {
        return this.scrollDirection;
    }

    public static class AnimationCallback {
        public void onStartAnimation() {
        }

        public void onEndAnimation() {
        }

        public void recycleView(View view) {
        }
    }

    public static abstract class AnimatableAdapter extends RecyclerListView.SelectionAdapter {
        public boolean animationRunning;
        private ArrayList<Integer> rangeInserted = new ArrayList<>();
        private ArrayList<Integer> rangeRemoved = new ArrayList<>();
        private boolean shouldNotifyDataSetChanged;

        public void notifyDataSetChanged() {
            if (!this.animationRunning) {
                super.notifyDataSetChanged();
            } else {
                this.shouldNotifyDataSetChanged = true;
            }
        }

        public void notifyItemInserted(int position) {
            if (!this.animationRunning) {
                super.notifyItemInserted(position);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(position));
            this.rangeInserted.add(1);
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeInserted(positionStart, itemCount);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(positionStart));
            this.rangeInserted.add(Integer.valueOf(itemCount));
        }

        public void notifyItemRemoved(int position) {
            if (!this.animationRunning) {
                super.notifyItemRemoved(position);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(position));
            this.rangeRemoved.add(1);
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeRemoved(positionStart, itemCount);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(positionStart));
            this.rangeRemoved.add(Integer.valueOf(itemCount));
        }

        public void notifyItemChanged(int position) {
            if (!this.animationRunning) {
                super.notifyItemChanged(position);
            }
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            if (!this.animationRunning) {
                super.notifyItemRangeChanged(positionStart, itemCount);
            }
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
}
