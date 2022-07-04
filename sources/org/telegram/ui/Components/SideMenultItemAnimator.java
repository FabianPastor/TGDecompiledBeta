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

    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(RecyclerView.ViewHolder holder2, int fromX2, int fromY2, int toX2, int toY2) {
            this.holder = holder2;
            this.fromX = fromX2;
            this.fromY = fromY2;
            this.toX = toX2;
            this.toY = toY2;
        }
    }

    private static class ChangeInfo {
        public int fromX;
        public int fromY;
        public RecyclerView.ViewHolder newHolder;
        public RecyclerView.ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(RecyclerView.ViewHolder oldHolder2, RecyclerView.ViewHolder newHolder2) {
            this.oldHolder = oldHolder2;
            this.newHolder = newHolder2;
        }

        ChangeInfo(RecyclerView.ViewHolder oldHolder2, RecyclerView.ViewHolder newHolder2, int fromX2, int fromY2, int toX2, int toY2) {
            this(oldHolder2, newHolder2);
            this.fromX = fromX2;
            this.fromY = fromY2;
            this.toX = toX2;
            this.toY = toY2;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    public SideMenultItemAnimator(RecyclerListView view) {
        this.parentRecyclerView = view;
        view.setChildDrawingOrderCallback(SideMenultItemAnimator$$ExternalSyntheticLambda0.INSTANCE);
    }

    static /* synthetic */ int lambda$new$0(int childCount, int i) {
        int childPos = i;
        if (i == childCount - 1) {
            return 0;
        }
        if (i >= 0) {
            return i + 1;
        }
        return childPos;
    }

    public void runPendingAnimations() {
        boolean removalsPending = !this.mPendingRemovals.isEmpty();
        boolean movesPending = !this.mPendingMoves.isEmpty();
        boolean changesPending = !this.mPendingChanges.isEmpty();
        boolean additionsPending = !this.mPendingAdditions.isEmpty();
        if (removalsPending || movesPending || additionsPending || changesPending) {
            int animatingHeight = 0;
            int N = this.mPendingRemovals.size();
            for (int a = 0; a < N; a++) {
                animatingHeight += this.mPendingRemovals.get(a).itemView.getMeasuredHeight();
            }
            int N2 = this.mPendingRemovals.size();
            for (int a2 = 0; a2 < N2; a2++) {
                animateRemoveImpl(this.mPendingRemovals.get(a2), animatingHeight);
            }
            this.mPendingRemovals.clear();
            if (movesPending) {
                ArrayList<MoveInfo> moves = new ArrayList<>(this.mPendingMoves);
                this.mMovesList.add(moves);
                this.mPendingMoves.clear();
                Iterator<MoveInfo> it = moves.iterator();
                while (it.hasNext()) {
                    MoveInfo moveInfo = it.next();
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
                }
                moves.clear();
                this.mMovesList.remove(moves);
            }
            if (changesPending) {
                ArrayList<ChangeInfo> changes = new ArrayList<>(this.mPendingChanges);
                this.mChangesList.add(changes);
                this.mPendingChanges.clear();
                Iterator<ChangeInfo> it2 = changes.iterator();
                while (it2.hasNext()) {
                    animateChangeImpl(it2.next());
                }
                changes.clear();
                this.mChangesList.remove(changes);
            }
            if (additionsPending) {
                ArrayList<RecyclerView.ViewHolder> additions = new ArrayList<>(this.mPendingAdditions);
                this.mAdditionsList.add(additions);
                this.mPendingAdditions.clear();
                int animatingHeight2 = 0;
                int N3 = additions.size();
                for (int a3 = 0; a3 < N3; a3++) {
                    animatingHeight2 += additions.get(a3).itemView.getMeasuredHeight();
                }
                int N4 = additions.size();
                for (int a4 = 0; a4 < N4; a4++) {
                    animateAddImpl(additions.get(a4), a4, N4, animatingHeight2);
                }
                additions.clear();
                this.mAdditionsList.remove(additions);
            }
            this.parentRecyclerView.invalidateViews();
            this.parentRecyclerView.invalidate();
        }
    }

    public boolean animateRemove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info) {
        resetAnimation(holder);
        this.mPendingRemovals.add(holder);
        return true;
    }

    private void animateRemoveImpl(final RecyclerView.ViewHolder holder, int totalHeight) {
        final ViewPropertyAnimator animation = holder.itemView.animate();
        this.mRemoveAnimations.add(holder);
        animation.setDuration(220).translationY((float) (-totalHeight)).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchRemoveStarting(holder);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchRemoveFinished(holder);
                SideMenultItemAnimator.this.mRemoveAnimations.remove(holder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        resetAnimation(holder);
        this.mPendingAdditions.add(holder);
        holder.itemView.setAlpha(0.0f);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateAddImpl(final RecyclerView.ViewHolder holder, int num, int addCount, int totalHeight) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.mAddAnimations.add(holder);
        view.setAlpha(1.0f);
        view.setTranslationY((float) (-totalHeight));
        animation.translationY(0.0f).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchAddStarting(holder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setTranslationY(0.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchAddFinished(holder);
                SideMenultItemAnimator.this.mAddAnimations.remove(holder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateMove(RecyclerView.ViewHolder holder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        RecyclerView.ViewHolder viewHolder = holder;
        View view = viewHolder.itemView;
        int fromX2 = fromX + ((int) viewHolder.itemView.getTranslationX());
        int fromY2 = fromY + ((int) viewHolder.itemView.getTranslationY());
        resetAnimation(holder);
        int deltaX = toX - fromX2;
        int deltaY = toY - fromY2;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaX != 0) {
            view.setTranslationX((float) (-deltaX));
        }
        if (deltaY != 0) {
            view.setTranslationY((float) (-deltaY));
        }
        this.mPendingMoves.add(new MoveInfo(holder, fromX2, fromY2, toX, toY));
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateMoveImpl(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        RecyclerView.ViewHolder viewHolder = holder;
        View view = viewHolder.itemView;
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX != 0) {
            view.animate().translationX(0.0f);
        }
        if (deltaY != 0) {
            view.animate().translationY(0.0f);
        }
        ViewPropertyAnimator animation = view.animate();
        this.mMoveAnimations.add(viewHolder);
        final RecyclerView.ViewHolder viewHolder2 = holder;
        final int i = deltaX;
        final View view2 = view;
        final int i2 = deltaY;
        final ViewPropertyAnimator viewPropertyAnimator = animation;
        animation.setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchMoveStarting(viewHolder2);
            }

            public void onAnimationCancel(Animator animator) {
                if (i != 0) {
                    view2.setTranslationX(0.0f);
                }
                if (i2 != 0) {
                    view2.setTranslationY(0.0f);
                }
            }

            public void onAnimationEnd(Animator animator) {
                viewPropertyAnimator.setListener((Animator.AnimatorListener) null);
                SideMenultItemAnimator.this.dispatchMoveFinished(viewHolder2);
                SideMenultItemAnimator.this.mMoveAnimations.remove(viewHolder2);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, RecyclerView.ItemAnimator.ItemHolderInfo info, int fromX, int fromY, int toX, int toY) {
        RecyclerView.ViewHolder viewHolder = oldHolder;
        RecyclerView.ViewHolder viewHolder2 = newHolder;
        if (viewHolder == viewHolder2) {
            return animateMove(oldHolder, (RecyclerView.ItemAnimator.ItemHolderInfo) null, fromX, fromY, toX, toY);
        }
        float prevTranslationX = viewHolder.itemView.getTranslationX();
        float prevTranslationY = viewHolder.itemView.getTranslationY();
        float prevAlpha = viewHolder.itemView.getAlpha();
        resetAnimation(oldHolder);
        int deltaX = (int) (((float) (toX - fromX)) - prevTranslationX);
        int deltaY = (int) (((float) (toY - fromY)) - prevTranslationY);
        viewHolder.itemView.setTranslationX(prevTranslationX);
        viewHolder.itemView.setTranslationY(prevTranslationY);
        viewHolder.itemView.setAlpha(prevAlpha);
        if (viewHolder2 != null) {
            resetAnimation(viewHolder2);
            viewHolder2.itemView.setTranslationX((float) (-deltaX));
            viewHolder2.itemView.setTranslationY((float) (-deltaY));
            viewHolder2.itemView.setAlpha(0.0f);
        }
        ArrayList<ChangeInfo> arrayList = this.mPendingChanges;
        ChangeInfo changeInfo = r0;
        ChangeInfo changeInfo2 = new ChangeInfo(oldHolder, newHolder, fromX, fromY, toX, toY);
        arrayList.add(changeInfo);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void animateChangeImpl(final ChangeInfo changeInfo) {
        RecyclerView.ViewHolder holder = changeInfo.oldHolder;
        final View newView = null;
        final View view = holder == null ? null : holder.itemView;
        RecyclerView.ViewHolder newHolder = changeInfo.newHolder;
        if (newHolder != null) {
            newView = newHolder.itemView;
        }
        if (view != null) {
            final ViewPropertyAnimator oldViewAnim = view.animate().setDuration(getChangeRemoveDuration());
            this.mChangeAnimations.add(changeInfo.oldHolder);
            oldViewAnim.translationX((float) (changeInfo.toX - changeInfo.fromX));
            oldViewAnim.translationY((float) (changeInfo.toY - changeInfo.fromY));
            oldViewAnim.alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    SideMenultItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                }

                public void onAnimationEnd(Animator animator) {
                    oldViewAnim.setListener((Animator.AnimatorListener) null);
                    view.setAlpha(1.0f);
                    view.setTranslationX(0.0f);
                    view.setTranslationY(0.0f);
                    SideMenultItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                    SideMenultItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                    SideMenultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
        if (newView != null) {
            final ViewPropertyAnimator newViewAnimation = newView.animate();
            this.mChangeAnimations.add(changeInfo.newHolder);
            newViewAnimation.translationX(0.0f).translationY(0.0f).setDuration(getChangeAddDuration()).setStartDelay(getChangeDuration() - getChangeAddDuration()).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    SideMenultItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                public void onAnimationEnd(Animator animator) {
                    newViewAnimation.setListener((Animator.AnimatorListener) null);
                    newView.setAlpha(1.0f);
                    newView.setTranslationX(0.0f);
                    newView.setTranslationY(0.0f);
                    SideMenultItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                    SideMenultItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                    SideMenultItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, RecyclerView.ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = infoList.get(i);
            if (endChangeAnimationIfNecessary(changeInfo, item) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                infoList.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        if (changeInfo.oldHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.oldHolder);
        }
        if (changeInfo.newHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, changeInfo.newHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, RecyclerView.ViewHolder item) {
        boolean oldItem = false;
        if (changeInfo.newHolder == item) {
            changeInfo.newHolder = null;
        } else if (changeInfo.oldHolder != item) {
            return false;
        } else {
            changeInfo.oldHolder = null;
            oldItem = true;
        }
        item.itemView.setAlpha(1.0f);
        item.itemView.setTranslationX(0.0f);
        item.itemView.setTranslationY(0.0f);
        dispatchChangeFinished(item, oldItem);
        return true;
    }

    public void endAnimation(RecyclerView.ViewHolder item) {
        View view = item.itemView;
        view.animate().cancel();
        int i = this.mPendingMoves.size();
        while (true) {
            i--;
            if (i < 0) {
                break;
            } else if (this.mPendingMoves.get(i).holder == item) {
                view.setTranslationY(0.0f);
                view.setTranslationX(0.0f);
                dispatchMoveFinished(item);
                this.mPendingMoves.remove(i);
            }
        }
        endChangeAnimation(this.mPendingChanges, item);
        if (this.mPendingRemovals.remove(item)) {
            view.setTranslationY(0.0f);
            dispatchRemoveFinished(item);
        }
        if (this.mPendingAdditions.remove(item)) {
            view.setTranslationY(0.0f);
            dispatchAddFinished(item);
        }
        for (int i2 = this.mChangesList.size() - 1; i2 >= 0; i2--) {
            ArrayList<ChangeInfo> changes = this.mChangesList.get(i2);
            endChangeAnimation(changes, item);
            if (changes.isEmpty()) {
                this.mChangesList.remove(i2);
            }
        }
        for (int i3 = this.mMovesList.size() - 1; i3 >= 0; i3--) {
            ArrayList<MoveInfo> moves = this.mMovesList.get(i3);
            int j = moves.size() - 1;
            while (true) {
                if (j < 0) {
                    break;
                } else if (moves.get(j).holder == item) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    dispatchMoveFinished(item);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(i3);
                    }
                } else {
                    j--;
                }
            }
        }
        for (int i4 = this.mAdditionsList.size() - 1; i4 >= 0; i4--) {
            ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i4);
            if (additions.remove(item)) {
                view.setTranslationY(0.0f);
                dispatchAddFinished(item);
                if (additions.isEmpty()) {
                    this.mAdditionsList.remove(i4);
                }
            }
        }
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(RecyclerView.ViewHolder holder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        holder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(holder);
    }

    public boolean isRunning() {
        return !this.mPendingAdditions.isEmpty() || !this.mPendingChanges.isEmpty() || !this.mPendingMoves.isEmpty() || !this.mPendingRemovals.isEmpty() || !this.mMoveAnimations.isEmpty() || !this.mRemoveAnimations.isEmpty() || !this.mAddAnimations.isEmpty() || !this.mChangeAnimations.isEmpty() || !this.mMovesList.isEmpty() || !this.mAdditionsList.isEmpty() || !this.mChangesList.isEmpty();
    }

    public boolean isAnimatingChild(View child) {
        if (!this.shouldClipChildren) {
            return false;
        }
        int N = this.mRemoveAnimations.size();
        for (int a = 0; a < N; a++) {
            if (this.mRemoveAnimations.get(a).itemView == child) {
                return true;
            }
        }
        int N2 = this.mAddAnimations.size();
        for (int a2 = 0; a2 < N2; a2++) {
            if (this.mAddAnimations.get(a2).itemView == child) {
                return true;
            }
        }
        return false;
    }

    public void setShouldClipChildren(boolean value) {
        this.shouldClipChildren = value;
    }

    public int getAnimationClipTop() {
        if (!this.shouldClipChildren) {
            return 0;
        }
        if (!this.mRemoveAnimations.isEmpty()) {
            int top = Integer.MAX_VALUE;
            int N = this.mRemoveAnimations.size();
            for (int a = 0; a < N; a++) {
                top = Math.min(top, this.mRemoveAnimations.get(a).itemView.getTop());
            }
            return top;
        } else if (this.mAddAnimations.isEmpty()) {
            return 0;
        } else {
            int top2 = Integer.MAX_VALUE;
            int N2 = this.mAddAnimations.size();
            for (int a2 = 0; a2 < N2; a2++) {
                top2 = Math.min(top2, this.mAddAnimations.get(a2).itemView.getTop());
            }
            return top2;
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            onAllAnimationsDone();
        }
    }

    /* access modifiers changed from: protected */
    public void onAllAnimationsDone() {
    }

    public void endAnimations() {
        for (int i = this.mPendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo item = this.mPendingMoves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(item.holder);
            this.mPendingMoves.remove(i);
        }
        for (int i2 = this.mPendingRemovals.size() - 1; i2 >= 0; i2--) {
            dispatchRemoveFinished(this.mPendingRemovals.get(i2));
            this.mPendingRemovals.remove(i2);
        }
        for (int i3 = this.mPendingAdditions.size() - 1; i3 >= 0; i3--) {
            RecyclerView.ViewHolder item2 = this.mPendingAdditions.get(i3);
            item2.itemView.setTranslationY(0.0f);
            dispatchAddFinished(item2);
            this.mPendingAdditions.remove(i3);
        }
        for (int i4 = this.mPendingChanges.size() - 1; i4 >= 0; i4--) {
            endChangeAnimationIfNecessary(this.mPendingChanges.get(i4));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            for (int i5 = this.mMovesList.size() - 1; i5 >= 0; i5--) {
                ArrayList<MoveInfo> moves = this.mMovesList.get(i5);
                for (int j = moves.size() - 1; j >= 0; j--) {
                    MoveInfo moveInfo = moves.get(j);
                    View view2 = moveInfo.holder.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    dispatchMoveFinished(moveInfo.holder);
                    moves.remove(j);
                    if (moves.isEmpty()) {
                        this.mMovesList.remove(moves);
                    }
                }
            }
            for (int i6 = this.mAdditionsList.size() - 1; i6 >= 0; i6--) {
                ArrayList<RecyclerView.ViewHolder> additions = this.mAdditionsList.get(i6);
                for (int j2 = additions.size() - 1; j2 >= 0; j2--) {
                    RecyclerView.ViewHolder item3 = additions.get(j2);
                    item3.itemView.setTranslationY(0.0f);
                    dispatchAddFinished(item3);
                    additions.remove(j2);
                    if (additions.isEmpty()) {
                        this.mAdditionsList.remove(additions);
                    }
                }
            }
            for (int i7 = this.mChangesList.size() - 1; i7 >= 0; i7--) {
                ArrayList<ChangeInfo> changes = this.mChangesList.get(i7);
                for (int j3 = changes.size() - 1; j3 >= 0; j3--) {
                    endChangeAnimationIfNecessary(changes.get(j3));
                    if (changes.isEmpty()) {
                        this.mChangesList.remove(changes);
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
    public void cancelAll(List<RecyclerView.ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            viewHolders.get(i).itemView.animate().cancel();
        }
    }

    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder, List<Object> payloads) {
        return !payloads.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, payloads);
    }
}
