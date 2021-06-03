package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.Comparator;
import j$.util.function.Function;
import j$.util.function.ToDoubleFunction;
import j$.util.function.ToIntFunction;
import j$.util.function.ToLongFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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

public class ContactsAdapter extends RecyclerListView.SectionsAdapter {
    private SparseArray<?> checkedMap;
    private int currentAccount = UserConfig.selectedAccount;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public boolean hasGps;
    private SparseArray<TLRPC$User> ignoreUsers;
    /* access modifiers changed from: private */
    public boolean isAdmin;
    private boolean isChannel;
    private boolean isEmpty;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private ArrayList<TLRPC$TL_contact> onlineContacts;
    /* access modifiers changed from: private */
    public int onlyUsers;
    private boolean scrolling;
    private int sortType;

    public ContactsAdapter(Context context, int i, boolean z, SparseArray<TLRPC$User> sparseArray, int i2, boolean z2) {
        this.mContext = context;
        this.onlyUsers = i;
        this.needPhonebook = z;
        this.ignoreUsers = sparseArray;
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
                int i2 = UserConfig.getInstance(this.currentAccount).clientUserId;
                int i3 = 0;
                int size = this.onlineContacts.size();
                while (true) {
                    if (i3 >= size) {
                        break;
                    } else if (this.onlineContacts.get(i3).user_id == i2) {
                        this.onlineContacts.remove(i3);
                        break;
                    } else {
                        i3++;
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
                Collections.sort(this.onlineContacts, new Object(currentTime) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final int compare(Object obj, Object obj2) {
                        return ContactsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, this.f$1, (TLRPC$TL_contact) obj, (TLRPC$TL_contact) obj2);
                    }

                    public /* synthetic */ Comparator reversed() {
                        return Comparator.CC.$default$reversed(this);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, function);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(Function function, java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing(this, function, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparing(java.util.Comparator comparator) {
                        return Comparator.CC.$default$thenComparing((java.util.Comparator) this, comparator);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingDouble(ToDoubleFunction toDoubleFunction) {
                        return Comparator.CC.$default$thenComparingDouble(this, toDoubleFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingInt(ToIntFunction toIntFunction) {
                        return Comparator.CC.$default$thenComparingInt(this, toIntFunction);
                    }

                    public /* synthetic */ java.util.Comparator thenComparingLong(ToLongFunction toLongFunction) {
                        return Comparator.CC.$default$thenComparingLong(this, toLongFunction);
                    }
                });
                notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002b  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x003d A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0048 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0053 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x005c A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static /* synthetic */ int lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController r2, int r3, org.telegram.tgnet.TLRPC$TL_contact r4, org.telegram.tgnet.TLRPC$TL_contact r5) {
        /*
            int r5 = r5.user_id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$User r5 = r2.getUser(r5)
            int r4 = r4.user_id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r4)
            r4 = 50000(0xCLASSNAME, float:7.0065E-41)
            r0 = 0
            if (r5 == 0) goto L_0x0028
            boolean r1 = r5.self
            if (r1 == 0) goto L_0x0021
            int r5 = r3 + r4
            goto L_0x0029
        L_0x0021:
            org.telegram.tgnet.TLRPC$UserStatus r5 = r5.status
            if (r5 == 0) goto L_0x0028
            int r5 = r5.expires
            goto L_0x0029
        L_0x0028:
            r5 = 0
        L_0x0029:
            if (r2 == 0) goto L_0x0038
            boolean r1 = r2.self
            if (r1 == 0) goto L_0x0031
            int r3 = r3 + r4
            goto L_0x0039
        L_0x0031:
            org.telegram.tgnet.TLRPC$UserStatus r2 = r2.status
            if (r2 == 0) goto L_0x0038
            int r3 = r2.expires
            goto L_0x0039
        L_0x0038:
            r3 = 0
        L_0x0039:
            r2 = -1
            r4 = 1
            if (r5 <= 0) goto L_0x0046
            if (r3 <= 0) goto L_0x0046
            if (r5 <= r3) goto L_0x0042
            return r4
        L_0x0042:
            if (r5 >= r3) goto L_0x0045
            return r2
        L_0x0045:
            return r0
        L_0x0046:
            if (r5 >= 0) goto L_0x0051
            if (r3 >= 0) goto L_0x0051
            if (r5 <= r3) goto L_0x004d
            return r4
        L_0x004d:
            if (r5 >= r3) goto L_0x0050
            return r2
        L_0x0050:
            return r0
        L_0x0051:
            if (r5 >= 0) goto L_0x0055
            if (r3 > 0) goto L_0x0059
        L_0x0055:
            if (r5 != 0) goto L_0x005a
            if (r3 == 0) goto L_0x005a
        L_0x0059:
            return r2
        L_0x005a:
            if (r3 >= 0) goto L_0x005e
            if (r5 > 0) goto L_0x0062
        L_0x005e:
            if (r3 != 0) goto L_0x0063
            if (r5 == 0) goto L_0x0063
        L_0x0062:
            return r4
        L_0x0063:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.ContactsAdapter.lambda$sortOnlineContacts$0(org.telegram.messenger.MessagesController, int, org.telegram.tgnet.TLRPC$TL_contact, org.telegram.tgnet.TLRPC$TL_contact):int");
    }

    public Object getItem(int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers != 0 && !this.isAdmin) {
            if (i < arrayList.size()) {
                ArrayList arrayList2 = hashMap.get(arrayList.get(i));
                if (i2 < arrayList2.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC$TL_contact) arrayList2.get(i2)).user_id));
                }
            }
            return null;
        } else if (i == 0) {
            return null;
        } else {
            if (this.sortType != 2) {
                int i3 = i - 1;
                if (i3 < arrayList.size()) {
                    ArrayList arrayList3 = hashMap.get(arrayList.get(i3));
                    if (i2 < arrayList3.size()) {
                        return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((TLRPC$TL_contact) arrayList3.get(i2)).user_id));
                    }
                    return null;
                }
            } else if (i == 1) {
                if (i2 < this.onlineContacts.size()) {
                    return MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.onlineContacts.get(i2).user_id));
                }
                return null;
            }
            if (!this.needPhonebook || i2 < 0 || i2 >= ContactsController.getInstance(this.currentAccount).phoneBookContacts.size()) {
                return null;
            }
            return ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
        }
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                if (this.isAdmin) {
                    if (i2 != 1) {
                        return true;
                    }
                    return false;
                } else if (this.needPhonebook) {
                    boolean z = this.hasGps;
                    if ((!z || i2 == 2) && (z || i2 == 1)) {
                        return false;
                    }
                    return true;
                } else if (i2 != 3) {
                    return true;
                } else {
                    return false;
                }
            } else if (this.isEmpty) {
                return false;
            } else {
                if (this.sortType != 2) {
                    int i3 = i - 1;
                    if (i3 >= arrayList.size() || i2 < hashMap.get(arrayList.get(i3)).size()) {
                        return true;
                    }
                    return false;
                } else if (i != 1 || i2 < this.onlineContacts.size()) {
                    return true;
                } else {
                    return false;
                }
                return true;
            }
        } else if (!this.isEmpty && i2 < hashMap.get(arrayList.get(i)).size()) {
            return true;
        } else {
            return false;
        }
    }

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
                if (this.hasGps) {
                    return 3;
                }
                return 2;
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
                    if (this.onlineContacts.isEmpty()) {
                        return 0;
                    }
                    return this.onlineContacts.size() + 1;
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

    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view;
        if (i == 0) {
            view = new UserCell(this.mContext, 58, 1, false);
        } else if (i == 1) {
            view = new TextCell(this.mContext);
        } else if (i == 2) {
            view = new GraySectionCell(this.mContext);
        } else if (i == 3) {
            view = new DividerCell(this.mContext);
            float f = 28.0f;
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : 72.0f);
            int dp2 = AndroidUtilities.dp(8.0f);
            if (LocaleController.isRTL) {
                f = 72.0f;
            }
            view.setPadding(dp, dp2, AndroidUtilities.dp(f), AndroidUtilities.dp(8.0f));
        } else if (i != 4) {
            view = new ShadowSectionCell(this.mContext);
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
            combinedDrawable.setFullsize(true);
            view.setBackgroundDrawable(combinedDrawable);
        } else {
            AnonymousClass1 r7 = new FrameLayout(this.mContext) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    int size = View.MeasureSpec.getSize(i2);
                    if (size == 0) {
                        size = viewGroup.getMeasuredHeight();
                    }
                    int i3 = 0;
                    if (size == 0) {
                        size = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                    }
                    int dp = AndroidUtilities.dp(50.0f);
                    int dp2 = ContactsAdapter.this.onlyUsers != 0 ? 0 : AndroidUtilities.dp(30.0f) + dp;
                    if (ContactsAdapter.this.hasGps) {
                        dp2 += dp;
                    }
                    if (!ContactsAdapter.this.isAdmin && !ContactsAdapter.this.needPhonebook) {
                        dp2 += dp;
                    }
                    if (dp2 < size) {
                        i3 = size - dp2;
                    }
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(i3, NUM));
                }
            };
            r7.addView(new ContactsEmptyView(this.mContext), LayoutHelper.createFrame(-2, -2, 17));
            view = r7;
        }
        return new RecyclerListView.Holder(view);
    }

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
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(arrayList.get(i2).user_id));
            userCell.setData(user, (CharSequence) null, (CharSequence) null, 0);
            SparseArray<?> sparseArray = this.checkedMap;
            if (sparseArray != null) {
                if (sparseArray.indexOfKey(user.id) >= 0) {
                    z = true;
                }
                userCell.setChecked(z, !this.scrolling);
            }
            SparseArray<TLRPC$User> sparseArray2 = this.ignoreUsers;
            if (sparseArray2 == null) {
                return;
            }
            if (sparseArray2.indexOfKey(user.id) >= 0) {
                userCell.setAlpha(0.5f);
            } else {
                userCell.setAlpha(1.0f);
            }
        } else if (itemViewType == 1) {
            TextCell textCell = (TextCell) viewHolder.itemView;
            if (i != 0) {
                ContactsController.Contact contact = ContactsController.getInstance(this.currentAccount).phoneBookContacts.get(i2);
                String str = contact.first_name;
                if (str != null && contact.last_name != null) {
                    textCell.setText(contact.first_name + " " + contact.last_name, false);
                } else if (str == null || contact.last_name != null) {
                    textCell.setText(contact.last_name, false);
                } else {
                    textCell.setText(str, false);
                }
            } else if (this.needPhonebook) {
                if (i2 == 0) {
                    textCell.setTextAndIcon(LocaleController.getString("InviteFriends", NUM), NUM, false);
                } else if (i2 == 1) {
                    textCell.setTextAndIcon(LocaleController.getString("AddPeopleNearby", NUM), NUM, false);
                }
            } else if (this.isAdmin) {
                if (this.isChannel) {
                    textCell.setTextAndIcon(LocaleController.getString("ChannelInviteViaLink", NUM), NUM, false);
                } else {
                    textCell.setTextAndIcon(LocaleController.getString("InviteToGroupByLink", NUM), NUM, false);
                }
            } else if (i2 == 0) {
                textCell.setTextAndIcon(LocaleController.getString("NewGroup", NUM), NUM, false);
            } else if (i2 == 1) {
                textCell.setTextAndIcon(LocaleController.getString("NewSecretChat", NUM), NUM, false);
            } else if (i2 == 2) {
                textCell.setTextAndIcon(LocaleController.getString("NewChannel", NUM), NUM, false);
            }
        } else if (itemViewType == 2) {
            GraySectionCell graySectionCell = (GraySectionCell) viewHolder.itemView;
            int i3 = this.sortType;
            if (i3 == 0) {
                graySectionCell.setText(LocaleController.getString("Contacts", NUM));
            } else if (i3 == 1) {
                graySectionCell.setText(LocaleController.getString("SortedByName", NUM));
            } else {
                graySectionCell.setText(LocaleController.getString("SortedByLastSeen", NUM));
            }
        }
    }

    public int getItemViewType(int i, int i2) {
        HashMap<String, ArrayList<TLRPC$TL_contact>> hashMap = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).usersMutualSectionsDict : ContactsController.getInstance(this.currentAccount).usersSectionsDict;
        ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
        if (this.onlyUsers == 0 || this.isAdmin) {
            if (i == 0) {
                if (this.isAdmin) {
                    if (i2 == 1) {
                        return 2;
                    }
                } else if (this.needPhonebook) {
                    boolean z = this.hasGps;
                    if ((z && i2 == 2) || (!z && i2 == 1)) {
                        if (this.isEmpty) {
                            return 5;
                        }
                        return 2;
                    }
                } else if (i2 == 3) {
                    if (this.isEmpty) {
                        return 5;
                    }
                    return 2;
                }
            } else if (this.isEmpty) {
                return 4;
            } else {
                if (this.sortType != 2) {
                    int i3 = i - 1;
                    if (i3 < arrayList.size()) {
                        if (i2 < hashMap.get(arrayList.get(i3)).size()) {
                            return 0;
                        }
                        return 3;
                    }
                } else if (i == 1) {
                    if (i2 < this.onlineContacts.size()) {
                        return 0;
                    }
                    return 3;
                }
            }
            return 1;
        } else if (this.isEmpty) {
            return 4;
        } else {
            if (i2 < hashMap.get(arrayList.get(i)).size()) {
                return 0;
            }
            return 3;
        }
    }

    public String getLetter(int i) {
        if (this.sortType != 2 && !this.isEmpty) {
            ArrayList<String> arrayList = this.onlyUsers == 2 ? ContactsController.getInstance(this.currentAccount).sortedUsersMutualSectionsArray : ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray;
            int sectionForPosition = getSectionForPosition(i);
            if (sectionForPosition == -1) {
                sectionForPosition = arrayList.size() - 1;
            }
            if (this.onlyUsers == 0 || this.isAdmin) {
                if (sectionForPosition > 0 && sectionForPosition <= arrayList.size()) {
                    return arrayList.get(sectionForPosition - 1);
                }
            } else if (sectionForPosition >= 0 && sectionForPosition < arrayList.size()) {
                return arrayList.get(sectionForPosition);
            }
        }
        return null;
    }

    public int getPositionForScrollProgress(float f) {
        return (int) (((float) getItemCount()) * f);
    }
}
