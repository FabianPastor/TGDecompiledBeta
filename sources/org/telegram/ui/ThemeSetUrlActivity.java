package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_account_createTheme;
import org.telegram.tgnet.TLRPC.TL_account_updateTheme;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputDocumentEmpty;
import org.telegram.tgnet.TLRPC.TL_inputTheme;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeSetUrlActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextInfoPrivacyCell checkInfoCell;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextSettingsCell createCell;
    private TextInfoPrivacyCell createInfoCell;
    private boolean creatingNewTheme;
    private View divider;
    private View doneButton;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private TextInfoPrivacyCell helpInfoCell;
    private boolean ignoreCheck;
    private TL_theme info;
    private CharSequence infoText;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayoutTypeContainer;
    private EditTextBoldCursor linkField;
    private ThemePreviewMessagesCell messagesCell;
    private EditTextBoldCursor nameField;
    private AlertDialog progressDialog;
    private ThemeAccent themeAccent;
    private ThemeInfo themeInfo;

    private static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        /* synthetic */ LinkMovementMethodMy(AnonymousClass1 anonymousClass1) {
            this();
        }

        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
            try {
                boolean onTouchEvent = super.onTouchEvent(textView, spannable, motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    Selection.removeSelection(spannable);
                }
                return onTouchEvent;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
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
                FileLog.e(e);
            }
        }
    }

    static /* synthetic */ void lambda$saveTheme$9(DialogInterface dialogInterface) {
    }

    public ThemeSetUrlActivity(ThemeInfo themeInfo, ThemeAccent themeAccent, boolean z) {
        this.themeInfo = themeInfo;
        this.themeAccent = themeAccent;
        this.info = themeAccent != null ? themeAccent.info : themeInfo.info;
        this.currentAccount = themeAccent != null ? themeAccent.account : themeInfo.account;
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeSetUrlActivity.this.finishFragment();
                } else if (i == 1) {
                    ThemeSetUrlActivity.this.saveTheme();
                }
            }
        });
        this.doneButton = this.actionBar.createMenu().addItem(1, LocaleController.getString("Done", NUM).toUpperCase());
        this.fragmentView = new LinearLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        LinearLayout linearLayout = (LinearLayout) this.fragmentView;
        linearLayout.setOrientation(1);
        this.fragmentView.setOnTouchListener(-$$Lambda$ThemeSetUrlActivity$_8Xemr4PmFieDLseu_SVMqLlMLs.INSTANCE);
        this.linearLayoutTypeContainer = new LinearLayout(context2);
        this.linearLayoutTypeContainer.setOrientation(1);
        this.linearLayoutTypeContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        linearLayout.addView(this.linearLayoutTypeContainer, LayoutHelper.createLinear(-1, -2));
        this.headerCell = new HeaderCell(context2, 23);
        this.headerCell.setText(LocaleController.getString("Info", NUM));
        this.linearLayoutTypeContainer.addView(this.headerCell);
        this.nameField = new EditTextBoldCursor(context2);
        this.nameField.setTextSize(1, 18.0f);
        String str = "windowBackgroundWhiteHintText";
        this.nameField.setHintTextColor(Theme.getColor(str));
        String str2 = "windowBackgroundWhiteBlackText";
        this.nameField.setTextColor(Theme.getColor(str2));
        this.nameField.setMaxLines(1);
        this.nameField.setLines(1);
        this.nameField.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.nameField.setBackgroundDrawable(null);
        this.nameField.setPadding(0, 0, 0, 0);
        this.nameField.setSingleLine(true);
        this.nameField.setFilters(new InputFilter[]{new LengthFilter(128)});
        this.nameField.setInputType(163872);
        this.nameField.setImeOptions(6);
        this.nameField.setHint(LocaleController.getString("ThemeNamePlaceholder", NUM));
        this.nameField.setCursorColor(Theme.getColor(str2));
        this.nameField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.nameField.setCursorWidth(1.5f);
        this.linearLayoutTypeContainer.addView(this.nameField, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.nameField.setOnEditorActionListener(new -$$Lambda$ThemeSetUrlActivity$NxGJz_o8mBYcP1uyM9LmgYiv0WU(this));
        this.divider = new View(context2) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        };
        this.linearLayoutTypeContainer.addView(this.divider, new LayoutParams(-1, 1));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        this.linearLayoutTypeContainer.addView(linearLayout2, LayoutHelper.createLinear(-1, 50, 23.0f, 0.0f, 23.0f, 0.0f));
        this.editText = new EditTextBoldCursor(context2);
        EditTextBoldCursor editTextBoldCursor = this.editText;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getMessagesController().linkPrefix);
        stringBuilder.append("/addtheme/");
        editTextBoldCursor.setText(stringBuilder.toString());
        this.editText.setTextSize(1, 18.0f);
        this.editText.setHintTextColor(Theme.getColor(str));
        this.editText.setTextColor(Theme.getColor(str2));
        this.editText.setMaxLines(1);
        this.editText.setLines(1);
        this.editText.setEnabled(false);
        this.editText.setBackgroundDrawable(null);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setSingleLine(true);
        this.editText.setInputType(163840);
        this.editText.setImeOptions(6);
        linearLayout2.addView(this.editText, LayoutHelper.createLinear(-2, 50));
        this.linkField = new EditTextBoldCursor(context2);
        this.linkField.setTextSize(1, 18.0f);
        this.linkField.setHintTextColor(Theme.getColor(str));
        this.linkField.setTextColor(Theme.getColor(str2));
        this.linkField.setMaxLines(1);
        this.linkField.setLines(1);
        this.linkField.setBackgroundDrawable(null);
        this.linkField.setPadding(0, 0, 0, 0);
        this.linkField.setSingleLine(true);
        this.linkField.setInputType(163872);
        this.linkField.setImeOptions(6);
        this.linkField.setHint(LocaleController.getString("SetUrlPlaceholder", NUM));
        this.linkField.setCursorColor(Theme.getColor(str2));
        this.linkField.setCursorSize(AndroidUtilities.dp(20.0f));
        this.linkField.setCursorWidth(1.5f);
        linearLayout2.addView(this.linkField, LayoutHelper.createLinear(-1, 50));
        this.linkField.setOnEditorActionListener(new -$$Lambda$ThemeSetUrlActivity$u33WvNZhJyBXiGrASqERpZgGbDY(this));
        this.linkField.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (!ThemeSetUrlActivity.this.ignoreCheck) {
                    ThemeSetUrlActivity themeSetUrlActivity = ThemeSetUrlActivity.this;
                    themeSetUrlActivity.checkUrl(themeSetUrlActivity.linkField.getText().toString(), false);
                }
            }

            public void afterTextChanged(Editable editable) {
                if (!ThemeSetUrlActivity.this.creatingNewTheme) {
                    if (ThemeSetUrlActivity.this.linkField.length() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("https://");
                        stringBuilder.append(ThemeSetUrlActivity.this.getMessagesController().linkPrefix);
                        stringBuilder.append("/addtheme/");
                        stringBuilder.append(ThemeSetUrlActivity.this.linkField.getText());
                        String stringBuilder2 = stringBuilder.toString();
                        String formatString = LocaleController.formatString("ThemeHelpLink", NUM, stringBuilder2);
                        int indexOf = formatString.indexOf(stringBuilder2);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(formatString);
                        if (indexOf >= 0) {
                            spannableStringBuilder.setSpan(new LinkSpan(stringBuilder2), indexOf, stringBuilder2.length() + indexOf, 33);
                        }
                        ThemeSetUrlActivity.this.helpInfoCell.setText(TextUtils.concat(new CharSequence[]{ThemeSetUrlActivity.this.infoText, "\n\n", spannableStringBuilder}));
                    } else {
                        ThemeSetUrlActivity.this.helpInfoCell.setText(ThemeSetUrlActivity.this.infoText);
                    }
                }
            }
        });
        if (this.creatingNewTheme) {
            this.linkField.setOnFocusChangeListener(new -$$Lambda$ThemeSetUrlActivity$mmEfyMJC0slMeWipaD_BgkkNjis(this));
        }
        this.checkInfoCell = new TextInfoPrivacyCell(context2);
        String str3 = "windowBackgroundGrayShadow";
        this.checkInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str3));
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
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str3));
            this.messagesCell = new ThemePreviewMessagesCell(context2, this.parentLayout, 1);
            linearLayout.addView(this.messagesCell, LayoutHelper.createLinear(-1, -2));
            this.createCell = new TextSettingsCell(context2);
            this.createCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.createCell.setText(LocaleController.getString("UseDifferentTheme", NUM), false);
            linearLayout.addView(this.createCell, LayoutHelper.createLinear(-1, -2));
            this.createCell.setOnClickListener(new -$$Lambda$ThemeSetUrlActivity$-1pVHv1AVzkRjo-UVpuW4MMulwg(this, context2));
            this.createInfoCell = new TextInfoPrivacyCell(context2);
            this.createInfoCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("UseDifferentThemeInfo", NUM)));
            this.createInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str3));
            linearLayout.addView(this.createInfoCell, LayoutHelper.createLinear(-1, -2));
        } else {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str3));
        }
        TL_theme tL_theme = this.info;
        if (tL_theme != null) {
            this.ignoreCheck = true;
            this.nameField.setText(tL_theme.title);
            EditTextBoldCursor editTextBoldCursor2 = this.nameField;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
            this.linkField.setText(this.info.slug);
            editTextBoldCursor2 = this.linkField;
            editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
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
        if (i == 6) {
            View view = this.doneButton;
            if (view != null) {
                view.performClick();
                return true;
            }
        }
        return false;
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
            Builder builder = new Builder(getParentActivity(), false);
            builder.setApplyBottomPadding(false);
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(1);
            TextView textView = new TextView(context2);
            textView.setText(LocaleController.getString("ChooseTheme", NUM));
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 51, 22, 12, 22, 4));
            textView.setOnTouchListener(-$$Lambda$ThemeSetUrlActivity$QxZyJw-LHHcW9tcx8U4a2QYVzp0.INSTANCE);
            builder.setCustomView(linearLayout);
            ArrayList arrayList = new ArrayList();
            int size = Theme.themes.size();
            for (int i = 0; i < size; i++) {
                ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(i);
                TL_theme tL_theme = themeInfo.info;
                if (tL_theme == null || tL_theme.document != null) {
                    arrayList.add(themeInfo);
                }
            }
            final Builder builder2 = builder;
            AnonymousClass4 anonymousClass4 = new ThemesHorizontalListCell(context, 2, arrayList, new ArrayList()) {
                /* Access modifiers changed, original: protected */
                public void updateRows() {
                    builder2.getDismissRunnable().run();
                }
            };
            linearLayout.addView(anonymousClass4, LayoutHelper.createLinear(-1, 148, 0.0f, 7.0f, 0.0f, 1.0f));
            anonymousClass4.scrollToCurrentTheme(this.fragmentView.getMeasuredWidth(), false);
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
        ThemeAccent themeAccent;
        AlertDialog alertDialog;
        if (i == NotificationCenter.themeUploadedToServer) {
            themeAccent = (ThemeAccent) objArr[1];
            if (((ThemeInfo) objArr[0]) == this.themeInfo && themeAccent == this.themeAccent) {
                alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    try {
                        alertDialog.dismiss();
                        this.progressDialog = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    Theme.applyTheme(this.themeInfo, false);
                    finishFragment();
                }
            }
        } else if (i == NotificationCenter.themeUploadError) {
            themeAccent = (ThemeAccent) objArr[1];
            if (((ThemeInfo) objArr[0]) == this.themeInfo && themeAccent == this.themeAccent) {
                alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    try {
                        alertDialog.dismiss();
                        this.progressDialog = null;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Missing block: B:55:0x00e0, code skipped:
            if (r13 != null) goto L_0x00e5;
     */
    private boolean checkUrl(java.lang.String r12, boolean r13) {
        /*
        r11 = this;
        r0 = r11.checkRunnable;
        r1 = 1;
        if (r0 == 0) goto L_0x001c;
    L_0x0005:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r0 = 0;
        r11.checkRunnable = r0;
        r11.lastCheckName = r0;
        r0 = r11.checkReqId;
        if (r0 == 0) goto L_0x001c;
    L_0x0011:
        r0 = r11.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = r11.checkReqId;
        r0.cancelRequest(r2, r1);
    L_0x001c:
        r0 = 0;
        r11.lastNameAvailable = r0;
        r2 = NUM; // 0x7f0e0afa float:1.8880737E38 double:1.053163545E-314;
        r3 = "Theme";
        r4 = "windowBackgroundWhiteRedText4";
        if (r12 == 0) goto L_0x00ab;
    L_0x0029:
        r5 = "_";
        r6 = r12.startsWith(r5);
        r7 = NUM; // 0x7f0e0a38 float:1.8880343E38 double:1.053163449E-314;
        r8 = "SetUrlInvalid";
        if (r6 != 0) goto L_0x00a3;
    L_0x0036:
        r5 = r12.endsWith(r5);
        if (r5 == 0) goto L_0x003d;
    L_0x003c:
        goto L_0x00a3;
    L_0x003d:
        r5 = 0;
    L_0x003e:
        r6 = r12.length();
        if (r5 >= r6) goto L_0x00ab;
    L_0x0044:
        r6 = r12.charAt(r5);
        r9 = 57;
        r10 = 48;
        if (r5 != 0) goto L_0x0072;
    L_0x004e:
        if (r6 < r10) goto L_0x0072;
    L_0x0050:
        if (r6 > r9) goto L_0x0072;
    L_0x0052:
        if (r13 == 0) goto L_0x0065;
    L_0x0054:
        r12 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r13 = NUM; // 0x7f0e0a3b float:1.888035E38 double:1.0531634506E-314;
        r1 = "SetUrlInvalidStartNumber";
        r13 = org.telegram.messenger.LocaleController.getString(r1, r13);
        org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r11, r12, r13);
        goto L_0x0071;
    L_0x0065:
        r12 = NUM; // 0x7f0e0a3b float:1.888035E38 double:1.0531634506E-314;
        r13 = "SetUrlInvalidStartNumber";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        r11.setCheckText(r12, r4);
    L_0x0071:
        return r0;
    L_0x0072:
        if (r6 < r10) goto L_0x0076;
    L_0x0074:
        if (r6 <= r9) goto L_0x00a0;
    L_0x0076:
        r9 = 97;
        if (r6 < r9) goto L_0x007e;
    L_0x007a:
        r9 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r6 <= r9) goto L_0x00a0;
    L_0x007e:
        r9 = 65;
        if (r6 < r9) goto L_0x0086;
    L_0x0082:
        r9 = 90;
        if (r6 <= r9) goto L_0x00a0;
    L_0x0086:
        r9 = 95;
        if (r6 == r9) goto L_0x00a0;
    L_0x008a:
        if (r13 == 0) goto L_0x0098;
    L_0x008c:
        r12 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r13 = org.telegram.messenger.LocaleController.getString(r8, r7);
        org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r11, r12, r13);
        goto L_0x009f;
    L_0x0098:
        r12 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r11.setCheckText(r12, r4);
    L_0x009f:
        return r0;
    L_0x00a0:
        r5 = r5 + 1;
        goto L_0x003e;
    L_0x00a3:
        r12 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r11.setCheckText(r12, r4);
        return r0;
    L_0x00ab:
        if (r12 == 0) goto L_0x011f;
    L_0x00ad:
        r5 = r12.length();
        r6 = 5;
        if (r5 >= r6) goto L_0x00b5;
    L_0x00b4:
        goto L_0x011f;
    L_0x00b5:
        r5 = r12.length();
        r6 = 64;
        if (r5 <= r6) goto L_0x00d8;
    L_0x00bd:
        r12 = NUM; // 0x7f0e0a39 float:1.8880345E38 double:1.0531634496E-314;
        r1 = "SetUrlInvalidLong";
        if (r13 == 0) goto L_0x00d0;
    L_0x00c4:
        r13 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r12 = org.telegram.messenger.LocaleController.getString(r1, r12);
        org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r11, r13, r12);
        goto L_0x00d7;
    L_0x00d0:
        r12 = org.telegram.messenger.LocaleController.getString(r1, r12);
        r11.setCheckText(r12, r4);
    L_0x00d7:
        return r0;
    L_0x00d8:
        if (r13 != 0) goto L_0x011e;
    L_0x00da:
        r13 = r11.info;
        if (r13 == 0) goto L_0x00e3;
    L_0x00de:
        r13 = r13.slug;
        if (r13 == 0) goto L_0x00e3;
    L_0x00e2:
        goto L_0x00e5;
    L_0x00e3:
        r13 = "";
    L_0x00e5:
        r13 = r12.equals(r13);
        if (r13 == 0) goto L_0x00ff;
    L_0x00eb:
        r13 = NUM; // 0x7f0e0a35 float:1.8880337E38 double:1.0531634476E-314;
        r2 = new java.lang.Object[r1];
        r2[r0] = r12;
        r12 = "SetUrlAvailable";
        r12 = org.telegram.messenger.LocaleController.formatString(r12, r13, r2);
        r13 = "windowBackgroundWhiteGreenText";
        r11.setCheckText(r12, r13);
        return r1;
    L_0x00ff:
        r13 = NUM; // 0x7f0e0a36 float:1.888034E38 double:1.053163448E-314;
        r0 = "SetUrlChecking";
        r13 = org.telegram.messenger.LocaleController.getString(r0, r13);
        r0 = "windowBackgroundWhiteGrayText8";
        r11.setCheckText(r13, r0);
        r11.lastCheckName = r12;
        r13 = new org.telegram.ui.-$$Lambda$ThemeSetUrlActivity$BOI1OGZjC9ZyL0GYbdTT_fDxcTk;
        r13.<init>(r11, r12);
        r11.checkRunnable = r13;
        r12 = r11.checkRunnable;
        r2 = 300; // 0x12c float:4.2E-43 double:1.48E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r12, r2);
    L_0x011e:
        return r1;
    L_0x011f:
        r12 = NUM; // 0x7f0e0a3a float:1.8880347E38 double:1.05316345E-314;
        r1 = "SetUrlInvalidShort";
        if (r13 == 0) goto L_0x0132;
    L_0x0126:
        r13 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r12 = org.telegram.messenger.LocaleController.getString(r1, r12);
        org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r11, r13, r12);
        goto L_0x0139;
    L_0x0132:
        r12 = org.telegram.messenger.LocaleController.getString(r1, r12);
        r11.setCheckText(r12, r4);
    L_0x0139:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeSetUrlActivity.checkUrl(java.lang.String, boolean):boolean");
    }

    public /* synthetic */ void lambda$checkUrl$8$ThemeSetUrlActivity(String str) {
        TL_account_createTheme tL_account_createTheme = new TL_account_createTheme();
        tL_account_createTheme.slug = str;
        tL_account_createTheme.title = "";
        tL_account_createTheme.document = new TL_inputDocumentEmpty();
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_createTheme, new -$$Lambda$ThemeSetUrlActivity$aQbtyL09ngk96Vga3xgJIqv0TIQ(this, str), 2);
    }

    public /* synthetic */ void lambda$null$7$ThemeSetUrlActivity(String str, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeSetUrlActivity$JnRqO6SkySlZJw_vX9T7S1fGtGs(this, str, tL_error));
    }

    /* JADX WARNING: Missing block: B:8:0x0021, code skipped:
            if ("THEME_SLUG_OCCUPIED".equals(r5.text) == false) goto L_0x0036;
     */
    public /* synthetic */ void lambda$null$6$ThemeSetUrlActivity(java.lang.String r4, org.telegram.tgnet.TLRPC.TL_error r5) {
        /*
        r3 = this;
        r0 = 0;
        r3.checkReqId = r0;
        r1 = r3.lastCheckName;
        if (r1 == 0) goto L_0x004c;
    L_0x0007:
        r1 = r1.equals(r4);
        if (r1 == 0) goto L_0x004c;
    L_0x000d:
        if (r5 == 0) goto L_0x0036;
    L_0x000f:
        r1 = r5.text;
        r2 = "THEME_SLUG_INVALID";
        r1 = r2.equals(r1);
        if (r1 != 0) goto L_0x0024;
    L_0x0019:
        r5 = r5.text;
        r1 = "THEME_SLUG_OCCUPIED";
        r5 = r1.equals(r5);
        if (r5 != 0) goto L_0x0024;
    L_0x0023:
        goto L_0x0036;
    L_0x0024:
        r4 = NUM; // 0x7f0e0a37 float:1.8880341E38 double:1.0531634486E-314;
        r5 = "SetUrlInUse";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = "windowBackgroundWhiteRedText4";
        r3.setCheckText(r4, r5);
        r3.lastNameAvailable = r0;
        goto L_0x004c;
    L_0x0036:
        r5 = NUM; // 0x7f0e0a35 float:1.8880337E38 double:1.0531634476E-314;
        r1 = 1;
        r2 = new java.lang.Object[r1];
        r2[r0] = r4;
        r4 = "SetUrlAvailable";
        r4 = org.telegram.messenger.LocaleController.formatString(r4, r5, r2);
        r5 = "windowBackgroundWhiteGreenText";
        r3.setCheckText(r4, r5);
        r3.lastNameAvailable = r1;
    L_0x004c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeSetUrlActivity.lambda$null$6$ThemeSetUrlActivity(java.lang.String, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    private void setCheckText(String str, String str2) {
        String str3 = "windowBackgroundGrayShadow";
        if (TextUtils.isEmpty(str)) {
            this.checkInfoCell.setVisibility(8);
            if (this.creatingNewTheme) {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
                return;
            } else {
                this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
                return;
            }
        }
        this.checkInfoCell.setVisibility(0);
        this.checkInfoCell.setText(str);
        this.checkInfoCell.setTag(str2);
        this.checkInfoCell.setTextColor(str2);
        if (this.creatingNewTheme) {
            this.helpInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(getParentActivity(), NUM, str3));
        } else {
            this.helpInfoCell.setBackgroundDrawable(null);
        }
    }

    private void saveTheme() {
        if (!checkUrl(this.linkField.getText().toString(), true) || getParentActivity() == null) {
            return;
        }
        String str;
        String str2;
        if (this.nameField.length() == 0) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("Theme", NUM), LocaleController.getString("ThemeNameInvalid", NUM));
        } else if (this.creatingNewTheme) {
            TL_theme tL_theme = this.info;
            str = tL_theme.title;
            str2 = tL_theme.slug;
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog.setOnCancelListener(-$$Lambda$ThemeSetUrlActivity$DJii0csNwFlvBjy-KJ7HA1_lGQY.INSTANCE);
            this.progressDialog.show();
            ThemeInfo themeInfo = this.themeInfo;
            TL_theme tL_theme2 = this.info;
            str = this.nameField.getText().toString();
            tL_theme2.title = str;
            themeInfo.name = str;
            this.themeInfo.info.slug = this.linkField.getText().toString();
            Theme.saveCurrentTheme(this.themeInfo, true, true, true);
        } else {
            str2 = this.info.slug;
            str = "";
            if (str2 == null) {
                str2 = str;
            }
            String str3 = this.info.title;
            if (str3 != null) {
                str = str3;
            }
            str3 = this.linkField.getText().toString();
            String obj = this.nameField.getText().toString();
            if (str2.equals(str3) && r3.equals(obj)) {
                finishFragment();
                return;
            }
            this.progressDialog = new AlertDialog(getParentActivity(), 3);
            TL_account_updateTheme tL_account_updateTheme = new TL_account_updateTheme();
            TL_inputTheme tL_inputTheme = new TL_inputTheme();
            TL_theme tL_theme3 = this.info;
            tL_inputTheme.id = tL_theme3.id;
            tL_inputTheme.access_hash = tL_theme3.access_hash;
            tL_account_updateTheme.theme = tL_inputTheme;
            tL_account_updateTheme.format = "android";
            tL_account_updateTheme.slug = str3;
            tL_account_updateTheme.flags = 1 | tL_account_updateTheme.flags;
            tL_account_updateTheme.title = obj;
            tL_account_updateTheme.flags |= 2;
            int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_updateTheme, new -$$Lambda$ThemeSetUrlActivity$RZNy0C-blX8HMmj88uQdPDrohSI(this, tL_account_updateTheme), 2);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(sendRequest, this.classGuid);
            this.progressDialog.setOnCancelListener(new -$$Lambda$ThemeSetUrlActivity$-rj0xYcSJRokmADGaQlIk9tW0x0(this, sendRequest));
            this.progressDialog.show();
        }
    }

    public /* synthetic */ void lambda$saveTheme$12$ThemeSetUrlActivity(TL_account_updateTheme tL_account_updateTheme, TLObject tLObject, TL_error tL_error) {
        if (tLObject instanceof TL_theme) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeSetUrlActivity$S69Vk5sv8sqRdHEH8Qfy_7_nJuA(this, (TL_theme) tLObject));
        } else {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeSetUrlActivity$OFM5DeupJh-qZ2ccB6uZLjCNlMI(this, tL_error, tL_account_updateTheme));
        }
    }

    public /* synthetic */ void lambda$null$10$ThemeSetUrlActivity(TL_theme tL_theme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        Theme.setThemeUploadInfo(this.themeInfo, this.themeAccent, tL_theme, this.currentAccount, false);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$11$ThemeSetUrlActivity(TL_error tL_error, TL_account_updateTheme tL_account_updateTheme) {
        try {
            this.progressDialog.dismiss();
            this.progressDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
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
        r1 = new ThemeDescription[58];
        View view = this.headerCell;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[6] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        r1[7] = new ThemeDescription(this.createInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[8] = new ThemeDescription(this.createInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r1[9] = new ThemeDescription(this.helpInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[10] = new ThemeDescription(this.helpInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r1[11] = new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[12] = new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText4");
        r1[13] = new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText8");
        r1[14] = new ThemeDescription(this.checkInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGreenText");
        r1[15] = new ThemeDescription(this.createCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[16] = new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21");
        r1[17] = new ThemeDescription(this.createCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite");
        r1[18] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[19] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r1[20] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
        r1[21] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
        r1[22] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[23] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r1[24] = new ThemeDescription(this.linkField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[25] = new ThemeDescription(this.nameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[26] = new ThemeDescription(this.nameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r1[27] = new ThemeDescription(this.nameField, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[28] = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
        r1[29] = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText");
        r1[30] = new ThemeDescription(this.divider, 0, null, Theme.dividerPaint, null, null, "divider");
        r1[31] = new ThemeDescription(this.divider, ThemeDescription.FLAG_BACKGROUND, null, Theme.dividerPaint, null, null, "divider");
        r1[32] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        r1[33] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        r1[34] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable.getShadowDrawable(), Theme.chat_msgInMediaDrawable.getShadowDrawable()}, null, "chat_inBubbleShadow");
        r1[35] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        r1[36] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient");
        r1[37] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        r1[38] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable.getShadowDrawable(), Theme.chat_msgOutMediaDrawable.getShadowDrawable()}, null, "chat_outBubbleShadow");
        r1[39] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_messageTextIn");
        r1[40] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_messageTextOut");
        r1[41] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        r1[42] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        r1[43] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        r1[44] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        r1[45] = new ThemeDescription(this.messagesCell, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        r1[46] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inReplyLine");
        r1[47] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outReplyLine");
        r1[48] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inReplyNameText");
        r1[49] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outReplyNameText");
        r1[50] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inReplyMessageText");
        r1[51] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outReplyMessageText");
        r1[52] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        r1[53] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        r1[54] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inTimeText");
        r1[55] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outTimeText");
        r1[56] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_inTimeSelectedText");
        r1[57] = new ThemeDescription(this.messagesCell, 0, null, null, null, null, "chat_outTimeSelectedText");
        return r1;
    }
}
