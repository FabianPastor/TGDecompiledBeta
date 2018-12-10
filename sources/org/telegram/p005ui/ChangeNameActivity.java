package org.telegram.p005ui;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.Components.EditTextBoldCursor;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ChangeNameActivity */
public class ChangeNameActivity extends BaseFragment {
    private static final int done_button = 1;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private View headerLabelView;
    private EditTextBoldCursor lastNameField;

    /* renamed from: org.telegram.ui.ChangeNameActivity$2 */
    class CLASSNAME implements OnTouchListener {
        CLASSNAME() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$3 */
    class CLASSNAME implements OnEditorActionListener {
        CLASSNAME() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 5) {
                return false;
            }
            ChangeNameActivity.this.lastNameField.requestFocus();
            ChangeNameActivity.this.lastNameField.setSelection(ChangeNameActivity.this.lastNameField.length());
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$4 */
    class CLASSNAME implements OnEditorActionListener {
        CLASSNAME() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6) {
                return false;
            }
            ChangeNameActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$6 */
    class CLASSNAME implements Runnable {
        CLASSNAME() {
        }

        public void run() {
            if (ChangeNameActivity.this.firstNameField != null) {
                ChangeNameActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$1 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeNameActivity.this.lambda$checkDiscard$70$PassportActivity();
            } else if (id == 1 && ChangeNameActivity.this.firstNameField.getText().length() != 0) {
                ChangeNameActivity.this.saveName();
                ChangeNameActivity.this.lambda$checkDiscard$70$PassportActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$5 */
    class CLASSNAME implements RequestDelegate {
        CLASSNAME() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    public View createView(Context context) {
        int i = 5;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditName", R.string.EditName));
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.m9dp(56.0f));
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new CLASSNAME());
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setLines(1);
        this.firstNameField.setSingleLine(true);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setOnEditorActionListener(new CLASSNAME());
        this.lastNameField = new EditTextBoldCursor(context);
        this.lastNameField.setTextSize(1, 18.0f);
        this.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        EditTextBoldCursor editTextBoldCursor = this.lastNameField;
        if (!LocaleController.isRTL) {
            i = 3;
        }
        editTextBoldCursor.setGravity(i);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        this.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.lastNameField.setCursorSize(AndroidUtilities.m9dp(20.0f));
        this.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        this.lastNameField.setOnEditorActionListener(new CLASSNAME());
        if (user != null) {
            this.firstNameField.setText(user.first_name);
            this.firstNameField.setSelection(this.firstNameField.length());
            this.lastNameField.setText(user.last_name);
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
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (currentUser != null && this.lastNameField.getText() != null && this.firstNameField.getText() != null) {
            String newFirst = this.firstNameField.getText().toString();
            String newLast = this.lastNameField.getText().toString();
            if (currentUser.first_name == null || !currentUser.first_name.equals(newFirst) || currentUser.last_name == null || !currentUser.last_name.equals(newLast)) {
                TL_account_updateProfile req = new TL_account_updateProfile();
                req.flags = 3;
                req.first_name = newFirst;
                currentUser.first_name = newFirst;
                req.last_name = newLast;
                currentUser.last_name = newLast;
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (user != null) {
                    user.first_name = req.first_name;
                    user.last_name = req.last_name;
                }
                UserConfig.getInstance(this.currentAccount).saveConfig(true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new CLASSNAME());
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new CLASSNAME(), 100);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[13];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[6] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[7] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[8] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        themeDescriptionArr[9] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[10] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[11] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[12] = new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        return themeDescriptionArr;
    }
}
