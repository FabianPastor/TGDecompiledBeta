package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.net.URLEncoder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareAlert;

public class WebviewActivity extends BaseFragment {
    private static final int open_in = 2;
    private static final int share = 1;
    private String currentBot;
    private String currentGame;
    private MessageObject currentMessageObject;
    private String currentUrl;
    private String linkToCopy;
    private ActionBarMenuItem progressItem;
    private ContextProgressView progressView;
    private String short_param;
    public Runnable typingRunnable = new C17721();
    private WebView webView;

    /* renamed from: org.telegram.ui.WebviewActivity$1 */
    class C17721 implements Runnable {
        C17721() {
        }

        public void run() {
            if (!(WebviewActivity.this.currentMessageObject == null || WebviewActivity.this.getParentActivity() == null)) {
                if (WebviewActivity.this.typingRunnable != null) {
                    MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
                    AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.WebviewActivity$3 */
    class C17743 extends WebViewClient {

        /* renamed from: org.telegram.ui.WebviewActivity$3$1 */
        class C17731 extends AnimatorListenerAdapter {
            C17731() {
            }

            public void onAnimationEnd(Animator animator) {
                WebviewActivity.this.progressView.setVisibility(4);
            }
        }

        C17743() {
        }

        private boolean isInternalUrl(String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            str = Uri.parse(str);
            if (!"tg".equals(str.getScheme())) {
                return false;
            }
            WebviewActivity.this.finishFragment(false);
            try {
                Intent intent = new Intent("android.intent.action.VIEW", str);
                intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                ApplicationLoader.applicationContext.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            return true;
        }

        public void onLoadResource(WebView webView, String str) {
            if (!isInternalUrl(str)) {
                super.onLoadResource(webView, str);
            }
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (!isInternalUrl(str)) {
                if (super.shouldOverrideUrlLoading(webView, str) == null) {
                    return null;
                }
            }
            return true;
        }

        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            WebviewActivity.this.progressItem.getImageView().setVisibility(0);
            WebviewActivity.this.progressItem.setEnabled(true);
            webView = new AnimatorSet();
            webView.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[]{0.0f, 1.0f})});
            webView.addListener(new C17731());
            webView.setDuration(150);
            webView.start();
        }
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (WebviewActivity.this.getParentActivity() != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d(str);
                        }
                        String str = str;
                        boolean z = true;
                        int hashCode = str.hashCode();
                        if (hashCode != -NUM) {
                            if (hashCode == 406539826) {
                                if (str.equals("share_score")) {
                                    z = true;
                                }
                            }
                        } else if (str.equals("share_game")) {
                            z = false;
                        }
                        switch (z) {
                            case false:
                                WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                                break;
                            case true:
                                WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                                break;
                            default:
                                break;
                        }
                        WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
                    }
                }
            });
        }
    }

    /* renamed from: org.telegram.ui.WebviewActivity$2 */
    class C23172 extends ActionBarMenuOnItemClick {
        C23172() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                WebviewActivity.this.finishFragment();
            } else if (i == 1) {
                WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            } else if (i == 2) {
                WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
            }
        }
    }

    public WebviewActivity(String str, String str2, String str3, String str4, MessageObject messageObject) {
        this.currentUrl = str;
        this.currentBot = str2;
        this.currentGame = str3;
        this.currentMessageObject = messageObject;
        this.short_param = str4;
        str = new StringBuilder();
        str.append("https://");
        str.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        str.append("/");
        str.append(this.currentBot);
        if (TextUtils.isEmpty(str4) != null) {
            str2 = TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            str2 = new StringBuilder();
            str2.append("?game=");
            str2.append(str4);
            str2 = str2.toString();
        }
        str.append(str2);
        this.linkToCopy = str.toString();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable = null;
        try {
            ViewParent parent = this.webView.getParent();
            if (parent != null) {
                ((FrameLayout) parent).removeView(this.webView);
            }
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
            this.webView = null;
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.currentGame);
        ActionBar actionBar = this.actionBar;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@");
        stringBuilder.append(this.currentBot);
        actionBar.setSubtitle(stringBuilder.toString());
        this.actionBar.setActionBarMenuOnItemClick(new C23172());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.progressItem = createMenu.addItemWithWidth(1, C0446R.drawable.share, AndroidUtilities.dp(54.0f));
        this.progressView = new ContextProgressView(context, 1);
        this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressItem.getImageView().setVisibility(4);
        createMenu.addItem(0, (int) C0446R.drawable.ic_ab_other).addSubItem(2, LocaleController.getString("OpenInExternalApp", C0446R.string.OpenInExternalApp));
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
        }
        this.webView.setWebViewClient(new C17743());
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && this.webView) {
            this.webView.loadUrl(this.currentUrl);
        }
    }

    public static boolean supportWebview() {
        return ("samsung".equals(Build.MANUFACTURER) && "GT-I9500".equals(Build.MODEL)) ? false : true;
    }

    public static void openGameInBrowser(String str, MessageObject messageObject, Activity activity, String str2, String str3) {
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(messageObject.getId());
            String string = sharedPreferences.getString(stringBuilder.toString(), null);
            CharSequence stringBuilder2 = new StringBuilder(string != null ? string : TtmlNode.ANONYMOUS_REGION_ID);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("tgShareScoreUrl=");
            stringBuilder3.append(URLEncoder.encode("tgb://share_game_score?hash=", C0542C.UTF8_NAME));
            StringBuilder stringBuilder4 = new StringBuilder(stringBuilder3.toString());
            if (string == null) {
                char[] toCharArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    stringBuilder2.append(toCharArray[Utilities.random.nextInt(toCharArray.length)]);
                }
            }
            stringBuilder4.append(stringBuilder2);
            int indexOf = str.indexOf(35);
            if (indexOf < 0) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("#");
                stringBuilder.append(stringBuilder4);
                str = stringBuilder.toString();
            } else {
                string = str.substring(indexOf + 1);
                if (string.indexOf(61) < 0) {
                    if (string.indexOf(63) < 0) {
                        if (string.length() > 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append("?");
                            stringBuilder.append(stringBuilder4);
                            str = stringBuilder.toString();
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(str);
                            stringBuilder.append(stringBuilder4);
                            str = stringBuilder.toString();
                        }
                    }
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("&");
                stringBuilder.append(stringBuilder4);
                str = stringBuilder.toString();
            }
            Editor edit = sharedPreferences.edit();
            stringBuilder = new StringBuilder();
            stringBuilder.append(stringBuilder2);
            stringBuilder.append("_date");
            edit.putInt(stringBuilder.toString(), (int) (System.currentTimeMillis() / 1000));
            AbstractSerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder2);
            stringBuilder4.append("_m");
            edit.putString(stringBuilder4.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
            stringBuilder = new StringBuilder();
            stringBuilder.append(stringBuilder2);
            stringBuilder.append("_link");
            string = stringBuilder.toString();
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append("https://");
            stringBuilder5.append(MessagesController.getInstance(messageObject.currentAccount).linkPrefix);
            stringBuilder5.append("/");
            stringBuilder5.append(str3);
            if (TextUtils.isEmpty(str2) != null) {
                messageObject = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                messageObject = new StringBuilder();
                messageObject.append("?game=");
                messageObject.append(str2);
                messageObject = messageObject.toString();
            }
            stringBuilder5.append(messageObject);
            edit.putString(string, stringBuilder5.toString());
            edit.commit();
            Browser.openUrl((Context) activity, str, false);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2), new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2)};
    }
}
