package org.telegram.ui.Adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
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
    private ArrayList<TL_contact> onlineContacts;
    private int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int onlyUsersType, boolean arg2, SparseArray<User> arg3, boolean arg4) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = arg2;
        this.ignoreUsers = arg3;
        this.isAdmin = arg4;
    }

    public void setSortType(int value) {
        this.sortType = value;
        if (this.sortType == 2) {
            if (this.onlineContacts == null) {
                this.onlineContacts = new ArrayList();
                int selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                this.onlineContacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
                int N = this.onlineContacts.size();
                for (int a = 0; a < N; a++) {
                    if (((TL_contact) this.onlineContacts.get(a)).user_id == selfId) {
                        this.onlineContacts.remove(a);
                        break;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
    }

    public void sortOnlineContacts() {
        try {
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            Collections.sort(this.onlineContacts, new ContactsAdapter$$Lambda$0(MessagesController.getInstance(this.currentAccount), currentTime));
            notifyDataSetChanged();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    static final /* synthetic */ int lambda$sortOnlineContacts$0$ContactsAdapter(MessagesController messagesController, int currentTime, TL_contact o1, TL_contact o2) {
        User user1 = messagesController.getUser(Integer.valueOf(o2.user_id));
        User user2 = messagesController.getUser(Integer.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                    return -1;
                }
                if (status2 < 0 && status1 > 0) {
                    return 1;
                }
                if (status2 != 0 || status1 == 0) {
                    return 0;
                }
                return 1;
            } else if (status1 > status2) {
                return 1;
            } else {
                if (status1 < status2) {
                    return -1;
                }
                return 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            if (status1 < status2) {
                return -1;
            }
            return 0;
        }
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
            if (this.sortType == 2) {
                if (section == 1) {
                    if (position < this.onlineContacts.size()) {
                        return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) this.onlineContacts.get(position)).user_id));
                    }
                    return null;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position < arr.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
                }
                return null;
            }
            if (this.needPhonebook) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
            }
            return null;
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
            } else if (this.sortType == 2) {
                if (section != 1 || row < this.onlineContacts.size()) {
                    return true;
                }
                return false;
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
        int count;
        if (this.sortType == 2) {
            count = 1;
        } else {
            count = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
        }
        if (this.onlyUsers == 0) {
            count++;
        }
        if (this.isAdmin) {
            count++;
        }
        if (this.needPhonebook) {
        }
        return count;
    }

    public int getCountForSection(int section) {
        ArrayList<String> sortedUsersSectionsArray;
        int i = 0;
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
            } else if (this.sortType == 2) {
                if (section == 1) {
                    if (!this.onlineContacts.isEmpty()) {
                        i = this.onlineContacts.size() + 1;
                    }
                    return i;
                }
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
        return this.needPhonebook ? ContactsController.getInstance(this.currentAccount).phoneBookContacts.size() : 0;
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
        if (this.sortType == 2) {
            cell.setLetter("");
        } else if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                cell.setLetter("");
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                cell.setLetter((String) sortedUsersSectionsArray.get(section - 1));
            } else {
                cell.setLetter("");
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            cell.setLetter((String) sortedUsersSectionsArray.get(section));
        } else {
            cell.setLetter("");
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
                int dp2 = AndroidUtilities.dp(8.0f);
                if (!LocaleController.isRTL) {
                    f = 28.0f;
                }
                view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
                break;
        }
        return new Holder(view);
    }

    public void onBindViewHolder(int section, int position, ViewHolder holder) {
        switch (holder.getItemViewType()) {
            case 0:
                ArrayList<TL_contact> arr;
                UserCell userCell = holder.itemView;
                userCell.setAvatarPadding(this.sortType == 2 ? 6 : 58);
                if (this.sortType == 2) {
                    arr = this.onlineContacts;
                } else {
                    HashMap<String, ArrayList<TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
                    ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
                    int i = (this.onlyUsers == 0 || this.isAdmin) ? 1 : 0;
                    arr = (ArrayList) usersSectionsDict.get(sortedUsersSectionsArray.get(section - i));
                }
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TL_contact) arr.get(position)).user_id));
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
                        textCell.setText(contact.first_name + " " + contact.last_name, false);
                        return;
                    } else if (contact.first_name == null || contact.last_name != null) {
                        textCell.setText(contact.last_name, false);
                        return;
                    } else {
                        textCell.setText(contact.first_name, false);
                        return;
                    }
                } else if (this.needPhonebook) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite, false);
                    return;
                } else if (this.isAdmin) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.profile_link, false);
                    return;
                } else if (position == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup, false);
                    return;
                } else if (position == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret, false);
                    return;
                } else if (position == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast, false);
                    return;
                } else {
                    return;
                }
            case 2:
                GraySectionCell sectionCell = holder.itemView;
                if (this.sortType == 0) {
                    sectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
                    return;
                } else if (this.sortType == 1) {
                    sectionCell.setText(LocaleController.getString("SortedByName", R.string.SortedByName));
                    return;
                } else {
                    sectionCell.setText(LocaleController.getString("SortedByLastSeen", R.string.SortedByLastSeen));
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
            } else if (this.sortType == 2) {
                if (section == 1) {
                    if (position >= this.onlineContacts.size()) {
                        return 3;
                    }
                    return 0;
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
        if (this.sortType == 2) {
            return null;
        }
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
