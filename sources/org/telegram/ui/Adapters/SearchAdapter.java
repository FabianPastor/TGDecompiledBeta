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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
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

    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00f2, code lost:
        r15 = 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$0$SearchAdapter(java.lang.String r18, java.util.ArrayList r19, int r20) {
        /*
            r17 = this;
            r0 = r17
            java.lang.String r1 = r18.trim()
            java.lang.String r1 = r1.toLowerCase()
            int r2 = r1.length()
            if (r2 != 0) goto L_0x001e
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.updateSearchResults(r1, r2)
            return
        L_0x001e:
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            java.lang.String r2 = r2.getTranslitString(r1)
            boolean r3 = r1.equals(r2)
            r4 = 0
            if (r3 != 0) goto L_0x0033
            int r3 = r2.length()
            if (r3 != 0) goto L_0x0034
        L_0x0033:
            r2 = r4
        L_0x0034:
            r3 = 0
            r5 = 1
            if (r2 == 0) goto L_0x003a
            r6 = 1
            goto L_0x003b
        L_0x003a:
            r6 = 0
        L_0x003b:
            int r6 = r6 + r5
            java.lang.String[] r7 = new java.lang.String[r6]
            r7[r3] = r1
            if (r2 == 0) goto L_0x0044
            r7[r5] = r2
        L_0x0044:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r8 = 0
        L_0x004f:
            int r9 = r19.size()
            if (r8 >= r9) goto L_0x0159
            r9 = r19
            java.lang.Object r10 = r9.get(r8)
            org.telegram.tgnet.TLRPC$TL_contact r10 = (org.telegram.tgnet.TLRPC$TL_contact) r10
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r20)
            int r12 = r10.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r11 = r11.getUser(r12)
            boolean r12 = r0.allowSelf
            if (r12 != 0) goto L_0x0073
            boolean r12 = r11.self
            if (r12 != 0) goto L_0x0087
        L_0x0073:
            boolean r12 = r0.onlyMutual
            if (r12 == 0) goto L_0x007b
            boolean r12 = r11.mutual_contact
            if (r12 == 0) goto L_0x0087
        L_0x007b:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$User> r12 = r0.ignoreUsers
            if (r12 == 0) goto L_0x008b
            int r10 = r10.user_id
            int r10 = r12.indexOfKey(r10)
            if (r10 < 0) goto L_0x008b
        L_0x0087:
            r10 = r4
            r4 = 1
            goto L_0x0152
        L_0x008b:
            r10 = 3
            java.lang.String[] r12 = new java.lang.String[r10]
            java.lang.String r13 = r11.first_name
            java.lang.String r14 = r11.last_name
            java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r13, r14)
            java.lang.String r13 = r13.toLowerCase()
            r12[r3] = r13
            org.telegram.messenger.LocaleController r13 = org.telegram.messenger.LocaleController.getInstance()
            r14 = r12[r3]
            java.lang.String r13 = r13.getTranslitString(r14)
            r12[r5] = r13
            r13 = r12[r3]
            r14 = r12[r5]
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x00b4
            r12[r5] = r4
        L_0x00b4:
            boolean r13 = r11.self
            r14 = 2
            if (r13 == 0) goto L_0x00c8
            r13 = 2131626771(0x7f0e0b13, float:1.8880788E38)
            java.lang.String r15 = "SavedMessages"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r15, r13)
            java.lang.String r13 = r13.toLowerCase()
            r12[r14] = r13
        L_0x00c8:
            r13 = 0
            r15 = 0
        L_0x00ca:
            if (r13 >= r6) goto L_0x0087
            r3 = r7[r13]
            r14 = 0
        L_0x00cf:
            if (r14 >= r10) goto L_0x00fa
            r10 = r12[r14]
            if (r10 == 0) goto L_0x00f4
            boolean r16 = r10.startsWith(r3)
            if (r16 != 0) goto L_0x00f2
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = " "
            r4.append(r5)
            r4.append(r3)
            java.lang.String r4 = r4.toString()
            boolean r4 = r10.contains(r4)
            if (r4 == 0) goto L_0x00f4
        L_0x00f2:
            r15 = 1
            goto L_0x00fa
        L_0x00f4:
            int r14 = r14 + 1
            r4 = 0
            r5 = 1
            r10 = 3
            goto L_0x00cf
        L_0x00fa:
            if (r15 != 0) goto L_0x0107
            java.lang.String r4 = r11.username
            if (r4 == 0) goto L_0x0107
            boolean r4 = r4.startsWith(r3)
            if (r4 == 0) goto L_0x0107
            r15 = 2
        L_0x0107:
            if (r15 == 0) goto L_0x0147
            r4 = 1
            if (r15 != r4) goto L_0x0119
            java.lang.String r5 = r11.first_name
            java.lang.String r10 = r11.last_name
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
            r2.add(r3)
            r10 = 0
            goto L_0x0143
        L_0x0119:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r10 = "@"
            r5.append(r10)
            java.lang.String r12 = r11.username
            r5.append(r12)
            java.lang.String r5 = r5.toString()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            r12.append(r10)
            r12.append(r3)
            java.lang.String r3 = r12.toString()
            r10 = 0
            java.lang.CharSequence r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r5, r10, r3)
            r2.add(r3)
        L_0x0143:
            r1.add(r11)
            goto L_0x0152
        L_0x0147:
            r4 = 1
            r10 = 0
            int r13 = r13 + 1
            r4 = r10
            r3 = 0
            r5 = 1
            r10 = 3
            r14 = 2
            goto L_0x00ca
        L_0x0152:
            int r8 = r8 + 1
            r4 = r10
            r3 = 0
            r5 = 1
            goto L_0x004f
        L_0x0159:
            r0.updateSearchResults(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapter.lambda$null$0$SearchAdapter(java.lang.String, java.util.ArrayList, int):void");
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
