package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_updateProfile;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ChangeBioActivity extends BaseFragment {
    private static final int done_button = 1;
    private TextView checkTextView;
    private View doneButton;
    private EditTextBoldCursor firstNameField;
    private TextView helpTextView;

    /* renamed from: org.telegram.ui.ChangeBioActivity$2 */
    class C08982 implements OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }

        C08982() {
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$4 */
    class C09004 implements OnEditorActionListener {
        C09004() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i != 6 || ChangeBioActivity.this.doneButton == null) {
                return null;
            }
            ChangeBioActivity.this.doneButton.performClick();
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$5 */
    class C09015 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C09015() {
        }

        public void afterTextChanged(Editable editable) {
            ChangeBioActivity.this.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70 - ChangeBioActivity.this.firstNameField.length())}));
        }
    }

    /* renamed from: org.telegram.ui.ChangeBioActivity$1 */
    class C19471 extends ActionBarMenuOnItemClick {
        C19471() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                ChangeBioActivity.this.finishFragment();
            } else if (i == 1) {
                ChangeBioActivity.this.saveName();
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("UserBio", C0446R.string.UserBio));
        this.actionBar.setActionBarMenuOnItemClick(new C19471());
        this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, C0446R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new LinearLayout(context2);
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(new C08982());
        View frameLayout = new FrameLayout(context2);
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2, 24.0f, 24.0f, 20.0f, 0.0f));
        this.firstNameField = new EditTextBoldCursor(context2);
        this.firstNameField.setTextSize(1, 18.0f);
        this.firstNameField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(context2, false));
        this.firstNameField.setMaxLines(4);
        EditTextBoldCursor editTextBoldCursor = this.firstNameField;
        float f = 0.0f;
        int dp = AndroidUtilities.dp(LocaleController.isRTL ? 24.0f : 0.0f);
        if (!LocaleController.isRTL) {
            f = 24.0f;
        }
        editTextBoldCursor.setPadding(dp, 0, AndroidUtilities.dp(f), AndroidUtilities.dp(6.0f));
        r0.firstNameField.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.firstNameField.setImeOptions(268435456);
        r0.firstNameField.setInputType(147457);
        r0.firstNameField.setImeOptions(6);
        r0.firstNameField.setFilters(new InputFilter[]{new LengthFilter(70) {
            public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                if (charSequence == null || TextUtils.indexOf(charSequence, '\n') == -1) {
                    i = super.filter(charSequence, i, i2, spanned, i3, i4);
                    if (!(i == 0 || charSequence == null || i.length() == charSequence.length())) {
                        Vibrator vibrator = (Vibrator) ChangeBioActivity.this.getParentActivity().getSystemService("vibrator");
                        if (vibrator != null) {
                            vibrator.vibrate(Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        }
                        AndroidUtilities.shakeView(ChangeBioActivity.this.checkTextView, NUM, null);
                    }
                    return i;
                }
                ChangeBioActivity.this.doneButton.performClick();
                return TtmlNode.ANONYMOUS_REGION_ID;
            }
        }});
        r0.firstNameField.setMinHeight(AndroidUtilities.dp(36.0f));
        r0.firstNameField.setHint(LocaleController.getString("UserBio", C0446R.string.UserBio));
        r0.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.firstNameField.setCursorSize(AndroidUtilities.dp(20.0f));
        r0.firstNameField.setCursorWidth(1.5f);
        r0.firstNameField.setOnEditorActionListener(new C09004());
        r0.firstNameField.addTextChangedListener(new C09015());
        frameLayout.addView(r0.firstNameField, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        r0.checkTextView = new TextView(context2);
        r0.checkTextView.setTextSize(1, 15.0f);
        r0.checkTextView.setText(String.format("%d", new Object[]{Integer.valueOf(70)}));
        r0.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
        frameLayout.addView(r0.checkTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 4.0f, 4.0f, 0.0f));
        r0.helpTextView = new TextView(context2);
        r0.helpTextView.setTextSize(1, 15.0f);
        r0.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
        r0.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        r0.helpTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("UserBioInfo", C0446R.string.UserBioInfo)));
        linearLayout.addView(r0.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 0));
        TL_userFull userFull = MessagesController.getInstance(r0.currentAccount).getUserFull(UserConfig.getInstance(r0.currentAccount).getClientUserId());
        if (!(userFull == null || userFull.about == null)) {
            r0.firstNameField.setText(userFull.about);
            r0.firstNameField.setSelection(r0.firstNameField.length());
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
        final TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (getParentActivity() != null) {
            if (userFull != null) {
                String str = userFull.about;
                if (str == null) {
                    str = TtmlNode.ANONYMOUS_REGION_ID;
                }
                final String replace = this.firstNameField.getText().toString().replace("\n", TtmlNode.ANONYMOUS_REGION_ID);
                if (str.equals(replace)) {
                    finishFragment();
                    return;
                }
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 1);
                alertDialog.setMessage(LocaleController.getString("Loading", C0446R.string.Loading));
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setCancelable(false);
                TLObject tL_account_updateProfile = new TL_account_updateProfile();
                tL_account_updateProfile.about = replace;
                tL_account_updateProfile.flags |= 4;
                final AlertDialog alertDialog2 = alertDialog;
                final TLObject tLObject = tL_account_updateProfile;
                final int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateProfile, new RequestDelegate() {
                    public void run(TLObject tLObject, final TL_error tL_error) {
                        if (tL_error == null) {
                            final User user = (User) tLObject;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        alertDialog2.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    userFull.about = replace;
                                    NotificationCenter.getInstance(ChangeBioActivity.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), userFull);
                                    ChangeBioActivity.this.finishFragment();
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    alertDialog2.dismiss();
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                }
                                AlertsCreator.processError(ChangeBioActivity.this.currentAccount, tL_error, ChangeBioActivity.this, tLObject, new Object[0]);
                            }
                        });
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
                alertDialog.setButton(-2, LocaleController.getString("Cancel", C0446R.string.Cancel), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ConnectionsManager.getInstance(ChangeBioActivity.this.currentAccount).cancelRequest(sendRequest, true);
                        try {
                            dialogInterface.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                });
                alertDialog.show();
            }
        }
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(this.firstNameField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText4)};
    }
}
