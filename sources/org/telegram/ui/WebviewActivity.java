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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.C0542C;
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

        private boolean isInternalUrl(String url) {
            if (TextUtils.isEmpty(url)) {
                return false;
            }
            Uri uri = Uri.parse(url);
            if (!"tg".equals(uri.getScheme())) {
                return false;
            }
            WebviewActivity.this.finishFragment(false);
            try {
                Intent intent = new Intent("android.intent.action.VIEW", uri);
                intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                ApplicationLoader.applicationContext.startActivity(intent);
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            return true;
        }

        public void onLoadResource(WebView view, String url) {
            if (!isInternalUrl(url)) {
                super.onLoadResource(view, url);
            }
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!isInternalUrl(url)) {
                if (!super.shouldOverrideUrlLoading(view, url)) {
                    return false;
                }
            }
            return true;
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            WebviewActivity.this.progressItem.getImageView().setVisibility(0);
            WebviewActivity.this.progressItem.setEnabled(true);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[]{0.0f, 1.0f})});
            animatorSet.addListener(new C17731());
            animatorSet.setDuration(150);
            animatorSet.start();
        }
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(final String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    if (WebviewActivity.this.getParentActivity() != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d(eventName);
                        }
                        String str = eventName;
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

        public void onItemClick(int id) {
            if (id == -1) {
                WebviewActivity.this.finishFragment();
            } else if (id == 1) {
                WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            } else if (id == 2) {
                WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
            }
        }
    }

    public WebviewActivity(String url, String botName, String gameName, String startParam, MessageObject messageObject) {
        String str;
        this.currentUrl = url;
        this.currentBot = botName;
        this.currentGame = gameName;
        this.currentMessageObject = messageObject;
        this.short_param = startParam;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://");
        stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        stringBuilder.append("/");
        stringBuilder.append(this.currentBot);
        if (TextUtils.isEmpty(startParam)) {
            str = TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("?game=");
            stringBuilder2.append(startParam);
            str = stringBuilder2.toString();
        }
        stringBuilder.append(str);
        this.linkToCopy = stringBuilder.toString();
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(this.currentGame);
        ActionBar actionBar = this.actionBar;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("@");
        stringBuilder.append(this.currentBot);
        actionBar.setSubtitle(stringBuilder.toString());
        this.actionBar.setActionBarMenuOnItemClick(new C23172());
        ActionBarMenu menu = this.actionBar.createMenu();
        this.progressItem = menu.addItemWithWidth(1, R.drawable.share, AndroidUtilities.dp(54.0f));
        this.progressView = new ContextProgressView(context, 1);
        this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressItem.getImageView().setVisibility(4);
        menu.addItem(0, (int) R.drawable.ic_ab_other).addSubItem(2, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
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

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.webView != null) {
            this.webView.loadUrl(this.currentUrl);
        }
    }

    public static boolean supportWebview() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if ("samsung".equals(manufacturer) && "GT-I9500".equals(model)) {
            return false;
        }
        return true;
    }

    public static void openGameInBrowser(String urlStr, MessageObject messageObject, Activity parentActivity, String short_name, String username) {
        Throwable e;
        Activity activity;
        MessageObject messageObject2 = messageObject;
        String url = urlStr;
        String str;
        try {
            StringBuilder stringBuilder;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder2.append(messageObject.getId());
            String existing = sharedPreferences.getString(stringBuilder2.toString(), null);
            StringBuilder hash = new StringBuilder(existing != null ? existing : TtmlNode.ANONYMOUS_REGION_ID);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("tgShareScoreUrl=");
            stringBuilder3.append(URLEncoder.encode("tgb://share_game_score?hash=", C0542C.UTF8_NAME));
            StringBuilder addHash = new StringBuilder(stringBuilder3.toString());
            if (existing == null) {
                char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    hash.append(chars[Utilities.random.nextInt(chars.length)]);
                }
            }
            addHash.append(hash);
            int index = url.indexOf(35);
            if (index < 0) {
                StringBuilder stringBuilder4 = new StringBuilder();
                stringBuilder4.append(url);
                stringBuilder4.append("#");
                stringBuilder4.append(addHash);
                url = stringBuilder4.toString();
            } else {
                String curHash = url.substring(index + 1);
                if (curHash.indexOf(61) < 0) {
                    if (curHash.indexOf(63) < 0) {
                        if (curHash.length() > 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(url);
                            stringBuilder.append("?");
                            stringBuilder.append(addHash);
                            url = stringBuilder.toString();
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(url);
                            stringBuilder.append(addHash);
                            url = stringBuilder.toString();
                        }
                    }
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append(url);
                stringBuilder.append("&");
                stringBuilder.append(addHash);
                url = stringBuilder.toString();
            }
            Editor editor = sharedPreferences.edit();
            stringBuilder = new StringBuilder();
            stringBuilder.append(hash);
            stringBuilder.append("_date");
            editor.putInt(stringBuilder.toString(), (int) (System.currentTimeMillis() / 1000));
            SerializedData serializedData = new SerializedData(messageObject2.messageOwner.getObjectSize());
            messageObject2.messageOwner.serializeToStream(serializedData);
            StringBuilder stringBuilder5 = new StringBuilder();
            stringBuilder5.append(hash);
            stringBuilder5.append("_m");
            editor.putString(stringBuilder5.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
            stringBuilder5 = new StringBuilder();
            stringBuilder5.append(hash);
            stringBuilder5.append("_link");
            String stringBuilder6 = stringBuilder5.toString();
            StringBuilder stringBuilder7 = new StringBuilder();
            stringBuilder7.append("https://");
            stringBuilder7.append(MessagesController.getInstance(messageObject2.currentAccount).linkPrefix);
            stringBuilder7.append("/");
            try {
                String str2;
                stringBuilder7.append(username);
                if (TextUtils.isEmpty(short_name)) {
                    str2 = TtmlNode.ANONYMOUS_REGION_ID;
                    str = short_name;
                } else {
                    StringBuilder stringBuilder8 = new StringBuilder();
                    stringBuilder8.append("?game=");
                    try {
                        stringBuilder8.append(short_name);
                        str2 = stringBuilder8.toString();
                    } catch (Exception e2) {
                        e = e2;
                        activity = parentActivity;
                        FileLog.m3e(e);
                    }
                }
                stringBuilder7.append(str2);
                editor.putString(stringBuilder6, stringBuilder7.toString());
                editor.commit();
            } catch (Exception e3) {
                e = e3;
                activity = parentActivity;
                str = short_name;
                FileLog.m3e(e);
            }
            try {
                Browser.openUrl((Context) parentActivity, url, false);
            } catch (Exception e4) {
                e = e4;
                FileLog.m3e(e);
            }
        } catch (Exception e5) {
            e = e5;
            activity = parentActivity;
            str = short_name;
            String str3 = username;
            FileLog.m3e(e);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem), new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressInner2), new ThemeDescription(this.progressView, 0, null, null, null, null, Theme.key_contextProgressOuter2)};
    }
}
