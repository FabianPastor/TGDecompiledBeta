package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_channels_getGroupsForDiscussion;
import org.telegram.tgnet.TLRPC.TL_channels_setDiscussionGroup;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.messages_Chats;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate;

public class ChatLinkActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private int chatEndRow;
    private int chatStartRow;
    private ArrayList<Chat> chats = new ArrayList();
    private int createChatRow;
    private Chat currentChat;
    private int currentChatId;
    private int detailRow;
    private EmptyTextProgressView emptyView;
    private int helpRow;
    private ChatFull info;
    private boolean isChannel;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingChats;
    private int removeChatRow;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private boolean waitingForChatCreate;
    private Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setImageResource(Theme.getCurrentTheme().isDark() ? NUM : NUM);
            addView(this.imageView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 20.0f, 8.0f, 0.0f));
            this.messageTextView = new TextView(context);
            this.messageTextView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (!ChatLinkActivity.this.isChannel) {
                if (ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id)) != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionGroupHelp", NUM, ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id)).title)));
                }
            } else if (ChatLinkActivity.this.info == null || ChatLinkActivity.this.info.linked_chat_id == 0) {
                this.messageTextView.setText(LocaleController.getString("DiscussionChannelHelp", NUM));
            } else {
                if (ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id)) != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionChannelGroupSetHelp", NUM, ChatLinkActivity.this.getMessagesController().getChat(Integer.valueOf(ChatLinkActivity.this.info.linked_chat_id)).title)));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 124.0f, 52.0f, 27.0f));
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public int getItemCount() {
            if (ChatLinkActivity.this.loadingChats) {
                return 0;
            }
            return ChatLinkActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textInfoPrivacyCell;
            View manageChatTextCell;
            String str = "windowBackgroundWhite";
            if (i != 0) {
                if (i == 1) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i != 2) {
                    textInfoPrivacyCell = new HintInnerCell(this.mContext);
                } else {
                    manageChatTextCell = new ManageChatTextCell(this.mContext);
                    manageChatTextCell.setBackgroundColor(Theme.getColor(str));
                }
                return new Holder(textInfoPrivacyCell);
            }
            manageChatTextCell = new ManageChatUserCell(this.mContext, 6, 2, false);
            manageChatTextCell.setBackgroundColor(Theme.getColor(str));
            textInfoPrivacyCell = manageChatTextCell;
            return new Holder(textInfoPrivacyCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                CharSequence charSequence;
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                Chat chat = (Chat) ChatLinkActivity.this.chats.get(i - ChatLinkActivity.this.chatStartRow);
                if (TextUtils.isEmpty(chat.username)) {
                    charSequence = null;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("@");
                    stringBuilder.append(chat.username);
                    charSequence = stringBuilder.toString();
                }
                if (!(i == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0)) {
                    z = true;
                }
                manageChatUserCell.setData(chat, null, charSequence, z);
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
                String str = "windowBackgroundWhiteRedText5";
                if (!ChatLinkActivity.this.isChannel) {
                    manageChatTextCell.setColors(str, str);
                    manageChatTextCell.setText(LocaleController.getString("DiscussionUnlinkChannel", NUM), null, NUM, false);
                } else if (ChatLinkActivity.this.info.linked_chat_id != 0) {
                    manageChatTextCell.setColors(str, str);
                    manageChatTextCell.setText(LocaleController.getString("DiscussionUnlinkGroup", NUM), null, NUM, false);
                } else {
                    manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    manageChatTextCell.setText(LocaleController.getString("DiscussionCreateGroup", NUM), null, NUM, true);
                }
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
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

    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<Chat> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Runnable searchRunnable;
        private int searchStartRow;
        private int totalCount;

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
            -$$Lambda$ChatLinkActivity$SearchAdapter$TUvgAYvpvIggtszTT20rbb2gK0U -__lambda_chatlinkactivity_searchadapter_tuvgayvpviggtsztt20rbb2gk0u = new -$$Lambda$ChatLinkActivity$SearchAdapter$TUvgAYvpvIggtszTT20rbb2gK0U(this, str);
            this.searchRunnable = -__lambda_chatlinkactivity_searchadapter_tuvgayvpviggtsztt20rbb2gk0u;
            dispatchQueue.postRunnable(-__lambda_chatlinkactivity_searchadapter_tuvgayvpviggtsztt20rbb2gk0u, 300);
        }

        public /* synthetic */ void lambda$searchDialogs$0$ChatLinkActivity$SearchAdapter(String str) {
            processSearch(str);
        }

        private void processSearch(String str) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$SearchAdapter$uzR0bcURzUaKW4VFIVGjup-TFMI(this, str));
        }

        public /* synthetic */ void lambda$processSearch$2$ChatLinkActivity$SearchAdapter(String str) {
            this.searchRunnable = null;
            Utilities.searchQueue.postRunnable(new -$$Lambda$ChatLinkActivity$SearchAdapter$W0qju9LU94_vCLASSNAMETYEYKWpSKSG8(this, str, new ArrayList(ChatLinkActivity.this.chats)));
        }

        /* JADX WARNING: Removed duplicated region for block: B:44:0x00ff A:{LOOP_END, LOOP:1: B:23:0x0074->B:44:0x00ff} */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x00c3 A:{SYNTHETIC} */
        /* JADX WARNING: Missing block: B:32:0x00b0, code skipped:
            if (r11.contains(r3.toString()) != false) goto L_0x00c0;
     */
        public /* synthetic */ void lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String r18, java.util.ArrayList r19) {
            /*
            r17 = this;
            r0 = r17;
            r1 = r18.trim();
            r1 = r1.toLowerCase();
            r2 = r1.length();
            if (r2 != 0) goto L_0x001e;
        L_0x0010:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r0.updateSearchResults(r1, r2);
            return;
        L_0x001e:
            r2 = org.telegram.messenger.LocaleController.getInstance();
            r2 = r2.getTranslitString(r1);
            r3 = r1.equals(r2);
            if (r3 != 0) goto L_0x0032;
        L_0x002c:
            r3 = r2.length();
            if (r3 != 0) goto L_0x0033;
        L_0x0032:
            r2 = 0;
        L_0x0033:
            r3 = 0;
            r5 = 1;
            if (r2 == 0) goto L_0x0039;
        L_0x0037:
            r6 = 1;
            goto L_0x003a;
        L_0x0039:
            r6 = 0;
        L_0x003a:
            r6 = r6 + r5;
            r6 = new java.lang.String[r6];
            r6[r3] = r1;
            if (r2 == 0) goto L_0x0043;
        L_0x0041:
            r6[r5] = r2;
        L_0x0043:
            r1 = new java.util.ArrayList;
            r1.<init>();
            r2 = new java.util.ArrayList;
            r2.<init>();
            r7 = 0;
        L_0x004e:
            r8 = r19.size();
            if (r7 >= r8) goto L_0x010b;
        L_0x0054:
            r8 = r19;
            r9 = r8.get(r7);
            r9 = (org.telegram.tgnet.TLRPC.Chat) r9;
            r10 = r9.title;
            r10 = r10.toLowerCase();
            r11 = org.telegram.messenger.LocaleController.getInstance();
            r11 = r11.getTranslitString(r10);
            r12 = r10.equals(r11);
            if (r12 == 0) goto L_0x0071;
        L_0x0070:
            r11 = 0;
        L_0x0071:
            r12 = r6.length;
            r13 = 0;
            r14 = 0;
        L_0x0074:
            if (r13 >= r12) goto L_0x0105;
        L_0x0076:
            r15 = r6[r13];
            r16 = r10.startsWith(r15);
            if (r16 != 0) goto L_0x00c0;
        L_0x007e:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = " ";
            r3.append(r4);
            r3.append(r15);
            r3 = r3.toString();
            r3 = r10.contains(r3);
            if (r3 != 0) goto L_0x00c0;
        L_0x0095:
            if (r11 == 0) goto L_0x00b3;
        L_0x0097:
            r3 = r11.startsWith(r15);
            if (r3 != 0) goto L_0x00c0;
        L_0x009d:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r3.append(r4);
            r3.append(r15);
            r3 = r3.toString();
            r3 = r11.contains(r3);
            if (r3 == 0) goto L_0x00b3;
        L_0x00b2:
            goto L_0x00c0;
        L_0x00b3:
            r3 = r9.username;
            if (r3 == 0) goto L_0x00c1;
        L_0x00b7:
            r3 = r3.startsWith(r15);
            if (r3 == 0) goto L_0x00c1;
        L_0x00bd:
            r3 = 2;
            r14 = 2;
            goto L_0x00c1;
        L_0x00c0:
            r14 = 1;
        L_0x00c1:
            if (r14 == 0) goto L_0x00ff;
        L_0x00c3:
            if (r14 != r5) goto L_0x00d1;
        L_0x00c5:
            r3 = r9.title;
            r4 = 0;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r4, r15);
            r2.add(r3);
            r15 = 0;
            goto L_0x00fb;
        L_0x00d1:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "@";
            r3.append(r4);
            r10 = r9.username;
            r3.append(r10);
            r3 = r3.toString();
            r10 = new java.lang.StringBuilder;
            r10.<init>();
            r10.append(r4);
            r10.append(r15);
            r4 = r10.toString();
            r15 = 0;
            r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r15, r4);
            r2.add(r3);
        L_0x00fb:
            r1.add(r9);
            goto L_0x0106;
        L_0x00ff:
            r15 = 0;
            r13 = r13 + 1;
            r3 = 0;
            goto L_0x0074;
        L_0x0105:
            r15 = 0;
        L_0x0106:
            r7 = r7 + 1;
            r3 = 0;
            goto L_0x004e;
        L_0x010b:
            r0.updateSearchResults(r1, r2);
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatLinkActivity$SearchAdapter.lambda$null$1$ChatLinkActivity$SearchAdapter(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(ArrayList<Chat> arrayList, ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$SearchAdapter$brffftfBeb-mTh3bCNRySLqW8nw(this, arrayList, arrayList2));
        }

        public /* synthetic */ void lambda$updateSearchResults$3$ChatLinkActivity$SearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
            this.searchResult = arrayList;
            this.searchResultNames = arrayList2;
            if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                ChatLinkActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public void notifyDataSetChanged() {
            this.totalCount = 0;
            int size = this.searchResult.size();
            if (size != 0) {
                int i = this.totalCount;
                this.searchStartRow = i;
                this.totalCount = i + (size + 1);
            } else {
                this.searchStartRow = -1;
            }
            super.notifyDataSetChanged();
        }

        public Chat getItem(int i) {
            return (Chat) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
            manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            return new Holder(manageChatUserCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Chat chat = (Chat) this.searchResult.get(i);
            String str = chat.username;
            CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
            CharSequence charSequence2 = null;
            if (!(charSequence == null || TextUtils.isEmpty(str))) {
                String charSequence3 = charSequence.toString();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(str);
                if (charSequence3.startsWith(stringBuilder.toString())) {
                    charSequence2 = charSequence;
                    charSequence = null;
                }
            }
            ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
            manageChatUserCell.setTag(Integer.valueOf(i));
            manageChatUserCell.setData(chat, charSequence, charSequence2, false);
        }

        public void onViewRecycled(ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }
    }

    public ChatLinkActivity(int i) {
        this.currentChatId = i;
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
        boolean z = ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup;
        this.isChannel = z;
    }

    private void updateRows() {
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.currentChatId));
        if (this.currentChat != null) {
            int i = 0;
            this.rowCount = 0;
            this.helpRow = -1;
            this.createChatRow = -1;
            this.chatStartRow = -1;
            this.chatEndRow = -1;
            this.removeChatRow = -1;
            this.detailRow = -1;
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.helpRow = i2;
            if (this.isChannel) {
                if (this.info.linked_chat_id == 0) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.createChatRow = i2;
                }
                i2 = this.rowCount;
                this.chatStartRow = i2;
                this.rowCount = i2 + this.chats.size();
                i2 = this.rowCount;
                this.chatEndRow = i2;
                if (this.info.linked_chat_id != 0) {
                    this.rowCount = i2 + 1;
                    this.createChatRow = i2;
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.detailRow = i2;
            } else {
                i2 = this.rowCount;
                this.chatStartRow = i2;
                this.rowCount = i2 + this.chats.size();
                i2 = this.rowCount;
                this.chatEndRow = i2;
                this.rowCount = i2 + 1;
                this.createChatRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.detailRow = i2;
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
            ChatFull chatFull = (ChatFull) objArr[0];
            int i3 = chatFull.id;
            if (i3 == this.currentChatId) {
                this.info = chatFull;
                loadChats();
                updateRows();
                return;
            }
            Chat chat = this.waitingForFullChat;
            if (chat != null && chat.id == i3) {
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
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discussion", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatLinkActivity.this.finishFragment();
                }
            }
        });
        this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                ChatLinkActivity.this.searching = true;
                ChatLinkActivity.this.emptyView.setShowAtCenter(true);
            }

            public void onSearchCollapse() {
                ChatLinkActivity.this.searchAdapter.searchDialogs(null);
                ChatLinkActivity.this.searching = false;
                ChatLinkActivity.this.searchWas = false;
                ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.listViewAdapter);
                ChatLinkActivity.this.listViewAdapter.notifyDataSetChanged();
                ChatLinkActivity.this.listView.setFastScrollVisible(true);
                ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(false);
                ChatLinkActivity.this.emptyView.setShowAtCenter(false);
                String str = "windowBackgroundGray";
                ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(str));
                ChatLinkActivity.this.fragmentView.setTag(str);
                ChatLinkActivity.this.emptyView.showProgress();
            }

            public void onTextChanged(EditText editText) {
                if (ChatLinkActivity.this.searchAdapter != null) {
                    String obj = editText.getText().toString();
                    if (obj.length() != 0) {
                        ChatLinkActivity.this.searchWas = true;
                        if (!(ChatLinkActivity.this.listView == null || ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter)) {
                            ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.searchAdapter);
                            String str = "windowBackgroundWhite";
                            ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(str));
                            ChatLinkActivity.this.fragmentView.setTag(str);
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
        this.searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        String str = "windowBackgroundGray";
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        this.fragmentView.setTag(str);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.showProgress();
        this.emptyView.setText(LocaleController.getString("NoResult", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$ChatLinkActivity$EId5OESEIJ9T5F7R7Ql_mhkWmi4(this));
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$ChatLinkActivity(View view, int i) {
        if (getParentActivity() != null) {
            Chat item;
            Adapter adapter = this.listView.getAdapter();
            Adapter adapter2 = this.searchAdapter;
            if (adapter == adapter2) {
                item = adapter2.getItem(i);
            } else {
                int i2 = this.chatStartRow;
                item = (i < i2 || i >= this.chatEndRow) ? null : (Chat) this.chats.get(i - i2);
            }
            if (item != null) {
                if (this.isChannel && this.info.linked_chat_id == 0) {
                    showLinkAlert(item, true);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("chat_id", item.id);
                    presentFragment(new ChatActivity(bundle));
                }
                return;
            }
            if (i == this.createChatRow) {
                if (this.isChannel && this.info.linked_chat_id == 0) {
                    Bundle bundle2 = new Bundle();
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Integer.valueOf(getUserConfig().getClientUserId()));
                    bundle2.putIntegerArrayList("result", arrayList);
                    bundle2.putInt("chatType", 4);
                    GroupCreateFinalActivity groupCreateFinalActivity = new GroupCreateFinalActivity(bundle2);
                    groupCreateFinalActivity.setDelegate(new GroupCreateFinalActivityDelegate() {
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
                    CharSequence string;
                    String formatString;
                    item = (Chat) this.chats.get(0);
                    Builder builder = new Builder(getParentActivity());
                    String str = "DiscussionUnlink";
                    if (this.isChannel) {
                        string = LocaleController.getString("DiscussionUnlinkGroup", NUM);
                        formatString = LocaleController.formatString("DiscussionUnlinkChannelAlert", NUM, item.title);
                    } else {
                        string = LocaleController.getString(str, NUM);
                        formatString = LocaleController.formatString("DiscussionUnlinkGroupAlert", NUM, item.title);
                    }
                    builder.setTitle(string);
                    builder.setMessage(AndroidUtilities.replaceTags(formatString));
                    builder.setPositiveButton(LocaleController.getString(str, NUM), new -$$Lambda$ChatLinkActivity$Igj2ZC8r9x-yYwrHQwXpVZFNIyY(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
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
            AlertDialog[] alertDialogArr = new AlertDialog[]{new AlertDialog(getParentActivity(), 3)};
            TL_channels_setDiscussionGroup tL_channels_setDiscussionGroup = new TL_channels_setDiscussionGroup();
            if (this.isChannel) {
                tL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
                tL_channels_setDiscussionGroup.group = new TL_inputChannelEmpty();
            } else {
                tL_channels_setDiscussionGroup.broadcast = new TL_inputChannelEmpty();
                tL_channels_setDiscussionGroup.group = MessagesController.getInputChannel(this.currentChat);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$EDqDx1-jf5fdYyg4SJu9ZmeA1SY(this, alertDialogArr, getConnectionsManager().sendRequest(tL_channels_setDiscussionGroup, new -$$Lambda$ChatLinkActivity$Hzb-oINiZZCK4So4QPZpn91gK3A(this, alertDialogArr))), 500);
        }
    }

    public /* synthetic */ void lambda$null$1$ChatLinkActivity(AlertDialog[] alertDialogArr, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$quMUtF8QHjfTQ-MuJuvx5U43M8k(this, alertDialogArr));
    }

    public /* synthetic */ void lambda$null$0$ChatLinkActivity(AlertDialog[] alertDialogArr) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        this.info.linked_chat_id = 0;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, Integer.valueOf(0), Boolean.valueOf(false), null);
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
        if (!this.isChannel) {
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$null$3$ChatLinkActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$ChatLinkActivity$Eb3fJqb9-RHvQPpwLb48kNyZrZ8(this, i));
            showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$2$ChatLinkActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    private void showLinkAlert(Chat chat, boolean z) {
        Object obj = chat;
        ChatFull chatFull = getMessagesController().getChatFull(obj.id);
        int i = 3;
        if (chatFull == null) {
            if (z) {
                getMessagesController().loadFullChat(obj.id, 0, true);
                this.waitingForFullChat = obj;
                this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$XAuETeIZs_27DO0KwtCzWWlThog(this), 500);
            }
            return;
        }
        String formatString;
        Builder builder = new Builder(getParentActivity());
        TextView textView = new TextView(getParentActivity());
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setTextSize(1, 16.0f);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (TextUtils.isEmpty(obj.username)) {
            formatString = LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", NUM, obj.title, this.currentChat.title);
        } else if (TextUtils.isEmpty(this.currentChat.username)) {
            formatString = LocaleController.formatString("DiscussionLinkGroupPrivateAlert", NUM, obj.title, this.currentChat.title);
        } else {
            formatString = LocaleController.formatString("DiscussionLinkGroupPublicAlert", NUM, obj.title, this.currentChat.title);
        }
        if (chatFull.hidden_prehistory) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(formatString);
            stringBuilder.append("\n\n");
            stringBuilder.append(LocaleController.getString("DiscussionLinkGroupAlertHistory", NUM));
            formatString = stringBuilder.toString();
        }
        textView.setText(AndroidUtilities.replaceTags(formatString));
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        builder.setView(frameLayout);
        Drawable avatarDrawable = new AvatarDrawable();
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
        textView2.setEllipsize(TruncateAt.END);
        textView2.setText(obj.title);
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
        avatarDrawable.setInfo((Chat) obj);
        backupImageView.setImage(ImageLocation.getForChat(obj, false), "50_50", avatarDrawable, obj);
        builder.setPositiveButton(LocaleController.getString("DiscussionLinkGroup", NUM), new -$$Lambda$ChatLinkActivity$tvkiY8dJp0f6xPm_0V9yT1Vcn74(this, chatFull, obj));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        showDialog(builder.create());
    }

    public /* synthetic */ void lambda$showLinkAlert$7$ChatLinkActivity() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog != null) {
            alertDialog.setOnCancelListener(new -$$Lambda$ChatLinkActivity$14f5beHvshaSysjkW1gsXt7mUc4(this));
            showDialog(this.waitingForFullChatProgressAlert);
        }
    }

    public /* synthetic */ void lambda$null$6$ChatLinkActivity(DialogInterface dialogInterface) {
        this.waitingForFullChat = null;
    }

    public /* synthetic */ void lambda$showLinkAlert$8$ChatLinkActivity(ChatFull chatFull, Chat chat, DialogInterface dialogInterface, int i) {
        if (chatFull.hidden_prehistory) {
            MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(chat.id, false);
        }
        linkChat(chat, null);
    }

    private void linkChat(Chat chat, BaseFragment baseFragment) {
        if (chat != null) {
            if (ChatObject.isChannel(chat)) {
                AlertDialog[] alertDialogArr = new AlertDialog[1];
                alertDialogArr[0] = baseFragment != null ? null : new AlertDialog(getParentActivity(), 3);
                TL_channels_setDiscussionGroup tL_channels_setDiscussionGroup = new TL_channels_setDiscussionGroup();
                tL_channels_setDiscussionGroup.broadcast = MessagesController.getInputChannel(this.currentChat);
                tL_channels_setDiscussionGroup.group = MessagesController.getInputChannel(chat);
                AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$97kBY0LYAi0P-kzDEVlDTHmupZw(this, alertDialogArr, getConnectionsManager().sendRequest(tL_channels_setDiscussionGroup, new -$$Lambda$ChatLinkActivity$Tjh48No2Z3EZ2EiDDrohPMflh_A(this, alertDialogArr, chat, baseFragment))), 500);
                return;
            }
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), chat.id, new -$$Lambda$ChatLinkActivity$KB-csNS4ZyyV341Vg7wZcvTIREM(this, baseFragment));
        }
    }

    public /* synthetic */ void lambda$linkChat$9$ChatLinkActivity(BaseFragment baseFragment, int i) {
        MessagesController.getInstance(this.currentAccount).toogleChannelInvitesHistory(i, false);
        linkChat(getMessagesController().getChat(Integer.valueOf(i)), baseFragment);
    }

    public /* synthetic */ void lambda$linkChat$11$ChatLinkActivity(AlertDialog[] alertDialogArr, Chat chat, BaseFragment baseFragment, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$QC2Mz33KGE4zAmpWeIpueArOQXY(this, alertDialogArr, chat, baseFragment));
    }

    public /* synthetic */ void lambda$null$10$ChatLinkActivity(AlertDialog[] alertDialogArr, Chat chat, BaseFragment baseFragment) {
        if (alertDialogArr[0] != null) {
            try {
                alertDialogArr[0].dismiss();
            } catch (Throwable unused) {
            }
            alertDialogArr[0] = null;
        }
        this.info.linked_chat_id = chat.id;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, Integer.valueOf(0), Boolean.valueOf(false), null);
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
            alertDialogArr[0].setOnCancelListener(new -$$Lambda$ChatLinkActivity$Iy-_bAEHS1bHHS34WDzDt7Rdm0U(this, i));
            showDialog(alertDialogArr[0]);
        }
    }

    public /* synthetic */ void lambda$null$12$ChatLinkActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void setInfo(ChatFull chatFull) {
        this.info = chatFull;
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            Chat chat = getMessagesController().getChat(Integer.valueOf(this.info.linked_chat_id));
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
            getConnectionsManager().sendRequest(new TL_channels_getGroupsForDiscussion(), new -$$Lambda$ChatLinkActivity$tq6x8HdoUQyHzEq8SE366Ggv0uM(this));
        }
    }

    public /* synthetic */ void lambda$loadChats$15$ChatLinkActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatLinkActivity$tbTNvaUEjjYHPnRSNVytKd0ibuA(this, tLObject));
    }

    public /* synthetic */ void lambda$null$14$ChatLinkActivity(TLObject tLObject) {
        if (tLObject instanceof messages_Chats) {
            messages_Chats messages_chats = (messages_Chats) tLObject;
            getMessagesController().putChats(messages_chats.chats, false);
            this.chats = messages_chats.chats;
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

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu = new -$$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU(this);
        r11 = new ThemeDescription[27];
        r11[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, null, null, null, "windowBackgroundWhite");
        r11[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        r11[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        r11[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r11[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r11[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r11[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r11[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r11[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        View view2 = view;
        r11[10] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r11[11] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        r11[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu;
        r11[13] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        r11[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        r11[15] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ChatLinkActivity$M285Vuz7T55WG0zESsKh6UUk1tU -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2 = -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu;
        r11[16] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundRed");
        r11[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundOrange");
        r11[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundViolet");
        r11[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundGreen");
        r11[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundCyan");
        r11[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundBlue");
        r11[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_chatlinkactivity_m285vuz7t55wg0zesskh6uuk1tu2, "avatar_backgroundPink");
        view = this.listView;
        view2 = view;
        r11[23] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{ManageChatTextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r11[24] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view2 = view;
        r11[25] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view2 = view;
        r11[26] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return r11;
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
