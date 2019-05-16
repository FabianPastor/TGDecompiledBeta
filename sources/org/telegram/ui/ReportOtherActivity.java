package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.TL_account_reportPeer;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.telegram.tgnet.TLRPC.TL_messages_report;
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
    private int message_id = getArguments().getInt("message_id", 0);

    public ReportOtherActivity(Bundle bundle) {
        super(bundle);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            static /* synthetic */ void lambda$onItemClick$0(TLObject tLObject, TL_error tL_error) {
            }

            public void onItemClick(int i) {
                if (i == -1) {
                    ReportOtherActivity.this.finishFragment();
                } else if (i == 1 && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                    TLObject tL_messages_report;
                    InputPeer inputPeer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                    TL_inputReportReasonOther tL_inputReportReasonOther;
                    if (ReportOtherActivity.this.message_id != 0) {
                        tL_messages_report = new TL_messages_report();
                        tL_messages_report.peer = inputPeer;
                        tL_messages_report.id.add(Integer.valueOf(ReportOtherActivity.this.message_id));
                        tL_inputReportReasonOther = new TL_inputReportReasonOther();
                        tL_inputReportReasonOther.text = ReportOtherActivity.this.firstNameField.getText().toString();
                        tL_messages_report.reason = tL_inputReportReasonOther;
                    } else {
                        tL_messages_report = new TL_account_reportPeer();
                        tL_messages_report.peer = MessagesController.getInstance(ReportOtherActivity.this.currentAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                        tL_inputReportReasonOther = new TL_inputReportReasonOther();
                        tL_inputReportReasonOther.text = ReportOtherActivity.this.firstNameField.getText().toString();
                        tL_messages_report.reason = tL_inputReportReasonOther;
                    }
                    ConnectionsManager.getInstance(ReportOtherActivity.this.currentAccount).sendRequest(tL_messages_report, -$$Lambda$ReportOtherActivity$1$PbLFyQbNnsMkC-qS1TkzMcffkwA.INSTANCE);
                    if (ReportOtherActivity.this.getParentActivity() != null) {
                        Toast.makeText(ReportOtherActivity.this.getParentActivity(), LocaleController.getString("ReportChatSent", NUM), 0).show();
                    }
                    ReportOtherActivity.this.finishFragment();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(-$$Lambda$ReportOtherActivity$VcwTn-4nik4XOSC4IbcsIN4IckE.INSTANCE);
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        String str = "windowBackgroundWhiteBlackText";
        this.firstNameField.setTextColor(Theme.getColor(str));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        int i = 3;
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
        this.firstNameField.setCursorColor(Theme.getColor(str));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new -$$Lambda$ReportOtherActivity$JRCa_EXPGvX6N9BVVFwqPlLZM80(this));
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", NUM));
        editTextBoldCursor = this.firstNameField;
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ReportOtherActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ReportOtherActivity$djrT4sV5rD_-jM1owBrq9EJKfOk(this), 100);
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$2$ReportOtherActivity() {
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated")};
    }
}
