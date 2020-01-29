package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
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

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShowed;
    private RecyclerView.ItemAnimator itemAnimator;
    private ArrayList<Item> items = new ArrayList<>(11);
    private Context mContext;
    private DrawerProfileCell profileCell;

    public DrawerLayoutAdapter(Context context, RecyclerView.ItemAnimator itemAnimator2) {
        this.mContext = context;
        this.itemAnimator = itemAnimator2;
        boolean z = true;
        this.accountsShowed = (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShowed", true)) ? false : z;
        Theme.createDialogsResources(context);
        resetItems();
    }

    private int getAccountRowsCount() {
        int size = this.accountNumbers.size() + 1;
        return this.accountNumbers.size() < 3 ? size + 1 : size;
    }

    public int getItemCount() {
        int size = this.items.size() + 2;
        return this.accountsShowed ? size + getAccountRowsCount() : size;
    }

    public void setAccountsShowed(boolean z, boolean z2) {
        if (this.accountsShowed != z && !this.itemAnimator.isRunning()) {
            this.accountsShowed = z;
            DrawerProfileCell drawerProfileCell = this.profileCell;
            if (drawerProfileCell != null) {
                drawerProfileCell.setAccountsShowed(this.accountsShowed, z2);
            }
            MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShowed", this.accountsShowed).commit();
            if (!z2) {
                notifyDataSetChanged();
            } else if (this.accountsShowed) {
                notifyItemRangeInserted(2, getAccountRowsCount());
            } else {
                notifyItemRangeRemoved(2, getAccountRowsCount());
            }
        }
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 3 || itemViewType == 4 || itemViewType == 5;
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
            ((DrawerProfileCell) viewHolder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
        } else if (itemViewType == 3) {
            int i2 = i - 2;
            if (this.accountsShowed) {
                i2 -= getAccountRowsCount();
            }
            DrawerActionCell drawerActionCell = (DrawerActionCell) viewHolder.itemView;
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
        if (this.accountsShowed) {
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

    private void resetItems() {
        this.accountNumbers.clear();
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(i));
            }
        }
        Collections.sort(this.accountNumbers, $$Lambda$DrawerLayoutAdapter$mi1sw6PViLc4Y6s0MqsHrAJKuc.INSTANCE);
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            if (Theme.getEventType() == 0) {
                this.items.add(new Item(2, LocaleController.getString("NewGroup", NUM), NUM));
                this.items.add(new Item(3, LocaleController.getString("NewSecretChat", NUM), NUM));
                this.items.add(new Item(4, LocaleController.getString("NewChannel", NUM), NUM));
                this.items.add(new Item(6, LocaleController.getString("Contacts", NUM), NUM));
                this.items.add(new Item(10, LocaleController.getString("Calls", NUM), NUM));
                this.items.add(new Item(11, LocaleController.getString("SavedMessages", NUM), NUM));
                this.items.add(new Item(8, LocaleController.getString("Settings", NUM), NUM));
                this.items.add((Object) null);
                this.items.add(new Item(7, LocaleController.getString("InviteFriends", NUM), NUM));
                this.items.add(new Item(9, LocaleController.getString("TelegramFAQ", NUM), NUM));
                return;
            }
            this.items.add(new Item(2, LocaleController.getString("NewGroup", NUM), NUM));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", NUM), NUM));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", NUM), NUM));
            this.items.add(new Item(6, LocaleController.getString("Contacts", NUM), NUM));
            this.items.add(new Item(10, LocaleController.getString("Calls", NUM), NUM));
            this.items.add(new Item(11, LocaleController.getString("SavedMessages", NUM), NUM));
            this.items.add(new Item(8, LocaleController.getString("Settings", NUM), NUM));
            this.items.add((Object) null);
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", NUM), NUM));
            this.items.add(new Item(9, LocaleController.getString("TelegramFAQ", NUM), NUM));
        }
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
        if (this.accountsShowed) {
            i2 -= getAccountRowsCount();
        }
        if (i2 < 0 || i2 >= this.items.size() || (item = this.items.get(i2)) == null) {
            return -1;
        }
        return item.id;
    }

    private class Item {
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
