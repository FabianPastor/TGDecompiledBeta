package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
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
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;

public class SetAdminsActivity extends BaseFragment implements NotificationCenterDelegate {
    private int allAdminsInfoRow;
    private int allAdminsRow;
    private Chat chat;
    private int chat_id;
    private ChatFull info;
    private ListAdapter listAdapter;
    private ListView listView;
    private ArrayList<ChatParticipant> participants = new ArrayList();
    private int rowCount;
    private SearchAdapter searchAdapter;
    private EmptyTextProgressView searchEmptyView;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private int usersEndRow;
    private int usersStartRow;

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            if (i == SetAdminsActivity.this.allAdminsRow) {
                return true;
            }
            if (i < SetAdminsActivity.this.usersStartRow || i >= SetAdminsActivity.this.usersEndRow || (((ChatParticipant) SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow)) instanceof TL_chatParticipantCreator)) {
                return false;
            }
            return true;
        }

        public int getCount() {
            return SetAdminsActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z = true;
            boolean z2 = false;
            int type = getItemViewType(i);
            if (type == 0) {
                if (view == null) {
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextCheckCell checkCell = (TextCheckCell) view;
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                String string = LocaleController.getString("SetAdminsAll", R.string.SetAdminsAll);
                if (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled) {
                    z = false;
                }
                checkCell.setTextAndCheck(string, z, false);
            } else if (type == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == SetAdminsActivity.this.allAdminsInfoRow) {
                    if (SetAdminsActivity.this.chat.admins_enabled) {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("SetAdminsNotAllInfo", R.string.SetAdminsNotAllInfo));
                    } else {
                        ((TextInfoPrivacyCell) view).setText(LocaleController.getString("SetAdminsAllInfo", R.string.SetAdminsAllInfo));
                    }
                    if (SetAdminsActivity.this.usersStartRow != -1) {
                        view.setBackgroundResource(R.drawable.greydivider);
                    } else {
                        view.setBackgroundResource(R.drawable.greydivider_bottom);
                    }
                } else if (i == SetAdminsActivity.this.usersEndRow) {
                    ((TextInfoPrivacyCell) view).setText("");
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                }
            } else if (type == 2) {
                boolean z3;
                if (view == null) {
                    view = new UserCell(this.mContext, 1, 2, false);
                    view.setBackgroundColor(-1);
                }
                UserCell userCell = (UserCell) view;
                ChatParticipant part = (ChatParticipant) SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow);
                userCell.setData(MessagesController.getInstance().getUser(Integer.valueOf(part.user_id)), null, null, 0);
                SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                if ((part instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) {
                    z3 = false;
                } else {
                    z3 = true;
                }
                userCell.setChecked(z3, false);
                if (SetAdminsActivity.this.chat == null || !SetAdminsActivity.this.chat.admins_enabled || part.user_id == UserConfig.getClientUserId()) {
                    z2 = true;
                }
                userCell.setCheckDisabled(z2);
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == SetAdminsActivity.this.allAdminsRow) {
                return 0;
            }
            if (i == SetAdminsActivity.this.allAdminsInfoRow || i == SetAdminsActivity.this.usersEndRow) {
                return 1;
            }
            return 2;
        }

        public int getViewTypeCount() {
            return 3;
        }

        public boolean isEmpty() {
            return false;
        }
    }

    public class SearchAdapter extends BaseFragmentAdapter {
        private Context mContext;
        private ArrayList<ChatParticipant> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void search(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
            }
            if (query == null) {
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
                        FileLog.e("tmessages", e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    final ArrayList<ChatParticipant> contactsCopy = new ArrayList();
                    contactsCopy.addAll(SetAdminsActivity.this.participants);
                    Utilities.searchQueue.postRunnable(new Runnable() {
                        public void run() {
                            String search1 = query.trim().toLowerCase();
                            if (search1.length() == 0) {
                                SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                                return;
                            }
                            String search2 = LocaleController.getInstance().getTranslitString(search1);
                            if (search1.equals(search2) || search2.length() == 0) {
                                search2 = null;
                            }
                            String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                            search[0] = search1;
                            if (search2 != null) {
                                search[1] = search2;
                            }
                            ArrayList<ChatParticipant> resultArray = new ArrayList();
                            ArrayList<CharSequence> resultArrayNames = new ArrayList();
                            for (int a = 0; a < contactsCopy.size(); a++) {
                                ChatParticipant participant = (ChatParticipant) contactsCopy.get(a);
                                User user = MessagesController.getInstance().getUser(Integer.valueOf(participant.user_id));
                                if (user.id != UserConfig.getClientUserId()) {
                                    String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                    String tName = LocaleController.getInstance().getTranslitString(name);
                                    if (name.equals(tName)) {
                                        tName = null;
                                    }
                                    int found = 0;
                                    int length = search.length;
                                    int i = 0;
                                    while (i < length) {
                                        String q = search[i];
                                        if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                                            found = 1;
                                        } else if (user.username != null && user.username.startsWith(q)) {
                                            found = 2;
                                        }
                                        if (found != 0) {
                                            if (found == 1) {
                                                resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                            } else {
                                                resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, null, "@" + q));
                                            }
                                            resultArray.add(participant);
                                        } else {
                                            i++;
                                        }
                                    }
                                }
                            }
                            SearchAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                        }
                    });
                }
            });
        }

        private void updateSearchResults(final ArrayList<ChatParticipant> users, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    SearchAdapter.this.searchResult = users;
                    SearchAdapter.this.searchResultNames = names;
                    SearchAdapter.this.notifyDataSetChanged();
                }
            });
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public int getCount() {
            return this.searchResult.size();
        }

        public ChatParticipant getItem(int i) {
            return (ChatParticipant) this.searchResult.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z;
            boolean z2 = false;
            if (view == null) {
                view = new UserCell(this.mContext, 1, 2, false);
            }
            ChatParticipant participant = getItem(i);
            User user = MessagesController.getInstance().getUser(Integer.valueOf(participant.user_id));
            String un = user.username;
            CharSequence username = null;
            CharSequence name = null;
            if (i < this.searchResult.size()) {
                name = (CharSequence) this.searchResultNames.get(i);
                if (name != null && un != null && un.length() > 0 && name.toString().startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            }
            UserCell userCell = (UserCell) view;
            userCell.setData(user, name, username, 0);
            SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
            if ((participant instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) {
                z = false;
            } else {
                z = true;
            }
            userCell.setChecked(z, false);
            if (SetAdminsActivity.this.chat == null || !SetAdminsActivity.this.chat.admins_enabled || participant.user_id == UserConfig.getClientUserId()) {
                z2 = true;
            }
            userCell.setCheckDisabled(z2);
            return view;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return this.searchResult.isEmpty();
        }
    }

    public SetAdminsActivity(Bundle args) {
        super(args);
        this.chat_id = args.getInt("chat_id");
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", R.string.SetAdminsTitle));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    SetAdminsActivity.this.finishFragment();
                }
            }
        });
        this.searchItem = this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                SetAdminsActivity.this.searching = true;
                SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
            }

            public void onSearchCollapse() {
                SetAdminsActivity.this.searching = false;
                SetAdminsActivity.this.searchWas = false;
                if (SetAdminsActivity.this.listView != null) {
                    SetAdminsActivity.this.listView.setEmptyView(null);
                    SetAdminsActivity.this.searchEmptyView.setVisibility(8);
                    if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter) {
                        SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
                        SetAdminsActivity.this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
                    }
                }
                if (SetAdminsActivity.this.searchAdapter != null) {
                    SetAdminsActivity.this.searchAdapter.search(null);
                }
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    SetAdminsActivity.this.searchWas = true;
                    if (!(SetAdminsActivity.this.searchAdapter == null || SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)) {
                        SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.searchAdapter);
                        SetAdminsActivity.this.fragmentView.setBackgroundColor(-1);
                    }
                    if (!(SetAdminsActivity.this.searchEmptyView == null || SetAdminsActivity.this.listView.getEmptyView() == SetAdminsActivity.this.searchEmptyView)) {
                        SetAdminsActivity.this.searchEmptyView.showTextView();
                        SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.searchEmptyView);
                    }
                }
                if (SetAdminsActivity.this.searchAdapter != null) {
                    SetAdminsActivity.this.searchAdapter.search(text);
                }
            }
        });
        this.searchItem.getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        this.listAdapter = new ListAdapter(context);
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.listView = new ListView(context);
        this.listView.setDivider(null);
        this.listView.setDividerHeight(0);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setDrawSelectorOnTop(true);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter || (i >= SetAdminsActivity.this.usersStartRow && i < SetAdminsActivity.this.usersEndRow)) {
                    ChatParticipant participant;
                    UserCell userCell = (UserCell) view;
                    SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                    int index = -1;
                    if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                        participant = SetAdminsActivity.this.searchAdapter.getItem(i);
                        for (int a = 0; a < SetAdminsActivity.this.participants.size(); a++) {
                            if (((ChatParticipant) SetAdminsActivity.this.participants.get(a)).user_id == participant.user_id) {
                                index = a;
                                break;
                            }
                        }
                    } else {
                        index = i - SetAdminsActivity.this.usersStartRow;
                        participant = (ChatParticipant) SetAdminsActivity.this.participants.get(index);
                    }
                    if (index != -1 && !(participant instanceof TL_chatParticipantCreator)) {
                        ChatParticipant newParticipant;
                        if (participant instanceof TL_chatParticipant) {
                            newParticipant = new TL_chatParticipantAdmin();
                            newParticipant.user_id = participant.user_id;
                            newParticipant.date = participant.date;
                            newParticipant.inviter_id = participant.inviter_id;
                        } else {
                            newParticipant = new TL_chatParticipant();
                            newParticipant.user_id = participant.user_id;
                            newParticipant.date = participant.date;
                            newParticipant.inviter_id = participant.inviter_id;
                        }
                        SetAdminsActivity.this.participants.set(index, newParticipant);
                        index = SetAdminsActivity.this.info.participants.participants.indexOf(participant);
                        if (index != -1) {
                            SetAdminsActivity.this.info.participants.participants.set(index, newParticipant);
                        }
                        if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) {
                            SetAdminsActivity.this.searchAdapter.searchResult.set(i, newParticipant);
                        }
                        participant = newParticipant;
                        boolean z = ((participant instanceof TL_chatParticipant) && (SetAdminsActivity.this.chat == null || SetAdminsActivity.this.chat.admins_enabled)) ? false : true;
                        userCell.setChecked(z, true);
                        if (SetAdminsActivity.this.chat != null && SetAdminsActivity.this.chat.admins_enabled) {
                            MessagesController instance = MessagesController.getInstance();
                            int access$1100 = SetAdminsActivity.this.chat_id;
                            int i2 = participant.user_id;
                            if (participant instanceof TL_chatParticipant) {
                                z = false;
                            } else {
                                z = true;
                            }
                            instance.toggleUserAdmin(access$1100, i2, z);
                        }
                    }
                } else if (i == SetAdminsActivity.this.allAdminsRow) {
                    SetAdminsActivity.this.chat = MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id));
                    if (SetAdminsActivity.this.chat != null) {
                        SetAdminsActivity.this.chat.admins_enabled = !SetAdminsActivity.this.chat.admins_enabled;
                        ((TextCheckCell) view).setChecked(!SetAdminsActivity.this.chat.admins_enabled);
                        MessagesController.getInstance().toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
                    }
                }
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.searchEmptyView.showTextView();
        updateRowsIds();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chat_id) {
                this.info = chatFull;
                updateChatParticipants();
                updateRowsIds();
            }
        }
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) && this.listView != null) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(mask);
                    }
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

    public void setChatInfo(ChatFull chatParticipants) {
        this.info = chatParticipants;
        updateChatParticipants();
    }

    private int getChatAdminParticipantType(ChatParticipant participant) {
        if (participant instanceof TL_chatParticipantCreator) {
            return 0;
        }
        if (participant instanceof TL_chatParticipantAdmin) {
            return 1;
        }
        return 2;
    }

    private void updateChatParticipants() {
        if (this.info != null && this.participants.size() != this.info.participants.participants.size()) {
            this.participants.clear();
            this.participants.addAll(this.info.participants.participants);
            try {
                Collections.sort(this.participants, new Comparator<ChatParticipant>() {
                    public int compare(ChatParticipant lhs, ChatParticipant rhs) {
                        int type1 = SetAdminsActivity.this.getChatAdminParticipantType(lhs);
                        int type2 = SetAdminsActivity.this.getChatAdminParticipantType(rhs);
                        if (type1 > type2) {
                            return 1;
                        }
                        if (type1 < type2) {
                            return -1;
                        }
                        if (type1 == type2) {
                            User user1 = MessagesController.getInstance().getUser(Integer.valueOf(rhs.user_id));
                            User user2 = MessagesController.getInstance().getUser(Integer.valueOf(lhs.user_id));
                            int status1 = 0;
                            int status2 = 0;
                            if (!(user1 == null || user1.status == null)) {
                                status1 = user1.status.expires;
                            }
                            if (!(user2 == null || user2.status == null)) {
                                status2 = user2.status.expires;
                            }
                            if (status1 <= 0 || status2 <= 0) {
                                if (status1 >= 0 || status2 >= 0) {
                                    if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                                        return -1;
                                    }
                                    if (status2 < 0 && status1 > 0) {
                                        return 1;
                                    }
                                    if (status2 == 0 && status1 != 0) {
                                        return 1;
                                    }
                                } else if (status1 > status2) {
                                    return 1;
                                } else {
                                    if (status1 < status2) {
                                        return -1;
                                    }
                                    return 0;
                                }
                            } else if (status1 > status2) {
                                return 1;
                            } else {
                                if (status1 < status2) {
                                    return -1;
                                }
                                return 0;
                            }
                        }
                        return 0;
                    }
                });
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
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
}
