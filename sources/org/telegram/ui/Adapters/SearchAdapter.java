package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
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
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class SearchAdapter extends SelectionAdapter {
    private boolean allowBots;
    private boolean allowChats;
    private boolean allowPhoneNumbers;
    private boolean allowSelf;
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

    public SearchAdapter(Context context, SparseArray<User> sparseArray, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6, int i) {
        this.mContext = context;
        this.ignoreUsers = sparseArray;
        this.onlyMutual = z2;
        this.allowUsernameSearch = z;
        this.allowChats = z3;
        this.allowBots = z4;
        this.channelId = i;
        this.allowSelf = z5;
        this.allowPhoneNumbers = z6;
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return -CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                -CC.$default$onSetHashtags(this, arrayList, hashMap);
            }

            public void onDataSetChanged(int i) {
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
                this.searchAdapterHelper.queryServerSearch(null, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, 0, 0);
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
            this.searchAdapterHelper.queryServerSearch(str, true, this.allowChats, this.allowBots, this.allowSelf, false, this.channelId, this.allowPhoneNumbers, -1, 0);
        }
        int i = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new -$$Lambda$SearchAdapter$MJ9cur0I3ZiqQGm3sZTS0MY0LdM(this, str, new ArrayList(ContactsController.getInstance(i).contacts), i));
    }

    public /* synthetic */ void lambda$null$0$SearchAdapter(String str, ArrayList arrayList, int i) {
        String toLowerCase = str.trim().toLowerCase();
        if (toLowerCase.length() == 0) {
            updateSearchResults(new ArrayList(), new ArrayList());
            return;
        }
        String translitString = LocaleController.getInstance().getTranslitString(toLowerCase);
        String str2 = null;
        if (toLowerCase.equals(translitString) || translitString.length() == 0) {
            translitString = null;
        }
        int i2 = 0;
        int i3 = 1;
        String[] strArr = new String[((translitString != null ? 1 : 0) + 1)];
        strArr[0] = toLowerCase;
        if (translitString != null) {
            strArr[1] = translitString;
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int i4 = 0;
        while (i4 < arrayList.size()) {
            String str3;
            TL_contact tL_contact = (TL_contact) arrayList.get(i4);
            User user = MessagesController.getInstance(i).getUser(Integer.valueOf(tL_contact.user_id));
            if ((this.allowSelf || !user.self) && (!this.onlyMutual || user.mutual_contact)) {
                SparseArray sparseArray = this.ignoreUsers;
                if (sparseArray == null || sparseArray.indexOfKey(tL_contact.user_id) < 0) {
                    String[] strArr2 = new String[3];
                    strArr2[i2] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                    strArr2[i3] = LocaleController.getInstance().getTranslitString(strArr2[i2]);
                    if (strArr2[i2].equals(strArr2[i3])) {
                        strArr2[i3] = str2;
                    }
                    if (user.self) {
                        strArr2[2] = LocaleController.getString("SavedMessages", NUM).toLowerCase();
                    }
                    int length = strArr.length;
                    int i5 = 0;
                    Object obj = null;
                    while (i5 < length) {
                        StringBuilder stringBuilder;
                        String str4 = strArr[i5];
                        for (i2 = 
/*
Method generation error in method: org.telegram.ui.Adapters.SearchAdapter.lambda$null$0$SearchAdapter(java.lang.String, java.util.ArrayList, int):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r3_5 'i2' int) = (r3_3 'i2' int), (r3_11 'i2' int) binds: {(r3_3 'i2' int)=B:38:0x00ca, (r3_11 'i2' int)=B:63:0x0149} in method: org.telegram.ui.Adapters.SearchAdapter.lambda$null$0$SearchAdapter(java.lang.String, java.util.ArrayList, int):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:220)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:220)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 39 more

*/

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
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 0 || itemViewType == 2;
    }

    public int getItemCount() {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        if (size2 != 0) {
            size += size2 + 1;
        }
        size2 = this.searchAdapterHelper.getPhoneSearch().size();
        return size2 != 0 ? size + size2 : size;
    }

    public boolean isGlobalSearch(int i) {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (i < 0 || i >= size) {
            return (i <= size || i >= size + size3) && i > size + size3 && i <= (size2 + size3) + size;
        } else {
            return false;
        }
    }

    public Object getItem(int i) {
        int size = this.searchResult.size();
        int size2 = this.searchAdapterHelper.getGlobalSearch().size();
        int size3 = this.searchAdapterHelper.getPhoneSearch().size();
        if (i >= 0 && i < size) {
            return this.searchResult.get(i);
        }
        i -= size;
        if (i >= 0 && i < size3) {
            return this.searchAdapterHelper.getPhoneSearch().get(i);
        }
        i -= size3;
        return (i <= 0 || i > size2) ? null : this.searchAdapterHelper.getGlobalSearch().get(i - 1);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View textCell;
        if (i != 0) {
            if (i != 1) {
                textCell = new TextCell(this.mContext, 16, false);
            } else {
                textCell = new GraySectionCell(this.mContext);
            }
        } else if (this.useUserCell) {
            View userCell = new UserCell(this.mContext, 1, 1, false);
            if (this.checkedMap != null) {
                userCell.setChecked(false, false);
            }
            textCell = userCell;
        } else {
            textCell = new ProfileSearchCell(this.mContext);
        }
        return new Holder(textCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        CharSequence charSequence = null;
        boolean z = false;
        boolean z2 = true;
        if (itemViewType == 0) {
            TLObject tLObject = (TLObject) getItem(i);
            if (tLObject != null) {
                String str;
                int i2;
                boolean z3;
                CharSequence charSequence2;
                if (tLObject instanceof User) {
                    User user = (User) tLObject;
                    str = user.username;
                    i2 = user.id;
                    z3 = user.self;
                    itemViewType = i2;
                } else {
                    if (tLObject instanceof Chat) {
                        Chat chat = (Chat) tLObject;
                        str = chat.username;
                        itemViewType = chat.id;
                    } else {
                        str = null;
                        itemViewType = 0;
                    }
                    z3 = false;
                }
                String str2 = "@";
                if (i < this.searchResult.size()) {
                    CharSequence charSequence3 = (CharSequence) this.searchResultNames.get(i);
                    if (!(charSequence3 == null || str == null || str.length() <= 0)) {
                        String charSequence4 = charSequence3.toString();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(str2);
                        stringBuilder.append(str);
                        if (charSequence4.startsWith(stringBuilder.toString())) {
                            charSequence2 = charSequence3;
                        }
                    }
                    charSequence2 = null;
                    charSequence = charSequence3;
                } else if (i <= this.searchResult.size() || str == null) {
                    charSequence2 = null;
                } else {
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (lastFoundUsername.startsWith(str2)) {
                        lastFoundUsername = lastFoundUsername.substring(1);
                    }
                    try {
                        charSequence2 = new SpannableStringBuilder();
                        charSequence2.append(str2);
                        charSequence2.append(str);
                        int indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str, lastFoundUsername);
                        if (indexOfIgnoreCase != -1) {
                            i2 = lastFoundUsername.length();
                            if (indexOfIgnoreCase == 0) {
                                i2++;
                            } else {
                                indexOfIgnoreCase++;
                            }
                            charSequence2.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), indexOfIgnoreCase, i2 + indexOfIgnoreCase, 33);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        charSequence2 = str;
                    }
                }
                if (this.useUserCell) {
                    UserCell userCell = (UserCell) viewHolder.itemView;
                    userCell.setData(tLObject, charSequence, charSequence2, 0);
                    SparseArray sparseArray = this.checkedMap;
                    if (sparseArray != null) {
                        if (sparseArray.indexOfKey(itemViewType) < 0) {
                            z2 = false;
                        }
                        userCell.setChecked(z2, false);
                        return;
                    }
                    return;
                }
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewHolder.itemView;
                profileSearchCell.setData(tLObject, null, z3 ? LocaleController.getString("SavedMessages", NUM) : charSequence, charSequence2, false, z3);
                if (!(i == getItemCount() - 1 || i == this.searchResult.size() - 1)) {
                    z = true;
                }
                profileSearchCell.useSeparator = z;
            }
        } else if (itemViewType == 1) {
            GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
            if (getItem(i) == null) {
                graySectionCell.setText(LocaleController.getString("GlobalSearch", NUM));
            } else {
                graySectionCell.setText(LocaleController.getString("PhoneNumberSearch", NUM));
            }
        } else if (itemViewType == 2) {
            String str3 = (String) getItem(i);
            TextCell textCell = (TextCell) viewHolder.itemView;
            textCell.setColors(null, "windowBackgroundWhiteBlueText2");
            Object[] objArr = new Object[1];
            PhoneFormat instance = PhoneFormat.getInstance();
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("+");
            stringBuilder2.append(str3);
            objArr[0] = instance.format(stringBuilder2.toString());
            textCell.setText(LocaleController.formatString("AddContactByPhone", NUM, objArr), false);
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
