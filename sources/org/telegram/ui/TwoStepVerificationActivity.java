package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.math.BigInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC.TL_account_cancelPasswordEmail;
import org.telegram.tgnet.TLRPC.TL_account_confirmPasswordEmail;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_passwordSettings;
import org.telegram.tgnet.TLRPC.TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC.TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoSHA512;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.TL_secureSecretSettings;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private boolean closeAfterSet;
    private EditTextSettingsCell codeFieldCell;
    private TL_account_password currentPassword;
    private byte[] currentPasswordHash;
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimation;
    private String email;
    private int emailCodeLength;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int passwordCodeFieldRow;
    private EditTextBoldCursor passwordEditText;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered;
    private int passwordSetState;
    private int passwordSetupDetailRow;
    private boolean paused;
    private AlertDialog progressDialog;
    private ContextProgressView progressView;
    private int resendCodeRow;
    private int rowCount;
    private ScrollView scrollView;
    private int setPasswordDetailRow;
    private int setPasswordRow;
    private int setRecoveryEmailRow;
    private int shadowRow;
    private Runnable shortPollRunnable;
    private TextView titleTextView;
    private int turnPasswordOffRow;
    private int type;
    private boolean waitingForEmail;

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) ? 0 : TwoStepVerificationActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            if (i == 0) {
                textSettingsCell = new TextSettingsCell(this.mContext);
                textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 1) {
                textSettingsCell = TwoStepVerificationActivity.this.codeFieldCell;
                if (textSettingsCell.getParent() != null) {
                    ((ViewGroup) textSettingsCell.getParent()).removeView(textSettingsCell);
                }
            } else {
                textSettingsCell = new TextInfoPrivacyCell(this.mContext);
            }
            return new Holder(textSettingsCell);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                String str = "windowBackgroundWhiteBlackText";
                textSettingsCell.setTag(str);
                textSettingsCell.setTextColor(Theme.getColor(str));
                String string;
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("SetAdditionalPassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textSettingsCell.setText(LocaleController.getString("TurnPasswordOff", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    string = LocaleController.getString("ChangeRecoveryEmail", NUM);
                    if (TwoStepVerificationActivity.this.abortPasswordRow != -1) {
                        z = true;
                    }
                    textSettingsCell.setText(string, z);
                } else if (i == TwoStepVerificationActivity.this.resendCodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ResendCode", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                } else if (i == TwoStepVerificationActivity.this.abortPasswordRow) {
                    string = "windowBackgroundWhiteRedText3";
                    textSettingsCell.setTag(string);
                    textSettingsCell.setTextColor(Theme.getColor(string));
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        textSettingsCell.setText(LocaleController.getString("AbortPassword", NUM), false);
                    } else {
                        textSettingsCell.setText(LocaleController.getString("AbortEmail", NUM), false);
                    }
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                String str2 = "windowBackgroundGrayShadow";
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                    return;
                }
                String str3 = "";
                if (i == TwoStepVerificationActivity.this.shadowRow) {
                    textInfoPrivacyCell.setText(str3);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    Object[] objArr;
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        objArr = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str3 = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr[0] = str3;
                        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                    } else {
                        objArr = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str3 = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr[0] = str3;
                        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText3", NUM, objArr));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str2));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            return i == TwoStepVerificationActivity.this.passwordCodeFieldRow ? 2 : 0;
        }
    }

    static /* synthetic */ void lambda$checkSecretValues$26(TLObject tLObject, TL_error tL_error) {
    }

    static /* synthetic */ void lambda$null$8(TLObject tLObject, TL_error tL_error) {
    }

    public TwoStepVerificationActivity(int i) {
        this.emailCodeLength = 6;
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.type = i;
        if (i == 0) {
            loadPasswordInfo(false);
        }
    }

    public TwoStepVerificationActivity(int i, int i2) {
        this.emailCodeLength = 6;
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.currentAccount = i;
        this.type = i2;
        if (i2 == 0) {
            loadPasswordInfo(false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void setRecoveryParams(TL_account_password tL_account_password) {
        this.currentPassword = tL_account_password;
        this.passwordSetState = 4;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
            Runnable runnable = this.shortPollRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.shortPollRunnable = null;
            }
            this.destroyed = true;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    TwoStepVerificationActivity.this.finishFragment();
                } else if (i == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        String str = "windowBackgroundWhite";
        frameLayout.setBackgroundColor(Theme.getColor(str));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context2, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.titleTextView = new TextView(context2);
        String str2 = "windowBackgroundWhiteGrayText6";
        this.titleTextView.setTextColor(Theme.getColor(str2));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        this.passwordEditText = new EditTextBoldCursor(context2);
        this.passwordEditText.setTextSize(1, 20.0f);
        String str3 = "windowBackgroundWhiteBlackText";
        this.passwordEditText.setTextColor(Theme.getColor(str3));
        this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor(str3));
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new -$$Lambda$TwoStepVerificationActivity$YWYhM6syo7BDAVMlJEwwl5l8lR0(this));
        this.passwordEditText.setCustomSelectionActionModeCallback(new Callback() {
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.bottomTextView = new TextView(context2);
        this.bottomTextView.setTextColor(Theme.getColor(str2));
        this.bottomTextView.setTextSize(1, 14.0f);
        int i = 5;
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", NUM));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        this.bottomButton = new TextView(context2);
        this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", NUM));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        TextView textView = this.bottomButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout2.addView(textView, LayoutHelper.createLinear(-1, -2, i | 80, 40, 0, 40, 14));
        this.bottomButton.setOnClickListener(new -$$Lambda$TwoStepVerificationActivity$hA8CuA2mAlwBUP9M3CP0dvU1Oc0(this));
        int i2 = this.type;
        if (i2 == 0) {
            this.emptyView = new EmptyTextProgressView(context2);
            this.emptyView.showProgress();
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.listView = new RecyclerListView(context2);
            this.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            this.listView.setEmptyView(this.emptyView);
            this.listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = this.listView;
            ListAdapter listAdapter = new ListAdapter(context2);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new -$$Lambda$TwoStepVerificationActivity$R271ECy978pWdDX5q4Leb46bpF0(this));
            this.codeFieldCell = new EditTextSettingsCell(context2);
            this.codeFieldCell.setTextAndHint("", LocaleController.getString("PasswordCode", NUM), false);
            this.codeFieldCell.setBackgroundColor(Theme.getColor(str));
            EditTextBoldCursor textView2 = this.codeFieldCell.getTextView();
            textView2.setInputType(3);
            textView2.setImeOptions(6);
            textView2.setOnEditorActionListener(new -$$Lambda$TwoStepVerificationActivity$Nl-K_LAtgIdu6AfA5V1bgHfleb8(this));
            textView2.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                public void afterTextChanged(Editable editable) {
                    if (TwoStepVerificationActivity.this.emailCodeLength != 0 && editable.length() == TwoStepVerificationActivity.this.emailCodeLength) {
                        TwoStepVerificationActivity.this.processDone();
                    }
                }
            });
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
            if (this.delegate != null) {
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPasswordTransfer", NUM));
            } else {
                this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
            }
        } else if (i2 == 1) {
            setPasswordSetState(this.passwordSetState);
        }
        if (!this.passwordEntered || this.type == 1) {
            this.fragmentView.setBackgroundColor(Theme.getColor(str));
            this.fragmentView.setTag(str);
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$0$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public /* synthetic */ void lambda$createView$6$TwoStepVerificationActivity(View view) {
        String str = "RestorePasswordNoEmailTitle";
        Builder builder;
        if (this.type == 0) {
            if (this.currentPassword.has_recovery) {
                needShowProgress();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new -$$Lambda$TwoStepVerificationActivity$rnLkpi4dhDuCJJJXKpOcjSAJ1bE(this), 10);
            } else if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", NUM), new -$$Lambda$TwoStepVerificationActivity$Lh9jzeSQrwQKDB5QSE89Hn8p2n8(this));
                builder.setTitle(LocaleController.getString(str, NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", NUM));
                showDialog(builder.create());
            }
        } else if (this.passwordSetState == 4) {
            showAlertWithText(LocaleController.getString(str, NUM), LocaleController.getString("RestoreEmailTroubleText", NUM));
        } else {
            builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new -$$Lambda$TwoStepVerificationActivity$Qtb1CP1a_ctZOdCDJuV26Qenpkg(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$null$3$TwoStepVerificationActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$RrCadl1PvK1PcC0yhZngVlsUO_I(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$2$TwoStepVerificationActivity(TL_error tL_error, TLObject tLObject) {
        needHideProgress();
        String str = "AppName";
        if (tL_error == null) {
            TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) tLObject;
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, tL_auth_passwordRecovery.email_pattern));
            builder.setTitle(LocaleController.getString(str, NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$TwoStepVerificationActivity$o3LczIT3DtDztCPkC-aGamfNhzE(this, tL_auth_passwordRecovery));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
            String formatPluralString;
            int intValue = Utilities.parseInt(tL_error.text).intValue();
            if (intValue < 60) {
                formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
        } else {
            showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
        }
    }

    public /* synthetic */ void lambda$null$1$TwoStepVerificationActivity(TL_auth_passwordRecovery tL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
        twoStepVerificationActivity.currentPassword = this.currentPassword;
        twoStepVerificationActivity.currentPassword.email_unconfirmed_pattern = tL_auth_passwordRecovery.email_pattern;
        twoStepVerificationActivity.currentSecretId = this.currentSecretId;
        twoStepVerificationActivity.currentSecret = this.currentSecret;
        twoStepVerificationActivity.passwordSetState = 4;
        presentFragment(twoStepVerificationActivity);
    }

    public /* synthetic */ void lambda$null$4$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        Context parentActivity = getParentActivity();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://telegram.org/deactivate?phone=");
        stringBuilder.append(UserConfig.getInstance(this.currentAccount).getClientPhone());
        Browser.openUrl(parentActivity, stringBuilder.toString());
    }

    public /* synthetic */ void lambda$null$5$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    public /* synthetic */ void lambda$createView$9$TwoStepVerificationActivity(View view, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity;
        if (i == this.setPasswordRow || i == this.changePasswordRow) {
            twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
            twoStepVerificationActivity.currentPasswordHash = this.currentPasswordHash;
            twoStepVerificationActivity.currentPassword = this.currentPassword;
            twoStepVerificationActivity.currentSecretId = this.currentSecretId;
            twoStepVerificationActivity.currentSecret = this.currentSecret;
            presentFragment(twoStepVerificationActivity);
        } else if (i == this.setRecoveryEmailRow || i == this.changeRecoveryEmailRow) {
            twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
            twoStepVerificationActivity.currentPasswordHash = this.currentPasswordHash;
            twoStepVerificationActivity.currentPassword = this.currentPassword;
            twoStepVerificationActivity.currentSecretId = this.currentSecretId;
            twoStepVerificationActivity.currentSecret = this.currentSecret;
            twoStepVerificationActivity.emailOnly = true;
            twoStepVerificationActivity.passwordSetState = 3;
            presentFragment(twoStepVerificationActivity);
        } else {
            String str = "OK";
            String str2 = "AppName";
            Builder builder;
            if (i == this.turnPasswordOffRow || i == this.abortPasswordRow) {
                CharSequence string;
                builder = new Builder(getParentActivity());
                if (i == this.abortPasswordRow) {
                    TL_account_password tL_account_password = this.currentPassword;
                    if (tL_account_password == null || !tL_account_password.has_password) {
                        string = LocaleController.getString("CancelPasswordQuestion", NUM);
                    } else {
                        string = LocaleController.getString("CancelEmailQuestion", NUM);
                    }
                } else {
                    string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
                    if (this.currentPassword.has_secure_values) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(string);
                        stringBuilder.append("\n\n");
                        stringBuilder.append(LocaleController.getString("TurnPasswordOffPassport", NUM));
                        string = stringBuilder.toString();
                    }
                }
                builder.setMessage(string);
                builder.setTitle(LocaleController.getString(str2, NUM));
                builder.setPositiveButton(LocaleController.getString(str, NUM), new -$$Lambda$TwoStepVerificationActivity$10etTYa5XWjUNmwZWDDim1g1lNY(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                showDialog(builder.create());
            } else if (i == this.resendCodeRow) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resendPasswordEmail(), -$$Lambda$TwoStepVerificationActivity$4F6YJgV3MBVhnoAYE2KFt09BlNg.INSTANCE);
                builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
                builder.setTitle(LocaleController.getString(str2, NUM));
                builder.setPositiveButton(LocaleController.getString(str, NUM), null);
                showDialog(builder.create());
            }
        }
    }

    public /* synthetic */ void lambda$null$7$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        setNewPassword(true);
    }

    public /* synthetic */ boolean lambda$createView$10$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetTwoStepPassword) {
            if (!(objArr == null || objArr.length <= 0 || objArr[0] == null)) {
                this.currentPasswordHash = (byte[]) objArr[0];
                if (this.closeAfterSet && TextUtils.isEmpty((String) objArr[4]) && this.closeAfterSet) {
                    removeSelfFromStack();
                }
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public void onPause() {
        super.onPause();
        this.paused = true;
    }

    public void onResume() {
        super.onResume();
        this.paused = false;
        int i = this.type;
        if (i == 1) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$K5PXZQZsq-ZtNeeIBTIP6T-teFE(this), 200);
        } else if (i == 0) {
            EditTextSettingsCell editTextSettingsCell = this.codeFieldCell;
            if (editTextSettingsCell != null && editTextSettingsCell.getVisibility() == 0) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$3nO3FhnPEKwuXE4FLUhVmyh2GII(this), 200);
            }
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public /* synthetic */ void lambda$onResume$11$TwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    public /* synthetic */ void lambda$onResume$12$TwoStepVerificationActivity() {
        EditTextSettingsCell editTextSettingsCell = this.codeFieldCell;
        if (editTextSettingsCell != null) {
            editTextSettingsCell.getTextView().requestFocus();
            AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
        }
    }

    public void setCloseAfterSet(boolean z) {
        this.closeAfterSet = z;
    }

    public void setCurrentPasswordInfo(byte[] bArr, TL_account_password tL_account_password) {
        if (bArr != null) {
            this.currentPasswordHash = bArr;
        }
        this.currentPassword = tL_account_password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            int i = this.type;
            if (i == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            } else if (i == 0) {
                EditTextSettingsCell editTextSettingsCell = this.codeFieldCell;
                if (editTextSettingsCell != null && editTextSettingsCell.getVisibility() == 0) {
                    AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
                }
            }
        }
    }

    public static boolean canHandleCurrentPassword(TL_account_password tL_account_password, boolean z) {
        if (z) {
            if (tL_account_password.current_algo instanceof TL_passwordKdfAlgoUnknown) {
                return false;
            }
        } else if ((tL_account_password.new_algo instanceof TL_passwordKdfAlgoUnknown) || (tL_account_password.current_algo instanceof TL_passwordKdfAlgoUnknown) || (tL_account_password.new_secure_algo instanceof TL_securePasswordKdfAlgoUnknown)) {
            return false;
        }
        return true;
    }

    public static void initPasswordNewAlgo(TL_account_password tL_account_password) {
        byte[] bArr;
        PasswordKdfAlgo passwordKdfAlgo = tL_account_password.new_algo;
        if (passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo;
            bArr = new byte[(tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1.length + 32)];
            Utilities.random.nextBytes(bArr);
            byte[] bArr2 = tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1;
            System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
            tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = bArr;
        }
        SecurePasswordKdfAlgo securePasswordKdfAlgo = tL_account_password.new_secure_algo;
        if (securePasswordKdfAlgo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo;
            byte[] bArr3 = new byte[(tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt.length + 32)];
            Utilities.random.nextBytes(bArr3);
            bArr = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt;
            System.arraycopy(bArr, 0, bArr3, 0, bArr.length);
            tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt = bArr3;
        }
    }

    private void loadPasswordInfo(boolean z) {
        if (!z) {
            this.loading = true;
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$TwoStepVerificationActivity$oYcvtMp3mI7Cv13Fi4uxabOCA00(this, z), 10);
    }

    public /* synthetic */ void lambda$loadPasswordInfo$14$TwoStepVerificationActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$4P2nkSO_oWh_p7A_7V0YWykRkO0(this, tL_error, tLObject, z));
    }

    public /* synthetic */ void lambda$null$13$TwoStepVerificationActivity(TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.loading = false;
            this.currentPassword = (TL_account_password) tLObject;
            if (canHandleCurrentPassword(this.currentPassword, false)) {
                if (!z) {
                    byte[] bArr = this.currentPasswordHash;
                    boolean z2 = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
                    this.passwordEntered = z2;
                }
                this.waitingForEmail = TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern) ^ 1;
                initPasswordNewAlgo(this.currentPassword);
                if (!this.paused && this.closeAfterSet) {
                    TL_account_password tL_account_password = this.currentPassword;
                    if (tL_account_password.has_password) {
                        PasswordKdfAlgo passwordKdfAlgo = tL_account_password.current_algo;
                        SecurePasswordKdfAlgo securePasswordKdfAlgo = tL_account_password.new_secure_algo;
                        byte[] bArr2 = tL_account_password.secure_random;
                        String str = tL_account_password.has_recovery ? "1" : null;
                        String str2 = this.currentPassword.hint;
                        if (str2 == null) {
                            str2 = "";
                        }
                        if (!(this.waitingForEmail || passwordKdfAlgo == null)) {
                            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
                            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, null, passwordKdfAlgo, securePasswordKdfAlgo, bArr2, str, str2, null, null);
                            finishFragment();
                        }
                    }
                }
            } else {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
        }
        if (this.type == 0 && !this.destroyed && this.shortPollRunnable == null) {
            TL_account_password tL_account_password2 = this.currentPassword;
            if (!(tL_account_password2 == null || TextUtils.isEmpty(tL_account_password2.email_unconfirmed_pattern))) {
                startShortpoll();
            }
        }
        updateRows();
    }

    private void startShortpoll() {
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.shortPollRunnable = new -$$Lambda$TwoStepVerificationActivity$wO-OLA1D7DR12e78ifRLekXVYec(this);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 5000);
    }

    public /* synthetic */ void lambda$startShortpoll$15$TwoStepVerificationActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo(true);
            this.shortPollRunnable = null;
        }
    }

    private void setPasswordSetState(int i) {
        if (this.passwordEditText != null) {
            this.passwordSetState = i;
            i = this.passwordSetState;
            String str = "";
            String str2 = "YourPassword";
            int i2 = 4;
            TextView textView;
            if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString(str2, NUM));
                if (this.currentPassword.has_password) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", NUM));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", NUM));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString(str2, NUM));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (i == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (i == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", NUM));
                this.titleTextView.setText(LocaleController.getString("YourEmail", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                textView = this.bottomButton;
                if (!this.emailOnly) {
                    i2 = 0;
                }
                textView.setVisibility(i2);
            } else if (i == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", NUM));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", NUM));
                textView = this.bottomButton;
                Object[] objArr = new Object[1];
                String str3 = this.currentPassword.email_unconfirmed_pattern;
                if (str3 == null) {
                    str3 = str;
                }
                objArr[0] = str3;
                textView.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText(str);
        }
    }

    private void updateRows() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.setPasswordRow);
        stringBuilder.append(this.setPasswordDetailRow);
        stringBuilder.append(this.changePasswordRow);
        stringBuilder.append(this.turnPasswordOffRow);
        stringBuilder.append(this.setRecoveryEmailRow);
        stringBuilder.append(this.changeRecoveryEmailRow);
        stringBuilder.append(this.resendCodeRow);
        stringBuilder.append(this.abortPasswordRow);
        stringBuilder.append(this.passwordSetupDetailRow);
        stringBuilder.append(this.passwordCodeFieldRow);
        stringBuilder.append(this.passwordEnabledDetailRow);
        stringBuilder.append(this.shadowRow);
        stringBuilder.append(this.rowCount);
        Object obj = this.passwordCodeFieldRow != -1 ? 1 : null;
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.abortPasswordRow = -1;
        this.resendCodeRow = -1;
        this.passwordSetupDetailRow = -1;
        this.passwordCodeFieldRow = -1;
        this.passwordEnabledDetailRow = -1;
        this.shadowRow = -1;
        if (!this.loading) {
            TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password != null) {
                int i;
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordCodeFieldRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordSetupDetailRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.resendCodeRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.abortPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.shadowRow = i;
                } else if (tL_account_password.has_password) {
                    int i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.changePasswordRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.turnPasswordOffRow = i2;
                    if (tL_account_password.has_recovery) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.changeRecoveryEmailRow = i;
                    } else {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.setRecoveryEmailRow = i;
                    }
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordEnabledDetailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.setPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.setPasswordDetailRow = i;
                }
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.setPasswordRow);
        stringBuilder2.append(this.setPasswordDetailRow);
        stringBuilder2.append(this.changePasswordRow);
        stringBuilder2.append(this.turnPasswordOffRow);
        stringBuilder2.append(this.setRecoveryEmailRow);
        stringBuilder2.append(this.changeRecoveryEmailRow);
        stringBuilder2.append(this.resendCodeRow);
        stringBuilder2.append(this.abortPasswordRow);
        stringBuilder2.append(this.passwordSetupDetailRow);
        stringBuilder2.append(this.passwordCodeFieldRow);
        stringBuilder2.append(this.passwordEnabledDetailRow);
        stringBuilder2.append(this.shadowRow);
        stringBuilder2.append(this.rowCount);
        String str = "";
        if (!(this.listAdapter == null || stringBuilder.toString().equals(stringBuilder2.toString()))) {
            this.listAdapter.notifyDataSetChanged();
            if (!(this.passwordCodeFieldRow != -1 || getParentActivity() == null || obj == null)) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                this.codeFieldCell.setText(str, false);
            }
        }
        if (this.fragmentView == null) {
            return;
        }
        RecyclerListView recyclerListView;
        if (this.loading || this.passwordEntered) {
            recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.waitingForEmail && this.currentPassword != null) {
                this.doneItem.setVisibility(0);
            } else if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            }
            String str2 = "windowBackgroundGray";
            this.fragmentView.setBackgroundColor(Theme.getColor(str2));
            this.fragmentView.setTag(str2);
            return;
        }
        recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.setEmptyView(null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.emptyView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            String str3 = "windowBackgroundWhite";
            this.fragmentView.setBackgroundColor(Theme.getColor(str3));
            this.fragmentView.setTag(str3);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
            if (TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(str);
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$9VaVKy9W3pbWvJiDnN2bOVx7AfA(this), 200);
        }
    }

    public /* synthetic */ void lambda$updateRows$16$TwoStepVerificationActivity() {
        if (!isFinishing() && !this.destroyed) {
            EditTextBoldCursor editTextBoldCursor = this.passwordEditText;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                AndroidUtilities.showKeyboard(this.passwordEditText);
            }
        }
    }

    private void showDoneProgress(boolean z) {
        final boolean z2 = z;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        String str = "alpha";
        String str2 = "scaleY";
        String str3 = "scaleX";
        if (z2) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            AnimatorSet animatorSet2 = this.doneItemAnimation;
            Animator[] animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{1.0f});
            animatorSet2.playTogether(animatorArr);
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            animatorSet = this.doneItemAnimation;
            Animator[] animatorArr2 = new Animator[6];
            animatorArr2[0] = ObjectAnimator.ofFloat(this.progressView, str3, new float[]{0.1f});
            animatorArr2[1] = ObjectAnimator.ofFloat(this.progressView, str2, new float[]{0.1f});
            animatorArr2[2] = ObjectAnimator.ofFloat(this.progressView, str, new float[]{0.0f});
            animatorArr2[3] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str3, new float[]{1.0f});
            animatorArr2[4] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str2, new float[]{1.0f});
            animatorArr2[5] = ObjectAnimator.ofFloat(this.doneItem.getContentView(), str, new float[]{1.0f});
            animatorSet.playTogether(animatorArr2);
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animator)) {
                    if (z2) {
                        TwoStepVerificationActivity.this.doneItem.getContentView().setVisibility(4);
                    } else {
                        TwoStepVerificationActivity.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animator)) {
                    TwoStepVerificationActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    /* Access modifiers changed, original: protected */
    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
    }

    private boolean isValidEmail(String str) {
        if (str == null || str.length() < 3) {
            return false;
        }
        int lastIndexOf = str.lastIndexOf(46);
        int lastIndexOf2 = str.lastIndexOf(64);
        if (lastIndexOf2 < 0 || lastIndexOf < lastIndexOf2) {
            return false;
        }
        return true;
    }

    private void showAlertWithText(String str, String str2) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void setNewPassword(boolean z) {
        if (z && this.waitingForEmail && this.currentPassword.has_password) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_cancelPasswordEmail(), new -$$Lambda$TwoStepVerificationActivity$XEU2evar_cmakN0jjZyU1ShYsjA(this));
            return;
        }
        String str = this.firstPassword;
        TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            tL_account_updatePasswordSettings.password = new TL_inputCheckPasswordEmpty();
        }
        tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
        String str2 = "";
        if (z) {
            UserConfig.getInstance(this.currentAccount).resetSavedPassword();
            this.currentSecret = null;
            if (this.waitingForEmail) {
                TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags = 2;
                tL_account_passwordInputSettings.email = str2;
                tL_account_updatePasswordSettings.password = new TL_inputCheckPasswordEmpty();
            } else {
                TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings2.flags = 3;
                tL_account_passwordInputSettings2.hint = str2;
                tL_account_passwordInputSettings2.new_password_hash = new byte[0];
                tL_account_passwordInputSettings2.new_algo = new TL_passwordKdfAlgoUnknown();
                tL_account_updatePasswordSettings.new_settings.email = str2;
            }
        } else {
            TL_account_passwordInputSettings tL_account_passwordInputSettings3;
            if (this.hint == null) {
                TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password != null) {
                    this.hint = tL_account_password.hint;
                }
            }
            if (this.hint == null) {
                this.hint = str2;
            }
            if (str != null) {
                tL_account_passwordInputSettings3 = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings3.flags |= 1;
                tL_account_passwordInputSettings3.hint = this.hint;
                tL_account_passwordInputSettings3.new_algo = this.currentPassword.new_algo;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings3 = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings3.flags = 2 | tL_account_passwordInputSettings3.flags;
                tL_account_passwordInputSettings3.email = this.email.trim();
            }
        }
        needShowProgress();
        Utilities.globalQueue.postRunnable(new -$$Lambda$TwoStepVerificationActivity$BbXpzRMc1z5d7yEqTJj2C6ReLvg(this, tL_account_updatePasswordSettings, z, str));
    }

    public /* synthetic */ void lambda$setNewPassword$18$TwoStepVerificationActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$ecl8yWbW6dpt4FVHunWRv6TTi0Y(this, tL_error));
    }

    public /* synthetic */ void lambda$null$17$TwoStepVerificationActivity(TL_error tL_error) {
        needHideProgress();
        if (tL_error == null) {
            loadPasswordInfo(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            updateRows();
        }
    }

    public /* synthetic */ void lambda$setNewPassword$25$TwoStepVerificationActivity(TL_account_updatePasswordSettings tL_account_updatePasswordSettings, boolean z, String str) {
        byte[] bArr;
        byte[] bArr2;
        PasswordKdfAlgo passwordKdfAlgo;
        TL_account_updatePasswordSettings tL_account_updatePasswordSettings2 = tL_account_updatePasswordSettings;
        if (tL_account_updatePasswordSettings2.password == null) {
            tL_account_updatePasswordSettings2.password = getNewSrpPassword();
        }
        if (z || str == null) {
            bArr = null;
            bArr2 = bArr;
        } else {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str);
            passwordKdfAlgo = this.currentPassword.new_algo;
            if (passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                bArr2 = stringBytes;
                bArr = SRPHelper.getX(stringBytes, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
            } else {
                bArr2 = stringBytes;
                bArr = null;
            }
        }
        -$$Lambda$TwoStepVerificationActivity$VJV5wRfpqB6jJ8dEtoqePT3HaYk -__lambda_twostepverificationactivity_vjv5wrfpqb6jj8detoqept3hayk = new -$$Lambda$TwoStepVerificationActivity$VJV5wRfpqB6jJ8dEtoqePT3HaYk(this, z, bArr, tL_account_updatePasswordSettings, str);
        if (z) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings2, -__lambda_twostepverificationactivity_vjv5wrfpqb6jj8detoqept3hayk, 10);
            return;
        }
        if (str != null) {
            byte[] bArr3 = this.currentSecret;
            if (bArr3 != null && bArr3.length == 32) {
                SecurePasswordKdfAlgo securePasswordKdfAlgo = this.currentPassword.new_secure_algo;
                if (securePasswordKdfAlgo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo;
                    bArr = Utilities.computePBKDF2(bArr2, tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                    byte[] bArr4 = new byte[32];
                    System.arraycopy(bArr, 0, bArr4, 0, 32);
                    byte[] bArr5 = new byte[16];
                    System.arraycopy(bArr, 32, bArr5, 0, 16);
                    bArr = new byte[32];
                    System.arraycopy(this.currentSecret, 0, bArr, 0, 32);
                    Utilities.aesCbcEncryptionByteArraySafe(bArr, bArr4, bArr5, 0, bArr.length, 0, 1);
                    tL_account_updatePasswordSettings2.new_settings.new_secure_settings = new TL_secureSecretSettings();
                    TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings2.new_settings;
                    TL_secureSecretSettings tL_secureSecretSettings = tL_account_passwordInputSettings.new_secure_settings;
                    tL_secureSecretSettings.secure_algo = tL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
                    tL_secureSecretSettings.secure_secret = bArr;
                    tL_secureSecretSettings.secure_secret_id = this.currentSecretId;
                    tL_account_passwordInputSettings.flags |= 4;
                }
            }
        }
        passwordKdfAlgo = this.currentPassword.new_algo;
        if (passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            if (str != null) {
                TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo;
                tL_account_updatePasswordSettings2.new_settings.new_password_hash = SRPHelper.getVBytes(bArr2, tL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow);
                if (tL_account_updatePasswordSettings2.new_settings.new_password_hash == null) {
                    TL_error tL_error = new TL_error();
                    tL_error.text = "ALGO_INVALID";
                    -__lambda_twostepverificationactivity_vjv5wrfpqb6jj8detoqept3hayk.run(null, tL_error);
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings2, -__lambda_twostepverificationactivity_vjv5wrfpqb6jj8detoqept3hayk, 10);
            return;
        }
        TL_error tL_error2 = new TL_error();
        tL_error2.text = "PASSWORD_HASH_INVALID";
        -__lambda_twostepverificationactivity_vjv5wrfpqb6jj8detoqept3hayk.run(null, tL_error2);
    }

    public /* synthetic */ void lambda$null$24$TwoStepVerificationActivity(boolean z, byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$tWVKNMhPWigeKF9TDwsCxxYPaU0(this, tL_error, z, tLObject, bArr, tL_account_updatePasswordSettings, str));
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a4  */
    public /* synthetic */ void lambda$null$23$TwoStepVerificationActivity(org.telegram.tgnet.TLRPC.TL_error r4, boolean r5, org.telegram.tgnet.TLObject r6, byte[] r7, org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings r8, java.lang.String r9) {
        /*
        r3 = this;
        if (r4 == 0) goto L_0x0022;
    L_0x0002:
        r0 = r4.text;
        r1 = "SRP_ID_INVALID";
        r0 = r1.equals(r0);
        if (r0 == 0) goto L_0x0022;
    L_0x000c:
        r4 = new org.telegram.tgnet.TLRPC$TL_account_getPassword;
        r4.<init>();
        r6 = r3.currentAccount;
        r6 = org.telegram.tgnet.ConnectionsManager.getInstance(r6);
        r7 = new org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$juf-ayzPNKWkx2ZD2C5VwnmYfxI;
        r7.<init>(r3, r5);
        r5 = 8;
        r6.sendRequest(r4, r7, r5);
        return;
    L_0x0022:
        r3.needHideProgress();
        r0 = NUM; // 0x7f0d06d0 float:1.8745652E38 double:1.053130639E-314;
        r1 = "OK";
        r2 = 0;
        if (r4 != 0) goto L_0x00ac;
    L_0x002d:
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_boolTrue;
        if (r6 == 0) goto L_0x00ac;
    L_0x0031:
        if (r5 == 0) goto L_0x004f;
    L_0x0033:
        r4 = 0;
        r3.currentPassword = r4;
        r4 = new byte[r2];
        r3.currentPasswordHash = r4;
        r3.loadPasswordInfo(r2);
        r4 = r3.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.didRemoveTwoStepPassword;
        r6 = new java.lang.Object[r2];
        r4.postNotificationName(r5, r6);
        r3.updateRows();
        goto L_0x0174;
    L_0x004f:
        r4 = r3.getParentActivity();
        if (r4 != 0) goto L_0x0056;
    L_0x0055:
        return;
    L_0x0056:
        r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5 = r3.getParentActivity();
        r4.<init>(r5);
        r5 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r6 = new org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$6t0ApRpo2RwhN-WZ8Os0Xk1Kb5c;
        r6.<init>(r3, r7, r8);
        r4.setPositiveButton(r5, r6);
        if (r9 != 0) goto L_0x0082;
    L_0x006d:
        r5 = r3.currentPassword;
        if (r5 == 0) goto L_0x0082;
    L_0x0071:
        r5 = r5.has_password;
        if (r5 == 0) goto L_0x0082;
    L_0x0075:
        r5 = NUM; // 0x7f0d0b08 float:1.8747842E38 double:1.053131173E-314;
        r6 = "YourEmailSuccessText";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setMessage(r5);
        goto L_0x008e;
    L_0x0082:
        r5 = NUM; // 0x7f0d0b0d float:1.8747853E38 double:1.0531311753E-314;
        r6 = "YourPasswordSuccessText";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setMessage(r5);
    L_0x008e:
        r5 = NUM; // 0x7f0d0b0c float:1.874785E38 double:1.053131175E-314;
        r6 = "YourPasswordSuccess";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setTitle(r5);
        r4 = r4.create();
        r4 = r3.showDialog(r4);
        if (r4 == 0) goto L_0x0174;
    L_0x00a4:
        r4.setCanceledOnTouchOutside(r2);
        r4.setCancelable(r2);
        goto L_0x0174;
    L_0x00ac:
        if (r4 == 0) goto L_0x0174;
    L_0x00ae:
        r5 = r4.text;
        r6 = "EMAIL_UNCONFIRMED";
        r5 = r6.equals(r5);
        if (r5 != 0) goto L_0x012a;
    L_0x00b8:
        r5 = r4.text;
        r6 = "EMAIL_UNCONFIRMED_";
        r5 = r5.startsWith(r6);
        if (r5 == 0) goto L_0x00c3;
    L_0x00c2:
        goto L_0x012a;
    L_0x00c3:
        r5 = r4.text;
        r6 = "EMAIL_INVALID";
        r5 = r6.equals(r5);
        r6 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r7 = "AppName";
        if (r5 == 0) goto L_0x00e4;
    L_0x00d2:
        r4 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r5 = NUM; // 0x7f0d07ca float:1.874616E38 double:1.0531307627E-314;
        r6 = "PasswordEmailInvalid";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r3.showAlertWithText(r4, r5);
        goto L_0x0174;
    L_0x00e4:
        r5 = r4.text;
        r8 = "FLOOD_WAIT";
        r5 = r5.startsWith(r8);
        if (r5 == 0) goto L_0x0120;
    L_0x00ee:
        r4 = r4.text;
        r4 = org.telegram.messenger.Utilities.parseInt(r4);
        r4 = r4.intValue();
        r5 = 60;
        if (r4 >= r5) goto L_0x0103;
    L_0x00fc:
        r5 = "Seconds";
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4);
        goto L_0x010a;
    L_0x0103:
        r4 = r4 / r5;
        r5 = "Minutes";
        r4 = org.telegram.messenger.LocaleController.formatPluralString(r5, r4);
    L_0x010a:
        r5 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = NUM; // 0x7f0d0468 float:1.8744403E38 double:1.053130335E-314;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r7[r2] = r4;
        r4 = "FloodWaitTime";
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r6, r7);
        r3.showAlertWithText(r5, r4);
        goto L_0x0174;
    L_0x0120:
        r5 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r4 = r4.text;
        r3.showAlertWithText(r5, r4);
        goto L_0x0174;
    L_0x012a:
        r4 = r3.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r5 = org.telegram.messenger.NotificationCenter.didSetTwoStepPassword;
        r6 = new java.lang.Object[r2];
        r4.postNotificationName(r5, r6);
        r4 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5 = r3.getParentActivity();
        r4.<init>(r5);
        r5 = org.telegram.messenger.LocaleController.getString(r1, r0);
        r6 = new org.telegram.ui.-$$Lambda$TwoStepVerificationActivity$dZl6L7A3pdg7u0zh2dz9t60mIJU;
        r6.<init>(r3, r7, r8);
        r4.setPositiveButton(r5, r6);
        r5 = NUM; // 0x7f0d0b01 float:1.8747828E38 double:1.0531311693E-314;
        r6 = "YourEmailAlmostThereText";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setMessage(r5);
        r5 = NUM; // 0x7f0d0b00 float:1.8747826E38 double:1.053131169E-314;
        r6 = "YourEmailAlmostThere";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r4.setTitle(r5);
        r4 = r4.create();
        r4 = r3.showDialog(r4);
        if (r4 == 0) goto L_0x0174;
    L_0x016e:
        r4.setCanceledOnTouchOutside(r2);
        r4.setCancelable(r2);
    L_0x0174:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.TwoStepVerificationActivity.lambda$null$23$TwoStepVerificationActivity(org.telegram.tgnet.TLRPC$TL_error, boolean, org.telegram.tgnet.TLObject, byte[], org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings, java.lang.String):void");
    }

    public /* synthetic */ void lambda$null$20$TwoStepVerificationActivity(boolean z, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$rcQJqDBv63lm3h19T-uWVt6J7mA(this, tL_error, tLObject, z));
    }

    public /* synthetic */ void lambda$null$19$TwoStepVerificationActivity(TL_error tL_error, TLObject tLObject, boolean z) {
        if (tL_error == null) {
            this.currentPassword = (TL_account_password) tLObject;
            initPasswordNewAlgo(this.currentPassword);
            setNewPassword(z);
        }
    }

    public /* synthetic */ void lambda$null$21$TwoStepVerificationActivity(byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, DialogInterface dialogInterface, int i) {
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        i = NotificationCenter.didSetTwoStepPassword;
        r0 = new Object[8];
        TL_account_password tL_account_password = this.currentPassword;
        r0[2] = tL_account_password.new_secure_algo;
        r0[3] = tL_account_password.secure_random;
        r0[4] = this.email;
        r0[5] = this.hint;
        r0[6] = null;
        r0[7] = this.firstPassword;
        instance.postNotificationName(i, r0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$22$TwoStepVerificationActivity(byte[] bArr, TL_account_updatePasswordSettings tL_account_updatePasswordSettings, DialogInterface dialogInterface, int i) {
        if (this.closeAfterSet) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 0);
            twoStepVerificationActivity.setCloseAfterSet(true);
            ActionBarLayout actionBarLayout = this.parentLayout;
            actionBarLayout.addFragmentToStack(twoStepVerificationActivity, actionBarLayout.fragmentsStack.size() - 1);
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        r2 = new Object[8];
        TL_account_password tL_account_password = this.currentPassword;
        r2[2] = tL_account_password.new_secure_algo;
        r2[3] = tL_account_password.secure_random;
        String str = this.email;
        r2[4] = str;
        r2[5] = this.hint;
        r2[6] = str;
        r2[7] = this.firstPassword;
        instance.postNotificationName(i2, r2);
        finishFragment();
    }

    /* Access modifiers changed, original: protected */
    public TL_inputCheckPasswordSRP getNewSrpPassword() {
        TL_account_password tL_account_password = this.currentPassword;
        PasswordKdfAlgo passwordKdfAlgo = tL_account_password.current_algo;
        if (!(passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, tL_account_password.srp_id, tL_account_password.srp_B, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo);
    }

    private boolean checkSecretValues(byte[] bArr, TL_account_passwordSettings tL_account_passwordSettings) {
        TL_secureSecretSettings tL_secureSecretSettings = tL_account_passwordSettings.secure_settings;
        if (tL_secureSecretSettings != null) {
            Object computePBKDF2;
            this.currentSecret = tL_secureSecretSettings.secure_secret;
            SecurePasswordKdfAlgo securePasswordKdfAlgo = tL_secureSecretSettings.secure_algo;
            if (securePasswordKdfAlgo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                computePBKDF2 = Utilities.computePBKDF2(bArr, ((TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) securePasswordKdfAlgo).salt);
            } else if (!(securePasswordKdfAlgo instanceof TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                byte[] bArr2 = ((TL_securePasswordKdfAlgoSHA512) securePasswordKdfAlgo).salt;
                computePBKDF2 = Utilities.computeSHA512(bArr2, bArr, bArr2);
            }
            this.currentSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] bArr3 = new byte[32];
            System.arraycopy(computePBKDF2, 0, bArr3, 0, 32);
            byte[] bArr4 = new byte[16];
            System.arraycopy(computePBKDF2, 32, bArr4, 0, 16);
            byte[] bArr5 = this.currentSecret;
            Utilities.aesCbcEncryptionByteArraySafe(bArr5, bArr3, bArr4, 0, bArr5.length, 0, 0);
            TL_secureSecretSettings tL_secureSecretSettings2 = tL_account_passwordSettings.secure_settings;
            if (!PassportActivity.checkSecret(tL_secureSecretSettings2.secure_secret, Long.valueOf(tL_secureSecretSettings2.secure_secret_id))) {
                TL_account_updatePasswordSettings tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
                tL_account_updatePasswordSettings.password = getNewSrpPassword();
                tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
                tL_account_updatePasswordSettings.new_settings.new_secure_settings = new TL_secureSecretSettings();
                TL_secureSecretSettings tL_secureSecretSettings3 = tL_account_updatePasswordSettings.new_settings.new_secure_settings;
                tL_secureSecretSettings3.secure_secret = new byte[0];
                tL_secureSecretSettings3.secure_algo = new TL_securePasswordKdfAlgoUnknown();
                TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.new_secure_settings.secure_secret_id = 0;
                tL_account_passwordInputSettings.flags |= 4;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, -$$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM.INSTANCE);
                this.currentSecret = null;
                this.currentSecretId = 0;
            }
        } else {
            this.currentSecret = null;
            this.currentSecretId = 0;
        }
        return true;
    }

    private static byte[] getBigIntegerBytes(BigInteger bigInteger) {
        byte[] toByteArray = bigInteger.toByteArray();
        if (toByteArray.length <= 256) {
            return toByteArray;
        }
        byte[] bArr = new byte[256];
        System.arraycopy(toByteArray, 1, bArr, 0, 256);
        return bArr;
    }

    private void processDone() {
        int i = this.type;
        String obj;
        if (i == 0) {
            if (!this.passwordEntered) {
                obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                byte[] stringBytes = AndroidUtilities.getStringBytes(obj);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new -$$Lambda$TwoStepVerificationActivity$q99ypmQww_xNEmbw14QjiBotShg(this, stringBytes));
            } else if (this.waitingForEmail && this.currentPassword != null) {
                if (this.codeFieldCell.length() == 0) {
                    onFieldError(this.codeFieldCell.getTextView(), false);
                } else {
                    sendEmailConfirm(this.codeFieldCell.getText());
                    showDoneProgress(true);
                }
            }
        } else if (i == 1) {
            i = this.passwordSetState;
            if (i == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", NUM));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (i == 1) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                } else {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    onFieldError(this.passwordEditText, true);
                }
            } else if (i == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", NUM), 0).show();
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    onFieldError(this.passwordEditText, false);
                } else if (this.currentPassword.has_recovery) {
                    this.email = "";
                    setNewPassword(false);
                } else {
                    setPasswordSetState(3);
                }
            } else if (i == 3) {
                this.email = this.passwordEditText.getText().toString();
                if (isValidEmail(this.email)) {
                    setNewPassword(false);
                } else {
                    onFieldError(this.passwordEditText, false);
                }
            } else if (i == 4) {
                obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                TL_auth_recoverPassword tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_auth_recoverPassword, new -$$Lambda$TwoStepVerificationActivity$f9cuV_saSqd5BKXFxtWSGi8SzTE(this), 10);
            }
        }
    }

    public /* synthetic */ void lambda$processDone$33$TwoStepVerificationActivity(byte[] bArr) {
        TL_account_getPasswordSettings tL_account_getPasswordSettings = new TL_account_getPasswordSettings();
        PasswordKdfAlgo passwordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = passwordKdfAlgo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo) : null;
        -$$Lambda$TwoStepVerificationActivity$9nDa_eoqxtx9fYaZZmibmVZpEjY -__lambda_twostepverificationactivity_9nda_eoqxtx9fyazzmibmvzpejy = new -$$Lambda$TwoStepVerificationActivity$9nDa_eoqxtx9fYaZZmibmVZpEjY(this, bArr, x);
        TL_account_password tL_account_password = this.currentPassword;
        PasswordKdfAlgo passwordKdfAlgo2 = tL_account_password.current_algo;
        TL_error tL_error;
        if (passwordKdfAlgo2 instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            tL_account_getPasswordSettings.password = SRPHelper.startCheck(x, tL_account_password.srp_id, tL_account_password.srp_B, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) passwordKdfAlgo2);
            if (tL_account_getPasswordSettings.password == null) {
                tL_error = new TL_error();
                tL_error.text = "ALGO_INVALID";
                -__lambda_twostepverificationactivity_9nda_eoqxtx9fyazzmibmvzpejy.run(null, tL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPasswordSettings, -__lambda_twostepverificationactivity_9nda_eoqxtx9fyazzmibmvzpejy, 10);
        } else {
            tL_error = new TL_error();
            tL_error.text = "PASSWORD_HASH_INVALID";
            -__lambda_twostepverificationactivity_9nda_eoqxtx9fyazzmibmvzpejy.run(null, tL_error);
        }
    }

    public /* synthetic */ void lambda$null$32$TwoStepVerificationActivity(byte[] bArr, byte[] bArr2, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            Utilities.globalQueue.postRunnable(new -$$Lambda$TwoStepVerificationActivity$WUc7fmeEGuagN_JxrWFhQ9_Sfr0(this, bArr, tLObject, bArr2));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$E4rg93feEBQhYJq6Ifx6EQm4LFA(this, tL_error));
        }
    }

    public /* synthetic */ void lambda$null$28$TwoStepVerificationActivity(byte[] bArr, TLObject tLObject, byte[] bArr2) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$xhbQi4PJ11MM_ccHkQglBt5UyIo(this, checkSecretValues(bArr, (TL_account_passwordSettings) tLObject), bArr2));
    }

    public /* synthetic */ void lambda$null$27$TwoStepVerificationActivity(boolean z, byte[] bArr) {
        if (this.delegate == null || !z) {
            needHideProgress();
        }
        if (z) {
            this.currentPasswordHash = bArr;
            this.passwordEntered = true;
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate = this.delegate;
            if (twoStepVerificationActivityDelegate != null) {
                twoStepVerificationActivityDelegate.didEnterPassword(getNewSrpPassword());
                return;
            } else {
                updateRows();
                return;
            }
        }
        AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
    }

    public /* synthetic */ void lambda$null$31$TwoStepVerificationActivity(TL_error tL_error) {
        if ("SRP_ID_INVALID".equals(tL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new -$$Lambda$TwoStepVerificationActivity$Rpy7sAZdpMPMoVwAFeMyYhG4S6U(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tL_error.text)) {
            onFieldError(this.passwordEditText, true);
        } else {
            String str = "AppName";
            if (tL_error.text.startsWith("FLOOD_WAIT")) {
                String formatPluralString;
                int intValue = Utilities.parseInt(tL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
            } else {
                showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
            }
        }
    }

    public /* synthetic */ void lambda$null$30$TwoStepVerificationActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$6TAE9CBLd9XTecWOIuc8_sMGfrI(this, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$29$TwoStepVerificationActivity(TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            this.currentPassword = (TL_account_password) tLObject;
            initPasswordNewAlgo(this.currentPassword);
            processDone();
        }
    }

    public /* synthetic */ void lambda$processDone$36$TwoStepVerificationActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$KRiOgrWvOScAk9_kyHUo1nOtRGA(this, tL_error));
    }

    public /* synthetic */ void lambda$null$35$TwoStepVerificationActivity(TL_error tL_error) {
        String str = "AppName";
        if (tL_error == null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$TwoStepVerificationActivity$WcaCems7fna6T9hxKJ46cWEKKY4(this));
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString(str, NUM));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tL_error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
        } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
            String formatPluralString;
            int intValue = Utilities.parseInt(tL_error.text).intValue();
            if (intValue < 60) {
                formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
        } else {
            showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
        }
    }

    public /* synthetic */ void lambda$null$34$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
        finishFragment();
    }

    private void sendEmailConfirm(String str) {
        TL_account_confirmPasswordEmail tL_account_confirmPasswordEmail = new TL_account_confirmPasswordEmail();
        tL_account_confirmPasswordEmail.code = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_confirmPasswordEmail, new -$$Lambda$TwoStepVerificationActivity$zGkZlp2DnS104dUP2z3jhg8EgmM(this), 10);
    }

    public /* synthetic */ void lambda$sendEmailConfirm$39$TwoStepVerificationActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$TwoStepVerificationActivity$WFxjAtXG7ouiOAVtFI0iHMnbBt4(this, tL_error));
    }

    public /* synthetic */ void lambda$null$38$TwoStepVerificationActivity(TL_error tL_error) {
        if (this.type == 0 && this.waitingForEmail) {
            showDoneProgress(false);
        }
        if (tL_error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                Builder builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$TwoStepVerificationActivity$pOfSC0LoruYxxVqu7mmiCuIPGxU(this));
                TL_account_password tL_account_password = this.currentPassword;
                if (tL_account_password == null || !tL_account_password.has_password) {
                    builder.setMessage(LocaleController.getString("YourPasswordSuccessText", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
                }
                builder.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
                Dialog showDialog = showDialog(builder.create());
                if (showDialog != null) {
                    showDialog.setCanceledOnTouchOutside(false);
                    showDialog.setCancelable(false);
                }
            }
        } else if (tL_error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.waitingForEmail ? this.codeFieldCell.getTextView() : this.passwordEditText, true);
        } else {
            String str = "AppName";
            if (tL_error.text.startsWith("FLOOD_WAIT")) {
                String formatPluralString;
                int intValue = Utilities.parseInt(tL_error.text).intValue();
                if (intValue < 60) {
                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                } else {
                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                }
                showAlertWithText(LocaleController.getString(str, NUM), LocaleController.formatString("FloodWaitTime", NUM, formatPluralString));
            } else {
                showAlertWithText(LocaleController.getString(str, NUM), tL_error.text);
            }
        }
    }

    public /* synthetic */ void lambda$null$37$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        if (this.type == 0) {
            loadPasswordInfo(false);
            this.doneItem.setVisibility(8);
            return;
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        r0 = new Object[8];
        TL_account_password tL_account_password = this.currentPassword;
        r0[1] = tL_account_password.new_algo;
        r0[2] = tL_account_password.new_secure_algo;
        r0[3] = tL_account_password.secure_random;
        r0[4] = this.email;
        r0[5] = this.hint;
        r0[6] = null;
        r0[7] = this.firstPassword;
        instance.postNotificationName(i2, r0);
        finishFragment();
    }

    private void onFieldError(TextView textView, boolean z) {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            if (z) {
                textView.setText("");
            }
            AndroidUtilities.shakeView(textView, 2.0f, 0);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[24];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[11] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText3");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[17] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[18] = new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6");
        themeDescriptionArr[19] = new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4");
        themeDescriptionArr[20] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[21] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        themeDescriptionArr[22] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        themeDescriptionArr[23] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        return themeDescriptionArr;
    }
}
