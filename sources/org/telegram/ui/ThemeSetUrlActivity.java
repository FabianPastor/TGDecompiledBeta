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
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
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
import org.telegram.ui.Components.BulletinFactory;
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

    public class LinkSpan extends ClickableSpan {
        private String url;

        public LinkSpan(String value) {
            this.url = value;
        }

        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        public void onClick(View widget) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
                if (BulletinFactory.canShowBulletin(ThemeSetUrlActivity.this)) {
                    BulletinFactory.createCopyLinkBulletin((BaseFragment) ThemeSetUrlActivity.this).show();
                }
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                return false;
            }
        }
    }

    public ThemeSetUrlActivity(Theme.ThemeInfo theme, Theme.ThemeAccent accent, boolean newTheme) {
        this.themeInfo = theme;
        this.themeAccent = accent;
        this.info = accent != null ? accent.info : theme.info;
        this.currentAccount = accent != null ? accent.account : theme.account;
        this.creatingNewTheme = newTheme;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    ThemeSetUrlActivity.this.finishFragment();
                } else if (id == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        this.fragmentView = new LinearLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(ThemeSetUrlActivity$$ExternalSyntheticLambda8.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        this.linearLayoutTypeContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        HeaderCell headerCell2 = new HeaderCell(context2, 23);
        this.headerCell = headerCell2;
        headerCell2.setText(LocaleController.getString("Info", NUM));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        this.nameField = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
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
        this.nameField.setOnEditorActionListener(new ThemeSetUrlActivity$$ExternalSyntheticLambda10(this));
        AnonymousClass2 r7 = new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        };
        this.divider = r7;
        this.linearLayoutTypeContainer.addView(r7, new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linkContainer = new LinearLayout(context2);
        linkContainer.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linkContainer, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context2);
        this.editText = editTextBoldCursor2;
        editTextBoldCursor2.setText(getMessagesController().linkPrefix + "/addtheme/");
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
        linkContainer.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context2);
        this.linkField = editTextBoldCursor3;
        editTextBoldCursor3.setTextSize(1, 18.0f);
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
        linkContainer.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new ThemeSetUrlActivity$$ExternalSyntheticLambda11(this));
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
                        String url = "https://" + ThemeSetUrlActivity.this.getMessagesController().linkPrefix + "/addtheme/" + ThemeSetUrlActivity.this.linkField.getText();
                        String text = LocaleController.formatString("ThemeHelpLink", NUM, url);
                        int index = text.indexOf(url);
                        SpannableStringBuilder textSpan = new SpannableStringBuilder(text);
                        if (index >= 0) {
                            textSpan.setSpan(new LinkSpan(url), index, url.length() + index, 33);
                        }
                        ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(new CharSequence[]{ThemeSetUrlActivity.this.infoText, "\n\n", textSpan}));
                        return;
                    }
                    ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                }
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new ThemeSetUrlActivity$$ExternalSyntheticLambda7(this));
        }
        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        this.checkInfoCell = textInfoPrivacyCell;
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.checkInfoCell.setVisibility(8);
        this.checkInfoCell.setBottomPadding(0);
        linearLayout.addView(this.checkInfoCell, LayoutHelper.createLinear(-1, -2));
        TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context2);
        this.helpInfoCell = textInfoPrivacyCell2;
        textInfoPrivacyCell2.getTextView().setMovementMethod(new LinkMovementMethodMy());
        this.helpInfoCell.getTextView().setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", NUM)));
        } else {
            TextInfoPrivacyCell textInfoPrivacyCell3 = this.helpInfoCell;
            SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString("ThemeSetUrlHelp", NUM));
            this.infoText = replaceTags;
            textInfoPrivacyCell3.setText(replaceTags);
        }
        linearLayout.addView(this.helpInfoCell, LayoutHelper.createLinear(-1, -2));
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context2, this.parentLayout, 1);
            this.messagesCell = themePreviewMessagesCell;
            linearLayout.addView(themePreviewMessagesCell, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context2);
            this.createCell = textSettingsCell;
            textSettingsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString("UseDifferentTheme", NUM), false);
            linearLayout.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new ThemeSetUrlActivity$$ExternalSyntheticLambda6(this, context2));
            TextInfoPrivacyCell textInfoPrivacyCell4 = new TextInfoPrivacyCell(context2);
            this.createInfoCell = textInfoPrivacyCell4;
            textInfoPrivacyCell4.setText(AndroidUtilities.replaceTags(LocaleController.getString("UseDifferentThemeInfo", NUM)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            linearLayout.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        }
        TLRPC.TL_theme tL_theme = this.info;
        if (tL_theme != null) {
            this.ignoreCheck = true;
            this.nameField.setText(tL_theme.title);
            EditTextBoldCursor editTextBoldCursor4 = this.nameField;
            editTextBoldCursor4.setSelection(editTextBoldCursor4.length());
            this.linkField.setText(this.info.slug);
            EditTextBoldCursor editTextBoldCursor5 = this.linkField;
            editTextBoldCursor5.setSelection(editTextBoldCursor5.length());
            this.ignoreCheck = false;
        }
        return this.fragmentView;
    }

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ boolean m3954lambda$createView$1$orgtelegramuiThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.nameField);
        return true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ boolean m3955lambda$createView$2$orgtelegramuiThemeSetUrlActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3956lambda$createView$3$orgtelegramuiThemeSetUrlActivity(View v, boolean hasFocus) {
        if (hasFocus) {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp2", NUM)));
        } else {
            this.helpInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("ThemeCreateHelp", NUM)));
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3957lambda$createView$5$orgtelegramuiThemeSetUrlActivity(Context context, View v) {
        Context context2 = context;
        if (getParentActivity() != null) {
            BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false);
            builder.setApplyBottomPadding(false);
            LinearLayout container = new LinearLayout(context2);
            container.setOrientation(1);
            TextView titleView = new TextView(context2);
            titleView.setText(LocaleController.getString("ChooseTheme", NUM));
            titleView.setTextColor(Theme.getColor("dialogTextBlack"));
            titleView.setTextSize(1, 20.0f);
            titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            container.addView(titleView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
            titleView.setOnTouchListener(ThemeSetUrlActivity$$ExternalSyntheticLambda9.INSTANCE);
            builder.setCustomView(container);
            ArrayList<Theme.ThemeInfo> themes = new ArrayList<>();
            int N = Theme.themes.size();
            for (int a = 0; a < N; a++) {
                Theme.ThemeInfo themeInfo2 = Theme.themes.get(a);
                if (themeInfo2.info == null || themeInfo2.info.document != null) {
                    themes.add(themeInfo2);
                }
            }
            final BottomSheet.Builder builder2 = builder;
            ThemesHorizontalListCell cell = new ThemesHorizontalListCell(context, 2, themes, new ArrayList()) {
                /* access modifiers changed from: protected */
                public void updateRows() {
                    builder2.getDismissRunnable().run();
                }
            };
            container.addView(cell, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
            cell.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
            showDialog(builder.create());
        }
    }

    static /* synthetic */ boolean lambda$createView$4(View v2, MotionEvent event) {
        return true;
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

    public void didReceivedNotification(int id, int account, Object... args) {
        AlertDialog alertDialog;
        AlertDialog alertDialog2;
        if (id == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo theme = args[0];
            Theme.ThemeAccent accent = args[1];
            if (theme == this.themeInfo && accent == this.themeAccent && (alertDialog2 = this.progressDialog) != null) {
                try {
                    alertDialog2.dismiss();
                    this.progressDialog = null;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
                Theme.applyTheme(this.themeInfo, false);
                finishFragment();
            }
        } else if (id == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo theme2 = args[0];
            Theme.ThemeAccent accent2 = args[1];
            if (theme2 == this.themeInfo && accent2 == this.themeAccent && (alertDialog = this.progressDialog) != null) {
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
    public boolean checkUrl(String url, boolean alert) {
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
        if (url != null) {
            if (url.startsWith("_") || url.endsWith("_")) {
                setCheckText(LocaleController.getString("SetUrlInvalid", NUM), "windowBackgroundWhiteRedText4");
                return false;
            }
            int a = 0;
            while (a < url.length()) {
                char ch = url.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidStartNumber", NUM));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalidStartNumber", NUM), "windowBackgroundWhiteRedText4");
                    }
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    if (alert) {
                        AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalid", NUM));
                    } else {
                        setCheckText(LocaleController.getString("SetUrlInvalid", NUM), "windowBackgroundWhiteRedText4");
                    }
                    return false;
                } else {
                    a++;
                }
            }
        }
        if (url == null || url.length() < 5) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidShort", NUM));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidShort", NUM), "windowBackgroundWhiteRedText4");
            }
            return false;
        } else if (url.length() > 64) {
            if (alert) {
                AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("SetUrlInvalidLong", NUM));
            } else {
                setCheckText(LocaleController.getString("SetUrlInvalidLong", NUM), "windowBackgroundWhiteRedText4");
            }
            return false;
        } else {
            if (!alert) {
                TLRPC.TL_theme tL_theme = this.info;
                if (url.equals((tL_theme == null || tL_theme.slug == null) ? "" : this.info.slug)) {
                    setCheckText(LocaleController.formatString("SetUrlAvailable", NUM, url), "windowBackgroundWhiteGreenText");
                    return true;
                }
                setCheckText(LocaleController.getString("SetUrlChecking", NUM), "windowBackgroundWhiteGrayText8");
                this.lastCheckName = url;
                ThemeSetUrlActivity$$ExternalSyntheticLambda12 themeSetUrlActivity$$ExternalSyntheticLambda12 = new ThemeSetUrlActivity$$ExternalSyntheticLambda12(this, url);
                this.checkRunnable = themeSetUrlActivity$$ExternalSyntheticLambda12;
                AndroidUtilities.runOnUIThread(themeSetUrlActivity$$ExternalSyntheticLambda12, 300);
            }
            return true;
        }
    }

    /* renamed from: lambda$checkUrl$8$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3953lambda$checkUrl$8$orgtelegramuiThemeSetUrlActivity(String url) {
        TLRPC.TL_account_createTheme req = new TLRPC.TL_account_createTheme();
        req.slug = url;
        req.title = "";
        req.document = new TLRPC.TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ThemeSetUrlActivity$$ExternalSyntheticLambda3(this, url), 2);
    }

    /* renamed from: lambda$checkUrl$7$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3952lambda$checkUrl$7$orgtelegramuiThemeSetUrlActivity(String url, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new ThemeSetUrlActivity$$ExternalSyntheticLambda13(this, url, error));
    }

    /* renamed from: lambda$checkUrl$6$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3951lambda$checkUrl$6$orgtelegramuiThemeSetUrlActivity(String url, TLRPC.TL_error error) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(url)) {
            if (error == null || (!"THEME_SLUG_INVALID".equals(error.text) && !"THEME_SLUG_OCCUPIED".equals(error.text))) {
                setCheckText(LocaleController.formatString("SetUrlAvailable", NUM, url), "windowBackgroundWhiteGreenText");
                this.lastNameAvailable = true;
                return;
            }
            setCheckText(LocaleController.getString("SetUrlInUse", NUM), "windowBackgroundWhiteRedText4");
            this.lastNameAvailable = false;
        }
    }

    private void setCheckText(String text, String colorKey) {
        if (TextUtils.isEmpty(text)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), NUM, "windowBackgroundGrayShadow"));
            } else {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable((Context) getParentActivity(), NUM, "windowBackgroundGrayShadow"));
            }
        } else {
            this.checkInfoCell.setVisibility(0);
            this.checkInfoCell.setText(text);
            this.checkInfoCell.setTag(colorKey);
            this.checkInfoCell.setTextColor(colorKey);
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
            String str = this.info.title;
            String str2 = this.info.slug;
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setOnCancelListener(ThemeSetUrlActivity$$ExternalSyntheticLambda5.INSTANCE);
            this.progressDialog.show();
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            TLRPC.TL_theme tL_theme = this.info;
            String obj = this.nameField.getText().toString();
            tL_theme.title = obj;
            themeInfo2.name = obj;
            this.themeInfo.info.slug = this.linkField.getText().toString();
            Theme.saveCurrentTheme(this.themeInfo, true, true, true);
        } else {
            String currentName = "";
            String currentUrl = this.info.slug == null ? currentName : this.info.slug;
            if (this.info.title != null) {
                currentName = this.info.title;
            }
            String newUrl = this.linkField.getText().toString();
            String newName = this.nameField.getText().toString();
            if (!currentUrl.equals(newUrl) || !currentName.equals(newName)) {
                this.progressDialog = new AlertDialog(getParentActivity(), 3);
                TLRPC.TL_account_updateTheme req = new TLRPC.TL_account_updateTheme();
                TLRPC.TL_inputTheme inputTheme = new TLRPC.TL_inputTheme();
                inputTheme.id = this.info.id;
                inputTheme.access_hash = this.info.access_hash;
                req.theme = inputTheme;
                req.format = "android";
                req.slug = newUrl;
                req.flags = 1 | req.flags;
                req.title = newName;
                req.flags |= 2;
                int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ThemeSetUrlActivity$$ExternalSyntheticLambda4(this, req), 2);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
                this.progressDialog.setOnCancelListener(new ThemeSetUrlActivity$$ExternalSyntheticLambda0(this, reqId));
                this.progressDialog.show();
                return;
            }
            finishFragment();
        }
    }

    static /* synthetic */ void lambda$saveTheme$9(DialogInterface dialog) {
    }

    /* renamed from: lambda$saveTheme$12$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3960lambda$saveTheme$12$orgtelegramuiThemeSetUrlActivity(TLRPC.TL_account_updateTheme req, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_theme) {
            AndroidUtilities.runOnUIThread(new ThemeSetUrlActivity$$ExternalSyntheticLambda2(this, (TLRPC.TL_theme) response));
        } else {
            AndroidUtilities.runOnUIThread(new ThemeSetUrlActivity$$ExternalSyntheticLambda1(this, error, req));
        }
    }

    /* renamed from: lambda$saveTheme$10$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3958lambda$saveTheme$10$orgtelegramuiThemeSetUrlActivity(TLRPC.TL_theme theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, this.themeAccent, theme, this.currentAccount, false);
        finishFragment();
    }

    /* renamed from: lambda$saveTheme$11$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3959lambda$saveTheme$11$orgtelegramuiThemeSetUrlActivity(TLRPC.TL_error error, TLRPC.TL_account_updateTheme req) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
        AlertsCreator.processError(this.currentAccount, error, this, req, new Object[0]);
    }

    /* renamed from: lambda$saveTheme$13$org-telegram-ui-ThemeSetUrlActivity  reason: not valid java name */
    public /* synthetic */ void m3961lambda$saveTheme$13$orgtelegramuiThemeSetUrlActivity(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !this.creatingNewTheme) {
            this.linkField.requestFocus();
            AndroidUtilities.showKeyboard(this.linkField);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.linearLayoutTypeContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText4"));
        themeDescriptions.add(new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText8"));
        themeDescriptions.add(new ThemeDescription((View) this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGreenText"));
        themeDescriptions.add(new ThemeDescription((View) this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputField"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteInputFieldActivated"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        themeDescriptions.add(new ThemeDescription(this.divider, 0, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.messagesCell, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        return themeDescriptions;
    }
}
