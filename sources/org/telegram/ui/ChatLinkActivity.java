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
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingStickerDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateFinalActivity;

public class ChatLinkActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    /* access modifiers changed from: private */
    public int chatEndRow;
    /* access modifiers changed from: private */
    public int chatStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    /* access modifiers changed from: private */
    public boolean chatsLoaded;
    /* access modifiers changed from: private */
    public int createChatRow;
    private TLRPC.Chat currentChat;
    private long currentChatId;
    /* access modifiers changed from: private */
    public int detailRow;
    /* access modifiers changed from: private */
    public EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public TLRPC.ChatFull info;
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
    private boolean waitingForChatCreate;
    private TLRPC.Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;

    private static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final String stickerSetName = "tg_placeholders_android";
        private int currentAccount = UserConfig.selectedAccount;
        private LoadingStickerDrawable drawable;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            this.stickerView = new BackupImageView(context);
            LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "M476.1,397.4CLASSNAME.8-47.2,0.3-105.9-50.9-120c-2.5-6.9-7.8-12.7-15-16.4l0.4-229.4c0-12.3-10-22.4-22.4-22.4H128.5c-12.3,0-22.4,10-22.4,22.4l-0.4,229.8v0c0,6.7,2.9,12.6,7.6,16.7c-51.6,15.9-79.2,77.2-48.1,116.4c-8.7,11.7-13.4,27.5-14,47.2c-1.7,34.5,21.6,45.8,55.9,45.8CLASSNAME.3,0,99.1,4.6,105.1-36.2CLASSNAME.5,0.9,7.1-37.3-6.5-53.3CLASSNAME.4-22.4,18.3-52.9,4.9-78.2c-0.7-5.3-3.8-9.8-8.1-12.6c-1.5-2-1.6-2-2.1-2.7c0.2-1,1.2-11.8-3.4-20.9h138.5c-4.8,8.8-4.7,17-2.9,22.1c-5.3,4.8-6.8,12.3-5.2,17c-11.4,24.9-10,53.8,4.3,77.5c-6.8,9.7-11.2,21.7-12.6,31.6c-0.2-0.2-0.4-0.3-0.6-0.5c0.8-3.3,0.4-6.4-1.3-7.8c9.3-12.1-4.5-29.2-17-21.7c-3.8-2.8-10.6-3.2-18.1-0.5c-2.4-10.6-21.1-10.6-28.6-1c-1.3,0.3-2.9,0.8-4.5,1.9c-5.2-0.9-10.9,0.1-14.1,4.4c-6.9,3-9.5,10.4-7.8,17c-0.9,1.8-1.1,4-0.8,6.3c-1.6,1.2-2.3,3.1-2,4.9c0.1,0.6,10.4,56.6,11.2,62c0.3,1.8,1.5,3.2,3.1,3.9c8.7,3.4,12,3.8,30.1,9.4c2.7,0.8,2.4,0.8,6.7-0.1CLASSNAME.4-3.5,30.2-8.9,30.8-9.2c1.6-0.6,2.7-2,3.1-3.7c0.1-0.4,6.8-36.5,10-53.2c0.9,4.2,3.3,7.3,7.4,7.5c1.2,7.8,4.4,14.5,9.5,19.9CLASSNAME.4,17.3,44.9,15.7,64.9,16.1CLASSNAME.3,0.8,74.5,1.5,84.4-24.4CLASSNAME.9,453.5,491.3,421.3,476.1,397.4z", AndroidUtilities.dp(104.0f), AndroidUtilities.dp(104.0f));
            this.drawable = loadingStickerDrawable;
            this.stickerView.setImageDrawable(loadingStickerDrawable);
            addView(this.stickerView, LayoutHelper.createLinear(104, 104, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC.messages_StickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (set == null) {
                set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            if (set == null || set.documents.size() < 3) {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, set == null);
                this.stickerView.setImageDrawable(this.drawable);
                return;
            }
            this.stickerView.setImage(ImageLocation.getForDocument(set.documents.get(2)), "104_104", "tgs", (Drawable) this.drawable, (Object) set);
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad && "tg_placeholders_android".equals(args[0])) {
                setSticker();
            }
        }
    }

    public ChatLinkActivity(long chatId) {
        this.currentChatId = chatId;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(chatId));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
    }

    private void updateRows() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.currentChatId));
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
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.createChatRow = i3;
                }
                int i4 = this.rowCount;
                this.chatStartRow = i4;
                int size = i4 + this.chats.size();
                this.rowCount = size;
                this.chatEndRow = size;
                if (this.info.linked_chat_id != 0) {
                    int i5 = this.rowCount;
                    this.rowCount = i5 + 1;
                    this.createChatRow = i5;
                }
            } else {
                this.chatStartRow = i2;
                int size2 = i2 + this.chats.size();
                this.rowCount = size2;
                this.chatEndRow = size2;
                this.rowCount = size2 + 1;
                this.createChatRow = size2;
            }
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.detailRow = i6;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = args[0];
            if (chatFull.id == this.currentChatId) {
                this.info = chatFull;
                loadChats();
                updateRows();
                return;
            }
            TLRPC.Chat chat = this.waitingForFullChat;
            if (chat != null && chat.id == chatFull.id) {
                try {
                    this.waitingForFullChatProgressAlert.dismiss();
                } catch (Throwable th) {
                }
                this.waitingForFullChatProgressAlert = null;
                showLinkAlert(this.waitingForFullChat, false);
                this.waitingForFullChat = null;
            }
        }
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discussion", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatLinkActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
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
                    ChatLinkActivity.this.searchAdapter.searchDialogs(text);
                }
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
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
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ChatLinkActivity$$ExternalSyntheticLambda9(this));
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1946lambda$createView$6$orgtelegramuiChatLinkActivity(View view, int position) {
        TLRPC.Chat chat;
        String title;
        String message;
        if (getParentActivity() != null) {
            RecyclerView.Adapter adapter = this.listView.getAdapter();
            SearchAdapter searchAdapter2 = this.searchAdapter;
            if (adapter == searchAdapter2) {
                chat = searchAdapter2.getItem(position);
            } else {
                int i = this.chatStartRow;
                if (position < i || position >= this.chatEndRow) {
                    chat = null;
                } else {
                    chat = this.chats.get(position - i);
                }
            }
            if (chat != null) {
                if (!this.isChannel || this.info.linked_chat_id != 0) {
                    Bundle args = new Bundle();
                    args.putLong("chat_id", chat.id);
                    presentFragment(new ChatActivity(args));
                    return;
                }
                showLinkAlert(chat, true);
            } else if (position != this.createChatRow) {
            } else {
                if (this.isChannel && this.info.linked_chat_id == 0) {
                    Bundle args2 = new Bundle();
                    args2.putLongArray("result", new long[]{getUserConfig().getClientUserId()});
                    args2.putInt("chatType", 4);
                    GroupCreateFinalActivity activity = new GroupCreateFinalActivity(args2);
                    activity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() {
                        public void didStartChatCreation() {
                        }

                        public void didFinishChatCreation(GroupCreateFinalActivity fragment, long chatId) {
                            ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                            chatLinkActivity.linkChat(chatLinkActivity.getMessagesController().getChat(Long.valueOf(chatId)), fragment);
                        }

                        public void didFailChatCreation() {
                        }
                    });
                    presentFragment(activity);
                } else if (!this.chats.isEmpty()) {
                    TLRPC.Chat c = this.chats.get(0);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (this.isChannel) {
                        title = LocaleController.getString("DiscussionUnlinkGroup", NUM);
                        message = LocaleController.formatString("DiscussionUnlinkChannelAlert", NUM, c.title);
                    } else {
                        title = LocaleController.getString("DiscussionUnlink", NUM);
                        message = LocaleController.formatString("DiscussionUnlinkGroupAlert", NUM, c.title);
                    }
                    builder.setTitle(title);
                    builder.setMessage(AndroidUtilities.replaceTags(message));
                    builder.setPositiveButton(LocaleController.getString("DiscussionUnlink", NUM), new ChatLinkActivity$$ExternalSyntheticLambda12(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog dialog = builder.create();
                    showDialog(dialog);
                    TextView button = (TextView) dialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1945lambda$createView$5$orgtelegramuiChatLinkActivity(DialogInterface dialogInterface, int i) {
        if (!this.isChannel || this.info.linked_chat_id != 0) {
            AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
            TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
            if (this.isChannel) {
                req.broadcast = MessagesController.getInputChannel(this.currentChat);
                req.group = new TLRPC.TL_inputChannelEmpty();
            } else {
                req.broadcast = new TLRPC.TL_inputChannelEmpty();
                req.group = MessagesController.getInputChannel(this.currentChat);
            }
            AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda1(this, progressDialog, getConnectionsManager().sendRequest(req, new ChatLinkActivity$$ExternalSyntheticLambda6(this, progressDialog))), 500);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1942lambda$createView$2$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda18(this, progressDialog));
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1941lambda$createView$1$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.info.linked_chat_id = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, false);
        AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda14(this), 1000);
        if (!this.isChannel) {
            finishFragment();
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1940lambda$createView$0$orgtelegramuiChatLinkActivity() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1944lambda$createView$4$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new ChatLinkActivity$$ExternalSyntheticLambda10(this, requestId));
            showDialog(progressDialog[0]);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1943lambda$createView$3$orgtelegramuiChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    private void showLinkAlert(TLRPC.Chat chat, boolean query) {
        String message;
        TLRPC.Chat chat2 = chat;
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(chat2.id);
        int i = 3;
        if (chatFull != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            TextView messageTextView = new TextView(getParentActivity());
            messageTextView.setTextColor(Theme.getColor("dialogTextBlack"));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (TextUtils.isEmpty(chat2.username)) {
                message = LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", NUM, chat2.title, this.currentChat.title);
            } else if (TextUtils.isEmpty(this.currentChat.username)) {
                message = LocaleController.formatString("DiscussionLinkGroupPrivateAlert", NUM, chat2.title, this.currentChat.title);
            } else {
                message = LocaleController.formatString("DiscussionLinkGroupPublicAlert", NUM, chat2.title, this.currentChat.title);
            }
            if (chatFull.hidden_prehistory) {
                message = message + "\n\n" + LocaleController.getString("DiscussionLinkGroupAlertHistory", NUM);
            }
            messageTextView.setText(AndroidUtilities.replaceTags(message));
            FrameLayout frameLayout2 = new FrameLayout(getParentActivity());
            builder.setView(frameLayout2);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(getParentActivity());
            imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout2.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            TextView textView = new TextView(getParentActivity());
            textView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setText(chat2.title);
            int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
            int i3 = 21;
            float f = (float) (LocaleController.isRTL ? 21 : 76);
            if (LocaleController.isRTL) {
                i3 = 76;
            }
            frameLayout2.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i2, f, 11.0f, (float) i3, 0.0f));
            if (LocaleController.isRTL) {
                i = 5;
            }
            frameLayout2.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            avatarDrawable.setInfo(chat2);
            imageView.setForUserOrChat(chat2, avatarDrawable);
            builder.setPositiveButton(LocaleController.getString("DiscussionLinkGroup", NUM), new ChatLinkActivity$$ExternalSyntheticLambda13(this, chatFull, chat2));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        } else if (query) {
            getMessagesController().loadFullChat(chat2.id, 0, true);
            this.waitingForFullChat = chat2;
            this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
            AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda16(this), 500);
        }
    }

    /* renamed from: lambda$showLinkAlert$8$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1957lambda$showLinkAlert$8$orgtelegramuiChatLinkActivity() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new ChatLinkActivity$$ExternalSyntheticLambda0(this));
            showDialog(this.waitingForFullChatProgressAlert);
        }
    }

    /* renamed from: lambda$showLinkAlert$7$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1956lambda$showLinkAlert$7$orgtelegramuiChatLinkActivity(DialogInterface dialog) {
        this.waitingForFullChat = null;
    }

    /* renamed from: lambda$showLinkAlert$9$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1958lambda$showLinkAlert$9$orgtelegramuiChatLinkActivity(TLRPC.ChatFull chatFull, TLRPC.Chat chat, DialogInterface dialogInterface, int i) {
        if (chatFull.hidden_prehistory) {
            getMessagesController().toogleChannelInvitesHistory(chat.id, false);
        }
        linkChat(chat, (BaseFragment) null);
    }

    /* access modifiers changed from: private */
    public void linkChat(TLRPC.Chat chat, BaseFragment createFragment) {
        if (chat != null) {
            if (!ChatObject.isChannel(chat)) {
                getMessagesController().convertToMegaGroup(getParentActivity(), chat.id, this, new ChatLinkActivity$$ExternalSyntheticLambda4(this, createFragment));
                return;
            }
            AlertDialog[] progressDialog = new AlertDialog[1];
            progressDialog[0] = createFragment != null ? null : new AlertDialog(getParentActivity(), 3);
            TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
            req.broadcast = MessagesController.getInputChannel(this.currentChat);
            req.group = MessagesController.getInputChannel(chat);
            AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda2(this, progressDialog, getConnectionsManager().sendRequest(req, new ChatLinkActivity$$ExternalSyntheticLambda7(this, progressDialog, chat, createFragment), 64)), 500);
        }
    }

    /* renamed from: lambda$linkChat$10$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1948lambda$linkChat$10$orgtelegramuiChatLinkActivity(BaseFragment createFragment, long param) {
        if (param != 0) {
            getMessagesController().toogleChannelInvitesHistory(param, false);
            linkChat(getMessagesController().getChat(Long.valueOf(param)), createFragment);
        }
    }

    /* renamed from: lambda$linkChat$13$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1951lambda$linkChat$13$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, TLRPC.Chat chat, BaseFragment createFragment, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda3(this, progressDialog, chat, createFragment));
    }

    /* renamed from: lambda$linkChat$12$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1950lambda$linkChat$12$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, TLRPC.Chat chat, BaseFragment createFragment) {
        if (progressDialog[0] != null) {
            try {
                progressDialog[0].dismiss();
            } catch (Throwable th) {
            }
            progressDialog[0] = null;
        }
        this.info.linked_chat_id = chat.id;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, false);
        AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda15(this), 1000);
        if (createFragment != null) {
            removeSelfFromStack();
            createFragment.finishFragment();
            return;
        }
        finishFragment();
    }

    /* renamed from: lambda$linkChat$11$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1949lambda$linkChat$11$orgtelegramuiChatLinkActivity() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    /* renamed from: lambda$linkChat$15$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1953lambda$linkChat$15$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new ChatLinkActivity$$ExternalSyntheticLambda11(this, requestId));
            showDialog(progressDialog[0]);
        }
    }

    /* renamed from: lambda$linkChat$14$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1952lambda$linkChat$14$orgtelegramuiChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
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
            getConnectionsManager().sendRequest(new TLRPC.TL_channels_getGroupsForDiscussion(), new ChatLinkActivity$$ExternalSyntheticLambda5(this));
        }
    }

    /* renamed from: lambda$loadChats$17$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1955lambda$loadChats$17$orgtelegramuiChatLinkActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChatLinkActivity$$ExternalSyntheticLambda17(this, response));
    }

    /* renamed from: lambda$loadChats$16$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1954lambda$loadChats$16$orgtelegramuiChatLinkActivity(TLObject response) {
        if (response instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
            getMessagesController().putChats(res.chats, false);
            this.chats = res.chats;
        }
        this.loadingChats = false;
        this.chatsLoaded = true;
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
        private EmptyView emptyView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            EmptyView emptyView2 = new EmptyView(context);
            this.emptyView = emptyView2;
            addView(emptyView2, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (!ChatLinkActivity.this.isChannel) {
                TLRPC.Chat chat = ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionGroupHelp", NUM, chat.title)));
                }
            } else if (ChatLinkActivity.this.info == null || ChatLinkActivity.this.info.linked_chat_id == 0) {
                this.messageTextView.setText(LocaleController.getString("DiscussionChannelHelp3", NUM));
            } else {
                TLRPC.Chat chat2 = ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(ChatLinkActivity.this.info.linked_chat_id));
                if (chat2 != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionChannelGroupSetHelp2", NUM, chat2.title)));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 143.0f, 52.0f, 18.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
        }
    }

    private class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<TLRPC.Chat> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1 chatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1 = new ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1(this, query);
            this.searchRunnable = chatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1;
            dispatchQueue.postRunnable(chatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1, 300);
        }

        /* access modifiers changed from: private */
        /* renamed from: processSearch */
        public void m1961x7729b16c(String query) {
            AndroidUtilities.runOnUIThread(new ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda0(this, query));
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-ChatLinkActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1960xd276var_(String query) {
            this.searchRunnable = null;
            Utilities.searchQueue.postRunnable(new ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda2(this, query, new ArrayList<>(ChatLinkActivity.this.chats)));
        }

        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b2, code lost:
            if (r12.contains(" " + r3) != false) goto L_0x00c6;
         */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x0105 A[LOOP:1: B:23:0x0074->B:46:0x0105, LOOP_END] */
        /* JADX WARNING: Removed duplicated region for block: B:54:0x00ca A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-ChatLinkActivity$SearchAdapter  reason: not valid java name */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void m1959x98ac4e37(java.lang.String r19, java.util.ArrayList r20) {
            /*
                r18 = this;
                r0 = r18
                java.lang.String r1 = r19.trim()
                java.lang.String r1 = r1.toLowerCase()
                int r2 = r1.length()
                if (r2 != 0) goto L_0x001e
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                r0.updateSearchResults(r2, r3)
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
                r4 = 1
                if (r2 == 0) goto L_0x0039
                r5 = 1
                goto L_0x003a
            L_0x0039:
                r5 = 0
            L_0x003a:
                int r5 = r5 + r4
                java.lang.String[] r5 = new java.lang.String[r5]
                r5[r3] = r1
                if (r2 == 0) goto L_0x0043
                r5[r4] = r2
            L_0x0043:
                java.util.ArrayList r6 = new java.util.ArrayList
                r6.<init>()
                java.util.ArrayList r7 = new java.util.ArrayList
                r7.<init>()
                r8 = 0
            L_0x004e:
                int r9 = r20.size()
                if (r8 >= r9) goto L_0x0117
                r9 = r20
                java.lang.Object r10 = r9.get(r8)
                org.telegram.tgnet.TLRPC$Chat r10 = (org.telegram.tgnet.TLRPC.Chat) r10
                java.lang.String r11 = r10.title
                java.lang.String r11 = r11.toLowerCase()
                org.telegram.messenger.LocaleController r12 = org.telegram.messenger.LocaleController.getInstance()
                java.lang.String r12 = r12.getTranslitString(r11)
                boolean r13 = r11.equals(r12)
                if (r13 == 0) goto L_0x0071
                r12 = 0
            L_0x0071:
                r13 = 0
                int r14 = r5.length
                r15 = 0
            L_0x0074:
                if (r15 >= r14) goto L_0x010d
                r3 = r5[r15]
                boolean r16 = r11.startsWith(r3)
                if (r16 != 0) goto L_0x00c4
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r17 = r1
                java.lang.String r1 = " "
                r4.append(r1)
                r4.append(r3)
                java.lang.String r4 = r4.toString()
                boolean r4 = r11.contains(r4)
                if (r4 != 0) goto L_0x00c6
                if (r12 == 0) goto L_0x00b5
                boolean r4 = r12.startsWith(r3)
                if (r4 != 0) goto L_0x00c6
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r1)
                r4.append(r3)
                java.lang.String r1 = r4.toString()
                boolean r1 = r12.contains(r1)
                if (r1 == 0) goto L_0x00b5
                goto L_0x00c6
            L_0x00b5:
                java.lang.String r1 = r10.username
                if (r1 == 0) goto L_0x00c8
                java.lang.String r1 = r10.username
                boolean r1 = r1.startsWith(r3)
                if (r1 == 0) goto L_0x00c8
                r1 = 2
                r13 = r1
                goto L_0x00c8
            L_0x00c4:
                r17 = r1
            L_0x00c6:
                r1 = 1
                r13 = r1
            L_0x00c8:
                if (r13 == 0) goto L_0x0105
                r1 = 0
                r4 = 1
                if (r13 != r4) goto L_0x00d8
                java.lang.String r14 = r10.title
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r14, r1, r3)
                r7.add(r1)
                goto L_0x0101
            L_0x00d8:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                java.lang.String r15 = "@"
                r14.append(r15)
                java.lang.String r4 = r10.username
                r14.append(r4)
                java.lang.String r4 = r14.toString()
                java.lang.StringBuilder r14 = new java.lang.StringBuilder
                r14.<init>()
                r14.append(r15)
                r14.append(r3)
                java.lang.String r14 = r14.toString()
                java.lang.CharSequence r1 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r1, r14)
                r7.add(r1)
            L_0x0101:
                r6.add(r10)
                goto L_0x010f
            L_0x0105:
                int r15 = r15 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x0074
            L_0x010d:
                r17 = r1
            L_0x010f:
                int r8 = r8 + 1
                r1 = r17
                r3 = 0
                r4 = 1
                goto L_0x004e
            L_0x0117:
                r0.updateSearchResults(r6, r7)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatLinkActivity.SearchAdapter.m1959x98ac4e37(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<TLRPC.Chat> chats, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda3(this, chats, names));
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-ChatLinkActivity$SearchAdapter  reason: not valid java name */
        public /* synthetic */ void m1962x87a2aCLASSNAME(ArrayList chats, ArrayList names) {
            if (ChatLinkActivity.this.searching) {
                this.searchResult = chats;
                this.searchResultNames = names;
                if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                    ChatLinkActivity.this.emptyView.showTextView();
                }
                notifyDataSetChanged();
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public TLRPC.Chat getItem(int i) {
            return this.searchResult.get(i);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ManageChatUserCell(this.mContext, 6, 2, false);
            view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Chat chat = this.searchResult.get(position);
            String un = chat.username;
            CharSequence username = null;
            CharSequence name = this.searchResultNames.get(position);
            if (name != null && !TextUtils.isEmpty(un)) {
                String charSequence = name.toString();
                if (charSequence.startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            }
            ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(chat, name, username, false);
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        public int getItemCount() {
            if (!ChatLinkActivity.this.loadingChats || ChatLinkActivity.this.chatsLoaded) {
                return ChatLinkActivity.this.rowCount;
            }
            return 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ManageChatUserCell(this.mContext, 6, 2, false);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 2:
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new HintInnerCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    TLRPC.Chat chat = (TLRPC.Chat) ChatLinkActivity.this.chats.get(position - ChatLinkActivity.this.chatStartRow);
                    if (TextUtils.isEmpty(chat.username)) {
                        str = null;
                    } else {
                        str = "@" + chat.username;
                    }
                    if (position == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0) {
                        z = false;
                    }
                    userCell.setData(chat, (CharSequence) null, str, z);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != ChatLinkActivity.this.detailRow) {
                        return;
                    }
                    if (ChatLinkActivity.this.isChannel) {
                        privacyCell.setText(LocaleController.getString("DiscussionChannelHelp2", NUM));
                        return;
                    } else {
                        privacyCell.setText(LocaleController.getString("DiscussionGroupHelp2", NUM));
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    if (!ChatLinkActivity.this.isChannel) {
                        actionCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                        actionCell.setText(LocaleController.getString("DiscussionUnlinkChannel", NUM), (String) null, NUM, false);
                        return;
                    } else if (ChatLinkActivity.this.info.linked_chat_id != 0) {
                        actionCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                        actionCell.setText(LocaleController.getString("DiscussionUnlinkGroup", NUM), (String) null, NUM, false);
                        return;
                    } else {
                        actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                        actionCell.setText(LocaleController.getString("DiscussionCreateGroup", NUM), (String) null, NUM, true);
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == ChatLinkActivity.this.helpRow) {
                return 3;
            }
            if (position == ChatLinkActivity.this.createChatRow || position == ChatLinkActivity.this.removeChatRow) {
                return 2;
            }
            if (position < ChatLinkActivity.this.chatStartRow || position >= ChatLinkActivity.this.chatEndRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ChatLinkActivity$$ExternalSyntheticLambda8(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$18$org-telegram-ui-ChatLinkActivity  reason: not valid java name */
    public /* synthetic */ void m1947lambda$getThemeDescriptions$18$orgtelegramuiChatLinkActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
