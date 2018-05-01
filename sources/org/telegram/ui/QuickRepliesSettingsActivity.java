package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
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
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class QuickRepliesSettingsActivity extends BaseFragment {
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

    /* renamed from: org.telegram.ui.QuickRepliesSettingsActivity$1 */
    class C22671 extends ActionBarMenuOnItemClick {
        C22671() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                QuickRepliesSettingsActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.QuickRepliesSettingsActivity$2 */
    class C22682 implements OnItemClickListener {
        public void onItemClick(View view, int i) {
        }

        C22682() {
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return QuickRepliesSettingsActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 4) {
                switch (itemViewType) {
                    case 0:
                        TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        textInfoPrivacyCell.setText(LocaleController.getString("VoipQuickRepliesExplain", C0446R.string.VoipQuickRepliesExplain));
                        return;
                    case 1:
                        TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                        return;
                    case 2:
                        HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                        if (i == QuickRepliesSettingsActivity.this.sectionHeaderRow) {
                            headerCell.setText(LocaleController.getString("VoipQuickReplies", C0446R.string.VoipQuickReplies));
                            return;
                        }
                        return;
                    default:
                        switch (itemViewType) {
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                                EditTextSettingsCell editTextSettingsCell = (EditTextSettingsCell) viewHolder.itemView;
                                String str = null;
                                if (i == QuickRepliesSettingsActivity.this.reply1Row) {
                                    str = "quick_reply_msg1";
                                    i = LocaleController.getString("QuickReplyDefault1", C0446R.string.QuickReplyDefault1);
                                } else if (i == QuickRepliesSettingsActivity.this.reply2Row) {
                                    str = "quick_reply_msg2";
                                    i = LocaleController.getString("QuickReplyDefault2", C0446R.string.QuickReplyDefault2);
                                } else if (i == QuickRepliesSettingsActivity.this.reply3Row) {
                                    str = "quick_reply_msg3";
                                    i = LocaleController.getString("QuickReplyDefault3", C0446R.string.QuickReplyDefault3);
                                } else if (i == QuickRepliesSettingsActivity.this.reply4Row) {
                                    str = "quick_reply_msg4";
                                    i = LocaleController.getString("QuickReplyDefault4", C0446R.string.QuickReplyDefault4);
                                } else {
                                    i = 0;
                                }
                                editTextSettingsCell.setTextAndHint(QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getString(str, TtmlNode.ANONYMOUS_REGION_ID), i, true);
                                return;
                            default:
                                return;
                        }
                }
            }
            ((TextCheckCell) viewHolder.itemView).setTextAndCheck(LocaleController.getString("AllowCustomQuickReply", C0446R.string.AllowCustomQuickReply), QuickRepliesSettingsActivity.this.getParentActivity().getSharedPreferences("mainconfig", 0).getBoolean("quick_reply_allow_custom", true), false);
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            if (!(viewHolder == QuickRepliesSettingsActivity.this.reply1Row || viewHolder == QuickRepliesSettingsActivity.this.reply2Row || viewHolder == QuickRepliesSettingsActivity.this.reply3Row)) {
                if (viewHolder != QuickRepliesSettingsActivity.this.reply4Row) {
                    return null;
                }
            }
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 4) {
                switch (i) {
                    case 0:
                        viewGroup = new TextInfoPrivacyCell(this.mContext);
                        break;
                    case 1:
                        viewGroup = new TextSettingsCell(this.mContext);
                        viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        break;
                    case 2:
                        viewGroup = new HeaderCell(this.mContext);
                        viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        break;
                    default:
                        switch (i) {
                            case 9:
                            case 10:
                            case 11:
                            case 12:
                                viewGroup = new EditTextSettingsCell(this.mContext);
                                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                                QuickRepliesSettingsActivity.this.textCells[i - 9] = (EditTextSettingsCell) viewGroup;
                                break;
                            default:
                                viewGroup = null;
                                break;
                        }
                }
            }
            viewGroup = new TextCheckCell(this.mContext);
            viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public int getItemViewType(int i) {
            if (i == QuickRepliesSettingsActivity.this.explanationRow) {
                return 0;
            }
            if (!(i == QuickRepliesSettingsActivity.this.reply1Row || i == QuickRepliesSettingsActivity.this.reply2Row || i == QuickRepliesSettingsActivity.this.reply3Row)) {
                if (i != QuickRepliesSettingsActivity.this.reply4Row) {
                    return i == QuickRepliesSettingsActivity.this.sectionHeaderRow ? 2 : 1;
                }
            }
            return 9 + (i - QuickRepliesSettingsActivity.this.reply1Row);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        this.sectionHeaderRow = -1;
        int i = this.rowCount;
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
        this.explanationRow = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("VoipQuickReplies", C0446R.string.VoipQuickReplies));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C22671());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C22682());
        frameLayout.addView(this.actionBar);
        return this.fragmentView;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        Editor edit = getParentActivity().getSharedPreferences("mainconfig", 0).edit();
        while (i < this.textCells.length) {
            if (this.textCells[i] != null) {
                Object charSequence = this.textCells[i].getTextView().getText().toString();
                if (TextUtils.isEmpty(charSequence)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("quick_reply_msg");
                    stringBuilder.append(i + 1);
                    edit.remove(stringBuilder.toString());
                } else {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("quick_reply_msg");
                    stringBuilder2.append(i + 1);
                    edit.putString(stringBuilder2.toString(), charSequence);
                }
            }
            i++;
        }
        edit.commit();
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
