package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView;

public class SearchAdapter extends RecyclerListView.SelectionAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowPhoneNumbers;
    private boolean allowSelf;
    private boolean allowUsernameSearch;
    private int channelId;
    private SparseArray<?> checkedMap;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC$User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private ArrayList<TLObject> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, SparseArray<TLRPC$User> sparseArray, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i) {
        this.mContext = context;
        this.ignoreUsers = sparseArray;
        this.onlyMutual = z2;
        this.allowUsernameSearch = z;
        this.allowChats = z3;
        this.allowBots = z4;
        this.channelId = i;
        this.allowSelf = z5;
        this.allowPhoneNumbers = z6;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
            }

            public void onDataSetChanged(int i) {
                SearchAdapter.this.notifyDataSetChanged();
            }

            public SparseArray<TLRPC$User> getExcludeUsers() {
                return SearchAdapter.this.ignoreUsers;
            }
        });
    }

    public void searchDialogs(String str) {
        final String str2 = str;
        try {
            if (this.searchTimer != null) {
                this.searchTimer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        if (str2 == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                this.searchAdapterHelper.queryServerSearch((String) null, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, 0, 0);
            }
            notifyDataSetChanged();
            return;
        }
        Timer timer = new Timer();
        this.searchTimer = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                try {
                    SearchAdapter.this.searchTimer.cancel();
                    Timer unused = SearchAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                SearchAdapter.this.processSearch(str2);
            }
        }, 200, 300);
    }

    /* access modifiers changed from: private */
    public void processSearch(String str) {
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            public final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                SearchAdapter.this.lambda$processSearch$1$SearchAdapter(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$processSearch$1$SearchAdapter(String str) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(str, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, -1, 0);
        }
        int i = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new Runnable(str, new ArrayList(ContactsController.getInstance(i).contacts), i) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ ArrayList f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                SearchAdapter.this.lambda$null$0$SearchAdapter(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$SearchAdapter(String str, ArrayList arrayList, int i) {
        String str2;
        SparseArray<TLRPC$User> sparseArray;
        String str3;
        String lowerCase = str.trim().toLowerCase();
        if (lowerCase.length() == 0) {
            updateSearchResults(new ArrayList(), new ArrayList());
            return;
        }
        String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
        String str4 = null;
        if (lowerCase.equals(translitString) || translitString.length() == 0) {
            translitString = null;
        }
        char c = 0;
        char c2 = 1;
        int i2 = (translitString != null ? 1 : 0) + 1;
        String[] strArr = new String[i2];
        strArr[0] = lowerCase;
        if (translitString != null) {
            strArr[1] = translitString;
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int i3 = 0;
        while (i3 < arrayList.size()) {
            TLRPC$TL_contact tLRPC$TL_contact = (TLRPC$TL_contact) arrayList.get(i3);
            TLRPC$User user = MessagesController.getInstance(i).getUser(Integer.valueOf(tLRPC$TL_contact.user_id));
            if ((this.allowSelf || !user.self) && ((!this.onlyMutual || user.mutual_contact) && ((sparseArray = this.ignoreUsers) == null || sparseArray.indexOfKey(tLRPC$TL_contact.user_id) < 0))) {
                int i4 = 3;
                String[] strArr2 = new String[3];
                strArr2[c] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                strArr2[c2] = LocaleController.getInstance().getTranslitString(strArr2[c]);
                if (strArr2[c].equals(strArr2[c2])) {
                    strArr2[c2] = str4;
                }
                if (UserObject.isReplyUser(user)) {
                    strArr2[2] = LocaleController.getString("RepliesTitle", NUM).toLowerCase();
                } else if (user.self) {
                    strArr2[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                }
                int i5 = 0;
                char c3 = 0;
                while (true) {
                    if (i5 >= i2) {
                        break;
                    }
                    String str5 = strArr[i5];
                    int i6 = 0;
                    while (true) {
                        if (i6 >= i4) {
                            break;
                        }
                        String str6 = strArr2[i6];
                        if (str6 != null) {
                            if (str6.startsWith(str5)) {
                                break;
                            }
                            if (str6.contains(" " + str5)) {
                                break;
                            }
                        }
                        i6++;
                        i4 = 3;
                    }
                    c3 = 1;
                    if (c3 == 0 && (str3 = user.username) != null && str3.startsWith(str5)) {
                        c3 = 2;
                    }
                    if (c3 != 0) {
                        if (c3 == 1) {
                            arrayList3.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, str5));
                            str2 = null;
                        } else {
                            str2 = null;
                            arrayList3.add(AndroidUtilities.generateSearchName("@" + user.username, (String) null, "@" + str5));
                        }
                        arrayList2.add(user);
                    } else {
                        i5++;
                        str4 = null;
                        i4 = 3;
                    }
                }
                i3++;
                str4 = str2;
                c = 0;
                c2 = 1;
            }
            str2 = str4;
            i3++;
            str4 = str2;
            c = 0;
            c2 = 1;
        }
        updateSearchResults(arrayList2, arrayList3);
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2) {
        AndroidUtilities.runOnUIThread(new Runnable(arrayList, arrayList2) {
            public final /* synthetic */ ArrayList f$1;
            public final /* synthetic */ ArrayList f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                SearchAdapter.this.lambda$updateSearchResults$2$SearchAdapter(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$updateSearchResults$2$SearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
        this.searchResult = arrayList;
        this.searchResultNames = arrayList2;
        this.searchAdapterHelper.mergeResults(arrayList);
        notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 0 || itemViewType == 2;
    }

    public int getItemCount() {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        if (size2 != 0) {
            size += size2 + 1;
        }
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        return size3 != 0 ? size + size3 : size;
    }

    public boolean isGlobalSearch(int i) {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (i < 0 || i >= size) {
            return (i <= size || i >= size + size3) && i > size + size3 && i <= (size2 + size3) + size;
        }
        return false;
    }

    public Object getItem(int i) {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < size) {
            return this.searchResult.get(i);
        }
        int i2 = i - size;
        if (i2 >= 0 && i2 < size3) {
            return this.searchAdapterHelper.getPhoneSearch().get(i2);
        }
        int i3 = i2 - size3;
        if (i3 <= 0 || i3 > size2) {
            return null;
        }
        return this.searchAdapterHelper.getGlobalSearch().get(i3 - 1);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i != 0) {
            if (i != 1) {
                view = new TextCell(this.mContext, 16, false);
            } else {
                view = new GraySectionCell(this.mContext);
            }
        } else if (this.useUserCell) {
            UserCell userCell = new UserCell(this.mContext, 1, 1, false);
            if (this.checkedMap != null) {
                userCell.setChecked(false, false);
            }
            view = userCell;
        } else {
            view = new ProfileSearchCell(this.mContext);
        }
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        boolean z;
        int i2;
        String str;
        CharSequence charSequence;
        int itemViewType = viewHolder.getItemViewType();
        CharSequence charSequence2 = null;
        boolean z2 = false;
        boolean z3 = true;
        if (itemViewType == 0) {
            TLObject tLObject = (TLObject) getItem(i);
            if (tLObject != null) {
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    str = tLRPC$User.username;
                    i2 = tLRPC$User.id;
                    z = tLRPC$User.self;
                } else {
                    if (tLObject instanceof TLRPC$Chat) {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                        str = tLRPC$Chat.username;
                        i2 = tLRPC$Chat.id;
                    } else {
                        str = null;
                        i2 = 0;
                    }
                    z = false;
                }
                if (i < this.searchResult.size()) {
                    CharSequence charSequence3 = this.searchResultNames.get(i);
                    if (!(charSequence3 == null || str == null || str.length() <= 0)) {
                        if (charSequence3.toString().startsWith("@" + str)) {
                            charSequence = charSequence3;
                        }
                    }
                    charSequence = null;
                    charSequence2 = charSequence3;
                } else if (i <= this.searchResult.size() || str == null) {
                    charSequence = null;
                } else {
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (lastFoundUsername.startsWith("@")) {
                        lastFoundUsername = lastFoundUsername.substring(1);
                    }
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append("@");
                        spannableStringBuilder.append(str);
                        int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str, lastFoundUsername);
                        charSequence = spannableStringBuilder;
                        if (indexOfIgnoreCase != -1) {
                            int length = lastFoundUsername.length();
                            if (indexOfIgnoreCase == 0) {
                                length++;
                            } else {
                                indexOfIgnoreCase++;
                            }
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOfIgnoreCase, length + indexOfIgnoreCase, 33);
                            charSequence = spannableStringBuilder;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        charSequence = str;
                    }
                }
                if (this.useUserCell) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    userCell.setData(tLObject, charSequence2, charSequence, 0);
                    SparseArray<?> sparseArray = this.checkedMap;
                    if (sparseArray != null) {
                        if (sparseArray.indexOfKey(i2) < 0) {
                            z3 = false;
                        }
                        userCell.setChecked(z3, false);
                        return;
                    }
                    return;
                }
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(tLObject, (TLRPC$EncryptedChat) null, z ? LocaleController.getString("SavedMessages", NUM) : charSequence2, charSequence, false, z);
                if (!(i == getItemCount() - 1 || i == this.searchResult.size() - 1)) {
                    z2 = true;
                }
                profileSearchCell.useSeparator = z2;
            }
        } else if (itemViewType == 1) {
            GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
            if (getItem(i) == null) {
                graySectionCell.setText(LocaleController.getString("GlobalSearch", NUM));
            } else {
                graySectionCell.setText(LocaleController.getString("PhoneNumberSearch", NUM));
            }
        } else if (itemViewType == 2) {
            TextCell textCell = (TextCell) viewHolder.itemView;
            textCell.setColors((String) null, "windowBackgroundWhiteBlueText2");
            textCell.setText(LocaleController.formatString("AddContactByPhone", NUM, PhoneFormat.getInstance().format("+" + ((String) getItem(i)))), false);
        }
    }

    public int getItemViewType(int i) {
        Object item = getItem(i);
        if (item == null) {
            return 1;
        }
        if (!(item instanceof String)) {
            return 0;
        }
        if ("section".equals((String) item)) {
            return 1;
        }
        return 2;
    }
}
