package org.telegram.p005ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.Keep;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject.GroupedMessages;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.ItemAnimator.ItemHolderInfo;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.SimpleItemAnimator;
import org.telegram.p005ui.Cells.ChatMessageCell;
import org.telegram.p005ui.ChatActivity;
import org.telegram.tgnet.ConnectionsManager;

/* renamed from: org.telegram.ui.Components.ChatItemAnimator */
public class ChatItemAnimator extends SimpleItemAnimator {
    private static final boolean DEBUG = false;
    ArrayList<ViewHolder> addAnimations = new ArrayList();
    private LongSparseArray<AnimatingMessageGroup> animatingGroups = new LongSparseArray();
    ArrayList<ViewHolder> changeAnimations = new ArrayList();
    private ChatActivity chatFragment;
    private RecyclerView list;
    ArrayList<ViewHolder> moveAnimations = new ArrayList();
    private ArrayList<ViewHolder> pendingAdditions = new ArrayList();
    private ArrayList<ChangeInfo> pendingChanges = new ArrayList();
    private ArrayList<MoveInfo> pendingMoves = new ArrayList();
    private ArrayList<ViewHolder> pendingRemovals = new ArrayList();
    private ArrayList<ResizeInfo> pendingResizes = new ArrayList();
    ArrayList<ViewHolder> removeAnimations = new ArrayList();

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$1 */
    class C06591 implements Comparator<ViewHolder> {
        C06591() {
        }

