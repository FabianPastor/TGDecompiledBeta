package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messages_messagesSlice;
import org.telegram.tgnet.TLRPC$TL_stats_getMessagePublicForwards;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ForwardsActivity extends BaseFragment {
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    /* access modifiers changed from: private */
    public int endRow;
    private boolean firstLoaded;
    /* access modifiers changed from: private */
    public int headerRow;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int loadingRow;
    /* access modifiers changed from: private */
    public MessageObject messageObject;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    private int nextRate;
    /* access modifiers changed from: private */
    public int privateRow;
    /* access modifiers changed from: private */
    public int publicChats;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int sectionRow;
    /* access modifiers changed from: private */
    public int startRow;

    public ForwardsActivity(MessageObject messageObject2) {
        this.messageObject = messageObject2;
    }

    private void updateRows() {
        this.sectionRow = -1;
        this.headerRow = -1;
        this.startRow = -1;
        this.endRow = -1;
        this.loadingRow = -1;
        this.privateRow = -1;
        this.rowCount = 0;
        if (this.firstLoaded) {
            int i = 0 + 1;
            this.rowCount = i;
            this.headerRow = 0;
            if (this.messageObject.messageOwner.forwards - this.publicChats > 0) {
                this.rowCount = i + 1;
                this.privateRow = i;
            }
            if (!this.messages.isEmpty()) {
                int i2 = this.rowCount;
                this.startRow = i2;
                int size = i2 + this.messages.size();
                this.rowCount = size;
                this.endRow = size;
                if (!this.endReached) {
                    this.rowCount = size + 1;
                    this.loadingRow = size;
                }
            }
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.sectionRow = i3;
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        loadChats(100);
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Shares", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ForwardsActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.setVisibility(8);
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ForwardsActivity.this.lambda$createView$0$ForwardsActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int i3;
                int findFirstVisibleItemPosition = ForwardsActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition == -1) {
                    i3 = 0;
                } else {
                    i3 = Math.abs(ForwardsActivity.this.layoutManager.findLastVisibleItemPosition() - findFirstVisibleItemPosition) + 1;
                }
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (i3 > 0 && !ForwardsActivity.this.endReached && !ForwardsActivity.this.loading && !ForwardsActivity.this.messages.isEmpty() && findFirstVisibleItemPosition + i3 >= itemCount - 5) {
                    ForwardsActivity.this.loadChats(100);
                }
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ForwardsActivity(View view, int i) {
        int i2 = this.startRow;
        if (i >= i2 && i < this.endRow) {
            TLRPC$Message tLRPC$Message = this.messages.get(i - i2);
            int dialogId = (int) MessageObject.getDialogId(tLRPC$Message);
            Bundle bundle = new Bundle();
            if (dialogId > 0) {
                bundle.putInt("user_id", dialogId);
            } else {
                bundle.putInt("chat_id", -dialogId);
            }
            bundle.putInt("message_id", tLRPC$Message.id);
            if (getMessagesController().checkCanOpenChat(bundle, this)) {
                presentFragment(new ChatActivity(bundle));
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadChats(int i) {
        if (!this.loading) {
            this.loading = true;
            if (this.emptyView != null && this.messages.isEmpty()) {
                this.emptyView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC$TL_stats_getMessagePublicForwards tLRPC$TL_stats_getMessagePublicForwards = new TLRPC$TL_stats_getMessagePublicForwards();
            tLRPC$TL_stats_getMessagePublicForwards.msg_id = this.messageObject.getId();
            tLRPC$TL_stats_getMessagePublicForwards.limit = i;
            tLRPC$TL_stats_getMessagePublicForwards.channel = getMessagesController().getInputChannel((int) (-this.messageObject.getDialogId()));
            if (!this.messages.isEmpty()) {
                ArrayList<TLRPC$Message> arrayList = this.messages;
                TLRPC$Message tLRPC$Message = arrayList.get(arrayList.size());
                tLRPC$TL_stats_getMessagePublicForwards.offset_id = tLRPC$Message.id;
                tLRPC$TL_stats_getMessagePublicForwards.offset_peer = getMessagesController().getInputPeer((int) MessageObject.getDialogId(tLRPC$Message));
                tLRPC$TL_stats_getMessagePublicForwards.offset_rate = this.nextRate;
            } else {
                tLRPC$TL_stats_getMessagePublicForwards.offset_peer = new TLRPC$TL_inputPeerEmpty();
            }
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_stats_getMessagePublicForwards, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ForwardsActivity.this.lambda$loadChats$2$ForwardsActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$loadChats$2$ForwardsActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ForwardsActivity.this.lambda$null$1$ForwardsActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$1$ForwardsActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            if ((tLRPC$messages_Messages.flags & 1) != 0) {
                this.nextRate = tLRPC$messages_Messages.next_rate;
            }
            int i = tLRPC$messages_Messages.count;
            if (i != 0) {
                this.publicChats = i;
            } else if (this.publicChats == 0) {
                this.publicChats = tLRPC$messages_Messages.messages.size();
            }
            this.endReached = !(tLRPC$messages_Messages instanceof TLRPC$TL_messages_messagesSlice);
            getMessagesController().putChats(tLRPC$messages_Messages.chats, false);
            getMessagesController().putUsers(tLRPC$messages_Messages.users, false);
            this.messages.addAll(tLRPC$messages_Messages.messages);
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        this.firstLoaded = true;
        this.loading = false;
        updateRows();
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 0) {
                return ((ManageChatUserCell) viewHolder.itemView).getCurrentObject() instanceof TLObject;
            }
            return false;
        }

        public int getItemCount() {
            return ForwardsActivity.this.rowCount;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v3, resolved type: org.telegram.ui.Cells.ManageChatUserCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v7, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r10v2 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                java.lang.String r9 = "windowBackgroundWhite"
                r0 = 2
                if (r10 == 0) goto L_0x0044
                r1 = 1
                if (r10 == r1) goto L_0x003c
                if (r10 == r0) goto L_0x001f
                org.telegram.ui.Cells.LoadingCell r9 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r10 = r8.mContext
                r0 = 1109393408(0x42200000, float:40.0)
                int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
                r1 = 1123024896(0x42var_, float:120.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r9.<init>(r10, r0, r1)
                goto L_0x0055
            L_0x001f:
                org.telegram.ui.Cells.HeaderCell r10 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r3 = r8.mContext
                r5 = 21
                r6 = 11
                r7 = 0
                java.lang.String r4 = "windowBackgroundWhiteBlueHeader"
                r2 = r10
                r2.<init>(r3, r4, r5, r6, r7)
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r10.setBackgroundColor(r9)
                r9 = 43
                r10.setHeight(r9)
                goto L_0x0054
            L_0x003c:
                org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                goto L_0x0055
            L_0x0044:
                org.telegram.ui.Cells.ManageChatUserCell r10 = new org.telegram.ui.Cells.ManageChatUserCell
                android.content.Context r1 = r8.mContext
                r2 = 6
                r3 = 0
                r10.<init>(r1, r2, r0, r3)
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r10.setBackgroundColor(r9)
            L_0x0054:
                r9 = r10
            L_0x0055:
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ForwardsActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            Object obj;
            String str2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                if (i == ForwardsActivity.this.privateRow) {
                    String formatPluralString = LocaleController.formatPluralString("Shared", ForwardsActivity.this.messageObject.messageOwner.forwards - ForwardsActivity.this.publicChats);
                    String string = LocaleController.getString("SharedToPrivateMessagesAndGroups", NUM);
                    if (ForwardsActivity.this.startRow == -1) {
                        z = false;
                    }
                    manageChatUserCell.setData(1, formatPluralString, string, z);
                    return;
                }
                TLRPC$Message item = getItem(i);
                int dialogId = (int) MessageObject.getDialogId(item);
                if (dialogId > 0) {
                    obj = ForwardsActivity.this.getMessagesController().getUser(Integer.valueOf(dialogId));
                    str = null;
                } else {
                    TLRPC$Chat chat = ForwardsActivity.this.getMessagesController().getChat(Integer.valueOf(-dialogId));
                    if (chat.participants_count != 0) {
                        if (!ChatObject.isChannel(chat) || chat.megagroup) {
                            str2 = LocaleController.formatPluralString("Members", chat.participants_count);
                        } else {
                            str2 = LocaleController.formatPluralString("Subscribers", chat.participants_count);
                        }
                        str = String.format("%1$s, %2$s", new Object[]{str2, LocaleController.formatDateAudio((long) item.date, false)});
                    } else {
                        str = null;
                    }
                    obj = chat;
                }
                if (obj != null) {
                    if (i == ForwardsActivity.this.endRow - 1) {
                        z = false;
                    }
                    manageChatUserCell.setData(obj, (CharSequence) null, str, z);
                }
            } else if (itemViewType == 1) {
                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            } else if (itemViewType == 2) {
                ((HeaderCell) viewHolder.itemView).setText(LocaleController.formatPluralString("Shares", ForwardsActivity.this.messageObject.messageOwner.forwards));
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == ForwardsActivity.this.sectionRow) {
                return 1;
            }
            if (i == ForwardsActivity.this.headerRow) {
                return 2;
            }
            return i == ForwardsActivity.this.loadingRow ? 3 : 0;
        }

        public TLRPC$Message getItem(int i) {
            if (i < ForwardsActivity.this.startRow || i >= ForwardsActivity.this.endRow) {
                return null;
            }
            return (TLRPC$Message) ForwardsActivity.this.messages.get(i - ForwardsActivity.this.startRow);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$ForwardsActivity$wA766VKhqZYCLASSNAMErmcflOdohwpys r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ForwardsActivity.this.lambda$getThemeDescriptions$3$ForwardsActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$ForwardsActivity$wA766VKhqZYCLASSNAMErmcflOdohwpys r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$ForwardsActivity$wA766VKhqZYCLASSNAMErmcflOdohwpys r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        return arrayList;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$3$ForwardsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }
}
