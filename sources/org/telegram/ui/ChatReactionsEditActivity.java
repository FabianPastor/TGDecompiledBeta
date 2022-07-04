package org.telegram.ui;

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
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;
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
    public static final String KEY_CHAT_ID = "chat_id";
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_INFO = 0;
    private static final int TYPE_REACTION = 2;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_availableReaction> availableReactions = new ArrayList<>();
    private long chatId;
    /* access modifiers changed from: private */
    public List<String> chatReactions = new ArrayList();
    private LinearLayout contentView;
    /* access modifiers changed from: private */
    public TLRPC.Chat currentChat;
    private TextCheckCell enableReactionsCell;
    private TLRPC.ChatFull info;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;

    public ChatReactionsEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getLong("chat_id", 0);
    }

    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, ChatObject.isChannel(this.currentChat), new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        return super.onFragmentCreate();
    }

    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatReactionsEditActivity.this.finishFragment();
                }
            }
        });
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
        TextCheckCell textCheckCell = new TextCheckCell(context);
        this.enableReactionsCell = textCheckCell;
        textCheckCell.setHeight(56);
        this.enableReactionsCell.setTextAndCheck(LocaleController.getString("EnableReactions", NUM), true ^ this.chatReactions.isEmpty(), false);
        TextCheckCell textCheckCell2 = this.enableReactionsCell;
        textCheckCell2.setBackgroundColor(Theme.getColor(textCheckCell2.isChecked() ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
        this.enableReactionsCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.enableReactionsCell.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda0(this));
        ll.addView(this.enableReactionsCell, LayoutHelper.createLinear(-1, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r2 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        return new RecyclerListView.Holder(new TextInfoPrivacyCell(context));
                    case 1:
                        return new RecyclerListView.Holder(new HeaderCell(context, 23));
                    default:
                        return new RecyclerListView.Holder(new AvailableReactionCell(context, false));
                }
            }

            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                String str;
                switch (getItemViewType(position)) {
                    case 0:
                        TextInfoPrivacyCell infoCell = (TextInfoPrivacyCell) holder.itemView;
                        infoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                        if (ChatObject.isChannelAndNotMegaGroup(ChatReactionsEditActivity.this.currentChat)) {
                            str = LocaleController.getString("EnableReactionsChannelInfo", NUM);
                        } else {
                            str = LocaleController.getString("EnableReactionsGroupInfo", NUM);
                        }
                        infoCell.setText(str);
                        return;
                    case 1:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        headerCell.setText(LocaleController.getString("AvailableReactions", NUM));
                        headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        return;
                    case 2:
                        TLRPC.TL_availableReaction react = (TLRPC.TL_availableReaction) ChatReactionsEditActivity.this.availableReactions.get(position - 2);
                        ((AvailableReactionCell) holder.itemView).bind(react, ChatReactionsEditActivity.this.chatReactions.contains(react.reaction));
                        return;
                    default:
                        return;
                }
            }

            public int getItemCount() {
                return (!ChatReactionsEditActivity.this.chatReactions.isEmpty() ? ChatReactionsEditActivity.this.availableReactions.size() + 1 : 0) + 1;
            }

            public int getItemViewType(int position) {
                if (position == 0) {
                    return 0;
                }
                return position == 1 ? 1 : 2;
            }
        };
        this.listAdapter = r2;
        recyclerListView2.setAdapter(r2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatReactionsEditActivity$$ExternalSyntheticLambda2(this));
        ll.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        this.contentView = ll;
        this.fragmentView = ll;
        updateColors();
        return this.contentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatReactionsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3270lambda$createView$0$orgtelegramuiChatReactionsEditActivity(View v) {
        setCheckedEnableReactionCell(!this.enableReactionsCell.isChecked());
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatReactionsEditActivity  reason: not valid java name */
    public /* synthetic */ void m3271lambda$createView$1$orgtelegramuiChatReactionsEditActivity(View view, int position) {
        if (position > 1) {
            AvailableReactionCell cell = (AvailableReactionCell) view;
            TLRPC.TL_availableReaction react = this.availableReactions.get(position - 2);
            boolean nc = !this.chatReactions.contains(react.reaction);
            if (nc) {
                this.chatReactions.add(react.reaction);
            } else {
                this.chatReactions.remove(react.reaction);
                if (this.chatReactions.isEmpty()) {
                    setCheckedEnableReactionCell(false);
                }
            }
            cell.setChecked(nc, true);
        }
    }

    private void setCheckedEnableReactionCell(boolean c) {
        if (this.enableReactionsCell.isChecked() != c) {
            this.enableReactionsCell.setChecked(c);
            int clr = Theme.getColor(c ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
            if (c) {
                this.enableReactionsCell.setBackgroundColorAnimated(c, clr);
            } else {
                this.enableReactionsCell.setBackgroundColorAnimatedReverse(clr);
            }
            if (c) {
                Iterator<TLRPC.TL_availableReaction> it = this.availableReactions.iterator();
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
        boolean changed = true;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            changed = !chatFull.available_reactions.equals(this.chatReactions);
        }
        if (changed) {
            getMessagesController().setChatReactions(this.chatId, this.chatReactions);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    public void setInfo(TLRPC.ChatFull info2) {
        this.info = info2;
        if (info2 != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.chatReactions = new ArrayList(info2.available_reactions);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ChatReactionsEditActivity$$ExternalSyntheticLambda1(this), "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.enableReactionsCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
        this.listAdapter.notifyDataSetChanged();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (account == this.currentAccount && id == NotificationCenter.reactionsDidLoad) {
            this.availableReactions.clear();
            this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
