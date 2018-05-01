package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
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

    /* renamed from: org.telegram.ui.Adapters.DrawerLayoutAdapter$1 */
    class C07841 implements OnClickListener {
        C07841() {
        }

        public void onClick(View view) {
            DrawerLayoutAdapter.this.setAccountsShowed(((DrawerProfileCell) view).isAccountsShowed(), true);
        }
    }

    /* renamed from: org.telegram.ui.Adapters.DrawerLayoutAdapter$2 */
    class C07852 implements Comparator<Integer> {
        C07852() {
        }

        public int compare(Integer num, Integer num2) {
            long j = (long) UserConfig.getInstance(num.intValue()).loginTime;
            num = (long) UserConfig.getInstance(num2.intValue()).loginTime;
            if (j > num) {
                return 1;
            }
            return j < num ? -1 : null;
        }
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

    public DrawerLayoutAdapter(Context context) {
        this.mContext = context;
        boolean z = true;
        if (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShowed", true)) {
            z = false;
        }
        this.accountsShowed = z;
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
        if (this.accountsShowed != z) {
            this.accountsShowed = z;
            if (this.profileCell) {
                this.profileCell.setAccountsShowed(this.accountsShowed);
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

    public boolean isEnabled(ViewHolder viewHolder) {
        viewHolder = viewHolder.getItemViewType();
        if (!(viewHolder == 3 || viewHolder == 4)) {
            if (viewHolder != 5) {
                return null;
            }
        }
        return true;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i != 0) {
            switch (i) {
                case 2:
                    viewGroup = new DividerCell(this.mContext);
                    break;
                case 3:
                    viewGroup = new DrawerActionCell(this.mContext);
                    break;
                case 4:
                    viewGroup = new DrawerUserCell(this.mContext);
                    break;
                case 5:
                    viewGroup = new DrawerAddCell(this.mContext);
                    break;
                default:
                    viewGroup = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
                    break;
            }
        }
        this.profileCell = new DrawerProfileCell(this.mContext);
        this.profileCell.setOnArrowClickListener(new C07841());
        viewGroup = this.profileCell;
        viewGroup.setLayoutParams(new LayoutParams(-1, -2));
        return new Holder(viewGroup);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType != 0) {
            switch (itemViewType) {
                case 3:
                    i -= 2;
                    if (this.accountsShowed) {
                        i -= getAccountRowsCount();
                    }
                    DrawerActionCell drawerActionCell = (DrawerActionCell) viewHolder.itemView;
                    ((Item) this.items.get(i)).bind(drawerActionCell);
                    drawerActionCell.setPadding(0, 0, 0, 0);
                    return;
                case 4:
                    ((DrawerUserCell) viewHolder.itemView).setAccount(((Integer) this.accountNumbers.get(i - 2)).intValue());
                    return;
                default:
                    return;
            }
        }
        ((DrawerProfileCell) viewHolder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShowed);
        viewHolder.itemView.setBackgroundColor(Theme.getColor(Theme.key_avatar_backgroundActionBarBlue));
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
        return i == 3 ? 2 : 3;
    }

    private void resetItems() {
        this.accountNumbers.clear();
        for (int i = 0; i < 3; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(i));
            }
        }
        Collections.sort(this.accountNumbers, new C07852());
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            this.items.add(new Item(2, LocaleController.getString("NewGroup", C0446R.string.NewGroup), C0446R.drawable.menu_newgroup));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", C0446R.string.NewSecretChat), C0446R.drawable.menu_secret));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", C0446R.string.NewChannel), C0446R.drawable.menu_broadcast));
            this.items.add(null);
            this.items.add(new Item(6, LocaleController.getString("Contacts", C0446R.string.Contacts), C0446R.drawable.menu_contacts));
            this.items.add(new Item(11, LocaleController.getString("SavedMessages", C0446R.string.SavedMessages), C0446R.drawable.menu_saved));
            this.items.add(new Item(10, LocaleController.getString("Calls", C0446R.string.Calls), C0446R.drawable.menu_calls));
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", C0446R.string.InviteFriends), C0446R.drawable.menu_invite));
            this.items.add(new Item(8, LocaleController.getString("Settings", C0446R.string.Settings), C0446R.drawable.menu_settings));
            this.items.add(new Item(9, LocaleController.getString("TelegramFAQ", C0446R.string.TelegramFAQ), C0446R.drawable.menu_help));
        }
    }

    public int getId(int i) {
        i -= 2;
        if (this.accountsShowed) {
            i -= getAccountRowsCount();
        }
        int i2 = -1;
        if (i >= 0) {
            if (i < this.items.size()) {
                Item item = (Item) this.items.get(i);
                if (item != null) {
                    i2 = item.id;
                }
                return i2;
            }
        }
        return -1;
    }
}
