package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.RecyclerListView;

public class MessagesSearchAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private Context mContext;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();

    public long getItemId(int i) {
        return (long) i;
    }

    public MessagesSearchAdapter(Context context) {
        this.mContext = context;
    }

    public void notifyDataSetChanged() {
        this.searchResultMessages = MediaDataController.getInstance(this.currentAccount).getFoundMessageObjects();
        super.notifyDataSetChanged();
    }

    public int getItemCount() {
        return this.searchResultMessages.size();
    }

    public Object getItem(int i) {
        if (i < 0 || i >= this.searchResultMessages.size()) {
            return null;
        }
        return this.searchResultMessages.get(i);
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == 0;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i != 0) {
            view = i != 1 ? null : new LoadingCell(this.mContext);
        } else {
            view = new DialogCell(this.mContext, false, true);
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            dialogCell.useSeparator = true;
            MessageObject messageObject = (MessageObject) getItem(i);
            dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, true);
        }
    }

    public int getItemViewType(int i) {
        return i < this.searchResultMessages.size() ? 0 : 1;
    }
}
