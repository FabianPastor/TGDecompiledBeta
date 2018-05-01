package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.support.annotation.Keep;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.Collection;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.PhotoViewer;

public class PipVideoView
{
  private View controlsView;
  private DecelerateInterpolator decelerateInterpolator;
  private Activity parentActivity;
  private EmbedBottomSheet parentSheet;
  private PhotoViewer photoViewer;
  private SharedPreferences preferences;
  private int videoHeight;
  private int videoWidth;
  private WindowManager.LayoutParams windowLayoutParams;
  private WindowManager windowManager;
  private FrameLayout windowView;
  
  private void animateToBoundsMaybe()
  {
    int i = getSideCoord(true, 0, 0.0F, this.videoWidth);
    int j = getSideCoord(true, 1, 0.0F, this.videoWidth);
    int k = getSideCoord(false, 0, 0.0F, this.videoHeight);
    int m = getSideCoord(false, 1, 0.0F, this.videoHeight);
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    SharedPreferences.Editor localEditor = this.preferences.edit();
    int n = AndroidUtilities.dp(20.0F);
    int i1 = 0;
    if ((Math.abs(i - this.windowLayoutParams.x) <= n) || ((this.windowLayoutParams.x < 0) && (this.windowLayoutParams.x > -this.videoWidth / 4)))
    {
      if (0 == 0) {
        localObject4 = new ArrayList();
      }
      localEditor.putInt("sidex", 0);
      if (this.windowView.getAlpha() != 1.0F) {
        ((ArrayList)localObject4).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
      }
      ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { i }));
      localObject1 = localObject4;
      if (i1 == 0)
      {
        if ((Math.abs(k - this.windowLayoutParams.y) > n) && (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight())) {
          break label690;
        }
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = new ArrayList();
        }
        localEditor.putInt("sidey", 0);
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "y", new int[] { k }));
        localObject4 = localObject1;
      }
    }
    for (;;)
    {
      localEditor.commit();
      localObject1 = localObject4;
      if (localObject1 != null)
      {
        if (this.decelerateInterpolator == null) {
          this.decelerateInterpolator = new DecelerateInterpolator();
        }
        localObject4 = new AnimatorSet();
        ((AnimatorSet)localObject4).setInterpolator(this.decelerateInterpolator);
        ((AnimatorSet)localObject4).setDuration(150L);
        if (i1 != 0)
        {
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }));
          ((AnimatorSet)localObject4).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              if (PipVideoView.this.parentSheet != null) {
                PipVideoView.this.parentSheet.destroy();
              }
              for (;;)
              {
                return;
                if (PipVideoView.this.photoViewer != null) {
                  PipVideoView.this.photoViewer.destroyPhotoViewer();
                }
              }
            }
          });
        }
        ((AnimatorSet)localObject4).playTogether((Collection)localObject1);
        ((AnimatorSet)localObject4).start();
      }
      return;
      if ((Math.abs(j - this.windowLayoutParams.x) <= n) || ((this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.videoWidth) && (this.windowLayoutParams.x < AndroidUtilities.displaySize.x - this.videoWidth / 4 * 3)))
      {
        localObject4 = localObject2;
        if (0 == 0) {
          localObject4 = new ArrayList();
        }
        localEditor.putInt("sidex", 1);
        if (this.windowView.getAlpha() != 1.0F) {
          ((ArrayList)localObject4).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
        }
        ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { j }));
        break;
      }
      if (this.windowView.getAlpha() != 1.0F)
      {
        localObject4 = localObject3;
        if (0 == 0) {
          localObject4 = new ArrayList();
        }
        if (this.windowLayoutParams.x < 0) {
          ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { -this.videoWidth }));
        }
        for (;;)
        {
          i1 = 1;
          break;
          ((ArrayList)localObject4).add(ObjectAnimator.ofInt(this, "x", new int[] { AndroidUtilities.displaySize.x }));
        }
      }
      localEditor.putFloat("px", (this.windowLayoutParams.x - i) / (j - i));
      localEditor.putInt("sidex", 2);
      localObject4 = localObject1;
      break;
      label690:
      if (Math.abs(m - this.windowLayoutParams.y) <= n)
      {
        localObject1 = localObject4;
        if (localObject4 == null) {
          localObject1 = new ArrayList();
        }
        localEditor.putInt("sidey", 1);
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "y", new int[] { m }));
        localObject4 = localObject1;
      }
      else
      {
        localEditor.putFloat("py", (this.windowLayoutParams.y - k) / (m - k));
        localEditor.putInt("sidey", 2);
      }
    }
  }
  
  public static Rect getPipRect(float paramFloat)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
    int i = localSharedPreferences.getInt("sidex", 1);
    int j = localSharedPreferences.getInt("sidey", 0);
    float f1 = localSharedPreferences.getFloat("px", 0.0F);
    float f2 = localSharedPreferences.getFloat("py", 0.0F);
    int k;
    int m;
    if (paramFloat > 1.0F)
    {
      k = AndroidUtilities.dp(192.0F);
      m = (int)(k / paramFloat);
    }
    for (;;)
    {
      return new Rect(getSideCoord(true, i, f1, k), getSideCoord(false, j, f2, m), k, m);
      m = AndroidUtilities.dp(192.0F);
      k = (int)(m * paramFloat);
    }
  }
  
  private static int getSideCoord(boolean paramBoolean, int paramInt1, float paramFloat, int paramInt2)
  {
    if (paramBoolean)
    {
      paramInt2 = AndroidUtilities.displaySize.x - paramInt2;
      if (paramInt1 != 0) {
        break label53;
      }
      paramInt1 = AndroidUtilities.dp(10.0F);
    }
    for (;;)
    {
      paramInt2 = paramInt1;
      if (!paramBoolean) {
        paramInt2 = paramInt1 + ActionBar.getCurrentActionBarHeight();
      }
      return paramInt2;
      paramInt2 = AndroidUtilities.displaySize.y - paramInt2 - ActionBar.getCurrentActionBarHeight();
      break;
      label53:
      if (paramInt1 == 1) {
        paramInt1 = paramInt2 - AndroidUtilities.dp(10.0F);
      } else {
        paramInt1 = Math.round((paramInt2 - AndroidUtilities.dp(20.0F)) * paramFloat) + AndroidUtilities.dp(10.0F);
      }
    }
  }
  
  public void close()
  {
    try
    {
      this.windowManager.removeView(this.windowView);
      this.parentSheet = null;
      this.photoViewer = null;
      this.parentActivity = null;
      return;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
  }
  
  @Keep
  public int getX()
  {
    return this.windowLayoutParams.x;
  }
  
  @Keep
  public int getY()
  {
    return this.windowLayoutParams.y;
  }
  
  public void onConfigurationChanged()
  {
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    this.windowLayoutParams.x = getSideCoord(true, i, f1, this.videoWidth);
    this.windowLayoutParams.y = getSideCoord(false, j, f2, this.videoHeight);
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }
  
  public void onVideoCompleted()
  {
    if ((this.controlsView instanceof MiniControlsView))
    {
      MiniControlsView localMiniControlsView = (MiniControlsView)this.controlsView;
      MiniControlsView.access$1102(localMiniControlsView, true);
      MiniControlsView.access$1202(localMiniControlsView, 0.0F);
      MiniControlsView.access$1302(localMiniControlsView, 0.0F);
      localMiniControlsView.updatePlayButton();
      localMiniControlsView.invalidate();
      localMiniControlsView.show(true, true);
    }
  }
  
  public void setBufferedProgress(float paramFloat)
  {
    if ((this.controlsView instanceof MiniControlsView)) {
      ((MiniControlsView)this.controlsView).setBufferedProgress(paramFloat);
    }
  }
  
  @Keep
  public void setX(int paramInt)
  {
    this.windowLayoutParams.x = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }
  
  @Keep
  public void setY(int paramInt)
  {
    this.windowLayoutParams.y = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }
  
  public TextureView show(Activity paramActivity, EmbedBottomSheet paramEmbedBottomSheet, View paramView, float paramFloat, int paramInt, WebView paramWebView)
  {
    return show(paramActivity, null, paramEmbedBottomSheet, paramView, paramFloat, paramInt, paramWebView);
  }
  
  public TextureView show(Activity paramActivity, PhotoViewer paramPhotoViewer, float paramFloat, int paramInt)
  {
    return show(paramActivity, paramPhotoViewer, null, null, paramFloat, paramInt, null);
  }
  
  public TextureView show(Activity paramActivity, PhotoViewer paramPhotoViewer, EmbedBottomSheet paramEmbedBottomSheet, View paramView, float paramFloat, int paramInt, WebView paramWebView)
  {
    this.parentSheet = paramEmbedBottomSheet;
    this.parentActivity = paramActivity;
    this.photoViewer = paramPhotoViewer;
    this.windowView = new FrameLayout(paramActivity)
    {
      private boolean dragging;
      private float startX;
      private float startY;
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        float f1 = paramAnonymousMotionEvent.getRawX();
        float f2 = paramAnonymousMotionEvent.getRawY();
        if (paramAnonymousMotionEvent.getAction() == 0)
        {
          this.startX = f1;
          this.startY = f2;
        }
        for (boolean bool = super.onInterceptTouchEvent(paramAnonymousMotionEvent);; bool = true)
        {
          return bool;
          if ((paramAnonymousMotionEvent.getAction() != 2) || (this.dragging) || ((Math.abs(this.startX - f1) < AndroidUtilities.getPixelsInCM(0.3F, true)) && (Math.abs(this.startY - f2) < AndroidUtilities.getPixelsInCM(0.3F, false)))) {
            break;
          }
          this.dragging = true;
          this.startX = f1;
          this.startY = f2;
          if (PipVideoView.this.controlsView != null) {
            ((ViewParent)PipVideoView.this.controlsView).requestDisallowInterceptTouchEvent(true);
          }
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        boolean bool = false;
        if (!this.dragging) {
          return bool;
        }
        float f1 = paramAnonymousMotionEvent.getRawX();
        float f2 = paramAnonymousMotionEvent.getRawY();
        float f3;
        int i;
        if (paramAnonymousMotionEvent.getAction() == 2)
        {
          f3 = this.startX;
          float f4 = this.startY;
          paramAnonymousMotionEvent = PipVideoView.this.windowLayoutParams;
          paramAnonymousMotionEvent.x = ((int)(paramAnonymousMotionEvent.x + (f1 - f3)));
          paramAnonymousMotionEvent = PipVideoView.this.windowLayoutParams;
          paramAnonymousMotionEvent.y = ((int)(paramAnonymousMotionEvent.y + (f2 - f4)));
          i = PipVideoView.this.videoWidth / 2;
          if (PipVideoView.this.windowLayoutParams.x < -i)
          {
            PipVideoView.this.windowLayoutParams.x = (-i);
            label129:
            f3 = 1.0F;
            if (PipVideoView.this.windowLayoutParams.x >= 0) {
              break label330;
            }
            f3 = 1.0F + PipVideoView.this.windowLayoutParams.x / i * 0.5F;
            label167:
            if (PipVideoView.this.windowView.getAlpha() != f3) {
              PipVideoView.this.windowView.setAlpha(f3);
            }
            if (PipVideoView.this.windowLayoutParams.y >= -0) {
              break label403;
            }
            PipVideoView.this.windowLayoutParams.y = (-0);
            label222:
            PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
            this.startX = f1;
            this.startY = f2;
          }
        }
        for (;;)
        {
          bool = true;
          break;
          if (PipVideoView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width + i) {
            break label129;
          }
          PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width + i);
          break label129;
          label330:
          if (PipVideoView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width) {
            break label167;
          }
          f3 = 1.0F - (PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x + PipVideoView.this.windowLayoutParams.width) / i * 0.5F;
          break label167;
          label403:
          if (PipVideoView.this.windowLayoutParams.y <= AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height + 0) {
            break label222;
          }
          PipVideoView.this.windowLayoutParams.y = (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height + 0);
          break label222;
          if (paramAnonymousMotionEvent.getAction() == 1)
          {
            this.dragging = false;
            PipVideoView.this.animateToBoundsMaybe();
          }
        }
      }
      
      public void requestDisallowInterceptTouchEvent(boolean paramAnonymousBoolean)
      {
        super.requestDisallowInterceptTouchEvent(paramAnonymousBoolean);
      }
    };
    AspectRatioFrameLayout localAspectRatioFrameLayout;
    label132:
    boolean bool;
    label144:
    label159:
    int i;
    float f;
    if (paramFloat > 1.0F)
    {
      this.videoWidth = AndroidUtilities.dp(192.0F);
      this.videoHeight = ((int)(this.videoWidth / paramFloat));
      localAspectRatioFrameLayout = new AspectRatioFrameLayout(paramActivity);
      localAspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt);
      this.windowView.addView(localAspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
      if (paramWebView == null) {
        break label425;
      }
      paramEmbedBottomSheet = (ViewGroup)paramWebView.getParent();
      if (paramEmbedBottomSheet != null) {
        paramEmbedBottomSheet.removeView(paramWebView);
      }
      localAspectRatioFrameLayout.addView(paramWebView, LayoutHelper.createFrame(-1, -1.0F));
      paramEmbedBottomSheet = null;
      if (paramView != null) {
        break label456;
      }
      if (paramPhotoViewer == null) {
        break label450;
      }
      bool = true;
      this.controlsView = new MiniControlsView(paramActivity, bool);
      this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0F));
      this.windowManager = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window"));
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
      i = this.preferences.getInt("sidex", 1);
      paramInt = this.preferences.getInt("sidey", 0);
      paramFloat = this.preferences.getFloat("px", 0.0F);
      f = this.preferences.getFloat("py", 0.0F);
    }
    for (;;)
    {
      try
      {
        paramActivity = new android/view/WindowManager$LayoutParams;
        paramActivity.<init>();
        this.windowLayoutParams = paramActivity;
        this.windowLayoutParams.width = this.videoWidth;
        this.windowLayoutParams.height = this.videoHeight;
        this.windowLayoutParams.x = getSideCoord(true, i, paramFloat, this.videoWidth);
        this.windowLayoutParams.y = getSideCoord(false, paramInt, f, this.videoHeight);
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.gravity = 51;
        if (Build.VERSION.SDK_INT < 26) {
          continue;
        }
        this.windowLayoutParams.type = 2038;
        this.windowLayoutParams.flags = 16777736;
        this.windowManager.addView(this.windowView, this.windowLayoutParams);
      }
      catch (Exception paramActivity)
      {
        label425:
        label450:
        label456:
        FileLog.e(paramActivity);
        paramEmbedBottomSheet = null;
        continue;
      }
      return paramEmbedBottomSheet;
      this.videoHeight = AndroidUtilities.dp(192.0F);
      this.videoWidth = ((int)(this.videoHeight * paramFloat));
      break;
      paramEmbedBottomSheet = new TextureView(paramActivity);
      localAspectRatioFrameLayout.addView(paramEmbedBottomSheet, LayoutHelper.createFrame(-1, -1.0F));
      break label132;
      bool = false;
      break label144;
      this.controlsView = paramView;
      break label159;
      this.windowLayoutParams.type = 2003;
    }
  }
  
  public void updatePlayButton()
  {
    if ((this.controlsView instanceof MiniControlsView))
    {
      MiniControlsView localMiniControlsView = (MiniControlsView)this.controlsView;
      localMiniControlsView.updatePlayButton();
      localMiniControlsView.invalidate();
    }
  }
  
  private class MiniControlsView
    extends FrameLayout
  {
    private float bufferedPosition;
    private AnimatorSet currentAnimation;
    private Runnable hideRunnable = new Runnable()
    {
      public void run()
      {
        PipVideoView.MiniControlsView.this.show(false, true);
      }
    };
    private ImageView inlineButton;
    private boolean isCompleted;
    private boolean isVisible = true;
    private ImageView playButton;
    private float progress;
    private Paint progressInnerPaint;
    private Paint progressPaint;
    private Runnable progressRunnable = new Runnable()
    {
      public void run()
      {
        if (PipVideoView.this.photoViewer == null) {}
        for (;;)
        {
          return;
          VideoPlayer localVideoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
          if (localVideoPlayer != null)
          {
            PipVideoView.MiniControlsView.this.setProgress((float)localVideoPlayer.getCurrentPosition() / (float)localVideoPlayer.getDuration());
            if (PipVideoView.this.photoViewer == null) {
              PipVideoView.MiniControlsView.this.setBufferedProgress((float)localVideoPlayer.getBufferedPosition() / (float)localVideoPlayer.getDuration());
            }
            AndroidUtilities.runOnUIThread(PipVideoView.MiniControlsView.this.progressRunnable, 1000L);
          }
        }
      }
    };
    
    public MiniControlsView(Context paramContext, boolean paramBoolean)
    {
      super();
      this.inlineButton = new ImageView(paramContext);
      this.inlineButton.setScaleType(ImageView.ScaleType.CENTER);
      this.inlineButton.setImageResource(NUM);
      addView(this.inlineButton, LayoutHelper.createFrame(56, 48, 53));
      this.inlineButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (PipVideoView.this.parentSheet != null) {
            PipVideoView.this.parentSheet.exitFromPip();
          }
          for (;;)
          {
            return;
            if (PipVideoView.this.photoViewer != null) {
              PipVideoView.this.photoViewer.exitFromPip();
            }
          }
        }
      });
      if (paramBoolean)
      {
        this.progressPaint = new Paint();
        this.progressPaint.setColor(-15095832);
        this.progressInnerPaint = new Paint();
        this.progressInnerPaint.setColor(-6975081);
        setWillNotDraw(false);
        this.playButton = new ImageView(paramContext);
        this.playButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.playButton, LayoutHelper.createFrame(48, 48, 17));
        this.playButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (PipVideoView.this.photoViewer == null) {}
            do
            {
              return;
              paramAnonymousView = PipVideoView.this.photoViewer.getVideoPlayer();
            } while (paramAnonymousView == null);
            if (paramAnonymousView.isPlaying()) {
              paramAnonymousView.pause();
            }
            for (;;)
            {
              PipVideoView.MiniControlsView.this.updatePlayButton();
              break;
              paramAnonymousView.play();
            }
          }
        });
      }
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      updatePlayButton();
      show(false, false);
    }
    
    private void checkNeedHide()
    {
      AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
      if (this.isVisible) {
        AndroidUtilities.runOnUIThread(this.hideRunnable, 3000L);
      }
    }
    
    private void updatePlayButton()
    {
      if (PipVideoView.this.photoViewer == null) {}
      for (;;)
      {
        return;
        VideoPlayer localVideoPlayer = PipVideoView.this.photoViewer.getVideoPlayer();
        if (localVideoPlayer != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.progressRunnable);
          if (!localVideoPlayer.isPlaying())
          {
            if (this.isCompleted) {
              this.playButton.setImageResource(NUM);
            } else {
              this.playButton.setImageResource(NUM);
            }
          }
          else
          {
            this.playButton.setImageResource(NUM);
            AndroidUtilities.runOnUIThread(this.progressRunnable, 500L);
          }
        }
      }
    }
    
    protected void onAttachedToWindow()
    {
      super.onAttachedToWindow();
      checkNeedHide();
    }
    
    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredWidth();
      int j = getMeasuredHeight();
      j -= AndroidUtilities.dp(3.0F);
      AndroidUtilities.dp(7.0F);
      int k = (int)((i - 0) * this.progress);
      if (this.bufferedPosition != 0.0F)
      {
        float f1 = 0;
        float f2 = j;
        float f3 = 0;
        paramCanvas.drawRect(f1, f2, (i - 0) * this.bufferedPosition + f3, AndroidUtilities.dp(3.0F) + j, this.progressInnerPaint);
      }
      paramCanvas.drawRect(0, j, 0 + k, AndroidUtilities.dp(3.0F) + j, this.progressPaint);
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
        checkNeedHide();
        bool = super.onInterceptTouchEvent(paramMotionEvent);
      }
    }
    
    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      super.requestDisallowInterceptTouchEvent(paramBoolean);
      checkNeedHide();
    }
    
    public void setBufferedProgress(float paramFloat)
    {
      this.bufferedPosition = paramFloat;
      invalidate();
    }
    
    public void setProgress(float paramFloat)
    {
      this.progress = paramFloat;
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
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
              PipVideoView.MiniControlsView.access$402(PipVideoView.MiniControlsView.this, null);
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
              PipVideoView.MiniControlsView.access$402(PipVideoView.MiniControlsView.this, null);
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
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/PipVideoView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */