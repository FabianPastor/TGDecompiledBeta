package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_updateProfile;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CodepointsLengthInputFilter;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
/* loaded from: classes3.dex */
public class ChangeBioActivity extends BaseFragment {
    private NumberTextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        String str;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBar actionBar = this.actionBar;
        int i = R.string.UserBio;
        actionBar.setTitle(LocaleController.getString("UserBio", i));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChangeBioActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ChangeBioActivity.this.finishFragment();
                } else if (i2 != 1) {
                } else {
                    ChangeBioActivity.this.saveName();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
        this.doneButton = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        LinearLayout linearLayout = new LinearLayout(context);
        this.fragmentView = linearLayout;
        LinearLayout linearLayout2 = linearLayout;
        linearLayout2.setOrientation(1);
        this.fragmentView.setOnTouchListener(ChangeBioActivity$$ExternalSyntheticLambda1.INSTANCE);
        FrameLayout frameLayout = new FrameLayout(context);
        linearLayout2.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) { // from class: org.telegram.ui.ChangeBioActivity.2
            @Override // org.telegram.ui.Components.EditTextBoldCursor, android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                Editable editableText = getEditableText();
                int aboutLimit = ChangeBioActivity.this.getMessagesController().getAboutLimit() - Character.codePointCount(editableText, 0, editableText.length());
                accessibilityNodeInfo.setText(((Object) getText()) + ", " + LocaleController.formatPluralString("PeopleJoinedRemaining", aboutLimit, new Object[0]));
            }
        };
        this.firstNameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable(null);
        this.firstNameField.setLineColors(getThemedColor("windowBackgroundWhiteInputField"), getThemedColor("windowBackgroundWhiteInputFieldActivated"), getThemedColor("windowBackgroundWhiteRedText3"));
        this.firstNameField.setMaxLines(4);
        EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
        float f = 24.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f);
        if (LocaleController.isRTL) {
            f = 0.0f;
        }
        editTextBoldCursor2.setPadding(dp, 0, AndroidUtilities.dp(f), AndroidUtilities.dp(6.0f));
        this.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        this.firstNameField.setImeOptions(NUM);
        this.firstNameField.setInputType(147457);
        this.firstNameField.setImeOptions(6);
        this.firstNameField.setFilters(new InputFilter[]{new CodepointsLengthInputFilter(getMessagesController().getAboutLimit()) { // from class: org.telegram.ui.ChangeBioActivity.3
            @Override // org.telegram.ui.Components.CodepointsLengthInputFilter, android.text.InputFilter
            public CharSequence filter(CharSequence charSequence, int i2, int i3, Spanned spanned, int i4, int i5) {
                if (charSequence != null && charSequence.length() > 0 && TextUtils.indexOf(charSequence, '\n') == charSequence.length() - 1) {
                    ChangeBioActivity.this.doneButton.performClick();
                    return "";
                }
                CharSequence filter = super.filter(charSequence, i2, i3, spanned, i4, i5);
                if (filter != null && charSequence != null && filter.length() != charSequence.length()) {
                    Vibrator vibrator = (Vibrator) ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200L);
                    }
                    AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView, 2.0f, 0);
                }
                return filter;
            }
        }});
        this.firstNameField.setMinHeight(AndroidUtilities.dp(36.0f));
        this.firstNameField.setHint(LocaleController.getString("UserBio", i));
        this.firstNameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.firstNameField.setCursorWidth(1.5f);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChangeBioActivity$$ExternalSyntheticLambda2
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$createView$1;
                lambda$createView$1 = ChangeBioActivity.this.lambda$createView$1(textView, i2, keyEvent);
                return lambda$createView$1;
            }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChangeBioActivity.4
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                ChangeBioActivity.this.checkTextView.setNumber(ChangeBioActivity.this.getMessagesController().getAboutLimit() - Character.codePointCount(editable, 0, editable.length()), true);
            }
        });
        frameLayout.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        NumberTextView numberTextView = new NumberTextView(context);
        this.checkTextView = numberTextView;
        numberTextView.setCenterAlign(true);
        this.checkTextView.setTextSize(15);
        this.checkTextView.setNumber(getMessagesController().getAboutLimit(), false);
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
        this.checkTextView.setImportantForAccessibility(2);
        frameLayout.addView(this.checkTextView, LayoutHelper.createFrame(26, 20.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 4.0f, 4.0f, 0.0f));
        TextView textView = new TextView(context);
        this.helpTextView = textView;
        textView.setFocusable(true);
        this.helpTextView.setTextSize(1, 15.0f);
        this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", R.string.UserBioInfo)));
        linearLayout2.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (userFull != null && (str = userFull.about) != null) {
            this.firstNameField.setText(str);
            EditTextBoldCursor editTextBoldCursor3 = this.firstNameField;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
        }
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$1(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true)) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveName() {
        final TLRPC$UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (getParentActivity() == null || userFull == null) {
            return;
        }
        String str = userFull.about;
        if (str == null) {
            str = "";
        }
        final String replace = this.firstNameField.getText().toString().replace("\n", "");
        if (str.equals(replace)) {
            finishFragment();
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        final TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile = new TLRPC$TL_account_updateProfile();
        tLRPC$TL_account_updateProfile.about = replace;
        tLRPC$TL_account_updateProfile.flags |= 4;
        final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_updateProfile, new RequestDelegate() { // from class: org.telegram.ui.ChangeBioActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                ChangeBioActivity.this.lambda$saveName$4(alertDialog, userFull, replace, tLRPC$TL_account_updateProfile, tLObject, tLRPC$TL_error);
            }
        }, 2);
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChangeBioActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChangeBioActivity.this.lambda$saveName$5(sendRequest, dialogInterface);
            }
        });
        alertDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$4(final AlertDialog alertDialog, final TLRPC$UserFull tLRPC$UserFull, final String str, final TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile, TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error == null) {
            final TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeBioActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ChangeBioActivity.this.lambda$saveName$2(alertDialog, tLRPC$UserFull, str, tLRPC$User);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChangeBioActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChangeBioActivity.this.lambda$saveName$3(alertDialog, tLRPC$TL_error, tLRPC$TL_account_updateProfile);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$2(AlertDialog alertDialog, TLRPC$UserFull tLRPC$UserFull, String str, TLRPC$User tLRPC$User) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        tLRPC$UserFull.about = str;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Long.valueOf(tLRPC$User.id), tLRPC$UserFull);
        finishFragment();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$3(AlertDialog alertDialog, TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_account_updateProfile tLRPC$TL_account_updateProfile) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this, tLRPC$TL_account_updateProfile, new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$saveName$5(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"));
        arrayList.add(new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"));
        arrayList.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8"));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText4"));
        return arrayList;
    }
}
