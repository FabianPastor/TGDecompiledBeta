package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SideMenultItemAnimator extends SimpleItemAnimator {
    private static TimeInterpolator sDefaultInterpolator;
    ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList<>();
    ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList<>();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList<>();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList<>();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList<>();
    ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList<>();
    private RecyclerListView parentRecyclerView;
    private boolean shouldClipChildren;

    static /* synthetic */ int lambda$new$0(int i, int i2) {
        if (i2 == i - 1) {
            return 0;
        }
        return i2 >= 0 ? i2 + 1 : i2;
    }

    /* access modifiers changed from: protected */
    public void onAllAnimationsDone() {
    }

    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            this.holder = viewHolder;
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }
    }

    private static class ChangeInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder newHolder;
        public RecyclerView.ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            this.oldHolder = viewHolder;
            this.newHolder = viewHolder2;
        }

        ChangeInfo(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
            this(viewHolder, viewHolder2);
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    public SideMenultItemAnimator(RecyclerListView recyclerListView) {
        this.parentRecyclerView = recyclerListView;
        recyclerListView.setChildDrawingOrderCallback($$Lambda$SideMenultItemAnimator$3zsexel4aNKW4snpNImcgrRLPEo.INSTANCE);
    }

    public void runPendingAnimations() {
        boolean z = !this.mPendingRemovals.isEmpty();
        boolean z2 = !this.mPendingMoves.isEmpty();
        boolean z3 = !this.mPendingChanges.isEmpty();
        boolean z4 = !this.mPendingAdditions.isEmpty();
        if (z || z2 || z4 || z3) {
            int size = this.mPendingRemovals.size();
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                i += this.mPendingRemovals.get(i2).itemView.getMeasuredHeight();
            }
            int size2 = this.mPendingRemovals.size();
            for (int i3 = 0; i3 < size2; i3++) {
                animateRemoveImpl(this.mPendingRemovals.get(i3), i);
            }
            this.mPendingRemovals.clear();
            if (z2) {
                ArrayList arrayList = new ArrayList(this.mPendingMoves);
                this.mMovesList.add(arrayList);
                this.mPendingMoves.clear();
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    MoveInfo moveInfo = (MoveInfo) it.next();
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
                }
                arrayList.clear();
                this.mMovesList.remove(arrayList);
            }
            if (z3) {
                ArrayList arrayList2 = new ArrayList(this.mPendingChanges);
                this.mChangesList.add(arrayList2);
                this.mPendingChanges.clear();
                Iterator it2 = arrayList2.iterator();
                while (it2.hasNext()) {
                    animateChangeImpl((ChangeInfo) it2.next());
                }
                arrayList2.clear();
                this.mChangesList.remove(arrayList2);
            }
            if (z4) {
                ArrayList arrayList3 = new ArrayList(this.mPendingAdditions);
                this.mAdditionsList.add(arrayList3);
                this.mPendingAdditions.clear();
                int size3 = arrayList3.size();
                int i4 = 0;
                for (int i5 = 0; i5 < size3; i5++) {
                    i4 += ((RecyclerView.ViewHolder) arrayList3.get(i5)).itemView.getMeasuredHeight();
                }
                int size4 = arrayList3.size();
                for (int i6 = 0; i6 < size4; i6++) {
                    animateAddImpl((RecyclerView.ViewHolder) arrayList3.get(i6), i6, size4, i4);
                }
                arrayList3.clear();
                this.mAdditionsList.remove(arrayList3);
            }
            this.parentRecyclerView.invalidateViews();
            this.parentRecyclerView.invalidate();
        }
    }

    public boolean animateRemove(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo) {
        resetAnimation(viewHolder);
        this.mPendingRemovals.add(viewHolder);
        return true;
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder viewHolder, int i) {
        final ViewPropertyAnimator animate = viewHolder.itemView.animate();
        this.mRemoveAnimations.add(viewHolder);
        animate.setDuration(220).translationY((float) (-i)).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchRemoveStarting(viewHolder);
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchRemoveFinished(viewHolder);
                SideMenultItemAnimator.this.mRemoveAnimations.remove(viewHolder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingAdditions.add(viewHolder);
        viewHolder.itemView.setAlpha(0.0f);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateAddImpl(final RecyclerView.ViewHolder viewHolder, int i, int i2, int i3) {
        final View view = viewHolder.itemView;
        final ViewPropertyAnimator animate = view.animate();
        this.mAddAnimations.add(viewHolder);
        view.setAlpha(1.0f);
        view.setTranslationY((float) (-i3));
        animate.translationY(0.0f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchAddStarting(viewHolder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setTranslationY(0.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchAddFinished(viewHolder);
                SideMenultItemAnimator.this.mAddAnimations.remove(viewHolder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, int i, int i2, int i3, int i4) {
        View view = viewHolder.itemView;
        int translationX = i + ((int) view.getTranslationX());
        int translationY = i2 + ((int) viewHolder.itemView.getTranslationY());
        resetAnimation(viewHolder);
        int i5 = i3 - translationX;
        int i6 = i4 - translationY;
        if (i5 == 0 && i6 == 0) {
            dispatchMoveFinished(viewHolder);
            return false;
        }
        if (i5 != 0) {
            view.setTranslationX((float) (-i5));
        }
        if (i6 != 0) {
            view.setTranslationY((float) (-i6));
        }
        this.mPendingMoves.add(new MoveInfo(viewHolder, translationX, translationY, i3, i4));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateMoveImpl(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        final View view = viewHolder.itemView;
        final int i5 = i3 - i;
        final int i6 = i4 - i2;
        if (i5 != 0) {
            view.animate().translationX(0.0f);
        }
        if (i6 != 0) {
            view.animate().translationY(0.0f);
        }
        final ViewPropertyAnimator animate = view.animate();
        this.mMoveAnimations.add(viewHolder);
        final RecyclerView.ViewHolder viewHolder2 = viewHolder;
        animate.setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchMoveStarting(viewHolder2);
            }

            public void onAnimationCancel(Animator animator) {
                if (i5 != 0) {
                    view.setTranslationX(0.0f);
                }
                if (i6 != 0) {
                    view.setTranslationY(0.0f);
                }
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchMoveFinished(viewHolder2);
                SideMenultItemAnimator.this.mMoveAnimations.remove(viewHolder2);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo, int i, int i2, int i3, int i4) {
        if (viewHolder == viewHolder2) {
            return animateMove(viewHolder, (RecyclerView.ItemAnimator.ItemHolderInfo) null, i, i2, i3, i4);
        }
        float translationX = viewHolder.itemView.getTranslationX();
        float translationY = viewHolder.itemView.getTranslationY();
        float alpha = viewHolder.itemView.getAlpha();
        resetAnimation(viewHolder);
        int i5 = (int) (((float) (i3 - i)) - translationX);
        int i6 = (int) (((float) (i4 - i2)) - translationY);
        viewHolder.itemView.setTranslationX(translationX);
        viewHolder.itemView.setTranslationY(translationY);
        viewHolder.itemView.setAlpha(alpha);
        if (viewHolder2 != null) {
            resetAnimation(viewHolder2);
            viewHolder2.itemView.setTranslationX((float) (-i5));
            viewHolder2.itemView.setTranslationY((float) (-i6));
            viewHolder2.itemView.setAlpha(0.0f);
        }
        this.mPendingChanges.add(new ChangeInfo(viewHolder, viewHolder2, i, i2, i3, i4));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateChangeImpl(final ChangeInfo changeInfo) {
        final View view;
        RecyclerView.ViewHolder viewHolder = changeInfo.oldHolder;
        final View view2 = null;
        if (viewHolder == null) {
            view = null;
        } else {
            view = viewHolder.itemView;
        }
        RecyclerView.ViewHolder viewHolder2 = changeInfo.newHolder;
        if (viewHolder2 != null) {
            view2 = viewHolder2.itemView;
        }
        if (view != null) {
            final ViewPropertyAnimator duration = view.animate().setDuration(getChangeDuration());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            duration.translationX((float) (changeInfo.toX - changeInfo.fromX));
            duration.translationY((float) (changeInfo.toY - changeInfo.fromY));
            duration.alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    SideMenultItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                public void onAnimationEnd(Animator animator) {
                    duration.setListener((Animator.AnimatorListener) null);
                    view.setAlpha(1.0f);
                    view.setTranslationX(0.0f);
                    view.setTranslationY(0.0f);
                    SideMenultItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                    SideMenultItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                    SideMenultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
        if (view2 != null) {
            final ViewPropertyAnimator animate = view2.animate();
            this.mChangeAnimations.add(changeInfo.newHolder);
            animate.translationX(0.0f).translationY(0.0f).setDuration(getChangeDuration()).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    SideMenultItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                public void onAnimationEnd(Animator animator) {
                    animate.setListener((Animator.AnimatorListener) null);
                    view2.setAlpha(1.0f);
                    view2.setTranslationX(0.0f);
                    view2.setTranslationY(0.0f);
                    SideMenultItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                    SideMenultItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                    SideMenultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
    }

    private void endChangeAnimation(List<ChangeInfo> list, RecyclerView.ViewHolder viewHolder) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ChangeInfo changeInfo = list.get(size);
            if (endChangeAnimationIfNecessary(changeInfo, viewHolder) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                list.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        RecyclerView.ViewHolder viewHolder = changeInfo.oldHolder;
        if (viewHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder);
        }
        RecyclerView.ViewHolder viewHolder2 = changeInfo.newHolder;
        if (viewHolder2 != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder2);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder viewHolder) {
        boolean z = false;
        if (changeInfo.newHolder == viewHolder) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != viewHolder) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            z = true;
        }
        viewHolder.itemView.setAlpha(1.0f);
        viewHolder.itemView.setTranslationX(0.0f);
        viewHolder.itemView.setTranslationY(0.0f);
        dispatchChangeFinished(viewHolder, z);
        return true;
    }

    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        view.animate().cancel();
        int size = this.mPendingMoves.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (this.mPendingMoves.get(size).holder == viewHolder) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                dispatchMoveFinished(viewHolder);
                this.mPendingMoves.remove(size);
            }
        }
        endChangeAnimation(this.mPendingChanges, viewHolder);
        if (this.mPendingRemovals.remove(viewHolder)) {
            view.setTranslationY(0.0f);
            dispatchRemoveFinished(viewHolder);
        }
        if (this.mPendingAdditions.remove(viewHolder)) {
            view.setTranslationY(0.0f);
            dispatchAddFinished(viewHolder);
        }
        for (int size2 = this.mChangesList.size() - 1; size2 >= 0; size2--) {
            ArrayList arrayList = this.mChangesList.get(size2);
            endChangeAnimation(arrayList, viewHolder);
            if (arrayList.isEmpty()) {
                this.mChangesList.remove(size2);
            }
        }
        for (int size3 = this.mMovesList.size() - 1; size3 >= 0; size3--) {
            ArrayList arrayList2 = this.mMovesList.get(size3);
            int size4 = arrayList2.size() - 1;
            while (true) {
                if (size4 < 0) {
                    break;
                } else if (((MoveInfo) arrayList2.get(size4)).holder == viewHolder) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    dispatchMoveFinished(viewHolder);
                    arrayList2.remove(size4);
                    if (arrayList2.isEmpty()) {
                        this.mMovesList.remove(size3);
                    }
                } else {
                    size4--;
                }
            }
        }
        for (int size5 = this.mAdditionsList.size() - 1; size5 >= 0; size5--) {
            ArrayList arrayList3 = this.mAdditionsList.get(size5);
            if (arrayList3.remove(viewHolder)) {
                view.setTranslationY(0.0f);
                dispatchAddFinished(viewHolder);
                if (arrayList3.isEmpty()) {
                    this.mAdditionsList.remove(size5);
                }
            }
        }
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(RecyclerView.ViewHolder viewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        viewHolder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(viewHolder);
    }

    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() || !this.mPendingChanges.isEmpty() || !this.mPendingMoves.isEmpty() || !this.mPendingRemovals.isEmpty() || !this.mMoveAnimations.isEmpty() || !this.mRemoveAnimations.isEmpty() || !this.mAddAnimations.isEmpty() || !this.mChangeAnimations.isEmpty() || !this.mMovesList.isEmpty() || !this.mAdditionsList.isEmpty() || !this.mChangesList.isEmpty();
    }

    public boolean isAnimatingChild(View view) {
        if (!this.shouldClipChildren) {
            return false;
        }
        int size = this.mRemoveAnimations.size();
        for (int i = 0; i < size; i++) {
            if (this.mRemoveAnimations.get(i).itemView == view) {
                return true;
            }
        }
        int size2 = this.mAddAnimations.size();
        for (int i2 = 0; i2 < size2; i2++) {
            if (this.mAddAnimations.get(i2).itemView == view) {
                return true;
            }
        }
        return false;
    }

    public void setShouldClipChildren(boolean z) {
        this.shouldClipChildren = z;
    }

    public int getAnimationClipTop() {
        int i = 0;
        if (!this.shouldClipChildren) {
            return 0;
        }
        int i2 = Integer.MAX_VALUE;
        if (!this.mRemoveAnimations.isEmpty()) {
            int size = this.mRemoveAnimations.size();
            while (i < size) {
                i2 = Math.min(i2, this.mRemoveAnimations.get(i).itemView.getTop());
                i++;
            }
            return i2;
        } else if (this.mAddAnimations.isEmpty()) {
            return 0;
        } else {
            int size2 = this.mAddAnimations.size();
            while (i < size2) {
                i2 = Math.min(i2, this.mAddAnimations.get(i).itemView.getTop());
                i++;
            }
            return i2;
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            onAllAnimationsDone();
        }
    }

    public void endAnimations() {
        int size = this.mPendingMoves.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MoveInfo moveInfo = this.mPendingMoves.get(size);
            View view = moveInfo.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(moveInfo.holder);
            this.mPendingMoves.remove(size);
        }
        for (int size2 = this.mPendingRemovals.size() - 1; size2 >= 0; size2--) {
            dispatchRemoveFinished(this.mPendingRemovals.get(size2));
            this.mPendingRemovals.remove(size2);
        }
        for (int size3 = this.mPendingAdditions.size() - 1; size3 >= 0; size3--) {
            RecyclerView.ViewHolder viewHolder = this.mPendingAdditions.get(size3);
            viewHolder.itemView.setTranslationY(0.0f);
            dispatchAddFinished(viewHolder);
            this.mPendingAdditions.remove(size3);
        }
        for (int size4 = this.mPendingChanges.size() - 1; size4 >= 0; size4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(size4));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            for (int size5 = this.mMovesList.size() - 1; size5 >= 0; size5--) {
                ArrayList arrayList = this.mMovesList.get(size5);
                for (int size6 = arrayList.size() - 1; size6 >= 0; size6--) {
                    MoveInfo moveInfo2 = (MoveInfo) arrayList.get(size6);
                    View view2 = moveInfo2.holder.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    dispatchMoveFinished(moveInfo2.holder);
                    arrayList.remove(size6);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(arrayList);
                    }
                }
            }
            for (int size7 = this.mAdditionsList.size() - 1; size7 >= 0; size7--) {
                ArrayList arrayList2 = this.mAdditionsList.get(size7);
                for (int size8 = arrayList2.size() - 1; size8 >= 0; size8--) {
                    RecyclerView.ViewHolder viewHolder2 = (RecyclerView.ViewHolder) arrayList2.get(size8);
                    viewHolder2.itemView.setTranslationY(0.0f);
                    dispatchAddFinished(viewHolder2);
                    arrayList2.remove(size8);
                    if (arrayList2.isEmpty()) {
                        this.mAdditionsList.remove(arrayList2);
                    }
                }
            }
            for (int size9 = this.mChangesList.size() - 1; size9 >= 0; size9--) {
                ArrayList arrayList3 = this.mChangesList.get(size9);
                for (int size10 = arrayList3.size() - 1; size10 >= 0; size10--) {
                    endChangeAnimationIfNecessary((ChangeInfo) arrayList3.get(size10));
                    if (arrayList3.isEmpty()) {
                        this.mChangesList.remove(arrayList3);
                    }
                }
            }
            cancelAll(this.mRemoveAnimations);
            cancelAll(this.mMoveAnimations);
            cancelAll(this.mAddAnimations);
            cancelAll(this.mChangeAnimations);
            dispatchAnimationsFinished();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAll(List<RecyclerView.ViewHolder> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            list.get(size).itemView.animate().cancel();
        }
    }

    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> list) {
        return !list.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, list);
    }
}
