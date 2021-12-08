package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    /* access modifiers changed from: private */
    public TLRPC.TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private String email;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private String firstPassword;
    private boolean forgotPasswordOnShow;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    int otherwiseReloginDays = -1;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private boolean paused;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public boolean resetPasswordOnShow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScrollView scrollView;
    /* access modifiers changed from: private */
    public int setPasswordDetailRow;
    /* access modifiers changed from: private */
    public int setPasswordRow;
    /* access modifiers changed from: private */
    public int setRecoveryEmailRow;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int turnPasswordOffRow;
    private Runnable updateTimeRunnable = new TwoStepVerificationActivity$$ExternalSyntheticLambda2(this);

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP);
    }

    public TwoStepVerificationActivity() {
    }

    public TwoStepVerificationActivity(int account) {
        this.currentAccount = account;
    }

    public void setPassword(TLRPC.TL_account_password password) {
        this.currentPassword = password;
        this.passwordEntered = false;
    }

    public void setCurrentPasswordParams(TLRPC.TL_account_password password, byte[] passwordHash, long secretId, byte[] secret) {
        this.currentPassword = password;
        this.currentPasswordHash = passwordHash;
        this.currentSecret = secret;
        this.currentSecretId = secretId;
        this.passwordEntered = (passwordHash != null && passwordHash.length > 0) || !password.has_password;
    }

    public boolean onFragmentCreate() {
        byte[] bArr;
        super.onFragmentCreate();
        TLRPC.TL_account_password tL_account_password = this.currentPassword;
        if (tL_account_password == null || tL_account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, this.currentPassword != null);
        }
        updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.twoStepPasswordChanged);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.twoStepPasswordChanged);
        this.destroyed = true;
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (TwoStepVerificationActivity.this.otherwiseReloginDays >= 0) {
                        TwoStepVerificationActivity.this.showSetForcePasswordAlert();
                    } else {
                        TwoStepVerificationActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        this.titleTextView.setPadding(AndroidUtilities.dp(40.0f), 0, AndroidUtilities.dp(40.0f), 0);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.passwordEditText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(129);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda35(this));
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
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
        TextView textView2 = new TextView(context2);
        this.bottomTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", NUM));
        linearLayout.addView(this.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(80);
        linearLayout2.setClipChildren(false);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.bottomButton = simpleTextView;
        simpleTextView.setTextSize(14);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton, LayoutHelper.createLinear(-1, 40, (LocaleController.isRTL ? 5 : 3) | 80, 40, 0, 40, 14));
        this.bottomButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda33(this));
        TextView textView3 = new TextView(context2);
        this.cancelResetButton = textView3;
        textView3.setTextSize(1, 14.0f);
        this.cancelResetButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.cancelResetButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        this.cancelResetButton.setText(LocaleController.getString("CancelReset", NUM));
        this.cancelResetButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        linearLayout2.addView(this.cancelResetButton, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 80, 40, 0, 40, 26));
        this.cancelResetButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda34(this));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new TwoStepVerificationActivity$$ExternalSyntheticLambda28(this));
        updateRows();
        this.actionBar.setTitle(LocaleController.getString("TwoStepVerificationTitle", NUM));
        if (this.delegate != null) {
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPasswordTransfer", NUM));
        } else {
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", NUM));
        }
        if (this.passwordEntered) {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
        } else {
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ boolean m3983lambda$createView$0$orgtelegramuiTwoStepVerificationActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3984lambda$createView$1$orgtelegramuiTwoStepVerificationActivity(View v) {
        onPasswordForgot();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3985lambda$createView$2$orgtelegramuiTwoStepVerificationActivity(View v) {
        cancelPasswordReset();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3987lambda$createView$4$orgtelegramuiTwoStepVerificationActivity(View view, int position) {
        if (position == this.setPasswordRow || position == this.changePasswordRow) {
            TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            fragment.addFragmentToClose(this);
            fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(fragment);
        } else if (position == this.setRecoveryEmailRow || position == this.changeRecoveryEmailRow) {
            TwoStepVerificationSetupActivity fragment2 = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            fragment2.addFragmentToClose(this);
            fragment2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(fragment2);
        } else if (position == this.turnPasswordOffRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            String text = LocaleController.getString("TurnPasswordOffQuestion", NUM);
            if (this.currentPassword.has_secure_values) {
                text = text + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
            }
            String title = LocaleController.getString("TurnPasswordOffQuestionTitle", NUM);
            String buttonText = LocaleController.getString("Disable", NUM);
            builder.setMessage(text);
            builder.setTitle(title);
            builder.setPositiveButton(buttonText, new TwoStepVerificationActivity$$ExternalSyntheticLambda11(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3986lambda$createView$3$orgtelegramuiTwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        clearPassword();
    }

    private void cancelPasswordReset() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("CancelPasswordResetYes", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda0(this));
            builder.setNegativeButton(LocaleController.getString("CancelPasswordResetNo", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("CancelReset", NUM));
            builder.setMessage(LocaleController.getString("CancelPasswordReset", NUM));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$cancelPasswordReset$7$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3975x5b9d5var_(DialogInterface dialog, int which) {
        getConnectionsManager().sendRequest(new TLRPC.TL_account_declinePasswordReset(), new TwoStepVerificationActivity$$ExternalSyntheticLambda17(this));
    }

    /* renamed from: lambda$cancelPasswordReset$6$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3974xc0fc9d17(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda3(this, response));
    }

    /* renamed from: lambda$cancelPasswordReset$5$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3973x265bda96(TLObject response) {
        if (response instanceof TLRPC.TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    private void resetPassword() {
        needShowProgress(true);
        getConnectionsManager().sendRequest(new TLRPC.TL_account_resetPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda24(this));
    }

    /* renamed from: lambda$resetPassword$10$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4001x369var_be(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda4(this, response));
    }

    /* renamed from: lambda$resetPassword$9$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4003x9b8410ca(TLObject response) {
        String timeString;
        needHideProgress();
        if (response instanceof TLRPC.TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("ResetPassword", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordOk", NUM));
            showDialog(builder.create(), new TwoStepVerificationActivity$$ExternalSyntheticLambda32(this));
        } else if (response instanceof TLRPC.TL_account_resetPasswordRequestedWait) {
            this.currentPassword.pending_reset_date = ((TLRPC.TL_account_resetPasswordRequestedWait) response).until_date;
            updateBottomButton();
        } else if (response instanceof TLRPC.TL_account_resetPasswordFailedWait) {
            int time = ((TLRPC.TL_account_resetPasswordFailedWait) response).retry_date - getConnectionsManager().getCurrentTime();
            if (time > 86400) {
                timeString = LocaleController.formatPluralString("Days", time / 86400);
            } else if (time > 3600) {
                timeString = LocaleController.formatPluralString("Hours", time / 86400);
            } else if (time > 60) {
                timeString = LocaleController.formatPluralString("Minutes", time / 60);
            } else {
                timeString = LocaleController.formatPluralString("Seconds", Math.max(1, time));
            }
            showAlertWithText(LocaleController.getString("ResetPassword", NUM), LocaleController.formatString("ResetPasswordWait", NUM, timeString));
        }
    }

    /* renamed from: lambda$resetPassword$8$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4002xe34e49(DialogInterface dialog) {
        getNotificationCenter().postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void updateBottomButton() {
        SimpleTextView simpleTextView;
        String time;
        if (this.currentPassword == null || (simpleTextView = this.bottomButton) == null || simpleTextView.getVisibility() != 0) {
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            TextView textView = this.cancelResetButton;
            if (textView != null) {
                textView.setVisibility(8);
                return;
            }
            return;
        }
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.bottomButton.getLayoutParams();
        if (this.currentPassword.pending_reset_date == 0 || getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
            if (this.currentPassword.pending_reset_date == 0) {
                this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
                this.cancelResetButton.setVisibility(8);
                layoutParams.bottomMargin = AndroidUtilities.dp(14.0f);
                layoutParams.height = AndroidUtilities.dp(40.0f);
            } else {
                this.bottomButton.setText(LocaleController.getString("ResetPassword", NUM));
                this.cancelResetButton.setVisibility(0);
                layoutParams.bottomMargin = 0;
                layoutParams.height = AndroidUtilities.dp(22.0f);
            }
            this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        } else {
            int t = Math.max(1, this.currentPassword.pending_reset_date - getConnectionsManager().getCurrentTime());
            if (t > 86400) {
                time = LocaleController.formatPluralString("Days", t / 86400);
            } else if (t >= 3600) {
                time = LocaleController.formatPluralString("Hours", t / 3600);
            } else {
                time = String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(t / 60), Integer.valueOf(t % 60)});
            }
            this.bottomButton.setText(LocaleController.formatString("RestorePasswordResetIn", NUM, time));
            this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
            this.cancelResetButton.setVisibility(0);
            layoutParams.bottomMargin = 0;
            layoutParams.height = AndroidUtilities.dp(22.0f);
            AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
            AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 1000);
        }
        this.bottomButton.setLayoutParams(layoutParams);
    }

    private void onPasswordForgot() {
        if (this.currentPassword.pending_reset_date == 0 && this.currentPassword.has_recovery) {
            needShowProgress(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$ExternalSyntheticLambda21(this), 10);
        } else if (getParentActivity() != null) {
            if (this.currentPassword.pending_reset_date == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda29(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText2", NUM));
                showDialog(builder.create());
            } else if (getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda22(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder2.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder2.setMessage(LocaleController.getString("RestorePasswordResetPasswordText", NUM));
                AlertDialog dialog = builder2.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else {
                cancelPasswordReset();
            }
        }
    }

    /* renamed from: lambda$onPasswordForgot$12$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3991xa3var_(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda10(this, error, response));
    }

    /* renamed from: lambda$onPasswordForgot$11$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3990x6f9e66f2(TLRPC.TL_error error, TLObject response) {
        String timeString;
        needHideProgress();
        if (error == null) {
            this.currentPassword.email_unconfirmed_pattern = ((TLRPC.TL_auth_passwordRecovery) response).email_pattern;
            AnonymousClass3 r1 = new TwoStepVerificationSetupActivity(this.currentAccount, 4, this.currentPassword) {
                /* access modifiers changed from: protected */
                public void onReset() {
                    boolean unused = TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            r1.addFragmentToClose(this);
            r1.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(r1);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
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

    /* renamed from: lambda$onPasswordForgot$13$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3992xa4dfebf4(DialogInterface dialog, int which) {
        resetPassword();
    }

    /* renamed from: lambda$onPasswordForgot$14$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3993x3var_ae75(DialogInterface dialog, int which) {
        resetPassword();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.twoStepPasswordChanged) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = args[0];
            }
            loadPasswordInfo(false, false);
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
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setCurrentPasswordInfo(byte[] hash, TLRPC.TL_account_password password) {
        if (hash != null) {
            this.currentPasswordHash = hash;
        }
        this.currentPassword = password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public static boolean canHandleCurrentPassword(TLRPC.TL_account_password password, boolean login) {
        if (login) {
            if (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((password.new_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.current_algo instanceof TLRPC.TL_passwordKdfAlgoUnknown) || (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC.TL_account_password password) {
        if (password.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) password.new_algo;
            byte[] salt = new byte[(algo.salt1.length + 32)];
            Utilities.random.nextBytes(salt);
            System.arraycopy(algo.salt1, 0, salt, 0, algo.salt1.length);
            algo.salt1 = salt;
        }
        if (password.new_secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 algo2 = (TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) password.new_secure_algo;
            byte[] salt2 = new byte[(algo2.salt.length + 32)];
            Utilities.random.nextBytes(salt2);
            System.arraycopy(algo2.salt, 0, salt2, 0, algo2.salt.length);
            algo2.salt = salt2;
        }
    }

    private void loadPasswordInfo(boolean first, boolean silent) {
        if (!silent) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda25(this, silent, first), 10);
    }

    /* renamed from: lambda$loadPasswordInfo$16$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3989xCLASSNAMEbd8a5(boolean silent, boolean first, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda13(this, error, response, silent, first));
    }

    /* renamed from: lambda$loadPasswordInfo$15$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3988x2dbb1624(TLRPC.TL_error error, TLObject response, boolean silent, boolean first) {
        if (error == null) {
            this.loading = false;
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response;
            this.currentPassword = tL_account_password;
            if (!canHandleCurrentPassword(tL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (!silent || first) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        updateRows();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        super.onTransitionAnimationEnd(isOpen, backward);
        if (!isOpen) {
            return;
        }
        if (this.forgotPasswordOnShow) {
            onPasswordForgot();
            this.forgotPasswordOnShow = false;
        } else if (this.resetPasswordOnShow) {
            resetPassword();
            this.resetPasswordOnShow = false;
        }
    }

    private void updateRows() {
        TLRPC.TL_account_password tL_account_password;
        StringBuilder lastValue = new StringBuilder();
        lastValue.append(this.setPasswordRow);
        lastValue.append(this.setPasswordDetailRow);
        lastValue.append(this.changePasswordRow);
        lastValue.append(this.turnPasswordOffRow);
        lastValue.append(this.setRecoveryEmailRow);
        lastValue.append(this.changeRecoveryEmailRow);
        lastValue.append(this.passwordEnabledDetailRow);
        lastValue.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (tL_account_password = this.currentPassword) != null && this.passwordEntered) {
            if (tL_account_password.has_password) {
                int i = this.rowCount;
                int i2 = i + 1;
                this.rowCount = i2;
                this.changePasswordRow = i;
                this.rowCount = i2 + 1;
                this.turnPasswordOffRow = i2;
                if (this.currentPassword.has_recovery) {
                    int i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.changeRecoveryEmailRow = i3;
                } else {
                    int i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.setRecoveryEmailRow = i4;
                }
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.passwordEnabledDetailRow = i5;
            } else {
                int i6 = this.rowCount;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.setPasswordRow = i6;
                this.rowCount = i7 + 1;
                this.setPasswordDetailRow = i7;
            }
        }
        StringBuilder newValue = new StringBuilder();
        newValue.append(this.setPasswordRow);
        newValue.append(this.setPasswordDetailRow);
        newValue.append(this.changePasswordRow);
        newValue.append(this.turnPasswordOffRow);
        newValue.append(this.setRecoveryEmailRow);
        newValue.append(this.changeRecoveryEmailRow);
        newValue.append(this.passwordEnabledDetailRow);
        newValue.append(this.rowCount);
        if (this.listAdapter != null && !lastValue.toString().equals(newValue.toString())) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.fragmentView == null) {
            return;
        }
        if (this.loading || this.passwordEntered) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
                updateBottomButton();
            }
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
            this.fragmentView.setTag("windowBackgroundGray");
            return;
        }
        RecyclerListView recyclerListView2 = this.listView;
        if (recyclerListView2 != null) {
            recyclerListView2.setEmptyView((View) null);
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
            updateBottomButton();
            this.bottomTextView.setVisibility(4);
            if (!TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(this.currentPassword.hint);
            } else {
                this.passwordEditText.setHint("");
            }
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda1(this), 200);
        }
    }

    /* renamed from: lambda$updateRows$17$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4005lambda$updateRows$17$orgtelegramuiTwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean delay) {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCacnel(false);
            if (delay) {
                this.progressDialog.showDelayed(300);
            } else {
                this.progressDialog.show();
            }
        }
    }

    public void needHideProgress() {
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            this.progressDialog = null;
        }
    }

    private void showAlertWithText(String title, String text) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(title);
            builder.setMessage(text);
            showDialog(builder.create());
        }
    }

    private void clearPassword() {
        String str = this.firstPassword;
        TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            req.password = new TLRPC.TL_inputCheckPasswordEmpty();
        }
        req.new_settings = new TLRPC.TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        req.new_settings.flags = 3;
        req.new_settings.hint = "";
        req.new_settings.new_password_hash = new byte[0];
        req.new_settings.new_algo = new TLRPC.TL_passwordKdfAlgoUnknown();
        req.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda5(this, req));
    }

    /* renamed from: lambda$clearPassword$24$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3982x27a5b2a3(TLRPC.TL_account_updatePasswordSettings req) {
        if (req.password == null) {
            if (this.currentPassword.current_algo == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda18(this), 8);
                return;
            }
            req.password = getNewSrpPassword();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new TwoStepVerificationActivity$$ExternalSyntheticLambda20(this), 10);
    }

    /* renamed from: lambda$clearPassword$19$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3977x7351var_(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda7(this, error2, response2));
    }

    /* renamed from: lambda$clearPassword$18$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3976xd8b12var_(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    /* renamed from: lambda$clearPassword$23$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3981x8d04var_(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda9(this, error, response));
    }

    /* renamed from: lambda$clearPassword$22$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3980xvar_da1(TLRPC.TL_error error, TLObject response) {
        String timeString;
        if (error == null || !"SRP_ID_INVALID".equals(error.text)) {
            needHideProgress();
            if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
                finishFragment();
            } else if (error == null) {
            } else {
                if (error.text.startsWith("FLOOD_WAIT")) {
                    int time = Utilities.parseInt(error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, timeString));
                    return;
                }
                showAlertWithText(LocaleController.getString("AppName", NUM), error.text);
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda19(this), 8);
        }
    }

    /* renamed from: lambda$clearPassword$21$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3979x57CLASSNAMEb20(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda8(this, error2, response2));
    }

    /* renamed from: lambda$clearPassword$20$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3978xbd22a89f(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public TLRPC.TL_inputCheckPasswordSRP getNewSrpPassword() {
        if (!(this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
    }

    private boolean checkSecretValues(byte[] passwordBytes, TLRPC.TL_account_passwordSettings passwordSettings) {
        byte[] passwordHash;
        byte[] bArr = passwordBytes;
        TLRPC.TL_account_passwordSettings tL_account_passwordSettings = passwordSettings;
        if (tL_account_passwordSettings.secure_settings != null) {
            this.currentSecret = tL_account_passwordSettings.secure_settings.secure_secret;
            if (tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                passwordHash = Utilities.computePBKDF2(bArr, ((TLRPC.TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tL_account_passwordSettings.secure_settings.secure_algo).salt);
            } else if (!(tL_account_passwordSettings.secure_settings.secure_algo instanceof TLRPC.TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                TLRPC.TL_securePasswordKdfAlgoSHA512 algo = (TLRPC.TL_securePasswordKdfAlgoSHA512) tL_account_passwordSettings.secure_settings.secure_algo;
                passwordHash = Utilities.computeSHA512(algo.salt, bArr, algo.salt);
            }
            this.currentSecretId = tL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] key = new byte[32];
            System.arraycopy(passwordHash, 0, key, 0, 32);
            byte[] iv = new byte[16];
            System.arraycopy(passwordHash, 32, iv, 0, 16);
            byte[] bArr2 = this.currentSecret;
            byte[] bArr3 = iv;
            byte[] bArr4 = key;
            Utilities.aesCbcEncryptionByteArraySafe(bArr2, key, iv, 0, bArr2.length, 0, 0);
            if (PassportActivity.checkSecret(tL_account_passwordSettings.secure_settings.secure_secret, Long.valueOf(tL_account_passwordSettings.secure_settings.secure_secret_id))) {
                return true;
            }
            TLRPC.TL_account_updatePasswordSettings req = new TLRPC.TL_account_updatePasswordSettings();
            req.password = getNewSrpPassword();
            req.new_settings = new TLRPC.TL_account_passwordInputSettings();
            req.new_settings.new_secure_settings = new TLRPC.TL_secureSecretSettings();
            req.new_settings.new_secure_settings.secure_secret = new byte[0];
            req.new_settings.new_secure_settings.secure_algo = new TLRPC.TL_securePasswordKdfAlgoUnknown();
            req.new_settings.new_secure_settings.secure_secret_id = 0;
            req.new_settings.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, TwoStepVerificationActivity$$ExternalSyntheticLambda27.INSTANCE);
            this.currentSecret = null;
            this.currentSecretId = 0;
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0;
        return true;
    }

    static /* synthetic */ void lambda$checkSecretValues$25(TLObject response, TLRPC.TL_error error) {
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (!this.passwordEntered) {
            String oldPassword = this.passwordEditText.getText().toString();
            if (oldPassword.length() == 0) {
                onFieldError(this.passwordEditText, false);
                return;
            }
            byte[] oldPasswordBytes = AndroidUtilities.getStringBytes(oldPassword);
            needShowProgress();
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda15(this, oldPasswordBytes));
        }
    }

    /* renamed from: lambda$processDone$32$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4000xbaa0b8d7(byte[] oldPasswordBytes) {
        byte[] x_bytes;
        TLRPC.TL_account_getPasswordSettings req = new TLRPC.TL_account_getPasswordSettings();
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            x_bytes = SRPHelper.getX(oldPasswordBytes, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
        } else {
            x_bytes = null;
        }
        RequestDelegate requestDelegate = new TwoStepVerificationActivity$$ExternalSyntheticLambda26(this, oldPasswordBytes, x_bytes);
        if (this.currentPassword.current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            req.password = SRPHelper.startCheck(x_bytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.current_algo);
            if (req.password == null) {
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "ALGO_INVALID";
                requestDelegate.run((TLObject) null, error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate, 10);
            return;
        }
        TLRPC.TL_error error2 = new TLRPC.TL_error();
        error2.text = "PASSWORD_HASH_INVALID";
        requestDelegate.run((TLObject) null, error2);
    }

    /* renamed from: lambda$processDone$31$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3999x1fffvar_(byte[] oldPasswordBytes, byte[] x_bytes, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda16(this, oldPasswordBytes, response, x_bytes));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda6(this, error));
        }
    }

    /* renamed from: lambda$processDone$27$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3995x64cf7bd(byte[] oldPasswordBytes, TLObject response, byte[] x_bytes) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda14(this, checkSecretValues(oldPasswordBytes, (TLRPC.TL_account_passwordSettings) response), x_bytes));
    }

    /* renamed from: lambda$processDone$26$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3994x6baCLASSNAMEc(boolean secretOk, byte[] x_bytes) {
        if (this.delegate == null || !secretOk) {
            needHideProgress();
        }
        if (secretOk) {
            this.currentPasswordHash = x_bytes;
            this.passwordEntered = true;
            if (this.delegate != null) {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                this.delegate.didEnterPassword(getNewSrpPassword());
            } else if (!TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                TwoStepVerificationSetupActivity fragment = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
                fragment.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
                presentFragment(fragment, true);
            } else {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                TwoStepVerificationActivity fragment2 = new TwoStepVerificationActivity();
                fragment2.passwordEntered = true;
                fragment2.currentPasswordHash = this.currentPasswordHash;
                fragment2.currentPassword = this.currentPassword;
                fragment2.currentSecret = this.currentSecret;
                fragment2.currentSecretId = this.currentSecretId;
                presentFragment(fragment2, true);
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    /* renamed from: lambda$processDone$30$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3998x855var_d5(TLRPC.TL_error error) {
        String timeString;
        if ("SRP_ID_INVALID".equals(error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda23(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(error.text)) {
            onFieldError(this.passwordEditText, true);
        } else if (error.text.startsWith("FLOOD_WAIT")) {
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

    /* renamed from: lambda$processDone$29$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3997x3b8e7cbf(TLObject response2, TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda12(this, error2, response2));
    }

    /* renamed from: lambda$processDone$28$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m3996xa0edba3e(TLRPC.TL_error error2, TLObject response2) {
        if (error2 == null) {
            TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
            this.currentPassword = tL_account_password;
            initPasswordNewAlgo(tL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
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
                        textCell.setText(LocaleController.getString("ChangeRecoveryEmail", NUM), false);
                        return;
                    } else if (position == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        textCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        privacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
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
            if (position == TwoStepVerificationActivity.this.setPasswordDetailRow || position == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                return 1;
            }
            return 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        themeDescriptions.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return themeDescriptions;
    }

    public boolean onBackPressed() {
        if (this.otherwiseReloginDays < 0) {
            return super.onBackPressed();
        }
        showSetForcePasswordAlert();
        return false;
    }

    /* access modifiers changed from: private */
    public void showSetForcePasswordAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("Warning", NUM));
        builder.setMessage(LocaleController.formatPluralString("ForceSetPasswordAlertMessage", this.otherwiseReloginDays));
        builder.setPositiveButton(LocaleController.getString("ForceSetPasswordContinue", NUM), TwoStepVerificationActivity$$ExternalSyntheticLambda31.INSTANCE);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda30(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    static /* synthetic */ void lambda$showSetForcePasswordAlert$33(DialogInterface a1, int a2) {
    }

    /* renamed from: lambda$showSetForcePasswordAlert$34$org-telegram-ui-TwoStepVerificationActivity  reason: not valid java name */
    public /* synthetic */ void m4004xd73CLASSNAMEf(DialogInterface a1, int a2) {
        finishFragment();
    }

    public void setBlockingAlert(int otherwiseRelogin) {
        this.otherwiseReloginDays = otherwiseRelogin;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0) {
            Bundle args = new Bundle();
            args.putBoolean("afterSignup", true);
            presentFragment(new DialogsActivity(args), true);
            return;
        }
        super.finishFragment();
    }
}
