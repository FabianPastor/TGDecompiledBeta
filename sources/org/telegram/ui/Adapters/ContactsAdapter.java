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
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC$TL_contact;
import org.telegram.tgnet.TLRPC$User;
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
/* loaded from: classes3.dex */
public class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    private LongSparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    private boolean hasGps;
    private LongSparseArray<TLRPC$User> ignoreUsers;
    private boolean isAdmin;
    private boolean isChannel;
    private boolean isEmpty;
    private Context mContext;
    private boolean needPhonebook;
    private ArrayList<TLRPC$TL_contact> onlineContacts;
    private int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int i, boolean z, LongSparseArray<TLRPC$User> longSparseArray, int i2, boolean z2) {
        this.mContext = context;
        this.onlyUsers = i;
        this.needPhonebook = z;
        this.ignoreUsers = longSparseArray;
        boolean z3 = true;
        this.isAdmin = i2 != 0;
        this.isChannel = i2 != 2 ? false : z3;
        this.hasGps = z2;
    }

    public void setDisableSections(boolean z) {
        this.disableSections = z;
    }

    public void setSortType(int i, boolean z) {
        this.sortType = i;
        if (i == 2) {
            if (this.onlineContacts == null || z) {
                this.onlineContacts = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                long j = UserConfig.getInstance(this.currentAccount).clientUserId;
                int i2 = 0;
                int size = this.onlineContacts.size();
                while (true) {
                    if (i2 >= size) {
                        break;
                    } else if (this.onlineContacts.get(i2).user_id == j) {
                        this.onlineContacts.remove(i2);
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            sortOnlineContacts();
            return;
        }
        notifyDataSetChanged();
    }

    public void sortOnlineContacts() {
        if (this.onlineContacts == null) {
            return;
        }
        try {
            final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            Collections.sort(this.onlineContacts, new Comparator() { // from class: org.telegram.ui.Adapters.ContactsAdapter$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$sortOnlineContacts$0;
                    lambda$sortOnlineContacts$0 = ContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, currentTime, (TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
                    return lambda$sortOnlineContacts$0;
                }
            });
            notifyDataSetChanged();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:12:0x002b  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x003d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0048 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0053 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x005c A[ADDED_TO_REGION] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static /* synthetic */ int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC$TL_contact r4, org.telegram.tgnet.TLRPC$TL_contact r5) {
        /*
            long r0 = r5.user_id
            java.lang.Long r5 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r5 = r2.getUser(r5)
            long r0 = r4.user_id
            java.lang.Long r4 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            r4 = 50000(0xCLASSNAME, float:7.0065E-41)
            r0 = 0
            if (r5 == 0) goto L28
            boolean r1 = r5.self
            if (r1 == 0) goto L21
            int r5 = r3 + r4
            goto L29
        L21:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
            if (r5 == 0) goto L28
            int r5 = r5.expires
            goto L29
        L28:
            r5 = 0
        L29:
            if (r2 == 0) goto L38
            boolean r1 = r2.self
            if (r1 == 0) goto L31
            int r3 = r3 + r4
            goto L39
        L31:
            org.telegram.tgnet.TLRPC$UserStatus r2 = r2.status
            if (r2 == 0) goto L38
            int r3 = r2.expires
            goto L39
        L38:
            r3 = 0
        L39:
            r2 = -1
            r4 = 1
            if (r5 <= 0) goto L46
            if (r3 <= 0) goto L46
            if (r5 <= r3) goto L42
            return r4
        L42:
            if (r5 >= r3) goto L45
            return r2
        L45:
            return r0
        L46:
            if (r5 >= 0) goto L51
            if (r3 >= 0) goto L51
            if (r5 <= r3) goto L4d
            return r4
        L4d:
            if (r5 >= r3) goto L50
            return r2
        L50:
            return r0
        L51:
            if (r5 >= 0) goto L55
            if (r3 > 0) goto L59
        L55:
            if (r5 != 0) goto L5a
            if (r3 == 0) goto L5a
        L59:
            return r2
        L5a:
            if (r3 >= 0) goto L5e
            if (r5 > 0) goto L62
        L5e:
            if (r3 != 0) goto L63
            if (r5 == 0) goto L63
        L62:
            return r4
        L63:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.ContactsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    /* renamed from: getItem */
    public Object mo1741getItem(int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i < arrayList.size()) {
                ArrayList<TLRPC$TL_contact> arrayList2 = hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList2.get(i2).user_id));
                }
            }
            return null;
        } else if (i == 0) {
            return null;
        } else {
            if (this.sortType != 2) {
                int i3 = i - 1;
                if (i3 < arrayList.size()) {
                    ArrayList<TLRPC$TL_contact> arrayList3 = hashMap.get(arrayList.get(i3));
                    if (i2 >= arrayList3.size()) {
                        return null;
                    }
                    return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList3.get(i2).user_id));
                }
            } else if (i == 1) {
                if (i2 >= this.onlineContacts.size()) {
                    return null;
                }
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i2).user_id));
            }
            if (this.needPhonebook && i2 >= 0 && i2 < ContactsController.getInstance(this.currentAccount).phoneBookContacts.size()) {
                return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
            }
            return null;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            return !this.isEmpty && i2 < hashMap.get(arrayList.get(i)).size();
        } else if (i == 0) {
            if (this.isAdmin) {
                return i2 != 1;
            } else if (!this.needPhonebook) {
                return i2 != 3;
            } else {
                boolean z = this.hasGps;
                return (z && i2 != 2) || (!z && i2 != 1);
            }
        } else if (this.isEmpty) {
            return false;
        } else {
            if (this.sortType == 2) {
                return i != 1 || i2 < this.onlineContacts.size();
            }
            int i3 = i - 1;
            return i3 >= arrayList.size() || i2 < hashMap.get(arrayList.get(i3)).size();
            return true;
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getSectionCount() {
        this.isEmpty = false;
        int i = 1;
        if (this.sortType == 2) {
            this.isEmpty = this.onlineContacts.isEmpty();
        } else {
            int size = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).size();
            if (size == 0) {
                this.isEmpty = true;
            } else {
                i = size;
            }
        }
        if (this.onlyUsers == 0) {
            i++;
        }
        return this.isAdmin ? i + 1 : i;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getCountForSection(int i) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                if (this.isAdmin) {
                    return 2;
                }
                if (!this.needPhonebook) {
                    return 4;
                }
                return this.hasGps ? 3 : 2;
            } else if (this.isEmpty) {
                return 1;
            } else {
                if (this.sortType != 2) {
                    int i2 = i - 1;
                    if (i2 < arrayList.size()) {
                        int size = hashMap.get(arrayList.get(i2)).size();
                        return (i2 != arrayList.size() - 1 || this.needPhonebook) ? size + 1 : size;
                    }
                } else if (i == 1) {
                    if (!this.onlineContacts.isEmpty()) {
                        return this.onlineContacts.size() + 1;
                    }
                    return 0;
                }
            }
        } else if (this.isEmpty) {
            return 1;
        } else {
            if (i < arrayList.size()) {
                int size2 = hashMap.get(arrayList.get(i)).size();
                return (i != arrayList.size() - 1 || this.needPhonebook) ? size2 + 1 : size2;
            }
        }
        if (this.needPhonebook) {
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.size();
        }
        return 0;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public View getSectionHeaderView(int i, View view) {
        if (this.onlyUsers == 2) {
            HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict;
        } else {
            HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap2 = ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        }
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (view == null) {
            view = new LetterSectionCell(this.mContext);
        }
        LetterSectionCell letterSectionCell = (LetterSectionCell) view;
        if (this.sortType == 2 || this.disableSections || this.isEmpty) {
            letterSectionCell.setLetter("");
        } else if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                letterSectionCell.setLetter("");
            } else {
                int i2 = i - 1;
                if (i2 < arrayList.size()) {
                    letterSectionCell.setLetter(arrayList.get(i2));
                } else {
                    letterSectionCell.setLetter("");
                }
            }
        } else if (i < arrayList.size()) {
            letterSectionCell.setLetter(arrayList.get(i));
        } else {
            letterSectionCell.setLetter("");
        }
        return view;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder */
    public RecyclerView.ViewHolder mo1753onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View userCell;
        if (i == 0) {
            userCell = new UserCell(this.mContext, 58, 1, false);
        } else if (i == 1) {
            userCell = new TextCell(this.mContext);
        } else if (i == 2) {
            userCell = new GraySectionCell(this.mContext);
        } else if (i == 3) {
            userCell = new DividerCell(this.mContext);
            float f = 28.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            }
            userCell.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
        } else if (i == 4) {
            FrameLayout frameLayout = new FrameLayout(this.mContext) { // from class: org.telegram.ui.Adapters.ContactsAdapter.1
                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int i2, int i3) {
                    int size = View.MeasureSpec.getSize(i3);
                    if (size == 0) {
                        size = viewGroup.getMeasuredHeight();
                    }
                    int i4 = 0;
                    if (size == 0) {
                        size = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    }
                    int dp3 = AndroidUtilities.dp(50.0f);
                    int dp4 = ContactsAdapter.this.onlyUsers != 0 ? 0 : AndroidUtilities.dp(30.0f) + dp3;
                    if (ContactsAdapter.this.hasGps) {
                        dp4 += dp3;
                    }
                    if (!ContactsAdapter.this.isAdmin && !ContactsAdapter.this.needPhonebook) {
                        dp4 += dp3;
                    }
                    if (dp4 < size) {
                        i4 = size - dp4;
                    }
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), NUM), View.MeasureSpec.makeMeasureSpec(i4, NUM));
                }
            };
            frameLayout.addView(new ContactsEmptyView(this.mContext), LayoutHelper.createFrame(-2, -2, 17));
            userCell = frameLayout;
        } else {
            userCell = new ShadowSectionCell(this.mContext);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
            combinedDrawable.setFullsize(true);
            userCell.setBackgroundDrawable(combinedDrawable);
        }
        return new RecyclerListView.Holder(userCell);
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
        ArrayList<TLRPC$TL_contact> arrayList;
        int itemViewType = viewHolder.getItemViewType();
        boolean z = false;
        if (itemViewType == 0) {
            UserCell userCell = (UserCell) viewHolder.itemView;
            userCell.setAvatarPadding((this.sortType == 2 || this.disableSections) ? 6 : 58);
            if (this.sortType == 2) {
                arrayList = this.onlineContacts;
            } else {
                arrayList = (this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict).get((this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray).get(i - ((this.onlyUsers == 0 || this.isAdmin) ? 1 : 0)));
            }
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(arrayList.get(i2).user_id));
            userCell.setData(user, null, null, 0);
            LongSparseArray<?> longSparseArray = this.checkedMap;
            if (longSparseArray != null) {
                if (longSparseArray.indexOfKey(user.id) >= 0) {
                    z = true;
                }
                userCell.setChecked(z, !this.scrolling);
            }
            LongSparseArray<TLRPC$User> longSparseArray2 = this.ignoreUsers;
            if (longSparseArray2 == null) {
                return;
            }
            if (longSparseArray2.indexOfKey(user.id) >= 0) {
                userCell.setAlpha(0.5f);
            } else {
                userCell.setAlpha(1.0f);
            }
        } else if (itemViewType != 1) {
            if (itemViewType != 2) {
                return;
            }
            GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
            int i3 = this.sortType;
            if (i3 == 0) {
                graySectionCell.setText(LocaleController.getString("Contacts", R.string.Contacts));
            } else if (i3 == 1) {
                graySectionCell.setText(LocaleController.getString("SortedByName", R.string.SortedByName));
            } else {
                graySectionCell.setText(LocaleController.getString("SortedByLastSeen", R.string.SortedByLastSeen));
            }
        } else {
            TextCell textCell = (TextCell) viewHolder.itemView;
            if (i == 0) {
                if (this.needPhonebook) {
                    if (i2 == 0) {
                        textCell.setTextAndIcon(LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.msg_invite, false);
                        return;
                    } else if (i2 != 1) {
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("AddPeopleNearby", R.string.AddPeopleNearby), R.drawable.msg_location, false);
                        return;
                    }
                } else if (this.isAdmin) {
                    if (this.isChannel) {
                        textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", R.string.ChannelInviteViaLink), R.drawable.msg_link2, false);
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink), R.drawable.msg_link2, false);
                        return;
                    }
                } else if (i2 == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.msg_groups, false);
                    return;
                } else if (i2 == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.msg_secret, false);
                    return;
                } else if (i2 != 2) {
                    return;
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.msg_channel, false);
                    return;
                }
            }
            ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
            String str = contact.first_name;
            if (str != null && contact.last_name != null) {
                textCell.setText(contact.first_name + " " + contact.last_name, false);
            } else if (str != null && contact.last_name == null) {
                textCell.setText(str, false);
            } else {
                textCell.setText(contact.last_name, false);
            }
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter
    public int getItemViewType(int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (this.isEmpty) {
                return 4;
            }
            return i2 < hashMap.get(arrayList.get(i)).size() ? 0 : 3;
        }
        if (i == 0) {
            if (this.isAdmin) {
                if (i2 == 1) {
                    return 2;
                }
            } else if (this.needPhonebook) {
                boolean z = this.hasGps;
                if ((z && i2 == 2) || (!z && i2 == 1)) {
                    return this.isEmpty ? 5 : 2;
                }
            } else if (i2 == 3) {
                return this.isEmpty ? 5 : 2;
            }
        } else if (this.isEmpty) {
            return 4;
        } else {
            if (this.sortType != 2) {
                int i3 = i - 1;
                if (i3 < arrayList.size()) {
                    return i2 < hashMap.get(arrayList.get(i3)).size() ? 0 : 3;
                }
            } else if (i == 1) {
                return i2 < this.onlineContacts.size() ? 0 : 3;
            }
        }
        return 1;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
    public String getLetter(int i) {
        if (this.sortType != 2 && !this.isEmpty) {
            ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
            int sectionForPosition = getSectionForPosition(i);
            if (sectionForPosition == -1) {
                sectionForPosition = arrayList.size() - 1;
            }
            if (this.onlyUsers != 0 && !this.isAdmin) {
                if (sectionForPosition >= 0 && sectionForPosition < arrayList.size()) {
                    return arrayList.get(sectionForPosition);
                }
            } else if (sectionForPosition > 0 && sectionForPosition <= arrayList.size()) {
                return arrayList.get(sectionForPosition - 1);
            }
        }
        return null;
    }

    @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
    public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
        iArr[0] = (int) (getItemCount() * f);
        iArr[1] = 0;
    }
}
