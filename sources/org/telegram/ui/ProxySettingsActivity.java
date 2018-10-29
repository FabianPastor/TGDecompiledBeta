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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.google.android.exoplayer2.C0020C;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0431R;
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

    /* renamed from: org.telegram.ui.ProxySettingsActivity$1 */
    class C16701 extends ActionBarMenuOnItemClick {
        C16701() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                ProxySettingsActivity.this.finishFragment();
            } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                boolean enabled;
                ProxySettingsActivity.this.currentProxyInfo.address = ProxySettingsActivity.this.inputFields[0].getText().toString();
                ProxySettingsActivity.this.currentProxyInfo.port = Utilities.parseInt(ProxySettingsActivity.this.inputFields[1].getText().toString()).intValue();
                if (ProxySettingsActivity.this.currentType == 0) {
                    ProxySettingsActivity.this.currentProxyInfo.secret = TtmlNode.ANONYMOUS_REGION_ID;
                    ProxySettingsActivity.this.currentProxyInfo.username = ProxySettingsActivity.this.inputFields[2].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.password = ProxySettingsActivity.this.inputFields[3].getText().toString();
                } else {
                    ProxySettingsActivity.this.currentProxyInfo.secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
                    ProxySettingsActivity.this.currentProxyInfo.username = TtmlNode.ANONYMOUS_REGION_ID;
                    ProxySettingsActivity.this.currentProxyInfo.password = TtmlNode.ANONYMOUS_REGION_ID;
                }
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                Editor editor = preferences.edit();
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
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$2 */
    class C16712 implements OnClickListener {
        C16712() {
        }

        public void onClick(View view) {
            ProxySettingsActivity.this.currentType = ((Integer) view.getTag()).intValue();
            ProxySettingsActivity.this.updateUiForType();
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$3 */
    class C16723 implements TextWatcher {
        C16723() {
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
    class C16734 implements TextWatcher {
        C16734() {
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
                if (port < 0 || port > 65535 || !str.equals(builder.toString())) {
                    if (port < 0) {
                        phoneField.setText("0");
                    } else if (port > 65535) {
                        phoneField.setText("65535");
                    } else {
                        phoneField.setText(builder.toString());
                    }
                } else if (start >= 0) {
                    if (start > phoneField.length()) {
                        start = phoneField.length();
                    }
                    phoneField.setSelection(start);
                }
                ProxySettingsActivity.this.ignoreOnTextChange = false;
                ProxySettingsActivity.this.checkShareButton();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProxySettingsActivity$5 */
    class C16745 implements OnEditorActionListener {
        C16745() {
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
    class C16756 implements OnClickListener {
        C16756() {
        }

        public void onClick(View v) {
            StringBuilder params = new StringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
            String address = ProxySettingsActivity.this.inputFields[0].getText().toString();
            String password = ProxySettingsActivity.this.inputFields[3].getText().toString();
            String user = ProxySettingsActivity.this.inputFields[2].getText().toString();
            String port = ProxySettingsActivity.this.inputFields[1].getText().toString();
            String secret = ProxySettingsActivity.this.inputFields[4].getText().toString();
            try {
                String url;
                if (!TextUtils.isEmpty(address)) {
                    params.append("server=").append(URLEncoder.encode(address, C0020C.UTF8_NAME));
                }
                if (!TextUtils.isEmpty(port)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("port=").append(URLEncoder.encode(port, C0020C.UTF8_NAME));
                }
                if (ProxySettingsActivity.this.currentType == 1) {
                    url = "https://t.me/proxy?";
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("secret=").append(URLEncoder.encode(secret, C0020C.UTF8_NAME));
                } else {
                    url = "https://t.me/socks?";
                    if (!TextUtils.isEmpty(user)) {
                        if (params.length() != 0) {
                            params.append("&");
                        }
                        params.append("user=").append(URLEncoder.encode(user, C0020C.UTF8_NAME));
                    }
                    if (!TextUtils.isEmpty(password)) {
                        if (params.length() != 0) {
                            params.append("&");
                        }
                        params.append("pass=").append(URLEncoder.encode(password, C0020C.UTF8_NAME));
                    }
                }
                if (params.length() != 0) {
                    Intent shareIntent = new Intent("android.intent.action.SEND");
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra("android.intent.extra.TEXT", url + params.toString());
                    Intent chooserIntent = Intent.createChooser(shareIntent, LocaleController.getString("ShareLink", C0431R.string.ShareLink));
                    chooserIntent.setFlags(C0020C.ENCODING_PCM_MU_LAW);
                    ProxySettingsActivity.this.getParentActivity().startActivity(chooserIntent);
                }
            } catch (Exception e) {
            }
        }
    }

    public class TypeCell extends FrameLayout {
        private ImageView checkImage;
        private boolean needDivider;
        private TextView textView;
        final /* synthetic */ ProxySettingsActivity this$0;

        public TypeCell(ProxySettingsActivity this$0, Context context) {
            int i;
            float f = 17.0f;
            int i2 = 3;
            this.this$0 = this$0;
            super(context);
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            View view = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            i |= 48;
            float f2 = LocaleController.isRTL ? 71.0f : 17.0f;
            if (!LocaleController.isRTL) {
                f = 23.0f;
            }
            addView(view, LayoutHelper.createFrame(-1, -1.0f, i, f2, 0.0f, f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), Mode.MULTIPLY));
            this.checkImage.setImageResource(C0431R.drawable.sticker_added);
            view = this.checkImage;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(view, LayoutHelper.createFrame(19, 14.0f, i2 | 16, 18.0f, 0.0f, 18.0f, 0.0f));
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(48.0f), NUM));
        }

        public void setValue(String name, boolean checked, boolean divider) {
            this.textView.setText(name);
            this.checkImage.setVisibility(checked ? 0 : 4);
            this.needDivider = divider;
        }

        public void setTypeChecked(boolean value) {
            this.checkImage.setVisibility(value ? 0 : 4);
        }

        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine((float) getPaddingLeft(), (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public ProxySettingsActivity() {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = new ProxyInfo(TtmlNode.ANONYMOUS_REGION_ID, 1080, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID, TtmlNode.ANONYMOUS_REGION_ID);
        this.addingNewProxy = true;
    }

    public ProxySettingsActivity(ProxyInfo proxyInfo) {
        this.sectionCell = new ShadowSectionCell[2];
        this.typeCell = new TypeCell[2];
        this.currentProxyInfo = proxyInfo;
        this.currentType = TextUtils.isEmpty(proxyInfo.secret) ? 0 : 1;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", C0431R.string.ProxyDetails));
        this.actionBar.setBackButtonImage(C0431R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new C16701());
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, C0431R.drawable.ic_done, AndroidUtilities.dp(56.0f));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_actionBarDefault));
        frameLayout.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f));
        this.linearLayout2 = new LinearLayout(context);
        this.linearLayout2.setOrientation(1);
        this.scrollView.addView(this.linearLayout2, new LayoutParams(-1, -2));
        int a = 0;
        while (a < 2) {
            this.typeCell[a] = new TypeCell(this, context);
            this.typeCell[a].setBackgroundDrawable(Theme.getSelectorDrawable(true));
            this.typeCell[a].setTag(Integer.valueOf(a));
            if (a == 0) {
                this.typeCell[a].setValue(LocaleController.getString("UseProxySocks5", C0431R.string.UseProxySocks5), a == this.currentType, true);
            } else if (a == 1) {
                this.typeCell[a].setValue(LocaleController.getString("UseProxyTelegram", C0431R.string.UseProxyTelegram), a == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[a], LayoutHelper.createLinear(-1, 48));
            this.typeCell[a].setOnClickListener(new C16712());
            a++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[5];
        for (a = 0; a < 5; a++) {
            FrameLayout container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.inputFields[a].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setSingleLine(true);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[a].setHeaderHintColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), Theme.getColor(Theme.key_windowBackgroundWhiteRedText3));
            if (a == 0) {
                this.inputFields[a].setInputType(524305);
                this.inputFields[a].addTextChangedListener(new C16723());
            } else if (a == 1) {
                this.inputFields[a].setInputType(2);
                this.inputFields[a].addTextChangedListener(new C16734());
            } else if (a == 3) {
                this.inputFields[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                this.inputFields[a].setTypeface(Typeface.DEFAULT);
                this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[a].setInputType(524289);
            }
            this.inputFields[a].setImeOptions(268435461);
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyAddress", C0431R.string.UseProxyAddress));
                    this.inputFields[a].setText(this.currentProxyInfo.address);
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyPort", C0431R.string.UseProxyPort));
                    this.inputFields[a].setText(TtmlNode.ANONYMOUS_REGION_ID + this.currentProxyInfo.port);
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyUsername", C0431R.string.UseProxyUsername));
                    this.inputFields[a].setText(this.currentProxyInfo.username);
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyPassword", C0431R.string.UseProxyPassword));
                    this.inputFields[a].setText(this.currentProxyInfo.password);
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxySecret", C0431R.string.UseProxySecret));
                    this.inputFields[a].setText(this.currentProxyInfo.secret);
                    break;
                default:
                    break;
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new C16745());
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, C0431R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.bottomCell.setText(LocaleController.getString("UseProxyInfo", C0431R.string.UseProxyInfo));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell = new TextSettingsCell(context);
        this.shareCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", C0431R.string.ShareFile), false);
        this.shareCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new C16756());
        this.sectionCell[1] = new ShadowSectionCell(context);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, C0431R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        checkShareButton();
        updateUiForType();
        return this.fragmentView;
    }

    private void checkShareButton() {
        if (this.shareCell != null && this.doneItem != null && this.inputFields[0] != null && this.inputFields[1] != null) {
            if (this.inputFields[0].length() == 0 || Utilities.parseInt(this.inputFields[1].getText().toString()).intValue() == 0) {
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

    private void updateUiForType() {
        boolean z;
        boolean z2 = true;
        if (this.currentType == 0) {
            this.bottomCell.setText(LocaleController.getString("UseProxyInfo", C0431R.string.UseProxyInfo));
            ((View) this.inputFields[4].getParent()).setVisibility(8);
            ((View) this.inputFields[3].getParent()).setVisibility(0);
            ((View) this.inputFields[2].getParent()).setVisibility(0);
        } else if (this.currentType == 1) {
            this.bottomCell.setText(LocaleController.getString("UseProxyTelegramInfo", C0431R.string.UseProxyTelegramInfo) + "\n\n" + LocaleController.getString("UseProxyTelegramInfo2", C0431R.string.UseProxyTelegramInfo2));
            ((View) this.inputFields[4].getParent()).setVisibility(0);
            ((View) this.inputFields[3].getParent()).setVisibility(8);
            ((View) this.inputFields[2].getParent()).setVisibility(8);
        }
        TypeCell typeCell = this.typeCell[0];
        if (this.currentType == 0) {
            z = true;
        } else {
            z = false;
        }
        typeCell.setTypeChecked(z);
        TypeCell typeCell2 = this.typeCell[1];
        if (this.currentType != 1) {
            z2 = false;
        }
        typeCell2.setTypeChecked(z2);
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
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
        arrayList.add(new ThemeDescription(this.shareCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText));
        for (a = 0; a < this.typeCell.length; a++) {
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            arrayList.add(new ThemeDescription(this.typeCell[a], 0, new Class[]{TypeCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, null, null, null, Theme.key_featuredStickers_addedIcon));
        }
        if (this.inputFields != null) {
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
        for (a = 0; a < 2; a++) {
            arrayList.add(new ThemeDescription(this.sectionCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        }
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.bottomCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
