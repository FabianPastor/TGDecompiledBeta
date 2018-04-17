package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeBioActivity extends BaseFragment {
    private static final int done_button = 1;
    private TextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;

    /* renamed from: org.telegram.ui.ChangeBioActivity$2 */
    class C08922 implements OnTouchListener {
        C08922() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$4 */
    class C08944 implements OnEditorActionListener {
        C08944() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeBioActivity.this.doneButton == null) {
                return false;
            }
            ChangeBioActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$5 */
    class C08955 implements TextWatcher {
        C08955() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ChangeBioActivity.this.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70 - ChangeBioActivity.this.firstNameField.length())}));
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$1 */
    class C19411 extends ActionBarMenuOnItemClick {
        C19411() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeBioActivity.this.finishFragment();
            } else if (id == 1) {
                ChangeBioActivity.this.saveName();
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("UserBio", R.string.UserBio));
        this.actionBar.setActionBarMenuOnItemClick(new C19411());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(new C08922());
        FrameLayout fieldContainer = new FrameLayout(context2);
        linearLayout.addView(fieldContainer, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(4);
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        float f = 0.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f);
        if (!LocaleController.isRTL) {
            f = 24.0f;
        }
        editTextBoldCursor.setPadding(dp, 0, AndroidUtilities.dp(f), AndroidUtilities.dp(6.0f));
        r0.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.firstNameField.setImeOptions(268435456);
        r0.firstNameField.setInputType(147457);
        r0.firstNameField.setImeOptions(6);
        r0.firstNameField.setFilters(new InputFilter[]{new LengthFilter(70) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source == null || TextUtils.indexOf(source, '\n') == -1) {
                    CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                    if (!(result == null || source == null || result.length() == source.length())) {
                        Vibrator v = (Vibrator) ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                        if (v != null) {
                            v.vibrate(200);
                        }
                        AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView, 2.0f, 0);
                    }
                    return result;
                }
                ChangeBioActivity.this.doneButton.performClick();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }});
        r0.firstNameField.setMinHeight(AndroidUtilities.dp(36.0f));
        r0.firstNameField.setHint(LocaleController.getString("UserBio", R.string.UserBio));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        r0.firstNameField.setOnEditorActionListener(new C08944());
        r0.firstNameField.addTextChangedListener(new C08955());
        fieldContainer.addView(r0.firstNameField, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        r0.checkTextView = new TextView(context2);
        r0.checkTextView.setTextSize(1, 15.0f);
        r0.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70)}));
        r0.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        fieldContainer.addView(r0.checkTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 4.0f, 4.0f, 0.0f));
        r0.helpTextView = new TextView(context2);
        r0.helpTextView.setTextSize(1, 15.0f);
        r0.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        r0.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", R.string.UserBioInfo)));
        linearLayout.addView(r0.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TL_userFull userFull = MessagesController.getInstance(r0.currentAccount).getUserFull(UserConfig.getInstance(r0.currentAccount).getClientUserId());
        if (!(userFull == null || userFull.about == null)) {
            r0.firstNameField.setText(userFull.about);
            r0.firstNameField.setSelection(r0.firstNameField.length());
        }
        return r0.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    private void saveName() {
        TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (getParentActivity() != null) {
            if (userFull != null) {
                String currentName = userFull.about;
                if (currentName == null) {
                    currentName = TtmlNode.ANONYMOUS_REGION_ID;
                }
                String newName = this.firstNameField.getText().toString().replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                if (currentName.equals(newName)) {
                    finishFragment();
                    return;
                }
                AlertDialog progressDialog = new AlertDialog(getParentActivity(), 1);
                progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                TL_account_updateProfile req = new TL_account_updateProfile();
                req.about = newName;
                req.flags |= 4;
                final AlertDialog alertDialog = progressDialog;
                final TL_userFull tL_userFull = userFull;
                final String str = newName;
                final TL_account_updateProfile tL_account_updateProfile = req;
                final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        if (error == null) {
                            final User user = (User) response;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        alertDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    tL_userFull.about = str;
                                    NotificationCenter.getInstance(ChangeBioActivity.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), tL_userFull);
                                    ChangeBioActivity.this.finishFragment();
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    alertDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                AlertsCreator.processError(ChangeBioActivity.this.currentAccount, error, ChangeBioActivity.this, tL_account_updateProfile, new Object[0]);
                            }
                        });
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionsManager.getInstance(ChangeBioActivity.this.currentAccount).cancelRequest(reqId, true);
                        try {
                            dialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                progressDialog.show();
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
