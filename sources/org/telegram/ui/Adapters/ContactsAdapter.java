package org.telegram.ui.Adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0488R;
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

    public ContactsAdapter(Context context, int onlyUsersType, boolean arg2, SparseArray<User> arg3, boolean arg4) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = arg2;
        this.ignoreUsers = arg3;
        this.isAdmin = arg4;
    }

    public void setCheckedMap(SparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        this.scrolling = value;
    }

    public Object getItem(int section, int position) {
        ArrayList<String> sortedUsersSectionsArray;
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        if (this.onlyUsers == 2) {
            sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
        } else {
            sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        }
        ArrayList<TL_contact> arr;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                return null;
            }
            if (section - 1 < sortedUsersSectionsArray.size()) {
                arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position < arr.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
                }
                return null;
            } else if (this.needPhonebook) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
            } else {
                return null;
            }
        } else if (section >= sortedUsersSectionsArray.size()) {
            return null;
        } else {
            arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            if (position < arr.size()) {
                return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
            }
            return null;
        }
    }

    public boolean isEnabled(int section, int row) {
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.needPhonebook || this.isAdmin) {
                    if (row == 1) {
                        return false;
                    }
                    return true;
                } else if (row == 3) {
                    return false;
                } else {
                    return true;
                }
            } else if (section - 1 >= sortedUsersSectionsArray.size() || row < ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))).size()) {
                return true;
            } else {
                return false;
            }
        } else if (row < ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section))).size()) {
            return true;
        } else {
            return false;
        }
    }

    public int getSectionCount() {
        int count = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
        if (this.onlyUsers == 0) {
            count++;
        }
        if (this.isAdmin) {
            count++;
        }
        if (this.needPhonebook) {
            return count + 1;
        }
        return count;
    }

    public int getCountForSection(int section) {
        ArrayList<String> sortedUsersSectionsArray;
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        if (this.onlyUsers == 2) {
            sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray;
        } else {
            sortedUsersSectionsArray = ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        }
        int count;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.needPhonebook || this.isAdmin) {
                    return 2;
                }
                return 4;
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                count = ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))).size();
                if (section - 1 != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                    return count + 1;
                }
                return count;
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            count = ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section))).size();
            if (section != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                return count + 1;
            }
            return count;
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    public View getSectionHeaderView(int section, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TL_contact>> usersSectionsDict = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap hashMap = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                cell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                cell.setLetter((String) sortedUsersSectionsArray.get(section - 1));
            } else {
                cell.setLetter(TtmlNode.ANONYMOUS_REGION_ID);
            }
        } else if (section < sortedUsersSectionsArray.size()) {
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
                break;
            case 1:
                view = new TextCell(this.mContext);
                break;
            case 2:
                view = new GraySectionCell(this.mContext);
                ((GraySectionCell) view).setText(LocaleController.getString("Contacts", C0488R.string.Contacts).toUpperCase());
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
                HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
                ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
                int i = (this.onlyUsers == 0 || this.isAdmin) ? 1 : 0;
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - i))).get(position)).user_id));
                userCell.setData(user, null, null, 0);
                if (this.checkedMap != null) {
                    userCell.setChecked(this.checkedMap.indexOfKey(user.id) >= 0, !this.scrolling);
                }
                if (this.ignoreUsers == null) {
                    return;
                }
                if (this.ignoreUsers.indexOfKey(user.id) >= 0) {
                    userCell.setAlpha(0.5f);
                    return;
                } else {
                    userCell.setAlpha(1.0f);
                    return;
                }
            case 1:
                TextCell textCell = holder.itemView;
                if (section != 0) {
                    Contact contact = (Contact) ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
                    if (contact.first_name != null && contact.last_name != null) {
                        textCell.setText(contact.first_name + " " + contact.last_name);
                        return;
                    } else if (contact.first_name == null || contact.last_name != null) {
                        textCell.setText(contact.last_name);
                        return;
                    } else {
                        textCell.setText(contact.first_name);
                        return;
                    }
                } else if (this.needPhonebook) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", C0488R.string.InviteFriends), C0488R.drawable.menu_invite);
                    return;
                } else if (this.isAdmin) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", C0488R.string.InviteToGroupByLink), C0488R.drawable.menu_invite);
                    return;
                } else if (position == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", C0488R.string.NewGroup), C0488R.drawable.menu_newgroup);
                    return;
                } else if (position == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", C0488R.string.NewSecretChat), C0488R.drawable.menu_secret);
                    return;
                } else if (position == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", C0488R.string.NewChannel), C0488R.drawable.menu_broadcast);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (((this.needPhonebook || this.isAdmin) && position == 1) || position == 3) {
                    return 2;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                if (position >= ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))).size()) {
                    return 3;
                }
                return 0;
            }
            return 1;
        } else if (position < ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section))).size()) {
            return 0;
        } else {
            return 3;
        }
    }

    public String getLetter(int position) {
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (section <= 0 || section > sortedUsersSectionsArray.size()) {
            return null;
        }
        return (String) sortedUsersSectionsArray.get(section - 1);
    }

    public int getPositionForScrollProgress(float progress) {
        return (int) (((float) getItemCount()) * progress);
    }
}
