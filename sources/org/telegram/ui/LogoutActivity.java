package org.telegram.ui;

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
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
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
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class LogoutActivity extends BaseFragment {
    private int addAccountRow;
    private int alternativeHeaderRow;
    private int alternativeSectionRow;
    private int cacheRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int logoutRow;
    private int logoutSectionRow;
    private int passcodeRow;
    private int phoneRow;
    private int rowCount;
    private int supportRow;

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("LogOutTitle", R.string.LogOutTitle));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.LogoutActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    LogoutActivity.this.finishFragment();
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
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.LogoutActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                LogoutActivity.this.lambda$createView$0(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i, float f, float f2) {
        Integer num = null;
        if (i == this.addAccountRow) {
            int i2 = 0;
            for (int i3 = 3; i3 >= 0; i3--) {
                if (!UserConfig.getInstance(i3).isClientActivated()) {
                    i2++;
                    if (num == null) {
                        num = Integer.valueOf(i3);
                    }
                }
            }
            if (!UserConfig.hasPremiumOnAccounts()) {
                i2--;
            }
            if (i2 > 0 && num != null) {
                presentFragment(new LoginActivity(num.intValue()));
            } else if (!UserConfig.hasPremiumOnAccounts()) {
                showDialog(new LimitReachedBottomSheet(this, getContext(), 7, this.currentAccount));
            }
        } else if (i == this.passcodeRow) {
            presentFragment(PasscodeActivity.determineOpenFragment());
        } else if (i == this.cacheRow) {
            presentFragment(new CacheControlActivity());
        } else if (i == this.phoneRow) {
            presentFragment(new ActionIntroActivity(3));
        } else if (i == this.supportRow) {
            showDialog(AlertsCreator.createSupportAlert(this, null));
        } else if (i == this.logoutRow && getParentActivity() != null) {
            showDialog(makeLogOutDialog(getParentActivity(), this.currentAccount));
        }
    }

    public static AlertDialog makeLogOutDialog(Context context, final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(LocaleController.getString("AreYouSureLogout", R.string.AreYouSureLogout));
        int i2 = R.string.LogOut;
        builder.setTitle(LocaleController.getString("LogOut", i2));
        builder.setPositiveButton(LocaleController.getString("LogOut", i2), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LogoutActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i3) {
                LogoutActivity.lambda$makeLogOutDialog$1(i, dialogInterface, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        return create;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$makeLogOutDialog$1(int i, DialogInterface dialogInterface, int i2) {
        MessagesController.getInstance(i).performLogout(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes3.dex */
    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return LogoutActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i != LogoutActivity.this.alternativeHeaderRow) {
                    return;
                }
                headerCell.setText(LocaleController.getString("AlternativeOptions", R.string.AlternativeOptions));
            } else if (itemViewType != 1) {
                if (itemViewType == 3) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i != LogoutActivity.this.logoutRow) {
                        return;
                    }
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
                    textSettingsCell.setText(LocaleController.getString("LogOutTitle", R.string.LogOutTitle), false);
                } else if (itemViewType != 4) {
                } else {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i != LogoutActivity.this.logoutSectionRow) {
                        return;
                    }
                    textInfoPrivacyCell.setText(LocaleController.getString("LogOutInfo", R.string.LogOutInfo));
                }
            } else {
                TextDetailSettingsCell textDetailSettingsCell = (TextDetailSettingsCell) viewHolder.itemView;
                if (i != LogoutActivity.this.addAccountRow) {
                    if (i != LogoutActivity.this.passcodeRow) {
                        if (i != LogoutActivity.this.cacheRow) {
                            if (i != LogoutActivity.this.phoneRow) {
                                if (i != LogoutActivity.this.supportRow) {
                                    return;
                                }
                                textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ContactSupport", R.string.ContactSupport), LocaleController.getString("ContactSupportInfo", R.string.ContactSupportInfo), R.drawable.msg_help, false);
                                return;
                            }
                            textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ChangePhoneNumber", R.string.ChangePhoneNumber), LocaleController.getString("ChangePhoneNumberInfo", R.string.ChangePhoneNumberInfo), R.drawable.msg_newphone, true);
                            return;
                        }
                        textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("ClearCache", R.string.ClearCache), LocaleController.getString("ClearCacheInfo", R.string.ClearCacheInfo), R.drawable.msg_clearcache, true);
                        return;
                    }
                    textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("SetPasscode", R.string.SetPasscode), LocaleController.getString("SetPasscodeInfo", R.string.SetPasscodeInfo), R.drawable.msg_permissions, true);
                    return;
                }
                textDetailSettingsCell.setTextAndValueAndIcon(LocaleController.getString("AddAnotherAccount", R.string.AddAnotherAccount), LocaleController.getString("AddAnotherAccountInfo", R.string.AddAnotherAccountInfo), R.drawable.msg_contact_add, true);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int adapterPosition = viewHolder.getAdapterPosition();
            return adapterPosition == LogoutActivity.this.addAccountRow || adapterPosition == LogoutActivity.this.passcodeRow || adapterPosition == LogoutActivity.this.cacheRow || adapterPosition == LogoutActivity.this.phoneRow || adapterPosition == LogoutActivity.this.supportRow || adapterPosition == LogoutActivity.this.logoutRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1818onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextDetailSettingsCell textDetailSettingsCell;
            View view;
            if (i == 0) {
                FrameLayout headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textDetailSettingsCell = headerCell;
            } else if (i == 1) {
                TextDetailSettingsCell textDetailSettingsCell2 = new TextDetailSettingsCell(this.mContext);
                textDetailSettingsCell2.setMultilineDetail(true);
                textDetailSettingsCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                textDetailSettingsCell = textDetailSettingsCell2;
            } else {
                if (i == 2) {
                    view = new ShadowSectionCell(this.mContext);
                } else if (i == 3) {
                    FrameLayout textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textDetailSettingsCell = textSettingsCell;
                } else {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }
            view = textDetailSettingsCell;
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, TextDetailSettingsCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailSettingsCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        return arrayList;
    }
}
