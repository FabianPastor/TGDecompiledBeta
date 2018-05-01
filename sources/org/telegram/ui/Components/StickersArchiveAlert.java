package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.StickerSetCovered;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ArchivedStickerSetCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.StickersActivity;

public class StickersArchiveAlert extends Builder {
    private int currentType;
    private boolean ignoreLayout;
    private BaseFragment parentFragment;
    private int reqId;
    private int scrollOffsetY;
    private ArrayList<StickerSetCovered> stickerSets;

    /* renamed from: org.telegram.ui.Components.StickersArchiveAlert$1 */
    class C13161 implements OnClickListener {
        C13161() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersArchiveAlert$2 */
    class C13172 implements OnClickListener {
        C13172() {
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            StickersArchiveAlert.this.parentFragment.presentFragment(new StickersActivity(StickersArchiveAlert.this.currentType));
            dialogInterface.dismiss();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        Context context;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            viewGroup = new ArchivedStickerSetCell(this.context, false);
            viewGroup.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(82.0f)));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ArchivedStickerSetCell archivedStickerSetCell = (ArchivedStickerSetCell) viewHolder.itemView;
            StickerSetCovered stickerSetCovered = (StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(i);
            boolean z = true;
            if (i == StickersArchiveAlert.this.stickerSets.size() - 1) {
                z = false;
            }
            archivedStickerSetCell.setStickersSet(stickerSetCovered, z);
        }
    }

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList<StickerSetCovered> arrayList) {
        super(context);
        StickerSetCovered stickerSetCovered = (StickerSetCovered) arrayList.get(0);
        if (stickerSetCovered.set.masks) {
            this.currentType = 1;
            setTitle(LocaleController.getString("ArchivedMasksAlertTitle", C0446R.string.ArchivedMasksAlertTitle));
        } else {
            this.currentType = 0;
            setTitle(LocaleController.getString("ArchivedStickersAlertTitle", C0446R.string.ArchivedStickersAlertTitle));
        }
        this.stickerSets = new ArrayList(arrayList);
        this.parentFragment = baseFragment;
        baseFragment = new LinearLayout(context);
        baseFragment.setOrientation(1);
        setView(baseFragment);
        arrayList = new TextView(context);
        arrayList.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        arrayList.setTextSize(1, 16.0f);
        arrayList.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(23.0f), 0);
        if (stickerSetCovered.set.masks) {
            arrayList.setText(LocaleController.getString("ArchivedMasksAlertInfo", C0446R.string.ArchivedMasksAlertInfo));
        } else {
            arrayList.setText(LocaleController.getString("ArchivedStickersAlertInfo", C0446R.string.ArchivedStickersAlertInfo));
        }
        baseFragment.addView(arrayList, LayoutHelper.createLinear(-2, -2));
        arrayList = new RecyclerListView(context);
        arrayList.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        arrayList.setAdapter(new ListAdapter(context));
        arrayList.setVerticalScrollBarEnabled(false);
        arrayList.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        arrayList.setGlowColor(-657673);
        baseFragment.addView(arrayList, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString("Close", C0446R.string.Close), new C13161());
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString("Settings", C0446R.string.Settings), new C13172());
        }
    }
}
