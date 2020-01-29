package org.telegram.ui.Wallet;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import drinkless.org.ton.TonApi;
import javax.crypto.Cipher;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BiometricPromtHelper;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Wallet.WalletSettingsActivity;

public class WalletSettingsActivity extends BaseFragment {
    public static final int SEND_ACTIVITY_RESULT_CODE = 34;
    public static final int TYPE_SERVER = 1;
    public static final int TYPE_SETTINGS = 0;
    private static final int done_button = 1;
    private Adapter adapter;
    private BiometricPromtHelper biometricPromtHelper;
    private String blockchainConfigFromUrl;
    /* access modifiers changed from: private */
    public String blockchainJson;
    /* access modifiers changed from: private */
    public String blockchainName;
    /* access modifiers changed from: private */
    public int blockchainNameHeaderRow = -1;
    /* access modifiers changed from: private */
    public int blockchainNameRow = -1;
    /* access modifiers changed from: private */
    public int blockchainNameSectionRow = -1;
    /* access modifiers changed from: private */
    public String blockchainUrl;
    /* access modifiers changed from: private */
    public int changePasscodeRow = -1;
    /* access modifiers changed from: private */
    public int configType;
    private int currentType;
    /* access modifiers changed from: private */
    public int deleteRow = -1;
    /* access modifiers changed from: private */
    public int deleteSectionRow = -1;
    /* access modifiers changed from: private */
    public int exportRow = -1;
    /* access modifiers changed from: private */
    public int fieldHeaderRow = -1;
    /* access modifiers changed from: private */
    public int fieldRow = -1;
    /* access modifiers changed from: private */
    public int fieldSectionRow = -1;
    /* access modifiers changed from: private */
    public int headerRow = -1;
    /* access modifiers changed from: private */
    public int jsonTypeRow = -1;
    private RecyclerListView listView;
    private BaseFragment parentFragment;
    /* access modifiers changed from: private */
    public int rowCount = 0;
    /* access modifiers changed from: private */
    public int serverSettingsRow = -1;
    /* access modifiers changed from: private */
    public int typeHeaderRow = -1;
    /* access modifiers changed from: private */
    public int typeSectionRow = -1;
    /* access modifiers changed from: private */
    public int urlTypeRow = -1;
    /* access modifiers changed from: private */
    public int walletSectionRow = -1;

    public class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;

        public TypeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void setValue(String str, boolean z, boolean z2) {
            this.textView.setText(str);
            this.checkImage.setVisibility(z ? 0 : 4);
            this.needDivider = z2;
        }

