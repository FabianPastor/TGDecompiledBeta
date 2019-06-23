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
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStatsURL;
import org.telegram.tgnet.TLRPC.TL_statsURL;
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
    private static final int TYPE_GAME = 0;
    private static final int TYPE_STAT = 1;
    private static final int open_in = 2;
    private static final int share = 1;
    private String currentBot;
    private long currentDialogId;
    private String currentGame;
    private MessageObject currentMessageObject;
    private String currentUrl;
    private String linkToCopy;
    private boolean loadStats;
    private ActionBarMenuItem progressItem;
    private ContextProgressView progressView;
    private String short_param;
    private int type;
    public Runnable typingRunnable = new Runnable() {
        public void run() {
            if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null) {
                WebviewActivity webviewActivity = WebviewActivity.this;
                if (webviewActivity.typingRunnable != null) {
                    MessagesController.getInstance(webviewActivity.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
                    AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000);
                }
            }
        }
    };
    private WebView webView;

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        /* synthetic */ TelegramWebviewProxy(WebviewActivity webviewActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$WebviewActivity$TelegramWebviewProxy$4b6v9uSCLRENGHQEEzvYSfqVMkw(this, str));
        }

        public /* synthetic */ void lambda$postEvent$0$WebviewActivity$TelegramWebviewProxy(String str) {
            if (WebviewActivity.this.getParentActivity() != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(str);
                }
                Object obj = -1;
                int hashCode = str.hashCode();
                if (hashCode != -NUM) {
                    if (hashCode == NUM && str.equals("share_score")) {
                        obj = 1;
                    }
                } else if (str.equals("share_game")) {
                    obj = null;
                }
                if (obj == null) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                } else if (obj == 1) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                }
                WebviewActivity webviewActivity = WebviewActivity.this;
                webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            }
        }
    }

    public WebviewActivity(String str, String str2, String str3, String str4, MessageObject messageObject) {
        this.currentUrl = str;
        this.currentBot = str2;
        this.currentGame = str3;
        this.currentMessageObject = messageObject;
        this.short_param = str4;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://");
        stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        stringBuilder.append("/");
        stringBuilder.append(this.currentBot);
        if (TextUtils.isEmpty(str4)) {
            str2 = "";
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("?game=");
            stringBuilder2.append(str4);
            str2 = stringBuilder2.toString();
        }
        stringBuilder.append(str2);
        this.linkToCopy = stringBuilder.toString();
        this.type = 0;
    }

    public WebviewActivity(String str, long j) {
        this.currentUrl = str;
        this.currentDialogId = j;
        this.type = 1;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.webView.setLayerType(0, null);
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
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WebviewActivity.this.finishFragment();
                } else if (i == 1) {
                    if (WebviewActivity.this.currentMessageObject != null) {
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                        WebviewActivity webviewActivity = WebviewActivity.this;
                        webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
                    }
                } else if (i == 2) {
                    WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        this.progressItem = createMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0f));
        int i = this.type;
        if (i == 0) {
            createMenu.addItem(0, NUM).addSubItem(2, NUM, LocaleController.getString("OpenInExternalApp", NUM));
            this.actionBar.setTitle(this.currentGame);
            ActionBar actionBar = this.actionBar;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("@");
            stringBuilder.append(this.currentBot);
            actionBar.setSubtitle(stringBuilder.toString());
            this.progressView = new ContextProgressView(context, 1);
            this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
        } else if (i == 1) {
            this.actionBar.setBackgroundColor(Theme.getColor("player_actionBar"));
            this.actionBar.setItemsColor(Theme.getColor("player_actionBarItems"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("player_actionBarSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("player_actionBarTitle"));
            this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
            this.actionBar.setTitle(LocaleController.getString("Statistics", NUM));
            this.progressView = new ContextProgressView(context, 3);
            this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        }
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        if (VERSION.SDK_INT >= 19) {
            this.webView.setLayerType(2, null);
        }
        if (VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            if (this.type == 0) {
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(this, null), "TelegramWebviewProxy");
            }
        }
        this.webView.setWebViewClient(new WebViewClient() {
            private boolean isInternalUrl(String str) {
                if (TextUtils.isEmpty(str)) {
                    return false;
                }
                Uri parse = Uri.parse(str);
                if (!"tg".equals(parse.getScheme())) {
                    return false;
                }
                if (WebviewActivity.this.type == 1) {
                    try {
                        WebviewActivity.this.reloadStats(Uri.parse(str.replace("tg:statsrefresh", "tg://telegram.org")).getQueryParameter("params"));
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                } else {
                    WebviewActivity.this.finishFragment(false);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", parse);
                        intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                        intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                        ApplicationLoader.applicationContext.startActivity(intent);
                    } catch (Exception th2) {
                        FileLog.e(th2);
                    }
                }
                return true;
            }

            public void onLoadResource(WebView webView, String str) {
                if (!isInternalUrl(str)) {
                    super.onLoadResource(webView, str);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                return isInternalUrl(str) || super.shouldOverrideUrlLoading(webView, str);
            }

            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (WebviewActivity.this.progressView != null && WebviewActivity.this.progressView.getVisibility() == 0) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    String str2 = "alpha";
                    String str3 = "scaleY";
                    String str4 = "scaleX";
                    if (WebviewActivity.this.type == 0) {
                        WebviewActivity.this.progressItem.getContentView().setVisibility(0);
                        WebviewActivity.this.progressItem.setEnabled(true);
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str4, new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str3, new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str2, new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), str4, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), str3, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), str2, new float[]{0.0f, 1.0f})});
                    } else {
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str4, new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str3, new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, str2, new float[]{1.0f, 0.0f})});
                    }
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            if (WebviewActivity.this.type == 1) {
                                WebviewActivity.this.progressItem.setVisibility(8);
                            } else {
                                WebviewActivity.this.progressView.setVisibility(4);
                            }
                        }
                    });
                    animatorSet.setDuration(150);
                    animatorSet.start();
                }
            }
        });
        frameLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2) {
            WebView webView = this.webView;
            if (webView != null) {
                webView.loadUrl(this.currentUrl);
            }
        }
    }

    public static boolean supportWebview() {
        return ("samsung".equals(Build.MANUFACTURER) && "GT-I9500".equals(Build.MODEL)) ? false : true;
    }

    private void reloadStats(String str) {
        if (!this.loadStats) {
            this.loadStats = true;
            TL_messages_getStatsURL tL_messages_getStatsURL = new TL_messages_getStatsURL();
            tL_messages_getStatsURL.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.currentDialogId);
            if (str == null) {
                str = "";
            }
            tL_messages_getStatsURL.params = str;
            tL_messages_getStatsURL.dark = Theme.getCurrentTheme().isDark();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getStatsURL, new -$$Lambda$WebviewActivity$O62eLYvTahA2PRe6UItNx5NI6ao(this));
        }
    }

    public /* synthetic */ void lambda$reloadStats$1$WebviewActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$WebviewActivity$sRTqxzi1H4zOl0Tg9DGFjQm9YEQ(this, tLObject));
    }

    public /* synthetic */ void lambda$null$0$WebviewActivity(TLObject tLObject) {
        this.loadStats = false;
        if (tLObject != null) {
            TL_statsURL tL_statsURL = (TL_statsURL) tLObject;
            WebView webView = this.webView;
            String str = tL_statsURL.url;
            this.currentUrl = str;
            webView.loadUrl(str);
        }
    }

    public static void openGameInBrowser(String str, MessageObject messageObject, Activity activity, String str2, String str3) {
        String str4 = "";
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str4);
            stringBuilder.append(messageObject.getId());
            String string = sharedPreferences.getString(stringBuilder.toString(), null);
            StringBuilder stringBuilder2 = new StringBuilder(string != null ? string : str4);
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("tgShareScoreUrl=");
            stringBuilder3.append(URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
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
            SerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder2);
            stringBuilder4.append("_m");
            edit.putString(stringBuilder4.toString(), Utilities.bytesToHex(serializedData.toByteArray()));
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder2);
            stringBuilder4.append("_link");
            String stringBuilder5 = stringBuilder4.toString();
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append("https://");
            stringBuilder4.append(MessagesController.getInstance(messageObject.currentAccount).linkPrefix);
            stringBuilder4.append("/");
            stringBuilder4.append(str3);
            if (!TextUtils.isEmpty(str2)) {
                StringBuilder stringBuilder6 = new StringBuilder();
                stringBuilder6.append("?game=");
                stringBuilder6.append(str2);
                str4 = stringBuilder6.toString();
            }
            stringBuilder4.append(str4);
            edit.putString(stringBuilder5, stringBuilder4.toString());
            edit.commit();
            Browser.openUrl((Context) activity, str, false);
            serializedData.cleanup();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr;
        if (this.type == 0) {
            themeDescriptionArr = new ThemeDescription[10];
            themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
            themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
            themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
            themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
            themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
            themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
            themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
            themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
            themeDescriptionArr[8] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2");
            themeDescriptionArr[9] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2");
            return themeDescriptionArr;
        }
        themeDescriptionArr = new ThemeDescription[11];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "player_actionBar");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "player_actionBarItems");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "player_actionBarTitle");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "player_actionBarTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "player_actionBarSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[9] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner4");
        themeDescriptionArr[10] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter4");
        return themeDescriptionArr;
    }
}
