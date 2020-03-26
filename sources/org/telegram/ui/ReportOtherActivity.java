package org.telegram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public long dialog_id = getArguments().getLong("dialog_id", 0);
    private View doneButton;
    /* access modifiers changed from: private */
    public EditTextBoldCursor firstNameField;
    /* access modifiers changed from: private */
    public int message_id = getArguments().getInt("message_id", 0);

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public ReportOtherActivity(Bundle bundle) {
        super(bundle);
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("ReportChat", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            static /* synthetic */ void lambda$onItemClick$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            }

            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.tgnet.TLRPC$TL_messages_report} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.tgnet.TLRPC$TL_account_reportPeer} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r4) {
                /*
                    r3 = this;
                    r0 = -1
                    if (r4 != r0) goto L_0x000a
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    r4.finishFragment()
                    goto L_0x00ca
                L_0x000a:
                    r0 = 1
                    if (r4 != r0) goto L_0x00ca
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor r4 = r4.firstNameField
                    android.text.Editable r4 = r4.getText()
                    int r4 = r4.length()
                    if (r4 == 0) goto L_0x00ca
                    int r4 = org.telegram.messenger.UserConfig.selectedAccount
                    org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                    org.telegram.ui.ReportOtherActivity r0 = org.telegram.ui.ReportOtherActivity.this
                    long r0 = r0.dialog_id
                    int r1 = (int) r0
                    org.telegram.tgnet.TLRPC$InputPeer r4 = r4.getInputPeer(r1)
                    org.telegram.ui.ReportOtherActivity r0 = org.telegram.ui.ReportOtherActivity.this
                    int r0 = r0.message_id
                    if (r0 == 0) goto L_0x0064
                    org.telegram.tgnet.TLRPC$TL_messages_report r0 = new org.telegram.tgnet.TLRPC$TL_messages_report
                    r0.<init>()
                    r0.peer = r4
                    java.util.ArrayList<java.lang.Integer> r4 = r0.id
                    org.telegram.ui.ReportOtherActivity r1 = org.telegram.ui.ReportOtherActivity.this
                    int r1 = r1.message_id
                    java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
                    r4.add(r1)
                    org.telegram.tgnet.TLRPC$TL_inputReportReasonOther r4 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonOther
                    r4.<init>()
                    org.telegram.ui.ReportOtherActivity r1 = org.telegram.ui.ReportOtherActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor r1 = r1.firstNameField
                    android.text.Editable r1 = r1.getText()
                    java.lang.String r1 = r1.toString()
                    r4.text = r1
                    r0.reason = r4
                    goto L_0x0097
                L_0x0064:
                    org.telegram.tgnet.TLRPC$TL_account_reportPeer r0 = new org.telegram.tgnet.TLRPC$TL_account_reportPeer
                    r0.<init>()
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    int r4 = r4.currentAccount
                    org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
                    org.telegram.ui.ReportOtherActivity r1 = org.telegram.ui.ReportOtherActivity.this
                    long r1 = r1.dialog_id
                    int r2 = (int) r1
                    org.telegram.tgnet.TLRPC$InputPeer r4 = r4.getInputPeer(r2)
                    r0.peer = r4
                    org.telegram.tgnet.TLRPC$TL_inputReportReasonOther r4 = new org.telegram.tgnet.TLRPC$TL_inputReportReasonOther
                    r4.<init>()
                    org.telegram.ui.ReportOtherActivity r1 = org.telegram.ui.ReportOtherActivity.this
                    org.telegram.ui.Components.EditTextBoldCursor r1 = r1.firstNameField
                    android.text.Editable r1 = r1.getText()
                    java.lang.String r1 = r1.toString()
                    r4.text = r1
                    r0.reason = r4
                L_0x0097:
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    int r4 = r4.currentAccount
                    org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                    org.telegram.ui.-$$Lambda$ReportOtherActivity$1$PbLFyQbNnsMkC-qS1TkzMcffkwA r1 = org.telegram.ui.$$Lambda$ReportOtherActivity$1$PbLFyQbNnsMkCqS1TkzMcffkwA.INSTANCE
                    r4.sendRequest(r0, r1)
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    android.app.Activity r4 = r4.getParentActivity()
                    if (r4 == 0) goto L_0x00c5
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    android.app.Activity r4 = r4.getParentActivity()
                    r0 = 2131626516(0x7f0e0a14, float:1.888027E38)
                    java.lang.String r1 = "ReportChatSent"
                    java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
                    r1 = 0
                    android.widget.Toast r4 = android.widget.Toast.makeText(r4, r0, r1)
                    r4.show()
                L_0x00c5:
                    org.telegram.ui.ReportOtherActivity r4 = org.telegram.ui.ReportOtherActivity.this
                    r4.finishFragment()
                L_0x00ca:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReportOtherActivity.AnonymousClass1.onItemClick(int):void");
            }
        });
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        ((LinearLayout) this.fragmentView).setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ReportOtherActivity$VcwTn4nik4XOSC4IbcsIN4IckE.INSTANCE);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        int i = 3;
        this.firstNameField.setMaxLines(3);
        this.firstNameField.setPadding(0, 0, 0, 0);
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        if (LocaleController.isRTL) {
            i = 5;
        }
        editTextBoldCursor2.setGravity(i);
        this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ReportOtherActivity.this.lambda$createView$1$ReportOtherActivity(textView, i, keyEvent);
            }
        });
        linearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0f, 24.0f, 24.0f, 0.0f));
        this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", NUM));
        EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
        editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ReportOtherActivity(TextView textView, int i, KeyEvent keyEvent) {
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

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    ReportOtherActivity.this.lambda$onTransitionAnimationEnd$2$ReportOtherActivity();
                }
            }, 100);
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
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated")};
    }
}
