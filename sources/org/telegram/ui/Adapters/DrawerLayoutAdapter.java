package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
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
import org.telegram.ui.Components.SideMenultItemAnimator;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter {
    private ArrayList<Integer> accountNumbers = new ArrayList<>();
    private boolean accountsShown;
    private boolean hasGps;
    private SideMenultItemAnimator itemAnimator;
    private ArrayList<Item> items = new ArrayList<>(11);
    private Context mContext;
    private DrawerProfileCell profileCell;

    public DrawerLayoutAdapter(Context context, SideMenultItemAnimator animator) {
        this.mContext = context;
        this.itemAnimator = animator;
        boolean z = true;
        this.accountsShown = (UserConfig.getActivatedAccountsCount() <= 1 || !MessagesController.getGlobalMainSettings().getBoolean("accountsShown", true)) ? false : z;
        Theme.createCommonDialogResources(context);
        resetItems();
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
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
        if (this.accountsShown) {
            return count + getAccountRowsCount();
        }
        return count;
    }

    public void setAccountsShown(boolean value, boolean animated) {
        if (this.accountsShown != value && !this.itemAnimator.isRunning()) {
            this.accountsShown = value;
            DrawerProfileCell drawerProfileCell = this.profileCell;
            if (drawerProfileCell != null) {
                drawerProfileCell.setAccountsShown(value, animated);
            }
            MessagesController.getGlobalMainSettings().edit().putBoolean("accountsShown", this.accountsShown).commit();
            if (animated) {
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

    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int itemType = holder.getItemViewType();
        return itemType == 3 || itemType == 4 || itemType == 5 || itemType == 6;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                DrawerProfileCell drawerProfileCell = new DrawerProfileCell(this.mContext);
                this.profileCell = drawerProfileCell;
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
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ((DrawerProfileCell) holder.itemView).setUser(MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId())), this.accountsShown);
                return;
            case 3:
                DrawerActionCell drawerActionCell = (DrawerActionCell) holder.itemView;
                int position2 = position - 2;
                if (this.accountsShown) {
                    position2 -= getAccountRowsCount();
                }
                this.items.get(position2).bind(drawerActionCell);
                drawerActionCell.setPadding(0, 0, 0, 0);
                return;
            case 4:
                ((DrawerUserCell) holder.itemView).setAccount(this.accountNumbers.get(position - 2).intValue());
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

    public void swapElements(int fromIndex, int toIndex) {
        int idx1 = fromIndex - 2;
        int idx2 = toIndex - 2;
        if (idx1 >= 0 && idx2 >= 0 && idx1 < this.accountNumbers.size() && idx2 < this.accountNumbers.size()) {
            UserConfig userConfig1 = UserConfig.getInstance(this.accountNumbers.get(idx1).intValue());
            UserConfig userConfig2 = UserConfig.getInstance(this.accountNumbers.get(idx2).intValue());
            int tempLoginTime = userConfig1.loginTime;
            userConfig1.loginTime = userConfig2.loginTime;
            userConfig2.loginTime = tempLoginTime;
            userConfig1.saveConfig(false);
            userConfig2.saveConfig(false);
            Collections.swap(this.accountNumbers, idx1, idx2);
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    private void resetItems() {
        int peopleNearbyIcon;
        int helpIcon;
        int inviteIcon;
        int settingsIcon;
        int savedIcon;
        int callsIcon;
        int contactsIcon;
        int newGroupIcon;
        this.accountNumbers.clear();
        for (int a = 0; a < 3; a++) {
            if (UserConfig.getInstance(a).isClientActivated()) {
                this.accountNumbers.add(Integer.valueOf(a));
            }
        }
        Collections.sort(this.accountNumbers, DrawerLayoutAdapter$$ExternalSyntheticLambda0.INSTANCE);
        this.items.clear();
        if (UserConfig.getInstance(UserConfig.selectedAccount).isClientActivated()) {
            int eventType = Theme.getEventType();
            if (eventType == 0) {
                newGroupIcon = NUM;
                contactsIcon = NUM;
                callsIcon = NUM;
                savedIcon = NUM;
                settingsIcon = NUM;
                inviteIcon = NUM;
                helpIcon = NUM;
                peopleNearbyIcon = NUM;
            } else if (eventType == 1) {
                newGroupIcon = NUM;
                contactsIcon = NUM;
                callsIcon = NUM;
                savedIcon = NUM;
                settingsIcon = NUM;
                inviteIcon = NUM;
                helpIcon = NUM;
                peopleNearbyIcon = NUM;
            } else if (eventType == 2) {
                newGroupIcon = NUM;
                contactsIcon = NUM;
                callsIcon = NUM;
                savedIcon = NUM;
                settingsIcon = NUM;
                inviteIcon = NUM;
                helpIcon = NUM;
                peopleNearbyIcon = NUM;
            } else {
                newGroupIcon = NUM;
                contactsIcon = NUM;
                callsIcon = NUM;
                savedIcon = NUM;
                settingsIcon = NUM;
                inviteIcon = NUM;
                helpIcon = NUM;
                peopleNearbyIcon = NUM;
            }
            this.items.add(new Item(2, LocaleController.getString("NewGroup", NUM), newGroupIcon));
            this.items.add(new Item(6, LocaleController.getString("Contacts", NUM), contactsIcon));
            this.items.add(new Item(10, LocaleController.getString("Calls", NUM), callsIcon));
            if (this.hasGps) {
                this.items.add(new Item(12, LocaleController.getString("PeopleNearby", NUM), peopleNearbyIcon));
            }
            this.items.add(new Item(11, LocaleController.getString("SavedMessages", NUM), savedIcon));
            this.items.add(new Item(8, LocaleController.getString("Settings", NUM), settingsIcon));
            this.items.add((Object) null);
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", NUM), inviteIcon));
            this.items.add(new Item(13, LocaleController.getString("TelegramFeatures", NUM), helpIcon));
        }
    }

    static /* synthetic */ int lambda$resetItems$0(Integer o1, Integer o2) {
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

    public int getId(int position) {
        Item item;
        int position2 = position - 2;
        if (this.accountsShown) {
            position2 -= getAccountRowsCount();
        }
        if (position2 < 0 || position2 >= this.items.size() || (item = this.items.get(position2)) == null) {
            return -1;
        }
        return item.id;
    }

    public int getFirstAccountPosition() {
        if (!this.accountsShown) {
            return -1;
        }
        return 2;
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

        public Item(int id2, String text2, int icon2) {
            this.icon = icon2;
            this.id = id2;
            this.text = text2;
        }

        public void bind(DrawerActionCell actionCell) {
            actionCell.setTextAndIcon(this.id, this.text, this.icon);
        }
    }
}