        public int compare(ViewHolder o1, ViewHolder o2) {
            return o1.getAdapterPosition() - o2.getAdapterPosition();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$AnimatingMessageGroup */
    private static class AnimatingMessageGroup {
        public ArrayList<ViewHolder> holders;

        private AnimatingMessageGroup() {
            this.holders = new ArrayList();
        }

        /* synthetic */ AnimatingMessageGroup(C06591 x0) {
            this();
        }

        @Keep
        public void setTranslationY(float translationY) {
            Iterator it = this.holders.iterator();
            while (it.hasNext()) {
                ((ViewHolder) it.next()).itemView.setTranslationY(translationY);
            }
        }

        @Keep
        public float getTranslationY() {
            return ((ViewHolder) this.holders.get(0)).itemView.getTranslationY();
        }

        @Keep
        public void setAlpha(float alpha) {
            Iterator it = this.holders.iterator();
            while (it.hasNext()) {
                ((ViewHolder) it.next()).itemView.setAlpha(alpha);
            }
        }

        @Keep
        public float getAlpha() {
            return ((ViewHolder) this.holders.get(0)).itemView.getAlpha();
        }

        public int getTotalHeight() {
            int minTop = ConnectionsManager.DEFAULT_DATACENTER_ID;
            int maxBottom = Integer.MIN_VALUE;
            Iterator it = this.holders.iterator();
            while (it.hasNext()) {
                ViewHolder holder = (ViewHolder) it.next();
                minTop = Math.min(minTop, holder.itemView.getTop());
                maxBottom = Math.max(maxBottom, holder.itemView.getBottom());
            }
            return maxBottom - minTop;
        }

        public void addAndUpdateHolder(ViewHolder holder) {
            if (!this.holders.isEmpty()) {
                holder.itemView.setTranslationY(((ViewHolder) this.holders.get(0)).itemView.getTranslationY());
                holder.itemView.setAlpha(((ViewHolder) this.holders.get(0)).itemView.getAlpha());
            }
            this.holders.add(holder);
        }

        public String toString() {
            return "AnimatingMessageGroup{holders=" + this.holders + '}';
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$ChangeInfo */
    private static class ChangeInfo {
        public boolean animateTranslation;
        public int fromX;
        public int fromY;
        public ViewHolder newHolder;
        public ViewHolder oldHolder;
        public int toX;
        public int toY;

        private ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder) {
            this.oldHolder = oldHolder;
            this.newHolder = newHolder;
        }

        ChangeInfo(ViewHolder oldHolder, ViewHolder newHolder, int fromX, int fromY, int toX, int toY, boolean animateTranslation) {
            this(oldHolder, newHolder);
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.animateTranslation = animateTranslation;
        }

        public String toString() {
            return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$GroupChangeInfo */
    private static class GroupChangeInfo {
        public float fromY;
        public AnimatingMessageGroup newGroup;
        public AnimatingMessageGroup oldGroup;
        public int smallestTop;
        public float toY;

        private GroupChangeInfo() {
            this.oldGroup = new AnimatingMessageGroup();
            this.newGroup = new AnimatingMessageGroup();
            this.smallestTop = ConnectionsManager.DEFAULT_DATACENTER_ID;
        }

        /* synthetic */ GroupChangeInfo(C06591 x0) {
            this();
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$MoveInfo */
    private static class MoveInfo {
        public int fromX;
        public int fromY;
        public ViewHolder holder;
        public int toX;
        public int toY;

        MoveInfo(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }

        public String toString() {
            return "MoveInfo{holder=" + this.holder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatItemAnimator$ResizeInfo */
    private static class ResizeInfo {
        public ItemHolderInfo from;
        public ViewHolder holder;
        /* renamed from: to */
        public ItemHolderInfo f219to;

        public ResizeInfo(ViewHolder holder, ItemHolderInfo from, ItemHolderInfo to) {
            this.holder = holder;
            this.from = from;
            this.f219to = to;
        }
    }

    public ChatItemAnimator(RecyclerView parent, ChatActivity fragment) {
        this.list = parent;
        this.chatFragment = fragment;
        setAddDuration(200);
        setRemoveDuration(100);
        setChangeDuration(200);
        setMoveDuration(200);
    }

    public void runPendingAnimations() {
        boolean removalsPending = !this.pendingRemovals.isEmpty();
        boolean movesPending = !this.pendingMoves.isEmpty();
        boolean changesPending = !this.pendingChanges.isEmpty();
        boolean additionsPending = !this.pendingAdditions.isEmpty();
        boolean resizesPending = !this.pendingResizes.isEmpty();
        if (removalsPending || movesPending || additionsPending || changesPending || resizesPending) {
            ViewHolder holder;
            GroupedMessages gm;
            ArrayList<ViewHolder> group;
            int i;
            Iterator it;
            AnimatingMessageGroup amg;
            ArrayList<AnimatingMessageGroup> addingGroups = null;
            LongSparseArray<GroupChangeInfo> changingGroups = null;
            LongSparseArray<ArrayList<ViewHolder>> groupAdditions = null;
            LongSparseArray<ArrayList<ViewHolder>> groupRemovals = null;
            Iterator it2 = this.pendingRemovals.iterator();
            while (it2.hasNext()) {
                holder = (ViewHolder) it2.next();
                if (!(holder.itemView instanceof ChatMessageCell) || ((ChatMessageCell) holder.itemView).getCurrentMessagesGroup() == null) {
                    animateRemoveImpl(holder);
                } else {
                    if (groupRemovals == null) {
                        groupRemovals = new LongSparseArray();
                    }
                    gm = ((ChatMessageCell) holder.itemView).getCurrentMessagesGroup();
                    group = (ArrayList) groupRemovals.get(gm.groupId);
                    if (group == null) {
                        group = new ArrayList();
                        groupRemovals.put(gm.groupId, group);
                    }
                    group.add(holder);
                }
            }
            if (groupRemovals != null) {
                for (i = 0; i < groupRemovals.size(); i++) {
                    group = (ArrayList) groupRemovals.valueAt(i);
                    if (((ChatMessageCell) ((ViewHolder) group.get(0)).itemView).getCurrentMessagesGroup().posArray.size() == group.size()) {
                        it = group.iterator();
                        while (it.hasNext()) {
                            animateRemoveImpl((ViewHolder) it.next());
                        }
                    }
                }
            }
            this.pendingRemovals.clear();
            ArrayList<MoveInfo> arrayList = new ArrayList(this.pendingMoves);
            if (movesPending) {
                Iterator it3 = this.pendingMoves.iterator();
                while (it3.hasNext()) {
                    MoveInfo moveInfo = (MoveInfo) it3.next();
                    animateMoveImpl(moveInfo.holder, moveInfo.fromX, moveInfo.fromY, moveInfo.toX, moveInfo.toY);
                }
                this.pendingMoves.clear();
            }
            if (additionsPending) {
                ArrayList<ViewHolder> additions = new ArrayList(this.pendingAdditions);
                ArrayList<ViewHolder> pushUp = new ArrayList();
                Collections.sort(additions, new C06591());
                i = 0;
                it = additions.iterator();
                while (it.hasNext()) {
                    holder = (ViewHolder) it.next();
                    if (i == holder.getAdapterPosition()) {
                        pushUp.add(holder);
                    } else {
                        animateAddByFadingIn(holder);
                    }
                    i++;
                }
                int smallestTop = ConnectionsManager.DEFAULT_DATACENTER_ID;
                int largestBottom = 0;
                it = pushUp.iterator();
                while (it.hasNext()) {
                    holder = (ViewHolder) it.next();
                    smallestTop = Math.min(smallestTop, holder.itemView.getTop());
                    largestBottom = Math.max(largestBottom, holder.itemView.getBottom());
                }
                it2 = pushUp.iterator();
                while (it2.hasNext()) {
                    ViewHolder _holder;
                    holder = (ViewHolder) it2.next();
                    float offsetY = 0.0f;
                    it = this.changeAnimations.iterator();
                    while (it.hasNext()) {
                        _holder = (ViewHolder) it.next();
                        if (holder.itemView.getTop() > _holder.itemView.getTop()) {
                            offsetY = Math.max(offsetY, _holder.itemView.getTranslationY());
                        }
                    }
                    it = this.moveAnimations.iterator();
                    while (it.hasNext()) {
                        _holder = (ViewHolder) it.next();
                        if (holder.itemView.getTop() > _holder.itemView.getTop()) {
                            offsetY = Math.max(offsetY, _holder.itemView.getTranslationY());
                        }
                    }
                    it = arrayList.iterator();
                    while (it.hasNext()) {
                        MoveInfo move = (MoveInfo) it.next();
                        if (holder.itemView.getTop() > move.holder.itemView.getTop()) {
                            offsetY = Math.max(offsetY, move.holder.itemView.getTranslationY());
                        }
                    }
                    if (!(holder.itemView instanceof ChatMessageCell) || ((ChatMessageCell) holder.itemView).getCurrentMessagesGroup() == null) {
                        holder.itemView.setTranslationY((((float) (largestBottom - smallestTop)) + offsetY) - ((float) holder.itemView.getHeight()));
                        animateAddByPushingUp(holder);
                    } else {
                        gm = ((ChatMessageCell) holder.itemView).getCurrentMessagesGroup();
                        if (groupAdditions == null) {
                            groupAdditions = new LongSparseArray();
                        }
                        ArrayList<ViewHolder> add = (ArrayList) groupAdditions.get(gm.groupId);
                        if (add == null) {
                            add = new ArrayList();
                            groupAdditions.put(gm.groupId, add);
                        }
                        add.add(holder);
                        amg = (AnimatingMessageGroup) this.animatingGroups.get(gm.groupId);
                        if (amg != null) {
                            amg.addAndUpdateHolder(holder);
                        } else {
                            amg = new AnimatingMessageGroup();
                            amg.addAndUpdateHolder(holder);
                            this.animatingGroups.put(gm.groupId, amg);
                        }
                        if (addingGroups == null) {
                            addingGroups = new ArrayList();
                        }
                        if (!addingGroups.contains(amg)) {
                            addingGroups.add(amg);
                        }
                    }
                }
                this.pendingAdditions.clear();
            }
            if (changesPending) {
                it2 = this.pendingChanges.iterator();
                while (it2.hasNext()) {
                    ChangeInfo change = (ChangeInfo) it2.next();
                    gm = null;
                    if ((change.newHolder.itemView instanceof ChatMessageCell) && (change.oldHolder.itemView instanceof ChatMessageCell)) {
                        gm = ((ChatMessageCell) change.newHolder.itemView).getCurrentMessagesGroup();
                        if (gm == null) {
                            gm = ((ChatMessageCell) change.oldHolder.itemView).getCurrentMessagesGroup();
                        }
                    }
                    if (gm != null) {
                        if (groupRemovals != null) {
                            if (groupRemovals.get(gm.groupId) != null) {
                                if (changingGroups == null) {
                                    changingGroups = new LongSparseArray();
                                }
                                GroupChangeInfo gChange = (GroupChangeInfo) changingGroups.get(gm.groupId);
                                if (gChange == null) {
                                    GroupChangeInfo groupChangeInfo = new GroupChangeInfo();
                                    changingGroups.put(gm.groupId, groupChangeInfo);
                                    it = ((ArrayList) groupRemovals.get(gm.groupId)).iterator();
                                    while (it.hasNext()) {
                                        groupChangeInfo.oldGroup.addAndUpdateHolder((ViewHolder) it.next());
                                    }
                                }
                                gChange.oldGroup.addAndUpdateHolder(change.oldHolder);
                                gChange.newGroup.addAndUpdateHolder(change.newHolder);
                                if (gChange.smallestTop > change.newHolder.itemView.getTop()) {
                                    gChange.smallestTop = change.newHolder.itemView.getTop();
                                    gChange.fromY = (float) change.fromY;
                                    gChange.toY = (float) change.toY;
                                }
                            }
                        }
                        if (groupAdditions != null) {
                            if (groupAdditions.get(gm.groupId) != null) {
                                amg = (AnimatingMessageGroup) this.animatingGroups.get(gm.groupId);
                                if (amg != null) {
                                    amg.addAndUpdateHolder(change.newHolder);
                                } else {
                                    amg = new AnimatingMessageGroup();
                                    it = ((ArrayList) groupAdditions.get(gm.groupId)).iterator();
                                    while (it.hasNext()) {
                                        amg.addAndUpdateHolder((ViewHolder) it.next());
                                    }
                                    amg.addAndUpdateHolder(change.newHolder);
                                    this.animatingGroups.put(gm.groupId, amg);
                                }
                                if (addingGroups == null) {
                                    addingGroups = new ArrayList();
                                }
                                if (!addingGroups.contains(amg)) {
                                    addingGroups.add(amg);
                                }
                                dispatchChangeFinished(change.oldHolder, true);
                                change.newHolder.itemView.setAlpha(1.0f);
                                change.newHolder.itemView.setTranslationY(0.0f);
                            }
                        }
                        animateChangeImpl(change);
                    } else {
                        animateChangeImpl(change);
                    }
                }
                this.pendingChanges.clear();
            }
            if (!(addingGroups == null || addingGroups.isEmpty())) {
                it = addingGroups.iterator();
                while (it.hasNext()) {
                    AnimatingMessageGroup g = (AnimatingMessageGroup) it.next();
                    g.setTranslationY((float) g.getTotalHeight());
                    g.setAlpha(1.0f);
                    animateAddByPushingUp(g);
                }
            }
            if (changingGroups != null && changingGroups.size() > 0) {
                for (i = 0; i < changingGroups.size(); i++) {
                    animateGroupChangeImpl((GroupChangeInfo) changingGroups.valueAt(i));
                }
            }
        }
    }

    public boolean animateRemove(ViewHolder holder) {
        resetAnimation(holder);
        this.pendingRemovals.add(holder);
        return true;
    }

    private void animateRemoveImpl(final ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate().withLayer();
        this.removeAnimations.add(holder);
        animation.setDuration(getRemoveDuration()).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                ChatItemAnimator.this.dispatchRemoveStarting(holder);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                view.setAlpha(1.0f);
                ChatItemAnimator.this.dispatchRemoveFinished(holder);
                ChatItemAnimator.this.removeAnimations.remove(holder);
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateAdd(ViewHolder holder) {
        resetAnimation(holder);
        holder.itemView.setAlpha(0.0f);
        this.pendingAdditions.add(holder);
        return true;
    }

    void animateAddByFadingIn(final ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate().withLayer();
        this.addAnimations.add(holder);
        animation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animation.alpha(1.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                ChatItemAnimator.this.dispatchAddStarting(holder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                ChatItemAnimator.this.dispatchAddFinished(holder);
                ChatItemAnimator.this.addAnimations.remove(holder);
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    void animateAddByPushingUp(final ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        this.addAnimations.add(holder);
        animation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        view.setAlpha(1.0f);
        animation.translationY(0.0f).setDuration(getAddDuration()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                ChatItemAnimator.this.dispatchAddStarting(holder);
            }

            public void onAnimationCancel(Animator animator) {
                view.setAlpha(1.0f);
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                ChatItemAnimator.this.dispatchAddFinished(holder);
                ChatItemAnimator.this.addAnimations.remove(holder);
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    void animateAddByPushingUp(final AnimatingMessageGroup group) {
        this.addAnimations.addAll(group.holders);
        group.setAlpha(1.0f);
        ObjectAnimator anim = ObjectAnimator.ofFloat(group, "translationY", new float[]{0.0f});
        anim.setDuration(getAddDuration());
        anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                Iterator it = group.holders.iterator();
                while (it.hasNext()) {
                    ChatItemAnimator.this.dispatchAddStarting((ViewHolder) it.next());
                }
            }

            public void onAnimationCancel(Animator animator) {
            }

            public void onAnimationEnd(Animator animator) {
                Iterator it = group.holders.iterator();
                while (it.hasNext()) {
                    ViewHolder holder = (ViewHolder) it.next();
                    ChatItemAnimator.this.dispatchAddFinished(holder);
                    ChatItemAnimator.this.addAnimations.remove(holder);
                    holder.itemView.setTag(R.id.current_animation, null);
                    holder.itemView.setTranslationY(0.0f);
                    holder.itemView.setLayerType(0, null);
                }
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        });
        Iterator it = group.holders.iterator();
        while (it.hasNext()) {
            ViewHolder holder = (ViewHolder) it.next();
            holder.itemView.setTag(R.id.current_animation, anim);
            holder.itemView.setLayerType(2, null);
        }
        anim.start();
    }

    public boolean animateMove(ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        View view = holder.itemView;
        fromX = (int) (((float) fromX) + holder.itemView.getTranslationX());
        fromY = (int) (((float) fromY) + holder.itemView.getTranslationY());
        resetAnimation(holder);
        int deltaY = toY - fromY;
        if (toX - fromX == 0 && deltaY == 0) {
            dispatchMoveFinished(holder);
            return false;
        }
        if (deltaY != 0) {
            view.setTranslationY((float) (-deltaY));
        }
        this.pendingMoves.add(new MoveInfo(holder, fromX, fromY, toX, toY));
        return true;
    }

    private boolean animateMoveAndResize(ViewHolder holder, ItemHolderInfo preInfo, ItemHolderInfo postInfo) {
        this.pendingResizes.add(new ResizeInfo(holder, preInfo, postInfo));
        return true;
    }

    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    void animateMoveImpl(final ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        final View view = holder.itemView;
        final int deltaX = toX - fromX;
        final int deltaY = toY - fromY;
        if (deltaX != 0) {
            view.animate().translationX(0.0f);
        }
        if (deltaY != 0) {
            view.animate().translationY(0.0f);
        }
        final OnPreDrawListener visiblePartUpdater = new OnPreDrawListener() {
            public boolean onPreDraw() {
                ChatItemAnimator.this.updateVisiblePart(holder);
                return true;
            }
        };
        final ViewPropertyAnimator animation = view.animate();
        this.moveAnimations.add(holder);
        animation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        final ViewHolder viewHolder = holder;
        animation.setDuration(getMoveDuration()).setListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animator) {
                ChatItemAnimator.this.dispatchMoveStarting(viewHolder);
                viewHolder.itemView.getViewTreeObserver().addOnPreDrawListener(visiblePartUpdater);
            }

            public void onAnimationCancel(Animator animator) {
                if (deltaX != 0) {
                    view.setTranslationX(0.0f);
                }
                if (deltaY != 0) {
                    view.setTranslationY(0.0f);
                }
            }

            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                ChatItemAnimator.this.dispatchMoveFinished(viewHolder);
                ChatItemAnimator.this.moveAnimations.remove(viewHolder);
                viewHolder.itemView.getViewTreeObserver().removeOnPreDrawListener(visiblePartUpdater);
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        }).start();
    }

    public boolean animateChange(ViewHolder oldHolder, ViewHolder newHolder, ItemHolderInfo preInfo, ItemHolderInfo postInfo) {
        if (oldHolder == newHolder) {
            return animateMove(oldHolder, preInfo.left, preInfo.top, postInfo.left, postInfo.top);
        }
        View _oldView = oldHolder.itemView;
        View _newView = newHolder.itemView;
        if (_oldView.getWidth() == _newView.getWidth() && _oldView.getHeight() == _newView.getHeight() && preInfo.left == postInfo.left && preInfo.top == postInfo.top) {
            if ((_oldView instanceof ChatMessageCell) && (_newView instanceof ChatMessageCell)) {
                if (((ChatMessageCell) _oldView).getActualWidth() == ((ChatMessageCell) _newView).getActualWidth()) {
                    dispatchChangeFinished(oldHolder, true);
                    dispatchChangeFinished(newHolder, false);
                    return false;
                }
            }
            dispatchChangeFinished(oldHolder, true);
            dispatchChangeFinished(newHolder, false);
            return false;
        }
        float prevTranslationX = oldHolder.itemView.getTranslationX();
        float prevTranslationY = oldHolder.itemView.getTranslationY();
        float prevAlpha = oldHolder.itemView.getAlpha();
        resetAnimation(oldHolder);
        int deltaY = 1 != null ? (int) (((float) (postInfo.top - preInfo.top)) - prevTranslationY) : 0;
        oldHolder.itemView.setTranslationY(prevTranslationY);
        oldHolder.itemView.setAlpha(prevAlpha);
        if (newHolder != null) {
            resetAnimation(newHolder);
            newHolder.itemView.setTranslationY((float) (-deltaY));
            newHolder.itemView.setAlpha(0.0f);
        }
        this.pendingChanges.add(new ChangeInfo(oldHolder, newHolder, preInfo.left, preInfo.top, postInfo.left, postInfo.top, true));
        return true;
    }

    void animateChangeImpl(final ChangeInfo changeInfo) {
        View newView;
        ViewHolder holder = changeInfo.oldHolder;
        final View view = holder == null ? null : holder.itemView;
        ViewHolder newHolder = changeInfo.newHolder;
        if (newHolder != null) {
            newView = newHolder.itemView;
        } else {
            newView = null;
        }
        boolean needAnimateAlpha = true;
        if (view != null) {
            if (newView == null || view.getWidth() != newView.getWidth() || view.getHeight() != newView.getHeight() || ((view instanceof ChatMessageCell) && (newView instanceof ChatMessageCell) && ((ChatMessageCell) view).getActualWidth() != ((ChatMessageCell) newView).getActualWidth())) {
                final ViewPropertyAnimator oldViewAnim = view.animate().withLayer().setDuration(getChangeDuration());
                this.changeAnimations.add(changeInfo.oldHolder);
                if (changeInfo.animateTranslation) {
                    oldViewAnim.translationX((float) (changeInfo.toX - changeInfo.fromX));
                    oldViewAnim.translationY((float) (changeInfo.toY - changeInfo.fromY));
                }
                oldViewAnim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                oldViewAnim.alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animator) {
                        ChatItemAnimator.this.dispatchChangeStarting(changeInfo.oldHolder, true);
                    }

                    public void onAnimationEnd(Animator animator) {
                        oldViewAnim.setListener(null);
                        view.setAlpha(1.0f);
                        view.setTranslationX(0.0f);
                        view.setTranslationY(0.0f);
                        ChatItemAnimator.this.dispatchChangeFinished(changeInfo.oldHolder, true);
                        ChatItemAnimator.this.changeAnimations.remove(changeInfo.oldHolder);
                        ChatItemAnimator.this.dispatchFinishedWhenDone();
                    }
                }).start();
            } else {
                dispatchChangeFinished(changeInfo.oldHolder, true);
                needAnimateAlpha = false;
            }
        }
        if (newView != null) {
            final ViewPropertyAnimator newViewAnimation = newView.animate().withLayer();
            this.changeAnimations.add(changeInfo.newHolder);
            newViewAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            if (changeInfo.animateTranslation) {
                newViewAnimation.translationX(0.0f).translationY(0.0f);
            }
            if (!needAnimateAlpha) {
                newView.setAlpha(1.0f);
            }
            newViewAnimation.setDuration(getChangeDuration()).alpha(1.0f).setListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animator) {
                    ChatItemAnimator.this.dispatchChangeStarting(changeInfo.newHolder, false);
                }

                public void onAnimationEnd(Animator animator) {
                    newViewAnimation.setListener(null);
                    newView.setAlpha(1.0f);
                    newView.setTranslationX(0.0f);
                    newView.setTranslationY(0.0f);
                    ChatItemAnimator.this.dispatchChangeFinished(changeInfo.newHolder, false);
                    ChatItemAnimator.this.changeAnimations.remove(changeInfo.newHolder);
                    ChatItemAnimator.this.dispatchFinishedWhenDone();
                }
            }).start();
        }
    }

    private void animateGroupChangeImpl(final GroupChangeInfo change) {
        ViewHolder holder;
        Iterator it = change.oldGroup.holders.iterator();
        while (it.hasNext()) {
            holder = (ViewHolder) it.next();
            endAnimation(holder);
            this.changeAnimations.add(holder);
        }
        it = change.newGroup.holders.iterator();
        while (it.hasNext()) {
            holder = (ViewHolder) it.next();
            endAnimation(holder);
            this.changeAnimations.add(holder);
        }
        change.oldGroup.setAlpha(1.0f);
        change.newGroup.setAlpha(0.0f);
        change.newGroup.setTranslationY(change.fromY - change.toY);
        AnimatorSet set = new AnimatorSet();
        Animator[] animatorArr = new Animator[4];
        animatorArr[0] = ObjectAnimator.ofFloat(change.newGroup, "alpha", new float[]{1.0f});
        animatorArr[1] = ObjectAnimator.ofFloat(change.oldGroup, "alpha", new float[]{0.0f});
        animatorArr[2] = ObjectAnimator.ofFloat(change.newGroup, "translationY", new float[]{0.0f});
        animatorArr[3] = ObjectAnimator.ofFloat(change.oldGroup, "translationY", new float[]{change.toY - change.fromY});
        set.playTogether(animatorArr);
        set.setDuration(getChangeDuration());
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ViewHolder holder;
                Iterator it = change.oldGroup.holders.iterator();
                while (it.hasNext()) {
                    holder = (ViewHolder) it.next();
                    ChatItemAnimator.this.dispatchChangeFinished(holder, true);
                    holder.itemView.setTag(R.id.current_animation, null);
                    holder.itemView.setLayerType(0, null);
                    ChatItemAnimator.this.changeAnimations.remove(holder);
                }
                it = change.newGroup.holders.iterator();
                while (it.hasNext()) {
                    holder = (ViewHolder) it.next();
                    ChatItemAnimator.this.dispatchChangeFinished(holder, false);
                    holder.itemView.setTag(R.id.current_animation, null);
                    holder.itemView.setLayerType(0, null);
                    ChatItemAnimator.this.changeAnimations.remove(holder);
                }
                change.oldGroup.setAlpha(1.0f);
                change.oldGroup.setTranslationY(0.0f);
                change.newGroup.setAlpha(1.0f);
                change.newGroup.setTranslationY(0.0f);
                ChatItemAnimator.this.dispatchFinishedWhenDone();
            }
        });
        it = change.oldGroup.holders.iterator();
        while (it.hasNext()) {
            holder = (ViewHolder) it.next();
            holder.itemView.setTag(R.id.current_animation, set);
            holder.itemView.setLayerType(2, null);
        }
        it = change.newGroup.holders.iterator();
        while (it.hasNext()) {
            holder = (ViewHolder) it.next();
            holder.itemView.setTag(R.id.current_animation, set);
            holder.itemView.setLayerType(2, null);
        }
        set.start();
    }

