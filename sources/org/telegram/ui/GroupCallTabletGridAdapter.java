package org.telegram.ui;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
/* loaded from: classes3.dex */
public class GroupCallTabletGridAdapter extends RecyclerListView.SelectionAdapter {
    private final GroupCallActivity activity;
    private ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final int currentAccount;
    private ChatObject.Call groupCall;
    private GroupCallRenderersContainer renderersContainer;
    private final ArrayList<ChatObject.VideoParticipant> videoParticipants = new ArrayList<>();
    private boolean visible = false;

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    public GroupCallTabletGridAdapter(ChatObject.Call call, int i, GroupCallActivity groupCallActivity) {
        this.groupCall = call;
        this.currentAccount = i;
        this.activity = groupCallActivity;
    }

    public void setRenderersPool(ArrayList<GroupCallMiniTextureView> arrayList, GroupCallRenderersContainer groupCallRenderersContainer) {
        this.attachedRenderers = arrayList;
        this.renderersContainer = groupCallRenderersContainer;
    }

    public void setGroupCall(ChatObject.Call call) {
        this.groupCall = call;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder */
    public RecyclerView.ViewHolder moNUMonCreateViewHolder(ViewGroup viewGroup, int i) {
        return new RecyclerListView.Holder(new GroupCallGridCell(viewGroup.getContext(), true) { // from class: org.telegram.ui.GroupCallTabletGridAdapter.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.voip.GroupCallGridCell, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (!GroupCallTabletGridAdapter.this.visible || getParticipant() == null) {
                    return;
                }
                GroupCallTabletGridAdapter.this.attachRenderer(this, true);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.voip.GroupCallGridCell, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                GroupCallTabletGridAdapter.this.attachRenderer(this, false);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attachRenderer(GroupCallGridCell groupCallGridCell, boolean z) {
        if (z && groupCallGridCell.getRenderer() == null) {
            groupCallGridCell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, null, null, groupCallGridCell, groupCallGridCell.getParticipant(), this.groupCall, this.activity));
        } else if (z || groupCallGridCell.getRenderer() == null) {
        } else {
            groupCallGridCell.getRenderer().setTabletGridView(null);
            groupCallGridCell.setRenderer(null);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        GroupCallGridCell groupCallGridCell = (GroupCallGridCell) viewHolder.itemView;
        ChatObject.VideoParticipant participant = groupCallGridCell.getParticipant();
        ChatObject.VideoParticipant videoParticipant = this.videoParticipants.get(i);
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.videoParticipants.get(i).participant;
        groupCallGridCell.spanCount = getSpanCount(i);
        groupCallGridCell.position = i;
        groupCallGridCell.gridAdapter = this;
        if (groupCallGridCell.getMeasuredHeight() != getItemHeight(i)) {
            groupCallGridCell.requestLayout();
        }
        AccountInstance accountInstance = AccountInstance.getInstance(this.currentAccount);
        ChatObject.Call call = this.groupCall;
        groupCallGridCell.setData(accountInstance, videoParticipant, call, MessageObject.getPeerId(call.selfPeer));
        if (participant != null && !participant.equals(videoParticipant) && groupCallGridCell.attached && groupCallGridCell.getRenderer() != null) {
            attachRenderer(groupCallGridCell, false);
            attachRenderer(groupCallGridCell, true);
        } else if (groupCallGridCell.getRenderer() == null) {
        } else {
            groupCallGridCell.getRenderer().updateAttachState(true);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.videoParticipants.size();
    }

    public void setVisibility(RecyclerListView recyclerListView, boolean z, boolean z2) {
        this.visible = z;
        if (z2) {
            for (int i = 0; i < recyclerListView.getChildCount(); i++) {
                View childAt = recyclerListView.getChildAt(i);
                if (childAt instanceof GroupCallGridCell) {
                    GroupCallGridCell groupCallGridCell = (GroupCallGridCell) childAt;
                    if (groupCallGridCell.getParticipant() != null) {
                        attachRenderer(groupCallGridCell, z);
                    }
                }
            }
        }
    }

    public void update(boolean z, RecyclerListView recyclerListView) {
        if (this.groupCall == null) {
            return;
        }
        if (z) {
            final ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.videoParticipants);
            this.videoParticipants.clear();
            this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
            DiffUtil.calculateDiff(new DiffUtil.Callback() { // from class: org.telegram.ui.GroupCallTabletGridAdapter.2
                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areContentsTheSame(int i, int i2) {
                    return true;
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getOldListSize() {
                    return arrayList.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public int getNewListSize() {
                    return GroupCallTabletGridAdapter.this.videoParticipants.size();
                }

                @Override // androidx.recyclerview.widget.DiffUtil.Callback
                public boolean areItemsTheSame(int i, int i2) {
                    if (i >= arrayList.size() || i2 >= GroupCallTabletGridAdapter.this.videoParticipants.size()) {
                        return false;
                    }
                    return ((ChatObject.VideoParticipant) arrayList.get(i)).equals(GroupCallTabletGridAdapter.this.videoParticipants.get(i2));
                }
            }).dispatchUpdatesTo(this);
            AndroidUtilities.updateVisibleRows(recyclerListView);
            return;
        }
        this.videoParticipants.clear();
        this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
        notifyDataSetChanged();
    }

    public int getSpanCount(int i) {
        int itemCount = getItemCount();
        if (itemCount > 1 && itemCount != 2) {
            return (itemCount != 3 || i == 0 || i == 1) ? 3 : 6;
        }
        return 6;
    }

    public int getItemHeight(int i) {
        RecyclerListView recyclerListView = this.activity.tabletVideoGridView;
        int itemCount = getItemCount();
        if (itemCount <= 1) {
            return recyclerListView.getMeasuredHeight();
        }
        if (itemCount <= 4) {
            return recyclerListView.getMeasuredHeight() / 2;
        }
        return (int) (recyclerListView.getMeasuredHeight() / 2.5f);
    }
}
