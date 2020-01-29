package org.telegram.ui;

import android.animation.AnimatorSet;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
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
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.alternativeHeaderRow = i;
        if (UserConfig.getActivatedAccountsCount() < 3) {
            int i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.addAccountRow = i2;
        } else {
            this.addAccountRow = -1;
        }
        if (SharedConfig.passcodeHash.length() <= 0) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.passcodeRow = i3;
        } else {
            this.passcodeRow = -1;
        }
        int i4 = this.rowCount;
        this.rowCount = i4 + 1;
        this.cacheRow = i4;
        int i5 = this.rowCount;
        this.rowCount = i5 + 1;
        this.phoneRow = i5;
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.supportRow = i6;
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.alternativeSectionRow = i7;
        int i8 = this.rowCount;
        this.rowCount = i8 + 1;
        this.logoutRow = i8;
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.logoutSectionRow = i9;
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
            public void onItemClick(int i) {
                if (i == -1) {
                    LogoutActivity.this.finishFragment();
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                LogoutActivity.this.lambda$createView$1$LogoutActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$LogoutActivity(View view, int i, float f, float f2) {
        int i2 = 0;
        if (i == this.addAccountRow) {
            while (true) {
                if (i2 >= 3) {
                    i2 = -1;
                    break;
                } else if (!UserConfig.getInstance(i2).isClientActivated()) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i2 >= 0) {
                presentFragment(new LoginActivity(i2));
            }
        } else if (i == this.passcodeRow) {
            presentFragment(new PasscodeActivity(0));
        } else if (i == this.cacheRow) {
            presentFragment(new CacheControlActivity());
        } else if (i == this.phoneRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (i == this.supportRow) {
            showDialog(AlertsCreator.createSupportAlert(this));
        } else if (i == this.logoutRow && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            UserConfig userConfig = getUserConfig();
            if (TextUtils.isEmpty(userConfig.tonEncryptedData) || !userConfig.tonCreationFinished) {
                builder.setMessage(LocaleController.getString("AreYouSureLogout", NUM));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("WalletTelegramLogout", NUM)));
            }
            builder.setTitle(LocaleController.getString("LogOut", NUM));
            builder.setPositiveButton(LocaleController.getString("LogOut", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LogoutActivity.this.lambda$null$0$LogoutActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    public /* synthetic */ void lambda$null$0$LogoutActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).performLogout(1);
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

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == LogoutActivity.this.alternativeHeaderRow) {
                    headerCell.setText(LocaleController.getString("AlternativeOptions", NUM));
                }
            } else if (itemViewType == 1) {
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                if (i == LogoutActivity.this.addAccountRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("AddAnotherAccount", NUM), LocaleController.getString("AddAnotherAccountInfo", NUM), NUM, true);
                } else if (i == LogoutActivity.this.passcodeRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("SetPasscode", NUM), LocaleController.getString("SetPasscodeInfo", NUM), NUM, true);
                } else if (i == LogoutActivity.this.cacheRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ClearCache", NUM), LocaleController.getString("ClearCacheInfo", NUM), NUM, true);
                } else if (i == LogoutActivity.this.phoneRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ChangePhoneNumber", NUM), LocaleController.getString("ChangePhoneNumberInfo", NUM), NUM, true);
                } else if (i == LogoutActivity.this.supportRow) {
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ContactSupport", NUM), LocaleController.getString("ContactSupportInfo", NUM), NUM, false);
                }
            } else if (itemViewType == 3) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                if (i == LogoutActivity.this.logoutRow) {
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                    textSettingsCell.setText(LocaleController.getString("LogOutTitle", NUM), false);
                }
            } else if (itemViewType == 4) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == LogoutActivity.this.logoutSectionRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("LogOutInfo", NUM));
                }
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == LogoutActivity.this.addAccountRow || adapterPosition == LogoutActivity.this.passcodeRow || adapterPosition == LogoutActivity.this.cacheRow || adapterPosition == LogoutActivity.this.phoneRow || adapterPosition == LogoutActivity.this.supportRow || adapterPosition == LogoutActivity.this.logoutRow;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextSettingsCell textSettingsCell;
            View shadowSectionCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = headerCell;
            } else if (i != 1) {
                if (i == 2) {
                    shadowSectionCell = new ShadowSectionCell(this.mContext);
                } else if (i != 3) {
                    shadowSectionCell = new TextInfoPrivacyCell(this.mContext);
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    TextSettingsCell textSettingsCell2 = new TextSettingsCell(this.mContext);
                    textSettingsCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textSettingsCell = textSettingsCell2;
                }
                textSettingsCell = shadowSectionCell;
            } else {
                TextDetailSettingsCell textDetailSettingsCell = new TextDetailSettingsCell(this.mContext);
                textDetailSettingsCell.setMultilineDetail(true);
                textDetailSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textSettingsCell = textDetailSettingsCell;
            }
            textSettingsCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(textSettingsCell);
        }

        public int getItemViewType(int i) {
            if (i == LogoutActivity.this.alternativeHeaderRow) {
                return 0;
            }
            if (i == LogoutActivity.this.addAccountRow || i == LogoutActivity.this.passcodeRow || i == LogoutActivity.this.cacheRow || i == LogoutActivity.this.phoneRow || i == LogoutActivity.this.supportRow) {
                return 1;
            }
            if (i == LogoutActivity.this.alternativeSectionRow) {
                return 2;
            }
            return i == LogoutActivity.this.logoutRow ? 3 : 4;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon")};
    }
}
