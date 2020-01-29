package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeSetUrlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextInfoPrivacyCell checkInfoCell;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextSettingsCell createCell;
    private TextInfoPrivacyCell createInfoCell;
    /* access modifiers changed from: private */
    public boolean creatingNewTheme;
    private View divider;
    private View doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public TextInfoPrivacyCell helpInfoCell;
    /* access modifiers changed from: private */
    public boolean ignoreCheck;
    private TLRPC.TL_theme info;
    /* access modifiers changed from: private */
    public CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayoutTypeContainer;
    /* access modifiers changed from: private */
    public EditTextBoldCursor linkField;
    private ThemePreviewMessagesCell messagesCell;
    private EditTextBoldCursor nameField;
    private AlertDialog progressDialog;
    private Theme.ThemeAccent themeAccent;
    private Theme.ThemeInfo themeInfo;

    static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ boolean lambda$null$4(View view, MotionEvent motionEvent) {
        return true;
    }

    static /* synthetic */ void lambda$saveTheme$9(DialogInterface dialogInterface) {
    }

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String str) {
            this.url = str;
        }

        public void updateDrawState(TextPaint textPaint) {
            super.updateDrawState(textPaint);
            textPaint.setUnderlineText(false);
        }

        public void onClick(View view) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                Toast.makeText(ThemeSetUrlActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public ThemeSetUrlActivity(Theme.ThemeInfo themeInfo2, Theme.ThemeAccent themeAccent2, boolean z) {
        this.themeInfo = themeInfo2;
        this.themeAccent = themeAccent2;
        this.info = themeAccent2 != null ? themeAccent2.info : themeInfo2.info;
        this.currentAccount = themeAccent2 != null ? themeAccent2.account : themeInfo2.account;
        this.creatingNewTheme = z;
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.creatingNewTheme) {
            this.actionBar.setTitle(LocaleController.getString("NewThemeTitle", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("EditThemeTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeSetUrlActivity.this.finishFragment();
                } else if (i == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        this.fragmentView = new LinearLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener($$Lambda$ThemeSetUrlActivity$_8Xemr4PmFieDLseu_SVMqLlMLs.INSTANCE);
        this.linearLayoutTypeContainer = new LinearLayout(context2);
        this.linearLayoutTypeContainer.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context2, 23);
        this.headerCell.setText(LocaleController.getString("Info", NUM));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        this.nameField = new EditTextBoldCursor(context2);
        this.nameField.setTextSize(1, 18.0f);
        this.nameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.nameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameField.setMaxLines(1);
        this.nameField.setLines(1);
        this.nameField.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameField.setBackgroundDrawable((Drawable) null);
        this.nameField.setPadding(0, 0, 0, 0);
        this.nameField.setSingleLine(true);
        this.nameField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(128)});
        this.nameField.setInputType(163872);
        this.nameField.setImeOptions(6);
        this.nameField.setHint(LocaleController.getString("ThemeNamePlaceholder", NUM));
        this.nameField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameField.setCursorWidth(1.5f);
        this.linearLayoutTypeContainer.addView(this.nameField, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.nameField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.lambda$createView$1$ThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        this.divider = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        };
        this.linearLayoutTypeContainer.addView(this.divider, new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linearLayout2, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.editText = new EditTextBoldCursor(context2);
        EditTextBoldCursor editTextBoldCursor = this.editText;
        editTextBoldCursor.setText(getMessagesController().linkPrefix + "/addtheme/");
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable((Drawable) null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        linearLayout2.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        this.linkField = new EditTextBoldCursor(context2);
        this.linkField.setTextSize(1, 18.0f);
        this.linkField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.linkField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.linkField.setMaxLines(1);
        this.linkField.setLines(1);
        this.linkField.setBackgroundDrawable((Drawable) null);
        this.linkField.setPadding(0, 0, 0, 0);
        this.linkField.setSingleLine(true);
        this.linkField.setInputType(163872);
        this.linkField.setImeOptions(6);
        this.linkField.setHint(LocaleController.getString("SetUrlPlaceholder", NUM));
        this.linkField.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.linkField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.linkField.setCursorWidth(1.5f);
        linearLayout2.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ThemeSetUrlActivity.this.lambda$createView$2$ThemeSetUrlActivity(textView, i, keyEvent);
            }
        });
        this.linkField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ThemeSetUrlActivity.this.ignoreCheck) {
                    ThemeSetUrlActivity themeSetUrlActivity = ThemeSetUrlActivity.this;
                    boolean unused = themeSetUrlActivity.checkUrl(themeSetUrlActivity.linkField.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!ThemeSetUrlActivity.this.creatingNewTheme) {
                    if (ThemeSetUrlActivity.this.linkField.length() > 0) {
                        String str = "https://" + ThemeSetUrlActivity.this.getMessagesController().linkPrefix + "/addtheme/" + ThemeSetUrlActivity.this.linkField.getText();
                        String formatString = LocaleController.formatString("ThemeHelpLink", NUM, str);
                        int indexOf = formatString.indexOf(str);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                        if (indexOf >= 0) {
                            spannableStringBuilder.setSpan(new LinkSpan(str), indexOf, str.length() + indexOf, 33);
                        }
                        ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(new CharSequence[]{ThemeSetUrlActivity.this.infoText, "\n\n", spannableStringBuilder}));
                        return;
                    }
                    ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                }
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public final void onFocusChange(View view, boolean z) {
                    ThemeSetUrlActivity.this.lambda$createView$3$ThemeSetUrlActivity(view, z);
                }
            });
        }
        this.checkInfoCell = new TextInfoPrivacyCell(context2);
        this.checkInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.checkInfoCell.setVisibility(8);
        this.checkInfoCell.setBottomPadding(0);
        linearLayout.addView(this.checkInfoCell, LayoutHelper.createLinear(-1, -2));
        this.helpInfoCell = new TextInfoPrivacyCell(context2);
        this.helpInfoCell.getTextView().setMovementMethod(new LinkMovementMethodMy());
        this.helpInfoCell.getTextView().setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", NUM)));
        } else {
            TextInfoPrivacyCell textInfoPrivacyCell = this.helpInfoCell;
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("ThemeSetUrlHelp", NUM));
            this.infoText = replaceTags;
            textInfoPrivacyCell.setText(replaceTags);
        }
        linearLayout.addView(this.helpInfoCell, LayoutHelper.createLinear(-1, -2));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            this.messagesCell = new ThemePreviewMessagesCell(context2, this.parentLayout, 1);
            linearLayout.addView(this.messagesCell, LayoutHelper.createLinear(-1, -2));
            this.createCell = new TextSettingsCell(context2);
            this.createCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString("UseDifferentTheme", NUM), false);
            linearLayout.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new View.OnClickListener(context2) {
                private final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ThemeSetUrlActivity.this.lambda$createView$5$ThemeSetUrlActivity(this.f$1, view);
                }
            });
            this.createInfoCell = new TextInfoPrivacyCell(context2);
            this.createInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("UseDifferentThemeInfo", NUM)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            linearLayout.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        }
        TLRPC.TL_theme tL_theme = this.info;
        if (tL_theme != null) {
            this.ignoreCheck = true;
            this.nameField.setText(tL_theme.title);
            EditTextBoldCursor editTextBoldCursor2 = this.nameField;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            this.linkField.setText(this.info.slug);
            EditTextBoldCursor editTextBoldCursor3 = this.linkField;
            editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    public /* synthetic */ boolean lambda$createView$1$ThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.nameField);
        return true;
    }

    public /* synthetic */ boolean lambda$createView$2$ThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public /* synthetic */ void lambda$createView$3$ThemeSetUrlActivity(View view, boolean z) {
        if (z) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp2", NUM)));
        } else {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", NUM)));
        }
    }

    public /* synthetic */ void lambda$createView$5$ThemeSetUrlActivity(Context context, View view) {
        Context context2 = context;
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false);
            builder.setApplyBottomPadding(false);
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(1);
            TextView textView = new TextView(context2);
            textView.setText(LocaleController.getString("ChooseTheme", NUM));
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
            textView.setOnTouchListener($$Lambda$ThemeSetUrlActivity$QxZyJwLHHcW9tcx8U4a2QYVzp0.INSTANCE);
            builder.setCustomView(linearLayout);
            ArrayList arrayList = new ArrayList();
            int size = Theme.themes.size();
            for (int i = 0; i < size; i++) {
                Theme.ThemeInfo themeInfo2 = Theme.themes.get(i);
                TLRPC.TL_theme tL_theme = themeInfo2.info;
                if (tL_theme == null || tL_theme.document != null) {
                    arrayList.add(themeInfo2);
                }
            }
            final BottomSheet.Builder builder2 = builder;
            AnonymousClass4 r0 = new ThemesHorizontalListCell(context, 2, arrayList, new ArrayList()) {
                /* access modifiers changed from: protected */
                public void updateRows() {
                    builder2.getDismissRunnable().run();
                }
            };
            linearLayout.addView(r0, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
            r0.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
            showDialog(builder.create());
        }
    }

    public void onResume() {
        super.onResume();
        if (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AlertDialog alertDialog;
        AlertDialog alertDialog2;
        if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo2 = objArr[0];
            Theme.ThemeAccent themeAccent2 = objArr[1];
            if (themeInfo2 == this.themeInfo && themeAccent2 == this.themeAccent && (alertDialog2 = this.progressDialog) != null) {
                try {
                    alertDialog2.dismiss();
                    this.progressDialog = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                Theme.applyTheme(this.themeInfo, false);
                finishFragment();
            }
        } else if (i == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo3 = objArr[0];
            Theme.ThemeAccent themeAccent3 = objArr[1];
            if (themeInfo3 == this.themeInfo && themeAccent3 == this.themeAccent && (alertDialog = this.progressDialog) != null) {
                try {
                    alertDialog.dismiss();
                    this.progressDialog = null;
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean checkUrl(String str, boolean z) {
        String str2;
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
        if (str != null) {
            if (str.startsWith("_") || str.endsWith("_")) {
                setCheckText(LocaleController.getString("SetUrlInvalid", NUM), "windowBackgroundWhiteRedText4");
                return false;
            }
            int i = 0;
            while (i < str.length()) {
                char charAt = str.charAt(i);
                if (i == 0 && charAt >= '0' && charAt <= '9') {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidStartNumber", NUM));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalidStartNumber", NUM), "windowBackgroundWhiteRedText4");
                    }
                    return false;
                } else if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && charAt != '_'))) {
                    if (z) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalid", NUM));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalid", NUM), "windowBackgroundWhiteRedText4");
                    }
                    return false;
                } else {
                    i++;
                }
            }
        }
        if (str == null || str.length() < 5) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidShort", NUM));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidShort", NUM), "windowBackgroundWhiteRedText4");
            }
            return false;
        } else if (str.length() > 64) {
            if (z) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidLong", NUM));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidLong", NUM), "windowBackgroundWhiteRedText4");
            }
            return false;
        } else {
            if (!z) {
                TLRPC.TL_theme tL_theme = this.info;
                if (tL_theme == null || (str2 = tL_theme.slug) == null) {
                    str2 = "";
                }
                if (str.equals(str2)) {
                    setCheckText(LocaleController.formatString("SetUrlAvailable", NUM, str), "windowBackgroundWhiteGreenText");
                    return true;
                }
                setCheckText(LocaleController.getString("SetUrlChecking", NUM), "windowBackgroundWhiteGrayText8");
                this.lastCheckName = str;
                this.checkRunnable = new Runnable(str) {
                    private final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ThemeSetUrlActivity.this.lambda$checkUrl$8$ThemeSetUrlActivity(this.f$1);
                    }
                };
                AndroidUtilities.runOnUIThread(this.checkRunnable, 300);
            }
            return true;
        }
    }

    public /* synthetic */ void lambda$checkUrl$8$ThemeSetUrlActivity(String str) {
        TLRPC.TL_account_createTheme tL_account_createTheme = new TLRPC.TL_account_createTheme();
        tL_account_createTheme.slug = str;
        tL_account_createTheme.title = "";
        tL_account_createTheme.document = new TLRPC.TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_createTheme, new RequestDelegate(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ThemeSetUrlActivity.this.lambda$null$7$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
            }
        }, 2);
    }

    public /* synthetic */ void lambda$null$7$ThemeSetUrlActivity(String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, tL_error) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ TLRPC.TL_error f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ThemeSetUrlActivity.this.lambda$null$6$ThemeSetUrlActivity(this.f$1, this.f$2);
            }
        });
    }

    public /* synthetic */ void lambda$null$6$ThemeSetUrlActivity(String str, TLRPC.TL_error tL_error) {
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 != null && str2.equals(str)) {
            if (tL_error == null || (!"THEME_SLUG_INVALID".equals(tL_error.text) && !"THEME_SLUG_OCCUPIED".equals(tL_error.text))) {
                setCheckText(LocaleController.formatString("SetUrlAvailable", NUM, str), "windowBackgroundWhiteGreenText");
                this.lastNameAvailable = true;
                return;
            }
            setCheckText(LocaleController.getString("SetUrlInUse", NUM), "windowBackgroundWhiteRedText4");
            this.lastNameAvailable = false;
        }
    }

    private void setCheckText(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), NUM, "windowBackgroundGrayShadow"));
            } else {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), NUM, "windowBackgroundGrayShadow"));
            }
        } else {
            this.checkInfoCell.setVisibility(0);
            this.checkInfoCell.setText(str);
            this.checkInfoCell.setTag(str2);
            this.checkInfoCell.setTextColor(str2);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), NUM, "windowBackgroundGrayShadow"));
            } else {
                this.helpInfoCell.setBackgroundDrawable((Drawable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveTheme() {
        if (!checkUrl(this.linkField.getText().toString(), true) || getParentActivity() == null) {
            return;
        }
        if (this.nameField.length() == 0) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNameInvalid", NUM));
        } else if (this.creatingNewTheme) {
            TLRPC.TL_theme tL_theme = this.info;
            String str = tL_theme.title;
            String str2 = tL_theme.slug;
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog.setOnCancelListener($$Lambda$ThemeSetUrlActivity$DJii0csNwFlvBjyKJ7HA1_lGQY.INSTANCE);
            this.progressDialog.show();
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            TLRPC.TL_theme tL_theme2 = this.info;
            String obj = this.nameField.getText().toString();
            tL_theme2.title = obj;
            themeInfo2.name = obj;
            this.themeInfo.info.slug = this.linkField.getText().toString();
            Theme.saveCurrentTheme(this.themeInfo, true, true, true);
        } else {
            String str3 = this.info.slug;
            String str4 = "";
            if (str3 == null) {
                str3 = str4;
            }
            String str5 = this.info.title;
            if (str5 != null) {
                str4 = str5;
            }
            String obj2 = this.linkField.getText().toString();
            String obj3 = this.nameField.getText().toString();
            if (!str3.equals(obj2) || !str4.equals(obj3)) {
                this.progressDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC.TL_account_updateTheme tL_account_updateTheme = new TLRPC.TL_account_updateTheme();
                TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
                TLRPC.TL_theme tL_theme3 = this.info;
                tL_inputTheme.id = tL_theme3.id;
                tL_inputTheme.access_hash = tL_theme3.access_hash;
                tL_account_updateTheme.theme = tL_inputTheme;
                tL_account_updateTheme.format = "android";
                tL_account_updateTheme.slug = obj2;
                tL_account_updateTheme.flags = 1 | tL_account_updateTheme.flags;
                tL_account_updateTheme.title = obj3;
                tL_account_updateTheme.flags |= 2;
                int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateTheme, new RequestDelegate(tL_account_updateTheme) {
                    private final /* synthetic */ TLRPC.TL_account_updateTheme f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ThemeSetUrlActivity.this.lambda$saveTheme$12$ThemeSetUrlActivity(this.f$1, tLObject, tL_error);
                    }
                }, 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
                this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(sendRequest) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onCancel(DialogInterface dialogInterface) {
                        ThemeSetUrlActivity.this.lambda$saveTheme$13$ThemeSetUrlActivity(this.f$1, dialogInterface);
                    }
                });
                this.progressDialog.show();
                return;
            }
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$saveTheme$12$ThemeSetUrlActivity(TLRPC.TL_account_updateTheme tL_account_updateTheme, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_theme) {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_theme) tLObject) {
                private final /* synthetic */ TLRPC.TL_theme f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ThemeSetUrlActivity.this.lambda$null$10$ThemeSetUrlActivity(this.f$1);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(tL_error, tL_account_updateTheme) {
                private final /* synthetic */ TLRPC.TL_error f$1;
                private final /* synthetic */ TLRPC.TL_account_updateTheme f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    ThemeSetUrlActivity.this.lambda$null$11$ThemeSetUrlActivity(this.f$1, this.f$2);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$10$ThemeSetUrlActivity(TLRPC.TL_theme tL_theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, this.themeAccent, tL_theme, this.currentAccount, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$11$ThemeSetUrlActivity(TLRPC.TL_error tL_error, TLRPC.TL_account_updateTheme tL_account_updateTheme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertsCreator.processError(this.currentAccount, tL_error, this, tL_account_updateTheme, new Object[0]);
    }

    public /* synthetic */ void lambda$saveTheme$13$ThemeSetUrlActivity(int i, DialogInterface dialogInterface) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(i, true);
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"), new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"), new ThemeDescription((View) this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.divider, 0, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable.getShadowDrawable(), Theme.chat_msgInMediaDrawable.getShadowDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable.getShadowDrawable(), Theme.chat_msgOutMediaDrawable.getShadowDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"), new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText")};
    }
}
