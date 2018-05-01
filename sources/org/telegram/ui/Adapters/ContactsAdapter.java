package org.telegram.ui.Adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;

public class ContactsAdapter extends SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private SparseArray<User> ignoreUsers;
    private boolean isAdmin;
    private Context mContext;
    private boolean needPhonebook;
    private int onlyUsers;
    private boolean scrolling;

    public ContactsAdapter(Context context, int i, boolean z, SparseArray<User> sparseArray, boolean z2) {
        this.mContext = context;
        this.onlyUsers = i;
        this.needPhonebook = z;
        this.ignoreUsers = sparseArray;
        this.isAdmin = z2;
    }

    public void setCheckedMap(SparseArray<?> sparseArray) {
        this.checkedMap = sparseArray;
    }

    public void setIsScrolling(boolean z) {
        this.scrolling = z;
    }

    public Object getItem(int i, int i2) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        ArrayList arrayList2;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i < arrayList.size()) {
                arrayList2 = (ArrayList) hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList2.get(i2)).user_id));
                }
            }
            return null;
        } else if (i == 0) {
            return null;
        } else {
            i--;
            if (i < arrayList.size()) {
                arrayList2 = (ArrayList) hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arrayList2.get(i2)).user_id));
                }
                return null;
            } else if (this.needPhonebook != 0) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
            } else {
                return null;
            }
        }
    }

    public boolean isEnabled(int i, int i2) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        boolean z = false;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i2 < ((ArrayList) hashMap.get(arrayList.get(i))).size()) {
                z = true;
            }
            return z;
        } else if (i == 0) {
            if (this.needPhonebook == 0) {
                if (this.isAdmin == 0) {
                    return i2 != 3;
                }
            }
            if (i2 == 1) {
                return false;
            }
        } else {
            i--;
            if (i >= arrayList.size()) {
                return true;
            }
            if (i2 < ((ArrayList) hashMap.get(arrayList.get(i))).size()) {
                z = true;
            }
            return z;
        }
    }

    public int getSectionCount() {
        int size = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
        if (this.onlyUsers == 0) {
            size++;
        }
        if (this.isAdmin) {
            size++;
        }
        return this.needPhonebook ? size + 1 : size;
    }

    public int getCountForSection(int i) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int size;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                if (this.needPhonebook == 0) {
                    if (this.isAdmin == 0) {
                        return 4;
                    }
                }
                return 2;
            }
            i--;
            if (i < arrayList.size()) {
                size = ((ArrayList) hashMap.get(arrayList.get(i))).size();
                if (!(i == arrayList.size() - 1 && this.needPhonebook == 0)) {
                    size++;
                }
                return size;
            }
        } else if (i < arrayList.size()) {
            size = ((ArrayList) hashMap.get(arrayList.get(i))).size();
            if (!(i == arrayList.size() - 1 && this.needPhonebook == 0)) {
                size++;
            }
            return size;
        }
        return this.needPhonebook != 0 ? ContactsController.getInstance(this.currentAccount).phoneBookContacts.size() : 0;
    }

    public View getSectionHeaderView(int i, View view) {
        HashMap hashMap;
        if (this.onlyUsers == 2) {
            hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            hashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell letterSectionCell = (LetterSectionCell) view;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                letterSectionCell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            } else {
                i--;
                if (i < arrayList.size()) {
                    letterSectionCell.setLetter((String) arrayList.get(i));
                } else {
                    letterSectionCell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
                }
            }
        } else if (i < arrayList.size()) {
            letterSectionCell.setLetter((String) arrayList.get(i));
        } else {
            letterSectionCell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
        }
        return view;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (i) {
            case 0:
                i = new UserCell(this.mContext, 58, 1, false);
                break;
            case 1:
                viewGroup = new TextCell(this.mContext);
                break;
            case 2:
                viewGroup = new GraySectionCell(this.mContext);
                ((GraySectionCell) viewGroup).setText(LocaleController.getString("Contacts", C0446R.string.Contacts).toUpperCase());
                break;
            default:
                float f;
                i = new DividerCell(this.mContext);
                float f2 = 72.0f;
                if (LocaleController.isRTL) {
                    f = 28.0f;
                } else {
                    f = 72.0f;
                }
                int dp = AndroidUtilities.dp(f);
                if (!LocaleController.isRTL) {
                    f2 = 28.0f;
                }
                i.setPadding(dp, 0, AndroidUtilities.dp(f2), 0);
                break;
        }
        viewGroup = i;
        return new Holder(viewGroup);
    }

    public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
        switch (viewHolder.getItemViewType()) {
            case 0:
                UserCell userCell = (UserCell) viewHolder.itemView;
                HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
                ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
                boolean z = false;
                int i3 = (this.onlyUsers == 0 || this.isAdmin) ? 1 : 0;
                i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) ((ArrayList) hashMap.get(arrayList.get(i - i3))).get(i2)).user_id));
                userCell.setData(i, null, null, 0);
                if (this.checkedMap != 0) {
                    if (this.checkedMap.indexOfKey(i.id) >= 0) {
                        z = true;
                    }
                    userCell.setChecked(z, this.scrolling ^ 1);
                }
                if (this.ignoreUsers == 0) {
                    return;
                }
                if (this.ignoreUsers.indexOfKey(i.id) >= 0) {
                    userCell.setAlpha(NUM);
                    return;
                } else {
                    userCell.setAlpha(NUM);
                    return;
                }
            case 1:
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (i != 0) {
                    Contact contact = (Contact) ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
                    if (contact.first_name != 0 && contact.last_name != 0) {
                        i2 = new StringBuilder();
                        i2.append(contact.first_name);
                        i2.append(" ");
                        i2.append(contact.last_name);
                        textCell.setText(i2.toString());
                        return;
                    } else if (contact.first_name == 0 || contact.last_name != 0) {
                        textCell.setText(contact.last_name);
                        return;
                    } else {
                        textCell.setText(contact.first_name);
                        return;
                    }
                } else if (this.needPhonebook != 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", C0446R.string.InviteFriends), C0446R.drawable.menu_invite);
                    return;
                } else if (this.isAdmin != 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", C0446R.string.InviteToGroupByLink), C0446R.drawable.menu_invite);
                    return;
                } else if (i2 == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", C0446R.string.NewGroup), C0446R.drawable.menu_newgroup);
                    return;
                } else if (i2 == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", C0446R.string.NewSecretChat), C0446R.drawable.menu_secret);
                    return;
                } else if (i2 == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", C0446R.string.NewChannel), C0446R.drawable.menu_broadcast);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    public int getItemViewType(int i, int i2) {
        HashMap hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int i3 = 0;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i != 0) {
                i--;
                if (i < arrayList.size()) {
                    if (i2 >= ((ArrayList) hashMap.get(arrayList.get(i))).size()) {
                        i3 = 3;
                    }
                    return i3;
                }
            } else if ((!(this.needPhonebook == 0 && this.isAdmin == 0) && i2 == 1) || i2 == 3) {
                return 2;
            }
            return 1;
        }
        if (i2 >= ((ArrayList) hashMap.get(arrayList.get(i))).size()) {
            i3 = 3;
        }
        return i3;
    }

    public String getLetter(int i) {
        ArrayList arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        i = getSectionForPosition(i);
        if (i == -1) {
            i = arrayList.size() - 1;
        }
        return (i <= 0 || i > arrayList.size()) ? 0 : (String) arrayList.get(i - 1);
    }

    public int getPositionForScrollProgress(float f) {
        return (int) (((float) getItemCount()) * f);
    }
}
