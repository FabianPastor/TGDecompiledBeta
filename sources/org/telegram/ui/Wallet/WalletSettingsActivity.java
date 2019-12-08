package org.telegram.ui.Wallet;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import drinkless.org.ton.TonApi.Error;
import javax.crypto.Cipher;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
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
import org.telegram.ui.Cells.PollEditTextCell;
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
    public static final int TYPE_SERVER = 1;
    public static final int TYPE_SETTINGS = 0;
    private static final int done_button = 1;
    private Adapter adapter;
    private BiometricPromtHelper biometricPromtHelper;
    private String blockchainConfigFromUrl;
    private String blockchainJson;
    private String blockchainName;
    private int blockchainNameHeaderRow = -1;
    private int blockchainNameRow = -1;
    private int blockchainNameSectionRow = -1;
    private String blockchainUrl;
    private int changePasscodeRow = -1;
    private int configType;
    private int currentType;
    private int deleteRow = -1;
    private int deleteSectionRow = -1;
    private int exportRow = -1;
    private int fieldHeaderRow = -1;
    private int fieldRow = -1;
    private int fieldSectionRow = -1;
    private int headerRow = -1;
    private int jsonTypeRow = -1;
    private RecyclerListView listView;
    private BaseFragment parentFragment;
    private int rowCount = 0;
    private int serverSettingsRow = -1;
    private int typeHeaderRow = -1;
    private int typeSectionRow = -1;
    private int urlTypeRow = -1;
    private int walletSectionRow = -1;

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
            this.textView.setEllipsize(TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            ImageView imageView = this.checkImage;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(19, 14.0f, i | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + this.needDivider, NUM));
        }

        public void setValue(String str, boolean z, boolean z2) {
            this.textView.setText(str);
            this.checkImage.setVisibility(z ? 0 : 4);
            this.needDivider = z2;
        }

        public void setTypeChecked(boolean z) {
            this.checkImage.setVisibility(z ? 0 : 4);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    private class Adapter extends SelectionAdapter {
        private Context context;

        public Adapter(Context context) {
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                headerCell = new HeaderCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 1) {
                headerCell = new TextSettingsCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor(str));
            } else if (i == 2) {
                headerCell = new ShadowSectionCell(this.context);
            } else if (i == 3) {
                headerCell = new TextInfoPrivacyCell(this.context);
            } else if (i != 4) {
                headerCell = new PollEditTextCell(this.context, null);
                headerCell.setBackgroundColor(Theme.getColor(str));
                headerCell.getTextView().setPadding(0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(37.0f), AndroidUtilities.dp(14.0f));
                headerCell.addTextWatcher(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        Integer num = (Integer) headerCell.getTag();
                        if (num != null) {
                            if (num.intValue() == WalletSettingsActivity.this.fieldRow) {
                                if (WalletSettingsActivity.this.configType == 0) {
                                    WalletSettingsActivity.this.blockchainUrl = editable.toString();
                                } else {
                                    WalletSettingsActivity.this.blockchainJson = editable.toString();
                                }
                            } else if (num.intValue() == WalletSettingsActivity.this.blockchainNameRow) {
                                WalletSettingsActivity.this.blockchainName = editable.toString();
                            }
                        }
                    }
                });
            } else {
                headerCell = new TypeCell(this.context);
                headerCell.setBackgroundColor(Theme.getColor(str));
            }
            return new Holder(headerCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                boolean z2 = false;
                String string;
                if (itemViewType != 1) {
                    String str = "windowBackgroundGrayShadow";
                    if (itemViewType != 2) {
                        if (itemViewType == 3) {
                            TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                            if (i == WalletSettingsActivity.this.deleteSectionRow) {
                                textInfoPrivacyCell.setText(LocaleController.getString("WalletDeleteInfo", NUM));
                                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                                return;
                            } else if (i == WalletSettingsActivity.this.typeSectionRow) {
                                textInfoPrivacyCell.setText(LocaleController.getString("WalletConfigTypeInfo", NUM));
                                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                                return;
                            } else if (i == WalletSettingsActivity.this.blockchainNameSectionRow) {
                                textInfoPrivacyCell.setText(LocaleController.getString("WalletBlockchainNameInfo", NUM));
                                viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                                return;
                            } else {
                                return;
                            }
                        } else if (itemViewType == 4) {
                            TypeCell typeCell = (TypeCell) viewHolder.itemView;
                            if (i == WalletSettingsActivity.this.urlTypeRow) {
                                string = LocaleController.getString("WalletConfigTypeUrl", NUM);
                                if (WalletSettingsActivity.this.configType == 0) {
                                    z2 = true;
                                }
                                typeCell.setValue(string, z2, true);
                                return;
                            } else if (i == WalletSettingsActivity.this.jsonTypeRow) {
                                string = LocaleController.getString("WalletConfigTypeJson", NUM);
                                if (WalletSettingsActivity.this.configType != 1) {
                                    z = false;
                                }
                                typeCell.setValue(string, z, false);
                                return;
                            } else {
                                return;
                            }
                        } else if (itemViewType == 5) {
                            PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                            pollEditTextCell.setTag(null);
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
                            return;
                        } else {
                            return;
                        }
                    } else if (i == WalletSettingsActivity.this.walletSectionRow || i == WalletSettingsActivity.this.fieldSectionRow) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.context, NUM, str));
                        return;
                    } else {
                        return;
                    }
                }
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                String str2 = "windowBackgroundWhiteBlackText";
                if (i == WalletSettingsActivity.this.exportRow) {
                    string = LocaleController.getString("WalletExport", NUM);
                    if (WalletSettingsActivity.this.changePasscodeRow == -1 && WalletSettingsActivity.this.serverSettingsRow == -1) {
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
                } else if (i == WalletSettingsActivity.this.serverSettingsRow) {
                    string = LocaleController.getString("WalletServerSettings", NUM);
                    if (WalletSettingsActivity.this.changePasscodeRow == -1) {
                        z = false;
                    }
                    textSettingsCell.setText(string, z);
                    textSettingsCell.setTextColor(Theme.getColor(str2));
                    textSettingsCell.setTag(str2);
                    return;
                } else {
                    return;
                }
            }
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

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }

        public int getItemCount() {
            return WalletSettingsActivity.this.rowCount;
        }
    }

    public WalletSettingsActivity(int i, BaseFragment baseFragment) {
        this.parentFragment = baseFragment;
        this.currentType = i;
        int i2 = this.currentType;
        if (i2 == 0) {
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.headerRow = i2;
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.exportRow = i2;
            if (BuildVars.TON_WALLET_STANDALONE) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.serverSettingsRow = i2;
            }
            if (getUserConfig().tonPasscodeType != -1) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changePasscodeRow = i;
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
        } else if (i2 == 1) {
            UserConfig userConfig = getUserConfig();
            this.blockchainName = userConfig.walletBlockchainName;
            this.blockchainJson = userConfig.walletConfig;
            this.blockchainUrl = userConfig.walletConfigUrl;
            this.configType = userConfig.walletConfigType;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.typeHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.urlTypeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.jsonTypeRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.typeSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.fieldHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.fieldRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.fieldSectionRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.blockchainNameHeaderRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.blockchainNameRow = i;
            i = this.rowCount;
            this.rowCount = i + 1;
            this.blockchainNameSectionRow = i;
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
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
        int i = this.currentType;
        if (i == 0) {
            actionBar.setTitle(LocaleController.getString("WalletSettings", NUM));
        } else if (i == 1) {
            actionBar.setTitle(LocaleController.getString("WalletServerSettings", NUM));
            actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f)).setContentDescription(LocaleController.getString("Done", NUM));
        }
        actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletSettingsActivity.this.finishFragment();
                } else if (i == 1 && WalletSettingsActivity.this.getParentActivity() != null) {
                    if (TextUtils.equals(WalletSettingsActivity.this.getUserConfig().walletBlockchainName, WalletSettingsActivity.this.blockchainName)) {
                        WalletSettingsActivity.this.saveConfig(true);
                    } else {
                        Builder builder = new Builder(WalletSettingsActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("Wallet", NUM));
                        builder.setMessage(LocaleController.getString("WalletBlockchainNameWarning", NUM));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        builder.setPositiveButton(LocaleController.getString("WalletContinue", NUM), new -$$Lambda$WalletSettingsActivity$1$viXd3sP3oU1m2rffX1LJRnd3cYE(this));
                        AlertDialog create = builder.create();
                        WalletSettingsActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$WalletSettingsActivity$1(DialogInterface dialogInterface, int i) {
                WalletSettingsActivity.this.saveConfig(true);
            }
        });
        return actionBar;
    }

    private void saveConfig(boolean z) {
        UserConfig userConfig = getUserConfig();
        int equals = TextUtils.equals(userConfig.walletBlockchainName, this.blockchainName) ^ 1;
        int i = this.configType;
        if (i == userConfig.walletConfigType && equals == 0) {
            if (i == 0) {
                i = TextUtils.equals(userConfig.walletConfigUrl, this.blockchainUrl);
            } else {
                i = i == 1 ? TextUtils.equals(userConfig.walletBlockchainName, this.blockchainJson) : 0;
            }
            i ^= 1;
        } else {
            i = 1;
        }
        String str = "WalletBlockchainConfigInvalid";
        String str2 = "WalletError";
        if (i != 0) {
            int i2 = this.configType;
            if (i2 == 1) {
                if (!TextUtils.isEmpty(this.blockchainJson)) {
                    try {
                        JSONObject jSONObject = new JSONObject(this.blockchainJson);
                    } catch (Throwable th) {
                        FileLog.e(th);
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM));
                        return;
                    }
                }
                return;
            } else if (z && i2 == 0) {
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
                WalletConfigLoader.loadConfig(this.blockchainUrl, new -$$Lambda$WalletSettingsActivity$TrEmkwn3XPC-3uHXqyl6C_MRyjM(this, alertDialog));
                return;
            }
        }
        if (i != 0) {
            String str3 = userConfig.walletBlockchainName;
            String str4 = userConfig.walletConfig;
            String str5 = userConfig.walletConfigUrl;
            int i3 = userConfig.walletConfigType;
            String str6 = this.blockchainConfigFromUrl;
            userConfig.walletBlockchainName = this.blockchainName;
            userConfig.walletConfig = this.blockchainJson;
            userConfig.walletConfigUrl = this.blockchainUrl;
            userConfig.walletConfigType = this.configType;
            userConfig.walletConfigFromUrl = str6;
            if (getTonController().onTonConfigUpdated()) {
                userConfig.saveConfig(false);
            } else {
                userConfig.walletBlockchainName = str3;
                userConfig.walletConfig = str4;
                userConfig.walletConfigUrl = str5;
                userConfig.walletConfigType = i3;
                userConfig.walletConfigFromUrl = str6;
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(str2, NUM), LocaleController.getString(str, NUM));
                return;
            }
        }
        if (equals != 0) {
            doLogout();
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment != null) {
                baseFragment.removeSelfFromStack();
            }
            presentFragment(new WalletCreateActivity(0), true);
        } else {
            finishFragment();
        }
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
        Adapter adapter = new Adapter(context);
        this.adapter = adapter;
        recyclerListView.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor("wallet_blackBackground"));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$WalletSettingsActivity$b9eOBP3pgO2Z8_TZGhe1-8pjSkI(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$WalletSettingsActivity(View view, int i) {
        int i2 = 1;
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
            builder.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$WalletSettingsActivity$Spsf9BC1vhyTqoYKLIZBZ52gKmk(this));
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
            doExport(null);
        }
    }

    private void doLogout() {
        getTonController().cleanup();
        UserConfig userConfig = getUserConfig();
        userConfig.clearTonConfig();
        userConfig.saveConfig(false);
    }

    private void doExport(Cipher cipher) {
        if (getParentActivity() != null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCacnel(false);
            alertDialog.show();
            getTonController().getSecretWords(null, cipher, new -$$Lambda$WalletSettingsActivity$xt_-q7SZrWigUB-IwPA0FB1L6Ks(this, alertDialog), new -$$Lambda$WalletSettingsActivity$J8_wBHKD1jX1SMcWPV8uiJbgccc(this, alertDialog));
        }
    }

    public /* synthetic */ void lambda$doExport$3$WalletSettingsActivity(AlertDialog alertDialog, String[] strArr) {
        alertDialog.dismiss();
        WalletCreateActivity walletCreateActivity = new WalletCreateActivity(4);
        walletCreateActivity.setSecretWords(strArr);
        presentFragment(walletCreateActivity);
    }

    public /* synthetic */ void lambda$doExport$4$WalletSettingsActivity(AlertDialog alertDialog, String str, Error error) {
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
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[18];
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
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        return themeDescriptionArr;
    }
}
