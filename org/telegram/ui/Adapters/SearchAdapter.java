package org.telegram.ui.Adapters;

import android.content.Context;
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
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;

public class SearchAdapter extends BaseFragmentAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowUsernameSearch;
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<User> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, HashMap<Integer, User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots) {
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
        this.searchAdapterHelper = new SearchAdapterHelper();
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public void onDataSetChanged() {
                SearchAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
            }
        });
    }

    public void setCheckedMap(HashMap<Integer, ?> map) {
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
            FileLog.e("tmessages", e);
        }
        if (query == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                this.searchAdapterHelper.queryServerSearch(null, this.allowChats, this.allowBots);
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
                    FileLog.e("tmessages", e);
                }
                SearchAdapter.this.processSearch(query);
            }
        }, 200, 300);
    }

    private void processSearch(final String query) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (SearchAdapter.this.allowUsernameSearch) {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(query, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots);
                }
                final ArrayList<TL_contact> contactsCopy = new ArrayList();
                contactsCopy.addAll(ContactsController.getInstance().contacts);
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
                        ArrayList<User> resultArray = new ArrayList();
                        ArrayList<CharSequence> resultArrayNames = new ArrayList();
                        for (int a = 0; a < contactsCopy.size(); a++) {
                            User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) contactsCopy.get(a)).user_id));
                            if (user.id != UserConfig.getClientUserId() && (!SearchAdapter.this.onlyMutual || user.mutual_contact)) {
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

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int i) {
        return i != this.searchResult.size();
    }

    public int getCount() {
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

    public long getItemId(int i) {
        return (long) i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (i != this.searchResult.size()) {
            if (view == null) {
                if (this.useUserCell) {
                    view = new UserCell(this.mContext, 1, 1, false);
                    if (this.checkedMap != null) {
                        ((UserCell) view).setChecked(false, false);
                    }
                } else {
                    view = new ProfileSearchCell(this.mContext);
                }
            }
            TLObject object = getItem(i);
            if (object == null) {
                return view;
            }
            int id = 0;
            String un = null;
            if (object instanceof User) {
                un = ((User) object).username;
                id = ((User) object).id;
            } else if (object instanceof Chat) {
                un = ((Chat) object).username;
                id = ((Chat) object).id;
            }
            CharSequence username = null;
            CharSequence name = null;
            if (i < this.searchResult.size()) {
                name = (CharSequence) this.searchResultNames.get(i);
                if (name != null && un != null && un.length() > 0 && name.toString().startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            } else if (i > this.searchResult.size() && un != null) {
                String foundUserName = this.searchAdapterHelper.getLastFoundUsername();
                if (foundUserName.startsWith("@")) {
                    foundUserName = foundUserName.substring(1);
                }
                try {
                    username = AndroidUtilities.replaceTags(String.format("<c#ff4d83b3>@%s</c>%s", new Object[]{un.substring(0, foundUserName.length()), un.substring(foundUserName.length())}));
                } catch (Throwable e) {
                    Object username2 = un;
                    FileLog.e("tmessages", e);
                }
            }
            if (this.useUserCell) {
                ((UserCell) view).setData(object, name, username, 0);
                if (this.checkedMap == null) {
                    return view;
                }
                ((UserCell) view).setChecked(this.checkedMap.containsKey(Integer.valueOf(id)), false);
                return view;
            }
            ((ProfileSearchCell) view).setData(object, null, name, username, false);
            ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
            boolean z = (i == getCount() + -1 || i == this.searchResult.size() - 1) ? false : true;
            profileSearchCell.useSeparator = z;
            if (this.ignoreUsers == null) {
                return view;
            }
            if (this.ignoreUsers.containsKey(Integer.valueOf(id))) {
                ((ProfileSearchCell) view).drawAlpha = 0.5f;
                return view;
            }
            ((ProfileSearchCell) view).drawAlpha = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            return view;
        } else if (view != null) {
            return view;
        } else {
            view = new GreySectionCell(this.mContext);
            ((GreySectionCell) view).setText(LocaleController.getString("GlobalSearch", R.string.GlobalSearch));
            return view;
        }
    }

    public int getItemViewType(int i) {
        if (i == this.searchResult.size()) {
            return 1;
        }
        return 0;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean isEmpty() {
        return this.searchResult.isEmpty() && this.searchAdapterHelper.getGlobalSearch().isEmpty();
    }
}
