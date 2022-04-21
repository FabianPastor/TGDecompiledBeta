package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ProxySettingsActivity extends BaseFragment {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_SECRET = 4;
    private static final int FIELD_USER = 2;
    private static final int TYPE_MTPROTO = 1;
    private static final int TYPE_SOCKS5 = 0;
    private static final int done_button = 1;
    /* access modifiers changed from: private */
    public boolean addingNewProxy;
    private TextInfoPrivacyCell[] bottomCells;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener;
    private ClipboardManager clipboardManager;
    /* access modifiers changed from: private */
    public SharedConfig.ProxyInfo currentProxyInfo;
    /* access modifiers changed from: private */
    public int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    /* access modifiers changed from: private */
    public boolean ignoreOnTextChange;
    /* access modifiers changed from: private */
    public EditTextBoldCursor[] inputFields;
    private LinearLayout inputFieldsContainer;
    private LinearLayout linearLayout2;
    private TextSettingsCell pasteCell;
    private String[] pasteFields;
    private String pasteString;
    private int pasteType;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell shareCell;
    private ValueAnimator shareDoneAnimator;
    private boolean shareDoneEnabled;
    private float shareDoneProgress;
    private float[] shareDoneProgressAnimValues;
    private RadioCell[] typeCell;

    public static class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;

        public TypeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.checkImage = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void setValue(String name, boolean checked, boolean divider) {
            this.textView.setText(name);
            this.checkImage.setVisibility(checked ? 0 : 4);
            this.needDivider = divider;
        }

        public void setTypeChecked(boolean value) {
            this.checkImage.setVisibility(value ? 0 : 4);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public ProxySettingsActivity() {
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCells = new TextInfoPrivacyCell[2];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ProxySettingsActivity$$ExternalSyntheticLambda1(this);
        this.currentProxyInfo = new SharedConfig.ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(SharedConfig.ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCells = new TextInfoPrivacyCell[2];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new ProxySettingsActivity$$ExternalSyntheticLambda1(this);
        this.currentProxyInfo = proxyInfo;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.clipboardManager.addPrimaryClipChangedListener(this.clipChangedListener);
        updatePasteCell();
    }

    public void onPause() {
        super.onPause();
        this.clipboardManager.removePrimaryClipChangedListener(this.clipChangedListener);
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                boolean enabled;
                if (id == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                    ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt(ProxySettingsActivity.this.inputFields[1].getText().toString()).intValue();
                    if (ProxySettingsActivity.this.currentType == 0) {
                        ProxySettingsActivity.this.currentProxyInfo.secret = "";
                        ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[2].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                    } else {
                        ProxySettingsActivity.this.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.username = "";
                        ProxySettingsActivity.this.currentProxyInfo.password = "";
                    }
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    SharedPreferences.Editor editor = preferences.edit();
                    if (ProxySettingsActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(ProxySettingsActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = ProxySettingsActivity.this.currentProxyInfo;
                        editor.putBoolean("proxy_enabled", true);
                        enabled = true;
                    } else {
                        enabled = preferences.getBoolean("proxy_enabled", false);
                        SharedConfig.saveProxyList();
                    }
                    if (ProxySettingsActivity.this.addingNewProxy || SharedConfig.currentProxy == ProxySettingsActivity.this.currentProxyInfo) {
                        editor.putString("proxy_ip", ProxySettingsActivity.this.currentProxyInfo.address);
                        editor.putString("proxy_pass", ProxySettingsActivity.this.currentProxyInfo.password);
                        editor.putString("proxy_user", ProxySettingsActivity.this.currentProxyInfo.username);
                        editor.putInt("proxy_port", ProxySettingsActivity.this.currentProxyInfo.port);
                        editor.putString("proxy_secret", ProxySettingsActivity.this.currentProxyInfo.secret);
                        ConnectionsManager.setProxySettings(enabled, ProxySettingsActivity.this.currentProxyInfo.address, ProxySettingsActivity.this.currentProxyInfo.port, ProxySettingsActivity.this.currentProxyInfo.username, ProxySettingsActivity.this.currentProxyInfo.password, ProxySettingsActivity.this.currentProxyInfo.secret);
                    }
                    editor.commit();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    ProxySettingsActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneItem = addItemWithWidth;
        addItemWithWidth.setContentDescription(LocaleController.getString("Done", NUM));
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        ScrollView scrollView2 = new ScrollView(context2);
        this.scrollView = scrollView2;
        scrollView2.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        ((FrameLayout) this.fragmentView).addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        this.linearLayout2 = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        View.OnClickListener typeCellClickListener = new ProxySettingsActivity$$ExternalSyntheticLambda2(this);
        int a = 0;
        while (a < 2) {
            this.typeCell[a] = new RadioCell(context2);
            this.typeCell[a].setBackground(Theme.getSelectorDrawable(true));
            this.typeCell[a].setTag(Integer.valueOf(a));
            if (a == 0) {
                this.typeCell[a].setText(LocaleController.getString("UseProxySocks5", NUM), a == this.currentType, true);
            } else {
                this.typeCell[a].setText(LocaleController.getString("UseProxyTelegram", NUM), a == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[a], LayoutHelper.createLinear(-1, 50));
            this.typeCell[a].setOnClickListener(typeCellClickListener);
            a++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout3 = new LinearLayout(context2);
        this.inputFieldsContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.inputFieldsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= 21) {
            this.inputFieldsContainer.setElevation((float) AndroidUtilities.dp(1.0f));
            this.inputFieldsContainer.setOutlineProvider((ViewOutlineProvider) null);
        }
        this.linearLayout2.addView(this.inputFieldsContainer, LayoutHelper.createLinear(-1, -2));
        int i = 5;
        this.inputFields = new EditTextBoldCursor[5];
        int a2 = 0;
        while (a2 < i) {
            FrameLayout container = new FrameLayout(context2);
            this.inputFieldsContainer.addView(container, LayoutHelper.createLinear(-1, 64));
            this.inputFields[a2] = new EditTextBoldCursor(context2);
            this.inputFields[a2].setTag(Integer.valueOf(a2));
            this.inputFields[a2].setTextSize(1, 16.0f);
            this.inputFields[a2].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a2].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a2].setBackground(drawable);
            this.inputFields[a2].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a2].setCursorWidth(1.5f);
            this.inputFields[a2].setSingleLine(true);
            this.inputFields[a2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[a2].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[a2].setTransformHintToHeader(true);
            this.inputFields[a2].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (a2 == 0) {
                this.inputFields[a2].setInputType(524305);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        ProxySettingsActivity.this.checkShareDone(true);
                    }
                });
            } else if (a2 == 1) {
                this.inputFields[a2].setInputType(2);
                this.inputFields[a2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditText phoneField = ProxySettingsActivity.this.inputFields[1];
                            int start = phoneField.getSelectionStart();
                            String str = phoneField.getText().toString();
                            StringBuilder builder = new StringBuilder(str.length());
                            for (int a = 0; a < str.length(); a++) {
                                String ch = str.substring(a, a + 1);
                                if ("NUM".contains(ch)) {
                                    builder.append(ch);
                                }
                            }
                            boolean unused = ProxySettingsActivity.this.ignoreOnTextChange = true;
                            int port = Utilities.parseInt(builder.toString()).intValue();
                            if (port < 0 || port > 65535 || !str.equals(builder.toString())) {
                                if (port < 0) {
                                    phoneField.setText("0");
                                } else if (port > 65535) {
                                    phoneField.setText("65535");
                                } else {
                                    phoneField.setText(builder.toString());
                                }
                            } else if (start >= 0) {
                                phoneField.setSelection(Math.min(start, phoneField.length()));
                            }
                            boolean unused2 = ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    }
                });
            } else if (a2 == 3) {
                this.inputFields[a2].setInputType(129);
                this.inputFields[a2].setTypeface(Typeface.DEFAULT);
                this.inputFields[a2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[a2].setInputType(524289);
            }
            this.inputFields[a2].setImeOptions(NUM);
            switch (a2) {
                case 0:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyAddress", NUM));
                    this.inputFields[a2].setText(this.currentProxyInfo.address);
                    break;
                case 1:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPort", NUM));
                    EditTextBoldCursor editTextBoldCursor = this.inputFields[a2];
                    editTextBoldCursor.setText("" + this.currentProxyInfo.port);
                    break;
                case 2:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyUsername", NUM));
                    this.inputFields[a2].setText(this.currentProxyInfo.username);
                    break;
                case 3:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxyPassword", NUM));
                    this.inputFields[a2].setText(this.currentProxyInfo.password);
                    break;
                case 4:
                    this.inputFields[a2].setHintText(LocaleController.getString("UseProxySecret", NUM));
                    this.inputFields[a2].setText(this.currentProxyInfo.secret);
                    break;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[a2].setSelection(editTextBoldCursorArr[a2].length());
            this.inputFields[a2].setPadding(0, 0, 0, 0);
            container.addView(this.inputFields[a2], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, a2 == 0 ? 12.0f : 0.0f, 17.0f, 0.0f));
            this.inputFields[a2].setOnEditorActionListener(new ProxySettingsActivity$$ExternalSyntheticLambda5(this));
            a2++;
            i = 5;
            drawable = null;
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.bottomCells[i2] = new TextInfoPrivacyCell(context2);
            this.bottomCells[i2].setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            if (i2 == 0) {
                this.bottomCells[i2].setText(LocaleController.getString("UseProxyInfo", NUM));
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCells[i2];
                textInfoPrivacyCell.setText(LocaleController.getString("UseProxyTelegramInfo", NUM) + "\n\n" + LocaleController.getString("UseProxyTelegramInfo2", NUM));
                this.bottomCells[i2].setVisibility(8);
            }
            this.linearLayout2.addView(this.bottomCells[i2], LayoutHelper.createLinear(-1, -2));
        }
        TextSettingsCell textSettingsCell = new TextSettingsCell(this.fragmentView.getContext());
        this.pasteCell = textSettingsCell;
        textSettingsCell.setBackground(Theme.getSelectorDrawable(true));
        this.pasteCell.setText(LocaleController.getString("PasteFromClipboard", NUM), false);
        this.pasteCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.pasteCell.setOnClickListener(new ProxySettingsActivity$$ExternalSyntheticLambda3(this));
        this.linearLayout2.addView(this.pasteCell, 0, LayoutHelper.createLinear(-1, -2));
        this.pasteCell.setVisibility(8);
        this.sectionCell[2] = new ShadowSectionCell(this.fragmentView.getContext());
        this.sectionCell[2].setBackground(Theme.getThemedDrawable(this.fragmentView.getContext(), NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.sectionCell[2], 1, LayoutHelper.createLinear(-1, -2));
        this.sectionCell[2].setVisibility(8);
        TextSettingsCell textSettingsCell2 = new TextSettingsCell(context2);
        this.shareCell = textSettingsCell2;
        textSettingsCell2.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", NUM), false);
        this.shareCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new ProxySettingsActivity$$ExternalSyntheticLambda4(this));
        this.sectionCell[1] = new ShadowSectionCell(context2);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        this.clipboardManager = (ClipboardManager) context2.getSystemService("clipboard");
        this.shareDoneEnabled = true;
        this.shareDoneProgress = 1.0f;
        checkShareDone(false);
        this.currentType = -1;
        setProxyType(TextUtils.isEmpty(this.currentProxyInfo.secret) ^ true ? 1 : 0, false);
        this.pasteType = -1;
        this.pasteString = null;
        updatePasteCell();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3188lambda$createView$0$orgtelegramuiProxySettingsActivity(View view) {
        setProxyType(((Integer) view.getTag()).intValue(), true);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ boolean m3189lambda$createView$1$orgtelegramuiProxySettingsActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            int i2 = num + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (i2 < editTextBoldCursorArr.length) {
                editTextBoldCursorArr[num + 1].requestFocus();
            }
            return true;
        } else if (i != 6) {
            return false;
        } else {
            finishFragment();
            return true;
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3191lambda$createView$3$orgtelegramuiProxySettingsActivity(View v) {
        if (this.pasteType != -1) {
            int i = 0;
            while (true) {
                String[] strArr = this.pasteFields;
                if (i < strArr.length) {
                    int i2 = this.pasteType;
                    if (!((i2 == 0 && i == 4) || (i2 == 1 && (i == 2 || i == 3)))) {
                        if (strArr[i] != null) {
                            try {
                                this.inputFields[i].setText(URLDecoder.decode(strArr[i], "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                this.inputFields[i].setText(this.pasteFields[i]);
                            }
                        } else {
                            this.inputFields[i].setText((CharSequence) null);
                        }
                    }
                    i++;
                } else {
                    EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                    editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
                    setProxyType(this.pasteType, true, new ProxySettingsActivity$$ExternalSyntheticLambda6(this));
                    return;
                }
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3190lambda$createView$2$orgtelegramuiProxySettingsActivity() {
        AndroidUtilities.hideKeyboard(this.inputFieldsContainer.findFocus());
        for (int i = 0; i < this.pasteFields.length; i++) {
            int i2 = this.pasteType;
            if ((i2 != 0 || i == 4) && (i2 != 1 || i == 2 || i == 3)) {
                this.inputFields[i].setText((CharSequence) null);
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3192lambda$createView$4$orgtelegramuiProxySettingsActivity(View v) {
        String url;
        StringBuilder params = new StringBuilder();
        String address = this.inputFields[0].getText().toString();
        String password = this.inputFields[3].getText().toString();
        String user = this.inputFields[2].getText().toString();
        String port = this.inputFields[1].getText().toString();
        String secret = this.inputFields[4].getText().toString();
        try {
            if (!TextUtils.isEmpty(address)) {
                params.append("server=");
                params.append(URLEncoder.encode(address, "UTF-8"));
            }
            if (!TextUtils.isEmpty(port)) {
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("port=");
                params.append(URLEncoder.encode(port, "UTF-8"));
            }
            if (this.currentType == 1) {
                url = "https://t.me/proxy?";
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("secret=");
                params.append(URLEncoder.encode(secret, "UTF-8"));
            } else {
                url = "https://t.me/socks?";
                if (!TextUtils.isEmpty(user)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("user=");
                    params.append(URLEncoder.encode(user, "UTF-8"));
                }
                if (!TextUtils.isEmpty(password)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("pass=");
                    params.append(URLEncoder.encode(password, "UTF-8"));
                }
            }
            if (params.length() != 0) {
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.TEXT", url + params.toString());
                Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", NUM));
                chooserIntent.setFlags(NUM);
                getParentActivity().startActivity(chooserIntent);
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePasteCell() {
        /*
            r14 = this;
            android.content.ClipboardManager r0 = r14.clipboardManager
            android.content.ClipData r0 = r0.getPrimaryClip()
            r1 = 0
            if (r0 == 0) goto L_0x0025
            int r2 = r0.getItemCount()
            if (r2 <= 0) goto L_0x0025
            android.content.ClipData$Item r2 = r0.getItemAt(r1)     // Catch:{ Exception -> 0x0022 }
            android.view.View r3 = r14.fragmentView     // Catch:{ Exception -> 0x0022 }
            android.content.Context r3 = r3.getContext()     // Catch:{ Exception -> 0x0022 }
            java.lang.CharSequence r2 = r2.coerceToText(r3)     // Catch:{ Exception -> 0x0022 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0022 }
            goto L_0x0024
        L_0x0022:
            r2 = move-exception
            r2 = 0
        L_0x0024:
            goto L_0x0026
        L_0x0025:
            r2 = 0
        L_0x0026:
            java.lang.String r3 = r14.pasteString
            boolean r3 = android.text.TextUtils.equals(r2, r3)
            if (r3 == 0) goto L_0x002f
            return
        L_0x002f:
            r3 = -1
            r14.pasteType = r3
            r14.pasteString = r2
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r14.inputFields
            int r4 = r4.length
            java.lang.String[] r4 = new java.lang.String[r4]
            r14.pasteFields = r4
            r4 = 2
            if (r2 == 0) goto L_0x0127
            r5 = 0
            java.lang.String[] r6 = new java.lang.String[r4]
            java.lang.String r7 = "t.me/socks?"
            r6[r1] = r7
            java.lang.String r7 = "tg://socks?"
            r8 = 1
            r6[r8] = r7
            r7 = 0
        L_0x004b:
            int r9 = r6.length
            java.lang.String r10 = "&"
            if (r7 >= r9) goto L_0x006d
            r9 = r6[r7]
            int r9 = r2.indexOf(r9)
            if (r9 < 0) goto L_0x006a
            r14.pasteType = r1
            r11 = r6[r7]
            int r11 = r11.length()
            int r11 = r11 + r9
            java.lang.String r11 = r2.substring(r11)
            java.lang.String[] r5 = r11.split(r10)
            goto L_0x006d
        L_0x006a:
            int r7 = r7 + 1
            goto L_0x004b
        L_0x006d:
            if (r5 != 0) goto L_0x009a
            java.lang.String[] r7 = new java.lang.String[r4]
            java.lang.String r9 = "t.me/proxy?"
            r7[r1] = r9
            java.lang.String r9 = "tg://proxy?"
            r7[r8] = r9
            r9 = 0
        L_0x007a:
            int r11 = r7.length
            if (r9 >= r11) goto L_0x009a
            r11 = r7[r9]
            int r11 = r2.indexOf(r11)
            if (r11 < 0) goto L_0x0097
            r14.pasteType = r8
            r12 = r7[r9]
            int r12 = r12.length()
            int r12 = r12 + r11
            java.lang.String r12 = r2.substring(r12)
            java.lang.String[] r5 = r12.split(r10)
            goto L_0x009a
        L_0x0097:
            int r9 = r9 + 1
            goto L_0x007a
        L_0x009a:
            if (r5 == 0) goto L_0x0127
            r7 = 0
        L_0x009d:
            int r9 = r5.length
            if (r7 >= r9) goto L_0x0127
            r9 = r5[r7]
            java.lang.String r10 = "="
            java.lang.String[] r9 = r9.split(r10)
            int r10 = r9.length
            if (r10 == r4) goto L_0x00ad
            goto L_0x0123
        L_0x00ad:
            r10 = r9[r1]
            java.lang.String r10 = r10.toLowerCase()
            int r11 = r10.hashCode()
            r12 = 3
            r13 = 4
            switch(r11) {
                case -906277200: goto L_0x00e5;
                case -905826493: goto L_0x00db;
                case 3433489: goto L_0x00d1;
                case 3446913: goto L_0x00c7;
                case 3599307: goto L_0x00bd;
                default: goto L_0x00bc;
            }
        L_0x00bc:
            goto L_0x00ef
        L_0x00bd:
            java.lang.String r11 = "user"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00bc
            r10 = 2
            goto L_0x00f0
        L_0x00c7:
            java.lang.String r11 = "port"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00bc
            r10 = 1
            goto L_0x00f0
        L_0x00d1:
            java.lang.String r11 = "pass"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00bc
            r10 = 3
            goto L_0x00f0
        L_0x00db:
            java.lang.String r11 = "server"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00bc
            r10 = 0
            goto L_0x00f0
        L_0x00e5:
            java.lang.String r11 = "secret"
            boolean r10 = r10.equals(r11)
            if (r10 == 0) goto L_0x00bc
            r10 = 4
            goto L_0x00f0
        L_0x00ef:
            r10 = -1
        L_0x00f0:
            switch(r10) {
                case 0: goto L_0x011c;
                case 1: goto L_0x0115;
                case 2: goto L_0x010a;
                case 3: goto L_0x00ff;
                case 4: goto L_0x00f4;
                default: goto L_0x00f3;
            }
        L_0x00f3:
            goto L_0x0123
        L_0x00f4:
            int r10 = r14.pasteType
            if (r10 != r8) goto L_0x0123
            java.lang.String[] r10 = r14.pasteFields
            r11 = r9[r8]
            r10[r13] = r11
            goto L_0x0123
        L_0x00ff:
            int r10 = r14.pasteType
            if (r10 != 0) goto L_0x0123
            java.lang.String[] r10 = r14.pasteFields
            r11 = r9[r8]
            r10[r12] = r11
            goto L_0x0123
        L_0x010a:
            int r10 = r14.pasteType
            if (r10 != 0) goto L_0x0123
            java.lang.String[] r10 = r14.pasteFields
            r11 = r9[r8]
            r10[r4] = r11
            goto L_0x0123
        L_0x0115:
            java.lang.String[] r10 = r14.pasteFields
            r11 = r9[r8]
            r10[r8] = r11
            goto L_0x0123
        L_0x011c:
            java.lang.String[] r10 = r14.pasteFields
            r11 = r9[r8]
            r10[r1] = r11
        L_0x0123:
            int r7 = r7 + 1
            goto L_0x009d
        L_0x0127:
            int r5 = r14.pasteType
            if (r5 == r3) goto L_0x0140
            org.telegram.ui.Cells.TextSettingsCell r3 = r14.pasteCell
            int r3 = r3.getVisibility()
            if (r3 == 0) goto L_0x0156
            org.telegram.ui.Cells.TextSettingsCell r3 = r14.pasteCell
            r3.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r3 = r14.sectionCell
            r3 = r3[r4]
            r3.setVisibility(r1)
            goto L_0x0156
        L_0x0140:
            org.telegram.ui.Cells.TextSettingsCell r1 = r14.pasteCell
            int r1 = r1.getVisibility()
            r3 = 8
            if (r1 == r3) goto L_0x0156
            org.telegram.ui.Cells.TextSettingsCell r1 = r14.pasteCell
            r1.setVisibility(r3)
            org.telegram.ui.Cells.ShadowSectionCell[] r1 = r14.sectionCell
            r1 = r1[r4]
            r1.setVisibility(r3)
        L_0x0156:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProxySettingsActivity.updatePasteCell():void");
    }

    private void setShareDoneEnabled(boolean enabled, boolean animated) {
        if (this.shareDoneEnabled != enabled) {
            ValueAnimator valueAnimator = this.shareDoneAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            } else if (animated) {
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.shareDoneAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.shareDoneAnimator.addUpdateListener(new ProxySettingsActivity$$ExternalSyntheticLambda0(this));
            }
            float f = 0.0f;
            float f2 = 1.0f;
            if (animated) {
                float[] fArr = this.shareDoneProgressAnimValues;
                fArr[0] = this.shareDoneProgress;
                if (enabled) {
                    f = 1.0f;
                }
                fArr[1] = f;
                this.shareDoneAnimator.start();
            } else {
                if (enabled) {
                    f = 1.0f;
                }
                this.shareDoneProgress = f;
                this.shareCell.setTextColor(Theme.getColor(enabled ? "windowBackgroundWhiteBlueText4" : "windowBackgroundWhiteGrayText2"));
                ActionBarMenuItem actionBarMenuItem = this.doneItem;
                if (!enabled) {
                    f2 = 0.5f;
                }
                actionBarMenuItem.setAlpha(f2);
            }
            this.shareCell.setEnabled(enabled);
            this.doneItem.setEnabled(enabled);
            this.shareDoneEnabled = enabled;
        }
    }

    /* renamed from: lambda$setShareDoneEnabled$5$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3194x3401043c(ValueAnimator a) {
        this.shareDoneProgress = AndroidUtilities.lerp(this.shareDoneProgressAnimValues, a.getAnimatedFraction());
        this.shareCell.setTextColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteGrayText2"), Theme.getColor("windowBackgroundWhiteBlueText4"), this.shareDoneProgress));
        this.doneItem.setAlpha((this.shareDoneProgress / 2.0f) + 0.5f);
    }

    /* access modifiers changed from: private */
    public void checkShareDone(boolean animated) {
        if (this.shareCell != null && this.doneItem != null) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            boolean z = false;
            if (editTextBoldCursorArr[0] != null && editTextBoldCursorArr[1] != null) {
                if (!(editTextBoldCursorArr[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0)) {
                    z = true;
                }
                setShareDoneEnabled(z, animated);
            }
        }
    }

    private void setProxyType(int type, boolean animated) {
        setProxyType(type, animated, (Runnable) null);
    }

    private void setProxyType(int type, boolean animated, final Runnable onTransitionEnd) {
        if (this.currentType != type) {
            this.currentType = type;
            if (Build.VERSION.SDK_INT >= 23) {
                TransitionManager.endTransitions(this.linearLayout2);
            }
            boolean z = true;
            if (animated && Build.VERSION.SDK_INT >= 21) {
                TransitionSet transitionSet = new TransitionSet().addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1)).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                if (onTransitionEnd != null) {
                    transitionSet.addListener(new Transition.TransitionListener() {
                        public void onTransitionStart(Transition transition) {
                        }

                        public void onTransitionEnd(Transition transition) {
                            onTransitionEnd.run();
                        }

                        public void onTransitionCancel(Transition transition) {
                        }

                        public void onTransitionPause(Transition transition) {
                        }

                        public void onTransitionResume(Transition transition) {
                        }
                    });
                }
                TransitionManager.beginDelayedTransition(this.linearLayout2, transitionSet);
            }
            int i = this.currentType;
            if (i == 0) {
                this.bottomCells[0].setVisibility(0);
                this.bottomCells[1].setVisibility(8);
                ((View) this.inputFields[4].getParent()).setVisibility(8);
                ((View) this.inputFields[3].getParent()).setVisibility(0);
                ((View) this.inputFields[2].getParent()).setVisibility(0);
            } else if (i == 1) {
                this.bottomCells[0].setVisibility(8);
                this.bottomCells[1].setVisibility(0);
                ((View) this.inputFields[4].getParent()).setVisibility(0);
                ((View) this.inputFields[3].getParent()).setVisibility(8);
                ((View) this.inputFields[2].getParent()).setVisibility(8);
            }
            this.typeCell[0].setChecked(this.currentType == 0, animated);
            RadioCell radioCell = this.typeCell[1];
            if (this.currentType != 1) {
                z = false;
            }
            radioCell.setChecked(z, animated);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate delegate = new ProxySettingsActivity$$ExternalSyntheticLambda7(this);
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.inputFieldsContainer, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, delegate, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.pasteCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        for (int a = 0; a < this.typeCell.length; a++) {
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.typeCell[a], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.typeCell[a], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
            arrayList.add(new ThemeDescription((View) this.typeCell[a], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        }
        if (this.inputFields != null) {
            for (int a2 = 0; a2 < this.inputFields.length; a2++) {
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[a2], ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = delegate;
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, delegate, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "windowBackgroundWhiteRedText3"));
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        int a3 = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (a3 >= shadowSectionCellArr.length) {
                break;
            }
            if (shadowSectionCellArr[a3] != null) {
                arrayList.add(new ThemeDescription(this.sectionCell[a3], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            }
            a3++;
        }
        for (int i = 0; i < this.bottomCells.length; i++) {
            arrayList.add(new ThemeDescription(this.bottomCells[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.bottomCells[i], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.bottomCells[i], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
        }
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$6$org-telegram-ui-ProxySettingsActivity  reason: not valid java name */
    public /* synthetic */ void m3193x763ae1bf() {
        ValueAnimator valueAnimator;
        if (this.shareCell != null && ((valueAnimator = this.shareDoneAnimator) == null || !valueAnimator.isRunning())) {
            this.shareCell.setTextColor(Theme.getColor(this.shareDoneEnabled ? "windowBackgroundWhiteBlueText4" : "windowBackgroundWhiteGrayText2"));
        }
        if (this.inputFields != null) {
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i < editTextBoldCursorArr.length) {
                    editTextBoldCursorArr[i].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
                    i++;
                } else {
                    return;
                }
            }
        }
    }
}
