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

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("UserBio", R.string.UserBio));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeBioActivity.this.finishFragment();
                } else if (id == 1) {
                    ChangeBioActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        FrameLayout fieldContainer = new FrameLayout(context);
        linearLayout.addView(fieldContainer, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.firstNameField.setMaxLines(4);
        this.firstNameField.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f), 0, AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 24.0f), AndroidUtilities.dp(6.0f));
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setImeOptions(268435456);
        this.firstNameField.setInputType(147457);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setFilters(new InputFilter[]{new LengthFilter(70) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source == null || TextUtils.indexOf(source, '\n') == -1) {
                    CharSequence result = super.filter(source, start, end, dest, dstart, dend);
                    if (result == null || source == null || result.length() == source.length()) {
                        return result;
                    }
                    Vibrator v = (Vibrator) ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                    if (v != null) {
                        v.vibrate(200);
                    }
                    AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView, 2.0f, 0);
                    return result;
                }
                ChangeBioActivity.this.doneButton.performClick();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }});
        this.firstNameField.setMinHeight(AndroidUtilities.dp(36.0f));
        this.firstNameField.setHint(LocaleController.getString("UserBio", R.string.UserBio));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6 || ChangeBioActivity.this.doneButton == null) {
                    return false;
                }
                ChangeBioActivity.this.doneButton.performClick();
                return true;
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                ChangeBioActivity.this.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70 - ChangeBioActivity.this.firstNameField.length())}));
            }
        });
        fieldContainer.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        this.checkTextView = new TextView(context);
        this.checkTextView.setTextSize(1, 15.0f);
        this.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70)}));
        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        fieldContainer.addView(this.checkTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 4.0f, 4.0f, 0.0f));
        this.helpTextView = new TextView(context);
        this.helpTextView.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", R.string.UserBioInfo)));
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TL_userFull userFull = MessagesController.getAccountInstance().getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (!(userFull == null || userFull.about == null)) {
            this.firstNameField.setText(userFull.about);
            this.firstNameField.setSelection(this.firstNameField.length());
        }
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    private void saveName() {
        final TL_userFull userFull = MessagesController.getAccountInstance().getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (getParentActivity() != null && userFull != null) {
            String currentName = userFull.about;
            if (currentName == null) {
                currentName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            final String newName = this.firstNameField.getText().toString().replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
            if (currentName.equals(newName)) {
                finishFragment();
                return;
            }
            final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 1);
            progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            final TL_account_updateProfile req = new TL_account_updateProfile();
            req.about = newName;
            req.flags |= 4;
            final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error == null) {
                        final User user = (User) response;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                                userFull.about = newName;
                                NotificationCenter.getInstance(ChangeBioActivity.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), userFull);
                                ChangeBioActivity.this.finishFragment();
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            try {
                                progressDialog.dismiss();
                            } catch (Throwable e) {
                                FileLog.e(e);
                            }
                            AlertsCreator.processError(ChangeBioActivity.this.currentAccount, error, ChangeBioActivity.this, req, new Object[0]);
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
                        FileLog.e(e);
                    }
                }
            });
            progressDialog.show();
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[11];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[6] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[7] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[8] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[9] = new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8);
        themeDescriptionArr[10] = new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        return themeDescriptionArr;
    }
}
