package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SideMenultItemAnimator extends SimpleItemAnimator {
    private static TimeInterpolator sDefaultInterpolator;
    ArrayList<ViewHolder> mAddAnimations = new ArrayList();
    ArrayList<ArrayList<ViewHolder>> mAdditionsList = new ArrayList();
    ArrayList<ViewHolder> mChangeAnimations = new ArrayList();
    ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList();
    ArrayList<ViewHolder> mMoveAnimations = new ArrayList();
    ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList();
    private ArrayList<ViewHolder> mPendingAdditions = new ArrayList();
    private ArrayList<ChangeInfo> mPendingChanges = new ArrayList();
    private ArrayList<MoveInfo> mPendingMoves = new ArrayList();
    private ArrayList<ViewHolder> mPendingRemovals = new ArrayList();
    ArrayList<ViewHolder> mRemoveAnimations = new ArrayList();

    private static class ChangeInfo {
        public int fromX;
        public int fromY;
        public ViewHolder newHolder;
        public ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(ViewHolder viewHolder, ViewHolder viewHolder2) {
            this.oldHolder = viewHolder;
            this.newHolder = viewHolder2;
        }

        ChangeInfo(ViewHolder viewHolder, ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
            this(viewHolder, viewHolder2);
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ChangeInfo{oldHolder=");
            stringBuilder.append(this.oldHolder);
            stringBuilder.append(", newHolder=");
            stringBuilder.append(this.newHolder);
            stringBuilder.append(", fromX=");
            stringBuilder.append(this.fromX);
            stringBuilder.append(", fromY=");
            stringBuilder.append(this.fromY);
            stringBuilder.append(", toX=");
            stringBuilder.append(this.toX);
            stringBuilder.append(", toY=");
            stringBuilder.append(this.toY);
            stringBuilder.append('}');
            return stringBuilder.toString();
        }
    }

    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            this.holder = viewHolder;
            this.fromX = i;
            this.fromY = i2;
            this.toX = i3;
            this.toY = i4;
        }
    }

    static /* synthetic */ int lambda$new$0(int i, int i2) {
        return i2 == i + -1 ? 0 : i2 >= 0 ? i2 + 1 : i2;
    }

    /* Access modifiers changed, original: protected */
    public void onAllAnimationsDone() {
    }

    public SideMenultItemAnimator(RecyclerView recyclerView) {
        recyclerView.setChildDrawingOrderCallback(-$$Lambda$SideMenultItemAnimator$cpmqh8LIS4phD2-GDdX7-QI5CFM.INSTANCE);
    }

    public void runPendingAnimations() {
        int isEmpty = this.mPendingMoves.isEmpty() ^ 1;
        int isEmpty2 = this.mPendingChanges.isEmpty() ^ 1;
        int isEmpty3 = this.mPendingAdditions.isEmpty() ^ 1;
        if ((this.mPendingRemovals.isEmpty() ^ 1) != 0 || isEmpty != 0 || isEmpty3 != 0 || isEmpty2 != 0) {
            int i;
            ArrayList arrayList;
            Iterator it;
            int i2 = 0;
            for (i = 0; i < this.mPendingRemovals.size(); i++) {
                i2 += ((ViewHolder) this.mPendingRemovals.get(i)).itemView.getMeasuredHeight();
            }
            int size = this.mPendingRemovals.size();
            for (i = 0; i < size; i++) {
                animateRemoveImpl((ViewHolder) this.mPendingRemovals.get(i), i2);
            }
            this.mPendingRemovals.clear();
            if (isEmpty != 0) {
                arrayList = new ArrayList(this.mPendingMoves);
                this.mMovesList.add(arrayList);
                this.mPendingMoves.clear();
                it = arrayList.iterator();
                while (it.hasNext()) {
                    MoveInfo moveInfo = (MoveInfo) it.next();
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
                }
                arrayList.clear();
                this.mMovesList.remove(arrayList);
            }
            if (isEmpty2 != 0) {
                arrayList = new ArrayList(this.mPendingChanges);
                this.mChangesList.add(arrayList);
                this.mPendingChanges.clear();
                it = arrayList.iterator();
                while (it.hasNext()) {
                    animateChangeImpl((ChangeInfo) it.next());
                }
                arrayList.clear();
                this.mChangesList.remove(arrayList);
            }
            if (isEmpty3 != 0) {
                arrayList = new ArrayList(this.mPendingAdditions);
                this.mAdditionsList.add(arrayList);
                this.mPendingAdditions.clear();
                isEmpty3 = 0;
                for (isEmpty2 = 0; isEmpty2 < arrayList.size(); isEmpty2++) {
                    isEmpty3 += ((ViewHolder) arrayList.get(isEmpty2)).itemView.getMeasuredHeight();
                }
                isEmpty = arrayList.size();
                for (int i3 = 0; i3 < isEmpty; i3++) {
                    animateAddImpl((ViewHolder) arrayList.get(i3), i3, isEmpty, isEmpty3);
                }
                arrayList.clear();
                this.mAdditionsList.remove(arrayList);
            }
        }
    }

    public boolean animateRemove(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingRemovals.add(viewHolder);
        return true;
    }

    private void animateRemoveImpl(final ViewHolder viewHolder, int i) {
        final ViewPropertyAnimator animate = viewHolder.itemView.animate();
        this.mRemoveAnimations.add(viewHolder);
        animate.setDuration(220).translationY((float) (-i)).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                SideMenultItemAnimator.this.dispatchRemoveStarting(viewHolder);
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener(null);
                SideMenultItemAnimator.this.dispatchRemoveFinished(viewHolder);
                SideMenultItemAnimator.this.mRemoveAnimations.remove(viewHolder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingAdditions.add(viewHolder);
        viewHolder.itemView.setAlpha(0.0f);
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void animateAddImpl(final ViewHolder viewHolder, int i, int i2, int i3) {
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
                animate.setListener(null);
                SideMenultItemAnimator.this.dispatchAddFinished(viewHolder);
                SideMenultItemAnimator.this.mAddAnimations.remove(viewHolder);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateMove(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
        View view = viewHolder.itemView;
        int translationX = i + ((int) view.getTranslationX());
        int translationY = i2 + ((int) viewHolder.itemView.getTranslationY());
        resetAnimation(viewHolder);
        i = i3 - translationX;
        i2 = i4 - translationY;
        if (i == 0 && i2 == 0) {
            dispatchMoveFinished(viewHolder);
            return false;
        }
        if (i != 0) {
            view.setTranslationX((float) (-i));
        }
        if (i2 != 0) {
            view.setTranslationY((float) (-i2));
        }
        this.mPendingMoves.add(new MoveInfo(viewHolder, translationX, translationY, i3, i4));
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void animateMoveImpl(ViewHolder viewHolder, int i, int i2, int i3, int i4) {
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
        final ViewHolder viewHolder2 = viewHolder;
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
                animate.setListener(null);
                SideMenultItemAnimator.this.dispatchMoveFinished(viewHolder2);
                SideMenultItemAnimator.this.mMoveAnimations.remove(viewHolder2);
                SideMenultItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        if (viewHolder == viewHolder2) {
            return animateMove(viewHolder, i, i2, i3, i4);
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

    /* Access modifiers changed, original: 0000 */
    public void animateChangeImpl(final ChangeInfo changeInfo) {
        View view;
        ViewHolder viewHolder = changeInfo.oldHolder;
        View view2 = null;
        if (viewHolder == null) {
            view = null;
        } else {
            view = viewHolder.itemView;
        }
        ViewHolder viewHolder2 = changeInfo.newHolder;
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
                    duration.setListener(null);
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
                    animate.setListener(null);
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

    private void endChangeAnimation(List<ChangeInfo> list, ViewHolder viewHolder) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ChangeInfo changeInfo = (ChangeInfo) list.get(size);
            if (endChangeAnimationIfNecessary(changeInfo, viewHolder) && changeInfo.oldHolder == null && changeInfo.newHolder == null) {
                list.remove(changeInfo);
            }
        }
    }

    private void endChangeAnimationIfNecessary(ChangeInfo changeInfo) {
        ViewHolder viewHolder = changeInfo.oldHolder;
        if (viewHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder);
        }
        viewHolder = changeInfo.newHolder;
        if (viewHolder != null) {
            endChangeAnimationIfNecessary(changeInfo, viewHolder);
        }
    }

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, ViewHolder viewHolder) {
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

    public void endAnimation(ViewHolder viewHolder) {
        ArrayList arrayList;
        View view = viewHolder.itemView;
        view.animate().cancel();
        int size = this.mPendingMoves.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (((MoveInfo) this.mPendingMoves.get(size)).holder == viewHolder) {
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
        for (size = this.mChangesList.size() - 1; size >= 0; size--) {
            arrayList = (ArrayList) this.mChangesList.get(size);
            endChangeAnimation(arrayList, viewHolder);
            if (arrayList.isEmpty()) {
                this.mChangesList.remove(size);
            }
        }
        for (size = this.mMovesList.size() - 1; size >= 0; size--) {
            arrayList = (ArrayList) this.mMovesList.get(size);
            int size2 = arrayList.size() - 1;
            while (size2 >= 0) {
                if (((MoveInfo) arrayList.get(size2)).holder == viewHolder) {
                    view.setTranslationY(0.0f);
                    view.setTranslationX(0.0f);
                    dispatchMoveFinished(viewHolder);
                    arrayList.remove(size2);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(size);
                    }
                } else {
                    size2--;
                }
            }
        }
        for (size = this.mAdditionsList.size() - 1; size >= 0; size--) {
            arrayList = (ArrayList) this.mAdditionsList.get(size);
            if (arrayList.remove(viewHolder)) {
                view.setTranslationY(0.0f);
                dispatchAddFinished(viewHolder);
                if (arrayList.isEmpty()) {
                    this.mAdditionsList.remove(size);
                }
            }
        }
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(ViewHolder viewHolder) {
        if (sDefaultInterpolator == null) {
            sDefaultInterpolator = new ValueAnimator().getInterpolator();
        }
        viewHolder.itemView.animate().setInterpolator(sDefaultInterpolator);
        endAnimation(viewHolder);
    }

    public boolean isRunning() {
        return (this.mPendingAdditions.isEmpty() && this.mPendingChanges.isEmpty() && this.mPendingMoves.isEmpty() && this.mPendingRemovals.isEmpty() && this.mMoveAnimations.isEmpty() && this.mRemoveAnimations.isEmpty() && this.mAddAnimations.isEmpty() && this.mChangeAnimations.isEmpty() && this.mMovesList.isEmpty() && this.mAdditionsList.isEmpty() && this.mChangesList.isEmpty()) ? false : true;
    }

    /* Access modifiers changed, original: 0000 */
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
            MoveInfo moveInfo = (MoveInfo) this.mPendingMoves.get(size);
            View view = moveInfo.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(moveInfo.holder);
            this.mPendingMoves.remove(size);
        }
        for (size = this.mPendingRemovals.size() - 1; size >= 0; size--) {
            dispatchRemoveFinished((ViewHolder) this.mPendingRemovals.get(size));
            this.mPendingRemovals.remove(size);
        }
        for (size = this.mPendingAdditions.size() - 1; size >= 0; size--) {
            ViewHolder viewHolder = (ViewHolder) this.mPendingAdditions.get(size);
            viewHolder.itemView.setTranslationY(0.0f);
            dispatchAddFinished(viewHolder);
            this.mPendingAdditions.remove(size);
        }
        for (size = this.mPendingChanges.size() - 1; size >= 0; size--) {
            endChangeAnimationIfNecessary((ChangeInfo) this.mPendingChanges.get(size));
        }
        this.mPendingChanges.clear();
        if (isRunning()) {
            ArrayList arrayList;
            int size2;
            for (size = this.mMovesList.size() - 1; size >= 0; size--) {
                arrayList = (ArrayList) this.mMovesList.get(size);
                for (size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                    MoveInfo moveInfo2 = (MoveInfo) arrayList.get(size2);
                    View view2 = moveInfo2.holder.itemView;
                    view2.setTranslationY(0.0f);
                    view2.setTranslationX(0.0f);
                    dispatchMoveFinished(moveInfo2.holder);
                    arrayList.remove(size2);
                    if (arrayList.isEmpty()) {
                        this.mMovesList.remove(arrayList);
                    }
                }
            }
            for (size = this.mAdditionsList.size() - 1; size >= 0; size--) {
                arrayList = (ArrayList) this.mAdditionsList.get(size);
                for (size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                    ViewHolder viewHolder2 = (ViewHolder) arrayList.get(size2);
                    viewHolder2.itemView.setTranslationY(0.0f);
                    dispatchAddFinished(viewHolder2);
                    arrayList.remove(size2);
                    if (arrayList.isEmpty()) {
                        this.mAdditionsList.remove(arrayList);
                    }
                }
            }
            for (size = this.mChangesList.size() - 1; size >= 0; size--) {
                ArrayList arrayList2 = (ArrayList) this.mChangesList.get(size);
                for (int size3 = arrayList2.size() - 1; size3 >= 0; size3--) {
                    endChangeAnimationIfNecessary((ChangeInfo) arrayList2.get(size3));
                    if (arrayList2.isEmpty()) {
                        this.mChangesList.remove(arrayList2);
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

    /* Access modifiers changed, original: 0000 */
    public void cancelAll(List<ViewHolder> list) {
        for (int size = list.size() - 1; size >= 0; size--) {
            ((ViewHolder) list.get(size)).itemView.animate().cancel();
        }
    }

    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> list) {
        return !list.isEmpty() || super.canReuseUpdatedViewHolder(viewHolder, list);
    }
}
