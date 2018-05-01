package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
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
        public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
        }

        C18991() {
        }

        public void onDataSetChanged() {
            SearchAdapter.this.notifyDataSetChanged();
        }
    }

    public SearchAdapter(Context context, SparseArray<User> sparseArray, boolean z, boolean z2, boolean z3, boolean z4, int i) {
        this.mContext = context;
        this.ignoreUsers = sparseArray;
        this.onlyMutual = z2;
        this.allowUsernameSearch = z;
        this.allowChats = z3;
        this.allowBots = z4;
        this.channelId = i;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new C18991());
    }

    public void setCheckedMap(SparseArray<?> sparseArray) {
        this.checkedMap = sparseArray;
    }

    public void setUseUserCell(boolean z) {
        this.useUserCell = z;
    }

    public void searchDialogs(final String str) {
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
            if (this.allowUsernameSearch != null) {
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
                SearchAdapter.this.processSearch(str);
            }
        }, 200, 300);
    }

    private void processSearch(final String str) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (SearchAdapter.this.allowUsernameSearch) {
                    SearchAdapter.this.searchAdapterHelper.queryServerSearch(str, true, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots, true, SearchAdapter.this.channelId, false);
                }
                final int i = UserConfig.selectedAccount;
                final ArrayList arrayList = new ArrayList();
                arrayList.addAll(ContactsController.getInstance(i).contacts);
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
                            User user = MessagesController.getInstance(i).getUser(Integer.valueOf(((TL_contact) arrayList.get(i2)).user_id));
                            if (user.id != UserConfig.getInstance(i).getClientUserId()) {
                                if (!SearchAdapter.this.onlyMutual || user.mutual_contact) {
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
                                            StringBuilder stringBuilder4 = new StringBuilder();
                                            stringBuilder4.append(" ");
                                            stringBuilder4.append(str);
                                            if (!toLowerCase2.contains(stringBuilder4.toString())) {
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
                                                    arrayList.add(user);
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
                                            arrayList.add(user);
                                        }
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

    private void updateSearchResults(final ArrayList<User> arrayList, final ArrayList<CharSequence> arrayList2) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                SearchAdapter.this.searchResult = arrayList;
                SearchAdapter.this.searchResultNames = arrayList2;
                SearchAdapter.this.notifyDataSetChanged();
            }
        });
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return viewHolder.getAdapterPosition() != this.searchResult.size() ? true : null;
    }

    public int getItemCount() {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        return size2 != 0 ? size + (size2 + 1) : size;
    }

    public boolean isGlobalSearch(int i) {
        int size = this.searchResult.size();
        return (i < 0 || i >= size) && i > size && i <= this.searchAdapterHelper.getGlobalSearch().size() + size;
    }

    public TLObject getItem(int i) {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        if (i < 0 || i >= size) {
            return (i <= size || i > size2 + size) ? 0 : (TLObject) this.searchAdapterHelper.getGlobalSearch().get((i - size) - 1);
        } else {
            return (TLObject) this.searchResult.get(i);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i != 0) {
            viewGroup = new GraySectionCell(this.mContext);
            ((GraySectionCell) viewGroup).setText(LocaleController.getString("GlobalSearch", C0446R.string.GlobalSearch));
        } else if (this.useUserCell != null) {
            viewGroup = new UserCell(this.mContext, 1, 1, false);
            if (this.checkedMap != 0) {
                ((UserCell) viewGroup).setChecked(false, false);
            }
        } else {
            viewGroup = new ProfileSearchCell(this.mContext);
        }
        return new Holder(viewGroup);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            TLObject item = getItem(i);
            if (item != null) {
                CharSequence charSequence;
                int i2;
                CharSequence charSequence2;
                CharSequence charSequence3;
                UserCell userCell;
                ProfileSearchCell profileSearchCell;
                boolean z = false;
                if (item instanceof User) {
                    User user = (User) item;
                    charSequence = user.username;
                    i2 = user.id;
                } else if (item instanceof Chat) {
                    Chat chat = (Chat) item;
                    charSequence = chat.username;
                    i2 = chat.id;
                } else {
                    charSequence = null;
                    i2 = 0;
                }
                boolean z2 = true;
                if (i < this.searchResult.size()) {
                    charSequence2 = (CharSequence) this.searchResultNames.get(i);
                    if (!(charSequence2 == null || charSequence == null || charSequence.length() <= 0)) {
                        String charSequence4 = charSequence2.toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(charSequence);
                        if (charSequence4.startsWith(stringBuilder.toString())) {
                            charSequence3 = charSequence2;
                        }
                    }
                    charSequence3 = null;
                    if (this.useUserCell) {
                        userCell = (UserCell) viewHolder.itemView;
                        userCell.setData(item, charSequence2, charSequence3, 0);
                        if (this.checkedMap == 0) {
                            if (this.checkedMap.indexOfKey(i2) >= 0) {
                                z2 = false;
                            }
                            userCell.setChecked(z2, false);
                            return;
                        }
                        return;
                    }
                    profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                    profileSearchCell.setData(item, null, charSequence2, charSequence3, false, false);
                    if (!(i == getItemCount() - 1 || i == this.searchResult.size() - 1)) {
                        z = true;
                    }
                    profileSearchCell.useSeparator = z;
                } else if (i <= this.searchResult.size() || charSequence == null) {
                    charSequence2 = null;
                    charSequence3 = charSequence2;
                    if (this.useUserCell) {
                        profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                        profileSearchCell.setData(item, null, charSequence2, charSequence3, false, false);
                        z = true;
                        profileSearchCell.useSeparator = z;
                    }
                    userCell = (UserCell) viewHolder.itemView;
                    userCell.setData(item, charSequence2, charSequence3, 0);
                    if (this.checkedMap == 0) {
                        if (this.checkedMap.indexOfKey(i2) >= 0) {
                            z2 = false;
                        }
                        userCell.setChecked(z2, false);
                        return;
                    }
                    return;
                } else {
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (lastFoundUsername.startsWith("@")) {
                        lastFoundUsername = lastFoundUsername.substring(1);
                    }
                    try {
                        charSequence3 = new SpannableStringBuilder();
                        charSequence3.append("@");
                        charSequence3.append(charSequence);
                        int indexOf = charSequence.toLowerCase().indexOf(lastFoundUsername);
                        if (indexOf != -1) {
                            int length = lastFoundUsername.length();
                            if (indexOf == 0) {
                                length++;
                            } else {
                                indexOf++;
                            }
                            charSequence3.setSpan(new ForegroundColorSpan(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4)), indexOf, length + indexOf, 33);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                        charSequence2 = null;
                        charSequence3 = charSequence;
                    }
                }
                charSequence2 = null;
                if (this.useUserCell) {
                    userCell = (UserCell) viewHolder.itemView;
                    userCell.setData(item, charSequence2, charSequence3, 0);
                    if (this.checkedMap == 0) {
                        if (this.checkedMap.indexOfKey(i2) >= 0) {
                            z2 = false;
                        }
                        userCell.setChecked(z2, false);
                        return;
                    }
                    return;
                }
                profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(item, null, charSequence2, charSequence3, false, false);
                z = true;
                profileSearchCell.useSeparator = z;
            }
        }
    }

    public int getItemViewType(int i) {
        return i == this.searchResult.size() ? 1 : 0;
    }
}