    private void animateResizeImpl(final ResizeInfo info) {
        View view = info.holder.itemView;
        AnimatorSet set = new AnimatorSet();
        r2 = new Animator[4];
        r2[0] = ObjectAnimator.ofInt(view, "top", new int[]{info.from.top, info.f219to.top});
        r2[1] = ObjectAnimator.ofInt(view, "bottom", new int[]{info.from.bottom, info.f219to.bottom});
        r2[2] = ObjectAnimator.ofInt(view, TtmlNode.LEFT, new int[]{info.from.left, info.f219to.left});
        r2[3] = ObjectAnimator.ofInt(view, TtmlNode.RIGHT, new int[]{info.from.right, info.f219to.right});
        set.playTogether(r2);
        set.setDuration(getChangeDuration());
        set.setInterpolator(CubicBezierInterpolator.DEFAULT);
        set.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                ChatItemAnimator.this.dispatchChangeStarting(info.holder, true);
            }

            public void onAnimationEnd(Animator animation) {
                ChatItemAnimator.this.dispatchChangeFinished(info.holder, true);
            }
        });
        set.start();
    }

    private void endChangeAnimation(List<ChangeInfo> infoList, ViewHolder item) {
        for (int i = infoList.size() - 1; i >= 0; i--) {
            ChangeInfo changeInfo = (ChangeInfo) infoList.get(i);
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

    private boolean endChangeAnimationIfNecessary(ChangeInfo changeInfo, ViewHolder item) {
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

    public void endAnimation(ViewHolder item) {
        View view = item.itemView;
        view.animate().cancel();
        Object animator = view.getTag(R.id.current_animation);
        if (animator != null && (animator instanceof Animator)) {
            ((Animator) animator).cancel();
            view.setTag(R.id.current_animation, null);
        }
        view.setTranslationY(0.0f);
        view.setTranslationX(0.0f);
        view.setAlpha(1.0f);
        for (int i = this.pendingMoves.size() - 1; i >= 0; i--) {
            if (((MoveInfo) this.pendingMoves.get(i)).holder == item) {
                dispatchMoveFinished(item);
                this.pendingMoves.remove(i);
            }
        }
        endChangeAnimation(this.pendingChanges, item);
        if (this.pendingRemovals.remove(item)) {
            dispatchRemoveFinished(item);
        }
        if (this.pendingAdditions.remove(item)) {
            dispatchAddFinished(item);
        }
        if (this.removeAnimations.remove(item)) {
        }
        if (this.addAnimations.remove(item)) {
        }
        if (this.changeAnimations.remove(item)) {
        }
        if (this.moveAnimations.remove(item)) {
        }
        dispatchFinishedWhenDone();
    }

    private void resetAnimation(ViewHolder holder) {
        endAnimation(holder);
    }

    public boolean isRunning() {
        if (this.pendingAdditions.isEmpty() && this.pendingChanges.isEmpty() && this.pendingMoves.isEmpty() && this.pendingRemovals.isEmpty() && this.moveAnimations.isEmpty() && this.removeAnimations.isEmpty() && this.addAnimations.isEmpty() && this.changeAnimations.isEmpty()) {
            return false;
        }
        return true;
    }

    void dispatchFinishedWhenDone() {
        if (!isRunning()) {
            dispatchAnimationsFinished();
            this.animatingGroups.clear();
        }
    }

    public void endAnimations() {
        int i;
        for (i = this.pendingMoves.size() - 1; i >= 0; i--) {
            MoveInfo item = (MoveInfo) this.pendingMoves.get(i);
            View view = item.holder.itemView;
            view.setTranslationY(0.0f);
            view.setTranslationX(0.0f);
            dispatchMoveFinished(item.holder);
            this.pendingMoves.remove(i);
        }
        for (i = this.pendingRemovals.size() - 1; i >= 0; i--) {
            dispatchRemoveFinished((ViewHolder) this.pendingRemovals.get(i));
            this.pendingRemovals.remove(i);
        }
        for (i = this.pendingAdditions.size() - 1; i >= 0; i--) {
            ViewHolder item2 = (ViewHolder) this.pendingAdditions.get(i);
            item2.itemView.setAlpha(1.0f);
            dispatchAddFinished(item2);
            this.pendingAdditions.remove(i);
        }
        for (i = this.pendingChanges.size() - 1; i >= 0; i--) {
            endChangeAnimationIfNecessary((ChangeInfo) this.pendingChanges.get(i));
        }
        this.pendingChanges.clear();
        if (isRunning()) {
            cancelAll(this.removeAnimations);
            cancelAll(this.moveAnimations);
            cancelAll(this.addAnimations);
            cancelAll(this.changeAnimations);
            dispatchAnimationsFinished();
        }
    }

    void cancelAll(List<ViewHolder> viewHolders) {
        for (int i = viewHolders.size() - 1; i >= 0; i--) {
            ((ViewHolder) viewHolders.get(i)).itemView.animate().cancel();
        }
    }

    private void updateVisiblePart(ViewHolder holder) {
        if (holder.itemView instanceof ChatMessageCell) {
            ChatMessageCell cell = holder.itemView;
            int y = (int) holder.itemView.getY();
            int top = y >= 0 ? 0 : -y;
            int height = holder.itemView.getMeasuredHeight();
            if (height > this.list.getMeasuredHeight()) {
                height = top + this.list.getMeasuredHeight();
            }
            cell.setVisiblePart(top, height - top);
            if (cell.getMessageObject().isRoundVideo() && MediaController.getInstance().isPlayingMessage(cell.getMessageObject())) {
                this.chatFragment.updateTextureViewPosition(false);
            }
        }
    }

    public boolean canReuseUpdatedViewHolder(ViewHolder viewHolder, List<Object> list) {
        return false;
    }
}
