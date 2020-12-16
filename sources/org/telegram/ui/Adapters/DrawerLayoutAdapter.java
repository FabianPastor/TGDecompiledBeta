package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SideMenultItemAnimator;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShown;
    private boolean hasGps;
    private SideMenultItemAnimator itemAnimator;
    private ArrayList<Item> items = new ArrayList<>(11);
    private Context mContext;
    private DrawerProfileCell profileCell;

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator sideMenultItemAnimator) {
        this.mContext = context;
        this.itemAnimator = sideMenultItemAnimator;
        boolean z = true;
        this.accountsShown = (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true)) ? false : z;
        Theme.createDialogsResources(context);
        resetItems();
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable unused) {
            this.hasGps = false;
        }
    }

    private int getAccountRowsCount() {
        int size = this.accountNumbers.size() + 1;
        return this.accountNumbers.size() < 3 ? size + 1 : size;
    }

    public int getItemCount() {
        int size = this.items.size() + 2;
        return this.accountsShown ? size + getAccountRowsCount() : size;
    }

    public void setAccountsShown(boolean z, boolean z2) {
        if (this.accountsShown != z && !this.itemAnimator.isRunning()) {
            this.accountsShown = z;
            DrawerProfileCell drawerProfileCell = this.profileCell;
            if (drawerProfileCell != null) {
                drawerProfileCell.setAccountsShown(z, z2);
            }
            MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", this.accountsShown).commit();
            if (z2) {
                this.itemAnimator.setShouldClipChildren(false);
                if (this.accountsShown) {
                    notifyItemRangeInserted(2, getAccountRowsCount());
                } else {
                    notifyItemRangeRemoved(2, getAccountRowsCount());
                }
            } else {
                notifyDataSetChanged();
            }
        }
    }

    public boolean isAccountsShown() {
        return this.accountsShown;
    }

    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 3 || itemViewType == 4 || itemViewType == 5 || itemViewType == 6;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        EmptyCell emptyCell;
        if (i == 0) {
            DrawerProfileCell drawerProfileCell = new DrawerProfileCell(this.mContext);
            this.profileCell = drawerProfileCell;
            emptyCell = drawerProfileCell;
        } else if (i == 2) {
            emptyCell = new DividerCell(this.mContext);
        } else if (i == 3) {
            emptyCell = new DrawerActionCell(this.mContext);
        } else if (i == 4) {
            emptyCell = new DrawerUserCell(this.mContext);
        } else if (i != 5) {
            emptyCell = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
        } else {
            emptyCell = new DrawerAddCell(this.mContext);
        }
        emptyCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(emptyCell);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            ((DrawerProfileCell) viewHolder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShown);
        } else if (itemViewType == 3) {
            DrawerActionCell drawerActionCell = (DrawerActionCell) viewHolder.itemView;
            int i2 = i - 2;
            if (this.accountsShown) {
                i2 -= getAccountRowsCount();
            }
            this.items.get(i2).bind(drawerActionCell);
            drawerActionCell.setPadding(0, 0, 0, 0);
        } else if (itemViewType == 4) {
            ((DrawerUserCell) viewHolder.itemView).setAccount(this.accountNumbers.get(i - 2).intValue());
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        int i2 = i - 2;
        if (this.accountsShown) {
            if (i2 < this.accountNumbers.size()) {
                return 4;
            }
            if (this.accountNumbers.size() < 3) {
                if (i2 == this.accountNumbers.size()) {
                    return 5;
                }
                if (i2 == this.accountNumbers.size() + 1) {
                    return 2;
                }
            } else if (i2 == this.accountNumbers.size()) {
                return 2;
            }
            i2 -= getAccountRowsCount();
        }
        return this.items.get(i2) == null ? 2 : 3;
    }

    public void swapElements(int i, int i2) {
        int i3 = i - 2;
        int i4 = i2 - 2;
        if (i3 >= 0 && i4 >= 0 && i3 < this.accountNumbers.size() && i4 < this.accountNumbers.size()) {
            UserConfig instance = UserConfig.getInstance(this.accountNumbers.get(i3).intValue());
            UserConfig instance2 = UserConfig.getInstance(this.accountNumbers.get(i4).intValue());
            int i5 = instance.loginTime;
            instance.loginTime = instance2.loginTime;
            instance2.loginTime = i5;
            instance.saveConfig(false);
            instance2.saveConfig(false);
            Collections.swap(this.accountNumbers, i3, i4);
            notifyItemMoved(i, i2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0100  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void resetItems() {
        /*
            r13 = this;
            java.util.ArrayList<java.lang.Integer> r0 = r13.accountNumbers
            r0.clear()
            r0 = 0
        L_0x0006:
            r1 = 3
            if (r0 >= r1) goto L_0x001f
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r1 = r1.isClientActivated()
            if (r1 == 0) goto L_0x001c
            java.util.ArrayList<java.lang.Integer> r1 = r13.accountNumbers
            java.lang.Integer r2 = java.lang.Integer.valueOf(r0)
            r1.add(r2)
        L_0x001c:
            int r0 = r0 + 1
            goto L_0x0006
        L_0x001f:
            java.util.ArrayList<java.lang.Integer> r0 = r13.accountNumbers
            org.telegram.ui.Adapters.-$$Lambda$DrawerLayoutAdapter$pyljm01qtLl0BbFoRFh7-svzDDE r1 = org.telegram.ui.Adapters.$$Lambda$DrawerLayoutAdapter$pyljm01qtLl0BbFoRFh7svzDDE.INSTANCE
            java.util.Collections.sort(r0, r1)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            r0.clear()
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isClientActivated()
            if (r0 != 0) goto L_0x0038
            return
        L_0x0038:
            int r0 = org.telegram.ui.ActionBar.Theme.getEventType()
            r1 = 2131165654(0x7var_d6, float:1.7945531E38)
            r2 = 2
            r3 = 2131165665(0x7var_e1, float:1.7945554E38)
            if (r0 != 0) goto L_0x0061
            r0 = 2131165653(0x7var_d5, float:1.794553E38)
            r1 = 2131165643(0x7var_cb, float:1.7945509E38)
            r4 = 2131165632(0x7var_c0, float:1.7945487E38)
            r5 = 2131165624(0x7var_b8, float:1.794547E38)
            r6 = 2131165688(0x7var_f8, float:1.79456E38)
            r7 = 2131165660(0x7var_dc, float:1.7945543E38)
            r8 = 2131165656(0x7var_d8, float:1.7945535E38)
            r3 = 2131165656(0x7var_d8, float:1.7945535E38)
        L_0x005d:
            r8 = 2131165665(0x7var_e1, float:1.7945554E38)
            goto L_0x00c0
        L_0x0061:
            r4 = 1
            if (r0 != r4) goto L_0x0080
            r0 = 2131165651(0x7var_d3, float:1.7945525E38)
            r3 = 2131165641(0x7var_c9, float:1.7945505E38)
            r4 = 2131165630(0x7var_be, float:1.7945483E38)
            r5 = 2131165622(0x7var_b6, float:1.7945466E38)
            r6 = 2131165684(0x7var_f4, float:1.7945592E38)
            r7 = 2131165682(0x7var_f2, float:1.7945588E38)
            r8 = 2131165680(0x7var_f0, float:1.7945584E38)
            r1 = 2131165641(0x7var_c9, float:1.7945505E38)
            r3 = 2131165654(0x7var_d6, float:1.7945531E38)
            goto L_0x00c0
        L_0x0080:
            if (r0 != r2) goto L_0x009b
            r0 = 2131165652(0x7var_d4, float:1.7945527E38)
            r1 = 2131165642(0x7var_ca, float:1.7945507E38)
            r4 = 2131165631(0x7var_bf, float:1.7945485E38)
            r5 = 2131165623(0x7var_b7, float:1.7945468E38)
            r6 = 2131165687(0x7var_f7, float:1.7945598E38)
            r7 = 2131165659(0x7var_db, float:1.7945541E38)
            r3 = 2131165655(0x7var_d7, float:1.7945533E38)
            r8 = 2131165681(0x7var_f1, float:1.7945586E38)
            goto L_0x00c0
        L_0x009b:
            r0 = 2131165650(0x7var_d2, float:1.7945523E38)
            r4 = 2131165640(0x7var_c8, float:1.7945503E38)
            r5 = 2131165629(0x7var_bd, float:1.794548E38)
            r6 = 2131165677(0x7var_ed, float:1.7945578E38)
            r7 = 2131165683(0x7var_f3, float:1.794559E38)
            r8 = 2131165658(0x7var_da, float:1.794554E38)
            r1 = 2131165640(0x7var_c8, float:1.7945503E38)
            r3 = 2131165654(0x7var_d6, float:1.7945531E38)
            r4 = 2131165629(0x7var_bd, float:1.794548E38)
            r5 = 2131165677(0x7var_ed, float:1.7945578E38)
            r6 = 2131165683(0x7var_f3, float:1.794559E38)
            r7 = 2131165658(0x7var_da, float:1.794554E38)
            goto L_0x005d
        L_0x00c0:
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r9 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r10 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r11 = 2131626030(0x7f0e082e, float:1.8879285E38)
            java.lang.String r12 = "NewGroup"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            r10.<init>(r2, r11, r0)
            r9.add(r10)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r2 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r9 = 6
            r10 = 2131624920(0x7f0e03d8, float:1.8877033E38)
            java.lang.String r11 = "Contacts"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r2.<init>(r9, r10, r1)
            r0.add(r2)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 10
            r9 = 2131624580(0x7f0e0284, float:1.8876344E38)
            java.lang.String r10 = "Calls"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r1.<init>(r2, r9, r4)
            r0.add(r1)
            boolean r0 = r13.hasGps
            if (r0 == 0) goto L_0x0115
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 12
            r4 = 2131626637(0x7f0e0a8d, float:1.8880516E38)
            java.lang.String r9 = "PeopleNearby"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r9, r4)
            r1.<init>(r2, r4, r8)
            r0.add(r1)
        L_0x0115:
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 11
            r4 = 2131626994(0x7f0e0bf2, float:1.888124E38)
            java.lang.String r8 = "SavedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r8, r4)
            r1.<init>(r2, r4, r5)
            r0.add(r1)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 8
            r4 = 2131627147(0x7f0e0c8b, float:1.888155E38)
            java.lang.String r5 = "Settings"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.<init>(r2, r4, r6)
            r0.add(r1)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            r1 = 0
            r0.add(r1)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 7
            r4 = 2131625678(0x7f0e06ce, float:1.887857E38)
            java.lang.String r5 = "InviteFriends"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.<init>(r2, r4, r7)
            r0.add(r1)
            java.util.ArrayList<org.telegram.ui.Adapters.DrawerLayoutAdapter$Item> r0 = r13.items
            org.telegram.ui.Adapters.DrawerLayoutAdapter$Item r1 = new org.telegram.ui.Adapters.DrawerLayoutAdapter$Item
            r2 = 9
            r4 = 2131627361(0x7f0e0d61, float:1.8881984E38)
            java.lang.String r5 = "TelegramFAQ"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.<init>(r2, r4, r3)
            r0.add(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.DrawerLayoutAdapter.resetItems():void");
    }

    static /* synthetic */ int lambda$resetItems$0(Integer num, Integer num2) {
        long j = (long) UserConfig.getInstance(num.intValue()).loginTime;
        long j2 = (long) UserConfig.getInstance(num2.intValue()).loginTime;
        if (j > j2) {
            return 1;
        }
        return j < j2 ? -1 : 0;
    }

    public int getId(int i) {
        Item item;
        int i2 = i - 2;
        if (this.accountsShown) {
            i2 -= getAccountRowsCount();
        }
        if (i2 < 0 || i2 >= this.items.size() || (item = this.items.get(i2)) == null) {
            return -1;
        }
        return item.id;
    }

    public int getFirstAccountPosition() {
        return !this.accountsShown ? -1 : 2;
    }

    public int getLastAccountPosition() {
        if (!this.accountsShown) {
            return -1;
        }
        return this.accountNumbers.size() + 1;
    }

    private static class Item {
        public int icon;
        public int id;
        public String text;

        public Item(int i, String str, int i2) {
            this.icon = i2;
            this.id = i;
            this.text = str;
        }

        public void bind(DrawerActionCell drawerActionCell) {
            drawerActionCell.setTextAndIcon(this.text, this.icon);
        }
    }
}
