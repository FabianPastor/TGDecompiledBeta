package org.telegram.p005ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.CLASSNAMEC;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.CLASSNAMER;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChangeBioActivity */
public class ChangeBioActivity extends BaseFragment {
    private static final int done_button = 1;
    private TextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;

    /* renamed from: org.telegram.ui.ChangeBioActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeBioActivity.this.finishFragment();
            } else if (id == 1) {
                ChangeBioActivity.this.saveName();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$3 */
    class CLASSNAME implements TextWatcher {
        CLASSNAME() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ChangeBioActivity.this.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70 - ChangeBioActivity.this.firstNameField.length())}));
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(CLASSNAMER.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("UserBio", CLASSNAMER.string.UserBio));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, CLASSNAMER.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        this.fragmentView = new LinearLayout(context);
        LinearLayout linearLayout = this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(ChangeBioActivity$$Lambda$0.$instance);
        FrameLayout fieldContainer = new FrameLayout(context);
        linearLayout.addView(fieldContainer, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.firstNameField.setMaxLines(4);
        this.firstNameField.setPadding(AndroidUtilities.m9dp(LocaleController.isRTL ? 24.0f : 0.0f), 0, AndroidUtilities.m9dp(LocaleController.isRTL ? 0.0f : 24.0f), AndroidUtilities.m9dp(6.0f));
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setImeOptions(CLASSNAMEC.ENCODING_PCM_MU_LAW);
        this.firstNameField.setInputType(147457);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setFilters(new InputFilter[]{new LengthFilter(70) {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source == null || TextUtils.indexOf(source, 10) == -1) {
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
        this.firstNameField.setMinHeight(AndroidUtilities.m9dp(36.0f));
        this.firstNameField.setHint(LocaleController.getString("UserBio", CLASSNAMER.string.UserBio));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new ChangeBioActivity$$Lambda$1(this));
        this.firstNameField.addTextChangedListener(new CLASSNAME());
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
        this.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", CLASSNAMER.string.UserBioInfo)));
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (!(userFull == null || userFull.about == null)) {
            this.firstNameField.setText(userFull.about);
            this.firstNameField.setSelection(this.firstNameField.length());
        }
        return this.fragmentView;
    }

    final /* synthetic */ boolean lambda$createView$1$ChangeBioActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6 || this.doneButton == null) {
            return false;
        }
        this.doneButton.performClick();
        return true;
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
        if (getParentActivity() != null && userFull != null) {
            String currentName = userFull.about;
            if (currentName == null) {
                currentName = TtmlNode.ANONYMOUS_REGION_ID;
            }
            String newName = this.firstNameField.getText().toString().replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
            if (currentName.equals(newName)) {
                finishFragment();
                return;
            }
            AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
            TL_account_updateProfile req = new TL_account_updateProfile();
            req.about = newName;
            req.flags |= 4;
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChangeBioActivity$$Lambda$2(this, progressDialog, userFull, newName, req), 2);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
            progressDialog.setOnCancelListener(new ChangeBioActivity$$Lambda$3(this, reqId));
            progressDialog.show();
        }
    }

    final /* synthetic */ void lambda$saveName$4$ChangeBioActivity(AlertDialog progressDialog, TL_userFull userFull, String newName, TL_account_updateProfile req, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new ChangeBioActivity$$Lambda$4(this, progressDialog, userFull, newName, (User) response));
            return;
        }
        AndroidUtilities.runOnUIThread(new ChangeBioActivity$$Lambda$5(this, progressDialog, error, req));
    }

    final /* synthetic */ void lambda$null$2$ChangeBioActivity(AlertDialog progressDialog, TL_userFull userFull, String newName, User user) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        userFull.about = newName;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.var_id), userFull, null);
        finishFragment();
    }

    final /* synthetic */ void lambda$null$3$ChangeBioActivity(AlertDialog progressDialog, TL_error error, TL_account_updateProfile req) {
        try {
            progressDialog.dismiss();
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    final /* synthetic */ void lambda$saveName$5$ChangeBioActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
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
