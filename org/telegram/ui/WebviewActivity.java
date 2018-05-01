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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.ThemeDescription;
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
  public Runnable typingRunnable = new Runnable()
  {
    public void run()
    {
      if ((WebviewActivity.this.currentMessageObject == null) || (WebviewActivity.this.getParentActivity() == null) || (WebviewActivity.this.typingRunnable == null)) {}
      for (;;)
      {
        return;
        MessagesController.getInstance(WebviewActivity.this.currentAccount).sendTyping(WebviewActivity.this.currentMessageObject.getDialogId(), 6, 0);
        AndroidUtilities.runOnUIThread(WebviewActivity.this.typingRunnable, 25000L);
      }
    }
  };
  private WebView webView;
  
  public WebviewActivity(String paramString1, String paramString2, String paramString3, String paramString4, MessageObject paramMessageObject)
  {
    this.currentUrl = paramString1;
    this.currentBot = paramString2;
    this.currentGame = paramString3;
    this.currentMessageObject = paramMessageObject;
    this.short_param = paramString4;
    paramString2 = new StringBuilder().append("https://").append(MessagesController.getInstance(this.currentAccount).linkPrefix).append("/").append(this.currentBot);
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
        Object localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        localObject3 = ((SharedPreferences)localObject1).getString("" + paramMessageObject.getId(), null);
        StringBuilder localStringBuilder1 = new java/lang/StringBuilder;
        if (localObject3 != null)
        {
          localObject2 = localObject3;
          localStringBuilder1.<init>((String)localObject2);
          localObject2 = new java/lang/StringBuilder;
          StringBuilder localStringBuilder2 = new java/lang/StringBuilder;
          localStringBuilder2.<init>();
          ((StringBuilder)localObject2).<init>("tgShareScoreUrl=" + URLEncoder.encode("tgb://share_game_score?hash=", "UTF-8"));
          if (localObject3 == null)
          {
            localObject3 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            i = 0;
            if (i < 20)
            {
              localStringBuilder1.append(localObject3[Utilities.random.nextInt(localObject3.length)]);
              i++;
              continue;
            }
          }
        }
        else
        {
          localObject2 = "";
          continue;
        }
        ((StringBuilder)localObject2).append(localStringBuilder1);
        i = paramString1.indexOf('#');
        if (i < 0)
        {
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          paramString1 = paramString1 + "#" + localObject2;
          localObject2 = ((SharedPreferences)localObject1).edit();
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SharedPreferences.Editor)localObject2).putInt(localStringBuilder1 + "_date", (int)(System.currentTimeMillis() / 1000L));
          localObject1 = new org/telegram/tgnet/SerializedData;
          ((SerializedData)localObject1).<init>(paramMessageObject.messageOwner.getObjectSize());
          paramMessageObject.messageOwner.serializeToStream((AbstractSerializedData)localObject1);
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((SharedPreferences.Editor)localObject2).putString(localStringBuilder1 + "_m", Utilities.bytesToHex(((SerializedData)localObject1).toByteArray()));
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          localObject3 = localStringBuilder1 + "_link";
          localStringBuilder1 = new java/lang/StringBuilder;
          localStringBuilder1.<init>();
          paramString3 = localStringBuilder1.append("https://").append(MessagesController.getInstance(paramMessageObject.currentAccount).linkPrefix).append("/").append(paramString3);
          if (!TextUtils.isEmpty(paramString2)) {
            continue;
          }
          paramMessageObject = "";
          ((SharedPreferences.Editor)localObject2).putString((String)localObject3, paramMessageObject);
          ((SharedPreferences.Editor)localObject2).commit();
          Browser.openUrl(paramActivity, paramString1, false);
          return;
        }
      }
      catch (Exception paramString1)
      {
        Object localObject2;
        Object localObject3;
        int i;
        FileLog.e(paramString1);
        continue;
      }
      localObject3 = paramString1.substring(i + 1);
      if ((((String)localObject3).indexOf('=') >= 0) || (((String)localObject3).indexOf('?') >= 0))
      {
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        paramString1 = paramString1 + "&" + localObject2;
      }
      else if (((String)localObject3).length() > 0)
      {
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        paramString1 = paramString1 + "?" + localObject2;
      }
      else
      {
        localObject3 = new java/lang/StringBuilder;
        ((StringBuilder)localObject3).<init>();
        paramString1 = paramString1 + localObject2;
        continue;
        paramMessageObject = new java/lang/StringBuilder;
        paramMessageObject.<init>();
        paramMessageObject = "?game=" + paramString2;
      }
    }
  }
  
  public static boolean supportWebview()
  {
    String str1 = Build.MANUFACTURER;
    String str2 = Build.MODEL;
    if (("samsung".equals(str1)) && ("GT-I9500".equals(str2))) {}
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public View createView(Context paramContext)
  {
    this.swipeBackEnabled = false;
    this.actionBar.setBackButtonImage(NUM);
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
        for (;;)
        {
          return;
          if (paramAnonymousInt == 1)
          {
            WebviewActivity.this.currentMessageObject.messageOwner.with_my_score = false;
            WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
          }
          else if (paramAnonymousInt == 2)
          {
            WebviewActivity.openGameInBrowser(WebviewActivity.this.currentUrl, WebviewActivity.this.currentMessageObject, WebviewActivity.this.getParentActivity(), WebviewActivity.this.short_param, WebviewActivity.this.currentBot);
          }
        }
      }
    });
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    this.progressItem = localActionBarMenu.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    this.progressItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressItem.getImageView().setVisibility(4);
    localActionBarMenu.addItem(0, NUM).addSubItem(2, LocaleController.getString("OpenInExternalApp", NUM));
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
      private boolean isInternalUrl(String paramAnonymousString)
      {
        boolean bool = false;
        if (TextUtils.isEmpty(paramAnonymousString)) {}
        for (;;)
        {
          return bool;
          Object localObject = Uri.parse(paramAnonymousString);
          if (!"tg".equals(((Uri)localObject).getScheme())) {
            continue;
          }
          WebviewActivity.this.finishFragment(false);
          try
          {
            paramAnonymousString = new android/content/Intent;
            paramAnonymousString.<init>("android.intent.action.VIEW", (Uri)localObject);
            localObject = new android/content/ComponentName;
            ((ComponentName)localObject).<init>(ApplicationLoader.applicationContext.getPackageName(), LaunchActivity.class.getName());
            paramAnonymousString.setComponent((ComponentName)localObject);
            paramAnonymousString.putExtra("com.android.browser.application_id", ApplicationLoader.applicationContext.getPackageName());
            ApplicationLoader.applicationContext.startActivity(paramAnonymousString);
            bool = true;
          }
          catch (Exception paramAnonymousString)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousString);
            }
          }
        }
      }
      
      public void onLoadResource(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        if (isInternalUrl(paramAnonymousString)) {}
        for (;;)
        {
          return;
          super.onLoadResource(paramAnonymousWebView, paramAnonymousString);
        }
      }
      
      public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
        WebviewActivity.this.progressItem.getImageView().setVisibility(0);
        WebviewActivity.this.progressItem.setEnabled(true);
        paramAnonymousWebView = new AnimatorSet();
        paramAnonymousWebView.playTogether(new Animator[] { ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleX", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "scaleY", new float[] { 1.0F, 0.1F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressView, "alpha", new float[] { 1.0F, 0.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleX", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "scaleY", new float[] { 0.0F, 1.0F }), ObjectAnimator.ofFloat(WebviewActivity.this.progressItem.getImageView(), "alpha", new float[] { 0.0F, 1.0F }) });
        paramAnonymousWebView.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymous2Animator)
          {
            WebviewActivity.this.progressView.setVisibility(4);
          }
        });
        paramAnonymousWebView.setDuration(150L);
        paramAnonymousWebView.start();
      }
      
      public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString)
      {
        if ((isInternalUrl(paramAnonymousString)) || (super.shouldOverrideUrlLoading(paramAnonymousWebView, paramAnonymousString))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
    });
    paramContext.addView(this.webView, LayoutHelper.createFrame(-1, -1.0F));
    return this.fragmentView;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2") };
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
    this.typingRunnable = null;
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
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.cancelRunOnUIThread(this.typingRunnable);
    this.typingRunnable.run();
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
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d(paramString1);
          }
          String str = paramString1;
          int i = -1;
          switch (str.hashCode())
          {
          default: 
            label64:
            switch (i)
            {
            }
            break;
          }
          for (;;)
          {
            WebviewActivity.this.showDialog(ShareAlert.createShareAlert(WebviewActivity.this.getParentActivity(), WebviewActivity.this.currentMessageObject, null, false, WebviewActivity.this.linkToCopy, false));
            break;
            if (!str.equals("share_game")) {
              break label64;
            }
            i = 0;
            break label64;
            if (!str.equals("share_score")) {
              break label64;
            }
            i = 1;
            break label64;
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