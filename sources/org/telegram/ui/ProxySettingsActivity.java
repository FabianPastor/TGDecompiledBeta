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
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            int i = 5;
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            addView(this.checkImage, LayoutHelper.createFrame(19, 14.0f, (LocaleController.isRTL ? 3 : i) | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f) + (this.needDivider ? 1 : 0), NUM));
        }

        public void setValue(String str, boolean z, boolean z2) {
            this.textView.setText(str);
            this.checkImage.setVisibility(z ? 0 : 4);
            this.needDivider = z2;
        }

        public void setTypeChecked(boolean z) {
            this.checkImage.setVisibility(z ? 0 : 4);
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
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            public final void onPrimaryClipChanged() {
                ProxySettingsActivity.this.updatePasteCell();
            }
        };
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
        this.clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            public final void onPrimaryClipChanged() {
                ProxySettingsActivity.this.updatePasteCell();
            }
        };
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
            public void onItemClick(int i) {
                boolean z;
                if (i == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (i == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
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
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    SharedPreferences.Editor edit = globalMainSettings.edit();
                    if (ProxySettingsActivity.this.addingNewProxy) {
                        SharedConfig.addProxy(ProxySettingsActivity.this.currentProxyInfo);
                        SharedConfig.currentProxy = ProxySettingsActivity.this.currentProxyInfo;
                        edit.putBoolean("proxy_enabled", true);
                        z = true;
                    } else {
                        boolean z2 = globalMainSettings.getBoolean("proxy_enabled", false);
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
        view.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
        ((FrameLayout) view).addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        this.linearLayout2 = new LinearLayout(context2);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new FrameLayout.LayoutParams(-1, -2));
        $$Lambda$ProxySettingsActivity$m9BkTAI6IqJctow06HtssG_kLaQ r2 = new View.OnClickListener() {
            public final void onClick(View view) {
                ProxySettingsActivity.this.lambda$createView$0$ProxySettingsActivity(view);
            }
        };
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
            this.typeCell[i].setOnClickListener(r2);
            i++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFieldsContainer = new LinearLayout(context2);
        this.inputFieldsContainer.setOrientation(1);
        this.inputFieldsContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        if (Build.VERSION.SDK_INT >= 21) {
            this.inputFieldsContainer.setElevation((float) AndroidUtilities.dp(1.0f));
            this.inputFieldsContainer.setOutlineProvider((ViewOutlineProvider) null);
        }
        this.linearLayout2.addView(this.inputFieldsContainer, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[5];
        int i2 = 0;
        for (int i3 = 5; i2 < i3; i3 = 5) {
            FrameLayout frameLayout = new FrameLayout(context2);
            this.inputFieldsContainer.addView(frameLayout, LayoutHelper.createLinear(-1, 64));
            this.inputFields[i2] = new EditTextBoldCursor(context2);
            this.inputFields[i2].setTag(Integer.valueOf(i2));
            this.inputFields[i2].setTextSize(1, 16.0f);
            this.inputFields[i2].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[i2].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i2].setBackground((Drawable) null);
            this.inputFields[i2].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[i2].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[i2].setCursorWidth(1.5f);
            this.inputFields[i2].setSingleLine(true);
            this.inputFields[i2].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[i2].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[i2].setTransformHintToHeader(true);
            this.inputFields[i2].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (i2 == 0) {
                this.inputFields[i2].setInputType(524305);
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        ProxySettingsActivity.this.checkShareDone(true);
                    }
                });
            } else if (i2 == 1) {
                this.inputFields[i2].setInputType(2);
                this.inputFields[i2].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditTextBoldCursor editTextBoldCursor = ProxySettingsActivity.this.inputFields[1];
                            int selectionStart = editTextBoldCursor.getSelectionStart();
                            String obj = editTextBoldCursor.getText().toString();
                            StringBuilder sb = new StringBuilder(obj.length());
                            int i = 0;
                            while (i < obj.length()) {
                                int i2 = i + 1;
                                String substring = obj.substring(i, i2);
                                if ("NUM".contains(substring)) {
                                    sb.append(substring);
                                }
                                i = i2;
                            }
                            boolean unused = ProxySettingsActivity.this.ignoreOnTextChange = true;
                            int intValue = Utilities.parseInt(sb.toString()).intValue();
                            if (intValue < 0 || intValue > 65535 || !obj.equals(sb.toString())) {
                                if (intValue < 0) {
                                    editTextBoldCursor.setText("0");
                                } else if (intValue > 65535) {
                                    editTextBoldCursor.setText("65535");
                                } else {
                                    editTextBoldCursor.setText(sb.toString());
                                }
                            } else if (selectionStart >= 0) {
                                if (selectionStart > editTextBoldCursor.length()) {
                                    selectionStart = editTextBoldCursor.length();
                                }
                                editTextBoldCursor.setSelection(selectionStart);
                            }
                            boolean unused2 = ProxySettingsActivity.this.ignoreOnTextChange = false;
                            ProxySettingsActivity.this.checkShareDone(true);
                        }
                    }
                });
            } else if (i2 == 3) {
                this.inputFields[i2].setInputType(129);
                this.inputFields[i2].setTypeface(Typeface.DEFAULT);
                this.inputFields[i2].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[i2].setInputType(524289);
            }
            this.inputFields[i2].setImeOptions(NUM);
            if (i2 == 0) {
                this.inputFields[i2].setHintText(LocaleController.getString("UseProxyAddress", NUM));
                this.inputFields[i2].setText(this.currentProxyInfo.address);
            } else if (i2 == 1) {
                this.inputFields[i2].setHintText(LocaleController.getString("UseProxyPort", NUM));
                EditTextBoldCursor editTextBoldCursor = this.inputFields[i2];
                editTextBoldCursor.setText("" + this.currentProxyInfo.port);
            } else if (i2 == 2) {
                this.inputFields[i2].setHintText(LocaleController.getString("UseProxyUsername", NUM));
                this.inputFields[i2].setText(this.currentProxyInfo.username);
            } else if (i2 == 3) {
                this.inputFields[i2].setHintText(LocaleController.getString("UseProxyPassword", NUM));
                this.inputFields[i2].setText(this.currentProxyInfo.password);
            } else if (i2 == 4) {
                this.inputFields[i2].setHintText(LocaleController.getString("UseProxySecret", NUM));
                this.inputFields[i2].setText(this.currentProxyInfo.secret);
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[i2].setSelection(editTextBoldCursorArr[i2].length());
            this.inputFields[i2].setPadding(0, 0, 0, 0);
            frameLayout.addView(this.inputFields[i2], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, i2 == 0 ? 12.0f : 0.0f, 17.0f, 0.0f));
            this.inputFields[i2].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return ProxySettingsActivity.this.lambda$createView$1$ProxySettingsActivity(textView, i, keyEvent);
                }
            });
            i2++;
        }
        for (int i4 = 0; i4 < 2; i4++) {
            this.bottomCells[i4] = new TextInfoPrivacyCell(context2);
            this.bottomCells[i4].setBackground(Theme.getThemedDrawable(context2, NUM, "windowBackgroundGrayShadow"));
            if (i4 == 0) {
                this.bottomCells[i4].setText(LocaleController.getString("UseProxyInfo", NUM));
            } else if (i4 == 1) {
                TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCells[i4];
                textInfoPrivacyCell.setText(LocaleController.getString("UseProxyTelegramInfo", NUM) + "\n\n" + LocaleController.getString("UseProxyTelegramInfo2", NUM));
                this.bottomCells[i4].setVisibility(8);
            }
            this.linearLayout2.addView(this.bottomCells[i4], LayoutHelper.createLinear(-1, -2));
        }
        this.pasteCell = new TextSettingsCell(this.fragmentView.getContext());
        this.pasteCell.setBackground(Theme.getSelectorDrawable(true));
        this.pasteCell.setText(LocaleController.getString("PasteFromClipboard", NUM), false);
        this.pasteCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.pasteCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ProxySettingsActivity.this.lambda$createView$3$ProxySettingsActivity(view);
            }
        });
        this.linearLayout2.addView(this.pasteCell, 0, LayoutHelper.createLinear(-1, -2));
        this.pasteCell.setVisibility(8);
        this.sectionCell[2] = new ShadowSectionCell(this.fragmentView.getContext());
        this.sectionCell[2].setBackground(Theme.getThemedDrawable(this.fragmentView.getContext(), NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.sectionCell[2], 1, LayoutHelper.createLinear(-1, -2));
        this.sectionCell[2].setVisibility(8);
        this.shareCell = new TextSettingsCell(context2);
        this.shareCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", NUM), false);
        this.shareCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                ProxySettingsActivity.this.lambda$createView$4$ProxySettingsActivity(view);
            }
        });
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
            for (int i = 0; i < this.pasteFields.length; i++) {
                if (!((this.pasteType == 0 && i == 4) || (this.pasteType == 1 && (i == 2 || i == 3)))) {
                    String[] strArr = this.pasteFields;
                    if (strArr[i] != null) {
                        try {
                            this.inputFields[i].setText(URLDecoder.decode(strArr[i], "UTF-8"));
                        } catch (UnsupportedEncodingException unused) {
                            this.inputFields[i].setText(this.pasteFields[i]);
                        }
                    } else {
                        this.inputFields[i].setText((CharSequence) null);
                    }
                }
            }
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            editTextBoldCursorArr[0].setSelection(editTextBoldCursorArr[0].length());
            setProxyType(this.pasteType, true, new Runnable() {
                public final void run() {
                    ProxySettingsActivity.this.lambda$null$2$ProxySettingsActivity();
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$2$ProxySettingsActivity() {
        AndroidUtilities.hideKeyboard(this.inputFieldsContainer.findFocus());
        for (int i = 0; i < this.pasteFields.length; i++) {
            if ((this.pasteType != 0 || i == 4) && (this.pasteType != 1 || i == 2 || i == 3)) {
                this.inputFields[i].setText((CharSequence) null);
            }
        }
    }

    public /* synthetic */ void lambda$createView$4$ProxySettingsActivity(View view) {
        String str;
        StringBuilder sb = new StringBuilder();
        String obj = this.inputFields[0].getText().toString();
        String obj2 = this.inputFields[3].getText().toString();
        String obj3 = this.inputFields[2].getText().toString();
        String obj4 = this.inputFields[1].getText().toString();
        String obj5 = this.inputFields[4].getText().toString();
        try {
            if (!TextUtils.isEmpty(obj)) {
                sb.append("server=");
                sb.append(URLEncoder.encode(obj, "UTF-8"));
            }
            if (!TextUtils.isEmpty(obj4)) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append("port=");
                sb.append(URLEncoder.encode(obj4, "UTF-8"));
            }
            if (this.currentType == 1) {
                str = "https://t.me/proxy?";
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append("secret=");
                sb.append(URLEncoder.encode(obj5, "UTF-8"));
            } else {
                str = "https://t.me/socks?";
                if (!TextUtils.isEmpty(obj3)) {
                    if (sb.length() != 0) {
                        sb.append("&");
                    }
                    sb.append("user=");
                    sb.append(URLEncoder.encode(obj3, "UTF-8"));
                }
                if (!TextUtils.isEmpty(obj2)) {
                    if (sb.length() != 0) {
                        sb.append("&");
                    }
                    sb.append("pass=");
                    sb.append(URLEncoder.encode(obj2, "UTF-8"));
                }
            }
            if (sb.length() != 0) {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.TEXT", str + sb.toString());
                Intent createChooser = Intent.createChooser(intent, LocaleController.getString("ShareLink", NUM));
                createChooser.setFlags(NUM);
                getParentActivity().startActivity(createChooser);
            }
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002c A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x002d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePasteCell() {
        /*
            r11 = this;
            android.content.ClipboardManager r0 = r11.clipboardManager
            android.content.ClipData r0 = r0.getPrimaryClip()
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L_0x0023
            int r3 = r0.getItemCount()
            if (r3 <= 0) goto L_0x0023
            android.content.ClipData$Item r0 = r0.getItemAt(r2)     // Catch:{ Exception -> 0x0023 }
            android.view.View r3 = r11.fragmentView     // Catch:{ Exception -> 0x0023 }
            android.content.Context r3 = r3.getContext()     // Catch:{ Exception -> 0x0023 }
            java.lang.CharSequence r0 = r0.coerceToText(r3)     // Catch:{ Exception -> 0x0023 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0023 }
            goto L_0x0024
        L_0x0023:
            r0 = r1
        L_0x0024:
            java.lang.String r3 = r11.pasteString
            boolean r3 = android.text.TextUtils.equals(r0, r3)
            if (r3 == 0) goto L_0x002d
            return
        L_0x002d:
            r3 = -1
            r11.pasteType = r3
            r11.pasteString = r0
            org.telegram.ui.Components.EditTextBoldCursor[] r4 = r11.inputFields
            int r4 = r4.length
            java.lang.String[] r4 = new java.lang.String[r4]
            r11.pasteFields = r4
            r4 = 2
            if (r0 == 0) goto L_0x012b
            java.lang.String[] r5 = new java.lang.String[r4]
            java.lang.String r6 = "t.me/socks?"
            r5[r2] = r6
            r6 = 1
            java.lang.String r7 = "tg://socks?"
            r5[r6] = r7
            r7 = 0
        L_0x0048:
            int r8 = r5.length
            java.lang.String r9 = "&"
            if (r7 >= r8) goto L_0x006a
            r8 = r5[r7]
            int r8 = r0.indexOf(r8)
            if (r8 < 0) goto L_0x0067
            r11.pasteType = r2
            r1 = r5[r7]
            int r1 = r1.length()
            int r8 = r8 + r1
            java.lang.String r1 = r0.substring(r8)
            java.lang.String[] r1 = r1.split(r9)
            goto L_0x006a
        L_0x0067:
            int r7 = r7 + 1
            goto L_0x0048
        L_0x006a:
            if (r1 != 0) goto L_0x0097
            java.lang.String[] r5 = new java.lang.String[r4]
            java.lang.String r7 = "t.me/proxy?"
            r5[r2] = r7
            java.lang.String r7 = "tg://proxy?"
            r5[r6] = r7
            r7 = 0
        L_0x0077:
            int r8 = r5.length
            if (r7 >= r8) goto L_0x0097
            r8 = r5[r7]
            int r8 = r0.indexOf(r8)
            if (r8 < 0) goto L_0x0094
            r11.pasteType = r6
            r1 = r5[r7]
            int r1 = r1.length()
            int r8 = r8 + r1
            java.lang.String r0 = r0.substring(r8)
            java.lang.String[] r1 = r0.split(r9)
            goto L_0x0097
        L_0x0094:
            int r7 = r7 + 1
            goto L_0x0077
        L_0x0097:
            if (r1 == 0) goto L_0x012b
            r0 = 0
        L_0x009a:
            int r5 = r1.length
            if (r0 >= r5) goto L_0x012b
            r5 = r1[r0]
            java.lang.String r7 = "="
            java.lang.String[] r5 = r5.split(r7)
            int r7 = r5.length
            if (r7 == r4) goto L_0x00aa
            goto L_0x0127
        L_0x00aa:
            r7 = r5[r2]
            java.lang.String r7 = r7.toLowerCase()
            int r8 = r7.hashCode()
            r9 = 4
            r10 = 3
            switch(r8) {
                case -906277200: goto L_0x00e3;
                case -905826493: goto L_0x00d9;
                case 3433489: goto L_0x00cf;
                case 3446913: goto L_0x00c5;
                case 3599307: goto L_0x00ba;
                default: goto L_0x00b9;
            }
        L_0x00b9:
            goto L_0x00ed
        L_0x00ba:
            java.lang.String r8 = "user"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x00ed
            r7 = 2
            goto L_0x00ee
        L_0x00c5:
            java.lang.String r8 = "port"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x00ed
            r7 = 1
            goto L_0x00ee
        L_0x00cf:
            java.lang.String r8 = "pass"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x00ed
            r7 = 3
            goto L_0x00ee
        L_0x00d9:
            java.lang.String r8 = "server"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x00ed
            r7 = 0
            goto L_0x00ee
        L_0x00e3:
            java.lang.String r8 = "secret"
            boolean r7 = r7.equals(r8)
            if (r7 == 0) goto L_0x00ed
            r7 = 4
            goto L_0x00ee
        L_0x00ed:
            r7 = -1
        L_0x00ee:
            if (r7 == 0) goto L_0x0121
            if (r7 == r6) goto L_0x011a
            if (r7 == r4) goto L_0x010f
            if (r7 == r10) goto L_0x0104
            if (r7 == r9) goto L_0x00f9
            goto L_0x0127
        L_0x00f9:
            int r7 = r11.pasteType
            if (r7 != r6) goto L_0x0127
            java.lang.String[] r7 = r11.pasteFields
            r5 = r5[r6]
            r7[r9] = r5
            goto L_0x0127
        L_0x0104:
            int r7 = r11.pasteType
            if (r7 != 0) goto L_0x0127
            java.lang.String[] r7 = r11.pasteFields
            r5 = r5[r6]
            r7[r10] = r5
            goto L_0x0127
        L_0x010f:
            int r7 = r11.pasteType
            if (r7 != 0) goto L_0x0127
            java.lang.String[] r7 = r11.pasteFields
            r5 = r5[r6]
            r7[r4] = r5
            goto L_0x0127
        L_0x011a:
            java.lang.String[] r7 = r11.pasteFields
            r5 = r5[r6]
            r7[r6] = r5
            goto L_0x0127
        L_0x0121:
            java.lang.String[] r7 = r11.pasteFields
            r5 = r5[r6]
            r7[r2] = r5
        L_0x0127:
            int r0 = r0 + 1
            goto L_0x009a
        L_0x012b:
            int r0 = r11.pasteType
            if (r0 == r3) goto L_0x0144
            org.telegram.ui.Cells.TextSettingsCell r0 = r11.pasteCell
            int r0 = r0.getVisibility()
            if (r0 == 0) goto L_0x015a
            org.telegram.ui.Cells.TextSettingsCell r0 = r11.pasteCell
            r0.setVisibility(r2)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r11.sectionCell
            r0 = r0[r4]
            r0.setVisibility(r2)
            goto L_0x015a
        L_0x0144:
            org.telegram.ui.Cells.TextSettingsCell r0 = r11.pasteCell
            int r0 = r0.getVisibility()
            r1 = 8
            if (r0 == r1) goto L_0x015a
            org.telegram.ui.Cells.TextSettingsCell r0 = r11.pasteCell
            r0.setVisibility(r1)
            org.telegram.ui.Cells.ShadowSectionCell[] r0 = r11.sectionCell
            r0 = r0[r4]
            r0.setVisibility(r1)
        L_0x015a:
            return
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
                this.shareDoneAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ProxySettingsActivity.this.lambda$setShareDoneEnabled$5$ProxySettingsActivity(valueAnimator);
                    }
                });
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

    /* access modifiers changed from: private */
    public void checkShareDone(boolean z) {
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
        setProxyType(i, z, (Runnable) null);
    }

    private void setProxyType(int i, boolean z, final Runnable runnable) {
        if (this.currentType != i) {
            this.currentType = i;
            if (Build.VERSION.SDK_INT >= 23) {
                TransitionManager.endTransitions(this.linearLayout2);
            }
            boolean z2 = true;
            if (z && Build.VERSION.SDK_INT >= 21) {
                TransitionSet duration = new TransitionSet().addTransition(new Fade(2)).addTransition(new ChangeBounds()).addTransition(new Fade(1)).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(250);
                if (runnable != null) {
                    duration.addListener(new Transition.TransitionListener() {
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

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                ProxySettingsActivity.this.lambda$getThemeDescriptions$6$ProxySettingsActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
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
        $$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.pasteCell, ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription((View) this.pasteCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        int i = 0;
        while (true) {
            RadioCell[] radioCellArr = this.typeCell;
            if (i >= radioCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(radioCellArr[i], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.typeCell[i], ThemeDescription.FLAG_SELECTORWHITE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
            arrayList.add(new ThemeDescription((View) this.typeCell[i], 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) this.typeCell[i], ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
            arrayList.add(new ThemeDescription((View) this.typeCell[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
            i++;
        }
        if (this.inputFields != null) {
            int i2 = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i2 >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription(editTextBoldCursorArr[i2], ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_HINTTEXTCOLOR | ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
                arrayList.add(new ThemeDescription(this.inputFields[i2], ThemeDescription.FLAG_CURSORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
                $$Lambda$ProxySettingsActivity$qS6lDCijO5MZFsaWHNwLVj0xZHU r7 = r10;
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteInputField"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteInputFieldActivated"));
                arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteRedText3"));
                i2++;
            }
        } else {
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription((View) null, ThemeDescription.FLAG_HINTTEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteHintText"));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        int i3 = 0;
        while (true) {
            ShadowSectionCell[] shadowSectionCellArr = this.sectionCell;
            if (i3 >= shadowSectionCellArr.length) {
                break;
            }
            if (shadowSectionCellArr[i3] != null) {
                arrayList.add(new ThemeDescription(shadowSectionCellArr[i3], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            }
            i3++;
        }
        int i4 = 0;
        while (true) {
            TextInfoPrivacyCell[] textInfoPrivacyCellArr = this.bottomCells;
            if (i4 >= textInfoPrivacyCellArr.length) {
                return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
            }
            arrayList.add(new ThemeDescription(textInfoPrivacyCellArr[i4], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
            arrayList.add(new ThemeDescription((View) this.bottomCells[i4], 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
            arrayList.add(new ThemeDescription((View) this.bottomCells[i4], ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteLinkText"));
            i4++;
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$6$ProxySettingsActivity() {
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
