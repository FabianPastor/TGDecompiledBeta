package org.telegram.ui.Adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Components.RecyclerListView;

public class MessagesSearchAdapter extends RecyclerListView.SelectionAdapter {
    private int currentAccount = UserConfig.selectedAccount;
    private Context mContext;
    private final Theme.ResourcesProvider resourcesProvider;
    private ArrayList<MessageObject> searchResultMessages = new ArrayList<>();

    public long getItemId(int i) {
        return (long) i;
    }

    public MessagesSearchAdapter(Context context, Theme.ResourcesProvider resourcesProvider2) {
        this.resourcesProvider = resourcesProvider2;
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

    /* JADX WARNING: type inference failed for: r8v4, types: [org.telegram.ui.Cells.LoadingCell] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r8, int r9) {
        /*
            r7 = this;
            if (r9 == 0) goto L_0x000f
            r8 = 1
            if (r9 == r8) goto L_0x0007
            r8 = 0
            goto L_0x001e
        L_0x0007:
            org.telegram.ui.Cells.LoadingCell r8 = new org.telegram.ui.Cells.LoadingCell
            android.content.Context r9 = r7.mContext
            r8.<init>(r9)
            goto L_0x001e
        L_0x000f:
            org.telegram.ui.Cells.DialogCell r8 = new org.telegram.ui.Cells.DialogCell
            r1 = 0
            android.content.Context r2 = r7.mContext
            r3 = 0
            r4 = 1
            int r5 = r7.currentAccount
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r0 = r8
            r0.<init>(r1, r2, r3, r4, r5, r6)
        L_0x001e:
            androidx.recyclerview.widget.RecyclerView$LayoutParams r9 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
            r0 = -1
            r1 = -2
            r9.<init>((int) r0, (int) r1)
            r8.setLayoutParams(r9)
            org.telegram.ui.Components.RecyclerListView$Holder r9 = new org.telegram.ui.Components.RecyclerListView$Holder
            r9.<init>(r8)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MessagesSearchAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
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
