package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.TL_contact;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GreySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;

public class ContactsAdapter extends BaseSectionsAdapter {
    private HashMap<Integer, ?> checkedMap;
    private HashMap<Integer, User> ignoreUsers;
    private boolean isAdmin;
    private Context mContext;
    private boolean needPhonebook;
    private int onlyUsers;
    private boolean scrolling;

    public ContactsAdapter(Context context, int onlyUsersType, boolean arg2, HashMap<Integer, User> arg3, boolean arg4) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = arg2;
        this.ignoreUsers = arg3;
        this.isAdmin = arg4;
    }

    public void setCheckedMap(HashMap<Integer, ?> map) {
        this.checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        this.scrolling = value;
    }

    public Object getItem(int section, int position) {
        ArrayList<String> sortedUsersSectionsArray;
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        if (this.onlyUsers == 2) {
            sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersMutualSectionsArray;
        } else {
            sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersSectionsArray;
        }
        ArrayList<TL_contact> arr;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                return null;
            }
            if (section - 1 < sortedUsersSectionsArray.size()) {
                arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position < arr.size()) {
                    return MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
                }
                return null;
            } else if (this.needPhonebook) {
                return ContactsController.getInstance().phoneBookContacts.get(position);
            } else {
                return null;
            }
        } else if (section >= sortedUsersSectionsArray.size()) {
            return null;
        } else {
            arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section));
            if (position < arr.size()) {
                return MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
            }
            return null;
        }
    }

    public boolean isRowEnabled(int section, int row) {
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
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
        int count = (this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray).size();
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
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        if (this.onlyUsers == 2) {
            sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersMutualSectionsArray;
        } else {
            sortedUsersSectionsArray = ContactsController.getInstance().sortedUsersSectionsArray;
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
            return ContactsController.getInstance().phoneBookContacts.size();
        }
        return 0;
    }

    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TL_contact>> usersSectionsDict = ContactsController.getInstance().usersMutualSectionsDict;
        } else {
            HashMap hashMap = ContactsController.getInstance().usersSectionsDict;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        if (convertView == null) {
            convertView = new LetterSectionCell(this.mContext);
        }
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                ((LetterSectionCell) convertView).setLetter("");
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ((LetterSectionCell) convertView).setLetter((String) sortedUsersSectionsArray.get(section - 1));
            } else {
                ((LetterSectionCell) convertView).setLetter("");
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            ((LetterSectionCell) convertView).setLetter((String) sortedUsersSectionsArray.get(section));
        } else {
            ((LetterSectionCell) convertView).setLetter("");
        }
        return convertView;
    }

    public View getItemView(int section, int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(section, position);
        if (type == 4) {
            if (convertView != null) {
                return convertView;
            }
            float f;
            convertView = new DividerCell(this.mContext);
            if (LocaleController.isRTL) {
                f = 28.0f;
            } else {
                f = 72.0f;
            }
            int dp = AndroidUtilities.dp(f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            } else {
                f = 28.0f;
            }
            convertView.setPadding(dp, 0, AndroidUtilities.dp(f), 0);
            return convertView;
        } else if (type == 3) {
            if (convertView != null) {
                return convertView;
            }
            convertView = new GreySectionCell(this.mContext);
            ((GreySectionCell) convertView).setText(LocaleController.getString("Contacts", R.string.Contacts).toUpperCase());
            return convertView;
        } else if (type == 2) {
            if (convertView == null) {
                convertView = new TextCell(this.mContext);
            }
            TextCell actionCell = (TextCell) convertView;
            if (this.needPhonebook) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite);
                return convertView;
            } else if (this.isAdmin) {
                actionCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.menu_invite);
                return convertView;
            } else if (position == 0) {
                actionCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup);
                return convertView;
            } else if (position == 1) {
                actionCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret);
                return convertView;
            } else if (position != 2) {
                return convertView;
            } else {
                actionCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast);
                return convertView;
            }
        } else if (type == 1) {
            if (convertView == null) {
                convertView = new TextCell(this.mContext);
            }
            Contact contact = (Contact) ContactsController.getInstance().phoneBookContacts.get(position);
            TextCell textCell = (TextCell) convertView;
            if (contact.first_name != null && contact.last_name != null) {
                textCell.setText(contact.first_name + " " + contact.last_name);
                return convertView;
            } else if (contact.first_name == null || contact.last_name != null) {
                textCell.setText(contact.last_name);
                return convertView;
            } else {
                textCell.setText(contact.first_name);
                return convertView;
            }
        } else if (type != 0) {
            return convertView;
        } else {
            if (convertView == null) {
                convertView = new UserCell(this.mContext, 58, 1, false);
                ((UserCell) convertView).setStatusColors(-5723992, -12876608);
            }
            HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
            ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
            int i = (this.onlyUsers == 0 || this.isAdmin) ? 1 : 0;
            User user = MessagesController.getInstance().getUser(Integer.valueOf(((TL_contact) ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - i))).get(position)).user_id));
            ((UserCell) convertView).setData(user, null, null, 0);
            if (this.checkedMap != null) {
                ((UserCell) convertView).setChecked(this.checkedMap.containsKey(Integer.valueOf(user.id)), !this.scrolling);
            }
            if (this.ignoreUsers == null) {
                return convertView;
            }
            if (this.ignoreUsers.containsKey(Integer.valueOf(user.id))) {
                convertView.setAlpha(0.5f);
                return convertView;
            }
            convertView.setAlpha(1.0f);
            return convertView;
        }
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance().usersMutualSectionsDict : ContactsController.getInstance().usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance().sortedUsersMutualSectionsArray : ContactsController.getInstance().sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.needPhonebook || this.isAdmin) {
                    if (position == 1) {
                        return 3;
                    }
                } else if (position == 3) {
                    return 3;
                }
                return 2;
            } else if (section - 1 >= sortedUsersSectionsArray.size()) {
                return 1;
            } else {
                if (position >= ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1))).size()) {
                    return 4;
                }
                return 0;
            }
        } else if (position < ((ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section))).size()) {
            return 0;
        } else {
            return 4;
        }
    }

    public int getViewTypeCount() {
        return 5;
    }
}
