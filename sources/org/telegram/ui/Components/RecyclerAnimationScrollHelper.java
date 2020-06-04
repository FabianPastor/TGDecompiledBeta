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
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;

public class RecyclerAnimationScrollHelper {
    /* access modifiers changed from: private */
    public AnimationCallback animationCallback;
    /* access modifiers changed from: private */
    public ValueAnimator animator;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public HashMap<Long, View> oldStableIds = new HashMap<>();
    public SparseArray<View> positionToOldView = new SparseArray<>();
    /* access modifiers changed from: private */
    public RecyclerListView recyclerView;
    private int scrollDirection;
    /* access modifiers changed from: private */
    public ScrollListener scrollListener;

    public static class AnimationCallback {
        public void onEndAnimation() {
            throw null;
        }

        public void onStartAnimation() {
        }
    }

    public interface ScrollListener {
        void onScroll();
    }

    public RecyclerAnimationScrollHelper(RecyclerListView recyclerListView, LinearLayoutManager linearLayoutManager) {
        this.recyclerView = recyclerListView;
        this.layoutManager = linearLayoutManager;
    }

    public void scrollToPosition(int i, int i2, boolean z, boolean z2) {
        RecyclerListView recyclerListView = this.recyclerView;
        if (recyclerListView.scrollAnimationRunning) {
            return;
        }
        if (recyclerListView.getItemAnimator() != null && this.recyclerView.getItemAnimator().isRunning()) {
            return;
        }
        if (!z2 || this.scrollDirection == -1) {
            this.layoutManager.scrollToPositionWithOffset(i, i2, z);
            return;
        }
        int childCount = this.recyclerView.getChildCount();
        if (childCount == 0) {
            this.layoutManager.scrollToPositionWithOffset(i, i2, z);
            return;
        }
        final boolean z3 = this.scrollDirection == 0;
        this.recyclerView.setScrollEnabled(false);
        final ArrayList arrayList = new ArrayList();
        this.positionToOldView.clear();
        final RecyclerView.Adapter adapter = this.recyclerView.getAdapter();
        this.oldStableIds.clear();
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.recyclerView.getChildAt(0);
            arrayList.add(childAt);
            int position = this.layoutManager.getPosition(childAt);
            this.positionToOldView.put(position, childAt);
            if (adapter != null && adapter.hasStableIds()) {
                if (childAt instanceof ChatMessageCell) {
                    this.oldStableIds.put(Long.valueOf((long) ((ChatMessageCell) childAt).getMessageObject().stableId), childAt);
                } else {
                    this.oldStableIds.put(Long.valueOf(adapter.getItemId(position)), childAt);
                }
            }
            if (childAt instanceof ChatMessageCell) {
                ((ChatMessageCell) childAt).setAnimationRunning(true, true);
            }
            this.layoutManager.removeAndRecycleView(childAt, this.recyclerView.mRecycler);
        }
        this.recyclerView.mRecycler.clear();
        this.recyclerView.getRecycledViewPool().clear();
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
        AnimationCallback animationCallback2 = this.animationCallback;
        if (animationCallback2 != null) {
            animationCallback2.onStartAnimation();
        }
        this.recyclerView.scrollAnimationRunning = true;
        if (animatableAdapter2 != null) {
            animatableAdapter2.onAnimationStart();
        }
        this.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                int height;
                View view2;
                ArrayList arrayList = new ArrayList();
                RecyclerAnimationScrollHelper.this.recyclerView.stopScroll();
                int childCount = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                int i9 = 0;
                int i10 = 0;
                int i11 = 0;
                int i12 = 0;
                for (int i13 = 0; i13 < childCount; i13++) {
                    View childAt = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i13);
                    arrayList.add(childAt);
                    if (childAt.getTop() < i10) {
                        i10 = childAt.getTop();
                    }
                    if (childAt.getBottom() > i11) {
                        i11 = childAt.getBottom();
                    }
                    boolean z = childAt instanceof ChatMessageCell;
                    if (z) {
                        ((ChatMessageCell) childAt).setAnimationRunning(true, false);
                    }
                    RecyclerView.Adapter adapter = adapter;
                    if (adapter != null && adapter.hasStableIds()) {
                        long itemId = adapter.getItemId(RecyclerAnimationScrollHelper.this.recyclerView.getChildAdapterPosition(childAt));
                        if (RecyclerAnimationScrollHelper.this.oldStableIds.containsKey(Long.valueOf(itemId)) && (view2 = (View) RecyclerAnimationScrollHelper.this.oldStableIds.get(Long.valueOf(itemId))) != null) {
                            if (z) {
                                ((ChatMessageCell) childAt).setAnimationRunning(false, false);
                            }
                            arrayList.remove(view2);
                            int top = childAt.getTop() - view2.getTop();
                            if (top != 0) {
                                i12 = top;
                            }
                        }
                    }
                }
                RecyclerAnimationScrollHelper.this.oldStableIds.clear();
                Iterator it = arrayList.iterator();
                int i14 = 0;
                while (it.hasNext()) {
                    View view3 = (View) it.next();
                    int bottom = view3.getBottom();
                    int top2 = view3.getTop();
                    if (bottom > i9) {
                        i9 = bottom;
                    }
                    if (top2 < i14) {
                        i14 = top2;
                    }
                    if (view3.getParent() == null) {
                        RecyclerAnimationScrollHelper.this.recyclerView.addView(view3);
                    }
                    if (view3 instanceof ChatMessageCell) {
                        ((ChatMessageCell) view3).setAnimationRunning(true, true);
                    }
                }
                if (arrayList.isEmpty()) {
                    height = Math.abs(i12);
                } else {
                    if (!z3) {
                        i9 = RecyclerAnimationScrollHelper.this.recyclerView.getHeight() - i14;
                    }
                    height = (z3 ? -i10 : i11 - RecyclerAnimationScrollHelper.this.recyclerView.getHeight()) + i9;
                }
                int i15 = height;
                if (RecyclerAnimationScrollHelper.this.animator != null) {
                    RecyclerAnimationScrollHelper.this.animator.removeAllListeners();
                    RecyclerAnimationScrollHelper.this.animator.cancel();
                }
                ValueAnimator unused = RecyclerAnimationScrollHelper.this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                RecyclerAnimationScrollHelper.this.animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(arrayList, z3, i15, arrayList) {
                    private final /* synthetic */ ArrayList f$1;
                    private final /* synthetic */ boolean f$2;
                    private final /* synthetic */ int f$3;
                    private final /* synthetic */ ArrayList f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        RecyclerAnimationScrollHelper.AnonymousClass1.this.lambda$onLayoutChange$0$RecyclerAnimationScrollHelper$1(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
                    }
                });
                RecyclerAnimationScrollHelper.this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        if (RecyclerAnimationScrollHelper.this.animator != null) {
                            RecyclerAnimationScrollHelper.this.recyclerView.scrollAnimationRunning = false;
                            Iterator it = arrayList.iterator();
                            while (it.hasNext()) {
                                View view = (View) it.next();
                                if (view instanceof ChatMessageCell) {
                                    ((ChatMessageCell) view).setAnimationRunning(false, true);
                                }
                                view.setTranslationY(0.0f);
                                RecyclerAnimationScrollHelper.this.recyclerView.removeView(view);
                            }
                            RecyclerAnimationScrollHelper.this.recyclerView.setVerticalScrollBarEnabled(true);
                            if (BuildVars.DEBUG_VERSION) {
                                if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getChildCount() != RecyclerAnimationScrollHelper.this.recyclerView.getChildCount()) {
                                    throw new RuntimeException("views count in child helper must be quals views count in recycler view");
                                } else if (RecyclerAnimationScrollHelper.this.recyclerView.mChildHelper.getHiddenChildCount() != 0) {
                                    throw new RuntimeException("hidden child count must be 0");
                                } else if (RecyclerAnimationScrollHelper.this.recyclerView.getCachedChildCount() != 0) {
                                    throw new RuntimeException("cached child count must be 0");
                                }
                            }
                            int childCount = RecyclerAnimationScrollHelper.this.recyclerView.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                View childAt = RecyclerAnimationScrollHelper.this.recyclerView.getChildAt(i);
                                if (childAt instanceof ChatMessageCell) {
                                    ((ChatMessageCell) childAt).setAnimationRunning(false, false);
                                }
                                childAt.setTranslationY(0.0f);
                            }
                            AnimatableAdapter animatableAdapter = animatableAdapter2;
                            if (animatableAdapter != null) {
                                animatableAdapter.onAnimationEnd();
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
                long measuredHeight = (long) (((((float) i15) / ((float) RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight())) + 1.0f) * 200.0f);
                if (measuredHeight < 80) {
                    measuredHeight = 80;
                }
                RecyclerAnimationScrollHelper.this.animator.setDuration(Math.min(measuredHeight, 1300));
                RecyclerAnimationScrollHelper.this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                RecyclerAnimationScrollHelper.this.animator.start();
            }

            public /* synthetic */ void lambda$onLayoutChange$0$RecyclerAnimationScrollHelper$1(ArrayList arrayList, boolean z, int i, ArrayList arrayList2, ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    View view = (View) arrayList.get(i2);
                    float y = view.getY();
                    if (view.getY() + ((float) view.getMeasuredHeight()) >= 0.0f && y <= ((float) RecyclerAnimationScrollHelper.this.recyclerView.getMeasuredHeight())) {
                        if (z) {
                            view.setTranslationY(((float) (-i)) * floatValue);
                        } else {
                            view.setTranslationY(((float) i) * floatValue);
                        }
                    }
                }
                int size2 = arrayList2.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    View view2 = (View) arrayList2.get(i3);
                    if (z) {
                        view2.setTranslationY(((float) i) * (1.0f - floatValue));
                    } else {
                        view2.setTranslationY(((float) (-i)) * (1.0f - floatValue));
                    }
                }
                RecyclerAnimationScrollHelper.this.recyclerView.invalidate();
                if (RecyclerAnimationScrollHelper.this.scrollListener != null) {
                    RecyclerAnimationScrollHelper.this.scrollListener.onScroll();
                }
            }
        });
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
        recyclerListView.scrollAnimationRunning = false;
        RecyclerView.Adapter adapter = recyclerListView.getAdapter();
        if (adapter instanceof AnimatableAdapter) {
            ((AnimatableAdapter) adapter).onAnimationEnd();
        }
        this.animator = null;
        int childCount = this.recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.recyclerView.getChildAt(i);
            childAt.setTranslationY(0.0f);
            if (childAt instanceof ChatMessageCell) {
                ((ChatMessageCell) childAt).setAnimationRunning(false, false);
            }
        }
    }

    public void setScrollDirection(int i) {
        this.scrollDirection = i;
    }

    public void setScrollListener(ScrollListener scrollListener2) {
        this.scrollListener = scrollListener2;
    }

    public void setAnimationCallback(AnimationCallback animationCallback2) {
        this.animationCallback = animationCallback2;
    }

    public int getScrollDirection() {
        return this.scrollDirection;
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

        public void notifyItemInserted(int i) {
            if (!this.animationRunning) {
                super.notifyItemInserted(i);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(i));
            this.rangeInserted.add(1);
        }

        public void notifyItemRangeInserted(int i, int i2) {
            if (!this.animationRunning) {
                super.notifyItemRangeInserted(i, i2);
                return;
            }
            this.rangeInserted.add(Integer.valueOf(i));
            this.rangeInserted.add(Integer.valueOf(i2));
        }

        public void notifyItemRemoved(int i) {
            if (!this.animationRunning) {
                super.notifyItemRemoved(i);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(i));
            this.rangeRemoved.add(1);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            if (!this.animationRunning) {
                super.notifyItemRangeRemoved(i, i2);
                return;
            }
            this.rangeRemoved.add(Integer.valueOf(i));
            this.rangeRemoved.add(Integer.valueOf(i2));
        }

        public void notifyItemChanged(int i) {
            if (!this.animationRunning) {
                super.notifyItemChanged(i);
            }
        }

        public void notifyItemRangeChanged(int i, int i2) {
            if (!this.animationRunning) {
                super.notifyItemRangeChanged(i, i2);
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
