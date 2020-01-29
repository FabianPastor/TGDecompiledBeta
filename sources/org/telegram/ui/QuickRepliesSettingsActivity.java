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
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.reply1Row = i;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.reply2Row = i2;
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.reply3Row = i3;
        int i4 = this.rowCount;
        this.rowCount = i4 + 1;
        this.reply4Row = i4;
        int i5 = this.rowCount;
        this.rowCount = i5 + 1;
        this.explanationRow = i5;
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
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
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

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                } else if (itemViewType != 4) {
                    switch (itemViewType) {
                        case 9:
                        case 10:
                        case 11:
                        case 12:
                            EditTextSettingsCell editTextSettingsCell = (EditTextSettingsCell) viewHolder.itemView;
                            String str2 = null;
                            if (i == QuickRepliesSettingsActivity.this.reply1Row) {
                                str2 = LocaleController.getString("QuickReplyDefault1", NUM);
                                str = "quick_reply_msg1";
                            } else if (i == QuickRepliesSettingsActivity.this.reply2Row) {
                                str2 = LocaleController.getString("QuickReplyDefault2", NUM);
                                str = "quick_reply_msg2";
                            } else if (i == QuickRepliesSettingsActivity.this.reply3Row) {
                                str2 = LocaleController.getString("QuickReplyDefault3", NUM);
                                str = "quick_reply_msg3";
                            } else if (i == QuickRepliesSettingsActivity.this.reply4Row) {
                                str2 = LocaleController.getString("QuickReplyDefault4", NUM);
                                str = "quick_reply_msg4";
                            } else {
                                str = null;
                            }
                            String string = QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getString(str, "");
                            if (i == QuickRepliesSettingsActivity.this.reply4Row) {
                                z = false;
                            }
                            editTextSettingsCell.setTextAndHint(string, str2, z);
                            return;
                        default:
                            return;
                    }
                } else {
                    ((TextCheckCell) viewHolder.itemView).setTextAndCheck(LocaleController.getString("AllowCustomQuickReply", NUM), QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getBoolean("quick_reply_allow_custom", true), false);
                }
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                textInfoPrivacyCell.setText(LocaleController.getString("VoipQuickRepliesExplain", NUM));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == QuickRepliesSettingsActivity.this.reply1Row || adapterPosition == QuickRepliesSettingsActivity.this.reply2Row || adapterPosition == QuickRepliesSettingsActivity.this.reply3Row || adapterPosition == QuickRepliesSettingsActivity.this.reply4Row;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            EditTextSettingsCell editTextSettingsCell;
            if (i == 0) {
                editTextSettingsCell = new TextInfoPrivacyCell(this.mContext);
            } else if (i == 1) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                editTextSettingsCell = textSettingsCell;
            } else if (i != 4) {
                switch (i) {
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                        EditTextSettingsCell editTextSettingsCell2 = new EditTextSettingsCell(this.mContext);
                        editTextSettingsCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                        QuickRepliesSettingsActivity.this.textCells[i - 9] = editTextSettingsCell2;
                        editTextSettingsCell = editTextSettingsCell2;
                        break;
                    default:
                        editTextSettingsCell = null;
                        break;
                }
            } else {
                TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                textCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                editTextSettingsCell = textCheckCell;
            }
            editTextSettingsCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(editTextSettingsCell);
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

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText")};
    }
}
