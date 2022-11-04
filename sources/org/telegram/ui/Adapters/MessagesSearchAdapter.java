package org.telegram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class MessagesSearchAdapter extends RecyclerListView.SelectionAdapter {
    private Context mContext;
    private final Theme.ResourcesProvider resourcesProvider;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();
    private int currentAccount = UserConfig.selectedAccount;

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public long getItemId(int i) {
        return i;
    }

    public MessagesSearchAdapter(Context context, Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        this.mContext = context;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyDataSetChanged() {
        this.searchResultMessages = MediaDataController.getInstance(this.currentAccount).getFoundMessageObjects();
        super.notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.searchResultMessages.size();
    }

    public Object getItem(int i) {
        if (i < 0 || i >= this.searchResultMessages.size()) {
            return null;
        }
        return this.searchResultMessages.get(i);
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder */
    public RecyclerView.ViewHolder mo1821onCreateViewHolder(ViewGroup viewGroup, int i) {
        View dialogCell;
        if (i == 0) {
            dialogCell = new DialogCell(null, this.mContext, false, true, this.currentAccount, this.resourcesProvider);
        } else {
            dialogCell = i != 1 ? null : new LoadingCell(this.mContext);
        }
        dialogCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(dialogCell);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 0) {
            DialogCell dialogCell = (DialogCell) viewHolder.itemView;
            dialogCell.useSeparator = true;
            MessageObject messageObject = (MessageObject) getItem(i);
            dialogCell.setDialog(messageObject.getDialogId(), messageObject, messageObject.messageOwner.date, true, false);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return i < this.searchResultMessages.size() ? 0 : 1;
    }
}
