package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.net.URLEncoder;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ShareAlert;

public class WebviewActivity
  extends BaseFragment
{
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
  private WebView webView;
  
  public WebviewActivity(String paramString1, String paramString2, String paramString3, String paramString4, MessageObject paramMessageObject)
  {
    this.currentUrl = paramString1;
    this.currentBot = paramString2;
    this.currentGame = paramString3;
    this.currentMessageObject = paramMessageObject;
    this.short_param = paramString4;
    paramString2 = new StringBuilder().append("https://telegram.me/").append(this.currentBot);
    if (TextUtils.isEmpty(paramString4)) {}
    for (paramString1 = "";; paramString1 = "?game=" + paramString4)
    {
      this.linkToCopy = paramString1;
      return;
    }
  }
  
  public static void openGameInBrowser(String paramString1, MessageObject paramMessageObject, Activity paramActivity, String paramString2, String paramString3)
  {
    for (;;)
    {
      try
      {
        Object localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        Object localObject2 = ((SharedPreferences)localObject3).getString("" + paramMessageObject.getId(), null);
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          localObject1 = new StringBuilder((String)localObject1);
          StringBuilder localStringBuilder = new StringBuilder("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
          if (localObject2 == null)
          {
            localObject2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            i = 0;
            if (i < 20)
            {
              ((StringBuilder)localObject1).append(localObject2[Utilities.random.nextInt(localObject2.length)]);
              i += 1;
              continue;
            }
          }
          localStringBuilder.append((CharSequence)localObject1);
          int i = paramString1.indexOf('#');
          if (i < 0)
          {
            paramString1 = paramString1 + "#" + localStringBuilder;
            localObject2 = ((SharedPreferences)localObject3).edit();
            ((SharedPreferences.Editor)localObject2).putInt(localObject1 + "_date", (int)(System.currentTimeMillis() / 1000L));
            localObject3 = new SerializedData(paramMessageObject.messageOwner.getObjectSize());
            paramMessageObject.messageOwner.serializeToStream((AbstractSerializedData)localObject3);
            ((SharedPreferences.Editor)localObject2).putString(localObject1 + "_m", Utilities.bytesToHex(((SerializedData)localObject3).toByteArray()));
            localObject1 = localObject1 + "_link";
            paramString3 = new StringBuilder().append("https://telegram.me/").append(paramString3);
            if (TextUtils.isEmpty(paramString2))
            {
              paramMessageObject = "";
              ((SharedPreferences.Editor)localObject2).putString((String)localObject1, paramMessageObject);
              ((SharedPreferences.Editor)localObject2).commit();
              Browser.openUrl(paramActivity, paramString1, false);
            }
          }
          else
          {
            localObject2 = paramString1.substring(i + 1);
            if ((((String)localObject2).indexOf('=') >= 0) || (((String)localObject2).indexOf('?') >= 0))
            {
              paramString1 = paramString1 + "&" + localStringBuilder;
              continue;
            }
            if (((String)localObject2).length() > 0)
            {
              paramString1 = paramString1 + "?" + localStringBuilder;
              continue;
            }
            paramString1 = paramString1 + localStringBuilder;
            continue;
          }
          paramMessageObject = "?game=" + paramString2;
          continue;
        }
        Object localObject1 = "";
      }
      catch (Exception paramString1)
      {
        FileLog.e("tmessages", paramString1);
        return;
      }
    }
  }
  
  public static boolean supportWebview()
  {
    String str1 = Build.MANUFACTURER;
    String str2 = Build.MODEL;
    return (!"samsung".equals(str1)) || (!"GT-I9500".equals(str2));
  }
  
  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public View createView(Context paramContext)
  {
    this.swipeBackEnabled = false;
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(this.currentGame);
    this.actionBar.setSubtitle("@" + this.currentBot);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          WebviewActivity.this.finishFragment();
        }
        do
        {
          return;
          if (paramAnonymousInt == 1)
          {
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
            WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy));
            return;
          }
        } while (paramAnonymousInt != 2);
        WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
      }
    });
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    this.progressItem = localActionBarMenu.addItemWithWidth(1, 2130837979, AndroidUtilities.dp(54.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressItem.getImageView().setVisibility(4);
    localActionBarMenu.addItem(0, 2130837708).addSubItem(2, LocaleController.getString("OpenInExternalApp", 2131166058), 0);
    this.webView = new WebView(paramContext);
    this.webView.getSettings().setJavaScriptEnabled(true);
    this.webView.getSettings().setDomStorageEnabled(true);
    this.fragmentView = new FrameLayout(paramContext);
    paramContext = (FrameLayout)this.fragmentView;
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.webView.getSettings().setMixedContentMode(0);
      CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
      this.webView.addJavascriptInterface(new TelegramWebviewProxy(null), "TelegramWebviewProxy");
    }
    this.webView.setWebViewClient(new WebViewClient()
    {
      public void onLoadResource(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        super.onLoadResource(paramAnonymousWebView, paramAnonymousString);
      }
      
      public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
        WebviewActivity.this.progressItem.getImageView().setVisibility(0);
        WebviewActivity.this.progressItem.setEnabled(true);
        paramAnonymousWebView = new AnimatorSet();
        paramAnonymousWebView.playTogether(new Animator[] { ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[] { 0.0F, 1.0F }) });
        paramAnonymousWebView.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            WebviewActivity.this.progressView.setVisibility(4);
          }
        });
        paramAnonymousWebView.setDuration(150L);
        paramAnonymousWebView.start();
      }
    });
    paramContext.addView(this.webView, LayoutHelper.createFrame(-1, -1.0F));
    return this.fragmentView;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    try
    {
      ViewParent localViewParent = this.webView.getParent();
      if (localViewParent != null) {
        ((FrameLayout)localViewParent).removeView(this.webView);
      }
      this.webView.stopLoading();
      this.webView.loadUrl("about:blank");
      this.webView.destroy();
      this.webView = null;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2) && (this.webView != null)) {
      this.webView.loadUrl(this.currentUrl);
    }
  }
  
  private class TelegramWebviewProxy
  {
    private TelegramWebviewProxy() {}
    
    @JavascriptInterface
    public void postEvent(final String paramString1, String paramString2)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (WebviewActivity.this.getParentActivity() == null) {
            return;
          }
          FileLog.e("tmessages", paramString1);
          String str = paramString1;
          int i = -1;
          switch (str.hashCode())
          {
          default: 
            switch (i)
            {
            }
            break;
          }
          for (;;)
          {
            WebviewActivity.this.showDialog(new ShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy));
            return;
            if (!str.equals("share_game")) {
              break;
            }
            i = 0;
            break;
            if (!str.equals("share_score")) {
              break;
            }
            i = 1;
            break;
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
            continue;
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = true;
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/WebviewActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */