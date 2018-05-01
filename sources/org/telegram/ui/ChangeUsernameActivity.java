package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$2 */
    class C09412 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C09412() {
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$3 */
    class C09423 implements OnEditorActionListener {
        C09423() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeUsernameActivity.this.doneButton == null) {
                return null;
            }
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$4 */
    class C09434 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09434() {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (ChangeUsernameActivity.this.ignoreCheck == null) {
                ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), 0);
            }
        }

        public void afterTextChanged(Editable editable) {
            if (ChangeUsernameActivity.this.firstNameField.length() > null) {
                editable = new StringBuilder();
                editable.append("https://");
                editable.append(MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix);
                editable.append("/");
                editable.append(ChangeUsernameActivity.this.firstNameField.getText());
                editable = editable.toString();
                CharSequence formatString = LocaleController.formatString("UsernameHelpLink", C0446R.string.UsernameHelpLink, editable);
                int indexOf = formatString.indexOf(editable);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                if (indexOf >= 0) {
                    spannableStringBuilder.setSpan(new LinkSpan(editable), indexOf, editable.length() + indexOf, 33);
                }
                ChangeUsernameActivity.this.helpTextView.setText(TextUtils.concat(new CharSequence[]{ChangeUsernameActivity.this.infoText, "\n\n", spannableStringBuilder}));
                return;
            }
            ChangeUsernameActivity.this.helpTextView.setText(ChangeUsernameActivity.this.infoText);
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                textView = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return textView;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return null;
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
                Toast.makeText(ChangeUsernameActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", C0446R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$1 */
    class C19601 extends ActionBarMenuOnItemClick {
        C19601() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeUsernameActivity.this.finishFragment();
            } else if (i == 1) {
                ChangeUsernameActivity.this.saveName();
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", C0446R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new C19601());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
        }
        r0.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = (LinearLayout) r0.fragmentView;
        linearLayout.setOrientation(1);
        r0.fragmentView.setOnTouchListener(new C09412());
        r0.firstNameField = new EditTextBoldCursor(context2);
        r0.firstNameField.setTextSize(1, 18.0f);
        r0.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.firstNameField.setMaxLines(1);
        r0.firstNameField.setLines(1);
        r0.firstNameField.setPadding(0, 0, 0, 0);
        r0.firstNameField.setSingleLine(true);
        r0.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.firstNameField.setInputType(180224);
        r0.firstNameField.setImeOptions(6);
        r0.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", C0446R.string.UsernamePlaceholder));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        r0.firstNameField.setOnEditorActionListener(new C09423());
        r0.firstNameField.addTextChangedListener(new C09434());
        linearLayout.addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        r0.checkTextView = new TextView(context2);
        r0.checkTextView.setTextSize(1, 15.0f);
        r0.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linearLayout.addView(r0.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        r0.helpTextView = new TextView(context2);
        r0.helpTextView.setTextSize(1, 15.0f);
        r0.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        r0.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = r0.helpTextView;
        CharSequence replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", C0446R.string.UsernameHelp));
        r0.infoText = replaceTags;
        textView.setText(replaceTags);
        r0.helpTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        r0.helpTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
        r0.helpTextView.setMovementMethod(new LinkMovementMethodMy());
        linearLayout.addView(r0.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        r0.checkTextView.setVisibility(8);
        if (!(user == null || user.username == null || user.username.length() <= 0)) {
            r0.ignoreCheck = true;
            r0.firstNameField.setText(user.username);
            r0.firstNameField.setSelection(r0.firstNameField.length());
            r0.ignoreCheck = false;
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

    private boolean checkUserName(final String str, boolean z) {
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (z && str.length() == 0) {
            return true;
        }
        if (this.checkRunnable != null) {
            AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (!str.startsWith("_")) {
                if (!str.endsWith("_")) {
                    int i = 0;
                    while (i < str.length()) {
                        char charAt = str.charAt(i);
                        if (i == 0 && charAt >= '0' && charAt <= '9') {
                            if (z) {
                                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", C0446R.string.UsernameInvalidStartNumber));
                            } else {
                                this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", C0446R.string.UsernameInvalidStartNumber));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            }
                            return false;
                        } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                            if (z) {
                                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", C0446R.string.UsernameInvalid));
                            } else {
                                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0446R.string.UsernameInvalid));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            }
                            return false;
                        } else {
                            i++;
                        }
                    }
                }
            }
            this.checkTextView.setText(LocaleController.getString("UsernameInvalid", C0446R.string.UsernameInvalid));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        }
        if (str != null) {
            if (str.length() >= 5) {
                if (str.length() > 32) {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", C0446R.string.UsernameInvalidLong));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", C0446R.string.UsernameInvalidLong));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    }
                    return false;
                }
                if (!z) {
                    z = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                    if (!z) {
                        z = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (str.equals(z)) {
                        this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", C0446R.string.UsernameAvailable, str));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                        return true;
                    }
                    this.checkTextView.setText(LocaleController.getString("UsernameChecking", C0446R.string.UsernameChecking));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                    this.lastCheckName = str;
                    this.checkRunnable = new Runnable() {

                        /* renamed from: org.telegram.ui.ChangeUsernameActivity$5$1 */
                        class C19611 implements RequestDelegate {
                            C19611() {
                            }

                            public void run(final TLObject tLObject, final TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ChangeUsernameActivity.this.checkReqId = 0;
                                        if (ChangeUsernameActivity.this.lastCheckName != null && ChangeUsernameActivity.this.lastCheckName.equals(str)) {
                                            if (tL_error == null && (tLObject instanceof TL_boolTrue)) {
                                                ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", C0446R.string.UsernameAvailable, str));
                                                ChangeUsernameActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                                                ChangeUsernameActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                                                ChangeUsernameActivity.this.lastNameAvailable = true;
                                                return;
                                            }
                                            ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", C0446R.string.UsernameInUse));
                                            ChangeUsernameActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                            ChangeUsernameActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                            ChangeUsernameActivity.this.lastNameAvailable = false;
                                        }
                                    }
                                });
                            }
                        }

                        public void run() {
                            TLObject tL_account_checkUsername = new TL_account_checkUsername();
                            tL_account_checkUsername.username = str;
                            ChangeUsernameActivity.this.checkReqId = ConnectionsManager.getInstance(ChangeUsernameActivity.this.currentAccount).sendRequest(tL_account_checkUsername, new C19611(), 2);
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
                }
                return true;
            }
        }
        if (z) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", C0446R.string.UsernameInvalidShort));
        } else {
            this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", C0446R.string.UsernameInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
        }
        return false;
    }

    private void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (getParentActivity() != null) {
                if (currentUser != null) {
                    String str = currentUser.username;
                    if (str == null) {
                        str = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    String obj = this.firstNameField.getText().toString();
                    if (str.equals(obj)) {
                        finishFragment();
                        return;
                    }
                    final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 1);
                    alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setCancelable(false);
                    final TLObject tL_account_updateUsername = new TL_account_updateUsername();
                    tL_account_updateUsername.username = obj;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                    final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateUsername, new RequestDelegate() {
                        public void run(TLObject tLObject, final TL_error tL_error) {
                            if (tL_error == null) {
                                final User user = (User) tLObject;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        try {
                                            alertDialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        ArrayList arrayList = new ArrayList();
                                        arrayList.add(user);
                                        MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).putUsers(arrayList, false);
                                        MessagesStorage.getInstance(ChangeUsernameActivity.this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                                        UserConfig.getInstance(ChangeUsernameActivity.this.currentAccount).saveConfig(true);
                                        ChangeUsernameActivity.this.finishFragment();
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
                                    AlertsCreator.processError(ChangeUsernameActivity.this.currentAccount, tL_error, ChangeUsernameActivity.this, tL_account_updateUsername, new Object[0]);
                                }
                            });
                        }
                    }, 2);
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
                    alertDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ConnectionsManager.getInstance(ChangeUsernameActivity.this.currentAccount).cancelRequest(sendRequest, true);
                            try {
                                dialogInterface.dismiss();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8)};
    }
}
