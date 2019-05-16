package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.SimpleItemAnimator;
import org.telegram.ui.Cells.DialogCell;

public class DialogsItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = false;
    private static final int changeDuration = 180;
    private static final int deleteDuration = 180;
    private static TimeInterpolator sDefaultInterpolator = new DecelerateInterpolator();
    private int bottomClip;
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
    private DialogCell removingDialog;
    private int topClip;

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

    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> list) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onAllAnimationsDone() {
    }

    public void runPendingAnimations() {
        int isEmpty = this.mPendingMoves.isEmpty() ^ 1;
        int isEmpty2 = this.mPendingChanges.isEmpty() ^ 1;
        int isEmpty3 = this.mPendingAdditions.isEmpty() ^ 1;
        if ((this.mPendingRemovals.isEmpty() ^ 1) != 0 || isEmpty != 0 || isEmpty3 != 0 || isEmpty2 != 0) {
            ArrayList arrayList;
            Iterator it = this.mPendingRemovals.iterator();
            while (it.hasNext()) {
                animateRemoveImpl((ViewHolder) it.next());
            }
            this.mPendingRemovals.clear();
            if (isEmpty != 0) {
                arrayList = new ArrayList(this.mPendingMoves);
                this.mMovesList.add(arrayList);
                this.mPendingMoves.clear();
                new -$$Lambda$DialogsItemAnimator$FzJ8o5Mz2rO6C7ZkKkUswV9PN8U(this, arrayList).run();
            }
            if (isEmpty2 != 0) {
                arrayList = new ArrayList(this.mPendingChanges);
                this.mChangesList.add(arrayList);
                this.mPendingChanges.clear();
                new -$$Lambda$DialogsItemAnimator$cc5l3oNCPWmFl8oTuclZaKvqaQ8(this, arrayList).run();
            }
            if (isEmpty3 != 0) {
                arrayList = new ArrayList(this.mPendingAdditions);
                this.mAdditionsList.add(arrayList);
                this.mPendingAdditions.clear();
                new -$$Lambda$DialogsItemAnimator$zsvBbBTBPz9JWwcIM9KIgALVodc(this, arrayList).run();
            }
        }
    }

    public /* synthetic */ void lambda$runPendingAnimations$0$DialogsItemAnimator(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            MoveInfo moveInfo = (MoveInfo) it.next();
            animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
        }
        arrayList.clear();
        this.mMovesList.remove(arrayList);
    }

    public /* synthetic */ void lambda$runPendingAnimations$1$DialogsItemAnimator(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            animateChangeImpl((ChangeInfo) it.next());
        }
        arrayList.clear();
        this.mChangesList.remove(arrayList);
    }

    public /* synthetic */ void lambda$runPendingAnimations$2$DialogsItemAnimator(ArrayList arrayList) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            animateAddImpl((ViewHolder) it.next());
        }
        arrayList.clear();
        this.mAdditionsList.remove(arrayList);
    }

    public boolean animateRemove(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        this.mPendingRemovals.add(viewHolder);
        return true;
    }

    public void prepareForRemove() {
        this.topClip = Integer.MAX_VALUE;
        this.bottomClip = Integer.MAX_VALUE;
        this.removingDialog = null;
    }

    private void animateRemoveImpl(final ViewHolder viewHolder) {
        final View view = viewHolder.itemView;
        this.mRemoveAnimations.add(viewHolder);
        if (view instanceof DialogCell) {
            final DialogCell dialogCell = (DialogCell) view;
            this.removingDialog = dialogCell;
            if (this.topClip != Integer.MAX_VALUE) {
                int measuredHeight = this.removingDialog.getMeasuredHeight();
                int i = this.topClip;
                this.bottomClip = measuredHeight - i;
                this.removingDialog.setTopClip(i);
                this.removingDialog.setBottomClip(this.bottomClip);
            } else if (this.bottomClip != Integer.MAX_VALUE) {
                this.topClip = this.removingDialog.getMeasuredHeight() - this.bottomClip;
                this.removingDialog.setTopClip(this.topClip);
                this.removingDialog.setBottomClip(this.bottomClip);
            }
            if (VERSION.SDK_INT >= 21) {
                dialogCell.setElevation(-1.0f);
                dialogCell.setOutlineProvider(null);
            }
            ObjectAnimator duration = ObjectAnimator.ofFloat(dialogCell, AnimationProperties.CLIP_DIALOG_CELL_PROGRESS, new float[]{1.0f}).setDuration(180);
            duration.setInterpolator(sDefaultInterpolator);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    DialogsItemAnimator.this.dispatchRemoveStarting(viewHolder);
                }

                public void onAnimationEnd(Animator animator) {
                    animator.removeAllListeners();
                    dialogCell.setClipProgress(0.0f);
                    if (VERSION.SDK_INT >= 21) {
                        dialogCell.setElevation(0.0f);
                    }
                    DialogsItemAnimator.this.dispatchRemoveFinished(viewHolder);
                    DialogsItemAnimator.this.mRemoveAnimations.remove(viewHolder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            duration.start();
            return;
        }
        final ViewPropertyAnimator animate = view.animate();
        animate.setDuration(180).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchRemoveStarting(viewHolder);
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener(null);
                view.setAlpha(1.0f);
                DialogsItemAnimator.this.dispatchRemoveFinished(viewHolder);
                DialogsItemAnimator.this.mRemoveAnimations.remove(viewHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(ViewHolder viewHolder) {
        resetAnimation(viewHolder);
        View view = viewHolder.itemView;
        if (view instanceof DialogCell) {
            ((DialogCell) view).setClipProgress(1.0f);
        } else {
            view.setAlpha(0.0f);
        }
        this.mPendingAdditions.add(viewHolder);
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void animateAddImpl(final ViewHolder viewHolder) {
        final View view = viewHolder.itemView;
        this.mAddAnimations.add(viewHolder);
        if (view instanceof DialogCell) {
            final DialogCell dialogCell = (DialogCell) view;
            ObjectAnimator duration = ObjectAnimator.ofFloat(dialogCell, AnimationProperties.CLIP_DIALOG_CELL_PROGRESS, new float[]{0.0f}).setDuration(180);
            duration.setInterpolator(sDefaultInterpolator);
            duration.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    DialogsItemAnimator.this.dispatchAddStarting(viewHolder);
                }

                public void onAnimationCancel(Animator animator) {
                    dialogCell.setClipProgress(0.0f);
                }

                public void onAnimationEnd(Animator animator) {
                    animator.removeAllListeners();
                    DialogsItemAnimator.this.dispatchAddFinished(viewHolder);
                    DialogsItemAnimator.this.mAddAnimations.remove(viewHolder);
                    DialogsItemAnimator.this.dispatchFinishedWhenDone();
                }
            });
            duration.start();
            return;
        }
        final ViewPropertyAnimator animate = view.animate();
        animate.alpha(1.0f).setDuration(180).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchAddStarting(viewHolder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animate.setListener(null);
                DialogsItemAnimator.this.dispatchAddFinished(viewHolder);
                DialogsItemAnimator.this.mAddAnimations.remove(viewHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
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
        if (i2 > i4) {
            this.bottomClip = i2 - i4;
        } else {
            this.topClip = i6;
        }
        DialogCell dialogCell = this.removingDialog;
        if (dialogCell != null) {
            if (this.topClip != Integer.MAX_VALUE) {
                i = dialogCell.getMeasuredHeight();
                i2 = this.topClip;
                this.bottomClip = i - i2;
                this.removingDialog.setTopClip(i2);
                this.removingDialog.setBottomClip(this.bottomClip);
            } else if (this.bottomClip != Integer.MAX_VALUE) {
                this.topClip = dialogCell.getMeasuredHeight() - this.bottomClip;
                this.removingDialog.setTopClip(this.topClip);
                this.removingDialog.setBottomClip(this.bottomClip);
            }
        }
        final ViewPropertyAnimator animate = view.animate();
        this.mMoveAnimations.add(viewHolder);
        final ViewHolder viewHolder2 = viewHolder;
        animate.setDuration(180).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchMoveStarting(viewHolder2);
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
                DialogsItemAnimator.this.dispatchMoveFinished(viewHolder2);
                DialogsItemAnimator.this.mMoveAnimations.remove(viewHolder2);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateChange(ViewHolder viewHolder, ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        if (!(viewHolder.itemView instanceof DialogCell)) {
            return false;
        }
        resetAnimation(viewHolder);
        resetAnimation(viewHolder2);
        viewHolder.itemView.setAlpha(1.0f);
        viewHolder2.itemView.setAlpha(0.0f);
        viewHolder2.itemView.setTranslationX(0.0f);
        this.mPendingChanges.add(new ChangeInfo(viewHolder, viewHolder2, i, i2, i3, i4));
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void animateChangeImpl(final ChangeInfo changeInfo) {
        final ViewHolder viewHolder = changeInfo.oldHolder;
        ViewHolder viewHolder2 = changeInfo.newHolder;
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        Animator[] animatorArr = new Animator[2];
        animatorArr[0] = ObjectAnimator.ofFloat(viewHolder.itemView, View.ALPHA, new float[]{0.0f});
        animatorArr[1] = ObjectAnimator.ofFloat(viewHolder2.itemView, View.ALPHA, new float[]{1.0f});
        animatorSet.playTogether(animatorArr);
        this.mChangeAnimations.add(changeInfo.oldHolder);
        this.mChangeAnimations.add(changeInfo.newHolder);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                DialogsItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
            }

            public void onAnimationEnd(Animator animator) {
                viewHolder.itemView.setAlpha(1.0f);
                animatorSet.removeAllListeners();
                DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.oldHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
                DialogsItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                DialogsItemAnimator.this.mChangeAnimations.remove(changeInfo.newHolder);
                DialogsItemAnimator.this.dispatchFinishedWhenDone();
            }
        });
        animatorSet.start();
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
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
            dispatchRemoveFinished(viewHolder);
        }
        if (this.mPendingAdditions.remove(viewHolder)) {
            if (view instanceof DialogCell) {
                ((DialogCell) view).setClipProgress(0.0f);
            } else {
                view.setAlpha(1.0f);
            }
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
            ArrayList arrayList2 = (ArrayList) this.mAdditionsList.get(size);
            if (arrayList2.remove(viewHolder)) {
                if (view instanceof DialogCell) {
                    ((DialogCell) view).setClipProgress(1.0f);
                } else {
                    view.setAlpha(1.0f);
                }
                dispatchAddFinished(viewHolder);
                if (arrayList2.isEmpty()) {
                    this.mAdditionsList.remove(size);
                }
            }
        }
        this.mRemoveAnimations.remove(viewHolder);
        this.mAddAnimations.remove(viewHolder);
        this.mChangeAnimations.remove(viewHolder);
        this.mMoveAnimations.remove(viewHolder);
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(ViewHolder viewHolder) {
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
        size = this.mPendingAdditions.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            ViewHolder viewHolder = (ViewHolder) this.mPendingAdditions.get(size);
            View view2 = viewHolder.itemView;
            if (view2 instanceof DialogCell) {
                ((DialogCell) view2).setClipProgress(0.0f);
            } else {
                view2.setAlpha(1.0f);
            }
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
            View view3;
            for (size = this.mMovesList.size() - 1; size >= 0; size--) {
                arrayList = (ArrayList) this.mMovesList.get(size);
                for (size2 = arrayList.size() - 1; size2 >= 0; size2--) {
                    MoveInfo moveInfo2 = (MoveInfo) arrayList.get(size2);
                    view3 = moveInfo2.holder.itemView;
                    view3.setTranslationY(0.0f);
                    view3.setTranslationX(0.0f);
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
                    view3 = viewHolder2.itemView;
                    if (view3 instanceof DialogCell) {
                        ((DialogCell) view3).setClipProgress(0.0f);
                    } else {
                        view3.setAlpha(1.0f);
                    }
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
}
