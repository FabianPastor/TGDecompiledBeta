package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class ConvertGroupActivity extends BaseFragment implements NotificationCenterDelegate {
    private int chat_id;
    private int convertDetailRow;
    private int convertInfoRow;
    private int convertRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int rowCount;

    /* renamed from: org.telegram.ui.ConvertGroupActivity$1 */
    class C21141 extends ActionBarMenuOnItemClick {
        C21141() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ConvertGroupActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ConvertGroupActivity$2 */
    class C21152 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ConvertGroupActivity$2$1 */
        class C13661 implements OnClickListener {
            C13661() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ConvertGroupActivity.this.currentAccount).convertToMegaGroup(ConvertGroupActivity.this.getParentActivity(), ConvertGroupActivity.this.chat_id);
            }
        }

        C21152() {
        }

        public void onItemClick(View view, int i) {
            if (i == ConvertGroupActivity.this.convertRow) {
                view = new Builder(ConvertGroupActivity.this.getParentActivity());
                view.setMessage(LocaleController.getString("ConvertGroupAlert", C0446R.string.ConvertGroupAlert));
                view.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0446R.string.ConvertGroupAlertWarning));
                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C13661());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                ConvertGroupActivity.this.showDialog(view.create());
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition() == ConvertGroupActivity.this.convertRow ? true : null;
        }

        public int getItemCount() {
            return ConvertGroupActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoPrivacyCell(this.mContext);
            } else {
                viewGroup = new TextSettingsCell(this.mContext);
                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ConvertGroupActivity.this.convertRow) {
                        textSettingsCell.setText(LocaleController.getString("ConvertGroup", C0446R.string.ConvertGroup), false);
                        return;
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ConvertGroupActivity.this.convertInfoRow) {
                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo2", C0446R.string.ConvertGroupInfo2)));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == ConvertGroupActivity.this.convertDetailRow) {
                        textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo3", C0446R.string.ConvertGroupInfo3)));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (i == ConvertGroupActivity.this.convertRow) {
                return 0;
            }
            if (i != ConvertGroupActivity.this.convertInfoRow) {
                if (i != ConvertGroupActivity.this.convertDetailRow) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public ConvertGroupActivity(Bundle bundle) {
        super(bundle);
        this.chat_id = bundle.getInt("chat_id");
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.convertInfoRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.convertRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.convertDetailRow = i;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ConvertGroup", C0446R.string.ConvertGroup));
        this.actionBar.setActionBarMenuOnItemClick(new C21141());
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new C21152());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[11];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        r1[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r1[8] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return r1;
    }
}
