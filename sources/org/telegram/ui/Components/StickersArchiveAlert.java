package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StickersActivity;

public class StickersArchiveAlert extends AlertDialog.Builder {
    private int currentType;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> stickerSets;

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList<TLRPC$StickerSetCovered> arrayList) {
        super(context);
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = arrayList.get(0);
        if (tLRPC$StickerSetCovered.set.masks) {
            this.currentType = 1;
            setTitle(LocaleController.getString("ArchivedMasksAlertTitle", R.string.ArchivedMasksAlertTitle));
        } else {
            this.currentType = 0;
            setTitle(LocaleController.getString("ArchivedStickersAlertTitle", R.string.ArchivedStickersAlertTitle));
        }
        this.stickerSets = new ArrayList<>(arrayList);
        this.parentFragment = baseFragment;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        setView(linearLayout);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(23.0f), 0);
        if (tLRPC$StickerSetCovered.set.masks) {
            textView.setText(LocaleController.getString("ArchivedMasksAlertInfo", R.string.ArchivedMasksAlertInfo));
        } else {
            textView.setText(LocaleController.getString("ArchivedStickersAlertInfo", R.string.ArchivedStickersAlertInfo));
        }
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        recyclerListView.setAdapter(new ListAdapter(context));
        recyclerListView.setVerticalScrollBarEnabled(false);
        recyclerListView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        recyclerListView.setGlowColor(-657673);
        linearLayout.addView(recyclerListView, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString("Close", R.string.Close), StickersArchiveAlert$$ExternalSyntheticLambda1.INSTANCE);
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString("Settings", R.string.Settings), new StickersArchiveAlert$$ExternalSyntheticLambda0(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DialogInterface dialogInterface, int i) {
        this.parentFragment.presentFragment(new StickersActivity(this.currentType, (ArrayList<TLRPC$TL_messages_stickerSet>) null));
        dialogInterface.dismiss();
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        Context context;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context2) {
            this.context = context2;
        }

        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ArchivedStickerSetCell archivedStickerSetCell = new ArchivedStickerSetCell(this.context, false);
            archivedStickerSetCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new RecyclerListView.Holder(archivedStickerSetCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) viewHolder.itemView;
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(i);
            boolean z = true;
            if (i == StickersArchiveAlert.this.stickerSets.size() - 1) {
                z = false;
            }
            archivedStickerSetCell.setStickersSet(tLRPC$StickerSetCovered, z);
        }
    }
}
