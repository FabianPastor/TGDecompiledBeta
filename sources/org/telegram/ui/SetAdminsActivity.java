package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SetAdminsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int allAdminsInfoRow;
    private int allAdminsRow;
    private Chat chat;
    private int chat_id;
    private EmptyTextProgressView emptyView;
    private ChatFull info;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ArrayList<ChatParticipant> participants = new ArrayList();
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private int usersEndRow;
    private int usersStartRow;

    /* renamed from: org.telegram.ui.SetAdminsActivity$4 */
    class C16814 implements Comparator<ChatParticipant> {
        C16814() {
        }

        public int compare(ChatParticipant chatParticipant, ChatParticipant chatParticipant2) {
            int access$1900 = SetAdminsActivity.this.getChatAdminParticipantType(chatParticipant);
            int access$19002 = SetAdminsActivity.this.getChatAdminParticipantType(chatParticipant2);
            if (access$1900 > access$19002) {
                return 1;
            }
            if (access$1900 < access$19002) {
                return -1;
            }
            if (access$1900 == access$19002) {
                chatParticipant2 = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant2.user_id));
                chatParticipant = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id));
                chatParticipant2 = (chatParticipant2 == null || chatParticipant2.status == null) ? null : chatParticipant2.status.expires;
                chatParticipant = (chatParticipant == null || chatParticipant.status == null) ? null : chatParticipant.status.expires;
                if (chatParticipant2 <= null || chatParticipant <= null) {
                    if (chatParticipant2 >= null || chatParticipant >= null) {
                        if ((chatParticipant2 >= null || chatParticipant <= null) && (chatParticipant2 != null || chatParticipant == null)) {
                            return ((chatParticipant >= null || chatParticipant2 <= null) && (chatParticipant != null || chatParticipant2 == null)) ? 0 : 1;
                        } else {
                            return -1;
                        }
                    } else if (chatParticipant2 > chatParticipant) {
                        return 1;
                    } else {
                        return chatParticipant2 < chatParticipant ? -1 : 0;
                    }
                } else if (chatParticipant2 > chatParticipant) {
                    return 1;
                } else {
                    return chatParticipant2 < chatParticipant ? -1 : 0;
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SetAdminsActivity$1 */
    class C22821 extends ActionBarMenuOnItemClick {
        C22821() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                SetAdminsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.SetAdminsActivity$2 */
    class C22832 extends ActionBarMenuItemSearchListener {
        C22832() {
        }

        public void onSearchExpand() {
            SetAdminsActivity.this.searching = true;
            SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
        }

        public void onSearchCollapse() {
            SetAdminsActivity.this.searching = false;
            SetAdminsActivity.this.searchWas = false;
            if (SetAdminsActivity.this.listView != null) {
                SetAdminsActivity.this.listView.setEmptyView(null);
                SetAdminsActivity.this.emptyView.setVisibility(8);
                if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter) {
                    SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
                }
            }
            if (SetAdminsActivity.this.searchAdapter != null) {
                SetAdminsActivity.this.searchAdapter.search(null);
            }
        }

        public void onTextChanged(EditText editText) {
            editText = editText.getText().toString();
            if (editText.length() != 0) {
                SetAdminsActivity.this.searchWas = true;
                if (!(SetAdminsActivity.this.searchAdapter == null || SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)) {
                    SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.searchAdapter);
                    SetAdminsActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                }
                if (!(SetAdminsActivity.this.emptyView == null || SetAdminsActivity.this.listView.getEmptyView() == SetAdminsActivity.this.emptyView)) {
                    SetAdminsActivity.this.emptyView.showTextView();
                    SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
                }
            }
            if (SetAdminsActivity.this.searchAdapter != null) {
                SetAdminsActivity.this.searchAdapter.search(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.SetAdminsActivity$3 */
    class C22843 implements OnItemClickListener {
        C22843() {
        }

        public void onItemClick(View view, int i) {
            ChatParticipant item;
            int i2;
            boolean z = true;
            if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.searchAdapter) {
                if (i < SetAdminsActivity.this.usersStartRow || i >= SetAdminsActivity.this.usersEndRow) {
                    if (i == SetAdminsActivity.this.allAdminsRow) {
                        SetAdminsActivity.this.chat = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                        if (SetAdminsActivity.this.chat != 0) {
                            SetAdminsActivity.this.chat.admins_enabled ^= true;
                            ((TextCheckCell) view).setChecked(SetAdminsActivity.this.chat.admins_enabled ^ 1);
                            MessagesController.getInstance(SetAdminsActivity.this.currentAccount).toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
                            return;
                        }
                        return;
                    }
                    return;
                }
            }
            UserCell userCell = (UserCell) view;
            SetAdminsActivity.this.chat = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
            if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                item = SetAdminsActivity.this.searchAdapter.getItem(i);
                i2 = 0;
                while (i2 < SetAdminsActivity.this.participants.size()) {
                    if (((ChatParticipant) SetAdminsActivity.this.participants.get(i2)).user_id == item.user_id) {
                        break;
                    }
                    i2++;
                }
                i2 = -1;
            } else {
                i2 = i - SetAdminsActivity.this.usersStartRow;
                item = (ChatParticipant) SetAdminsActivity.this.participants.get(i2);
            }
            if (i2 != -1 && !(item instanceof TL_chatParticipantCreator)) {
                ChatParticipant tL_chatParticipantAdmin;
                boolean z2;
                if (item instanceof TL_chatParticipant) {
                    tL_chatParticipantAdmin = new TL_chatParticipantAdmin();
                    tL_chatParticipantAdmin.user_id = item.user_id;
                    tL_chatParticipantAdmin.date = item.date;
                    tL_chatParticipantAdmin.inviter_id = item.inviter_id;
                } else {
                    tL_chatParticipantAdmin = new TL_chatParticipant();
                    tL_chatParticipantAdmin.user_id = item.user_id;
                    tL_chatParticipantAdmin.date = item.date;
                    tL_chatParticipantAdmin.inviter_id = item.inviter_id;
                }
                SetAdminsActivity.this.participants.set(i2, tL_chatParticipantAdmin);
                int indexOf = SetAdminsActivity.this.info.participants.participants.indexOf(item);
                if (indexOf != -1) {
                    SetAdminsActivity.this.info.participants.participants.set(indexOf, tL_chatParticipantAdmin);
                }
                if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                    SetAdminsActivity.this.searchAdapter.searchResult.set(i, tL_chatParticipantAdmin);
                }
                i = tL_chatParticipantAdmin instanceof TL_chatParticipant;
                if (i != 0) {
                    if (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled) {
                        z2 = false;
                        userCell.setChecked(z2, true);
                        if (SetAdminsActivity.this.chat != null && SetAdminsActivity.this.chat.admins_enabled != null) {
                            view = MessagesController.getInstance(SetAdminsActivity.this.currentAccount);
                            indexOf = SetAdminsActivity.this.chat_id;
                            i2 = tL_chatParticipantAdmin.user_id;
                            if (i != 0) {
                                z = false;
                            }
                            view.toggleUserAdmin(indexOf, i2, z);
                            return;
                        }
                    }
                }
                z2 = true;
                userCell.setChecked(z2, true);
                if (SetAdminsActivity.this.chat != null) {
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.SetAdminsActivity$5 */
    class C22855 implements ThemeDescriptionDelegate {
        C22855() {
        }

        public void didSetColor() {
            if (SetAdminsActivity.this.listView != null) {
                int childCount = SetAdminsActivity.this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = SetAdminsActivity.this.listView.getChildAt(i);
                    if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    }
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (viewHolder == SetAdminsActivity.this.allAdminsRow) {
                return true;
            }
            if (viewHolder < SetAdminsActivity.this.usersStartRow || viewHolder >= SetAdminsActivity.this.usersEndRow || (((ChatParticipant) SetAdminsActivity.this.participants.get(viewHolder - SetAdminsActivity.this.usersStartRow)) instanceof TL_chatParticipantCreator) != null) {
                return null;
            }
            return true;
        }

        public int getItemCount() {
            return SetAdminsActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new TextCheckCell(this.mContext);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    viewGroup = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    viewGroup = new UserCell(this.mContext, 1, 2, false);
                    viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = true;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    SetAdminsActivity.this.chat = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                    i = LocaleController.getString("SetAdminsAll", C0446R.string.SetAdminsAll);
                    if (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled) {
                        z = false;
                    }
                    textCheckCell.setTextAndCheck(i, z, false);
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == SetAdminsActivity.this.allAdminsInfoRow) {
                        if (SetAdminsActivity.this.chat.admins_enabled != 0) {
                            textInfoPrivacyCell.setText(LocaleController.getString("SetAdminsNotAllInfo", C0446R.string.SetAdminsNotAllInfo));
                        } else {
                            textInfoPrivacyCell.setText(LocaleController.getString("SetAdminsAllInfo", C0446R.string.SetAdminsAllInfo));
                        }
                        if (SetAdminsActivity.this.usersStartRow != -1) {
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        } else {
                            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    } else if (i == SetAdminsActivity.this.usersEndRow) {
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    boolean z2;
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    ChatParticipant chatParticipant = (ChatParticipant) SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow);
                    userCell.setData(MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id)), null, null, 0);
                    SetAdminsActivity.this.chat = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                    if (chatParticipant instanceof TL_chatParticipant) {
                        if (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled) {
                            z2 = false;
                            userCell.setChecked(z2, false);
                            if (SetAdminsActivity.this.chat != null && SetAdminsActivity.this.chat.admins_enabled) {
                                if (chatParticipant.user_id == UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                                    z = false;
                                }
                            }
                            userCell.setCheckDisabled(z);
                            return;
                        }
                    }
                    z2 = true;
                    userCell.setChecked(z2, false);
                    if (chatParticipant.user_id == UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                        z = false;
                    }
                    userCell.setCheckDisabled(z);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == SetAdminsActivity.this.allAdminsRow) {
                return 0;
            }
            if (i != SetAdminsActivity.this.allAdminsInfoRow) {
                if (i != SetAdminsActivity.this.usersEndRow) {
                    return 2;
                }
            }
            return 1;
        }
    }

    public class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<ChatParticipant> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(final String str) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            this.searchTimer = new Timer();
            this.searchTimer.schedule(new TimerTask() {
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        SearchAdapter.this.searchTimer = null;
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                    SearchAdapter.this.processSearch(str);
                }
            }, 200, 300);
        }

        private void processSearch(final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList arrayList = new ArrayList();
                    arrayList.addAll(SetAdminsActivity.this.participants);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String toLowerCase = str.trim().toLowerCase();
                            if (toLowerCase.length() == 0) {
                                SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
                            if (toLowerCase.equals(translitString) || translitString.length() == 0) {
                                translitString = null;
                            }
                            int i = 0;
                            String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
                            strArr[0] = toLowerCase;
                            if (translitString != null) {
                                strArr[1] = translitString;
                            }
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            int i2 = 0;
                            while (i2 < arrayList.size()) {
                                ChatParticipant chatParticipant = (ChatParticipant) arrayList.get(i2);
                                User user = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id));
                                if (user.id != UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                                    String toLowerCase2 = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                    String translitString2 = LocaleController.getInstance().getTranslitString(toLowerCase2);
                                    if (toLowerCase2.equals(translitString2)) {
                                        translitString2 = null;
                                    }
                                    int length = strArr.length;
                                    int i3 = i;
                                    int i4 = i3;
                                    while (i3 < length) {
                                        StringBuilder stringBuilder;
                                        String stringBuilder2;
                                        StringBuilder stringBuilder3;
                                        String str = strArr[i3];
                                        if (!toLowerCase2.startsWith(str)) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(" ");
                                            stringBuilder.append(str);
                                            if (!toLowerCase2.contains(stringBuilder.toString())) {
                                                if (translitString2 != null) {
                                                    if (!translitString2.startsWith(str)) {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append(" ");
                                                        stringBuilder.append(str);
                                                        if (translitString2.contains(stringBuilder.toString())) {
                                                        }
                                                    }
                                                }
                                                if (user.username != null && user.username.startsWith(str)) {
                                                    i4 = 2;
                                                }
                                                if (i4 == 0) {
                                                    if (i4 != 1) {
                                                        arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(user.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(str);
                                                        arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                    }
                                                    arrayList.add(chatParticipant);
                                                    i2++;
                                                    i = 0;
                                                } else {
                                                    i3++;
                                                }
                                            }
                                        }
                                        i4 = 1;
                                        if (i4 == 0) {
                                            i3++;
                                        } else {
                                            if (i4 != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(user.username);
                                                stringBuilder2 = stringBuilder.toString();
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("@");
                                                stringBuilder3.append(str);
                                                arrayList2.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                            } else {
                                                arrayList2.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str));
                                            }
                                            arrayList.add(chatParticipant);
                                            i2++;
                                            i = 0;
                                        }
                                    }
                                }
                                i2++;
                                i = 0;
                            }
                            SearchAdapter.this.updateSearchResults(arrayList, arrayList2);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<ChatParticipant> arrayList, final ArrayList<CharSequence> arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = arrayList;
                    SearchAdapter.this.searchResultNames = arrayList2;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ChatParticipant getItem(int i) {
            return (ChatParticipant) this.searchResult.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new UserCell(this.mContext, 1, 2, false));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ChatParticipant item = getItem(i);
            TLObject user = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getUser(Integer.valueOf(item.user_id));
            String str = user.username;
            CharSequence charSequence = null;
            if (i < this.searchResult.size()) {
                i = (CharSequence) this.searchResultNames.get(i);
                if (!(i == null || str == null || str.length() <= 0)) {
                    String charSequence2 = i.toString();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("@");
                    stringBuilder.append(str);
                    if (charSequence2.startsWith(stringBuilder.toString())) {
                    }
                }
                charSequence = i;
                i = null;
            } else {
                i = 0;
            }
            UserCell userCell = (UserCell) viewHolder.itemView;
            userCell.setData(user, charSequence, i, 0);
            SetAdminsActivity.this.chat = MessagesController.getInstance(SetAdminsActivity.this.currentAccount).getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
            boolean z = true;
            if ((item instanceof TL_chatParticipant) != 0) {
                if (SetAdminsActivity.this.chat == 0 || SetAdminsActivity.this.chat.admins_enabled != 0) {
                    i = 0;
                    userCell.setChecked(i, false);
                    if (!(SetAdminsActivity.this.chat == 0 || SetAdminsActivity.this.chat.admins_enabled == 0)) {
                        if (item.user_id == UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                            z = false;
                        }
                    }
                    userCell.setCheckDisabled(z);
                }
            }
            i = 1;
            userCell.setChecked(i, false);
            if (item.user_id == UserConfig.getInstance(SetAdminsActivity.this.currentAccount).getClientUserId()) {
                z = false;
            }
            userCell.setCheckDisabled(z);
        }
    }

    public SetAdminsActivity(Bundle bundle) {
        super(bundle);
        this.chat_id = bundle.getInt("chat_id");
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", C0446R.string.SetAdminsTitle));
        this.actionBar.setActionBarMenuOnItemClick(new C22821());
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22832());
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C22843());
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setVisibility(8);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.showTextView();
        updateRowsIds();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chat_id) {
                this.info = chatFull;
                updateChatParticipants();
                updateRowsIds();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if (((i & 2) != 0 || (i & 1) != 0 || (i & 4) != 0) && this.listView != 0) {
                i2 = this.listView.getChildCount();
                while (i3 < i2) {
                    objArr = this.listView.getChildAt(i3);
                    if (objArr instanceof UserCell) {
                        ((UserCell) objArr).update(i);
                    }
                    i3++;
                }
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        updateChatParticipants();
    }

    private int getChatAdminParticipantType(ChatParticipant chatParticipant) {
        if (chatParticipant instanceof TL_chatParticipantCreator) {
            return null;
        }
        return (chatParticipant instanceof TL_chatParticipantAdmin) != null ? 1 : 2;
    }

    private void updateChatParticipants() {
        if (!(this.info == null || this.participants.size() == this.info.participants.participants.size())) {
            this.participants.clear();
            this.participants.addAll(this.info.participants.participants);
            try {
                Collections.sort(this.participants, new C16814());
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    private void updateRowsIds() {
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.allAdminsRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.allAdminsInfoRow = i;
        if (this.info != null) {
            this.usersStartRow = this.rowCount;
            this.rowCount += this.participants.size();
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usersEndRow = i;
            if (!(this.searchItem == null || this.searchWas)) {
                this.searchItem.setVisibility(0);
            }
        } else {
            this.usersStartRow = -1;
            this.usersEndRow = -1;
            if (this.searchItem != null) {
                this.searchItem.setVisibility(8);
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        C22855 c22855 = new C22855();
        r11 = new ThemeDescription[34];
        r11[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCheckCell.class, UserCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r11[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r11[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r11[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r11[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r11[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r11[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        r11[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        r11[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r11[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r11[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r11[12] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r11[13] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb);
        r11[14] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r11[15] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked);
        r11[16] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        View view = this.listView;
        View view2 = view;
        r11[17] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r11[18] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r11[19] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, null, null, Theme.key_checkboxSquareUnchecked);
        r11[20] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, null, null, Theme.key_checkboxSquareDisabled);
        r11[21] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, null, null, Theme.key_checkboxSquareBackground);
        r11[22] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, null, null, Theme.key_checkboxSquareCheck);
        r11[23] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = c22855;
        r11[24] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r11[25] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r11[26] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        C22855 c228552 = c22855;
        r11[27] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundRed);
        r11[28] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundOrange);
        r11[29] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundViolet);
        r11[30] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundGreen);
        r11[31] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundCyan);
        r11[32] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundBlue);
        r11[33] = new ThemeDescription(null, 0, null, null, null, c228552, Theme.key_avatar_backgroundPink);
        return r11;
    }
}
