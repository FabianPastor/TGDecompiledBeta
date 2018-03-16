package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class QuickRepliesSettingsActivity extends BaseFragment {
    private int allowCustomRow;
    private int explanationRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int reply1Row;
    private int reply2Row;
    private int reply3Row;
    private int reply4Row;
    private int rowCount;
    private int sectionHeaderRow;
    private EditTextSettingsCell[] textCells = new EditTextSettingsCell[4];

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return QuickRepliesSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 1:
                    TextSettingsCell textCell = holder.itemView;
                    return;
                case 2:
                    HeaderCell headerCell = holder.itemView;
                    if (position == QuickRepliesSettingsActivity.this.sectionHeaderRow) {
                        headerCell.setText(LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies));
                        return;
                    }
                    return;
                case 4:
                    holder.itemView.setTextAndCheck(LocaleController.getString("AllowCustomQuickReply", R.string.AllowCustomQuickReply), QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getBoolean("quick_reply_allow_custom", true), false);
                    return;
                case 9:
                case 10:
                case 11:
                case 12:
                    EditTextSettingsCell textCell2 = holder.itemView;
                    String settingsKey = null;
                    String defValue = null;
                    if (position == QuickRepliesSettingsActivity.this.reply1Row) {
                        settingsKey = "quick_reply_msg1";
                        defValue = LocaleController.getString("QuickReplayDefault1", R.string.QuickReplyDefault1);
                    } else if (position == QuickRepliesSettingsActivity.this.reply2Row) {
                        settingsKey = "quick_reply_msg2";
                        defValue = LocaleController.getString("QuickReplayDefault2", R.string.QuickReplyDefault2);
                    } else if (position == QuickRepliesSettingsActivity.this.reply3Row) {
                        settingsKey = "quick_reply_msg3";
                        defValue = LocaleController.getString("QuickReplayDefault3", R.string.QuickReplyDefault3);
                    } else if (position == QuickRepliesSettingsActivity.this.reply4Row) {
                        settingsKey = "quick_reply_msg4";
                        defValue = LocaleController.getString("QuickReplayDefault4", R.string.QuickReplyDefault4);
                    }
                    textCell2.setTextAndHint(QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getString(settingsKey, TtmlNode.ANONYMOUS_REGION_ID), defValue, true);
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == QuickRepliesSettingsActivity.this.reply1Row || position == QuickRepliesSettingsActivity.this.reply2Row || position == QuickRepliesSettingsActivity.this.reply3Row || position == QuickRepliesSettingsActivity.this.reply4Row || position == QuickRepliesSettingsActivity.this.allowCustomRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 9:
                case 10:
                case 11:
                case 12:
                    view = new EditTextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    QuickRepliesSettingsActivity.this.textCells[viewType - 9] = (EditTextSettingsCell) view;
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == QuickRepliesSettingsActivity.this.explanationRow) {
                return 0;
            }
            if (position == QuickRepliesSettingsActivity.this.reply1Row || position == QuickRepliesSettingsActivity.this.reply2Row || position == QuickRepliesSettingsActivity.this.reply3Row || position == QuickRepliesSettingsActivity.this.reply4Row) {
                return (position - QuickRepliesSettingsActivity.this.reply1Row) + 9;
            }
            if (position == QuickRepliesSettingsActivity.this.sectionHeaderRow) {
                return 2;
            }
            if (position == QuickRepliesSettingsActivity.this.allowCustomRow) {
                return 4;
            }
            return 1;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.sectionHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.reply1Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.reply2Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.reply3Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.reply4Row = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.allowCustomRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.explanationRow = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    QuickRepliesSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (position == QuickRepliesSettingsActivity.this.allowCustomRow) {
                    boolean z;
                    TextCheckCell cell = (TextCheckCell) view;
                    if (cell.isChecked()) {
                        z = false;
                    } else {
                        z = true;
                    }
                    cell.setChecked(z);
                    QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).edit().putBoolean("quick_reply_allow_custom", cell.isChecked()).commit();
                }
            }
        });
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        Editor editor = getParentActivity().getSharedPreferences("mainconfig", 0).edit();
        for (int i = 0; i < this.textCells.length; i++) {
            if (this.textCells[i] != null) {
                String text = this.textCells[i].getTextView().getText().toString();
                if (TextUtils.isEmpty(text)) {
                    editor.remove("quick_reply_msg" + (i + 1));
                } else {
                    editor.putString("quick_reply_msg" + (i + 1), text);
                }
            }
        }
        editor.commit();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[15];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextSettingsCell.class, TextDetailSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        return themeDescriptionArr;
    }
}
