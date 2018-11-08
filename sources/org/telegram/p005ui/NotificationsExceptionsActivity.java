package org.telegram.p005ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.C0704ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.ProfileSearchCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.NotificationsSettingsActivity.NotificationException;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.NotificationsExceptionsActivity */
public class NotificationsExceptionsActivity extends BaseFragment {
    private static final int search_button = 0;
    private EmptyTextProgressView emptyView;
    ArrayList<NotificationException> exceptions;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    /* renamed from: org.telegram.ui.NotificationsExceptionsActivity$1 */
    class C18761 extends ActionBarMenuOnItemClick {
        C18761() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                NotificationsExceptionsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsExceptionsActivity$2 */
    class C18772 extends ActionBarMenuItemSearchListener {
        C18772() {
        }

        public void onSearchExpand() {
            NotificationsExceptionsActivity.this.searching = true;
            NotificationsExceptionsActivity.this.emptyView.setShowAtCenter(true);
        }

        public void onSearchCollapse() {
            NotificationsExceptionsActivity.this.searchListViewAdapter.searchDialogs(null);
            NotificationsExceptionsActivity.this.searching = false;
            NotificationsExceptionsActivity.this.searchWas = false;
            NotificationsExceptionsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", C0541R.string.NoExceptions));
            NotificationsExceptionsActivity.this.listView.setAdapter(NotificationsExceptionsActivity.this.listViewAdapter);
            NotificationsExceptionsActivity.this.listViewAdapter.notifyDataSetChanged();
            NotificationsExceptionsActivity.this.listView.setFastScrollVisible(true);
            NotificationsExceptionsActivity.this.listView.setVerticalScrollBarEnabled(false);
            NotificationsExceptionsActivity.this.emptyView.setShowAtCenter(false);
        }

