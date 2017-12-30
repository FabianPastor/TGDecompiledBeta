package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DrawerLayoutAdapter extends SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList();
    private boolean accountsShowed;
    private ArrayList<Item> items = new ArrayList(11);
    private Context mContext;
    private DrawerProfileCell profileCell;

    private class Item {
        public int icon;
        public int id;
        public String text;

        public Item(int id, String text, int icon) {
            this.icon = icon;
            this.id = id;
            this.text = text;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(this.text, this.icon);
        }
    }

    public DrawerLayoutAdapter(Context context) {
        boolean z = true;
        this.mContext = context;
        if (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShowed", true)) {
            z = false;
        }
        this.accountsShowed = z;
        Theme.createDialogsResources(context);
        resetItems();
    }

    private int getAccountRowsCount() {
        int count = this.accountNumbers.size() + 1;
        if (this.accountNumbers.size() < 3) {
            return count + 1;
        }
        return count;
    }

    public int getItemCount() {
        int count = this.items.size() + 2;
        if (this.accountsShowed) {
            return count + getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShowed(boolean value, boolean animated) {
        if (this.accountsShowed != value) {
            this.accountsShowed = value;
            if (this.profileCell != null) {
                this.profileCell.setAccountsShowed(this.accountsShowed);
            }
            MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShowed", this.accountsShowed).commit();
            if (!animated) {
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

    public boolean isEnabled(ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                this.profileCell = new DrawerProfileCell(this.mContext);
                this.profileCell.setOnArrowClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        DrawerLayoutAdapter.this.setAccountsShowed(((DrawerProfileCell) v).isAccountsShowed(), true);
                    }
                });
                view = this.profileCell;
                break;
            case 2:
                view = new DividerCell(this.mContext);
                break;
            case 3:
                view = new DrawerActionCell(this.mContext);
                break;
            case 4:
                view = new DrawerUserCell(this.mContext);
                break;
            case 5:
                view = new DrawerAddCell(this.mContext);
                break;
            default:
                view = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
                break;
        }
        view.setLayoutParams(new LayoutParams(-1, -2));
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                return;
            case 3:
                position -= 2;
                if (this.accountsShowed) {
                    position -= getAccountRowsCount();
                }
                DrawerActionCell drawerActionCell = holder.itemView;
                ((Item) this.items.get(position)).bind(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                return;
            case 4:
                holder.itemView.setAccount(((Integer) this.accountNumbers.get(position - 2)).intValue());
                return;
            default:
                return;
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        i -= 2;
        if (this.accountsShowed) {
            if (i < this.accountNumbers.size()) {
                return 4;
            }
            if (this.accountNumbers.size() < 3) {
                if (i == this.accountNumbers.size()) {
                    return 5;
                }
                if (i == this.accountNumbers.size() + 1) {
                    return 2;
                }
            } else if (i == this.accountNumbers.size()) {
                return 2;
            }
            i -= getAccountRowsCount();
        }
        if (i == 3) {
            return 2;
        }
        return 3;
    }

    private void resetItems() {
        this.accountNumbers.clear();
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(a));
            }
        }
        Collections.sort(this.accountNumbers, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                long l1 = (long) UserConfig.getInstance(o1.intValue()).loginTime;
                long l2 = (long) UserConfig.getInstance(o2.intValue()).loginTime;
                if (l1 > l2) {
                    return 1;
                }
                if (l1 < l2) {
                    return -1;
                }
                return 0;
            }
        });
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
            this.items.add(null);
            this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
            this.items.add(new Item(11, LocaleController.getString("SavedMessages", R.string.SavedMessages), R.drawable.menu_saved));
            this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite));
            this.items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
            this.items.add(new Item(9, LocaleController.getString("TelegramFAQ", R.string.TelegramFAQ), R.drawable.menu_help));
        }
    }

    public int getId(int position) {
        position -= 2;
        if (this.accountsShowed) {
            position -= getAccountRowsCount();
        }
        if (position < 0 || position >= this.items.size()) {
            return -1;
        }
        Item item = (Item) this.items.get(position);
        if (item != null) {
            return item.id;
        }
        return -1;
    }
}
