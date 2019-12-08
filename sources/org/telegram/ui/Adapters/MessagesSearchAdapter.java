package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class MessagesSearchAdapter extends SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private Context mContext;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList();

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
        return (i < 0 || i >= this.searchResultMessages.size()) ? null : this.searchResultMessages.get(i);
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View loadingCell = i != 0 ? i != 1 ? null : new LoadingCell(this.mContext) : new DialogCell(this.mContext, false, true);
        loadingCell.setLayoutParams(new LayoutParams(-1, -2));
        return new Holder(loadingCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
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
