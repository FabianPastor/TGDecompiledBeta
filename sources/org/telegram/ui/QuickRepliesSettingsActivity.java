package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class QuickRepliesSettingsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int explanationRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int reply1Row;
    /* access modifiers changed from: private */
    public int reply2Row;
    /* access modifiers changed from: private */
    public int reply3Row;
    /* access modifiers changed from: private */
    public int reply4Row;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public EditTextSettingsCell[] textCells = new EditTextSettingsCell[4];

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.reply1Row = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.reply2Row = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.reply3Row = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.reply4Row = i3;
        this.rowCount = i4 + 1;
        this.explanationRow = i4;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("VoipQuickReplies", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    QuickRepliesSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        return this.fragmentView;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        SharedPreferences.Editor edit = getParentActivity().getSharedPreferences("mainconfig", 0).edit();
        while (true) {
            EditTextSettingsCell[] editTextSettingsCellArr = this.textCells;
            if (i < editTextSettingsCellArr.length) {
                if (editTextSettingsCellArr[i] != null) {
                    String obj = editTextSettingsCellArr[i].getTextView().getText().toString();
                    if (!TextUtils.isEmpty(obj)) {
                        edit.putString("quick_reply_msg" + (i + 1), obj);
                    } else {
                        edit.remove("quick_reply_msg" + (i + 1));
                    }
                }
                i++;
            } else {
                edit.commit();
                return;
            }
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return QuickRepliesSettingsActivity.this.rowCount;
        }

        /* JADX WARNING: Removed duplicated region for block: B:23:0x0086  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r8, int r9) {
            /*
                r7 = this;
                int r0 = r8.getItemViewType()
                if (r0 == 0) goto L_0x00b1
                r1 = 1
                if (r0 == r1) goto L_0x00ac
                r2 = 4
                java.lang.String r3 = "mainconfig"
                r4 = 0
                if (r0 == r2) goto L_0x008b
                switch(r0) {
                    case 9: goto L_0x0014;
                    case 10: goto L_0x0014;
                    case 11: goto L_0x0014;
                    case 12: goto L_0x0014;
                    default: goto L_0x0012;
                }
            L_0x0012:
                goto L_0x00cf
            L_0x0014:
                android.view.View r8 = r8.itemView
                org.telegram.ui.Cells.EditTextSettingsCell r8 = (org.telegram.ui.Cells.EditTextSettingsCell) r8
                org.telegram.ui.QuickRepliesSettingsActivity r0 = org.telegram.ui.QuickRepliesSettingsActivity.this
                int r0 = r0.reply1Row
                r2 = 0
                if (r9 != r0) goto L_0x0030
                r0 = 2131627150(0x7f0e0c8e, float:1.8881556E38)
                java.lang.String r2 = "QuickReplyDefault1"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r0 = "quick_reply_msg1"
            L_0x002c:
                r6 = r2
                r2 = r0
                r0 = r6
                goto L_0x006d
            L_0x0030:
                org.telegram.ui.QuickRepliesSettingsActivity r0 = org.telegram.ui.QuickRepliesSettingsActivity.this
                int r0 = r0.reply2Row
                if (r9 != r0) goto L_0x0044
                r0 = 2131627151(0x7f0e0c8f, float:1.8881558E38)
                java.lang.String r2 = "QuickReplyDefault2"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r0 = "quick_reply_msg2"
                goto L_0x002c
            L_0x0044:
                org.telegram.ui.QuickRepliesSettingsActivity r0 = org.telegram.ui.QuickRepliesSettingsActivity.this
                int r0 = r0.reply3Row
                if (r9 != r0) goto L_0x0058
                r0 = 2131627152(0x7f0e0CLASSNAME, float:1.888156E38)
                java.lang.String r2 = "QuickReplyDefault3"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r0 = "quick_reply_msg3"
                goto L_0x002c
            L_0x0058:
                org.telegram.ui.QuickRepliesSettingsActivity r0 = org.telegram.ui.QuickRepliesSettingsActivity.this
                int r0 = r0.reply4Row
                if (r9 != r0) goto L_0x006c
                r0 = 2131627153(0x7f0e0CLASSNAME, float:1.8881562E38)
                java.lang.String r2 = "QuickReplyDefault4"
                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r0)
                java.lang.String r0 = "quick_reply_msg4"
                goto L_0x002c
            L_0x006c:
                r0 = r2
            L_0x006d:
                org.telegram.ui.QuickRepliesSettingsActivity r5 = org.telegram.ui.QuickRepliesSettingsActivity.this
                android.app.Activity r5 = r5.getParentActivity()
                android.content.SharedPreferences r3 = r5.getSharedPreferences(r3, r4)
                java.lang.String r5 = ""
                java.lang.String r2 = r3.getString(r2, r5)
                org.telegram.ui.QuickRepliesSettingsActivity r3 = org.telegram.ui.QuickRepliesSettingsActivity.this
                int r3 = r3.reply4Row
                if (r9 == r3) goto L_0x0086
                goto L_0x0087
            L_0x0086:
                r1 = 0
            L_0x0087:
                r8.setTextAndHint(r2, r0, r1)
                goto L_0x00cf
            L_0x008b:
                android.view.View r8 = r8.itemView
                org.telegram.ui.Cells.TextCheckCell r8 = (org.telegram.ui.Cells.TextCheckCell) r8
                r9 = 2131624245(0x7f0e0135, float:1.8875664E38)
                java.lang.String r0 = "AllowCustomQuickReply"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r0, r9)
                org.telegram.ui.QuickRepliesSettingsActivity r0 = org.telegram.ui.QuickRepliesSettingsActivity.this
                android.app.Activity r0 = r0.getParentActivity()
                android.content.SharedPreferences r0 = r0.getSharedPreferences(r3, r4)
                java.lang.String r2 = "quick_reply_allow_custom"
                boolean r0 = r0.getBoolean(r2, r1)
                r8.setTextAndCheck(r9, r0, r4)
                goto L_0x00cf
            L_0x00ac:
                android.view.View r8 = r8.itemView
                org.telegram.ui.Cells.TextSettingsCell r8 = (org.telegram.ui.Cells.TextSettingsCell) r8
                goto L_0x00cf
            L_0x00b1:
                android.view.View r8 = r8.itemView
                org.telegram.ui.Cells.TextInfoPrivacyCell r8 = (org.telegram.ui.Cells.TextInfoPrivacyCell) r8
                android.content.Context r9 = r7.mContext
                r0 = 2131165444(0x7var_, float:1.7945105E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r9 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r9, (int) r0, (java.lang.String) r1)
                r8.setBackgroundDrawable(r9)
                r9 = 2131628275(0x7f0e10f3, float:1.8883838E38)
                java.lang.String r0 = "VoipQuickRepliesExplain"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r0, r9)
                r8.setText(r9)
            L_0x00cf:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.QuickRepliesSettingsActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == QuickRepliesSettingsActivity.this.reply1Row || adapterPosition == QuickRepliesSettingsActivity.this.reply2Row || adapterPosition == QuickRepliesSettingsActivity.this.reply3Row || adapterPosition == QuickRepliesSettingsActivity.this.reply4Row;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 1) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 4) {
                switch (i) {
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        view = new EditTextSettingsCell(this.mContext);
                        view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        QuickRepliesSettingsActivity.this.textCells[i - 9] = view;
                        break;
                    default:
                        view = null;
                        break;
                }
            } else {
                view = new TextCheckCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i == QuickRepliesSettingsActivity.this.explanationRow) {
                return 0;
            }
            if (i == QuickRepliesSettingsActivity.this.reply1Row || i == QuickRepliesSettingsActivity.this.reply2Row || i == QuickRepliesSettingsActivity.this.reply3Row || i == QuickRepliesSettingsActivity.this.reply4Row) {
                return (i - QuickRepliesSettingsActivity.this.reply1Row) + 9;
            }
            return 1;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        return arrayList;
    }
}
