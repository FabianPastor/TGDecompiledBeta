package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_checkUsername;
import org.telegram.tgnet.TLRPC.TL_account_updateUsername;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeUsernameActivity extends BaseFragment {
    private static final int done_button = 1;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;
    private boolean ignoreCheck;
    private CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String str) {
            this.url = str;
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        public void onClick(View view) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                Toast.makeText(ChangeUsernameActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (i == 1) {
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(-$$Lambda$ChangeUsernameActivity$OwHEXkVzOQRLF0pIctj-xlc9Dcc.INSTANCE);
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        String str = "windowBackgroundWhiteBlackText";
        this.firstNameField.setTextColor(Theme.getColor(str));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", NUM));
        this.firstNameField.setCursorColor(Theme.getColor(str));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new -$$Lambda$ChangeUsernameActivity$I2mPiyaJW258mybxgiHBR_VofGA(this));
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ChangeUsernameActivity.this.ignoreCheck) {
                    ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                    changeUsernameActivity.checkUserName(changeUsernameActivity.firstNameField.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (ChangeUsernameActivity.this.firstNameField.length() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("https://");
                    stringBuilder.append(MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix);
                    stringBuilder.append("/");
                    stringBuilder.append(ChangeUsernameActivity.this.firstNameField.getText());
                    String stringBuilder2 = stringBuilder.toString();
                    String formatString = LocaleController.formatString("UsernameHelpLink", NUM, stringBuilder2);
                    int indexOf = formatString.indexOf(stringBuilder2);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new LinkSpan(stringBuilder2), indexOf, stringBuilder2.length() + indexOf, 33);
                    }
                    ChangeUsernameActivity.this.helpTextView.setText(TextUtils.concat(new CharSequence[]{ChangeUsernameActivity.this.infoText, "\n\n", spannableStringBuilder}));
                    return;
                }
                ChangeUsernameActivity.this.helpTextView.setText(ChangeUsernameActivity.this.infoText);
            }
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.checkTextView = new TextView(context2);
        this.checkTextView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        this.helpTextView = new TextView(context2);
        this.helpTextView.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = this.helpTextView;
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", NUM));
        this.infoText = replaceTags;
        textView.setText(replaceTags);
        this.helpTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.helpTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
        this.helpTextView.setMovementMethod(new LinkMovementMethodMy());
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.checkTextView.setVisibility(8);
        if (user != null) {
            String str2 = user.username;
            if (str2 != null && str2.length() > 0) {
                this.ignoreCheck = true;
                this.firstNameField.setText(user.username);
                EditTextBoldCursor editTextBoldCursor = this.firstNameField;
                editTextBoldCursor.setSelection(editTextBoldCursor.length());
                this.ignoreCheck = false;
            }
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ChangeUsernameActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 6) {
            View view = this.doneButton;
            if (view != null) {
                view.performClick();
                return true;
            }
        }
        return false;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    private boolean checkUserName(String str, boolean z) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (z && str.length() == 0) {
            return true;
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        String str2 = "windowBackgroundWhiteRedText4";
        if (str != null) {
            String str3 = "_";
            String str4 = "UsernameInvalid";
            if (str.startsWith(str3) || str.endsWith(str3)) {
                this.checkTextView.setText(LocaleController.getString(str4, NUM));
                this.checkTextView.setTag(str2);
                this.checkTextView.setTextColor(Theme.getColor(str2));
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", NUM));
                        this.checkTextView.setTag(str2);
                        this.checkTextView.setTextColor(Theme.getColor(str2));
                    }
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString(str4, NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString(str4, NUM));
                        this.checkTextView.setTag(str2);
                        this.checkTextView.setTextColor(Theme.getColor(str2));
                    }
                    return false;
                } else {
                    i++;
                }
            }
        }
        String str5;
        if (str == null || str.length() < 5) {
            str5 = "UsernameInvalidShort";
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(str5, NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString(str5, NUM));
                this.checkTextView.setTag(str2);
                this.checkTextView.setTextColor(Theme.getColor(str2));
            }
            return false;
        } else if (str.length() > 32) {
            str5 = "UsernameInvalidLong";
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString(str5, NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString(str5, NUM));
                this.checkTextView.setTag(str2);
                this.checkTextView.setTextColor(Theme.getColor(str2));
            }
            return false;
        } else {
            if (!z) {
                Object obj = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                if (obj == null) {
                    obj = "";
                }
                if (str.equals(obj)) {
                    this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, str));
                    String str6 = "windowBackgroundWhiteGreenText";
                    this.checkTextView.setTag(str6);
                    this.checkTextView.setTextColor(Theme.getColor(str6));
                    return true;
                }
                this.checkTextView.setText(LocaleController.getString("UsernameChecking", NUM));
                String str7 = "windowBackgroundWhiteGrayText8";
                this.checkTextView.setTag(str7);
                this.checkTextView.setTextColor(Theme.getColor(str7));
                this.lastCheckName = str;
                this.checkRunnable = new -$$Lambda$ChangeUsernameActivity$a8ZrhEmIlukHAljdlSoaVTMTqDc(this, str);
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$4$ChangeUsernameActivity(String str) {
        TL_account_checkUsername tL_account_checkUsername = new TL_account_checkUsername();
        tL_account_checkUsername.username = str;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_checkUsername, new -$$Lambda$ChangeUsernameActivity$E2MlLRNMSBRM9AKBv43t22lAhfU(this, str), 2);
    }

    public /* synthetic */ void lambda$null$3$ChangeUsernameActivity(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChangeUsernameActivity$Lqd5P0eUXkJ-zO6dWug8-uUp7XY(this, str, tL_error, tLObject));
    }

    public /* synthetic */ void lambda$null$2$ChangeUsernameActivity(String str, TL_error tL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            String str3;
            if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, str));
                str3 = "windowBackgroundWhiteGreenText";
                this.checkTextView.setTag(str3);
                this.checkTextView.setTextColor(Theme.getColor(str3));
                this.lastNameAvailable = true;
                return;
            }
            this.checkTextView.setText(LocaleController.getString("UsernameInUse", NUM));
            str3 = "windowBackgroundWhiteRedText4";
            this.checkTextView.setTag(str3);
            this.checkTextView.setTextColor(Theme.getColor(str3));
            this.lastNameAvailable = false;
        }
    }

    private void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (!(getParentActivity() == null || currentUser == null)) {
                String str = currentUser.username;
                if (str == null) {
                    str = "";
                }
                String obj = this.firstNameField.getText().toString();
                if (str.equals(obj)) {
                    finishFragment();
                    return;
                }
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                TL_account_updateUsername tL_account_updateUsername = new TL_account_updateUsername();
                tL_account_updateUsername.username = obj;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateUsername, new -$$Lambda$ChangeUsernameActivity$v8ZSRwHtLRMnSL9d8TsxyGq5Q58(this, alertDialog, tL_account_updateUsername), 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
                alertDialog.setOnCancelListener(new -$$Lambda$ChangeUsernameActivity$gFWNPcfawPg-VyQnhopqwvS2g8Q(this, sendRequest));
                alertDialog.show();
            }
        }
    }

    public /* synthetic */ void lambda$saveName$7$ChangeUsernameActivity(AlertDialog alertDialog, TL_account_updateUsername tL_account_updateUsername, TLObject tLObject, TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChangeUsernameActivity$iXI1HI9G_hbgkQiH6y4X4YZM9HI(this, alertDialog, (User) tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ChangeUsernameActivity$dxm9wNh1IKlvP_EniBN7iVOIWGY(this, alertDialog, tL_error, tL_account_updateUsername));
        }
    }

    public /* synthetic */ void lambda$null$5$ChangeUsernameActivity(AlertDialog alertDialog, User user) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(user);
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$6$ChangeUsernameActivity(AlertDialog alertDialog, TL_error tL_error, TL_account_updateUsername tL_account_updateUsername) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_updateUsername, new Object[0]);
    }

    public /* synthetic */ void lambda$saveName$8$ChangeUsernameActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8")};
    }
}
