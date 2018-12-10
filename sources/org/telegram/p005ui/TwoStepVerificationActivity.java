package org.telegram.p005ui;

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
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.p003ts.TsExtractor;
import java.math.BigInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Cells.EditTextSettingsCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.TextSettingsCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.ContextProgressView;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.CLASSNAMExb6caa888;
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
import org.telegram.tgnet.TLRPC.TL_passwordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoSHA512;
import org.telegram.tgnet.TLRPC.TL_securePasswordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC.TL_secureSecretSettings;

/* renamed from: org.telegram.ui.TwoStepVerificationActivity */
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

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$2 */
    class CLASSNAME implements Callback {
        CLASSNAME() {
        }

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
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$3 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (TwoStepVerificationActivity.this.emailCodeLength != 0 && s.length() == TwoStepVerificationActivity.this.emailCodeLength) {
                TwoStepVerificationActivity.this.processDone();
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                TwoStepVerificationActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1) {
                TwoStepVerificationActivity.this.processDone();
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$ListAdapter */
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
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position == TwoStepVerificationActivity.this.changePasswordRow) {
                        textCell.setText(LocaleController.getString("ChangePassword", R.string.ChangePassword), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setPasswordRow) {
                        textCell.setText(LocaleController.getString("SetAdditionalPassword", R.string.SetAdditionalPassword), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                        textCell.setText(LocaleController.getString("TurnPasswordOff", R.string.TurnPasswordOff), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                        string = LocaleController.getString("ChangeRecoveryEmail", R.string.ChangeRecoveryEmail);
                        if (TwoStepVerificationActivity.this.abortPasswordRow == -1) {
                            z = false;
                        }
                        textCell.setText(string, z);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.resendCodeRow) {
                        textCell.setText(LocaleController.getString("ResendCode", R.string.ResendCode), true);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        textCell.setText(LocaleController.getString("SetRecoveryEmail", R.string.SetRecoveryEmail), false);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.abortPasswordRow) {
                        textCell.setTag(Theme.key_windowBackgroundWhiteRedText3);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                        if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                            textCell.setText(LocaleController.getString("AbortPassword", R.string.AbortPassword), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("AbortEmail", R.string.AbortEmail), false);
                            return;
                        }
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = holder.itemView;
                    if (position == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.shadowRow) {
                        privacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                        Object[] objArr;
                        if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                            string = "EmailPasswordConfirmText2";
                            objArr = new Object[1];
                            objArr[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null ? TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern : TtmlNode.ANONYMOUS_REGION_ID;
                            privacyCell.setText(LocaleController.formatString(string, R.string.EmailPasswordConfirmText2, objArr));
                        } else {
                            string = "EmailPasswordConfirmText3";
                            objArr = new Object[1];
                            objArr[0] = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null ? TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern : TtmlNode.ANONYMOUS_REGION_ID;
                            privacyCell.setText(LocaleController.formatString(string, R.string.EmailPasswordConfirmText3, objArr));
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                        privacyCell.setText(LocaleController.getString("EnabledPasswordText", R.string.EnabledPasswordText));
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
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

    protected void setRecoveryParams(TL_account_password password) {
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
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
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
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        this.passwordEditText = new EditTextBoldCursor(context);
        this.passwordEditText.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationActivity$$Lambda$0(this));
        this.passwordEditText.setCustomSelectionActionModeCallback(new CLASSNAME());
        this.bottomTextView = new TextView(context);
        this.bottomTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        this.bottomButton = new TextView(context);
        this.bottomButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
        this.bottomButton.setPadding(0, AndroidUtilities.m9dp(10.0f), 0, 0);
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
            Adapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new TwoStepVerificationActivity$$Lambda$2(this));
            this.codeFieldCell = new EditTextSettingsCell(context);
            this.codeFieldCell.setTextAndHint(TtmlNode.ANONYMOUS_REGION_ID, LocaleController.getString("PasswordCode", R.string.PasswordCode), false);
            this.codeFieldCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            EditTextBoldCursor editText = this.codeFieldCell.getTextView();
            editText.setInputType(3);
            editText.setImeOptions(6);
            editText.setOnEditorActionListener(new TwoStepVerificationActivity$$Lambda$3(this));
            editText.addTextChangedListener(new CLASSNAME());
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", R.string.TwoStepVerificationTitle));
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", R.string.PleaseEnterCurrentPassword));
        } else if (this.type == 1) {
            setPasswordSetState(this.passwordSetState);
        }
        if (!this.passwordEntered || this.type == 1) {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        }
        return this.fragmentView;
    }

    final /* synthetic */ boolean lambda$createView$0$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    final /* synthetic */ void lambda$createView$6$TwoStepVerificationActivity(View v) {
        Builder builder;
        if (this.type == 0) {
            if (this.currentPassword.has_recovery) {
                needShowProgress();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$Lambda$35(this), 10);
            } else if (getParentActivity() != null) {
                builder = new Builder(getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", R.string.RestorePasswordResetAccount), new TwoStepVerificationActivity$$Lambda$36(this));
                builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                showDialog(builder.create());
            }
        } else if (this.passwordSetState == 4) {
            showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
        } else {
            builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", R.string.YourEmailSkipWarningText));
            builder.setTitle(LocaleController.getString("YourEmailSkipWarning", R.string.YourEmailSkipWarning));
            builder.setPositiveButton(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip), new TwoStepVerificationActivity$$Lambda$37(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$3$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$38(this, error, response));
    }

    final /* synthetic */ void lambda$null$2$TwoStepVerificationActivity(TL_error error, TLObject response) {
        needHideProgress();
        if (error == null) {
            TL_auth_passwordRecovery res = (TL_auth_passwordRecovery) response;
            Builder builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$39(this, res));
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
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    final /* synthetic */ void lambda$null$1$TwoStepVerificationActivity(TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(this.currentAccount, 1);
        fragment.currentPassword = this.currentPassword;
        fragment.currentPassword.email_unconfirmed_pattern = res.email_pattern;
        fragment.currentSecretId = this.currentSecretId;
        fragment.currentSecret = this.currentSecret;
        fragment.passwordSetState = 4;
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$null$4$TwoStepVerificationActivity(DialogInterface dialog, int which) {
        Browser.openUrl(getParentActivity(), "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    final /* synthetic */ void lambda$null$5$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        this.email = TtmlNode.ANONYMOUS_REGION_ID;
        setNewPassword(false);
    }

    final /* synthetic */ void lambda$createView$9$TwoStepVerificationActivity(View view, int position) {
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
                text = LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion);
                if (this.currentPassword.has_secure_values) {
                    text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", R.string.TurnPasswordOffPassport);
                }
            } else if (this.currentPassword == null || !this.currentPassword.has_password) {
                text = LocaleController.getString("CancelPasswordQuestion", R.string.CancelPasswordQuestion);
            } else {
                text = LocaleController.getString("CancelEmailQuestion", R.string.CancelEmailQuestion);
            }
            builder.setMessage(text);
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$33(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        } else if (position == this.resendCodeRow) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_resendPasswordEmail(), TwoStepVerificationActivity$$Lambda$34.$instance);
            builder = new Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("ResendCodeInfo", R.string.ResendCodeInfo));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$null$7$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        setNewPassword(true);
    }

    static final /* synthetic */ void lambda$null$8$TwoStepVerificationActivity(TLObject response, TL_error error) {
    }

    final /* synthetic */ boolean lambda$createView$10$TwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
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
                    lambda$null$10$ProfileActivity();
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

    final /* synthetic */ void lambda$onResume$11$TwoStepVerificationActivity() {
        if (this.passwordEditText != null) {
            this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    final /* synthetic */ void lambda$onResume$12$TwoStepVerificationActivity() {
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
        if (password.new_algo instanceof CLASSNAMExb6caa888) {
            CLASSNAMExb6caa888 algo = password.new_algo;
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

    final /* synthetic */ void lambda$loadPasswordInfo$14$TwoStepVerificationActivity(boolean silent, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$32(this, error, response, silent));
    }

    final /* synthetic */ void lambda$null$13$TwoStepVerificationActivity(TL_error error, TLObject response, boolean silent) {
        if (error == null) {
            this.loading = false;
            this.currentPassword = (TL_account_password) response;
            if (TwoStepVerificationActivity.canHandleCurrentPassword(this.currentPassword, false)) {
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
                TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
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
                    String pendingHint = this.currentPassword.hint != null ? this.currentPassword.hint : TtmlNode.ANONYMOUS_REGION_ID;
                    if (!(this.waitingForEmail || pendingCurrentAlgo == null)) {
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, null, pendingCurrentAlgo, pendingNewSecureAlgo, pendingSecureRandom, pendingEmail, pendingHint, null, null);
                        lambda$checkDiscard$70$PassportActivity();
                    }
                }
            } else {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
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
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    final /* synthetic */ void lambda$startShortpoll$15$TwoStepVerificationActivity() {
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
                this.actionBar.setTitle(LocaleController.getString("YourPassword", R.string.YourPassword));
                if (this.currentPassword.has_password) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", R.string.PleaseEnterPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", R.string.PleaseEnterFirstPassword));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 1) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", R.string.YourPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", R.string.PleaseReEnterPassword));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", R.string.PasswordHint));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", R.string.PasswordHintText));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", R.string.RecoveryEmail));
                this.titleTextView.setText(LocaleController.getString("YourEmail", R.string.YourEmail));
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
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", R.string.RestoreEmailSentInfo));
                textView = this.bottomButton;
                String str = "RestoreEmailTrouble";
                Object[] objArr = new Object[1];
                objArr[0] = this.currentPassword.email_unconfirmed_pattern != null ? this.currentPassword.email_unconfirmed_pattern : TtmlNode.ANONYMOUS_REGION_ID;
                textView.setText(LocaleController.formatString(str, R.string.RestoreEmailTrouble, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
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
                this.codeFieldCell.setText(TtmlNode.ANONYMOUS_REGION_ID, false);
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
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
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
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            if (TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(TtmlNode.ANONYMOUS_REGION_ID);
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$8(this), 200);
        }
    }

    final /* synthetic */ void lambda$updateRows$16$TwoStepVerificationActivity() {
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
            } catch (Throwable e) {
                FileLog.m13e(e);
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
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
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
                req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
                req.password = new TL_inputCheckPasswordEmpty();
            } else {
                req.new_settings.flags = 3;
                req.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
                req.new_settings.new_password_hash = new byte[0];
                req.new_settings.new_algo = new TL_passwordKdfAlgoUnknown();
                req.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            }
        } else {
            TL_account_passwordInputSettings tL_account_passwordInputSettings;
            if (this.hint == null && this.currentPassword != null) {
                this.hint = this.currentPassword.hint;
            }
            if (this.hint == null) {
                this.hint = TtmlNode.ANONYMOUS_REGION_ID;
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

    final /* synthetic */ void lambda$setNewPassword$18$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$31(this, error));
    }

    final /* synthetic */ void lambda$null$17$TwoStepVerificationActivity(TL_error error) {
        needHideProgress();
        if (error == null) {
            loadPasswordInfo(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            updateRows();
        }
    }

    final /* synthetic */ void lambda$setNewPassword$25$TwoStepVerificationActivity(TL_account_updatePasswordSettings req, boolean clear, String password) {
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
            if (this.currentPassword.new_algo instanceof CLASSNAMExb6caa888) {
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
        if (this.currentPassword.new_algo instanceof CLASSNAMExb6caa888) {
            if (password != null) {
                CLASSNAMExb6caa888 algo = (CLASSNAMExb6caa888) this.currentPassword.new_algo;
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

    final /* synthetic */ void lambda$null$24$TwoStepVerificationActivity(boolean clear, byte[] newPasswordHash, TL_account_updatePasswordSettings req, String password, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$26(this, error, clear, response, newPasswordHash, req, password));
    }

    final /* synthetic */ void lambda$null$23$TwoStepVerificationActivity(TL_error error, boolean clear, TLObject response, byte[] newPasswordHash, TL_account_updatePasswordSettings req, String password) {
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
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$28(this, newPasswordHash, req));
                    if (password == null && this.currentPassword != null && this.currentPassword.has_password) {
                        builder.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
                    } else {
                        builder.setMessage(LocaleController.getString("YourPasswordSuccessText", R.string.YourPasswordSuccessText));
                    }
                    builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
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
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$29(this, newPasswordHash, req));
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                    dialog = showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if ("EMAIL_INVALID".equals(error.text)) {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
                    return;
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    String timeString;
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                    return;
                } else {
                    showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                    return;
                }
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new TwoStepVerificationActivity$$Lambda$27(this, clear), 8);
    }

    final /* synthetic */ void lambda$null$20$TwoStepVerificationActivity(boolean clear, TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$30(this, error2, response2, clear));
    }

    final /* synthetic */ void lambda$null$19$TwoStepVerificationActivity(TL_error error2, TLObject response2, boolean clear) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            setNewPassword(clear);
        }
    }

    final /* synthetic */ void lambda$null$21$TwoStepVerificationActivity(byte[] newPasswordHash, TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        lambda$checkDiscard$70$PassportActivity();
    }

    final /* synthetic */ void lambda$null$22$TwoStepVerificationActivity(byte[] newPasswordHash, TL_account_updatePasswordSettings req, DialogInterface dialogInterface, int i) {
        if (this.closeAfterSet) {
            TwoStepVerificationActivity activity = new TwoStepVerificationActivity(this.currentAccount, 0);
            activity.setCloseAfterSet(true);
            this.parentLayout.addFragmentToStack(activity, this.parentLayout.fragmentsStack.size() - 1);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, newPasswordHash, req.new_settings.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, this.email, this.firstPassword);
        lambda$checkDiscard$70$PassportActivity();
    }

    private TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof CLASSNAMExb6caa888)) {
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
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (this.passwordSetState == 1) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                    return;
                }
                try {
                    Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", R.string.PasswordDoNotMatch), 0).show();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                onFieldError(this.passwordEditText, true);
            } else if (this.passwordSetState == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", R.string.PasswordAsHintError), 0).show();
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                    }
                    onFieldError(this.passwordEditText, false);
                } else if (this.currentPassword.has_recovery) {
                    this.email = TtmlNode.ANONYMOUS_REGION_ID;
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

    final /* synthetic */ void lambda$processDone$33$TwoStepVerificationActivity(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof CLASSNAMExb6caa888) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationActivity$$Lambda$19(this, oldPasswordBytes, x_bytes);
        TL_error error;
        if (this.currentPassword.current_algo instanceof CLASSNAMExb6caa888) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (CLASSNAMExb6caa888) this.currentPassword.current_algo);
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

    final /* synthetic */ void lambda$null$32$TwoStepVerificationActivity(byte[] oldPasswordBytes, byte[] x_bytes, TLObject response, TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$Lambda$20(this, oldPasswordBytes, response, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$21(this, error));
        }
    }

    final /* synthetic */ void lambda$null$28$TwoStepVerificationActivity(byte[] oldPasswordBytes, TLObject response, byte[] x_bytes) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$24(this, checkSecretValues(oldPasswordBytes, (TL_account_passwordSettings) response), x_bytes));
    }

    final /* synthetic */ void lambda$null$27$TwoStepVerificationActivity(boolean secretOk, byte[] x_bytes) {
        needHideProgress();
        if (secretOk) {
            this.currentPasswordHash = x_bytes;
            this.passwordEntered = true;
            AndroidUtilities.hideKeyboard(this.passwordEditText);
            updateRows();
            return;
        }
        AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
    }

    final /* synthetic */ void lambda$null$31$TwoStepVerificationActivity(TL_error error) {
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
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    final /* synthetic */ void lambda$null$30$TwoStepVerificationActivity(TLObject response2, TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$23(this, error2, response2));
    }

    final /* synthetic */ void lambda$null$29$TwoStepVerificationActivity(TL_error error2, TLObject response2) {
        if (error2 == null) {
            this.currentPassword = (TL_account_password) response2;
            TwoStepVerificationActivity.initPasswordNewAlgo(this.currentPassword);
            processDone();
        }
    }

    final /* synthetic */ void lambda$processDone$36$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$17(this, error));
    }

    final /* synthetic */ void lambda$null$35$TwoStepVerificationActivity(TL_error error) {
        if (error == null) {
            Builder builder = new Builder(getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$18(this));
            builder.setMessage(LocaleController.getString("PasswordReset", R.string.PasswordReset));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
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
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    final /* synthetic */ void lambda$null$34$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
        lambda$checkDiscard$70$PassportActivity();
    }

    private void sendEmailConfirm(String code) {
        TL_account_confirmPasswordEmail req = new TL_account_confirmPasswordEmail();
        req.code = code;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationActivity$$Lambda$14(this), 10);
    }

    final /* synthetic */ void lambda$sendEmailConfirm$39$TwoStepVerificationActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$Lambda$15(this, error));
    }

    final /* synthetic */ void lambda$null$38$TwoStepVerificationActivity(TL_error error) {
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
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new TwoStepVerificationActivity$$Lambda$16(this));
                if (this.currentPassword == null || !this.currentPassword.has_password) {
                    builder.setMessage(LocaleController.getString("YourPasswordSuccessText", R.string.YourPasswordSuccessText));
                } else {
                    builder.setMessage(LocaleController.getString("YourEmailSuccessText", R.string.YourEmailSuccessText));
                }
                builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
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
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
        } else {
            showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
        }
    }

    final /* synthetic */ void lambda$null$37$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        if (this.type == 0) {
            loadPasswordInfo(false);
            this.doneItem.setVisibility(8);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, this.currentPasswordHash, this.currentPassword.new_algo, this.currentPassword.new_secure_algo, this.currentPassword.secure_random, this.email, this.hint, null, this.firstPassword);
        lambda$checkDiscard$70$PassportActivity();
    }

    private void onFieldError(TextView field, boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                field.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            AndroidUtilities.shakeView(field, 2.0f, 0);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[24];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[10] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText3);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[17] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[18] = new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[19] = new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[20] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[21] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[22] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[23] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        return themeDescriptionArr;
    }
}
