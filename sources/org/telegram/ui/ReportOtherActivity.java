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
import android.widget.Toast;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
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

    /* renamed from: org.telegram.ui.ReportOtherActivity$2 */
    class C16622 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C16622() {
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$3 */
    class C16633 implements OnEditorActionListener {
        C16633() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ReportOtherActivity.this.doneButton == null) {
                return null;
            }
            ReportOtherActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$4 */
    class C16644 implements Runnable {
        C16644() {
        }

        public void run() {
            if (ReportOtherActivity.this.firstNameField != null) {
                ReportOtherActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$1 */
    class C22701 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ReportOtherActivity$1$1 */
        class C22691 implements RequestDelegate {
            public void run(TLObject tLObject, TL_error tL_error) {
            }

            C22691() {
            }
        }

        C22701() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ReportOtherActivity.this.finishFragment();
            } else if (i == 1 && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                TLObject tL_messages_report;
                i = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                if (ReportOtherActivity.this.message_id != 0) {
                    tL_messages_report = new TL_messages_report();
                    tL_messages_report.peer = i;
                    tL_messages_report.id.add(Integer.valueOf(ReportOtherActivity.this.message_id));
                    tL_messages_report.reason = new TL_inputReportReasonOther();
                    tL_messages_report.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                } else {
                    tL_messages_report = new TL_account_reportPeer();
                    tL_messages_report.peer = MessagesController.getInstance(ReportOtherActivity.this.currentAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                    tL_messages_report.reason = new TL_inputReportReasonOther();
                    tL_messages_report.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                }
                ConnectionsManager.getInstance(ReportOtherActivity.this.currentAccount).sendRequest(tL_messages_report, new C22691());
                if (ReportOtherActivity.this.getParentActivity() != 0) {
                    Toast.makeText(ReportOtherActivity.this.getParentActivity(), LocaleController.getString("ReportChatSent", C0446R.string.ReportChatSent), 0).show();
                }
                ReportOtherActivity.this.finishFragment();
            }
        }
    }

    public ReportOtherActivity(Bundle bundle) {
        super(bundle);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", C0446R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new C22701());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        View linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new C16622());
        this.firstNameField = new EditTextBoldCursor(context);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        int i = 3;
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        context = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        context.setGravity(i);
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new C16633());
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", C0446R.string.ReportChatDescription));
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

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new C16644(), 100);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated)};
    }
}