        public void onTextChanged(EditText editText) {
            if (NotificationsExceptionsActivity.this.searchListViewAdapter != null) {
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    NotificationsExceptionsActivity.this.searchWas = true;
                    if (NotificationsExceptionsActivity.this.listView != null) {
                        NotificationsExceptionsActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0541R.string.NoResult));
                        NotificationsExceptionsActivity.this.listView.setAdapter(NotificationsExceptionsActivity.this.searchListViewAdapter);
                        NotificationsExceptionsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        NotificationsExceptionsActivity.this.listView.setFastScrollVisible(false);
                        NotificationsExceptionsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                }
                NotificationsExceptionsActivity.this.searchListViewAdapter.searchDialogs(text);
            }
        }
    }

    /* renamed from: org.telegram.ui.NotificationsExceptionsActivity$3 */
    class C18783 extends OnScrollListener {
        C18783() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && NotificationsExceptionsActivity.this.searching && NotificationsExceptionsActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(NotificationsExceptionsActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /* renamed from: org.telegram.ui.NotificationsExceptionsActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2 || type == 6;
        }

        public int getItemCount() {
            return NotificationsExceptionsActivity.this.exceptions.isEmpty() ? 0 : NotificationsExceptionsActivity.this.exceptions.size() + 1;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ProfileSearchCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0541R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    String text;
                    ProfileSearchCell cell = holder.itemView;
                    NotificationException exception = (NotificationException) NotificationsExceptionsActivity.this.exceptions.get(position);
                    boolean custom = exception.hasCustom;
                    int value = exception.notify;
                    int delta = exception.muteUntil;
                    if (value != 3 || delta == Integer.MAX_VALUE) {
                        boolean enabled;
                        if (value == 0) {
                            enabled = true;
                        } else if (value == 1) {
                            enabled = true;
                        } else if (value == 2) {
                            enabled = false;
                        } else {
                            enabled = false;
                        }
                        if (enabled && custom) {
                            text = LocaleController.getString("NotificationsCustom", C0541R.string.NotificationsCustom);
                        } else if (enabled) {
                            text = LocaleController.getString("NotificationsUnmuted", C0541R.string.NotificationsUnmuted);
                        } else {
                            text = LocaleController.getString("NotificationsMuted", C0541R.string.NotificationsMuted);
                        }
                    } else {
                        delta -= ConnectionsManager.getInstance(NotificationsExceptionsActivity.this.currentAccount).getCurrentTime();
                        if (delta <= 0) {
                            if (custom) {
                                text = LocaleController.getString("NotificationsCustom", C0541R.string.NotificationsCustom);
                            } else {
                                text = LocaleController.getString("NotificationsUnmuted", C0541R.string.NotificationsUnmuted);
                            }
                        } else if (delta < 3600) {
                            text = LocaleController.formatString("WillUnmuteIn", C0541R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                        } else if (delta < 86400) {
                            text = LocaleController.formatString("WillUnmuteIn", C0541R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / 60.0f) / 60.0f))));
                        } else if (delta < 31536000) {
                            text = LocaleController.formatString("WillUnmuteIn", C0541R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / 60.0f) / 60.0f) / 24.0f))));
                        } else {
                            text = null;
                        }
                    }
                    if (text == null) {
                        text = LocaleController.getString("NotificationsOff", C0541R.string.NotificationsOff);
                    }
                    int lower_id = (int) exception.did;
                    int high_id = (int) (exception.did >> 32);
                    if (lower_id == 0) {
                        EncryptedChat encryptedChat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                        if (encryptedChat != null) {
                            TLObject user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                            if (user != null) {
                                cell.setData(user, encryptedChat, null, text, false, false);
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (lower_id > 0) {
                        User user2 = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                        if (user2 != null) {
                            cell.setData(user2, null, null, text, false, false);
                            return;
                        }
                        return;
                    } else {
                        Chat chat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        if (chat != null) {
                            cell.setData(chat, null, null, text, false, false);
                            return;
                        }
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position < 0 || position >= NotificationsExceptionsActivity.this.exceptions.size()) {
                return 1;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.NotificationsExceptionsActivity$SearchAdapter */
    private class SearchAdapter extends SelectionAdapter {
        private Context mContext;
        private ArrayList<NotificationException> searchResult = new ArrayList();
        private ArrayList<CharSequence> searchResultNames = new ArrayList();
        private Timer searchTimer;

        public SearchAdapter(Context context) {
            this.mContext = context;
        }

        public void searchDialogs(final String query) {
            try {
                if (this.searchTimer != null) {
                    this.searchTimer.cancel();
                }
            } catch (Throwable e) {
                FileLog.m14e(e);
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
                        FileLog.m14e(e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }

        private void processSearch(String query) {
            AndroidUtilities.runOnUIThread(new NotificationsExceptionsActivity$SearchAdapter$$Lambda$0(this, query));
        }

        /* renamed from: lambda$processSearch$1$NotificationsExceptionsActivity$SearchAdapter */
        final /* synthetic */ void mo17737xf1435ea1(String query) {
            Utilities.searchQueue.postRunnable(new NotificationsExceptionsActivity$SearchAdapter$$Lambda$2(this, query, new ArrayList(NotificationsExceptionsActivity.this.exceptions)));
        }

        final /* synthetic */ void lambda$null$0$NotificationsExceptionsActivity$SearchAdapter(String query, ArrayList contactsCopy) {
            String search1 = query.trim().toLowerCase();
            if (search1.length() == 0) {
                updateSearchResults(new ArrayList(), new ArrayList());
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
            ArrayList<NotificationException> resultArray = new ArrayList();
            ArrayList<CharSequence> resultArrayNames = new ArrayList();
            String[] names = new String[2];
            for (int a = 0; a < contactsCopy.size(); a++) {
                NotificationException exception = (NotificationException) contactsCopy.get(a);
                int lower_id = (int) exception.did;
                int high_id = (int) (exception.did >> 32);
                User user;
                if (lower_id == 0) {
                    EncryptedChat encryptedChat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                    if (encryptedChat != null) {
                        user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                        if (user != null) {
                            names[0] = ContactsController.formatName(user.first_name, user.last_name);
                            names[1] = user.username;
                        }
                    }
                } else if (lower_id > 0) {
                    user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                    if (user != null) {
                        names[0] = ContactsController.formatName(user.first_name, user.last_name);
                        names[1] = user.username;
                    }
                } else {
                    Chat chat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                    if (chat != null) {
                        if (!(chat.left || chat.kicked || chat.migrated_to != null)) {
                            names[0] = chat.title;
                            names[1] = chat.username;
                        }
                    }
                }
                String originalName = names[0];
                names[0] = names[0].toLowerCase();
                String tName = LocaleController.getInstance().getTranslitString(names[0]);
                if (names[0] != null && names[0].equals(tName)) {
                    tName = null;
                }
                int found = 0;
                int b = 0;
                while (b < search.length) {
                    String q = search[b];
                    if ((names[0] != null && (names[0].startsWith(q) || names[0].contains(" " + q))) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                        found = 1;
                    } else if (names[1] != null && names[1].startsWith(q)) {
                        found = 2;
                    }
                    if (found != 0) {
                        if (found == 1) {
                            resultArrayNames.add(AndroidUtilities.generateSearchName(originalName, null, q));
                        } else {
                            resultArrayNames.add(AndroidUtilities.generateSearchName("@" + names[1], null, "@" + q));
                        }
                        resultArray.add(exception);
                    } else {
                        b++;
                    }
                }
            }
            updateSearchResults(resultArray, resultArrayNames);
        }

        private void updateSearchResults(ArrayList<NotificationException> users, ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new NotificationsExceptionsActivity$SearchAdapter$$Lambda$1(this, users, names));
        }

        /* renamed from: lambda$updateSearchResults$2$NotificationsExceptionsActivity$SearchAdapter */
        final /* synthetic */ void mo17738x28a7e090(ArrayList users, ArrayList names) {
            this.searchResult = users;
            this.searchResultNames = names;
            notifyDataSetChanged();
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public int getItemCount() {
            return this.searchResult.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ProfileSearchCell(this.mContext);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            EncryptedChat encryptedChat;
            User user;
            Chat chat;
            ProfileSearchCell cell = holder.itemView;
            NotificationException exception = (NotificationException) this.searchResult.get(position);
            int lower_id = (int) exception.did;
            int high_id = (int) (exception.did >> 32);
            String un = null;
            if (lower_id == 0) {
                encryptedChat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user != null) {
                        un = user.username;
                    }
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                if (user != null) {
                    un = user.username;
                }
            } else {
                chat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (chat != null) {
                    un = chat.username;
                }
            }
            CharSequence name = (CharSequence) this.searchResultNames.get(position);
            CharSequence username = null;
            if (name != null && un != null && un.length() > 0 && name.toString().startsWith("@" + un)) {
                username = name;
                name = null;
            }
            if (lower_id == 0) {
                encryptedChat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                if (encryptedChat != null) {
                    TLObject user2 = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id));
                    if (user2 != null) {
                        cell.setData(user2, encryptedChat, name, username, false, false);
                    }
                }
            } else if (lower_id > 0) {
                user = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                if (user != null) {
                    cell.setData(user, null, name, username, false, false);
                }
            } else {
                chat = MessagesController.getInstance(NotificationsExceptionsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                if (chat != null) {
                    cell.setData(chat, null, name, username, false, false);
                }
            }
        }

        public int getItemViewType(int i) {
            return 0;
        }
    }

    public NotificationsExceptionsActivity(ArrayList<NotificationException> arrayList) {
        this.exceptions = arrayList;
    }

    public View createView(Context context) {
        int i = 1;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0541R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", C0541R.string.NotificationsExceptions));
        this.actionBar.setActionBarMenuOnItemClick(new C18761());
        this.searchListViewAdapter = new SearchAdapter(context);
        this.actionBar.createMenu().addItem(0, (int) C0541R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C18772()).getSearchField().setHint(LocaleController.getString("Search", C0541R.string.Search));
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", C0541R.string.NoExceptions));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new NotificationsExceptionsActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new C18783());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$NotificationsExceptionsActivity(View view, int position) {
        ArrayList<NotificationException> arrayList;
        if (this.listView.getAdapter() == this.searchListViewAdapter) {
            arrayList = this.searchListViewAdapter.searchResult;
        } else {
            arrayList = this.exceptions;
        }
        if (position >= 0 && position < arrayList.size()) {
            NotificationException exception = (NotificationException) arrayList.get(position);
            AlertsCreator.showCustomNotificationsDialog(this, exception.did, this.currentAccount, new NotificationsExceptionsActivity$$Lambda$2(this, arrayList, exception, position));
        }
    }

    final /* synthetic */ void lambda$null$0$NotificationsExceptionsActivity(ArrayList arrayList, NotificationException exception, int position, int param) {
        if (param == 0) {
            if (arrayList != this.exceptions) {
                int index = this.exceptions.indexOf(exception);
                if (index >= 0) {
                    this.exceptions.remove(index);
                    this.listViewAdapter.notifyItemRemoved(index);
                }
            }
            arrayList.remove(exception);
            if (arrayList.isEmpty() && arrayList == this.exceptions) {
                this.listView.getAdapter().notifyItemRemoved(1);
            }
            this.listView.getAdapter().notifyItemRemoved(position);
            return;
        }
        SharedPreferences preferences = MessagesController.getNotificationsSettings(this.currentAccount);
        exception.hasCustom = preferences.getBoolean("custom_" + exception.did, false);
        exception.notify = preferences.getInt("notify2_" + exception.did, 0);
        if (exception.notify != 0) {
            int time = preferences.getInt("notifyuntil_" + exception.did, -1);
            if (time != -1) {
                exception.muteUntil = time;
            }
        }
        this.listView.getAdapter().notifyItemChanged(position);
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new NotificationsExceptionsActivity$$Lambda$1(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[22];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ProfileSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[15] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[16] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[17] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$2$NotificationsExceptionsActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
