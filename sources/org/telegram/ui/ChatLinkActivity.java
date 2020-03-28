package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_channels_getGroupsForDiscussion;
import org.telegram.tgnet.TLRPC$TL_channels_setDiscussionGroup;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC$messages_Chats;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatLinkActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateFinalActivity;

public class ChatLinkActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int chatEndRow;
    /* access modifiers changed from: private */
    public int chatStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    /* access modifiers changed from: private */
    public int createChatRow;
    private TLRPC$Chat currentChat;
    private int currentChatId;
    /* access modifiers changed from: private */
    public int detailRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public TLRPC$ChatFull info;
    /* access modifiers changed from: private */
    public boolean isChannel;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loadingChats;
    /* access modifiers changed from: private */
    public int removeChatRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private TLRPC$Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;

    public ChatLinkActivity(int i) {
        this.currentChatId = i;
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
    }

    private void updateRows() {
        TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat != null) {
            int i = 0;
            this.rowCount = 0;
            this.helpRow = -1;
            this.createChatRow = -1;
            this.chatStartRow = -1;
            this.chatEndRow = -1;
            this.removeChatRow = -1;
            this.detailRow = -1;
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.helpRow = 0;
            if (this.isChannel) {
                if (this.info.linked_chat_id == 0) {
                    this.rowCount = i2 + 1;
                    this.createChatRow = i2;
                }
                int i3 = this.rowCount;
                this.chatStartRow = i3;
                int size = i3 + this.chats.size();
                this.rowCount = size;
                this.chatEndRow = size;
                if (this.info.linked_chat_id != 0) {
                    this.rowCount = size + 1;
                    this.createChatRow = size;
                }
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.detailRow = i4;
            } else {
                this.chatStartRow = i2;
                int size2 = i2 + this.chats.size();
                this.rowCount = size2;
                this.chatEndRow = size2;
                int i5 = size2 + 1;
                this.rowCount = i5;
                this.createChatRow = size2;
                this.rowCount = i5 + 1;
                this.detailRow = i5;
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                if (this.chats.size() <= 10) {
                    i = 8;
                }
                actionBarMenuItem.setVisibility(i);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        loadChats();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            int i3 = tLRPC$ChatFull.id;
            if (i3 == this.currentChatId) {
                this.info = tLRPC$ChatFull;
                loadChats();
                updateRows();
                return;
            }
            TLRPC$Chat tLRPC$Chat = this.waitingForFullChat;
            if (tLRPC$Chat != null && tLRPC$Chat.id == i3) {
                try {
                    this.waitingForFullChatProgressAlert.dismiss();
                } catch (Throwable unused) {
                }
                this.waitingForFullChatProgressAlert = null;
                showLinkAlert(this.waitingForFullChat, false);
                this.waitingForFullChat = null;
            }
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discussion", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatLinkActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
        addItem.setIsSearchField(true);
        addItem.setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ChatLinkActivity.this.searching = true;
                ChatLinkActivity.this.emptyView.setShowAtCenter(true);
            }

            public void onSearchCollapse() {
                ChatLinkActivity.this.searchAdapter.searchDialogs((String) null);
                boolean unused = ChatLinkActivity.this.searching = false;
                boolean unused2 = ChatLinkActivity.this.searchWas = false;
                ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.listViewAdapter);
                ChatLinkActivity.this.listViewAdapter.notifyDataSetChanged();
                ChatLinkActivity.this.listView.setFastScrollVisible(true);
                ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(false);
                ChatLinkActivity.this.emptyView.setShowAtCenter(false);
                ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
                ChatLinkActivity.this.fragmentView.setTag("windowBackgroundGray");
                ChatLinkActivity.this.emptyView.showProgress();
            }

            public void onTextChanged(EditText editText) {
                if (ChatLinkActivity.this.searchAdapter != null) {
                    String obj = editText.getText().toString();
                    if (obj.length() != 0) {
                        boolean unused = ChatLinkActivity.this.searchWas = true;
                        if (!(ChatLinkActivity.this.listView == null || ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter)) {
                            ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.searchAdapter);
                            ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                            ChatLinkActivity.this.fragmentView.setTag("windowBackgroundWhite");
                            ChatLinkActivity.this.searchAdapter.notifyDataSetChanged();
                            ChatLinkActivity.this.listView.setFastScrollVisible(false);
                            ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(true);
                            ChatLinkActivity.this.emptyView.showProgress();
                        }
                    }
                    ChatLinkActivity.this.searchAdapter.searchDialogs(obj);
                }
            }
        });
        this.searchItem = addItem;
        addItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchAdapter = new SearchAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
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
                ChatLinkActivity.this.lambda$createView$5$ChatLinkActivity(view, i);
            }
        });
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$ChatLinkActivity(View view, int i) {
        TLRPC$Chat tLRPC$Chat;
        String str;
        String str2;
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            SearchAdapter searchAdapter2 = this.searchAdapter;
            if (adapter == searchAdapter2) {
                tLRPC$Chat = searchAdapter2.getItem(i);
            } else {
                int i2 = this.chatStartRow;
                tLRPC$Chat = (i < i2 || i >= this.chatEndRow) ? null : this.chats.get(i - i2);
            }
            if (tLRPC$Chat != null) {
                if (!this.isChannel || this.info.linked_chat_id != 0) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", tLRPC$Chat.id);
                    presentFragment(new ChatActivity(bundle));
                    return;
                }
                showLinkAlert(tLRPC$Chat, true);
            } else if (i != this.createChatRow) {
            } else {
                if (this.isChannel && this.info.linked_chat_id == 0) {
                    Bundle bundle2 = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Integer.valueOf(getUserConfig().getClientUserId()));
                    bundle2.putIntegerArrayList("result", arrayList);
                    bundle2.putInt("chatType", 4);
                    GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle2);
                    groupCreateFinalActivity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                        public void didFailChatCreation() {
                        }

                        public void didStartChatCreation() {
                        }

                        public void didFinishChatCreation(GroupCreateFinalActivity groupCreateFinalActivity, int i) {
                            ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                            chatLinkActivity.linkChat(chatLinkActivity.getMessagesController().getChat(Integer.valueOf(i)), groupCreateFinalActivity);
                        }
                    });
                    presentFragment(groupCreateFinalActivity);
                } else if (!this.chats.isEmpty()) {
                    TLRPC$Chat tLRPC$Chat2 = this.chats.get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.isChannel) {
                        str2 = LocaleController.getString("DiscussionUnlinkGroup", NUM);
                        str = LocaleController.formatString("DiscussionUnlinkChannelAlert", NUM, tLRPC$Chat2.title);
                    } else {
                        str2 = LocaleController.getString("DiscussionUnlink", NUM);
                        str = LocaleController.formatString("DiscussionUnlinkGroupAlert", NUM, tLRPC$Chat2.title);
                    }
                    builder.setTitle(str2);
                    builder.setMessage(AndroidUtilities.replaceTags(str));
                    builder.setPositiveButton(LocaleController.getString("DiscussionUnlink", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatLinkActivity.this.lambda$null$4$ChatLinkActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$4$ChatLinkActivity(DialogInterface dialogInterface, int i) {
        if (!this.isChannel || this.info.linked_chat_id != 0) {
            AlertDialog[] alertDialogArr = {new AlertDialog(getParentActivity(), 3)};
            TLRPC$TL_channels_setDiscussionGroup tLRPC$TL_channels_setDiscussionGroup = new TLRPC$TL_channels_setDiscussionGroup();
            if (this.isChannel) {
                tLRPC$TL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
                tLRPC$TL_channels_setDiscussionGroup.group = new TLRPC$TL_inputChannelEmpty();
            } else {
                tLRPC$TL_channels_setDiscussionGroup.broadcast = new TLRPC$TL_inputChannelEmpty();
                tLRPC$TL_channels_setDiscussionGroup.group = MessagesController.getInputChannel(this.currentChat);
            }
            AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, getConnectionsManager().sendRequest(tLRPC$TL_channels_setDiscussionGroup, new RequestDelegate(alertDialogArr) {
                private final /* synthetic */ AlertDialog[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatLinkActivity.this.lambda$null$1$ChatLinkActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            })) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.this.lambda$null$3$ChatLinkActivity(this.f$1, this.f$2);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$null$1$ChatLinkActivity(AlertDialog[] alertDialogArr, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr) {
            private final /* synthetic */ AlertDialog[] f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$0$ChatLinkActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$ChatLinkActivity(AlertDialog[] alertDialogArr) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.info.linked_chat_id = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, null);
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
        if (!this.isChannel) {
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$3$ChatLinkActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$2$ChatLinkActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$2$ChatLinkActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    private void showLinkAlert(TLRPC$Chat tLRPC$Chat, boolean z) {
        String str;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        TLRPC$ChatFull chatFull = getMessagesController().getChatFull(tLRPC$Chat2.id);
        int i = 3;
        if (chatFull != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            TextView textView = new TextView(getParentActivity());
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 16.0f);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (TextUtils.isEmpty(tLRPC$Chat2.username)) {
                str = LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", NUM, tLRPC$Chat2.title, this.currentChat.title);
            } else if (TextUtils.isEmpty(this.currentChat.username)) {
                str = LocaleController.formatString("DiscussionLinkGroupPrivateAlert", NUM, tLRPC$Chat2.title, this.currentChat.title);
            } else {
                str = LocaleController.formatString("DiscussionLinkGroupPublicAlert", NUM, tLRPC$Chat2.title, this.currentChat.title);
            }
            if (chatFull.hidden_prehistory) {
                str = str + "\n\n" + LocaleController.getString("DiscussionLinkGroupAlertHistory", NUM);
            }
            textView.setText(AndroidUtilities.replaceTags(str));
            FrameLayout frameLayout = new FrameLayout(getParentActivity());
            builder.setView(frameLayout);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView backupImageView = new BackupImageView(getParentActivity());
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView2 = new TextView(getParentActivity());
            textView2.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView2.setTextSize(1, 20.0f);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setLines(1);
            textView2.setMaxLines(1);
            textView2.setSingleLine(true);
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setText(tLRPC$Chat2.title);
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 21;
            float f = (float) (LocaleController.isRTL ? 21 : 76);
            if (LocaleController.isRTL) {
                i3 = 76;
            }
            frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i2, f, 11.0f, (float) i3, 0.0f));
            if (LocaleController.isRTL) {
                i = 5;
            }
            frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            avatarDrawable.setInfo(tLRPC$Chat2);
            backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat2, false), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$Chat2);
            builder.setPositiveButton(LocaleController.getString("DiscussionLinkGroup", NUM), new DialogInterface.OnClickListener(chatFull, tLRPC$Chat2) {
                private final /* synthetic */ TLRPC$ChatFull f$1;
                private final /* synthetic */ TLRPC$Chat f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatLinkActivity.this.lambda$showLinkAlert$8$ChatLinkActivity(this.f$1, this.f$2, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (z) {
            getMessagesController().loadFullChat(tLRPC$Chat2.id, 0, true);
            this.waitingForFullChat = tLRPC$Chat2;
            this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ChatLinkActivity.this.lambda$showLinkAlert$7$ChatLinkActivity();
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$showLinkAlert$7$ChatLinkActivity() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$6$ChatLinkActivity(dialogInterface);
                }
            });
            showDialog(this.waitingForFullChatProgressAlert);
        }
    }

    public /* synthetic */ void lambda$null$6$ChatLinkActivity(DialogInterface dialogInterface) {
        this.waitingForFullChat = null;
    }

    public /* synthetic */ void lambda$showLinkAlert$8$ChatLinkActivity(TLRPC$ChatFull tLRPC$ChatFull, TLRPC$Chat tLRPC$Chat, DialogInterface dialogInterface, int i) {
        if (tLRPC$ChatFull.hidden_prehistory) {
            MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(tLRPC$Chat.id, false);
        }
        linkChat(tLRPC$Chat, (BaseFragment) null);
    }

    /* access modifiers changed from: private */
    public void linkChat(TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment) {
        AlertDialog alertDialog;
        if (tLRPC$Chat != null) {
            if (!ChatObject.isChannel(tLRPC$Chat)) {
                MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), tLRPC$Chat.id, this, new MessagesStorage.IntCallback(baseFragment) {
                    private final /* synthetic */ BaseFragment f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(int i) {
                        ChatLinkActivity.this.lambda$linkChat$9$ChatLinkActivity(this.f$1, i);
                    }
                });
                return;
            }
            AlertDialog[] alertDialogArr = new AlertDialog[1];
            if (baseFragment != null) {
                alertDialog = null;
            } else {
                alertDialog = new AlertDialog(getParentActivity(), 3);
            }
            alertDialogArr[0] = alertDialog;
            TLRPC$TL_channels_setDiscussionGroup tLRPC$TL_channels_setDiscussionGroup = new TLRPC$TL_channels_setDiscussionGroup();
            tLRPC$TL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
            tLRPC$TL_channels_setDiscussionGroup.group = MessagesController.getInputChannel(tLRPC$Chat);
            AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, getConnectionsManager().sendRequest(tLRPC$TL_channels_setDiscussionGroup, new RequestDelegate(alertDialogArr, tLRPC$Chat, baseFragment) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ TLRPC$Chat f$2;
                private final /* synthetic */ BaseFragment f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatLinkActivity.this.lambda$linkChat$11$ChatLinkActivity(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                }
            })) {
                private final /* synthetic */ AlertDialog[] f$1;
                private final /* synthetic */ int f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.this.lambda$linkChat$13$ChatLinkActivity(this.f$1, this.f$2);
                }
            }, 500);
        }
    }

    public /* synthetic */ void lambda$linkChat$9$ChatLinkActivity(BaseFragment baseFragment, int i) {
        if (i != 0) {
            MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(i, false);
            linkChat(getMessagesController().getChat(Integer.valueOf(i)), baseFragment);
        }
    }

    public /* synthetic */ void lambda$linkChat$11$ChatLinkActivity(AlertDialog[] alertDialogArr, TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, tLRPC$Chat, baseFragment) {
            private final /* synthetic */ AlertDialog[] f$1;
            private final /* synthetic */ TLRPC$Chat f$2;
            private final /* synthetic */ BaseFragment f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$10$ChatLinkActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$10$ChatLinkActivity(AlertDialog[] alertDialogArr, TLRPC$Chat tLRPC$Chat, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            try {
                alertDialogArr[0].dismiss();
            } catch (Throwable unused) {
            }
            alertDialogArr[0] = null;
        }
        this.info.linked_chat_id = tLRPC$Chat.id;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, null);
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
        if (baseFragment != null) {
            removeSelfFromStack();
            baseFragment.finishFragment();
            return;
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$linkChat$13$ChatLinkActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    ChatLinkActivity.this.lambda$null$12$ChatLinkActivity(this.f$1, dialogInterface);
                }
            });
            showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$12$ChatLinkActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.info.linked_chat_id));
            if (chat != null) {
                this.chats.add(chat);
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
        }
        if (!this.loadingChats && this.isChannel && this.info.linked_chat_id == 0) {
            this.loadingChats = true;
            getConnectionsManager().sendRequest(new TLRPC$TL_channels_getGroupsForDiscussion(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    ChatLinkActivity.this.lambda$loadChats$15$ChatLinkActivity(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$loadChats$15$ChatLinkActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ChatLinkActivity.this.lambda$null$14$ChatLinkActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$14$ChatLinkActivity(TLObject tLObject) {
        if (tLObject instanceof TLRPC$messages_Chats) {
            TLRPC$messages_Chats tLRPC$messages_Chats = (TLRPC$messages_Chats) tLObject;
            getMessagesController().putChats(tLRPC$messages_Chats.chats, false);
            this.chats = tLRPC$messages_Chats.chats;
        }
        this.loadingChats = false;
        updateRows();
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(ChatLinkActivity chatLinkActivity, Context context) {
            super(context);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 20.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (!chatLinkActivity.isChannel) {
                TLRPC$Chat chat = chatLinkActivity.getMessagesController().getChat(Integer.valueOf(chatLinkActivity.info.linked_chat_id));
                if (chat != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionGroupHelp", NUM, chat.title)));
                }
            } else if (chatLinkActivity.info == null || chatLinkActivity.info.linked_chat_id == 0) {
                this.messageTextView.setText(LocaleController.getString("DiscussionChannelHelp", NUM));
            } else {
                TLRPC$Chat chat2 = chatLinkActivity.getMessagesController().getChat(Integer.valueOf(chatLinkActivity.info.linked_chat_id));
                if (chat2 != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionChannelGroupSetHelp", NUM, chat2.title)));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 124.0f, 52.0f, 27.0f));
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<TLRPC$Chat> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public int getItemViewType(int i) {
            return 0;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(String str) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            $$Lambda$ChatLinkActivity$SearchAdapter$TUvgAYvpvIggtszTT20rbb2gK0U r1 = new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$searchDialogs$0$ChatLinkActivity$SearchAdapter(this.f$1);
                }
            };
            this.searchRunnable = r1;
            dispatchQueue.postRunnable(r1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void lambda$searchDialogs$0$ChatLinkActivity$SearchAdapter(String str) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$processSearch$2$ChatLinkActivity$SearchAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$processSearch$2$ChatLinkActivity$SearchAdapter(String str) {
            this.searchRunnable = null;
            Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(ChatLinkActivity.this.chats)) {
                private final /* synthetic */ String f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$null$1$ChatLinkActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00af, code lost:
            if (r12.contains(" " + r15) != false) goto L_0x00be;
         */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x00fd A[LOOP:1: B:23:0x0073->B:44:0x00fd, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x00c1 A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String r18, java.util.ArrayList r19) {
            /*
                r17 = this;
                r0 = r17
                java.lang.String r1 = r18.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r0.updateSearchResults(r1, r2)
                return
            L_0x001e:
                org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r2 = r2.getTranslitString(r1)
                boolean r3 = r1.equals(r2)
                if (r3 != 0) goto L_0x0032
                int r3 = r2.length()
                if (r3 != 0) goto L_0x0033
            L_0x0032:
                r2 = 0
            L_0x0033:
                r3 = 0
                r5 = 1
                if (r2 == 0) goto L_0x0039
                r6 = 1
                goto L_0x003a
            L_0x0039:
                r6 = 0
            L_0x003a:
                int r6 = r6 + r5
                java.lang.String[] r7 = new java.lang.String[r6]
                r7[r3] = r1
                if (r2 == 0) goto L_0x0043
                r7[r5] = r2
            L_0x0043:
                java.util.ArrayList r1 = new java.util.ArrayList
                r1.<init>()
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                r8 = 0
            L_0x004e:
                int r9 = r19.size()
                if (r8 >= r9) goto L_0x0109
                r9 = r19
                java.lang.Object r10 = r9.get(r8)
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC$Chat) r10
                java.lang.String r11 = r10.title
                java.lang.String r11 = r11.toLowerCase()
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r11)
                boolean r13 = r11.equals(r12)
                if (r13 == 0) goto L_0x0071
                r12 = 0
            L_0x0071:
                r13 = 0
                r14 = 0
            L_0x0073:
                if (r13 >= r6) goto L_0x0103
                r15 = r7[r13]
                boolean r16 = r11.startsWith(r15)
                if (r16 != 0) goto L_0x00be
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = " "
                r3.append(r4)
                r3.append(r15)
                java.lang.String r3 = r3.toString()
                boolean r3 = r11.contains(r3)
                if (r3 != 0) goto L_0x00be
                if (r12 == 0) goto L_0x00b2
                boolean r3 = r12.startsWith(r15)
                if (r3 != 0) goto L_0x00be
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r4)
                r3.append(r15)
                java.lang.String r3 = r3.toString()
                boolean r3 = r12.contains(r3)
                if (r3 == 0) goto L_0x00b2
                goto L_0x00be
            L_0x00b2:
                java.lang.String r3 = r10.username
                if (r3 == 0) goto L_0x00bf
                boolean r3 = r3.startsWith(r15)
                if (r3 == 0) goto L_0x00bf
                r14 = 2
                goto L_0x00bf
            L_0x00be:
                r14 = 1
            L_0x00bf:
                if (r14 == 0) goto L_0x00fd
                if (r14 != r5) goto L_0x00cf
                java.lang.String r3 = r10.title
                r4 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r4, r15)
                r2.add(r3)
                r15 = 0
                goto L_0x00f9
            L_0x00cf:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "@"
                r3.append(r4)
                java.lang.String r11 = r10.username
                r3.append(r11)
                java.lang.String r3 = r3.toString()
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r4)
                r11.append(r15)
                java.lang.String r4 = r11.toString()
                r15 = 0
                java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r15, r4)
                r2.add(r3)
            L_0x00f9:
                r1.add(r10)
                goto L_0x0104
            L_0x00fd:
                r15 = 0
                int r13 = r13 + 1
                r3 = 0
                goto L_0x0073
            L_0x0103:
                r15 = 0
            L_0x0104:
                int r8 = r8 + 1
                r3 = 0
                goto L_0x004e
            L_0x0109:
                r0.updateSearchResults(r1, r2)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatLinkActivity.SearchAdapter.lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLRPC$Chat> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable(arrayList, arrayList2) {
                private final /* synthetic */ ArrayList f$1;
                private final /* synthetic */ ArrayList f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.lambda$updateSearchResults$3$ChatLinkActivity$SearchAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$updateSearchResults$3$ChatLinkActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
            if (ChatLinkActivity.this.searching) {
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                    ChatLinkActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public TLRPC$Chat getItem(int i) {
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
            manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            return new RecyclerListView.Holder(manageChatUserCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$Chat tLRPC$Chat = this.searchResult.get(i);
            String str = tLRPC$Chat.username;
            CharSequence charSequence = this.searchResultNames.get(i);
            CharSequence charSequence2 = null;
            if (charSequence != null && !TextUtils.isEmpty(str)) {
                if (charSequence.toString().startsWith("@" + str)) {
                    charSequence2 = charSequence;
                    charSequence = null;
                }
            }
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
            manageChatUserCell.setTag(Integer.valueOf(i));
            manageChatUserCell.setData(tLRPC$Chat, charSequence, charSequence2, false);
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public int getItemCount() {
            if (ChatLinkActivity.this.loadingChats) {
                return 0;
            }
            return ChatLinkActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i != 2) {
                    view = new HintInnerCell(ChatLinkActivity.this, this.mContext);
                } else {
                    view2 = new ManageChatTextCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                }
                return new RecyclerListView.Holder(view);
            }
            view2 = new ManageChatUserCell(this.mContext, 6, 2, false);
            view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            view = view2;
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) ChatLinkActivity.this.chats.get(i - ChatLinkActivity.this.chatStartRow);
                if (TextUtils.isEmpty(tLRPC$Chat.username)) {
                    str = null;
                } else {
                    str = "@" + tLRPC$Chat.username;
                }
                if (!(i == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0)) {
                    z = true;
                }
                manageChatUserCell.setData(tLRPC$Chat, (CharSequence) null, str, z);
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i != ChatLinkActivity.this.detailRow) {
                    return;
                }
                if (ChatLinkActivity.this.isChannel) {
                    textInfoPrivacyCell.setText(LocaleController.getString("DiscussionChannelHelp2", NUM));
                } else {
                    textInfoPrivacyCell.setText(LocaleController.getString("DiscussionGroupHelp2", NUM));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                if (!ChatLinkActivity.this.isChannel) {
                    manageChatTextCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                    manageChatTextCell.setText(LocaleController.getString("DiscussionUnlinkChannel", NUM), (String) null, NUM, false);
                } else if (ChatLinkActivity.this.info.linked_chat_id != 0) {
                    manageChatTextCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                    manageChatTextCell.setText(LocaleController.getString("DiscussionUnlinkGroup", NUM), (String) null, NUM, false);
                } else {
                    manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    manageChatTextCell.setText(LocaleController.getString("DiscussionCreateGroup", NUM), (String) null, NUM, true);
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == ChatLinkActivity.this.helpRow) {
                return 3;
            }
            if (i == ChatLinkActivity.this.createChatRow || i == ChatLinkActivity.this.removeChatRow) {
                return 2;
            }
            return (i < ChatLinkActivity.this.chatStartRow || i >= ChatLinkActivity.this.chatEndRow) ? 1 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ChatLinkActivity.this.lambda$getThemeDescriptions$16$ChatLinkActivity();
            }
        };
        $$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU r8 = r10;
        $$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU r7 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink"), new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$16$ChatLinkActivity() {
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
