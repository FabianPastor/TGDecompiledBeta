package org.telegram.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextDetailSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class LogoutActivity extends BaseFragment {
    private int addAccountRow;
    private int alternativeHeaderRow;
    private int alternativeSectionRow;
    private AnimatorSet animatorSet;
    private int cacheRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int logoutRow;
    private int logoutSectionRow;
    private int passcodeRow;
    private int phoneRow;
    private int rowCount;
    private int supportRow;

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return LogoutActivity.this.rowCount;
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell view = holder.itemView;
                    if (position == LogoutActivity.this.alternativeHeaderRow) {
                        view.setText(LocaleController.getString("AlternativeOptions", NUM));
                        return;
                    }
                    return;
                case 1:
                    TextDetailSettingsCell view2 = holder.itemView;
                    if (position == LogoutActivity.this.addAccountRow) {
                        view2.setTextAndValueAndIcon(LocaleController.getString("AddAnotherAccount", NUM), LocaleController.getString("AddAnotherAccountInfo", NUM), NUM, true);
                        return;
                    } else if (position == LogoutActivity.this.passcodeRow) {
                        view2.setTextAndValueAndIcon(LocaleController.getString("SetPasscode", NUM), LocaleController.getString("SetPasscodeInfo", NUM), NUM, true);
                        return;
                    } else if (position == LogoutActivity.this.cacheRow) {
                        view2.setTextAndValueAndIcon(LocaleController.getString("ClearCache", NUM), LocaleController.getString("ClearCacheInfo", NUM), NUM, true);
                        return;
                    } else if (position == LogoutActivity.this.phoneRow) {
                        view2.setTextAndValueAndIcon(LocaleController.getString("ChangePhoneNumber", NUM), LocaleController.getString("ChangePhoneNumberInfo", NUM), NUM, true);
                        return;
                    } else if (position == LogoutActivity.this.supportRow) {
                        view2.setTextAndValueAndIcon(LocaleController.getString("ContactSupport", NUM), LocaleController.getString("ContactSupportInfo", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextSettingsCell view3 = holder.itemView;
                    if (position == LogoutActivity.this.logoutRow) {
                        view3.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                        view3.setText(LocaleController.getString("LogOutTitle", NUM), false);
                        return;
                    }
                    return;
                case 4:
                    TextInfoPrivacyCell view4 = holder.itemView;
                    if (position == LogoutActivity.this.logoutSectionRow) {
                        view4.setText(LocaleController.getString("LogOutInfo", NUM));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == LogoutActivity.this.addAccountRow || position == LogoutActivity.this.passcodeRow || position == LogoutActivity.this.cacheRow || position == LogoutActivity.this.phoneRow || position == LogoutActivity.this.supportRow || position == LogoutActivity.this.logoutRow;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    View cell = new TextDetailSettingsCell(this.mContext);
                    cell.setMultilineDetail(true);
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = cell;
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 3:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == LogoutActivity.this.alternativeHeaderRow) {
                return 0;
            }
            if (position == LogoutActivity.this.addAccountRow || position == LogoutActivity.this.passcodeRow || position == LogoutActivity.this.cacheRow || position == LogoutActivity.this.phoneRow || position == LogoutActivity.this.supportRow) {
                return 1;
            }
            if (position == LogoutActivity.this.alternativeSectionRow) {
                return 2;
            }
            if (position == LogoutActivity.this.logoutRow) {
                return 3;
            }
            return 4;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.alternativeHeaderRow = i;
        if (UserConfig.getActivatedAccountsCount() < 3) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.addAccountRow = i;
        } else {
            this.addAccountRow = -1;
        }
        if (SharedConfig.passcodeHash.length() <= 0) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.passcodeRow = i;
        } else {
            this.passcodeRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.cacheRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.phoneRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.supportRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.alternativeSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.logoutRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.logoutSectionRow = i;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("LogOutTitle", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LogoutActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new LogoutActivity$$Lambda$0(this));
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$1$LogoutActivity(View view, int position, float x, float y) {
        if (position == this.addAccountRow) {
            int freeAccount = -1;
            for (int a = 0; a < 3; a++) {
                if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginActivity(freeAccount));
            }
        } else if (position == this.passcodeRow) {
            presentFragment(new PasscodeActivity(0));
        } else if (position == this.cacheRow) {
            presentFragment(new CacheControlActivity());
        } else if (position == this.phoneRow) {
            presentFragment(new ChangePhoneHelpActivity());
        } else if (position == this.supportRow) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (position == this.logoutRow && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("AreYouSureLogout", NUM));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new LogoutActivity$$Lambda$1(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$0$LogoutActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(1);
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        r9 = new ThemeDescription[17];
        r9[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        r9[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r9[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r9[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r9[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r9[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r9[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r9[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r9[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r9[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[10] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        r9[11] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r9[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r9[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r9[14] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r9[15] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r9[16] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        return r9;
    }
}
