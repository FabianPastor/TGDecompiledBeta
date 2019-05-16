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
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
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
    private ArrayList<TLObject> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;
    private boolean useUserCell;

    public SearchAdapter(Context context, SparseArray<User> sparseArray, boolean z, boolean z2, boolean z3, boolean z4, int i) {
        this.mContext = context;
        this.ignoreUsers = sparseArray;
        this.onlyMutual = z2;
        this.allowUsernameSearch = z;
        this.allowChats = z3;
        this.allowBots = z4;
        this.channelId = i;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
            }

            public void onDataSetChanged() {
                SearchAdapter.this.notifyDataSetChanged();
            }

            public SparseArray<User> getExcludeUsers() {
                return SearchAdapter.this.ignoreUsers;
            }
        });
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
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (str == null) {
            this.searchResult.clear();
            this.searchResultNames.clear();
            if (this.allowUsernameSearch) {
                this.searchAdapterHelper.queryServerSearch(null, true, this.allowChats, this.allowBots, true, this.channelId, 0);
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
                } catch (Exception e) {
                    FileLog.e(e);
                }
                SearchAdapter.this.processSearch(str);
            }
        }, 200, 300);
    }

    private void processSearch(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapter$orWB0TdKMjyMiCeh3w5NvcjUVZU(this, str));
    }

    public /* synthetic */ void lambda$processSearch$1$SearchAdapter(String str) {
        if (this.allowUsernameSearch) {
            this.searchAdapterHelper.queryServerSearch(str, true, this.allowChats, this.allowBots, true, this.channelId, -1);
        }
        int i = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new -$$Lambda$SearchAdapter$MJ9cur0I3ZiqQGm3sZTS0MY0LdM(this, str, new ArrayList(ContactsController.getInstance(i).contacts), i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:54:0x0136 A:{LOOP_END, LOOP:1: B:33:0x00aa->B:54:0x0136} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x00f9 A:{SYNTHETIC} */
    /* JADX WARNING: Missing block: B:42:0x00e6, code skipped:
            if (r11.contains(r3.toString()) != false) goto L_0x00f6;
     */
    public /* synthetic */ void lambda$null$0$SearchAdapter(java.lang.String r18, java.util.ArrayList r19, int r20) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18.trim();
        r1 = r1.toLowerCase();
        r2 = r1.length();
        if (r2 != 0) goto L_0x001e;
    L_0x0010:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0.updateSearchResults(r1, r2);
        return;
    L_0x001e:
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r2 = r2.getTranslitString(r1);
        r3 = r1.equals(r2);
        if (r3 != 0) goto L_0x0032;
    L_0x002c:
        r3 = r2.length();
        if (r3 != 0) goto L_0x0033;
    L_0x0032:
        r2 = 0;
    L_0x0033:
        r3 = 0;
        r5 = 1;
        if (r2 == 0) goto L_0x0039;
    L_0x0037:
        r6 = 1;
        goto L_0x003a;
    L_0x0039:
        r6 = 0;
    L_0x003a:
        r6 = r6 + r5;
        r6 = new java.lang.String[r6];
        r6[r3] = r1;
        if (r2 == 0) goto L_0x0043;
    L_0x0041:
        r6[r5] = r2;
    L_0x0043:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r7 = 0;
    L_0x004e:
        r8 = r19.size();
        if (r7 >= r8) goto L_0x0142;
    L_0x0054:
        r8 = r19;
        r9 = r8.get(r7);
        r9 = (org.telegram.tgnet.TLRPC.TL_contact) r9;
        r10 = org.telegram.messenger.MessagesController.getInstance(r20);
        r11 = r9.user_id;
        r11 = java.lang.Integer.valueOf(r11);
        r10 = r10.getUser(r11);
        r11 = r10.id;
        r12 = org.telegram.messenger.UserConfig.getInstance(r20);
        r12 = r12.getClientUserId();
        if (r11 == r12) goto L_0x013c;
    L_0x0076:
        r11 = r0.onlyMutual;
        if (r11 == 0) goto L_0x007e;
    L_0x007a:
        r11 = r10.mutual_contact;
        if (r11 == 0) goto L_0x013c;
    L_0x007e:
        r11 = r0.ignoreUsers;
        if (r11 == 0) goto L_0x008c;
    L_0x0082:
        r9 = r9.user_id;
        r9 = r11.indexOfKey(r9);
        if (r9 < 0) goto L_0x008c;
    L_0x008a:
        goto L_0x013c;
    L_0x008c:
        r9 = r10.first_name;
        r11 = r10.last_name;
        r9 = org.telegram.messenger.ContactsController.formatName(r9, r11);
        r9 = r9.toLowerCase();
        r11 = org.telegram.messenger.LocaleController.getInstance();
        r11 = r11.getTranslitString(r9);
        r12 = r9.equals(r11);
        if (r12 == 0) goto L_0x00a7;
    L_0x00a6:
        r11 = 0;
    L_0x00a7:
        r12 = r6.length;
        r13 = 0;
        r14 = 0;
    L_0x00aa:
        if (r13 >= r12) goto L_0x013c;
    L_0x00ac:
        r15 = r6[r13];
        r16 = r9.startsWith(r15);
        if (r16 != 0) goto L_0x00f6;
    L_0x00b4:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = " ";
        r3.append(r4);
        r3.append(r15);
        r3 = r3.toString();
        r3 = r9.contains(r3);
        if (r3 != 0) goto L_0x00f6;
    L_0x00cb:
        if (r11 == 0) goto L_0x00e9;
    L_0x00cd:
        r3 = r11.startsWith(r15);
        if (r3 != 0) goto L_0x00f6;
    L_0x00d3:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3.append(r4);
        r3.append(r15);
        r3 = r3.toString();
        r3 = r11.contains(r3);
        if (r3 == 0) goto L_0x00e9;
    L_0x00e8:
        goto L_0x00f6;
    L_0x00e9:
        r3 = r10.username;
        if (r3 == 0) goto L_0x00f7;
    L_0x00ed:
        r3 = r3.startsWith(r15);
        if (r3 == 0) goto L_0x00f7;
    L_0x00f3:
        r3 = 2;
        r14 = 2;
        goto L_0x00f7;
    L_0x00f6:
        r14 = 1;
    L_0x00f7:
        if (r14 == 0) goto L_0x0136;
    L_0x00f9:
        if (r14 != r5) goto L_0x0108;
    L_0x00fb:
        r3 = r10.first_name;
        r4 = r10.last_name;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r4, r15);
        r2.add(r3);
        r15 = 0;
        goto L_0x0132;
    L_0x0108:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "@";
        r3.append(r4);
        r9 = r10.username;
        r3.append(r9);
        r3 = r3.toString();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r4);
        r9.append(r15);
        r4 = r9.toString();
        r15 = 0;
        r3 = org.telegram.messenger.AndroidUtilities.generateSearchName(r3, r15, r4);
        r2.add(r3);
    L_0x0132:
        r1.add(r10);
        goto L_0x013d;
    L_0x0136:
        r15 = 0;
        r13 = r13 + 1;
        r3 = 0;
        goto L_0x00aa;
    L_0x013c:
        r15 = 0;
    L_0x013d:
        r7 = r7 + 1;
        r3 = 0;
        goto L_0x004e;
    L_0x0142:
        r0.updateSearchResults(r1, r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.SearchAdapter.lambda$null$0$SearchAdapter(java.lang.String, java.util.ArrayList, int):void");
    }

    private void updateSearchResults(ArrayList<TLObject> arrayList, ArrayList<CharSequence> arrayList2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$SearchAdapter$6uwOh4k7ujlooXYRN4wvar_fqlek(this, arrayList, arrayList2));
    }

    public /* synthetic */ void lambda$updateSearchResults$2$SearchAdapter(ArrayList arrayList, ArrayList arrayList2) {
        this.searchResult = arrayList;
        this.searchResultNames = arrayList2;
        this.searchAdapterHelper.mergeResults(arrayList);
        notifyDataSetChanged();
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == 0;
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
            return (i <= size || i > size2 + size) ? null : (TLObject) this.searchAdapterHelper.getGlobalSearch().get((i - size) - 1);
        } else {
            return (TLObject) this.searchResult.get(i);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View graySectionCell;
        if (i != 0) {
            graySectionCell = new GraySectionCell(this.mContext);
            graySectionCell.setText(LocaleController.getString("GlobalSearch", NUM));
        } else if (this.useUserCell) {
            graySectionCell = new UserCell(this.mContext, 1, 1, false);
            if (this.checkedMap != null) {
                graySectionCell.setChecked(false, false);
            }
        } else {
            graySectionCell = new ProfileSearchCell(this.mContext);
        }
        return new Holder(graySectionCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            TLObject item = getItem(i);
            if (item != null) {
                String str;
                int i2;
                CharSequence charSequence;
                CharSequence charSequence2;
                boolean z = false;
                if (item instanceof User) {
                    User user = (User) item;
                    str = user.username;
                    i2 = user.id;
                } else if (item instanceof Chat) {
                    Chat chat = (Chat) item;
                    str = chat.username;
                    i2 = chat.id;
                } else {
                    str = null;
                    i2 = 0;
                }
                String str2 = "@";
                boolean z2 = true;
                if (i < this.searchResult.size()) {
                    charSequence = (CharSequence) this.searchResultNames.get(i);
                    if (!(charSequence == null || str == null || str.length() <= 0)) {
                        String charSequence3 = charSequence.toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str2);
                        stringBuilder.append(str);
                        if (charSequence3.startsWith(stringBuilder.toString())) {
                            charSequence2 = charSequence;
                            charSequence = null;
                        }
                    }
                    charSequence2 = null;
                } else if (i <= this.searchResult.size() || str == null) {
                    charSequence = null;
                    charSequence2 = charSequence;
                } else {
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (lastFoundUsername.startsWith(str2)) {
                        lastFoundUsername = lastFoundUsername.substring(1);
                    }
                    try {
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append(str2);
                        spannableStringBuilder.append(str);
                        int indexOf = str.toLowerCase().indexOf(lastFoundUsername);
                        if (indexOf != -1) {
                            int length = lastFoundUsername.length();
                            if (indexOf == 0) {
                                length++;
                            } else {
                                indexOf++;
                            }
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOf, length + indexOf, 33);
                        }
                        charSequence = null;
                        charSequence2 = spannableStringBuilder;
                    } catch (Exception e) {
                        FileLog.e(e);
                        charSequence = null;
                        charSequence2 = str;
                    }
                }
                if (this.useUserCell) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    userCell.setData(item, charSequence, charSequence2, 0);
                    SparseArray sparseArray = this.checkedMap;
                    if (sparseArray != null) {
                        if (sparseArray.indexOfKey(i2) < 0) {
                            z2 = false;
                        }
                        userCell.setChecked(z2, false);
                        return;
                    }
                    return;
                }
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(item, null, charSequence, charSequence2, false, false);
                if (!(i == getItemCount() - 1 || i == this.searchResult.size() - 1)) {
                    z = true;
                }
                profileSearchCell.useSeparator = z;
            }
        }
    }

    public int getItemViewType(int i) {
        return i == this.searchResult.size() ? 1 : 0;
    }
}
