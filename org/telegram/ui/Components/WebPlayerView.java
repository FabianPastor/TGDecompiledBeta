package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;

public class WebPlayerView
  extends ViewGroup
  implements AudioManager.OnAudioFocusChangeListener, VideoPlayer.VideoPlayerDelegate
{
  private static final int AUDIO_FOCUSED = 2;
  private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
  private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
  private static final Pattern aparatFileListPattern;
  private static final Pattern aparatIdRegex;
  private static final Pattern coubIdRegex;
  private static final String exprName = "[a-zA-Z_$][a-zA-Z_$0-9]*";
  private static final Pattern exprParensPattern = Pattern.compile("[()]");
  private static final Pattern jsPattern;
  private static int lastContainerId = 4001;
  private static final Pattern playerIdPattern = Pattern.compile(".*?-([a-zA-Z0-9_-]+)(?:/watch_as3|/html5player(?:-new)?|(?:/[a-z]{2}_[A-Z]{2})?/base)?\\.([a-z]+)$");
  private static final Pattern sigPattern;
  private static final Pattern sigPattern2;
  private static final Pattern stmtReturnPattern;
  private static final Pattern stmtVarPattern;
  private static final Pattern stsPattern;
  private static final Pattern twitchClipFilePattern;
  private static final Pattern twitchClipIdRegex;
  private static final Pattern twitchStreamIdRegex;
  private static final Pattern vimeoIdRegex;
  private static final Pattern youtubeIdRegex = Pattern.compile("(?:youtube(?:-nocookie)?\\.com/(?:[^/\\n\\s]+/\\S+/|(?:v|e(?:mbed)?)/|\\S*?[?&]v=)|youtu\\.be/)([a-zA-Z0-9_-]{11})");
  private boolean allowInlineAnimation;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private int audioFocus;
  private Paint backgroundPaint;
  private TextureView changedTextureView;
  private boolean changingTextureView;
  private ControlsView controlsView;
  private float currentAlpha;
  private Bitmap currentBitmap;
  private AsyncTask currentTask;
  private String currentYoutubeId;
  private WebPlayerViewDelegate delegate;
  private boolean drawImage;
  private boolean firstFrameRendered;
  private int fragment_container_id;
  private ImageView fullscreenButton;
  private boolean hasAudioFocus;
  private boolean inFullscreen;
  private boolean initFailed;
  private boolean initied;
  private ImageView inlineButton;
  private String interfaceName;
  private boolean isAutoplay;
  private boolean isCompleted;
  private boolean isInline;
  private boolean isLoading;
  private boolean isStream;
  private long lastUpdateTime;
  private String playAudioType;
  private String playAudioUrl;
  private ImageView playButton;
  private String playVideoType;
  private String playVideoUrl;
  private AnimatorSet progressAnimation;
  private Runnable progressRunnable;
  private RadialProgressView progressView;
  private boolean resumeAudioOnFocusGain;
  private int seekToTime;
  private ImageView shareButton;
  private TextureView.SurfaceTextureListener surfaceTextureListener;
  private Runnable switchToInlineRunnable;
  private boolean switchingInlineMode;
  private ImageView textureImageView;
  private TextureView textureView;
  private ViewGroup textureViewContainer;
  private VideoPlayer videoPlayer;
  private int waitingForFirstTextureUpload;
  private WebView webView;
  
  static
  {
    vimeoIdRegex = Pattern.compile("https?://(?:(?:www|(player))\\.)?vimeo(pro)?\\.com/(?!(?:channels|album)/[^/?#]+/?(?:$|[?#])|[^/]+/review/|ondemand/)(?:.*?/)?(?:(?:play_redirect_hls|moogaloop\\.swf)\\?clip_id=)?(?:videos?/)?([0-9]+)(?:/[\\da-f]+)?/?(?:[?&].*)?(?:[#].*)?$");
    coubIdRegex = Pattern.compile("(?:coub:|https?://(?:coub\\.com/(?:view|embed|coubs)/|c-cdn\\.coub\\.com/fb-player\\.swf\\?.*\\bcoub(?:ID|id)=))([\\da-z]+)");
    aparatIdRegex = Pattern.compile("^https?://(?:www\\.)?aparat\\.com/(?:v/|video/video/embed/videohash/)([a-zA-Z0-9]+)");
    twitchClipIdRegex = Pattern.compile("https?://clips\\.twitch\\.tv/(?:[^/]+/)*([^/?#&]+)");
    twitchStreamIdRegex = Pattern.compile("https?://(?:(?:www\\.)?twitch\\.tv/|player\\.twitch\\.tv/\\?.*?\\bchannel=)([^/#?]+)");
    aparatFileListPattern = Pattern.compile("fileList\\s*=\\s*JSON\\.parse\\('([^']+)'\\)");
    twitchClipFilePattern = Pattern.compile("clipInfo\\s*=\\s*(\\{[^']+\\});");
    stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
    jsPattern = Pattern.compile("\"assets\":.+?\"js\":\\s*(\"[^\"]+\")");
    sigPattern = Pattern.compile("\\.sig\\|\\|([a-zA-Z0-9$]+)\\(");
    sigPattern2 = Pattern.compile("[\"']signature[\"']\\s*,\\s*([a-zA-Z0-9$]+)\\(");
    stmtVarPattern = Pattern.compile("var\\s");
    stmtReturnPattern = Pattern.compile("return(?:\\s+|$)");
  }
  
  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public WebPlayerView(Context paramContext, boolean paramBoolean1, boolean paramBoolean2, WebPlayerViewDelegate paramWebPlayerViewDelegate)
  {
    super(paramContext);
    int i = lastContainerId;
    lastContainerId = i + 1;
    this.fragment_container_id = i;
    boolean bool;
    if (Build.VERSION.SDK_INT >= 21)
    {
      bool = true;
      this.allowInlineAnimation = bool;
      this.backgroundPaint = new Paint();
      this.progressRunnable = new Runnable()
      {
        public void run()
        {
          if ((WebPlayerView.this.videoPlayer == null) || (!WebPlayerView.this.videoPlayer.isPlaying())) {}
          for (;;)
          {
            return;
            WebPlayerView.this.controlsView.setProgress((int)(WebPlayerView.this.videoPlayer.getCurrentPosition() / 1000L));
            WebPlayerView.this.controlsView.setBufferedProgress((int)(WebPlayerView.this.videoPlayer.getBufferedPosition() / 1000L));
            AndroidUtilities.runOnUIThread(WebPlayerView.this.progressRunnable, 1000L);
          }
        }
      };
      this.surfaceTextureListener = new TextureView.SurfaceTextureListener()
      {
        public void onSurfaceTextureAvailable(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
        
        public boolean onSurfaceTextureDestroyed(SurfaceTexture paramAnonymousSurfaceTexture)
        {
          boolean bool = false;
          if (WebPlayerView.this.changingTextureView)
          {
            if (WebPlayerView.this.switchingInlineMode) {
              WebPlayerView.access$3102(WebPlayerView.this, 2);
            }
            WebPlayerView.this.textureView.setSurfaceTexture(paramAnonymousSurfaceTexture);
            WebPlayerView.this.textureView.setVisibility(0);
            WebPlayerView.access$2902(WebPlayerView.this, false);
          }
          for (;;)
          {
            return bool;
            bool = true;
          }
        }
        
        public void onSurfaceTextureSizeChanged(SurfaceTexture paramAnonymousSurfaceTexture, int paramAnonymousInt1, int paramAnonymousInt2) {}
        
        public void onSurfaceTextureUpdated(SurfaceTexture paramAnonymousSurfaceTexture)
        {
          if (WebPlayerView.this.waitingForFirstTextureUpload == 1)
          {
            WebPlayerView.this.changedTextureView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
            {
              public boolean onPreDraw()
              {
                WebPlayerView.this.changedTextureView.getViewTreeObserver().removeOnPreDrawListener(this);
                if (WebPlayerView.this.textureImageView != null)
                {
                  WebPlayerView.this.textureImageView.setVisibility(4);
                  WebPlayerView.this.textureImageView.setImageDrawable(null);
                  if (WebPlayerView.this.currentBitmap != null)
                  {
                    WebPlayerView.this.currentBitmap.recycle();
                    WebPlayerView.access$3502(WebPlayerView.this, null);
                  }
                }
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    WebPlayerView.this.delegate.onInlineSurfaceTextureReady();
                  }
                });
                WebPlayerView.access$3102(WebPlayerView.this, 0);
                return true;
              }
            });
            WebPlayerView.this.changedTextureView.invalidate();
          }
        }
      };
      this.switchToInlineRunnable = new Runnable()
      {
        public void run()
        {
          WebPlayerView.access$3002(WebPlayerView.this, false);
          if (WebPlayerView.this.currentBitmap != null)
          {
            WebPlayerView.this.currentBitmap.recycle();
            WebPlayerView.access$3502(WebPlayerView.this, null);
          }
          WebPlayerView.access$2902(WebPlayerView.this, true);
          if (WebPlayerView.this.textureImageView != null) {}
          try
          {
            WebPlayerView.access$3502(WebPlayerView.this, Bitmaps.createBitmap(WebPlayerView.this.textureView.getWidth(), WebPlayerView.this.textureView.getHeight(), Bitmap.Config.ARGB_8888));
            WebPlayerView.this.textureView.getBitmap(WebPlayerView.this.currentBitmap);
            if (WebPlayerView.this.currentBitmap != null)
            {
              WebPlayerView.this.textureImageView.setVisibility(0);
              WebPlayerView.this.textureImageView.setImageBitmap(WebPlayerView.this.currentBitmap);
              WebPlayerView.access$3702(WebPlayerView.this, true);
              WebPlayerView.this.updatePlayButton();
              WebPlayerView.this.updateShareButton();
              WebPlayerView.this.updateFullscreenButton();
              WebPlayerView.this.updateInlineButton();
              ViewGroup localViewGroup = (ViewGroup)WebPlayerView.this.controlsView.getParent();
              if (localViewGroup != null) {
                localViewGroup.removeView(WebPlayerView.this.controlsView);
              }
              WebPlayerView.access$3302(WebPlayerView.this, WebPlayerView.this.delegate.onSwitchInlineMode(WebPlayerView.this.controlsView, WebPlayerView.this.isInline, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.aspectRatioFrameLayout.getVideoRotation(), WebPlayerView.this.allowInlineAnimation));
              WebPlayerView.this.changedTextureView.setVisibility(4);
              localViewGroup = (ViewGroup)WebPlayerView.this.textureView.getParent();
              if (localViewGroup != null) {
                localViewGroup.removeView(WebPlayerView.this.textureView);
              }
              WebPlayerView.this.controlsView.show(false, false);
              return;
            }
          }
          catch (Throwable localThrowable)
          {
            for (;;)
            {
              if (WebPlayerView.this.currentBitmap != null)
              {
                WebPlayerView.this.currentBitmap.recycle();
                WebPlayerView.access$3502(WebPlayerView.this, null);
              }
              FileLog.e(localThrowable);
              continue;
              WebPlayerView.this.textureImageView.setImageDrawable(null);
            }
          }
        }
      };
      setWillNotDraw(false);
      this.delegate = paramWebPlayerViewDelegate;
      this.backgroundPaint.setColor(-16777216);
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(paramContext)
      {
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          super.onMeasure(paramAnonymousInt1, paramAnonymousInt2);
          if (WebPlayerView.this.textureViewContainer != null)
          {
            ViewGroup.LayoutParams localLayoutParams = WebPlayerView.this.textureView.getLayoutParams();
            localLayoutParams.width = getMeasuredWidth();
            localLayoutParams.height = getMeasuredHeight();
            if (WebPlayerView.this.textureImageView != null)
            {
              localLayoutParams = WebPlayerView.this.textureImageView.getLayoutParams();
              localLayoutParams.width = getMeasuredWidth();
              localLayoutParams.height = getMeasuredHeight();
            }
          }
        }
      };
      addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
      this.interfaceName = "JavaScriptInterface";
      this.webView = new WebView(paramContext);
      this.webView.addJavascriptInterface(new JavaScriptInterface(new CallJavaResultInterface()
      {
        public void jsCallFinished(String paramAnonymousString)
        {
          if ((WebPlayerView.this.currentTask != null) && (!WebPlayerView.this.currentTask.isCancelled()) && ((WebPlayerView.this.currentTask instanceof WebPlayerView.YoutubeVideoTask))) {
            WebPlayerView.YoutubeVideoTask.access$5200((WebPlayerView.YoutubeVideoTask)WebPlayerView.this.currentTask, paramAnonymousString);
          }
        }
      }), this.interfaceName);
      paramWebPlayerViewDelegate = this.webView.getSettings();
      paramWebPlayerViewDelegate.setJavaScriptEnabled(true);
      paramWebPlayerViewDelegate.setDefaultTextEncodingName("utf-8");
      this.textureViewContainer = this.delegate.getTextureViewContainer();
      this.textureView = new TextureView(paramContext);
      this.textureView.setPivotX(0.0F);
      this.textureView.setPivotY(0.0F);
      if (this.textureViewContainer == null) {
        break label709;
      }
      this.textureViewContainer.addView(this.textureView);
      label264:
      if ((this.allowInlineAnimation) && (this.textureViewContainer != null))
      {
        this.textureImageView = new ImageView(paramContext);
        this.textureImageView.setBackgroundColor(-65536);
        this.textureImageView.setPivotX(0.0F);
        this.textureImageView.setPivotY(0.0F);
        this.textureImageView.setVisibility(4);
        this.textureViewContainer.addView(this.textureImageView);
      }
      this.videoPlayer = new VideoPlayer();
      this.videoPlayer.setDelegate(this);
      this.videoPlayer.setTextureView(this.textureView);
      this.controlsView = new ControlsView(paramContext);
      if (this.textureViewContainer == null) {
        break label730;
      }
      this.textureViewContainer.addView(this.controlsView);
    }
    for (;;)
    {
      this.progressView = new RadialProgressView(paramContext);
      this.progressView.setProgressColor(-1);
      addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
      this.fullscreenButton = new ImageView(paramContext);
      this.fullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
      this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 5.0F));
      this.fullscreenButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((!WebPlayerView.this.initied) || (WebPlayerView.this.changingTextureView) || (WebPlayerView.this.switchingInlineMode) || (!WebPlayerView.this.firstFrameRendered)) {
            return;
          }
          paramAnonymousView = WebPlayerView.this;
          if (!WebPlayerView.this.inFullscreen) {}
          for (boolean bool = true;; bool = false)
          {
            WebPlayerView.access$4502(paramAnonymousView, bool);
            WebPlayerView.this.updateFullscreenState(true);
            break;
          }
        }
      });
      this.playButton = new ImageView(paramContext);
      this.playButton.setScaleType(ImageView.ScaleType.CENTER);
      this.controlsView.addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
      this.playButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if ((!WebPlayerView.this.initied) || (WebPlayerView.this.playVideoUrl == null)) {
            return;
          }
          if (!WebPlayerView.this.videoPlayer.isPlayerPrepared()) {
            WebPlayerView.this.preparePlayer();
          }
          if (WebPlayerView.this.videoPlayer.isPlaying()) {
            WebPlayerView.this.videoPlayer.pause();
          }
          for (;;)
          {
            WebPlayerView.this.updatePlayButton();
            break;
            WebPlayerView.access$5402(WebPlayerView.this, false);
            WebPlayerView.this.videoPlayer.play();
          }
        }
      });
      if (paramBoolean1)
      {
        this.inlineButton = new ImageView(paramContext);
        this.inlineButton.setScaleType(ImageView.ScaleType.CENTER);
        this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
        this.inlineButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if ((WebPlayerView.this.textureView == null) || (!WebPlayerView.this.delegate.checkInlinePermissions()) || (WebPlayerView.this.changingTextureView) || (WebPlayerView.this.switchingInlineMode) || (!WebPlayerView.this.firstFrameRendered)) {}
            for (;;)
            {
              return;
              WebPlayerView.access$3002(WebPlayerView.this, true);
              if (WebPlayerView.this.isInline) {
                break;
              }
              WebPlayerView.access$4502(WebPlayerView.this, false);
              WebPlayerView.this.delegate.prepareToSwitchInlineMode(true, WebPlayerView.this.switchToInlineRunnable, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
            }
            paramAnonymousView = (ViewGroup)WebPlayerView.this.aspectRatioFrameLayout.getParent();
            if (paramAnonymousView != WebPlayerView.this)
            {
              if (paramAnonymousView != null) {
                paramAnonymousView.removeView(WebPlayerView.this.aspectRatioFrameLayout);
              }
              WebPlayerView.this.addView(WebPlayerView.this.aspectRatioFrameLayout, 0, LayoutHelper.createFrame(-1, -1, 17));
              WebPlayerView.this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(WebPlayerView.this.getMeasuredHeight() - AndroidUtilities.dp(10.0F), NUM));
            }
            if (WebPlayerView.this.currentBitmap != null)
            {
              WebPlayerView.this.currentBitmap.recycle();
              WebPlayerView.access$3502(WebPlayerView.this, null);
            }
            WebPlayerView.access$2902(WebPlayerView.this, true);
            WebPlayerView.access$3702(WebPlayerView.this, false);
            WebPlayerView.this.updatePlayButton();
            WebPlayerView.this.updateShareButton();
            WebPlayerView.this.updateFullscreenButton();
            WebPlayerView.this.updateInlineButton();
            WebPlayerView.this.textureView.setVisibility(4);
            if (WebPlayerView.this.textureViewContainer != null)
            {
              WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.textureView);
              label336:
              paramAnonymousView = (ViewGroup)WebPlayerView.this.controlsView.getParent();
              if (paramAnonymousView != WebPlayerView.this)
              {
                if (paramAnonymousView != null) {
                  paramAnonymousView.removeView(WebPlayerView.this.controlsView);
                }
                if (WebPlayerView.this.textureViewContainer == null) {
                  break label466;
                }
                WebPlayerView.this.textureViewContainer.addView(WebPlayerView.this.controlsView);
              }
            }
            for (;;)
            {
              WebPlayerView.this.controlsView.show(false, false);
              WebPlayerView.this.delegate.prepareToSwitchInlineMode(false, null, WebPlayerView.this.aspectRatioFrameLayout.getAspectRatio(), WebPlayerView.this.allowInlineAnimation);
              break;
              WebPlayerView.this.aspectRatioFrameLayout.addView(WebPlayerView.this.textureView);
              break label336;
              label466:
              WebPlayerView.this.addView(WebPlayerView.this.controlsView, 1);
            }
          }
        });
      }
      if (paramBoolean2)
      {
        this.shareButton = new ImageView(paramContext);
        this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
        this.shareButton.setImageResource(NUM);
        this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
        this.shareButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (WebPlayerView.this.delegate != null) {
              WebPlayerView.this.delegate.onSharePressed();
            }
          }
        });
      }
      updatePlayButton();
      updateFullscreenButton();
      updateInlineButton();
      updateShareButton();
      return;
      bool = false;
      break;
      label709:
      this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
      break label264;
      label730:
      addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0F));
    }
  }
  
  private void checkAudioFocus()
  {
    if (!this.hasAudioFocus)
    {
      AudioManager localAudioManager = (AudioManager)ApplicationLoader.applicationContext.getSystemService("audio");
      this.hasAudioFocus = true;
      if (localAudioManager.requestAudioFocus(this, 3, 1) == 1) {
        this.audioFocus = 2;
      }
    }
  }
  
  private View getControlView()
  {
    return this.controlsView;
  }
  
  private View getProgressView()
  {
    return this.progressView;
  }
  
  private void onInitFailed()
  {
    if (this.controlsView.getParent() != this) {
      this.controlsView.setVisibility(8);
    }
    this.delegate.onInitFailed();
  }
  
  private void preparePlayer()
  {
    if (this.playVideoUrl == null) {
      return;
    }
    if ((this.playVideoUrl != null) && (this.playAudioUrl != null))
    {
      this.videoPlayer.preparePlayerLoop(Uri.parse(this.playVideoUrl), this.playVideoType, Uri.parse(this.playAudioUrl), this.playAudioType);
      label51:
      this.videoPlayer.setPlayWhenReady(this.isAutoplay);
      this.isLoading = false;
      if (this.videoPlayer.getDuration() == -9223372036854775807L) {
        break label167;
      }
      this.controlsView.setDuration((int)(this.videoPlayer.getDuration() / 1000L));
    }
    for (;;)
    {
      updateFullscreenButton();
      updateShareButton();
      updateInlineButton();
      this.controlsView.invalidate();
      if (this.seekToTime == -1) {
        break;
      }
      this.videoPlayer.seekTo(this.seekToTime * 1000);
      break;
      this.videoPlayer.preparePlayer(Uri.parse(this.playVideoUrl), this.playVideoType);
      break label51;
      label167:
      this.controlsView.setDuration(0);
    }
  }
  
  private void showProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f = 1.0F;
    if (paramBoolean2)
    {
      if (this.progressAnimation != null) {
        this.progressAnimation.cancel();
      }
      this.progressAnimation = new AnimatorSet();
      localObject = this.progressAnimation;
      RadialProgressView localRadialProgressView = this.progressView;
      if (paramBoolean1) {}
      for (;;)
      {
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localRadialProgressView, "alpha", new float[] { f }) });
        this.progressAnimation.setDuration(150L);
        this.progressAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            WebPlayerView.access$5802(WebPlayerView.this, null);
          }
        });
        this.progressAnimation.start();
        return;
        f = 0.0F;
      }
    }
    Object localObject = this.progressView;
    if (paramBoolean1) {}
    for (;;)
    {
      ((RadialProgressView)localObject).setAlpha(f);
      break;
      f = 0.0F;
    }
  }
  
  private void updateFullscreenButton()
  {
    if ((!this.videoPlayer.isPlayerPrepared()) || (this.isInline)) {
      this.fullscreenButton.setVisibility(8);
    }
    for (;;)
    {
      return;
      this.fullscreenButton.setVisibility(0);
      if (!this.inFullscreen)
      {
        this.fullscreenButton.setImageResource(NUM);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 5.0F));
      }
      else
      {
        this.fullscreenButton.setImageResource(NUM);
        this.fullscreenButton.setLayoutParams(LayoutHelper.createFrame(56, 56.0F, 85, 0.0F, 0.0F, 0.0F, 1.0F));
      }
    }
  }
  
  private void updateFullscreenState(boolean paramBoolean)
  {
    if (this.textureView == null) {
      return;
    }
    updateFullscreenButton();
    label49:
    ViewGroup localViewGroup;
    if (this.textureViewContainer == null)
    {
      this.changingTextureView = true;
      if (!this.inFullscreen)
      {
        if (this.textureViewContainer != null) {
          this.textureViewContainer.addView(this.textureView);
        }
      }
      else
      {
        if (!this.inFullscreen) {
          break label184;
        }
        localViewGroup = (ViewGroup)this.controlsView.getParent();
        if (localViewGroup != null) {
          localViewGroup.removeView(this.controlsView);
        }
      }
      for (;;)
      {
        this.changedTextureView = this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), paramBoolean);
        this.changedTextureView.setVisibility(4);
        if ((this.inFullscreen) && (this.changedTextureView != null))
        {
          localViewGroup = (ViewGroup)this.textureView.getParent();
          if (localViewGroup != null) {
            localViewGroup.removeView(this.textureView);
          }
        }
        this.controlsView.checkNeedHide();
        break;
        this.aspectRatioFrameLayout.addView(this.textureView);
        break label49;
        label184:
        localViewGroup = (ViewGroup)this.controlsView.getParent();
        if (localViewGroup != this)
        {
          if (localViewGroup != null) {
            localViewGroup.removeView(this.controlsView);
          }
          if (this.textureViewContainer != null) {
            this.textureViewContainer.addView(this.controlsView);
          } else {
            addView(this.controlsView, 1);
          }
        }
      }
    }
    if (this.inFullscreen)
    {
      localViewGroup = (ViewGroup)this.aspectRatioFrameLayout.getParent();
      if (localViewGroup != null) {
        localViewGroup.removeView(this.aspectRatioFrameLayout);
      }
    }
    for (;;)
    {
      this.delegate.onSwitchToFullscreen(this.controlsView, this.inFullscreen, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), paramBoolean);
      break;
      localViewGroup = (ViewGroup)this.aspectRatioFrameLayout.getParent();
      if (localViewGroup != this)
      {
        if (localViewGroup != null) {
          localViewGroup.removeView(this.aspectRatioFrameLayout);
        }
        addView(this.aspectRatioFrameLayout, 0);
      }
    }
  }
  
  private void updateInlineButton()
  {
    if (this.inlineButton == null) {}
    for (;;)
    {
      return;
      ImageView localImageView = this.inlineButton;
      if (this.isInline)
      {
        i = NUM;
        label24:
        localImageView.setImageResource(i);
        localImageView = this.inlineButton;
        if (!this.videoPlayer.isPlayerPrepared()) {
          break label84;
        }
      }
      label84:
      for (int i = 0;; i = 8)
      {
        localImageView.setVisibility(i);
        if (!this.isInline) {
          break label90;
        }
        this.inlineButton.setLayoutParams(LayoutHelper.createFrame(40, 40, 53));
        break;
        i = NUM;
        break label24;
      }
      label90:
      this.inlineButton.setLayoutParams(LayoutHelper.createFrame(56, 50, 53));
    }
  }
  
  private void updatePlayButton()
  {
    this.controlsView.checkNeedHide();
    AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
    if (!this.videoPlayer.isPlaying())
    {
      if (this.isCompleted)
      {
        localImageView = this.playButton;
        if (this.isInline) {}
        for (i = NUM;; i = NUM)
        {
          localImageView.setImageResource(i);
          return;
        }
      }
      localImageView = this.playButton;
      if (this.isInline) {}
      for (i = NUM;; i = NUM)
      {
        localImageView.setImageResource(i);
        break;
      }
    }
    ImageView localImageView = this.playButton;
    if (this.isInline) {}
    for (int i = NUM;; i = NUM)
    {
      localImageView.setImageResource(i);
      AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
      checkAudioFocus();
      break;
    }
  }
  
  private void updateShareButton()
  {
    if (this.shareButton == null) {
      return;
    }
    ImageView localImageView = this.shareButton;
    if ((this.isInline) || (!this.videoPlayer.isPlayerPrepared())) {}
    for (int i = 8;; i = 0)
    {
      localImageView.setVisibility(i);
      break;
    }
  }
  
  public void destroy()
  {
    this.videoPlayer.releasePlayer();
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
    this.webView.stopLoading();
  }
  
  protected String downloadUrlContent(AsyncTask paramAsyncTask, String paramString)
  {
    return downloadUrlContent(paramAsyncTask, paramString, null, true);
  }
  
  /* Error */
  protected String downloadUrlContent(AsyncTask paramAsyncTask, String paramString, HashMap<String, String> paramHashMap, boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore 5
    //   3: iconst_1
    //   4: istore 6
    //   6: aconst_null
    //   7: astore 7
    //   9: iconst_0
    //   10: istore 8
    //   12: iconst_0
    //   13: istore 9
    //   15: aconst_null
    //   16: astore 10
    //   18: aconst_null
    //   19: astore 11
    //   21: aconst_null
    //   22: astore 12
    //   24: aconst_null
    //   25: astore 13
    //   27: aload 13
    //   29: astore 14
    //   31: new 793	java/net/URL
    //   34: astore 15
    //   36: aload 13
    //   38: astore 14
    //   40: aload 15
    //   42: aload_2
    //   43: invokespecial 795	java/net/URL:<init>	(Ljava/lang/String;)V
    //   46: aload 13
    //   48: astore 14
    //   50: aload 15
    //   52: invokevirtual 799	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   55: astore 16
    //   57: aload 16
    //   59: astore 14
    //   61: aload 16
    //   63: ldc_w 801
    //   66: ldc_w 803
    //   69: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   72: iload 4
    //   74: ifeq +18 -> 92
    //   77: aload 16
    //   79: astore 14
    //   81: aload 16
    //   83: ldc_w 811
    //   86: ldc_w 813
    //   89: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   92: aload 16
    //   94: astore 14
    //   96: aload 16
    //   98: ldc_w 815
    //   101: ldc_w 817
    //   104: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   107: aload 16
    //   109: astore 14
    //   111: aload 16
    //   113: ldc_w 819
    //   116: ldc_w 821
    //   119: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   122: aload 16
    //   124: astore 14
    //   126: aload 16
    //   128: ldc_w 823
    //   131: ldc_w 825
    //   134: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   137: aload_3
    //   138: ifnull +235 -> 373
    //   141: aload 16
    //   143: astore 14
    //   145: aload_3
    //   146: invokevirtual 831	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   149: invokeinterface 837 1 0
    //   154: astore 13
    //   156: aload 16
    //   158: astore 14
    //   160: aload 13
    //   162: invokeinterface 842 1 0
    //   167: ifeq +206 -> 373
    //   170: aload 16
    //   172: astore 14
    //   174: aload 13
    //   176: invokeinterface 846 1 0
    //   181: checkcast 848	java/util/Map$Entry
    //   184: astore_2
    //   185: aload 16
    //   187: astore 14
    //   189: aload 16
    //   191: aload_2
    //   192: invokeinterface 851 1 0
    //   197: checkcast 853	java/lang/String
    //   200: aload_2
    //   201: invokeinterface 856 1 0
    //   206: checkcast 853	java/lang/String
    //   209: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   212: goto -56 -> 156
    //   215: astore_2
    //   216: aload_2
    //   217: instanceof 858
    //   220: ifeq +586 -> 806
    //   223: iload 6
    //   225: istore 5
    //   227: invokestatic 863	org/telegram/tgnet/ConnectionsManager:isNetworkOnline	()Z
    //   230: ifeq +6 -> 236
    //   233: iconst_0
    //   234: istore 5
    //   236: aload_2
    //   237: invokestatic 869	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   240: aload 7
    //   242: astore_3
    //   243: aload 14
    //   245: astore_2
    //   246: aload 10
    //   248: astore 14
    //   250: iload 5
    //   252: ifeq +108 -> 360
    //   255: aload_2
    //   256: ifnull +43 -> 299
    //   259: aload_2
    //   260: instanceof 871
    //   263: ifeq +36 -> 299
    //   266: aload_2
    //   267: checkcast 871	java/net/HttpURLConnection
    //   270: invokevirtual 874	java/net/HttpURLConnection:getResponseCode	()I
    //   273: istore 5
    //   275: iload 5
    //   277: sipush 200
    //   280: if_icmpeq +19 -> 299
    //   283: iload 5
    //   285: sipush 202
    //   288: if_icmpeq +11 -> 299
    //   291: iload 5
    //   293: sipush 304
    //   296: if_icmpeq +3 -> 299
    //   299: iload 9
    //   301: istore 5
    //   303: aload 12
    //   305: astore_2
    //   306: aload_3
    //   307: ifnull +31 -> 338
    //   310: aload 11
    //   312: astore 14
    //   314: ldc_w 875
    //   317: newarray <illegal type>
    //   319: astore 13
    //   321: aconst_null
    //   322: astore_2
    //   323: aload_1
    //   324: invokevirtual 878	android/os/AsyncTask:isCancelled	()Z
    //   327: istore 4
    //   329: iload 4
    //   331: ifeq +554 -> 885
    //   334: iload 9
    //   336: istore 5
    //   338: iload 5
    //   340: istore 8
    //   342: aload_2
    //   343: astore 14
    //   345: aload_3
    //   346: ifnull +14 -> 360
    //   349: aload_3
    //   350: invokevirtual 883	java/io/InputStream:close	()V
    //   353: aload_2
    //   354: astore 14
    //   356: iload 5
    //   358: istore 8
    //   360: iload 8
    //   362: ifeq +654 -> 1016
    //   365: aload 14
    //   367: invokevirtual 889	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   370: astore_1
    //   371: aload_1
    //   372: areturn
    //   373: aload 16
    //   375: astore 14
    //   377: aload 16
    //   379: sipush 5000
    //   382: invokevirtual 892	java/net/URLConnection:setConnectTimeout	(I)V
    //   385: aload 16
    //   387: astore 14
    //   389: aload 16
    //   391: sipush 5000
    //   394: invokevirtual 895	java/net/URLConnection:setReadTimeout	(I)V
    //   397: aload 16
    //   399: astore 14
    //   401: aload 15
    //   403: astore 13
    //   405: aload 16
    //   407: astore_2
    //   408: aload 16
    //   410: instanceof 871
    //   413: ifeq +306 -> 719
    //   416: aload 16
    //   418: astore 14
    //   420: aload 16
    //   422: checkcast 871	java/net/HttpURLConnection
    //   425: astore 17
    //   427: aload 16
    //   429: astore 14
    //   431: aload 17
    //   433: iconst_1
    //   434: invokevirtual 898	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
    //   437: aload 16
    //   439: astore 14
    //   441: aload 17
    //   443: invokevirtual 874	java/net/HttpURLConnection:getResponseCode	()I
    //   446: istore 18
    //   448: iload 18
    //   450: sipush 302
    //   453: if_icmpeq +26 -> 479
    //   456: iload 18
    //   458: sipush 301
    //   461: if_icmpeq +18 -> 479
    //   464: aload 15
    //   466: astore 13
    //   468: aload 16
    //   470: astore_2
    //   471: iload 18
    //   473: sipush 303
    //   476: if_icmpne +243 -> 719
    //   479: aload 16
    //   481: astore 14
    //   483: aload 17
    //   485: ldc_w 900
    //   488: invokevirtual 904	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   491: astore_2
    //   492: aload 16
    //   494: astore 14
    //   496: aload 17
    //   498: ldc_w 906
    //   501: invokevirtual 904	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
    //   504: astore 13
    //   506: aload 16
    //   508: astore 14
    //   510: new 793	java/net/URL
    //   513: astore 15
    //   515: aload 16
    //   517: astore 14
    //   519: aload 15
    //   521: aload_2
    //   522: invokespecial 795	java/net/URL:<init>	(Ljava/lang/String;)V
    //   525: aload 16
    //   527: astore 14
    //   529: aload 15
    //   531: invokevirtual 799	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   534: astore 16
    //   536: aload 16
    //   538: astore 14
    //   540: aload 16
    //   542: ldc_w 908
    //   545: aload 13
    //   547: invokevirtual 911	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   550: aload 16
    //   552: astore 14
    //   554: aload 16
    //   556: ldc_w 801
    //   559: ldc_w 803
    //   562: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   565: iload 4
    //   567: ifeq +18 -> 585
    //   570: aload 16
    //   572: astore 14
    //   574: aload 16
    //   576: ldc_w 811
    //   579: ldc_w 813
    //   582: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   585: aload 16
    //   587: astore 14
    //   589: aload 16
    //   591: ldc_w 815
    //   594: ldc_w 817
    //   597: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   600: aload 16
    //   602: astore 14
    //   604: aload 16
    //   606: ldc_w 819
    //   609: ldc_w 821
    //   612: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   615: aload 16
    //   617: astore 14
    //   619: aload 16
    //   621: ldc_w 823
    //   624: ldc_w 825
    //   627: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   630: aload 15
    //   632: astore 13
    //   634: aload 16
    //   636: astore_2
    //   637: aload_3
    //   638: ifnull +81 -> 719
    //   641: aload 16
    //   643: astore 14
    //   645: aload_3
    //   646: invokevirtual 831	java/util/HashMap:entrySet	()Ljava/util/Set;
    //   649: invokeinterface 837 1 0
    //   654: astore_3
    //   655: aload 16
    //   657: astore 14
    //   659: aload 15
    //   661: astore 13
    //   663: aload 16
    //   665: astore_2
    //   666: aload_3
    //   667: invokeinterface 842 1 0
    //   672: ifeq +47 -> 719
    //   675: aload 16
    //   677: astore 14
    //   679: aload_3
    //   680: invokeinterface 846 1 0
    //   685: checkcast 848	java/util/Map$Entry
    //   688: astore_2
    //   689: aload 16
    //   691: astore 14
    //   693: aload 16
    //   695: aload_2
    //   696: invokeinterface 851 1 0
    //   701: checkcast 853	java/lang/String
    //   704: aload_2
    //   705: invokeinterface 856 1 0
    //   710: checkcast 853	java/lang/String
    //   713: invokevirtual 809	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   716: goto -61 -> 655
    //   719: aload_2
    //   720: astore 14
    //   722: aload_2
    //   723: invokevirtual 914	java/net/URLConnection:connect	()V
    //   726: iload 4
    //   728: ifeq +67 -> 795
    //   731: aload_2
    //   732: astore 14
    //   734: new 916	java/util/zip/GZIPInputStream
    //   737: astore_3
    //   738: aload_2
    //   739: astore 14
    //   741: aload_3
    //   742: aload_2
    //   743: invokevirtual 920	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   746: invokespecial 923	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   749: goto -503 -> 246
    //   752: astore_3
    //   753: iconst_0
    //   754: ifeq +14 -> 768
    //   757: aload_2
    //   758: astore 14
    //   760: new 925	java/lang/NullPointerException
    //   763: dup
    //   764: invokespecial 926	java/lang/NullPointerException:<init>	()V
    //   767: athrow
    //   768: aload_2
    //   769: astore 14
    //   771: aload 13
    //   773: invokevirtual 799	java/net/URL:openConnection	()Ljava/net/URLConnection;
    //   776: astore_2
    //   777: aload_2
    //   778: astore 14
    //   780: aload_2
    //   781: invokevirtual 914	java/net/URLConnection:connect	()V
    //   784: aload_2
    //   785: astore 14
    //   787: aload_2
    //   788: invokevirtual 920	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   791: astore_3
    //   792: goto -546 -> 246
    //   795: aload_2
    //   796: astore 14
    //   798: aload_2
    //   799: invokevirtual 920	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
    //   802: astore_3
    //   803: goto -557 -> 246
    //   806: aload_2
    //   807: instanceof 928
    //   810: ifeq +9 -> 819
    //   813: iconst_0
    //   814: istore 5
    //   816: goto -580 -> 236
    //   819: aload_2
    //   820: instanceof 930
    //   823: ifeq +37 -> 860
    //   826: iload 6
    //   828: istore 5
    //   830: aload_2
    //   831: invokevirtual 933	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   834: ifnull -598 -> 236
    //   837: iload 6
    //   839: istore 5
    //   841: aload_2
    //   842: invokevirtual 933	java/lang/Throwable:getMessage	()Ljava/lang/String;
    //   845: ldc_w 935
    //   848: invokevirtual 939	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   851: ifeq -615 -> 236
    //   854: iconst_0
    //   855: istore 5
    //   857: goto -621 -> 236
    //   860: iload 6
    //   862: istore 5
    //   864: aload_2
    //   865: instanceof 941
    //   868: ifeq -632 -> 236
    //   871: iconst_0
    //   872: istore 5
    //   874: goto -638 -> 236
    //   877: astore_2
    //   878: aload_2
    //   879: invokestatic 869	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   882: goto -583 -> 299
    //   885: aload_3
    //   886: aload 13
    //   888: invokevirtual 945	java/io/InputStream:read	([B)I
    //   891: istore 5
    //   893: iload 5
    //   895: ifle +57 -> 952
    //   898: aload_2
    //   899: ifnonnull +134 -> 1033
    //   902: new 885	java/lang/StringBuilder
    //   905: astore 14
    //   907: aload 14
    //   909: invokespecial 946	java/lang/StringBuilder:<init>	()V
    //   912: aload 14
    //   914: astore_2
    //   915: aload_2
    //   916: astore 14
    //   918: new 853	java/lang/String
    //   921: astore 16
    //   923: aload_2
    //   924: astore 14
    //   926: aload 16
    //   928: aload 13
    //   930: iconst_0
    //   931: iload 5
    //   933: ldc_w 948
    //   936: invokespecial 951	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   939: aload_2
    //   940: astore 14
    //   942: aload_2
    //   943: aload 16
    //   945: invokevirtual 955	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   948: pop
    //   949: goto -626 -> 323
    //   952: iload 5
    //   954: iconst_m1
    //   955: if_icmpne +9 -> 964
    //   958: iconst_1
    //   959: istore 5
    //   961: goto -623 -> 338
    //   964: iload 9
    //   966: istore 5
    //   968: goto -630 -> 338
    //   971: astore_1
    //   972: aload_2
    //   973: astore 14
    //   975: aload_1
    //   976: invokestatic 869	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   979: iload 9
    //   981: istore 5
    //   983: goto -645 -> 338
    //   986: astore_1
    //   987: aload 14
    //   989: astore_2
    //   990: aload_1
    //   991: invokestatic 869	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   994: iload 9
    //   996: istore 5
    //   998: goto -660 -> 338
    //   1001: astore_1
    //   1002: aload_1
    //   1003: invokestatic 869	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   1006: iload 5
    //   1008: istore 8
    //   1010: aload_2
    //   1011: astore 14
    //   1013: goto -653 -> 360
    //   1016: aconst_null
    //   1017: astore_1
    //   1018: goto -647 -> 371
    //   1021: astore_3
    //   1022: goto -254 -> 768
    //   1025: astore_1
    //   1026: goto -36 -> 990
    //   1029: astore_1
    //   1030: goto -58 -> 972
    //   1033: goto -118 -> 915
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1036	0	this	WebPlayerView
    //   0	1036	1	paramAsyncTask	AsyncTask
    //   0	1036	2	paramString	String
    //   0	1036	3	paramHashMap	HashMap<String, String>
    //   0	1036	4	paramBoolean	boolean
    //   1	1006	5	i	int
    //   4	857	6	j	int
    //   7	234	7	localObject1	Object
    //   10	999	8	k	int
    //   13	982	9	m	int
    //   16	231	10	localObject2	Object
    //   19	292	11	localObject3	Object
    //   22	282	12	localObject4	Object
    //   25	904	13	localObject5	Object
    //   29	983	14	localObject6	Object
    //   34	626	15	localURL	java.net.URL
    //   55	889	16	localObject7	Object
    //   425	72	17	localHttpURLConnection	java.net.HttpURLConnection
    //   446	31	18	n	int
    // Exception table:
    //   from	to	target	type
    //   31	36	215	java/lang/Throwable
    //   40	46	215	java/lang/Throwable
    //   50	57	215	java/lang/Throwable
    //   61	72	215	java/lang/Throwable
    //   81	92	215	java/lang/Throwable
    //   96	107	215	java/lang/Throwable
    //   111	122	215	java/lang/Throwable
    //   126	137	215	java/lang/Throwable
    //   145	156	215	java/lang/Throwable
    //   160	170	215	java/lang/Throwable
    //   174	185	215	java/lang/Throwable
    //   189	212	215	java/lang/Throwable
    //   377	385	215	java/lang/Throwable
    //   389	397	215	java/lang/Throwable
    //   408	416	215	java/lang/Throwable
    //   420	427	215	java/lang/Throwable
    //   431	437	215	java/lang/Throwable
    //   441	448	215	java/lang/Throwable
    //   483	492	215	java/lang/Throwable
    //   496	506	215	java/lang/Throwable
    //   510	515	215	java/lang/Throwable
    //   519	525	215	java/lang/Throwable
    //   529	536	215	java/lang/Throwable
    //   540	550	215	java/lang/Throwable
    //   554	565	215	java/lang/Throwable
    //   574	585	215	java/lang/Throwable
    //   589	600	215	java/lang/Throwable
    //   604	615	215	java/lang/Throwable
    //   619	630	215	java/lang/Throwable
    //   645	655	215	java/lang/Throwable
    //   666	675	215	java/lang/Throwable
    //   679	689	215	java/lang/Throwable
    //   693	716	215	java/lang/Throwable
    //   722	726	215	java/lang/Throwable
    //   734	738	215	java/lang/Throwable
    //   741	749	215	java/lang/Throwable
    //   760	768	215	java/lang/Throwable
    //   771	777	215	java/lang/Throwable
    //   780	784	215	java/lang/Throwable
    //   787	792	215	java/lang/Throwable
    //   798	803	215	java/lang/Throwable
    //   734	738	752	java/lang/Exception
    //   741	749	752	java/lang/Exception
    //   259	275	877	java/lang/Exception
    //   885	893	971	java/lang/Exception
    //   902	912	971	java/lang/Exception
    //   314	321	986	java/lang/Throwable
    //   918	923	986	java/lang/Throwable
    //   926	939	986	java/lang/Throwable
    //   942	949	986	java/lang/Throwable
    //   975	979	986	java/lang/Throwable
    //   349	353	1001	java/lang/Throwable
    //   760	768	1021	java/lang/Exception
    //   323	329	1025	java/lang/Throwable
    //   885	893	1025	java/lang/Throwable
    //   902	912	1025	java/lang/Throwable
    //   918	923	1029	java/lang/Exception
    //   926	939	1029	java/lang/Exception
    //   942	949	1029	java/lang/Exception
  }
  
  public void enterFullscreen()
  {
    if (this.inFullscreen) {}
    for (;;)
    {
      return;
      this.inFullscreen = true;
      updateInlineButton();
      updateFullscreenState(false);
    }
  }
  
  public void exitFullscreen()
  {
    if (!this.inFullscreen) {}
    for (;;)
    {
      return;
      this.inFullscreen = false;
      updateInlineButton();
      updateFullscreenState(false);
    }
  }
  
  public View getAspectRatioView()
  {
    return this.aspectRatioFrameLayout;
  }
  
  public View getControlsView()
  {
    return this.controlsView;
  }
  
  public ImageView getTextureImageView()
  {
    return this.textureImageView;
  }
  
  public TextureView getTextureView()
  {
    return this.textureView;
  }
  
  public String getYoutubeId()
  {
    return this.currentYoutubeId;
  }
  
  public boolean isInFullscreen()
  {
    return this.inFullscreen;
  }
  
  public boolean isInitied()
  {
    return this.initied;
  }
  
  public boolean isInline()
  {
    if ((this.isInline) || (this.switchingInlineMode)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean loadVideo(String paramString1, TLRPC.Photo paramPhoto, String paramString2, boolean paramBoolean)
  {
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject5 = null;
    Object localObject6 = null;
    Object localObject7 = null;
    Object localObject8 = null;
    Object localObject9 = null;
    String str1 = null;
    Object localObject10 = null;
    Object localObject11 = null;
    Object localObject12 = null;
    Object localObject13 = null;
    this.seekToTime = -1;
    Object localObject14 = localObject13;
    Object localObject15 = localObject7;
    Object localObject16 = localObject11;
    Object localObject17 = localObject9;
    Object localObject18 = localObject10;
    Object localObject19 = localObject6;
    Object localObject20 = localObject3;
    if (paramString1 != null)
    {
      if (paramString1.endsWith(".mp4"))
      {
        localObject16 = paramString1;
        localObject20 = localObject3;
        localObject19 = localObject6;
        localObject18 = localObject10;
        localObject17 = localObject9;
        localObject15 = localObject7;
        localObject14 = localObject13;
      }
    }
    else
    {
      this.initied = false;
      this.isCompleted = false;
      this.isAutoplay = paramBoolean;
      this.playVideoUrl = null;
      this.playAudioUrl = null;
      destroy();
      this.firstFrameRendered = false;
      this.currentAlpha = 1.0F;
      if (this.currentTask != null)
      {
        this.currentTask.cancel(true);
        this.currentTask = null;
      }
      updateFullscreenButton();
      updateShareButton();
      updateInlineButton();
      updatePlayButton();
      if (paramPhoto == null) {
        break label981;
      }
      paramString2 = FileLoader.getClosestPhotoSizeWithSize(paramPhoto.sizes, 80, true);
      if (paramString2 != null)
      {
        localObject3 = this.controlsView.imageReceiver;
        if (paramPhoto == null) {
          break label971;
        }
        paramString2 = paramString2.location;
        label224:
        if (paramPhoto == null) {
          break label976;
        }
        paramPhoto = "80_80_b";
        label232:
        ((ImageReceiver)localObject3).setImage(null, null, paramString2, paramPhoto, 0, null, 1);
        this.drawImage = true;
      }
      label249:
      if (this.progressAnimation != null)
      {
        this.progressAnimation.cancel();
        this.progressAnimation = null;
      }
      this.isLoading = true;
      this.controlsView.setProgress(0);
      paramPhoto = (TLRPC.Photo)localObject20;
      if (localObject20 != null)
      {
        paramPhoto = (TLRPC.Photo)localObject20;
        if (!BuildVars.DEBUG_PRIVATE_VERSION)
        {
          this.currentYoutubeId = ((String)localObject20);
          paramPhoto = null;
        }
      }
      if (localObject16 == null) {
        break label989;
      }
      this.initied = true;
      this.playVideoUrl = ((String)localObject16);
      this.playVideoType = "other";
      if (this.isAutoplay) {
        preparePlayer();
      }
      showProgress(false, false);
      this.controlsView.show(true, true);
      if ((paramPhoto == null) && (localObject19 == null) && (localObject15 == null) && (localObject14 == null) && (localObject16 == null) && (localObject17 == null) && (localObject18 == null)) {
        break label1302;
      }
      this.controlsView.setVisibility(0);
    }
    for (paramBoolean = true;; paramBoolean = false)
    {
      return paramBoolean;
      if (paramString2 != null) {}
      for (;;)
      {
        try
        {
          localObject20 = Uri.parse(paramString2);
          localObject3 = ((Uri)localObject20).getQueryParameter("t");
          paramString2 = (String)localObject3;
          if (localObject3 == null) {
            paramString2 = ((Uri)localObject20).getQueryParameter("time_continue");
          }
          if (paramString2 != null)
          {
            if (!paramString2.contains("m")) {
              continue;
            }
            paramString2 = paramString2.split("m");
            this.seekToTime = (Utilities.parseInt(paramString2[0]).intValue() * 60 + Utilities.parseInt(paramString2[1]).intValue());
          }
        }
        catch (Exception paramString2)
        {
          FileLog.e(paramString2);
          continue;
        }
        try
        {
          paramString2 = youtubeIdRegex.matcher(paramString1);
          localObject3 = null;
          if (paramString2.find()) {
            localObject3 = paramString2.group(1);
          }
          paramString2 = (String)localObject1;
          if (localObject3 != null) {
            paramString2 = (String)localObject3;
          }
        }
        catch (Exception paramString2)
        {
          FileLog.e(paramString2);
          paramString2 = (String)localObject1;
          continue;
        }
        localObject3 = localObject5;
        if (paramString2 == null) {}
        try
        {
          localObject3 = vimeoIdRegex.matcher(paramString1);
          localObject1 = null;
          if (((Matcher)localObject3).find()) {
            localObject1 = ((Matcher)localObject3).group(3);
          }
          localObject3 = localObject5;
          if (localObject1 != null) {
            localObject3 = localObject1;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
          localObject4 = localObject5;
          continue;
        }
        localObject1 = localObject12;
        if (localObject3 == null) {}
        try
        {
          localObject1 = aparatIdRegex.matcher(paramString1);
          localObject20 = null;
          if (((Matcher)localObject1).find()) {
            localObject20 = ((Matcher)localObject1).group(1);
          }
          localObject1 = localObject12;
          if (localObject20 != null) {
            localObject1 = localObject20;
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          localObject2 = localObject12;
          continue;
        }
        localObject12 = localObject8;
        if (localObject1 == null) {}
        try
        {
          localObject12 = twitchClipIdRegex.matcher(paramString1);
          localObject20 = null;
          if (((Matcher)localObject12).find()) {
            localObject20 = ((Matcher)localObject12).group(1);
          }
          localObject12 = localObject8;
          if (localObject20 != null) {
            localObject12 = localObject20;
          }
        }
        catch (Exception localException3)
        {
          FileLog.e(localException3);
          localObject12 = localObject8;
          continue;
        }
        localObject8 = str1;
        if (localObject12 == null) {}
        try
        {
          localObject8 = twitchStreamIdRegex.matcher(paramString1);
          localObject20 = null;
          if (((Matcher)localObject8).find()) {
            localObject20 = ((Matcher)localObject8).group(1);
          }
          localObject8 = str1;
          if (localObject20 != null) {
            localObject8 = localObject20;
          }
        }
        catch (Exception localException4)
        {
          FileLog.e(localException4);
          localObject8 = str1;
          continue;
        }
        localObject14 = localObject1;
        localObject15 = localObject7;
        localObject16 = localObject11;
        localObject17 = localObject12;
        localObject18 = localObject8;
        localObject19 = localObject3;
        localObject20 = paramString2;
        if (localObject8 != null) {
          break;
        }
        try
        {
          localObject20 = coubIdRegex.matcher(paramString1);
          str1 = null;
          if (((Matcher)localObject20).find()) {
            str1 = ((Matcher)localObject20).group(1);
          }
          localObject14 = localObject1;
          localObject15 = localObject7;
          localObject16 = localObject11;
          localObject17 = localObject12;
          localObject18 = localObject8;
          localObject19 = localObject3;
          localObject20 = paramString2;
          if (str1 == null) {
            break;
          }
          localObject14 = localObject1;
          localObject15 = str1;
          localObject16 = localObject11;
          localObject17 = localObject12;
          localObject18 = localObject8;
          localObject19 = localObject3;
          localObject20 = paramString2;
        }
        catch (Exception localException5)
        {
          Object localObject4;
          Object localObject2;
          FileLog.e(localException5);
          localObject14 = localObject2;
          localObject15 = localObject7;
          localObject16 = localObject11;
          localObject17 = localObject12;
          localObject18 = localObject8;
          localObject19 = localObject4;
          String str2 = paramString2;
        }
        this.seekToTime = Utilities.parseInt(paramString2).intValue();
      }
      break;
      label971:
      paramString2 = null;
      break label224;
      label976:
      paramPhoto = null;
      break label232;
      label981:
      this.drawImage = false;
      break label249;
      label989:
      if (paramPhoto != null)
      {
        paramString1 = new YoutubeVideoTask(paramPhoto);
        paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
        this.currentTask = paramString1;
      }
      for (;;)
      {
        this.controlsView.show(false, false);
        showProgress(true, false);
        break;
        if (localObject19 != null)
        {
          paramString1 = new VimeoVideoTask((String)localObject19);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
        }
        else if (localObject15 != null)
        {
          paramString1 = new CoubVideoTask((String)localObject15);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
          this.isStream = true;
        }
        else if (localObject14 != null)
        {
          paramString1 = new AparatVideoTask((String)localObject14);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
        }
        else if (localObject17 != null)
        {
          paramString1 = new TwitchClipVideoTask(paramString1, (String)localObject17);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
        }
        else if (localObject18 != null)
        {
          paramString1 = new TwitchStreamVideoTask(paramString1, (String)localObject18);
          paramString1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          this.currentTask = paramString1;
          this.isStream = true;
        }
      }
      label1302:
      this.controlsView.setVisibility(8);
    }
  }
  
  public void onAudioFocusChange(int paramInt)
  {
    if (paramInt == -1)
    {
      if (this.videoPlayer.isPlaying())
      {
        this.videoPlayer.pause();
        updatePlayButton();
      }
      this.hasAudioFocus = false;
      this.audioFocus = 0;
    }
    for (;;)
    {
      return;
      if (paramInt == 1)
      {
        this.audioFocus = 2;
        if (this.resumeAudioOnFocusGain)
        {
          this.resumeAudioOnFocusGain = false;
          this.videoPlayer.play();
        }
      }
      else if (paramInt == -3)
      {
        this.audioFocus = 1;
      }
      else if (paramInt == -2)
      {
        this.audioFocus = 0;
        if (this.videoPlayer.isPlaying())
        {
          this.resumeAudioOnFocusGain = true;
          this.videoPlayer.pause();
          updatePlayButton();
        }
      }
    }
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0F), this.backgroundPaint);
  }
  
  public void onError(Exception paramException)
  {
    FileLog.e(paramException);
    onInitFailed();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = (paramInt3 - paramInt1 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
    int j = (paramInt4 - paramInt2 - AndroidUtilities.dp(10.0F) - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
    this.aspectRatioFrameLayout.layout(i, j, this.aspectRatioFrameLayout.getMeasuredWidth() + i, this.aspectRatioFrameLayout.getMeasuredHeight() + j);
    if (this.controlsView.getParent() == this) {
      this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
    }
    paramInt1 = (paramInt3 - paramInt1 - this.progressView.getMeasuredWidth()) / 2;
    paramInt2 = (paramInt4 - paramInt2 - this.progressView.getMeasuredHeight()) / 2;
    this.progressView.layout(paramInt1, paramInt2, this.progressView.getMeasuredWidth() + paramInt1, this.progressView.getMeasuredHeight() + paramInt2);
    this.controlsView.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(10.0F));
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, NUM), View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(10.0F), NUM));
    if (this.controlsView.getParent() == this) {
      this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, NUM), View.MeasureSpec.makeMeasureSpec(paramInt2, NUM));
    }
    this.progressView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), NUM));
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public void onRenderedFirstFrame()
  {
    this.firstFrameRendered = true;
    this.lastUpdateTime = System.currentTimeMillis();
    this.controlsView.invalidate();
  }
  
  public void onStateChanged(boolean paramBoolean, int paramInt)
  {
    if (paramInt != 2)
    {
      if (this.videoPlayer.getDuration() != -9223372036854775807L) {
        this.controlsView.setDuration((int)(this.videoPlayer.getDuration() / 1000L));
      }
    }
    else
    {
      if ((paramInt == 4) || (paramInt == 1) || (!this.videoPlayer.isPlaying())) {
        break label100;
      }
      this.delegate.onPlayStateChanged(this, true);
      label69:
      if ((!this.videoPlayer.isPlaying()) || (paramInt == 4)) {
        break label114;
      }
      updatePlayButton();
    }
    for (;;)
    {
      return;
      this.controlsView.setDuration(0);
      break;
      label100:
      this.delegate.onPlayStateChanged(this, false);
      break label69;
      label114:
      if (paramInt == 4)
      {
        this.isCompleted = true;
        this.videoPlayer.pause();
        this.videoPlayer.seekTo(0L);
        updatePlayButton();
        this.controlsView.show(true, true);
      }
    }
  }
  
  public boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    boolean bool = true;
    if (this.changingTextureView)
    {
      this.changingTextureView = false;
      if ((this.inFullscreen) || (this.isInline))
      {
        if (this.isInline) {
          this.waitingForFirstTextureUpload = 1;
        }
        this.changedTextureView.setSurfaceTexture(paramSurfaceTexture);
        this.changedTextureView.setSurfaceTextureListener(this.surfaceTextureListener);
        this.changedTextureView.setVisibility(0);
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    if (this.waitingForFirstTextureUpload == 2)
    {
      if (this.textureImageView != null)
      {
        this.textureImageView.setVisibility(4);
        this.textureImageView.setImageDrawable(null);
        if (this.currentBitmap != null)
        {
          this.currentBitmap.recycle();
          this.currentBitmap = null;
        }
      }
      this.switchingInlineMode = false;
      this.delegate.onSwitchInlineMode(this.controlsView, false, this.aspectRatioFrameLayout.getAspectRatio(), this.aspectRatioFrameLayout.getVideoRotation(), this.allowInlineAnimation);
      this.waitingForFirstTextureUpload = 0;
    }
  }
  
  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    int i;
    int j;
    if (this.aspectRatioFrameLayout != null)
    {
      if (paramInt3 != 90)
      {
        i = paramInt1;
        j = paramInt2;
        if (paramInt3 != 270) {}
      }
      else
      {
        j = paramInt1;
        i = paramInt2;
      }
      if (j != 0) {
        break label70;
      }
    }
    label70:
    for (paramFloat = 1.0F;; paramFloat = i * paramFloat / j)
    {
      this.aspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt3);
      if (this.inFullscreen) {
        this.delegate.onVideoSizeChanged(paramFloat, paramInt3);
      }
      return;
    }
  }
  
  public void pause()
  {
    this.videoPlayer.pause();
    updatePlayButton();
    this.controlsView.show(true, true);
  }
  
  public void updateTextureImageView()
  {
    if (this.textureImageView == null) {}
    for (;;)
    {
      return;
      try
      {
        this.currentBitmap = Bitmaps.createBitmap(this.textureView.getWidth(), this.textureView.getHeight(), Bitmap.Config.ARGB_8888);
        this.changedTextureView.getBitmap(this.currentBitmap);
        if (this.currentBitmap != null)
        {
          this.textureImageView.setVisibility(0);
          this.textureImageView.setImageBitmap(this.currentBitmap);
        }
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          if (this.currentBitmap != null)
          {
            this.currentBitmap.recycle();
            this.currentBitmap = null;
          }
          FileLog.e(localThrowable);
        }
        this.textureImageView.setImageDrawable(null);
      }
    }
  }
  
  private class AparatVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[2];
    private String videoId;
    
    public AparatVideoTask(String paramString)
    {
      this.videoId = paramString;
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "http://www.aparat.com/video/video/embed/vt/frame/showvideo/yes/videohash/%s", new Object[] { this.videoId }));
      if (isCancelled()) {
        paramVarArgs = null;
      }
      for (;;)
      {
        return paramVarArgs;
        try
        {
          paramVarArgs = WebPlayerView.aparatFileListPattern.matcher(paramVarArgs);
          if (paramVarArgs.find())
          {
            Object localObject = paramVarArgs.group(1);
            paramVarArgs = new org/json/JSONArray;
            paramVarArgs.<init>((String)localObject);
            int i = 0;
            if (i < paramVarArgs.length())
            {
              localObject = paramVarArgs.getJSONArray(i);
              if (((JSONArray)localObject).length() == 0) {}
              for (;;)
              {
                i++;
                break;
                localObject = ((JSONArray)localObject).getJSONObject(0);
                if (((JSONObject)localObject).has("file"))
                {
                  this.results[0] = ((JSONObject)localObject).getString("file");
                  this.results[1] = "other";
                }
              }
            }
          }
        }
        catch (Exception paramVarArgs)
        {
          FileLog.e(paramVarArgs);
          if (isCancelled()) {
            paramVarArgs = null;
          } else {
            paramVarArgs = this.results[0];
          }
        }
      }
    }
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramString);
        WebPlayerView.access$1902(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  public static abstract interface CallJavaResultInterface
  {
    public abstract void jsCallFinished(String paramString);
  }
  
  private class ControlsView
    extends FrameLayout
  {
    private int bufferedPosition;
    private AnimatorSet currentAnimation;
    private int currentProgressX;
    private int duration;
    private StaticLayout durationLayout;
    private int durationWidth;
    private Runnable hideRunnable = new Runnable()
    {
      public void run()
      {
        WebPlayerView.ControlsView.this.show(false, true);
      }
    };
    private ImageReceiver imageReceiver;
    private boolean isVisible = true;
    private int lastProgressX;
    private int progress;
    private Paint progressBufferedPaint;
    private Paint progressInnerPaint;
    private StaticLayout progressLayout;
    private Paint progressPaint;
    private boolean progressPressed;
    private TextPaint textPaint;
    
    public ControlsView(Context paramContext)
    {
      super();
      setWillNotDraw(false);
      this.textPaint = new TextPaint(1);
      this.textPaint.setColor(-1);
      this.textPaint.setTextSize(AndroidUtilities.dp(12.0F));
      this.progressPaint = new Paint(1);
      this.progressPaint.setColor(-15095832);
      this.progressInnerPaint = new Paint();
      this.progressInnerPaint.setColor(-6975081);
      this.progressBufferedPaint = new Paint(1);
      this.progressBufferedPaint.setColor(-1);
      this.imageReceiver = new ImageReceiver(this);
    }
    
    private void checkNeedHide()
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      if ((this.isVisible) && (WebPlayerView.this.videoPlayer.isPlaying())) {
        AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      if (WebPlayerView.this.drawImage)
      {
        if ((WebPlayerView.this.firstFrameRendered) && (WebPlayerView.this.currentAlpha != 0.0F))
        {
          long l1 = System.currentTimeMillis();
          long l2 = WebPlayerView.this.lastUpdateTime;
          WebPlayerView.access$4902(WebPlayerView.this, l1);
          WebPlayerView.access$4802(WebPlayerView.this, WebPlayerView.this.currentAlpha - (float)(l1 - l2) / 150.0F);
          if (WebPlayerView.this.currentAlpha < 0.0F) {
            WebPlayerView.access$4802(WebPlayerView.this, 0.0F);
          }
          invalidate();
        }
        this.imageReceiver.setAlpha(WebPlayerView.this.currentAlpha);
        this.imageReceiver.draw(paramCanvas);
      }
      int i;
      int j;
      int k;
      label278:
      int m;
      int n;
      label357:
      label406:
      float f3;
      float f5;
      Paint localPaint;
      if ((WebPlayerView.this.videoPlayer.isPlayerPrepared()) && (!WebPlayerView.this.isStream))
      {
        i = getMeasuredWidth();
        j = getMeasuredHeight();
        if (!WebPlayerView.this.isInline)
        {
          if (this.durationLayout != null)
          {
            paramCanvas.save();
            f1 = i - AndroidUtilities.dp(58.0F) - this.durationWidth;
            if (!WebPlayerView.this.inFullscreen) {
              break label579;
            }
            k = 6;
            paramCanvas.translate(f1, j - AndroidUtilities.dp(k + 29));
            this.durationLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          if (this.progressLayout != null)
          {
            paramCanvas.save();
            f1 = AndroidUtilities.dp(18.0F);
            if (!WebPlayerView.this.inFullscreen) {
              break label586;
            }
            k = 6;
            paramCanvas.translate(f1, j - AndroidUtilities.dp(k + 29));
            this.progressLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
        }
        if (this.duration != 0)
        {
          if (!WebPlayerView.this.isInline) {
            break label593;
          }
          m = j - AndroidUtilities.dp(3.0F);
          n = 0;
          j -= AndroidUtilities.dp(7.0F);
          k = i;
          i = j;
          if (WebPlayerView.this.inFullscreen) {
            paramCanvas.drawRect(n, m, k, AndroidUtilities.dp(3.0F) + m, this.progressInnerPaint);
          }
          if (!this.progressPressed) {
            break label683;
          }
          j = this.currentProgressX;
          if ((this.bufferedPosition != 0) && (this.duration != 0))
          {
            float f2 = n;
            f3 = m;
            float f4 = n;
            f5 = k - n;
            f1 = this.bufferedPosition / this.duration;
            float f6 = AndroidUtilities.dp(3.0F) + m;
            if (!WebPlayerView.this.inFullscreen) {
              break label710;
            }
            localPaint = this.progressBufferedPaint;
            label483:
            paramCanvas.drawRect(f2, f3, f5 * f1 + f4, f6, localPaint);
          }
          paramCanvas.drawRect(n, m, j, AndroidUtilities.dp(3.0F) + m, this.progressPaint);
          if (!WebPlayerView.this.isInline)
          {
            f3 = j;
            f5 = i;
            if (!this.progressPressed) {
              break label719;
            }
          }
        }
      }
      label579:
      label586:
      label593:
      label683:
      label710:
      label719:
      for (float f1 = 7.0F;; f1 = 5.0F)
      {
        paramCanvas.drawCircle(f3, f5, AndroidUtilities.dp(f1), this.progressPaint);
        return;
        k = 10;
        break;
        k = 10;
        break label278;
        if (WebPlayerView.this.inFullscreen)
        {
          m = j - AndroidUtilities.dp(29.0F);
          n = AndroidUtilities.dp(36.0F) + this.durationWidth;
          k = i - AndroidUtilities.dp(76.0F) - this.durationWidth;
          i = j - AndroidUtilities.dp(28.0F);
          break label357;
        }
        m = j - AndroidUtilities.dp(13.0F);
        n = 0;
        k = i;
        i = j - AndroidUtilities.dp(12.0F);
        break label357;
        j = n + (int)((k - n) * (this.progress / this.duration));
        break label406;
        localPaint = this.progressInnerPaint;
        break label483;
      }
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = true;
      if (paramMotionEvent.getAction() == 0) {
        if (!this.isVisible) {
          show(true, true);
        }
      }
      for (;;)
      {
        return bool;
        onTouchEvent(paramMotionEvent);
        bool = this.progressPressed;
        continue;
        bool = super.onInterceptTouchEvent(paramMotionEvent);
      }
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int i;
      int j;
      int m;
      if (WebPlayerView.this.inFullscreen)
      {
        i = AndroidUtilities.dp(36.0F) + this.durationWidth;
        j = getMeasuredWidth() - AndroidUtilities.dp(76.0F) - this.durationWidth;
        k = getMeasuredHeight() - AndroidUtilities.dp(28.0F);
        if (this.duration == 0) {
          break label253;
        }
        m = (int)((j - i) * (this.progress / this.duration));
        label75:
        m = i + m;
        if (paramMotionEvent.getAction() != 0) {
          break label268;
        }
        if ((!this.isVisible) || (WebPlayerView.this.isInline) || (WebPlayerView.this.isStream)) {
          break label259;
        }
        if (this.duration != 0)
        {
          i = (int)paramMotionEvent.getX();
          j = (int)paramMotionEvent.getY();
          if ((i >= m - AndroidUtilities.dp(10.0F)) && (i <= AndroidUtilities.dp(10.0F) + m) && (j >= k - AndroidUtilities.dp(10.0F)) && (j <= AndroidUtilities.dp(10.0F) + k))
          {
            this.progressPressed = true;
            this.lastProgressX = i;
            this.currentProgressX = m;
            getParent().requestDisallowInterceptTouchEvent(true);
            invalidate();
          }
        }
        label216:
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      }
      label253:
      label259:
      label268:
      label384:
      do
      {
        for (;;)
        {
          super.onTouchEvent(paramMotionEvent);
          return true;
          i = 0;
          j = getMeasuredWidth();
          k = getMeasuredHeight() - AndroidUtilities.dp(12.0F);
          break;
          m = 0;
          break label75;
          show(true, true);
          break label216;
          if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3)) {
            break label384;
          }
          if ((WebPlayerView.this.initied) && (WebPlayerView.this.videoPlayer.isPlaying())) {
            AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
          }
          if (this.progressPressed)
          {
            this.progressPressed = false;
            if (WebPlayerView.this.initied)
            {
              this.progress = ((int)(this.duration * ((this.currentProgressX - i) / (j - i))));
              WebPlayerView.this.videoPlayer.seekTo(this.progress * 1000L);
            }
          }
        }
      } while ((paramMotionEvent.getAction() != 2) || (!this.progressPressed));
      int k = (int)paramMotionEvent.getX();
      this.currentProgressX -= this.lastProgressX - k;
      this.lastProgressX = k;
      if (this.currentProgressX < i) {
        this.currentProgressX = i;
      }
      for (;;)
      {
        setProgress((int)(this.duration * 1000 * ((this.currentProgressX - i) / (j - i))));
        invalidate();
        break;
        if (this.currentProgressX > j) {
          this.currentProgressX = j;
        }
      }
    }
    
    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      super.requestDisallowInterceptTouchEvent(paramBoolean);
      checkNeedHide();
    }
    
    public void setBufferedProgress(int paramInt)
    {
      this.bufferedPosition = paramInt;
      invalidate();
    }
    
    public void setDuration(int paramInt)
    {
      if ((this.duration == paramInt) || (paramInt < 0) || (WebPlayerView.this.isStream)) {}
      for (;;)
      {
        return;
        this.duration = paramInt;
        this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.durationLayout.getLineCount() > 0) {
          this.durationWidth = ((int)Math.ceil(this.durationLayout.getLineWidth(0)));
        }
        invalidate();
      }
    }
    
    public void setProgress(int paramInt)
    {
      if ((this.progressPressed) || (paramInt < 0) || (WebPlayerView.this.isStream)) {}
      for (;;)
      {
        return;
        this.progress = paramInt;
        this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        invalidate();
      }
    }
    
    public void show(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (this.isVisible == paramBoolean1) {
        return;
      }
      this.isVisible = paramBoolean1;
      if (this.currentAnimation != null) {
        this.currentAnimation.cancel();
      }
      if (this.isVisible) {
        if (paramBoolean2)
        {
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "alpha", new float[] { 1.0F }) });
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              WebPlayerView.ControlsView.access$4402(WebPlayerView.ControlsView.this, null);
            }
          });
          this.currentAnimation.start();
        }
      }
      for (;;)
      {
        checkNeedHide();
        break;
        setAlpha(1.0F);
        continue;
        if (paramBoolean2)
        {
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "alpha", new float[] { 0.0F }) });
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              WebPlayerView.ControlsView.access$4402(WebPlayerView.ControlsView.this, null);
            }
          });
          this.currentAnimation.start();
        }
        else
        {
          setAlpha(0.0F);
        }
      }
    }
  }
  
  private class CoubVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[4];
    private String videoId;
    
    public CoubVideoTask(String paramString)
    {
      this.videoId = paramString;
    }
    
    private String decodeUrl(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder(paramString);
      for (int i = 0; i < localStringBuilder.length(); i++)
      {
        char c1 = localStringBuilder.charAt(i);
        char c2 = Character.toLowerCase(c1);
        char c3 = c2;
        if (c1 == c2)
        {
          c2 = Character.toUpperCase(c1);
          c3 = c2;
        }
        localStringBuilder.setCharAt(i, c3);
      }
      try
      {
        paramString = new java/lang/String;
        paramString.<init>(Base64.decode(localStringBuilder.toString(), 0), "UTF-8");
        return paramString;
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          paramString = null;
        }
      }
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = null;
      String str = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://coub.com/api/v2/coubs/%s.json", new Object[] { this.videoId }));
      if (isCancelled()) {}
      for (;;)
      {
        return paramVarArgs;
        try
        {
          Object localObject = new org/json/JSONObject;
          ((JSONObject)localObject).<init>(str);
          localObject = ((JSONObject)localObject).getJSONObject("file_versions").getJSONObject("mobile");
          str = decodeUrl(((JSONObject)localObject).getString("gifv"));
          localObject = ((JSONObject)localObject).getJSONArray("audio").getString(0);
          if ((str != null) && (localObject != null))
          {
            this.results[0] = str;
            this.results[1] = "other";
            this.results[2] = localObject;
            this.results[3] = "other";
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e(localException);
          }
        }
        if (!isCancelled()) {
          paramVarArgs = this.results[0];
        }
      }
    }
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramString);
        WebPlayerView.access$1902(WebPlayerView.this, this.results[1]);
        WebPlayerView.access$2702(WebPlayerView.this, this.results[2]);
        WebPlayerView.access$2802(WebPlayerView.this, this.results[3]);
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  private class JSExtractor
  {
    private String[] assign_operators = { "|=", "^=", "&=", ">>=", "<<=", "-=", "+=", "%=", "/=", "*=", "=" };
    ArrayList<String> codeLines = new ArrayList();
    private String jsCode;
    private String[] operators = { "|", "^", "&", ">>", "<<", "-", "+", "%", "/", "*" };
    
    public JSExtractor(String paramString)
    {
      this.jsCode = paramString;
    }
    
    private void buildFunction(String[] paramArrayOfString, String paramString)
      throws Exception
    {
      HashMap localHashMap = new HashMap();
      for (int i = 0; i < paramArrayOfString.length; i++) {
        localHashMap.put(paramArrayOfString[i], "");
      }
      paramArrayOfString = paramString.split(";");
      paramString = new boolean[1];
      for (i = 0;; i++) {
        if (i < paramArrayOfString.length)
        {
          interpretStatement(paramArrayOfString[i], localHashMap, paramString, 100);
          if (paramString[0] == 0) {}
        }
        else
        {
          return;
        }
      }
    }
    
    private String extractFunction(String paramString)
      throws Exception
    {
      try
      {
        paramString = Pattern.quote(paramString);
        Matcher localMatcher = Pattern.compile(String.format(Locale.US, "(?x)(?:function\\s+%s|[{;,]\\s*%s\\s*=\\s*function|var\\s+%s\\s*=\\s*function)\\s*\\(([^)]*)\\)\\s*\\{([^}]+)\\}", new Object[] { paramString, paramString, paramString })).matcher(this.jsCode);
        if (localMatcher.find())
        {
          String str = localMatcher.group();
          if (!this.codeLines.contains(str))
          {
            ArrayList localArrayList = this.codeLines;
            paramString = new java/lang/StringBuilder;
            paramString.<init>();
            localArrayList.add(str + ";");
          }
          buildFunction(localMatcher.group(1).split(","), localMatcher.group(2));
        }
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          this.codeLines.clear();
          FileLog.e(paramString);
        }
      }
      return TextUtils.join("", this.codeLines);
    }
    
    private HashMap<String, Object> extractObject(String paramString)
      throws Exception
    {
      HashMap localHashMap = new HashMap();
      Matcher localMatcher = Pattern.compile(String.format(Locale.US, "(?:var\\s+)?%s\\s*=\\s*\\{\\s*((%s\\s*:\\s*function\\(.*?\\)\\s*\\{.*?\\}(?:,\\s*)?)*)\\}\\s*;", new Object[] { Pattern.quote(paramString), "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')" })).matcher(this.jsCode);
      paramString = null;
      while (localMatcher.find())
      {
        String str1 = localMatcher.group();
        String str2 = localMatcher.group(2);
        paramString = str2;
        if (!TextUtils.isEmpty(str2))
        {
          paramString = str2;
          if (!this.codeLines.contains(str1))
          {
            this.codeLines.add(localMatcher.group());
            paramString = str2;
          }
        }
      }
      paramString = Pattern.compile(String.format("(%s)\\s*:\\s*function\\(([a-z,]+)\\)\\{([^}]+)\\}", new Object[] { "(?:[a-zA-Z$0-9]+|\"[a-zA-Z$0-9]+\"|'[a-zA-Z$0-9]+')" })).matcher(paramString);
      while (paramString.find()) {
        buildFunction(paramString.group(2).split(","), paramString.group(3));
      }
      return localHashMap;
    }
    
    private void interpretExpression(String paramString, HashMap<String, String> paramHashMap, int paramInt)
      throws Exception
    {
      String str = paramString.trim();
      if (TextUtils.isEmpty(str)) {}
      for (;;)
      {
        return;
        Object localObject1 = str;
        if (str.charAt(0) == '(')
        {
          i = 0;
          localObject1 = WebPlayerView.exprParensPattern.matcher(str);
          int j;
          do
          {
            for (;;)
            {
              j = i;
              paramString = str;
              if (!((Matcher)localObject1).find()) {
                break label133;
              }
              if (((Matcher)localObject1).group(0).indexOf('0') != 40) {
                break;
              }
              i++;
            }
            j = i - 1;
            i = j;
          } while (j != 0);
          interpretExpression(str.substring(1, ((Matcher)localObject1).start()), paramHashMap, paramInt);
          paramString = str.substring(((Matcher)localObject1).end()).trim();
          if (TextUtils.isEmpty(paramString)) {
            continue;
          }
          label133:
          localObject1 = paramString;
          if (j != 0) {
            throw new Exception(String.format("Premature end of parens in %s", new Object[] { paramString }));
          }
        }
        int i = 0;
        for (;;)
        {
          if (i < this.assign_operators.length)
          {
            paramString = this.assign_operators[i];
            paramString = Pattern.compile(String.format(Locale.US, "(?x)(%s)(?:\\[([^\\]]+?)\\])?\\s*%s(.*)$", new Object[] { "[a-zA-Z_$][a-zA-Z_$0-9]*", Pattern.quote(paramString) })).matcher((CharSequence)localObject1);
            if (!paramString.find())
            {
              i++;
            }
            else
            {
              interpretExpression(paramString.group(3), paramHashMap, paramInt - 1);
              localObject1 = paramString.group(2);
              if (!TextUtils.isEmpty((CharSequence)localObject1))
              {
                interpretExpression((String)localObject1, paramHashMap, paramInt);
                break;
              }
              paramHashMap.put(paramString.group(1), "");
              break;
            }
          }
        }
        try
        {
          Integer.parseInt((String)localObject1);
        }
        catch (Exception paramString) {}
        if ((Pattern.compile(String.format(Locale.US, "(?!if|return|true|false)(%s)$", tmp302_299)).matcher((CharSequence)localObject1).find()) || ((((String)localObject1).charAt(0) == '"') && (((String)localObject1).charAt(((String)localObject1).length() - 1) == '"'))) {
          continue;
        }
        try
        {
          paramString = new org/json/JSONObject;
          paramString.<init>((String)localObject1);
          paramString.toString();
        }
        catch (Exception paramString)
        {
          paramString = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[] { "[a-zA-Z_$][a-zA-Z_$0-9]*" })).matcher((CharSequence)localObject1);
          if (paramString.find())
          {
            paramString.group(1);
            interpretExpression(paramString.group(2), paramHashMap, paramInt - 1);
            continue;
          }
          Matcher localMatcher = Pattern.compile(String.format(Locale.US, "(%s)(?:\\.([^(]+)|\\[([^]]+)\\])\\s*(?:\\(+([^()]*)\\))?$", new Object[] { "[a-zA-Z_$][a-zA-Z_$0-9]*" })).matcher((CharSequence)localObject1);
          Object localObject2;
          if (localMatcher.find())
          {
            localObject2 = localMatcher.group(1);
            str = localMatcher.group(2);
            paramString = localMatcher.group(3);
            if (TextUtils.isEmpty(str)) {}
            for (;;)
            {
              paramString.replace("\"", "");
              paramString = localMatcher.group(4);
              if (paramHashMap.get(localObject2) == null) {
                extractObject((String)localObject2);
              }
              if (paramString == null) {
                break;
              }
              if (((String)localObject1).charAt(((String)localObject1).length() - 1) == ')') {
                break label565;
              }
              throw new Exception("last char not ')'");
              paramString = str;
            }
            label565:
            if (paramString.length() == 0) {
              continue;
            }
            paramString = paramString.split(",");
            for (i = 0; i < paramString.length; i++) {
              interpretExpression(paramString[i], paramHashMap, paramInt);
            }
          }
          paramString = Pattern.compile(String.format(Locale.US, "(%s)\\[(.+)\\]$", new Object[] { "[a-zA-Z_$][a-zA-Z_$0-9]*" })).matcher((CharSequence)localObject1);
          if (paramString.find())
          {
            paramHashMap.get(paramString.group(1));
            interpretExpression(paramString.group(2), paramHashMap, paramInt - 1);
            continue;
          }
          i = 0;
          if (i < this.operators.length)
          {
            str = this.operators[i];
            localObject2 = Pattern.compile(String.format(Locale.US, "(.+?)%s(.+)", new Object[] { Pattern.quote(str) })).matcher((CharSequence)localObject1);
            if (!((Matcher)localObject2).find()) {}
            do
            {
              i++;
              break;
              paramString = new boolean[1];
              interpretStatement(((Matcher)localObject2).group(1), paramHashMap, paramString, paramInt - 1);
              if (paramString[0] != 0) {
                throw new Exception(String.format("Premature left-side return of %s in %s", new Object[] { str, localObject1 }));
              }
              interpretStatement(((Matcher)localObject2).group(2), paramHashMap, paramString, paramInt - 1);
            } while (paramString[0] == 0);
            throw new Exception(String.format("Premature right-side return of %s in %s", new Object[] { str, localObject1 }));
          }
          paramString = Pattern.compile(String.format(Locale.US, "^(%s)\\(([a-zA-Z0-9_$,]*)\\)$", new Object[] { "[a-zA-Z_$][a-zA-Z_$0-9]*" })).matcher((CharSequence)localObject1);
          if (paramString.find()) {
            extractFunction(paramString.group(1));
          }
          throw new Exception(String.format("Unsupported JS expression %s", new Object[] { localObject1 }));
        }
      }
    }
    
    private void interpretStatement(String paramString, HashMap<String, String> paramHashMap, boolean[] paramArrayOfBoolean, int paramInt)
      throws Exception
    {
      if (paramInt < 0) {
        throw new Exception("recursion limit reached");
      }
      paramArrayOfBoolean[0] = false;
      paramString = paramString.trim();
      Matcher localMatcher = WebPlayerView.stmtVarPattern.matcher(paramString);
      if (localMatcher.find()) {
        paramString = paramString.substring(localMatcher.group(0).length());
      }
      for (;;)
      {
        interpretExpression(paramString, paramHashMap, paramInt);
        return;
        localMatcher = WebPlayerView.stmtReturnPattern.matcher(paramString);
        if (localMatcher.find())
        {
          paramString = paramString.substring(localMatcher.group(0).length());
          paramArrayOfBoolean[0] = true;
        }
      }
    }
  }
  
  public class JavaScriptInterface
  {
    private final WebPlayerView.CallJavaResultInterface callJavaResultInterface;
    
    public JavaScriptInterface(WebPlayerView.CallJavaResultInterface paramCallJavaResultInterface)
    {
      this.callJavaResultInterface = paramCallJavaResultInterface;
    }
    
    @JavascriptInterface
    public void returnResultToJava(String paramString)
    {
      this.callJavaResultInterface.jsCallFinished(paramString);
    }
  }
  
  private class TwitchClipVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String currentUrl;
    private String[] results = new String[2];
    private String videoId;
    
    public TwitchClipVideoTask(String paramString1, String paramString2)
    {
      this.videoId = paramString2;
      this.currentUrl = paramString1;
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = null;
      Object localObject = WebPlayerView.this.downloadUrlContent(this, this.currentUrl, null, false);
      if (isCancelled()) {}
      for (;;)
      {
        return paramVarArgs;
        try
        {
          localObject = WebPlayerView.twitchClipFilePattern.matcher((CharSequence)localObject);
          if (((Matcher)localObject).find())
          {
            String str = ((Matcher)localObject).group(1);
            localObject = new org/json/JSONObject;
            ((JSONObject)localObject).<init>(str);
            localObject = ((JSONObject)localObject).getJSONArray("quality_options").getJSONObject(0);
            this.results[0] = ((JSONObject)localObject).getString("source");
            this.results[1] = "other";
          }
          if (isCancelled()) {
            continue;
          }
          paramVarArgs = this.results[0];
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
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramString);
        WebPlayerView.access$1902(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  private class TwitchStreamVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String currentUrl;
    private String[] results = new String[2];
    private String videoId;
    
    public TwitchStreamVideoTask(String paramString1, String paramString2)
    {
      this.videoId = paramString2;
      this.currentUrl = paramString1;
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      Object localObject1 = new HashMap();
      ((HashMap)localObject1).put("Client-ID", "jzkbprff40iqj646a697cyrvl0zt2m6");
      int i = this.videoId.indexOf('&');
      if (i > 0) {
        this.videoId = this.videoId.substring(0, i);
      }
      paramVarArgs = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/kraken/streams/%s?stream_type=all", new Object[] { this.videoId }), (HashMap)localObject1, false);
      if (isCancelled()) {
        paramVarArgs = null;
      }
      for (;;)
      {
        return paramVarArgs;
        try
        {
          Object localObject2 = new org/json/JSONObject;
          ((JSONObject)localObject2).<init>(paramVarArgs);
          ((JSONObject)localObject2).getJSONObject("stream");
          paramVarArgs = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://api.twitch.tv/api/channels/%s/access_token", new Object[] { this.videoId }), (HashMap)localObject1, false);
          localObject1 = new org/json/JSONObject;
          ((JSONObject)localObject1).<init>(paramVarArgs);
          paramVarArgs = URLEncoder.encode(((JSONObject)localObject1).getString("sig"), "UTF-8");
          localObject1 = URLEncoder.encode(((JSONObject)localObject1).getString("token"), "UTF-8");
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          URLEncoder.encode("https://youtube.googleapis.com/v/" + this.videoId, "UTF-8");
          localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          paramVarArgs = "allow_source=true&allow_audio_only=true&allow_spectre=true&player=twitchweb&segment_preference=4&p=" + (int)(Math.random() * 1.0E7D) + "&sig=" + paramVarArgs + "&token=" + (String)localObject1;
          paramVarArgs = String.format(Locale.US, "https://usher.ttvnw.net/api/channel/hls/%s.m3u8?%s", new Object[] { this.videoId, paramVarArgs });
          this.results[0] = paramVarArgs;
          this.results[1] = "hls";
          if (isCancelled()) {
            paramVarArgs = null;
          }
        }
        catch (Exception paramVarArgs)
        {
          for (;;)
          {
            FileLog.e(paramVarArgs);
          }
          paramVarArgs = this.results[0];
        }
      }
    }
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramString);
        WebPlayerView.access$1902(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  private class VimeoVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String[] results = new String[2];
    private String videoId;
    
    public VimeoVideoTask(String paramString)
    {
      this.videoId = paramString;
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      paramVarArgs = WebPlayerView.this.downloadUrlContent(this, String.format(Locale.US, "https://player.vimeo.com/video/%s/config", new Object[] { this.videoId }));
      if (isCancelled()) {
        paramVarArgs = null;
      }
      label88:
      label150:
      for (;;)
      {
        return paramVarArgs;
        try
        {
          JSONObject localJSONObject = new org/json/JSONObject;
          localJSONObject.<init>(paramVarArgs);
          paramVarArgs = localJSONObject.getJSONObject("request").getJSONObject("files");
          if (!paramVarArgs.has("hls")) {
            break label150;
          }
          paramVarArgs = paramVarArgs.getJSONObject("hls");
        }
        catch (Exception paramVarArgs)
        {
          for (;;)
          {
            String str;
            FileLog.e(paramVarArgs);
            continue;
            if (paramVarArgs.has("progressive"))
            {
              this.results[1] = "other";
              paramVarArgs = paramVarArgs.getJSONArray("progressive").getJSONObject(0);
              this.results[0] = paramVarArgs.getString("url");
            }
          }
          paramVarArgs = this.results[0];
        }
        try
        {
          this.results[0] = paramVarArgs.getString("url");
          this.results[1] = "hls";
          if (isCancelled()) {
            paramVarArgs = null;
          }
        }
        catch (Exception localException)
        {
          str = paramVarArgs.getString("default_cdn");
          paramVarArgs = paramVarArgs.getJSONObject("cdns").getJSONObject(str);
          this.results[0] = paramVarArgs.getString("url");
          break label88;
        }
      }
    }
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramString);
        WebPlayerView.access$1902(WebPlayerView.this, this.results[1]);
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  public static abstract interface WebPlayerViewDelegate
  {
    public abstract boolean checkInlinePermissions();
    
    public abstract ViewGroup getTextureViewContainer();
    
    public abstract void onInitFailed();
    
    public abstract void onInlineSurfaceTextureReady();
    
    public abstract void onPlayStateChanged(WebPlayerView paramWebPlayerView, boolean paramBoolean);
    
    public abstract void onSharePressed();
    
    public abstract TextureView onSwitchInlineMode(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2);
    
    public abstract TextureView onSwitchToFullscreen(View paramView, boolean paramBoolean1, float paramFloat, int paramInt, boolean paramBoolean2);
    
    public abstract void onVideoSizeChanged(float paramFloat, int paramInt);
    
    public abstract void prepareToSwitchInlineMode(boolean paramBoolean1, Runnable paramRunnable, float paramFloat, boolean paramBoolean2);
  }
  
  private class YoutubeVideoTask
    extends AsyncTask<Void, Void, String[]>
  {
    private boolean canRetry = true;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private String[] result = new String[2];
    private String sig;
    private String videoId;
    
    public YoutubeVideoTask(String paramString)
    {
      this.videoId = paramString;
    }
    
    private void onInterfaceResult(String paramString)
    {
      this.result[0] = this.result[0].replace(this.sig, "/signature/" + paramString);
      this.countDownLatch.countDown();
    }
    
    protected String[] doInBackground(Void... paramVarArgs)
    {
      Object localObject1 = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/embed/" + this.videoId);
      if (isCancelled()) {
        paramVarArgs = null;
      }
      Object localObject5;
      int i;
      label337:
      Object localObject8;
      Object localObject9;
      int i2;
      label797:
      label1226:
      label1643:
      label1649:
      label1690:
      Object localObject4;
      label1869:
      label1957:
      label2017:
      label2098:
      for (;;)
      {
        return paramVarArgs;
        paramVarArgs = "video_id=" + this.videoId + "&ps=default&gl=US&hl=en";
        try
        {
          Object localObject2 = new java/lang/StringBuilder;
          ((StringBuilder)localObject2).<init>();
          localObject2 = ((StringBuilder)localObject2).append(paramVarArgs).append("&eurl=");
          localObject5 = new java/lang/StringBuilder;
          ((StringBuilder)localObject5).<init>();
          localObject2 = URLEncoder.encode(((StringBuilder)localObject5).append("https://youtube.googleapis.com/v/").append(this.videoId).toString(), "UTF-8");
          paramVarArgs = (Void[])localObject2;
          localObject2 = paramVarArgs;
          if (localObject1 != null)
          {
            localObject2 = WebPlayerView.stsPattern.matcher((CharSequence)localObject1);
            if (((Matcher)localObject2).find()) {
              localObject2 = paramVarArgs + "&sts=" + ((String)localObject1).substring(((Matcher)localObject2).start() + 6, ((Matcher)localObject2).end());
            }
          }
          else
          {
            this.result[1] = "dash";
            i = 0;
            paramVarArgs = null;
            localObject6 = new String[5];
            localObject6[0] = "";
            localObject6[1] = "&el=leanback";
            localObject6[2] = "&el=embedded";
            localObject6[3] = "&el=detailpage";
            localObject6[4] = "&el=vevo";
            j = 0;
            k = i;
            localObject7 = paramVarArgs;
            if (j >= localObject6.length) {
              break label1226;
            }
            localObject5 = WebPlayerView.this.downloadUrlContent(this, "https://www.youtube.com/get_video_info?" + (String)localObject2 + localObject6[j]);
            if (!isCancelled()) {
              break label337;
            }
            paramVarArgs = null;
          }
        }
        catch (Exception localException1)
        {
          Object localObject6;
          Object localObject7;
          for (;;)
          {
            int j;
            FileLog.e(localException1);
            continue;
            Object localObject3 = paramVarArgs + "&sts=";
            continue;
            int m = 0;
            int n = 0;
            localObject8 = null;
            localObject9 = null;
            int i1 = 0;
            i2 = 0;
            int k = i;
            localObject7 = paramVarArgs;
            if (localObject5 != null)
            {
              String[] arrayOfString1 = ((String)localObject5).split("&");
              int i3 = 0;
              localObject5 = paramVarArgs;
              k = i;
              m = n;
              localObject8 = localObject9;
              i1 = i2;
              localObject7 = localObject5;
              if (i3 < arrayOfString1.length)
              {
                int i4;
                if (arrayOfString1[i3].startsWith("dashmpd"))
                {
                  n = 1;
                  localObject8 = arrayOfString1[i3].split("=");
                  m = i;
                  k = n;
                  paramVarArgs = (Void[])localObject9;
                  i4 = i2;
                  localObject7 = localObject5;
                  if (localObject8.length != 2) {}
                }
                for (;;)
                {
                  try
                  {
                    this.result[0] = URLDecoder.decode(localObject8[1], "UTF-8");
                    localObject7 = localObject5;
                    i4 = i2;
                    paramVarArgs = (Void[])localObject9;
                    k = n;
                    m = i;
                    i3++;
                  }
                  catch (Exception paramVarArgs)
                  {
                    FileLog.e(paramVarArgs);
                    m = i;
                    k = n;
                    paramVarArgs = (Void[])localObject9;
                    i4 = i2;
                    localObject7 = localObject5;
                    continue;
                  }
                  i = m;
                  n = k;
                  localObject9 = paramVarArgs;
                  i2 = i4;
                  localObject5 = localObject7;
                  break;
                  if (arrayOfString1[i3].startsWith("url_encoded_fmt_stream_map"))
                  {
                    localObject8 = arrayOfString1[i3].split("=");
                    m = i;
                    k = n;
                    paramVarArgs = (Void[])localObject9;
                    i4 = i2;
                    localObject7 = localObject5;
                    if (localObject8.length == 2) {
                      try
                      {
                        String[] arrayOfString2 = URLDecoder.decode(localObject8[1], "UTF-8").split("[&,]");
                        localObject8 = null;
                        int i5 = 0;
                        i1 = 0;
                        for (;;)
                        {
                          m = i;
                          k = n;
                          paramVarArgs = (Void[])localObject9;
                          i4 = i2;
                          localObject7 = localObject5;
                          if (i1 >= arrayOfString2.length) {
                            break;
                          }
                          localObject7 = arrayOfString2[i1].split("=");
                          if (localObject7[0].startsWith("type"))
                          {
                            paramVarArgs = (Void[])localObject8;
                            k = i5;
                            if (URLDecoder.decode(localObject7[1], "UTF-8").contains("video/mp4"))
                            {
                              k = 1;
                              paramVarArgs = (Void[])localObject8;
                            }
                          }
                          for (;;)
                          {
                            if ((k == 0) || (paramVarArgs == null)) {
                              break label797;
                            }
                            localObject7 = paramVarArgs;
                            m = i;
                            k = n;
                            paramVarArgs = (Void[])localObject9;
                            i4 = i2;
                            break;
                            if (localObject7[0].startsWith("url"))
                            {
                              paramVarArgs = URLDecoder.decode(localObject7[1], "UTF-8");
                              k = i5;
                            }
                            else
                            {
                              boolean bool = localObject7[0].startsWith("itag");
                              paramVarArgs = (Void[])localObject8;
                              k = i5;
                              if (bool)
                              {
                                paramVarArgs = null;
                                k = 0;
                              }
                            }
                          }
                          i1++;
                          localObject8 = paramVarArgs;
                          i5 = k;
                        }
                      }
                      catch (Exception paramVarArgs)
                      {
                        FileLog.e(paramVarArgs);
                        m = i;
                        k = n;
                        paramVarArgs = (Void[])localObject9;
                        i4 = i2;
                        localObject7 = localObject5;
                      }
                    }
                  }
                  else if (arrayOfString1[i3].startsWith("use_cipher_signature"))
                  {
                    localObject8 = arrayOfString1[i3].split("=");
                    m = i;
                    k = n;
                    paramVarArgs = (Void[])localObject9;
                    i4 = i2;
                    localObject7 = localObject5;
                    if (localObject8.length == 2)
                    {
                      m = i;
                      k = n;
                      paramVarArgs = (Void[])localObject9;
                      i4 = i2;
                      localObject7 = localObject5;
                      if (localObject8[1].toLowerCase().equals("true"))
                      {
                        m = 1;
                        k = n;
                        paramVarArgs = (Void[])localObject9;
                        i4 = i2;
                        localObject7 = localObject5;
                      }
                    }
                  }
                  else if (arrayOfString1[i3].startsWith("hlsvp"))
                  {
                    localObject8 = arrayOfString1[i3].split("=");
                    m = i;
                    k = n;
                    paramVarArgs = (Void[])localObject9;
                    i4 = i2;
                    localObject7 = localObject5;
                    if (localObject8.length == 2) {
                      try
                      {
                        paramVarArgs = URLDecoder.decode(localObject8[1], "UTF-8");
                        m = i;
                        k = n;
                        i4 = i2;
                        localObject7 = localObject5;
                      }
                      catch (Exception paramVarArgs)
                      {
                        FileLog.e(paramVarArgs);
                        m = i;
                        k = n;
                        paramVarArgs = (Void[])localObject9;
                        i4 = i2;
                        localObject7 = localObject5;
                      }
                    }
                  }
                  else
                  {
                    m = i;
                    k = n;
                    paramVarArgs = (Void[])localObject9;
                    i4 = i2;
                    localObject7 = localObject5;
                    if (arrayOfString1[i3].startsWith("livestream"))
                    {
                      localObject8 = arrayOfString1[i3].split("=");
                      m = i;
                      k = n;
                      paramVarArgs = (Void[])localObject9;
                      i4 = i2;
                      localObject7 = localObject5;
                      if (localObject8.length == 2)
                      {
                        m = i;
                        k = n;
                        paramVarArgs = (Void[])localObject9;
                        i4 = i2;
                        localObject7 = localObject5;
                        if (localObject8[1].toLowerCase().equals("1"))
                        {
                          i4 = 1;
                          m = i;
                          k = n;
                          paramVarArgs = (Void[])localObject9;
                          localObject7 = localObject5;
                        }
                      }
                    }
                  }
                }
              }
            }
            if (i1 != 0)
            {
              if ((localObject8 == null) || (k != 0) || (((String)localObject8).contains("/s/")))
              {
                paramVarArgs = null;
                break;
              }
              this.result[0] = localObject8;
              this.result[1] = "hls";
            }
            if (m != 0)
            {
              if ((this.result[0] == null) && (localObject7 != null))
              {
                this.result[0] = localObject7;
                this.result[1] = "other";
              }
              i = k;
              if (this.result[0] == null) {
                break label1957;
              }
              if (k == 0)
              {
                i = k;
                if (!this.result[0].contains("/s/")) {
                  break label1957;
                }
              }
              i = k;
              if (localObject1 == null) {
                break label1957;
              }
              i2 = 1;
              k = this.result[0].indexOf("/s/");
              n = this.result[0].indexOf('/', k + 10);
              i = i2;
              if (k == -1) {
                break label1957;
              }
              i = n;
              if (n == -1) {
                i = this.result[0].length();
              }
              this.sig = this.result[0].substring(k, i);
              paramVarArgs = null;
              localObject3 = WebPlayerView.jsPattern.matcher((CharSequence)localObject1);
              localObject5 = paramVarArgs;
              if (!((Matcher)localObject3).find()) {}
            }
            try
            {
              localObject5 = new org/json/JSONTokener;
              ((JSONTokener)localObject5).<init>(((Matcher)localObject3).group(1));
              localObject3 = ((JSONTokener)localObject5).nextValue();
              localObject5 = paramVarArgs;
              if ((localObject3 instanceof String)) {
                localObject5 = (String)localObject3;
              }
            }
            catch (Exception localException2)
            {
              for (;;)
              {
                FileLog.e(localException2);
                localObject5 = paramVarArgs;
                continue;
                localObject7 = null;
                continue;
                localObject9 = localObject5;
                if (((String)localObject5).startsWith("/")) {
                  localObject9 = "https://www.youtube.com" + (String)localObject5;
                }
              }
              localObject9 = paramVarArgs;
              localObject8 = localException2;
              if (localObject6 == null) {
                break label1869;
              }
            }
            i = i2;
            if (localObject5 == null) {
              break label1957;
            }
            paramVarArgs = WebPlayerView.playerIdPattern.matcher((CharSequence)localObject5);
            if (!paramVarArgs.find()) {
              break label1643;
            }
            localObject7 = paramVarArgs.group(1) + paramVarArgs.group(2);
            paramVarArgs = null;
            localObject3 = null;
            localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("youtubecode", 0);
            if (localObject7 != null)
            {
              paramVarArgs = ((SharedPreferences)localObject1).getString((String)localObject7, null);
              localObject3 = ((SharedPreferences)localObject1).getString((String)localObject7 + "n", null);
            }
            localObject9 = paramVarArgs;
            localObject8 = localObject3;
            if (paramVarArgs != null) {
              break label1869;
            }
            if (!((String)localObject5).startsWith("//")) {
              break label1649;
            }
            localObject9 = "https:" + (String)localObject5;
            localObject6 = WebPlayerView.this.downloadUrlContent(this, (String)localObject9);
            if (!isCancelled()) {
              break label1690;
            }
            paramVarArgs = null;
            break;
            j++;
            i = k;
            paramVarArgs = (Void[])localObject7;
          }
          localObject5 = WebPlayerView.sigPattern.matcher((CharSequence)localObject6);
          if (((Matcher)localObject5).find()) {
            localObject4 = ((Matcher)localObject5).group(1);
          }
          for (;;)
          {
            localObject9 = paramVarArgs;
            localObject8 = localObject4;
            if (localObject4 != null) {
              localObject5 = paramVarArgs;
            }
            try
            {
              localObject9 = new org/telegram/ui/Components/WebPlayerView$JSExtractor;
              localObject5 = paramVarArgs;
              ((WebPlayerView.JSExtractor)localObject9).<init>(WebPlayerView.this, (String)localObject6);
              localObject5 = paramVarArgs;
              paramVarArgs = ((WebPlayerView.JSExtractor)localObject9).extractFunction((String)localObject4);
              localObject9 = paramVarArgs;
              localObject8 = localObject4;
              localObject5 = paramVarArgs;
              if (!TextUtils.isEmpty(paramVarArgs))
              {
                localObject9 = paramVarArgs;
                localObject8 = localObject4;
                if (localObject7 != null)
                {
                  localObject5 = paramVarArgs;
                  localObject9 = ((SharedPreferences)localObject1).edit().putString((String)localObject7, paramVarArgs);
                  localObject5 = paramVarArgs;
                  localObject8 = new java/lang/StringBuilder;
                  localObject5 = paramVarArgs;
                  ((StringBuilder)localObject8).<init>();
                  localObject5 = paramVarArgs;
                  ((SharedPreferences.Editor)localObject9).putString((String)localObject7 + "n", (String)localObject4).commit();
                  localObject8 = localObject4;
                  localObject9 = paramVarArgs;
                }
              }
            }
            catch (Exception paramVarArgs)
            {
              for (;;)
              {
                FileLog.e(paramVarArgs);
                localObject9 = localObject5;
                localObject8 = localObject4;
                continue;
                paramVarArgs = (String)localObject9 + "window." + WebPlayerView.this.interfaceName + ".returnResultToJava(" + (String)localObject8 + "('" + this.sig.substring(3) + "'));";
              }
            }
            i = i2;
            if (!TextUtils.isEmpty((CharSequence)localObject9))
            {
              if (Build.VERSION.SDK_INT < 21) {
                break label2017;
              }
              paramVarArgs = (String)localObject9 + (String)localObject8 + "('" + this.sig.substring(3) + "');";
            }
            try
            {
              localObject4 = new org/telegram/ui/Components/WebPlayerView$YoutubeVideoTask$1;
              ((1)localObject4).<init>(this, paramVarArgs);
              AndroidUtilities.runOnUIThread((Runnable)localObject4);
              this.countDownLatch.await();
              i = 0;
            }
            catch (Exception paramVarArgs)
            {
              for (;;)
              {
                FileLog.e(paramVarArgs);
                i = i2;
              }
              paramVarArgs = this.result;
            }
            if ((!isCancelled()) && (i == 0)) {
              break label2098;
            }
            paramVarArgs = null;
            break;
            localObject5 = WebPlayerView.sigPattern2.matcher((CharSequence)localObject6);
            if (((Matcher)localObject5).find()) {
              localObject4 = ((Matcher)localObject5).group(1);
            }
          }
        }
      }
    }
    
    protected void onPostExecute(String[] paramArrayOfString)
    {
      if (paramArrayOfString[0] != null)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("start play youtube video " + paramArrayOfString[1] + " " + paramArrayOfString[0]);
        }
        WebPlayerView.access$1702(WebPlayerView.this, true);
        WebPlayerView.access$1802(WebPlayerView.this, paramArrayOfString[0]);
        WebPlayerView.access$1902(WebPlayerView.this, paramArrayOfString[1]);
        if (WebPlayerView.this.playVideoType.equals("hls")) {
          WebPlayerView.access$2002(WebPlayerView.this, true);
        }
        if (WebPlayerView.this.isAutoplay) {
          WebPlayerView.this.preparePlayer();
        }
        WebPlayerView.this.showProgress(false, true);
        WebPlayerView.this.controlsView.show(true, true);
      }
      for (;;)
      {
        return;
        if (!isCancelled()) {
          WebPlayerView.this.onInitFailed();
        }
      }
    }
  }
  
  private abstract class function
  {
    private function() {}
    
    public abstract Object run(Object[] paramArrayOfObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/WebPlayerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */