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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
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
                if (BulletinFactory.canShowBulletin(ChangeUsernameActivity.this)) {
                    BulletinFactory.createCopyLinkBulletin((BaseFragment) ChangeUsernameActivity.this).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
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
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Username", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ChangeUsernameActivity.this.finishFragment();
                } else if (id == 1) {
                    ChangeUsernameActivity.this.saveName();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", NUM));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        this.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(ChangeUsernameActivity$$ExternalSyntheticLambda1.INSTANCE);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable((Drawable) null);
        this.firstNameField.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
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
        this.firstNameField.setOnEditorActionListener(new ChangeUsernameActivity$$ExternalSyntheticLambda2(this));
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
                String name = ChangeUsernameActivity.this.firstNameField.getText().toString();
                if (name.startsWith("@")) {
                    name = name.substring(1);
                }
                if (name.length() > 0) {
                    String url = "https://" + MessagesController.getInstance(ChangeUsernameActivity.this.currentAccount).linkPrefix + "/" + name;
                    String text = LocaleController.formatString("UsernameHelpLink", NUM, url);
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
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.checkTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        linearLayout.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 12, 24, 0));
        TextView textView2 = new TextView(context2);
        this.helpTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView3 = this.helpTextView;
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", NUM));
        this.infoText = replaceTags;
        textView3.setText(replaceTags);
        this.helpTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.helpTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
        this.helpTextView.setMovementMethod(new LinkMovementMethodMy());
        linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        this.checkTextView.setVisibility(8);
        if (!(user == null || user.username == null || user.username.length() <= 0)) {
            this.ignoreCheck = true;
            this.firstNameField.setText(user.username);
            EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ boolean m1575lambda$createView$1$orgtelegramuiChangeUsernameActivity(TextView textView, int i, KeyEvent keyEvent) {
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
    public boolean checkUserName(String name, boolean alert) {
        if (name != null && name.startsWith("@")) {
            name = name.substring(1);
        }
        if (!TextUtils.isEmpty(name)) {
            this.checkTextView.setVisibility(0);
        } else {
            this.checkTextView.setVisibility(8);
        }
        if (alert && name.length() == 0) {
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
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalid", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                return false;
            }
            int a = 0;
            while (a < name.length()) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", NUM));
                        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", NUM));
                    } else {
                        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", NUM));
                        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                    }
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (name == null || name.length() < 5) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            }
            return false;
        } else if (name.length() > 32) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", NUM));
            } else {
                this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
            }
            return false;
        } else {
            if (!alert) {
                String currentName = UserConfig.getInstance(this.currentAccount).getCurrentUser().username;
                if (currentName == null) {
                    currentName = "";
                }
                if (name.equals(currentName)) {
                    this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, name));
                    this.checkTextView.setTag("windowBackgroundWhiteGreenText");
                    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                    return true;
                }
                this.checkTextView.setText(LocaleController.getString("UsernameChecking", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
                this.lastCheckName = name;
                ChangeUsernameActivity$$ExternalSyntheticLambda3 changeUsernameActivity$$ExternalSyntheticLambda3 = new ChangeUsernameActivity$$ExternalSyntheticLambda3(this, name);
                this.checkRunnable = changeUsernameActivity$$ExternalSyntheticLambda3;
                AndroidUtilities.runOnUIThread(changeUsernameActivity$$ExternalSyntheticLambda3, 300);
            }
            return true;
        }
    }

    /* renamed from: lambda$checkUserName$4$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1574lambda$checkUserName$4$orgtelegramuiChangeUsernameActivity(String nameFinal) {
        TLRPC.TL_account_checkUsername req = new TLRPC.TL_account_checkUsername();
        req.username = nameFinal;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChangeUsernameActivity$$ExternalSyntheticLambda7(this, nameFinal), 2);
    }

    /* renamed from: lambda$checkUserName$3$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1573lambda$checkUserName$3$orgtelegramuiChangeUsernameActivity(String nameFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ChangeUsernameActivity$$ExternalSyntheticLambda4(this, nameFinal, error, response));
    }

    /* renamed from: lambda$checkUserName$2$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1572lambda$checkUserName$2$orgtelegramuiChangeUsernameActivity(String nameFinal, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(nameFinal)) {
            if (error != null || !(response instanceof TLRPC.TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.getString("UsernameInUse", NUM));
                this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                this.lastNameAvailable = false;
                return;
            }
            this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", NUM, nameFinal));
            this.checkTextView.setTag("windowBackgroundWhiteGreenText");
            this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
            this.lastNameAvailable = true;
        }
    }

    /* access modifiers changed from: private */
    public void saveName() {
        String newName = this.firstNameField.getText().toString();
        if (newName.startsWith("@")) {
            newName = newName.substring(1);
        }
        if (checkUserName(newName, true)) {
            TLRPC.User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (getParentActivity() != null && user != null) {
                String currentName = user.username;
                if (currentName == null) {
                    currentName = "";
                }
                if (currentName.equals(newName)) {
                    finishFragment();
                    return;
                }
                AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC.TL_account_updateUsername req = new TLRPC.TL_account_updateUsername();
                req.username = newName;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_NAME));
                int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ChangeUsernameActivity$$ExternalSyntheticLambda8(this, progressDialog, req), 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                progressDialog.setOnCancelListener(new ChangeUsernameActivity$$ExternalSyntheticLambda0(this, reqId));
                progressDialog.show();
            }
        }
    }

    /* renamed from: lambda$saveName$7$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1578lambda$saveName$7$orgtelegramuiChangeUsernameActivity(AlertDialog progressDialog, TLRPC.TL_account_updateUsername req, TLObject response, TLRPC.TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new ChangeUsernameActivity$$ExternalSyntheticLambda6(this, progressDialog, (TLRPC.User) response));
        } else {
            AndroidUtilities.runOnUIThread(new ChangeUsernameActivity$$ExternalSyntheticLambda5(this, progressDialog, error, req));
        }
    }

    /* renamed from: lambda$saveName$5$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1576lambda$saveName$5$orgtelegramuiChangeUsernameActivity(AlertDialog progressDialog, TLRPC.User user1) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(user1);
        MessagesController.getInstance(this.currentAccount).putUsers(users, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        finishFragment();
    }

    /* renamed from: lambda$saveName$6$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1577lambda$saveName$6$orgtelegramuiChangeUsernameActivity(AlertDialog progressDialog, TLRPC.TL_error error, TLRPC.TL_account_updateUsername req) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    /* renamed from: lambda$saveName$8$org-telegram-ui-ChangeUsernameActivity  reason: not valid java name */
    public /* synthetic */ void m1579lambda$saveName$8$orgtelegramuiChangeUsernameActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        return themeDescriptions;
    }
}
