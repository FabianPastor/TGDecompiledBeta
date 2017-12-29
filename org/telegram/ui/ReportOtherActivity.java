package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
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
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment {
    private static final int done_button = 1;
    private long dialog_id = getArguments().getLong("dialog_id", 0);
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private View headerLabelView;

    public ReportOtherActivity(Bundle args) {
        super(args);
    }

    public View createView(Context context) {
        int i = 3;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ReportOtherActivity.this.finishFragment();
                } else if (id == 1 && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                    TL_account_reportPeer req = new TL_account_reportPeer();
                    req.peer = MessagesController.getInstance(ReportOtherActivity.this.currentAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                    req.reason = new TL_inputReportReasonOther();
                    req.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                    ConnectionsManager.getInstance(ReportOtherActivity.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                        }
                    });
                    ReportOtherActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor.setGravity(i);
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6 || ReportOtherActivity.this.doneButton == null) {
                    return false;
                }
                ReportOtherActivity.this.doneButton.performClick();
                return true;
            }
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
        this.firstNameField.setSelection(this.firstNameField.length());
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (ReportOtherActivity.this.firstNameField != null) {
                        ReportOtherActivity.this.firstNameField.requestFocus();
                        AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
                    }
                }
            }, 100);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[9];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[5] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[6] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText);
        themeDescriptionArr[7] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField);
        themeDescriptionArr[8] = new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated);
        return themeDescriptionArr;
    }
}
