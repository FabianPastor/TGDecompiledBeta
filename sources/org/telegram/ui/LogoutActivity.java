package org.telegram.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
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

public class LogoutActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public int addAccountRow;
    /* access modifiers changed from: private */
    public int alternativeHeaderRow;
    /* access modifiers changed from: private */
    public int alternativeSectionRow;
    private AnimatorSet animatorSet;
    /* access modifiers changed from: private */
    public int cacheRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public int logoutRow;
    /* access modifiers changed from: private */
    public int logoutSectionRow;
    /* access modifiers changed from: private */
    public int passcodeRow;
    /* access modifiers changed from: private */
    public int phoneRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int supportRow;

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.alternativeHeaderRow = 0;
        if (UserConfig.getActivatedAccountsCount() < 4) {
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.addAccountRow = i;
        } else {
            this.addAccountRow = -1;
        }
        if (SharedConfig.passcodeHash.length() <= 0) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.passcodeRow = i2;
        } else {
            this.passcodeRow = -1;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.cacheRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.phoneRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.supportRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.alternativeSectionRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.logoutRow = i7;
        this.rowCount = i8 + 1;
        this.logoutSectionRow = i8;
        return true;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setTitle(LocaleController.getString("LogOutTitle", NUM));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    LogoutActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        ((FrameLayout) this.fragmentView).addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new LogoutActivity$$ExternalSyntheticLambda1(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LogoutActivity  reason: not valid java name */
    public /* synthetic */ void m3918lambda$createView$0$orgtelegramuiLogoutActivity(View view, int position, float x, float y) {
        if (position == this.addAccountRow) {
            int freeAccount = -1;
            int a = 0;
            while (true) {
                if (a >= 4) {
                    break;
                } else if (!UserConfig.getInstance(a).isClientActivated()) {
                    freeAccount = a;
                    break;
                } else {
                    a++;
                }
            }
            if (freeAccount >= 0) {
                presentFragment(new LoginActivity(freeAccount));
            }
        } else if (position == this.passcodeRow) {
            presentFragment(PasscodeActivity.determineOpenFragment());
        } else if (position == this.cacheRow) {
            presentFragment(new CacheControlActivity());
        } else if (position == this.phoneRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (position == this.supportRow) {
            showDialog(AlertsCreator.createSupportAlert(this, (Theme.ResourcesProvider) null));
        } else if (position == this.logoutRow && getParentActivity() != null) {
            showDialog(makeLogOutDialog(getParentActivity(), this.currentAccount));
        }
    }

    public static AlertDialog makeLogOutDialog(Context context, int currentAccount) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(LocaleController.getString("AreYouSureLogout", NUM));
        builder.setTitle(LocaleController.getString("LogOut", NUM));
        builder.setPositiveButton(LocaleController.getString("LogOut", NUM), new LogoutActivity$$ExternalSyntheticLambda0(currentAccount));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return alertDialog;
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
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
            return LogoutActivity.this.rowCount;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell view = (HeaderCell) holder.itemView;
                    if (position == LogoutActivity.this.alternativeHeaderRow) {
                        view.setText(LocaleController.getString("AlternativeOptions", NUM));
                        return;
                    }
                    return;
                case 1:
                    TextDetailSettingsCell view2 = (TextDetailSettingsCell) holder.itemView;
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
                    TextSettingsCell view3 = (TextSettingsCell) holder.itemView;
                    if (position == LogoutActivity.this.logoutRow) {
                        view3.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                        view3.setText(LocaleController.getString("LogOutTitle", NUM), false);
                        return;
                    }
                    return;
                case 4:
                    TextInfoPrivacyCell view4 = (TextInfoPrivacyCell) holder.itemView;
                    if (position == LogoutActivity.this.logoutSectionRow) {
                        view4.setText(LocaleController.getString("LogOutInfo", NUM));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == LogoutActivity.this.addAccountRow || position == LogoutActivity.this.passcodeRow || position == LogoutActivity.this.cacheRow || position == LogoutActivity.this.phoneRow || position == LogoutActivity.this.supportRow || position == LogoutActivity.this.logoutRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                    break;
                case 1:
                    TextDetailSettingsCell cell = new TextDetailSettingsCell(this.mContext);
                    cell.setMultilineDetail(true);
                    cell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    TextDetailSettingsCell textDetailSettingsCell = cell;
                    view = cell;
                    break;
                case 2:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 3:
                    View textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = textSettingsCell;
                    break;
                default:
                    View view2 = new TextInfoPrivacyCell(this.mContext);
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    view = view2;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return themeDescriptions;
    }
}
