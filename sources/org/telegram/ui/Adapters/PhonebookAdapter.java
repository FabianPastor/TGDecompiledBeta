package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;

public class PhonebookAdapter extends SectionsAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private Context mContext;

    public PhonebookAdapter(Context context) {
        this.mContext = context;
    }

    public Object getItem(int i, int i2) {
        HashMap hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (i < arrayList.size()) {
            ArrayList arrayList2 = (ArrayList) hashMap.get(arrayList.get(i));
            if (i2 < arrayList2.size()) {
                return arrayList2.get(i2);
            }
        }
        return null;
    }

    public boolean isEnabled(int i, int i2) {
        return i2 < ((ArrayList) ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i))).size();
    }

    public int getSectionCount() {
        return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size();
    }

    public int getCountForSection(int i) {
        HashMap hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (i >= arrayList.size()) {
            return 0;
        }
        int size = ((ArrayList) hashMap.get(arrayList.get(i))).size();
        if (i != arrayList.size() - 1) {
            size++;
        }
        return size;
    }

    public View getSectionHeaderView(int i, View view) {
        HashMap hashMap = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell letterSectionCell = (LetterSectionCell) view;
        if (i < arrayList.size()) {
            letterSectionCell.setLetter((String) arrayList.get(i));
        } else {
            letterSectionCell.setLetter("");
        }
        return view;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dividerCell;
        if (i != 0) {
            dividerCell = new DividerCell(this.mContext);
            float f = 28.0f;
            i = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
            int dp = AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            }
            dividerCell.setPadding(i, dp, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
        } else {
            dividerCell = new UserCell(this.mContext, 58, 1, false);
            dividerCell.setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        }
        return new Holder(dividerCell);
    }

    public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
        if (viewHolder.getItemViewType() == 0) {
            User user;
            UserCell userCell = (UserCell) viewHolder.itemView;
            Object item = getItem(i, i2);
            if (item instanceof Contact) {
                Contact contact = (Contact) item;
                user = contact.user;
                if (user == null) {
                    userCell.setCurrentId(contact.contact_id);
                    userCell.setData(null, ContactsController.formatName(contact.first_name, contact.last_name), contact.phones.isEmpty() ? "" : PhoneFormat.getInstance().format((String) contact.phones.get(0)), 0);
                    user = null;
                }
            } else {
                user = (User) item;
            }
            if (user != null) {
                PhoneFormat instance = PhoneFormat.getInstance();
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                userCell.setData(user, null, instance.format(stringBuilder.toString()), 0);
            }
        }
    }

    public int getItemViewType(int i, int i2) {
        return i2 < ((ArrayList) ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(i))).size() ? 0 : 1;
    }

    public String getLetter(int i) {
        ArrayList arrayList = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        i = getSectionForPosition(i);
        if (i == -1) {
            i = arrayList.size() - 1;
        }
        return (i < 0 || i >= arrayList.size()) ? null : (String) arrayList.get(i);
    }

    public int getPositionForScrollProgress(float f) {
        return (int) (((float) getItemCount()) * f);
    }
}
