package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
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
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    /* access modifiers changed from: private */
    public TextView helpTextView;
    /* access modifiers changed from: private */
    public boolean ignoreCheck;
    /* access modifiers changed from: private */
    public CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
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
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public View createView(Context context) {
        String str;
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (i == 1) {
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ChangeUsernameActivity$OwHEXkVzOQRLF0pIctjxlc9Dcc.INSTANCE);
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", NUM));
        this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChangeUsernameActivity.this.lambda$createView$1$ChangeUsernameActivity(textView, i, keyEvent);
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ChangeUsernameActivity.this.ignoreCheck) {
                    ChangeUsernameActivity changeUsernameActivity = ChangeUsernameActivity.this;
                    boolean unused = changeUsernameActivity.checkUserName(changeUsernameActivity.firstNameField.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (ChangeUsernameActivity.this.firstNameField.length() > 0) {
                    String str = "https://" + MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix + "/" + ChangeUsernameActivity.this.firstNameField.getText();
                    String formatString = LocaleController.formatString("UsernameHelpLink", NUM, str);
                    int indexOf = formatString.indexOf(str);
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                    if (indexOf >= 0) {
                        spannableStringBuilder.setSpan(new LinkSpan(str), indexOf, str.length() + indexOf, 33);
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
        if (!(user == null || (str = user.username) == null || str.length() <= 0)) {
            this.ignoreCheck = true;
            this.firstNameField.setText(user.username);
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            editTextBoldCursor.setSelection(editTextBoldCursor.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ChangeUsernameActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    /* access modifiers changed from: private */
    public boolean checkUserName(String str, boolean z) {
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
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
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
                        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    }
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", NUM));
                        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    }
                    return false;
                } else {
                    i++;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            }
            return false;
        } else if (str.length() > 32) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            }
            return false;
        } else {
            if (!z) {
                String str2 = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                if (str2 == null) {
                    str2 = "";
                }
                if (str.equals(str2)) {
                    this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, str));
                    this.checkTextView.setTag("windowBackgroundWhiteGreenText");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                    return true;
                }
                this.checkTextView.setText(LocaleController.getString("UsernameChecking", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
                this.lastCheckName = str;
                this.checkRunnable = new Runnable(str) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ChangeUsernameActivity.this.lambda$checkUserName$4$ChangeUsernameActivity(this.f$1);
                    }
                };
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUserName$4$ChangeUsernameActivity(String str) {
        TLRPC.TL_account_checkUsername tL_account_checkUsername = new TLRPC.TL_account_checkUsername();
        tL_account_checkUsername.username = str;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_checkUsername, new RequestDelegate(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChangeUsernameActivity.this.lambda$null$3$ChangeUsernameActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$3$ChangeUsernameActivity(String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tL_error, tLObject) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;
            private final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                ChangeUsernameActivity.this.lambda$null$2$ChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$ChangeUsernameActivity(String str, TLRPC.TL_error tL_error, TLObject tLObject) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tL_error != null || !(tLObject instanceof TLRPC.TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.getString("UsernameInUse", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, str));
            this.checkTextView.setTag("windowBackgroundWhiteGreenText");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    public void saveName() {
        if (checkUserName(this.firstNameField.getText().toString(), true)) {
            TLRPC.User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (getParentActivity() != null && currentUser != null) {
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
                TLRPC.TL_account_updateUsername tL_account_updateUsername = new TLRPC.TL_account_updateUsername();
                tL_account_updateUsername.username = obj;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, 1);
                int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateUsername, new RequestDelegate(alertDialog, tL_account_updateUsername) {
                    private final /* synthetic */ AlertDialog f$1;
                    private final /* synthetic */ TLRPC.TL_account_updateUsername f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ChangeUsernameActivity.this.lambda$saveName$7$ChangeUsernameActivity(this.f$1, this.f$2, tLObject, tL_error);
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(sendRequest) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ChangeUsernameActivity.this.lambda$saveName$8$ChangeUsernameActivity(this.f$1, dialogInterface);
                    }
                });
                alertDialog.show();
            }
        }
    }

    public /* synthetic */ void lambda$saveName$7$ChangeUsernameActivity(AlertDialog alertDialog, TLRPC.TL_account_updateUsername tL_account_updateUsername, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable(alertDialog, (TLRPC.User) tLObject) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLRPC.User f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ChangeUsernameActivity.this.lambda$null$5$ChangeUsernameActivity(this.f$1, this.f$2);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(alertDialog, tL_error, tL_account_updateUsername) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ TLRPC.TL_error f$2;
                private final /* synthetic */ TLRPC.TL_account_updateUsername f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    ChangeUsernameActivity.this.lambda$null$6$ChangeUsernameActivity(this.f$1, this.f$2, this.f$3);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$5$ChangeUsernameActivity(AlertDialog alertDialog, TLRPC.User user) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(user);
        MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, (ArrayList<TLRPC.Chat>) null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$6$ChangeUsernameActivity(AlertDialog alertDialog, TLRPC.TL_error tL_error, TLRPC.TL_account_updateUsername tL_account_updateUsername) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
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
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8")};
    }
}
