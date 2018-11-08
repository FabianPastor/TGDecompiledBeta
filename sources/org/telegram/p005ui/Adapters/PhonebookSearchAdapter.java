package org.telegram.p005ui.Adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.C0216PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.Cells.UserCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.Adapters.PhonebookSearchAdapter */
public class PhonebookSearchAdapter extends SelectionAdapter {
    private Context mContext;
    private ArrayList<Object> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;

    public PhonebookSearchAdapter(Context context) {
        this.mContext = context;
    }

    public void search(final String query) {
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
                    PhonebookSearchAdapter.this.searchTimer.cancel();
                    PhonebookSearchAdapter.this.searchTimer = null;
                } catch (Throwable e) {
                    FileLog.m14e(e);
                }
                PhonebookSearchAdapter.this.processSearch(query);
            }
        }, 200, 300);
    }

    private void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new PhonebookSearchAdapter$$Lambda$0(this, query));
    }

    final /* synthetic */ void lambda$processSearch$1$PhonebookSearchAdapter(String query) {
        int currentAccount = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new PhonebookSearchAdapter$$Lambda$2(this, query, new ArrayList(ContactsController.getInstance(currentAccount).contactsBook.values()), new ArrayList(ContactsController.getInstance(currentAccount).contacts), currentAccount));
    }

    final /* synthetic */ void lambda$null$0$PhonebookSearchAdapter(String query, ArrayList contactsCopy, ArrayList contactsCopy2, int currentAccount) {
        String search1 = query.trim().toLowerCase();
        if (search1.length() == 0) {
            updateSearchResults(query, new ArrayList(), new ArrayList());
            return;
        }
        int a;
        String name;
        String tName;
        int found;
        int length;
        int i;
        String q;
        String search2 = LocaleController.getInstance().getTranslitString(search1);
        if (search1.equals(search2) || search2.length() == 0) {
            search2 = null;
        }
        String[] search = new String[((search2 != null ? 1 : 0) + 1)];
        search[0] = search1;
        if (search2 != null) {
            search[1] = search2;
        }
        ArrayList<Object> resultArray = new ArrayList();
        ArrayList<CharSequence> resultArrayNames = new ArrayList();
        SparseBooleanArray foundUids = new SparseBooleanArray();
        for (a = 0; a < contactsCopy.size(); a++) {
            String name2;
            Contact contact = (Contact) contactsCopy.get(a);
            name = ContactsController.formatName(contact.first_name, contact.last_name).toLowerCase();
            tName = LocaleController.getInstance().getTranslitString(name);
            String tName2;
            if (contact.user != null) {
                name2 = ContactsController.formatName(contact.user.first_name, contact.user.last_name).toLowerCase();
                tName2 = LocaleController.getInstance().getTranslitString(name);
            } else {
                name2 = null;
                tName2 = null;
            }
            if (name.equals(tName)) {
                tName = null;
            }
            found = 0;
            length = search.length;
            i = 0;
            while (i < length) {
                q = search[i];
                if ((name2 != null && (name2.startsWith(q) || name2.contains(" " + q))) || (tName2 != null && (tName2.startsWith(q) || tName2.contains(" " + q)))) {
                    found = 1;
                } else if (contact.user != null && contact.user.username != null && contact.user.username.startsWith(q)) {
                    found = 2;
                } else if (name.startsWith(q) || name.contains(" " + q) || (tName != null && (tName.startsWith(q) || tName.contains(" " + q)))) {
                    found = 3;
                }
                if (found != 0) {
                    if (found == 3) {
                        resultArrayNames.add(AndroidUtilities.generateSearchName(contact.first_name, contact.last_name, q));
                    } else if (found == 1) {
                        resultArrayNames.add(AndroidUtilities.generateSearchName(contact.user.first_name, contact.user.last_name, q));
                    } else {
                        resultArrayNames.add(AndroidUtilities.generateSearchName("@" + contact.user.username, null, "@" + q));
                    }
                    if (contact.user != null) {
                        foundUids.put(contact.user.f228id, true);
                    }
                    resultArray.add(contact);
                } else {
                    i++;
                }
            }
        }
        for (a = 0; a < contactsCopy2.size(); a++) {
            TL_contact contact2 = (TL_contact) contactsCopy2.get(a);
            if (foundUids.indexOfKey(contact2.user_id) < 0) {
                User user = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(contact2.user_id));
                name = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                tName = LocaleController.getInstance().getTranslitString(name);
                if (name.equals(tName)) {
                    tName = null;
                }
                found = 0;
                length = search.length;
                i = 0;
                while (i < length) {
                    q = search[i];
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
        updateSearchResults(query, resultArray, resultArrayNames);
    }

    protected void onUpdateSearchResults(String query) {
    }

    private void updateSearchResults(String query, ArrayList<Object> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new PhonebookSearchAdapter$$Lambda$1(this, query, users, names));
    }

    final /* synthetic */ void lambda$updateSearchResults$2$PhonebookSearchAdapter(String query, ArrayList users, ArrayList names) {
        onUpdateSearchResults(query);
        this.searchResult = users;
        this.searchResultNames = names;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.searchResult.size();
    }

    public Object getItem(int i) {
        return this.searchResult.get(i);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new UserCell(this.mContext, 8, 0, false);
        ((UserCell) view).setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            UserCell userCell = holder.itemView;
            Contact object = getItem(position);
            User user = null;
            if (object instanceof Contact) {
                Contact contact = object;
                if (contact.user != null) {
                    user = contact.user;
                } else {
                    userCell.setCurrentId(contact.contact_id);
                    userCell.setData(null, (CharSequence) this.searchResultNames.get(position), contact.phones.isEmpty() ? TtmlNode.ANONYMOUS_REGION_ID : C0216PhoneFormat.getInstance().format((String) contact.phones.get(0)), 0);
                }
            } else {
                user = (User) object;
            }
            if (user != null) {
                userCell.setData(user, (CharSequence) this.searchResultNames.get(position), C0216PhoneFormat.getInstance().format("+" + user.phone), 0);
            }
        }
    }

    public boolean isEnabled(ViewHolder holder) {
        return true;
    }

    public int getItemViewType(int i) {
        return 0;
    }
}
