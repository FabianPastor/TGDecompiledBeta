package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
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

    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (section < sortedUsersSectionsArray.size()) {
            ArrayList<Object> arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            if (position < arr.size()) {
                return arr.get(position);
            }
        }
        return null;
    }

    public boolean isEnabled(int section, int row) {
        return row < ((ArrayList) ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(section))).size();
    }

    public int getSectionCount() {
        return ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.size();
    }

    public int getCountForSection(int section) {
        HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (section >= sortedUsersSectionsArray.size()) {
            return 0;
        }
        int count = ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section))).size();
        if (section != sortedUsersSectionsArray.size() - 1) {
            return count + 1;
        }
        return count;
    }

    public View getSectionHeaderView(int section, View view) {
        HashMap<String, ArrayList<Object>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (section < sortedUsersSectionsArray.size()) {
            cell.setLetter((String) sortedUsersSectionsArray.get(section));
        } else {
            cell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
        }
        return view;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        float f = 72.0f;
        switch (viewType) {
            case 0:
                view = new UserCell(this.mContext, 58, 1, false);
                ((UserCell) view).setNameTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                break;
            default:
                float f2;
                view = new DividerCell(this.mContext);
                if (LocaleController.isRTL) {
                    f2 = 28.0f;
                } else {
                    f2 = 72.0f;
                }
                int dp = AndroidUtilities.dp(f2);
                if (!LocaleController.isRTL) {
                    f = 28.0f;
                }
                view.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
                break;
        }
        return new Holder(view);
    }

    public void onBindViewHolder(int section, int position, ViewHolder holder) {
        switch (holder.getItemViewType()) {
            case 0:
                UserCell userCell = holder.itemView;
                Contact object = getItem(section, position);
                User user = null;
                if (object instanceof Contact) {
                    Contact contact = object;
                    if (contact.user != null) {
                        user = contact.user;
                    } else {
                        userCell.setCurrentId(contact.contact_id);
                        userCell.setData(null, ContactsController.formatName(contact.first_name, contact.last_name), contact.phones.isEmpty() ? TtmlNode.ANONYMOUS_REGION_ID : PhoneFormat.getInstance().format((String) contact.phones.get(0)), 0);
                    }
                } else {
                    user = (User) object;
                }
                if (user != null) {
                    userCell.setData(user, null, PhoneFormat.getInstance().format("+" + user.phone), 0);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int section, int position) {
        return position < ((ArrayList) ContactsController.getInstance(this.currentAccount).phoneBookSectionsDict.get(ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray.get(section))).size() ? 0 : 1;
    }

    public String getLetter(int position) {
        ArrayList<String> sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).phoneBookSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (section < 0 || section >= sortedUsersSectionsArray.size()) {
            return null;
        }
        return (String) sortedUsersSectionsArray.get(section);
    }

    public int getPositionForScrollProgress(float progress) {
        return (int) (((float) getItemCount()) * progress);
    }
}
