package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsNone;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;

public class ChatReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private RadioCell allReactions;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_availableReaction> availableReactions = new ArrayList<>();
    private long chatId;
    /* access modifiers changed from: private */
    public List<String> chatReactions = new ArrayList();
    private LinearLayout contentView;
    LinearLayout contorlsLayout;
    /* access modifiers changed from: private */
    public TLRPC$Chat currentChat;
    private RadioCell disableReactions;
    private TextCheckCell enableReactionsCell;
    private TLRPC$ChatFull info;
    boolean isChannel;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    ArrayList<RadioCell> radioCells = new ArrayList<>();
    int selectedType = -1;
    private RadioCell someReactions;
    int startFromType;

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
        this.isChannel = ChatObject.isChannelAndNotMegaGroup(this.chatId, this.currentAccount);
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
        if (this.isChannel) {
            TextCheckCell textCheckCell = new TextCheckCell(context);
            this.enableReactionsCell = textCheckCell;
            textCheckCell.setHeight(56);
            this.enableReactionsCell.setTextAndCheck(LocaleController.getString("EnableReactions", R.string.EnableReactions), !this.chatReactions.isEmpty(), false);
            TextCheckCell textCheckCell2 = this.enableReactionsCell;
            textCheckCell2.setBackgroundColor(Theme.getColor(textCheckCell2.isChecked() ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
            this.enableReactionsCell.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.enableReactionsCell.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda2(this));
            linearLayout.addView(this.enableReactionsCell, LayoutHelper.createLinear(-1, -2));
        }
        HeaderCell headerCell = new HeaderCell(context);
        headerCell.setText(LocaleController.getString("AvailableReactions", R.string.AvailableReactions));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.contorlsLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        RadioCell radioCell = new RadioCell(context);
        this.allReactions = radioCell;
        radioCell.setText(LocaleController.getString("AllReactions", R.string.AllReactions), false, true);
        RadioCell radioCell2 = new RadioCell(context);
        this.someReactions = radioCell2;
        radioCell2.setText(LocaleController.getString("SomeReactions", R.string.SomeReactions), false, true);
        RadioCell radioCell3 = new RadioCell(context);
        this.disableReactions = radioCell3;
        radioCell3.setText(LocaleController.getString("NoReactions", R.string.NoReactions), false, false);
        this.contorlsLayout.addView(headerCell, LayoutHelper.createLinear(-1, -2));
        this.contorlsLayout.addView(this.allReactions, LayoutHelper.createLinear(-1, -2));
        this.contorlsLayout.addView(this.someReactions, LayoutHelper.createLinear(-1, -2));
        this.contorlsLayout.addView(this.disableReactions, LayoutHelper.createLinear(-1, -2));
        this.radioCells.clear();
        this.radioCells.add(this.allReactions);
        this.radioCells.add(this.someReactions);
        this.radioCells.add(this.disableReactions);
        this.allReactions.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda3(this));
        this.someReactions.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda1(this));
        this.disableReactions.setOnClickListener(new ChatReactionsEditActivity$$ExternalSyntheticLambda0(this));
        headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.allReactions.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
        this.someReactions.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
        this.disableReactions.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor("windowBackgroundWhite"), Theme.getColor("listSelectorSDK21")));
        setCheckedEnableReactionCell(this.startFromType, false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        AnonymousClass2 r2 = new RecyclerView.Adapter() {
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i == 0) {
                    return new RecyclerListView.Holder(new TextInfoPrivacyCell(context));
                }
                if (i == 1) {
                    return new RecyclerListView.Holder(new HeaderCell(context, 23));
                }
                if (i != 3) {
                    return new RecyclerListView.Holder(new AvailableReactionCell(context, false, false));
                }
                FrameLayout frameLayout = new FrameLayout(context);
                if (ChatReactionsEditActivity.this.contorlsLayout.getParent() != null) {
                    ((ViewGroup) ChatReactionsEditActivity.this.contorlsLayout.getParent()).removeView(ChatReactionsEditActivity.this.contorlsLayout);
                }
                frameLayout.addView(ChatReactionsEditActivity.this.contorlsLayout);
                frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(frameLayout);
            }

            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                String str;
                int itemViewType = getItemViewType(i);
                int i2 = 2;
                if (itemViewType == 0) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                    ChatReactionsEditActivity chatReactionsEditActivity = ChatReactionsEditActivity.this;
                    if (chatReactionsEditActivity.isChannel) {
                        if (ChatObject.isChannelAndNotMegaGroup(chatReactionsEditActivity.currentChat)) {
                            str = LocaleController.getString("EnableReactionsChannelInfo", R.string.EnableReactionsChannelInfo);
                        } else {
                            str = LocaleController.getString("EnableReactionsGroupInfo", R.string.EnableReactionsGroupInfo);
                        }
                        textInfoPrivacyCell.setText(str);
                        return;
                    }
                    int i3 = chatReactionsEditActivity.selectedType;
                    if (i3 == 1) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EnableSomeReactionsInfo", R.string.EnableSomeReactionsInfo));
                    } else if (i3 == 0) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EnableAllReactionsInfo", R.string.EnableAllReactionsInfo));
                    } else if (i3 == 2) {
                        textInfoPrivacyCell.setText(LocaleController.getString("DisableReactionsInfo", R.string.DisableReactionsInfo));
                    }
                } else if (itemViewType == 1) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setText(LocaleController.getString("OnlyAllowThisReactions", R.string.OnlyAllowThisReactions));
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (itemViewType == 2) {
                    AvailableReactionCell availableReactionCell = (AvailableReactionCell) viewHolder.itemView;
                    ArrayList access$100 = ChatReactionsEditActivity.this.availableReactions;
                    if (!ChatReactionsEditActivity.this.isChannel) {
                        i2 = 3;
                    }
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) access$100.get(i - i2);
                    availableReactionCell.bind(tLRPC$TL_availableReaction, ChatReactionsEditActivity.this.chatReactions.contains(tLRPC$TL_availableReaction.reaction), ChatReactionsEditActivity.this.currentAccount);
                }
            }

            public int getItemCount() {
                ChatReactionsEditActivity chatReactionsEditActivity = ChatReactionsEditActivity.this;
                int i = 0;
                if (chatReactionsEditActivity.isChannel) {
                    if (!chatReactionsEditActivity.chatReactions.isEmpty()) {
                        i = ChatReactionsEditActivity.this.availableReactions.size() + 1;
                    }
                    return i + 1;
                }
                if (!chatReactionsEditActivity.chatReactions.isEmpty()) {
                    i = ChatReactionsEditActivity.this.availableReactions.size() + 1;
                }
                return i + 2;
            }

            public int getItemViewType(int i) {
                if (ChatReactionsEditActivity.this.isChannel) {
                    if (i == 0) {
                        return 0;
                    }
                    return i == 1 ? 1 : 2;
                } else if (i == 0) {
                    return 3;
                } else {
                    if (i == 1) {
                        return 0;
                    }
                    return i == 2 ? 1 : 2;
                }
            }
        };
        this.listAdapter = r2;
        recyclerListView2.setAdapter(r2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatReactionsEditActivity$$ExternalSyntheticLambda5(this));
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        return this.contentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        setCheckedEnableReactionCell(this.enableReactionsCell.isChecked() ? 2 : 1, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        setCheckedEnableReactionCell(0, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        setCheckedEnableReactionCell(1, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        setCheckedEnableReactionCell(2, true);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view, int i) {
        boolean z = this.isChannel;
        if (i > (z ? 1 : 2)) {
            AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction = this.availableReactions.get(i - (z ? 2 : 3));
            boolean z2 = !this.chatReactions.contains(tLRPC$TL_availableReaction.reaction);
            if (z2) {
                this.chatReactions.add(tLRPC$TL_availableReaction.reaction);
            } else {
                this.chatReactions.remove(tLRPC$TL_availableReaction.reaction);
                if (this.chatReactions.isEmpty()) {
                    setCheckedEnableReactionCell(2, true);
                }
            }
            availableReactionCell.setChecked(z2, true);
        }
    }

    private void setCheckedEnableReactionCell(int i, boolean z) {
        RecyclerView.Adapter adapter;
        if (this.selectedType != i) {
            TextCheckCell textCheckCell = this.enableReactionsCell;
            if (textCheckCell != null) {
                boolean z2 = i == 1;
                textCheckCell.setChecked(z2);
                int color = Theme.getColor(z2 ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
                if (z2) {
                    this.enableReactionsCell.setBackgroundColorAnimated(z2, color);
                } else {
                    this.enableReactionsCell.setBackgroundColorAnimatedReverse(color);
                }
            }
            this.selectedType = i;
            int i2 = 0;
            while (i2 < this.radioCells.size()) {
                this.radioCells.get(i2).setChecked(i == i2, z);
                i2++;
            }
            int i3 = 2;
            if (i == 1) {
                if (z) {
                    this.chatReactions.clear();
                    Iterator<TLRPC$TL_availableReaction> it = this.availableReactions.iterator();
                    while (it.hasNext()) {
                        TLRPC$TL_availableReaction next = it.next();
                        if (next.reaction.equals("👍") || next.reaction.equals("👎")) {
                            this.chatReactions.add(next.reaction);
                        }
                    }
                    if (this.chatReactions.isEmpty() && this.availableReactions.size() >= 2) {
                        this.chatReactions.add(this.availableReactions.get(0).reaction);
                        this.chatReactions.add(this.availableReactions.get(1).reaction);
                    }
                }
                RecyclerView.Adapter adapter2 = this.listAdapter;
                if (adapter2 != null && z) {
                    if (this.isChannel) {
                        i3 = 1;
                    }
                    adapter2.notifyItemRangeInserted(i3, this.availableReactions.size() + 1);
                }
            } else if (!this.chatReactions.isEmpty()) {
                this.chatReactions.clear();
                RecyclerView.Adapter adapter3 = this.listAdapter;
                if (adapter3 != null && z) {
                    if (this.isChannel) {
                        i3 = 1;
                    }
                    adapter3.notifyItemRangeRemoved(i3, this.availableReactions.size() + 1);
                }
            }
            if (!this.isChannel && (adapter = this.listAdapter) != null && z) {
                adapter.notifyItemChanged(1);
            }
            RecyclerView.Adapter adapter4 = this.listAdapter;
            if (adapter4 != null && !z) {
                adapter4.notifyDataSetChanged();
            }
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getMessagesController().setChatReactions(this.chatId, this.selectedType, this.chatReactions);
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.chatReactions = new ArrayList();
            TLRPC$ChatReactions tLRPC$ChatReactions = tLRPC$ChatFull.available_reactions;
            if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsAll) {
                this.startFromType = 0;
            } else if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsNone) {
                this.startFromType = 2;
            } else if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
                TLRPC$TL_chatReactionsSome tLRPC$TL_chatReactionsSome = (TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions;
                for (int i = 0; i < tLRPC$TL_chatReactionsSome.reactions.size(); i++) {
                    if (tLRPC$TL_chatReactionsSome.reactions.get(i) instanceof TLRPC$TL_reactionEmoji) {
                        this.chatReactions.add(((TLRPC$TL_reactionEmoji) tLRPC$TL_chatReactionsSome.reactions.get(i)).emoticon);
                    }
                }
                this.startFromType = 1;
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ChatReactionsEditActivity$$ExternalSyntheticLambda4(this), "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        TextCheckCell textCheckCell = this.enableReactionsCell;
        if (textCheckCell != null) {
            textCheckCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
        }
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
