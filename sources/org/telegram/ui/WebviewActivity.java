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
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getStatsURL;
import org.telegram.tgnet.TLRPC$TL_statsURL;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.WebviewActivity;

public class WebviewActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public String currentBot;
    private long currentDialogId;
    private String currentGame;
    /* access modifiers changed from: private */
    public MessageObject currentMessageObject;
    /* access modifiers changed from: private */
    public String currentUrl;
    /* access modifiers changed from: private */
    public String linkToCopy;
    private boolean loadStats;
    /* access modifiers changed from: private */
    public ActionBarMenuItem progressItem;
    /* access modifiers changed from: private */
    public ContextProgressView progressView;
    /* access modifiers changed from: private */
    public String short_param;
    /* access modifiers changed from: private */
    public int type;
    public Runnable typingRunnable = new Runnable() {
        public void run() {
            if (WebviewActivity.this.currentMessageObject != null && WebviewActivity.this.getParentActivity() != null) {
                WebviewActivity webviewActivity = WebviewActivity.this;
                if (webviewActivity.typingRunnable != null) {
                    MessagesController.getInstance(webviewActivity.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 0, 6, 0);
                    AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000);
                }
            }
        }
    };
    private WebView webView;

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    private class TelegramWebviewProxy {
        private TelegramWebviewProxy() {
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            AndroidUtilities.runOnUIThread(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WebviewActivity.TelegramWebviewProxy.this.lambda$postEvent$0$WebviewActivity$TelegramWebviewProxy(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$postEvent$0$WebviewActivity$TelegramWebviewProxy(String str) {
            if (WebviewActivity.this.getParentActivity() != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(str);
                }
                char c = 65535;
                int hashCode = str.hashCode();
                if (hashCode != -NUM) {
                    if (hashCode == NUM && str.equals("share_score")) {
                        c = 1;
                    }
                } else if (str.equals("share_game")) {
                    c = 0;
                }
                if (c == 0) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                } else if (c == 1) {
                    WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
                }
                WebviewActivity webviewActivity = WebviewActivity.this;
                webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, (String) null, false, WebviewActivity.this.linkToCopy, false));
            }
        }
    }

    public WebviewActivity(String str, String str2, String str3, String str4, MessageObject messageObject) {
        String str5;
        this.currentUrl = str;
        this.currentBot = str2;
        this.currentGame = str3;
        this.currentMessageObject = messageObject;
        this.short_param = str4;
        StringBuilder sb = new StringBuilder();
        sb.append("https://");
        sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        sb.append("/");
        sb.append(this.currentBot);
        if (TextUtils.isEmpty(str4)) {
            str5 = "";
        } else {
            str5 = "?game=" + str4;
        }
        sb.append(str5);
        this.linkToCopy = sb.toString();
        this.type = 0;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.webView.setLayerType(0, (Paint) null);
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
            FileLog.e((Throwable) e);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WebviewActivity.this.finishFragment();
                } else if (i == 1) {
                    if (WebviewActivity.this.currentMessageObject != null) {
                        WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
                        WebviewActivity webviewActivity = WebviewActivity.this;
                        webviewActivity.showDialog(ShareAlert.createShareAlert(webviewActivity.getParentActivity(), WebviewActivity.this.currentMessageObject, (String) null, false, WebviewActivity.this.linkToCopy, false));
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
            actionBar.setSubtitle("@" + this.currentBot);
            ContextProgressView contextProgressView = new ContextProgressView(context, 1);
            this.progressView = contextProgressView;
            this.progressItem.addView(contextProgressView, LayoutHelper.createFrame(-1, -1.0f));
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
            ContextProgressView contextProgressView2 = new ContextProgressView(context, 3);
            this.progressView = contextProgressView2;
            this.progressItem.addView(contextProgressView2, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView.setAlpha(1.0f);
            this.progressView.setScaleX(1.0f);
            this.progressView.setScaleY(1.0f);
            this.progressView.setVisibility(0);
            this.progressItem.getContentView().setVisibility(8);
            this.progressItem.setEnabled(false);
        }
        WebView webView2 = new WebView(context);
        this.webView = webView2;
        webView2.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        if (Build.VERSION.SDK_INT >= 19) {
            this.webView.setLayerType(2, (Paint) null);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
            if (this.type == 0) {
                this.webView.addJavascriptInterface(new TelegramWebviewProxy(), "TelegramWebviewProxy");
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
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
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
                    if (WebviewActivity.this.type == 0) {
                        WebviewActivity.this.progressItem.getContentView().setVisibility(0);
                        WebviewActivity.this.progressItem.setEnabled(true);
                        animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[]{1.0f, 0.1f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[]{1.0f, 0.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleX", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "scaleY", new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getContentView(), "alpha", new float[]{0.0f, 1.0f})});
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
        frameLayout2.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f));
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
        this.typingRunnable.run();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        WebView webView2;
        if (z && !z2 && (webView2 = this.webView) != null) {
            webView2.loadUrl(this.currentUrl);
        }
    }

    public static boolean supportWebview() {
        return !"samsung".equals(Build.MANUFACTURER) || !"GT-I9500".equals(Build.MODEL);
    }

    /* access modifiers changed from: private */
    public void reloadStats(String str) {
        if (!this.loadStats) {
            this.loadStats = true;
            TLRPC$TL_messages_getStatsURL tLRPC$TL_messages_getStatsURL = new TLRPC$TL_messages_getStatsURL();
            tLRPC$TL_messages_getStatsURL.peer = MessagesController.getInstance(this.currentAccount).getInputPeer((int) this.currentDialogId);
            if (str == null) {
                str = "";
            }
            tLRPC$TL_messages_getStatsURL.params = str;
            tLRPC$TL_messages_getStatsURL.dark = Theme.getCurrentTheme().isDark();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getStatsURL, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    WebviewActivity.this.lambda$reloadStats$1$WebviewActivity(tLObject, tLRPC$TL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$reloadStats$1$WebviewActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            public final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WebviewActivity.this.lambda$null$0$WebviewActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$0$WebviewActivity(TLObject tLObject) {
        this.loadStats = false;
        if (tLObject != null) {
            WebView webView2 = this.webView;
            String str = ((TLRPC$TL_statsURL) tLObject).url;
            this.currentUrl = str;
            webView2.loadUrl(str);
        }
    }

    public static void openGameInBrowser(String str, MessageObject messageObject, Activity activity, String str2, String str3) {
        String str4;
        String str5 = "";
        try {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String string = sharedPreferences.getString(str5 + messageObject.getId(), (String) null);
            StringBuilder sb = new StringBuilder(string != null ? string : str5);
            StringBuilder sb2 = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
            if (string == null) {
                char[] charArray = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
                for (int i = 0; i < 20; i++) {
                    sb.append(charArray[Utilities.random.nextInt(charArray.length)]);
                }
            }
            sb2.append(sb);
            int indexOf = str.indexOf(35);
            if (indexOf < 0) {
                str4 = str + "#" + sb2;
            } else {
                String substring = str.substring(indexOf + 1);
                if (substring.indexOf(61) < 0) {
                    if (substring.indexOf(63) < 0) {
                        if (substring.length() > 0) {
                            str4 = str + "?" + sb2;
                        } else {
                            str4 = str + sb2;
                        }
                    }
                }
                str4 = str + "&" + sb2;
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt(sb + "_date", (int) (System.currentTimeMillis() / 1000));
            SerializedData serializedData = new SerializedData(messageObject.messageOwner.getObjectSize());
            messageObject.messageOwner.serializeToStream(serializedData);
            edit.putString(sb + "_m", Utilities.bytesToHex(serializedData.toByteArray()));
            String str6 = sb + "_link";
            StringBuilder sb3 = new StringBuilder();
            sb3.append("https://");
            sb3.append(MessagesController.getInstance(messageObject.currentAccount).linkPrefix);
            sb3.append("/");
            sb3.append(str3);
            if (!TextUtils.isEmpty(str2)) {
                str5 = "?game=" + str2;
            }
            sb3.append(str5);
            edit.putString(str6, sb3.toString());
            edit.commit();
            Browser.openUrl((Context) activity, str4, false);
            serializedData.cleanup();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        if (this.type == 0) {
            arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
            arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner2"));
            arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter2"));
        } else {
            arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBar"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarItems"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSelector"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
            arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressInner4"));
            arrayList.add(new ThemeDescription(this.progressView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "contextProgressOuter4"));
        }
        return arrayList;
    }
}
