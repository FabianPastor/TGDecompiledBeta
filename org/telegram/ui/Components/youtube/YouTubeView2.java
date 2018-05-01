package org.telegram.ui.Components.youtube;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimatorListenerAdapterProxy;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.Components.ContextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate;

public class YouTubeView2
  extends ViewGroup
  implements VideoPlayer.VideoPlayerDelegate
{
  private static Pattern stsPattern = Pattern.compile("\"sts\"\\s*:\\s*(\\d+)");
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private ControlsView controlsView;
  private YouTubeViewDelegate delegate;
  private ImageView fullscreenButton;
  private boolean inFullscreen;
  private boolean initFailed;
  private boolean initied;
  private ImageView inlineButton;
  private boolean isAutoplay;
  private boolean isCompleted;
  private boolean isInline;
  private boolean isLoading;
  private int lastProgress = -1;
  private ImageView nextButton;
  private ImageView playButton;
  private ImageView prevButton;
  private AnimatorSet progressAnimation;
  private Runnable progressRunnable = new Runnable()
  {
    public void run()
    {
      if ((YouTubeView2.this.videoPlayer == null) || (!YouTubeView2.this.videoPlayer.isPlaying())) {
        return;
      }
      YouTubeView2.this.controlsView.setProgress((int)(YouTubeView2.this.videoPlayer.getCurrentPosition() / 1000L));
      YouTubeView2.this.controlsView.setBufferedProgress(YouTubeView2.this.videoPlayer.getBufferedPosition(), YouTubeView2.this.videoPlayer.getBufferedPercentage());
      AndroidUtilities.runOnUIThread(YouTubeView2.this.progressRunnable, 1000L);
    }
  };
  private ContextProgressView progressView;
  private ImageView shareButton;
  private TextureView textureView;
  private String videoId;
  private VideoPlayer videoPlayer;
  
  public YouTubeView2(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setBackgroundColor(-16777216);
    this.aspectRatioFrameLayout = new AspectRatioFrameLayout(paramContext);
    addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
    this.textureView = new TextureView(paramContext);
    this.aspectRatioFrameLayout.addView(this.textureView, LayoutHelper.createFrame(-1, -1, 17));
    this.videoPlayer = new VideoPlayer();
    this.videoPlayer.setDelegate(this);
    this.videoPlayer.setTextureView(this.textureView);
    this.controlsView = new ControlsView(paramContext);
    this.controlsView.setWillNotDraw(false);
    addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    addView(this.progressView, LayoutHelper.createFrame(48, 48, 17));
    this.fullscreenButton = new ImageView(paramContext);
    this.fullscreenButton.setScaleType(ImageView.ScaleType.CENTER);
    this.controlsView.addView(this.fullscreenButton, LayoutHelper.createFrame(56, 56, 85));
    this.fullscreenButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!YouTubeView2.this.initied) {
          return;
        }
        paramAnonymousView = YouTubeView2.this;
        if (!YouTubeView2.this.inFullscreen) {}
        for (boolean bool = true;; bool = false)
        {
          YouTubeView2.access$1102(paramAnonymousView, bool);
          YouTubeView2.this.updateFullscreenButton();
          if (YouTubeView2.this.delegate == null) {
            break;
          }
          YouTubeView2.this.delegate.onSwithToFullscreen(YouTubeView2.this.inFullscreen);
          return;
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
        if (!YouTubeView2.this.initied) {
          return;
        }
        if (YouTubeView2.this.videoPlayer.isPlaying()) {
          YouTubeView2.this.videoPlayer.pause();
        }
        for (;;)
        {
          YouTubeView2.this.updatePlayButton();
          return;
          YouTubeView2.this.videoPlayer.play();
        }
      }
    });
    this.prevButton = new ImageView(paramContext);
    this.prevButton.setScaleType(ImageView.ScaleType.CENTER);
    this.prevButton.setImageResource(NUM);
    this.controlsView.addView(this.prevButton, LayoutHelper.createFrame(48, 48, 17));
    this.prevButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView) {}
    });
    this.nextButton = new ImageView(paramContext);
    this.nextButton.setScaleType(ImageView.ScaleType.CENTER);
    this.nextButton.setImageResource(NUM);
    this.controlsView.addView(this.nextButton, LayoutHelper.createFrame(48, 48, 17));
    this.nextButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView) {}
    });
    if (paramBoolean)
    {
      this.inlineButton = new ImageView(paramContext);
      this.inlineButton.setScaleType(ImageView.ScaleType.CENTER);
      this.controlsView.addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 51));
      this.inlineButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = YouTubeView2.this;
          if (!YouTubeView2.this.isInline) {}
          for (boolean bool = true;; bool = false)
          {
            YouTubeView2.access$1402(paramAnonymousView, bool);
            YouTubeView2.this.updatePlayButton();
            YouTubeView2.this.updateShareButton();
            YouTubeView2.this.updateFullscreenButton();
            YouTubeView2.this.updateInlineButton();
            if (YouTubeView2.this.delegate != null) {
              YouTubeView2.this.delegate.onSwtichToInline(YouTubeView2.this.isInline);
            }
            return;
          }
        }
      });
    }
    this.shareButton = new ImageView(paramContext);
    this.shareButton.setScaleType(ImageView.ScaleType.CENTER);
    this.shareButton.setImageResource(NUM);
    this.controlsView.addView(this.shareButton, LayoutHelper.createFrame(56, 48, 53));
    this.shareButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (YouTubeView2.this.delegate != null) {
          YouTubeView2.this.delegate.onSharePressed();
        }
      }
    });
    updatePlayButton();
    updateFullscreenButton();
    updateMoveButtons();
    updateInlineButton();
    updateShareButton();
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
      ContextProgressView localContextProgressView = this.progressView;
      if (paramBoolean1) {}
      for (;;)
      {
        ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(localContextProgressView, "alpha", new float[] { f }) });
        this.progressAnimation.setDuration(150L);
        this.progressAnimation.addListener(new AnimatorListenerAdapterProxy()
        {
          public void onAnimationEnd(Animator paramAnonymousAnimator)
          {
            YouTubeView2.access$1702(YouTubeView2.this, null);
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
      ((ContextProgressView)localObject).setAlpha(f);
      return;
      f = 0.0F;
    }
  }
  
  private void updateFullscreenButton()
  {
    if (!this.inFullscreen)
    {
      this.fullscreenButton.setImageResource(NUM);
      return;
    }
    this.fullscreenButton.setImageResource(NUM);
  }
  
  private void updateInlineButton()
  {
    if (this.inlineButton == null) {
      return;
    }
    ImageView localImageView = this.inlineButton;
    if (this.isInline) {}
    for (int i = NUM;; i = NUM)
    {
      localImageView.setImageResource(i);
      return;
    }
  }
  
  private void updateMoveButtons()
  {
    this.prevButton.setVisibility(8);
    this.nextButton.setVisibility(8);
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
        return;
      }
    }
    ImageView localImageView = this.playButton;
    if (this.isInline) {}
    for (int i = NUM;; i = NUM)
    {
      localImageView.setImageResource(i);
      AndroidUtilities.runOnUIThread(this.progressRunnable);
      return;
    }
  }
  
  private void updateShareButton()
  {
    ImageView localImageView = this.shareButton;
    if (this.isInline) {}
    for (int i = 8;; i = 0)
    {
      localImageView.setVisibility(i);
      return;
    }
  }
  
  public View getChildAt(int paramInt)
  {
    if (paramInt == 1) {
      return null;
    }
    return super.getChildAt(paramInt);
  }
  
  public int getChildCount()
  {
    return 1;
  }
  
  public void loadVideo(String paramString, boolean paramBoolean)
  {
    this.initied = false;
    this.lastProgress = -1;
    this.videoId = paramString;
    this.isAutoplay = paramBoolean;
    this.isAutoplay = true;
    if (paramString != null) {
      new YoutubeVideoTask(paramString).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
    }
    this.isLoading = true;
    this.controlsView.show(false, false);
    if (this.progressAnimation != null)
    {
      this.progressAnimation.cancel();
      this.progressAnimation = null;
    }
    showProgress(true, false);
  }
  
  public void onError(Exception paramException)
  {
    FileLog.e("tmessages", paramException);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = (paramInt3 - paramInt1 - this.aspectRatioFrameLayout.getMeasuredWidth()) / 2;
    int j = (paramInt4 - paramInt2 - this.aspectRatioFrameLayout.getMeasuredHeight()) / 2;
    this.aspectRatioFrameLayout.layout(i, j, this.aspectRatioFrameLayout.getMeasuredWidth() + i, this.aspectRatioFrameLayout.getMeasuredHeight() + j);
    this.controlsView.layout(0, 0, this.controlsView.getMeasuredWidth(), this.controlsView.getMeasuredHeight());
    paramInt1 = (paramInt3 - paramInt1 - this.progressView.getMeasuredWidth()) / 2;
    paramInt2 = (paramInt4 - paramInt2 - this.progressView.getMeasuredHeight()) / 2;
    this.progressView.layout(paramInt1, paramInt2, this.progressView.getMeasuredWidth() + paramInt1, this.progressView.getMeasuredHeight() + paramInt2);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    paramInt2 = View.MeasureSpec.getSize(paramInt2);
    this.aspectRatioFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, NUM), View.MeasureSpec.makeMeasureSpec(paramInt2 - AndroidUtilities.dp(5.0F), NUM));
    this.controlsView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, NUM), View.MeasureSpec.makeMeasureSpec(paramInt2, NUM));
    this.progressView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(paramInt2, Integer.MIN_VALUE));
    setMeasuredDimension(paramInt1, paramInt2);
  }
  
  public void onRenderedFirstFrame() {}
  
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
      if ((!this.videoPlayer.isPlaying()) || (paramInt == 4)) {
        break label69;
      }
      updatePlayButton();
    }
    label69:
    while (paramInt != 4)
    {
      return;
      this.controlsView.setDuration(0);
      break;
    }
    this.isCompleted = true;
    updatePlayButton();
    this.controlsView.show(true, true);
  }
  
  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    int j;
    int i;
    AspectRatioFrameLayout localAspectRatioFrameLayout;
    if (this.aspectRatioFrameLayout != null)
    {
      if (paramInt3 != 90)
      {
        j = paramInt1;
        i = paramInt2;
        if (paramInt3 != 270) {}
      }
      else
      {
        i = paramInt1;
        j = paramInt2;
      }
      localAspectRatioFrameLayout = this.aspectRatioFrameLayout;
      if (i != 0) {
        break label55;
      }
    }
    label55:
    for (paramFloat = 1.0F;; paramFloat = j * paramFloat / i)
    {
      localAspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt3);
      return;
    }
  }
  
  public void setDelegate(YouTubeViewDelegate paramYouTubeViewDelegate)
  {
    this.delegate = paramYouTubeViewDelegate;
  }
  
  private class ControlsView
    extends FrameLayout
  {
    private int bufferedPercentage;
    private long bufferedPosition;
    private AnimatorSet currentAnimation;
    private int currentProgressX;
    private int duration;
    private StaticLayout durationLayout;
    private int durationWidth;
    private Runnable hideRunnable = new Runnable()
    {
      public void run()
      {
        YouTubeView2.ControlsView.this.show(false, true);
      }
    };
    private boolean isVisible = true;
    private int lastProgressX;
    private int progress;
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
      this.progressInnerPaint = new Paint(1);
      this.progressInnerPaint.setColor(-6975081);
    }
    
    private void checkNeedHide()
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      if ((this.isVisible) && (YouTubeView2.this.videoPlayer.isPlaying())) {
        AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
      }
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int m = getMeasuredWidth();
      int i = getMeasuredHeight();
      if (this.durationLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(m - AndroidUtilities.dp(58.0F) - this.durationWidth, i - AndroidUtilities.dp(34.0F));
        this.durationLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.progressLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(18.0F), i - AndroidUtilities.dp(34.0F));
        this.progressLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      int k;
      int j;
      label202:
      float f2;
      float f3;
      if (YouTubeView2.this.inFullscreen)
      {
        k = i - AndroidUtilities.dp(29.0F);
        j = AndroidUtilities.dp(36.0F) + this.durationWidth;
        m = m - AndroidUtilities.dp(76.0F) - this.durationWidth;
        i -= AndroidUtilities.dp(28.0F);
        paramCanvas.drawRect(j, k, m, AndroidUtilities.dp(3.0F) + k, this.progressInnerPaint);
        if (!this.progressPressed) {
          break label290;
        }
        m = this.currentProgressX;
        paramCanvas.drawRect(j, k, m, AndroidUtilities.dp(3.0F) + k, this.progressPaint);
        f2 = m;
        f3 = i;
        if (!this.progressPressed) {
          break label317;
        }
      }
      label290:
      label317:
      for (float f1 = 7.0F;; f1 = 5.0F)
      {
        paramCanvas.drawCircle(f2, f3, AndroidUtilities.dp(f1), this.progressPaint);
        return;
        k = i - AndroidUtilities.dp(8.0F);
        j = 0;
        i -= AndroidUtilities.dp(7.0F);
        break;
        m = j + (int)((m - j) * (this.progress / this.duration));
        break label202;
      }
    }
    
    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      if ((paramMotionEvent.getAction() == 0) && (!this.isVisible))
      {
        onTouchEvent(paramMotionEvent);
        return true;
      }
      return super.onInterceptTouchEvent(paramMotionEvent);
    }
    
    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int k;
      int j;
      if (YouTubeView2.this.inFullscreen)
      {
        k = AndroidUtilities.dp(36.0F) + this.durationWidth;
        j = getMeasuredWidth() - AndroidUtilities.dp(76.0F) - this.durationWidth;
        i = getMeasuredHeight() - AndroidUtilities.dp(28.0F);
        int m = k + (int)((j - k) * (this.progress / this.duration));
        if (paramMotionEvent.getAction() != 0) {
          break label223;
        }
        if (!this.isVisible) {
          break label214;
        }
        j = (int)paramMotionEvent.getX();
        k = (int)paramMotionEvent.getY();
        if ((j >= m - AndroidUtilities.dp(10.0F)) && (j <= AndroidUtilities.dp(10.0F) + m) && (k >= i - AndroidUtilities.dp(10.0F)) && (k <= AndroidUtilities.dp(10.0F) + i))
        {
          this.progressPressed = true;
          this.lastProgressX = j;
          this.currentProgressX = m;
          getParent().requestDisallowInterceptTouchEvent(true);
          invalidate();
        }
        label177:
        AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      }
      label214:
      label223:
      label322:
      do
      {
        for (;;)
        {
          super.onTouchEvent(paramMotionEvent);
          return true;
          k = 0;
          j = getMeasuredWidth();
          i = getMeasuredHeight() - AndroidUtilities.dp(7.0F);
          break;
          show(true, true);
          break label177;
          if (paramMotionEvent.getAction() != 1) {
            break label322;
          }
          if ((YouTubeView2.this.initied) && (YouTubeView2.this.videoPlayer.isPlaying())) {
            AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
          }
          if (this.progressPressed)
          {
            this.progressPressed = false;
            if (YouTubeView2.this.initied) {
              YouTubeView2.this.videoPlayer.seekTo((int)(this.duration * 1000 * (this.currentProgressX / getMeasuredWidth())));
            }
          }
        }
      } while ((paramMotionEvent.getAction() != 2) || (!this.progressPressed));
      int i = (int)paramMotionEvent.getX();
      this.currentProgressX -= this.lastProgressX - i;
      this.lastProgressX = i;
      if (this.currentProgressX < k) {
        this.currentProgressX = k;
      }
      for (;;)
      {
        setProgress((int)(this.duration * 1000 * ((this.currentProgressX - k) / (j - k))));
        invalidate();
        break;
        if (this.currentProgressX > j) {
          this.currentProgressX = j;
        }
      }
    }
    
    public void setBufferedProgress(long paramLong, int paramInt)
    {
      this.bufferedPosition = paramLong;
      this.bufferedPercentage = paramInt;
    }
    
    public void setDuration(int paramInt)
    {
      if (this.duration == paramInt) {
        return;
      }
      this.duration = paramInt;
      this.durationLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.duration / 60), Integer.valueOf(this.duration % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.durationLayout.getLineCount() > 0) {
        this.durationWidth = ((int)Math.ceil(this.durationLayout.getLineWidth(0)));
      }
      invalidate();
    }
    
    public void setProgress(int paramInt)
    {
      if (this.progressPressed) {
        return;
      }
      this.progress = paramInt;
      this.progressLayout = new StaticLayout(String.format(Locale.US, "%d:%02d", new Object[] { Integer.valueOf(this.progress / 60), Integer.valueOf(this.progress % 60) }), this.textPaint, AndroidUtilities.dp(1000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      invalidate();
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
          this.currentAnimation.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              YouTubeView2.ControlsView.access$1002(YouTubeView2.ControlsView.this, null);
            }
          });
          this.currentAnimation.start();
        }
      }
      for (;;)
      {
        checkNeedHide();
        return;
        setAlpha(1.0F);
        continue;
        if (paramBoolean2)
        {
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "alpha", new float[] { 0.0F }) });
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.addListener(new AnimatorListenerAdapterProxy()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              YouTubeView2.ControlsView.access$1002(YouTubeView2.ControlsView.this, null);
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
  
  public static abstract interface YouTubeViewDelegate
  {
    public abstract void onInitFailed();
    
    public abstract void onSharePressed();
    
    public abstract void onSwithToFullscreen(boolean paramBoolean);
    
    public abstract void onSwtichToInline(boolean paramBoolean);
  }
  
  private class YoutubeVideoTask
    extends AsyncTask<Void, Void, String>
  {
    private boolean canRetry = true;
    private String videoId;
    
    public YoutubeVideoTask(String paramString)
    {
      this.videoId = paramString;
    }
    
    private String downloadUrlContent(String paramString)
    {
      Object localObject5 = null;
      int j = 0;
      int k = 0;
      Object localObject4 = null;
      byte[] arrayOfByte = null;
      Object localObject3 = null;
      Object localObject1 = null;
      Object localObject2;
      try
      {
        localObject2 = new URL(paramString).openConnection();
        localObject1 = localObject2;
        ((URLConnection)localObject2).addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        localObject1 = localObject2;
        ((URLConnection)localObject2).addRequestProperty("Referer", "google.com");
        localObject1 = localObject2;
        ((URLConnection)localObject2).setConnectTimeout(5000);
        localObject1 = localObject2;
        ((URLConnection)localObject2).setReadTimeout(5000);
        paramString = (String)localObject2;
        localObject1 = localObject2;
        if ((localObject2 instanceof HttpURLConnection))
        {
          localObject1 = localObject2;
          Object localObject6 = (HttpURLConnection)localObject2;
          localObject1 = localObject2;
          ((HttpURLConnection)localObject6).setInstanceFollowRedirects(true);
          localObject1 = localObject2;
          i = ((HttpURLConnection)localObject6).getResponseCode();
          if ((i != 302) && (i != 301))
          {
            paramString = (String)localObject2;
            if (i != 303) {}
          }
          else
          {
            localObject1 = localObject2;
            paramString = ((HttpURLConnection)localObject6).getHeaderField("Location");
            localObject1 = localObject2;
            localObject6 = ((HttpURLConnection)localObject6).getHeaderField("Set-Cookie");
            localObject1 = localObject2;
            paramString = new URL(paramString).openConnection();
            localObject1 = paramString;
            paramString.setRequestProperty("Cookie", (String)localObject6);
            localObject1 = paramString;
            paramString.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4; Nexus 5 Build/_BuildID_) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
            localObject1 = paramString;
            paramString.addRequestProperty("Referer", "google.com");
          }
        }
        localObject1 = paramString;
        paramString.connect();
        localObject1 = paramString;
        localObject2 = paramString.getInputStream();
      }
      catch (Throwable paramString)
      {
        if (!(paramString instanceof SocketTimeoutException)) {
          break label401;
        }
        if (!ConnectionsManager.isNetworkOnline()) {
          break label385;
        }
        this.canRetry = false;
        for (;;)
        {
          label385:
          FileLog.e("tmessages", paramString);
          paramString = (String)localObject1;
          localObject2 = localObject5;
          break;
          label401:
          if ((paramString instanceof UnknownHostException)) {
            this.canRetry = false;
          } else if ((paramString instanceof SocketException))
          {
            if ((paramString.getMessage() != null) && (paramString.getMessage().contains("ECONNRESET"))) {
              this.canRetry = false;
            }
          }
          else if ((paramString instanceof FileNotFoundException)) {
            this.canRetry = false;
          }
        }
      }
      localObject1 = localObject4;
      if ((!this.canRetry) || (paramString != null)) {}
      try
      {
        if ((paramString instanceof HttpURLConnection))
        {
          i = ((HttpURLConnection)paramString).getResponseCode();
          if ((i != 200) && (i != 202) && (i != 304)) {
            this.canRetry = false;
          }
        }
      }
      catch (Exception paramString)
      {
        for (;;)
        {
          FileLog.e("tmessages", paramString);
        }
      }
      int i = k;
      paramString = (String)localObject3;
      if (localObject2 != null) {
        localObject1 = arrayOfByte;
      }
      for (;;)
      {
        try
        {
          arrayOfByte = new byte[32768];
          paramString = null;
        }
        catch (Throwable localThrowable3)
        {
          boolean bool;
          label503:
          paramString = (String)localObject1;
          localObject1 = localThrowable3;
          FileLog.e("tmessages", (Throwable)localObject1);
          i = k;
          continue;
        }
        for (;;)
        {
          try
          {
            bool = isCancelled();
            if (bool)
            {
              i = k;
              j = i;
              localObject1 = paramString;
              if (localObject2 == null) {
                break;
              }
            }
          }
          catch (Throwable localThrowable2)
          {
            String str;
            break label570;
            break label503;
          }
          try
          {
            ((InputStream)localObject2).close();
            localObject1 = paramString;
            j = i;
          }
          catch (Throwable localThrowable1)
          {
            FileLog.e("tmessages", localThrowable1);
            j = i;
            str = paramString;
            break;
          }
        }
        if (j == 0) {
          break label600;
        }
        return ((StringBuilder)localObject1).toString();
        try
        {
          i = ((InputStream)localObject2).read(arrayOfByte);
          if (i > 0)
          {
            if (paramString != null) {
              break label612;
            }
            localObject1 = new StringBuilder();
            paramString = (String)localObject1;
            localObject1 = paramString;
          }
        }
        catch (Exception localException1) {}
        try
        {
          paramString.append(new String(arrayOfByte, 0, i, "UTF-8"));
        }
        catch (Exception localException2)
        {
          continue;
        }
        if (i == -1)
        {
          i = 1;
        }
        else
        {
          i = k;
          continue;
          localObject1 = paramString;
          FileLog.e("tmessages", localException1);
          i = k;
        }
      }
      label570:
      label600:
      return null;
    }
    
    protected String doInBackground(Void... paramVarArgs)
    {
      localObject2 = downloadUrlContent("https://www.youtube.com/embed/" + this.videoId);
      localObject1 = "video_id=" + this.videoId;
      try
      {
        paramVarArgs = (String)localObject1 + "&eurl=" + URLEncoder.encode(new StringBuilder().append("https://youtube.googleapis.com/v/").append(this.videoId).toString(), "UTF-8");
        localObject1 = paramVarArgs;
      }
      catch (Exception paramVarArgs)
      {
        int k;
        for (;;)
        {
          int i;
          String[] arrayOfString;
          int j;
          FileLog.e("tmessages", paramVarArgs);
          continue;
          paramVarArgs = (String)localObject1 + "&sts=";
          continue;
          k = i;
          arrayOfVoid = paramVarArgs;
          if (arrayOfString[j].startsWith("use_cipher_signature"))
          {
            localObject2 = arrayOfString[j].split("=");
            k = i;
            arrayOfVoid = paramVarArgs;
            if (localObject2.length == 2)
            {
              k = i;
              arrayOfVoid = paramVarArgs;
              if (localObject2[1].toLowerCase().equals("true"))
              {
                k = 1;
                arrayOfVoid = paramVarArgs;
              }
            }
          }
        }
        if (k == 0) {
          break label430;
        }
        Void[] arrayOfVoid = null;
        return arrayOfVoid;
      }
      paramVarArgs = (Void[])localObject1;
      if (localObject2 != null)
      {
        paramVarArgs = YouTubeView2.stsPattern.matcher((CharSequence)localObject2);
        if (paramVarArgs.find()) {
          paramVarArgs = (String)localObject1 + "&sts=" + ((String)localObject2).substring(paramVarArgs.start() + 6, paramVarArgs.end());
        }
      }
      else
      {
        localObject1 = null;
        localObject2 = null;
        k = 0;
        i = 0;
        paramVarArgs = downloadUrlContent("https://www.youtube.com/get_video_info?&" + paramVarArgs);
        if (paramVarArgs == null) {
          break label422;
        }
        arrayOfString = paramVarArgs.split("&");
        j = 0;
        for (paramVarArgs = (Void[])localObject2;; paramVarArgs = (Void[])localObject1)
        {
          k = i;
          localObject1 = paramVarArgs;
          if (j >= arrayOfString.length) {
            break label422;
          }
          if (!arrayOfString[j].startsWith("dashmpd")) {
            break label350;
          }
          localObject2 = arrayOfString[j].split("=");
          k = i;
          localObject1 = paramVarArgs;
          if (localObject2.length == 2) {}
          try
          {
            localObject1 = URLDecoder.decode(localObject2[1], "UTF-8");
            k = i;
          }
          catch (Exception localException)
          {
            for (;;)
            {
              FileLog.e("tmessages", localException);
              k = i;
              arrayOfVoid = paramVarArgs;
            }
          }
          j += 1;
          i = k;
        }
      }
    }
    
    protected void onPostExecute(String paramString)
    {
      if (paramString != null)
      {
        YouTubeView2.access$402(YouTubeView2.this, true);
        YouTubeView2.this.videoPlayer.preparePlayer(Uri.parse(paramString));
        YouTubeView2.this.videoPlayer.setPlayWhenReady(YouTubeView2.this.isAutoplay);
        YouTubeView2.this.updateShareButton();
        YouTubeView2.access$702(YouTubeView2.this, false);
        YouTubeView2.this.showProgress(false, true);
        YouTubeView2.this.controlsView.show(true, true);
        if (YouTubeView2.this.videoPlayer.getDuration() != -9223372036854775807L)
        {
          YouTubeView2.this.controlsView.setDuration((int)(YouTubeView2.this.videoPlayer.getDuration() / 1000L));
          return;
        }
        YouTubeView2.this.controlsView.setDuration(0);
        return;
      }
      YouTubeView2.this.delegate.onInitFailed();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/youtube/YouTubeView2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */