package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class PhonebookSearchAdapter extends SelectionAdapter {
    private Context mContext;
    private ArrayList<Object> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;

    public int getItemViewType(int i) {
        return 0;
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return true;
    }

    /* Access modifiers changed, original: protected */
    public void onUpdateSearchResults(String str) {
    }

    public PhonebookSearchAdapter(Context context) {
        this.mContext = context;
    }

    public void search(final String str) {
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
            notifyDataSetChanged();
            return;
        }
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    PhonebookSearchAdapter.this.searchTimer.cancel();
                    PhonebookSearchAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                PhonebookSearchAdapter.this.processSearch(str);
            }
        }, 200, 300);
    }

    private void processSearch(String str) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PhonebookSearchAdapter$KOELxq6var_LZaR2Hw7LDqS_qxfw(this, str));
    }

    public /* synthetic */ void lambda$processSearch$1$PhonebookSearchAdapter(String str) {
        int i = UserConfig.selectedAccount;
        Utilities.searchQueue.postRunnable(new -$$Lambda$PhonebookSearchAdapter$tWtQ3i-wKL8GTUJsG8Cqe7b7WRE(this, str, new ArrayList(ContactsController.getInstance(i).contactsBook.values()), new ArrayList(ContactsController.getInstance(i).contacts), i));
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x018a A:{LOOP_END, LOOP:1: B:27:0x00a1->B:70:0x018a} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0132 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0132 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x018a A:{LOOP_END, LOOP:1: B:27:0x00a1->B:70:0x018a} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x018a A:{LOOP_END, LOOP:1: B:27:0x00a1->B:70:0x018a} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0132 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x0132 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x018a A:{LOOP_END, LOOP:1: B:27:0x00a1->B:70:0x018a} */
    /* JADX WARNING: Removed duplicated region for block: B:104:0x026d A:{LOOP_END, LOOP:3: B:82:0x01e5->B:104:0x026d} */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0231 A:{SYNTHETIC} */
    /* JADX WARNING: Missing block: B:33:0x00c2, code skipped:
            if (r5.contains(r0.toString()) == false) goto L_0x00c4;
     */
    /* JADX WARNING: Missing block: B:38:0x00df, code skipped:
            if (r11.contains(r0.toString()) != false) goto L_0x00e1;
     */
    /* JADX WARNING: Missing block: B:55:0x0129, code skipped:
            if (r15.contains(r0.toString()) != false) goto L_0x012f;
     */
    /* JADX WARNING: Missing block: B:91:0x021f, code skipped:
            if (r9.contains(r4.toString()) != false) goto L_0x022e;
     */
    public /* synthetic */ void lambda$null$0$PhonebookSearchAdapter(java.lang.String r21, java.util.ArrayList r22, java.util.ArrayList r23, int r24) {
        /*
        r20 = this;
        r0 = r20;
        r1 = r21;
        r2 = r21.trim();
        r2 = r2.toLowerCase();
        r3 = r2.length();
        if (r3 != 0) goto L_0x0020;
    L_0x0012:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r0.updateSearchResults(r1, r2, r3);
        return;
    L_0x0020:
        r3 = org.telegram.messenger.LocaleController.getInstance();
        r3 = r3.getTranslitString(r2);
        r4 = r2.equals(r3);
        if (r4 != 0) goto L_0x0034;
    L_0x002e:
        r4 = r3.length();
        if (r4 != 0) goto L_0x0035;
    L_0x0034:
        r3 = 0;
    L_0x0035:
        r4 = 0;
        r6 = 1;
        if (r3 == 0) goto L_0x003b;
    L_0x0039:
        r7 = 1;
        goto L_0x003c;
    L_0x003b:
        r7 = 0;
    L_0x003c:
        r7 = r7 + r6;
        r7 = new java.lang.String[r7];
        r7[r4] = r2;
        if (r3 == 0) goto L_0x0045;
    L_0x0043:
        r7[r6] = r3;
    L_0x0045:
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r8 = new android.util.SparseBooleanArray;
        r8.<init>();
        r9 = 0;
    L_0x0055:
        r10 = r22.size();
        r12 = "@";
        r13 = " ";
        if (r9 >= r10) goto L_0x019e;
    L_0x005f:
        r10 = r22;
        r14 = r10.get(r9);
        r14 = (org.telegram.messenger.ContactsController.Contact) r14;
        r15 = r14.first_name;
        r4 = r14.last_name;
        r4 = org.telegram.messenger.ContactsController.formatName(r15, r4);
        r4 = r4.toLowerCase();
        r15 = org.telegram.messenger.LocaleController.getInstance();
        r15 = r15.getTranslitString(r4);
        r11 = r14.user;
        if (r11 == 0) goto L_0x0094;
    L_0x007f:
        r5 = r11.first_name;
        r11 = r11.last_name;
        r5 = org.telegram.messenger.ContactsController.formatName(r5, r11);
        r5 = r5.toLowerCase();
        r11 = org.telegram.messenger.LocaleController.getInstance();
        r11 = r11.getTranslitString(r4);
        goto L_0x0096;
    L_0x0094:
        r5 = 0;
        r11 = 0;
    L_0x0096:
        r16 = r4.equals(r15);
        if (r16 == 0) goto L_0x009d;
    L_0x009c:
        r15 = 0;
    L_0x009d:
        r6 = r7.length;
        r10 = 0;
        r17 = 0;
    L_0x00a1:
        if (r10 >= r6) goto L_0x0196;
    L_0x00a3:
        r18 = r6;
        r6 = r7[r10];
        if (r5 == 0) goto L_0x00c4;
    L_0x00a9:
        r19 = r5.startsWith(r6);
        if (r19 != 0) goto L_0x00e1;
    L_0x00af:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r0.append(r6);
        r0 = r0.toString();
        r0 = r5.contains(r0);
        if (r0 != 0) goto L_0x00e1;
    L_0x00c4:
        if (r11 == 0) goto L_0x00e3;
    L_0x00c6:
        r0 = r11.startsWith(r6);
        if (r0 != 0) goto L_0x00e1;
    L_0x00cc:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r0.append(r6);
        r0 = r0.toString();
        r0 = r11.contains(r0);
        if (r0 == 0) goto L_0x00e3;
    L_0x00e1:
        r0 = 1;
        goto L_0x0130;
    L_0x00e3:
        r0 = r14.user;
        if (r0 == 0) goto L_0x00f3;
    L_0x00e7:
        r0 = r0.username;
        if (r0 == 0) goto L_0x00f3;
    L_0x00eb:
        r0 = r0.startsWith(r6);
        if (r0 == 0) goto L_0x00f3;
    L_0x00f1:
        r0 = 2;
        goto L_0x0130;
    L_0x00f3:
        r0 = r4.startsWith(r6);
        if (r0 != 0) goto L_0x012f;
    L_0x00f9:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r0.append(r6);
        r0 = r0.toString();
        r0 = r4.contains(r0);
        if (r0 != 0) goto L_0x012f;
    L_0x010e:
        if (r15 == 0) goto L_0x012c;
    L_0x0110:
        r0 = r15.startsWith(r6);
        if (r0 != 0) goto L_0x012f;
    L_0x0116:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r13);
        r0.append(r6);
        r0 = r0.toString();
        r0 = r15.contains(r0);
        if (r0 == 0) goto L_0x012c;
    L_0x012b:
        goto L_0x012f;
    L_0x012c:
        r0 = r17;
        goto L_0x0130;
    L_0x012f:
        r0 = 3;
    L_0x0130:
        if (r0 == 0) goto L_0x018a;
    L_0x0132:
        r4 = 3;
        if (r0 != r4) goto L_0x0141;
    L_0x0135:
        r0 = r14.first_name;
        r4 = r14.last_name;
        r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r4, r6);
        r3.add(r0);
        goto L_0x017c;
    L_0x0141:
        r4 = 1;
        if (r0 != r4) goto L_0x0152;
    L_0x0144:
        r0 = r14.user;
        r4 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r4, r0, r6);
        r3.add(r0);
        goto L_0x017c;
    L_0x0152:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r12);
        r4 = r14.user;
        r4 = r4.username;
        r0.append(r4);
        r0 = r0.toString();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r12);
        r4.append(r6);
        r4 = r4.toString();
        r5 = 0;
        r0 = org.telegram.messenger.AndroidUtilities.generateSearchName(r0, r5, r4);
        r3.add(r0);
    L_0x017c:
        r0 = r14.user;
        if (r0 == 0) goto L_0x0186;
    L_0x0180:
        r0 = r0.id;
        r4 = 1;
        r8.put(r0, r4);
    L_0x0186:
        r2.add(r14);
        goto L_0x0196;
    L_0x018a:
        r17 = r4;
        r10 = r10 + 1;
        r6 = r18;
        r17 = r0;
        r0 = r20;
        goto L_0x00a1;
    L_0x0196:
        r9 = r9 + 1;
        r4 = 0;
        r6 = 1;
        r0 = r20;
        goto L_0x0055;
    L_0x019e:
        r0 = 0;
    L_0x019f:
        r4 = r23.size();
        if (r0 >= r4) goto L_0x0279;
    L_0x01a5:
        r4 = r23;
        r5 = r4.get(r0);
        r5 = (org.telegram.tgnet.TLRPC.TL_contact) r5;
        r6 = r5.user_id;
        r6 = r8.indexOfKey(r6);
        if (r6 < 0) goto L_0x01b9;
    L_0x01b5:
        r4 = 1;
        r15 = 0;
        goto L_0x0275;
    L_0x01b9:
        r6 = org.telegram.messenger.MessagesController.getInstance(r24);
        r5 = r5.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r6.getUser(r5);
        r6 = r5.first_name;
        r9 = r5.last_name;
        r6 = org.telegram.messenger.ContactsController.formatName(r6, r9);
        r6 = r6.toLowerCase();
        r9 = org.telegram.messenger.LocaleController.getInstance();
        r9 = r9.getTranslitString(r6);
        r10 = r6.equals(r9);
        if (r10 == 0) goto L_0x01e2;
    L_0x01e1:
        r9 = 0;
    L_0x01e2:
        r10 = r7.length;
        r11 = 0;
        r14 = 0;
    L_0x01e5:
        if (r11 >= r10) goto L_0x01b5;
    L_0x01e7:
        r15 = r7[r11];
        r17 = r6.startsWith(r15);
        if (r17 != 0) goto L_0x022e;
    L_0x01ef:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r13);
        r4.append(r15);
        r4 = r4.toString();
        r4 = r6.contains(r4);
        if (r4 != 0) goto L_0x022e;
    L_0x0204:
        if (r9 == 0) goto L_0x0222;
    L_0x0206:
        r4 = r9.startsWith(r15);
        if (r4 != 0) goto L_0x022e;
    L_0x020c:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r13);
        r4.append(r15);
        r4 = r4.toString();
        r4 = r9.contains(r4);
        if (r4 == 0) goto L_0x0222;
    L_0x0221:
        goto L_0x022e;
    L_0x0222:
        r4 = r5.username;
        if (r4 == 0) goto L_0x022f;
    L_0x0226:
        r4 = r4.startsWith(r15);
        if (r4 == 0) goto L_0x022f;
    L_0x022c:
        r14 = 2;
        goto L_0x022f;
    L_0x022e:
        r14 = 1;
    L_0x022f:
        if (r14 == 0) goto L_0x026d;
    L_0x0231:
        r4 = 1;
        if (r14 != r4) goto L_0x0241;
    L_0x0234:
        r6 = r5.first_name;
        r9 = r5.last_name;
        r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r9, r15);
        r3.add(r6);
        r15 = 0;
        goto L_0x0269;
    L_0x0241:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r12);
        r9 = r5.username;
        r6.append(r9);
        r6 = r6.toString();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r12);
        r9.append(r15);
        r9 = r9.toString();
        r15 = 0;
        r6 = org.telegram.messenger.AndroidUtilities.generateSearchName(r6, r15, r9);
        r3.add(r6);
    L_0x0269:
        r2.add(r5);
        goto L_0x0275;
    L_0x026d:
        r4 = 1;
        r15 = 0;
        r11 = r11 + 1;
        r4 = r23;
        goto L_0x01e5;
    L_0x0275:
        r0 = r0 + 1;
        goto L_0x019f;
    L_0x0279:
        r0 = r20;
        r0.updateSearchResults(r1, r2, r3);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.PhonebookSearchAdapter.lambda$null$0$PhonebookSearchAdapter(java.lang.String, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    private void updateSearchResults(String str, ArrayList<Object> arrayList, ArrayList<CharSequence> arrayList2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PhonebookSearchAdapter$5E7mSLYtYVP-MgRNvar_lnHW5RXo(this, str, arrayList, arrayList2));
    }

    public /* synthetic */ void lambda$updateSearchResults$2$PhonebookSearchAdapter(String str, ArrayList arrayList, ArrayList arrayList2) {
        onUpdateSearchResults(str);
        this.searchResult = arrayList;
        this.searchResultNames = arrayList2;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.searchResult.size();
    }

    public Object getItem(int i) {
        return this.searchResult.get(i);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        UserCell userCell = new UserCell(this.mContext, 8, 0, false);
        userCell.setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        return new Holder(userCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            User user;
            UserCell userCell = (UserCell) viewHolder.itemView;
            Object item = getItem(i);
            if (item instanceof Contact) {
                Contact contact = (Contact) item;
                user = contact.user;
                if (user == null) {
                    userCell.setCurrentId(contact.contact_id);
                    userCell.setData(null, (CharSequence) this.searchResultNames.get(i), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format((String) contact.phones.get(0)), 0);
                    user = null;
                }
            } else {
                user = (User) item;
            }
            if (user != null) {
                CharSequence charSequence = (CharSequence) this.searchResultNames.get(i);
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                userCell.setData(user, charSequence, instance.format(stringBuilder.toString()), 0);
            }
        }
    }
}
