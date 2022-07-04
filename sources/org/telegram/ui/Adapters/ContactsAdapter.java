package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.ContactsEmptyView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    private LongSparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public boolean hasGps;
    private LongSparseArray<TLRPC.User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isAdmin;
    private boolean isChannel;
    private boolean isEmpty;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private ArrayList<TLRPC.TL_contact> onlineContacts;
    /* access modifiers changed from: private */
    public int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int onlyUsersType, boolean showPhoneBook, LongSparseArray<TLRPC.User> usersToIgnore, int flags, boolean gps) {
        this.mContext = context;
        this.onlyUsers = onlyUsersType;
        this.needPhonebook = showPhoneBook;
        this.ignoreUsers = usersToIgnore;
        boolean z = true;
        this.isAdmin = flags != 0;
        this.isChannel = flags != 2 ? false : z;
        this.hasGps = gps;
    }

    public void setDisableSections(boolean value) {
        this.disableSections = value;
    }

    public void setSortType(int value, boolean force) {
        this.sortType = value;
        if (value == 2) {
            if (this.onlineContacts == null || force) {
                this.onlineContacts = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                int a = 0;
                int N = this.onlineContacts.size();
                while (true) {
                    if (a >= N) {
                        break;
                    } else if (this.onlineContacts.get(a).user_id == selfId) {
                        this.onlineContacts.remove(a);
                        break;
                    } else {
                        a++;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
    }

    public void sortOnlineContacts() {
        if (this.onlineContacts != null) {
            try {
                int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                Collections.sort(this.onlineContacts, new ContactsAdapter$$ExternalSyntheticLambda0(MessagesController.getInstance(this.currentAccount), currentTime));
                notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.TL_contact o1, TLRPC.TL_contact o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(o1.user_id));
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
                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                }
                return -1;
            } else if (status1 > status2) {
                return 1;
            } else {
                return status1 < status2 ? -1 : 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            return status1 < status2 ? -1 : 0;
        }
    }

    public void setCheckedMap(LongSparseArray<?> map) {
        this.checkedMap = map;
    }

    public void setIsScrolling(boolean value) {
        this.scrolling = value;
    }

    public Object getItem(int section, int position) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (section < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr = usersSectionsDict.get(sortedUsersSectionsArray.get(section));
                if (position < arr.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr.get(position).user_id));
                }
            }
            return null;
        } else if (section == 0) {
            return null;
        } else {
            if (this.sortType == 2) {
                if (section == 1) {
                    if (position < this.onlineContacts.size()) {
                        return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(position).user_id));
                    }
                    return null;
                }
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                ArrayList<TLRPC.TL_contact> arr2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1));
                if (position < arr2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr2.get(position).user_id));
                }
                return null;
            }
            if (!this.needPhonebook || position < 0 || position >= ContactsController.getInstance(this.currentAccount).phoneBookContacts.size()) {
                return null;
            }
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder holder, int section, int row) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.isAdmin) {
                    if (row != 1) {
                        return true;
                    }
                    return false;
                } else if (this.needPhonebook) {
                    boolean z = this.hasGps;
                    if ((!z || row == 2) && (z || row == 1)) {
                        return false;
                    }
                    return true;
                } else if (row != 3) {
                    return true;
                } else {
                    return false;
                }
            } else if (this.isEmpty) {
                return false;
            } else {
                if (this.sortType == 2) {
                    if (section != 1 || row < this.onlineContacts.size()) {
                        return true;
                    }
                    return false;
                } else if (section - 1 >= sortedUsersSectionsArray.size() || row < usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1)).size()) {
                    return true;
                } else {
                    return false;
                }
                return true;
            }
        } else if (!this.isEmpty && row < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
            return true;
        } else {
            return false;
        }
    }

    public int getSectionCount() {
        int count;
        this.isEmpty = false;
        if (this.sortType == 2) {
            count = 1;
            this.isEmpty = this.onlineContacts.isEmpty();
        } else {
            int count2 = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
            if (count2 == 0) {
                this.isEmpty = true;
                count = 1;
            } else {
                count = count2;
            }
        }
        if (this.onlyUsers == 0) {
            count++;
        }
        if (this.isAdmin) {
            return count + 1;
        }
        return count;
    }

    public int getCountForSection(int section) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.isAdmin) {
                    return 2;
                }
                if (!this.needPhonebook) {
                    return 4;
                }
                if (this.hasGps) {
                    return 3;
                }
                return 2;
            } else if (this.isEmpty) {
                return 1;
            } else {
                if (this.sortType == 2) {
                    if (section == 1) {
                        if (this.onlineContacts.isEmpty()) {
                            return 0;
                        }
                        return this.onlineContacts.size() + 1;
                    }
                } else if (section - 1 < sortedUsersSectionsArray.size()) {
                    int count = usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1)).size();
                    if (section - 1 != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                        return count + 1;
                    }
                    return count;
                }
            }
        } else if (this.isEmpty) {
            return 1;
        } else {
            if (section < sortedUsersSectionsArray.size()) {
                int count2 = usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size();
                if (section != sortedUsersSectionsArray.size() - 1 || this.needPhonebook) {
                    return count2 + 1;
                }
                return count2;
            }
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    public View getSectionHeaderView(int section, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap<String, ArrayList<TLRPC.TL_contact>> hashMap2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell cell = (LetterSectionCell) view;
        if (this.sortType == 2 || this.disableSections || this.isEmpty) {
            cell.setLetter("");
        } else if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                cell.setLetter("");
            } else if (section - 1 < sortedUsersSectionsArray.size()) {
                cell.setLetter(sortedUsersSectionsArray.get(section - 1));
            } else {
                cell.setLetter("");
            }
        } else if (section < sortedUsersSectionsArray.size()) {
            cell.setLetter(sortedUsersSectionsArray.get(section));
        } else {
            cell.setLetter("");
        }
        return view;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
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
            case 3:
                view = new DividerCell(this.mContext);
                float f = 28.0f;
                int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
                int dp2 = AndroidUtilities.dp(8.0f);
                if (LocaleController.isRTL) {
                    f = 72.0f;
                }
                view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
                break;
            case 4:
                AnonymousClass1 r0 = new FrameLayout(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        int height;
                        int height2 = View.MeasureSpec.getSize(heightMeasureSpec);
                        if (height2 == 0) {
                            height2 = parent.getMeasuredHeight();
                        }
                        int totalHeight = 0;
                        if (height2 == 0) {
                            height2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                        }
                        int cellHeight = AndroidUtilities.dp(50.0f);
                        if (ContactsAdapter.this.onlyUsers == 0) {
                            totalHeight = AndroidUtilities.dp(30.0f) + cellHeight;
                        }
                        if (ContactsAdapter.this.hasGps) {
                            totalHeight += cellHeight;
                        }
                        if (!ContactsAdapter.this.isAdmin && !ContactsAdapter.this.needPhonebook) {
                            totalHeight += cellHeight;
                        }
                        if (totalHeight < height2) {
                            height = height2 - totalHeight;
                        } else {
                            height = 0;
                        }
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(height, NUM));
                    }
                };
                r0.addView(new ContactsEmptyView(this.mContext), LayoutHelper.createFrame(-2, -2, 17));
                view = r0;
                break;
            default:
                view = new ShadowSectionCell(this.mContext);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                combinedDrawable.setFullsize(true);
                view.setBackgroundDrawable(combinedDrawable);
                break;
        }
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(int section, int position, RecyclerView.ViewHolder holder) {
        ArrayList<TLRPC.TL_contact> arr;
        boolean z = false;
        switch (holder.getItemViewType()) {
            case 0:
                UserCell userCell = (UserCell) holder.itemView;
                userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
                if (this.sortType == 2) {
                    arr = this.onlineContacts;
                } else {
                    arr = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict).get((this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).get(section - ((this.onlyUsers == 0 || this.isAdmin) ? 1 : 0)));
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arr.get(position).user_id));
                userCell.setData(user, (CharSequence) null, (CharSequence) null, 0);
                LongSparseArray<?> longSparseArray = this.checkedMap;
                if (longSparseArray != null) {
                    if (longSparseArray.indexOfKey(user.id) >= 0) {
                        z = true;
                    }
                    userCell.setChecked(z, true ^ this.scrolling);
                }
                LongSparseArray<TLRPC.User> longSparseArray2 = this.ignoreUsers;
                if (longSparseArray2 == null) {
                    return;
                }
                if (longSparseArray2.indexOfKey(user.id) >= 0) {
                    userCell.setAlpha(0.5f);
                    return;
                } else {
                    userCell.setAlpha(1.0f);
                    return;
                }
            case 1:
                TextCell textCell = (TextCell) holder.itemView;
                if (section != 0) {
                    ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(position);
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
                    if (position == 0) {
                        textCell.setTextAndIcon(LocaleController.getString("InviteFriends", NUM), NUM, false);
                        return;
                    } else if (position == 1) {
                        textCell.setTextAndIcon(LocaleController.getString("AddPeopleNearby", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                } else if (this.isAdmin) {
                    if (this.isChannel) {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", NUM), NUM, false);
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", NUM), NUM, false);
                        return;
                    }
                } else if (position == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", NUM), NUM, false);
                    return;
                } else if (position == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", NUM), NUM, false);
                    return;
                } else if (position == 2) {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", NUM), NUM, false);
                    return;
                } else {
                    return;
                }
            case 2:
                GraySectionCell sectionCell = (GraySectionCell) holder.itemView;
                int i = this.sortType;
                if (i == 0) {
                    sectionCell.setText(LocaleController.getString("Contacts", NUM));
                    return;
                } else if (i == 1) {
                    sectionCell.setText(LocaleController.getString("SortedByName", NUM));
                    return;
                } else {
                    sectionCell.setText(LocaleController.getString("SortedByLastSeen", NUM));
                    return;
                }
            default:
                return;
        }
    }

    public int getItemViewType(int section, int position) {
        HashMap<String, ArrayList<TLRPC.TL_contact>> usersSectionsDict = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section == 0) {
                if (this.isAdmin) {
                    if (position == 1) {
                        return 2;
                    }
                } else if (this.needPhonebook) {
                    boolean z = this.hasGps;
                    if ((z && position == 2) || (!z && position == 1)) {
                        if (this.isEmpty) {
                            return 5;
                        }
                        return 2;
                    }
                } else if (position == 3) {
                    if (this.isEmpty) {
                        return 5;
                    }
                    return 2;
                }
            } else if (this.isEmpty) {
                return 4;
            } else {
                if (this.sortType == 2) {
                    if (section == 1) {
                        if (position < this.onlineContacts.size()) {
                            return 0;
                        }
                        return 3;
                    }
                } else if (section - 1 < sortedUsersSectionsArray.size()) {
                    if (position < usersSectionsDict.get(sortedUsersSectionsArray.get(section - 1)).size()) {
                        return 0;
                    }
                    return 3;
                }
            }
            return 1;
        } else if (this.isEmpty) {
            return 4;
        } else {
            if (position < usersSectionsDict.get(sortedUsersSectionsArray.get(section)).size()) {
                return 0;
            }
            return 3;
        }
    }

    public String getLetter(int position) {
        if (this.sortType == 2 || this.isEmpty) {
            return null;
        }
        ArrayList<String> sortedUsersSectionsArray = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        int section = getSectionForPosition(position);
        if (section == -1) {
            section = sortedUsersSectionsArray.size() - 1;
        }
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (section > 0 && section <= sortedUsersSectionsArray.size()) {
                return sortedUsersSectionsArray.get(section - 1);
            }
        } else if (section >= 0 && section < sortedUsersSectionsArray.size()) {
            return sortedUsersSectionsArray.get(section);
        }
        return null;
    }

    public void getPositionForScrollProgress(RecyclerListView listView, float progress, int[] position) {
        position[0] = (int) (((float) getItemCount()) * progress);
        position[1] = 0;
    }
}
