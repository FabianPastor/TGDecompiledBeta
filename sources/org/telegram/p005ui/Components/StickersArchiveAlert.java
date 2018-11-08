package org.telegram.p005ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0541R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.Cells.ArchivedStickerSetCell;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.StickersActivity;
import org.telegram.tgnet.TLRPC.StickerSetCovered;

/* renamed from: org.telegram.ui.Components.StickersArchiveAlert */
public class StickersArchiveAlert extends Builder {
    private int currentType;
    private boolean ignoreLayout;
    private BaseFragment parentFragment;
    private int reqId;
    private int scrollOffsetY;
    private ArrayList<StickerSetCovered> stickerSets;

    /* renamed from: org.telegram.ui.Components.StickersArchiveAlert$1 */
    class C15801 implements OnClickListener {
        C15801() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersArchiveAlert$2 */
    class C15812 implements OnClickListener {
        C15812() {
        }

        public void onClick(DialogInterface dialog, int which) {
            StickersArchiveAlert.this.parentFragment.presentFragment(new StickersActivity(StickersArchiveAlert.this.currentType));
            dialog.dismiss();
        }
    }

    /* renamed from: org.telegram.ui.Components.StickersArchiveAlert$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        public int getItemCount() {
            return StickersArchiveAlert.this.stickerSets.size();
        }

        public boolean isEnabled(ViewHolder holder) {
            return false;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ArchivedStickerSetCell(this.context, false);
            view.setLayoutParams(new LayoutParams(-1, AndroidUtilities.m10dp(82.0f)));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            ((ArchivedStickerSetCell) holder.itemView).setStickersSet((StickerSetCovered) StickersArchiveAlert.this.stickerSets.get(position), position != StickersArchiveAlert.this.stickerSets.size() + -1);
        }
    }

    public StickersArchiveAlert(Context context, BaseFragment baseFragment, ArrayList<StickerSetCovered> sets) {
        super(context);
        StickerSetCovered set = (StickerSetCovered) sets.get(0);
        if (set.set.masks) {
            this.currentType = 1;
            setTitle(LocaleController.getString("ArchivedMasksAlertTitle", C0541R.string.ArchivedMasksAlertTitle));
        } else {
            this.currentType = 0;
            setTitle(LocaleController.getString("ArchivedStickersAlertTitle", C0541R.string.ArchivedStickersAlertTitle));
        }
        this.stickerSets = new ArrayList(sets);
        this.parentFragment = baseFragment;
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(1);
        setView(container);
        TextView textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 16.0f);
        textView.setPadding(AndroidUtilities.m10dp(23.0f), AndroidUtilities.m10dp(10.0f), AndroidUtilities.m10dp(23.0f), 0);
        if (set.set.masks) {
            textView.setText(LocaleController.getString("ArchivedMasksAlertInfo", C0541R.string.ArchivedMasksAlertInfo));
        } else {
            textView.setText(LocaleController.getString("ArchivedStickersAlertInfo", C0541R.string.ArchivedStickersAlertInfo));
        }
        container.addView(textView, LayoutHelper.createLinear(-2, -2));
        RecyclerListView listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        listView.setAdapter(new ListAdapter(context));
        listView.setVerticalScrollBarEnabled(false);
        listView.setPadding(AndroidUtilities.m10dp(10.0f), 0, AndroidUtilities.m10dp(10.0f), 0);
        listView.setGlowColor(-657673);
        container.addView(listView, LayoutHelper.createLinear(-1, -2, 0.0f, 10.0f, 0.0f, 0.0f));
        setNegativeButton(LocaleController.getString("Close", C0541R.string.Close), new C15801());
        if (this.parentFragment != null) {
            setPositiveButton(LocaleController.getString("Settings", C0541R.string.Settings), new C15812());
        }
    }
}
