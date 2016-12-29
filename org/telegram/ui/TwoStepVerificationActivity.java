package org.telegram.ui;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.ExoPlayerFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

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
    private String firstPassword;
    private String hint;
    private ListAdapter listAdapter;
    private ListView listView;
    private boolean loading;
    private EditText passwordEditText;
    private int passwordEmailVerifyDetailRow;
    private int passwordEnabledDetailRow;
    private boolean passwordEntered = true;
    private int passwordSetState;
    private int passwordSetupDetailRow;
    private ProgressDialog progressDialog;
    private FrameLayout progressView;
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

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int i) {
            return (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) ? false : true;
        }

        public int getCount() {
            return (TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null) ? 0 : TwoStepVerificationActivity.this.rowCount;
        }

        public Object getItem(int i) {
            return null;
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int viewType = getItemViewType(i);
            if (viewType == 0) {
                if (view == null) {
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(-1);
                }
                TextSettingsCell textCell = (TextSettingsCell) view;
                textCell.setTextColor(-14606047);
                if (i == TwoStepVerificationActivity.this.changePasswordRow) {
                    textCell.setText(LocaleController.getString("ChangePassword", R.string.ChangePassword), true);
                } else if (i == TwoStepVerificationActivity.this.setPasswordRow) {
                    textCell.setText(LocaleController.getString("SetAdditionalPassword", R.string.SetAdditionalPassword), true);
                } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow) {
                    textCell.setText(LocaleController.getString("TurnPasswordOff", R.string.TurnPasswordOff), true);
                } else if (i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                    textCell.setText(LocaleController.getString("ChangeRecoveryEmail", R.string.ChangeRecoveryEmail), TwoStepVerificationActivity.this.abortPasswordRow != -1);
                } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow) {
                    textCell.setText(LocaleController.getString("SetRecoveryEmail", R.string.SetRecoveryEmail), false);
                } else if (i == TwoStepVerificationActivity.this.abortPasswordRow) {
                    textCell.setTextColor(-2995895);
                    textCell.setText(LocaleController.getString("AbortPassword", R.string.AbortPassword), false);
                }
            } else if (viewType == 1) {
                if (view == null) {
                    view = new TextInfoPrivacyCell(this.mContext);
                }
                if (i == TwoStepVerificationActivity.this.setPasswordDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("SetAdditionalPasswordInfo", R.string.SetAdditionalPasswordInfo));
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.shadowRow) {
                    ((TextInfoPrivacyCell) view).setText("");
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.passwordSetupDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.formatString("EmailPasswordConfirmText", R.string.EmailPasswordConfirmText, TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern));
                    view.setBackgroundResource(R.drawable.greydivider_top);
                } else if (i == TwoStepVerificationActivity.this.passwordEnabledDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.getString("EnabledPasswordText", R.string.EnabledPasswordText));
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                } else if (i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                    ((TextInfoPrivacyCell) view).setText(LocaleController.formatString("PendingEmailText", R.string.PendingEmailText, TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern));
                    view.setBackgroundResource(R.drawable.greydivider_bottom);
                }
            }
            return view;
        }

        public int getItemViewType(int i) {
            if (i == TwoStepVerificationActivity.this.setPasswordDetailRow || i == TwoStepVerificationActivity.this.shadowRow || i == TwoStepVerificationActivity.this.passwordSetupDetailRow || i == TwoStepVerificationActivity.this.passwordEnabledDetailRow || i == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) {
                return 1;
            }
            return 0;
        }

        public int getViewTypeCount() {
            return 2;
        }

        public boolean isEmpty() {
            return TwoStepVerificationActivity.this.loading || TwoStepVerificationActivity.this.currentPassword == null;
        }
    }

    public TwoStepVerificationActivity(int type) {
        this.type = type;
        if (type == 0) {
            loadPasswordInfo(false);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        updateRows();
        if (this.type == 0) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.type == 0) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
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
                FileLog.e("tmessages", e);
            }
            this.progressDialog = null;
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
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
        frameLayout.setBackgroundColor(Theme.ACTION_BAR_MODE_SELECTOR_COLOR);
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        frameLayout.addView(this.scrollView);
        LayoutParams layoutParams = (LayoutParams) this.scrollView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.scrollView.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.scrollView.addView(linearLayout);
        LayoutParams layoutParams2 = (LayoutParams) linearLayout.getLayoutParams();
        layoutParams2.width = -1;
        layoutParams2.height = -2;
        linearLayout.setLayoutParams(layoutParams2);
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
        this.titleTextView.setTextSize(1, 18.0f);
        this.titleTextView.setGravity(1);
        linearLayout.addView(this.titleTextView);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.titleTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = 1;
        layoutParams3.topMargin = AndroidUtilities.dp(38.0f);
        this.titleTextView.setLayoutParams(layoutParams3);
        this.passwordEditText = new EditText(context);
        this.passwordEditText.setTextSize(1, 20.0f);
        this.passwordEditText.setTextColor(-16777216);
        this.passwordEditText.setMaxLines(1);
        this.passwordEditText.setLines(1);
        this.passwordEditText.setGravity(1);
        this.passwordEditText.setSingleLine(true);
        this.passwordEditText.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.passwordEditText.setTypeface(Typeface.DEFAULT);
        AndroidUtilities.clearCursorDrawable(this.passwordEditText);
        linearLayout.addView(this.passwordEditText);
        layoutParams3 = (LinearLayout.LayoutParams) this.passwordEditText.getLayoutParams();
        layoutParams3.topMargin = AndroidUtilities.dp(32.0f);
        layoutParams3.height = AndroidUtilities.dp(36.0f);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.gravity = 51;
        layoutParams3.width = -1;
        this.passwordEditText.setLayoutParams(layoutParams3);
        this.passwordEditText.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 5 && i != 6) {
                    return false;
                }
                TwoStepVerificationActivity.this.processDone();
                return true;
            }
        });
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
        this.bottomTextView.setTextColor(Theme.ATTACH_SHEET_TEXT_COLOR);
        this.bottomTextView.setTextSize(1, 14.0f);
        this.bottomTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", R.string.YourEmailInfo));
        linearLayout.addView(this.bottomTextView);
        layoutParams3 = (LinearLayout.LayoutParams) this.bottomTextView.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        layoutParams3.topMargin = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomTextView.setLayoutParams(layoutParams3);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(80);
        linearLayout.addView(linearLayout2);
        layoutParams3 = (LinearLayout.LayoutParams) linearLayout2.getLayoutParams();
        layoutParams3.width = -1;
        layoutParams3.height = -1;
        linearLayout2.setLayoutParams(layoutParams3);
        this.bottomButton = new TextView(context);
        this.bottomButton.setTextColor(-11697229);
        this.bottomButton.setTextSize(1, 14.0f);
        this.bottomButton.setGravity((LocaleController.isRTL ? 5 : 3) | 80);
        this.bottomButton.setText(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip));
        this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0f), 0, 0);
        linearLayout2.addView(this.bottomButton);
        layoutParams3 = (LinearLayout.LayoutParams) this.bottomButton.getLayoutParams();
        layoutParams3.width = -2;
        layoutParams3.height = -2;
        layoutParams3.gravity = (LocaleController.isRTL ? 5 : 3) | 80;
        layoutParams3.bottomMargin = AndroidUtilities.dp(14.0f);
        layoutParams3.leftMargin = AndroidUtilities.dp(40.0f);
        layoutParams3.rightMargin = AndroidUtilities.dp(40.0f);
        this.bottomButton.setLayoutParams(layoutParams3);
        this.bottomButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TwoStepVerificationActivity.this.type == 0) {
                    if (TwoStepVerificationActivity.this.currentPassword.has_recovery) {
                        TwoStepVerificationActivity.this.needShowProgress();
                        ConnectionsManager.getInstance().sendRequest(new TL_auth_requestPasswordRecovery(), new RequestDelegate() {
                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        TwoStepVerificationActivity.this.needHideProgress();
                                        if (error == null) {
                                            final TL_auth_passwordRecovery res = response;
                                            Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                            builder.setMessage(LocaleController.formatString("RestoreEmailSent", R.string.RestoreEmailSent, res.email_pattern));
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    TwoStepVerificationActivity fragment = new TwoStepVerificationActivity(1);
                                                    fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                                                    fragment.currentPassword.email_unconfirmed_pattern = res.email_pattern;
                                                    fragment.passwordSetState = 4;
                                                    TwoStepVerificationActivity.this.presentFragment(fragment);
                                                }
                                            });
                                            Dialog dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
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
                                            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                        } else {
                                            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                                        }
                                    }
                                });
                            }
                        }, 10);
                        return;
                    }
                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestorePasswordNoEmailText", R.string.RestorePasswordNoEmailText));
                } else if (TwoStepVerificationActivity.this.passwordSetState == 4) {
                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle), LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText));
                } else {
                    Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("YourEmailSkipWarningText", R.string.YourEmailSkipWarningText));
                    builder.setTitle(LocaleController.getString("YourEmailSkipWarning", R.string.YourEmailSkipWarning));
                    builder.setPositiveButton(LocaleController.getString("YourEmailSkip", R.string.YourEmailSkip), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            TwoStepVerificationActivity.this.email = "";
                            TwoStepVerificationActivity.this.setNewPassword(false);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    TwoStepVerificationActivity.this.showDialog(builder.create());
                }
            }
        });
        if (this.type == 0) {
            this.progressView = new FrameLayout(context);
            frameLayout.addView(this.progressView);
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            this.progressView.setLayoutParams(layoutParams);
            this.progressView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            this.progressView.addView(new ProgressBar(context));
            layoutParams = (LayoutParams) this.progressView.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -2;
            layoutParams.gravity = 17;
            this.progressView.setLayoutParams(layoutParams);
            this.listView = new ListView(context);
            this.listView.setDivider(null);
            this.listView.setEmptyView(this.progressView);
            this.listView.setDividerHeight(0);
            this.listView.setVerticalScrollBarEnabled(false);
            this.listView.setDrawSelectorOnTop(true);
            frameLayout.addView(this.listView);
            layoutParams = (LayoutParams) this.listView.getLayoutParams();
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.gravity = 48;
            this.listView.setLayoutParams(layoutParams);
            ListView listView = this.listView;
            android.widget.ListAdapter listAdapter = new ListAdapter(context);
            this.listAdapter = listAdapter;
            listView.setAdapter(listAdapter);
            this.listView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TwoStepVerificationActivity fragment;
                    if (i == TwoStepVerificationActivity.this.setPasswordRow || i == TwoStepVerificationActivity.this.changePasswordRow) {
                        fragment = new TwoStepVerificationActivity(1);
                        fragment.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                        fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                        TwoStepVerificationActivity.this.presentFragment(fragment);
                    } else if (i == TwoStepVerificationActivity.this.setRecoveryEmailRow || i == TwoStepVerificationActivity.this.changeRecoveryEmailRow) {
                        fragment = new TwoStepVerificationActivity(1);
                        fragment.currentPasswordHash = TwoStepVerificationActivity.this.currentPasswordHash;
                        fragment.currentPassword = TwoStepVerificationActivity.this.currentPassword;
                        fragment.emailOnly = true;
                        fragment.passwordSetState = 3;
                        TwoStepVerificationActivity.this.presentFragment(fragment);
                    } else if (i == TwoStepVerificationActivity.this.turnPasswordOffRow || i == TwoStepVerificationActivity.this.abortPasswordRow) {
                        Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("TurnPasswordOffQuestion", R.string.TurnPasswordOffQuestion));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TwoStepVerificationActivity.this.setNewPassword(true);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        TwoStepVerificationActivity.this.showDialog(builder.create());
                    }
                }
            });
            updateRows();
            this.actionBar.setTitle(LocaleController.getString("TwoStepVerification", R.string.TwoStepVerification));
            this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", R.string.PleaseEnterCurrentPassword));
        } else if (this.type == 1) {
            setPasswordSetState(this.passwordSetState);
        }
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.didSetTwoStepPassword) {
            if (!(args == null || args.length <= 0 || args[0] == null)) {
                this.currentPasswordHash = (byte[]) args[0];
            }
            loadPasswordInfo(false);
            updateRows();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.type == 1) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (TwoStepVerificationActivity.this.passwordEditText != null) {
                        TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                        AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                    }
                }
            }, 200);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.type == 1) {
            AndroidUtilities.showKeyboard(this.passwordEditText);
        }
    }

    private void loadPasswordInfo(final boolean silent) {
        if (!silent) {
            this.loading = true;
            this.listAdapter.notifyDataSetChanged();
        }
        ConnectionsManager.getInstance().sendRequest(new TL_account_getPassword(), new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        boolean z = true;
                        TwoStepVerificationActivity.this.loading = false;
                        if (error == null) {
                            if (!silent) {
                                TwoStepVerificationActivity twoStepVerificationActivity = TwoStepVerificationActivity.this;
                                boolean z2 = TwoStepVerificationActivity.this.currentPassword != null || (response instanceof TL_account_noPassword);
                                twoStepVerificationActivity.passwordEntered = z2;
                            }
                            TwoStepVerificationActivity.this.currentPassword = (account_Password) response;
                            TwoStepVerificationActivity twoStepVerificationActivity2 = TwoStepVerificationActivity.this;
                            if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0) {
                                z = false;
                            }
                            twoStepVerificationActivity2.waitingForEmail = z;
                            byte[] salt = new byte[(TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8)];
                            Utilities.random.nextBytes(salt);
                            System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, salt, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
                            TwoStepVerificationActivity.this.currentPassword.new_salt = salt;
                        }
                        if (TwoStepVerificationActivity.this.type == 0 && !TwoStepVerificationActivity.this.destroyed && TwoStepVerificationActivity.this.shortPollRunnable == null) {
                            TwoStepVerificationActivity.this.shortPollRunnable = new Runnable() {
                                public void run() {
                                    if (TwoStepVerificationActivity.this.shortPollRunnable != null) {
                                        TwoStepVerificationActivity.this.loadPasswordInfo(true);
                                        TwoStepVerificationActivity.this.shortPollRunnable = null;
                                    }
                                }
                            };
                            AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, ExoPlayerFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
                        }
                        TwoStepVerificationActivity.this.updateRows();
                    }
                });
            }
        }, 10);
    }

    private void setPasswordSetState(int state) {
        int i = 4;
        if (this.passwordEditText != null) {
            this.passwordSetState = state;
            if (this.passwordSetState == 0) {
                this.actionBar.setTitle(LocaleController.getString("YourPassword", R.string.YourPassword));
                if (this.currentPassword instanceof TL_account_noPassword) {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", R.string.PleaseEnterFirstPassword));
                } else {
                    this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", R.string.PleaseEnterPassword));
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
                this.passwordEditText.setImeOptions(6);
                this.passwordEditText.setTransformationMethod(null);
                this.passwordEditText.setInputType(33);
                this.bottomTextView.setVisibility(0);
                TextView textView = this.bottomButton;
                if (!this.emailOnly) {
                    i = 0;
                }
                textView.setVisibility(i);
            } else if (this.passwordSetState == 4) {
                this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", R.string.PasswordRecovery));
                this.titleTextView.setText(LocaleController.getString("PasswordCode", R.string.PasswordCode));
                this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", R.string.RestoreEmailSentInfo));
                this.bottomButton.setText(LocaleController.formatString("RestoreEmailTrouble", R.string.RestoreEmailTrouble, this.currentPassword.email_unconfirmed_pattern));
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
                this.progressView.setVisibility(0);
                this.listView.setEmptyView(this.progressView);
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
            this.progressView.setVisibility(4);
        }
        if (this.passwordEditText != null) {
            this.doneItem.setVisibility(0);
            this.passwordEditText.setVisibility(0);
            this.titleTextView.setVisibility(0);
            this.bottomButton.setVisibility(0);
            this.bottomTextView.setVisibility(4);
            this.bottomButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            if (this.currentPassword.hint == null || this.currentPassword.hint.length() <= 0) {
                this.passwordEditText.setHint("");
            } else {
                this.passwordEditText.setHint(this.currentPassword.hint);
            }
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (TwoStepVerificationActivity.this.passwordEditText != null) {
                        TwoStepVerificationActivity.this.passwordEditText.requestFocus();
                        AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                    }
                }
            }, 200);
        }
    }

    private void needShowProgress() {
        if (getParentActivity() != null && !getParentActivity().isFinishing() && this.progressDialog == null) {
            this.progressDialog = new ProgressDialog(getParentActivity());
            this.progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }
    }

    private void needHideProgress() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e("tmessages", e);
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

    private void setNewPassword(final boolean clear) {
        final TL_account_updatePasswordSettings req = new TL_account_updatePasswordSettings();
        req.current_password_hash = this.currentPasswordHash;
        req.new_settings = new TL_account_passwordInputSettings();
        if (!clear) {
            TL_account_passwordInputSettings tL_account_passwordInputSettings;
            if (this.firstPassword != null && this.firstPassword.length() > 0) {
                byte[] newPasswordBytes = null;
                try {
                    newPasswordBytes = this.firstPassword.getBytes("UTF-8");
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                byte[] new_salt = this.currentPassword.new_salt;
                byte[] hash = new byte[((new_salt.length * 2) + newPasswordBytes.length)];
                System.arraycopy(new_salt, 0, hash, 0, new_salt.length);
                System.arraycopy(newPasswordBytes, 0, hash, new_salt.length, newPasswordBytes.length);
                System.arraycopy(new_salt, 0, hash, hash.length - new_salt.length, new_salt.length);
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 1;
                req.new_settings.hint = this.hint;
                req.new_settings.new_password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                req.new_settings.new_salt = new_salt;
            }
            if (this.email.length() > 0) {
                tL_account_passwordInputSettings = req.new_settings;
                tL_account_passwordInputSettings.flags |= 2;
                req.new_settings.email = this.email;
            }
        } else if (this.waitingForEmail && (this.currentPassword instanceof TL_account_noPassword)) {
            req.new_settings.flags = 2;
            req.new_settings.email = "";
            req.current_password_hash = new byte[0];
        } else {
            req.new_settings.flags = 3;
            req.new_settings.hint = "";
            req.new_settings.new_password_hash = new byte[0];
            req.new_settings.new_salt = new byte[0];
            req.new_settings.email = "";
        }
        needShowProgress();
        ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
            public void run(final TLObject response, final TL_error error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        TwoStepVerificationActivity.this.needHideProgress();
                        Builder builder;
                        Dialog dialog;
                        if (error == null && (response instanceof TL_boolTrue)) {
                            if (clear) {
                                TwoStepVerificationActivity.this.currentPassword = null;
                                TwoStepVerificationActivity.this.currentPasswordHash = new byte[0];
                                TwoStepVerificationActivity.this.loadPasswordInfo(false);
                                TwoStepVerificationActivity.this.updateRows();
                            } else if (TwoStepVerificationActivity.this.getParentActivity() != null) {
                                builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, req.new_settings.new_password_hash);
                                        TwoStepVerificationActivity.this.finishFragment();
                                    }
                                });
                                builder.setMessage(LocaleController.getString("YourPasswordSuccessText", R.string.YourPasswordSuccessText));
                                builder.setTitle(LocaleController.getString("YourPasswordSuccess", R.string.YourPasswordSuccess));
                                dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            }
                        } else if (error == null) {
                        } else {
                            if (error.text.equals("EMAIL_UNCONFIRMED")) {
                                builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, req.new_settings.new_password_hash);
                                        TwoStepVerificationActivity.this.finishFragment();
                                    }
                                });
                                builder.setMessage(LocaleController.getString("YourEmailAlmostThereText", R.string.YourEmailAlmostThereText));
                                builder.setTitle(LocaleController.getString("YourEmailAlmostThere", R.string.YourEmailAlmostThere));
                                dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                if (dialog != null) {
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                }
                            } else if (error.text.equals("EMAIL_INVALID")) {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.getString("PasswordEmailInvalid", R.string.PasswordEmailInvalid));
                            } else if (error.text.startsWith("FLOOD_WAIT")) {
                                String timeString;
                                int time = Utilities.parseInt(error.text).intValue();
                                if (time < 60) {
                                    timeString = LocaleController.formatPluralString("Seconds", time);
                                } else {
                                    timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                }
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                            } else {
                                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                            }
                        }
                    }
                });
            }
        }, 10);
    }

    private void processDone() {
        if (this.type == 0) {
            if (!this.passwordEntered) {
                String oldPassword = this.passwordEditText.getText().toString();
                if (oldPassword.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                byte[] oldPasswordBytes = null;
                try {
                    oldPasswordBytes = oldPassword.getBytes("UTF-8");
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                }
                needShowProgress();
                byte[] hash = new byte[((this.currentPassword.current_salt.length * 2) + oldPasswordBytes.length)];
                System.arraycopy(this.currentPassword.current_salt, 0, hash, 0, this.currentPassword.current_salt.length);
                System.arraycopy(oldPasswordBytes, 0, hash, this.currentPassword.current_salt.length, oldPasswordBytes.length);
                System.arraycopy(this.currentPassword.current_salt, 0, hash, hash.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
                final TL_account_getPasswordSettings req = new TL_account_getPasswordSettings();
                req.current_password_hash = Utilities.computeSHA256(hash, 0, hash.length);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TwoStepVerificationActivity.this.needHideProgress();
                                if (error == null) {
                                    TwoStepVerificationActivity.this.currentPasswordHash = req.current_password_hash;
                                    TwoStepVerificationActivity.this.passwordEntered = true;
                                    AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                                    TwoStepVerificationActivity.this.updateRows();
                                } else if (error.text.equals("PASSWORD_HASH_INVALID")) {
                                    TwoStepVerificationActivity.this.onPasscodeError(true);
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    String timeString;
                                    int time = Utilities.parseInt(error.text).intValue();
                                    if (time < 60) {
                                        timeString = LocaleController.formatPluralString("Seconds", time);
                                    } else {
                                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                    }
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            }
                        });
                    }
                }, 10);
            }
        } else if (this.type != 1) {
        } else {
            if (this.passwordSetState == 0) {
                if (this.passwordEditText.getText().length() == 0) {
                    onPasscodeError(false);
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
                } catch (Throwable e2) {
                    FileLog.e("tmessages", e2);
                }
                onPasscodeError(true);
            } else if (this.passwordSetState == 2) {
                this.hint = this.passwordEditText.getText().toString();
                if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase())) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", R.string.PasswordAsHintError), 0).show();
                    } catch (Throwable e22) {
                        FileLog.e("tmessages", e22);
                    }
                    onPasscodeError(false);
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
                    onPasscodeError(false);
                }
            } else if (this.passwordSetState == 4) {
                String code = this.passwordEditText.getText().toString();
                if (code.length() == 0) {
                    onPasscodeError(false);
                    return;
                }
                TL_auth_recoverPassword req2 = new TL_auth_recoverPassword();
                req2.code = code;
                ConnectionsManager.getInstance().sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (error == null) {
                                    Builder builder = new Builder(TwoStepVerificationActivity.this.getParentActivity());
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                                            TwoStepVerificationActivity.this.finishFragment();
                                        }
                                    });
                                    builder.setMessage(LocaleController.getString("PasswordReset", R.string.PasswordReset));
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    Dialog dialog = TwoStepVerificationActivity.this.showDialog(builder.create());
                                    if (dialog != null) {
                                        dialog.setCanceledOnTouchOutside(false);
                                        dialog.setCancelable(false);
                                    }
                                } else if (error.text.startsWith("CODE_INVALID")) {
                                    TwoStepVerificationActivity.this.onPasscodeError(true);
                                } else if (error.text.startsWith("FLOOD_WAIT")) {
                                    String timeString;
                                    int time = Utilities.parseInt(error.text).intValue();
                                    if (time < 60) {
                                        timeString = LocaleController.formatPluralString("Seconds", time);
                                    } else {
                                        timeString = LocaleController.formatPluralString("Minutes", time / 60);
                                    }
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                                } else {
                                    TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", R.string.AppName), error.text);
                                }
                            }
                        });
                    }
                }, 10);
            }
        }
    }

    private void onPasscodeError(boolean clear) {
        if (getParentActivity() != null) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200);
            }
            if (clear) {
                this.passwordEditText.setText("");
            }
            AndroidUtilities.shakeView(this.titleTextView, 2.0f, 0);
        }
    }
}
