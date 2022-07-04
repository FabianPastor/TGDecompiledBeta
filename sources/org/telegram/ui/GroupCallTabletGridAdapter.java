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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;

public class GroupCallTabletGridAdapter extends RecyclerListView.SelectionAdapter {
    private final GroupCallActivity activity;
    private ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final int currentAccount;
    private ChatObject.Call groupCall;
    private GroupCallRenderersContainer renderersContainer;
    /* access modifiers changed from: private */
    public final ArrayList<ChatObject.VideoParticipant> videoParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean visible = false;

    public GroupCallTabletGridAdapter(ChatObject.Call groupCall2, int currentAccount2, GroupCallActivity activity2) {
        this.groupCall = groupCall2;
        this.currentAccount = currentAccount2;
        this.activity = activity2;
    }

    public void setRenderersPool(ArrayList<GroupCallMiniTextureView> attachedRenderers2, GroupCallRenderersContainer renderersContainer2) {
        this.attachedRenderers = attachedRenderers2;
        this.renderersContainer = renderersContainer2;
    }

    public void setGroupCall(ChatObject.Call groupCall2) {
        this.groupCall = groupCall2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        return false;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerListView.Holder(new GroupCallGridCell(parent.getContext(), true) {
            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (GroupCallTabletGridAdapter.this.visible && getParticipant() != null) {
                    GroupCallTabletGridAdapter.this.attachRenderer(this, true);
                }
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                GroupCallTabletGridAdapter.this.attachRenderer(this, false);
            }
        });
    }

    /* access modifiers changed from: private */
    public void attachRenderer(GroupCallGridCell cell, boolean attach) {
        if (attach && cell.getRenderer() == null) {
            cell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, (GroupCallGridCell) null, (GroupCallFullscreenAdapter.GroupCallUserCell) null, cell, cell.getParticipant(), this.groupCall, this.activity));
        } else if (!attach && cell.getRenderer() != null) {
            cell.getRenderer().setTabletGridView((GroupCallGridCell) null);
            cell.setRenderer((GroupCallMiniTextureView) null);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupCallGridCell cell = (GroupCallGridCell) holder.itemView;
        ChatObject.VideoParticipant oldVideoParticipant = cell.getParticipant();
        ChatObject.VideoParticipant videoParticipant = this.videoParticipants.get(position);
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.videoParticipants.get(position).participant;
        cell.spanCount = getSpanCount(position);
        cell.position = position;
        cell.gridAdapter = this;
        if (cell.getMeasuredHeight() != getItemHeight(position)) {
            cell.requestLayout();
        }
        AccountInstance instance = AccountInstance.getInstance(this.currentAccount);
        ChatObject.Call call = this.groupCall;
        cell.setData(instance, videoParticipant, call, MessageObject.getPeerId(call.selfPeer));
        if (oldVideoParticipant != null && !oldVideoParticipant.equals(videoParticipant) && cell.attached && cell.getRenderer() != null) {
            attachRenderer(cell, false);
            attachRenderer(cell, true);
        } else if (cell.getRenderer() != null) {
            cell.getRenderer().updateAttachState(true);
        }
    }

    public int getItemCount() {
        return this.videoParticipants.size();
    }

    public void setVisibility(RecyclerListView listView, boolean visibility, boolean updateAttach) {
        this.visible = visibility;
        if (updateAttach) {
            for (int i = 0; i < listView.getChildCount(); i++) {
                View view = listView.getChildAt(i);
                if (view instanceof GroupCallGridCell) {
                    GroupCallGridCell cell = (GroupCallGridCell) view;
                    if (cell.getParticipant() != null) {
                        attachRenderer(cell, visibility);
                    }
                }
            }
        }
    }

    public void scrollToPeerId(long peerId, RecyclerListView fullscreenUsersListView) {
    }

    public void update(boolean animated, RecyclerListView listView) {
        if (this.groupCall != null) {
            if (animated) {
                final ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>();
                oldVideoParticipants.addAll(this.videoParticipants);
                this.videoParticipants.clear();
                this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
                DiffUtil.calculateDiff(new DiffUtil.Callback() {
                    public int getOldListSize() {
                        return oldVideoParticipants.size();
                    }

                    public int getNewListSize() {
                        return GroupCallTabletGridAdapter.this.videoParticipants.size();
                    }

                    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                        if (oldItemPosition >= oldVideoParticipants.size() || newItemPosition >= GroupCallTabletGridAdapter.this.videoParticipants.size()) {
                            return false;
                        }
                        return ((ChatObject.VideoParticipant) oldVideoParticipants.get(oldItemPosition)).equals(GroupCallTabletGridAdapter.this.videoParticipants.get(newItemPosition));
                    }

                    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                        return true;
                    }
                }).dispatchUpdatesTo((RecyclerView.Adapter) this);
                AndroidUtilities.updateVisibleRows(listView);
                return;
            }
            this.videoParticipants.clear();
            this.videoParticipants.addAll(this.groupCall.visibleVideoParticipants);
            notifyDataSetChanged();
        }
    }

    public int getSpanCount(int position) {
        int itemsCount = getItemCount();
        if (itemsCount <= 1 || itemsCount == 2) {
            return 6;
        }
        if (itemsCount != 3 || position == 0 || position == 1) {
            return 3;
        }
        return 6;
    }

    public int getItemHeight(int position) {
        View parentView = this.activity.tabletVideoGridView;
        int itemsCount = getItemCount();
        if (itemsCount <= 1) {
            return parentView.getMeasuredHeight();
        }
        if (itemsCount <= 4) {
            return parentView.getMeasuredHeight() / 2;
        }
        return (int) (((float) parentView.getMeasuredHeight()) / 2.5f);
    }
}
