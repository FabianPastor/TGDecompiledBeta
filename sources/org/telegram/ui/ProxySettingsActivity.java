package org.telegram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class ProxySettingsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int FIELD_IP = 0;
    private static final int FIELD_PASSWORD = 3;
    private static final int FIELD_PORT = 1;
    private static final int FIELD_USER = 2;
    private static final int share_item = 1;
    private TextInfoPrivacyCell bottomCell;
    private TextCheckCell checkCell1;
    private ArrayList<View> dividers = new ArrayList();
    private HeaderCell headerCell;
    private boolean ignoreOnTextChange;
    private EditTextBoldCursor[] inputFields;
    private LinearLayout linearLayout2;
    private ScrollView scrollView;
    private ShadowSectionCell sectionCell;
    private ActionBarMenuItem shareItem;
    private TextCheckCell useForCallsCell;
    private boolean useProxyForCalls;
    private boolean useProxySettings;

    /* renamed from: org.telegram.ui.ProxySettingsActivity$3 */
    class C16583 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C16583() {
        }

        public void afterTextChanged(Editable editable) {
            ProxySettingsActivity.this.checkShareButton();
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$4 */
    class C16594 implements TextWatcher {
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        C16594() {
        }

        public void afterTextChanged(Editable editable) {
            if (ProxySettingsActivity.this.ignoreOnTextChange == null) {
                editable = ProxySettingsActivity.this.inputFields[1];
                int selectionStart = editable.getSelectionStart();
                String str = "0123456789";
                String obj = editable.getText().toString();
                StringBuilder stringBuilder = new StringBuilder(obj.length());
                int i = 0;
                while (i < obj.length()) {
                    int i2 = i + 1;
                    Object substring = obj.substring(i, i2);
                    if (str.contains(substring)) {
                        stringBuilder.append(substring);
                    }
                    i = i2;
                }
                ProxySettingsActivity.this.ignoreOnTextChange = true;
                int intValue = Utilities.parseInt(stringBuilder.toString()).intValue();
                if (intValue >= 0 && intValue <= 65535) {
                    if (obj.equals(stringBuilder.toString())) {
                        if (selectionStart >= 0) {
                            if (selectionStart > editable.length()) {
                                selectionStart = editable.length();
                            }
                            editable.setSelection(selectionStart);
                        }
                        ProxySettingsActivity.this.ignoreOnTextChange = false;
                        ProxySettingsActivity.this.checkShareButton();
                    }
                }
                if (intValue < 0) {
                    editable.setText("0");
                } else if (intValue > 65535) {
                    editable.setText("65535");
                } else {
                    editable.setText(stringBuilder.toString());
                }
                ProxySettingsActivity.this.ignoreOnTextChange = false;
                ProxySettingsActivity.this.checkShareButton();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$5 */
    class C16605 implements OnEditorActionListener {
        C16605() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                textView = ((Integer) textView.getTag()).intValue() + 1;
                if (textView < ProxySettingsActivity.this.inputFields.length) {
                    ProxySettingsActivity.this.inputFields[textView].requestFocus();
                }
                return true;
            } else if (i != 6) {
                return null;
            } else {
                ProxySettingsActivity.this.finishFragment();
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$6 */
    class C16616 implements OnClickListener {
        C16616() {
        }

        public void onClick(View view) {
            ProxySettingsActivity.this.useProxyForCalls = ProxySettingsActivity.this.useProxyForCalls ^ 1;
            ProxySettingsActivity.this.useForCallsCell.setChecked(ProxySettingsActivity.this.useProxyForCalls);
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$1 */
    class C22661 extends ActionBarMenuOnItemClick {
        C22661() {
        }

        public void onItemClick(int r6) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
            /*
            r5 = this;
            r0 = -1;
            if (r6 != r0) goto L_0x000a;
        L_0x0003:
            r6 = org.telegram.ui.ProxySettingsActivity.this;
            r6.finishFragment();
            goto L_0x011b;
        L_0x000a:
            r0 = 1;
            if (r6 != r0) goto L_0x011b;
        L_0x000d:
            r6 = org.telegram.ui.ProxySettingsActivity.this;
            r6 = r6.getParentActivity();
            if (r6 != 0) goto L_0x0016;
        L_0x0015:
            return;
        L_0x0016:
            r6 = new java.lang.StringBuilder;
            r1 = "";
            r6.<init>(r1);
            r1 = org.telegram.ui.ProxySettingsActivity.this;
            r1 = r1.inputFields;
            r2 = 0;
            r1 = r1[r2];
            r1 = r1.getText();
            r1 = r1.toString();
            r2 = org.telegram.ui.ProxySettingsActivity.this;
            r2 = r2.inputFields;
            r3 = 3;
            r2 = r2[r3];
            r2 = r2.getText();
            r2 = r2.toString();
            r3 = org.telegram.ui.ProxySettingsActivity.this;
            r3 = r3.inputFields;
            r4 = 2;
            r3 = r3[r4];
            r3 = r3.getText();
            r3 = r3.toString();
            r4 = org.telegram.ui.ProxySettingsActivity.this;
            r4 = r4.inputFields;
            r0 = r4[r0];
            r0 = r0.getText();
            r0 = r0.toString();
            r4 = android.text.TextUtils.isEmpty(r1);	 Catch:{ Exception -> 0x011a }
            if (r4 != 0) goto L_0x0074;	 Catch:{ Exception -> 0x011a }
        L_0x0066:
            r4 = "server=";	 Catch:{ Exception -> 0x011a }
            r6.append(r4);	 Catch:{ Exception -> 0x011a }
            r4 = "UTF-8";	 Catch:{ Exception -> 0x011a }
            r1 = java.net.URLEncoder.encode(r1, r4);	 Catch:{ Exception -> 0x011a }
            r6.append(r1);	 Catch:{ Exception -> 0x011a }
        L_0x0074:
            r1 = android.text.TextUtils.isEmpty(r0);	 Catch:{ Exception -> 0x011a }
            if (r1 != 0) goto L_0x0093;	 Catch:{ Exception -> 0x011a }
        L_0x007a:
            r1 = r6.length();	 Catch:{ Exception -> 0x011a }
            if (r1 == 0) goto L_0x0085;	 Catch:{ Exception -> 0x011a }
        L_0x0080:
            r1 = "&";	 Catch:{ Exception -> 0x011a }
            r6.append(r1);	 Catch:{ Exception -> 0x011a }
        L_0x0085:
            r1 = "port=";	 Catch:{ Exception -> 0x011a }
            r6.append(r1);	 Catch:{ Exception -> 0x011a }
            r1 = "UTF-8";	 Catch:{ Exception -> 0x011a }
            r0 = java.net.URLEncoder.encode(r0, r1);	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
        L_0x0093:
            r0 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x011a }
            if (r0 != 0) goto L_0x00b2;	 Catch:{ Exception -> 0x011a }
        L_0x0099:
            r0 = r6.length();	 Catch:{ Exception -> 0x011a }
            if (r0 == 0) goto L_0x00a4;	 Catch:{ Exception -> 0x011a }
        L_0x009f:
            r0 = "&";	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
        L_0x00a4:
            r0 = "user=";	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
            r0 = "UTF-8";	 Catch:{ Exception -> 0x011a }
            r0 = java.net.URLEncoder.encode(r3, r0);	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
        L_0x00b2:
            r0 = android.text.TextUtils.isEmpty(r2);	 Catch:{ Exception -> 0x011a }
            if (r0 != 0) goto L_0x00d1;	 Catch:{ Exception -> 0x011a }
        L_0x00b8:
            r0 = r6.length();	 Catch:{ Exception -> 0x011a }
            if (r0 == 0) goto L_0x00c3;	 Catch:{ Exception -> 0x011a }
        L_0x00be:
            r0 = "&";	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
        L_0x00c3:
            r0 = "pass=";	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
            r0 = "UTF-8";	 Catch:{ Exception -> 0x011a }
            r0 = java.net.URLEncoder.encode(r2, r0);	 Catch:{ Exception -> 0x011a }
            r6.append(r0);	 Catch:{ Exception -> 0x011a }
        L_0x00d1:
            r0 = r6.length();
            if (r0 != 0) goto L_0x00d8;
        L_0x00d7:
            return;
        L_0x00d8:
            r0 = new android.content.Intent;
            r1 = "android.intent.action.SEND";
            r0.<init>(r1);
            r1 = "text/plain";
            r0.setType(r1);
            r1 = "android.intent.extra.TEXT";
            r2 = new java.lang.StringBuilder;
            r2.<init>();
            r3 = "https://t.me/socks?";
            r2.append(r3);
            r6 = r6.toString();
            r2.append(r6);
            r6 = r2.toString();
            r0.putExtra(r1, r6);
            r6 = "ShareLink";
            r1 = NUM; // 0x7f0c05f0 float:1.8612275E38 double:1.0530981494E-314;
            r6 = org.telegram.messenger.LocaleController.getString(r6, r1);
            r6 = android.content.Intent.createChooser(r0, r6);
            r0 = 268435456; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
            r6.setFlags(r0);
            r0 = org.telegram.ui.ProxySettingsActivity.this;
            r0 = r0.getParentActivity();
            r0.startActivity(r6);
            goto L_0x011b;
        L_0x011a:
            return;
        L_0x011b:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProxySettingsActivity.1.onItemClick(int):void");
        }
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putBoolean("proxy_enabled", this.useProxySettings);
        edit.putBoolean("proxy_enabled_calls", this.useProxyForCalls);
        String obj = this.inputFields[0].getText().toString();
        String obj2 = this.inputFields[3].getText().toString();
        String obj3 = this.inputFields[2].getText().toString();
        int intValue = Utilities.parseInt(this.inputFields[1].getText().toString()).intValue();
        edit.putString("proxy_ip", obj);
        edit.putString("proxy_pass", obj2);
        edit.putString("proxy_user", obj3);
        edit.putInt("proxy_port", intValue);
        edit.commit();
        for (int i = 0; i < 3; i++) {
            if (this.useProxySettings) {
                ConnectionsManager.native_setProxySettings(i, obj, intValue, obj3, obj2);
            } else {
                ConnectionsManager.native_setProxySettings(i, TtmlNode.ANONYMOUS_REGION_ID, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        final SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        this.useProxySettings = globalMainSettings.getBoolean("proxy_enabled", false);
        this.useProxyForCalls = globalMainSettings.getBoolean("proxy_enabled_calls", false);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", C0446R.string.ProxySettings));
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C22661());
        this.shareItem = this.actionBar.createMenu().addItem(1, (int) C0446R.drawable.abc_ic_menu_share_mtrl_alpha);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.scrollView = new ScrollView(context2);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        this.linearLayout2 = new LinearLayout(context2);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        this.checkCell1 = new TextCheckCell(context2);
        this.checkCell1.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.checkCell1.setTextAndCheck(LocaleController.getString("UseProxySettings", C0446R.string.UseProxySettings), this.useProxySettings, false);
        this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
        this.checkCell1.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                ProxySettingsActivity.this.useProxySettings = ProxySettingsActivity.this.useProxySettings ^ 1;
                ProxySettingsActivity.this.checkCell1.setChecked(ProxySettingsActivity.this.useProxySettings);
                if (ProxySettingsActivity.this.useProxySettings == null) {
                    ProxySettingsActivity.this.useForCallsCell.setChecked(false);
                    globalMainSettings.edit().putBoolean("proxy_enabled_calls", false).commit();
                }
                ProxySettingsActivity.this.useForCallsCell.setEnabled(ProxySettingsActivity.this.useProxySettings);
            }
        });
        this.sectionCell = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[4];
        int i = 0;
        while (i < 4) {
            EditTextBoldCursor editTextBoldCursor;
            View frameLayout2 = new FrameLayout(context2);
            r0.linearLayout2.addView(frameLayout2, LayoutHelper.createLinear(-1, 48));
            frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            int i2 = 3;
            if (i != 3) {
                View view = new View(context2);
                r0.dividers.add(view);
                view.setBackgroundColor(Theme.getColor(Theme.key_divider));
                frameLayout2.addView(view, new LayoutParams(-1, 1, 83));
            }
            r0.inputFields[i] = new EditTextBoldCursor(context2);
            r0.inputFields[i].setTag(Integer.valueOf(i));
            r0.inputFields[i].setTextSize(1, 16.0f);
            r0.inputFields[i].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.inputFields[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.inputFields[i].setBackgroundDrawable(null);
            r0.inputFields[i].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.inputFields[i].setCursorSize(AndroidUtilities.dp(20.0f));
            r0.inputFields[i].setCursorWidth(1.5f);
            r0.inputFields[i].setSingleLine(true);
            if (i == 0) {
                r0.inputFields[i].addTextChangedListener(new C16583());
            } else if (i == 1) {
                r0.inputFields[i].setInputType(2);
                r0.inputFields[i].addTextChangedListener(new C16594());
            } else if (i == 3) {
                r0.inputFields[i].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                r0.inputFields[i].setTypeface(Typeface.DEFAULT);
                r0.inputFields[i].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                r0.inputFields[i].setInputType(1);
            }
            r0.inputFields[i].setImeOptions(268435461);
            switch (i) {
                case 0:
                    r0.inputFields[i].setHint(LocaleController.getString("UseProxyAddress", C0446R.string.UseProxyAddress));
                    r0.inputFields[i].setText(globalMainSettings.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                case 1:
                    r0.inputFields[i].setHint(LocaleController.getString("UseProxyPort", C0446R.string.UseProxyPort));
                    editTextBoldCursor = r0.inputFields[i];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(globalMainSettings.getInt("proxy_port", 1080));
                    editTextBoldCursor.setText(stringBuilder.toString());
                    break;
                case 2:
                    r0.inputFields[i].setHint(LocaleController.getString("UseProxyUsername", C0446R.string.UseProxyUsername));
                    r0.inputFields[i].setText(globalMainSettings.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                case 3:
                    r0.inputFields[i].setHint(LocaleController.getString("UseProxyPassword", C0446R.string.UseProxyPassword));
                    r0.inputFields[i].setText(globalMainSettings.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                default:
                    break;
            }
            r0.inputFields[i].setSelection(r0.inputFields[i].length());
            r0.inputFields[i].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            editTextBoldCursor = r0.inputFields[i];
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            editTextBoldCursor.setGravity(i2);
            frameLayout2.addView(r0.inputFields[i], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            r0.inputFields[i].setOnEditorActionListener(new C16605());
            i++;
        }
        r0.bottomCell = new TextInfoPrivacyCell(context2);
        r0.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        r0.bottomCell.setText(LocaleController.getString("UseProxyInfo", C0446R.string.UseProxyInfo));
        r0.linearLayout2.addView(r0.bottomCell, LayoutHelper.createLinear(-1, -2));
        r0.useForCallsCell = new TextCheckCell(context2);
        r0.useForCallsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        r0.useForCallsCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", C0446R.string.UseProxyForCalls), r0.useProxyForCalls, false);
        r0.useForCallsCell.setEnabled(r0.useProxySettings);
        r0.linearLayout2.addView(r0.useForCallsCell, LayoutHelper.createLinear(-1, -2));
        r0.useForCallsCell.setOnClickListener(new C16616());
        View textInfoPrivacyCell = new TextInfoPrivacyCell(context2);
        textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, C0446R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        textInfoPrivacyCell.setText(LocaleController.getString("UseProxyForCallsInfo", C0446R.string.UseProxyForCallsInfo));
        r0.linearLayout2.addView(textInfoPrivacyCell, LayoutHelper.createLinear(-1, -2));
        checkShareButton();
        return r0.fragmentView;
    }

    private void checkShareButton() {
        if (this.inputFields[0] != null) {
            if (this.inputFields[1] != null) {
                if (this.inputFields[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0) {
                    this.shareItem.setAlpha(0.5f);
                    this.shareItem.setEnabled(false);
                } else {
                    this.shareItem.setAlpha(1.0f);
                    this.shareItem.setEnabled(true);
                }
            }
        }
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.proxySettingsChanged && this.checkCell1 != 0) {
            i = MessagesController.getGlobalMainSettings();
            objArr = null;
            this.useProxySettings = i.getBoolean("proxy_enabled", false);
            if (this.useProxySettings == 0) {
                this.checkCell1.setChecked(false);
            } else {
                this.checkCell1.setChecked(true);
                while (objArr < 4) {
                    switch (objArr) {
                        case null:
                            this.inputFields[objArr].setText(i.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID));
                            break;
                        case 1:
                            i2 = this.inputFields[objArr];
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                            stringBuilder.append(i.getInt("proxy_port", 1080));
                            i2.setText(stringBuilder.toString());
                            break;
                        case 2:
                            this.inputFields[objArr].setText(i.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID));
                            break;
                        case 3:
                            this.inputFields[objArr].setText(i.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID));
                            break;
                        default:
                            break;
                    }
                    objArr++;
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int i;
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        arrayList.add(new ThemeDescription(this.linearLayout2, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        if (this.inputFields != null) {
            for (i = 0; i < r0.inputFields.length; i++) {
                arrayList.add(new ThemeDescription((View) r0.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        arrayList.add(new ThemeDescription(r0.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        View view = r0.sectionCell;
        View view2 = view;
        arrayList.add(new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(r0.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(r0.bottomCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(r0.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        for (i = 0; i < r0.dividers.size(); i++) {
            arrayList.add(new ThemeDescription((View) r0.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
        }
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumb));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchThumbChecked));
        arrayList.add(new ThemeDescription(r0.checkCell1, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(r0.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(r0.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, Theme.key_listSelector));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
