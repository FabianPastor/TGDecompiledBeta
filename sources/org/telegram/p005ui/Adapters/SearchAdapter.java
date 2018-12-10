package org.telegram.p005ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.p005ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.p005ui.Cells.GraySectionCell;
import org.telegram.p005ui.Cells.ProfileSearchCell;
import org.telegram.p005ui.Cells.UserCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Adapters.SearchAdapter */
public class SearchAdapter extends SelectionAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowUsernameSearch;
    private int channelId;
    private SparseArray<?> checkedMap;
    private SparseArray<User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLObject> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean useUserCell;

    /* renamed from: org.telegram.ui.Adapters.SearchAdapter$1 */
    class CLASSNAME implements SearchAdapterHelperDelegate {
        CLASSNAME() {
        }

        public void onDataSetChanged() {
            SearchAdapter.this.notifyDataSetChanged();
        }

        public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        }

        public SparseArray<User> getExcludeUsers() {
            return SearchAdapter.this.ignoreUsers;
        }
    }

    public SearchAdapter(Context context, SparseArray<User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots, int searchChannelId) {
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
        this.channelId = searchChannelId;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new CLASSNAME());
    }

    public void setCheckedMap(SparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setUseUserCell(boolean value) {
        this.useUserCell = value;
    }

    public void searchDialogs(final String query) {
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        if (query == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                this.searchAdapterHelper.queryServerSearch(null, true, this.allowChats, this.allowBots, true, this.channelId, false);
            }
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
                    FileLog.m13e(e);
                }
                SearchAdapter.this.processSearch(query);
            }
        }, 200, 300);
    }

    private void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new SearchAdapter$$Lambda$0(this, query));
    }

    final /* synthetic */ void lambda$processSearch$1$SearchAdapter(String query) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(query, true, this.allowChats, this.allowBots, true, this.channelId, false);
        }
        int currentAccount = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new SearchAdapter$$Lambda$2(this, query, new ArrayList(ContactsController.getInstance(currentAccount).contacts), currentAccount));
    }

    final /* synthetic */ void lambda$null$0$SearchAdapter(String query, ArrayList contactsCopy, int currentAccount) {
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
        ArrayList<TLObject> resultArray = new ArrayList();
        ArrayList<CharSequence> resultArrayNames = new ArrayList();
        for (int a = 0; a < contactsCopy.size(); a++) {
            TL_contact contact = (TL_contact) contactsCopy.get(a);
            User user = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(contact.user_id));
            if (user.f176id != UserConfig.getInstance(currentAccount).getClientUserId() && ((!this.onlyMutual || user.mutual_contact) && (this.ignoreUsers == null || this.ignoreUsers.indexOfKey(contact.user_id) < 0))) {
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
                        resultArray.add(user);
                    } else {
                        i++;
                    }
                }
            }
        }
        updateSearchResults(resultArray, resultArrayNames);
    }

    private void updateSearchResults(ArrayList<TLObject> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new SearchAdapter$$Lambda$1(this, users, names));
    }

    final /* synthetic */ void lambda$updateSearchResults$2$SearchAdapter(ArrayList users, ArrayList names) {
        this.searchResult = users;
        this.searchResultNames = names;
        this.searchAdapterHelper.mergeResults(users);
        notifyDataSetChanged();
    }

    public boolean isEnabled(ViewHolder holder) {
        return holder.getItemViewType() == 0;
    }

    public int getItemCount() {
        int count = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount != 0) {
            return count + (globalCount + 1);
        }
        return count;
    }

    public boolean isGlobalSearch(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if ((i < 0 || i >= localCount) && i > localCount && i <= globalCount + localCount) {
            return true;
        }
        return false;
    }

    public TLObject getItem(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (i >= 0 && i < localCount) {
            return (TLObject) this.searchResult.get(i);
        }
        if (i <= localCount || i > globalCount + localCount) {
            return null;
        }
        return (TLObject) this.searchAdapterHelper.getGlobalSearch().get((i - localCount) - 1);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                if (!this.useUserCell) {
                    view = new ProfileSearchCell(this.mContext);
                    break;
                }
                view = new UserCell(this.mContext, 1, 1, false);
                if (this.checkedMap != null) {
                    ((UserCell) view).setChecked(false, false);
                    break;
                }
                break;
            default:
                view = new GraySectionCell(this.mContext);
                ((GraySectionCell) view).setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
                break;
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            TLObject object = getItem(position);
            if (object != null) {
                int id = 0;
                String un = null;
                if (object instanceof User) {
                    un = ((User) object).username;
                    id = ((User) object).f176id;
                } else if (object instanceof Chat) {
                    un = ((Chat) object).username;
                    id = ((Chat) object).f78id;
                }
                CharSequence username = null;
                CharSequence name = null;
                if (position < this.searchResult.size()) {
                    name = (CharSequence) this.searchResultNames.get(position);
                    if (name != null && un != null && un.length() > 0 && name.toString().startsWith("@" + un)) {
                        username = name;
                        name = null;
                    }
                } else if (position > this.searchResult.size() && un != null) {
                    String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                    if (foundUserName.startsWith("@")) {
                        foundUserName = foundUserName.substring(1);
                    }
                    Object username2;
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("@");
                        spannableStringBuilder.append(un);
                        int index = un.toLowerCase().indexOf(foundUserName);
                        if (index != -1) {
                            int len = foundUserName.length();
                            if (index == 0) {
                                len++;
                            } else {
                                index++;
                            }
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + len, 33);
                        }
                        username2 = spannableStringBuilder;
                    } catch (Throwable e) {
                        username2 = un;
                        FileLog.m13e(e);
                    }
                }
                boolean z;
                if (this.useUserCell) {
                    UserCell userCell = (UserCell) holder.itemView;
                    userCell.setData(object, name, username2, 0);
                    if (this.checkedMap != null) {
                        if (this.checkedMap.indexOfKey(id) >= 0) {
                            z = true;
                        } else {
                            z = false;
                        }
                        userCell.setChecked(z, false);
                        return;
                    }
                    return;
                }
                ProfileSearchCell profileSearchCell = holder.itemView;
                profileSearchCell.setData(object, null, name, username2, false, false);
                z = (position == getItemCount() + -1 || position == this.searchResult.size() - 1) ? false : true;
                profileSearchCell.useSeparator = z;
            }
        }
    }

    public int getItemViewType(int i) {
        if (i == this.searchResult.size()) {
            return 1;
        }
        return 0;
    }
}
