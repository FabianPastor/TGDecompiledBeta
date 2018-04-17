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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
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
        C09412() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$3 */
    class C09423 implements OnEditorActionListener {
        C09423() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeUsernameActivity.this.doneButton == null) {
                return false;
            }
            ChangeUsernameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$4 */
    class C09434 implements TextWatcher {
        C09434() {
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!ChangeUsernameActivity.this.ignoreCheck) {
                ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), false);
            }
        }

        public void afterTextChanged(Editable editable) {
            if (ChangeUsernameActivity.this.firstNameField.length() > 0) {
                String url = new StringBuilder();
                url.append("https://");
                url.append(MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix);
                url.append("/");
                url.append(ChangeUsernameActivity.this.firstNameField.getText());
                url = url.toString();
                String text = LocaleController.formatString("UsernameHelpLink", R.string.UsernameHelpLink, url);
                int index = text.indexOf(url);
                SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
                if (index >= 0) {
                    textSpan.setSpan(new LinkSpan(url), index, url.length() + index, 33);
                }
                ChangeUsernameActivity.this.helpTextView.setText(TextUtils.concat(new CharSequence[]{ChangeUsernameActivity.this.infoText, "\n\n", textSpan}));
                return;
            }
            ChangeUsernameActivity.this.helpTextView.setText(ChangeUsernameActivity.this.infoText);
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Throwable e) {
                FileLog.m3e(e);
                return false;
            }
        }
    }

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            this.url = value;
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                Toast.makeText(ChangeUsernameActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", R.string.LinkCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeUsernameActivity$1 */
    class C19601 extends ActionBarMenuOnItemClick {
        C19601() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeUsernameActivity.this.finishFragment();
            } else if (id == 1) {
                ChangeUsernameActivity.this.saveName();
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", R.string.Username));
        this.actionBar.setActionBarMenuOnItemClick(new C19601());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
        }
        r0.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = r0.fragmentView;
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
        r0.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", R.string.UsernamePlaceholder));
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
        CharSequence replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", R.string.UsernameHelp));
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

    private boolean checkUserName(final String name, boolean alert) {
        if (name == null || name.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        if (alert && name.length() == 0) {
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
        if (name != null) {
            if (!name.startsWith("_")) {
                if (!name.endsWith("_")) {
                    int a = 0;
                    while (a < name.length()) {
                        char ch = name.charAt(a);
                        if (a == 0 && ch >= '0' && ch <= '9') {
                            if (alert) {
                                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                            } else {
                                this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", R.string.UsernameInvalidStartNumber));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            }
                            return false;
                        } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                            if (alert) {
                                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                            } else {
                                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
                                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                            }
                            return false;
                        } else {
                            a++;
                        }
                    }
                }
            }
            this.checkTextView.setText(LocaleController.getString("UsernameInvalid", R.string.UsernameInvalid));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        }
        if (name != null) {
            if (name.length() >= 5) {
                if (name.length() > 32) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", R.string.UsernameInvalidLong));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    }
                    return false;
                }
                if (!alert) {
                    String currentName = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                    if (currentName == null) {
                        currentName = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    if (name.equals(currentName)) {
                        this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, name));
                        this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                        this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                        return true;
                    }
                    this.checkTextView.setText(LocaleController.getString("UsernameChecking", R.string.UsernameChecking));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
                    this.lastCheckName = name;
                    this.checkRunnable = new Runnable() {

                        /* renamed from: org.telegram.ui.ChangeUsernameActivity$5$1 */
                        class C19611 implements RequestDelegate {
                            C19611() {
                            }

                            public void run(final TLObject response, final TL_error error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        ChangeUsernameActivity.this.checkReqId = 0;
                                        if (ChangeUsernameActivity.this.lastCheckName != null && ChangeUsernameActivity.this.lastCheckName.equals(name)) {
                                            if (error == null && (response instanceof TL_boolTrue)) {
                                                ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", R.string.UsernameAvailable, name));
                                                ChangeUsernameActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                                                ChangeUsernameActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                                                ChangeUsernameActivity.this.lastNameAvailable = true;
                                                return;
                                            }
                                            ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", R.string.UsernameInUse));
                                            ChangeUsernameActivity.this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                                            ChangeUsernameActivity.this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                                            ChangeUsernameActivity.this.lastNameAvailable = false;
                                        }
                                    }
                                });
                            }
                        }

                        public void run() {
                            TL_account_checkUsername req = new TL_account_checkUsername();
                            req.username = name;
                            ChangeUsernameActivity.this.checkReqId = ConnectionsManager.getInstance(ChangeUsernameActivity.this.currentAccount).sendRequest(req, new C19611(), 2);
                        }
                    };
                    AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
                }
                return true;
            }
        }
        if (alert) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
        } else {
            this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", R.string.UsernameInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
        }
        return false;
    }

    private void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (getParentActivity() != null) {
                if (user != null) {
                    String currentName = user.username;
                    if (currentName == null) {
                        currentName = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    String newName = this.firstNameField.getText().toString();
                    if (currentName.equals(newName)) {
                        finishFragment();
                        return;
                    }
                    final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 1);
                    progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    final TL_account_updateUsername req = new TL_account_updateUsername();
                    req.username = newName;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                    final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, final TL_error error) {
                            if (error == null) {
                                final User user = (User) response;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        try {
                                            progressDialog.dismiss();
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                        ArrayList<User> users = new ArrayList();
                                        users.add(user);
                                        MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).putUsers(users, false);
                                        MessagesStorage.getInstance(ChangeUsernameActivity.this.currentAccount).putUsersAndChats(users, null, false, true);
                                        UserConfig.getInstance(ChangeUsernameActivity.this.currentAccount).saveConfig(true);
                                        ChangeUsernameActivity.this.finishFragment();
                                    }
                                });
                                return;
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    AlertsCreator.processError(ChangeUsernameActivity.this.currentAccount, error, ChangeUsernameActivity.this, req, new Object[0]);
                                }
                            });
                        }
                    }, 2);
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                    progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ConnectionsManager.getInstance(ChangeUsernameActivity.this.currentAccount).cancelRequest(reqId, true);
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
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8)};
    }
}
