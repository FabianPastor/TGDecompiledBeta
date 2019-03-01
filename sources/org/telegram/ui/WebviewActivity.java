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
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStatsURL;
import org.telegram.tgnet.TLRPC.TL_statsURL;
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
            if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null && WebviewActivity.this.typingRunnable != null) {
                MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
                AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000);
            }
        }
    };
    private WebView webView;

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        /* synthetic */ TelegramWebviewProxy(WebviewActivity x0, AnonymousClass1 x1) {
            this();
        }

        @JavascriptInterface
        public void postEvent(String eventName, String eventData) {
            AndroidUtilities.runOnUIThread(new WebviewActivity$TelegramWebviewProxy$$Lambda$0(this, eventName));
        }

        final /* synthetic */ void lambda$postEvent$0$WebviewActivity$TelegramWebviewProxy(String eventName) {
            if (WebviewActivity.this.getParentActivity() != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(eventName);
                }
                boolean z = true;
                switch (eventName.hashCode()) {
                    case -1788360622:
                        if (eventName.equals("share_game")) {
                            z = false;
                            break;
                        }
                        break;
                    case 406539826:
                        if (eventName.equals("share_score")) {
                            z = true;
                            break;
                        }
                        break;
                }
                switch (z) {
                    case false:
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                        break;
                    case true:
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                        break;
                }
                WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            }
        }
    }

    public WebviewActivity(String url, String botName, String gameName, String startParam, MessageObject messageObject) {
        this.currentUrl = url;
        this.currentBot = botName;
        this.currentGame = gameName;
        this.currentMessageObject = messageObject;
        this.short_param = startParam;
        this.linkToCopy = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentBot + (TextUtils.isEmpty(startParam) ? "" : "?game=" + startParam);
        this.type = 0;
    }

    public WebviewActivity(String statUrl, long did) {
        this.currentUrl = statUrl;
        this.currentDialogId = did;
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
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        this.swipeBackEnabled = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    WebviewActivity.this.lambda$checkDiscard$70$PassportActivity();
                } else if (id == 1) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                    WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
                } else if (id == 2) {
                    WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.progressItem = menu.addItemWithWidth(1, R.drawable.share, AndroidUtilities.dp(54.0f));
        if (this.type == 0) {
            menu.addItem(0, (int) R.drawable.ic_ab_other).addSubItem(2, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
            this.actionBar.setTitle(this.currentGame);
            this.actionBar.setSubtitle("@" + this.currentBot);
            this.progressView = new ContextProgressView(context, 1);
            this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(0.0f);
            this.progressView.setScaleX(0.1f);
            this.progressView.setScaleY(0.1f);
            this.progressView.setVisibility(4);
        } else if (this.type == 1) {
            this.actionBar.setBackgroundColor(Theme.getColor("player_actionBar"));
            this.actionBar.setItemsColor(Theme.getColor("player_actionBarItems"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("player_actionBarSelector"), false);
            this.actionBar.setTitleColor(Theme.getColor("player_actionBarTitle"));
            this.actionBar.setSubtitleColor(Theme.getColor("player_actionBarSubtitle"));
            this.actionBar.setTitle(LocaleController.getString("Statistics", R.string.Statistics));
            this.progressView = new ContextProgressView(context, 3);
            this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getImageView().setVisibility(8);
        }
        this.webView = new WebView(context);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
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
            private boolean isInternalUrl(String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                Uri uri = Uri.parse(url);
                if (!"tg".equals(uri.getScheme())) {
                    return false;
                }
                if (WebviewActivity.this.type == 1) {
                    try {
                        WebviewActivity.this.reloadStats(Uri.parse(url.replace("tg:statsrefresh", "tg://telegram.org")).getQueryParameter("params"));
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                } else {
                    WebviewActivity.this.finishFragment(false);
                    try {
                        Intent intent = new Intent("android.intent.action.VIEW", uri);
                        intent.setComponent(new ComponentName(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName()));
                        intent.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
                        ApplicationLoader.applicationContext.startActivity(intent);
                    } catch (Throwable e2) {
                        FileLog.e(e2);
                    }
                }
                return true;
            }

            public void onLoadResource(WebView view, String url) {
                if (!isInternalUrl(url)) {
                    super.onLoadResource(view, url);
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return isInternalUrl(url) || super.shouldOverrideUrlLoading(view, url);
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (WebviewActivity.this.progressView != null && WebviewActivity.this.progressView.getVisibility() == 0) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (WebviewActivity.this.type == 0) {
                        WebviewActivity.this.progressItem.getImageView().setVisibility(0);
                        WebviewActivity.this.progressItem.setEnabled(true);
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[]{0.0f, 1.0f})});
                    } else {
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f})});
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

    private void reloadStats(String params) {
        if (!this.loadStats) {
            this.loadStats = true;
            TL_messages_getStatsURL req = new TL_messages_getStatsURL();
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.currentDialogId);
            if (params == null) {
                params = "";
            }
            req.params = params;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new WebviewActivity$$Lambda$0(this));
        }
    }

    final /* synthetic */ void lambda$reloadStats$1$WebviewActivity(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new WebviewActivity$$Lambda$1(this, response));
    }

    final /* synthetic */ void lambda$null$0$WebviewActivity(TLObject response) {
        this.loadStats = false;
        if (response != null) {
            TL_statsURL url = (TL_statsURL) response;
            WebView webView = this.webView;
            String str = url.url;
            this.currentUrl = str;
            webView.loadUrl(str);
        }
    }

    public static void openGameInBrowser(String urlStr, MessageObject messageObject, Activity parentActivity, String short_name, String username) {
        String url = urlStr;
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String existing = sharedPreferences.getString("" + messageObject.getId(), null);
            StringBuilder hash = new StringBuilder(existing != null ? existing : "");
            StringBuilder addHash = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
            if (existing == null) {
                char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    hash.append(chars[Utilities.random.nextInt(chars.length)]);
                }
            }
            addHash.append(hash);
            int index = url.indexOf(35);
            if (index < 0) {
                url = url + "#" + addHash;
            } else {
                String curHash = url.substring(index + 1);
                if (curHash.indexOf(61) >= 0 || curHash.indexOf(63) >= 0) {
                    url = url + "&" + addHash;
                } else if (curHash.length() > 0) {
                    url = url + "?" + addHash;
                } else {
                    url = url + addHash;
                }
            }
            Editor editor = sharedPreferences.edit();
            editor.putInt(hash + "_date", (int) (System.currentTimeMillis() / 1000));
            SerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            editor.putString(hash + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            editor.putString(hash + "_link", "https://" + MessagesController.getInstance(messageObject.currentAccount).linkPrefix + "/" + username + (TextUtils.isEmpty(short_name) ? "" : "?game=" + short_name));
            editor.commit();
            Browser.openUrl((Context) parentActivity, url, false);
            serializedData.cleanup();
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr;
        if (this.type == 0) {
            themeDescriptionArr = new ThemeDescription[9];
            themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
            themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
            themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
            themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
            themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
            themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
            themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
            themeDescriptionArr[7] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2");
            themeDescriptionArr[8] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2");
            return themeDescriptionArr;
        }
        themeDescriptionArr = new ThemeDescription[10];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "player_actionBar");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "player_actionBarItems");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "player_actionBarTitle");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, "player_actionBarTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "player_actionBarSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[8] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner4");
        themeDescriptionArr[9] = new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter4");
        return themeDescriptionArr;
    }
}
