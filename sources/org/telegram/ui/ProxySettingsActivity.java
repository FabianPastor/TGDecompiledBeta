package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.Transition.TransitionListener;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
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
import org.telegram.messenger.SharedConfig.ProxyInfo;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
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
    private boolean addingNewProxy;
    private TextInfoPrivacyCell[] bottomCells;
    private OnPrimaryClipChangedListener clipChangedListener;
    private ClipboardManager clipboardManager;
    private ProxyInfo currentProxyInfo;
    private int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    private boolean ignoreOnTextChange;
    private EditTextBoldCursor[] inputFields;
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

    public class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;

        public TypeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            ImageView imageView = this.checkImage;
            if (LocaleController.isRTL) {
                i = 3;
            }
            addView(imageView, LayoutHelper.createFrame(19, 14.0f, i | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + this.needDivider, NUM));
        }

        public void setValue(String str, boolean z, boolean z2) {
            this.textView.setText(str);
            this.checkImage.setVisibility(z ? 0 : 4);
            this.needDivider = z2;
        }

        public void setTypeChecked(boolean z) {
            this.checkImage.setVisibility(z ? 0 : 4);
        }

        /* Access modifiers changed, original: protected */
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
        this.clipChangedListener = new -$$Lambda$ProxySettingsActivity$86x431i8X6AC1Da2KQzhzyF_ZiQ(this);
        this.currentProxyInfo = new ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[3];
        this.bottomCells = new TextInfoPrivacyCell[2];
        this.typeCell = new RadioCell[2];
        this.currentType = -1;
        this.pasteType = -1;
        this.shareDoneProgress = 1.0f;
        this.shareDoneProgressAnimValues = new float[2];
        this.shareDoneEnabled = true;
        this.clipChangedListener = new -$$Lambda$ProxySettingsActivity$86x431i8X6AC1Da2KQzhzyF_ZiQ(this);
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
        String str;
        Context context2 = context;
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (i == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                    boolean z;
                    ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt(ProxySettingsActivity.this.inputFields[1].getText().toString()).intValue();
                    String str = "";
                    if (ProxySettingsActivity.this.currentType == 0) {
                        ProxySettingsActivity.this.currentProxyInfo.secret = str;
                        ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[2].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                    } else {
                        ProxySettingsActivity.this.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
                        ProxySettingsActivity.this.currentProxyInfo.username = str;
                        ProxySettingsActivity.this.currentProxyInfo.password = str;
                    }
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    Editor edit = globalMainSettings.edit();
                    String str2 = "proxy_enabled";
                    if (ProxySettingsActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(ProxySettingsActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = ProxySettingsActivity.this.currentProxyInfo;
                        edit.putBoolean(str2, true);
                        z = true;
                    } else {
                        boolean z2 = globalMainSettings.getBoolean(str2, false);
                        SharedConfig.saveProxyList();
                        z = z2;
                    }
                    if (ProxySettingsActivity.this.addingNewProxy || SharedConfig.currentProxy == ProxySettingsActivity.this.currentProxyInfo) {
                        edit.putString("proxy_ip", ProxySettingsActivity.this.currentProxyInfo.address);
                        edit.putString("proxy_pass", ProxySettingsActivity.this.currentProxyInfo.password);
                        edit.putString("proxy_user", ProxySettingsActivity.this.currentProxyInfo.username);
                        edit.putInt("proxy_port", ProxySettingsActivity.this.currentProxyInfo.port);
                        edit.putString("proxy_secret", ProxySettingsActivity.this.currentProxyInfo.secret);
                        ConnectionsManager.setProxySettings(z, ProxySettingsActivity.this.currentProxyInfo.address, ProxySettingsActivity.this.currentProxyInfo.port, ProxySettingsActivity.this.currentProxyInfo.username, ProxySettingsActivity.this.currentProxyInfo.password, ProxySettingsActivity.this.currentProxyInfo.secret);
                    }
                    edit.commit();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.proxySettingsChanged, new Object[0]);
                    ProxySettingsActivity.this.finishFragment();
                }
            }
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneItem.setContentDescription(LocaleController.getString("Done", NUM));
        this.fragmentView = new FrameLayout(context2);
        View view = this.fragmentView;
        FrameLayout frameLayout = (FrameLayout) view;
        view.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        this.linearLayout2 = new LinearLayout(context2);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        -$$Lambda$ProxySettingsActivity$m9BkTAI6IqJctow06HtssG_kLaQ -__lambda_proxysettingsactivity_m9bktai6iqjctow06htssg_klaq = new -$$Lambda$ProxySettingsActivity$m9BkTAI6IqJctow06HtssG_kLaQ(this);
        int i = 0;
        while (i < 2) {
            this.typeCell[i] = new RadioCell(context2);
            this.typeCell[i].setBackground(Theme.getSelectorDrawable(true));
            this.typeCell[i].setTag(Integer.valueOf(i));
            if (i == 0) {
                this.typeCell[i].setText(LocaleController.getString("UseProxySocks5", NUM), i == this.currentType, true);
            } else if (i == 1) {
                this.typeCell[i].setText(LocaleController.getString("UseProxyTelegram", NUM), i == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[i], LayoutHelper.createLinear(-1, 50));
            this.typeCell[i].setOnClickListener(-__lambda_proxysettingsactivity_m9bktai6iqjctow06htssg_klaq);
            i++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFieldsContainer = new LinearLayout(context2);
        this.inputFieldsContainer.setOrientation(1);
        this.inputFieldsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        if (VERSION.SDK_INT >= 21) {
            this.inputFieldsContainer.setElevation((float) AndroidUtilities.dp(1.0f));
            this.inputFieldsContainer.setOutlineProvider(null);
        }
        this.linearLayout2.addView(this.inputFieldsContainer, LayoutHelper.createLinear(-1, -2));
        int i2 = 5;
        this.inputFields = new EditTextBoldCursor[5];
        i = 0;
        while (i < i2) {
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.inputFieldsContainer.addView(frameLayout2, LayoutHelper.createLinear(-1, 64));
            this.inputFields[i] = new EditTextBoldCursor(context2);
            this.inputFields[i].setTag(Integer.valueOf(i));
            this.inputFields[i].setTextSize(1, 16.0f);
            this.inputFields[i].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            str = "windowBackgroundWhiteBlackText";
            this.inputFields[i].setTextColor(Theme.getColor(str));
            this.inputFields[i].setBackground(null);
            this.inputFields[i].setCursorColor(Theme.getColor(str));
            this.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i].setCursorWidth(1.5f);
            this.inputFields[i].setSingleLine(true);
            this.inputFields[i].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[i].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[i].setTransformHintToHeader(true);
            this.inputFields[i].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (i == 0) {
                this.inputFields[i].setInputType(524305);
                this.inputFields[i].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        ProxySettingsActivity.this.checkShareDone(true);
                    }
                });
            } else if (i == 1) {
                this.inputFields[i].setInputType(2);
                this.inputFields[i].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditText editText = ProxySettingsActivity.this.inputFields[1];
                            int selectionStart = editText.getSelectionStart();
                            String obj = editText.getText().toString();
                            StringBuilder stringBuilder = new StringBuilder(obj.length());
                            int i = 0;
                            while (i < obj.length()) {
                                int i2 = i + 1;
                                String substring = obj.substring(i, i2);
                                if ("NUM".contains(substring)) {
                                    stringBuilder.append(substring);
                                }
                                i = i2;
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = true;
                            i = Utilities.parseInt(stringBuilder.toString()).intValue();
                            if (i < 0 || i > 65535 || !obj.equals(stringBuilder.toString())) {
                                if (i < 0) {
                                    editText.setText("0");
                                } else if (i > 65535) {
                                    editText.setText("65535");
                                } else {
                                    editText.setText(stringBuilder.toString());
                                }
                            } else if (selectionStart >= 0) {
                                if (selectionStart > editText.length()) {
                                    selectionStart = editText.length();
                                }
                                editText.setSelection(selectionStart);
                            }
                            ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    }
                });
            } else if (i == 3) {
                this.inputFields[i].setInputType(129);
                this.inputFields[i].setTypeface(Typeface.DEFAULT);
                this.inputFields[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[i].setInputType(524289);
            }
            this.inputFields[i].setImeOptions(NUM);
            if (i == 0) {
                this.inputFields[i].setHintText(LocaleController.getString("UseProxyAddress", NUM));
                this.inputFields[i].setText(this.currentProxyInfo.address);
            } else if (i == 1) {
                this.inputFields[i].setHintText(LocaleController.getString("UseProxyPort", NUM));
                EditText editText = this.inputFields[i];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(this.currentProxyInfo.port);
                editText.setText(stringBuilder.toString());
            } else if (i == 2) {
                this.inputFields[i].setHintText(LocaleController.getString("UseProxyUsername", NUM));
                this.inputFields[i].setText(this.currentProxyInfo.username);
            } else if (i == 3) {
                this.inputFields[i].setHintText(LocaleController.getString("UseProxyPassword", NUM));
                this.inputFields[i].setText(this.currentProxyInfo.password);
            } else if (i == 4) {
                this.inputFields[i].setHintText(LocaleController.getString("UseProxySecret", NUM));
                this.inputFields[i].setText(this.currentProxyInfo.secret);
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i].setSelection(editTextBoldCursorArr[i].length());
            this.inputFields[i].setPadding(0, 0, 0, 0);
            frameLayout2.addView(this.inputFields[i], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, i == 0 ? 12.0f : 0.0f, 17.0f, 0.0f));
            this.inputFields[i].setOnEditorActionListener(new -$$Lambda$ProxySettingsActivity$6uZhE0rYQm5lNBN7Lia6YJLxnRo(this));
            i++;
            i2 = 5;
        }
        i2 = 0;
        while (true) {
            String str2 = "windowBackgroundGrayShadow";
            if (i2 < 2) {
                this.bottomCells[i2] = new TextInfoPrivacyCell(context2);
                this.bottomCells[i2].setBackground(Theme.getThemedDrawable(context2, NUM, str2));
                if (i2 == 0) {
                    this.bottomCells[i2].setText(LocaleController.getString("UseProxyInfo", NUM));
                } else if (i2 == 1) {
                    TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCells[i2];
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(LocaleController.getString("UseProxyTelegramInfo", NUM));
                    stringBuilder2.append("\n\n");
                    stringBuilder2.append(LocaleController.getString("UseProxyTelegramInfo2", NUM));
                    textInfoPrivacyCell.setText(stringBuilder2.toString());
                    this.bottomCells[i2].setVisibility(8);
                }
                this.linearLayout2.addView(this.bottomCells[i2], LayoutHelper.createLinear(-1, -2));
                i2++;
            } else {
                this.pasteCell = new TextSettingsCell(this.fragmentView.getContext());
                this.pasteCell.setBackground(Theme.getSelectorDrawable(true));
                this.pasteCell.setText(LocaleController.getString("PasteFromClipboard", NUM), false);
                str = "windowBackgroundWhiteBlueText4";
                this.pasteCell.setTextColor(Theme.getColor(str));
                this.pasteCell.setOnClickListener(new -$$Lambda$ProxySettingsActivity$Nr3GM_Jcu9XvWiwmJb6NLw7o790(this));
                this.linearLayout2.addView(this.pasteCell, 0, LayoutHelper.createLinear(-1, -2));
                this.pasteCell.setVisibility(8);
                this.sectionCell[2] = new ShadowSectionCell(this.fragmentView.getContext());
                this.sectionCell[2].setBackground(Theme.getThemedDrawable(this.fragmentView.getContext(), NUM, str2));
                this.linearLayout2.addView(this.sectionCell[2], 1, LayoutHelper.createLinear(-1, -2));
                this.sectionCell[2].setVisibility(8);
                this.shareCell = new TextSettingsCell(context2);
                this.shareCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                this.shareCell.setText(LocaleController.getString("ShareFile", NUM), false);
                this.shareCell.setTextColor(Theme.getColor(str));
                this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
                this.shareCell.setOnClickListener(new -$$Lambda$ProxySettingsActivity$zpuMZOLUK7xH8EuguyzDt8Cp8vA(this));
                this.sectionCell[1] = new ShadowSectionCell(context2);
                this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
                this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
                this.clipboardManager = (ClipboardManager) context2.getSystemService("clipboard");
                this.shareDoneEnabled = true;
                this.shareDoneProgress = 1.0f;
                checkShareDone(false);
                this.currentType = -1;
                setProxyType(TextUtils.isEmpty(this.currentProxyInfo.secret) ^ 1, false);
                this.pasteType = -1;
                this.pasteString = null;
                updatePasteCell();
                return this.fragmentView;
            }
        }
    }

    public /* synthetic */ void lambda$createView$0$ProxySettingsActivity(View view) {
        setProxyType(((Integer) view.getTag()).intValue(), true);
    }

    public /* synthetic */ boolean lambda$createView$1$ProxySettingsActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int intValue = ((Integer) textView.getTag()).intValue() + 1;
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (intValue < editTextBoldCursorArr.length) {
                editTextBoldCursorArr[intValue].requestFocus();
            }
            return true;
        } else if (i != 6) {
            return false;
        } else {
            finishFragment();
            return true;
        }
    }

    public /* synthetic */ void lambda$createView$3$ProxySettingsActivity(View view) {
        if (this.pasteType != -1) {
            int i = 0;
            while (i < this.pasteFields.length) {
                if (!((this.pasteType == 0 && i == 4) || (this.pasteType == 1 && (i == 2 || i == 3)))) {
                    String[] strArr = this.pasteFields;
                    if (strArr[i] != null) {
                        try {
                            this.inputFields[i].setText(URLDecoder.decode(strArr[i], "UTF-8"));
                        } catch (UnsupportedEncodingException unused) {
                            this.inputFields[i].setText(this.pasteFields[i]);
                        }
                    } else {
                        this.inputFields[i].setText(null);
                    }
                }
                i++;
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
            setProxyType(this.pasteType, true, new -$$Lambda$ProxySettingsActivity$B-mSU76b11W2URJ91CVALYazdWg(this));
        }
    }

    public /* synthetic */ void lambda$null$2$ProxySettingsActivity() {
        AndroidUtilities.hideKeyboard(this.inputFieldsContainer.findFocus());
        int i = 0;
        while (i < this.pasteFields.length) {
            if ((this.pasteType != 0 || i == 4) && (this.pasteType != 1 || i == 2 || i == 3)) {
                this.inputFields[i].setText(null);
            }
            i++;
        }
    }

    public /* synthetic */ void lambda$createView$4$ProxySettingsActivity(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        String obj = this.inputFields[0].getText().toString();
        String obj2 = this.inputFields[3].getText().toString();
        String obj3 = this.inputFields[2].getText().toString();
        String obj4 = this.inputFields[1].getText().toString();
        String obj5 = this.inputFields[4].getText().toString();
        try {
            String str = "UTF-8";
            if (!TextUtils.isEmpty(obj)) {
                stringBuilder.append("server=");
                stringBuilder.append(URLEncoder.encode(obj, str));
            }
            String str2 = "&";
            if (!TextUtils.isEmpty(obj4)) {
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str2);
                }
                stringBuilder.append("port=");
                stringBuilder.append(URLEncoder.encode(obj4, str));
            }
            if (this.currentType == 1) {
                obj = "https://t.me/proxy?";
                if (stringBuilder.length() != 0) {
                    stringBuilder.append(str2);
                }
                stringBuilder.append("secret=");
                stringBuilder.append(URLEncoder.encode(obj5, str));
            } else {
                obj = "https://t.me/socks?";
                if (!TextUtils.isEmpty(obj3)) {
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append("user=");
                    stringBuilder.append(URLEncoder.encode(obj3, str));
                }
                if (!TextUtils.isEmpty(obj2)) {
                    if (stringBuilder.length() != 0) {
                        stringBuilder.append(str2);
                    }
                    stringBuilder.append("pass=");
                    stringBuilder.append(URLEncoder.encode(obj2, str));
                }
            }
            if (stringBuilder.length() != 0) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(obj);
                stringBuilder2.append(stringBuilder.toString());
                intent.putExtra("android.intent.extra.TEXT", stringBuilder2.toString());
                Intent createChooser = Intent.createChooser(intent, LocaleController.getString("ShareLink", NUM));
                createChooser.setFlags(NUM);
                getParentActivity().startActivity(createChooser);
            }
        } catch (Exception unused) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x002d  */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002c A:{RETURN} */
    private void updatePasteCell() {
        /*
        r11 = this;
        r0 = r11.clipboardManager;
        r0 = r0.getPrimaryClip();
        r1 = 0;
        r2 = 0;
        if (r0 == 0) goto L_0x0023;
    L_0x000a:
        r3 = r0.getItemCount();
        if (r3 <= 0) goto L_0x0023;
    L_0x0010:
        r0 = r0.getItemAt(r2);	 Catch:{ Exception -> 0x0023 }
        r3 = r11.fragmentView;	 Catch:{ Exception -> 0x0023 }
        r3 = r3.getContext();	 Catch:{ Exception -> 0x0023 }
        r0 = r0.coerceToText(r3);	 Catch:{ Exception -> 0x0023 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0023 }
        goto L_0x0024;
    L_0x0023:
        r0 = r1;
    L_0x0024:
        r3 = r11.pasteString;
        r3 = android.text.TextUtils.equals(r0, r3);
        if (r3 == 0) goto L_0x002d;
    L_0x002c:
        return;
    L_0x002d:
        r3 = -1;
        r11.pasteType = r3;
        r11.pasteString = r0;
        r4 = r11.inputFields;
        r4 = r4.length;
        r4 = new java.lang.String[r4];
        r11.pasteFields = r4;
        r4 = 2;
        if (r0 == 0) goto L_0x012b;
    L_0x003c:
        r5 = new java.lang.String[r4];
        r6 = "t.me/socks?";
        r5[r2] = r6;
        r6 = 1;
        r7 = "tg://socks?";
        r5[r6] = r7;
        r7 = 0;
    L_0x0048:
        r8 = r5.length;
        r9 = "&";
        if (r7 >= r8) goto L_0x006a;
    L_0x004d:
        r8 = r5[r7];
        r8 = r0.indexOf(r8);
        if (r8 < 0) goto L_0x0067;
    L_0x0055:
        r11.pasteType = r2;
        r1 = r5[r7];
        r1 = r1.length();
        r8 = r8 + r1;
        r1 = r0.substring(r8);
        r1 = r1.split(r9);
        goto L_0x006a;
    L_0x0067:
        r7 = r7 + 1;
        goto L_0x0048;
    L_0x006a:
        if (r1 != 0) goto L_0x0097;
    L_0x006c:
        r5 = new java.lang.String[r4];
        r7 = "t.me/proxy?";
        r5[r2] = r7;
        r7 = "tg://proxy?";
        r5[r6] = r7;
        r7 = 0;
    L_0x0077:
        r8 = r5.length;
        if (r7 >= r8) goto L_0x0097;
    L_0x007a:
        r8 = r5[r7];
        r8 = r0.indexOf(r8);
        if (r8 < 0) goto L_0x0094;
    L_0x0082:
        r11.pasteType = r6;
        r1 = r5[r7];
        r1 = r1.length();
        r8 = r8 + r1;
        r0 = r0.substring(r8);
        r1 = r0.split(r9);
        goto L_0x0097;
    L_0x0094:
        r7 = r7 + 1;
        goto L_0x0077;
    L_0x0097:
        if (r1 == 0) goto L_0x012b;
    L_0x0099:
        r0 = 0;
    L_0x009a:
        r5 = r1.length;
        if (r0 >= r5) goto L_0x012b;
    L_0x009d:
        r5 = r1[r0];
        r7 = "=";
        r5 = r5.split(r7);
        r7 = r5.length;
        if (r7 == r4) goto L_0x00aa;
    L_0x00a8:
        goto L_0x0127;
    L_0x00aa:
        r7 = r5[r2];
        r7 = r7.toLowerCase();
        r8 = r7.hashCode();
        r9 = 4;
        r10 = 3;
        switch(r8) {
            case -906277200: goto L_0x00e3;
            case -905826493: goto L_0x00d9;
            case 3433489: goto L_0x00cf;
            case 3446913: goto L_0x00c5;
            case 3599307: goto L_0x00ba;
            default: goto L_0x00b9;
        };
    L_0x00b9:
        goto L_0x00ed;
    L_0x00ba:
        r8 = "user";
        r7 = r7.equals(r8);
        if (r7 == 0) goto L_0x00ed;
    L_0x00c3:
        r7 = 2;
        goto L_0x00ee;
    L_0x00c5:
        r8 = "port";
        r7 = r7.equals(r8);
        if (r7 == 0) goto L_0x00ed;
    L_0x00cd:
        r7 = 1;
        goto L_0x00ee;
    L_0x00cf:
        r8 = "pass";
        r7 = r7.equals(r8);
        if (r7 == 0) goto L_0x00ed;
    L_0x00d7:
        r7 = 3;
        goto L_0x00ee;
    L_0x00d9:
        r8 = "server";
        r7 = r7.equals(r8);
        if (r7 == 0) goto L_0x00ed;
    L_0x00e1:
        r7 = 0;
        goto L_0x00ee;
    L_0x00e3:
        r8 = "secret";
        r7 = r7.equals(r8);
        if (r7 == 0) goto L_0x00ed;
    L_0x00eb:
        r7 = 4;
        goto L_0x00ee;
    L_0x00ed:
        r7 = -1;
    L_0x00ee:
        if (r7 == 0) goto L_0x0121;
    L_0x00f0:
        if (r7 == r6) goto L_0x011a;
    L_0x00f2:
        if (r7 == r4) goto L_0x010f;
    L_0x00f4:
        if (r7 == r10) goto L_0x0104;
    L_0x00f6:
        if (r7 == r9) goto L_0x00f9;
    L_0x00f8:
        goto L_0x0127;
    L_0x00f9:
        r7 = r11.pasteType;
        if (r7 != r6) goto L_0x0127;
    L_0x00fd:
        r7 = r11.pasteFields;
        r5 = r5[r6];
        r7[r9] = r5;
        goto L_0x0127;
    L_0x0104:
        r7 = r11.pasteType;
        if (r7 != 0) goto L_0x0127;
    L_0x0108:
        r7 = r11.pasteFields;
        r5 = r5[r6];
        r7[r10] = r5;
        goto L_0x0127;
    L_0x010f:
        r7 = r11.pasteType;
        if (r7 != 0) goto L_0x0127;
    L_0x0113:
        r7 = r11.pasteFields;
        r5 = r5[r6];
        r7[r4] = r5;
        goto L_0x0127;
    L_0x011a:
        r7 = r11.pasteFields;
        r5 = r5[r6];
        r7[r6] = r5;
        goto L_0x0127;
    L_0x0121:
        r7 = r11.pasteFields;
        r5 = r5[r6];
        r7[r2] = r5;
    L_0x0127:
        r0 = r0 + 1;
        goto L_0x009a;
    L_0x012b:
        r0 = r11.pasteType;
        if (r0 == r3) goto L_0x0144;
    L_0x012f:
        r0 = r11.pasteCell;
        r0 = r0.getVisibility();
        if (r0 == 0) goto L_0x015a;
    L_0x0137:
        r0 = r11.pasteCell;
        r0.setVisibility(r2);
        r0 = r11.sectionCell;
        r0 = r0[r4];
        r0.setVisibility(r2);
        goto L_0x015a;
    L_0x0144:
        r0 = r11.pasteCell;
        r0 = r0.getVisibility();
        r1 = 8;
        if (r0 == r1) goto L_0x015a;
    L_0x014e:
        r0 = r11.pasteCell;
        r0.setVisibility(r1);
        r0 = r11.sectionCell;
        r0 = r0[r4];
        r0.setVisibility(r1);
    L_0x015a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProxySettingsActivity.updatePasteCell():void");
    }

    private void setShareDoneEnabled(boolean z, boolean z2) {
        if (this.shareDoneEnabled != z) {
            ValueAnimator valueAnimator = this.shareDoneAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            } else if (z2) {
                this.shareDoneAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.shareDoneAnimator.setDuration(200);
                this.shareDoneAnimator.addUpdateListener(new -$$Lambda$ProxySettingsActivity$53rzfdw6zTqW2BSy-byLXDlX17M(this));
            }
            float f = 0.0f;
            float f2 = 1.0f;
            if (z2) {
                float[] fArr = this.shareDoneProgressAnimValues;
                fArr[0] = this.shareDoneProgress;
                if (z) {
                    f = 1.0f;
                }
                fArr[1] = f;
                this.shareDoneAnimator.start();
            } else {
                if (z) {
                    f = 1.0f;
                }
                this.shareDoneProgress = f;
                this.shareCell.setTextColor(Theme.getColor(z ? "windowBackgroundWhiteBlueText4" : "windowBackgroundWhiteGrayText2"));
                ActionBarMenuItem actionBarMenuItem = this.doneItem;
                if (!z) {
                    f2 = 0.5f;
                }
                actionBarMenuItem.setAlpha(f2);
            }
            this.shareCell.setEnabled(z);
            this.doneItem.setEnabled(z);
            this.shareDoneEnabled = z;
        }
    }

    public /* synthetic */ void lambda$setShareDoneEnabled$5$ProxySettingsActivity(ValueAnimator valueAnimator) {
        this.shareDoneProgress = AndroidUtilities.lerp(this.shareDoneProgressAnimValues, valueAnimator.getAnimatedFraction());
        this.shareCell.setTextColor(ColorUtils.blendARGB(Theme.getColor("windowBackgroundWhiteGrayText2"), Theme.getColor("windowBackgroundWhiteBlueText4"), this.shareDoneProgress));
        this.doneItem.setAlpha((this.shareDoneProgress / 2.0f) + 0.5f);
    }

    private void checkShareDone(boolean z) {
        if (this.shareCell != null && this.doneItem != null) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            boolean z2 = false;
            if (editTextBoldCursorArr[0] != null && editTextBoldCursorArr[1] != null) {
                if (!(editTextBoldCursorArr[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0)) {
                    z2 = true;
                }
                setShareDoneEnabled(z2, z);
            }
        }
    }

    private void setProxyType(int i, boolean z) {
        setProxyType(i, z, null);
    }

    private void setProxyType(int i, boolean z, final Runnable runnable) {
        if (this.currentType != i) {
            this.currentType = i;
            if (VERSION.SDK_INT >= 23) {
                TransitionManager.endTransitions(this.linearLayout2);
            }
            boolean z2 = true;
            if (z && VERSION.SDK_INT >= 21) {
                TransitionSet duration = new TransitionSet().addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1)).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                if (runnable != null) {
                    duration.addListener(new TransitionListener() {
                        public void onTransitionCancel(Transition transition) {
                        }

                        public void onTransitionPause(Transition transition) {
                        }

                        public void onTransitionResume(Transition transition) {
                        }

                        public void onTransitionStart(Transition transition) {
                        }

                        public void onTransitionEnd(Transition transition) {
                            runnable.run();
                        }
                    });
                }
                TransitionManager.beginDelayedTransition(this.linearLayout2, duration);
            }
            int i2 = this.currentType;
            if (i2 == 0) {
                this.bottomCells[0].setVisibility(0);
                this.bottomCells[1].setVisibility(8);
                ((View) this.inputFields[4].getParent()).setVisibility(8);
                ((View) this.inputFields[3].getParent()).setVisibility(0);
                ((View) this.inputFields[2].getParent()).setVisibility(0);
            } else if (i2 == 1) {
                this.bottomCells[0].setVisibility(8);
                this.bottomCells[1].setVisibility(0);
                ((View) this.inputFields[4].getParent()).setVisibility(0);
                ((View) this.inputFields[3].getParent()).setVisibility(8);
                ((View) this.inputFields[2].getParent()).setVisibility(8);
            }
            this.typeCell[0].setChecked(this.currentType == 0, z);
            RadioCell radioCell = this.typeCell[1];
            if (this.currentType != 1) {
                z2 = false;
            }
            radioCell.setChecked(z2, z);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu = new -$$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU(this);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.inputFieldsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        View view = this.pasteCell;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        arrayList.add(new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText4"));
        int i = 0;
        while (true) {
            RadioCell[] radioCellArr = this.typeCell;
            if (i >= radioCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(radioCellArr[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.typeCell[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription(this.typeCell[i], 0, new Class[]{RadioCell.class}, new String[]{r9}, null, null, null, "windowBackgroundWhiteBlackText"));
            View view2 = this.typeCell[i];
            int i2 = ThemeDescription.FLAG_CHECKBOX;
            clsArr = new Class[]{RadioCell.class};
            strArr = new String[1];
            strArr[0] = "radioButton";
            arrayList.add(new ThemeDescription(view2, i2, clsArr, strArr, null, null, null, "radioBackground"));
            arrayList.add(new ThemeDescription(this.typeCell[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, null, null, null, "radioBackgroundChecked"));
            i++;
        }
        if (this.inputFields != null) {
            int i3 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i3 >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(editTextBoldCursorArr[i3], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i3], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[i3], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[i3], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                -$$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu2 = -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu;
                arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu2, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu2, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription(null, 0, null, null, null, -__lambda_proxysettingsactivity_qs6ldcijo5mzfsawhnwlvj0xzhu2, "windowBackgroundWhiteRedText3"));
                i3++;
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{r9}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        i = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (i >= shadowSectionCellArr.length) {
                break;
            }
            if (shadowSectionCellArr[i] != null) {
                arrayList.add(new ThemeDescription(shadowSectionCellArr[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            }
            i++;
        }
        i = 0;
        while (true) {
            TextInfoPrivacyCell[] textInfoPrivacyCellArr = this.bottomCells;
            if (i >= textInfoPrivacyCellArr.length) {
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
            arrayList.add(new ThemeDescription(textInfoPrivacyCellArr[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription(this.bottomCells[i], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{r9}, null, null, null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription(this.bottomCells[i], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{r9}, null, null, null, "windowBackgroundWhiteLinkText"));
            i++;
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$6$ProxySettingsActivity() {
        if (this.shareCell != null) {
            ValueAnimator valueAnimator = this.shareDoneAnimator;
            if (valueAnimator == null || !valueAnimator.isRunning()) {
                this.shareCell.setTextColor(Theme.getColor(this.shareDoneEnabled ? "windowBackgroundWhiteBlueText4" : "windowBackgroundWhiteGrayText2"));
            }
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
