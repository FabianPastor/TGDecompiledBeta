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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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
import org.telegram.tgnet.TLRPC.TL_boolTrue;
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

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            return (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) ? 0 : TwoStepVerificationActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    view = TwoStepVerificationActivity.this.codeFieldCell;
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).removeView(view);
                        break;
                    }
                    break;
            }
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            boolean z = true;
            String string;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = holder.itemView;
                    textCell.setTag("windowBackgroundWhiteBlackText");
                    textCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                    if (position == TwoStepVerificationActivity.this.changePasswordRow) {
                        textCell.setText(LocaleController.getString("ChangePassword", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setPasswordRow) {
                        textCell.setText(LocaleController.getString("SetAdditionalPassword", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                        textCell.setText(LocaleController.getString("TurnPasswordOff", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                        string = LocaleController.getString("ChangeRecoveryEmail", NUM);
                        if (TwoStepVerificationActivity.this.abortPasswordRow == -1) {
                            z = false;
                        }
                        textCell.setText(string, z);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.resendCodeRow) {
                        textCell.setText(LocaleController.getString("ResendCode", NUM), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        textCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.abortPasswordRow) {
                        textCell.setTag("windowBackgroundWhiteRedText3");
                        textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                        if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                            textCell.setText(LocaleController.getString("AbortPassword", NUM), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("AbortEmail", NUM), false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.shadowRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                        Object[] objArr;
                        if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                            string = "EmailPasswordConfirmText2";
                            objArr = new Object[1];
                            objArr[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null ? TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern : "";
                            privacyCell.setText(LocaleController.formatString(string, NUM, objArr));
                        } else {
                            string = "EmailPasswordConfirmText3";
                            objArr = new Object[1];
                            objArr[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null ? TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern : "";
                            privacyCell.setText(LocaleController.formatString(string, NUM, objArr));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                        privacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == TwoStepVerificationActivity.this.setPasswordDetailRow || position == TwoStepVerificationActivity.this.shadowRow || position == TwoStepVerificationActivity.this.passwordSetupDetailRow || position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            if (position == TwoStepVerificationActivity.this.passwordCodeFieldRow) {
                return 2;
            }
            return 0;
        }
    }

    public TwoStepVerificationActivity(int type) {
        this.emailCodeLength = 6;
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.type = type;
        if (type == 0) {
            loadPasswordInfo(false);
        }
    }

    public TwoStepVerificationActivity(int account, int type) {
        this.emailCodeLength = 6;
        this.passwordEntered = true;
        this.currentPasswordHash = new byte[0];
        this.currentAccount = account;
        this.type = type;
        if (type == 0) {
            loadPasswordInfo(false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void setRecoveryParams(TL_account_password password) {
        this.currentPassword = password;
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
            if (this.shortPollRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
                this.shortPollRunnable = null;
            }
            this.destroyed = true;
        }
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    TwoStepVerificationActivity.this.finishFragment();
                } else if (id == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.progressView = new ContextProgressView(context, 1);
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        this.passwordEditText = new EditTextBoldCursor(context);
        this.passwordEditText.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationActivity$$Lambda$0(this));
        this.passwordEditText.setCustomSelectionActionModeCallback(new Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        this.bottomTextView = new TextView(context);
        this.bottomTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", NUM));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        this.bottomButton = new TextView(context);
        this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", NUM));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 80, 40, 0, 40, 14));
        this.bottomButton.setOnClickListener(new TwoStepVerificationActivity$$Lambda$1(this));
        if (this.type == 0) {
            this.emptyView = new EmptyTextProgressView(context);
            this.emptyView.showProgress();
            frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
            this.listView = new RecyclerListView(context);
            this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
            this.listView.setEmptyView(this.emptyView);
            this.listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = this.listView;
            ListAdapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new TwoStepVerificationActivity$$Lambda$2(this));
            this.codeFieldCell = new EditTextSettingsCell(context);
            this.codeFieldCell.setTextAndHint("", LocaleController.getString("PasswordCode", NUM), false);
            this.codeFieldCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            EditTextBoldCursor editText = this.codeFieldCell.getTextView();
            editText.setInputType(3);
            editText.setImeOptions(6);
            editText.setOnEditorActionListener(new TwoStepVerificationActivity$$Lambda$3(this));
            editText.addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    if (TwoStepVerificationActivity.this.emailCodeLength != 0 && s.length() == TwoStepVerificationActivity.this.emailCodeLength) {
                        TwoStepVerificationActivity.this.processDone();
                    }
                }
            });
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
        } else if (this.type == 1) {
            setPasswordSetState(this.passwordSetState);
        }
        if (!this.passwordEntered || this.type == 1) {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
        }
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$0$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$6$TwoStepVerificationActivity(View v) {
        Builder builder;
        if (this.type == 0) {
            if (this.currentPassword.has_recovery) {
                needShowProgress();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$Lambda$35(this), 10);
            } else if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", NUM), new TwoStepVerificationActivity$$Lambda$36(this));
                builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", NUM));
                showDialog(builder.create());
            }
        } else if (this.passwordSetState == 4) {
            showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", NUM), LocaleController.getString("RestoreEmailTroubleText", NUM));
        } else {
            builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new TwoStepVerificationActivity$$Lambda$37(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$3$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$38(this, error, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$2$TwoStepVerificationActivity(TL_error error, TLObject response) {
        needHideProgress();
        if (error == null) {
            TL_auth_passwordRecovery res = (TL_auth_passwordRecovery) response;
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, res.email_pattern));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$39(this, res));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$1$TwoStepVerificationActivity(TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
        fragment.currentPassword = this.currentPassword;
        fragment.currentPassword.email_unconfirmed_pattern = res.email_pattern;
        fragment.currentSecretId = this.currentSecretId;
        fragment.currentSecret = this.currentSecret;
        fragment.passwordSetState = 4;
        presentFragment(fragment);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$4$TwoStepVerificationActivity(DialogInterface dialog, int which) {
        Browser.openUrl(getParentActivity(), "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$5$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$9$TwoStepVerificationActivity(View view, int position) {
        TwoStepVerificationActivity fragment;
        Builder builder;
        if (position == this.setPasswordRow || position == this.changePasswordRow) {
            fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
            fragment.currentPasswordHash = this.currentPasswordHash;
            fragment.currentPassword = this.currentPassword;
            fragment.currentSecretId = this.currentSecretId;
            fragment.currentSecret = this.currentSecret;
            presentFragment(fragment);
        } else if (position == this.setRecoveryEmailRow || position == this.changeRecoveryEmailRow) {
            fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
            fragment.currentPasswordHash = this.currentPasswordHash;
            fragment.currentPassword = this.currentPassword;
            fragment.currentSecretId = this.currentSecretId;
            fragment.currentSecret = this.currentSecret;
            fragment.emailOnly = true;
            fragment.passwordSetState = 3;
            presentFragment(fragment);
        } else if (position == this.turnPasswordOffRow || position == this.abortPasswordRow) {
            String text;
            builder = new Builder(getParentActivity());
            if (position != this.abortPasswordRow) {
                text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
                if (this.currentPassword.has_secure_values) {
                    text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
                }
            } else if (this.currentPassword == null || !this.currentPassword.has_password) {
                text = LocaleController.getString("CancelPasswordQuestion", NUM);
            } else {
                text = LocaleController.getString("CancelEmailQuestion", NUM);
            }
            builder.setMessage(text);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$33(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        } else if (position == this.resendCodeRow) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resendPasswordEmail(), TwoStepVerificationActivity$$Lambda$34.$instance);
            builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$7$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        setNewPassword(true);
    }

    static final /* synthetic */ void lambda$null$8$TwoStepVerificationActivity(TLObject response, TL_error error) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$10$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = (byte[]) args[0];
                if (this.closeAfterSet && TextUtils.isEmpty(args[4]) && this.closeAfterSet) {
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
        if (this.type == 1) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$4(this), 200);
        } else if (this.type == 0 && this.codeFieldCell != null && this.codeFieldCell.getVisibility() == 0) {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$5(this), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onResume$11$TwoStepVerificationActivity() {
        if (this.passwordEditText != null) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$onResume$12$TwoStepVerificationActivity() {
        if (this.codeFieldCell != null) {
            this.codeFieldCell.getTextView().requestFocus();
            AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
        }
    }

    public void setCloseAfterSet(boolean value) {
        this.closeAfterSet = value;
    }

    public void setCurrentPasswordInfo(byte[] hash, TL_account_password password) {
        this.currentPasswordHash = hash;
        this.currentPassword = password;
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (!isOpen) {
            return;
        }
        if (this.type == 1) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        } else if (this.type == 0 && this.codeFieldCell != null && this.codeFieldCell.getVisibility() == 0) {
            AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
        }
    }

    public static boolean canHandleCurrentPassword(TL_account_password password, boolean login) {
        if (login) {
            if (password.current_algo instanceof TL_passwordKdfAlgoUnknown) {
                return false;
            }
        } else if ((password.new_algo instanceof TL_passwordKdfAlgoUnknown) || (password.current_algo instanceof TL_passwordKdfAlgoUnknown) || (password.new_secure_algo instanceof TL_securePasswordKdfAlgoUnknown)) {
            return false;
        }
        return true;
    }

    public static void initPasswordNewAlgo(TL_account_password password) {
        byte[] salt;
        if (password.new_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = password.new_algo;
            salt = new byte[(algo.salt1.length + 32)];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo.salt1, 0, salt, 0, algo.salt1.length);
            algo.salt1 = salt;
        }
        if (password.new_secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = password.new_secure_algo;
            salt = new byte[(algo2.salt.length + 32)];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo2.salt, 0, salt, 0, algo2.salt.length);
            algo2.salt = salt;
        }
    }

    private void loadPasswordInfo(boolean silent) {
        if (!silent) {
            this.loading = true;
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new TwoStepVerificationActivity$$Lambda$6(this, silent), 10);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadPasswordInfo$14$TwoStepVerificationActivity(boolean silent, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$32(this, error, response, silent));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$13$TwoStepVerificationActivity(TL_error error, TLObject response, boolean silent) {
        if (error == null) {
            this.loading = false;
            this.currentPassword = (TL_account_password) response;
            if (canHandleCurrentPassword(this.currentPassword, false)) {
                boolean z;
                if (!silent) {
                    z = (this.currentPasswordHash != null && this.currentPasswordHash.length > 0) || !this.currentPassword.has_password;
                    this.passwordEntered = z;
                }
                if (TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                    z = false;
                } else {
                    z = true;
                }
                this.waitingForEmail = z;
                initPasswordNewAlgo(this.currentPassword);
                if (!this.paused && this.closeAfterSet && this.currentPassword.has_password) {
                    String pendingEmail;
                    PasswordKdfAlgo pendingCurrentAlgo = this.currentPassword.current_algo;
                    SecurePasswordKdfAlgo pendingNewSecureAlgo = this.currentPassword.new_secure_algo;
                    byte[] pendingSecureRandom = this.currentPassword.secure_random;
                    if (this.currentPassword.has_recovery) {
                        pendingEmail = "1";
                    } else {
                        pendingEmail = null;
                    }
                    String pendingHint = this.currentPassword.hint != null ? this.currentPassword.hint : "";
                    if (!(this.waitingForEmail || pendingCurrentAlgo == null)) {
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, null, pendingCurrentAlgo, pendingNewSecureAlgo, pendingSecureRandom, pendingEmail, pendingHint, null, null);
                        finishFragment();
                    }
                }
            } else {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
        }
        if (!(this.type != 0 || this.destroyed || this.shortPollRunnable != null || this.currentPassword == null || TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern))) {
            startShortpoll();
        }
        updateRows();
    }

    private void startShortpoll() {
        if (this.shortPollRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        }
        this.shortPollRunnable = new TwoStepVerificationActivity$$Lambda$7(this);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 5000);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$startShortpoll$15$TwoStepVerificationActivity() {
        if (this.shortPollRunnable != null) {
            loadPasswordInfo(true);
            this.shortPollRunnable = null;
        }
    }

    private void setPasswordSetState(int state) {
        int i = 4;
        if (this.passwordEditText != null) {
            this.passwordSetState = state;
            TextView textView;
            if (this.passwordSetState == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", NUM));
                if (this.currentPassword.has_password) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", NUM));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", NUM));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 1) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", NUM));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", NUM));
                this.titleTextView.setText(LocaleController.getString("YourEmail", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                textView = this.bottomButton;
                if (!this.emailOnly) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.passwordSetState == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", NUM));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", NUM));
                textView = this.bottomButton;
                String str = "RestoreEmailTrouble";
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : "";
                textView.setText(LocaleController.formatString(str, NUM, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText("");
        }
    }

    private void updateRows() {
        StringBuilder lastValue = new StringBuilder();
        lastValue.append(this.setPasswordRow);
        lastValue.append(this.setPasswordDetailRow);
        lastValue.append(this.changePasswordRow);
        lastValue.append(this.turnPasswordOffRow);
        lastValue.append(this.setRecoveryEmailRow);
        lastValue.append(this.changeRecoveryEmailRow);
        lastValue.append(this.resendCodeRow);
        lastValue.append(this.abortPasswordRow);
        lastValue.append(this.passwordSetupDetailRow);
        lastValue.append(this.passwordCodeFieldRow);
        lastValue.append(this.passwordEnabledDetailRow);
        lastValue.append(this.shadowRow);
        lastValue.append(this.rowCount);
        boolean wasCodeField;
        if (this.passwordCodeFieldRow != -1) {
            wasCodeField = true;
        } else {
            wasCodeField = false;
        }
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
        if (!(this.loading || this.currentPassword == null)) {
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
            } else if (this.currentPassword.has_password) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.changePasswordRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.turnPasswordOffRow = i;
                if (this.currentPassword.has_recovery) {
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
        StringBuilder newValue = new StringBuilder();
        newValue.append(this.setPasswordRow);
        newValue.append(this.setPasswordDetailRow);
        newValue.append(this.changePasswordRow);
        newValue.append(this.turnPasswordOffRow);
        newValue.append(this.setRecoveryEmailRow);
        newValue.append(this.changeRecoveryEmailRow);
        newValue.append(this.resendCodeRow);
        newValue.append(this.abortPasswordRow);
        newValue.append(this.passwordSetupDetailRow);
        newValue.append(this.passwordCodeFieldRow);
        newValue.append(this.passwordEnabledDetailRow);
        newValue.append(this.shadowRow);
        newValue.append(this.rowCount);
        if (!(this.listAdapter == null || lastValue.toString().equals(newValue.toString()))) {
            this.listAdapter.notifyDataSetChanged();
            if (this.passwordCodeFieldRow == -1 && getParentActivity() != null && wasCodeField) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                this.codeFieldCell.setText("", false);
            }
        }
        if (this.fragmentView == null) {
            return;
        }
        if (this.loading || this.passwordEntered) {
            if (this.listView != null) {
                this.listView.setVisibility(0);
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
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
            return;
        }
        if (this.listView != null) {
            this.listView.setEmptyView(null);
            this.listView.setVisibility(4);
            this.scrollView.setVisibility(0);
            this.emptyView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
            if (TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint("");
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$8(this), 200);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateRows$16$TwoStepVerificationActivity() {
        if (!isFinishing() && !this.destroyed && this.passwordEditText != null) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void showDoneProgress(final boolean show) {
        if (this.doneItemAnimation != null) {
            this.doneItemAnimation.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        AnimatorSet animatorSet;
        Animator[] animatorArr;
        if (show) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        } else {
            this.doneItem.getImageView().setVisibility(0);
            this.doneItem.setEnabled(true);
            animatorSet = this.doneItemAnimation;
            animatorArr = new Animator[6];
            animatorArr[0] = ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f});
            animatorArr[1] = ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f});
            animatorArr[2] = ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f});
            animatorArr[3] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[]{1.0f});
            animatorArr[4] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[]{1.0f});
            animatorArr[5] = ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[]{1.0f});
            animatorSet.playTogether(animatorArr);
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animation)) {
                    if (show) {
                        TwoStepVerificationActivity.this.doneItem.getImageView().setVisibility(4);
                    } else {
                        TwoStepVerificationActivity.this.progressView.setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animation) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animation)) {
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

    private void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
        }
    }

    private boolean isValidEmail(String text) {
        if (text == null || text.length() < 3) {
            return false;
        }
        int dot = text.lastIndexOf(46);
        int dog = text.lastIndexOf(64);
        if (dot < 0 || dog < 0 || dot < dog) {
            return false;
        }
        return true;
    }

    private void showAlertWithText(String title, String text) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
        builder.setTitle(title);
        builder.setMessage(text);
        showDialog(builder.create());
    }

    private void setNewPassword(boolean clear) {
        if (clear && this.waitingForEmail && this.currentPassword.has_password) {
            needShowProgress();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_cancelPasswordEmail(), new TwoStepVerificationActivity$$Lambda$9(this));
            return;
        }
        String password = this.firstPassword;
        TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
        if (this.currentPasswordHash == null || this.currentPasswordHash.length == 0) {
            req.password = new TL_inputCheckPasswordEmpty();
        }
        req.new_settings = new TL_account_passwordInputSettings();
        if (clear) {
            UserConfig.getInstance(this.currentAccount).resetSavedPassword();
            this.currentSecret = null;
            if (this.waitingForEmail) {
                req.new_settings.flags = 2;
                req.new_settings.email = "";
                req.password = new TL_inputCheckPasswordEmpty();
            } else {
                req.new_settings.flags = 3;
                req.new_settings.hint = "";
                req.new_settings.new_password_hash = new byte[0];
                req.new_settings.new_algo = new TL_passwordKdfAlgoUnknown();
                req.new_settings.email = "";
            }
        } else {
            TL_account_passwordInputSettings tL_account_passwordInputSettings;
            if (this.hint == null && this.currentPassword != null) {
                this.hint = this.currentPassword.hint;
            }
            if (this.hint == null) {
                this.hint = "";
            }
            if (password != null) {
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 1;
                req.new_settings.hint = this.hint;
                req.new_settings.new_algo = this.currentPassword.new_algo;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 2;
                req.new_settings.email = this.email.trim();
            }
        }
        needShowProgress();
        Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$Lambda$10(this, req, clear, password));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setNewPassword$18$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$31(this, error));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$17$TwoStepVerificationActivity(TL_error error) {
        needHideProgress();
        if (error == null) {
            loadPasswordInfo(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            updateRows();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setNewPassword$25$TwoStepVerificationActivity(TL_account_updatePasswordSettings req, boolean clear, String password) {
        byte[] newPasswordBytes;
        byte[] newPasswordHash;
        if (req.password == null) {
            req.password = getNewSrpPassword();
        }
        if (clear || password == null) {
            newPasswordBytes = null;
            newPasswordHash = null;
        } else {
            newPasswordBytes = AndroidUtilities.getStringBytes(password);
            if (this.currentPassword.new_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                newPasswordHash = SRPHelper.getX(newPasswordBytes, this.currentPassword.new_algo);
            } else {
                newPasswordHash = null;
            }
        }
        RequestDelegate requestDelegate = new TwoStepVerificationActivity$$Lambda$25(this, clear, newPasswordHash, req, password);
        if (clear) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        if (password != null && this.currentSecret != null && this.currentSecret.length == 32 && (this.currentPassword.new_secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000)) {
            TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 newAlgo = this.currentPassword.new_secure_algo;
            Object passwordHash = Utilities.computePBKDF2(newPasswordBytes, newAlgo.salt);
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            byte[] encryptedSecret = new byte[32];
            System.arraycopy(this.currentSecret, 0, encryptedSecret, 0, 32);
            Utilities.aesCbcEncryptionByteArraySafe(encryptedSecret, key, iv, 0, encryptedSecret.length, 0, 1);
            req.new_settings.new_secure_settings = new TL_secureSecretSettings();
            req.new_settings.new_secure_settings.secure_algo = newAlgo;
            req.new_settings.new_secure_settings.secure_secret = encryptedSecret;
            req.new_settings.new_secure_settings.secure_secret_id = this.currentSecretId;
            TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
            tL_account_passwordInputSettings.flags |= 4;
        }
        TL_error error;
        if (this.currentPassword.new_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            if (password != null) {
                TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo;
                req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
                if (req.new_settings.new_password_hash == null) {
                    error = new TL_error();
                    error.text = "ALGO_INVALID";
                    requestDelegate.run(null, error);
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        error = new TL_error();
        error.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$24$TwoStepVerificationActivity(boolean clear, byte[] newPasswordHash, TL_account_updatePasswordSettings req, String password, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$26(this, error, clear, response, newPasswordHash, req, password));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$23$TwoStepVerificationActivity(TL_error error, boolean clear, TLObject response, byte[] newPasswordHash, TL_account_updatePasswordSettings req, String password) {
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            needHideProgress();
            Builder builder;
            Dialog dialog;
            if (error == null && (response instanceof TL_boolTrue)) {
                if (clear) {
                    this.currentPassword = null;
                    this.currentPasswordHash = new byte[0];
                    loadPasswordInfo(false);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                    updateRows();
                    return;
                } else if (getParentActivity() != null) {
                    builder = new Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$28(this, newPasswordHash, req));
                    if (password == null && this.currentPassword != null && this.currentPassword.has_password) {
                        builder.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
                    } else {
                        builder.setMessage(LocaleController.getString("YourPasswordSuccessText", NUM));
                    }
                    builder.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
                    dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else {
                    return;
                }
            } else if (error == null) {
                return;
            } else {
                if ("EMAIL_UNCONFIRMED".equals(error.text) || error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                    builder = new Builder(getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$29(this, newPasswordHash, req));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if ("EMAIL_INVALID".equals(error.text)) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                    return;
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
                    return;
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new TwoStepVerificationActivity$$Lambda$27(this, clear), 8);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$20$TwoStepVerificationActivity(boolean clear, TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$30(this, error2, response2, clear));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$19$TwoStepVerificationActivity(TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            initPasswordNewAlgo(this.currentPassword);
            setNewPassword(clear);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$21$TwoStepVerificationActivity(byte[] newPasswordHash, TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        finishFragment();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$22$TwoStepVerificationActivity(byte[] newPasswordHash, TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        if (this.closeAfterSet) {
            TwoStepVerificationActivity activity = new TwoStepVerificationActivity(this.currentAccount, 0);
            activity.setCloseAfterSet(true);
            this.parentLayout.addFragmentToStack(activity, this.parentLayout.fragmentsStack.size() - 1);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, this.email, this.firstPassword);
        finishFragment();
    }

    private TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, this.currentPassword.current_algo);
    }

    private boolean checkSecretValues(byte[] passwordBytes, TL_account_passwordSettings passwordSettings) {
        if (passwordSettings.secure_settings != null) {
            byte[] passwordHash;
            this.currentSecret = passwordSettings.secure_settings.secure_secret;
            if (passwordSettings.secure_settings.secure_algo instanceof TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                passwordHash = Utilities.computePBKDF2(passwordBytes, passwordSettings.secure_settings.secure_algo.salt);
            } else if (!(passwordSettings.secure_settings.secure_algo instanceof TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                TL_securePasswordKdfAlgoSHA512 algo = passwordSettings.secure_settings.secure_algo;
                passwordHash = Utilities.computeSHA512(algo.salt, passwordBytes, algo.salt);
            }
            this.currentSecretId = passwordSettings.secure_settings.secure_secret_id;
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            Utilities.aesCbcEncryptionByteArraySafe(this.currentSecret, key, iv, 0, this.currentSecret.length, 0, 0);
            if (!PassportActivity.checkSecret(passwordSettings.secure_settings.secure_secret, Long.valueOf(passwordSettings.secure_settings.secure_secret_id))) {
                TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
                req.password = getNewSrpPassword();
                req.new_settings = new TL_account_passwordInputSettings();
                req.new_settings.new_secure_settings = new TL_secureSecretSettings();
                req.new_settings.new_secure_settings.secure_secret = new byte[0];
                req.new_settings.new_secure_settings.secure_algo = new TL_securePasswordKdfAlgoUnknown();
                req.new_settings.new_secure_settings.secure_secret_id = 0;
                TL_account_passwordInputSettings tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 4;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TwoStepVerificationActivity$$Lambda$11.$instance);
                this.currentSecret = null;
                this.currentSecretId = 0;
            }
        } else {
            this.currentSecret = null;
            this.currentSecretId = 0;
        }
        return true;
    }

    static final /* synthetic */ void lambda$checkSecretValues$26$TwoStepVerificationActivity(TLObject response, TL_error error) {
    }

    private static byte[] getBigIntegerBytes(BigInteger value) {
        byte[] bytes = value.toByteArray();
        if (bytes.length <= 256) {
            return bytes;
        }
        byte[] correctedAuth = new byte[256];
        System.arraycopy(bytes, 1, correctedAuth, 0, 256);
        return correctedAuth;
    }

    private void processDone() {
        if (this.type == 0) {
            if (!this.passwordEntered) {
                String oldPassword = this.passwordEditText.getText().toString();
                if (oldPassword.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$Lambda$12(this, oldPasswordBytes));
            } else if (this.waitingForEmail && this.currentPassword != null) {
                if (this.codeFieldCell.length() == 0) {
                    onFieldError(this.codeFieldCell.getTextView(), false);
                    return;
                }
                sendEmailConfirm(this.codeFieldCell.getText());
                showDoneProgress(true);
            }
        } else if (this.type != 1) {
        } else {
            if (this.passwordSetState == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", NUM));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (this.passwordSetState == 1) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                    return;
                }
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                onFieldError(this.passwordEditText, true);
            } else if (this.passwordSetState == 2) {
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
            } else if (this.passwordSetState == 3) {
                this.email = this.passwordEditText.getText().toString();
                if (isValidEmail(this.email)) {
                    setNewPassword(false);
                } else {
                    onFieldError(this.passwordEditText, false);
                }
            } else if (this.passwordSetState == 4) {
                String code = this.passwordEditText.getText().toString();
                if (code.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                TL_auth_recoverPassword req = new TL_auth_recoverPassword();
                req.code = code;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationActivity$$Lambda$13(this), 10);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDone$33$TwoStepVerificationActivity(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationActivity$$Lambda$19(this, oldPasswordBytes, x_bytes);
        TL_error error;
        if (this.currentPassword.current_algo instanceof TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
            if (req.password == null) {
                error = new TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run(null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        error = new TL_error();
        error.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run(null, error);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$32$TwoStepVerificationActivity(byte[] oldPasswordBytes, byte[] x_bytes, TLObject response, TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$Lambda$20(this, oldPasswordBytes, response, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$21(this, error));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$28$TwoStepVerificationActivity(byte[] oldPasswordBytes, TLObject response, byte[] x_bytes) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$24(this, checkSecretValues(oldPasswordBytes, (TL_account_passwordSettings) response), x_bytes));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$27$TwoStepVerificationActivity(boolean secretOk, byte[] x_bytes) {
        needHideProgress();
        if (secretOk) {
            this.currentPasswordHash = x_bytes;
            this.passwordEntered = true;
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            updateRows();
            return;
        }
        AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$31$TwoStepVerificationActivity(TL_error error) {
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new TwoStepVerificationActivity$$Lambda$22(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            onFieldError(this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$30$TwoStepVerificationActivity(TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$23(this, error2, response2));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$29$TwoStepVerificationActivity(TL_error error2, TLObject response2) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            initPasswordNewAlgo(this.currentPassword);
            processDone();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDone$36$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$17(this, error));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$35$TwoStepVerificationActivity(TL_error error) {
        if (error == null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$18(this));
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            Dialog dialog = showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$34$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
        finishFragment();
    }

    private void sendEmailConfirm(String code) {
        TL_account_confirmPasswordEmail req = new TL_account_confirmPasswordEmail();
        req.code = code;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationActivity$$Lambda$14(this), 10);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendEmailConfirm$39$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$15(this, error));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$38$TwoStepVerificationActivity(TL_error error) {
        if (this.type == 0 && this.waitingForEmail) {
            showDoneProgress(false);
        }
        if (error == null) {
            if (getParentActivity() != null) {
                if (this.shortPollRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
                    this.shortPollRunnable = null;
                }
                Builder builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new TwoStepVerificationActivity$$Lambda$16(this));
                if (this.currentPassword == null || !this.currentPassword.has_password) {
                    builder.setMessage(LocaleController.getString("YourPasswordSuccessText", NUM));
                } else {
                    builder.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
                }
                builder.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
                Dialog dialog = showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            }
        } else if (error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.waitingForEmail ? this.codeFieldCell.getTextView() : this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
            String timeString;
            int time = Utilities.parseInt(error.text).intValue();
            if (time < 60) {
                timeString = LocaleController.formatPluralString("Seconds", time);
            } else {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$37$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        if (this.type == 0) {
            loadPasswordInfo(false);
            this.doneItem.setVisibility(8);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        finishFragment();
    }

    private void onFieldError(TextView field, boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                field.setText("");
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[24];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
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
