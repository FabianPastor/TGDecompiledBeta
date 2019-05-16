package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ProxySettingsActivity extends BaseFragment {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_SECRET = 4;
    private static final int FIELD_USER = 2;
    private static final int done_button = 1;
    private boolean addingNewProxy;
    private TextInfoPrivacyCell bottomCell;
    private ProxyInfo currentProxyInfo;
    private int currentType;
    private ActionBarMenuItem doneItem;
    private HeaderCell headerCell;
    private boolean ignoreOnTextChange;
    private EditTextBoldCursor[] inputFields;
    private LinearLayout linearLayout2;
    private ScrollView scrollView;
    private ShadowSectionCell[] sectionCell;
    private TextSettingsCell shareCell;
    private TypeCell[] typeCell;

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
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = new ProxyInfo("", 1080, "", "", "");
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = proxyInfo;
        this.currentType = TextUtils.isEmpty(proxyInfo.secret) ^ 1;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
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
        int i = 0;
        while (i < 2) {
            this.typeCell[i] = new TypeCell(context2);
            this.typeCell[i].setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.typeCell[i].setTag(Integer.valueOf(i));
            if (i == 0) {
                this.typeCell[i].setValue(LocaleController.getString("UseProxySocks5", NUM), i == this.currentType, true);
            } else if (i == 1) {
                this.typeCell[i].setValue(LocaleController.getString("UseProxyTelegram", NUM), i == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[i], LayoutHelper.createLinear(-1, 50));
            this.typeCell[i].setOnClickListener(new -$$Lambda$ProxySettingsActivity$m9BkTAI6IqJctow06HtssG_kLaQ(this));
            i++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[5];
        for (int i2 = 0; i2 < 5; i2++) {
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.linearLayout2.addView(frameLayout2, LayoutHelper.createLinear(-1, 64));
            frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[i2] = new EditTextBoldCursor(context2);
            this.inputFields[i2].setTag(Integer.valueOf(i2));
            this.inputFields[i2].setTextSize(1, 16.0f);
            this.inputFields[i2].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            String str = "windowBackgroundWhiteBlackText";
            this.inputFields[i2].setTextColor(Theme.getColor(str));
            this.inputFields[i2].setBackgroundDrawable(null);
            this.inputFields[i2].setCursorColor(Theme.getColor(str));
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
                        ProxySettingsActivity.this.checkShareButton();
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
                            int intValue = Utilities.parseInt(stringBuilder.toString()).intValue();
                            if (intValue < 0 || intValue > 65535 || !obj.equals(stringBuilder.toString())) {
                                if (intValue < 0) {
                                    editText.setText("0");
                                } else if (intValue > 65535) {
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
                            ProxySettingsActivity.this.checkShareButton();
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
                EditText editText = this.inputFields[i2];
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(this.currentProxyInfo.port);
                editText.setText(stringBuilder.toString());
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
            frameLayout2.addView(this.inputFields[i2], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[i2].setOnEditorActionListener(new -$$Lambda$ProxySettingsActivity$6uZhE0rYQm5lNBN7Lia6YJLxnRo(this));
        }
        this.bottomCell = new TextInfoPrivacyCell(context2);
        String str2 = "windowBackgroundGrayShadow";
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
        this.bottomCell.setText(LocaleController.getString("UseProxyInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell = new TextSettingsCell(context2);
        this.shareCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", NUM), false);
        this.shareCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new -$$Lambda$ProxySettingsActivity$4RcOr5eJR76wYO-TQE9eb5ryku8(this));
        this.sectionCell[1] = new ShadowSectionCell(context2);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context2, NUM, str2));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        checkShareButton();
        updateUiForType();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$ProxySettingsActivity(View view) {
        this.currentType = ((Integer) view.getTag()).intValue();
        updateUiForType();
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

    public /* synthetic */ void lambda$createView$2$ProxySettingsActivity(View view) {
        StringBuilder stringBuilder = new StringBuilder("");
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

    private void checkShareButton() {
        if (this.shareCell != null && this.doneItem != null) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
            if (editTextBoldCursorArr[0] != null && editTextBoldCursorArr[1] != null) {
                if (editTextBoldCursorArr[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0) {
                    this.shareCell.getTextView().setAlpha(0.5f);
                    this.doneItem.setAlpha(0.5f);
                    this.shareCell.setEnabled(false);
                    this.doneItem.setEnabled(false);
                    return;
                }
                this.shareCell.getTextView().setAlpha(1.0f);
                this.doneItem.setAlpha(1.0f);
                this.shareCell.setEnabled(true);
                this.doneItem.setEnabled(true);
            }
        }
    }

    private void updateUiForType() {
        int i = this.currentType;
        boolean z = true;
        if (i == 0) {
            this.bottomCell.setText(LocaleController.getString("UseProxyInfo", NUM));
            ((View) this.inputFields[4].getParent()).setVisibility(8);
            ((View) this.inputFields[3].getParent()).setVisibility(0);
            ((View) this.inputFields[2].getParent()).setVisibility(0);
        } else if (i == 1) {
            TextInfoPrivacyCell textInfoPrivacyCell = this.bottomCell;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(LocaleController.getString("UseProxyTelegramInfo", NUM));
            stringBuilder.append("\n\n");
            stringBuilder.append(LocaleController.getString("UseProxyTelegramInfo2", NUM));
            textInfoPrivacyCell.setText(stringBuilder.toString());
            ((View) this.inputFields[4].getParent()).setVisibility(0);
            ((View) this.inputFields[3].getParent()).setVisibility(8);
            ((View) this.inputFields[2].getParent()).setVisibility(8);
        }
        this.typeCell[0].setTypeChecked(this.currentType == 0);
        TypeCell typeCell = this.typeCell[1];
        if (this.currentType != 1) {
            z = false;
        }
        typeCell.setTypeChecked(z);
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        View view = this.shareCell;
        Class[] clsArr = new Class[]{TextSettingsCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        arrayList.add(new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText"));
        int i = 0;
        while (true) {
            TypeCell[] typeCellArr = this.typeCell;
            if (i >= typeCellArr.length) {
                break;
            }
            arrayList.add(new ThemeDescription(typeCellArr[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.typeCell[i], 0, new Class[]{TypeCell.class}, new String[]{r6}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.typeCell[i], ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon"));
            i++;
        }
        if (this.inputFields != null) {
            i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.inputFields;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                arrayList.add(new ThemeDescription((View) editTextBoldCursorArr[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
                i++;
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{r6}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        for (i = 0; i < 2; i++) {
            arrayList.add(new ThemeDescription(this.sectionCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        }
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.bottomCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{r6}, null, null, null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{r6}, null, null, null, "windowBackgroundWhiteLinkText"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
