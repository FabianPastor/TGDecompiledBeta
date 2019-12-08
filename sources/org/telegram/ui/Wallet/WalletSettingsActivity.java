package org.telegram.ui.Wallet;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import drinkless.org.ton.TonApi.Error;
import javax.crypto.Cipher;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class WalletSettingsActivity extends BaseFragment {
    public static final int SEND_ACTIVITY_RESULT_CODE = 34;
    private Adapter adapter;
    private BiometricPromtHelper biometricPromtHelper;
    private int changePasscodeRow;
    private int deleteRow;
    private int deleteSectionRow;
    private int exportRow;
    private int headerRow;
    private RecyclerListView listView;
    private WalletActivity parentFragment;
    private int rowCount = 0;
    private int walletSectionRow;

    private class Adapter extends SelectionAdapter {
        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            View textInfoPrivacyCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                headerCell = new HeaderCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i != 1) {
                if (i != 2) {
                    textInfoPrivacyCell = new TextInfoPrivacyCell(this.context);
                } else {
                    textInfoPrivacyCell = new ShadowSectionCell(this.context);
                }
                return new Holder(textInfoPrivacyCell);
            } else {
                headerCell = new TextSettingsCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor(str));
            }
            textInfoPrivacyCell = headerCell;
            return new Holder(textInfoPrivacyCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType != 1) {
                    String str = "windowBackgroundGrayShadow";
                    if (itemViewType != 2) {
                        if (itemViewType == 3) {
                            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                            if (i == WalletSettingsActivity.this.deleteSectionRow) {
                                textInfoPrivacyCell.setText(LocaleController.getString("WalletDeleteInfo", NUM));
                                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                                return;
                            }
                            return;
                        }
                        return;
                    } else if (i == WalletSettingsActivity.this.walletSectionRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                        return;
                    } else {
                        return;
                    }
                }
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                String str2 = "windowBackgroundWhiteBlackText";
                String string;
                if (i == WalletSettingsActivity.this.exportRow) {
                    string = LocaleController.getString("WalletExport", NUM);
                    if (WalletSettingsActivity.this.changePasscodeRow == -1) {
                        z = false;
                    }
                    textSettingsCell.setText(string, z);
                    textSettingsCell.setTextColor(Theme.getColor(str2));
                    textSettingsCell.setTag(str2);
                    return;
                } else if (i == WalletSettingsActivity.this.changePasscodeRow) {
                    textSettingsCell.setText(LocaleController.getString("WalletChangePasscode", NUM), false);
                    textSettingsCell.setTextColor(Theme.getColor(str2));
                    textSettingsCell.setTag(str2);
                    return;
                } else if (i == WalletSettingsActivity.this.deleteRow) {
                    textSettingsCell.setText(LocaleController.getString("WalletDelete", NUM), false);
                    string = "windowBackgroundWhiteRedText2";
                    textSettingsCell.setTextColor(Theme.getColor(string));
                    textSettingsCell.setTag(string);
                    return;
                } else {
                    return;
                }
            }
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            if (i == WalletSettingsActivity.this.headerRow) {
                headerCell.setText(LocaleController.getString("Wallet", NUM));
            }
        }

        public int getItemViewType(int i) {
            if (i == WalletSettingsActivity.this.headerRow) {
                return 0;
            }
            if (i == WalletSettingsActivity.this.exportRow || i == WalletSettingsActivity.this.changePasscodeRow || i == WalletSettingsActivity.this.deleteRow) {
                return 1;
            }
            return i == WalletSettingsActivity.this.walletSectionRow ? 2 : 3;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public int getItemCount() {
            return WalletSettingsActivity.this.rowCount;
        }
    }

    public WalletSettingsActivity(WalletActivity walletActivity) {
        this.parentFragment = walletActivity;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.headerRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.exportRow = i;
        if (getUserConfig().tonPasscodeType != -1) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.changePasscodeRow = i;
        } else {
            this.changePasscodeRow = -1;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.walletSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.deleteSectionRow = i;
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonImage(NUM);
        actionBar.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
        String str = "wallet_whiteText";
        actionBar.setTitleColor(Theme.getColor(str));
        actionBar.setItemsColor(Theme.getColor(str), false);
        actionBar.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        actionBar.setTitle(LocaleController.getString("WalletSettings", NUM));
        actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletSettingsActivity.this.finishFragment();
                }
            }
        });
        return actionBar;
    }

    public View createView(Context context) {
        this.biometricPromtHelper = new BiometricPromtHelper(this);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter = new Adapter(context);
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor("wallet_blackBackground"));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$WalletSettingsActivity$VJHn6Rya7ET4UZx8Mlxl2CQzxq0(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$WalletSettingsActivity(View view, int i) {
        if (i == this.exportRow) {
            int keyProtectionType = getTonController().getKeyProtectionType();
            if (keyProtectionType != 0) {
                String str = "WalletExportConfirmCredentials";
                if (keyProtectionType != 1) {
                    if (keyProtectionType == 2) {
                        this.biometricPromtHelper.promtWithCipher(getTonController().getCipherForDecrypt(), LocaleController.getString(str, NUM), new -$$Lambda$WalletSettingsActivity$qvlGHcgNevar_au0i2cBQC4A1fmE(this));
                        return;
                    }
                    return;
                } else if (VERSION.SDK_INT >= 23) {
                    getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString(str, NUM)), 34);
                    return;
                } else {
                    return;
                }
            }
            presentFragment(new WalletPasscodeActivity(2));
        } else if (i == this.deleteRow) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("WalletDeleteTitle", NUM));
            builder.setMessage(LocaleController.getString("WalletDeleteInfo", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$WalletSettingsActivity$NC2giHcM90Ri4ZUWrJdWhkUkRsc(this));
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == this.changePasscodeRow) {
            presentFragment(new WalletPasscodeActivity(1));
        }
    }

    public /* synthetic */ void lambda$null$0$WalletSettingsActivity(DialogInterface dialogInterface, int i) {
        getTonController().cleanup();
        UserConfig userConfig = getUserConfig();
        userConfig.clearTonConfig();
        userConfig.saveConfig(false);
        WalletActivity walletActivity = this.parentFragment;
        if (walletActivity != null) {
            walletActivity.removeSelfFromStack();
        }
        finishFragment();
    }

    public void onPause() {
        super.onPause();
        BiometricPromtHelper biometricPromtHelper = this.biometricPromtHelper;
        if (biometricPromtHelper != null) {
            biometricPromtHelper.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 34 && i2 == -1) {
            doExport(null);
        }
    }

    private void doExport(Cipher cipher) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().getSecretWords(null, cipher, new -$$Lambda$WalletSettingsActivity$lMW2Po5CcFhcM1FspSpo823oDhM(this, alertDialog), new -$$Lambda$WalletSettingsActivity$QXV2YH3UdRsZX3GezS2n7FLWWS8(this, alertDialog));
        }
    }

    public /* synthetic */ void lambda$doExport$2$WalletSettingsActivity(AlertDialog alertDialog, String[] strArr) {
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.setSecretWords(strArr);
        presentFragment(walletCreateActivity);
    }

    public /* synthetic */ void lambda$doExport$3$WalletSettingsActivity(AlertDialog alertDialog, String str, Error error) {
        alertDialog.dismiss();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LocaleController.getString("ErrorOccurred", NUM));
        stringBuilder.append("\n");
        if (error != null) {
            str = error.message;
        }
        stringBuilder.append(str);
        AlertsCreator.showSimpleAlert(this, stringBuilder.toString());
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[15];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "wallet_blackBackground");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "wallet_blackBackground");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "wallet_whiteText");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "wallet_whiteText");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "wallet_blackBackgroundSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText2");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        return themeDescriptionArr;
    }
}
