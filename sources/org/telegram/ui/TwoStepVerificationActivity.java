package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_getPassword;
import org.telegram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.telegram.tgnet.TLRPC.TL_account_noPassword;
import org.telegram.tgnet.TLRPC.TL_account_password;
import org.telegram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.telegram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.telegram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.telegram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.telegram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.account_Password;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class TwoStepVerificationActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private int abortPasswordRow;
    private TextView bottomButton;
    private TextView bottomTextView;
    private int changePasswordRow;
    private int changeRecoveryEmailRow;
    private account_Password currentPassword;
    private byte[] currentPasswordHash = new byte[0];
    private boolean destroyed;
    private ActionBarMenuItem doneItem;
    private String email;
    private boolean emailOnly;
    private EmptyTextProgressView emptyView;
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private EditTextBoldCursor passwordEditText;
    private int passwordEmailVerifyDetailRow;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private int passwordSetState;
    private int passwordSetupDetailRow;
    private AlertDialog progressDialog;
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
    class C17332 implements OnEditorActionListener {
        C17332() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                if (i != 6) {
                    return null;
                }
            }
            TwoStepVerificationActivity.this.processDone();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$3 */
    class C17343 implements Callback {
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

        C17343() {
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$4 */
    class C17384 implements OnClickListener {

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity$4$2 */
        class C17372 implements DialogInterface.OnClickListener {
            C17372() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.email = TtmlNode.ANONYMOUS_REGION_ID;
                TwoStepVerificationActivity.this.setNewPassword(0);
            }
        }

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity$4$1 */
        class C23061 implements RequestDelegate {
            C23061() {
            }

            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        TwoStepVerificationActivity.this.needHideProgress();
                        if (tL_error == null) {
                            final TL_auth_passwordRecovery tL_auth_passwordRecovery = (TL_auth_passwordRecovery) tLObject;
                            Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.formatString("RestoreEmailSent", C0446R.string.RestoreEmailSent, tL_auth_passwordRecovery.email_pattern));
                            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface = new TwoStepVerificationActivity(1);
                                    dialogInterface.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                                    dialogInterface.currentPassword.email_unconfirmed_pattern = tL_auth_passwordRecovery.email_pattern;
                                    dialogInterface.passwordSetState = 4;
                                    TwoStepVerificationActivity.this.presentFragment(dialogInterface);
                                }
                            });
                            Dialog showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
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
                            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                        } else {
                            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                        }
                    }
                });
            }
        }

        C17384() {
        }

        public void onClick(View view) {
            if (TwoStepVerificationActivity.this.type == null) {
                if (TwoStepVerificationActivity.this.currentPassword.has_recovery != null) {
                    TwoStepVerificationActivity.this.needShowProgress();
                    ConnectionsManager.getInstance(TwoStepVerificationActivity.this.currentAccount).sendRequest(new TL_auth_requestPasswordRecovery(), new C23061(), 10);
                    return;
                }
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0446R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", C0446R.string.RestorePasswordNoEmailText));
            } else if (TwoStepVerificationActivity.this.passwordSetState == 4) {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", C0446R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", C0446R.string.RestoreEmailTroubleText));
            } else {
                view = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                view.setMessage(LocaleController.getString("YourEmailSkipWarningText", C0446R.string.YourEmailSkipWarningText));
                view.setTitle(LocaleController.getString("YourEmailSkipWarning", C0446R.string.YourEmailSkipWarning));
                view.setPositiveButton(LocaleController.getString("YourEmailSkip", C0446R.string.YourEmailSkip), new C17372());
                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                TwoStepVerificationActivity.this.showDialog(view.create());
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$6 */
    class C17406 implements Runnable {
        C17406() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$8 */
    class C17438 implements Runnable {
        C17438() {
        }

        public void run() {
            if (TwoStepVerificationActivity.this.passwordEditText != null) {
                TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$1 */
    class C23051 extends ActionBarMenuOnItemClick {
        C23051() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                TwoStepVerificationActivity.this.finishFragment();
            } else if (i == 1) {
                TwoStepVerificationActivity.this.processDone();
            }
        }
    }

    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$5 */
    class C23075 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.TwoStepVerificationActivity$5$1 */
        class C17391 implements DialogInterface.OnClickListener {
            C17391() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                TwoStepVerificationActivity.this.setNewPassword(1);
            }
        }

        C23075() {
        }

        public void onItemClick(View view, int i) {
            if (i != TwoStepVerificationActivity.this.setPasswordRow) {
                if (i != TwoStepVerificationActivity.this.changePasswordRow) {
                    if (i != TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        if (i != TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                            if (i == TwoStepVerificationActivity.this.turnPasswordOffRow || i == TwoStepVerificationActivity.this.abortPasswordRow) {
                                view = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                view.setMessage(LocaleController.getString("TurnPasswordOffQuestion", C0446R.string.TurnPasswordOffQuestion));
                                view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C17391());
                                view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                                TwoStepVerificationActivity.this.showDialog(view.create());
                                return;
                            }
                            return;
                        }
                    }
                    view = new TwoStepVerificationActivity(1);
                    view.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                    view.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                    view.emailOnly = true;
                    view.passwordSetState = 3;
                    TwoStepVerificationActivity.this.presentFragment(view);
                    return;
                }
            }
            view = new TwoStepVerificationActivity(1);
            view.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
            view.currentPassword = TwoStepVerificationActivity.this.currentPassword;
            TwoStepVerificationActivity.this.presentFragment(view);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            return (viewHolder == TwoStepVerificationActivity.this.setPasswordDetailRow || viewHolder == TwoStepVerificationActivity.this.shadowRow || viewHolder == TwoStepVerificationActivity.this.passwordSetupDetailRow || viewHolder == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow || viewHolder == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? null : true;
        }

        public int getItemCount() {
            if (!TwoStepVerificationActivity.this.loading) {
                if (TwoStepVerificationActivity.this.currentPassword != null) {
                    return TwoStepVerificationActivity.this.rowCount;
                }
            }
            return 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                viewGroup = new TextInfoPrivacyCell(this.mContext);
            } else {
                viewGroup = new TextSettingsCell(this.mContext);
                viewGroup.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            }
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            boolean z = false;
            switch (viewHolder.getItemViewType()) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    textSettingsCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                        textSettingsCell.setText(LocaleController.getString("ChangePassword", C0446R.string.ChangePassword), true);
                        return;
                    } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                        textSettingsCell.setText(LocaleController.getString("SetAdditionalPassword", C0446R.string.SetAdditionalPassword), true);
                        return;
                    } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                        textSettingsCell.setText(LocaleController.getString("TurnPasswordOff", C0446R.string.TurnPasswordOff), true);
                        return;
                    } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                        i = LocaleController.getString("ChangeRecoveryEmail", C0446R.string.ChangeRecoveryEmail);
                        if (TwoStepVerificationActivity.this.abortPasswordRow != -1) {
                            z = true;
                        }
                        textSettingsCell.setText(i, z);
                        return;
                    } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                        textSettingsCell.setText(LocaleController.getString("SetRecoveryEmail", C0446R.string.SetRecoveryEmail), false);
                        return;
                    } else if (i == TwoStepVerificationActivity.this.abortPasswordRow) {
                        textSettingsCell.setTag(Theme.key_windowBackgroundWhiteRedText3);
                        textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
                        textSettingsCell.setText(LocaleController.getString("AbortPassword", C0446R.string.AbortPassword), false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("SetAdditionalPasswordInfo", C0446R.string.SetAdditionalPasswordInfo));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == TwoStepVerificationActivity.this.shadowRow) {
                        textInfoPrivacyCell.setText(TtmlNode.ANONYMOUS_REGION_ID);
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("EmailPasswordConfirmText", C0446R.string.EmailPasswordConfirmText, TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.getString("EnabledPasswordText", C0446R.string.EnabledPasswordText));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else if (i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("PendingEmailText", C0446R.string.PendingEmailText, TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern));
                        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public int getItemViewType(int i) {
            if (!(i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow)) {
                if (i != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                    return 0;
                }
            }
            return 1;
        }
    }

    public TwoStepVerificationActivity(int i) {
        this.type = i;
        if (i == 0) {
            loadPasswordInfo(false);
        }
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
                FileLog.m3e(e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new C23051());
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        View linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        this.titleTextView = new TextView(context2);
        this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        linearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
        this.passwordEditText = new EditTextBoldCursor(context2);
        this.passwordEditText.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        this.passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.passwordEditText.setCursorSize(AndroidUtilities.dp(20.0f));
        this.passwordEditText.setCursorWidth(1.5f);
        linearLayout.addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
        this.passwordEditText.setOnEditorActionListener(new C17332());
        this.passwordEditText.setCustomSelectionActionModeCallback(new C17343());
        this.bottomTextView = new TextView(context2);
        this.bottomTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        this.bottomTextView.setTextSize(1, 14.0f);
        int i = 5;
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        r0.bottomTextView.setText(LocaleController.getString("YourEmailInfo", C0446R.string.YourEmailInfo));
        linearLayout.addView(r0.bottomTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 40, 30, 40, 0));
        View linearLayout2 = new LinearLayout(context2);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -1));
        r0.bottomButton = new TextView(context2);
        r0.bottomButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        r0.bottomButton.setTextSize(1, 14.0f);
        r0.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        r0.bottomButton.setText(LocaleController.getString("YourEmailSkip", C0446R.string.YourEmailSkip));
        r0.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout = r0.bottomButton;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        linearLayout2.addView(linearLayout, LayoutHelper.createLinear(-1, -2, i | 80, 40, 0, 40, 14));
        r0.bottomButton.setOnClickListener(new C17384());
        if (r0.type == 0) {
            r0.emptyView = new EmptyTextProgressView(context2);
            r0.emptyView.showProgress();
            r0.listView = new RecyclerListView(context2);
            r0.listView.setLayoutManager(new LinearLayoutManager(context2, 1, false));
            r0.listView.setEmptyView(r0.emptyView);
            r0.listView.setVerticalScrollBarEnabled(false);
            frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
            RecyclerListView recyclerListView = r0.listView;
            Adapter listAdapter = new ListAdapter(context2);
            r0.listAdapter = listAdapter;
            recyclerListView.setAdapter(listAdapter);
            r0.listView.setOnItemClickListener(new C23075());
            updateRows();
            r0.actionBar.setTitle(LocaleController.getString("TwoStepVerification", C0446R.string.TwoStepVerification));
            r0.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", C0446R.string.PleaseEnterCurrentPassword));
        } else if (r0.type == 1) {
            setPasswordSetState(r0.passwordSetState);
        }
        return r0.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.didSetTwoStepPassword) {
            if (!(objArr == null || objArr.length <= 0 || objArr[0] == 0)) {
                this.currentPasswordHash = (byte[]) objArr[0];
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.type == 1) {
            AndroidUtilities.runOnUIThread(new C17406(), 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && this.type) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void loadPasswordInfo(final boolean z) {
        if (!z) {
            this.loading = true;
            if (this.listAdapter != null) {
                this.listAdapter.notifyDataSetChanged();
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getPassword(), new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$7$1$1 */
                    class C17411 implements Runnable {
                        C17411() {
                        }

                        public void run() {
                            if (TwoStepVerificationActivity.this.shortPollRunnable != null) {
                                TwoStepVerificationActivity.this.loadPasswordInfo(true);
                                TwoStepVerificationActivity.this.shortPollRunnable = null;
                            }
                        }
                    }

                    public void run() {
                        TwoStepVerificationActivity.this.loading = false;
                        if (tL_error == null) {
                            TwoStepVerificationActivity twoStepVerificationActivity;
                            boolean z = true;
                            if (!z) {
                                boolean z2;
                                twoStepVerificationActivity = TwoStepVerificationActivity.this;
                                if (TwoStepVerificationActivity.this.currentPassword == null) {
                                    if (!(tLObject instanceof TL_account_noPassword)) {
                                        z2 = false;
                                        twoStepVerificationActivity.passwordEntered = z2;
                                    }
                                }
                                z2 = true;
                                twoStepVerificationActivity.passwordEntered = z2;
                            }
                            TwoStepVerificationActivity.this.currentPassword = (account_Password) tLObject;
                            twoStepVerificationActivity = TwoStepVerificationActivity.this;
                            if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
                                z = false;
                            }
                            twoStepVerificationActivity.waitingForEmail = z;
                            Object obj = new byte[(TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8)];
                            Utilities.random.nextBytes(obj);
                            System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, obj, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
                            TwoStepVerificationActivity.this.currentPassword.new_salt = obj;
                        }
                        if (TwoStepVerificationActivity.this.type == 0 && !TwoStepVerificationActivity.this.destroyed && TwoStepVerificationActivity.this.shortPollRunnable == null) {
                            TwoStepVerificationActivity.this.shortPollRunnable = new C17411();
                            AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                        TwoStepVerificationActivity.this.updateRows();
                    }
                });
            }
        }, true);
    }

    private void setPasswordSetState(int i) {
        if (this.passwordEditText != null) {
            this.passwordSetState = i;
            int i2 = 4;
            if (this.passwordSetState == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0446R.string.YourPassword));
                if ((this.currentPassword instanceof TL_account_noPassword) != 0) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", C0446R.string.PleaseEnterFirstPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", C0446R.string.PleaseEnterPassword));
                }
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 1) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", C0446R.string.YourPassword));
                this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", C0446R.string.PleaseReEnterPassword));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 2) {
                this.actionBar.setTitle(LocaleController.getString("PasswordHint", C0446R.string.PasswordHint));
                this.titleTextView.setText(LocaleController.getString("PasswordHintText", C0446R.string.PasswordHintText));
                this.passwordEditText.setImeOptions(5);
                this.passwordEditText.setTransformationMethod(null);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
            } else if (this.passwordSetState == 3) {
                this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", C0446R.string.RecoveryEmail));
                this.titleTextView.setText(LocaleController.getString("YourEmail", C0446R.string.YourEmail));
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                i = this.bottomButton;
                if (!this.emailOnly) {
                    i2 = 0;
                }
                i.setVisibility(i2);
            } else if (this.passwordSetState == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", C0446R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", C0446R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", C0446R.string.RestoreEmailSentInfo));
                this.bottomButton.setText(LocaleController.formatString("RestoreEmailTrouble", C0446R.string.RestoreEmailTrouble, this.currentPassword.email_unconfirmed_pattern));
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
        this.rowCount = 0;
        this.setPasswordRow = -1;
        this.setPasswordDetailRow = -1;
        this.changePasswordRow = -1;
        this.turnPasswordOffRow = -1;
        this.setRecoveryEmailRow = -1;
        this.changeRecoveryEmailRow = -1;
        this.abortPasswordRow = -1;
        this.passwordSetupDetailRow = -1;
        this.passwordEnabledDetailRow = -1;
        this.passwordEmailVerifyDetailRow = -1;
        this.shadowRow = -1;
        if (!(this.loading || this.currentPassword == null)) {
            int i;
            if (this.currentPassword instanceof TL_account_noPassword) {
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordSetupDetailRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.abortPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.shadowRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.setPasswordRow = i;
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.setPasswordDetailRow = i;
                }
            } else if (this.currentPassword instanceof TL_account_password) {
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
                if (this.waitingForEmail) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordEmailVerifyDetailRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.passwordEnabledDetailRow = i;
                }
            }
        }
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        if (this.passwordEntered) {
            if (this.listView != null) {
                this.listView.setVisibility(0);
                this.scrollView.setVisibility(4);
                this.emptyView.setVisibility(0);
                this.listView.setEmptyView(this.emptyView);
            }
            if (this.passwordEditText != null) {
                this.doneItem.setVisibility(8);
                this.passwordEditText.setVisibility(4);
                this.titleTextView.setVisibility(4);
                this.bottomTextView.setVisibility(4);
                this.bottomButton.setVisibility(4);
                return;
            }
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
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", C0446R.string.ForgotPassword));
            if (this.currentPassword.hint == null || this.currentPassword.hint.length() <= 0) {
                this.passwordEditText.setHint(TtmlNode.ANONYMOUS_REGION_ID);
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new C17438(), 200);
        }
    }

    private void needShowProgress() {
        if (!(getParentActivity() == null || getParentActivity().isFinishing())) {
            if (this.progressDialog == null) {
                this.progressDialog = new AlertDialog(getParentActivity(), 1);
                this.progressDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                this.progressDialog.setCanceledOnTouchOutside(false);
                this.progressDialog.setCancelable(false);
                this.progressDialog.show();
            }
        }
    }

    private void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            this.progressDialog = null;
        }
    }

    private boolean isValidEmail(String str) {
        boolean z = false;
        if (str != null) {
            if (str.length() >= 3) {
                int lastIndexOf = str.lastIndexOf(46);
                str = str.lastIndexOf(64);
                if (lastIndexOf >= 0 && str >= null && lastIndexOf >= str) {
                    z = true;
                }
                return z;
            }
        }
        return false;
    }

    private void showAlertWithText(String str, String str2) {
        Builder builder = new Builder(getParentActivity());
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
        builder.setTitle(str);
        builder.setMessage(str2);
        showDialog(builder.create());
    }

    private void setNewPassword(final boolean z) {
        final TLObject tL_account_updatePasswordSettings = new TL_account_updatePasswordSettings();
        tL_account_updatePasswordSettings.current_password_hash = this.currentPasswordHash;
        tL_account_updatePasswordSettings.new_settings = new TL_account_passwordInputSettings();
        if (!z) {
            if (this.firstPassword != null && this.firstPassword.length() > 0) {
                Object obj = null;
                try {
                    obj = this.firstPassword.getBytes(C0542C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                Object obj2 = this.currentPassword.new_salt;
                Object obj3 = new byte[((obj2.length * 2) + obj.length)];
                System.arraycopy(obj2, 0, obj3, 0, obj2.length);
                System.arraycopy(obj, 0, obj3, obj2.length, obj.length);
                System.arraycopy(obj2, 0, obj3, obj3.length - obj2.length, obj2.length);
                TL_account_passwordInputSettings tL_account_passwordInputSettings = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings.flags |= 1;
                tL_account_updatePasswordSettings.new_settings.hint = this.hint;
                tL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(obj3, 0, obj3.length);
                tL_account_updatePasswordSettings.new_settings.new_salt = obj2;
            }
            if (this.email.length() > 0) {
                TL_account_passwordInputSettings tL_account_passwordInputSettings2 = tL_account_updatePasswordSettings.new_settings;
                tL_account_passwordInputSettings2.flags = 2 | tL_account_passwordInputSettings2.flags;
                tL_account_updatePasswordSettings.new_settings.email = this.email.trim();
            }
        } else if (this.waitingForEmail && (this.currentPassword instanceof TL_account_noPassword)) {
            tL_account_updatePasswordSettings.new_settings.flags = 2;
            tL_account_updatePasswordSettings.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
            tL_account_updatePasswordSettings.current_password_hash = new byte[0];
        } else {
            tL_account_updatePasswordSettings.new_settings.flags = 3;
            tL_account_updatePasswordSettings.new_settings.hint = TtmlNode.ANONYMOUS_REGION_ID;
            tL_account_updatePasswordSettings.new_settings.new_password_hash = new byte[0];
            tL_account_updatePasswordSettings.new_settings.new_salt = new byte[0];
            tL_account_updatePasswordSettings.new_settings.email = TtmlNode.ANONYMOUS_REGION_ID;
        }
        needShowProgress();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updatePasswordSettings, new RequestDelegate() {
            public void run(final TLObject tLObject, final TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$9$1$1 */
                    class C17441 implements DialogInterface.OnClickListener {
                        C17441() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            NotificationCenter.getInstance(TwoStepVerificationActivity.this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, tL_account_updatePasswordSettings.new_settings.new_password_hash);
                            TwoStepVerificationActivity.this.finishFragment();
                        }
                    }

                    /* renamed from: org.telegram.ui.TwoStepVerificationActivity$9$1$2 */
                    class C17452 implements DialogInterface.OnClickListener {
                        C17452() {
                        }

                        public void onClick(DialogInterface dialogInterface, int i) {
                            NotificationCenter.getInstance(TwoStepVerificationActivity.this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, tL_account_updatePasswordSettings.new_settings.new_password_hash);
                            TwoStepVerificationActivity.this.finishFragment();
                        }
                    }

                    public void run() {
                        TwoStepVerificationActivity.this.needHideProgress();
                        Builder builder;
                        Dialog showDialog;
                        if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                            if (z) {
                                TwoStepVerificationActivity.this.currentPassword = null;
                                TwoStepVerificationActivity.this.currentPasswordHash = new byte[0];
                                TwoStepVerificationActivity.this.loadPasswordInfo(false);
                                NotificationCenter.getInstance(TwoStepVerificationActivity.this.currentAccount).postNotificationName(NotificationCenter.didRemovedTwoStepPassword, new Object[0]);
                                TwoStepVerificationActivity.this.updateRows();
                            } else if (TwoStepVerificationActivity.this.getParentActivity() != null) {
                                builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C17441());
                                builder.setMessage(LocaleController.getString("YourPasswordSuccessText", C0446R.string.YourPasswordSuccessText));
                                builder.setTitle(LocaleController.getString("YourPasswordSuccess", C0446R.string.YourPasswordSuccess));
                                showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (showDialog != null) {
                                    showDialog.setCanceledOnTouchOutside(false);
                                    showDialog.setCancelable(false);
                                }
                            }
                        } else if (tL_error != null) {
                            if (tL_error.text.equals("EMAIL_UNCONFIRMED")) {
                                builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C17452());
                                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", C0446R.string.YourEmailAlmostThereText));
                                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", C0446R.string.YourEmailAlmostThere));
                                showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (showDialog != null) {
                                    showDialog.setCanceledOnTouchOutside(false);
                                    showDialog.setCancelable(false);
                                }
                            } else if (tL_error.text.equals("EMAIL_INVALID")) {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.getString("PasswordEmailInvalid", C0446R.string.PasswordEmailInvalid));
                            } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                String formatPluralString;
                                int intValue = Utilities.parseInt(tL_error.text).intValue();
                                if (intValue < 60) {
                                    formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                                } else {
                                    formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                                }
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                            } else {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                            }
                        }
                    }
                });
            }
        }, true);
    }

    private void processDone() {
        String obj;
        if (this.type == 0) {
            if (!this.passwordEntered) {
                obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                Object bytes;
                try {
                    bytes = obj.getBytes(C0542C.UTF8_NAME);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                    bytes = null;
                }
                needShowProgress();
                Object obj2 = new byte[((this.currentPassword.current_salt.length * 2) + bytes.length)];
                System.arraycopy(this.currentPassword.current_salt, 0, obj2, 0, this.currentPassword.current_salt.length);
                System.arraycopy(bytes, 0, obj2, this.currentPassword.current_salt.length, bytes.length);
                System.arraycopy(this.currentPassword.current_salt, 0, obj2, obj2.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
                final TLObject tL_account_getPasswordSettings = new TL_account_getPasswordSettings();
                tL_account_getPasswordSettings.current_password_hash = Utilities.computeSHA256(obj2, 0, obj2.length);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_getPasswordSettings, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TwoStepVerificationActivity.this.needHideProgress();
                                if (tL_error == null) {
                                    TwoStepVerificationActivity.this.currentPasswordHash = tL_account_getPasswordSettings.current_password_hash;
                                    TwoStepVerificationActivity.this.passwordEntered = true;
                                    AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                                    TwoStepVerificationActivity.this.updateRows();
                                } else if (tL_error.text.equals("PASSWORD_HASH_INVALID")) {
                                    TwoStepVerificationActivity.this.onPasscodeError(true);
                                } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                    String formatPluralString;
                                    int intValue = Utilities.parseInt(tL_error.text).intValue();
                                    if (intValue < 60) {
                                        formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                                    } else {
                                        formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                                    }
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                                } else {
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                }
                            }
                        });
                    }
                }, 10);
            }
        } else if (this.type == 1) {
            if (this.passwordSetState == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", C0446R.string.ReEnterYourPasscode));
                this.firstPassword = this.passwordEditText.getText().toString();
                setPasswordSetState(1);
            } else if (this.passwordSetState == 1) {
                if (this.firstPassword.equals(this.passwordEditText.getText().toString())) {
                    setPasswordSetState(2);
                } else {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", C0446R.string.PasswordDoNotMatch), 0).show();
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                    onPasscodeError(true);
                }
            } else if (this.passwordSetState == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", C0446R.string.PasswordAsHintError), 0).show();
                    } catch (Throwable e22) {
                        FileLog.m3e(e22);
                    }
                    onPasscodeError(false);
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
                    onPasscodeError(false);
                }
            } else if (this.passwordSetState == 4) {
                obj = this.passwordEditText.getText().toString();
                if (obj.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                TLObject tL_auth_recoverPassword = new TL_auth_recoverPassword();
                tL_auth_recoverPassword.code = obj;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_auth_recoverPassword, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {

                            /* renamed from: org.telegram.ui.TwoStepVerificationActivity$11$1$1 */
                            class C17311 implements DialogInterface.OnClickListener {
                                C17311() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NotificationCenter.getInstance(TwoStepVerificationActivity.this.currentAccount).postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                                    TwoStepVerificationActivity.this.finishFragment();
                                }
                            }

                            public void run() {
                                if (tL_error == null) {
                                    Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C17311());
                                    builder.setMessage(LocaleController.getString("PasswordReset", C0446R.string.PasswordReset));
                                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                    Dialog showDialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                    if (showDialog != null) {
                                        showDialog.setCanceledOnTouchOutside(false);
                                        showDialog.setCancelable(false);
                                    }
                                } else if (tL_error.text.startsWith("CODE_INVALID")) {
                                    TwoStepVerificationActivity.this.onPasscodeError(true);
                                } else if (tL_error.text.startsWith("FLOOD_WAIT")) {
                                    String formatPluralString;
                                    int intValue = Utilities.parseInt(tL_error.text).intValue();
                                    if (intValue < 60) {
                                        formatPluralString = LocaleController.formatPluralString("Seconds", intValue);
                                    } else {
                                        formatPluralString = LocaleController.formatPluralString("Minutes", intValue / 60);
                                    }
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), LocaleController.formatString("FloodWaitTime", C0446R.string.FloodWaitTime, formatPluralString));
                                } else {
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", C0446R.string.AppName), tL_error.text);
                                }
                            }
                        });
                    }
                }, 10);
            }
        }
    }

    private void onPasscodeError(boolean z) {
        if (getParentActivity() != null) {
            Vibrator vibrator = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            if (z) {
                this.passwordEditText.setText(TtmlNode.ANONYMOUS_REGION_ID);
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[21];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class}, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[7] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[9] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[11] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText3);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[14] = new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[15] = new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6);
        themeDescriptionArr[16] = new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText4);
        themeDescriptionArr[17] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[18] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[19] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[20] = new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        return themeDescriptionArr;
    }
}
