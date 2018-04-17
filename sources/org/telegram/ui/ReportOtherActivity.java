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
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
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

    /* renamed from: org.telegram.ui.ReportOtherActivity$2 */
    class C16562 implements OnTouchListener {
        C16562() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$3 */
    class C16573 implements OnEditorActionListener {
        C16573() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ReportOtherActivity.this.doneButton == null) {
                return false;
            }
            ReportOtherActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$4 */
    class C16584 implements Runnable {
        C16584() {
        }

        public void run() {
            if (ReportOtherActivity.this.firstNameField != null) {
                ReportOtherActivity.this.firstNameField.requestFocus();
                AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
            }
        }
    }

    /* renamed from: org.telegram.ui.ReportOtherActivity$1 */
    class C22641 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ReportOtherActivity$1$1 */
        class C22631 implements RequestDelegate {
            C22631() {
            }

            public void run(TLObject response, TL_error error) {
            }
        }

        C22641() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ReportOtherActivity.this.finishFragment();
            } else if (id == 1 && ReportOtherActivity.this.firstNameField.getText().length() != 0) {
                TLObject req;
                InputPeer peer = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                if (ReportOtherActivity.this.message_id != 0) {
                    req = new TL_messages_report();
                    req.peer = peer;
                    req.id.add(Integer.valueOf(ReportOtherActivity.this.message_id));
                    req.reason = new TL_inputReportReasonOther();
                    req.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                } else {
                    req = new TL_account_reportPeer();
                    req.peer = MessagesController.getInstance(ReportOtherActivity.this.currentAccount).getInputPeer((int) ReportOtherActivity.this.dialog_id);
                    req.reason = new TL_inputReportReasonOther();
                    req.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
                }
                ConnectionsManager.getInstance(ReportOtherActivity.this.currentAccount).sendRequest(req, new C22631());
                if (ReportOtherActivity.this.getParentActivity() != null) {
                    Toast.makeText(ReportOtherActivity.this.getParentActivity(), LocaleController.getString("ReportChatSent", R.string.ReportChatSent), 0).show();
                }
                ReportOtherActivity.this.finishFragment();
            }
        }
    }

    public ReportOtherActivity(Bundle args) {
        super(args);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", R.string.ReportChat));
        this.actionBar.setActionBarMenuOnItemClick(new C22641());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        this.fragmentView.setLayoutParams(new LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener(new C16562());
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
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor.setGravity(i);
        this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new C16573());
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
            AndroidUtilities.runOnUIThread(new C16584(), 100);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated)};
    }
}
