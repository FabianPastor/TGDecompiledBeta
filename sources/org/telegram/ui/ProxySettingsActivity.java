package org.telegram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.C0539C;
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
    class C16523 implements TextWatcher {
        C16523() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            ProxySettingsActivity.this.checkShareButton();
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$4 */
    class C16534 implements TextWatcher {
        C16534() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                EditText phoneField = ProxySettingsActivity.this.inputFields[1];
                int start = phoneField.getSelectionStart();
                String chars = "0123456789";
                String str = phoneField.getText().toString();
                StringBuilder builder = new StringBuilder(str.length());
                for (int a = 0; a < str.length(); a++) {
                    String ch = str.substring(a, a + 1);
                    if (chars.contains(ch)) {
                        builder.append(ch);
                    }
                }
                ProxySettingsActivity.this.ignoreOnTextChange = true;
                int port = Utilities.parseInt(builder.toString()).intValue();
                if (port >= 0 && port <= 65535) {
                    if (str.equals(builder.toString())) {
                        if (start >= 0) {
                            phoneField.setSelection(start <= phoneField.length() ? start : phoneField.length());
                        }
                        ProxySettingsActivity.this.ignoreOnTextChange = false;
                        ProxySettingsActivity.this.checkShareButton();
                    }
                }
                if (port < 0) {
                    phoneField.setText("0");
                } else if (port > 65535) {
                    phoneField.setText("65535");
                } else {
                    phoneField.setText(builder.toString());
                }
                ProxySettingsActivity.this.ignoreOnTextChange = false;
                ProxySettingsActivity.this.checkShareButton();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$5 */
    class C16545 implements OnEditorActionListener {
        C16545() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                int num = ((Integer) textView.getTag()).intValue();
                if (num + 1 < ProxySettingsActivity.this.inputFields.length) {
                    ProxySettingsActivity.this.inputFields[num + 1].requestFocus();
                }
                return true;
            } else if (i != 6) {
                return false;
            } else {
                ProxySettingsActivity.this.finishFragment();
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$6 */
    class C16556 implements OnClickListener {
        C16556() {
        }

        public void onClick(View v) {
            ProxySettingsActivity.this.useProxyForCalls = ProxySettingsActivity.this.useProxyForCalls ^ 1;
            ProxySettingsActivity.this.useForCallsCell.setChecked(ProxySettingsActivity.this.useProxyForCalls);
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$1 */
    class C22601 extends ActionBarMenuOnItemClick {
        C22601() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ProxySettingsActivity.this.finishFragment();
            } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                StringBuilder params = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
                String address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                String password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                String user = ProxySettingsActivity.this.inputFields[2].getText().toString();
                String port = ProxySettingsActivity.this.inputFields[1].getText().toString();
                try {
                    if (!TextUtils.isEmpty(address)) {
                        params.append("server=");
                        params.append(URLEncoder.encode(address, C0539C.UTF8_NAME));
                    }
                    if (!TextUtils.isEmpty(port)) {
                        if (params.length() != 0) {
                            params.append("&");
                        }
                        params.append("port=");
                        params.append(URLEncoder.encode(port, C0539C.UTF8_NAME));
                    }
                    if (!TextUtils.isEmpty(user)) {
                        if (params.length() != 0) {
                            params.append("&");
                        }
                        params.append("user=");
                        params.append(URLEncoder.encode(user, C0539C.UTF8_NAME));
                    }
                    if (!TextUtils.isEmpty(password)) {
                        if (params.length() != 0) {
                            params.append("&");
                        }
                        params.append("pass=");
                        params.append(URLEncoder.encode(password, C0539C.UTF8_NAME));
                    }
                    if (params.length() != 0) {
                        Intent shareIntent = new Intent("android.intent.action.SEND");
                        shareIntent.setType("text/plain");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("https://t.me/socks?");
                        stringBuilder.append(params.toString());
                        shareIntent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", R.string.ShareLink));
                        chooserIntent.setFlags(268435456);
                        ProxySettingsActivity.this.getParentActivity().startActivity(chooserIntent);
                    }
                } catch (Exception e) {
                }
            }
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
        Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putBoolean("proxy_enabled", this.useProxySettings);
        editor.putBoolean("proxy_enabled_calls", this.useProxyForCalls);
        String address = this.inputFields[0].getText().toString();
        String password = this.inputFields[3].getText().toString();
        String user = this.inputFields[2].getText().toString();
        int port = Utilities.parseInt(this.inputFields[1].getText().toString()).intValue();
        editor.putString("proxy_ip", address);
        editor.putString("proxy_pass", password);
        editor.putString("proxy_user", user);
        editor.putInt("proxy_port", port);
        editor.commit();
        for (int a = 0; a < 3; a++) {
            if (this.useProxySettings) {
                ConnectionsManager.native_setProxySettings(a, address, port, user, password);
            } else {
                ConnectionsManager.native_setProxySettings(a, TtmlNode.ANONYMOUS_REGION_ID, 0, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
            }
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        Context context2 = context;
        final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        this.useProxySettings = preferences.getBoolean("proxy_enabled", false);
        this.useProxyForCalls = preferences.getBoolean("proxy_enabled_calls", false);
        this.actionBar.setTitle(LocaleController.getString("ProxySettings", R.string.ProxySettings));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C22601());
        this.shareItem = this.actionBar.createMenu().addItem(1, (int) R.drawable.abc_ic_menu_share_mtrl_alpha);
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = this.fragmentView;
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
        this.checkCell1.setTextAndCheck(LocaleController.getString("UseProxySettings", R.string.UseProxySettings), this.useProxySettings, false);
        this.linearLayout2.addView(this.checkCell1, LayoutHelper.createLinear(-1, -2));
        this.checkCell1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ProxySettingsActivity.this.useProxySettings = ProxySettingsActivity.this.useProxySettings ^ 1;
                ProxySettingsActivity.this.checkCell1.setChecked(ProxySettingsActivity.this.useProxySettings);
                if (!ProxySettingsActivity.this.useProxySettings) {
                    ProxySettingsActivity.this.useForCallsCell.setChecked(false);
                    preferences.edit().putBoolean("proxy_enabled_calls", false).commit();
                }
                ProxySettingsActivity.this.useForCallsCell.setEnabled(ProxySettingsActivity.this.useProxySettings);
            }
        });
        this.sectionCell = new ShadowSectionCell(context2);
        this.linearLayout2.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
        int i = 4;
        this.inputFields = new EditTextBoldCursor[4];
        int a = 0;
        while (a < i) {
            FrameLayout container = new FrameLayout(context2);
            r0.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 48));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            int i2 = 3;
            if (a != 3) {
                View divider = new View(context2);
                r0.dividers.add(divider);
                divider.setBackgroundColor(Theme.getColor(Theme.key_divider));
                container.addView(divider, new LayoutParams(-1, 1, 83));
            }
            r0.inputFields[a] = new EditTextBoldCursor(context2);
            r0.inputFields[a].setTag(Integer.valueOf(a));
            r0.inputFields[a].setTextSize(1, 16.0f);
            r0.inputFields[a].setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            r0.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.inputFields[a].setBackgroundDrawable(null);
            r0.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            r0.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            r0.inputFields[a].setCursorWidth(1.5f);
            r0.inputFields[a].setSingleLine(true);
            if (a == 0) {
                r0.inputFields[a].addTextChangedListener(new C16523());
            } else if (a == 1) {
                r0.inputFields[a].setInputType(2);
                r0.inputFields[a].addTextChangedListener(new C16534());
            } else if (a == 3) {
                r0.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                r0.inputFields[a].setTypeface(Typeface.DEFAULT);
                r0.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                r0.inputFields[a].setInputType(1);
            }
            r0.inputFields[a].setImeOptions(268435461);
            switch (a) {
                case 0:
                    r0.inputFields[a].setHint(LocaleController.getString("UseProxyAddress", R.string.UseProxyAddress));
                    r0.inputFields[a].setText(preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                case 1:
                    r0.inputFields[a].setHint(LocaleController.getString("UseProxyPort", R.string.UseProxyPort));
                    EditTextBoldCursor editTextBoldCursor = r0.inputFields[a];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(preferences.getInt("proxy_port", 1080));
                    editTextBoldCursor.setText(stringBuilder.toString());
                    break;
                case 2:
                    r0.inputFields[a].setHint(LocaleController.getString("UseProxyUsername", R.string.UseProxyUsername));
                    r0.inputFields[a].setText(preferences.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                case 3:
                    r0.inputFields[a].setHint(LocaleController.getString("UseProxyPassword", R.string.UseProxyPassword));
                    r0.inputFields[a].setText(preferences.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID));
                    break;
                default:
                    break;
            }
            r0.inputFields[a].setSelection(r0.inputFields[a].length());
            r0.inputFields[a].setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            EditTextBoldCursor editTextBoldCursor2 = r0.inputFields[a];
            if (LocaleController.isRTL) {
                i2 = 5;
            }
            editTextBoldCursor2.setGravity(i2);
            container.addView(r0.inputFields[a], LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, 12.0f, 17.0f, 6.0f));
            r0.inputFields[a].setOnEditorActionListener(new C16545());
            a++;
            i = 4;
        }
        r0.bottomCell = new TextInfoPrivacyCell(context2);
        r0.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        r0.bottomCell.setText(LocaleController.getString("UseProxyInfo", R.string.UseProxyInfo));
        r0.linearLayout2.addView(r0.bottomCell, LayoutHelper.createLinear(-1, -2));
        r0.useForCallsCell = new TextCheckCell(context2);
        r0.useForCallsCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        r0.useForCallsCell.setTextAndCheck(LocaleController.getString("UseProxyForCalls", R.string.UseProxyForCalls), r0.useProxyForCalls, false);
        r0.useForCallsCell.setEnabled(r0.useProxySettings);
        r0.linearLayout2.addView(r0.useForCallsCell, LayoutHelper.createLinear(-1, -2));
        r0.useForCallsCell.setOnClickListener(new C16556());
        TextInfoPrivacyCell useForCallsInfoCell = new TextInfoPrivacyCell(context2);
        useForCallsInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        useForCallsInfoCell.setText(LocaleController.getString("UseProxyForCallsInfo", R.string.UseProxyForCallsInfo));
        r0.linearLayout2.addView(useForCallsInfoCell, LayoutHelper.createLinear(-1, -2));
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

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.proxySettingsChanged && this.checkCell1 != null) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            int a = 0;
            this.useProxySettings = preferences.getBoolean("proxy_enabled", false);
            if (this.useProxySettings) {
                this.checkCell1.setChecked(true);
                while (true) {
                    int a2 = a;
                    if (a2 < 4) {
                        switch (a2) {
                            case 0:
                                this.inputFields[a2].setText(preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID));
                                break;
                            case 1:
                                EditTextBoldCursor editTextBoldCursor = this.inputFields[a2];
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(preferences.getInt("proxy_port", 1080));
                                editTextBoldCursor.setText(stringBuilder.toString());
                                break;
                            case 2:
                                this.inputFields[a2].setText(preferences.getString("proxy_user", TtmlNode.ANONYMOUS_REGION_ID));
                                break;
                            case 3:
                                this.inputFields[a2].setText(preferences.getString("proxy_pass", TtmlNode.ANONYMOUS_REGION_ID));
                                break;
                            default:
                                break;
                        }
                        a = a2 + 1;
                    }
                }
            } else {
                this.checkCell1.setChecked(false);
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
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
            for (a = 0; a < r0.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) r0.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(r0.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(r0.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
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
        for (a = 0; a < r0.dividers.size(); a++) {
            arrayList.add(new ThemeDescription((View) r0.dividers.get(a), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_divider));
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
