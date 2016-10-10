package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.Theme;

public class WebFrameLayout
  extends FrameLayout
{
  static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})");
  private View customView;
  private WebChromeClient.CustomViewCallback customViewCallback;
  private BottomSheet dialog;
  private String embedUrl;
  private FrameLayout fullscreenVideoContainer;
  private boolean hasDescription;
  private int height;
  private String openUrl;
  private ProgressBar progressBar;
  private WebView webView;
  private int width;
  private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
  
  @SuppressLint({"SetJavaScriptEnabled"})
  public WebFrameLayout(Context paramContext, BottomSheet paramBottomSheet, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    super(paramContext);
    this.embedUrl = paramString4;
    boolean bool;
    if ((paramString2 != null) && (paramString2.length() > 0))
    {
      bool = true;
      this.hasDescription = bool;
      this.openUrl = paramString3;
      this.width = paramInt1;
      this.height = paramInt2;
      if ((this.width == 0) || (this.height == 0))
      {
        this.width = AndroidUtilities.displaySize.x;
        this.height = (AndroidUtilities.displaySize.y / 2);
      }
      this.dialog = paramBottomSheet;
      this.fullscreenVideoContainer = new FrameLayout(paramContext);
      this.fullscreenVideoContainer.setBackgroundColor(-16777216);
      if (Build.VERSION.SDK_INT >= 21) {
        this.fullscreenVideoContainer.setFitsSystemWindows(true);
      }
      paramBottomSheet.setApplyTopPadding(false);
      paramBottomSheet.setApplyBottomPadding(false);
      this.dialog.getContainer().addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0F));
      this.fullscreenVideoContainer.setVisibility(4);
      this.webView = new WebView(paramContext);
      this.webView.getSettings().setJavaScriptEnabled(true);
      this.webView.getSettings().setDomStorageEnabled(true);
      if (Build.VERSION.SDK_INT >= 17) {
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
      }
      paramString3 = this.webView.getSettings().getUserAgentString();
      if (paramString3 != null)
      {
        paramString3 = paramString3.replace("Android", "");
        this.webView.getSettings().setUserAgentString(paramString3);
      }
      if (Build.VERSION.SDK_INT >= 21)
      {
        this.webView.getSettings().setMixedContentMode(0);
        CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
      }
      this.webView.setWebChromeClient(new WebChromeClient()
      {
        public void onHideCustomView()
        {
          super.onHideCustomView();
          if (WebFrameLayout.this.customView == null) {
            return;
          }
          if (WebFrameLayout.this.dialog != null)
          {
            WebFrameLayout.this.dialog.getSheetContainer().setVisibility(0);
            WebFrameLayout.this.fullscreenVideoContainer.setVisibility(4);
            WebFrameLayout.this.fullscreenVideoContainer.removeView(WebFrameLayout.this.customView);
          }
          if ((WebFrameLayout.this.customViewCallback != null) && (!WebFrameLayout.this.customViewCallback.getClass().getName().contains(".chromium."))) {
            WebFrameLayout.this.customViewCallback.onCustomViewHidden();
          }
          WebFrameLayout.access$002(WebFrameLayout.this, null);
        }
        
        public void onShowCustomView(View paramAnonymousView, int paramAnonymousInt, WebChromeClient.CustomViewCallback paramAnonymousCustomViewCallback)
        {
          onShowCustomView(paramAnonymousView, paramAnonymousCustomViewCallback);
        }
        
        public void onShowCustomView(View paramAnonymousView, WebChromeClient.CustomViewCallback paramAnonymousCustomViewCallback)
        {
          if (WebFrameLayout.this.customView != null)
          {
            paramAnonymousCustomViewCallback.onCustomViewHidden();
            return;
          }
          WebFrameLayout.access$002(WebFrameLayout.this, paramAnonymousView);
          if (WebFrameLayout.this.dialog != null)
          {
            WebFrameLayout.this.dialog.getSheetContainer().setVisibility(4);
            WebFrameLayout.this.fullscreenVideoContainer.setVisibility(0);
            WebFrameLayout.this.fullscreenVideoContainer.addView(paramAnonymousView, LayoutHelper.createFrame(-1, -1.0F));
          }
          WebFrameLayout.access$302(WebFrameLayout.this, paramAnonymousCustomViewCallback);
        }
      });
      this.webView.setWebViewClient(new WebViewClient()
      {
        public void onLoadResource(WebView paramAnonymousWebView, String paramAnonymousString)
        {
          super.onLoadResource(paramAnonymousWebView, paramAnonymousString);
        }
        
        public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString)
        {
          super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
          WebFrameLayout.this.progressBar.setVisibility(4);
        }
      });
      paramString3 = this.webView;
      if (!this.hasDescription) {
        break label1074;
      }
      paramInt1 = 22;
      label343:
      addView(paramString3, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84));
      this.progressBar = new ProgressBar(paramContext);
      paramString3 = this.progressBar;
      if (!this.hasDescription) {
        break label1080;
      }
    }
    label1074:
    label1080:
    for (paramInt1 = 22;; paramInt1 = 0)
    {
      addView(paramString3, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 0.0F, 0.0F, (paramInt1 + 84) / 2));
      if (this.hasDescription)
      {
        paramString3 = new TextView(paramContext);
        paramString3.setTextSize(1, 16.0F);
        paramString3.setTextColor(-14540254);
        paramString3.setText(paramString2);
        paramString3.setSingleLine(true);
        paramString3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramString3.setEllipsize(TextUtils.TruncateAt.END);
        paramString3.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
        addView(paramString3, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 77.0F));
      }
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(-7697782);
      paramString2.setText(paramString1);
      paramString2.setSingleLine(true);
      paramString2.setEllipsize(TextUtils.TruncateAt.END);
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      addView(paramString2, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 57.0F));
      paramString1 = new View(paramContext);
      paramString1.setBackgroundColor(-2368549);
      addView(paramString1, new FrameLayout.LayoutParams(-1, 1, 83));
      ((FrameLayout.LayoutParams)paramString1.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0F);
      paramString2 = new FrameLayout(paramContext);
      paramString2.setBackgroundColor(-1);
      addView(paramString2, LayoutHelper.createFrame(-1, 48, 83));
      paramString1 = new TextView(paramContext);
      paramString1.setTextSize(1, 14.0F);
      paramString1.setTextColor(-15095832);
      paramString1.setGravity(17);
      paramString1.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
      paramString1.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString1.setText(LocaleController.getString("Close", 2131165515).toUpperCase());
      paramString1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString2.addView(paramString1, LayoutHelper.createFrame(-2, -1, 51));
      paramString1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (WebFrameLayout.this.dialog != null) {
            WebFrameLayout.this.dialog.dismiss();
          }
        }
      });
      paramString1 = new LinearLayout(paramContext);
      paramString1.setOrientation(0);
      paramString2.addView(paramString1, LayoutHelper.createFrame(-2, -1, 53));
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(-15095832);
      paramString2.setGravity(17);
      paramString2.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString2.setText(LocaleController.getString("Copy", 2131165531).toUpperCase());
      paramString2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString1.addView(paramString2, LayoutHelper.createFrame(-2, -1, 51));
      paramString2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", WebFrameLayout.this.openUrl));
            Toast.makeText(WebFrameLayout.this.getContext(), LocaleController.getString("LinkCopied", 2131165823), 0).show();
            if (WebFrameLayout.this.dialog != null) {
              WebFrameLayout.this.dialog.dismiss();
            }
            return;
          }
          catch (Exception paramAnonymousView)
          {
            for (;;)
            {
              FileLog.e("tmessages", paramAnonymousView);
            }
          }
        }
      });
      paramContext = new TextView(paramContext);
      paramContext.setTextSize(1, 14.0F);
      paramContext.setTextColor(-15095832);
      paramContext.setGravity(17);
      paramContext.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
      paramContext.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramContext.setText(LocaleController.getString("OpenInBrowser", 2131166057).toUpperCase());
      paramContext.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString1.addView(paramContext, LayoutHelper.createFrame(-2, -1, 51));
      paramContext.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Browser.openUrl(WebFrameLayout.this.getContext(), WebFrameLayout.this.openUrl);
          if (WebFrameLayout.this.dialog != null) {
            WebFrameLayout.this.dialog.dismiss();
          }
        }
      });
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      paramBottomSheet.setDelegate(new BottomSheet.BottomSheetDelegate()
      {
        public void onOpenAnimationEnd()
        {
          HashMap localHashMap = new HashMap();
          localHashMap.put("Referer", "http://youtube.com");
          int m = 0;
          int k = 0;
          int i = m;
          try
          {
            String str = Uri.parse(WebFrameLayout.this.openUrl).getHost().toLowerCase();
            if (str != null)
            {
              i = m;
              if (str.endsWith("youtube.com")) {}
            }
            else
            {
              j = k;
              i = m;
              if (!str.endsWith("youtu.be")) {
                break label164;
              }
            }
            i = m;
            Matcher localMatcher = WebFrameLayout.youtubeIdRegex.matcher(WebFrameLayout.this.openUrl);
            str = null;
            i = m;
            if (localMatcher.find())
            {
              i = m;
              str = localMatcher.group(1);
            }
            j = k;
            if (str != null)
            {
              i = 1;
              j = 1;
              WebFrameLayout.this.webView.loadDataWithBaseURL("http://youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,    .embed-container embed {        position: absolute;        top: 0;        left: 0;        width: 100%% !important;        height: 100%% !important;    }    </style></head><body>    <div class=\"embed-container\">        <div id=\"player\"></div>    </div>    <script src=\"https://www.youtube.com/iframe_api\"></script>    <script>    var player;    YT.ready(function() {         player = new YT.Player(\"player\", {                                \"width\" : \"100%%\",                                \"events\" : {                                \"onReady\" : \"onReady\",                                },                                \"videoId\" : \"%1$s\",                                \"height\" : \"100%%\",                                \"playerVars\" : {                                \"start\" : 0,                                \"rel\" : 0,                                \"showinfo\" : 0,                                \"modestbranding\" : 1,                                \"iv_load_policy\" : 3,                                \"autohide\" : 1,                                \"cc_load_policy\" : 1,                                \"playsinline\" : 1,                                \"controls\" : 1                                }                                });        player.setSize(window.innerWidth, window.innerHeight);    });    function onReady(event) {        player.playVideo();    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[] { str }), "text/html", "UTF-8", "http://youtube.com");
            }
          }
          catch (Exception localException1)
          {
            for (;;)
            {
              try
              {
                label164:
                WebFrameLayout.this.webView.loadUrl(WebFrameLayout.this.embedUrl, localHashMap);
                return;
              }
              catch (Exception localException2)
              {
                int j;
                FileLog.e("tmessages", localException2);
              }
              localException1 = localException1;
              FileLog.e("tmessages", localException1);
              j = i;
            }
          }
          if (j == 0) {}
        }
      });
      return;
      bool = false;
      break;
      paramInt1 = 0;
      break label343;
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    try
    {
      removeView(this.webView);
      this.webView.stopLoading();
      this.webView.loadUrl("about:blank");
      this.webView.destroy();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    float f = this.width / paramInt2;
    int i = (int)Math.min(this.height / f, AndroidUtilities.displaySize.y / 2);
    if (this.hasDescription) {}
    for (paramInt2 = 22;; paramInt2 = 0)
    {
      super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt2 + 84) + i + 1, 1073741824));
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/WebFrameLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */