package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.view.Display;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate;
import org.telegram.ui.ActionBar.BottomSheet.ContainerView;
import org.telegram.ui.ActionBar.Theme;

public class EmbedBottomSheet
  extends BottomSheet
{
  @SuppressLint({"StaticFieldLeak"})
  private static EmbedBottomSheet instance;
  private boolean animationInProgress;
  private FrameLayout containerLayout;
  private TextView copyTextButton;
  private View customView;
  private WebChromeClient.CustomViewCallback customViewCallback;
  private String embedUrl;
  private FrameLayout fullscreenVideoContainer;
  private boolean fullscreenedByButton;
  private boolean hasDescription;
  private int height;
  private LinearLayout imageButtonsContainer;
  private boolean isYouTube;
  private int lastOrientation = -1;
  private DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener()
  {
    public void onShow(DialogInterface paramAnonymousDialogInterface)
    {
      if ((EmbedBottomSheet.this.pipVideoView != null) && (EmbedBottomSheet.this.videoView.isInline())) {
        EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
            return true;
          }
        });
      }
    }
  };
  private TextView openInButton;
  private String openUrl;
  private OrientationEventListener orientationEventListener;
  private Activity parentActivity;
  private ImageView pipButton;
  private PipVideoView pipVideoView;
  private int[] position = new int[2];
  private int prevOrientation = -2;
  private RadialProgressView progressBar;
  private View progressBarBlackBackground;
  private WebPlayerView videoView;
  private int waitingForDraw;
  private boolean wasInLandscape;
  private WebView webView;
  private int width;
  private final String youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>";
  private ImageView youtubeLogoImage;
  
  @SuppressLint({"SetJavaScriptEnabled"})
  private EmbedBottomSheet(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    super(paramContext, false);
    this.fullWidth = true;
    setApplyTopPadding(false);
    setApplyBottomPadding(false);
    if ((paramContext instanceof Activity)) {
      this.parentActivity = ((Activity)paramContext);
    }
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
      this.fullscreenVideoContainer = new FrameLayout(paramContext);
      this.fullscreenVideoContainer.setBackgroundColor(-16777216);
      if (Build.VERSION.SDK_INT >= 21) {
        this.fullscreenVideoContainer.setFitsSystemWindows(true);
      }
      this.fullscreenVideoContainer.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0F));
      this.fullscreenVideoContainer.setVisibility(4);
      this.fullscreenVideoContainer.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      this.containerLayout = new FrameLayout(paramContext)
      {
        protected void onDetachedFromWindow()
        {
          super.onDetachedFromWindow();
          try
          {
            if (((EmbedBottomSheet.this.pipVideoView == null) || (EmbedBottomSheet.this.webView.getVisibility() != 0)) && (EmbedBottomSheet.this.webView.getParent() != null))
            {
              removeView(EmbedBottomSheet.this.webView);
              EmbedBottomSheet.this.webView.stopLoading();
              EmbedBottomSheet.this.webView.loadUrl("about:blank");
              EmbedBottomSheet.this.webView.destroy();
            }
            if ((!EmbedBottomSheet.this.videoView.isInline()) && (EmbedBottomSheet.this.pipVideoView == null))
            {
              if (EmbedBottomSheet.instance == EmbedBottomSheet.this) {
                EmbedBottomSheet.access$702(null);
              }
              EmbedBottomSheet.this.videoView.destroy();
            }
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
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAnonymousInt2 = View.MeasureSpec.getSize(paramAnonymousInt1);
          float f = EmbedBottomSheet.this.width / paramAnonymousInt2;
          int i = (int)Math.min(EmbedBottomSheet.this.height / f, AndroidUtilities.displaySize.y / 2);
          if (EmbedBottomSheet.this.hasDescription) {}
          for (paramAnonymousInt2 = 22;; paramAnonymousInt2 = 0)
          {
            super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramAnonymousInt2 + 84) + i + 1, NUM));
            return;
          }
        }
      };
      this.containerLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      setCustomView(this.containerLayout);
      this.webView = new WebView(paramContext)
      {
        public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
        {
          if ((EmbedBottomSheet.this.isYouTube) && (paramAnonymousMotionEvent.getAction() == 0)) {
            EmbedBottomSheet.this.showOrHideYoutubeLogo(true);
          }
          return super.onTouchEvent(paramAnonymousMotionEvent);
        }
      };
      this.webView.getSettings().setJavaScriptEnabled(true);
      this.webView.getSettings().setDomStorageEnabled(true);
      if (Build.VERSION.SDK_INT >= 17) {
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
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
          if (EmbedBottomSheet.this.customView == null) {}
          for (;;)
          {
            return;
            EmbedBottomSheet.this.getSheetContainer().setVisibility(0);
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
            EmbedBottomSheet.this.fullscreenVideoContainer.removeView(EmbedBottomSheet.this.customView);
            if ((EmbedBottomSheet.this.customViewCallback != null) && (!EmbedBottomSheet.this.customViewCallback.getClass().getName().contains(".chromium."))) {
              EmbedBottomSheet.this.customViewCallback.onCustomViewHidden();
            }
            EmbedBottomSheet.access$1202(EmbedBottomSheet.this, null);
          }
        }
        
        public void onShowCustomView(View paramAnonymousView, int paramAnonymousInt, WebChromeClient.CustomViewCallback paramAnonymousCustomViewCallback)
        {
          onShowCustomView(paramAnonymousView, paramAnonymousCustomViewCallback);
        }
        
        public void onShowCustomView(View paramAnonymousView, WebChromeClient.CustomViewCallback paramAnonymousCustomViewCallback)
        {
          if ((EmbedBottomSheet.this.customView != null) || (EmbedBottomSheet.this.pipVideoView != null)) {
            paramAnonymousCustomViewCallback.onCustomViewHidden();
          }
          for (;;)
          {
            return;
            EmbedBottomSheet.this.exitFromPip();
            EmbedBottomSheet.access$1202(EmbedBottomSheet.this, paramAnonymousView);
            EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
            EmbedBottomSheet.this.fullscreenVideoContainer.addView(paramAnonymousView, LayoutHelper.createFrame(-1, -1.0F));
            EmbedBottomSheet.access$1402(EmbedBottomSheet.this, paramAnonymousCustomViewCallback);
          }
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
          if ((!EmbedBottomSheet.this.isYouTube) || (Build.VERSION.SDK_INT < 17))
          {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0F);
          }
        }
      });
      paramString3 = this.containerLayout;
      paramString4 = this.webView;
      if (!this.hasDescription) {
        break label1823;
      }
      paramInt1 = 22;
      label417:
      paramString3.addView(paramString4, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84));
      this.youtubeLogoImage = new ImageView(paramContext);
      this.youtubeLogoImage.setVisibility(8);
      this.containerLayout.addView(this.youtubeLogoImage, LayoutHelper.createFrame(66, 28.0F, 53, 0.0F, 8.0F, 8.0F, 0.0F));
      this.youtubeLogoImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (EmbedBottomSheet.this.youtubeLogoImage.getAlpha() == 0.0F) {}
          for (;;)
          {
            return;
            EmbedBottomSheet.this.openInButton.callOnClick();
          }
        }
      });
      this.videoView = new WebPlayerView(paramContext, true, false, new WebPlayerView.WebPlayerViewDelegate()
      {
        public boolean checkInlinePermissions()
        {
          return EmbedBottomSheet.this.checkInlinePermissions();
        }
        
        public ViewGroup getTextureViewContainer()
        {
          return EmbedBottomSheet.this.container;
        }
        
        public void onInitFailed()
        {
          EmbedBottomSheet.this.webView.setVisibility(0);
          EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
          EmbedBottomSheet.this.copyTextButton.setVisibility(4);
          EmbedBottomSheet.this.webView.setKeepScreenOn(true);
          EmbedBottomSheet.this.videoView.setVisibility(4);
          EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
          EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
          if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
            EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
          }
          EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
          HashMap localHashMap = new HashMap();
          localHashMap.put("Referer", "http://youtube.com");
          try
          {
            EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, localHashMap);
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
        
        public void onInlineSurfaceTextureReady()
        {
          if (EmbedBottomSheet.this.videoView.isInline()) {
            EmbedBottomSheet.this.dismissInternal();
          }
        }
        
        public void onPlayStateChanged(WebPlayerView paramAnonymousWebPlayerView, boolean paramAnonymousBoolean)
        {
          if (paramAnonymousBoolean) {}
          for (;;)
          {
            try
            {
              EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
              return;
            }
            catch (Exception paramAnonymousWebPlayerView)
            {
              FileLog.e(paramAnonymousWebPlayerView);
              continue;
            }
            try
            {
              EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
            }
            catch (Exception paramAnonymousWebPlayerView)
            {
              FileLog.e(paramAnonymousWebPlayerView);
            }
          }
        }
        
        public void onSharePressed() {}
        
        public TextureView onSwitchInlineMode(View paramAnonymousView, boolean paramAnonymousBoolean1, float paramAnonymousFloat, int paramAnonymousInt, boolean paramAnonymousBoolean2)
        {
          if (paramAnonymousBoolean1)
          {
            paramAnonymousView.setTranslationY(0.0F);
            EmbedBottomSheet.access$402(EmbedBottomSheet.this, new PipVideoView());
            paramAnonymousView = EmbedBottomSheet.this.pipVideoView.show(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this, paramAnonymousView, paramAnonymousFloat, paramAnonymousInt, null);
            return paramAnonymousView;
          }
          if (paramAnonymousBoolean2)
          {
            EmbedBottomSheet.access$3802(EmbedBottomSheet.this, true);
            EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
            paramAnonymousView = EmbedBottomSheet.this.position;
            paramAnonymousView[0] -= EmbedBottomSheet.this.getLeftInset();
            paramAnonymousView = EmbedBottomSheet.this.position;
            paramAnonymousView[1] = ((int)(paramAnonymousView[1] - EmbedBottomSheet.this.containerView.getTranslationY()));
            TextureView localTextureView = EmbedBottomSheet.this.videoView.getTextureView();
            ImageView localImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
            paramAnonymousView = new AnimatorSet();
            paramAnonymousView.playTogether(new Animator[] { ObjectAnimator.ofFloat(localImageView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(localImageView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(localImageView, "translationX", new float[] { EmbedBottomSheet.this.position[0] }), ObjectAnimator.ofFloat(localImageView, "translationY", new float[] { EmbedBottomSheet.this.position[1] }), ObjectAnimator.ofFloat(localTextureView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(localTextureView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(localTextureView, "translationX", new float[] { EmbedBottomSheet.this.position[0] }), ObjectAnimator.ofFloat(localTextureView, "translationY", new float[] { EmbedBottomSheet.this.position[1] }), ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[] { 51 }) });
            paramAnonymousView.setInterpolator(new DecelerateInterpolator());
            paramAnonymousView.setDuration(250L);
            paramAnonymousView.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                EmbedBottomSheet.access$3802(EmbedBottomSheet.this, false);
              }
            });
            paramAnonymousView.start();
          }
          for (;;)
          {
            paramAnonymousView = null;
            break;
            EmbedBottomSheet.this.containerView.setTranslationY(0.0F);
          }
        }
        
        public TextureView onSwitchToFullscreen(View paramAnonymousView, boolean paramAnonymousBoolean1, float paramAnonymousFloat, int paramAnonymousInt, boolean paramAnonymousBoolean2)
        {
          if (paramAnonymousBoolean1)
          {
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
            EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
            EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
            EmbedBottomSheet.access$2002(EmbedBottomSheet.this, false);
            EmbedBottomSheet.access$2102(EmbedBottomSheet.this, paramAnonymousBoolean2);
            if (EmbedBottomSheet.this.parentActivity == null) {}
          }
          for (;;)
          {
            try
            {
              EmbedBottomSheet.access$2302(EmbedBottomSheet.this, EmbedBottomSheet.this.parentActivity.getRequestedOrientation());
              if (paramAnonymousBoolean2)
              {
                if (((WindowManager)EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() != 3) {
                  continue;
                }
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
              }
              EmbedBottomSheet.this.containerView.setSystemUiVisibility(1028);
            }
            catch (Exception paramAnonymousView)
            {
              FileLog.e(paramAnonymousView);
              continue;
            }
            return null;
            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
            continue;
            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
            EmbedBottomSheet.access$2102(EmbedBottomSheet.this, false);
            if (EmbedBottomSheet.this.parentActivity != null) {
              try
              {
                EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e(paramAnonymousView);
              }
            }
          }
        }
        
        public void onVideoSizeChanged(float paramAnonymousFloat, int paramAnonymousInt) {}
        
        public void prepareToSwitchInlineMode(boolean paramAnonymousBoolean1, final Runnable paramAnonymousRunnable, float paramAnonymousFloat, boolean paramAnonymousBoolean2)
        {
          if (paramAnonymousBoolean1) {
            if (EmbedBottomSheet.this.parentActivity == null) {}
          }
          for (;;)
          {
            Object localObject2;
            try
            {
              EmbedBottomSheet.this.containerView.setSystemUiVisibility(0);
              if (EmbedBottomSheet.this.prevOrientation != -2) {
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
              }
              if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
              {
                EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F));
                EmbedBottomSheet.this.backDrawable.setAlpha(0);
              }
              EmbedBottomSheet.this.setOnShowListener(null);
              if (paramAnonymousBoolean2)
              {
                TextureView localTextureView = EmbedBottomSheet.this.videoView.getTextureView();
                View localView = EmbedBottomSheet.this.videoView.getControlsView();
                ImageView localImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                Rect localRect = PipVideoView.getPipRect(paramAnonymousFloat);
                paramAnonymousFloat = localRect.width / localTextureView.getWidth();
                if (Build.VERSION.SDK_INT >= 21) {
                  localRect.y += AndroidUtilities.statusBarHeight;
                }
                localObject2 = new AnimatorSet();
                ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(localImageView, "scaleX", new float[] { paramAnonymousFloat }), ObjectAnimator.ofFloat(localImageView, "scaleY", new float[] { paramAnonymousFloat }), ObjectAnimator.ofFloat(localImageView, "translationX", new float[] { localRect.x }), ObjectAnimator.ofFloat(localImageView, "translationY", new float[] { localRect.y }), ObjectAnimator.ofFloat(localTextureView, "scaleX", new float[] { paramAnonymousFloat }), ObjectAnimator.ofFloat(localTextureView, "scaleY", new float[] { paramAnonymousFloat }), ObjectAnimator.ofFloat(localTextureView, "translationX", new float[] { localRect.x }), ObjectAnimator.ofFloat(localTextureView, "translationY", new float[] { localRect.y }), ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[] { EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F) }), ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[] { 0 }), ObjectAnimator.ofFloat(EmbedBottomSheet.this.fullscreenVideoContainer, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(localView, "alpha", new float[] { 0.0F }) });
                ((AnimatorSet)localObject2).setInterpolator(new DecelerateInterpolator());
                ((AnimatorSet)localObject2).setDuration(250L);
                ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapter()
                {
                  public void onAnimationEnd(Animator paramAnonymous2Animator)
                  {
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
                    {
                      EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
                      EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    paramAnonymousRunnable.run();
                  }
                });
                ((AnimatorSet)localObject2).start();
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              continue;
              if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0)
              {
                EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0F);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
              }
              paramAnonymousRunnable.run();
              EmbedBottomSheet.this.dismissInternal();
              continue;
            }
            if (ApplicationLoader.mainInterfacePaused) {}
            try
            {
              Object localObject1 = EmbedBottomSheet.this.parentActivity;
              paramAnonymousRunnable = new android/content/Intent;
              paramAnonymousRunnable.<init>(ApplicationLoader.applicationContext, BringAppForegroundService.class);
              ((Activity)localObject1).startService(paramAnonymousRunnable);
              if (paramAnonymousBoolean2)
              {
                EmbedBottomSheet.this.setOnShowListener(EmbedBottomSheet.this.onShowListener);
                localObject2 = PipVideoView.getPipRect(paramAnonymousFloat);
                paramAnonymousRunnable = EmbedBottomSheet.this.videoView.getTextureView();
                localObject1 = EmbedBottomSheet.this.videoView.getTextureImageView();
                paramAnonymousFloat = ((Rect)localObject2).width / paramAnonymousRunnable.getLayoutParams().width;
                if (Build.VERSION.SDK_INT >= 21) {
                  ((Rect)localObject2).y += AndroidUtilities.statusBarHeight;
                }
                ((ImageView)localObject1).setScaleX(paramAnonymousFloat);
                ((ImageView)localObject1).setScaleY(paramAnonymousFloat);
                ((ImageView)localObject1).setTranslationX(((Rect)localObject2).x);
                ((ImageView)localObject1).setTranslationY(((Rect)localObject2).y);
                paramAnonymousRunnable.setScaleX(paramAnonymousFloat);
                paramAnonymousRunnable.setScaleY(paramAnonymousFloat);
                paramAnonymousRunnable.setTranslationX(((Rect)localObject2).x);
                paramAnonymousRunnable.setTranslationY(((Rect)localObject2).y);
                EmbedBottomSheet.this.setShowWithoutAnimation(true);
                EmbedBottomSheet.this.show();
                if (!paramAnonymousBoolean2) {
                  continue;
                }
                EmbedBottomSheet.access$3402(EmbedBottomSheet.this, 4);
                EmbedBottomSheet.this.backDrawable.setAlpha(1);
                EmbedBottomSheet.this.containerView.setTranslationY(EmbedBottomSheet.this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0F));
              }
            }
            catch (Throwable paramAnonymousRunnable)
            {
              for (;;)
              {
                FileLog.e(paramAnonymousRunnable);
                continue;
                EmbedBottomSheet.this.pipVideoView.close();
                EmbedBottomSheet.access$402(EmbedBottomSheet.this, null);
              }
            }
          }
        }
      });
      this.videoView.setVisibility(4);
      paramString3 = this.containerLayout;
      paramString4 = this.videoView;
      if (!this.hasDescription) {
        break label1829;
      }
      paramInt1 = 22;
      label559:
      paramString3.addView(paramString4, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84 - 10));
      this.progressBarBlackBackground = new View(paramContext);
      this.progressBarBlackBackground.setBackgroundColor(-16777216);
      this.progressBarBlackBackground.setVisibility(4);
      paramString4 = this.containerLayout;
      paramString3 = this.progressBarBlackBackground;
      if (!this.hasDescription) {
        break label1835;
      }
      paramInt1 = 22;
      label638:
      paramString4.addView(paramString3, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, paramInt1 + 84));
      this.progressBar = new RadialProgressView(paramContext);
      this.progressBar.setVisibility(4);
      paramString3 = this.containerLayout;
      paramString4 = this.progressBar;
      if (!this.hasDescription) {
        break label1841;
      }
      paramInt1 = 22;
      label705:
      paramString3.addView(paramString4, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 0.0F, 0.0F, (paramInt1 + 84) / 2));
      if (this.hasDescription)
      {
        paramString3 = new TextView(paramContext);
        paramString3.setTextSize(1, 16.0F);
        paramString3.setTextColor(Theme.getColor("dialogTextBlack"));
        paramString3.setText(paramString2);
        paramString3.setSingleLine(true);
        paramString3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        paramString3.setEllipsize(TextUtils.TruncateAt.END);
        paramString3.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
        this.containerLayout.addView(paramString3, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 77.0F));
      }
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(Theme.getColor("dialogTextGray"));
      paramString2.setText(paramString1);
      paramString2.setSingleLine(true);
      paramString2.setEllipsize(TextUtils.TruncateAt.END);
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.containerLayout.addView(paramString2, LayoutHelper.createFrame(-1, -2.0F, 83, 0.0F, 0.0F, 0.0F, 57.0F));
      paramString1 = new View(paramContext);
      paramString1.setBackgroundColor(Theme.getColor("dialogGrayLine"));
      this.containerLayout.addView(paramString1, new FrameLayout.LayoutParams(-1, 1, 83));
      ((FrameLayout.LayoutParams)paramString1.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0F);
      paramString3 = new FrameLayout(paramContext);
      paramString3.setBackgroundColor(Theme.getColor("dialogBackground"));
      this.containerLayout.addView(paramString3, LayoutHelper.createFrame(-1, 48, 83));
      paramString1 = new LinearLayout(paramContext);
      paramString1.setOrientation(0);
      paramString1.setWeightSum(1.0F);
      paramString3.addView(paramString1, LayoutHelper.createFrame(-2, -1, 53));
      paramString2 = new TextView(paramContext);
      paramString2.setTextSize(1, 14.0F);
      paramString2.setTextColor(Theme.getColor("dialogTextBlue4"));
      paramString2.setGravity(17);
      paramString2.setSingleLine(true);
      paramString2.setEllipsize(TextUtils.TruncateAt.END);
      paramString2.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      paramString2.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      paramString2.setText(LocaleController.getString("Close", NUM).toUpperCase());
      paramString2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString3.addView(paramString2, LayoutHelper.createLinear(-2, -1, 51));
      paramString2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          EmbedBottomSheet.this.dismiss();
        }
      });
      this.imageButtonsContainer = new LinearLayout(paramContext);
      this.imageButtonsContainer.setVisibility(4);
      paramString3.addView(this.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
      this.pipButton = new ImageView(paramContext);
      this.pipButton.setScaleType(ImageView.ScaleType.CENTER);
      this.pipButton.setImageResource(NUM);
      this.pipButton.setEnabled(false);
      this.pipButton.setAlpha(0.5F);
      this.pipButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlue4"), PorterDuff.Mode.MULTIPLY));
      this.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      this.imageButtonsContainer.addView(this.pipButton, LayoutHelper.createFrame(48, 48.0F, 51, 0.0F, 0.0F, 4.0F, 0.0F));
      this.pipButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (!EmbedBottomSheet.this.checkInlinePermissions()) {
            return;
          }
          float f;
          if (EmbedBottomSheet.this.progressBar.getVisibility() != 0)
          {
            EmbedBottomSheet.access$402(EmbedBottomSheet.this, new PipVideoView());
            Object localObject1 = EmbedBottomSheet.this.pipVideoView;
            Object localObject2 = EmbedBottomSheet.this.parentActivity;
            paramAnonymousView = EmbedBottomSheet.this;
            if ((EmbedBottomSheet.this.width == 0) || (EmbedBottomSheet.this.height == 0)) {
              break label515;
            }
            f = EmbedBottomSheet.this.width / EmbedBottomSheet.this.height;
            label99:
            ((PipVideoView)localObject1).show((Activity)localObject2, paramAnonymousView, null, f, 0, EmbedBottomSheet.this.webView);
            if (EmbedBottomSheet.this.isYouTube) {
              EmbedBottomSheet.this.runJsCode("hideControls();");
            }
            if (0 == 0) {
              break label521;
            }
            EmbedBottomSheet.access$3802(EmbedBottomSheet.this, true);
            EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
            paramAnonymousView = EmbedBottomSheet.this.position;
            paramAnonymousView[0] -= EmbedBottomSheet.this.getLeftInset();
            paramAnonymousView = EmbedBottomSheet.this.position;
            paramAnonymousView[1] = ((int)(paramAnonymousView[1] - EmbedBottomSheet.this.containerView.getTranslationY()));
            localObject1 = EmbedBottomSheet.this.videoView.getTextureView();
            paramAnonymousView = EmbedBottomSheet.this.videoView.getTextureImageView();
            localObject2 = new AnimatorSet();
            ((AnimatorSet)localObject2).playTogether(new Animator[] { ObjectAnimator.ofFloat(paramAnonymousView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(paramAnonymousView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(paramAnonymousView, "translationX", new float[] { EmbedBottomSheet.this.position[0] }), ObjectAnimator.ofFloat(paramAnonymousView, "translationY", new float[] { EmbedBottomSheet.this.position[1] }), ObjectAnimator.ofFloat(localObject1, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(localObject1, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(localObject1, "translationX", new float[] { EmbedBottomSheet.this.position[0] }), ObjectAnimator.ofFloat(localObject1, "translationY", new float[] { EmbedBottomSheet.this.position[1] }), ObjectAnimator.ofFloat(EmbedBottomSheet.this.containerView, "translationY", new float[] { 0.0F }), ObjectAnimator.ofInt(EmbedBottomSheet.this.backDrawable, "alpha", new int[] { 51 }) });
            ((AnimatorSet)localObject2).setInterpolator(new DecelerateInterpolator());
            ((AnimatorSet)localObject2).setDuration(250L);
            ((AnimatorSet)localObject2).addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                EmbedBottomSheet.access$3802(EmbedBottomSheet.this, false);
              }
            });
            ((AnimatorSet)localObject2).start();
          }
          for (;;)
          {
            EmbedBottomSheet.this.dismissInternal();
            break;
            break;
            label515:
            f = 1.0F;
            break label99;
            label521:
            EmbedBottomSheet.this.containerView.setTranslationY(0.0F);
          }
        }
      });
      paramString2 = new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", EmbedBottomSheet.this.openUrl));
            Toast.makeText(EmbedBottomSheet.this.getContext(), LocaleController.getString("LinkCopied", NUM), 0).show();
            EmbedBottomSheet.this.dismiss();
            return;
          }
          catch (Exception paramAnonymousView)
          {
            for (;;)
            {
              FileLog.e(paramAnonymousView);
            }
          }
        }
      };
      paramString3 = new ImageView(paramContext);
      paramString3.setScaleType(ImageView.ScaleType.CENTER);
      paramString3.setImageResource(NUM);
      paramString3.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogTextBlue4"), PorterDuff.Mode.MULTIPLY));
      paramString3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      this.imageButtonsContainer.addView(paramString3, LayoutHelper.createFrame(48, 48, 51));
      paramString3.setOnClickListener(paramString2);
      this.copyTextButton = new TextView(paramContext);
      this.copyTextButton.setTextSize(1, 14.0F);
      this.copyTextButton.setTextColor(Theme.getColor("dialogTextBlue4"));
      this.copyTextButton.setGravity(17);
      this.copyTextButton.setSingleLine(true);
      this.copyTextButton.setEllipsize(TextUtils.TruncateAt.END);
      this.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      this.copyTextButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.copyTextButton.setText(LocaleController.getString("Copy", NUM).toUpperCase());
      this.copyTextButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString1.addView(this.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
      this.copyTextButton.setOnClickListener(paramString2);
      this.openInButton = new TextView(paramContext);
      this.openInButton.setTextSize(1, 14.0F);
      this.openInButton.setTextColor(Theme.getColor("dialogTextBlue4"));
      this.openInButton.setGravity(17);
      this.openInButton.setSingleLine(true);
      this.openInButton.setEllipsize(TextUtils.TruncateAt.END);
      this.openInButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0));
      this.openInButton.setPadding(AndroidUtilities.dp(18.0F), 0, AndroidUtilities.dp(18.0F), 0);
      this.openInButton.setText(LocaleController.getString("OpenInBrowser", NUM).toUpperCase());
      this.openInButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      paramString1.addView(this.openInButton, LayoutHelper.createFrame(-2, -1, 51));
      this.openInButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          Browser.openUrl(EmbedBottomSheet.this.parentActivity, EmbedBottomSheet.this.openUrl);
          EmbedBottomSheet.this.dismiss();
        }
      });
      setDelegate(new BottomSheet.BottomSheetDelegate()
      {
        public boolean canDismiss()
        {
          boolean bool;
          if (EmbedBottomSheet.this.videoView.isInFullscreen())
          {
            EmbedBottomSheet.this.videoView.exitFullscreen();
            bool = false;
          }
          for (;;)
          {
            return bool;
            try
            {
              EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
              bool = true;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e(localException);
              }
            }
          }
        }
        
        public void onOpenAnimationEnd()
        {
          if (EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, EmbedBottomSheet.this.openUrl, true))
          {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.webView.setVisibility(4);
            EmbedBottomSheet.this.videoView.setVisibility(0);
          }
          for (;;)
          {
            return;
            EmbedBottomSheet.this.progressBar.setVisibility(0);
            EmbedBottomSheet.this.webView.setVisibility(0);
            EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
            EmbedBottomSheet.this.copyTextButton.setVisibility(4);
            EmbedBottomSheet.this.webView.setKeepScreenOn(true);
            EmbedBottomSheet.this.videoView.setVisibility(4);
            EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
            EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
            if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
              EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
            }
            EmbedBottomSheet.this.videoView.loadVideo(null, null, null, false);
            Object localObject1 = new HashMap();
            ((HashMap)localObject1).put("Referer", "http://youtube.com");
            int i;
            int j;
            for (;;)
            {
              try
              {
                str = EmbedBottomSheet.this.videoView.getYoutubeId();
                if (str == null) {
                  break label493;
                }
                EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                EmbedBottomSheet.this.youtubeLogoImage.setVisibility(0);
                EmbedBottomSheet.this.youtubeLogoImage.setImageResource(NUM);
                EmbedBottomSheet.access$1102(EmbedBottomSheet.this, true);
                if (Build.VERSION.SDK_INT >= 17)
                {
                  localObject2 = EmbedBottomSheet.this.webView;
                  localObject1 = new org/telegram/ui/Components/EmbedBottomSheet$YoutubeProxy;
                  ((EmbedBottomSheet.YoutubeProxy)localObject1).<init>(EmbedBottomSheet.this, null);
                  ((WebView)localObject2).addJavascriptInterface(localObject1, "YoutubeProxy");
                }
                i = 0;
                localObject1 = EmbedBottomSheet.this.openUrl;
                j = i;
                if (localObject1 == null) {}
              }
              catch (Exception localException1)
              {
                String str;
                Object localObject2;
                Uri localUri;
                FileLog.e(localException1);
              }
              try
              {
                localUri = Uri.parse(EmbedBottomSheet.this.openUrl);
                localObject2 = localUri.getQueryParameter("t");
                localObject1 = localObject2;
                if (localObject2 == null) {
                  localObject1 = localUri.getQueryParameter("time_continue");
                }
                j = i;
                if (localObject1 != null)
                {
                  if (!((String)localObject1).contains("m")) {
                    break label469;
                  }
                  localObject1 = ((String)localObject1).split("m");
                  j = Utilities.parseInt(localObject1[0]).intValue();
                  int k = Utilities.parseInt(localObject1[1]).intValue();
                  j = j * 60 + k;
                }
              }
              catch (Exception localException2)
              {
                FileLog.e(localException2);
                j = i;
                continue;
              }
              EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://www.youtube.com", String.format("<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 0,                              \"showinfo\" : 0,                              \"modestbranding\" : 1,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();       videoEl = player.getIframe().contentDocument.getElementsByTagName('video')[0];\n       videoEl.addEventListener(\"canplay\", function() {            if (playing) {               videoEl.play();            }       }, true);       videoEl.addEventListener(\"timeupdate\", function() {            if (!posted && videoEl.currentTime > 0) {               if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);                }               posted = true;           }       }, true);       observer = new MutationObserver(function() {\n          if (videoEl.controls) {\n               videoEl.controls = 0;\n          }       });\n    }    window.onresize = function() {        player.setSize(window.innerWidth, window.innerHeight);    }    </script></body></html>", new Object[] { str, Integer.valueOf(j) }), "text/html", "UTF-8", "http://youtube.com");
              break;
              break;
              label469:
              j = Utilities.parseInt(localException1).intValue();
            }
            label493:
            EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, localException2);
          }
        }
      });
      this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext)
      {
        public void onOrientationChanged(int paramAnonymousInt)
        {
          if ((EmbedBottomSheet.this.orientationEventListener == null) || (EmbedBottomSheet.this.videoView.getVisibility() != 0)) {}
          for (;;)
          {
            return;
            if ((EmbedBottomSheet.this.parentActivity != null) && (EmbedBottomSheet.this.videoView.isInFullscreen()) && (EmbedBottomSheet.this.fullscreenedByButton)) {
              if ((paramAnonymousInt >= 240) && (paramAnonymousInt <= 300))
              {
                EmbedBottomSheet.access$2002(EmbedBottomSheet.this, true);
              }
              else if ((EmbedBottomSheet.this.wasInLandscape) && ((paramAnonymousInt >= 330) || (paramAnonymousInt <= 30)))
              {
                EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                EmbedBottomSheet.access$2102(EmbedBottomSheet.this, false);
                EmbedBottomSheet.access$2002(EmbedBottomSheet.this, false);
              }
            }
          }
        }
      };
      if (!this.orientationEventListener.canDetectOrientation()) {
        break label1847;
      }
      this.orientationEventListener.enable();
    }
    for (;;)
    {
      instance = this;
      return;
      bool = false;
      break;
      label1823:
      paramInt1 = 0;
      break label417;
      label1829:
      paramInt1 = 0;
      break label559;
      label1835:
      paramInt1 = 0;
      break label638;
      label1841:
      paramInt1 = 0;
      break label705;
      label1847:
      this.orientationEventListener.disable();
      this.orientationEventListener = null;
    }
  }
  
  public static EmbedBottomSheet getInstance()
  {
    return instance;
  }
  
  private void runJsCode(String paramString)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      this.webView.evaluateJavascript(paramString, null);
    }
    for (;;)
    {
      return;
      try
      {
        WebView localWebView = this.webView;
        StringBuilder localStringBuilder = new java/lang/StringBuilder;
        localStringBuilder.<init>();
        localWebView.loadUrl("javascript:" + paramString);
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
      }
    }
  }
  
  public static void show(Context paramContext, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    if (instance != null) {
      instance.destroy();
    }
    new EmbedBottomSheet(paramContext, paramString1, paramString2, paramString3, paramString4, paramInt1, paramInt2).show();
  }
  
  private void showOrHideYoutubeLogo(final boolean paramBoolean)
  {
    ViewPropertyAnimator localViewPropertyAnimator = this.youtubeLogoImage.animate();
    float f;
    if (paramBoolean)
    {
      f = 1.0F;
      localViewPropertyAnimator = localViewPropertyAnimator.alpha(f).setDuration(200L);
      if (!paramBoolean) {
        break label70;
      }
    }
    label70:
    for (long l = 0L;; l = 2900L)
    {
      localViewPropertyAnimator.setStartDelay(l).setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramBoolean) {
            EmbedBottomSheet.this.showOrHideYoutubeLogo(false);
          }
        }
      }).start();
      return;
      f = 0.0F;
      break;
    }
  }
  
  protected boolean canDismissWithSwipe()
  {
    if ((this.videoView.getVisibility() != 0) || (!this.videoView.isInFullscreen())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  protected boolean canDismissWithTouchOutside()
  {
    if (this.fullscreenVideoContainer.getVisibility() != 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean checkInlinePermissions()
  {
    boolean bool = false;
    if (this.parentActivity == null) {}
    for (;;)
    {
      return bool;
      if ((Build.VERSION.SDK_INT < 23) || (Settings.canDrawOverlays(this.parentActivity))) {
        bool = true;
      } else {
        new AlertDialog.Builder(this.parentActivity).setTitle(LocaleController.getString("AppName", NUM)).setMessage(LocaleController.getString("PermissionDrawAboveOtherApps", NUM)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener()
        {
          @TargetApi(23)
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if (EmbedBottomSheet.this.parentActivity != null) {
              EmbedBottomSheet.this.parentActivity.startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + EmbedBottomSheet.this.parentActivity.getPackageName())));
            }
          }
        }).show();
      }
    }
  }
  
  public void destroy()
  {
    if ((this.webView != null) && (this.webView.getVisibility() == 0))
    {
      this.containerLayout.removeView(this.webView);
      this.webView.stopLoading();
      this.webView.loadUrl("about:blank");
      this.webView.destroy();
    }
    if (this.pipVideoView != null)
    {
      this.pipVideoView.close();
      this.pipVideoView = null;
    }
    if (this.videoView != null) {
      this.videoView.destroy();
    }
    instance = null;
    dismissInternal();
  }
  
  public void exitFromPip()
  {
    if ((this.webView == null) || (this.pipVideoView == null)) {}
    for (;;)
    {
      return;
      if (ApplicationLoader.mainInterfacePaused) {}
      try
      {
        Object localObject1 = this.parentActivity;
        Object localObject2 = new android/content/Intent;
        ((Intent)localObject2).<init>(ApplicationLoader.applicationContext, BringAppForegroundService.class);
        ((Activity)localObject1).startService((Intent)localObject2);
        if (this.isYouTube) {
          runJsCode("showControls();");
        }
        localObject2 = (ViewGroup)this.webView.getParent();
        if (localObject2 != null) {
          ((ViewGroup)localObject2).removeView(this.webView);
        }
        localObject2 = this.containerLayout;
        localObject1 = this.webView;
        if (this.hasDescription)
        {
          i = 22;
          ((FrameLayout)localObject2).addView((View)localObject1, 0, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, i + 84));
          setShowWithoutAnimation(true);
          show();
          this.pipVideoView.close();
          this.pipVideoView = null;
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          FileLog.e(localThrowable);
          continue;
          int i = 0;
        }
      }
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    if ((this.videoView.getVisibility() == 0) && (this.videoView.isInitied()) && (!this.videoView.isInline()))
    {
      if (paramConfiguration.orientation != 2) {
        break label70;
      }
      if (!this.videoView.isInFullscreen()) {
        this.videoView.enterFullscreen();
      }
    }
    for (;;)
    {
      if (this.pipVideoView != null) {
        this.pipVideoView.onConfigurationChanged();
      }
      return;
      label70:
      if (this.videoView.isInFullscreen()) {
        this.videoView.exitFullscreen();
      }
    }
  }
  
  public void onContainerDraw(Canvas paramCanvas)
  {
    if (this.waitingForDraw != 0)
    {
      this.waitingForDraw -= 1;
      if (this.waitingForDraw != 0) {
        break label44;
      }
      this.videoView.updateTextureImageView();
      this.pipVideoView.close();
      this.pipVideoView = null;
    }
    for (;;)
    {
      return;
      label44:
      this.container.invalidate();
    }
  }
  
  protected void onContainerTranslationYChanged(float paramFloat)
  {
    updateTextureViewPosition();
  }
  
  protected boolean onCustomLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramView == this.videoView.getControlsView()) {
      updateTextureViewPosition();
    }
    return false;
  }
  
  protected boolean onCustomMeasure(View paramView, int paramInt1, int paramInt2)
  {
    if (paramView == this.videoView.getControlsView())
    {
      paramView = paramView.getLayoutParams();
      paramView.width = this.videoView.getMeasuredWidth();
      paramInt2 = this.videoView.getAspectRatioView().getMeasuredHeight();
      if (!this.videoView.isInFullscreen()) {
        break label59;
      }
    }
    label59:
    for (paramInt1 = 0;; paramInt1 = AndroidUtilities.dp(10.0F))
    {
      paramView.height = (paramInt1 + paramInt2);
      return false;
    }
  }
  
  public void pause()
  {
    if ((this.videoView != null) && (this.videoView.isInitied())) {
      this.videoView.pause();
    }
  }
  
  public void updateTextureViewPosition()
  {
    this.videoView.getAspectRatioView().getLocationInWindow(this.position);
    Object localObject = this.position;
    localObject[0] -= getLeftInset();
    if ((!this.videoView.isInline()) && (!this.animationInProgress))
    {
      localObject = this.videoView.getTextureView();
      ((TextureView)localObject).setTranslationX(this.position[0]);
      ((TextureView)localObject).setTranslationY(this.position[1]);
      localObject = this.videoView.getTextureImageView();
      if (localObject != null)
      {
        ((View)localObject).setTranslationX(this.position[0]);
        ((View)localObject).setTranslationY(this.position[1]);
      }
    }
    localObject = this.videoView.getControlsView();
    if (((View)localObject).getParent() == this.container) {
      ((View)localObject).setTranslationY(this.position[1]);
    }
    for (;;)
    {
      return;
      ((View)localObject).setTranslationY(0.0F);
    }
  }
  
  private class YoutubeProxy
  {
    private YoutubeProxy() {}
    
    @JavascriptInterface
    public void postEvent(final String paramString1, String paramString2)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
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
            return;
            if (!str.equals("loaded")) {
              break;
            }
            i = 0;
            break;
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0F);
            EmbedBottomSheet.this.showOrHideYoutubeLogo(false);
          }
        }
      });
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/EmbedBottomSheet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */