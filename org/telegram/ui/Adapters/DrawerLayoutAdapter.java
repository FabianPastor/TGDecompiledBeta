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
        this.mContext = context;
        Theme.createDialogsResources(context);
        resetItems();
    }

    public int getItemCount() {
        if (!this.accountsShowed) {
            return this.items.size();
        }
        int count = this.accountNumbers.size();
        if (count < 3) {
            count++;
        }
        return count + 2;
    }

    public void setAccountsShowed(boolean value) {
        this.accountsShowed = value;
        notifyDataSetChanged();
    }

    public boolean isAccountsShowed() {
        return this.accountsShowed;
    }

    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    public boolean isEnabled(ViewHolder holder) {
        return holder.getAdapterPosition() != 0 && (this.accountsShowed || holder.getItemViewType() == 3);
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                View drawerProfileCell = new DrawerProfileCell(this.mContext);
                drawerProfileCell.setOnArrowClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        DrawerLayoutAdapter.this.accountsShowed = ((DrawerProfileCell) v).isAccountsShowed();
                        DrawerLayoutAdapter.this.notifyDataSetChanged();
                    }
                });
                view = drawerProfileCell;
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
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getAccountInstance().getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
                holder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
                return;
            case 3:
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
        if (this.accountsShowed) {
            if (i == 0) {
                return 0;
            }
            if (i == 1) {
                return 1;
            }
            return i + -2 < UserConfig.getActivatedAccountsCount() ? 4 : 5;
        } else if (i == 0) {
            return 0;
        } else {
            if (i == 1) {
                return 1;
            }
            if (i == 5) {
                return 2;
            }
            return 3;
        }
    }

    private void resetItems() {
        if (this.accountsShowed) {
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
            return;
        }
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            this.items.add(null);
            this.items.add(null);
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
