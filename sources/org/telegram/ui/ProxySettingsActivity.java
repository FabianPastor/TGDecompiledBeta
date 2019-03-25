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
        final /* synthetic */ ProxySettingsActivity this$0;

        public TypeCell(ProxySettingsActivity this$0, Context context) {
            int i;
            int i2 = 3;
            this.this$0 = this$0;
            super(context);
            setWillNotDraw(false);
            this.textView = new TextView(context);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView = this.textView;
            if (LocaleController.isRTL) {
                i = 5;
            } else {
                i = 3;
            }
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, LocaleController.isRTL ? 71.0f : 21.0f, 0.0f, LocaleController.isRTL ? 21.0f : 23.0f, 0.0f));
            this.checkImage = new ImageView(context);
            this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), Mode.MULTIPLY));
            this.checkImage.setImageResource(NUM);
            ImageView imageView = this.checkImage;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            addView(imageView, LayoutHelper.createFrame(19, 14.0f, i2 | 16, 21.0f, 0.0f, 21.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), NUM), MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(50.0f), NUM));
        }

        public void setValue(String name, boolean checked, boolean divider) {
            this.textView.setText(name);
            this.checkImage.setVisibility(checked ? 0 : 4);
            this.needDivider = divider;
        }

        public void setTypeChecked(boolean value) {
            this.checkImage.setVisibility(value ? 0 : 4);
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
        this.currentType = TextUtils.isEmpty(proxyInfo.secret) ? 0 : 1;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    public View createView(Context context) {
        this.actionBar.setTitle(LocaleController.getString("ProxyDetails", NUM));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ProxySettingsActivity.this.finishFragment();
                } else if (id == 1 && ProxySettingsActivity.this.getParentActivity() != null) {
                    boolean enabled;
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
        });
        this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, NUM, AndroidUtilities.dp(56.0f));
        this.doneItem.setContentDescription(LocaleController.getString("Done", NUM));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.scrollView = new ScrollView(context);
        this.scrollView.setFillViewport(true);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor("actionBarDefault"));
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
                this.typeCell[a].setValue(LocaleController.getString("UseProxySocks5", NUM), a == this.currentType, true);
            } else if (a == 1) {
                this.typeCell[a].setValue(LocaleController.getString("UseProxyTelegram", NUM), a == this.currentType, false);
            }
            this.linearLayout2.addView(this.typeCell[a], LayoutHelper.createLinear(-1, 50));
            this.typeCell[a].setOnClickListener(new ProxySettingsActivity$$Lambda$0(this));
            a++;
        }
        this.sectionCell[0] = new ShadowSectionCell(context);
        this.linearLayout2.addView(this.sectionCell[0], LayoutHelper.createLinear(-1, -2));
        this.inputFields = new EditTextBoldCursor[5];
        for (a = 0; a < 5; a++) {
            FrameLayout container = new FrameLayout(context);
            this.linearLayout2.addView(container, LayoutHelper.createLinear(-1, 64));
            container.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            this.inputFields[a] = new EditTextBoldCursor(context);
            this.inputFields[a].setTag(Integer.valueOf(a));
            this.inputFields[a].setTextSize(1, 16.0f);
            this.inputFields[a].setHintColor(Theme.getColor("windowBackgroundWhiteHintText"));
            this.inputFields[a].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setBackgroundDrawable(null);
            this.inputFields[a].setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.inputFields[a].setCursorSize(AndroidUtilities.dp(20.0f));
            this.inputFields[a].setCursorWidth(1.5f);
            this.inputFields[a].setSingleLine(true);
            this.inputFields[a].setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.inputFields[a].setHeaderHintColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            this.inputFields[a].setTransformHintToHeader(true);
            this.inputFields[a].setLineColors(Theme.getColor("windowBackgroundWhiteInputField"), Theme.getColor("windowBackgroundWhiteInputFieldActivated"), Theme.getColor("windowBackgroundWhiteRedText3"));
            if (a == 0) {
                this.inputFields[a].setInputType(524305);
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        ProxySettingsActivity.this.checkShareButton();
                    }
                });
            } else if (a == 1) {
                this.inputFields[a].setInputType(2);
                this.inputFields[a].addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        if (!ProxySettingsActivity.this.ignoreOnTextChange) {
                            EditText phoneField = ProxySettingsActivity.this.inputFields[1];
                            int start = phoneField.getSelectionStart();
                            String chars = "NUM";
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
                });
            } else if (a == 3) {
                this.inputFields[a].setInputType(129);
                this.inputFields[a].setTypeface(Typeface.DEFAULT);
                this.inputFields[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else {
                this.inputFields[a].setInputType(524289);
            }
            this.inputFields[a].setImeOptions(NUM);
            switch (a) {
                case 0:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyAddress", NUM));
                    this.inputFields[a].setText(this.currentProxyInfo.address);
                    break;
                case 1:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyPort", NUM));
                    this.inputFields[a].setText("" + this.currentProxyInfo.port);
                    break;
                case 2:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyUsername", NUM));
                    this.inputFields[a].setText(this.currentProxyInfo.username);
                    break;
                case 3:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxyPassword", NUM));
                    this.inputFields[a].setText(this.currentProxyInfo.password);
                    break;
                case 4:
                    this.inputFields[a].setHintText(LocaleController.getString("UseProxySecret", NUM));
                    this.inputFields[a].setText(this.currentProxyInfo.secret);
                    break;
                default:
                    break;
            }
            this.inputFields[a].setSelection(this.inputFields[a].length());
            this.inputFields[a].setPadding(0, 0, 0, 0);
            container.addView(this.inputFields[a], LayoutHelper.createFrame(-1, -1.0f, 51, 17.0f, 0.0f, 17.0f, 0.0f));
            this.inputFields[a].setOnEditorActionListener(new ProxySettingsActivity$$Lambda$1(this));
        }
        this.bottomCell = new TextInfoPrivacyCell(context);
        this.bottomCell.setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.bottomCell.setText(LocaleController.getString("UseProxyInfo", NUM));
        this.linearLayout2.addView(this.bottomCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell = new TextSettingsCell(context);
        this.shareCell.setBackgroundDrawable(Theme.getSelectorDrawable(true));
        this.shareCell.setText(LocaleController.getString("ShareFile", NUM), false);
        this.shareCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.linearLayout2.addView(this.shareCell, LayoutHelper.createLinear(-1, -2));
        this.shareCell.setOnClickListener(new ProxySettingsActivity$$Lambda$2(this));
        this.sectionCell[1] = new ShadowSectionCell(context);
        this.sectionCell[1].setBackgroundDrawable(Theme.getThemedDrawable(context, NUM, "windowBackgroundGrayShadow"));
        this.linearLayout2.addView(this.sectionCell[1], LayoutHelper.createLinear(-1, -2));
        checkShareButton();
        updateUiForType();
        return this.fragmentView;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$0$ProxySettingsActivity(View view) {
        this.currentType = ((Integer) view.getTag()).intValue();
        updateUiForType();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ boolean lambda$createView$1$ProxySettingsActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5) {
            int num = ((Integer) textView.getTag()).intValue();
            if (num + 1 < this.inputFields.length) {
                this.inputFields[num + 1].requestFocus();
            }
            return true;
        } else if (i != 6) {
            return false;
        } else {
            finishFragment();
            return true;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createView$2$ProxySettingsActivity(View v) {
        StringBuilder params = new StringBuilder("");
        String address = this.inputFields[0].getText().toString();
        String password = this.inputFields[3].getText().toString();
        String user = this.inputFields[2].getText().toString();
        String port = this.inputFields[1].getText().toString();
        String secret = this.inputFields[4].getText().toString();
        try {
            String url;
            if (!TextUtils.isEmpty(address)) {
                params.append("server=").append(URLEncoder.encode(address, "UTF-8"));
            }
            if (!TextUtils.isEmpty(port)) {
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("port=").append(URLEncoder.encode(port, "UTF-8"));
            }
            if (this.currentType == 1) {
                url = "https://t.me/proxy?";
                if (params.length() != 0) {
                    params.append("&");
                }
                params.append("secret=").append(URLEncoder.encode(secret, "UTF-8"));
            } else {
                url = "https://t.me/socks?";
                if (!TextUtils.isEmpty(user)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("user=").append(URLEncoder.encode(user, "UTF-8"));
                }
                if (!TextUtils.isEmpty(password)) {
                    if (params.length() != 0) {
                        params.append("&");
                    }
                    params.append("pass=").append(URLEncoder.encode(password, "UTF-8"));
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
            this.bottomCell.setText(LocaleController.getString("UseProxyInfo", NUM));
            ((View) this.inputFields[4].getParent()).setVisibility(8);
            ((View) this.inputFields[3].getParent()).setVisibility(0);
            ((View) this.inputFields[2].getParent()).setVisibility(0);
        } else if (this.currentType == 1) {
            this.bottomCell.setText(LocaleController.getString("UseProxyTelegramInfo", NUM) + "\n\n" + LocaleController.getString("UseProxyTelegramInfo2", NUM));
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

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.addingNewProxy) {
            this.inputFields[0].requestFocus();
            AndroidUtilities.showKeyboard(this.inputFields[0]);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        int a;
        ArrayList<ThemeDescription> arrayList = new ArrayList();
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
        arrayList.add(new ThemeDescription(this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.shareCell, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteValueText"));
        for (a = 0; a < this.typeCell.length; a++) {
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.typeCell[a], 0, new Class[]{TypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(this.typeCell[a], ThemeDescription.FLAG_IMAGECOLOR, new Class[]{TypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon"));
        }
        if (this.inputFields != null) {
            for (a = 0; a < this.inputFields.length; a++) {
                arrayList.add(new ThemeDescription((View) this.inputFields[a].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
                arrayList.add(new ThemeDescription(this.inputFields[a], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
            }
        } else {
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
            arrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        }
        arrayList.add(new ThemeDescription(this.headerCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader"));
        for (a = 0; a < 2; a++) {
            arrayList.add(new ThemeDescription(this.sectionCell[a], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        }
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.bottomCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.bottomCell, ThemeDescription.FLAG_LINKCOLOR, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteLinkText"));
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[arrayList.size()]);
    }
}
