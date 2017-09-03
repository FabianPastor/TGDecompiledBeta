package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsAdapter extends SelectionAdapter {
    private int currentCount;
    private int dialogsType;
    private boolean isOnlySelect;
    private Context mContext;
    private long openedDialogId;
    private ArrayList<Long> selectedDialogs;

    public DialogsAdapter(Context context, int type, boolean onlySelect) {
        this.mContext = context;
        this.dialogsType = type;
        this.isOnlySelect = onlySelect;
        if (onlySelect) {
            this.selectedDialogs = new ArrayList();
        }
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public boolean hasSelectedDialogs() {
        return (this.selectedDialogs == null || this.selectedDialogs.isEmpty()) ? false : true;
    }

    public void addOrRemoveSelectedDialog(long did, View cell) {
        if (this.selectedDialogs.contains(Long.valueOf(did))) {
            this.selectedDialogs.remove(Long.valueOf(did));
            if (cell instanceof DialogCell) {
                ((DialogCell) cell).setChecked(false, true);
                return;
            }
            return;
        }
        this.selectedDialogs.add(Long.valueOf(did));
        if (cell instanceof DialogCell) {
            ((DialogCell) cell).setChecked(true, true);
        }
    }

    public ArrayList<Long> getSelectedDialogs() {
        return this.selectedDialogs;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        if (current != getItemCount() || current == 1) {
            return true;
        }
        return false;
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
        }
        return null;
    }

    public int getItemCount() {
        int count = getDialogsArray().size();
        if (count == 0 && MessagesController.getInstance().loadingDialogs) {
            return 0;
        }
        if (!MessagesController.getInstance().dialogsEndReached) {
            count++;
        }
        this.currentCount = count;
        return count;
    }

    public TL_dialog getItem(int i) {
        ArrayList<TL_dialog> arrayList = getDialogsArray();
        if (i < 0 || i >= arrayList.size()) {
            return null;
        }
        return (TL_dialog) arrayList.get(i);
    }

    public void onViewAttachedToWindow(ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            ((DialogCell) holder.itemView).checkCurrentDialogIndex();
        }
    }

    public boolean isEnabled(ViewHolder holder) {
        return holder.getItemViewType() != 1;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = new DialogCell(this.mContext, this.isOnlySelect);
        } else if (viewType == 1) {
            view = new LoadingCell(this.mContext);
        }
        view.setLayoutParams(new LayoutParams(-1, -2));
        return new Holder(view);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        boolean z = true;
        if (viewHolder.getItemViewType() == 0) {
            boolean z2;
            DialogCell cell = viewHolder.itemView;
            if (i != getItemCount() - 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            cell.useSeparator = z2;
            TL_dialog dialog = getItem(i);
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                if (dialog.id != this.openedDialogId) {
                    z = false;
                }
                cell.setDialogSelected(z);
            }
            if (this.selectedDialogs != null) {
                cell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
            }
            cell.setDialog(dialog, i, this.dialogsType);
        }
    }

    public int getItemViewType(int i) {
        if (i == getDialogsArray().size()) {
            return 1;
        }
        return 0;
    }
}
