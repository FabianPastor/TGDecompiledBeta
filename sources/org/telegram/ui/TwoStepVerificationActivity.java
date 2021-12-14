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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_declinePasswordReset;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_passwordSettings;
import org.telegram.tgnet.TLRPC$TL_account_resetPassword;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordFailedWait;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordOk;
import org.telegram.tgnet.TLRPC$TL_account_resetPasswordRequestedWait;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC$TL_boolTrue;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordEmpty;
import org.telegram.tgnet.TLRPC$TL_inputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow;
import org.telegram.tgnet.TLRPC$TL_passwordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoSHA512;
import org.telegram.tgnet.TLRPC$TL_securePasswordKdfAlgoUnknown;
import org.telegram.tgnet.TLRPC$TL_secureSecretSettings;
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
    private SimpleTextView bottomButton;
    private TextView bottomTextView;
    private TextView cancelResetButton;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    /* access modifiers changed from: private */
    public TLRPC$TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private EmptyTextProgressView emptyView;
    private boolean forgotPasswordOnShow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    int otherwiseReloginDays = -1;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
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
    private Runnable updateTimeRunnable = new TwoStepVerificationActivity$$ExternalSyntheticLambda11(this);

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP);
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$checkSecretValues$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ void lambda$showSetForcePasswordAlert$33(DialogInterface dialogInterface, int i) {
    }

    public void setPassword(TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentPassword = tLRPC$TL_account_password;
        this.passwordEntered = false;
    }

    public void setCurrentPasswordParams(TLRPC$TL_account_password tLRPC$TL_account_password, byte[] bArr, long j, byte[] bArr2) {
        this.currentPassword = tLRPC$TL_account_password;
        this.currentPasswordHash = bArr;
        this.currentSecret = bArr2;
        this.currentSecretId = j;
        this.passwordEntered = (bArr != null && bArr.length > 0) || !tLRPC$TL_account_password.has_password;
    }

    public boolean onFragmentCreate() {
        byte[] bArr;
        super.onFragmentCreate();
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (tLRPC$TL_account_password == null || tLRPC$TL_account_password.current_algo == null || (bArr = this.currentPasswordHash) == null || bArr.length <= 0) {
            loadPasswordInfo(true, tLRPC$TL_account_password != null);
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
            public void onItemClick(int i) {
                if (i == -1) {
                    TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                    if (twoStepVerificationActivity.otherwiseReloginDays >= 0) {
                        twoStepVerificationActivity.showSetForcePasswordAlert();
                    } else {
                        twoStepVerificationActivity.finishFragment();
                    }
                } else if (i == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        frameLayout2.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
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
        this.passwordEditText.setOnEditorActionListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda9(this));
        this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback(this) {
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
        TextView textView2 = new TextView(context2);
        this.bottomTextView = textView2;
        textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.bottomTextView.setTextSize(1, 14.0f);
        int i = 5;
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
        this.bottomButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda8(this));
        TextView textView3 = new TextView(context2);
        this.cancelResetButton = textView3;
        textView3.setTextSize(1, 14.0f);
        this.cancelResetButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.cancelResetButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        this.cancelResetButton.setText(LocaleController.getString("CancelReset", NUM));
        this.cancelResetButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        TextView textView4 = this.cancelResetButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout2.addView(textView4, LayoutHelper.createLinear(-1, -2, i | 80, 40, 0, 40, 26));
        this.cancelResetButton.setOnClickListener(new TwoStepVerificationActivity$$ExternalSyntheticLambda7(this));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        recyclerListView2.setAdapter(listAdapter2);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new TwoStepVerificationActivity$$ExternalSyntheticLambda35(this));
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

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$0(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 && i != 6) {
            return false;
        }
        processDone();
        return true;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        onPasswordForgot();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        cancelPasswordReset();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(View view, int i) {
        if (i == this.setPasswordRow || i == this.changePasswordRow) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 0, this.currentPassword);
            twoStepVerificationSetupActivity.addFragmentToClose(this);
            twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(twoStepVerificationSetupActivity);
        } else if (i == this.setRecoveryEmailRow || i == this.changeRecoveryEmailRow) {
            TwoStepVerificationSetupActivity twoStepVerificationSetupActivity2 = new TwoStepVerificationSetupActivity(this.currentAccount, 3, this.currentPassword);
            twoStepVerificationSetupActivity2.addFragmentToClose(this);
            twoStepVerificationSetupActivity2.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
            presentFragment(twoStepVerificationSetupActivity2);
        } else if (i == this.turnPasswordOffRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
            if (this.currentPassword.has_secure_values) {
                string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
            }
            String string2 = LocaleController.getString("TurnPasswordOffQuestionTitle", NUM);
            String string3 = LocaleController.getString("Disable", NUM);
            builder.setMessage(string);
            builder.setTitle(string2);
            builder.setPositiveButton(string3, new TwoStepVerificationActivity$$ExternalSyntheticLambda3(this));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(DialogInterface dialogInterface, int i) {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$7(DialogInterface dialogInterface, int i) {
        getConnectionsManager().sendRequest(new TLRPC$TL_account_declinePasswordReset(), new TwoStepVerificationActivity$$ExternalSyntheticLambda26(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$6(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda13(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelPasswordReset$5(TLObject tLObject) {
        if (tLObject instanceof TLRPC$TL_boolTrue) {
            this.currentPassword.pending_reset_date = 0;
            updateBottomButton();
        }
    }

    public void setForgotPasswordOnShow() {
        this.forgotPasswordOnShow = true;
    }

    private void resetPassword() {
        needShowProgress(true);
        getConnectionsManager().sendRequest(new TLRPC$TL_account_resetPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda31(this));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$10(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda12(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$9(TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLObject instanceof TLRPC$TL_account_resetPasswordOk) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setNegativeButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(LocaleController.getString("ResetPassword", NUM));
            builder.setMessage(LocaleController.getString("RestorePasswordResetPasswordOk", NUM));
            showDialog(builder.create(), new TwoStepVerificationActivity$$ExternalSyntheticLambda6(this));
        } else if (tLObject instanceof TLRPC$TL_account_resetPasswordRequestedWait) {
            this.currentPassword.pending_reset_date = ((TLRPC$TL_account_resetPasswordRequestedWait) tLObject).until_date;
            updateBottomButton();
        } else if (tLObject instanceof TLRPC$TL_account_resetPasswordFailedWait) {
            int currentTime = ((TLRPC$TL_account_resetPasswordFailedWait) tLObject).retry_date - getConnectionsManager().getCurrentTime();
            if (currentTime > 86400) {
                str = LocaleController.formatPluralString("Days", currentTime / 86400);
            } else if (currentTime > 3600) {
                str = LocaleController.formatPluralString("Hours", currentTime / 86400);
            } else if (currentTime > 60) {
                str = LocaleController.formatPluralString("Minutes", currentTime / 60);
            } else {
                str = LocaleController.formatPluralString("Seconds", Math.max(1, currentTime));
            }
            showAlertWithText(LocaleController.getString("ResetPassword", NUM), LocaleController.formatString("ResetPasswordWait", NUM, str));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$resetPassword$8(DialogInterface dialogInterface) {
        getNotificationCenter().postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
        finishFragment();
    }

    /* access modifiers changed from: private */
    public void updateBottomButton() {
        SimpleTextView simpleTextView;
        int i;
        String str;
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
        if (this.currentPassword.pending_reset_date == 0 || getConnectionsManager().getCurrentTime() > (i = this.currentPassword.pending_reset_date)) {
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
            int max = Math.max(1, i - getConnectionsManager().getCurrentTime());
            if (max > 86400) {
                str = LocaleController.formatPluralString("Days", max / 86400);
            } else if (max >= 3600) {
                str = LocaleController.formatPluralString("Hours", max / 3600);
            } else {
                str = String.format(Locale.US, "%02d:%02d", new Object[]{Integer.valueOf(max / 60), Integer.valueOf(max % 60)});
            }
            this.bottomButton.setText(LocaleController.formatString("RestorePasswordResetIn", NUM, str));
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
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        if (tLRPC$TL_account_password.pending_reset_date == 0 && tLRPC$TL_account_password.has_recovery) {
            needShowProgress(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new TwoStepVerificationActivity$$ExternalSyntheticLambda29(this), 10);
        } else if (getParentActivity() != null) {
            if (this.currentPassword.pending_reset_date == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda2(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText2", NUM));
                showDialog(builder.create());
            } else if (getConnectionsManager().getCurrentTime() > this.currentPassword.pending_reset_date) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("Reset", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda1(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder2.setTitle(LocaleController.getString("ResetPassword", NUM));
                builder2.setMessage(LocaleController.getString("RestorePasswordResetPasswordText", NUM));
                AlertDialog create = builder2.create();
                showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            } else {
                cancelPasswordReset();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$12(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda18(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$11(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
            tLRPC$TL_account_password.email_unconfirmed_pattern = ((TLRPC$TL_auth_passwordRecovery) tLObject).email_pattern;
            AnonymousClass3 r8 = new TwoStepVerificationSetupActivity(this.currentAccount, 4, tLRPC$TL_account_password) {
                /* access modifiers changed from: protected */
                public void onReset() {
                    boolean unused = TwoStepVerificationActivity.this.resetPasswordOnShow = true;
                }
            };
            r8.addFragmentToClose(this);
            r8.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, false);
            presentFragment(r8);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$13(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onPasswordForgot$14(DialogInterface dialogInterface, int i) {
        resetPassword();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.twoStepPasswordChanged) {
            if (!(objArr == null || objArr.length <= 0 || objArr[0] == null)) {
                this.currentPasswordHash = objArr[0];
            }
            loadPasswordInfo(false, false);
            updateRows();
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void setCurrentPasswordInfo(byte[] bArr, TLRPC$TL_account_password tLRPC$TL_account_password) {
        if (bArr != null) {
            this.currentPasswordHash = bArr;
        }
        this.currentPassword = tLRPC$TL_account_password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public static boolean canHandleCurrentPassword(TLRPC$TL_account_password tLRPC$TL_account_password, boolean z) {
        if (z) {
            if (tLRPC$TL_account_password.current_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) {
                return false;
            }
            return true;
        } else if ((tLRPC$TL_account_password.new_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) || (tLRPC$TL_account_password.current_algo instanceof TLRPC$TL_passwordKdfAlgoUnknown) || (tLRPC$TL_account_password.new_secure_algo instanceof TLRPC$TL_securePasswordKdfAlgoUnknown)) {
            return false;
        } else {
            return true;
        }
    }

    public static void initPasswordNewAlgo(TLRPC$TL_account_password tLRPC$TL_account_password) {
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.new_algo;
        if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow = (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo;
            byte[] bArr = new byte[(tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1.length + 32)];
            Utilities.random.nextBytes(bArr);
            byte[] bArr2 = tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1;
            System.arraycopy(bArr2, 0, bArr, 0, bArr2.length);
            tLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow.salt1 = bArr;
        }
        TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = tLRPC$TL_account_password.new_secure_algo;
        if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
            TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo;
            byte[] bArr3 = new byte[(tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt.length + 32)];
            Utilities.random.nextBytes(bArr3);
            byte[] bArr4 = tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt;
            System.arraycopy(bArr4, 0, bArr3, 0, bArr4.length);
            tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt = bArr3;
        }
    }

    private void loadPasswordInfo(boolean z, boolean z2) {
        if (!z2) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda32(this, z2, z), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$16(boolean z, boolean z2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda21(this, tLRPC$TL_error, tLObject, z, z2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadPasswordInfo$15(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z, boolean z2) {
        if (tLRPC$TL_error == null) {
            this.loading = false;
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            if (!canHandleCurrentPassword(tLRPC$TL_account_password, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (!z || z2) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            initPasswordNewAlgo(this.currentPassword);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
        }
        updateRows();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (!z) {
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
        TLRPC$TL_account_password tLRPC$TL_account_password;
        StringBuilder sb = new StringBuilder();
        sb.append(this.setPasswordRow);
        sb.append(this.setPasswordDetailRow);
        sb.append(this.changePasswordRow);
        sb.append(this.turnPasswordOffRow);
        sb.append(this.setRecoveryEmailRow);
        sb.append(this.changeRecoveryEmailRow);
        sb.append(this.passwordEnabledDetailRow);
        sb.append(this.rowCount);
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.passwordEnabledDetailRow = -1;
        if (!this.loading && (tLRPC$TL_account_password = this.currentPassword) != null && this.passwordEntered) {
            if (tLRPC$TL_account_password.has_password) {
                int i = 0 + 1;
                this.rowCount = i;
                this.changePasswordRow = 0;
                int i2 = i + 1;
                this.rowCount = i2;
                this.turnPasswordOffRow = i;
                if (tLRPC$TL_account_password.has_recovery) {
                    this.rowCount = i2 + 1;
                    this.changeRecoveryEmailRow = i2;
                } else {
                    this.rowCount = i2 + 1;
                    this.setRecoveryEmailRow = i2;
                }
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.passwordEnabledDetailRow = i3;
            } else {
                int i4 = 0 + 1;
                this.rowCount = i4;
                this.setPasswordRow = 0;
                this.rowCount = i4 + 1;
                this.setPasswordDetailRow = i4;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.setPasswordRow);
        sb2.append(this.setPasswordDetailRow);
        sb2.append(this.changePasswordRow);
        sb2.append(this.turnPasswordOffRow);
        sb2.append(this.setRecoveryEmailRow);
        sb2.append(this.changeRecoveryEmailRow);
        sb2.append(this.passwordEnabledDetailRow);
        sb2.append(this.rowCount);
        if (this.listAdapter != null && !sb.toString().equals(sb2.toString())) {
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
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda10(this), 200);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateRows$17() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void needShowProgress() {
        needShowProgress(false);
    }

    private void needShowProgress(boolean z) {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCacnel(false);
            if (z) {
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

    private void showAlertWithText(String str, String str2) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    private void clearPassword() {
        TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
        byte[] bArr = this.currentPasswordHash;
        if (bArr == null || bArr.length == 0) {
            tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
        }
        tLRPC$TL_account_updatePasswordSettings.new_settings = new TLRPC$TL_account_passwordInputSettings();
        UserConfig.getInstance(this.currentAccount).resetSavedPassword();
        this.currentSecret = null;
        TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = tLRPC$TL_account_updatePasswordSettings.new_settings;
        tLRPC$TL_account_passwordInputSettings.flags = 3;
        tLRPC$TL_account_passwordInputSettings.hint = "";
        tLRPC$TL_account_passwordInputSettings.new_password_hash = new byte[0];
        tLRPC$TL_account_passwordInputSettings.new_algo = new TLRPC$TL_passwordKdfAlgoUnknown();
        tLRPC$TL_account_updatePasswordSettings.new_settings.email = "";
        needShowProgress();
        Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda14(this, tLRPC$TL_account_updatePasswordSettings));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$24(TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings) {
        if (tLRPC$TL_account_updatePasswordSettings.password == null) {
            if (this.currentPassword.current_algo == null) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda30(this), 8);
                return;
            }
            tLRPC$TL_account_updatePasswordSettings.password = getNewSrpPassword();
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, new TwoStepVerificationActivity$$ExternalSyntheticLambda25(this), 10);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$19(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda19(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$18(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$23(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda20(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$22(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        if (tLRPC$TL_error == null || !"SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            needHideProgress();
            if (tLRPC$TL_error == null && (tLObject instanceof TLRPC$TL_boolTrue)) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, new Object[0]);
                finishFragment();
            } else if (tLRPC$TL_error == null) {
            } else {
                if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
                    return;
                }
                showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda27(this), 8);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$21(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda17(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$clearPassword$20(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            clearPassword();
        }
    }

    public TLRPC$TL_inputCheckPasswordSRP getNewSrpPassword() {
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = tLRPC$TL_account_password.current_algo;
        if (!(tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow)) {
            return null;
        }
        return SRPHelper.startCheck(this.currentPasswordHash, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
    }

    private boolean checkSecretValues(byte[] bArr, TLRPC$TL_account_passwordSettings tLRPC$TL_account_passwordSettings) {
        byte[] bArr2;
        TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings = tLRPC$TL_account_passwordSettings.secure_settings;
        if (tLRPC$TL_secureSecretSettings != null) {
            this.currentSecret = tLRPC$TL_secureSecretSettings.secure_secret;
            TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = tLRPC$TL_secureSecretSettings.secure_algo;
            if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                bArr2 = Utilities.computePBKDF2(bArr, ((TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo).salt);
            } else if (!(tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoSHA512)) {
                return false;
            } else {
                byte[] bArr3 = ((TLRPC$TL_securePasswordKdfAlgoSHA512) tLRPC$SecurePasswordKdfAlgo).salt;
                bArr2 = Utilities.computeSHA512(bArr3, bArr, bArr3);
            }
            this.currentSecretId = tLRPC$TL_account_passwordSettings.secure_settings.secure_secret_id;
            byte[] bArr4 = new byte[32];
            System.arraycopy(bArr2, 0, bArr4, 0, 32);
            byte[] bArr5 = new byte[16];
            System.arraycopy(bArr2, 32, bArr5, 0, 16);
            byte[] bArr6 = this.currentSecret;
            Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, bArr6.length, 0, 0);
            TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings2 = tLRPC$TL_account_passwordSettings.secure_settings;
            if (PassportActivity.checkSecret(tLRPC$TL_secureSecretSettings2.secure_secret, Long.valueOf(tLRPC$TL_secureSecretSettings2.secure_secret_id))) {
                return true;
            }
            TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
            tLRPC$TL_account_updatePasswordSettings.password = getNewSrpPassword();
            TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = new TLRPC$TL_account_passwordInputSettings();
            tLRPC$TL_account_updatePasswordSettings.new_settings = tLRPC$TL_account_passwordInputSettings;
            tLRPC$TL_account_passwordInputSettings.new_secure_settings = new TLRPC$TL_secureSecretSettings();
            TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings3 = tLRPC$TL_account_updatePasswordSettings.new_settings.new_secure_settings;
            tLRPC$TL_secureSecretSettings3.secure_secret = new byte[0];
            tLRPC$TL_secureSecretSettings3.secure_algo = new TLRPC$TL_securePasswordKdfAlgoUnknown();
            TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings2 = tLRPC$TL_account_updatePasswordSettings.new_settings;
            tLRPC$TL_account_passwordInputSettings2.new_secure_settings.secure_secret_id = 0;
            tLRPC$TL_account_passwordInputSettings2.flags |= 4;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, TwoStepVerificationActivity$$ExternalSyntheticLambda34.INSTANCE);
            this.currentSecret = null;
            this.currentSecretId = 0;
            return true;
        }
        this.currentSecret = null;
        this.currentSecretId = 0;
        return true;
    }

    /* access modifiers changed from: private */
    public void processDone() {
        if (!this.passwordEntered) {
            String obj = this.passwordEditText.getText().toString();
            if (obj.length() == 0) {
                onFieldError(this.passwordEditText, false);
                return;
            }
            byte[] stringBytes = AndroidUtilities.getStringBytes(obj);
            needShowProgress();
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda23(this, stringBytes));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$32(byte[] bArr) {
        TLRPC$TL_account_getPasswordSettings tLRPC$TL_account_getPasswordSettings = new TLRPC$TL_account_getPasswordSettings();
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        TwoStepVerificationActivity$$ExternalSyntheticLambda33 twoStepVerificationActivity$$ExternalSyntheticLambda33 = new TwoStepVerificationActivity$$ExternalSyntheticLambda33(this, bArr, x);
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getPasswordSettings.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                twoStepVerificationActivity$$ExternalSyntheticLambda33.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPasswordSettings, twoStepVerificationActivity$$ExternalSyntheticLambda33, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        twoStepVerificationActivity$$ExternalSyntheticLambda33.run((TLObject) null, tLRPC$TL_error2);
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$31(byte[] bArr, byte[] bArr2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            Utilities.globalQueue.postRunnable(new TwoStepVerificationActivity$$ExternalSyntheticLambda24(this, bArr, tLObject, bArr2));
        } else {
            AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda15(this, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$27(byte[] bArr, TLObject tLObject, byte[] bArr2) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda22(this, checkSecretValues(bArr, (TLRPC$TL_account_passwordSettings) tLObject), bArr2));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$26(boolean z, byte[] bArr) {
        if (this.delegate == null || !z) {
            needHideProgress();
        }
        if (z) {
            this.currentPasswordHash = bArr;
            this.passwordEntered = true;
            if (this.delegate != null) {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                this.delegate.didEnterPassword(getNewSrpPassword());
            } else if (!TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern)) {
                TwoStepVerificationSetupActivity twoStepVerificationSetupActivity = new TwoStepVerificationSetupActivity(this.currentAccount, 5, this.currentPassword);
                twoStepVerificationSetupActivity.setCurrentPasswordParams(this.currentPasswordHash, this.currentSecretId, this.currentSecret, true);
                presentFragment(twoStepVerificationSetupActivity, true);
            } else {
                AndroidUtilities.hideKeyboard(this.passwordEditText);
                TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity();
                twoStepVerificationActivity.passwordEntered = true;
                twoStepVerificationActivity.currentPasswordHash = this.currentPasswordHash;
                twoStepVerificationActivity.currentPassword = this.currentPassword;
                twoStepVerificationActivity.currentSecret = this.currentSecret;
                twoStepVerificationActivity.currentSecretId = this.currentSecretId;
                presentFragment(twoStepVerificationActivity, true);
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$30(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if ("SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new TwoStepVerificationActivity$$ExternalSyntheticLambda28(this), 8);
            return;
        }
        needHideProgress();
        if ("PASSWORD_HASH_INVALID".equals(tLRPC$TL_error.text)) {
            onFieldError(this.passwordEditText, true);
        } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
            int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
            if (intValue < 60) {
                str = LocaleController.formatPluralString("Seconds", intValue);
            } else {
                str = LocaleController.formatPluralString("Minutes", intValue / 60);
            }
            showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str));
        } else {
            showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$29(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new TwoStepVerificationActivity$$ExternalSyntheticLambda16(this, tLRPC$TL_error, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$processDone$28(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetOrRemoveTwoStepPassword, this.currentPassword);
            processDone();
        }
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

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public int getItemCount() {
            if (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) {
                return 0;
            }
            return TwoStepVerificationActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new TextInfoPrivacyCell(this.mContext);
            } else {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                textSettingsCell.setTag("windowBackgroundWhiteBlackText");
                textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangePassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textSettingsCell.setText(LocaleController.getString("SetAdditionalPassword", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textSettingsCell.setText(LocaleController.getString("TurnPasswordOff", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("ChangeRecoveryEmail", NUM), false);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            }
        }

        public int getItemViewType(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? 1 : 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"));
        arrayList.add(new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        return arrayList;
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
        builder.setPositiveButton(LocaleController.getString("ForceSetPasswordContinue", NUM), TwoStepVerificationActivity$$ExternalSyntheticLambda5.INSTANCE);
        builder.setNegativeButton(LocaleController.getString("ForceSetPasswordCancel", NUM), new TwoStepVerificationActivity$$ExternalSyntheticLambda4(this));
        ((TextView) builder.show().getButton(-2)).setTextColor(Theme.getColor("dialogTextRed2"));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$showSetForcePasswordAlert$34(DialogInterface dialogInterface, int i) {
        finishFragment();
    }

    public void setBlockingAlert(int i) {
        this.otherwiseReloginDays = i;
    }

    public void finishFragment() {
        if (this.otherwiseReloginDays >= 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("afterSignup", true);
            presentFragment(new DialogsActivity(bundle), true);
            return;
        }
        super.finishFragment();
    }
}
