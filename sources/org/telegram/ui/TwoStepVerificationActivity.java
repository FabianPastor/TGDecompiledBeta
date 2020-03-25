package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
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
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputCheckPasswordSRP;
import org.telegram.tgnet.TLRPC$PasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$SecurePasswordKdfAlgo;
import org.telegram.tgnet.TLRPC$TL_account_cancelPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_confirmPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_getPassword;
import org.telegram.tgnet.TLRPC$TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC$TL_account_password;
import org.telegram.tgnet.TLRPC$TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC$TL_account_passwordSettings;
import org.telegram.tgnet.TLRPC$TL_account_resendPasswordEmail;
import org.telegram.tgnet.TLRPC$TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC$TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC$TL_auth_recoverPassword;
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
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
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

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    /* access modifiers changed from: private */
    public int changePasswordRow;
    /* access modifiers changed from: private */
    public int changeRecoveryEmailRow;
    private boolean closeAfterSet;
    /* access modifiers changed from: private */
    public EditTextSettingsCell codeFieldCell;
    /* access modifiers changed from: private */
    public TLRPC$TL_account_password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private byte[] currentSecret;
    private long currentSecretId;
    private TwoStepVerificationActivityDelegate delegate;
    private boolean destroyed;
    /* access modifiers changed from: private */
    public ActionBarMenuItem doneItem;
    /* access modifiers changed from: private */
    public AnimatorSet doneItemAnimation;
    private String email;
    /* access modifiers changed from: private */
    public int emailCodeLength = 6;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean loading;
    /* access modifiers changed from: private */
    public int passwordCodeFieldRow;
    private EditTextBoldCursor passwordEditText;
    /* access modifiers changed from: private */
    public int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private int passwordSetState;
    /* access modifiers changed from: private */
    public int passwordSetupDetailRow;
    private boolean paused;
    private AlertDialog progressDialog;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public int resendCodeRow;
    /* access modifiers changed from: private */
    public int rowCount;
    private ScrollView scrollView;
    /* access modifiers changed from: private */
    public int setPasswordDetailRow;
    /* access modifiers changed from: private */
    public int setPasswordRow;
    /* access modifiers changed from: private */
    public int setRecoveryEmailRow;
    /* access modifiers changed from: private */
    public int shadowRow;
    private Runnable shortPollRunnable;
    private TextView titleTextView;
    /* access modifiers changed from: private */
    public int turnPasswordOffRow;
    private int type;
    private boolean waitingForEmail;

    public interface TwoStepVerificationActivityDelegate {
        void didEnterPassword(TLRPC$InputCheckPasswordSRP tLRPC$InputCheckPasswordSRP);
    }

    static /* synthetic */ void lambda$checkSecretValues$26(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$null$8(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    public TwoStepVerificationActivity(int i) {
        this.type = i;
        if (i == 0) {
            loadPasswordInfo(false);
        }
    }

    public TwoStepVerificationActivity(int i, int i2) {
        this.currentAccount = i;
        this.type = i2;
        if (i2 == 0) {
            loadPasswordInfo(false);
        }
    }

    /* access modifiers changed from: protected */
    public void setRecoveryParams(TLRPC$TL_account_password tLRPC$TL_account_password) {
        this.currentPassword = tLRPC$TL_account_password;
        this.passwordSetState = 4;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type != 0) {
            return true;
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didSetTwoStepPassword);
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
                    TwoStepVerificationActivity.this.finishFragment();
                } else if (i == 1) {
                    TwoStepVerificationActivity.this.processDone();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        ContextProgressView contextProgressView = new ContextProgressView(context2, 1);
        this.progressView = contextProgressView;
        contextProgressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.1f);
        this.progressView.setScaleY(0.1f);
        this.progressView.setVisibility(4);
        this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
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
        this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return TwoStepVerificationActivity.this.lambda$createView$0$TwoStepVerificationActivity(textView, i, keyEvent);
            }
        });
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
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        TextView textView3 = new TextView(context2);
        this.bottomButton = textView3;
        textView3.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", NUM));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        TextView textView4 = this.bottomButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout2.addView(textView4, LayoutHelper.createLinear(-1, -2, i | 80, 40, 0, 40, 14));
        this.bottomButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                TwoStepVerificationActivity.this.lambda$createView$6$TwoStepVerificationActivity(view);
            }
        });
        int i2 = this.type;
        if (i2 == 0) {
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
            this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
                public final void onItemClick(View view, int i) {
                    TwoStepVerificationActivity.this.lambda$createView$9$TwoStepVerificationActivity(view, i);
                }
            });
            EditTextSettingsCell editTextSettingsCell = new EditTextSettingsCell(context2);
            this.codeFieldCell = editTextSettingsCell;
            editTextSettingsCell.setTextAndHint("", LocaleController.getString("PasswordCode", NUM), false);
            this.codeFieldCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            EditTextBoldCursor textView5 = this.codeFieldCell.getTextView();
            textView5.setInputType(3);
            textView5.setImeOptions(6);
            textView5.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return TwoStepVerificationActivity.this.lambda$createView$10$TwoStepVerificationActivity(textView, i, keyEvent);
                }
            });
            textView5.addTextChangedListener(new TextWatcher() {
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
            this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.fragmentView.setTag("windowBackgroundWhite");
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
        if (this.type == 0) {
            if (this.currentPassword.has_recovery) {
                needShowProgress();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        TwoStepVerificationActivity.this.lambda$null$3$TwoStepVerificationActivity(tLObject, tLRPC$TL_error);
                    }
                }, 10);
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                builder.setNegativeButton(LocaleController.getString("RestorePasswordResetAccount", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$4$TwoStepVerificationActivity(dialogInterface, i);
                    }
                });
                builder.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", NUM));
                builder.setMessage(LocaleController.getString("RestorePasswordNoEmailText", NUM));
                showDialog(builder.create());
            }
        } else if (this.passwordSetState == 4) {
            showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", NUM), LocaleController.getString("RestoreEmailTroubleText", NUM));
        } else {
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setMessage(LocaleController.getString("YourEmailSkipWarningText", NUM));
            builder2.setTitle(LocaleController.getString("YourEmailSkipWarning", NUM));
            builder2.setPositiveButton(LocaleController.getString("YourEmailSkip", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$5$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
        }
    }

    public /* synthetic */ void lambda$null$3$TwoStepVerificationActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$2$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        String str;
        needHideProgress();
        if (tLRPC$TL_error == null) {
            TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery = (TLRPC$TL_auth_passwordRecovery) tLObject;
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setMessage(LocaleController.formatString("RestoreEmailSent", NUM, tLRPC$TL_auth_passwordRecovery.email_pattern));
            builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(tLRPC$TL_auth_passwordRecovery) {
                private final /* synthetic */ TLRPC$TL_auth_passwordRecovery f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$1$TwoStepVerificationActivity(this.f$1, dialogInterface, i);
                }
            });
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
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

    public /* synthetic */ void lambda$null$1$TwoStepVerificationActivity(TLRPC$TL_auth_passwordRecovery tLRPC$TL_auth_passwordRecovery, DialogInterface dialogInterface, int i) {
        TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        twoStepVerificationActivity.currentPassword = tLRPC$TL_account_password;
        tLRPC$TL_account_password.email_unconfirmed_pattern = tLRPC$TL_auth_passwordRecovery.email_pattern;
        twoStepVerificationActivity.currentSecretId = this.currentSecretId;
        twoStepVerificationActivity.currentSecret = this.currentSecret;
        twoStepVerificationActivity.passwordSetState = 4;
        presentFragment(twoStepVerificationActivity);
    }

    public /* synthetic */ void lambda$null$4$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        Activity parentActivity = getParentActivity();
        Browser.openUrl((Context) parentActivity, "https://telegram.org/deactivate?phone=" + UserConfig.getInstance(this.currentAccount).getClientPhone());
    }

    public /* synthetic */ void lambda$null$5$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        this.email = "";
        setNewPassword(false);
    }

    public /* synthetic */ void lambda$createView$9$TwoStepVerificationActivity(View view, int i) {
        String str;
        String str2;
        String str3;
        if (i == this.setPasswordRow || i == this.changePasswordRow) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 1);
            twoStepVerificationActivity.currentPasswordHash = this.currentPasswordHash;
            twoStepVerificationActivity.currentPassword = this.currentPassword;
            twoStepVerificationActivity.currentSecretId = this.currentSecretId;
            twoStepVerificationActivity.currentSecret = this.currentSecret;
            presentFragment(twoStepVerificationActivity);
        } else if (i == this.setRecoveryEmailRow || i == this.changeRecoveryEmailRow) {
            TwoStepVerificationActivity twoStepVerificationActivity2 = new TwoStepVerificationActivity(this.currentAccount, 1);
            twoStepVerificationActivity2.currentPasswordHash = this.currentPasswordHash;
            twoStepVerificationActivity2.currentPassword = this.currentPassword;
            twoStepVerificationActivity2.currentSecretId = this.currentSecretId;
            twoStepVerificationActivity2.currentSecret = this.currentSecret;
            twoStepVerificationActivity2.emailOnly = true;
            twoStepVerificationActivity2.passwordSetState = 3;
            presentFragment(twoStepVerificationActivity2);
        } else if (i == this.turnPasswordOffRow || i == this.abortPasswordRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            if (i == this.abortPasswordRow) {
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                if (tLRPC$TL_account_password == null || !tLRPC$TL_account_password.has_password) {
                    str = LocaleController.getString("CancelPasswordQuestion", NUM);
                } else {
                    str = LocaleController.getString("CancelEmailQuestion", NUM);
                }
                str3 = LocaleController.getString("CancelEmailQuestionTitle", NUM);
                str2 = LocaleController.getString("Abort", NUM);
            } else {
                String string = LocaleController.getString("TurnPasswordOffQuestion", NUM);
                if (this.currentPassword.has_secure_values) {
                    string = string + "\n\n" + LocaleController.getString("TurnPasswordOffPassport", NUM);
                }
                str3 = LocaleController.getString("TurnPasswordOffQuestionTitle", NUM);
                str2 = LocaleController.getString("Disable", NUM);
            }
            builder.setMessage(str);
            builder.setTitle(str3);
            builder.setPositiveButton(str2, new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$7$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        } else if (i == this.resendCodeRow) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_resendPasswordEmail(), $$Lambda$TwoStepVerificationActivity$4F6YJgV3MBVhnoAYE2KFt09BlNg.INSTANCE);
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setMessage(LocaleController.getString("ResendCodeInfo", NUM));
            builder2.setTitle(LocaleController.getString("AppName", NUM));
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
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
                this.currentPasswordHash = objArr[0];
                if (this.closeAfterSet && TextUtils.isEmpty(objArr[4]) && this.closeAfterSet) {
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
        EditTextSettingsCell editTextSettingsCell;
        super.onResume();
        this.paused = false;
        int i = this.type;
        if (i == 1) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$onResume$11$TwoStepVerificationActivity();
                }
            }, 200);
        } else if (i == 0 && (editTextSettingsCell = this.codeFieldCell) != null && editTextSettingsCell.getVisibility() == 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$onResume$12$TwoStepVerificationActivity();
                }
            }, 200);
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

    public void setCurrentPasswordInfo(byte[] bArr, TLRPC$TL_account_password tLRPC$TL_account_password) {
        if (bArr != null) {
            this.currentPasswordHash = bArr;
        }
        this.currentPassword = tLRPC$TL_account_password;
    }

    public void setDelegate(TwoStepVerificationActivityDelegate twoStepVerificationActivityDelegate) {
        this.delegate = twoStepVerificationActivityDelegate;
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        EditTextSettingsCell editTextSettingsCell;
        if (z) {
            int i = this.type;
            if (i == 1) {
                AndroidUtilities.showKeyboard(this.passwordEditText);
            } else if (i == 0 && (editTextSettingsCell = this.codeFieldCell) != null && editTextSettingsCell.getVisibility() == 0) {
                AndroidUtilities.showKeyboard(this.codeFieldCell.getTextView());
            }
        }
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

    private void loadPasswordInfo(boolean z) {
        if (!z) {
            this.loading = true;
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate(z) {
            private final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationActivity.this.lambda$loadPasswordInfo$14$TwoStepVerificationActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$loadPasswordInfo$14$TwoStepVerificationActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$13$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$13$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        TLRPC$TL_account_password tLRPC$TL_account_password;
        if (tLRPC$TL_error == null) {
            this.loading = false;
            TLRPC$TL_account_password tLRPC$TL_account_password2 = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password2;
            if (!canHandleCurrentPassword(tLRPC$TL_account_password2, false)) {
                AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
                return;
            }
            if (!z) {
                byte[] bArr = this.currentPasswordHash;
                this.passwordEntered = (bArr != null && bArr.length > 0) || !this.currentPassword.has_password;
            }
            this.waitingForEmail = !TextUtils.isEmpty(this.currentPassword.email_unconfirmed_pattern);
            initPasswordNewAlgo(this.currentPassword);
            if (!this.paused && this.closeAfterSet) {
                TLRPC$TL_account_password tLRPC$TL_account_password3 = this.currentPassword;
                if (tLRPC$TL_account_password3.has_password) {
                    Object obj = tLRPC$TL_account_password3.current_algo;
                    Object obj2 = tLRPC$TL_account_password3.new_secure_algo;
                    Object obj3 = tLRPC$TL_account_password3.secure_random;
                    String str = tLRPC$TL_account_password3.has_recovery ? "1" : null;
                    String str2 = this.currentPassword.hint;
                    if (str2 == null) {
                        str2 = "";
                    }
                    if (!this.waitingForEmail && obj != null) {
                        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didSetTwoStepPassword);
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, null, obj, obj2, obj3, str, str2, null, null);
                        finishFragment();
                    }
                }
            }
        }
        if (this.type == 0 && !this.destroyed && this.shortPollRunnable == null && (tLRPC$TL_account_password = this.currentPassword) != null && !TextUtils.isEmpty(tLRPC$TL_account_password.email_unconfirmed_pattern)) {
            startShortpoll();
        }
        updateRows();
    }

    private void startShortpoll() {
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        $$Lambda$TwoStepVerificationActivity$wOOLA1D7DR12e78ifRLekXVYec r0 = new Runnable() {
            public final void run() {
                TwoStepVerificationActivity.this.lambda$startShortpoll$15$TwoStepVerificationActivity();
            }
        };
        this.shortPollRunnable = r0;
        AndroidUtilities.runOnUIThread(r0, 5000);
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
            int i2 = 4;
            if (i == 0) {
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
            } else if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", NUM));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (i == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (i == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", NUM));
                this.titleTextView.setText(LocaleController.getString("YourEmail", NUM));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                TextView textView = this.bottomButton;
                if (!this.emailOnly) {
                    i2 = 0;
                }
                textView.setVisibility(i2);
            } else if (i == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", NUM));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", NUM));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", NUM));
                TextView textView2 = this.bottomButton;
                Object[] objArr = new Object[1];
                String str = this.currentPassword.email_unconfirmed_pattern;
                if (str == null) {
                    str = "";
                }
                objArr[0] = str;
                textView2.setText(LocaleController.formatString("RestoreEmailTrouble", NUM, objArr));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod((TransformationMethod) null);
                this.passwordEditText.setInputType(3);
                this.bottomTextView.setVisibility(0);
                this.bottomButton.setVisibility(0);
            }
            this.passwordEditText.setText("");
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
        sb.append(this.resendCodeRow);
        sb.append(this.abortPasswordRow);
        sb.append(this.passwordSetupDetailRow);
        sb.append(this.passwordCodeFieldRow);
        sb.append(this.passwordEnabledDetailRow);
        sb.append(this.shadowRow);
        sb.append(this.rowCount);
        boolean z = this.passwordCodeFieldRow != -1;
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
        if (!this.loading && (tLRPC$TL_account_password = this.currentPassword) != null) {
            if (this.waitingForEmail) {
                int i = 0 + 1;
                this.rowCount = i;
                this.passwordCodeFieldRow = 0;
                int i2 = i + 1;
                this.rowCount = i2;
                this.passwordSetupDetailRow = i;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.resendCodeRow = i2;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.abortPasswordRow = i3;
                this.rowCount = i4 + 1;
                this.shadowRow = i4;
            } else if (tLRPC$TL_account_password.has_password) {
                int i5 = 0 + 1;
                this.rowCount = i5;
                this.changePasswordRow = 0;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.turnPasswordOffRow = i5;
                if (tLRPC$TL_account_password.has_recovery) {
                    this.rowCount = i6 + 1;
                    this.changeRecoveryEmailRow = i6;
                } else {
                    this.rowCount = i6 + 1;
                    this.setRecoveryEmailRow = i6;
                }
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.passwordEnabledDetailRow = i7;
            } else {
                int i8 = 0 + 1;
                this.rowCount = i8;
                this.setPasswordRow = 0;
                this.rowCount = i8 + 1;
                this.setPasswordDetailRow = i8;
            }
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(this.setPasswordRow);
        sb2.append(this.setPasswordDetailRow);
        sb2.append(this.changePasswordRow);
        sb2.append(this.turnPasswordOffRow);
        sb2.append(this.setRecoveryEmailRow);
        sb2.append(this.changeRecoveryEmailRow);
        sb2.append(this.resendCodeRow);
        sb2.append(this.abortPasswordRow);
        sb2.append(this.passwordSetupDetailRow);
        sb2.append(this.passwordCodeFieldRow);
        sb2.append(this.passwordEnabledDetailRow);
        sb2.append(this.shadowRow);
        sb2.append(this.rowCount);
        if (this.listAdapter != null && !sb.toString().equals(sb2.toString())) {
            this.listAdapter.notifyDataSetChanged();
            if (this.passwordCodeFieldRow == -1 && getParentActivity() != null && z) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
                this.codeFieldCell.setText("", false);
            }
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
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", NUM));
            if (!TextUtils.isEmpty(this.currentPassword.hint)) {
                this.passwordEditText.setHint(this.currentPassword.hint);
            } else {
                this.passwordEditText.setHint("");
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    TwoStepVerificationActivity.this.lambda$updateRows$16$TwoStepVerificationActivity();
                }
            }, 200);
        }
    }

    public /* synthetic */ void lambda$updateRows$16$TwoStepVerificationActivity() {
        EditTextBoldCursor editTextBoldCursor;
        if (!isFinishing() && !this.destroyed && (editTextBoldCursor = this.passwordEditText) != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void showDoneProgress(boolean z) {
        final boolean z2 = z;
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.doneItemAnimation = new AnimatorSet();
        if (z2) {
            this.progressView.setVisibility(0);
            this.doneItem.setEnabled(false);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{1.0f})});
        } else {
            this.doneItem.getContentView().setVisibility(0);
            this.doneItem.setEnabled(true);
            this.doneItemAnimation.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[]{0.1f}), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[]{0.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleX", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "scaleY", new float[]{1.0f}), ObjectAnimator.ofFloat(this.doneItem.getContentView(), "alpha", new float[]{1.0f})});
        }
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animator)) {
                    if (!z2) {
                        TwoStepVerificationActivity.this.progressView.setVisibility(4);
                    } else {
                        TwoStepVerificationActivity.this.doneItem.getContentView().setVisibility(4);
                    }
                }
            }

            public void onAnimationCancel(Animator animator) {
                if (TwoStepVerificationActivity.this.doneItemAnimation != null && TwoStepVerificationActivity.this.doneItemAnimation.equals(animator)) {
                    AnimatorSet unused = TwoStepVerificationActivity.this.doneItemAnimation = null;
                }
            }
        });
        this.doneItemAnimation.setDuration(150);
        this.doneItemAnimation.start();
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setCanCacnel(false);
            this.progressDialog.show();
        }
    }

    /* access modifiers changed from: protected */
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
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setTitle(str);
            builder.setMessage(str2);
            showDialog(builder.create());
        }
    }

    private void setNewPassword(boolean z) {
        TLRPC$TL_account_password tLRPC$TL_account_password;
        if (!z || !this.waitingForEmail || !this.currentPassword.has_password) {
            String str = this.firstPassword;
            TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings = new TLRPC$TL_account_updatePasswordSettings();
            byte[] bArr = this.currentPasswordHash;
            if (bArr == null || bArr.length == 0) {
                tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
            }
            tLRPC$TL_account_updatePasswordSettings.new_settings = new TLRPC$TL_account_passwordInputSettings();
            if (z) {
                UserConfig.getInstance(this.currentAccount).resetSavedPassword();
                this.currentSecret = null;
                if (this.waitingForEmail) {
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = tLRPC$TL_account_updatePasswordSettings.new_settings;
                    tLRPC$TL_account_passwordInputSettings.flags = 2;
                    tLRPC$TL_account_passwordInputSettings.email = "";
                    tLRPC$TL_account_updatePasswordSettings.password = new TLRPC$TL_inputCheckPasswordEmpty();
                } else {
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings2 = tLRPC$TL_account_updatePasswordSettings.new_settings;
                    tLRPC$TL_account_passwordInputSettings2.flags = 3;
                    tLRPC$TL_account_passwordInputSettings2.hint = "";
                    tLRPC$TL_account_passwordInputSettings2.new_password_hash = new byte[0];
                    tLRPC$TL_account_passwordInputSettings2.new_algo = new TLRPC$TL_passwordKdfAlgoUnknown();
                    tLRPC$TL_account_updatePasswordSettings.new_settings.email = "";
                }
            } else {
                if (this.hint == null && (tLRPC$TL_account_password = this.currentPassword) != null) {
                    this.hint = tLRPC$TL_account_password.hint;
                }
                if (this.hint == null) {
                    this.hint = "";
                }
                if (str != null) {
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings3 = tLRPC$TL_account_updatePasswordSettings.new_settings;
                    tLRPC$TL_account_passwordInputSettings3.flags |= 1;
                    tLRPC$TL_account_passwordInputSettings3.hint = this.hint;
                    tLRPC$TL_account_passwordInputSettings3.new_algo = this.currentPassword.new_algo;
                }
                if (this.email.length() > 0) {
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings4 = tLRPC$TL_account_updatePasswordSettings.new_settings;
                    tLRPC$TL_account_passwordInputSettings4.flags = 2 | tLRPC$TL_account_passwordInputSettings4.flags;
                    tLRPC$TL_account_passwordInputSettings4.email = this.email.trim();
                }
            }
            needShowProgress();
            Utilities.globalQueue.postRunnable(new Runnable(tLRPC$TL_account_updatePasswordSettings, z, str) {
                private final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$1;
                private final /* synthetic */ boolean f$2;
                private final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$setNewPassword$25$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_cancelPasswordEmail(), new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationActivity.this.lambda$setNewPassword$18$TwoStepVerificationActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$setNewPassword$18$TwoStepVerificationActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            private final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$17$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$17$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error) {
        needHideProgress();
        if (tLRPC$TL_error == null) {
            loadPasswordInfo(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
            updateRows();
        }
    }

    public /* synthetic */ void lambda$setNewPassword$25$TwoStepVerificationActivity(TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings, boolean z, String str) {
        byte[] bArr;
        byte[] bArr2;
        byte[] bArr3;
        TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings2 = tLRPC$TL_account_updatePasswordSettings;
        if (tLRPC$TL_account_updatePasswordSettings2.password == null) {
            tLRPC$TL_account_updatePasswordSettings2.password = getNewSrpPassword();
        }
        if (z || str == null) {
            bArr2 = null;
            bArr = null;
        } else {
            byte[] stringBytes = AndroidUtilities.getStringBytes(str);
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                bArr = stringBytes;
                bArr2 = SRPHelper.getX(stringBytes, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo);
            } else {
                bArr = stringBytes;
                bArr2 = null;
            }
        }
        $$Lambda$TwoStepVerificationActivity$VJV5wRfpqB6jJ8dEtoqePT3HaYk r0 = new RequestDelegate(z, bArr2, tLRPC$TL_account_updatePasswordSettings, str) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ byte[] f$2;
            private final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationActivity.this.lambda$null$24$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
            }
        };
        if (!z) {
            if (!(str == null || (bArr3 = this.currentSecret) == null || bArr3.length != 32)) {
                TLRPC$SecurePasswordKdfAlgo tLRPC$SecurePasswordKdfAlgo = this.currentPassword.new_secure_algo;
                if (tLRPC$SecurePasswordKdfAlgo instanceof TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) {
                    TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000 = (TLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000) tLRPC$SecurePasswordKdfAlgo;
                    byte[] computePBKDF2 = Utilities.computePBKDF2(bArr, tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000.salt);
                    byte[] bArr4 = new byte[32];
                    System.arraycopy(computePBKDF2, 0, bArr4, 0, 32);
                    byte[] bArr5 = new byte[16];
                    System.arraycopy(computePBKDF2, 32, bArr5, 0, 16);
                    byte[] bArr6 = new byte[32];
                    System.arraycopy(this.currentSecret, 0, bArr6, 0, 32);
                    Utilities.aesCbcEncryptionByteArraySafe(bArr6, bArr4, bArr5, 0, 32, 0, 1);
                    tLRPC$TL_account_updatePasswordSettings2.new_settings.new_secure_settings = new TLRPC$TL_secureSecretSettings();
                    TLRPC$TL_account_passwordInputSettings tLRPC$TL_account_passwordInputSettings = tLRPC$TL_account_updatePasswordSettings2.new_settings;
                    TLRPC$TL_secureSecretSettings tLRPC$TL_secureSecretSettings = tLRPC$TL_account_passwordInputSettings.new_secure_settings;
                    tLRPC$TL_secureSecretSettings.secure_algo = tLRPC$TL_securePasswordKdfAlgoPBKDF2HMACSHA512iter100000;
                    tLRPC$TL_secureSecretSettings.secure_secret = bArr6;
                    tLRPC$TL_secureSecretSettings.secure_secret_id = this.currentSecretId;
                    tLRPC$TL_account_passwordInputSettings.flags |= 4;
                }
            }
            TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = this.currentPassword.new_algo;
            if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (str != null) {
                    tLRPC$TL_account_updatePasswordSettings2.new_settings.new_password_hash = SRPHelper.getVBytes(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
                    if (tLRPC$TL_account_updatePasswordSettings2.new_settings.new_password_hash == null) {
                        TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                        tLRPC$TL_error.text = "ALGO_INVALID";
                        r0.run((TLObject) null, tLRPC$TL_error);
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings2, r0, 10);
                return;
            }
            TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
            tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
            r0.run((TLObject) null, tLRPC$TL_error2);
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings2, r0, 10);
    }

    public /* synthetic */ void lambda$null$24$TwoStepVerificationActivity(boolean z, byte[] bArr, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings, String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, z, tLObject, bArr, tLRPC$TL_account_updatePasswordSettings, str) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ byte[] f$4;
            private final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$5;
            private final /* synthetic */ String f$6;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$23$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        });
    }

    public /* synthetic */ void lambda$null$23$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error, boolean z, TLObject tLObject, byte[] bArr, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings, String str) {
        String str2;
        TLRPC$TL_account_password tLRPC$TL_account_password;
        if (tLRPC$TL_error == null || !"SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            needHideProgress();
            if (tLRPC$TL_error != null || !(tLObject instanceof TLRPC$TL_boolTrue)) {
                if (tLRPC$TL_error == null) {
                    return;
                }
                if ("EMAIL_UNCONFIRMED".equals(tLRPC$TL_error.text) || tLRPC$TL_error.text.startsWith("EMAIL_UNCONFIRMED_")) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(bArr, tLRPC$TL_account_updatePasswordSettings) {
                        private final /* synthetic */ byte[] f$1;
                        private final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationActivity.this.lambda$null$22$TwoStepVerificationActivity(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", NUM));
                    builder.setTitle(LocaleController.getString("YourEmailAlmostThere", NUM));
                    Dialog showDialog = showDialog(builder.create());
                    if (showDialog != null) {
                        showDialog.setCanceledOnTouchOutside(false);
                        showDialog.setCancelable(false);
                    }
                } else if ("EMAIL_INVALID".equals(tLRPC$TL_error.text)) {
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.getString("PasswordEmailInvalid", NUM));
                } else if (tLRPC$TL_error.text.startsWith("FLOOD_WAIT")) {
                    int intValue = Utilities.parseInt(tLRPC$TL_error.text).intValue();
                    if (intValue < 60) {
                        str2 = LocaleController.formatPluralString("Seconds", intValue);
                    } else {
                        str2 = LocaleController.formatPluralString("Minutes", intValue / 60);
                    }
                    showAlertWithText(LocaleController.getString("AppName", NUM), LocaleController.formatString("FloodWaitTime", NUM, str2));
                } else {
                    showAlertWithText(LocaleController.getString("AppName", NUM), tLRPC$TL_error.text);
                }
            } else if (z) {
                this.currentPassword = null;
                this.currentPasswordHash = new byte[0];
                loadPasswordInfo(false);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didRemoveTwoStepPassword, new Object[0]);
                updateRows();
            } else if (getParentActivity() != null) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                builder2.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(bArr, tLRPC$TL_account_updatePasswordSettings) {
                    private final /* synthetic */ byte[] f$1;
                    private final /* synthetic */ TLRPC$TL_account_updatePasswordSettings f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$21$TwoStepVerificationActivity(this.f$1, this.f$2, dialogInterface, i);
                    }
                });
                if (str != null || (tLRPC$TL_account_password = this.currentPassword) == null || !tLRPC$TL_account_password.has_password) {
                    builder2.setMessage(LocaleController.getString("YourPasswordSuccessText", NUM));
                } else {
                    builder2.setMessage(LocaleController.getString("YourEmailSuccessText", NUM));
                }
                builder2.setTitle(LocaleController.getString("YourPasswordSuccess", NUM));
                Dialog showDialog2 = showDialog(builder2.create());
                if (showDialog2 != null) {
                    showDialog2.setCanceledOnTouchOutside(false);
                    showDialog2.setCancelable(false);
                }
            }
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate(z) {
                private final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TwoStepVerificationActivity.this.lambda$null$20$TwoStepVerificationActivity(this.f$1, tLObject, tLRPC$TL_error);
                }
            }, 8);
        }
    }

    public /* synthetic */ void lambda$null$20$TwoStepVerificationActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, z) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;
            private final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$19$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$19$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, boolean z) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            setNewPassword(z);
        }
    }

    public /* synthetic */ void lambda$null$21$TwoStepVerificationActivity(byte[] bArr, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings, DialogInterface dialogInterface, int i) {
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        instance.postNotificationName(i2, bArr, tLRPC$TL_account_updatePasswordSettings.new_settings.new_algo, tLRPC$TL_account_password.new_secure_algo, tLRPC$TL_account_password.secure_random, this.email, this.hint, null, this.firstPassword);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$22$TwoStepVerificationActivity(byte[] bArr, TLRPC$TL_account_updatePasswordSettings tLRPC$TL_account_updatePasswordSettings, DialogInterface dialogInterface, int i) {
        if (this.closeAfterSet) {
            TwoStepVerificationActivity twoStepVerificationActivity = new TwoStepVerificationActivity(this.currentAccount, 0);
            twoStepVerificationActivity.setCloseAfterSet(true);
            ActionBarLayout actionBarLayout = this.parentLayout;
            actionBarLayout.addFragmentToStack(twoStepVerificationActivity, actionBarLayout.fragmentsStack.size() - 1);
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        String str = this.email;
        instance.postNotificationName(i2, bArr, tLRPC$TL_account_updatePasswordSettings.new_settings.new_algo, tLRPC$TL_account_password.new_secure_algo, tLRPC$TL_account_password.secure_random, str, this.hint, str, this.firstPassword);
        finishFragment();
    }

    /* access modifiers changed from: protected */
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updatePasswordSettings, $$Lambda$TwoStepVerificationActivity$0aZmkrG3QYsDjfpNDgl1nGy4PJM.INSTANCE);
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
        int i = this.type;
        if (i == 0) {
            if (!this.passwordEntered) {
                String obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                byte[] stringBytes = AndroidUtilities.getStringBytes(obj);
                needShowProgress();
                Utilities.globalQueue.postRunnable(new Runnable(stringBytes) {
                    private final /* synthetic */ byte[] f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        TwoStepVerificationActivity.this.lambda$processDone$33$TwoStepVerificationActivity(this.f$1);
                    }
                });
            } else if (this.waitingForEmail && this.currentPassword != null) {
                if (this.codeFieldCell.length() == 0) {
                    onFieldError(this.codeFieldCell.getTextView(), false);
                    return;
                }
                sendEmailConfirm(this.codeFieldCell.getText());
                showDoneProgress(true);
            }
        } else if (i == 1) {
            int i2 = this.passwordSetState;
            if (i2 == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", NUM));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (i2 == 1) {
                if (!this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                    onFieldError(this.passwordEditText, true);
                    return;
                }
                setPasswordSetState(2);
            } else if (i2 == 2) {
                String obj2 = this.passwordEditText.getText().toString();
                this.hint = obj2;
                if (obj2.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", NUM), 0).show();
                    } catch (Exception e2) {
                        FileLog.e((Throwable) e2);
                    }
                    onFieldError(this.passwordEditText, false);
                } else if (!this.currentPassword.has_recovery) {
                    setPasswordSetState(3);
                } else {
                    this.email = "";
                    setNewPassword(false);
                }
            } else if (i2 == 3) {
                String obj3 = this.passwordEditText.getText().toString();
                this.email = obj3;
                if (!isValidEmail(obj3)) {
                    onFieldError(this.passwordEditText, false);
                } else {
                    setNewPassword(false);
                }
            } else if (i2 == 4) {
                String obj4 = this.passwordEditText.getText().toString();
                if (obj4.length() == 0) {
                    onFieldError(this.passwordEditText, false);
                    return;
                }
                TLRPC$TL_auth_recoverPassword tLRPC$TL_auth_recoverPassword = new TLRPC$TL_auth_recoverPassword();
                tLRPC$TL_auth_recoverPassword.code = obj4;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_auth_recoverPassword, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        TwoStepVerificationActivity.this.lambda$processDone$36$TwoStepVerificationActivity(tLObject, tLRPC$TL_error);
                    }
                }, 10);
            }
        }
    }

    public /* synthetic */ void lambda$processDone$33$TwoStepVerificationActivity(byte[] bArr) {
        TLRPC$TL_account_getPasswordSettings tLRPC$TL_account_getPasswordSettings = new TLRPC$TL_account_getPasswordSettings();
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo = this.currentPassword.current_algo;
        byte[] x = tLRPC$PasswordKdfAlgo instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow ? SRPHelper.getX(bArr, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo) : null;
        $$Lambda$TwoStepVerificationActivity$9nDa_eoqxtx9fYaZZmibmVZpEjY r2 = new RequestDelegate(bArr, x) {
            private final /* synthetic */ byte[] f$1;
            private final /* synthetic */ byte[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationActivity.this.lambda$null$32$TwoStepVerificationActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        };
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        TLRPC$PasswordKdfAlgo tLRPC$PasswordKdfAlgo2 = tLRPC$TL_account_password.current_algo;
        if (tLRPC$PasswordKdfAlgo2 instanceof TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
            TLRPC$TL_inputCheckPasswordSRP startCheck = SRPHelper.startCheck(x, tLRPC$TL_account_password.srp_id, tLRPC$TL_account_password.srp_B, (TLRPC$TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) tLRPC$PasswordKdfAlgo2);
            tLRPC$TL_account_getPasswordSettings.password = startCheck;
            if (startCheck == null) {
                TLRPC$TL_error tLRPC$TL_error = new TLRPC$TL_error();
                tLRPC$TL_error.text = "ALGO_INVALID";
                r2.run((TLObject) null, tLRPC$TL_error);
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_getPasswordSettings, r2, 10);
            return;
        }
        TLRPC$TL_error tLRPC$TL_error2 = new TLRPC$TL_error();
        tLRPC$TL_error2.text = "PASSWORD_HASH_INVALID";
        r2.run((TLObject) null, tLRPC$TL_error2);
    }

    public /* synthetic */ void lambda$null$32$TwoStepVerificationActivity(byte[] bArr, byte[] bArr2, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            Utilities.globalQueue.postRunnable(new Runnable(bArr, tLObject, bArr2) {
                private final /* synthetic */ byte[] f$1;
                private final /* synthetic */ TLObject f$2;
                private final /* synthetic */ byte[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$null$28$TwoStepVerificationActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
                private final /* synthetic */ TLRPC$TL_error f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    TwoStepVerificationActivity.this.lambda$null$31$TwoStepVerificationActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$28$TwoStepVerificationActivity(byte[] bArr, TLObject tLObject, byte[] bArr2) {
        AndroidUtilities.runOnUIThread(new Runnable(checkSecretValues(bArr, (TLRPC$TL_account_passwordSettings) tLObject), bArr2) {
            private final /* synthetic */ boolean f$1;
            private final /* synthetic */ byte[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$27$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
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
            } else {
                updateRows();
            }
        } else {
            AlertsCreator.showUpdateAppAlert(getParentActivity(), LocaleController.getString("UpdateAppAlert", NUM), true);
        }
    }

    public /* synthetic */ void lambda$null$31$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if ("SRP_ID_INVALID".equals(tLRPC$TL_error.text)) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC$TL_account_getPassword(), new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    TwoStepVerificationActivity.this.lambda$null$30$TwoStepVerificationActivity(tLObject, tLRPC$TL_error);
                }
            }, 8);
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

    public /* synthetic */ void lambda$null$30$TwoStepVerificationActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            private final /* synthetic */ TLRPC$TL_error f$1;
            private final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$29$TwoStepVerificationActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$29$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_account_password tLRPC$TL_account_password = (TLRPC$TL_account_password) tLObject;
            this.currentPassword = tLRPC$TL_account_password;
            initPasswordNewAlgo(tLRPC$TL_account_password);
            processDone();
        }
    }

    public /* synthetic */ void lambda$processDone$36$TwoStepVerificationActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            private final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$35$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$35$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if (tLRPC$TL_error == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    TwoStepVerificationActivity.this.lambda$null$34$TwoStepVerificationActivity(dialogInterface, i);
                }
            });
            builder.setMessage(LocaleController.getString("PasswordReset", NUM));
            builder.setTitle(LocaleController.getString("AppName", NUM));
            Dialog showDialog = showDialog(builder.create());
            if (showDialog != null) {
                showDialog.setCanceledOnTouchOutside(false);
                showDialog.setCancelable(false);
            }
        } else if (tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
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

    public /* synthetic */ void lambda$null$34$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
        finishFragment();
    }

    private void sendEmailConfirm(String str) {
        TLRPC$TL_account_confirmPasswordEmail tLRPC$TL_account_confirmPasswordEmail = new TLRPC$TL_account_confirmPasswordEmail();
        tLRPC$TL_account_confirmPasswordEmail.code = str;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_confirmPasswordEmail, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                TwoStepVerificationActivity.this.lambda$sendEmailConfirm$39$TwoStepVerificationActivity(tLObject, tLRPC$TL_error);
            }
        }, 10);
    }

    public /* synthetic */ void lambda$sendEmailConfirm$39$TwoStepVerificationActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            private final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                TwoStepVerificationActivity.this.lambda$null$38$TwoStepVerificationActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$38$TwoStepVerificationActivity(TLRPC$TL_error tLRPC$TL_error) {
        String str;
        if (this.type == 0 && this.waitingForEmail) {
            showDoneProgress(false);
        }
        if (tLRPC$TL_error == null) {
            if (getParentActivity() != null) {
                Runnable runnable = this.shortPollRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.shortPollRunnable = null;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        TwoStepVerificationActivity.this.lambda$null$37$TwoStepVerificationActivity(dialogInterface, i);
                    }
                });
                TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
                if (tLRPC$TL_account_password == null || !tLRPC$TL_account_password.has_password) {
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
        } else if (tLRPC$TL_error.text.startsWith("CODE_INVALID")) {
            onFieldError(this.waitingForEmail ? this.codeFieldCell.getTextView() : this.passwordEditText, true);
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

    public /* synthetic */ void lambda$null$37$TwoStepVerificationActivity(DialogInterface dialogInterface, int i) {
        if (this.type == 0) {
            loadPasswordInfo(false);
            this.doneItem.setVisibility(8);
            return;
        }
        NotificationCenter instance = NotificationCenter.getInstance(this.currentAccount);
        int i2 = NotificationCenter.didSetTwoStepPassword;
        TLRPC$TL_account_password tLRPC$TL_account_password = this.currentPassword;
        instance.postNotificationName(i2, this.currentPasswordHash, tLRPC$TL_account_password.new_algo, tLRPC$TL_account_password.new_secure_algo, tLRPC$TL_account_password.secure_random, this.email, this.hint, null, this.firstPassword);
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
            if (i == 0) {
                view = new TextSettingsCell(this.mContext);
                view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            } else if (i != 1) {
                view = TwoStepVerificationActivity.this.codeFieldCell;
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            } else {
                view = new TextInfoPrivacyCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
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
                    String string = LocaleController.getString("ChangeRecoveryEmail", NUM);
                    if (TwoStepVerificationActivity.this.abortPasswordRow != -1) {
                        z = true;
                    }
                    textSettingsCell.setText(string, z);
                } else if (i == TwoStepVerificationActivity.this.resendCodeRow) {
                    textSettingsCell.setText(LocaleController.getString("ResendCode", NUM), true);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textSettingsCell.setText(LocaleController.getString("SetRecoveryEmail", NUM), false);
                } else if (i == TwoStepVerificationActivity.this.abortPasswordRow) {
                    textSettingsCell.setTag("windowBackgroundWhiteRedText3");
                    textSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        textSettingsCell.setText(LocaleController.getString("AbortPassword", NUM), false);
                    } else {
                        textSettingsCell.setText(LocaleController.getString("AbortEmail", NUM), false);
                    }
                }
            } else if (itemViewType == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    return;
                }
                String str = "";
                if (i == TwoStepVerificationActivity.this.shadowRow) {
                    textInfoPrivacyCell.setText(str);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    if (TwoStepVerificationActivity.this.currentPassword == null || !TwoStepVerificationActivity.this.currentPassword.has_password) {
                        Object[] objArr = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr[0] = str;
                        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText2", NUM, objArr));
                    } else {
                        Object[] objArr2 = new Object[1];
                        if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern != null) {
                            str = TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern;
                        }
                        objArr2[0] = str;
                        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText3", NUM, objArr2));
                    }
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    textInfoPrivacyCell.setText(LocaleController.getString("EnabledPasswordText", NUM));
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
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

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, EditTextSettingsCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText3"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{EditTextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated")};
    }
}
