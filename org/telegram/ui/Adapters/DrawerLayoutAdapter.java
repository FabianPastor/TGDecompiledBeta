package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.EmptyCell;

public class DrawerLayoutAdapter extends BaseAdapter {
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
        resetItems();
    }

    public boolean areAllItemsEnabled() {
        return false;
    }

    public boolean isEnabled(int i) {
        return (i == 1 || i == 5) ? false : true;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int i) {
        return null;
    }

    public long getItemId(int i) {
        if (i >= this.items.size()) {
            return 0;
        }
        Item item = (Item) this.items.get(i);
        if (item != null) {
            return (long) item.id;
        }
        return (long) (-i);
    }

    public boolean hasStableIds() {
        return true;
    }

    public void notifyDataSetChanged() {
        resetItems();
        super.notifyDataSetChanged();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        int type = getItemViewType(i);
        if (type == 0) {
            DrawerProfileCell drawerProfileCell;
            if (view == null) {
                drawerProfileCell = new DrawerProfileCell(this.mContext);
                view = drawerProfileCell;
            } else {
                drawerProfileCell = (DrawerProfileCell) view;
            }
            drawerProfileCell.setUser(MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())));
            return view;
        } else if (type == 1) {
            if (view == null) {
                return new EmptyCell(this.mContext, AndroidUtilities.dp(8.0f));
            }
            return view;
        } else if (type == 2) {
            if (view == null) {
                return new DividerCell(this.mContext);
            }
            return view;
        } else if (type != 3) {
            return view;
        } else {
            if (view == null) {
                view = new DrawerActionCell(this.mContext);
            }
            DrawerActionCell actionCell = (DrawerActionCell) view;
            Item item = (Item) this.items.get(i);
            if (item == null) {
                return view;
            }
            item.bind(actionCell);
            return view;
        }
    }

    public int getItemViewType(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return 1;
        }
        if (i == 5) {
            return 2;
        }
        return 3;
    }

    public int getViewTypeCount() {
        return 4;
    }

    public boolean isEmpty() {
        return this.items.size() > 0;
    }

    private void resetItems() {
        this.items.clear();
        if (UserConfig.isClientActivated()) {
            this.items.add(null);
            this.items.add(null);
            this.items.add(new Item(2, LocaleController.getString("NewGroup", R.string.NewGroup), R.drawable.menu_newgroup));
            this.items.add(new Item(3, LocaleController.getString("NewSecretChat", R.string.NewSecretChat), R.drawable.menu_secret));
            this.items.add(new Item(4, LocaleController.getString("NewChannel", R.string.NewChannel), R.drawable.menu_broadcast));
            this.items.add(null);
            this.items.add(new Item(6, LocaleController.getString("Contacts", R.string.Contacts), R.drawable.menu_contacts));
            if (MessagesController.getInstance().callsEnabled) {
                this.items.add(new Item(10, LocaleController.getString("Calls", R.string.Calls), R.drawable.menu_calls));
            }
            this.items.add(new Item(7, LocaleController.getString("InviteFriends", R.string.InviteFriends), R.drawable.menu_invite));
            this.items.add(new Item(8, LocaleController.getString("Settings", R.string.Settings), R.drawable.menu_settings));
            this.items.add(new Item(9, LocaleController.getString("TelegramFaq", R.string.TelegramFaq), R.drawable.menu_help));
        }
    }
}
