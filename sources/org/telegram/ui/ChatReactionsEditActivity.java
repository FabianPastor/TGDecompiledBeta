package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;

public class ChatReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_availableReaction> availableReactions = new ArrayList<>();
    private long chatId;
    /* access modifiers changed from: private */
    public List<String> chatReactions = new ArrayList();
    private LinearLayout contentView;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    private TextCheckCell enableReactionsCell;
    private TLRPC$ChatFull info;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;

    public ChatReactionsEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getLong("chat_id", 0);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:7:0x004c, code lost:
        if (r0 == null) goto L_0x004e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onFragmentCreate() {
        /*
            r11 = this;
            org.telegram.messenger.MessagesController r0 = r11.getMessagesController()
            long r1 = r11.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r11.currentChat = r0
            if (r0 != 0) goto L_0x004f
            int r0 = r11.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r1 = r11.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r1)
            r11.currentChat = r0
            r1 = 0
            if (r0 == 0) goto L_0x004e
            org.telegram.messenger.MessagesController r0 = r11.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r2 = r11.currentChat
            r3 = 1
            r0.putChat(r2, r3)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r11.info
            if (r0 != 0) goto L_0x004f
            int r0 = r11.currentAccount
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r5 = r11.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            java.util.concurrent.CountDownLatch r8 = new java.util.concurrent.CountDownLatch
            r8.<init>(r3)
            r9 = 0
            r10 = 0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r4.loadChatInfo(r5, r7, r8, r9, r10)
            r11.info = r0
            if (r0 != 0) goto L_0x004f
        L_0x004e:
            return r1
        L_0x004f:
            org.telegram.messenger.NotificationCenter r0 = r11.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.reactionsDidLoad
            r0.addObserver(r11, r1)
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatReactionsEditActivity.onFragmentCreate():boolean");
    }

    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", R.string.Reactions));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatReactionsEditActivity.this.finishFragment();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
        TextCheckCell textCheckCell = new TextCheckCell(context);
        this.enableReactionsCell = textCheckCell;
        textCheckCell.setHeight(56);
        this.enableReactionsCell.setTextAndCheck(LocaleController.getString("EnableReactions", R.string.EnableReactions), true ^ this.chatReactions.isEmpty(), false);
        TextCheckCell textCheckCell2 = this.enableReactionsCell;
        textCheckCell2.setBackgroundColor(Theme.getColor(textCheckCell2.isChecked() ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
        this.enableReactionsCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.enableReactionsCell.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda0(this));
        linearLayout.addView(this.enableReactionsCell, LayoutHelper.createLinear(-1, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r2 = new RecyclerView.Adapter() {
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 0;
                }
                return i == 1 ? 1 : 2;
            }

            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i == 0) {
                    return new RecyclerListView.Holder(new TextInfoPrivacyCell(context));
                }
                if (i != 1) {
                    return new RecyclerListView.Holder(new AvailableReactionCell(context, false, false));
                }
                return new RecyclerListView.Holder(new HeaderCell(context, 23));
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                String str;
                int itemViewType = getItemViewType(i);
                if (itemViewType == 0) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                    if (ChatObject.isChannelAndNotMegaGroup(ChatReactionsEditActivity.this.currentChat)) {
                        str = LocaleController.getString("EnableReactionsChannelInfo", R.string.EnableReactionsChannelInfo);
                    } else {
                        str = LocaleController.getString("EnableReactionsGroupInfo", R.string.EnableReactionsGroupInfo);
                    }
                    textInfoPrivacyCell.setText(str);
                } else if (itemViewType == 1) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setText(LocaleController.getString("AvailableReactions", R.string.AvailableReactions));
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (itemViewType == 2) {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) ChatReactionsEditActivity.this.availableReactions.get(i - 2);
                    ((AvailableReactionCell) viewHolder.itemView).bind(tLRPC$TL_availableReaction, ChatReactionsEditActivity.this.chatReactions.contains(tLRPC$TL_availableReaction.reaction), ChatReactionsEditActivity.this.currentAccount);
                }
            }

            public int getItemCount() {
                return (!ChatReactionsEditActivity.this.chatReactions.isEmpty() ? ChatReactionsEditActivity.this.availableReactions.size() + 1 : 0) + 1;
            }
        };
        this.listAdapter = r2;
        recyclerListView2.setAdapter(r2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatReactionsEditActivity$$ExternalSyntheticLambda2(this));
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        return this.contentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        setCheckedEnableReactionCell(!this.enableReactionsCell.isChecked());
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i) {
        if (i > 1) {
            AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = this.availableReactions.get(i - 2);
            boolean z = !this.chatReactions.contains(tLRPC$TL_availableReaction.reaction);
            if (z) {
                this.chatReactions.add(tLRPC$TL_availableReaction.reaction);
            } else {
                this.chatReactions.remove(tLRPC$TL_availableReaction.reaction);
                if (this.chatReactions.isEmpty()) {
                    setCheckedEnableReactionCell(false);
                }
            }
            availableReactionCell.setChecked(z, true);
        }
    }

    private void setCheckedEnableReactionCell(boolean z) {
        if (this.enableReactionsCell.isChecked() != z) {
            this.enableReactionsCell.setChecked(z);
            int color = Theme.getColor(z ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
            if (z) {
                this.enableReactionsCell.setBackgroundColorAnimated(z, color);
            } else {
                this.enableReactionsCell.setBackgroundColorAnimatedReverse(color);
            }
            if (z) {
                Iterator<TLRPC$TL_availableReaction> it = this.availableReactions.iterator();
                while (it.hasNext()) {
                    this.chatReactions.add(it.next().reaction);
                }
                this.listAdapter.notifyItemRangeInserted(1, this.availableReactions.size() + 1);
                return;
            }
            this.chatReactions.clear();
            this.listAdapter.notifyItemRangeRemoved(1, this.availableReactions.size() + 1);
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        boolean z = true;
        if (tLRPC$ChatFull != null) {
            z = true ^ tLRPC$ChatFull.available_reactions.equals(this.chatReactions);
        }
        if (z) {
            getMessagesController().setChatReactions(this.chatId, this.chatReactions);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.chatReactions = new ArrayList(tLRPC$ChatFull.available_reactions);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ChatReactionsEditActivity$$ExternalSyntheticLambda1(this), "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.enableReactionsCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
        this.listAdapter.notifyDataSetChanged();
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 == this.currentAccount && i == NotificationCenter.reactionsDidLoad) {
            this.availableReactions.clear();
            this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
