package org.telegram.ui.Adapters;

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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

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
    private ArrayList<User> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean useUserCell;

    /* renamed from: org.telegram.ui.Adapters.SearchAdapter$1 */
    class C18991 implements SearchAdapterHelperDelegate {
        C18991() {
        }

        public void onDataSetChanged() {
            SearchAdapter.this.notifyDataSetChanged();
        }

        public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
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
        this.searchAdapterHelper.setDelegate(new C18991());
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
            FileLog.m3e(e);
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
                    FileLog.m3e(e);
                }
                SearchAdapter.this.processSearch(query);
            }
        }, 200, 300);
    }

    private void processSearch(final String query) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (SearchAdapter.this.allowUsernameSearch) {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(query, true, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots, true, SearchAdapter.this.channelId, false);
                }
                final int currentAccount = UserConfig.selectedAccount;
                final ArrayList<TL_contact> contactsCopy = new ArrayList();
                contactsCopy.addAll(ContactsController.getInstance(currentAccount).contacts);
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
                        int i = 0;
                        String[] search = new String[((search2 != null ? 1 : 0) + 1)];
                        search[0] = search1;
                        if (search2 != null) {
                            search[1] = search2;
                        }
                        ArrayList<User> resultArray = new ArrayList();
                        ArrayList<CharSequence> resultArrayNames = new ArrayList();
                        int a = 0;
                        while (a < contactsCopy.size()) {
                            String str;
                            User user = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(((TL_contact) contactsCopy.get(a)).user_id));
                            if (user.id != UserConfig.getInstance(currentAccount).getClientUserId()) {
                                if (!SearchAdapter.this.onlyMutual || user.mutual_contact) {
                                    String name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                                    String tName = LocaleController.getInstance().getTranslitString(name);
                                    if (name.equals(tName)) {
                                        tName = null;
                                    }
                                    int length = search.length;
                                    int found = 0;
                                    int found2 = i;
                                    while (found2 < length) {
                                        StringBuilder stringBuilder;
                                        int found3;
                                        String stringBuilder2;
                                        StringBuilder stringBuilder3;
                                        String q = search[found2];
                                        if (name.startsWith(q)) {
                                            str = search1;
                                        } else {
                                            stringBuilder = new StringBuilder();
                                            str = search1;
                                            stringBuilder.append(" ");
                                            stringBuilder.append(q);
                                            if (!name.contains(stringBuilder.toString())) {
                                                if (tName != null) {
                                                    if (!tName.startsWith(q)) {
                                                        StringBuilder stringBuilder4 = new StringBuilder();
                                                        stringBuilder4.append(" ");
                                                        stringBuilder4.append(q);
                                                        if (tName.contains(stringBuilder4.toString())) {
                                                        }
                                                    }
                                                }
                                                if (user.username != null && user.username.startsWith(q)) {
                                                    found3 = 2;
                                                    found = found3;
                                                }
                                                if (found == 0) {
                                                    if (found != 1) {
                                                        resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                                    } else {
                                                        stringBuilder = new StringBuilder();
                                                        stringBuilder.append("@");
                                                        stringBuilder.append(user.username);
                                                        stringBuilder2 = stringBuilder.toString();
                                                        stringBuilder3 = new StringBuilder();
                                                        stringBuilder3.append("@");
                                                        stringBuilder3.append(q);
                                                        resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                                    }
                                                    resultArray.add(user);
                                                    a++;
                                                    search1 = str;
                                                    i = 0;
                                                } else {
                                                    found2++;
                                                    search1 = str;
                                                }
                                            }
                                        }
                                        found3 = 1;
                                        found = found3;
                                        if (found == 0) {
                                            found2++;
                                            search1 = str;
                                        } else {
                                            if (found != 1) {
                                                stringBuilder = new StringBuilder();
                                                stringBuilder.append("@");
                                                stringBuilder.append(user.username);
                                                stringBuilder2 = stringBuilder.toString();
                                                stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("@");
                                                stringBuilder3.append(q);
                                                resultArrayNames.add(AndroidUtilities.generateSearchName(stringBuilder2, null, stringBuilder3.toString()));
                                            } else {
                                                resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                                            }
                                            resultArray.add(user);
                                            a++;
                                            search1 = str;
                                            i = 0;
                                        }
                                    }
                                } else {
                                    str = search1;
                                    a++;
                                    search1 = str;
                                    i = 0;
                                }
                            }
                            str = search1;
                            a++;
                            search1 = str;
                            i = 0;
                        }
                        SearchAdapter.this.updateSearchResults(resultArray, resultArrayNames);
                    }
                });
            }
        });
    }

    private void updateSearchResults(final ArrayList<User> users, final ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SearchAdapter.this.searchResult = users;
                SearchAdapter.this.searchResultNames = names;
                SearchAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public boolean isEnabled(ViewHolder holder) {
        return holder.getAdapterPosition() != this.searchResult.size();
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
        if (viewType != 0) {
            view = new GraySectionCell(this.mContext);
            ((GraySectionCell) view).setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
        } else if (this.useUserCell) {
            view = new UserCell(this.mContext, 1, 1, false);
            if (this.checkedMap != null) {
                ((UserCell) view).setChecked(false, false);
            }
        } else {
            view = new ProfileSearchCell(this.mContext);
        }
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        SearchAdapter searchAdapter = this;
        ViewHolder viewHolder = holder;
        int i = position;
        if (holder.getItemViewType() == 0) {
            TLObject object = getItem(i);
            if (object != null) {
                int id = 0;
                String un = null;
                if (object instanceof User) {
                    un = ((User) object).username;
                    id = ((User) object).id;
                } else if (object instanceof Chat) {
                    un = ((Chat) object).username;
                    id = ((Chat) object).id;
                }
                int id2 = id;
                String un2 = un;
                CharSequence username = null;
                CharSequence name = null;
                String charSequence;
                if (i < searchAdapter.searchResult.size()) {
                    name = (CharSequence) searchAdapter.searchResultNames.get(i);
                    if (!(name == null || un2 == null || un2.length() <= 0)) {
                        charSequence = name.toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(un2);
                        if (charSequence.startsWith(stringBuilder.toString())) {
                            username = name;
                            name = null;
                        }
                    }
                } else if (i > searchAdapter.searchResult.size() && un2 != null) {
                    charSequence = searchAdapter.searchAdapterHelper.getLastFoundUsername();
                    if (charSequence.startsWith("@")) {
                        charSequence = charSequence.substring(1);
                    }
                    Object username2;
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("@");
                        spannableStringBuilder.append(un2);
                        int indexOf = un2.toLowerCase().indexOf(charSequence);
                        int index = indexOf;
                        if (indexOf != -1) {
                            indexOf = charSequence.length();
                            if (index == 0) {
                                indexOf++;
                            } else {
                                index++;
                            }
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), index, index + indexOf, 33);
                        }
                        username2 = spannableStringBuilder;
                    } catch (Throwable e) {
                        username2 = un2;
                        FileLog.m3e(e);
                    }
                }
                CharSequence username3 = username;
                CharSequence name2 = name;
                if (searchAdapter.useUserCell) {
                    UserCell userCell = viewHolder.itemView;
                    userCell.setData(object, name2, username3, 0);
                    if (searchAdapter.checkedMap != null) {
                        userCell.setChecked(searchAdapter.checkedMap.indexOfKey(id2) >= 0, false);
                    }
                    return;
                }
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                ProfileSearchCell profileSearchCell2 = profileSearchCell;
                boolean z = false;
                profileSearchCell.setData(object, null, name2, username3, false, false);
                boolean z2 = true;
                if (i == getItemCount() - 1 || i == searchAdapter.searchResult.size() - 1) {
                    z2 = z;
                }
                profileSearchCell2.useSeparator = z2;
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