        public void setTypeChecked(boolean z) {
            this.checkImage.setVisibility(z ? 0 : 4);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public WalletSettingsActivity(int i, BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
        this.currentType = i;
        int i2 = this.currentType;
        if (i2 == 0) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.headerRow = i3;
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.exportRow = i4;
            if (BuildVars.TON_WALLET_STANDALONE) {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.serverSettingsRow = i5;
            }
            if (getUserConfig().tonPasscodeType != -1) {
                int i6 = this.rowCount;
                this.rowCount = i6 + 1;
                this.changePasscodeRow = i6;
            }
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.walletSectionRow = i7;
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.deleteRow = i8;
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.deleteSectionRow = i9;
        } else if (i2 == 1) {
            UserConfig userConfig = getUserConfig();
            this.blockchainName = userConfig.walletBlockchainName;
            this.blockchainJson = userConfig.walletConfig;
            this.blockchainUrl = userConfig.walletConfigUrl;
            this.configType = userConfig.walletConfigType;
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.typeHeaderRow = i10;
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.urlTypeRow = i11;
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.jsonTypeRow = i12;
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.typeSectionRow = i13;
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.fieldHeaderRow = i14;
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.fieldRow = i15;
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.fieldSectionRow = i16;
            int i17 = this.rowCount;
            this.rowCount = i17 + 1;
            this.blockchainNameHeaderRow = i17;
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.blockchainNameRow = i18;
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.blockchainNameSectionRow = i19;
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonImage(NUM);
        actionBar.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
        actionBar.setTitleColor(Theme.getColor("wallet_whiteText"));
        actionBar.setItemsColor(Theme.getColor("wallet_whiteText"), false);
        actionBar.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        int i = this.currentType;
        if (i == 0) {
            actionBar.setTitle(LocaleController.getString("WalletSettings", NUM));
        } else if (i == 1) {
            actionBar.setTitle(LocaleController.getString("WalletServerSettings", NUM));
            actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f)).setContentDescription(LocaleController.getString("Done", NUM));
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletSettingsActivity.this.finishFragment();
                } else if (i == 1 && WalletSettingsActivity.this.getParentActivity() != null) {
                    if (!TextUtils.equals(WalletSettingsActivity.this.getUserConfig().walletBlockchainName, WalletSettingsActivity.this.blockchainName)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) WalletSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("Wallet", NUM));
                        builder.setMessage(LocaleController.getString("WalletBlockchainNameWarning", NUM));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        builder.setPositiveButton(LocaleController.getString("WalletContinue", NUM), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                WalletSettingsActivity.AnonymousClass1.this.lambda$onItemClick$0$WalletSettingsActivity$1(dialogInterface, i);
                            }
                        });
                        AlertDialog create = builder.create();
                        WalletSettingsActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                            return;
                        }
                        return;
                    }
                    WalletSettingsActivity.this.saveConfig(true);
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$WalletSettingsActivity$1(DialogInterface dialogInterface, int i) {
                WalletSettingsActivity.this.saveConfig(true);
            }
        });
        return actionBar;
    }

    /* access modifiers changed from: private */
    public void saveConfig(boolean z) {
        boolean z2;
        boolean equals;
        UserConfig userConfig = getUserConfig();
        boolean z3 = !TextUtils.equals(userConfig.walletBlockchainName, this.blockchainName);
        int i = this.configType;
        if (i != userConfig.walletConfigType || z3) {
            z2 = true;
        } else {
            if (i == 0) {
                equals = TextUtils.equals(userConfig.walletConfigUrl, this.blockchainUrl);
            } else if (i == 1) {
                equals = TextUtils.equals(userConfig.walletBlockchainName, this.blockchainJson);
            } else {
                z2 = false;
            }
            z2 = !equals;
        }
        if (z2) {
            int i2 = this.configType;
            if (i2 == 1) {
                if (!TextUtils.isEmpty(this.blockchainJson)) {
                    try {
                        new JSONObject(this.blockchainJson);
                    } catch (Throwable th) {
                        FileLog.e(th);
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("WalletError", NUM), LocaleController.getString("WalletBlockchainConfigInvalid", NUM));
                        return;
                    }
                } else {
                    return;
                }
            } else if (z && i2 == 0) {
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                WalletConfigLoader.loadConfig(this.blockchainUrl, new TonController.StringCallback(alertDialog) {
                    private final /* synthetic */ AlertDialog f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(String str) {
                        WalletSettingsActivity.this.lambda$saveConfig$0$WalletSettingsActivity(this.f$1, str);
                    }
                });
                return;
            }
        }
        if (z2) {
            String str = userConfig.walletBlockchainName;
            String str2 = userConfig.walletConfig;
            String str3 = userConfig.walletConfigUrl;
            int i3 = userConfig.walletConfigType;
            String str4 = this.blockchainConfigFromUrl;
            userConfig.walletBlockchainName = this.blockchainName;
            userConfig.walletConfig = this.blockchainJson;
            userConfig.walletConfigUrl = this.blockchainUrl;
            userConfig.walletConfigType = this.configType;
            userConfig.walletConfigFromUrl = str4;
            if (!getTonController().onTonConfigUpdated()) {
                userConfig.walletBlockchainName = str;
                userConfig.walletConfig = str2;
                userConfig.walletConfigUrl = str3;
                userConfig.walletConfigType = i3;
                userConfig.walletConfigFromUrl = str4;
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("WalletError", NUM), LocaleController.getString("WalletBlockchainConfigInvalid", NUM));
                return;
            }
            userConfig.saveConfig(false);
        }
        if (z3) {
            doLogout();
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment != null) {
                baseFragment.removeSelfFromStack();
            }
            presentFragment(new WalletCreateActivity(0), true);
            return;
        }
        finishFragment();
    }

    public /* synthetic */ void lambda$saveConfig$0$WalletSettingsActivity(AlertDialog alertDialog, String str) {
        alertDialog.dismiss();
        if (TextUtils.isEmpty(str)) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("WalletError", NUM), LocaleController.getString("WalletBlockchainConfigLoadError", NUM));
            return;
        }
        this.blockchainConfigFromUrl = str;
        saveConfig(false);
    }

    public View createView(Context context) {
        this.biometricPromtHelper = new BiometricPromtHelper(this);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        this.fragmentView.setFocusableInTouchMode(true);
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter adapter2 = new Adapter(context);
        this.adapter = adapter2;
        recyclerListView.setAdapter(adapter2);
        this.listView.setGlowColor(Theme.getColor("wallet_blackBackground"));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                WalletSettingsActivity.this.lambda$createView$2$WalletSettingsActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$WalletSettingsActivity(View view, int i) {
        int i2 = 1;
        if (i == this.exportRow) {
            int keyProtectionType = getTonController().getKeyProtectionType();
            if (keyProtectionType == 0) {
                presentFragment(new WalletPasscodeActivity(2));
            } else if (keyProtectionType != 1) {
                if (keyProtectionType == 2) {
                    this.biometricPromtHelper.promtWithCipher(getTonController().getCipherForDecrypt(), LocaleController.getString("WalletExportConfirmCredentials", NUM), new BiometricPromtHelper.CipherCallback() {
                        public final void run(Cipher cipher) {
                            WalletSettingsActivity.this.doExport(cipher);
                        }
                    });
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                getParentActivity().startActivityForResult(((KeyguardManager) ApplicationLoader.applicationContext.getSystemService("keyguard")).createConfirmDeviceCredentialIntent(LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletExportConfirmCredentials", NUM)), 34);
            }
        } else if (i == this.deleteRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("WalletDeleteTitle", NUM));
            builder.setMessage(LocaleController.getString("WalletDeleteInfo", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    WalletSettingsActivity.this.lambda$null$1$WalletSettingsActivity(dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == this.changePasscodeRow) {
            presentFragment(new WalletPasscodeActivity(1));
        } else if (i == this.urlTypeRow || i == this.jsonTypeRow) {
            if (i == this.urlTypeRow) {
                i2 = 0;
            }
            this.configType = i2;
            this.adapter.notifyDataSetChanged();
        } else if (i == this.serverSettingsRow) {
            presentFragment(new WalletSettingsActivity(1, this));
        }
    }

    public /* synthetic */ void lambda$null$1$WalletSettingsActivity(DialogInterface dialogInterface, int i) {
        doLogout();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.removeSelfFromStack();
        }
        if (BuildVars.TON_WALLET_STANDALONE) {
            presentFragment(new WalletCreateActivity(0), true);
        } else {
            finishFragment();
        }
    }

    public void onPause() {
        super.onPause();
        BiometricPromtHelper biometricPromtHelper2 = this.biometricPromtHelper;
        if (biometricPromtHelper2 != null) {
            biometricPromtHelper2.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void removeSelfFromStack() {
        super.removeSelfFromStack();
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null) {
            baseFragment.removeSelfFromStack();
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 34 && i2 == -1) {
            doExport((Cipher) null);
        }
    }

    private void doLogout() {
        getTonController().cleanup();
        UserConfig userConfig = getUserConfig();
        userConfig.clearTonConfig();
        userConfig.saveConfig(false);
    }

    /* access modifiers changed from: private */
    public void doExport(Cipher cipher) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().getSecretWords((String) null, cipher, new TonController.WordsCallback(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(String[] strArr) {
                    WalletSettingsActivity.this.lambda$doExport$3$WalletSettingsActivity(this.f$1, strArr);
                }
            }, new TonController.ErrorCallback(alertDialog) {
                private final /* synthetic */ AlertDialog f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(String str, TonApi.Error error) {
                    WalletSettingsActivity.this.lambda$doExport$4$WalletSettingsActivity(this.f$1, str, error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$doExport$3$WalletSettingsActivity(AlertDialog alertDialog, String[] strArr) {
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.setSecretWords(strArr);
        presentFragment(walletCreateActivity);
    }

    public /* synthetic */ void lambda$doExport$4$WalletSettingsActivity(AlertDialog alertDialog, String str, TonApi.Error error) {
        alertDialog.dismiss();
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleController.getString("ErrorOccurred", NUM));
        sb.append("\n");
        if (error != null) {
            str = error.message;
        }
        sb.append(str);
        AlertsCreator.showSimpleAlert(this, sb.toString());
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public Adapter(Context context2) {
            this.context = context2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            PollEditTextCell pollEditTextCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell = headerCell;
            } else if (i == 1) {
                TextSettingsCell textSettingsCell = new TextSettingsCell(this.context);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell = textSettingsCell;
            } else if (i == 2) {
                pollEditTextCell = new ShadowSectionCell(this.context);
            } else if (i == 3) {
                pollEditTextCell = new TextInfoPrivacyCell(this.context);
            } else if (i != 4) {
                final PollEditTextCell pollEditTextCell2 = new PollEditTextCell(this.context, (View.OnClickListener) null);
                pollEditTextCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell2.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(37.0f), AndroidUtilities.dp(14.0f));
                pollEditTextCell2.addTextWatcher(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        Integer num = (Integer) pollEditTextCell2.getTag();
                        if (num != null) {
                            if (num.intValue() == WalletSettingsActivity.this.fieldRow) {
                                if (WalletSettingsActivity.this.configType == 0) {
                                    String unused = WalletSettingsActivity.this.blockchainUrl = editable.toString();
                                } else {
                                    String unused2 = WalletSettingsActivity.this.blockchainJson = editable.toString();
                                }
                            } else if (num.intValue() == WalletSettingsActivity.this.blockchainNameRow) {
                                String unused3 = WalletSettingsActivity.this.blockchainName = editable.toString();
                            }
                        }
                    }
                });
                pollEditTextCell = pollEditTextCell2;
            } else {
                TypeCell typeCell = new TypeCell(this.context);
                typeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                pollEditTextCell = typeCell;
            }
            return new RecyclerListView.Holder(pollEditTextCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                boolean z2 = false;
                if (itemViewType == 1) {
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == WalletSettingsActivity.this.exportRow) {
                        String string = LocaleController.getString("WalletExport", NUM);
                        if (WalletSettingsActivity.this.changePasscodeRow == -1 && WalletSettingsActivity.this.serverSettingsRow == -1) {
                            z = false;
                        }
                        textSettingsCell.setText(string, z);
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    } else if (i == WalletSettingsActivity.this.changePasscodeRow) {
                        textSettingsCell.setText(LocaleController.getString("WalletChangePasscode", NUM), false);
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    } else if (i == WalletSettingsActivity.this.deleteRow) {
                        textSettingsCell.setText(LocaleController.getString("WalletDelete", NUM), false);
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
                        textSettingsCell.setTag("windowBackgroundWhiteRedText2");
                    } else if (i == WalletSettingsActivity.this.serverSettingsRow) {
                        String string2 = LocaleController.getString("WalletServerSettings", NUM);
                        if (WalletSettingsActivity.this.changePasscodeRow == -1) {
                            z = false;
                        }
                        textSettingsCell.setText(string2, z);
                        textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                        textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                    }
                } else if (itemViewType != 2) {
                    if (itemViewType == 3) {
                        TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                        if (i == WalletSettingsActivity.this.deleteSectionRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString("WalletDeleteInfo", NUM));
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        } else if (i == WalletSettingsActivity.this.typeSectionRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString("WalletConfigTypeInfo", NUM));
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        } else if (i == WalletSettingsActivity.this.blockchainNameSectionRow) {
                            textInfoPrivacyCell.setText(LocaleController.getString("WalletBlockchainNameInfo", NUM));
                            viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                        }
                    } else if (itemViewType == 4) {
                        TypeCell typeCell = (TypeCell) viewHolder.itemView;
                        if (i == WalletSettingsActivity.this.urlTypeRow) {
                            String string3 = LocaleController.getString("WalletConfigTypeUrl", NUM);
                            if (WalletSettingsActivity.this.configType == 0) {
                                z2 = true;
                            }
                            typeCell.setValue(string3, z2, true);
                        } else if (i == WalletSettingsActivity.this.jsonTypeRow) {
                            String string4 = LocaleController.getString("WalletConfigTypeJson", NUM);
                            if (WalletSettingsActivity.this.configType != 1) {
                                z = false;
                            }
                            typeCell.setValue(string4, z, false);
                        }
                    } else if (itemViewType == 5) {
                        PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                        pollEditTextCell.setTag((Object) null);
                        if (i == WalletSettingsActivity.this.blockchainNameRow) {
                            pollEditTextCell.setTextAndHint(WalletSettingsActivity.this.blockchainName, LocaleController.getString("WalletBlockchainNameHint", NUM), false);
                        } else if (i == WalletSettingsActivity.this.fieldRow) {
                            if (WalletSettingsActivity.this.configType == 0) {
                                pollEditTextCell.setTextAndHint(WalletSettingsActivity.this.blockchainUrl, LocaleController.getString("WalletConfigTypeUrlHint", NUM), false);
                            } else {
                                pollEditTextCell.setTextAndHint(WalletSettingsActivity.this.blockchainJson, LocaleController.getString("WalletConfigTypeJsonHint", NUM), false);
                            }
                        }
                        pollEditTextCell.setTag(Integer.valueOf(i));
                    }
                } else if (i == WalletSettingsActivity.this.walletSectionRow || i == WalletSettingsActivity.this.fieldSectionRow) {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, "windowBackgroundGrayShadow"));
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == WalletSettingsActivity.this.headerRow) {
                    headerCell.setText(LocaleController.getString("Wallet", NUM));
                } else if (i == WalletSettingsActivity.this.blockchainNameHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletBlockchainName", NUM));
                } else if (i == WalletSettingsActivity.this.typeHeaderRow) {
                    headerCell.setText(LocaleController.getString("WalletConfigType", NUM));
                } else if (i != WalletSettingsActivity.this.fieldHeaderRow) {
                } else {
                    if (WalletSettingsActivity.this.configType == 0) {
                        headerCell.setText(LocaleController.getString("WalletConfigTypeUrlHeader", NUM));
                    } else {
                        headerCell.setText(LocaleController.getString("WalletConfigTypeJsonHeader", NUM));
                    }
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == WalletSettingsActivity.this.headerRow || i == WalletSettingsActivity.this.blockchainNameHeaderRow || i == WalletSettingsActivity.this.typeHeaderRow || i == WalletSettingsActivity.this.fieldHeaderRow) {
                return 0;
            }
            if (i == WalletSettingsActivity.this.exportRow || i == WalletSettingsActivity.this.changePasscodeRow || i == WalletSettingsActivity.this.deleteRow || i == WalletSettingsActivity.this.serverSettingsRow) {
                return 1;
            }
            if (i == WalletSettingsActivity.this.walletSectionRow || i == WalletSettingsActivity.this.fieldSectionRow) {
                return 2;
            }
            if (i == WalletSettingsActivity.this.jsonTypeRow || i == WalletSettingsActivity.this.urlTypeRow) {
                return 4;
            }
            return (i == WalletSettingsActivity.this.fieldRow || i == WalletSettingsActivity.this.blockchainNameRow) ? 5 : 3;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public int getItemCount() {
            return WalletSettingsActivity.this.rowCount;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "wallet_blackBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "wallet_blackBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "wallet_whiteText"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "wallet_whiteText"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "wallet_blackBackgroundSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription((View) this.listView, 0, new Class[]{TypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon")};
    }
}
