package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
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
    private long channelId;
    private LongSparseArray<?> checkedMap;
    /* access modifiers changed from: private */
    public LongSparseArray<TLRPC.User> ignoreUsers;
    private Context mContext;
    private boolean onlyMutual;
    private SearchAdapterHelper searchAdapterHelper;
    private boolean searchInProgress;
    private int searchPointer;
    private int searchReqId;
    private ArrayList<Object> searchResult = new ArrayList<>();
    private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
    /* access modifiers changed from: private */
    public Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, LongSparseArray<TLRPC.User> arg1, boolean usernameSearch, boolean mutual, boolean chats, boolean bots, boolean self, boolean phones, int searchChannelId) {
        this.mContext = context;
        this.ignoreUsers = arg1;
        this.onlyMutual = mutual;
        this.allowUsernameSearch = usernameSearch;
        this.allowChats = chats;
        this.allowBots = bots;
        this.channelId = (long) searchChannelId;
        this.allowSelf = self;
        this.allowPhoneNumbers = phones;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
            }

            public void onDataSetChanged(int searchId) {
                SearchAdapter.this.notifyDataSetChanged();
                if (searchId != 0) {
                    SearchAdapter.this.onSearchProgressChanged();
                }
            }

            public LongSparseArray<TLRPC.User> getExcludeUsers() {
                return SearchAdapter.this.ignoreUsers;
            }
        });
    }

    public void setCheckedMap(LongSparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setUseUserCell(boolean value) {
        this.useUserCell = value;
    }

    public void searchDialogs(final String query) {
        try {
            Timer timer = this.searchTimer;
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        this.searchResult.clear();
        this.searchResultNames.clear();
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch((String) null, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, 0, 0);
        }
        notifyDataSetChanged();
        if (!TextUtils.isEmpty(query)) {
            Timer timer2 = new Timer();
            this.searchTimer = timer2;
            timer2.schedule(new TimerTask() {
                public void run() {
                    try {
                        SearchAdapter.this.searchTimer.cancel();
                        Timer unused = SearchAdapter.this.searchTimer = null;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    SearchAdapter.this.processSearch(query);
                }
            }, 200, 300);
        }
    }

    /* access modifiers changed from: private */
    public void processSearch(String query) {
        AndroidUtilities.runOnUIThread(new SearchAdapter$$ExternalSyntheticLambda1(this, query));
    }

    /* renamed from: lambda$processSearch$1$org-telegram-ui-Adapters-SearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2642lambda$processSearch$1$orgtelegramuiAdaptersSearchAdapter(String query) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(query, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, -1, 1);
        }
        int currentAccount = UserConfig.selectedAccount;
        ArrayList<TLRPC.TL_contact> contactsCopy = new ArrayList<>(ContactsController.getInstance(currentAccount).contacts);
        this.searchInProgress = true;
        int i = this.searchPointer;
        this.searchPointer = i + 1;
        this.searchReqId = i;
        Utilities.searchQueue.postRunnable(new SearchAdapter$$ExternalSyntheticLambda2(this, query, this.searchReqId, contactsCopy, currentAccount));
    }

    /* renamed from: lambda$processSearch$0$org-telegram-ui-Adapters-SearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2641lambda$processSearch$0$orgtelegramuiAdaptersSearchAdapter(String query, int searchReqIdFinal, ArrayList contactsCopy, int currentAccount) {
        String[] search;
        String search1;
        String search2;
        int found;
        int i = searchReqIdFinal;
        String search12 = query.trim().toLowerCase();
        if (search12.length() == 0) {
            updateSearchResults(i, new ArrayList(), new ArrayList());
            return;
        }
        String search22 = LocaleController.getInstance().getTranslitString(search12);
        if (search12.equals(search22) || search22.length() == 0) {
            search22 = null;
        }
        char c = 0;
        char c2 = 1;
        String[] search3 = new String[((search22 != null ? 1 : 0) + 1)];
        search3[0] = search12;
        if (search22 != null) {
            search3[1] = search22;
        }
        ArrayList<Object> resultArray = new ArrayList<>();
        ArrayList<CharSequence> resultArrayNames = new ArrayList<>();
        int a = 0;
        while (a < contactsCopy.size()) {
            TLRPC.TL_contact contact = (TLRPC.TL_contact) contactsCopy.get(a);
            TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(contact.user_id));
            if ((this.allowSelf || !user.self) && (!this.onlyMutual || user.mutual_contact)) {
                LongSparseArray<TLRPC.User> longSparseArray = this.ignoreUsers;
                if (longSparseArray == null || longSparseArray.indexOfKey(contact.user_id) < 0) {
                    String[] names = new String[3];
                    names[c] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    names[c2] = LocaleController.getInstance().getTranslitString(names[c]);
                    if (names[c].equals(names[c2])) {
                        names[c2] = null;
                    }
                    if (UserObject.isReplyUser(user)) {
                        names[2] = LocaleController.getString("RepliesTitle", NUM).toLowerCase();
                    } else if (user.self) {
                        names[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                    }
                    int found2 = false;
                    int length = search3.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            search1 = search12;
                            search2 = search22;
                            int i3 = found2;
                            search = search3;
                            break;
                        }
                        String q = search3[i2];
                        search1 = search12;
                        int i4 = 0;
                        while (true) {
                            search2 = search22;
                            if (i4 >= names.length) {
                                search = search3;
                                break;
                            }
                            String name = names[i4];
                            if (name != null) {
                                if (name.startsWith(q)) {
                                    search = search3;
                                    break;
                                }
                                found = found2;
                                StringBuilder sb = new StringBuilder();
                                search = search3;
                                sb.append(" ");
                                sb.append(q);
                                if (name.contains(sb.toString())) {
                                    break;
                                }
                            } else {
                                found = found2;
                                search = search3;
                            }
                            i4++;
                            search22 = search2;
                            found2 = found;
                            search3 = search;
                        }
                        found2 = 1;
                        if (found2 == 0 && user.username != null && user.username.startsWith(q)) {
                            found2 = 2;
                        }
                        if (found2 != 0) {
                            if (found2 == 1) {
                                resultArrayNames.add(AndroidUtilities.generateSearchName(user.first_name, user.last_name, q));
                            } else {
                                resultArrayNames.add(AndroidUtilities.generateSearchName("@" + user.username, (String) null, "@" + q));
                            }
                            resultArray.add(user);
                        } else {
                            i2++;
                            search22 = search2;
                            search12 = search1;
                            search3 = search;
                        }
                    }
                } else {
                    search1 = search12;
                    search2 = search22;
                    search = search3;
                }
            } else {
                search1 = search12;
                search2 = search22;
                search = search3;
            }
            a++;
            search22 = search2;
            search12 = search1;
            search3 = search;
            c = 0;
            c2 = 1;
        }
        updateSearchResults(i, resultArray, resultArrayNames);
    }

    private void updateSearchResults(int searchReqIdFinal, ArrayList<Object> users, ArrayList<CharSequence> names) {
        AndroidUtilities.runOnUIThread(new SearchAdapter$$ExternalSyntheticLambda0(this, searchReqIdFinal, users, names));
    }

    /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-Adapters-SearchAdapter  reason: not valid java name */
    public /* synthetic */ void m2643x2CLASSNAMEe2(int searchReqIdFinal, ArrayList users, ArrayList names) {
        if (searchReqIdFinal == this.searchReqId) {
            this.searchResult = users;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(users);
            this.searchInProgress = false;
            notifyDataSetChanged();
            onSearchProgressChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void onSearchProgressChanged() {
    }

    public boolean searchInProgress() {
        return this.searchInProgress || this.searchAdapterHelper.isSearchInProgress();
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int type = holder.getItemViewType();
        return type == 0 || type == 2;
    }

    public int getItemCount() {
        int count = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        if (globalCount != 0) {
            count += globalCount + 1;
        }
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (phoneCount != 0) {
            return count + phoneCount;
        }
        return count;
    }

    public boolean isGlobalSearch(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return false;
        }
        if ((i <= localCount || i >= localCount + phoneCount) && i > localCount + phoneCount && i <= globalCount + phoneCount + localCount) {
            return true;
        }
        return false;
    }

    public Object getItem(int i) {
        int localCount = this.searchResult.size();
        int globalCount = this.searchAdapterHelper.getGlobalSearch().size();
        int phoneCount = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < localCount) {
            return this.searchResult.get(i);
        }
        int i2 = i - localCount;
        if (i2 >= 0 && i2 < phoneCount) {
            return this.searchAdapterHelper.getPhoneSearch().get(i2);
        }
        int i3 = i2 - phoneCount;
        if (i3 <= 0 || i3 > globalCount) {
            return null;
        }
        return this.searchAdapterHelper.getGlobalSearch().get(i3 - 1);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                if (!this.useUserCell) {
                    view = new ProfileSearchCell(this.mContext);
                    break;
                } else {
                    UserCell userCell = new UserCell(this.mContext, 1, 1, false);
                    if (this.checkedMap != null) {
                        userCell.setChecked(false, false);
                    }
                    view = userCell;
                    break;
                }
            case 1:
                view = new GraySectionCell(this.mContext);
                break;
            default:
                view = new TextCell(this.mContext, 16, false);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v5, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v1, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v4, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v11, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v20, resolved type: java.lang.String} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x015e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r21, int r22) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r3 = r22
            int r0 = r21.getItemViewType()
            r4 = 0
            r5 = 1
            switch(r0) {
                case 0: goto L_0x0072;
                case 1: goto L_0x004c;
                case 2: goto L_0x0011;
                default: goto L_0x000f;
            }
        L_0x000f:
            goto L_0x0197
        L_0x0011:
            java.lang.Object r0 = r1.getItem(r3)
            java.lang.String r0 = (java.lang.String) r0
            android.view.View r6 = r2.itemView
            org.telegram.ui.Cells.TextCell r6 = (org.telegram.ui.Cells.TextCell) r6
            r7 = 0
            java.lang.String r8 = "windowBackgroundWhiteBlueText2"
            r6.setColors(r7, r8)
            r7 = 2131624260(0x7f0e0144, float:1.8875695E38)
            java.lang.Object[] r5 = new java.lang.Object[r5]
            org.telegram.PhoneFormat.PhoneFormat r8 = org.telegram.PhoneFormat.PhoneFormat.getInstance()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "+"
            r9.append(r10)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            java.lang.String r8 = r8.format(r9)
            r5[r4] = r8
            java.lang.String r8 = "AddContactByPhone"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatString(r8, r7, r5)
            r6.setText(r5, r4)
            goto L_0x0197
        L_0x004c:
            android.view.View r0 = r2.itemView
            org.telegram.ui.Cells.GraySectionCell r0 = (org.telegram.ui.Cells.GraySectionCell) r0
            java.lang.Object r4 = r1.getItem(r3)
            if (r4 != 0) goto L_0x0064
            r4 = 2131626079(0x7f0e085f, float:1.8879384E38)
            java.lang.String r5 = "GlobalSearch"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0197
        L_0x0064:
            r4 = 2131627500(0x7f0e0dec, float:1.8882266E38)
            java.lang.String r5 = "PhoneNumberSearch"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setText(r4)
            goto L_0x0197
        L_0x0072:
            java.lang.Object r0 = r1.getItem(r3)
            r13 = r0
            org.telegram.tgnet.TLObject r13 = (org.telegram.tgnet.TLObject) r13
            if (r13 == 0) goto L_0x0197
            r6 = 0
            r0 = 0
            r8 = 0
            boolean r9 = r13 instanceof org.telegram.tgnet.TLRPC.User
            if (r9 == 0) goto L_0x0096
            r9 = r13
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC.User) r9
            java.lang.String r0 = r9.username
            r9 = r13
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC.User) r9
            long r6 = r9.id
            r9 = r13
            org.telegram.tgnet.TLRPC$User r9 = (org.telegram.tgnet.TLRPC.User) r9
            boolean r8 = r9.self
            r14 = r0
            r11 = r6
            r15 = r8
            goto L_0x00ab
        L_0x0096:
            boolean r9 = r13 instanceof org.telegram.tgnet.TLRPC.Chat
            if (r9 == 0) goto L_0x00a8
            r9 = r13
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC.Chat) r9
            java.lang.String r0 = r9.username
            r9 = r13
            org.telegram.tgnet.TLRPC$Chat r9 = (org.telegram.tgnet.TLRPC.Chat) r9
            long r6 = r9.id
            r14 = r0
            r11 = r6
            r15 = r8
            goto L_0x00ab
        L_0x00a8:
            r14 = r0
            r11 = r6
            r15 = r8
        L_0x00ab:
            r6 = 0
            r7 = 0
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r0 = r0.size()
            java.lang.String r8 = "@"
            if (r3 >= r0) goto L_0x00e7
            java.util.ArrayList<java.lang.CharSequence> r0 = r1.searchResultNames
            java.lang.Object r0 = r0.get(r3)
            r7 = r0
            java.lang.CharSequence r7 = (java.lang.CharSequence) r7
            if (r7 == 0) goto L_0x013f
            if (r14 == 0) goto L_0x013f
            int r0 = r14.length()
            if (r0 <= 0) goto L_0x013f
            java.lang.String r0 = r7.toString()
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r8)
            r9.append(r14)
            java.lang.String r8 = r9.toString()
            boolean r0 = r0.startsWith(r8)
            if (r0 == 0) goto L_0x013f
            r6 = r7
            r7 = 0
            r0 = r6
            goto L_0x0140
        L_0x00e7:
            java.util.ArrayList<java.lang.Object> r0 = r1.searchResult
            int r0 = r0.size()
            if (r3 <= r0) goto L_0x013f
            if (r14 == 0) goto L_0x013f
            org.telegram.ui.Adapters.SearchAdapterHelper r0 = r1.searchAdapterHelper
            java.lang.String r0 = r0.getLastFoundUsername()
            if (r0 == 0) goto L_0x0105
            boolean r9 = r0.startsWith(r8)
            if (r9 == 0) goto L_0x0105
            java.lang.String r0 = r0.substring(r5)
            r9 = r0
            goto L_0x0106
        L_0x0105:
            r9 = r0
        L_0x0106:
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder     // Catch:{ Exception -> 0x0138 }
            r0.<init>()     // Catch:{ Exception -> 0x0138 }
            r0.append(r8)     // Catch:{ Exception -> 0x0138 }
            r0.append(r14)     // Catch:{ Exception -> 0x0138 }
            if (r9 == 0) goto L_0x0136
            int r8 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r14, r9)     // Catch:{ Exception -> 0x0138 }
            r10 = r8
            r5 = -1
            if (r8 == r5) goto L_0x0136
            int r5 = r9.length()     // Catch:{ Exception -> 0x0138 }
            if (r10 != 0) goto L_0x0124
            int r5 = r5 + 1
            goto L_0x0126
        L_0x0124:
            int r10 = r10 + 1
        L_0x0126:
            org.telegram.ui.Components.ForegroundColorSpanThemable r8 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0138 }
            java.lang.String r4 = "windowBackgroundWhiteBlueText4"
            r8.<init>(r4)     // Catch:{ Exception -> 0x0138 }
            int r4 = r10 + r5
            r17 = r5
            r5 = 33
            r0.setSpan(r8, r10, r4, r5)     // Catch:{ Exception -> 0x0138 }
        L_0x0136:
            r6 = r0
            goto L_0x0140
        L_0x0138:
            r0 = move-exception
            r6 = r14
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = r6
            goto L_0x0140
        L_0x013f:
            r0 = r6
        L_0x0140:
            boolean r4 = r1.useUserCell
            if (r4 == 0) goto L_0x015e
            android.view.View r4 = r2.itemView
            org.telegram.ui.Cells.UserCell r4 = (org.telegram.ui.Cells.UserCell) r4
            r5 = 0
            r4.setData(r13, r7, r0, r5)
            androidx.collection.LongSparseArray<?> r5 = r1.checkedMap
            if (r5 == 0) goto L_0x015d
            int r5 = r5.indexOfKey(r11)
            if (r5 < 0) goto L_0x0158
            r5 = 1
            goto L_0x0159
        L_0x0158:
            r5 = 0
        L_0x0159:
            r10 = 0
            r4.setChecked(r5, r10)
        L_0x015d:
            goto L_0x0197
        L_0x015e:
            r10 = 0
            android.view.View r4 = r2.itemView
            org.telegram.ui.Cells.ProfileSearchCell r4 = (org.telegram.ui.Cells.ProfileSearchCell) r4
            if (r15 == 0) goto L_0x0170
            r5 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r6 = "SavedMessages"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r6, r5)
            r5 = r7
            goto L_0x0171
        L_0x0170:
            r5 = r7
        L_0x0171:
            r8 = 0
            r16 = 0
            r6 = r4
            r7 = r13
            r9 = r5
            r17 = 0
            r10 = r0
            r18 = r11
            r11 = r16
            r12 = r15
            r6.setData(r7, r8, r9, r10, r11, r12)
            int r6 = r20.getItemCount()
            r7 = 1
            int r6 = r6 - r7
            if (r3 == r6) goto L_0x0194
            java.util.ArrayList<java.lang.Object> r6 = r1.searchResult
            int r6 = r6.size()
            int r6 = r6 - r7
            if (r3 == r6) goto L_0x0194
            goto L_0x0195
        L_0x0194:
            r7 = 0
        L_0x0195:
            r4.useSeparator = r7
        L_0x0197:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
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
