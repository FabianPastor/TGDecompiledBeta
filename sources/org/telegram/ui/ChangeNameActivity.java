package org.telegram.ui;

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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeNameActivity extends BaseFragment {
    private static final int done_button = 1;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private View headerLabelView;
    private EditTextBoldCursor lastNameField;

    /* renamed from: org.telegram.ui.ChangeNameActivity$2 */
    class C09082 implements OnTouchListener {
        C09082() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$3 */
    class C09093 implements OnEditorActionListener {
        C09093() {
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
    class C09104 implements OnEditorActionListener {
        C09104() {
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
    class C09116 implements Runnable {
        C09116() {
        }

        public void run() {
            if (ChangeNameActivity.this.firstNameField != null) {
                ChangeNameActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$1 */
    class C19441 extends ActionBarMenuOnItemClick {
        C19441() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ChangeNameActivity.this.finishFragment();
            } else if (id == 1 && ChangeNameActivity.this.firstNameField.getText().length() != 0) {
                ChangeNameActivity.this.saveName();
                ChangeNameActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.ChangeNameActivity$5 */
    class C19455 implements RequestDelegate {
        C19455() {
        }

        public void run(TLObject response, TL_error error) {
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("EditName", R.string.EditName));
        this.actionBar.setActionBarMenuOnItemClick(new C19441());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
        }
        LinearLayout linearLayout = new LinearLayout(context2);
        r0.fragmentView = linearLayout;
        r0.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) r0.fragmentView).setOrientation(1);
        r0.fragmentView.setOnTouchListener(new C09082());
        r0.firstNameField = new EditTextBoldCursor(context2);
        r0.firstNameField.setTextSize(1, 18.0f);
        r0.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.firstNameField.setMaxLines(1);
        r0.firstNameField.setLines(1);
        r0.firstNameField.setSingleLine(true);
        int i = 3;
        r0.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.firstNameField.setInputType(49152);
        r0.firstNameField.setImeOptions(5);
        r0.firstNameField.setHint(LocaleController.getString("FirstName", R.string.FirstName));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        r0.firstNameField.setOnEditorActionListener(new C09093());
        r0.lastNameField = new EditTextBoldCursor(context2);
        r0.lastNameField.setTextSize(1, 18.0f);
        r0.lastNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        r0.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        r0.lastNameField.setMaxLines(1);
        r0.lastNameField.setLines(1);
        r0.lastNameField.setSingleLine(true);
        EditTextBoldCursor editTextBoldCursor = r0.lastNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor.setGravity(i);
        r0.lastNameField.setInputType(49152);
        r0.lastNameField.setImeOptions(6);
        r0.lastNameField.setHint(LocaleController.getString("LastName", R.string.LastName));
        r0.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.lastNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.lastNameField.setCursorWidth(1.5f);
        linearLayout.addView(r0.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 16.0f, 24.0f, 0.0f));
        r0.lastNameField.setOnEditorActionListener(new C09104());
        if (user != null) {
            r0.firstNameField.setText(user.first_name);
            r0.firstNameField.setSelection(r0.firstNameField.length());
            r0.lastNameField.setText(user.last_name);
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
        User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        if (!(currentUser == null || this.lastNameField.getText() == null)) {
            if (this.firstNameField.getText() != null) {
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
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C19455());
                }
            }
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new C09116(), 100);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated)};
    }
}
